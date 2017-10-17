package cn.lmjia.cash.transfer.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Bean数据注入工具类
 * @Author: lxf
 */
public class BeanInjectUtil {

    /**
     * 注入相关属性值
     *
     * @param bean  目标对象
     * @param map	  需要注入的key-value(fieldName-fieldValue)
     */
    public static void injectFieldValue(Object bean, Map<String, String> map) {
        Class<?> clazz = bean.getClass();
        try {
            // 全部field
            Field[] fields = clazz.getDeclaredFields();
            // 全部methos
            Method[] methods = clazz.getDeclaredMethods();
            // 对全部field赋值
            for (Field field : fields) {
                // 属性name
                String fieldName = field.getName();
                // setter方法名
                String setterName = getSetterName(fieldName);
                // 如果不存在该方法则跳过
                if (!checkMethod(methods, setterName)) {
                    continue;
                }
                // 得到Method对象
                Method method = clazz.getMethod(setterName, field.getType());
                // 获取需要注入的值
                String value = map.get(fieldName);
                if (value != null && !"".equals(value)) {
                    // 根据Field类型进行value转换
                    String fieldType = field.getType().getSimpleName();
                    if ("String".equals(fieldType)) {
                        method.invoke(bean, value);
                    } else if ("Date".equals(fieldType)) {
                        Date dateValue = parseDate(value);
                        method.invoke(bean, dateValue);
                    } else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
                        int inteValue = Integer.parseInt(value);
                        method.invoke(bean, inteValue);
                    } else if ("Long".equalsIgnoreCase(fieldType)) {
                        long longValue = Long.parseLong(value);
                        method.invoke(bean, longValue);
                    } else if ("Float".equalsIgnoreCase(fieldType)) {
                        float floatValue = Float.parseFloat(value);
                        method.invoke(bean, floatValue);
                    } else if ("Double".equalsIgnoreCase(fieldType)) {
                        double doubleValue = Double.parseDouble(value);
                        method.invoke(bean, doubleValue);
                    } else if ("Boolean".equalsIgnoreCase(fieldType)) {
                        boolean booleanValue = Boolean.parseBoolean(value);
                        method.invoke(booleanValue);
                    } else if ("BigDecimal".equalsIgnoreCase(fieldType)){
                        BigDecimal bigDecimalValue = new BigDecimal(value);
                        method.invoke(bigDecimalValue);
                    } else{
                        System.out.println("Type that are not supported : " + fieldType);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * String To Date
     *
     * @param value
     * @return
     */
    private static Date parseDate(String value) {
        if (value == null || "".equals(value)) {
            return null;
        }
        try {
            // 两种规则： yyyy-MM-dd    yyyy-MM-dd HH:mm:ss
            String pattern = "";
            if (value.indexOf(":") != -1) {
                pattern = "yyyy-MM-dd HH:mm:ss";
            } else {
                pattern = "yyyy-MM-dd";
            }
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 检查对象方法集中,是否有此Setter方法
     *
     * @param methods
     * @param setterName
     * @return
     */
    private static boolean checkMethod(Method[] methods, String setterName) {
        boolean flag = false;
        for (Method method : methods) {
            String methodName = method.getName();
            if (setterName.equals(methodName)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 拼接Setter方法名称
     *
     * @param fieldName
     * @return
     */
    private static String getSetterName(String fieldName) {
        if (fieldName == null || "".equals(fieldName)) {
            return "";
        }
        int index = 0;
        return "set" + fieldName.substring(index, index + 1).toUpperCase()
                + fieldName.substring(index + 1);
    }
}
