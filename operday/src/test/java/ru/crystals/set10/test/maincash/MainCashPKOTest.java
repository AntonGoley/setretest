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
public class MainCashPKOTest extends MainCashConfigTest {
	
	MainCashManualDocPage pko;
	int docsOnPage = 0;
	
	String receivedBy = "Polychilov I.I";
	String receivedFrom = "Vrychalova A.A";
	
	@BeforeClass
	public void clearPKO(){
		MainCashConfigTool.clearPKODocs();
		reopenOdAndGreenShifts();
	}
	
	@DataProvider (name = "PKO")
	public Object[][] setUpRKOData(){
		return new Object[][]{
				{MainCashDoc.DOC_TYPE_PKO_CASH_EXCESS},
				{MainCashDoc.DOC_TYPE_PKO_UNENCLOSURE_ENCASHMENT},
				{MainCashDoc.DOC_TYPE_PKO_UNENCLOSURE_FROM_COUNTERPARTS},
				{MainCashDoc.DOC_TYPE_PKO_INCOME_FROM_OTHER_COUNTERPARTS},
				{MainCashDoc.DOC_TYPE_PKO_INCOME_FROM_EMPLOYEES},
		};
	}
	
	@Test(  description = "SRTE-175. Добавление нового документа Приходной кассовый ордер (ПКО) на вкладку \"Документы\" Главной кассы. Проверка изменения баланса при добавлении", 
			dataProvider = "PKO")
	public void testAddNewPKO(String docType){
		docsOnPage = docs.getDocCountOnPage();
		balance = docs.getBalanceEnd();
		
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
		Assert.assertEquals(docs.getBalanceEnd(), balance.add(new BigDecimal(docSum)), "Документ РКО " + docType.toString() + ": не изменился баланс после добавления документа");
	}
	
	@DataProvider(name = "PKOTextFields")
	private Object[][] pkoTextFields(){
		String docSum = DisinsectorTools.randomMoney(1000, ",");
		String testComment = "Test comment " + String.valueOf(new Date().getTime());
		
		pko = docs.addDoc();
		
		pko = pko.selectDocType(MainCashDoc.DOC_TYPE_PKO_CASH_EXCESS)
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
	
	@Test(  description = "SRTE-175. Печать ПКО, сохранение заполняемых полей и баланса в печатной форме")
	public void testPrintPKO() throws Exception{
		String sum = DisinsectorTools.randomMoney(1000, ",");
		String pkoType = MainCashDoc.DOC_TYPE_PKO_CASH_EXCESS;
		
		pko = docs.addDoc();
		log.info("Добавление документа ПКО: " + pkoType);
		pko.selectDocType(pkoType)
				.setTextField(FIELD_DOC_SUM, sum)
				.setOperDayDate(FIELD_DATE_OPERDAY, DisinsectorTools.getDate("dd.MM.yy", new Date().getTime()))
				.setTextField(FIELD_RECEIVED_FROM, receivedFrom)
				.setTextField(FIELD_PERSON_RECEIVED, receivedBy)
				.saveChanges();
		
		String number = pko.getAutogeneratedFieldValue(FIELD_DOC_NUMBER);
		docs = pko.backToMainCash();
		docs.getDocsOnPage();
		
		removeFileReports();
		docs.printDoc(docs.getDocByTypeAndNumber(pkoType, Integer.valueOf(number)));
		
		String pageContent = getFileContent(1);
		log.info(pageContent);
		
		Assert.assertTrue(pageContent.contains("Основание:\n" + pkoType), "Печатная форма не содержит название документа ПКО: " + pkoType);
		Assert.assertTrue(pageContent.contains("Принято от\n" + receivedFrom), "Печатная форма не содержит заполненное поле Принято от " );
		Assert.assertTrue(pageContent.contains("Получил кассир\n(расшифровка подписи)\n" + receivedBy), "Печатная форма не содержит заполненное поле Получил кассир");
		Assert.assertTrue(pageContent.contains(sum), "Печатная форма не содержит сумму в таблице документа " + sum);
	}
	
	
	
	
	
	
}
