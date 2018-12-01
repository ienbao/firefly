package com.dmsoft.firefly.gui.demo;

import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMain {

  private static Logger logger = LoggerFactory.getLogger(TestMain.class);

  private void firstTest(){
    IntegerProperty integerProperty2 = new SimpleIntegerProperty(2);
    IntegerProperty integerProperty1 = new SimpleIntegerProperty(1);
    NumberBinding sum = integerProperty1.add(integerProperty2);

    logger.info("first sum value:" + sum.getValue());

    integerProperty1.set(10);

    logger.info("last sum value:" + sum.getValue());
  }

  public static void main(String[] args){

    TestMain testMain = new TestMain();
    testMain.firstTest();
  }
}
