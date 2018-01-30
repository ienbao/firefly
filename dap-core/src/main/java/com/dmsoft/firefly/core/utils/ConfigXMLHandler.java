/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.core.utils;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Map;

import static com.dmsoft.firefly.sdk.plugin.PluginConstants.*;

/**
 * Handler parse config.xml and get config map
 */
public class ConfigXMLHandler extends DefaultHandler {
    private Map<String, String> configMap;
    private String name;
    private String value;
    private String tmp;

    /**
     * constructor to prepare the config map
     */
    public ConfigXMLHandler() {
        configMap = Maps.newHashMap();
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (XML_PROPERTY.equals(qName)) {
            name = null;
            value = null;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (XML_NAME.equals(qName)) {
            name = tmp;
        } else if (XML_VALUE.equals(qName)) {
            value = tmp;
        } else if (XML_PROPERTY.equals(qName)) {
            if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(value)) {
                configMap.put(name, value);
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        tmp = String.copyValueOf(ch, start, length).trim();
    }

    public Map<String, String> getConfigMap() {
        return configMap;
    }
}
