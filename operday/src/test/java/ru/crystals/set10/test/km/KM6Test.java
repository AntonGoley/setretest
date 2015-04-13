package ru.crystals.set10.test.km;



import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

import junit.framework.Assert;

import org.testng.ITestNGMethod;
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
import static ru.crystals.set10.utils.DbAdapter.*;
import static ru.crystals.set10.pages.operday.cashes.KmPage.*;
import static ru.crystals.set10.pages.operday.OperDayPage.CASHES;

public class KM6Test extends AbstractTest{
	
	KmPage km6;
	OperDayPage operDay;
	HTMLRepotResultPage htmlReportResults;
	private static PurchaseEntity purchase;
	private static PurchaseEntity purchaseReturn;
	private static String cashier;
	private String reportText;
	private boolean reportOpened = false;
	
	//количество отчетов на закладке КМ6
	int km6Tablerows;
	
	private static String SQL_CLEAN_KM6 = "delete from od_km6";
	private HashMap<Long, Long>  returnPositions = new HashMap<Long, Long>(); 
	
	@BeforeClass
	public void prepareData(){
		dbAdapter.batchUpdateDb(DB_RETAIL_OPERDAY, new String[] {SQL_CLEAN_KM6} );
		
		log.info("Записи в таблице od_km6 удалены в базе " + DB_RETAIL_OPERDAY);
		
		km6 = new LoginPage(getDriver(), Config.RETAIL_URL).
				openOperDay(Config.MANAGER, Config.MANAGER_PASSWORD)
				.navigatePage(CashesPage.class, CASHES)
				.openTab(KmPage.class, CashesPage.LOCATOR_ACTS_TAB)
				.switchToKm(LOCATOR_KM6);
		
		km6Tablerows = km6.getKmCountOnPage(LOCATOR_KM3_TABLE);
		
		cashier = cashEmulator.changeCashUser(10);
		
		cashEmulator.useNextShift();
		//генерим чек
		purchase = (PurchaseEntity) cashEmulator.nextPurchase();
		//Возвращаем 1-ю позицию
		returnPositions.put(1L, 1L * 1000);
		purchaseReturn = (PurchaseEntity) cashEmulator.nextRefundPositions(purchase, returnPositions, true);
		// закрываем смену
		cashEmulator.nextZReport();
	}
	
	@DataProvider (name = "Поля КМ6")
	public static Object[][] km6Fields(){
		BigDecimal sumPurchases = purchase.getCheckSumEndBigDecimal();
		BigDecimal sumRetunPositions  = purchaseReturn.getCheckSumEndBigDecimal();
		String shiftNum = String.valueOf(purchaseReturn.getShift().getNumShift());
		
		CashVO cashVO = new CashVO();
		cashVO = cashEmulator.setCashVO(cashEmulator.getCashNumber(), TARGET_HOST, new Date().getTime());
		cashEmulator.sendCashVO(cashVO);
		
		return new Object[][]{
				{"Название формы", "СПРАВКА-ОТЧЁТ"},
				{"Содержит ККМ: номер производителя (factory num)", cashVO.getFactoryNum()},
				{"Содержит ККМ: рег. номер (fisc num)", cashVO.getFiscalNum()},
//				{"Содержит фамилию кассира", cashier},
				{"Строка Итого", ("Итого " + String.valueOf(sumPurchases) + " " + String.valueOf(sumRetunPositions)  + " " + String.valueOf(sumPurchases.subtract(sumRetunPositions))).replace(".", ",")},
				
		};
	}
	
	@Test( description = "SRTE-29. Акт КМ6 создается, если приходит закрытие смены (Z-отчет)")
	public void testKM6CreatesAfterCloseShiftOnCash(){
		km6.switchToKm(LOCATOR_KM3).switchToKm(LOCATOR_KM6);
		km6 = new KmPage(getDriver());
		Assert.assertEquals("Не появилась форма КМ6", km6Tablerows + 1, km6.getKmCountOnPage(LOCATOR_KM6_TABLE));
	}
	
	@Test (	dependsOnMethods ="testKM6CreatesAfterCloseShiftOnCash",
			description = "SRTE-29. Правильность заполнения формы КМ6 данными",
			dataProvider = "Поля КМ6"
			)
	public void testKM6Data(String field, String expectedValue){
		
		if (!reportOpened) {
			reportText = km6.printAllKmForms();
			reportOpened = true;
		}
		
		log.info("Значение поля: " + field);
		Assert.assertTrue("Неверное значение поля в форме КМ6", reportText.contains(expectedValue));
	}
	
}
