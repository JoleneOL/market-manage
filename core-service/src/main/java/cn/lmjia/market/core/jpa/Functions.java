package cn.lmjia.market.core.jpa;

import cn.lmjia.market.core.entity.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author CJ
 */
@SuppressWarnings("unused")
public class Functions {

    /**
     * 任何登录的 level 查询代理表 存在则获取最低的；反之则查询customer 而且success 则100 否者200
     *
     * @return
     */
    public static int loginAgentLevel(Connection connection, long id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT min(`LEVEL`) FROM `agentLevel` WHERE `LOGIN_ID`=?")) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.isLast() && resultSet.next() && resultSet.getObject(1) != null) {
                    return resultSet.getInt(1);
                }
            }
        }

        try (PreparedStatement statement = connection.prepareStatement("SELECT `id` FROM `MainOrder` WHERE `ORDERBY_ID`=? AND `ORDERSTATUS` IN (2,3,4,5)")) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.isLast() && resultSet.next()) {
                    return Customer.LEVEL;
                }
            }
        }

        return Customer.LEVEL * 2;
    }

}
