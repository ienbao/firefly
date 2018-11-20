package com.dmsoft.firefly.sdk.utils;

import static org.junit.Assert.*;

import org.junit.Test;



public class DAPStringUtilsTest {

	@Test
	public void formatBigDecimal() {
		assertEquals("-0.000021", DAPStringUtils.formatBigDecimal("-0.000021"));
		assertEquals("0.000021", DAPStringUtils.formatBigDecimal("2.1e-5"));
		assertEquals("21000.01", DAPStringUtils.formatBigDecimal("21,000.01"));
		assertEquals("sfsdfsd", DAPStringUtils.formatBigDecimal("sfsdfsd"));
//		assertNull(DAPStringUtils.formatBigDecimal("   "));
		
	}
	
	@Test
	public void formatBigDecimalPref() {
		long start = System.currentTimeMillis();
		//优化前时间：运行时间:1298，优化后时间：运行时间:2104
		for(int index = 0; index < 10000000; index++) {
			assertEquals("-0.000021", DAPStringUtils.formatBigDecimal("-0.000021"));
		}
		System.out.println("运行时间:" + (System.currentTimeMillis() - start));

		start = System.currentTimeMillis();
		//优化前时间：运行时间:1030，优化后时间：运行时间:2833
		for(int index = 0; index < 10000000; index++) {
			assertEquals("0.000021", DAPStringUtils.formatBigDecimal("2.1e-5"));
		}
		System.out.println("运行时间:" + (System.currentTimeMillis() - start));

		start = System.currentTimeMillis();
		//优化前时间：运行时间:42247，优化后时间：运行时间:4305
		for(int index = 0; index < 10000000; index++) {
			assertEquals("21000.01", DAPStringUtils.formatBigDecimal("21,000.01"));
		}
		System.out.println("运行时间:" + (System.currentTimeMillis() - start));


	}
}
