package pl.rstepniewski.pool;

import pl.rstepniewski.pool.util.PropertiesUtil;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectionPool {
    private static final String POOL_INIT_SIZE = "pool.init.size";
    private static final String POOL_MAX_SIZE = "pool.max.size";
    private final List<DBConnection> connectionPool = new LinkedList<>();
    private final Semaphore updateSemaphore = new Semaphore(1);

    ReentrantLock myLonck = new ReentrantLock();
    private DBConnection dBConnection;

    public ConnectionPool() throws SQLException {
        initializePool();
    }

    public void initializePool() throws SQLException {
        System.out.println("POOL INITIALIZATION");
        for (int i = 0; i < PropertiesUtil.getNumber(POOL_INIT_SIZE) ; i++) {
            addConnectionToPool();
        }
        System.out.println("POOL CREATED rozmiar:" + connectionPool.size());
    }

    private DBConnection addConnectionToPool() throws SQLException {
        dBConnection = new DBConnection();
        connectionPool.add(dBConnection);
        return dBConnection;
    }

    private void acquireConnection(DBConnection dBConnection) {
        dBConnection.setFree(false);
    }

    public void releaseConnection(DBConnection dBConnection)  {
        dBConnection.setFree(true);
    }

    public DBConnection getFreeConnection() throws SQLException, InterruptedException {
        //updateSemaphore.acquire();
        myLonck.lock();
        try {
            if (connectionPool.stream().anyMatch(DBConnection::isFree)) {
                dBConnection = connectionPool
                        .stream()
                        .filter(DBConnection::isFree)
                        .findFirst()
                        .get();
            } else {
                if (connectionPool.size() < PropertiesUtil.getNumber(POOL_MAX_SIZE)) {
                    System.out.println("ADDING NEW CONNECTIONS");
                    dBConnection = addConnectionToPool();
                } else {
                    System.out.println("NO FREE CONNECTIONS AVAILABLE, TRY AGAIN LATER!");
                    while (connectionPool.size() > PropertiesUtil.getNumber(POOL_INIT_SIZE) ) {
                        Optional<DBConnection> dBConnection = connectionPool
                                .stream()
                                .filter(DBConnection::isFree)
                                .findFirst();
                        if(dBConnection.isPresent()) {
                            connectionPool.remove(dBConnection.get());
                            dBConnection.get(). closeConnection();
                        }
                    }
                }
            }
        } finally {
            dBConnection.setFree(false);
            //updateSemaphore.release();
            myLonck.unlock();
        }

        return dBConnection;
    }

    public void removeAllConnections() {
        System.out.println("Removing all connections!");
        connectionPool.clear();
    }
}
