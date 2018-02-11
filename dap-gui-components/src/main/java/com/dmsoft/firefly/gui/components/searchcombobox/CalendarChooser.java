package com.dmsoft.firefly.gui.components.searchcombobox;

import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

/**
 * calendar chooser pane
 */
public class CalendarChooser extends GridPane {
    private Calendar calendar = Calendar.getInstance();
    private int year, month, day, hour, minute, second;
    private String[] years = null;
    private String[] months = null;
    private int showYears = 100;
    private Label label = null;
    private Button okBtn = null;
    private StackPane preBtn = null;
    private StackPane nextBtn = null;
    private ComboBox<String> yearCmb = null;
    private ComboBox<String> monthCmb = null;
    private VBox pane;
    private Button currBtn;
    private String[] tits = {"Sun", "Mon", "Tue", "Wed", "Thr", "Fri", "Sat"};
    private String redBtnClass = "btn-primary";
    private String normalBtnClass = "btn-txt";

    /**
     * constructor
     *
     * @param title     title
     * @param xOnScreen x on screen to show
     * @param yOnScreen y on screen to show
     * @param date      date
     */
    public CalendarChooser(String title, int xOnScreen, int yOnScreen, Date date) {
        this.calendar.setTime(date);
        initComponents();
    }


    /**
     * constructor
     *
     * @param title     title
     * @param xOnScreen x on screen to show
     * @param yOnScreen y on screen to show
     */
    public CalendarChooser(String title, int xOnScreen, int yOnScreen) {
        initComponents();
    }

    private void initComponents() {
        this.year = this.calendar.get(Calendar.YEAR);
        this.month = this.calendar.get(Calendar.MONTH);
        this.day = this.calendar.get(Calendar.DAY_OF_MONTH);
        this.hour = this.calendar.get(Calendar.HOUR_OF_DAY);
        this.minute = this.calendar.get(Calendar.MINUTE);
        this.second = this.calendar.get(Calendar.SECOND);
        this.years = new String[showYears];
        this.months = new String[12];
        label = new Label();
        for (int i = 0; i < this.months.length; i++) {
            this.months[i] = formatDay(i + 1);
        }

        int start = this.year - this.showYears / 2;
        for (int i = start; i < start + showYears; i++) {
            this.years[i - start] = String.valueOf(i);
        }
        this.setInfo(this.year + "-" + this.formatDay(this.month + 1) + "-" + formatDay(this.day) + " " + formatDay(hour) + ":"
                + formatDay(minute) + ":" + formatDay(second));
        ColumnConstraints c0 = new ColumnConstraints(10, 10, 10);
        ColumnConstraints c1 = new ColumnConstraints(236, 236, 236);
        ColumnConstraints c2 = new ColumnConstraints(10, 10, 10);
        RowConstraints r0 = new RowConstraints(22, 22, 22);
        RowConstraints r1 = new RowConstraints(10, 10, 10);
        RowConstraints r2 = new RowConstraints(212, 212, 212);
        RowConstraints r3 = new RowConstraints(54, 54, 54);
        this.getColumnConstraints().addAll(c0, c1, c2);
        this.getRowConstraints().addAll(r0, r1, r2, r3);
        this.add(getNorthPane(), 1, 0);
        this.add(printCalendar(), 1, 2);
        this.add(getSouthPane(), 1, 3);
    }

    private Pane getNorthPane() {
        GridPane northPane = new GridPane();
        ColumnConstraints c0 = new ColumnConstraints(22, 22, 22);
        ColumnConstraints c1 = new ColumnConstraints(26, 26, 26);
        ColumnConstraints c2 = new ColumnConstraints(80, 80, 80);
        ColumnConstraints c3 = new ColumnConstraints(10, 10, 10);
        ColumnConstraints c4 = new ColumnConstraints(50, 50, 50);
        ColumnConstraints c5 = new ColumnConstraints(26, 26, 26);
        ColumnConstraints c6 = new ColumnConstraints(22, 22, 22);
        RowConstraints r0 = new RowConstraints(22, 22, 22);
        this.preBtn = new BasicArrowButton(BasicArrowButton.Direction.LEFT);
        Tooltip.install(this.preBtn, new Tooltip("Previous Month"));
        this.preBtn.addEventHandler(MouseEvent.MOUSE_RELEASED, this::preBtnHandler);
        this.preBtn.setPrefSize(22, 22);
        this.yearCmb = new ComboBox<>(FXCollections.observableArrayList(this.years));
        this.yearCmb.setPrefSize(80, 22);
        this.yearCmb.setValue(String.valueOf(calendar.get(Calendar.YEAR)));
        this.yearCmb.valueProperty().addListener((ov, v1, v2) -> {
            int value = Integer.valueOf(v2);
            this.calendar.set(Calendar.YEAR, value);
            updatePane();
        });
        this.monthCmb = new ComboBox<>(FXCollections.observableArrayList(this.months));
        this.monthCmb.setPrefSize(50, 22);
        this.monthCmb.getStyleClass().add("monthcmb");
        this.monthCmb.setValue(formatDay(calendar.get(Calendar.MONTH) + 1));
        this.monthCmb.valueProperty().addListener((ov, v1, v2) -> {
            int value = Integer.valueOf(v2);
            this.calendar.set(Calendar.MONTH, value - 1);
            this.updatePane();
        });
        this.nextBtn = new BasicArrowButton(BasicArrowButton.Direction.RIGHT);
        Tooltip.install(this.nextBtn, new Tooltip("Next Month"));
        this.nextBtn.addEventHandler(MouseEvent.MOUSE_RELEASED, this::nextBtnHandler);
        this.nextBtn.setPrefSize(22, 22);
        northPane.getColumnConstraints().addAll(c0, c1, c2, c3, c4, c5, c6);
        northPane.getRowConstraints().addAll(r0);
        northPane.add(preBtn, 0, 0);
        northPane.add(yearCmb, 2, 0);
        northPane.add(monthCmb, 4, 0);
        northPane.add(nextBtn, 6, 0);
        return northPane;
    }

    private Pane getSouthPane() {
        TimeSpinner daySpinner = new TimeSpinner(LocalTime.of(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND)));
        daySpinner.valueProperty().addListener((ov, v1, v2) -> {
            calendar.set(Calendar.HOUR_OF_DAY, v2.getHour());
            calendar.set(Calendar.MINUTE, v2.getMinute());
            calendar.set(Calendar.SECOND, v2.getSecond());
            StringBuffer sb = new StringBuffer();
            sb.append(calendar.get(Calendar.YEAR)).append("-")
                    .append(formatDay(calendar.get(Calendar.MONTH) + 1))
                    .append("-")
                    .append(formatDay(calendar.get(Calendar.DAY_OF_MONTH)))
                    .append(" ")
                    .append(formatDay(calendar.get(Calendar.HOUR_OF_DAY)))
                    .append(":")
                    .append(formatDay(calendar.get(Calendar.MINUTE)))
                    .append(":")
                    .append(formatDay(calendar.get(Calendar.SECOND)));
            setInfo(sb.toString());
        });
        this.okBtn = new Button("OK");
        this.okBtn.getStyleClass().add(redBtnClass);
        this.okBtn.setPrefSize(80, 22);
        GridPane southPane = new GridPane();
        southPane.setVgap(10);
        ColumnConstraints c0 = new ColumnConstraints(156, 156, 156);
        ColumnConstraints c1 = new ColumnConstraints(80, 80, 80);
        RowConstraints r0 = new RowConstraints(22, 22, 22);
        RowConstraints r1 = new RowConstraints(22, 22, 22);
        southPane.getColumnConstraints().addAll(c0, c1);
        southPane.getRowConstraints().addAll(r0, r1);
        southPane.setAlignment(Pos.TOP_LEFT);
        southPane.add(daySpinner, 0, 0);
        southPane.add(this.label, 0, 1);
        southPane.add(this.okBtn, 1, 1);
        return southPane;
    }

    private void updatePane() {
        this.getChildren().remove(this.pane);
        this.add(printCalendar(), 1, 2);
    }

    private VBox printCalendar() {
        this.pane = new VBox();
        this.pane.setSpacing(10);
        Label weekPane = new Label("  Sun   Mon   Tue  Wed   Thu    Fri    Sat");
        GridPane dayPane = new GridPane();
        dayPane.setHgap(10);
        dayPane.setVgap(10);
        ColumnConstraints c0 = new ColumnConstraints(22, 22, 22);
        ColumnConstraints c1 = new ColumnConstraints(22, 22, 22);
        ColumnConstraints c2 = new ColumnConstraints(22, 22, 22);
        ColumnConstraints c3 = new ColumnConstraints(22, 22, 22);
        ColumnConstraints c4 = new ColumnConstraints(22, 22, 22);
        ColumnConstraints c5 = new ColumnConstraints(22, 22, 22);
        ColumnConstraints c6 = new ColumnConstraints(22, 22, 22);
        RowConstraints r0 = new RowConstraints(22, 22, 22);
        RowConstraints r1 = new RowConstraints(22, 22, 22);
        RowConstraints r2 = new RowConstraints(22, 22, 22);
        RowConstraints r3 = new RowConstraints(22, 22, 22);
        RowConstraints r4 = new RowConstraints(22, 22, 22);
        dayPane.getColumnConstraints().addAll(c0, c1, c2, c3, c4, c5, c6);
        dayPane.getRowConstraints().addAll(r0, r1, r2, r3, r4);
        int year2 = calendar.get(Calendar.YEAR);
        int month2 = calendar.get(Calendar.MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        Button b = null;
        Label l = null;
        int columnCount = 0;
        int rowCount = 0;
        for (int i = Calendar.SUNDAY; i < weekDay; i++) {
            l = new Label(" ");
            dayPane.add(l, columnCount, rowCount);
            columnCount++;
            if (columnCount >= tits.length) {
                columnCount = 0;
                rowCount++;
            }
        }
        int currday = 0;
        String dayStr = null;
        do {
            currday = calendar.get(Calendar.DAY_OF_MONTH);
            dayStr = formatDay(currday);
            b = new Button(dayStr);
            if (currday == this.day && month2 == this.month && year2 == this.year) {
                b.getStyleClass().setAll(redBtnClass);
                b.setAlignment(Pos.CENTER);
                currBtn = b;
            } else {
                b.getStyleClass().setAll(normalBtnClass);
                b.setAlignment(Pos.CENTER);
            }
            Tooltip.install(b, new Tooltip(year2 + "-" + formatDay(month2 + 1) + "-" + dayStr));
            b.setPrefSize(22, 22);
            b.setOnAction(this::actionPerformed);
            dayPane.add(b, columnCount, rowCount);
            columnCount++;
            if (columnCount >= tits.length) {
                columnCount = 0;
                rowCount++;
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        } while (calendar.get(Calendar.MONTH) == month2);
        dayPane.getStyleClass().add("day-pane");
//        dayPane.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-width: 1");
        this.calendar.add(Calendar.MONTH, -1);
        this.calendar.set(Calendar.DAY_OF_MONTH, this.day);
        this.pane.getChildren().addAll(weekPane, dayPane);
        return this.pane;
    }

    private void actionPerformed(Event event) {
        if (currBtn != null) {
            currBtn.getStyleClass().setAll(normalBtnClass);
        }
        currBtn = (Button) event.getSource();
        currBtn.getStyleClass().setAll(redBtnClass);
        int day1 = Integer.valueOf(currBtn.getText());
        this.calendar.set(Calendar.DAY_OF_MONTH, day1);
        StringBuffer sb = new StringBuffer();
        sb.append(this.calendar.get(Calendar.YEAR)).append("-")
                .append(this.formatDay(this.calendar.get(Calendar.MONTH) + 1))
                .append("-")
                .append(this.formatDay(this.calendar.get(Calendar.DAY_OF_MONTH)))
                .append(" ")
                .append(this.formatDay(this.calendar.get(Calendar.HOUR_OF_DAY)))
                .append(":")
                .append(this.formatDay(this.calendar.get(Calendar.MINUTE)))
                .append(":")
                .append(this.formatDay(this.calendar.get(Calendar.SECOND)));
        this.setInfo(sb.toString());
    }

    private void nextBtnHandler(Event event) {
        this.calendar.add(Calendar.MONTH, 1);
        int year1 = calendar.get(Calendar.YEAR);
        int maxYear = this.year + this.showYears / 2 - 1;
        if (year1 > maxYear) {
            this.calendar.add(Calendar.MONTH, -1);
            this.setInfo("Over Year: [" + year1 + " > " + maxYear + "]      ");
            return;
        }
        int month1 = this.calendar.get(Calendar.MONTH) + 1;
        this.yearCmb.setValue(String.valueOf(year1));
        this.monthCmb.setValue(this.formatDay(month1));
        updatePane();
    }

    private void preBtnHandler(Event event) {
        this.calendar.add(Calendar.MONTH, -1);
        int year1 = calendar.get(Calendar.YEAR);
        int minYear = this.year - this.showYears / 2;
        if (year1 < minYear) {
            this.calendar.add(Calendar.MONTH, 1);
            this.setInfo("Over Year: [" + year1 + " < " + minYear + "]      ");
            return;
        }
        int month1 = this.calendar.get(Calendar.MONTH) + 1;
        this.yearCmb.setValue(String.valueOf(year1));
        this.monthCmb.setValue(formatDay(month1));
        updatePane();
    }

    private String formatDay(int day) {
        if (day < 10) {
            return "0" + day;
        }
        return String.valueOf(day);
    }

    private void setInfo(String info) {
        if (this.label != null) {
            this.label.setText(info);
        }
    }
}