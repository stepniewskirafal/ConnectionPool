package pl.rstepniewski.pool;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConnectionPoolTest {
    private ConnectionPool connectionPool;

    @BeforeEach
    void createPool() throws SQLException {
        connectionPool = new ConnectionPool();
    }

    @AfterEach
    void removeAll() throws SQLException {
        connectionPool
                .acquireConnection()
                .getConnection()
                .createStatement()
                .execute("TRUNCATE connection_pool_test");
        connectionPool.removeAllConnections();
    }

    @Test
    void connectionStressTest() throws InterruptedException, SQLException {
        ExecutorService executor = Executors.newFixedThreadPool(70);
        int expectedRows= 1_000;
        CountDownLatch latch = new CountDownLatch(expectedRows);
        for (int i = 0; i < expectedRows; i++) {
            executor.submit(() -> {
                try {
                    DBConnection freeConnection = connectionPool.acquireConnection();
                    freeConnection
                            .getConnection()
                            .createStatement()
                            .execute("INSERT INTO connection_pool_test VALUES ('test', 'test')");
                    connectionPool.markConnectionFree(freeConnection);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        ResultSet resultSet = connectionPool.acquireConnection()
                .getConnection()
                .createStatement()
                .executeQuery("SELECT COUNT(1) FROM connection_pool_test");
        resultSet.next();
        int counter = resultSet.getInt(1);
        assertEquals(expectedRows, counter);
    }

}