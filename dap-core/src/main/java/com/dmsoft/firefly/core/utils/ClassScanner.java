/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.core.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * basic class scanner
 *
 * @author Peter Li, Can Guan
 */
public class ClassScanner {
    /**
     * method to scan package class with annotation by classloader in packageName path
     *
     * @param packageName package name / path
     * @param classLoader class loader
     * @param annotation  annotation to scan
     * @return class list
     */
    public static List<Class> scanPackage(String packageName, ClassLoader classLoader, Class<? extends Annotation> annotation) {
        List<Class> result = Lists.newArrayList();
        if (packageName == null) {
            return result;
        }
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs = null;
        try {
            dirs = classLoader.getResources(packageDirName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (dirs == null) {
            return result;
        }

        while (dirs.hasMoreElements()) {
            URL url = dirs.nextElement();
            String protocol = url.getProtocol();
            if ("file".equals(protocol)) {
                String filePath = null;
                try {
                    filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (StringUtils.isNotBlank(filePath)) {
//                    filePath = filePath.substring(1);
                    Path dir = Paths.get(filePath);
                    DirectoryStream<Path> stream = null;
                    try {
                        stream = Files.newDirectoryStream(dir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (stream != null) {
                        for (Path path : stream) {
                            String fileName = String.valueOf(path.getFileName());
                            //remove ".class"
                            String className = fileName.substring(0, fileName.length() - 6);
                            Class<?> classes = null;
                            try {
                                classes = Thread.currentThread().getContextClassLoader().loadClass(packageName + "." + className);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            if (null != classes && null != classes.getAnnotation(annotation)) {
                                result.add(classes);
                            }
                        }
                    }
                }
            } else if ("jar".equals(protocol)) {
                JarFile jarFile = null;
                try {
                    JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                    jarFile = jarURLConnection.getJarFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (jarFile == null) {
                    continue;
                }
                Enumeration<JarEntry> jarEntries = jarFile.entries();
                while (jarEntries.hasMoreElements()) {
                    JarEntry jarEntry = jarEntries.nextElement();
                    String jarEntryName = jarEntry.getName();

                    if (jarEntryName.contains(packageDirName) && !jarEntryName.equals(packageDirName + "/")) {
                        if (jarEntry.isDirectory()) {
                            String clazzName = jarEntry.getName().replace("/", ".");
                            int endIndex = clazzName.lastIndexOf(".");
                            String prefix = null;
                            if (endIndex > 0) {
                                prefix = clazzName.substring(0, endIndex);
                            }
                            result.addAll(scanPackage(prefix, classLoader, annotation));
                        }
                        if (jarEntry.getName().endsWith(".class")) {
                            Class<?> clazz = null;
                            try {
                                clazz = classLoader.loadClass(jarEntry.getName().replace("/", ".").replace(".class", ""));
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            if (null != clazz && null != clazz.getAnnotation(annotation)) {
                                result.add(clazz);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * method to scan package
     *
     * @param c                  class
     * @param excludedAnnotation excluded annotation
     * @return list of method
     */
    public static Set<Method> scanMethod(Class c, Class<? extends Annotation> excludedAnnotation) {
        Set<Method> methodSet = Sets.newHashSet();
        for (Method m : c.getDeclaredMethods()) {
            if (m.getAnnotation(excludedAnnotation) == null) {
                methodSet.add(m);
            }
        }
        return methodSet;
    }
}
