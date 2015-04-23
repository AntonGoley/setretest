package ru.crystals.set10.test;


import java.util.HashMap;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.crystals.set10.config.*;
import ru.crystals.discount.processing.entity.LoyTransactionEntity;
import ru.crystals.httpclient.HttpFileTransport;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.set10.utils.CashEmulator;
import ru.crystals.set10.utils.CashEmulatorDiscounts;
import ru.crystals.set10.utils.PaymentGenerator;

public class LoyalExportTest {
	
	protected static final Logger log = Logger.getLogger(LoyalExportTest.class);
	
	HttpFileTransport httpFileTransport = new HttpFileTransport();
	
	CashEmulator cashEmulator;
	CashEmulator cashEmulatorVirtualShop;
	HashMap<Long, Long>  returnPositions = new HashMap<Long, Long>(); 
	
	PaymentGenerator payments = new PaymentGenerator();
	PurchaseEntity p1;
	PurchaseEntity p2;
	
	@BeforeClass
	public void setupCash(){
		cashEmulator = CashEmulator.getCashEmulator(Config.RETAIL_HOST, Integer.valueOf(Config.SHOP_NUMBER), Integer.valueOf(Config.CASH_NUMBER) + 2);
		//cashEmulatorVirtualShop = CashEmulator.getCashEmulator(Config.CENTRUM_HOST, Integer.valueOf(Config.VIRTUAL_SHOP_NUMBER), Integer.valueOf(Config.CASH_NUMBER));
		/*
		 * Очистить таблицы в SAP эмуляторе
		 */
	}
	
	@Test (	description = "Экспорт документов в SAP. Чек экспортируется в SAP")
	public void testExportChecks(){
		
		CashEmulatorDiscounts payments = new CashEmulatorDiscounts();
		LoyTransactionEntity loyTransaction = new LoyTransactionEntity();
		
		for (int i = 0; i < 1; i++) {
			p1 = cashEmulator.nextPurchaseWithoutSending();
			loyTransaction = payments.addDiscountForPosition(p1, 2, true);
			p1.setDiscountValueTotal(loyTransaction.getDiscountValueTotal());
			
			cashEmulator.nextPurchase(p1);
			cashEmulator.sendLoy(loyTransaction, p1);
			//cashEmulator.nextRefundAll(p1, false);
			//cashEmulator.nextZReport();
		}	
//		p2 = (PurchaseEntity) cashEmulatorVirtualShop.nextPurchase();
//		cashEmulatorVirtualShop.sendLoy(payments.addDiscount(p2), p2);
		
	}
	
}
