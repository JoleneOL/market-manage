package cn.lmjia.market.core.data_table;

import org.springframework.core.MethodParameter;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Method;

/**
 * draw:1
 * columns[0][data]:function
 * columns[0][name]:
 * columns[0][searchable]:true
 * columns[0][orderable]:false
 * columns[0][search][value]:
 * columns[0][search][regex]:false
 * columns[1][data]:rank
 * columns[1][name]:
 * columns[1][searchable]:true
 * columns[1][orderable]:false
 * columns[1][search][value]:
 * columns[1][search][regex]:false
 * columns[2][data]:name
 * columns[2][name]:
 * columns[2][searchable]:true
 * columns[2][orderable]:false
 * columns[2][search][value]:
 * columns[2][search][regex]:false
 * columns[3][data]:phone
 * columns[3][name]:
 * columns[3][searchable]:true
 * columns[3][orderable]:false
 * columns[3][search][value]:
 * columns[3][search][regex]:false
 * columns[4][data]:subordinate
 * columns[4][name]:
 * columns[4][searchable]:true
 * columns[4][orderable]:false
 * columns[4][search][value]:
 * columns[4][search][regex]:false
 * columns[5][data]:function
 * columns[5][name]:
 * columns[5][searchable]:true
 * columns[5][orderable]:false
 * columns[5][search][value]:
 * columns[5][search][regex]:false
 * start:0
 * length:15
 * search[value]:
 * search[regex]:false
 * _:1493576205693
 *
 * @author CJ
 */
public class DrawablePageableArgumentResolver implements HandlerMethodArgumentResolver {

    static final DataPageable DEFAULT_PAGE_REQUEST = new DataPageRequest(0, 20, 1);
    private static final String INVALID_DEFAULT_PAGE_SIZE = "Invalid default page size configured for method %s! Must not be less than one!";
    private DataPageable fallbackPageable = DEFAULT_PAGE_REQUEST;

    private static DataPageable getDefaultPageRequestFrom(MethodParameter parameter) {

        PageableDefault defaults = parameter.getParameterAnnotation(PageableDefault.class);

        Integer defaultPageNumber = defaults.page();
        Integer defaultPageSize = defaults.size();

        if (defaultPageSize < 1) {
            Method annotatedMethod = parameter.getMethod();
            throw new IllegalStateException(String.format(INVALID_DEFAULT_PAGE_SIZE, annotatedMethod));
        }
        int offset = defaultPageNumber * defaultPageSize;

        if (defaults.sort().length == 0) {
            return new DataPageRequest(offset, defaultPageSize, 1);
        }

        return new DataPageRequest(offset, defaultPageSize, 1, defaults.direction(), defaults.sort());
    }

    /**
     * Configures the {@link DataPageable} to be used as fallback in case no {@link PageableDefault} or
     * {@link PageableDefault} (the latter only supported in legacy mode) can be found at the method parameter to be
     * resolved.
     * <p>
     * If you set this to {@literal null}, be aware that you controller methods will get {@literal null} handed into them
     * in case no {@link DataPageable} data can be found in the request. Note, that doing so will require you supply bot the
     * page <em>and</em> the size parameter with the requests as there will be no default for any of the parameters
     * available.
     *
     * @param fallbackPageable the {@link DataPageable} to be used as general fallback.
     */
    public void setFallbackPageable(DataPageable fallbackPageable) {
        this.fallbackPageable = fallbackPageable;
    }

    private DataPageable getDefaultFromAnnotationOrFallback(MethodParameter methodParameter) {

        if (methodParameter.hasParameterAnnotation(PageableDefault.class)) {
            return getDefaultPageRequestFrom(methodParameter);
        }

        return fallbackPageable;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return DataPageable.class.equals(parameter.getParameterType());
    }

    private int parseAndApplyBoundaries(String parameter, int upper, boolean shiftIndex) {

        try {
            int parsed = Integer.parseInt(parameter);
            return parsed < 0 ? 0 : parsed > upper ? upper : parsed;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer
            , NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        DataPageable defaultOrFallback = getDefaultFromAnnotationOrFallback(parameter);


        String startString = webRequest.getParameter("start");
        String lengthString = webRequest.getParameter("length");
        String drawString = webRequest.getParameter("draw");

        boolean pageAndSizeGiven = StringUtils.hasText(startString) && StringUtils.hasText(lengthString);

        if (!pageAndSizeGiven && defaultOrFallback == null) {
            return null;
        }

        int start = StringUtils.hasText(startString) ? parseAndApplyBoundaries(startString, Integer.MAX_VALUE, true)
                : defaultOrFallback.getPageNumber();
        int length = StringUtils.hasText(lengthString) ? parseAndApplyBoundaries(lengthString, Integer.MAX_VALUE, false)
                : defaultOrFallback.getPageSize();
        int draw = StringUtils.hasText(drawString) ? parseAndApplyBoundaries(drawString, Integer.MAX_VALUE, false)
                : defaultOrFallback.getDraw();
        // todo sort
        // order[0][column]:2
        // order[0][dir]:desc

        return new DataPageRequest(start, length, draw);
    }
}
