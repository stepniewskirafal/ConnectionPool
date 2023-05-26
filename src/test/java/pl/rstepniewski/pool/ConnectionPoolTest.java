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
    void createPool() throws SQLException, InterruptedException {
        connectionPool = new ConnectionPool();
    }

    @AfterEach
    void removeAll() throws SQLException, InterruptedException {
        connectionPool
                .getFreeConnection()
                .getConnection()
                .createStatement()
                .executeUpdate("TRUNCATE connection_pool_test");
        connectionPool.removeAllConnections();
    }

    @Test
    void connectionStressTest() throws InterruptedException, SQLException {
        ExecutorService executor = Executors.newFixedThreadPool(10_000);
        int expectedRows= 10_000;
        CountDownLatch latch = new CountDownLatch(expectedRows);
        for (int i = 0; i < expectedRows; i++) {
            executor.submit(() -> {
                try {
                    DBConnection freeConnection = connectionPool.getFreeConnection();
                    freeConnection
                            .getConnection()
                            .createStatement()
                            .executeUpdate("INSERT INTO connection_pool_test VALUES ('test', 'test')");
                    connectionPool.releaseConnection(freeConnection);
                } catch (SQLException | InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        ResultSet resultSet = connectionPool.getFreeConnection()
                .getConnection()
                .createStatement()
                .executeQuery("select count(1) from connection_pool_test");
        resultSet.next();
        int counter = resultSet.getInt(1);
        assertEquals(expectedRows, counter);
    }

}