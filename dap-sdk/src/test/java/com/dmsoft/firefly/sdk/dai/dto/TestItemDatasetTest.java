package com.dmsoft.firefly.sdk.dai.dto;


import static org.junit.Assert.fail;

import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;



public class TestItemDatasetTest {

  private TestItemDataset testItemDataset;

  @Before
  public void before(){
    List<String> testItestNameList = Lists.newArrayList();
    testItestNameList.add("column1");
    testItestNameList.add("column2");
    testItemDataset = new TestItemDataset(testItestNameList, 10);
  }

  @Test
  public void addRowData() {

    for(int index = 1 ; index <= 10; index++){
      Map<String, String> rowData = new HashMap<String, String>();
      rowData.put("column1", index + "_column1");
      rowData.put("column2", index + "_column2");

      testItemDataset.addRowData(String.valueOf(index), rowData);
    }


    //下标越界
    try{
      Map<String, String> rowData = new HashMap<String, String>();
      rowData.put("column1", "11_column1");
      rowData.put("column2", "11_column2");

      this.testItemDataset.addRowData("11", rowData);

      fail("没有抛出数据下标越界异常，数据容量是10行,当前添加了11行.");
    }catch (ArrayIndexOutOfBoundsException e){

    }
  }
}