package pl.rstepniewski.pool;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by rafal on 24.05.2023
 *
 * @author : rafal
 * @date : 24.05.2023
 * @project : ConnectionPool
 */
public class Main {
    public static void main(String[] args) throws SQLException, InterruptedException {
        ConnectionPool connectionPool = new ConnectionPool();

        ExecutorService executor = Executors.newFixedThreadPool(500);
        int expectedRows= 500;
        for (int i = 0; i < expectedRows; i++) {
            executor.submit(() -> {
                try {
                    DBConnection freeConnection = connectionPool.getFreeConnection();
                    freeConnection.getConnection().createStatement().executeUpdate("INSERT INTO connection_pool_test VALUES ('test', 'test')");
                    connectionPool.releaseConnection(freeConnection);
                } catch (SQLException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
