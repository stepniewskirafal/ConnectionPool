package pl.rstepniewski.pool;

import pl.rstepniewski.pool.util.PropertiesUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

public class ConnectionPool {
    private static final String POOL_INIT_SIZE = "pool.init.size";
    private static final String POOL_MAX_SIZE = "pool.max.size";
    private final List<ConnectionThread> connectionPool = new ArrayList<>();
    private static final Object lock = new Object();
    private final Semaphore dupa1 = new Semaphore(1);
    private final Semaphore getList = new Semaphore(1);
    private final Semaphore getSizeList = new Semaphore(1);
    private final Semaphore getSizeList2 = new Semaphore(1);
    private final Semaphore getSizeList3 = new Semaphore(1);
    private final Semaphore getList2 = new Semaphore(1);


    private Timer timer;

    public void createInitialPool() throws SQLException, InterruptedException {
        System.out.println("CREATING CONNECTION POOL");
        int initSize = PropertiesUtil.getNumber(POOL_INIT_SIZE);
        for (int i = 0; i < initSize; i++) {
            addConnectionToPool();
        }
        System.out.println("CONNECTION POOL HAS BEEN CREATED");
        runSchedule();
    }

    private void runSchedule() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    removeExcessiveConnections();
                } catch (SQLException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        // Uruchomienie zadania co 1 sekundę (1000 milisekund)
        timer.schedule(task, 0, 1000);
    }

    private ConnectionThread addConnectionToPool() throws SQLException, InterruptedException {
        ConnectionThread connectionThread = null;
        dupa1.acquire();
        connectionThread = new ConnectionThread();
        connectionPool.add(connectionThread);
        dupa1.release();
        return connectionThread;
    }

    private Optional<ConnectionThread> getFreeConnectionThread() throws InterruptedException {
        return getFreeConnectionList()
                .stream()
                .findFirst();
    }

    protected synchronized List<ConnectionThread> getFreeConnectionList() throws InterruptedException {
        getList.acquire(1);
        List<ConnectionThread> list = connectionPool.stream()
                .filter(thread -> !thread.isBusy())
                .toList();
        getList.release(1);

        return list;
    }

    public synchronized void returnConnection(ConnectionThread connectionThread) {
        connectionThread.setBusyFalse();
    }

    public ConnectionThread getFreeConnection() throws SQLException, InterruptedException {
        ConnectionThread freeConnectionThread = null;
        synchronized (lock) {
/*        connectionPoolAvailable.acquire();
            try {*/
            Optional<ConnectionThread> optionalConnectionThread = getFreeConnectionThread();
            if (optionalConnectionThread.isPresent()) {
                System.out.println("Mam wolne wątki na liście");
                freeConnectionThread = optionalConnectionThread.get();
                freeConnectionThread.setBusyTrue();
            } else {
                System.out.println("Dodaję nowe wątki");
                int maxSize = PropertiesUtil.getNumber(POOL_MAX_SIZE);
                getSizeList3.acquire();
                if (getFreeConnectionList().size() < maxSize) {
                    freeConnectionThread = addConnectionToPool();
                    freeConnectionThread.setBusyTrue();
                } else {
                    System.out.println("NO FREE CONNECTIONS AVAILABLE, TRY AGAIN LATER!");
                    removeExcessiveConnections();
                }
                getSizeList3.release();
            }
/*            } finally {
                connectionPoolAvailable.release();
            }*/
        }
        return freeConnectionThread;
    }

    private synchronized int getPoolSize() throws InterruptedException {
        getSizeList.acquire(1);
        int size = connectionPool.size();
        getSizeList.release(1);
        return size;
    }

    protected synchronized void removeExcessiveConnections() throws SQLException, InterruptedException {
        int initSize = PropertiesUtil.getNumber(POOL_INIT_SIZE);
        getSizeList2.acquire(1);
        while (getPoolSize() > initSize) {
            if(getFreeConnectionList().size()==0) return;
            ConnectionThread connectionThread = getFreeConnectionList().get(0);
            connectionThread.closeConnection();
            connectionPool.remove(connectionThread);
        }
        getSizeList2.release(1);
    }

    public void removeAllConnections() throws SQLException, InterruptedException {
        System.out.println("Removing all connections!");
        getList2.acquire();
        if(connectionPool.size()==0)return;
        for (ConnectionThread connectionThread : connectionPool) {
            connectionThread.closeConnection();
        }
        getList2.release();
        connectionPool.clear();

    }
}
