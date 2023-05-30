package pl.rstepniewski.pool;

import pl.rstepniewski.pool.util.PropertiesUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {
    private static final String URL_KEY = PropertiesUtil.getString("db.url");
    private static final String USERNAME_KEY = PropertiesUtil.getString("db.username");
    private static final String PASSWORD_KEY = PropertiesUtil.getString("db.password");
    private final Connection connection;
    private boolean available;

    protected DBConnection() throws SQLException {
        this.connection = createConnection();
        this.available = true;
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(URL_KEY, USERNAME_KEY, PASSWORD_KEY);
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

    protected void closeConnection() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
