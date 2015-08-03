package ru.crystals.set10.test.maincash;

import java.math.BigDecimal;
import java.util.Date;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.crystals.set10.pages.operday.cashes.MainCashManualDocPage;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.pages.operday.cashes.MainCashManualDocPage.*;
import static ru.crystals.set10.pages.operday.cashes.MainCashDocsPage.*;


@Test (groups= "retail")
public class PKOTest extends MainCashConfigTest {
	
	MainCashManualDocPage pko;
	int docsOnPage = 0;
	
	String receivedBy = "Смирнов И.И";
	String receivedFrom = "Петров Г.Г";
	
	@BeforeClass
	public void clearPKO(){
		MainCashConfigTool.createODWithCashDocs(new Long[]{0L});
		MainCashConfigTool.clearPKODocs();
		openMainDocsPage();
	}
	
	@DataProvider (name = "PKO")
	public Object[][] setUpRKOData(){
		return new Object[][]{
				{DOC_TYPE_PKO_CASH_EXCESS},
				{DOC_TYPE_PKO_UNENCLOSURE_ENCASHMENT},
				{DOC_TYPE_PKO_UNENCLOSURE_FROM_COUNTERPARTS},
				{DOC_TYPE_PKO_INCOME_FROM_OTHER_COUNTERPARTS},
				{DOC_TYPE_PKO_INCOME_FROM_EMPLOYEES},
				{DOC_TYPE_PKO_EXCHANGE_INCOME}
		};
	}
	
	@Test(  description = "SRTE-175. Добавление нового документа Приходной кассовый ордер (ПКО) на вкладку \"Документы\" Главной кассы. Проверка изменения баланса при добавлении", 
			dataProvider = "PKO")
	public void testAddNewPKO(String docType){
		docsOnPage = docs.getDocCountOnPage();
		balance = docs.getBalance(BALANCE_END);
		
		pko = docs.addDoc();
		log.info("Добавление документа ПКО: " + docType);
		/* из списка ПКО, РКО выбирается текстовое представление PKOtypes */
		String docSum = DisinsectorTools.randomMoney(1000, ".");
		
		docs = pko.selectDocType(docType)
			.setTextField(FIELD_DOC_SUM, docSum)
			.setOperDayDate(FIELD_DATE_OPERDAY, DisinsectorTools.getDate("dd.MM.yy", new Date().getTime()))
			.setTextField(FIELD_PERSON_RECEIVED, receivedBy)
			.setTextField(FIELD_RECEIVED_FROM, receivedFrom)
			.saveChanges()
			.backToMainCash();
		Assert.assertEquals(docs.getDocCountOnPage(), docsOnPage + 1, "Документ РКО " + docType.toString() + " не добавился на вкладку главной кассы Документы");
		/* проверить увеличение баланса*/
		Assert.assertEquals(docs.getBalance(BALANCE_END), balance.add(new BigDecimal(docSum)), "Документ РКО " + docType.toString() + ": не изменился баланс после добавления документа");
	}
	
	@DataProvider(name = "PKOTextFields")
	private Object[][] pkoTextFields(){
		String docSum = DisinsectorTools.randomMoney(1000, ",");
		String testComment = "Test comment " + String.valueOf(new Date().getTime());
		
		pko = docs.addDoc();
		
		pko = pko.selectDocType(DOC_TYPE_PKO_EXCHANGE_INCOME)
				.setTextField(FIELD_DOC_SUM, docSum)
				.setOperDayDate(FIELD_DATE_OPERDAY, DisinsectorTools.getDate("dd.MM.yy", new Date().getTime()))
				.setTextField(FIELD_PERSON_RECEIVED, receivedBy)
				.setTextField(FIELD_RECEIVED_FROM, receivedFrom)
				.setTextField(FIELD_COMMENTS, testComment)
				.saveChanges();
		
		return new Object[][]{
				{FIELD_DOC_SUM, docSum},
				{FIELD_COMMENTS, testComment},
				{FIELD_PERSON_RECEIVED, receivedBy},
				{FIELD_RECEIVED_FROM, receivedFrom}
		};
	}
	
	@Test(description = "SRTE-175. Заполненные поля ПКО не сбрасываются при нажатии кнопки \"Сохранить изменения\"", 
			dataProvider = "PKOTextFields")
	public void testSavedFieldsPKO(String field, String value){
		log.info("Поле " + field);
		Assert.assertEquals(pko.getTextField(field), value,  "Некорректо сохранилось поле " +  field);
	}
	
	
	@Test(enabled = false, description = "SRTE-175. Редактирование ПКО, изменение баланса")
	public void testEditPKO(){
	}
	
	@Test(enabled = false, description = "SRTE-175. Удаление ПКО, изменение баланса")
	public void testDeletePKO(){
	}
	
	
	@Test(  enabled = false,
			description = "SRTE-175. Печать ПКО, сохранение полей и баланса в печатной форме")
	public void testPrintPKO(String pkoType){
		String sum = DisinsectorTools.randomMoney(1000, ",");
				
		pko = docs.addDoc();
		log.info("Добавление документа ПКО: " + pkoType);
		pko.selectDocType(pkoType)
				.setTextField(FIELD_DOC_SUM, sum)
				.setOperDayDate(FIELD_DATE_OPERDAY, DisinsectorTools.getDate("dd.MM.yy", new Date().getTime()))
				.setTextField(FIELD_RECEIVED_FROM, receivedFrom)
				.setTextField(FIELD_PERSON_RECEIVED, receivedBy)
				.saveChanges();

		docs = pko.backToMainCash();
		
		
		
	}
	
	
	
	
	
	
}
