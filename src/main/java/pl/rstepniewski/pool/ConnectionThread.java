package pl.rstepniewski.pool;

/**
 * Created by rafal on 22.05.2023
 *
 * @author : rafal
 * @date : 22.05.2023
 * @project : ConnectionPool
 */
import pl.rstepniewski.pool.util.PropertiesUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionThread {
    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";
    private final Connection connection;
    private boolean isBussy;


    public ConnectionThread(Connection connection, boolean isBussy) {
        this.connection = connection;
        this.isBussy = isBussy;
    }

    public Connection getConnection() throws SQLException {
        var user = PropertiesUtil.get(USERNAME_KEY);
        var password = PropertiesUtil.get(PASSWORD_KEY);
        var url = PropertiesUtil.get(URL_KEY);

        return DriverManager.getConnection(url, user, password);
    }

    public boolean isBussy() {
        return isBussy;
    }

    public void setFree(boolean free) {
        isBussy = free;
    }

    public void closeConnection() throws SQLException {
        this.connection.close();
    }

}