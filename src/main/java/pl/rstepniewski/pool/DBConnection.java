package pl.rstepniewski.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {
    private final Connection connection;
    private boolean isFree;
    public DBConnection() throws SQLException {
        this.connection = createConnection();
        this.isFree = true;
    }
    public Connection createConnection() throws SQLException {
        String user = "root";
        String password = "admin";
        String url = "jdbc:mysql://localhost:3306/ConnectionPool?serverTimezone=UTC";
        return DriverManager.getConnection(url, user, password);
    }
    public Connection getConnection() {
        return connection;
    }
    public boolean isFree() {
        return isFree;
    }
    public void setFree(boolean free) {
        isFree = free;
    }
    public void closeConnection() throws SQLException {
        this.connection.close();
    }
}
