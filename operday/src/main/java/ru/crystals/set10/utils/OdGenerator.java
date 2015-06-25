package ru.crystals.set10.utils;

import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.set10.config.Config;


public class OdGenerator implements Runnable {
	
	private static final Logger log = Logger.getLogger(OdGenerator.class);
	
	static int cashnumber = 3;
	
	CashEmulator cashEmulator;
	
	
	public OdGenerator (long operdayShift) {
		this.cashEmulator = CashEmulator.getCashEmulator(Config.RETAIL_HOST, Integer.valueOf(Config.SHOP_NUMBER), cashnumber);
		this.cashEmulator.setTimeOfset(operdayShift);
		log.info("Будет открыт опердень на дату: " + DisinsectorTools.getDate("dd:MM:yyyy", new Date().getTime()- operdayShift));
		this.cashEmulator.useNextShift();
		cashnumber++;
	}
	
	public void run() {
		long sumCashIn = DisinsectorTools.random(100000) + 100;
		log.info("Касса номер " + cashnumber);
		
		/* внесение*/
		this.cashEmulator.nextIntroduction();
		
		/* чек */
		PurchaseEntity p1 = (PurchaseEntity) this.cashEmulator.nextPurchase();
		
		/* возврат по чеку*/
		HashMap<Long, Long> returnPositions = new HashMap<Long, Long>();
		returnPositions.put(1L, 1L);
		this.cashEmulator.nextRefundPositions(p1, returnPositions, false);
		
		/* изъятие и Z отчет*/
		this.cashEmulator.nextWithdrawal();
		this.cashEmulator.nextZReport(sumCashIn, sumCashIn + (DisinsectorTools.random(100000) + 100));
	}
	
	

}
