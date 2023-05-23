package pl.rstepniewski.pool;

import org.jooq.DSLContext;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws SQLException, InterruptedException {
        System.out.println("Hello world!");
        ConnectionPool connectionPool= new ConnectionPool();
        DSLContext dslContext;
        ExecutorService executor = Executors.newFixedThreadPool(10000);
        connectionPool.createInitialPool();

        for (int i = 0; i < 101; i++) {
            final int pom = i;
            executor.submit(() -> {
                try {
                    ConnectionThread freeConnectionThead = connectionPool.getFreeConnection();
                    Connection freeConnection = freeConnectionThead.createConnection();
                        freeConnection
                                .createStatement()
                                .executeUpdate("INSERT INTO connection_pool_test VALUES ('test', 'test_text')");
                    if (pom % 10==0){
                        System.out.println("zwalniam jedno");
                        connectionPool.returnConnection(freeConnectionThead);
                    }
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