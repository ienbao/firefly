package com.dmsoft.firefly.sdk;

import java.util.HashMap;
import java.util.Map;

/**
 * context for run time env
 * refer to BizServicesMapping in ispc-ms project and the BeanFactory in Spring
 *
 * @author Can Guan, Peter Li
 */
public class RuntimeContext {
    private static final Map<String, Object> SERVICE_MAPPING = new HashMap<>();

    /**
     * method to register bean
     *
     * @param beanName bean name
     * @param instance bean instance
     * @return object
     */
    public static Object registerBean(String beanName, Object instance) {
        return SERVICE_MAPPING.put(beanName, instance);
    }

    /**
     * method to register bean
     *
     * @param instance bean instance
     * @param <T>      class
     * @return object
     */
    public static <T> T registerBean(T instance) {
        if (instance == null) {
            return null;
        }
        return (T) SERVICE_MAPPING.put(getSimpleBeanName(instance.getClass().getName()), instance);
    }

    /**
     * method to get bean
     *
     * @param beanName bean name
     * @return bean object
     */
    public static Object getBean(String beanName) {
        return SERVICE_MAPPING.get(beanName);
    }

    /**
     * method to get bean
     *
     * @param beanName  bean name
     * @param classType class type
     * @param <T>       class type
     * @return bean object
     */
    public static <T> T getBean(String beanName, Class<T> classType) {
        if (classType == null) {
            return null;
        }
        Object o = SERVICE_MAPPING.get(beanName);
        if (o != null && classType.isInstance(o)) {
            return (T) o;
        }
        return null;
    }

    /**
     * method to get bean
     *
     * @param classType class type
     * @param <T>       class type
     * @return bean object
     */
    public static <T> T getBean(Class<T> classType) {
        if (classType == null) {
            return null;
        }
        if (SERVICE_MAPPING.get(getSimpleBeanName(classType.getName())) != null
                && classType.isInstance(SERVICE_MAPPING.get(getSimpleBeanName(classType.getName())))) {
            return (T) SERVICE_MAPPING.get(getSimpleBeanName(classType.getName()));
        }
        for (Object o : SERVICE_MAPPING.values()) {
            if (classType.isInstance(o)) {
                return (T) o;
            }
        }
        return null;
    }

    /**
     * method to get simple bean name
     *
     * @param className class name
     * @return bean name
     */
    public static String getSimpleBeanName(String className) {
        return className.substring(className.lastIndexOf("."));
    }
}
