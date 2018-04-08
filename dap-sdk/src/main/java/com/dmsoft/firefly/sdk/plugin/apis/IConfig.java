package com.dmsoft.firefly.sdk.plugin.apis;

/**
 * config export and config api
 * {@link com.dmsoft.firefly.sdk.plugin.apis.annotation.Config}
 *
 * @author Can Guan
 */
public interface IConfig {
    /**
     * method to get config name
     *
     * @return name
     */
    String getConfigName();

    /**
     * method to export config
     *
     * @return config bytes
     */
    byte[] exportConfig();

    /**
     * method to import config
     *
     * @param config config bytes
     */
    void importConfig(byte[] config);

    /**
     * method to restore config
     */
    void restoreConfig();
}
