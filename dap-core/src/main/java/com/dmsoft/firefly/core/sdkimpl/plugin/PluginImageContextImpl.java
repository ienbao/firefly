package com.dmsoft.firefly.core.sdkimpl.plugin;

import com.dmsoft.firefly.core.utils.ClassScanner;
import com.dmsoft.firefly.sdk.plugin.*;
import com.dmsoft.firefly.sdk.plugin.apis.annotation.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Impl class for plugin image and plugin context listener
 *
 * @author Can Guan
 */
@Service
public class PluginImageContextImpl implements PluginImageContext, PluginContextListener {
    private Map<String, PluginImage> pluginImageMap;

    @Autowired
    private PluginContext pluginContext;

    /**
     * constructor
     */
    public PluginImageContextImpl() {
        this.pluginImageMap = Maps.newConcurrentMap();
    }

    @Override
    public void contextChange(PluginContextEvent event) {
        if (PluginContextEvent.EventType.ENABLE.equals(event.getEventType())) {
            registerPlugin(event.getPluginInfoIdList());
        } else if (PluginContextEvent.EventType.DISABLE.equals(event.getEventType())
                || PluginContextEvent.EventType.UNINSTALL.equals(event.getEventType())) {
            unregisterPlugin(event.getPluginInfoIdList());
        }
    }

    @Override
    public void registerPlugin(String pluginId) {
        PluginInfo pluginInfo = this.pluginContext.getEnabledPluginInfo(pluginId);
        if (pluginInfo != null) {
            Map<String, PluginClass> openClasses = privateScanClass(this.pluginContext.getDAPClassLoader(pluginInfo.getId()),
                    pluginInfo.getScanPath());
            for (PluginClass pc : openClasses.values()) {
                pc.setPluginId(pluginId);
            }
            PluginImage pluginImage = new PluginImage();
            pluginImage.setPluginId(pluginId);
            if (this.pluginImageMap.get(pluginId) != null) {
                Map<String, PluginClass> pluginClassMap = combineClassMap(openClasses, this.pluginImageMap.get(pluginId).getPluginClassMap());
                pluginImage.setPluginClassMap(pluginClassMap);
            } else {
                pluginImage.setPluginClassMap(openClasses);
            }
            this.pluginImageMap.put(pluginId, pluginImage);
        }
    }

    @Override
    public void registerPlugin(List<String> pluginIdList) {
        if (pluginIdList == null) {
            return;
        }
        for (String id : pluginIdList) {
            registerPlugin(id);
        }
    }

    @Override
    public void unregisterPlugin(String pluginId) {
        this.pluginImageMap.remove(pluginId);
    }

    @Override
    public void unregisterPlugin(List<String> pluginIdList) {
        if (pluginIdList == null) {
            return;
        }
        for (String id : pluginIdList) {
            unregisterPlugin(id);
        }
    }

    @Override
    public void registerPluginInstance(String pluginId, String className, Object o) {
        if (this.pluginImageMap.get(pluginId) == null) {
            PluginImage pluginImage = new PluginImage();
            pluginImage.setPluginId(pluginId);
            this.pluginImageMap.put(pluginId, pluginImage);
        }
        if (this.pluginImageMap.get(pluginId).getPluginClassMap() == null) {
            this.pluginImageMap.get(pluginId).setPluginClassMap(Maps.newHashMap());
        }
        if (this.pluginImageMap.get(pluginId).getPluginClassMap().get(className) == null) {
            PluginClass pluginClass = new PluginClass();
            pluginClass.setPluginId(pluginId);
            this.pluginImageMap.get(pluginId).getPluginClassMap().put(className, pluginClass);
        }
        this.pluginImageMap.get(pluginId).getPluginClassMap().get(className).setInstance(o);
    }

    @Override
    public Object getPluginInstance(String pluginId, String className) {
        PluginImage pluginImage = this.pluginImageMap.get(pluginId);
        if (pluginImage != null && pluginImage.getPluginClassMap() != null) {
            PluginClass pluginClass = pluginImage.getPluginClassMap().get(className);
            if (pluginClass != null) {
                return pluginClass.getInstance();
            }
        }
        return null;
    }

    @Override
    public List<Method> getRegisterAPI(String pluginId, String className, String methodName) {
        PluginImage pluginImage = this.pluginImageMap.get(pluginId);
        if (pluginImage != null && pluginImage.getPluginClassMap() != null) {
            PluginClass pluginClass = pluginImage.getPluginClassMap().get(className);
            if (pluginClass != null) {
                Set<Method> methodSet = pluginClass.getMethodSet();
                if (methodSet != null) {
                    List<Method> methodList = Lists.newArrayList();
                    for (Method m : methodSet) {
                        if (m.getName().equals(methodName)) {
                            methodList.add(m);
                        }
                    }
                    return methodList;
                }
            }
        }
        return null;
    }

    @Override
    public List<PluginClass> getPluginClassByType(PluginClassType type) {
        List<PluginClass> result = Lists.newArrayList();
        for (PluginImage pluginImage : this.pluginImageMap.values()) {
            if (pluginImage.getPluginClassMap() != null) {
                for (PluginClass pluginClass : pluginImage.getPluginClassMap().values()) {
                    if (type.equals(pluginClass.getType())) {
                        result.add(pluginClass);
                    }
                }
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, PluginClass> privateScanClass(ClassLoader classLoader, String scanPath) {
        Map<String, PluginClass> result = Maps.newHashMap();

        List<Class<? extends Annotation>> annotations = Lists.newArrayList(OpenService.class, DataParser.class,
                Analysis.class, Chart.class, DataOutput.class, RDAProtocol.class, Config.class);
        Set<Class> scanClassList = ClassScanner.scanPackage(scanPath, classLoader, annotations);
        for (Class c : scanClassList) {
            PluginClassType classType = PluginClassType.DEFAULT;
            if (c.getAnnotation(DataParser.class) != null) {
                classType = PluginClassType.DATA_PARSER;
            } else if (c.getAnnotation(Analysis.class) != null) {
                classType = PluginClassType.ANALYSIS;
            } else if (c.getAnnotation(Chart.class) != null) {
                classType = PluginClassType.CHART;
            } else if (c.getAnnotation(DataOutput.class) != null) {
                classType = PluginClassType.DATA_OUTPUT;
            } else if (c.getAnnotation(RDAProtocol.class) != null) {
                classType = PluginClassType.RDA_PROTOCOL;
            } else if (c.getAnnotation(Config.class) != null) {
                classType = PluginClassType.CONFIG;
            }
            PluginClass pluginClass = new PluginClass();
            pluginClass.setClassName(c.getName());
            pluginClass.setType(classType);
            Set<Method> methodSet = ClassScanner.scanMethod(c, ExcludeMethod.class);
            pluginClass.setMethodSet(methodSet);
            result.put(c.getName(), pluginClass);
        }
        return result;
    }

    private Map<String, PluginClass> combineClassMap(Map<String, PluginClass> map1, Map<String, PluginClass> map2) {
        Map<String, PluginClass> result = Maps.newHashMap();
        if (map1 == null && map2 == null) {
            return result;
        } else if (map1 == null) {
            result.putAll(map2);
            return result;
        } else if (map2 == null) {
            result.putAll(map1);
            return result;
        }
        result.putAll(map1);
        for (Map.Entry<String, PluginClass> entry : map2.entrySet()) {
            if (result.get(entry.getKey()) != null) {
                PluginClass pluginClass = result.get(entry.getKey());
                if (entry.getValue().getType() != null) {
                    pluginClass.setType(entry.getValue().getType());
                }
                if (entry.getValue().getClassName() != null) {
                    pluginClass.setClassName(entry.getValue().getClassName());
                }
                if (entry.getValue().getInstance() != null) {
                    pluginClass.setInstance(entry.getValue().getInstance());
                }
                if (entry.getValue().getPluginId() != null) {
                    pluginClass.setPluginId(entry.getValue().getPluginId());
                }
                if (entry.getValue().getMethodSet() != null) {
                    if (pluginClass.getMethodSet() != null) {
                        pluginClass.getMethodSet().addAll(entry.getValue().getMethodSet());
                    } else {
                        pluginClass.setMethodSet(Sets.newHashSet());
                        pluginClass.getMethodSet().addAll(Sets.newHashSet(entry.getValue().getMethodSet()));
                    }
                }

            } else {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
}
