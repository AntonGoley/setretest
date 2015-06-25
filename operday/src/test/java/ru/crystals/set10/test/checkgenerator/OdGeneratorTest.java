package ru.crystals.set10.test.checkgenerator;


import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.crystals.set10.test.maincash.ConfigMainCash;


public class OdGeneratorTest {
	
	long day = 86400000L;
	
	@BeforeClass
	private void clearOd(){
		ConfigMainCash.clearOD();
	}
	
	
	@Test (	description = "")
	public void testSendChecks(){
		Long[] operDays = {
				day*2L,
				day,
				0L
			};
		
		ConfigMainCash.createODWithCashDocs(operDays);

	}	
	
}
