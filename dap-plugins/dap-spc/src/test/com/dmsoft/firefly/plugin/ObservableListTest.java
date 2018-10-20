/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;

/**
 * Created by Ethan.Yang on 2018/3/1.
 */
public class ObservableListTest {
    public static void main(String[] args) {
        ObservableList<String> list = FXCollections.observableArrayList();
        list.addListener((ListChangeListener<String>) c -> {

            while (c.next()) {
                if (c.wasPermutated()) {
                    System.out.println("wasPermutated" + StringUtils.join(c.getList(), ','));
                } else if (c.wasAdded()) {
                    System.out.println("wasAdded" + StringUtils.join(c.getList(), ','));
                } else if (c.wasRemoved()) {
                    System.out.println("wasRemoved" + StringUtils.join(c.getRemoved(), ','));
                } else if (c.wasUpdated()) {
                    System.out.println("ASDFA");
                }
            }
        });
        list.add("ASDFA");
        list.remove("ASDFA");
        list.add("ASDF");
        list.clear();
        list.add("A");
        list.add("B");
        list.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if ("B".equals(o2)) {
                    return 1;
                }
                return 0;
            }
        });
        list.forEach(s -> System.out.println(s));
    }
}
