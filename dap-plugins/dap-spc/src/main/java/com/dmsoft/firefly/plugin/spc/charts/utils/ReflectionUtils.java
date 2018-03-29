package com.dmsoft.firefly.plugin.spc.charts.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cherry on 2018/2/27.
 */
public class ReflectionUtils {

    /**
     * Get method return value by method name
     *
     * @param classInstance class instance
     * @param methodName    method name
     * @param source        source
     * @param paramTypes    param types
     * @param params        params array
     * @return return value
     */
    public static Object forceMethodCall(Class classInstance,
                                         String methodName,
                                         Object source,
                                         Class[] paramTypes,
                                         Object[] params) {
        Object returnedObject = null;
        try {
            Method method = classInstance.getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);
            returnedObject = method.invoke(source, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnedObject;
    }

    /**
     * Get method return value by method name
     *
     * @param classInstance class instance
     * @param methodName    method name
     * @param source        source
     * @param params        params array
     * @return return value
     */
    public static Object forceMethodCall(Class classInstance,
                                         String methodName,
                                         Object source,
                                         Object... params) {
        Class[] paramTypes = new Class[]{};
        if (params == null) {
            params = new Object[]{};
        }
        List<Class> derivedTypes = new ArrayList<>();
        for (Object p : params) {
            derivedTypes.add(p.getClass());
        }
        if (derivedTypes.size() > 0) {
            paramTypes = derivedTypes.toArray(new Class[derivedTypes.size()]);
        }
        return forceMethodCall(classInstance, methodName, source, paramTypes, params);
    }

    /**
     * Get field value by field name
     *
     * @param classInstance class instance
     * @param source        source
     * @param fieldName     field name
     * @return field value
     */
    public static Object forceFieldCall(Class classInstance,
                                        String fieldName,
                                        Object source) {

        Object returnedObject = null;
        try {
            Field field = classInstance.getDeclaredField(fieldName);
            field.setAccessible(true);
            returnedObject = field.get(source);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return returnedObject;
    }

}
