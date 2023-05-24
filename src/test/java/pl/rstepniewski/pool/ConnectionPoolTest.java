package pl.rstepniewski.pool;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConnectionPoolTest {
    ConnectionPool connectionPool = new ConnectionPool();

    @BeforeEach
    void createPool() throws SQLException, InterruptedException {
        ConnectionPool connectionPool = new ConnectionPool();
        connectionPool.createInitialPool();
    }

    @AfterEach
    void removeAll() throws SQLException, InterruptedException {
        connectionPool.getFreeConnection().createConnection().createStatement().executeUpdate("delete from connection_pool_test where 1 =1");
        connectionPool.removeAllConnections();
    }

    @Test
    void connectionStressTest() throws InterruptedException, SQLException {
        ExecutorService executor = Executors.newFixedThreadPool(100);
        int expectedRows= 1_000;
        for (int i = 0; i < expectedRows; i++) {
            executor.submit(() -> {
                try {
                    DBConnection freeConnection = connectionPool.getFreeConnection();
                    freeConnection.createConnection().createStatement().executeUpdate("INSERT INTO connection_pool_test VALUES ('test', 'test')");
                    connectionPool.returnConnection(freeConnection);
                } catch (SQLException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        ResultSet resultSet = connectionPool.getFreeConnection().createConnection().createStatement().executeQuery("select count(1) from connection_pool_test");
        resultSet.next();
        int counter = resultSet.getInt(1);
        assertEquals(expectedRows, counter);
    }

}