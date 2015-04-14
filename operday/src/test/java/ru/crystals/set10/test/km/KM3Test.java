package ru.crystals.set10.test.km;


import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

import junit.framework.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.operday.HTMLRepotResultPage;
import ru.crystals.set10.pages.operday.OperDayPage;
import ru.crystals.set10.pages.operday.cashes.CashesPage;
import ru.crystals.set10.pages.operday.cashes.KmPage;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.setretailx.cash.CashVO;
import static ru.crystals.set10.pages.operday.cashes.KmPage.*;
import static ru.crystals.set10.pages.operday.OperDayPage.CASHES;

@Test(groups = {"retail", "centrum"})
public class KM3Test extends AbstractTest{
	
	KmPage km3;
	OperDayPage operDay;
	HTMLRepotResultPage htmlReportResults;
	private static PurchaseEntity purchase;
	private static PurchaseEntity purchaseReturn;
	
	private int km3Tablerows;
	
	private static String SQL_CLEAN_KM3 = "delete from od_km3";
	private static String SQL_CLEAN_KM3_ROW = "delete from od_km3_row";
	private HashMap<Long, Long>  returnPositions = new HashMap<Long, Long>(); 
	
	private String reportText;
	private boolean reportOpened = false;
	
	@BeforeClass
	public void prepareData(){
		dbAdapter.batchUpdateDb(DB_OPERDAY, new String[] {SQL_CLEAN_KM3, SQL_CLEAN_KM3_ROW} );
		log.info("Записи в таблице od_km3 и в таблице od_km3_row удалены в базе " + DB_OPERDAY);
		
		km3 = new LoginPage(getDriver(), TARGET_HOST_URL).
				openOperDay(Config.MANAGER, Config.MANAGER_PASSWORD)
				.navigatePage(CashesPage.class, CASHES)
				.openTab(KmPage.class, CashesPage.LOCATOR_ACTS_TAB)
				.switchToKm(LOCATOR_KM3);
		
		//количество актов km3
		km3Tablerows = km3.getKmCountOnPage(LOCATOR_KM3_TABLE);
		
		purchase = (PurchaseEntity) cashEmulator.nextPurchase();
		//Возвращаем 1-ю позицию
		returnPositions.put(1L, 1L * 1000);
		
		purchaseReturn = (PurchaseEntity) cashEmulator.nextRefundPositions(purchase, returnPositions, true);
	}
	
	@DataProvider (name = "Поля КМ3")
	private static Object[][] km3Fields(){
		BigDecimal sumRetunPositions  = purchaseReturn.getCheckSumEndBigDecimal();
		
		CashVO cashVO = new CashVO();
		cashVO = cashEmulator.setCashVO(cashEmulator.getCashNumber(), TARGET_SHOP, new Date().getTime());
		cashEmulator.sendCashVO(cashVO);
		
		return new Object[][]{
				{"Название формы", "О ВОЗВРАТЕ ДЕНЕЖНЫХ СУММ ПОКУПАТЕЛЯМ (КЛИЕНТАМ)\nПО НЕИСПОЛЬЗОВАННЫМ КАССОВЫМ ЧЕКАМ"},
				{"Строка Итого", ("Итого " + String.valueOf(sumRetunPositions)).replace(".", ",")},
				{"Содержит ККМ: номер производителя (factory num)", cashVO.getFactoryNum()},
				{"Содержит ККМ: рег. номер (fisc num)", cashVO.getFiscalNum()},
		};
	}
	
	@Test( description = "SRTE-28. Если в систему пришел первый возвратный чек, создается форма КМ3")
	public void testKM3CreatesAfter1stRefund(){
		km3.switchToKm(LOCATOR_KM6).switchToKm(LOCATOR_KM3);
		km3 = new KmPage(getDriver());
		Assert.assertEquals("Не появилась форма КМ3", km3Tablerows + 1, km3.getKmCountOnPage(LOCATOR_KM3_TABLE));
	}
	
	@Test (	dependsOnMethods ="testKM3CreatesAfter1stRefund",
			description = "SRTE-28. Правильность заполнения формы КМ3 данными",
			dataProvider = "Поля КМ3")
	public void testKM3Data(String fiels, String expectedValue){
		if (!reportOpened) {
			reportText = km3.printAllKmForms();
			reportOpened = true;
		}
		log.info("Значение поля: " + fiels);
		Assert.assertTrue("Неверное значение поля в форме КМ3", reportText.contains(expectedValue));
	}
//	
//	@Test( description = "SRL-2. Если форма КМ3 распечатана, следующий возвратный чек попадает в новую форму КМ3")
//	public void testNewKM3CreatesIfcurrentPrinted(){
//	}
//	
//	@Test( description = "SRL-2. Если в форме КМ3 12 чеков возврата, следующий возвратный чек попадает в новую форму КМ3")
//	public void testNewKM3After12RefundChecks(){
//	}
//	
//	@Test( description = "SRL-2. Новая форма КМ3 создается для новой смены")
//	public void testKM3CreatesForNewShift(){
//	}
//	
//	@Test( description = "Новая форма КМ3 создается для новой кассы")
//	public void testKM3CreatesForNewCash(){
//	}
//	
//	@Test( description = "SRL-2. Создается новая форма КМ3, если чек возврата с датой следующего дня (после 00:00:00 часов)")
//	public void testNewKM3AfterMidnight(){
//	}
	
}
