package pl.rstepniewski.pool;

/**
 * Created by rafal on 22.05.2023
 *
 * @author : rafal
 * @date : 22.05.2023
 * @project : ConnectionPool
 */
import pl.rstepniewski.pool.util.PropertiesUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;

public class ConnectionPool {
    private static final String POOL_INIT_SIZE = "pool.init.size";
    private static final String POOL_MAX_SIZE = "pool.max.size";
    private final List<ConnectionThread> connectionPool = new ArrayList<>( PropertiesUtil.getNumber(POOL_INIT_SIZE) );
    private final Semaphore connectionPoolAvailable = new Semaphore(1);
    private final Semaphore threadAvailable = new Semaphore(PropertiesUtil.getNumber(POOL_MAX_SIZE));
    private ConnectionThread connectionThread;

    public void createInitialPool() throws SQLException, InterruptedException {
        System.out.println("CREATING CONNECTION POOL");
        for (int i = 0; i < PropertiesUtil.getNumber(POOL_INIT_SIZE); i++) {
            System.out.println("threadAvailable.availablePermits() " + threadAvailable.availablePermits());
            addConnectionToPool();
        }
        System.out.println("CONNECTION POOL HAS BEEN CREATED");
    }

    private ConnectionThread addConnectionToPool() throws SQLException, InterruptedException {
        connectionThread = new ConnectionThread();
        connectionPool.add(connectionThread);
        return connectionThread;
    }

    private Optional<ConnectionThread> getFreeConnectionThread() {
        return getFreeConnectionList().stream().findFirst();
    }

    protected List<ConnectionThread> getFreeConnectionList() {
        return connectionPool.stream().filter(thread -> !thread.isBusy()).toList();
    }

    protected long getFreeConnectionsAmount() {
        return connectionPool.stream().filter(thread -> !thread.isBusy()).count();
    }

    public void returnConnection(ConnectionThread connectionThread) {
        connectionThread.setBusyFalse();
        threadAvailable.release();
    }

    public ConnectionThread getFreeConnection() throws SQLException, InterruptedException {
        ConnectionThread freeConnectionThread = null;
        connectionPoolAvailable.acquire();
        try {
            Optional<ConnectionThread> optionalConnectionThread = getFreeConnectionThread();
            if(optionalConnectionThread.isPresent() ){
                System.out.println("Mam wolne na liście");
                freeConnectionThread = optionalConnectionThread.get();
                freeConnectionThread.setBusyTrue();
            }else{
                System.out.println("Dorabiam wolne");
                if(threadAvailable.tryAcquire()) {
                    System.out.println("threadAvailable.availablePermits() " + threadAvailable.availablePermits());
                    freeConnectionThread = addConnectionToPool();
                    threadAvailable.acquire(1);
                    freeConnectionThread.setBusyTrue();
                }else {
                    System.out.println("NO FREE CONNECTIONS AVAILABLE, TRY AGAIN LATER!");
                }
            }
        } finally {
            connectionPoolAvailable.release();
        }
        return freeConnectionThread;
    }

    public void removeAllConnections() {
        System.out.println("Removing all connections!");
        while (!connectionPool.isEmpty()) {
            connectionPool.remove(0);
        }
    }
}
