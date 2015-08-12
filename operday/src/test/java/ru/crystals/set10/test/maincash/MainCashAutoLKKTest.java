package ru.crystals.set10.test.maincash;


import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ru.crystals.set10.pages.operday.cashes.MainCashDoc;


/*
 * Создание автоматического документа Лист кассовой книги
 */
@Test (groups= "retail")
public class MainCashAutoLKKTest extends MainCashConfigTest {
	
	private Integer lkkExpectedNumber = -1;
	
	private MainCashDoc lkk;
	
	@BeforeClass
	public void setup(){
		lkkExpectedNumber = MainCashConfigTool.getNexDocNumberForType(MainCashDoc.DOC_TYPE_LKK);
		MainCashConfigTool.makeShiftsGreenForDate(today);
		openMainDocsPage();
		docs.reopenOperDayAndSwitchBack(today);
	}
	
	@Test( 	priority = 1,
			description = "SRTE-177. ЛКК формируется при закрытии ОД")
	public void testLKKCreatedOnODClose() throws Exception{
		
		Assert.assertTrue(docs.getDocByType(MainCashDoc.DOC_TYPE_LKK).isEmpty(), "Документ " + MainCashDoc.DOC_TYPE_LKK + " не должен существовать на текущую дату перед началом теста!");
		/* отправить чек и Z-отчет*/
		cashEmulator.nextPurchase();
		cashEmulator.nextZReport(1000L, 2000L);
		
		docs.closeOperdayAndSwitchBack(today);
		docs.getDocsOnPage();
		Assert.assertTrue(docs.getDocByType(MainCashDoc.DOC_TYPE_LKK).size()==1, "Документ " + MainCashDoc.DOC_TYPE_LKK + " не создался после закрытия опердня");
	}
	
	@Test( priority = 2,
			description = "SRTE-177. ЛКК формируется на основании всех документов ПКО и РКО.")
	public void testLKKContainsAllPKOandRKOsums() throws Exception{
		
		List<MainCashDoc> docsPko = docs.getDocByType("ПКО");
		List<MainCashDoc> docsRko = docs.getDocByType("ПКО");
		lkk = docs.getDocByType(MainCashDoc.DOC_TYPE_LKK).get(0);
		
		docs.printDoc(lkk);
		String pageContent = getFileContent(1);
		
		for (MainCashDoc pko:docsPko){
			Assert.assertTrue(pageContent.contains(pko.getDocSum().toString()), "Документ " + MainCashDoc.DOC_TYPE_LKK + "не содержит данных по документу "  + pko.getType() + " номер " + pko.getNumber());
		}
		
		for (MainCashDoc rko:docsRko){
			Assert.assertTrue(pageContent.contains(rko.getDocSum().toString()), "Документ " + MainCashDoc.DOC_TYPE_LKK + "не содержит данных по документу "  + rko.getType() + " номер " + rko.getNumber());
		}
	}
	
	@Test(  priority = 2,
			description = "SRTE-177. Номер ЛКК формируется в рамках типа документов ЛКК")
	public void testLKKNumber() throws Exception{
		lkk = docs.getDocByType(MainCashDoc.DOC_TYPE_LKK).get(0);
		Assert.assertEquals(lkk.getNumber(),
				lkkExpectedNumber,
				"Неверно сгенерился номер документа");
	}
	
	@Test( enabled=false,
			priority = 2,
			description = "SRTE-177. ЛКК недоступен для редактирования")
	public void testLKKUnableToEdit(){
	}
	
	@Test( priority = 3,
			description = "SRTE-177. ЛКК недоступен для печати, если операционный день открыт")
	public void testLKKUnableToPrintIfOdOpened() throws Exception{
		docs.reopenOperDayAndSwitchBack(today);
		lkk = docs.getDocByType(MainCashDoc.DOC_TYPE_LKK).get(0);
		Assert.assertFalse(lkk.getPrinable(),  "Документ не должен быть доступен для печати, если ОД не закрыт!");
	}

}
