package cn.lmjia.market.core.jpa;

import me.jiangcai.lib.jdbc.ConnectionProvider;
import me.jiangcai.lib.seext.function.TriFunction;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueString;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author CJ
 */
public class JpaFunctionUtils {

    private static final DateTimeFormatter databaseFriendLyDateFormatter = DateTimeFormatter.ofPattern("yyyy-M-d"
            , Locale.CHINA);

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
     * @return {@link #DateEqual(CriteriaBuilder, Expression, Expression)}
     */
    public static Predicate DateEqual(CriteriaBuilder criteriaBuilder, Expression arg, LocalDate date) {
        return DateEqual(criteriaBuilder, arg, date.format(databaseFriendLyDateFormatter));
    }

    /**
     * 是指同一天
     *
     * @return Predicate for same date
     */
    public static <T> Predicate DateEqual(CriteriaBuilder criteriaBuilder, Expression arg, Expression<T> arg2) {
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
    public static Expression<String> LeftPaddingWith(CriteriaBuilder criteriaBuilder, Expression to, int length
            , char with) {
        return criteriaBuilder.function("LPAD", String.class, to
                , criteriaBuilder.literal(length), criteriaBuilder.literal(with));
    }

    /**
     * 类型需要一直
     *
     * @return 如果x非null则返回x, 否则返回y
     */
    public static <Y> Expression<Y> IfNull(CriteriaBuilder criteriaBuilder, Class<Y> type, Expression<Y> x
            , Expression<Y> y) {
        return criteriaBuilder.function("IFNULL", type, x, y);
    }

    /**
     * 类型需要一直
     *
     * @return expression?x:y
     */
    public static <Y> Expression<Y> IfElse(CriteriaBuilder criteriaBuilder, Class<Y> type, Expression<?> expression
            , Expression<Y> x, Expression<Y> y) {
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
                statement.executeUpdate("CREATE ALIAS IF NOT EXISTS `IF` FOR \""
                        + JpaFunctionUtils.class.getName() + ".H2If\"");
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

    /**
     * @param date 如果某参数为传入值则推荐使用字符串，可以避免因为数据库的异常而导致类型异常；通常各个数据库对于字符串都是比较友好的
     * @return 同年同月的谓语
     */
    public static Predicate YearAndMonthEqual(CriteriaBuilder cb, Expression arg, String date) {
        return YearAndMonthEqual(cb, arg, cb.literal(date));
    }

    /**
     * @return 同年同月的谓语
     */
    public static <T> Predicate YearAndMonthEqual(CriteriaBuilder criteriaBuilder, Expression arg, Expression<T> date) {
        return criteriaBuilder.and(
                criteriaBuilder.equal(criteriaBuilder.function("year", Integer.class, arg)
                        , criteriaBuilder.function("year", Integer.class, date))
                , criteriaBuilder.equal(criteriaBuilder.function("month", Integer.class, arg)
                        , criteriaBuilder.function("month", Integer.class, date))
        );
    }

    /**
     * @return 同年同月的谓语
     */
    public static Predicate YearAndMonthEqual(CriteriaBuilder cb, Expression arg, LocalDate date) {
        return YearAndMonthEqual(cb, arg, date.format(databaseFriendLyDateFormatter));
    }

    // YM 定义为Y*12+M

    /**
     * YM 定义为Y*12+M
     *
     * @return YM一致的谓语
     */
    public static Predicate YMEqual(CriteriaBuilder cb, Expression arg, LocalDate date) {
        return YMEqual(cb, arg, date.format(databaseFriendLyDateFormatter));
    }

    /**
     * YM 定义为Y*12+M
     *
     * @return YM一致的谓语
     */
    public static Predicate YMEqual(CriteriaBuilder cb, Expression arg, String date) {
        return YMEqual(cb, arg, cb.literal(date));
    }

    /**
     * YM 定义为Y*12+M
     *
     * @return YM一致的谓语
     */
    public static <T> Predicate YMEqual(CriteriaBuilder cb, Expression arg, Expression<T> date) {
        return YM(cb, arg, date, CriteriaBuilder::equal);
    }

    /**
     * YM 定义为Y*12+M
     *
     * @param ymPredicateGenerator 条件生成器
     * @return YM符合'条件生成器'的谓语
     */
    public static Predicate YM(CriteriaBuilder cb, Expression arg, LocalDate date
            , TriFunction<CriteriaBuilder, Expression<Integer>, Expression<Integer>, Predicate> ymPredicateGenerator) {
        return YM(cb, arg, date.format(databaseFriendLyDateFormatter), ymPredicateGenerator);
    }

    /**
     * YM 定义为Y*12+M
     *
     * @param ymPredicateGenerator 条件生成器
     * @return YM符合'条件生成器'的谓语
     */
    public static Predicate YM(CriteriaBuilder cb, Expression arg, String date
            , TriFunction<CriteriaBuilder, Expression<Integer>, Expression<Integer>, Predicate> ymPredicateGenerator) {
        return YM(cb, arg, cb.literal(date), ymPredicateGenerator);
    }

    /**
     * YM 定义为Y*12+M
     *
     * @param ymPredicateGenerator 条件生成器
     * @return YM符合'条件生成器'的谓语
     */
    public static <T> Predicate YM(CriteriaBuilder cb, Expression arg, Expression<T> date
            , TriFunction<CriteriaBuilder, Expression<Integer>, Expression<Integer>, Predicate> ymPredicateGenerator) {
        return ymPredicateGenerator.apply(cb, getYM(cb, arg)
                , getYM(cb, date)
        );
    }

    private static Expression<Integer> getYM(CriteriaBuilder cb, Expression arg) {
        return cb.sum(
                cb.prod(cb.function("year", Integer.class, arg), 12)
                , cb.function("month", Integer.class, arg));
    }

    /**
     * @param date                    如果某参数为传入值则推荐使用字符串，可以避免因为数据库的异常而导致类型异常；通常各个数据库对于字符串都是比较友好的
     * @param monthPredicateGenerator 可选的关于月的谓语
     * @return 同年谓语
     */
    public static Predicate YearEqual(CriteriaBuilder cb, Expression arg, String date
            , TriFunction<CriteriaBuilder, Expression<Integer>, Expression<Integer>, Predicate> monthPredicateGenerator) {
        return YearEqual(cb, arg, cb.literal(date), monthPredicateGenerator);
    }

    /**
     * @param monthPredicateGenerator 可选的关于月的谓语
     * @return 同年谓语
     */
    public static Predicate YearEqual(CriteriaBuilder cb, Expression arg, LocalDate date
            , TriFunction<CriteriaBuilder, Expression<Integer>, Expression<Integer>, Predicate> monthPredicateGenerator) {
        return YearEqual(cb, arg, date.format(databaseFriendLyDateFormatter), monthPredicateGenerator);
    }

    /**
     * @param monthPredicateGenerator 可选的关于月的谓语
     * @return 同年谓语
     */
    public static <T> Predicate YearEqual(CriteriaBuilder criteriaBuilder, Expression arg, Expression<T> date
            , TriFunction<CriteriaBuilder, Expression<Integer>, Expression<Integer>, Predicate> monthPredicateGenerator) {
        final Predicate baseEqual = criteriaBuilder.equal(criteriaBuilder.function("year", Integer.class, arg)
                , criteriaBuilder.function("year", Integer.class, date));
        if (monthPredicateGenerator == null) {
            return baseEqual;
        }
        Predicate predicate = monthPredicateGenerator.apply(
                criteriaBuilder, criteriaBuilder.function("month", Integer.class, arg)
                , criteriaBuilder.function("month", Integer.class, date)
        );
        if (predicate == null)
            return baseEqual;

        return criteriaBuilder.and(
                baseEqual
                , predicate
        );
    }

    /**
     * @return 将args使用 {@link CriteriaBuilder#concat(Expression, Expression)}链接起来
     */
    @SafeVarargs
    public static Expression<String> Contact(CriteriaBuilder criteriaBuilder, Expression<String>... args) {
        if (args.length == 1)
            return criteriaBuilder.concat(args[0], criteriaBuilder.literal(""));
        if (args.length == 2)
            return criteriaBuilder.concat(args[0], args[1]);
        // 合并
        @SuppressWarnings("unchecked")
        Expression<String>[] newArgs = (Expression<String>[]) Array.newInstance(Expression.class
                , args.length / 2 + (args.length % 2 == 1 ? 1 : 0));
        for (int i = 0; i < newArgs.length; i++) {
            // 有2个么？
            // 最后一个而且有多的
            if (i == newArgs.length - 1 && args.length % 2 == 1)
                newArgs[i] = args[i * 2];
            else
                newArgs[i] = criteriaBuilder.concat(args[i * 2], args[i * 2 + 1]);
        }
        return Contact(criteriaBuilder, newArgs);
    }
}
