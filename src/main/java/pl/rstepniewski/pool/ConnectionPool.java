package pl.rstepniewski.pool;

import pl.rstepniewski.pool.util.PropertiesUtil;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectionPool {
    private static final int POOL_INIT_SIZE = PropertiesUtil.getNumber("pool.init.size");
    private static final int POOL_MAX_SIZE = PropertiesUtil.getNumber("pool.max.size");
    private final List<DBConnection> connectionPool = new LinkedList<>();
    private final ReentrantLock connectionPoolLock = new ReentrantLock();

    public ConnectionPool() throws SQLException {
        for (int i = 0; i < POOL_INIT_SIZE ; i++) {
            addNewConnectionToPool(createNewConnection());
        }
    }

    private void addNewConnectionToPool(DBConnection dBConnection) {
        connectionPool.add(dBConnection);
    }

    private DBConnection createNewConnection() throws SQLException {
        return new DBConnection();
    }

    public DBConnection acquireConnection() throws SQLException{
        connectionPoolLock.lock();
        DBConnection dBConnection = null;
        try {
            if (isAnyConnectionFree()) {
                dBConnection = acquireFirstFreeConnection();
            } else {
                if (!isConnectionPoolSizeGreaterThan(POOL_MAX_SIZE)) {
                    dBConnection = createNewConnection();
                    addNewConnectionToPool(dBConnection);
                } else {
                    System.out.println("NO FREE CONNECTIONS AVAILABLE, TRY AGAIN LATER!");
                    removeRedundantConnections();
                }
            }
            markConnectionBusy(dBConnection);
        }finally {
            connectionPoolLock.unlock();
        }
        return dBConnection;
    }

    private DBConnection acquireFirstFreeConnection() throws SQLException {
        DBConnection dBConnection = null;
        while (dBConnection == null) {
            dBConnection = connectionPool
                    .stream()
                    .filter(DBConnection::isAvailable)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("No free connections available"));
            dBConnection = (dBConnection.getConnection().isValid(1)) ? dBConnection : null;
        }
        return dBConnection;
    }

    private void removeRedundantConnections() {
        while (isConnectionPoolSizeGreaterThan(POOL_INIT_SIZE) ) {
            connectionPool
                    .stream()
                    .filter(DBConnection::isAvailable)
                    .findFirst()
                    .ifPresent(dbConnection -> {
                        connectionPool.remove(dbConnection);
                        dbConnection.closeConnection();
                    });
        }
    }

    private boolean isConnectionPoolSizeGreaterThan(int size) {
        return connectionPool.size() > size;
    }

    private boolean isAnyConnectionFree() {
        return connectionPool.stream().anyMatch(DBConnection::isAvailable);
    }

    private void markConnectionBusy(DBConnection dBConnection) {
        if (dBConnection != null) {
            dBConnection.setAvailable(false);
        }
    }

    public void markConnectionFree(DBConnection dBConnection)  {
        if (dBConnection != null) {
            dBConnection.setAvailable(true);
        }
    }

    public void removeAllConnections() {
        connectionPool.forEach(DBConnection::closeConnection);
        connectionPool.clear();
    }
}
