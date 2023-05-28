package pl.rstepniewski.pool;

import pl.rstepniewski.pool.util.PropertiesUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {
    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";
    private final Connection connection;
    private boolean available;

    protected DBConnection() throws SQLException {
        this.connection = createConnection();
        this.available = true;
    }

    private Connection createConnection() throws SQLException {
        var user = PropertiesUtil.getString(USERNAME_KEY);
        var password = PropertiesUtil.getString(PASSWORD_KEY);
        var url = PropertiesUtil.getString(URL_KEY);
        return DriverManager.getConnection(url, user, password);
    }

    protected Connection getConnection() {
        return connection;
    }

    protected boolean isAvailable() {
        return available;
    }

    protected void setAvailable(boolean available) {
        this.available = available;
    }

    protected void closeConnection() throws SQLException {
        this.connection.close();
    }
}
