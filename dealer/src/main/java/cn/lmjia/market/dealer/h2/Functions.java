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
