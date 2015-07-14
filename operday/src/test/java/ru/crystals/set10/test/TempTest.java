package ru.crystals.set10.test;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.utils.CashEmulator;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.GoodGenerator;
import ru.crystals.set10.utils.PurchaseGenerator;
import ru.crystals.set10.utils.SoapRequestSender;
import ru.crystals.setretailx.products.catalog.Good;
import static ru.crystals.set10.utils.GoodGenerator.*;


public class TempTest {

	protected static final Logger log = Logger.getLogger(TempTest.class);
	long date = new Date().getTime();
	private Calendar calendar;
	SoapRequestSender soapSender = new SoapRequestSender();
	long macPrefix = new Date().getTime();
	
	@BeforeClass
	private void setUp(){
		calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, 1);
		calendar.add(Calendar.MONTH, 2);
		calendar.set(Calendar.DATE, 1);
		log.info(DisinsectorTools.getDate("dd.MM.yyyy", calendar.getTimeInMillis()));
		
		soapSender.setSoapServiceIP(Config.RETAIL_HOST);
	}
	
	@Test (enabled = false)
	public void fakeTest(){
		
		Good good = new GoodGenerator().generateGood(GOODTYPE_WEIGHT);
		soapSender.sendGood(good);
		
		PurchaseEntity p1;
		p1 = PurchaseGenerator.generatePurchase(0, false);
		PurchaseGenerator.addPositionToPurchase(p1, good, 50000L, 2);
		
		CashEmulator.getCashEmulator(Config.RETAIL_HOST, Integer.valueOf(Config.SHOP_NUMBER), 1).nextPurchase(p1);
	}
	
	@Test ()
	public void testPriceChecker(){
		String mac = "mac_" + macPrefix++;
		
		for (int i=0; i<5; i++) {
			Good good = new GoodGenerator().generateGood(GOODTYPE_PIECE);
			soapSender.sendGood(good);
			soapSender.sendPriceCheckerRequest(mac, good.getBarCodes().get(0).getCode());
		}	
		
	}
	
}
