package ru.crystals.set10.test.maincash;

import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.operday.cashes.CashesPage;
import ru.crystals.set10.pages.operday.cashes.MainCashDocsPage;
import ru.crystals.set10.pages.operday.cashes.MainCashManualDocPage;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.pages.operday.OperDayPage.CASHES;
import static ru.crystals.set10.pages.operday.cashes.CashesPage.LOCATOR_MAINCASH_TAB;
import static ru.crystals.set10.pages.operday.cashes.CashDocsAbstractPage.*;
import static ru.crystals.set10.pages.operday.cashes.MainCashManualDocPage.*;

//@Test (groups= "retail")
public class RKOTest extends AbstractTest {
	
	MainCashDocsPage docs;
	MainCashManualDocPage rko;
	int docsOnPage = 0;
	long dayOfset = -86400000L * 2;
	
	String headAccountant = "Главбухова О.А";
	String personReceived = "Вручалова Г.Г";
	
	
	//@Parameters({"dayOfset"})
	@BeforeClass
	public void setUp(/*String ofset*/){
		//dayOfset = Long.valueOf(ofset) * 86400000L;
		log.info("Смещение равно " + dayOfset);
		docs = new LoginPage(getDriver(), Config.RETAIL_URL)
		.openOperDay(Config.MANAGER, Config.MANAGER_PASSWORD)
		.navigatePage(CashesPage.class, CASHES)
		.openTab(MainCashDocsPage.class, LOCATOR_MAINCASH_TAB);
		
		docs.switchToTable(LOCATOR_DOCS);
	}
	
	@DataProvider (name = "RKO")
	private Object[][] setUpRKOData(){
		return new Object[][]{
				//{DOC_TYPE_RKO_ENCASHMENT},
				{DOC_TYPE_RKO_PAYMENT_FROM_DEPOSITOR},
				{DOC_TYPE_RKO_SALARY_PAYMENT},
//				{DOC_TYPE_RKO_CASH_LACK},
//				{DOC_TYPE_RKO_EXCESS_ENCASHMENT},
//				{DOC_TYPE_RKO_EXCHANGE_WITHDRAWAL}
		};
	}
	
	@Test(description = "SRTE-175. Добавление нового документа Расходный кассовый ордер (РКО) на вкладку \"Документы\" Главной кассы", 
			dataProvider = "RKO")
	public void testAddNewRKO(String docType){
		docsOnPage = docs.getDocCountOnPage();
		rko = docs.addDoc();
		log.info("Добавление документа ПКО: " + docType);
		/* из списка ПКО, РКО выбирается текстовое представление PKOtypes */
		docs = rko.selectDocType(docType)
			.setTextField(FIELD_DOC_SUM, DisinsectorTools.randomMoney(1000, ","))
			.setOperDayDate(FIELD_DATE_OPERDAY, DisinsectorTools.getDate("dd.MM.yy", new Date().getTime() + dayOfset))
			.setTextField(FIELD_HEAD_ACCOUNTANT, headAccountant)
			.setTextField(FIELD_PERSON_GIVE_TO, personReceived)
			.saveChanges()
			.backToMainCash();
		Assert.assertEquals(docs.getDocCountOnPage(), docsOnPage + 1, "Документ РКО " + docType.toString() + " не добавился на вкладку главной кассы Документы");
		/* проверить увеличение баланса*/
		//Assert.assertEquals(docs.getDocCountOnPage(), docsOnPage + 1, "Документ РКО " + docType.toString() + " не добавился на вкладку главной кассы Документы");
	}
	
	@Test( enabled = false, description = "SRTE-175. Добавление нового документа Инкассация торговой выручки на вкладку \"Документы\" Главной кассы", 
			dataProvider = "PKO")
	public void testAddNewRKOEncashment(String docType){
		docsOnPage = docs.getDocCountOnPage();
		rko = docs.addDoc();
		log.info("Добавление документа ПКО: " + docType);
		/* из списка ПКО, РКО выбирается текстовое представление PKOtypes */
		docs = rko.selectDocType(docType)
			//.setTextField(FIELD_ENCASHMENT_BANKNOTE_5000, String.valueOf(DisinsectorTools.random(5) + 1))
			.setTextField(FIELD_ENCASHMENT_BANKNOTE_1000, String.valueOf(DisinsectorTools.random(5) + 1))
			.setTextField(FIELD_ENCASHMENT_BANKNOTE_500, String.valueOf(DisinsectorTools.random(5) + 1))
			.setTextField(FIELD_ENCASHMENT_BANKNOTE_100, String.valueOf(DisinsectorTools.random(5) + 1))
			.setOperDayDate(FIELD_DATE_OPERDAY, DisinsectorTools.getDate("dd.MM.yy", new Date().getTime() + dayOfset))
			.saveChanges()
			.backToMainCash();
		Assert.assertEquals(docs.getDocCountOnPage(), docsOnPage + 1, "Документ РКО " + docType.toString() + " не добавился на вкладку главной кассы Документы");
		/* проверить увеличение баланса*/
		//Assert.assertEquals(docs.getDocCountOnPage(), docsOnPage + 1, "Документ РКО " + docType.toString() + " не добавился на вкладку главной кассы Документы");
	}
	
	
	
	
	
	//@Test(description = "SRTE-175. Автомтическое заполнение полей РКО")
	public void testRKOAutoGeneratedFields(){
	}
	
	//@Test(description = "SRTE-175. Редактирование РКО, изменение баланса")
	public void testEditRKO(){
	}
	
	//@Test(description = "SRTE-175. Удаление РКО, изменение баланса")
	public void testDeleteRKO(){
	}
	
	
	@Test(  enabled = false,
			description = "SRTE-175. Печать ПКО, сохранение полей и баланса в печатной форме")
	public void testPrintRKO(String pkoType){
		String headAccountant = "Главбухова О.А";
		String receivedBy = "Получилов И.И";
		String receivedFrom = "Вручалова Г.Г";
		String docNumber = "0";
		String sum = DisinsectorTools.randomMoney(1000, ",");
				
		rko = docs.addDoc();
		log.info("Добавление документа ПКО: " + pkoType);
		rko.selectDocType(pkoType)
				.setTextField(FIELD_DOC_SUM, sum)
				.setOperDayDate(FIELD_DATE_OPERDAY, DisinsectorTools.getDate("dd.MM.yy", new Date().getTime()))
				.setTextField(FIELD_HEAD_ACCOUNTANT, headAccountant)
				.setTextField(FIELD_RECEIVED_FROM, receivedFrom)
				.setTextField(FIELD_PERSON_RECEIVED, receivedBy)
				.saveChanges();

		docs = rko.backToMainCash();
		
		
		
	}
	
	
	
	
	
	
}
