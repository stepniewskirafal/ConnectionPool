package pl.rstepniewski.pool;

import pl.rstepniewski.pool.util.PropertiesUtil;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectionPool {
    private static final String POOL_INIT_SIZE = "pool.init.size";
    private static final String POOL_MAX_SIZE = "pool.max.size";
    private final List<DBConnection> connectionPool = new LinkedList<>();
    private final ReentrantLock connectionPoolLock = new ReentrantLock();
    private DBConnection dBConnection;

    public ConnectionPool() throws SQLException {
        for (int i = 0; i < PropertiesUtil.getNumber(POOL_INIT_SIZE) ; i++) {
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
        }finally {
            if(dBConnection != null){
                markConnectionBussy(dBConnection);
            }
            connectionPoolLock.unlock();
        }
        return dBConnection;
    }

    private DBConnection acquireFirstFreeConnection() {
        return connectionPool
                .stream()
                .filter(DBConnection::isAvailable)
                .findFirst()
                .get();
    }

    private void removeRedundantConnections() throws SQLException {
        while (isConnectionPoolSizeGreaterThan(POOL_INIT_SIZE) ) {
            Optional<DBConnection> dBConnection = connectionPool
                    .stream()
                    .filter(DBConnection::isAvailable)
                    .findFirst();
            if(dBConnection.isPresent()) {
                connectionPool.remove(dBConnection.get());
                dBConnection.get(). closeConnection();
            }
        }
    }

    private boolean isConnectionPoolSizeGreaterThan(String size) {
        return connectionPool.size() < PropertiesUtil.getNumber(size);
    }

    private boolean isAnyConnectionFree() {
        return connectionPool.stream().anyMatch(DBConnection::isAvailable);
    }

    private void markConnectionBussy(DBConnection dBConnection) {
        dBConnection.setAvailable(false);
    }

    public void markConnectionFree(DBConnection dBConnection)  {
        dBConnection.setAvailable(true);
    }

    public void removeAllConnections() {
        connectionPool.clear();
    }
}
