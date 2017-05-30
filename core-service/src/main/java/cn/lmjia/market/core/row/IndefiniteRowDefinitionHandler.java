package cn.lmjia.market.core.row;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * @author CJ
 */
@Component
public class IndefiniteRowDefinitionHandler implements HandlerMethodReturnValueHandler {
    private static final Log log = LogFactory.getLog(IndefiniteRowDefinitionHandler.class);

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return IndefiniteRowDefinition.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer
            , NativeWebRequest webRequest) throws Exception {
        IndefiniteRowDefinition rowDefinition = (IndefiniteRowDefinition) returnValue;
        if (rowDefinition == null) {
            throw new IllegalStateException("null can not work for Rows.");
        }

        RowCustom rowCustom = returnType.getMethodAnnotation(RowCustom.class);

        // 看看有没有
        RowDramatizer dramatizer;
        if (rowCustom != null) {
            dramatizer = rowCustom.dramatizer().newInstance();
        } else {
            dramatizer = new DefaultRowDramatizer();
        }

        List<IndefiniteFieldDefinition> fieldDefinitions = rowDefinition.fields();
        ////
        Query query = rowDefinition.createQuery(entityManager);
        List list = query
                .setFirstResult(dramatizer.queryOffset(webRequest))
                .setMaxResults(dramatizer.querySize(webRequest))
                .getResultList();

        log.debug("RW Result: total:-1, list:" + list + ", fields:" + fieldDefinitions.size());
        dramatizer.writeResponse(-1, list, fieldDefinitions, webRequest);
        mavContainer.setRequestHandled(true);
    }
}
