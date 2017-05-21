package cn.lmjia.market.core.jpa;

import me.jiangcai.lib.jdbc.ConnectionProvider;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author CJ
 */
public class JpaUtils {


    /**
     * 左边填充
     *
     * @param criteriaBuilder cb
     * @param to              来源表达式
     * @param length          达到长度目标
     * @param with            使用什么字符填充
     * @return 「左边填充」的表达式
     */
    public static Expression<String> LeftPaddingWith(CriteriaBuilder criteriaBuilder, Expression to, int length, char with) {
        return criteriaBuilder.function("LPAD", String.class, to
                , criteriaBuilder.literal(length), criteriaBuilder.literal(with));
    }

    /**
     * 类型需要一直
     *
     * @return 如果x非null则返回x, 否则返回y
     */
    public static <Y> Expression<Y> ifNull(CriteriaBuilder criteriaBuilder, Class<Y> type, Expression<Y> x, Expression<Y> y) {
        return criteriaBuilder.function("IFNULL", type, x, y);
    }

    /**
     * 类型需要一直
     *
     * @return expression?x:y
     */
    public static <Y> Expression<Y> ifElse(CriteriaBuilder criteriaBuilder, Class<Y> type, Expression<?> expression, Expression<Y> x, Expression<Y> y) {
        return criteriaBuilder.function("IF", type, expression, x, y);
    }

    /**
     * 增强当前数据库以符合该类规则
     *
     * @param connection 链接
     * @throws SQLException
     */
    public static void Enhance(ConnectionProvider connection) throws SQLException {
        if (connection.profile().isH2()) {
            try (Statement statement = connection.getConnection().createStatement()) {
                statement.executeUpdate("DROP ALIAS IF EXISTS `IF`");
                statement.executeUpdate("CREATE ALIAS IF NOT EXISTS `IF` FOR \"" + JpaUtils.class.getName() + ".H2If\"");
            }
        }
    }

    public static Object H2If(Connection connection, boolean exp, Object var, Object var2) {
        return exp ? var : var2;
    }
}
