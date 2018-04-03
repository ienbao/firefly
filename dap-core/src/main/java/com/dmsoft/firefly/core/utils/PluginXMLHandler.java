/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.core.utils;

import com.dmsoft.firefly.sdk.plugin.PluginInfo;
import com.google.common.collect.Lists;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;

import static com.dmsoft.firefly.sdk.plugin.PluginConstants.*;

/**
 * Handler parse plugin.xml and get plugin info.
 *
 * @author Can Guan
 */
public class PluginXMLHandler extends DefaultHandler {
    private PluginInfo pluginInfo;

    /**
     * constructor to prepare the plugin info
     */
    public PluginXMLHandler() {
        this.pluginInfo = new PluginInfo();
        List<PluginInfo> pluginInfoList = Lists.newArrayList();
        this.pluginInfo.setRequirements(pluginInfoList);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (XML_PLUGIN.equals(qName)) {
            getPluginByAttributes(attributes);
        } else if (XML_RUNTIME.equals(qName)) {
            getRuntimeByAttributes(attributes);
        } else if (XML_REQUIRE.equals(qName)) {
            getRequirementByAttributes(attributes);
        }
    }

    private void getPluginByAttributes(Attributes attributes) {
        for (int i = 0; i < attributes.getLength(); i++) {
            if (XML_ID.equals(attributes.getQName(i))) {
                pluginInfo.setId(attributes.getValue(i));
            } else if (XML_NAME.equals(attributes.getQName(i))) {
                pluginInfo.setName(attributes.getValue(i));
            } else if (XML_VERSION.equals(attributes.getQName(i))) {
                pluginInfo.setVersion(attributes.getValue(i));
            } else if (XML_AUTHOR.equals(attributes.getQName(i))) {
                pluginInfo.setAuthor(attributes.getValue(i));
            } else if (XML_SCAN_PATH.equals(attributes.getQName(i))) {
                pluginInfo.setScanPath(attributes.getValue(i));
            } else if (XML_PLUGIN_CLASS_NAME.equals(attributes.getQName(i))) {
                pluginInfo.setPluginClassName(attributes.getValue(i));
            } else if (XML_DESCRIPTION.equals(attributes.getQName(i))) {
                pluginInfo.setDescription(attributes.getValue(i));
            }
        }
    }

    private void getRuntimeByAttributes(Attributes attributes) {
        for (int i = 0; i < attributes.getLength(); i++) {
            if (XML_PATH.equals(attributes.getQName(i))) {
                pluginInfo.setRuntimePath(attributes.getValue(i));
            }
        }
    }

    private void getRequirementByAttributes(Attributes attributes) {
        PluginInfo requirePlugin = new PluginInfo();
        for (int i = 0; i < attributes.getLength(); i++) {
            if (XML_ID.equals(attributes.getQName(i))) {
                requirePlugin.setId(attributes.getValue(i));
            } else if (XML_NAME.equals(attributes.getQName(i))) {
                requirePlugin.setName(attributes.getValue(i));
            } else if (XML_VERSION.equals(attributes.getQName(i))) {
                requirePlugin.setVersion(attributes.getValue(i));
            }
        }
        pluginInfo.getRequirements().add(requirePlugin);
    }

    public PluginInfo getPluginInfo() {
        return pluginInfo;
    }
}
