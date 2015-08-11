package ru.crystals.set10.test.maincash;

import java.math.BigDecimal;
import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ru.crystals.set10.pages.operday.cashes.MainCashDoc;
import ru.crystals.set10.pages.operday.cashes.MainCashManualDocPage;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.pages.operday.cashes.MainCashManualDocPage.*;

@Test (groups= "retail")
public class MainCashRKOTest extends MainCashConfigTest {
	
	MainCashManualDocPage rko;
	int docsOnPage = 0;

	String personReceived = "Вручалова Г.Г";
	
	@BeforeClass
	public void cleaRKOdocs(){
		MainCashConfigTool.clearRKODocs();
		reopenOdAndGreenShifts();
	}
	
	@DataProvider (name = "RKO")
	private Object[][] setUpRKOData(){
		return new Object[][]{
				{MainCashDoc.DOC_TYPE_RKO_PAYMENT_FROM_DEPOSITOR},
				{MainCashDoc.DOC_TYPE_RKO_SALARY_PAYMENT},
				{MainCashDoc.DOC_TYPE_RKO_CASH_LACK},
				{MainCashDoc.DOC_TYPE_RKO_EXCESS_ENCASHMENT},
		};
	}
	
	@Test(description = "SRTE-175. Добавление нового документа Расходный кассовый ордер (РКО) на вкладку \"Документы\" Главной кассы. Проверка изменения баланса при добавлении", 
			dataProvider = "RKO")
	public void testAddNewRKO(String docType){
		docsOnPage = docs.getDocCountOnPage();
		rko = docs.addDoc();
		balance = docs.getBalanceEnd();
		
		String docSum = DisinsectorTools.randomMoney(1000, ".");
		
		log.info("Добавление документа ПКО: " + docType);

		docs = rko.selectDocType(docType)
			.setTextField(FIELD_DOC_SUM, docSum)
			.setOperDayDate(FIELD_DATE_OPERDAY, DisinsectorTools.getDate("dd.MM.yy", new Date().getTime()))
			.setTextField(FIELD_PERSON_GIVE_TO, personReceived)
			.saveChanges()
			.backToMainCash();
		Assert.assertEquals(docs.getDocCountOnPage(), docsOnPage + 1, "Документ РКО " + docType.toString() + " не добавился на вкладку главной кассы Документы");
		/* проверить уменьшение баланса*/
		Assert.assertEquals(docs.getBalanceEnd(), balance.subtract(new BigDecimal(docSum)), "Документ РКО " + docType.toString() + ": не изменился баланс после добавления документа");
	}
	
	@Test(enabled = false, description = "SRTE-175. Добавление нового документа Инкассация торговой выручки на вкладку \"Документы\" Главной кассы")
	public void testAddNewRKOEncashment(){
		docsOnPage = docs.getDocCountOnPage();
		rko = docs.addDoc();
		log.info("Добавление документа ПКО: " + MainCashDoc.DOC_TYPE_RKO_ENCASHMENT);
		/* из списка ПКО, РКО выбирается текстовое представление PKOtypes */
		docs = rko.selectDocType(MainCashDoc.DOC_TYPE_RKO_ENCASHMENT)
			.setTextField(FIELD_ENCASHMENT_BANKNOTE_5000, String.valueOf(DisinsectorTools.random(2) + 1))
			.setTextField(FIELD_ENCASHMENT_BANKNOTE_1000, String.valueOf(DisinsectorTools.random(2) + 1))
			.setTextField(FIELD_ENCASHMENT_BANKNOTE_500, String.valueOf(DisinsectorTools.random(5) + 1))
			.setTextField(FIELD_ENCASHMENT_BANKNOTE_100, String.valueOf(DisinsectorTools.random(5) + 1))
			.setTextField(FIELD_ENCASHMENT_BAG_NUMBER, "100/500")
			.setOperDayDate(FIELD_DATE_OPERDAY, DisinsectorTools.getDate("dd.MM.yy", new Date().getTime()))
			.saveChanges()
			.backToMainCash();
		Assert.assertEquals(docs.getDocCountOnPage(), docsOnPage + 1, "Документ РКО " + MainCashDoc.DOC_TYPE_RKO_ENCASHMENT + " не добавился на вкладку главной кассы Документы");
		/* проверить увеличение баланса*/
		//Assert.assertEquals(docs.getDocCountOnPage(), docsOnPage + 1, "Документ РКО " + docType.toString() + " не добавился на вкладку главной кассы Документы");
	}
	
	
	@DataProvider(name = "RKOTextFields")
	private Object[][] rkoTextFields(){
		String docSum = DisinsectorTools.randomMoney(1000, ",");
		String testComment = "Test comment " + String.valueOf(new Date().getTime());
		
		rko = docs.addDoc();
		
		rko = rko.selectDocType(MainCashDoc.DOC_TYPE_RKO_EXCESS_ENCASHMENT)
				.setTextField(FIELD_DOC_SUM, docSum)
				.setOperDayDate(FIELD_DATE_OPERDAY, DisinsectorTools.getDate("dd.MM.yy", new Date().getTime()))
				.setTextField(FIELD_PERSON_GIVE_TO, personReceived)
				.setTextField(FIELD_COMMENTS, testComment)
				.saveChanges();
		
		return new Object[][]{
				{FIELD_DOC_SUM, docSum},
				{FIELD_COMMENTS, testComment},
				{FIELD_PERSON_GIVE_TO, personReceived},
		};
	}
	
	@Test(description = "SRTE-175. Заполненные поля РКО не сбрасываются при нажатии кнопки \"Сохранить изменения\"", 
			dataProvider = "RKOTextFields")
	public void testSavedFieldsRKO(String field, String value){
		log.info("Поле " + field);
		Assert.assertEquals(rko.getTextField(field), value,  "Некорректо сохранилось поле " +  field);
	}
	
	@Test(enabled = false, description = "SRTE-175. Редактирование РКО, изменение баланса")
	public void testEditRKO(){
	}
	
	@Test(enabled = false, description = "SRTE-175. Удаление РКО, изменение баланса")
	public void testDeleteRKO(){
	}
	
	
	@Test(  description = "SRTE-175. Печать РКО, сохранение заполненых полей и баланса в печатной форме")
	public void testPrintRKO() throws NumberFormatException, Exception{
		String sum = DisinsectorTools.randomMoney(1000, ",");
		String rkoType = MainCashDoc.DOC_TYPE_RKO_SALARY_PAYMENT;
				
		rko = docs.addDoc();
		log.info("Добавление документа ПКО: " + rkoType);
		rko.selectDocType(rkoType)
				.setTextField(FIELD_DOC_SUM, sum)
				.setOperDayDate(FIELD_DATE_OPERDAY, DisinsectorTools.getDate("dd.MM.yy", new Date().getTime()))
				.setTextField(FIELD_PERSON_GIVE_TO, personReceived)
				.saveChanges();

		String number = rko.getAutogeneratedFieldValue(FIELD_DOC_NUMBER);
		docs = rko.backToMainCash();
		
		removeFileReports();
		docs.printDoc(docs.getDocByTypeAndNumber(rkoType, Integer.valueOf(number)));
		
		String pageContent = getFileContent(1);
		log.info(pageContent);
		
		Assert.assertTrue(pageContent.contains(rkoType), "Печатная форма не содержит название документа ПКО: " + rkoType);
		Assert.assertTrue(pageContent.contains(personReceived), "Печатная форма не содержит заполненное поле Принято от " );
		Assert.assertTrue(pageContent.contains(sum), "Печатная форма не содержит сумму в таблице документа " + sum);
		
	}
	
	
	
	
	
	
}
