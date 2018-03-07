package com.dmsoft.firefly.sdk.plugin.apis;

/**
 * config export and config api
 * {@link com.dmsoft.firefly.sdk.plugin.annotation.Config}
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
    Byte[] exportConfig();

    /**
     * method to import config
     *
     * @param config config bytes
     */
    void importConfig(Byte[] config);
}
