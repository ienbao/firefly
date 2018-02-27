package com.dmsoft.firefly.core.sdkimpl.dataframe;

import com.dmsoft.firefly.sdk.dataframe.DataFrame;

import java.util.function.Function;

/**
 * abstract class for data frame
 *
 * @author Can Guan
 */
public abstract class AbstractBasicDataFrame implements DataFrame {
    private Function<String, Object> cellResultFunction;

    @Override
    public Function<String, Object> getCellResultFunction() {
        return cellResultFunction;
    }

    @Override
    public void setCellResultFunction(Function<String, Object> cellResultFunction) {
        this.cellResultFunction = cellResultFunction;
    }

    @Override
    public Object getCellResult(String rowKey, String testItemName) {
        if (this.cellResultFunction == null) {
            return null;
        } else {
            return cellResultFunction.apply(getCellValue(rowKey, testItemName));
        }
    }
}
