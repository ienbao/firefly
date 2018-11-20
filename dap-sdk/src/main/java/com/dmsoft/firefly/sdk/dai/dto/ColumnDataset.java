package com.dmsoft.firefly.sdk.dai.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 列集合处理对象，具备添加行和获取列的功能
 * 当前数据存储结构：
 *
 *      行1   行2  行3
 *  列1
 *  列2
 *
 * @author yuanwen
 */
public class ColumnDataset {
  private static Logger logger = LoggerFactory.getLogger(ColumnDataset.class);

  //存储当前所有数据，通过列存储方式保存;第一维度列，第二给度行
  private Object[][] data;

  //行数
  private int rows;

  //列数
  private int cols;


  public int getRows(){

    return this.rows;
  }

  public int getCols(){

    return this.cols;
  }

  /**
   * 列数据集合对象构建
   *
   * @param rows
   * @param cols
   */
  public ColumnDataset(int rows, int cols){
    this.rows = rows;
    this.cols = cols;

    this.data = new Object[this.cols][this.rows];
  }


  /**
   * 根据列名称获取，当关列的所有数据
   *
   * @param col 列下标
   * @return
   */
  public Object[] getColumn(int col){
    if(col > this.getCols()){
      throw new ArrayIndexOutOfBoundsException("集合数据读取下标越界，集合总列数[" + this.getCols() + "]，当前读取列数[" + col + "]");
    }

    return this.data[col - 1];
  }

  /**
   * 设置集合中单元格的值
   *
   * @param row
   * @param col
   * @param data
   */
  public void setCell(int row, int col, Object data){
    if(row > this.getRows() || col > this.getCols()){
      throw new ArrayIndexOutOfBoundsException("集合数据存储下标越界，集合总列数[" +  this.getCols() + "],总行数[" + this.getRows() + "],当前存储列[" + col + "],当前存储行[" + row + "]");
    }

    this.data[col - 1][row - 1] = data;
  }


  /**
   * 获取一个单元格的值
   *
   * @param row 行
   * @param col 列
   */
  public Object getCell(int row, int col){
    if(row > this.getRows() || col > this.getCols()){
      throw new ArrayIndexOutOfBoundsException("集合数据存储下标越界，集合总列数[" +  this.getCols() + "],总行数[" + this.getRows() + "],当前存储列[" + col + "],当前存储行[" + row + "]");
    }

    return this.data[col - 1][row - 1];
  }

}
