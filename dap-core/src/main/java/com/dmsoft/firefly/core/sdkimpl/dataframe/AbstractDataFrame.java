package com.dmsoft.firefly.core.sdkimpl.dataframe;

import com.dmsoft.firefly.sdk.dataframe.DataFrame;
import com.dmsoft.firefly.sdk.dataframe.PassPolicy;

import java.util.function.Function;

/**
 * abstract class for data frame
 *
 * @author Can Guan
 */
public abstract class AbstractDataFrame implements DataFrame {
    private PassPolicy passPolicy = PassPolicy.NONE;
    private Function<String, Boolean> cellResultFunction;


    @Override
    public PassPolicy getPassPolicy() {
        return this.passPolicy;
    }

    @Override
    public void setPassPolicy(PassPolicy passPolicy) {
        this.passPolicy = passPolicy;
    }

    @Override
    public Function<String, Boolean> getCellResultFunction() {
        return cellResultFunction;
    }

    @Override
    public void setCellResultFunction(Function<String, Boolean> cellResultFunction) {
        this.cellResultFunction = cellResultFunction;
    }

    @Override
    public Boolean getCellResult(String rowKey, String testItemName) {
        if (this.cellResultFunction == null) {
            return null;
        } else {
            return cellResultFunction.apply(getCellValue(rowKey, testItemName));
        }
    }
}
