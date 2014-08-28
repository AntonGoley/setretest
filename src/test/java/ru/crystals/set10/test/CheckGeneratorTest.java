package ru.crystals.set10.test;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;
import ru.crystals.set10.config.*;
import ru.crystals.pos.check.PositionEntity;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.set10.utils.CheckGenerator;

public class CheckGeneratorTest {
	
	 protected static final Logger log = Logger.getLogger(CheckGeneratorTest.class);
	
	@Test 
	public void testSendReturnCheck(){
		log.info("Send checks to " + Config.SHOP_NUMBER + 
				"; dbUser: " + Config.DB_USER + 
				"; dbPassword: " + Config.DB_PASSWORD);
		
		for (int i=1; i<=Integer.valueOf(Config.CHECKS_COUNT); i++) {
			CheckGenerator checkGenerator = new CheckGenerator(Config.RETAIL_HOST, Integer.valueOf(Config.SHOP_NUMBER), 1);
			PurchaseEntity pe = (PurchaseEntity) checkGenerator.nextPurchase();
			PositionEntity pos = pe.getPositions().get(0);
			checkGenerator.nextRefundCheck(pe, pos, pos.getQnty(), false);
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
