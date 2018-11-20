package com.dmsoft.firefly.sdk.algorithm;

import static org.junit.Assert.*;

import com.google.common.collect.Lists;
import java.util.List;
import org.junit.Test;

public class HashListSetTest {


  @Test
  public void index(){

    HashListSet<String> hashList = new HashListSet<>();
    hashList.add("list1");
    hashList.add("list2");
    hashList.add("list3");

    assertEquals(0, hashList.indexOf("list1"));
    assertEquals(1, hashList.indexOf("list2"));
    assertEquals(2, hashList.indexOf("list3"));


    List<String> list = Lists.newArrayList();
    list.add("list1");
    list.add("list4");
    list.add("list5");
    hashList.addAll(list);
    assertEquals(0, hashList.indexOf("list1"));
    assertEquals(3, hashList.indexOf("list4"));
    assertEquals(4, hashList.indexOf("list5"));

  }

}