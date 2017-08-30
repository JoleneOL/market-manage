package cn.lmjia.market.dealer.h2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author CJ
 */
@SuppressWarnings("unused")
public class Functions {

    public static int loginBelongs(Connection connection, long id, long superior) throws SQLException {
        try (PreparedStatement preparedStatement
                     = connection.prepareStatement("SELECT count(t.`ID`) FROM `loginrelation` AS t WHERE t.`FROM_ID`=? AND t.`TO_ID`=?")) {
            preparedStatement.setLong(1, superior);
            preparedStatement.setLong(2, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) > 0 ? 1 : 0;
            }
        }
    }

    public static int agentBelongs(Connection connection, long id, long superior) throws SQLException {
        long currentId = id;
        try (PreparedStatement superStatement
                     = connection.prepareStatement("SELECT `SUPERIOR_ID` FROM `AGENTLEVEL` WHERE `ID`=?")) {
            while (true) {
                if (currentId == superior)
                    return 1;
                superStatement.setLong(1, currentId);
                try (ResultSet resultSet = superStatement.executeQuery()) {
                    resultSet.next();
                    long lastId = resultSet.getLong(1);
                    if (lastId == 0) {
                        return 0;
                    }
                    currentId = lastId;
                }
            }
        }
    }

    /**
     * 根据输入的代理商id获取它的等级；
     *
     * @param connection 当前链接
     * @param agentId    id
     * @return 规则上认同 {@link cn.lmjia.market.dealer.service.AgentService#agentLevel(cn.lmjia.market.core.entity.deal.AgentLevel)}
     * @throws IOException
     * @throws SQLException
     */
    public static int agentLevel(Connection connection, long agentId) throws IOException, SQLException {
        // 找到没有上级的 那是0
        int pass = 0;
        long currentId = agentId;
        try (PreparedStatement superStatement
                     = connection.prepareStatement("SELECT `SUPERIOR_ID` FROM `AGENTLEVEL` WHERE `ID`=?")) {
            while (true) {
                superStatement.setLong(1, currentId);
                try (ResultSet resultSet = superStatement.executeQuery()) {
                    resultSet.next();
                    long lastId = resultSet.getLong(1);
                    if (lastId == 0) {
                        return pass;
                    }
                    pass++;
                    currentId = lastId;
                }
            }
        }

    }
}
