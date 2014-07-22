package ru.crystals.disinsector2.test.dataproviders;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.testng.annotations.DataProvider;


public class SpiritRistrictionsDataprovider {
	
	
	static String calendarDateFormat = "dd.MM.yy (mm:ss)";
	static String assertDateFormat = "yyyy-MM-dd HH:mm:ss";
	static String timeFormat ="HH:mm";
	
	
	private static String getDate(String format, long date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}
	
	private static String getName(){
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "Алкогольное ограничение_" + new Date().getTime();
	}
	
	@DataProvider (name = "Процент содержания алкоголя")
	public static Object[][] spiritPercent() {
		return new Object[][] {
		{getName(), "0", "//restriction[@name=\"%s\"][alcoholic-content-percentage='0.00']"},
		{getName(), "5", "//restriction[@name=\"%s\"][alcoholic-content-percentage='5.00']"},
		{getName(), "5.0001", "//restriction[@name=\"%s\"][alcoholic-content-percentage='5.00']"},
		};
	}
	
	@DataProvider (name = "Период действия")
	public static Object[][] datePeriod() {
		long sinceDate = new Date().getTime();
		long tillDate = sinceDate + 60*60*24*7*1000; 
		String fromDate = getDate(calendarDateFormat, sinceDate);
		String toDate = getDate(calendarDateFormat, tillDate);
		
		return new Object[][] {
		{getName(), fromDate + " — " + toDate, getDate(assertDateFormat, sinceDate), "//restriction[@name=\"%s\"][since-date='%s']"},
		{getName(), fromDate + " — " + toDate, getDate(assertDateFormat, tillDate), "//restriction[@name=\"%s\"][till-date='%s']"},
		};
	}
	
	@DataProvider (name = "Время действия")
	public static Object[][] timePeriod() {
		long sinceTime = new Date().getTime();
		long tillTime = sinceTime + 60*60*1*1000; 
		String fromTime = getDate(timeFormat, sinceTime);
		String toTime = getDate(timeFormat, tillTime);
		
		return new Object[][] {
		{getName(), fromTime , toTime, fromTime + ":00", "//restriction[@name=\"%s\"][since-time='%s']"},
		{getName(), fromTime , toTime, toTime + ":00", "//restriction[@name=\"%s\"][till-time='%s']"},
		};
	}

	@DataProvider (name = "Минимальная цена")
	public static Object[][] minPrice() {
		long today = new Date().getTime();
		return new Object[][] {
		{getName(), "0", "//restriction[@name=\"%s\"][alcoholic-content-percentage='0.00']"},

		};
	}
	
}

