package com.dmsoft.firefly.gui.demo;

import java.awt.event.InputEvent;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import sun.util.resources.cldr.chr.CalendarData_chr_US;

/**
 * 自定义组件样例
 *
 * @author yuanwen
 */
public class CustomTextField extends TextField {

  public CustomTextField(){
    super();

    this.textProperty().addListener((obVal, oldVal, newVal) ->{
      if(newVal.equals("ok")){
        fireEvent(new ActionEvent());
      }
    });
  }


}
