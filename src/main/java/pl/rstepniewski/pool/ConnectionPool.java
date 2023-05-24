package pl.rstepniewski.pool;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

public class ConnectionPool {

    private static final int INITIAL_POOL_SIZE = 10;
    private static final int MAX_POOL_SIZE = 100;
    private final List<DBConnection> connectionPool = new ArrayList<>(INITIAL_POOL_SIZE);
    private final Semaphore poolSemaphore = new Semaphore(1);
    private int counter = 0;

    private Timer timer;

    private DBConnection singleConnection;

    public void createInitialPool() throws SQLException {
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            System.out.println("CREATING POOL");
            addConnectionToPool();
        }
        runSchedule();
    }

    private void runSchedule() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    removeExcessiveConnections();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        // Uruchomienie zadania co 1 sekundę (1000 milisekund)
        timer.schedule(task, 0, 500);
    }

    private DBConnection addConnectionToPool() throws SQLException {
        singleConnection = new DBConnection();
        connectionPool.add(singleConnection);
        return singleConnection;
    }

    private List<DBConnection> getFreeConnectionList() {
        return connectionPool.stream().filter(DBConnection::isFree).toList();
    }

    private long getFreeConnectionsAmount() {
        return connectionPool.stream().filter(DBConnection::isFree).count();
    }

    private void takeConnection(DBConnection singleConnection) {
        singleConnection.setFree(false);
    }

    public void returnConnection(DBConnection singleConnection) {
        singleConnection.setFree(true);
    }

    public DBConnection getFreeConnection() throws SQLException, InterruptedException {
        poolSemaphore.acquire();
        try {
            if (connectionPool.stream().anyMatch(DBConnection::isFree)) {
                singleConnection = connectionPool.stream().filter(DBConnection::isFree).findFirst().get();
                takeConnection(singleConnection);
            } else {
                if (connectionPool.size() < MAX_POOL_SIZE) {
                    System.out.println("ADDING NEW CONNECTIONS");
                    singleConnection = addConnectionToPool();
                    takeConnection(singleConnection);
                } else {
                    System.out.println("NO FREE CONNECTIONS AVAILABLE, TRY AGAIN LATER!");
                }
            }
        } finally {
            poolSemaphore.release();
        }
        return singleConnection;
    }

    private void removeExcessiveConnections() throws SQLException, InterruptedException {
        System.out.println("removeExcessiveConnections PROCEDURE");
        while (getFreeConnectionsAmount() > INITIAL_POOL_SIZE) {
            poolSemaphore.acquire();
            singleConnection = getFreeConnectionList().get(0);
            singleConnection.closeConnection();
            connectionPool.remove(singleConnection);
            poolSemaphore.release();
        }
    }

    // Only for cleaning after test
    public void removeAllConnections() {
        System.out.println("Removing all connections!");
        while (!connectionPool.isEmpty()) {
            connectionPool.remove(0);
        }
    }



}
