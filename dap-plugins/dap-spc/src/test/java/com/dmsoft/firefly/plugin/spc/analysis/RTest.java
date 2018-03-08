package com.dmsoft.firefly.plugin.spc.analysis;

import com.dmsoft.firefly.plugin.spc.utils.REnConnector;
import org.junit.Test;

public class RTest {
    @Test
    public void multiRTest() {
        REnConnector connector = new REnConnector();
        connector.connect();
        connector.setInput("x", 1.0d);
        REnConnector connector1 = new REnConnector();
        connector1.connect();
        System.out.println(connector1.getOutputDouble("x"));
        connector.disconnect();
        connector1.disconnect();
        REnConnector connector3 = new REnConnector();
        connector3.connect();
        connector3.disconnect();
    }
}
