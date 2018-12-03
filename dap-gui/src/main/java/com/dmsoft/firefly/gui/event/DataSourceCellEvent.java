package com.dmsoft.firefly.gui.event;

import javafx.event.Event;
import javafx.event.EventType;

public class DataSourceCellEvent extends Event {

    public static final EventType<DataSourceCellEvent> DELETE =
            new EventType<>("DELETE");

    public static final EventType<DataSourceCellEvent> RENAME =
            new EventType<>("RENAME");


    public DataSourceCellEvent(EventType eventType) {
        super(eventType);
    }
}
