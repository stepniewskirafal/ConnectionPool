package pl.rstepniewski.pool;

import pl.rstepniewski.pool.util.PropertiesUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionThread {
    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";
    private Connection connection;
    private boolean isBusy;
    private final Object lock = new Object();

    public ConnectionThread() throws SQLException {
        this.connection = createConnection();
        this.isBusy = false;
    }

    public Connection createConnection() throws SQLException {
        var user = PropertiesUtil.getString(USERNAME_KEY);
        var password = PropertiesUtil.getString(PASSWORD_KEY);
        var url = PropertiesUtil.getString(URL_KEY);

        return DriverManager.getConnection(url, user, password);
    }

    public boolean isBusy() {
        synchronized (lock) {
            return isBusy;
        }
    }

    public void setBusyTrue() {
        synchronized (lock) {
            isBusy = true;
        }
    }

    public void setBusyFalse() {
        synchronized (lock) {
            isBusy = false;
        }
    }

    public void closeConnection() throws SQLException {
        synchronized (lock) {
            this.connection.close();
        }
    }
}
