package com.dmsoft.firefly.gui.components.searchcombobox;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.geometry.Bounds;
import javafx.scene.control.ComboBox;
import javafx.stage.Popup;
import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * skin combo box to set calendar
 *
 * @param <T> string
 * @author Can Guan
 */
public class SearchComboBoxSkin<T> extends ComboBoxListViewSkin<T> {
    private Popup popup;

    /**
     * constructor
     *
     * @param comboBox combo box
     */
    public SearchComboBoxSkin(ComboBox<T> comboBox) {
        super(comboBox);
        popup = new Popup();
        popup.setAutoHide(true);
        popup.setHeight(287);
        arrowButton.setOnMouseReleased(event -> {
            if (arrowButton.getStyleClass().contains("arrow-calendar-button")) {
                comboBox.hide();
                event.consume();
                String timePattern = ((SearchComboBox) comboBox.getParent()).getSearchComboBoxController().getTimePattern();
                Date date = getDate(comboBox.getEditor().getText(), timePattern);
                popup.setWidth(comboBox.getWidth());
                CalendarChooser calendarChooser = new CalendarChooser(date);
                popup.getContent().add(calendarChooser);
                Bounds bounds = comboBox.localToScreen(comboBox.getBoundsInLocal());
                double x = bounds.getMinX() - 1;
                double y = bounds.getMinY() + 25;
                popup.show(comboBox, x, y);
                popup.setOnHiding(event1 -> {
                    comboBox.getEditor().setText(getTimeStr(calendarChooser.getCalendar(), timePattern));
                });
            }
        });
    }

    private Date getDate(String value, String timePattern) {
        try {
            return DateUtils.parseDate(value, timePattern);
        } catch (Exception e) {
            return null;
        }
    }

    private String getTimeStr(Calendar calendar, String timePattern) {
        if (timePattern == null) {
            return calendar.toString();
        }
        SimpleDateFormat format = new SimpleDateFormat(timePattern);
        return format.format(calendar.getTime());
    }
}
