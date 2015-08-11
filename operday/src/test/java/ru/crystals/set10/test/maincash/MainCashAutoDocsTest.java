package ru.crystals.set10.test.maincash;


import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ru.crystals.set10.pages.operday.cashes.MainCashDoc;
import ru.crystals.set10.pages.operday.cashes.MainCashDoc.MainCashDocStatus;


/*
 * Создание автоматических документов при закрытии опердня ЛКК, ДДС
 */
@Test (groups= "retail")
public class MainCashAutoDocsTest extends MainCashConfigTest {
	
	private Integer lkkExpectedNumber = -1;
	private Integer ddsExpectedNumber = -1;
	
	private MainCashDoc autoDoc;
	private static boolean reopenOd = false;
	
	@BeforeClass
	public void setup(){
		MainCashConfigTool.clearLastAutoDocs();
		
		lkkExpectedNumber = MainCashConfigTool.getNexDocNumberForType(MainCashDoc.DOC_TYPE_LKK);
		ddsExpectedNumber = MainCashConfigTool.getNexDocNumberForType(MainCashDoc.DOC_TYPE_DDS);
		
		reopenOdAndGreenShifts();
		
		/* отправить чек и Z-отчет*/
		cashEmulator.nextPurchase();
		cashEmulator.nextZReport(1000L, 2000L);
		
		docs.closeOperdayAndSwitchBack(today);
		docs.getDocsOnPage();
	}
	
	@DataProvider(name = "docTypes")
	private Object[][] setDocTypes(){
		return new Object[][]{
				{MainCashDoc.DOC_TYPE_LKK},
				{MainCashDoc.DOC_TYPE_DDS},
		};
	}
	
	@DataProvider(name = "docTypesWithNumbers")
	private Object[][] setDocTypesWithNumbers(){
		return new Object[][]{
				{MainCashDoc.DOC_TYPE_LKK, lkkExpectedNumber},
				{MainCashDoc.DOC_TYPE_DDS, ddsExpectedNumber},
		};
	}
	
	@Test( 	priority = 1,
			dataProvider = "docTypes",
			description = "Формирование автоматических документов при закрытии ОД")
	public void testAutoDocreatedOnODClose(String doctype) throws Exception{
		log.info(doctype);
		Assert.assertTrue(docs.getDocByType(doctype).size()==1, "Документ " + doctype + " не создался после закрытия опердня");
	}
	
	@Test(  priority = 2,  
			description = "Номер автоматического документа формируется с учетом нумерации внутри типа",
			dataProvider = "docTypesWithNumbers")
	public void testAutoDocNumber(String doctype, Integer docNumber) throws Exception{
		log.info(doctype);
		autoDoc = docs.getDocByType(doctype).get(0);
		Assert.assertEquals(autoDoc.getNumber(),
				docNumber,
				"Неверно сгенерился номер документа");
	}
	
	@Test(  priority = 2,  
			description = "Автоматические документы в зеленом статусе, если ОД закрыт",
			dataProvider = "docTypes")
	public void testAutoDocGreenStatusIfDayClosed(String doctype) throws Exception{
		log.info(doctype);
		autoDoc = docs.getDocByType(doctype).get(0);
		Assert.assertEquals(autoDoc.getStatus(),
				MainCashDocStatus.GREEN,
				"Статус документа не изменился на зеленый, когда ОД закрыт!");
	}
	
	@Test(  priority = 2,  
			description = "Печать автоматического документа ДДС")
	public void testPrintAutoDocDDC() throws Exception{
		String doctype = MainCashDoc.DOC_TYPE_DDS;
		
		log.info("Печать " + doctype);
		autoDoc = docs.getDocByType(doctype).get(0);

		removeFileReports();
		docs.printDoc(autoDoc);
		String reportResult = getFileContent(1);
		
		for (MainCashDoc doc:docs.getDocByType("ПКО")){
			log.info(doc.getType().replace("ПКО ", "") + ", ПКО №" + doc.getNumber() + " " + doc.getIncome().toPlainString().replace(".", ","));
			
			Assert.assertTrue(reportResult.contains(doc.getType().replace("ПКО ", "") 
					+ ", ПКО №" + doc.getNumber() 
					+ " " 
					+ doc.getIncome().toPlainString().replace(".", ",")), 
					"Печатная форма " + doctype + " не содержит данных по документу ПКО " + doc.getType() + "; номер " + doc.getNumber());
		}
		
		for (MainCashDoc doc:docs.getDocByType("РКО")){
			log.info(doc.getType().replace("РКО ", "") + ", РКО №" + doc.getNumber() + " " + doc.getOutcome().toPlainString().replace(".", ","));
			
			Assert.assertTrue(reportResult.contains(doc.getType().replace("РКО ", "")
					+ ", РКО №" + doc.getNumber()
					+ " "
					+ doc.getOutcome().toPlainString().replace(".", ",")), 
					"Печатная форма " + doctype + " не содержит данных по документу ПКО " + doc.getType() + "; номер " + doc.getNumber());
		}
		
		
		Assert.assertTrue(reportResult.contains("Сальдо на начало дня " + docs.getBalanceStart().toPlainString().replace(".", ",")), 
				"Печатная форма " + doctype + "содержит неверные данные о сальдо на начало дня ");
		
		Assert.assertTrue(reportResult.contains("Сальдо на конец дня " + docs.getBalanceEnd().toPlainString().replace(".", ",")), 
				"Печатная форма " + doctype + "содержит неверные данные о сальдо на конец дня ");
		
	}
		
	
	
	@Test(  priority = 3,
			description = "Автоматический документ переходит в серый статус, если опердень был переоткрыт",
			dataProvider = "docTypes")
	public void testGreyDocStatusIfOdReopened(String doctype) throws Exception{
		if (!reopenOd){
			docs.reopenOperDayAndSwitchBack(today);
			//TODO: как определить invocationCount??
			reopenOd = true;
			docs.getDocsOnPage();
		}
		
		autoDoc = docs.getDocByType(doctype).get(0);
		Assert.assertEquals(autoDoc.getStatus(),
				MainCashDocStatus.GREY,
				"Статус документа не изменился на зеленый, когда ОД закрыт!");
	}
	
	@Test( priority = 3,
			dependsOnMethods = "testGreyDocStatusIfOdReopened", alwaysRun = true,
			description = "Автоматический документ невозможно распечатать, если ОД был переоткрыт",
			dataProvider = "docTypes")
	public void testDocUnableToPrintIfOdReopened(String doctype) throws Exception{
		autoDoc = docs.getDocByType(doctype).get(0);
		Assert.assertFalse(autoDoc.getPrinable(),  "Документ не должен быть доступен для печати, если ОД не закрыт!");
	}
	
	
	@Test( enabled=false,
			priority = 2,
			description = "Автоматический документ не доступен для редактирования")
	public void testAutoDocUnableToEdit(){
	}

}
