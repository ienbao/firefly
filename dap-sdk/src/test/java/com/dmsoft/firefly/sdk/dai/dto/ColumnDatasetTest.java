package com.dmsoft.firefly.sdk.dai.dto;

import static org.junit.Assert.*;

import org.junit.Test;

public class ColumnDatasetTest {


  @Test
  public void testAll(){

    ColumnDataset columnDataset = new ColumnDataset(3,2);

    columnDataset.setCell(1,1, "1-1");
    columnDataset.setCell(1,2, "1-2");
    columnDataset.setCell(2,1, "2-1");
    columnDataset.setCell(2,2, "2-2");
    columnDataset.setCell(3,1, "3-1");
    columnDataset.setCell(3,2, "3-2");

    assertArrayEquals(new String[]{"1-1", "2-1", "3-1"}, columnDataset.getColumn(1));

    try {
      columnDataset.setCell(4, 1, "error");
      fail("下标越界示抛出异常！");
    }catch (ArrayIndexOutOfBoundsException e){
    }

    try {
      columnDataset.getColumn(3);
      fail("下标越界示抛出异常！");
    }catch (ArrayIndexOutOfBoundsException e){

    }


  }

}