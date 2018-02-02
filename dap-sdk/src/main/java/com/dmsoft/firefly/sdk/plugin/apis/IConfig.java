package com.dmsoft.firefly.sdk.plugin.apis;

/**
 * config export and config api
 * {@link com.dmsoft.firefly.sdk.plugin.annotation.Config}
 *
 * @author Can Guan
 */
public interface IConfig {
    String getConfigName();

    Byte[] exportConfig();

    void importConfig(Byte[] config);
}
