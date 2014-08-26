package ru.crystals.set10.test;

import org.apache.log4j.Logger;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import ru.crystals.pos.check.PositionEntity;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.set10.utils.CheckGenerator;

public class CheckGeneratorTest {
	
	 protected static final Logger log = Logger.getLogger(CheckGeneratorTest.class);
	
	@Parameters({"shopNumber", "dbUser", "dbPassword", "checkCount", "retail_ip"})
	@Test 
	public void testSendReturnCheck(String shopNumber, String dbUser, String dbPassword, String checkCount, String retail_ip){
		log.info("Send checks to " + shopNumber + "; dbUser: " + dbUser + "; dbPassword: " + dbPassword);
		
		for (int i=1; i<=Integer.valueOf(checkCount); i++) {
			CheckGenerator checkGenerator = new CheckGenerator(retail_ip, Integer.valueOf(shopNumber), 1);
			PurchaseEntity pe = (PurchaseEntity) checkGenerator.nextPurchase();
			checkGenerator.logCheckEntities(pe);
			PositionEntity prE = pe.getPositions().get(0);
			delay();
			checkGenerator.nextReturnCheck(prE, prE.getQnty());
		}	
	}
	
	private void delay(){
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
