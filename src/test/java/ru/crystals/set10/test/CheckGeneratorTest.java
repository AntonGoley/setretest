package ru.crystals.set10.test;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;
import ru.crystals.set10.config.*;
import ru.crystals.pos.check.PositionEntity;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.set10.utils.CheckGenerator;
import ru.crystals.set10.utils.DisinsectorTools;

public class CheckGeneratorTest {
	
	public String host = System.getProperty("testng_target_host");
	public String shopNumber = System.getProperty("testng_shop_number");
	public String checkNumber = System.getProperty("testng_check_number");
	public String dbUser = System.getProperty("testng_dbUser");
	public String dbPassword = System.getProperty("testng_dbPassword");
	
	
	protected static final Logger log = Logger.getLogger(CheckGeneratorTest.class);
	CheckGenerator checkGenerator = new CheckGenerator(host, Integer.valueOf(shopNumber), 1);
	
	@Test (description = "Сгенерить чеки продажи и чеки возврата")
	public void testSendReturnCheck(){
		log.info("Send checks to " + shopNumber + 
				"; dbUser: " + dbUser + 
				"; dbPassword: " + dbPassword);
		
		for (int i=1; i<=Integer.valueOf(checkNumber); i++) {
			PurchaseEntity pe = (PurchaseEntity) checkGenerator.nextPurchase();
			PositionEntity pos = pe.getPositions().get(0);
			checkGenerator.nextRefundCheck(pe, pos, pos.getQnty(), false);
		}	
	}
}
