package cn.lmjia.market.core.selection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
@Component
public class RowDefinitionHandler implements HandlerMethodReturnValueHandler {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return RowDefinition.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer
            , NativeWebRequest webRequest) throws Exception {
        RowDefinition<?> rowDefinition = (RowDefinition) returnValue;
        if (rowDefinition == null) {
            throw new IllegalStateException("null can not work for Rows.");
        }

        RowCustom rowCustom = returnType.getMethodAnnotation(RowCustom.class);

        // 看看有没有
        RowDramatizer dramatizer;
        if (rowCustom != null)
            dramatizer = rowCustom.dramatizer().newInstance();
        else
            dramatizer = new DefaultRowDramatizer();

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<?> originDataQuery = criteriaBuilder.createQuery();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);

        Root<?> root = originDataQuery.from(rowDefinition.entityClass());
        Root<?> countRoot = countQuery.from(rowDefinition.entityClass());

        CriteriaQuery<?> dataQuery = originDataQuery.multiselect(rowDefinition.fields().stream()
                .map(field
                        -> field.select(criteriaBuilder, originDataQuery, root))
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        countQuery = countQuery.select(criteriaBuilder.count(countRoot));

        // where
        dataQuery = where(criteriaBuilder, dataQuery, root, rowDefinition);
        countQuery = where(criteriaBuilder, countQuery, countRoot, rowDefinition);

        // sort
        dataQuery = dataQuery.orderBy(dramatizer.order(rowDefinition.fields(), webRequest, criteriaBuilder, root));

        // 打包成Object[]
        long total = entityManager.createQuery(countQuery).getSingleResult();
        List<?> list = entityManager.createQuery(dataQuery)
                .setFirstResult(dramatizer.queryOffset(webRequest))
                .setMaxResults(dramatizer.querySize(webRequest))
                .getResultList();

        // 输出到结果
        dramatizer.writeResponse(total, list, rowDefinition.fields(), webRequest);
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
