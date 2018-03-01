package com.dmsoft.firefly.gui.model;

import com.dmsoft.firefly.sdk.dai.dto.SpecificationDataDto;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by GuangLi on 2018/3/1.
 */
public class TemplateItemModel {
    private StringProperty testItemName;
    private StringProperty dataType;
    private StringProperty lslFail;
    private StringProperty uslPass;

    public TemplateItemModel(SpecificationDataDto dataDto) {
        testItemName = new SimpleStringProperty(dataDto.getTestItemName());
        dataType = new SimpleStringProperty(dataDto.getDataType());
        lslFail = new SimpleStringProperty(dataDto.getLslFail());
        uslPass = new SimpleStringProperty(dataDto.getUslPass());
    }

    public String getTestItemName() {
        return testItemName.get();
    }

    public StringProperty testItemNameProperty() {
        return testItemName;
    }

    public void setTestItemName(String testItemName) {
        this.testItemName.set(testItemName);
    }

    public String getDataType() {
        return dataType.get();
    }

    public StringProperty dataTypeProperty() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType.set(dataType);
    }

    public String getLslFail() {
        return lslFail.get();
    }

    public StringProperty lslFailProperty() {
        return lslFail;
    }

    public void setLslFail(String lslFail) {
        this.lslFail.set(lslFail);
    }

    public String getUslPass() {
        return uslPass.get();
    }

    public StringProperty uslPassProperty() {
        return uslPass;
    }

    public void setUslPass(String uslPass) {
        this.uslPass.set(uslPass);
    }
}
