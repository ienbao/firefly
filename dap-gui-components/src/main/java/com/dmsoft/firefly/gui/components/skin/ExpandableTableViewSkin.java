package com.dmsoft.firefly.gui.components.skin;

import com.sun.javafx.scene.control.skin.TableViewSkin;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

/**
 * skin for expandable table view
 *
 * @author Can Guan
 */
public class ExpandableTableViewSkin extends TableViewSkin {
    public static final Callback<TableView.ResizeFeatures, Boolean> AUTO_EXPAND_POLICY = new Callback<TableView.ResizeFeatures, Boolean>() {

        private boolean isFirstRun = true;

        @Override
        public String toString() {
            return "auto-expand";
        }

        @Override
        public Boolean call(TableView.ResizeFeatures prop) {
            return true;
        }
    };
    private TableView tableView;

    /**
     * constructor
     *
     * @param tableView table view
     */
    public ExpandableTableViewSkin(TableView tableView) {
        super(tableView);
        this.tableView = tableView;
        this.tableView.widthProperty().addListener((ov, w1, w2) -> {
            for (int i = 0; i < this.tableView.getColumns().size(); i++) {
                resizeColumnToFitContent((TableColumn) this.tableView.getColumns().get(i), 30);
            }
        });
    }

    @Override
    protected void resizeColumnToFitContent(TableColumn tc, int maxRows) {
        double preFix = 0;
        int lens = tableView.getColumns().size();
        if (!((TableColumn) tableView.getColumns().get(0)).isResizable()) {
            preFix = ((TableColumn) tableView.getColumns().get(0)).getWidth();
            lens--;
            if (tableView.getColumns().indexOf(tc) == 0) {
                super.resizeColumnToFitContent(tc, maxRows);
                return;
            }
        }
        tc.setMinWidth(30);
        double width = (tableView.getWidth() - preFix - 10) / lens;
        if ((tableView.getColumns().size() - 1) * 110 + 32 > tableView.getWidth()) {
            width = 110;
        }
        tc.setPrefWidth(width);
    }
}
