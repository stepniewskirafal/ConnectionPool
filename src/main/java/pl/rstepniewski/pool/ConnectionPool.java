package pl.rstepniewski.pool;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Semaphore;

public class ConnectionPool {
    private static final int INITIAL_POOL_SIZE = 10;
    private static final int MAX_POOL_SIZE = 100;
    private final List<DBConnection> connectionPool = new LinkedList<>();
    private final Semaphore updateSemaphore = new Semaphore(1);
    private final Semaphore dBConnectionSemaphore = new Semaphore(1);
    private DBConnection dBConnection;

    public void initializePool() throws SQLException {
        System.out.println("POOL INITIALIZATION");
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            addConnectionToPool();
        }
        System.out.println("POOL CREATED");
    }

    private DBConnection addConnectionToPool() throws SQLException {
        dBConnection = new DBConnection();
        connectionPool.add(dBConnection);
        return dBConnection;
    }

    private void acquireConnection(DBConnection dBConnection) throws InterruptedException {
        //dBConnectionSemaphore.acquire(1);
        dBConnection.setFree(false);
        //dBConnectionSemaphore.release(1);
    }

    public void releaseConnection(DBConnection dBConnection) throws InterruptedException {
        //dBConnectionSemaphore.acquire(1);
        dBConnection.setFree(true);
        //dBConnectionSemaphore.release(1);
    }

    public DBConnection getFreeConnection() throws SQLException, InterruptedException {
        updateSemaphore.acquire();
        try {
            if (connectionPool.stream().anyMatch(DBConnection::isFree)) {
                dBConnection = connectionPool
                        .stream()
                        .filter(DBConnection::isFree)
                        .findFirst()
                        .get();
                acquireConnection(dBConnection);
            } else {
                if (connectionPool.size() < MAX_POOL_SIZE) {
                    System.out.println("ADDING NEW CONNECTIONS");
                    dBConnection = addConnectionToPool();
                    acquireConnection(dBConnection);
                } else {
                    System.out.println("NO FREE CONNECTIONS AVAILABLE, TRY AGAIN LATER!");
                    while (connectionPool.size() > INITIAL_POOL_SIZE) {
                        Optional<DBConnection> dBConnection = connectionPool
                                .stream()
                                .filter(DBConnection::isFree)
                                .findFirst();
                        if(dBConnection.isPresent()) {
                            connectionPool.remove(dBConnection.get());
                            dBConnection.get().closeConnection();
                        }
                    }
                }
            }
        } finally {
            updateSemaphore.release();
        }
        return dBConnection;
    }

    public void removeAllConnections() {
        System.out.println("Removing all connections!");
        connectionPool.clear();
    }
}
