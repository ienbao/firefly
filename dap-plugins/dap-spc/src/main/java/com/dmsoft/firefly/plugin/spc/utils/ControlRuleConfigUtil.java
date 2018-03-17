/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.utils;

import com.dmsoft.firefly.plugin.spc.dto.RuleResultDto;
import com.dmsoft.firefly.plugin.spc.utils.enums.JudgeRuleType;
import com.dmsoft.firefly.sdk.utils.RangeUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Ethan.Yang on 2018/3/17.
 */
public class ControlRuleConfigUtil {
    private int countNum = 0;
    private Double[] analyseData;

    /**
     * method to verify R1 rule
     *
     * @param iSigmaNum  user defined sigma number
     * @param avgValue   AVG
     * @param stdevValue SD
     * @return rule result dto
     */
    public RuleResultDto setRuleR1(int iSigmaNum, double avgValue, double stdevValue) {
        int iPoints = 1;
        RuleResultDto ruleResultDto = new RuleResultDto();

        if (analyseData.length == 0) {
            return null;
        } else {
            double upperLimit = avgValue + (iSigmaNum * stdevValue);
            double lowerLimit = avgValue - (iSigmaNum * stdevValue);

            ruleResultDto.setRuleName(JudgeRuleType.R1.getCode());

            if (analyseData.length < iPoints) {
                return ruleResultDto;
            }
            List<Double> xList = Lists.newArrayList();
            List<Double> yList = Lists.newArrayList();

            countNum = 0;

            for (int i = 0; i < analyseData.length; i++) {
                if (analyseData[i] > upperLimit || analyseData[i] < lowerLimit) {
                    xList.add(Double.valueOf(i + 1));
                    yList.add(analyseData[i]);
                    countNum++;
                }
            }
            if (countNum >= iPoints) {
                Double[] x = new Double[xList.size()];
                Double[] y = new Double[yList.size()];
                for (int i = 0; i < xList.size(); i++) {
                    x[i] = xList.get(i);
                    y[i] = yList.get(i);
                }
                ruleResultDto.setX(x);
                ruleResultDto.setY(y);
            }
            return ruleResultDto;
        }
    }

    /**
     * method to verify R2
     *
     * @param iPoint   user defined point number
     * @param avgValue AVG
     * @return rule result dto
     */
    public RuleResultDto setRuleR2(int iPoint, double avgValue) {
        RuleResultDto ruleResultDto = new RuleResultDto();

        if (analyseData.length == 0) {
            return null;
        } else {
            ruleResultDto.setRuleName(JudgeRuleType.R2.getCode());

            if (analyseData.length < iPoint) {
                return ruleResultDto;
            }

            Map<Integer, Double> store = Maps.newHashMap();
            Set<Double> compare = Sets.newHashSet();
            List<Double> yList = Lists.newArrayList();

            for (int i = 0; i < analyseData.length - iPoint; ) {
                if (analyseData[i] == avgValue) {
                    i++;
                    continue;
                }
                Boolean isOver = false;
                if (analyseData[i] > avgValue) {
                    isOver = true;
                }
                int j = 0;
                while ((isOver && analyseData[i + j] > avgValue) || (!isOver && analyseData[i + j] < avgValue)) {
                    j++;
                    if (i + j >= analyseData.length) {
                        break;
                    }
                }
                if (j >= iPoint) {
                    for (int k = 0; k < j; k++) {
                        if (!compare.contains(Double.valueOf(i + k + 1))) {
                            store.put(store.size(), Double.valueOf(i + k + 1));
                            compare.add(Double.valueOf(i + k + 1));
                            yList.add(Double.valueOf(analyseData[i + k]));
                        }
                    }
                }
                if (j == 0) {
                    i = i++;
                } else {
                    i = i + j;
                }
            }

            assembleResult(ruleResultDto, store, yList);
            return ruleResultDto;
        }
    }

    /**
     * method to verify R3
     *
     * @param iPoint user defined point number
     * @return rule result dto
     */
    public RuleResultDto setRuleR3(int iPoint) {
        RuleResultDto ruleResultDto = new RuleResultDto();

        if (analyseData.length == 0) {
            return null;
        } else {
            ruleResultDto.setRuleName(JudgeRuleType.R3.getCode());

            if (analyseData.length < iPoint) {
                return ruleResultDto;
            }

            Map<Integer, Double> store = Maps.newHashMap();
            Set<Double> compare = Sets.newHashSet();
            List<Double> yList = Lists.newArrayList();
            for (int i = 0; i < analyseData.length - iPoint; ) {
                int j = 1;
                if ((j + i) >= analyseData.length) {
                    break;
                }
                int newFlag;
                if (analyseData[i + j] > analyseData[i + j - 1]) {
                    newFlag = 1;
                } else if (analyseData[i + j] < analyseData[i + j - 1]) {
                    newFlag = -1;
                } else {
                    i++;
                    continue;
                }
                while (i + j < analyseData.length) {
                    if (newFlag > 0 && analyseData[i + j] > analyseData[i + j - 1] || newFlag < 0 && analyseData[i + j] < analyseData[i + j - 1]) {
                        j++;
                    } else {
                        break;
                    }
                }

                if (j >= iPoint) {
                    for (int k = 0; k < j; k++) {
                        if (!compare.contains(Double.valueOf(i + k + 1))) {
                            store.put(store.size(), Double.valueOf(i + k + 1));
                            compare.add(Double.valueOf(i + k + 1));
                            yList.add(Double.valueOf(analyseData[i + k]));
                        }
                    }
                }
                i = i + j - 1;
            }

            assembleResult(ruleResultDto, store, yList);
            return ruleResultDto;
        }
    }

    /**
     * method to verify R4
     *
     * @param iPoint user defined point number
     * @return rule result dto
     */
    public RuleResultDto setRuleR4(int iPoint) {
        RuleResultDto ruleResultDto = new RuleResultDto();

        if (analyseData.length == 0) {
            return null;
        } else {
            ruleResultDto.setRuleName(JudgeRuleType.R4.getCode());

            if (analyseData.length < iPoint) {
                return ruleResultDto;
            }

            Map<Integer, Double> store = Maps.newHashMap();
            Set<Double> compare = Sets.newHashSet();
            List<Double> yList = Lists.newArrayList();
            List<Long> id = Lists.newArrayList();
            for (int i = 0; i < analyseData.length - iPoint; ) {
                int j = 1;
                int flag = 0;
                if (analyseData[i + j] > analyseData[i + j - 1]) {
                    flag = -1;
                } else if (analyseData[i + j] < analyseData[i + j - 1]) {
                    flag = 1;
                } else {
                    i++;
                    continue;
                }

                while (i + j < analyseData.length) {
                    if (analyseData[i + j] > analyseData[i + j - 1]) {
                        flag += 2;
                    } else if (analyseData[i + j] < analyseData[i + j - 1]) {
                        flag -= 2;
                    } else {
                        break;
                    }
                    if (Math.abs(flag) > 1) {
                        break;
                    }
                    j++;
                }
                if (j >= iPoint) {
                    for (int k = 0; k < j; k++) {
                        if (!compare.contains(Double.valueOf(i + k + 1))) {
                            store.put(store.size(), Double.valueOf(i + k + 1));
                            compare.add(Double.valueOf(i + k + 1));
                            yList.add(Double.valueOf(analyseData[i + k]));
                        }
                    }
                }
                i = i + j - 1;
            }
            assembleResult(ruleResultDto, store, yList);
            return ruleResultDto;
        }
    }

    /**
     * method to verify R5 or R6
     *
     * @param iPoint     user defined point number
     * @param iSomePoint user defined some point number
     * @param avgValue   AVG
     * @param stdevValue SD
     * @param iSigmaNum  user defined sigma number
     * @param rName      rule name
     * @return rule result dto
     */
    public RuleResultDto setRuleR5And6(int iPoint, int iSomePoint, double avgValue, double stdevValue, int iSigmaNum, String rName) {

        RuleResultDto ruleResultDto = new RuleResultDto();

        if (analyseData.length == 0) {
            return null;
        } else {

            ruleResultDto.setRuleName(rName);
            if (analyseData.length < iPoint) {
                return ruleResultDto;
            }
            double upperLimit = avgValue + (iSigmaNum * stdevValue);
            double lowerLimit = avgValue - (iSigmaNum * stdevValue);

            Map<Integer, Double> store = Maps.newHashMap();
            Set<Double> compare = Sets.newHashSet();
            List<Double> yList = Lists.newArrayList();

            for (int i = 0; i <= analyseData.length - iPoint; i++) {
                int overNum = 0;
                int lowerNum = 0;
                for (int j = 0; j < iPoint; j++) {
                    if (analyseData[i + j] > upperLimit) {
                        overNum++;
                    }
                    if (analyseData[i + j] < lowerLimit) {
                        lowerNum++;
                    }
                    if (overNum >= iSomePoint || lowerNum >= iSomePoint) {
                        for (int k = 0; k < iPoint; k++) {
                            if (!compare.contains(Double.valueOf(k + i + 1))) {
                                store.put(store.size(), Double.valueOf(i + k + 1));
                                compare.add(Double.valueOf(i + k + 1));
                                yList.add(Double.valueOf(analyseData[i + k]));
                            }
                        }
                    }
                }
            }
            assembleResult(ruleResultDto, store, yList);
            return ruleResultDto;
        }
    }

    /**
     * method to verify R7 or R8
     *
     * @param iPoint     user defined points
     * @param avgValue   AVG
     * @param stdevValue SD
     * @param iSigmaNum  user defined sigma number
     * @param rName      rule name
     * @return rule result dto
     */
    public RuleResultDto setRuleR7And8(int iPoint, double avgValue, double stdevValue, int iSigmaNum, String rName) {

        RuleResultDto ruleResultDto = new RuleResultDto();

        if (analyseData.length == 0) {
            return null;
        } else {
            ruleResultDto.setRuleName(rName);

            if (analyseData.length < iPoint) {
                return ruleResultDto;
            }

            double upperLimit = avgValue + (iSigmaNum * stdevValue);
            double lowerLimit = avgValue - (iSigmaNum * stdevValue);
            countNum = 0;

            Map<Integer, Double> store = Maps.newHashMap();
            Set<Double> compare = Sets.newHashSet();
            List<Double> yList = Lists.newArrayList();

            if (rName.equals(JudgeRuleType.R7.getCode())) {
                for (int i = 0; i < analyseData.length - iPoint; i++) {
                    int j = 0;
                    while (i + j < analyseData.length) {
                        if (analyseData[i + j] > lowerLimit && analyseData[i + j] < upperLimit) {
                            j++;
                        } else {
                            break;
                        }
                    }
                    if (j >= iPoint) {
                        for (int k = 0; k < j; k++) {
                            if (!compare.contains(Double.valueOf(k + i + 1))) {
                                store.put(store.size(), Double.valueOf(i + k + 1));
                                compare.add(Double.valueOf(i + k + 1));
                                yList.add(Double.valueOf(analyseData[i + k]));
                            }
                        }
                    }
                    i = i + j;
                }
            }

            if (rName.equals(JudgeRuleType.R8.getCode())) {
                for (int i = 0; i < analyseData.length; i++) {
                    int j = 0;
                    while (i + j < analyseData.length) {
                        if (analyseData[i + j] > upperLimit || analyseData[i + j] < lowerLimit) {
                            j++;
                        } else {
                            break;
                        }
                    }
                    if (j >= iPoint) {
                        for (int k = 0; k < j; k++) {
                            if (!compare.contains(Double.valueOf(k + i + 1))) {
                                store.put(store.size(), Double.valueOf(i + k + 1));
                                compare.add(Double.valueOf(i + k + 1));
                                yList.add(Double.valueOf(analyseData[i + k]));
                            }
                        }
                    }
                    i = i + j;
                }
            }
            assembleResult(ruleResultDto, store, yList);
            return ruleResultDto;
        }
    }

    /**
     * /**
     * method to verify R9 rule
     *
     * @param upperLimit upper limit
     * @param lowerLimit lower limit
     * @return rule result dto
     */
    public RuleResultDto setRuleR9(Double upperLimit, Double lowerLimit) {
        RuleResultDto ruleResultDto = new RuleResultDto();
        if (analyseData.length == 0) {
            return null;
        } else {

            ruleResultDto.setRuleName(JudgeRuleType.R1.getCode());

            List<Double> xList = Lists.newArrayList();
            List<Double> yList = Lists.newArrayList();

            countNum = 0;

            for (int i = 0; i < analyseData.length; i++) {
                if (RangeUtils.isWithoutRange(String.valueOf(analyseData[i]), String.valueOf(upperLimit), String.valueOf(lowerLimit))) {
                    yList.add(analyseData[i]);
                    xList.add(Double.valueOf(i + 1));
                }
            }

            Double[] x = new Double[xList.size()];
            Double[] y = new Double[yList.size()];
            for (int i = 0; i < xList.size(); i++) {
                x[i] = xList.get(i);
                y[i] = yList.get(i);
            }
            ruleResultDto.setX(x);
            ruleResultDto.setY(y);

            return ruleResultDto;
        }
    }

    private void assembleResult(RuleResultDto ruleResultDto, Map<Integer, Double> xList, List<Double> yList) {
        if (xList != null && !xList.isEmpty()) {
            Double[] x = new Double[xList.size()];
            Double[] y = new Double[yList.size()];
            for (int i = 0; i < xList.size(); i++) {
                x[i] = xList.get(i);
                y[i] = yList.get(i);
            }
            ruleResultDto.setX(x);
            ruleResultDto.setY(y);
        }
    }

    public void setAnalyseData(Double[] analyseData) {
        this.analyseData = analyseData;
    }
}
