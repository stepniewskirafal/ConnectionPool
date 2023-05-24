package pl.rstepniewski.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws SQLException, InterruptedException {
        System.out.println("Hello world!");
        ConnectionPool connectionPool = new ConnectionPool();
        ExecutorService executor = Executors.newFixedThreadPool(100);
        connectionPool.createInitialPool();

        for (int i = 0; i < 500; i++) {
            final int pom = i;
            executor.submit(() -> {
                try {
                    ConnectionThread freeConnectionThead = connectionPool.getFreeConnection();
                    Connection freeConnection = freeConnectionThead.createConnection();
                    freeConnection
                            .createStatement()
                            .executeUpdate("INSERT INTO connection_pool_test VALUES ('test', 'test_text')");
                    connectionPool.returnConnection(freeConnectionThead);
                } catch (SQLException | InterruptedException e) {
                    e.printStackTrace();
                }

            });
        }
        try {
            connectionPool.removeAllConnections();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}