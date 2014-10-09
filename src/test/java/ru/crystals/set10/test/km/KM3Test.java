package ru.crystals.set10.test.km;



import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.operday.HTMLRepotResultPage;
import ru.crystals.set10.pages.operday.OperDayPage;
import ru.crystals.set10.pages.operday.cashes.Km3Page;
import ru.crystals.set10.test.AbstractTest;
import static ru.crystals.set10.utils.DbAdapter.*;
import static ru.crystals.set10.pages.operday.cashes.Km3Page.*;

public class KM3Test extends AbstractTest{
	
	Km3Page km3;
	OperDayPage operDay;
	HTMLRepotResultPage htmlReportResults;
	PurchaseEntity pe;
	
	private static String SQL_CLEAN_KM3 = "delete from od_km3";
	private static String SQL_CLEAN_KM3_ROW = "delete from od_km3_row";
	
	@DataProvider (name = "Поля КМ3")
	public static Object[][] km3Fields(){
		return new Object[][]{
				{},
				{}
		};
	}
	
	@BeforeClass
	public void prepareData(){
		dbAdapter.batchUpdateDb(DB_RETAIL_OPERDAY, new String[] {SQL_CLEAN_KM3, SQL_CLEAN_KM3_ROW} );
		log.info("Записи в таблице od_km3 и в таблице od_km3_row удалены");
		
		km3 = new LoginPage(getDriver(), Config.RETAIL_URL).
				openOperDay(Config.MANAGER, Config.MANAGER_PASSWORD)
				.openCashes().openKmPage();
	}

	@Test( description = "Если в систему пришел первый возвратный чек, создается форма КМ3")
	public void testKM3CreatesAfter1stRefund(){
	}
	
	@Test (description = "Правильность заполнения формы КМ3 данными",
			dataProvider = "Поля КМ3")
	public void testKM3Data(){
	}
	
	@Test( description = "Если форма КМ3 распечатана, следующий возвратный чек попадает в новую форму КМ3")
	public void testNewKM3CreatesIfcurrentPrinted(){
	}
	
	@Test( description = "Если в форме КМ3 12 чеков возврата, следующий возвратный чек попадает в новую форму КМ3")
	public void testNewKM3After12RefundChecks(){
	}
	
	@Test( description = "Новая форма КМ3 создается для новой смены")
	public void testKM3CreatesForNewShift(){
	}
	
	@Test( description = "Новая форма КМ3 создается для новой кассы")
	public void testKM3CreatesForNewCash(){
	}
	
	@Test( description = "Создается новая форма КМ3, если чек возврата с датой следующего дня (после 00:00:00 часов)")
	public void testNewKM3AfterMidnight(){
	}
	
}
