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
public class MainCashLKKAndDDSTest extends MainCashConfigTest {
	
	private Integer lkkExpectedNumber = -1;
	private Integer ddsExpectedNumber = -1;
	
	private MainCashDoc autoDoc;
	private static boolean reopenOd = false;
	
	@BeforeClass
	public void setup(){
		MainCashConfigTool.clearLastAutoDocs(today);
		
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
	
	private void assertDDCLine(String searchType, String reportResult) throws Exception{
		log.info("Проверить, что отчет " + MainCashDoc.DOC_TYPE_DDS + " содержит все документы " + searchType);
		
		for (MainCashDoc doc:docs.getDocByType(searchType)){
			
			/*пример строки:  Недовложение инкассация, ПКО №2 525,85 */
			String docLineInReport = doc.getType().substring(4) + ", " + searchType + " №" + doc.getNumber() + " " + doc.getDocSum().toPlainString().replace(".", ",");
			log.info(docLineInReport);
			Assert.assertTrue(reportResult.contains(docLineInReport), 
					"Печатная форма " + MainCashDoc.DOC_TYPE_DDS + " не содержит данных по документу " + doc.getType() + "; номер " + doc.getNumber());
		}
	}
	
	private void assertLKKLine(String searchType, String reportResult) throws Exception{
		log.info("Прверить, что отчет " + MainCashDoc.DOC_TYPE_LKK + " содержит все документы " + searchType);
		for (MainCashDoc doc:docs.getDocByType(searchType)){
			
			/*пример строки:  //1 Вручалова Г.Г 691,23 1 Вручалова Г.Г 691,23 */
			
			/* TODO: сделать нормальную валидацию строки!
			 *  
			 */
			reportResult = reportResult.replaceAll("([aA-zZ. ]+)", " ").replaceAll("([аА-яЯ. ]+)", " ");
			log.info(reportResult);
			
			String sum = doc.getDocSum().toPlainString().replace(".", ",");
			String docLineInReport = doc.getNumber() + " " + sum + " " + sum + doc.getNumber();
			
			log.info(docLineInReport);
			Assert.assertTrue(reportResult.contains(docLineInReport), 
					"Печатная форма " + MainCashDoc.DOC_TYPE_LKK + " не содержит данных по документу " + doc.getType() + "; номер " + doc.getNumber());
		}
	}
	
	@Test(  priority = 2,  
			description = "Печать автоматического документа ДДС")
	public void testPrintAutoDocDDC() throws Exception{
		String doctype = MainCashDoc.DOC_TYPE_DDS;
		
		log.info("Печать " + doctype);
		autoDoc = docs.getDocByType(doctype).get(0);

		removeFileReports();
		docs.printDoc(autoDoc);
		String reportResult = getFileContent(new Integer[]{1, 2});
		
		assertDDCLine("ПКО", reportResult);
		assertDDCLine("РКО", reportResult);
		
		Assert.assertTrue(reportResult.contains("Сальдо на начало дня " + docs.getBalanceStart().toPlainString().replace(".", ",")), 
				"Печатная форма " + doctype + "содержит неверные данные о сальдо на начало дня ");
		
		Assert.assertTrue(reportResult.contains("Сальдо на конец дня " + docs.getBalanceEnd().toPlainString().replace(".", ",")), 
				"Печатная форма " + doctype + "содержит неверные данные о сальдо на конец дня ");
		
	}
	
	@Test(  priority = 2,  
			description = "Печать автоматического документа ЛКК")
	public void testPrintAutoDocLKK() throws Exception{
		String doctype = MainCashDoc.DOC_TYPE_LKK;
		
		log.info("Печать " + doctype);
		autoDoc = docs.getDocByType(doctype).get(0);

		removeFileReports();
		docs.printDoc(autoDoc);
		String reportResult = getFileContent(new Integer[]{1, 2});
		
		assertLKKLine("ПКО", reportResult);
		assertLKKLine("РКО", reportResult);
		
		Assert.assertTrue(reportResult.contains(String.format("%s Х", docs.getBalanceStart().toPlainString().replace(".", ",")) ), 
				"Печатная форма " + doctype + "содержит неверные данные о балансе на начало дня ");
		
		Assert.assertTrue(reportResult.contains(docs.getBalanceEnd().toPlainString().replace(".", ",")   ), 
				"Печатная форма " + doctype + "содержит неверные данные о балансе на конец дня ");
		
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
