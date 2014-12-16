package ru.crystals.set10.test;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ru.crystals.set10.config.*;
import ru.crystals.pos.check.PositionEntity;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.set10.utils.CashEmulator;

public class CheckGeneratorTest {
	
	protected static final Logger log = Logger.getLogger(CheckGeneratorTest.class);
	CashEmulator cashEmulator;
	HashMap<Long, Long>  returnPositions = new HashMap<Long, Long>(); 
	
	@BeforeClass
	public void setupCash(){
		cashEmulator = CashEmulator.getCashEmulator(Config.RETAIL_HOST, Integer.valueOf(Config.SHOP_NUMBER), Integer.valueOf(Config.CASH_NUMBER));
	}
	
	@Test (	
			description = "Сгенерить чеки продажи и чеки возврата и закрыть смену")
	public void testSendReturnCheck(){
		
		log.info("Send checks to " + Config.SHOP_NUMBER);
		
		cashEmulator.nextIntroduction();
		for (int i=1; i<=Integer.valueOf(Config.CHECK_COUNT); i++) {
			PurchaseEntity pe = (PurchaseEntity) cashEmulator.nextPurchase();
			returnPositions.put(1L, 1L * 1000);
			cashEmulator.nextRefundPositions(pe, returnPositions , false);
		//	cashEmulator.nextRefundAll(pe, false);
		}	
		cashEmulator.nextWithdrawal();
		cashEmulator.nextZReport();
	}
	
	@Test (enabled = false, description = "")
	public void testSendPartialReturnCheck(){
		cashEmulator = CashEmulator.getCashEmulator(Config.RETAIL_HOST, Integer.valueOf(Config.SHOP_NUMBER), Integer.valueOf(Config.CASH_NUMBER));
		
		log.info("Send checks to " + Config.SHOP_NUMBER);

		PurchaseEntity pe = (PurchaseEntity) cashEmulator.nextPurchase();
		
		for (int i=0; i<pe.getPositions().size(); i++){
			PositionEntity position = pe.getPositions().get(i);
			returnPositions.put(position.getNumber(), 1L * 1000);
		}
		cashEmulator.nextRefundPositions(pe, returnPositions, false);
		
		returnPositions = new HashMap<Long, Long>(); 
		for (int i=0; i<pe.getPositions().size(); i++){
			PositionEntity position = pe.getPositions().get(i);
			if (position.getQnty() > 1L * 1000) {
				returnPositions.put(position.getNumber(), position.getQnty() - 1L * 1000);
			}	
		}
		cashEmulator.nextRefundPositions(pe, returnPositions, false);
	}
	
	
	
}
