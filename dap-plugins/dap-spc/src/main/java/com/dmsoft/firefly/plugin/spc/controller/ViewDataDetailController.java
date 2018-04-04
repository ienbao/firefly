package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.plugin.spc.model.DetailDataModel;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * controller for view data detail dialog
 *
 * @author Can Guan
 */
public class ViewDataDetailController implements Initializable {
    @FXML
    private TableView<String> detailTB;
    @FXML
    private TextFieldFilter filterTF;

    private RowDataDto rowDataDto;
    private Map<String, TestItemWithTypeDto> testItemDtoMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DetailDataModel model = new DetailDataModel(rowDataDto, testItemDtoMap);
        filterTF.getTextField().textProperty().addListener((ov, s1, s2) -> {
            model.filterText(s2);
        });
        TableViewWrapper.decorate(detailTB, model);
        detailTB.getColumns().get(0).setPrefWidth(226);
        detailTB.getColumns().get(1).setPrefWidth(163);
    }

    public void setRowDataDto(RowDataDto rowDataDto) {
        this.rowDataDto = rowDataDto;
    }

    public Map<String, TestItemWithTypeDto> getTestItemDtoMap() {
        return testItemDtoMap;
    }

    public void setTestItemDtoMap(Map<String, TestItemWithTypeDto> testItemDtoMap) {
        this.testItemDtoMap = testItemDtoMap;
    }
}
