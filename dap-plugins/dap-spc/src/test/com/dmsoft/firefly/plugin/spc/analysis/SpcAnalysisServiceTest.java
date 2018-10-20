package com.dmsoft.firefly.plugin.spc.analysis;

public class SpcAnalysisServiceTest {
    public static void main(String[] args) {
        SpcAnalysisServiceTest test = new SpcAnalysisServiceTest();
        System.out.println(test.getClass().getClassLoader().getResource("rscripts/spc.R").getPath());
    }
}
