package com.dmsoft.firefly.core.sdkimpl.plugin;

import com.dmsoft.firefly.core.utils.VersionUtils;
import com.dmsoft.firefly.sdk.plugin.*;
import com.dmsoft.firefly.sdk.utils.enums.InitModel;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.input.CloseShieldInputStream;
import org.apache.commons.lang3.StringUtils;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * Impl class for plugin context and plugin context listener
 *
 * @author Can Guan
 */
@Service
public class PluginContextImpl implements PluginContext, PluginContextListener {
    private static Logger logger = LoggerFactory.getLogger(PluginContextImpl.class);

    @Autowired
    private ApplicationContext context;
    @Autowired
    private PluginImageContext pluginImageContext;

    private Map<String, PluginInfo> pluginInfoMap;
    private List<PluginContextListener> pluginContextListeners = new ArrayList<>();
    private InitModel initModel;
    private ClassLoader parentClassLoader;
    private Map<String, PluginClassLoader> pluginClassLoaderMap;


    public PluginContextImpl(){
        this(InitModel.INIT_WITH_UI);
    }

    /**
     * constructor
     *
     * @param initModel init model
     */
    public PluginContextImpl(InitModel initModel) {
        this.addListener((PluginContextListener) this.pluginImageContext);
        this.pluginInfoMap = Maps.newHashMap();
        this.pluginContextListeners = Lists.newArrayList();
        this.parentClassLoader = PluginContextImpl.class.getClassLoader();


        this.pluginClassLoaderMap = new ConcurrentHashMap<String, PluginClassLoader>() {
            @Override
            public PluginClassLoader put(String key, PluginClassLoader value) {
                logger.info("通过Classloader加载插件, key:[{}], pluginClassLoader[{}]", key, value);
                PluginClassLoader pcl = super.put(key, value);
                PluginInfo pluginInfo = value.getPluginInfo();

                Plugin plugin = (Plugin) context.getBean(pluginInfo.getPluginClassName());
                pluginInfo.setPluginObject(plugin);
                plugin.initialize(initModel);
                return pcl;
            }

            @Override
            public PluginClassLoader remove(Object key) {
                PluginClassLoader pcl = super.remove(key);
                if (pcl != null && pcl.getPluginInfo() != null && pcl.getPluginInfo().getPluginObject() != null) {
                    pcl.getPluginInfo().getPluginObject().destroy();
                }
                return pcl;
            }
        };
        this.initModel = initModel;
    }

    @Override
    public void installPlugin(PluginInfo pluginInfo) {
        if (pluginInfo == null) {
            return ;
        }

        if (privateInstallPlugin(pluginInfo)) {
            notifyListeners(new PluginContextEvent(PluginContextEvent.EventType.INSTALL, Lists.newArrayList(pluginInfo.getId())));
        }
    }

    @Override
    public void installPlugin(List<PluginInfo> pluginInfoList) {
        if (CollectionUtils.isEmpty(pluginInfoList)) {
            logger.info("当前插件列表为空，不进行插件加载！");
            return;
        }

        List<String> idList = Lists.newArrayList();
        for (PluginInfo pluginInfo : pluginInfoList) {
            if (!privateInstallPlugin(pluginInfo)) {
                logger.info("安装插件出错，当前插件信息：" + pluginInfo);
                continue;
            }

            logger.debug("安装插件， install plugin: " + pluginInfo.getId());
            idList.add(pluginInfo.getId());
        }

        notifyListeners(new PluginContextEvent(PluginContextEvent.EventType.INSTALL, idList));
    }

    @Override
    public void uninstallPlugin(String pluginId) {
        if (StringUtils.isNotBlank(pluginId)) {
            if (privateUninstallPlugin(pluginId)) {
                notifyListeners(new PluginContextEvent(PluginContextEvent.EventType.UNINSTALL, Lists.newArrayList(pluginId)));
            }
        }
    }

    @Override
    public void uninstallPlugin(List<String> pluginIdList) {
        if (pluginIdList != null) {
            List<String> idList = Lists.newArrayList();
            for (String id : pluginIdList) {
                if (privateUninstallPlugin(id)) {
                    idList.add(id);
                }
            }
            notifyListeners(new PluginContextEvent(PluginContextEvent.EventType.UNINSTALL, idList));
        }
    }

    @Override
    public PluginInfo getEnabledPluginInfo(String pluginInfoId) {
        PluginInfo pluginInfo = this.pluginInfoMap.get(pluginInfoId);
        if (pluginInfo != null && PluginStatus.ACTIVE.equals(pluginInfo.getStatus())) {
            return pluginInfo;
        }
        return null;
    }

    @Override
    public Map<String, PluginInfo> getEnabledPluginInfo(List<String> pluginIdList) {
        Map<String, PluginInfo> result = Maps.newHashMap();
        if (pluginIdList != null) {
            for (String id : pluginIdList) {
                PluginInfo pluginInfo = getEnabledPluginInfo(id);
                if (pluginInfo != null) {
                    result.put(id, pluginInfo);
                }
            }
        }
        return result;
    }

    @Override
    public Map<String, PluginInfo> getAllEnabledPluginInfo() {
        Map<String, PluginInfo> result = Maps.newHashMap();
        for (PluginInfo pluginInfo : this.pluginInfoMap.values()) {
            if (PluginStatus.ACTIVE.equals(pluginInfo.getStatus())) {
                result.put(pluginInfo.getId(), pluginInfo);
            }
        }
        return result;
    }

    @Override
    public Map<String, PluginInfo> getAllInstalledPluginInfo() {
        Map<String, PluginInfo> result = Maps.newHashMap();
        result.putAll(this.pluginInfoMap);
        return result;
    }

    @Override
    public void enablePlugin(List<String> pluginIdList) {
        List<String> idList = Lists.newArrayList();
        for (String id : pluginIdList) {
            if (privateEnablePlugin(id)) {
                idList.add(id);
            }
        }
        notifyListeners(new PluginContextEvent(PluginContextEvent.EventType.ENABLE, idList));
    }

    @Override
    public void enablePlugin(String pluginId) {
        if (privateEnablePlugin(pluginId)) {
            notifyListeners(new PluginContextEvent(PluginContextEvent.EventType.ENABLE, Lists.newArrayList(pluginId)));
        }
    }

    @Override
    public void disablePlugin(List<String> pluginIdList) {
        List<String> idList = Lists.newArrayList();
        for (String id : pluginIdList) {
            if (privateDisablePlugin(id)) {
                idList.add(id);
            }
        }
        notifyListeners(new PluginContextEvent(PluginContextEvent.EventType.DISABLE, idList));
    }

    @Override
    public void disablePlugin(String pluginId) {
        if (privateDisablePlugin(pluginId)) {
            notifyListeners(new PluginContextEvent(PluginContextEvent.EventType.DISABLE, Lists.newArrayList(pluginId)));
        }
    }

    @Override
    public DAPClassLoader getDAPClassLoader(List<String> pluginIdList) {
        logger.info("通过classloader加载当前插件列表组件");
        if (pluginIdList == null) {
            return AccessController.doPrivileged(new PrivilegedAction<DAPClassLoader>() {
                @Override
                public DAPClassLoader run() {
                    return new DAPClassLoader(parentClassLoader, null);
                }
            });
        }

        List<PluginClassLoader> pclList = Lists.newArrayList();
        for (String pluginId : pluginIdList) {
            if (this.pluginInfoMap.containsKey(pluginId)) {
                PluginInfo pluginInfo = this.pluginInfoMap.get(pluginId);
                if (PluginStatus.ACTIVE.equals(pluginInfo.getStatus())) {
                    if (!this.pluginClassLoaderMap.containsKey(pluginId)) {
                        PluginClassLoader pcl = AccessController.doPrivileged(new PrivilegedAction<PluginClassLoader>() {
                            @Override
                            public PluginClassLoader run() {
                                return new PluginClassLoader(null, parentClassLoader, pluginInfo);
                            }
                        });
                        this.pluginClassLoaderMap.put(pluginId, pcl);
                    }
                }
                pclList.add(this.pluginClassLoaderMap.get(pluginId));
            }
        }

        return AccessController.doPrivileged(new PrivilegedAction<DAPClassLoader>() {
            @Override
            public DAPClassLoader run() {
                return new DAPClassLoader(parentClassLoader, pclList);
            }
        });
    }

    @Override
    public DAPClassLoader getDAPClassLoader(String pluginId) {
        return this.getDAPClassLoader(Lists.newArrayList(pluginId));
    }

    @Override
    public DAPClassLoader getDAPClassLoaderWithoutParent(String pluginId) {
        logger.info("通过classloader加载当前插件列表组件，pluginId[{}]", pluginId);
        if (pluginId == null) {
            return AccessController.doPrivileged(new PrivilegedAction<DAPClassLoader>() {
                @Override
                public DAPClassLoader run() {
                    return new DAPClassLoader(parentClassLoader.getParent(), null);
                }
            });
        }

        List<PluginClassLoader> pclList = Lists.newArrayList();
        if (this.pluginInfoMap.containsKey(pluginId)) {
            PluginInfo pluginInfo = this.pluginInfoMap.get(pluginId);
            if (PluginStatus.ACTIVE.equals(pluginInfo.getStatus())) {
                if (!this.pluginClassLoaderMap.containsKey(pluginId)) {
                    PluginClassLoader pcl = AccessController.doPrivileged(new PrivilegedAction<PluginClassLoader>() {
                        @Override
                        public PluginClassLoader run() {
                            return new PluginClassLoader(null, pluginInfo);
                        }
                    });
                    this.pluginClassLoaderMap.put(pluginId, pcl);
                }
            }
            pclList.add(this.pluginClassLoaderMap.get(pluginId));
        }

        return AccessController.doPrivileged(new PrivilegedAction<DAPClassLoader>() {
            @Override
            public DAPClassLoader run() {
                return new DAPClassLoader(parentClassLoader.getParent(), pclList);
            }
        });
    }

    @Override
    public void addListener(PluginContextListener listener) {
        this.pluginContextListeners.add(listener);
    }

    @Override
    public void removeListener(PluginContextListener listener) {
        this.pluginContextListeners.remove(listener);
    }

    @Override
    public void contextChange(PluginContextEvent event) {
        validatePlugin();
    }

    @Override
    public void startPlugin(String pluginId) {
        logger.info("开始加载插件， start plugin:" + pluginId);
        PluginInfo pluginInfo = this.pluginInfoMap.get(pluginId);
        if (pluginInfo == null) {
            logger.info("加载plugin info 为空，pluginId:" + pluginId);
            return;
        }

        if(pluginInfo.getStatus() != PluginStatus.ACTIVE){
            logger.info("当前插件设置不为可用，pluginfo:" + pluginInfo);
            return;
        }

        if(pluginInfo.getPluginObject() == null){
            try {
                Class clazz = Class.forName(pluginInfo.getPluginClassName());
                logger.debug("通过类加载class, clazz[{}]", clazz);
                Plugin pluginObject = (Plugin) this.context.getBean(clazz);
                logger.debug("通过spring 获取Javabean， pluginObject[{}]", pluginObject);
                pluginInfo.setPluginObject(pluginObject);
            }catch (Exception e){
                logger.error(e.getMessage(), e);
            }
        }

        pluginInfo.getPluginObject().start();
    }

    @Override
    public void startPlugin(List<String> pluginIdList) {
        if (pluginIdList == null) {
            return;
        }

        for (String pluginId : pluginIdList) {
            startPlugin(pluginId);
        }
    }

    private boolean privateEnablePlugin(String pluginId) {
        PluginInfo pluginInfo = this.pluginInfoMap.get(pluginId);
        if (pluginInfo == null) {
            return false;
        }

        if (pluginInfo.getStatus() == PluginStatus.ACTIVE) {
            return false;
        }

        logger.debug("设置插件当前状态为可用，pluginId[{}]", pluginId);
        pluginInfo.setStatus(PluginStatus.ACTIVE);
        return true;
    }

    private boolean privateDisablePlugin(String pluginId) {
        if (this.pluginInfoMap.get(pluginId) != null) {
            return  false;
        }

        if (this.pluginInfoMap.get(pluginId).getStatus() == PluginStatus.INACTIVE) {
            return false;
        }

        logger.debug("设置插件当前状状态为不可用，pluginId[{}]", pluginId);
        this.pluginInfoMap.get(pluginId).setStatus(PluginStatus.INACTIVE);
        this.pluginClassLoaderMap.remove(pluginId);
        return true;
    }

    private boolean privateInstallPlugin(PluginInfo pluginInfo) {
        PluginInfo existsPlugin = this.pluginInfoMap.get(pluginInfo.getId());
        if (existsPlugin != null &&
            VersionUtils.compareVersion(existsPlugin.getVersion(), pluginInfo.getVersion()) >= 0) {
            logger.info("当前插件存在，并且已是最新版本。exist version:" + existsPlugin.getVersion() + ", install version:" + pluginInfo.getVersion());
            return false;
        }

        if (pluginInfo.getStatus() == null) {
            pluginInfo.setStatus(PluginStatus.INACTIVE);
        }

        this.pluginInfoMap.put(pluginInfo.getId(), pluginInfo);

        return true;
    }

    private boolean privateUninstallPlugin(String pluginId) {
        if (this.pluginInfoMap.containsKey(pluginId)) {
            this.pluginInfoMap.remove(pluginId);
            return true;
        }
        return false;
    }

    private void notifyListeners(PluginContextEvent event) {
        for (PluginContextListener listener : this.pluginContextListeners) {
            listener.contextChange(event);
        }
    }

    private void validatePlugin() {
        for (PluginInfo pluginInfo : this.pluginInfoMap.values()) {
            if (pluginInfo.getRequirements() != null) {
                for (PluginInfo requirePlugin : pluginInfo.getRequirements()) {
                    if (!this.pluginInfoMap.containsKey(requirePlugin.getId())) {
                        pluginInfo.setStatus(PluginStatus.ERROR);
                    } else if (VersionUtils.compareVersion(this.pluginInfoMap.get(requirePlugin.getId()).getVersion(),
                            requirePlugin.getVersion()) < 0) {
                        pluginInfo.setStatus(PluginStatus.ERROR);
                    } else if (PluginStatus.ERROR.equals(pluginInfo.getStatus())) {
                        pluginInfo.setStatus(PluginStatus.INACTIVE);
                    }
                }
            }
        }
    }
}
