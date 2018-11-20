package com.dmsoft.firefly.sdk.dai.dto;

import com.dmsoft.firefly.sdk.algorithm.HashListSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAP数据计算内存管数据集，管理列对象和当前添加行号
 *
 * @author yuanwen
 *
 */
public class TestItemDataset {
  private static Logger logger = LoggerFactory.getLogger(TestItemDataset.class);

  //数据列名字段
  private HashListSet<String> columnNameList;

  //列存储对象集合
  private ColumnDataset columnDataset;

  //当前添加行号
  private int addRowIndex;

  //行键值
  private HashListSet<String> rowKeyList;

  /**
   * 对象构建方法
   *
   * @param columnNameList 数据集列列
   * @param rows 行数
   */
  public TestItemDataset(List<String> columnNameList, int rows){
    this.setColumnNameList(columnNameList);
    this.columnDataset = new ColumnDataset(rows, columnNameList.size());
    this.addRowIndex = 1;
    this.rowKeyList = new HashListSet<>(rows);
  }

  /**
   * 设置字段列集合
   *
   * @param columnNameList
   */
  public void setColumnNameList(List<String>  columnNameList){
    this.columnNameList = new HashListSet<>();
    this.columnNameList.addAll(columnNameList);
  }

  /**
   * 获取列名对象集
   *
   * @return
   */
  public List<String> getColumnNameList(){
    return Lists.newArrayList(this.columnNameList);
  }

  /**
   * 获取行key对象集
   *
   * @return
   */
  public List<String> getRowKeyList(){
    return Lists.newArrayList(this.rowKeyList);
  }

  /**
   * 获取列数
   *
   * @return
   */
  public int getColumnLen(){

    return this.columnNameList.size();
  }


  /**
   * 根据列名称获取，当关列的所有数据
   *
   * @param testItemName
   * @return
   */
  public Object[] getColumn(String testItemName){
    int colIndex = this.columnNameList.indexOf(testItemName);
    if(colIndex == -1){
      return null;
    }

    return this.columnDataset.getColumn(colIndex + 1);
  }


  /**
   * 添加一行数据,会存在并发问题，在并发项目中需要解决
   *
   * @param rowKey 行唯一键值
   * @param rowData 行列数据集合
   */
  public void addRowData(String rowKey, Map<String, String> rowData){
    if(rowData == null || rowData.isEmpty() || StringUtils.isEmpty(rowKey)){
      logger.warn("当前要添加的记录关键内容为空, rowData[{}], rowKey[{}]", rowData, rowKey);
      return ;
    }

    this.rowKeyList.add(rowKey);

    for (Map.Entry<String, String> entry : rowData.entrySet()){
      String key = entry.getKey();
      String value = entry.getValue();

      int columnIndex = columnNameList.indexOf(key);
      if(columnIndex == -1){
        throw new RuntimeException("当前数据不是数据集中需求的列数据， key:" + key + ", value:" + value);
      }


      if(logger.isDebugEnabled()) {
        logger.debug("添加数据，当前行[{}],当前列名:[{}], 当前列:[{}]", this.addRowIndex, key, columnIndex);
      }
      //赋值数据集
      this.columnDataset.setCell(this.addRowIndex, columnIndex + 1, value);
    }

    this.addRowIndex++;
  }

  /**
   * 设置单元格的值
   *
   * @param rowKey
   * @param columnName
   * @param value
   */
  public void setCellValue(String rowKey, String columnName, String value){

    this.rowKeyList.add(rowKey);
    this.columnNameList.add(columnName);

    int row = this.rowKeyList.indexOf(rowKey);
    int col = this.columnNameList.indexOf(columnName);

    if(row == -1 || col == -1){
      throw new RuntimeException("数据集中当前行列不存在, rowKey[" + rowKey + "], columnName[" + columnName + "], row[" + row + "], col[" + col + "]");
    }

    this.columnDataset.setCell(row + 1, col + 1, value);
  }


  /**
   * 获取一个单元格的值
   *
   *
   * @param rowKey
   * @param columnName
   * @return
   */
  public String getCellValue(String rowKey, String columnName){
      int row = this.rowKeyList.indexOf(rowKey);
      int col = this.columnNameList.indexOf(columnName);

      if(row == -1 || col == -1){
        throw new RuntimeException("数据集中当前行列不存在, rowKey[" + rowKey + "], columnName[" + columnName + "], row[" + row + "], col[" + col + "]");
      }

      return (String)this.columnDataset.getCell(row + 1, col + 1);
  }

}
