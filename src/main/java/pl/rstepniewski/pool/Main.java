package pl.rstepniewski.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws SQLException, InterruptedException {
        System.out.println("Hello world!");
        ConnectionPool connectionPool= new ConnectionPool();
        ExecutorService executor = Executors.newFixedThreadPool(1000);
        connectionPool.createInitialPool();

        executor.submit(
                () -> {
                    try {
                        connectionPool.removeExcessiveConnections();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        for (int i = 0; i < 1000; i++) {
            final int pom = i;
            executor.submit(() -> {
                try {
                    ConnectionThread freeConnectionThead = connectionPool.getFreeConnection();
                    Connection freeConnection = freeConnectionThead.createConnection();
                    freeConnection
                        .createStatement()
                        .executeUpdate("INSERT INTO connection_pool_test VALUES ('test', 'test_text')");
                    connectionPool.returnConnection(freeConnectionThead);
                    System.out.println("STATS "
                            + "getFreeConnectionsAmount " + connectionPool.getFreeConnectionsAmount()
                            + "  getFreeThreadAvailable " + connectionPool.getFreeThreadAvailable());
                } catch (SQLException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
/*        ResultSet rs = ConnectionPool.getFreeConnection().createConnection().createStatement().executeQuery("select count(*) from data where Username ='test'");
        rs.next();
        int counter = rs.getInt(1);*/



    }
}