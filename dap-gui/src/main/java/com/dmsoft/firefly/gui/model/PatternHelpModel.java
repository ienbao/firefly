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

    /**
     * constructor
     *
     * @param character   character
     * @param description description
     */
    public PatternHelpModel(String character, String description) {
        this.character = new SimpleStringProperty(character);
        this.description = new SimpleStringProperty(description);
    }

    public String getCharacter() {
        return character.get();
    }

    /**
     * method set character
     *
     * @param character character
     */
    public void setCharacter(String character) {
        this.character.set(character);
    }

    /**
     * method to get character property
     *
     * @return string property
     */
    public StringProperty characterProperty() {
        return character;
    }

    public String getDescription() {
        return description.get();
    }

    /**
     * method to set description
     *
     * @param description description
     */
    public void setDescription(String description) {
        this.description.set(description);
    }

    /**
     * method to get description property
     *
     * @return string property
     */
    public StringProperty descriptionProperty() {
        return description;
    }
}
