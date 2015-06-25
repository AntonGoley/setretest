package ru.crystals.set10.test.checkgenerator;


import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;



import ru.crystals.set10.test.maincash.ConfigMainCash;


public class OdGeneratorTest {
	
	long day = 86400000L;
	
	@BeforeClass
	private void clearOd(){
		ConfigMainCash.clearOD();
	}
	
	
	//@Parameters ("operdays")
	@Test (	description = "Генерация 3 операционных дней на позавчера, вчера, сегодня", 
			groups = "od_generator")
	public void testSendChecks(/*int operdays*/){
		
		
		
		Long[] operDays = {
				day*2L,
				day,
				0L
			};
		ConfigMainCash.createODWithCashDocs(operDays);

	}	
	
}
