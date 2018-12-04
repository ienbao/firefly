/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.core.utils;

import com.dmsoft.firefly.sdk.plugin.PluginConstants;
import com.dmsoft.firefly.sdk.plugin.PluginInfo;
import com.dmsoft.firefly.sdk.utils.FileUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Util class for parsing xml into plugin info.
 *
 * @author Can Guan
 */
public class PluginXMLParser {
    private static Logger logger = LoggerFactory.getLogger(PluginXMLParser.class);
    /**
     * method to parse xml into plugin info
     *
     * @param pluginFolderUri xml file uri
     * @return plugin info
     */
    public static PluginInfo parseXML(String pluginFolderUri) {
        try {
            SAXParserFactory sf = SAXParserFactory.newInstance();
            SAXParser sp = sf.newSAXParser();
            return parserXML(pluginFolderUri, sp);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * method to parse xml into plugin infos
     *
     * @param pluginFolderUris xml file uri list
     * @return list of plugin info
     */
    public static List<PluginInfo> parseXML(List<String> pluginFolderUris) {
        try {
            List<PluginInfo> result = Lists.newArrayList();
            SAXParserFactory sf = SAXParserFactory.newInstance();
            SAXParser sp = sf.newSAXParser();

            for (String folderUri : pluginFolderUris) {
                logger.debug("解释当前插件配置文件, file:" +  folderUri);
                PluginInfo pluginInfo = parserXML(folderUri, sp);
                if (pluginInfo != null) {
                    result.add(pluginInfo);
                }
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    private static PluginInfo parserXML(String pluginFolderUri, SAXParser sp) {
        try {

            // judge file exist or not
            File pluginXMLFile = new File(pluginFolderUri);
            if (!pluginXMLFile.isFile()) {
                logger.debug("当前文件不存在，file:" + pluginXMLFile);
                return null;
            }

            PluginXMLHandler pluginXMLHandler = new PluginXMLHandler();
            sp.parse(pluginXMLFile, pluginXMLHandler);
            PluginInfo pluginInfo = pluginXMLHandler.getPluginInfo();
            pluginInfo.setFolderPath(pluginFolderUri);

            // get config xml file uri
            String configXMLFileUri = FileUtils.buildFilePath(pluginFolderUri, PluginConstants.FOLDER_NAME_CONFIG, PluginConstants.FILE_NAME_CONFIG);

            // judge file exist or not
            File configXMLFile = new File(configXMLFileUri);
            if (configXMLFile.isFile()) {
                ConfigXMLHandler configXMLHandler = new ConfigXMLHandler();
                sp.parse(configXMLFileUri, configXMLHandler);
                pluginInfo.setConfig(configXMLHandler.getConfigMap());
            }

            return pluginInfo;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
}
