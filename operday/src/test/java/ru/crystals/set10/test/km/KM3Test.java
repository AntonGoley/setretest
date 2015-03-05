package ru.crystals.set10.test.km;


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
import static ru.crystals.set10.pages.operday.cashes.KmPage.*;
import static ru.crystals.set10.pages.operday.OperDayPage.SEARCH_CASHES;

@Test(groups = {"retail", "centrum"})
public class KM3Test extends AbstractTest{
	
	KmPage km3;
	OperDayPage operDay;
	HTMLRepotResultPage htmlReportResults;
	PurchaseEntity check;
	
	private static String SQL_CLEAN_KM3 = "delete from od_km3";
	private static String SQL_CLEAN_KM3_ROW = "delete from od_km3_row";
	private HashMap<Long, Long>  returnPositions = new HashMap<Long, Long>(); 
	
	
	@DataProvider (name = "Поля КМ3")
	private static Object[][] km3Fields(){
		return new Object[][]{
				{"Название формы", "О ВОЗВРАТЕ ДЕНЕЖНЫХ СУММ ПОКУПАТЕЛЯМ (КЛИЕНТАМ)\nПО НЕИСПОЛЬЗОВАННЫМ КАССОВЫМ ЧЕКАМ"}
		};
	}
	
	@BeforeClass
	public void prepareData(){
		dbAdapter.batchUpdateDb(DB_OPERDAY, new String[] {SQL_CLEAN_KM3, SQL_CLEAN_KM3_ROW} );
		log.info("Записи в таблице od_km3 и в таблице od_km3_row удалены в базе " + DB_OPERDAY);
		
		km3 = new LoginPage(getDriver(), TARGET_HOST_URL).
				openOperDay(Config.MANAGER, Config.MANAGER_PASSWORD)
				.navigatePage(CashesPage.class, SEARCH_CASHES)
				.openKmPage()
				.switchToKm(LOCATOR_KM3);
		
		check = (PurchaseEntity) cashEmulator.nextPurchase();
	}
	
	@Test( description = "SRTE-28. Если в систему пришел первый возвратный чек, создается форма КМ3")
	public void testKM3CreatesAfter1stRefund(){
		int km3Tablerows = km3.getKmCountOnPage(LOCATOR_KM3_TABLE);
		returnPositions.put(1L, 1L * 1000);
		cashEmulator.nextRefundPositions(check, returnPositions, true);
		km3.switchToKm(LOCATOR_KM6).switchToKm(LOCATOR_KM3);
		km3 = new KmPage(getDriver());
		Assert.assertEquals("Не появилась форма КМ3", km3Tablerows + 1, km3.getKmCountOnPage(LOCATOR_KM3_TABLE));
	}
	
	
	//TODO: переместить тетст в отдельный класс
	@Test (	dependsOnMethods ="testKM3CreatesAfter1stRefund",
			description = "SRTE-28. Правильность заполнения формы КМ3 данными",
			dataProvider = "Поля КМ3")
	public void testKM3Data(String fiels, String expectedValue){
		log.info("Значение поля: " + fiels);
		Assert.assertTrue("Неверное значение поля в форме КМ3", km3.printAllKmForms().contains(expectedValue));
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
