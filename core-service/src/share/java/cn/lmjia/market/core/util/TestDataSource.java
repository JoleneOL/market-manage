package cn.lmjia.market.core.util;

import com.mysql.jdbc.MySQLConnection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author CJ
 */
public class TestDataSource extends DriverManagerDataSource {

    private static final Log log = LogFactory.getLog(TestDataSource.class);

    @Override
    public Connection getConnection() throws SQLException {
        final Connection connection = super.getConnection();
        forConnection(connection);
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        final Connection connection = super.getConnection(username, password);
        forConnection(connection);
        return connection;
    }

    private void forConnection(Connection connection) {
        if (connection instanceof MySQLConnection) {
            log.info("Mysql: " + ((MySQLConnection) connection).getId());
        }
    }
}
