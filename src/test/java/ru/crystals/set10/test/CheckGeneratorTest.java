package ru.crystals.set10.test;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;
import ru.crystals.set10.config.*;
import ru.crystals.pos.check.PositionEntity;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.set10.utils.CashEmulator;

public class CheckGeneratorTest {
	
	
	protected static final Logger log = Logger.getLogger(CheckGeneratorTest.class);
	//CashEmulator checkGenerator = new CashEmulator(Config.RETAIL_HOST, Integer.valueOf(Config.SHOP_NUMBER), Integer.valueOf(Config.CASH_NUMBER));
	CashEmulator cashEmulator;
	
	@Test (description = "Сгенерить чеки продажи и чеки возврата и закрыть смену")
	public void testSendReturnCheck(){
		cashEmulator = CashEmulator.getCashEmulator(Config.RETAIL_HOST, Integer.valueOf(Config.SHOP_NUMBER), Integer.valueOf(Config.CASH_NUMBER));
		
		log.info("Send checks to " + Config.SHOP_NUMBER + 
				"; dbUser: " + Config.DB_USER + 
				"; dbPassword: " + Config.DB_PASSWORD );
		
		//cashEmulator.nextIntroduction();
		for (int i=1; i<=Integer.valueOf(Config.CHECK_COUNT); i++) {
			PurchaseEntity pe = (PurchaseEntity) cashEmulator.nextPurchase();
			PositionEntity pos = pe.getPositions().get(0);
			cashEmulator.nextRefundCheck(pe, pos, pos.getQnty(), false);
			cashEmulator.nextZReport();
		}	
		//cashEmulator.nextWithdrawal();
		//cashEmulator.nextZReport();
	}
}
