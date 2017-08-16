package me.jiangcai.logistics.haier.model;

import me.jiangcai.logistics.PersistingReadable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Map;

/**
 * @author CJ
 */
abstract class AbstractModel implements PersistingReadable {

    private static final Log log = LogFactory.getLog(AbstractModel.class);

    @Override
    public String toHTML() {
        try {
            StringBuilder stringBuilder = new StringBuilder("<ul class=\"eventEntity\">");
            PropertyDescriptor[] propertyDescriptor = BeanUtils.getPropertyDescriptors(this.getClass());
            for (PropertyDescriptor descriptor : propertyDescriptor) {
                if (descriptor.getName().equals("class"))
                    continue;
                Object obj = descriptor.getReadMethod().invoke(this);
                stringBuilder.append(String.format("<li><span class=\"propertyName\">%s:</span>", descriptor.getName()));
                writeValue(stringBuilder, obj);
                stringBuilder.append("</li>");
            }
            stringBuilder.append("</ul>");
            return stringBuilder.toString();
        } catch (Throwable ex) {
            log.trace("impossible", ex);
            return "ERROR!";
        }
    }

    private void writeValue(final StringBuilder stringBuilder, Object obj) {
        // 如果是
        if (obj == null) {
            stringBuilder.append("<span class=\"propertyValue\">&nbsp;</span>");
        } else if (obj instanceof Map) {
            stringBuilder.append("<ul class=\"propertyMap\">");
            ((Map<?, ?>) obj).entrySet().forEach(data -> {
                stringBuilder.append(String.format("<li><span class=\"propertyName\">%s:</span>", data.getKey() == null ? "E" : data.getKey().toString()));
                writeValue(stringBuilder, data.getValue());
                stringBuilder.append("</li>");
            });
            stringBuilder.append("</ul>");
        } else if (obj instanceof Collection) {
            stringBuilder.append("<ul class=\"propertyList\">");
            ((Collection<?>) obj).forEach(data -> {
                stringBuilder.append("<li>");
                writeValue(stringBuilder, data);
                stringBuilder.append("</li>");
            });
            stringBuilder.append("</ul>");
        } else {
            stringBuilder.append(String.format("<span class=\"propertyValue\">%s</span>", obj.toString()));
        }
    }

}
