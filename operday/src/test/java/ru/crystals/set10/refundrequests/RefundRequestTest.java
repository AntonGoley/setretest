package ru.crystals.set10.refundrequests;



import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.operday.HTMLRepotResultPage;
import ru.crystals.set10.pages.operday.OperDayPage;
import ru.crystals.set10.pages.operday.searchcheck.CheckContentPage;
import ru.crystals.set10.test.AbstractTest;

public class RefundRequestTest extends AbstractTest{
	
	OperDayPage operDay;
	HTMLRepotResultPage htmlReportResults;
	CheckContentPage checkContent;
	PurchaseEntity check;
	
	//private HashMap<Long, Long>  returnPositions = new HashMap<Long, Long>(); 
	
	
//	@DataProvider (name = "Поля КМ3")
//	public static Object[][] km3Fields(){
//		return new Object[][]{
//				{"Название формы", "О ВОЗВРАТЕ ДЕНЕЖНЫХ СУММ ПОКУПАТЕЛЯМ (КЛИЕНТАМ)\nПО НЕИСПОЛЬЗОВАННЫМ КАССОВЫМ ЧЕКАМ"}
//		};
//	}
	
	@BeforeClass
	public void prepareData(){
		
		check = (PurchaseEntity) cashEmulator.nextPurchase();
		checkContent = new LoginPage(getDriver(), Config.RETAIL_URL).
				openOperDay(Config.MANAGER, Config.MANAGER_PASSWORD)
				.openCheckSearch()
				.setCheckNumber(check)
				.doSearch()
				.selectFirstCheck();
	}
	
	@Test( description = "SRL-78. ")
	public void testRefundRequestOpen(){
		
	}
	
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
