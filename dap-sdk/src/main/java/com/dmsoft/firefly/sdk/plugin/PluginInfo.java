package com.dmsoft.firefly.sdk.plugin;

import java.util.List;
import java.util.Map;

/**
 * Plugin info come from the plugin.xml including plugin location, and the plugin class instance
 *
 * @author Can Guan
 */
public class PluginInfo {
    private String id;
    private String name;
    private String version;
    private String author;
    private String description;
    private String scanPath;
    private String runtimePath;
    private String pluginClassName;
    private List<PluginInfo> requirements;
    private Map<String, String> config;
    //plugin root folder path
    private String folderPath;
    private PluginStatus status;
    private Plugin pluginObject;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getScanPath() {
        return scanPath;
    }

    public void setScanPath(String scanPath) {
        this.scanPath = scanPath;
    }

    public String getRuntimePath() {
        return runtimePath;
    }

    public void setRuntimePath(String runtimePath) {
        this.runtimePath = runtimePath;
    }

    public String getPluginClassName() {
        return pluginClassName;
    }

    public void setPluginClassName(String pluginClassName) {
        this.pluginClassName = pluginClassName;
    }

    public List<PluginInfo> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<PluginInfo> requirements) {
        this.requirements = requirements;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public PluginStatus getStatus() {
        return status;
    }

    public void setStatus(PluginStatus status) {
        this.status = status;
    }

    public Plugin getPluginObject() {
        return pluginObject;
    }

    public void setPluginObject(Plugin pluginObject) {
        this.pluginObject = pluginObject;
    }

    @Override
    public String toString() {
        return "PluginInfo{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", version='" + version + '\'' +
            ", author='" + author + '\'' +
            ", description='" + description + '\'' +
            ", scanPath='" + scanPath + '\'' +
            ", runtimePath='" + runtimePath + '\'' +
            ", pluginClassName='" + pluginClassName + '\'' +
            ", requirements=" + requirements +
            ", config=" + config +
            ", folderPath='" + folderPath + '\'' +
            ", status=" + status +
            ", pluginObject=" + pluginObject +
            '}';
    }
}
