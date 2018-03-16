package com.dmsoft.firefly.plugin.grr.utils;

import com.dmsoft.firefly.plugin.grr.dto.GrrDataFrameDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrItemResultDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrViewDataDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrMeanAndRangeDto;
import com.dmsoft.firefly.sdk.dataframe.CellData;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * Created by cherry on 2018/3/15.
 */
public class DataConvertUtils {

    public static GrrItemResultDto convertToItemResult(GrrDataFrameDto grrDataFrameDto, String itemName, String appraiserKey) {

        String splitFlag = "!@#";
        SearchDataFrame dataFrame = grrDataFrameDto.getDataFrame();
        List<GrrViewDataDto> includeData = grrDataFrameDto.getIncludeDatas();
        Map<String, List<String>> partRowKeys = Maps.newHashMap();
        Map<String, List<String>> partAppraiserRowKeys = Maps.newHashMap();
//      Package rowKey
        includeData.forEach(grrViewDataDto -> {
            String part = grrViewDataDto.getPart();
            String appraiser = grrViewDataDto.getOperator();
            String rowKey = grrViewDataDto.getRowKey();
            if (partRowKeys.containsKey(part)) {
                List<String> rowKeys = partRowKeys.get(part);
                rowKeys.add(rowKey);
            } else {
                partRowKeys.put(part, Lists.newArrayList(rowKey));
            }

            if (partAppraiserRowKeys.containsKey(part + splitFlag + appraiser)) {
                List<String> rowKeys = partAppraiserRowKeys.get(part + splitFlag + appraiser);
                rowKeys.add(rowKey);
            } else {
                partAppraiserRowKeys.put(part + splitFlag + appraiser, Lists.newArrayList(rowKey));
            }
        });

//      Get total mean and total range
        List<String> totalMeans = Lists.newArrayList();
        List<String> totalRanges = Lists.newArrayList();
        partRowKeys.forEach((partRowKey, rowKeys) -> {
//            String condition = partKey + "=" + partRowKey;
            List<String> data = dataFrame.subDataFrame(rowKeys, Lists.newArrayList(itemName)).getDataColumn(itemName, null).getData();
            double[] array = new double[data.size()];
            int i = 0;
            data.forEach(cellData -> {
                if (!DAPStringUtils.isBlankWithSpecialNumber(cellData)) {
                    array[i] = Double.valueOf(cellData);
                }
            });
            totalMeans.add(getAverage(array));
            totalRanges.add(getRange(MathUtils.getMax(array), MathUtils.getMin(array)));
        });

        //Get mean and range
        Map<String, GrrMeanAndRangeDto> meanAndRange = Maps.newHashMap();
        partAppraiserRowKeys.forEach((partAppraiserKey, rowKeys) -> {
//            String partValue = partAppraiserKey.split(splitFlag)[0];
            String appraiserValue = appraiserKey + "=" + partAppraiserKey.split(splitFlag)[1];
//            String condition = partKey + "=" + partValue + "&" + appraiserValue;
            List<String> data = dataFrame.subDataFrame(rowKeys, Lists.newArrayList(itemName)).getDataColumn(itemName, null).getData();
            double[] array = new double[data.size()];
            int i = 0;
            data.forEach(cellData -> {
                if (!DAPStringUtils.isBlankWithSpecialNumber(cellData)) {
                    array[i] = Double.valueOf(cellData);
                }
            });
            String mean = getAverage(array);
            String range = getRange(MathUtils.getMax(array), MathUtils.getMin(array));
            if (meanAndRange.containsKey(appraiserValue)) {
                GrrMeanAndRangeDto grrMeanAndRangeDto = meanAndRange.get(appraiserValue);
                grrMeanAndRangeDto.getMeans().add(mean);
                grrMeanAndRangeDto.getRanges().add(range);
            } else {
                GrrMeanAndRangeDto grrMeanAndRangeDto = new GrrMeanAndRangeDto();
                grrMeanAndRangeDto.setMeans(Lists.newArrayList(mean));
                grrMeanAndRangeDto.setRanges(Lists.newArrayList(range));
                meanAndRange.put(partAppraiserKey.split(splitFlag)[1], grrMeanAndRangeDto);
            }
        });

        GrrItemResultDto grrItemResultDto = new GrrItemResultDto();
        grrItemResultDto.setTotalMeans(totalMeans);
        grrItemResultDto.setTotalRanges(totalRanges);
        grrItemResultDto.setMeanAndRangeDtos(meanAndRange);
        return grrItemResultDto;
    }

    /**
     * Find row key for grr view data
     *
     * @param viewDataDtos
     * @param appraiserValue
     * @param trialValue
     * @param partValue
     * @return
     */
    public static String findRowKeyFromViewData(List<GrrViewDataDto> viewDataDtos,
                                                String appraiserValue,
                                                String trialValue,
                                                String partValue) {
        String rowKey = null;
        for (GrrViewDataDto viewDataDto : viewDataDtos) {
            boolean flag = appraiserValue.equals(viewDataDto.getOperator());
            flag = flag && trialValue.equals(viewDataDto.getTrial());
            flag = flag && partValue.equals(viewDataDto.getPart());
            rowKey = flag ? viewDataDto.getRowKey() : rowKey;
        }
        return rowKey;
    }

    private static String getAverage(double[] array) {
        double sum = 0, count = 0;
        if (array == null || array.length == 0) {
            return "-";
        }
        for (int i = 0; i < array.length; i++) {
            if (DAPStringUtils.isBlankWithSpecialNumber(String.valueOf(array[i]))) {
                continue;
            }
            sum += array[i];
            count++;
        }
        if (count == 0) {
            return "-";
        }
        return DAPStringUtils.formatBigDecimal(String.valueOf(sum / count));
    }

    private static String getRange(double max, double min) {
        return DAPStringUtils.formatBigDecimal(String.valueOf(max - min));
    }
}
