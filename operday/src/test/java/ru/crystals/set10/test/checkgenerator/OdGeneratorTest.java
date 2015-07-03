package ru.crystals.set10.test.checkgenerator;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import ru.crystals.set10.test.maincash.MainCashConfigTool;


public class OdGeneratorTest {
	
	private static final Logger log = Logger.getLogger(OdGeneratorTest.class);
	
	long day = 86400000L;
	
	@BeforeClass
	private void clearOd(){
		MainCashConfigTool.clearOD();
	}
	
	
	@Parameters ("operdays")
	@Test (	description = "Генерация операционных дней на позавчера, вчера, сегодня", 
			groups = "od_generator")
	public void testSendChecks(@Optional Integer operdays){
		
		if (operdays == null) {
			operdays = 3;
		};	
		
		log.info("В системе будет создано операционных дней: " +  operdays);
		
		/*сколько будет создано опердней */
		Long[] od_range = new Long[operdays];
		
		for (int i = operdays; i>0; i-- ) {
			Number n = i - 1;
			od_range[n.intValue()] = (day * n.longValue() );
		}

		MainCashConfigTool.createODWithCashDocs(od_range);

	}	
	
}
