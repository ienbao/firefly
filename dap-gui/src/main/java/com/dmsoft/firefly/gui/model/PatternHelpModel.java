/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Guang.Li on 2018/2/27.
 */
public class PatternHelpModel {
    private StringProperty character;
    private StringProperty description;

    public PatternHelpModel(String character, String description) {
        this.character = new SimpleStringProperty(character);
        this.description = new SimpleStringProperty(description);
    }

    public String getCharacter() {
        return character.get();
    }

    public StringProperty characterProperty() {
        return character;
    }

    public void setCharacter(String character) {
        this.character.set(character);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }
}
