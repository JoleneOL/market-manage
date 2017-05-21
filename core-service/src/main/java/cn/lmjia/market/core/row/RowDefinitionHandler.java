package cn.lmjia.market.core.row;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
@Component
public class RowDefinitionHandler implements HandlerMethodReturnValueHandler {

    private static final Log log = LogFactory.getLog(RowDefinitionHandler.class);
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return RowDefinition.class.isAssignableFrom(returnType.getParameterType());
    }

    //    @SuppressWarnings("unchecked")
    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer
            , NativeWebRequest webRequest) throws Exception {
        RowDefinition rowDefinition = (RowDefinition) returnValue;
        if (rowDefinition == null) {
            throw new IllegalStateException("null can not work for Rows.");
        }

        RowCustom rowCustom = returnType.getMethodAnnotation(RowCustom.class);

        // 看看有没有
        RowDramatizer dramatizer;
        boolean distinct;
        if (rowCustom != null) {
            dramatizer = rowCustom.dramatizer().newInstance();
            distinct = rowCustom.distinct();
        } else {
            dramatizer = new DefaultRowDramatizer();
            distinct = false;
        }

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery originDataQuery = criteriaBuilder.createQuery();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);

        Root root = originDataQuery.from(rowDefinition.entityClass());
        Root countRoot = countQuery.from(rowDefinition.entityClass());

        final List<FieldDefinition> fieldDefinitions = rowDefinition.fields();
        CriteriaQuery dataQuery = originDataQuery.multiselect(fieldDefinitions.stream()
                .map(new Function<FieldDefinition, Selection>() {
                    @Override
                    public Selection apply(FieldDefinition fieldDefinition) {
//                        field
//                                -> field.select(criteriaBuilder, originDataQuery, root)
                        return fieldDefinition.select(criteriaBuilder, originDataQuery, root);
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));


        if (distinct)
            countQuery = countQuery.select(criteriaBuilder.countDistinct(countRoot));
        else
            countQuery = countQuery.select(criteriaBuilder.count(countRoot));

        // where
        dataQuery = where(criteriaBuilder, dataQuery, root, rowDefinition);
        countQuery = where(criteriaBuilder, countQuery, countRoot, rowDefinition);

        // Distinct
        if (distinct)
            dataQuery = dataQuery.distinct(true);

        // sort
        final List<Order> order = dramatizer.order(fieldDefinitions, webRequest, criteriaBuilder, root);
        if (!CollectionUtils.isEmpty(order))
            dataQuery = dataQuery.orderBy(order);

        // 打包成Object[]
        long total = entityManager.createQuery(countQuery).getSingleResult();
        List<?> list = entityManager.createQuery(dataQuery)
                .setFirstResult(dramatizer.queryOffset(webRequest))
                .setMaxResults(dramatizer.querySize(webRequest))
                .getResultList();

        // 输出到结果
        log.debug("RW Result: total:" + total + ", list:" + list + ", fields:" + fieldDefinitions.size());
        dramatizer.writeResponse(total, list, fieldDefinitions, webRequest);
        mavContainer.setRequestHandled(true);
    }

    private <T> CriteriaQuery<T> where(CriteriaBuilder criteriaBuilder, CriteriaQuery<T> query, Root<?> root
            , RowDefinition<?> rowDefinition) {
        final Specification<?> specification = rowDefinition.specification();
        if (specification == null)
            return query;
        //noinspection unchecked
        return query.where(specification.toPredicate((Root) root, query, criteriaBuilder));
    }
}
