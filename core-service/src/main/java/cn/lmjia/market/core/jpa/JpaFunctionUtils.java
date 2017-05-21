package cn.lmjia.market.core.jpa;

import me.jiangcai.lib.jdbc.ConnectionProvider;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueString;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author CJ
 */
public class JpaFunctionUtils {

//    /**
//     * @return arg-arg2;Only the date parts of the values are used in the calculation.
//     */
//    public static Expression<Integer> DateDiff(CriteriaBuilder criteriaBuilder, Expression arg, Expression arg2) {
//        return criteriaBuilder.function("DATEDIFF", Integer.class, arg, arg2);
//    }

    /**
     * @param date 如果某参数为传入值则推荐使用字符串，可以避免因为数据库的异常而导致类型异常；通常各个数据库对于字符串都是比较友好的
     * @return Predicate for same date
     */
    public static Predicate DateEqual(CriteriaBuilder criteriaBuilder, Expression arg, String date) {
        return DateEqual(criteriaBuilder, arg, criteriaBuilder.literal(date));
    }

    /**
     * @return Predicate for same date
     */
    public static Predicate DateEqual(CriteriaBuilder criteriaBuilder, Expression arg, Expression arg2) {
        return criteriaBuilder.and(
                criteriaBuilder.equal(criteriaBuilder.function("year", Integer.class, arg)
                        , criteriaBuilder.function("year", Integer.class, arg2))
                , criteriaBuilder.equal(criteriaBuilder.function("month", Integer.class, arg)
                        , criteriaBuilder.function("month", Integer.class, arg2))
                , criteriaBuilder.equal(criteriaBuilder.function("day", Integer.class, arg)
                        , criteriaBuilder.function("day", Integer.class, arg2))
        );
    }


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
    public static <Y> Expression<Y> IfNull(CriteriaBuilder criteriaBuilder, Class<Y> type, Expression<Y> x, Expression<Y> y) {
        return criteriaBuilder.function("IFNULL", type, x, y);
    }

    /**
     * 类型需要一直
     *
     * @return expression?x:y
     */
    public static <Y> Expression<Y> IfElse(CriteriaBuilder criteriaBuilder, Class<Y> type, Expression<?> expression, Expression<Y> x, Expression<Y> y) {
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
                statement.executeUpdate("CREATE ALIAS IF NOT EXISTS `IF` FOR \"" + JpaFunctionUtils.class.getName() + ".H2If\"");
//
//                statement.executeUpdate("DROP ALIAS IF EXISTS `DATEDIFF`");
//                statement.executeUpdate("CREATE ALIAS IF NOT EXISTS `DATEDIFF` FOR \"" + JpaFunctionUtils.class.getName() + ".H2DATEDIFF\"");

            }
        }
    }

    public static Object H2If(Value exp, Value var, Value var2) {
        Value result = exp.getByte() != 0 ? var : var2;
        if (result instanceof ValueNull)
            return null;
        if (result instanceof ValueString)
            return result.getString();
        throw new IllegalStateException("I can do nothing. for:" + result);
    }

}
