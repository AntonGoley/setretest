package ru.crystals.set10.test.maincash;


import static ru.crystals.set10.pages.operday.cashes.CashDocsAbstractPage.LOCATOR_DOCS;
import static ru.crystals.set10.pages.operday.cashes.CashDocsAbstractPage.LOCATOR_KM6;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ru.crystals.set10.pages.operday.cashes.MainCashDoc;
import ru.crystals.set10.pages.operday.cashes.MainCashDoc.MainCashDocStatus;


/*
 * Создание автоматического документа ПКО Выручка магазина
 */
@Test (groups= "retail")
public class MainCashAutoKM7Test extends MainCashConfigTest {
	
	private Integer km7ExpectedNumber = -1;
	private MainCashDoc autoDoc;
	private static boolean reopenOd = false;
	
	@BeforeClass
	public void setup(){
		MainCashConfigTool.clearLastKM7Doc(today);
		km7ExpectedNumber = MainCashConfigTool.getNexDocNumberForType(MainCashDoc.DOC_TYPE_KM7);
		reopenOdAndGreenShifts();
		cashEmulator.nextZReport(1000L, 5000L);
		cashEmulator.nextZReport(2000L, 6000L);
		
		docs.switchToTable(LOCATOR_KM6);
		docs.switchToTable(LOCATOR_DOCS);
		
		docs.getDocsOnPage();

	}
	
	@Test( priority = 1,
			description = "SRTE-181. Закрытая смена (Z-отчет) создаёт документ АКТ КМ-7")
	public void testKM7CreatedOnZRepost() throws Exception{
		Assert.assertTrue(docs.getDocByType(MainCashDoc.DOC_TYPE_KM7).size()==1, "Документ " + MainCashDoc.DOC_TYPE_KM7 + " не создался после прихода Z отчета");
	}
	
	@Test( priority = 1,
			description = "SRTE-181. Номер акта КМ-7  в рамках типа документов Акт КМ-7")
	public void testKM7Number() throws Exception{
		autoDoc = docs.getDocByType(MainCashDoc.DOC_TYPE_KM7).get(0);
		Assert.assertEquals(autoDoc.getNumber(), km7ExpectedNumber,	"Неверно сгенерился номер документа");
	}
	
	
	@Test(  priority = 2,  
			description = " АКТ КМ-7 в зеленом статусе, если ОД закрыт")
	public void testAutoDocGreenStatusIfDayClosed() throws Exception{
		docs.closeOperdayAndSwitchBack(today);
		docs.getDocsOnPage();
		
		autoDoc = docs.getDocByType(MainCashDoc.DOC_TYPE_KM7 ).get(0);
		Assert.assertEquals(autoDoc.getStatus(),
				MainCashDocStatus.GREEN,
				"Статус документа не изменился на зеленый, когда ОД закрыт!");
	}
	
	@Test( priority = 3, enabled = false,
			description = "SRTE-181. АКТ КМ-7 содержит информацию по всем закрытым сменам")
	public void testKM7ContainsAllClosedShifts(){
		removeFileReports();
		docs.printDoc(autoDoc);
		String reportResult = getFileContent(1);
		
		Assert.assertTrue(reportResult.contains(""), 
				"Печатная форма " + MainCashDoc.DOC_TYPE_KM7  + "содержит неверные данные о балансе на начало дня ");
	}
	
	@Test(  priority = 4,
			description = "АКТ КМ-7 переходит в серый статус, если опердень был переоткрыт")
	public void testGreyDocStatusIfOdReopened() throws Exception{
		if (!reopenOd){
			docs.reopenOperDayAndSwitchBack(today);
			//TODO: как определить invocationCount??
			reopenOd = true;
			docs.getDocsOnPage();
		}
		
		autoDoc = docs.getDocByType(MainCashDoc.DOC_TYPE_KM7).get(0);
		Assert.assertEquals(autoDoc.getStatus(),
				MainCashDocStatus.GREY,
				"Статус документа не изменился на зеленый, когда ОД закрыт!");
	}
	
	@Test( priority = 4,
			dependsOnMethods = "testGreyDocStatusIfOdReopened", alwaysRun = true,
			description = "АКТ КМ-7 невозможно распечатать, если ОД был переоткрыт")
	public void testDocUnableToPrintIfOdReopened() throws Exception{
		autoDoc = docs.getDocByType(MainCashDoc.DOC_TYPE_KM7).get(0);
		Assert.assertFalse(autoDoc.getPrinable(),  "Документ не должен быть доступен для печати, если ОД не закрыт!");
	}
	
	@Test( enabled=false,
			description = "SRTE-181. Акт КМ7 недоступен для редактирования")
	public void testKM7UnableToEdit(){
	}
	
}
