package ru.crystals.set10.test;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.crystals.set10.config.*;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.set10.utils.CashEmulator;
import ru.crystals.set10.utils.CashEmulatorPayments;

public class ERPExportTest {
	
	protected static final Logger log = Logger.getLogger(ERPExportTest.class);
	
	CashEmulator cashEmulator;
	CashEmulator cashEmulatorVirtualShop;
	HashMap<Long, Long>  returnPositions = new HashMap<Long, Long>(); 
	
	CashEmulatorPayments payments = new CashEmulatorPayments();
	PurchaseEntity p1;
	PurchaseEntity p2;
	
	@BeforeClass
	public void setupCash(){
		cashEmulator = CashEmulator.getCashEmulator(Config.RETAIL_HOST, Integer.valueOf(Config.SHOP_NUMBER), Integer.valueOf(Config.CASH_NUMBER));
		cashEmulatorVirtualShop = CashEmulator.getCashEmulator(Config.CENTRUM_HOST, Integer.valueOf(Config.VIRTUAL_SHOP_NUMBER), Integer.valueOf(Config.CASH_NUMBER));
		/*
		 * Очистить таблицы в SAP эмуляторе
		 */
	}
	
	@Test (	description = "Экспорт документов в SAP. Чек экспортируется в SAP")
	public void testExportChecks(){
		for (int i = 0; i<2; i++) {
			p1 = (PurchaseEntity) cashEmulator.nextPurchase();
//			p2 = (PurchaseEntity) cashEmulatorVirtualShop.nextPurchase();
		}
	}
	
	@Test (	description = "Экспорт документов в SAP. Внесение экспортируется в SAP")
	public void testExportIntroduction(){
		cashEmulator.nextIntroduction();
//		cashEmulatorVirtualShop.nextIntroduction();
	}
	
	@Test (	description = "Экспорт документов в SAP. Изъятие экспортируется в SAP")
	public void testExportWithDrawals(){
		cashEmulator.nextWithdrawal();
//		cashEmulatorVirtualShop.nextWithdrawal();
	}
	
	@Test (	description = "Экспорт документов в SAP. Z-отчет экспортируется в SAP")
	public void testExportZReport(){
		cashEmulator.nextZReport();
//		cashEmulatorVirtualShop.nextZReport();
	}
	
}
