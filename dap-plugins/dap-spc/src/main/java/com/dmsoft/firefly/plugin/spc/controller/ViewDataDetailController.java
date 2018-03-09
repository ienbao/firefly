package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.table.NewTableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.plugin.spc.model.DetailDataModel;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.List;
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
    private List<TestItemWithTypeDto> typeDtoList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DetailDataModel model = new DetailDataModel(rowDataDto, typeDtoList);
        NewTableViewWrapper.decorate(detailTB, model);
        detailTB.getColumns().get(0).setPrefWidth(226);
        detailTB.getColumns().get(1).setPrefWidth(163);
    }

    public void setRowDataDto(RowDataDto rowDataDto) {
        this.rowDataDto = rowDataDto;
    }

    public void setTypeDtoList(List<TestItemWithTypeDto> typeDtoList) {
        this.typeDtoList = typeDtoList;
    }
}
