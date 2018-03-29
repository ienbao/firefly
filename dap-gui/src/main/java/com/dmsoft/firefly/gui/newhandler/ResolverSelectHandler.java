package com.dmsoft.firefly.gui.newhandler;


import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.job.core.AbstractBasicJobHandler;
import com.dmsoft.firefly.sdk.job.core.JobContext;
import com.dmsoft.firefly.sdk.plugin.PluginClass;
import com.dmsoft.firefly.sdk.plugin.PluginClassType;
import com.dmsoft.firefly.sdk.plugin.PluginImageContext;
import com.dmsoft.firefly.sdk.plugin.apis.IDataParser;

import java.util.List;

import static com.dmsoft.firefly.gui.newhandler.ParamKeys.RESOLVER_TEMPLATE_NAME;

/**
 * handler for select resolver
 *
 * @author Can Guan
 */
public class ResolverSelectHandler extends AbstractBasicJobHandler {
    /**
     * constructor
     */
    public ResolverSelectHandler() {
        setName(ParamKeys.RESOLVER_HANDLER);
    }

    @Override
    public void doJob(JobContext context) {
        PluginImageContext pluginImageContext = RuntimeContext.getBean(PluginImageContext.class);
        List<PluginClass> pluginClasses = pluginImageContext.getPluginClassByType(PluginClassType.DATA_PARSER);
        IDataParser service = null;
        String selectedTemplateName = context.get(RESOLVER_TEMPLATE_NAME).toString();
        for (int i = 0; i < pluginClasses.size(); i++) {
            if (((IDataParser) pluginClasses.get(0).getInstance()).getName().equals(selectedTemplateName)) {
                service = (IDataParser) pluginClasses.get(0).getInstance();
            }
        }
        context.put(ParamKeys.IDATA_PARSER, service);
    }
}
