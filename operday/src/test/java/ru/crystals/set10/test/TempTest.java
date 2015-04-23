package ru.crystals.set10.test;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ru.crystals.set10.utils.DisinsectorTools;

public class TempTest {

	protected static final Logger log = Logger.getLogger(TempTest.class);
	long date = new Date().getTime();
	private Calendar calendar;
	
	
	@BeforeClass
	public void setUp(){
		calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, 1);
		calendar.add(Calendar.MONTH, 2);
		calendar.set(Calendar.DATE, 1);
		log.info(DisinsectorTools.getDate("dd.MM.yyyy", calendar.getTimeInMillis()));
	}
	
	@Test
	public void fakeTest(){
		
	}
	
	
}
