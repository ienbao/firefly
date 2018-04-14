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

    /**
     * constructor
     *
     * @param dataDto data dto
     */
    public TemplateItemModel(SpecificationDataDto dataDto) {
        testItemName = new SimpleStringProperty(dataDto.getTestItemName());
        dataType = new SimpleStringProperty(dataDto.getDataType());
        lslFail = new SimpleStringProperty(dataDto.getLslFail());
        uslPass = new SimpleStringProperty(dataDto.getUslPass());
    }

    public String getTestItemName() {
        return testItemName.get();
    }

    /**
     * method to set test item name
     *
     * @param testItemName test item name
     */
    public void setTestItemName(String testItemName) {
        this.testItemName.set(testItemName);
    }

    /**
     * method to get test item
     *
     * @return string property
     */
    public StringProperty testItemNameProperty() {
        return testItemName;
    }

    public String getDataType() {
        return dataType.get();
    }

    /**
     * method to set data type
     *
     * @param dataType data type
     */
    public void setDataType(String dataType) {
        this.dataType.set(dataType);
    }

    /**
     * method to get data type property
     *
     * @return string property
     */
    public StringProperty dataTypeProperty() {
        return dataType;
    }

    public String getLslFail() {
        return lslFail.get();
    }

    /**
     * method to set lsl fail
     *
     * @param lslFail lsl or fail value
     */
    public void setLslFail(String lslFail) {
        this.lslFail.set(lslFail);
    }

    /**
     * method to get string property
     *
     * @return string property
     */
    public StringProperty lslFailProperty() {
        return lslFail;
    }

    public String getUslPass() {
        return uslPass.get();
    }

    /**
     * method to set usl or pass value
     *
     * @param uslPass usl pass value
     */
    public void setUslPass(String uslPass) {
        this.uslPass.set(uslPass);
    }

    /**
     * method to get string property
     *
     * @return string property
     */
    public StringProperty uslPassProperty() {
        return uslPass;
    }
}
