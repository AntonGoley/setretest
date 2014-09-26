package ru.crystals.set10.test.documents.accompanying;

import junit.framework.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static ru.crystals.set10.pages.operday.searchcheck.CheckContentPage.*;


public class AccompanyingDocumentSmokeTest extends AccompanyingDocumentsAbstractTest{
	
	@BeforeClass
	public void prepareData() {
		super.navigateToCheckSearchPage();
	}	
	
	@Test (description = "SRTE-35. Печать номенклатуры кассового чека. Документ выводится на печать и содержит верный заголовок")
	public void testNomenclatureCheckReport(){
		Assert.assertTrue("Не выводится название отчета \"Номенклатура кассового чека\"", 
				checkContent.generateReport(LINK_NOMENCLATURE).contains("Номенклатура кассового чека"));
	}
	
	@Test (description = "SRTE-36. Печать товарного чека. Документ выводится на печать и содержит верный заголовок")
	public void testGoodsCheckReport(){
		Assert.assertTrue("Не выводится название отчета \"Товарный чек\"", 
				checkContent.generateReport(LINK_GOODS_CHECK ).contains("Товарный чек"));
	}
	
	@Test (description = "SRTE-37. Печать товарной накладной. Документ выводится на печать и содержит верный заголовок")
	public void testInvoiceReport(){
		Assert.assertTrue("Не выводится название отчета \"Тованая накладная\"", 
				checkContent.generateReportWithCounterpart(LINK_INVOICE, counterpartName, counterpartInn, counterpartKpp, counterpartAdress)
				.contains("ТОВАРНАЯ НАКЛАДНАЯ"));
	}
	
	@Test (description = "SRTE-38. Печать счет-фактуры. Документ выводится на печать и содержит верный заголовок")
	public void testGoodsBillReport(){
		Assert.assertTrue("Не выводится название отчета \"Счет-фактура\"", 
				checkContent.generateReportWithCounterpart(LINK_GOODS_BILL, counterpartName, counterpartInn, counterpartKpp, counterpartAdress)
				.contains("СЧЕТ-ФАКТУРА"));
	}
	
}
