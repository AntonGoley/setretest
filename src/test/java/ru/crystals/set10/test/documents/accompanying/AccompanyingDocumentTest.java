package ru.crystals.set10.test.documents.accompanying;

import junit.framework.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static ru.crystals.set10.pages.operday.searchcheck.CheckContentPage.*;


public class AccompanyingDocumentTest extends AccompanyngDocumentsAbstractTest{
	
	String reportResult;
	String counterpartName = "Counterpart name";
	String counterpartInn = "100200300400";
	String counterpartKpp = "555666777";
	String counterpartAdress = "199123, Spb, Street 1-20";
	
	@BeforeClass
	public void prepareData() {
		super.navigateToheckSearchPage();
		reportResult = checkContent.generateReport(LINK_NOMENCLATURE);
	}	
	
	@Test (description = "SRTE-36. Печать товарного чека")
	public void testPrintGoodsCkeck(){
		Assert.assertTrue("Не выводится название отчета \"Товарный чек\"", 
				checkContent.generateReport(LINK_GOODS_CHECK ).contains("Товарный чек"));
	}
	
	@Test (description = "SRTE-35. Печать номенклатуры кассового чека")
	public void testPrintCheckNomenclature(){
		Assert.assertTrue("Не выводится название отчета \"Номенклатура кассового чека\"", 
				checkContent.generateReport(LINK_NOMENCLATURE).contains("Номенклатура кассового чека"));
	}
	
	@Test (description = "SRTE-38. Печать счет-фактуры")
	public void testPrintGoodsBill(){
		Assert.assertTrue("Не выводится название отчета \"Счет-фактура\"", 
				checkContent.generateReportWithCounterpart(LINK_GOODS_BILL, counterpartName, counterpartInn, counterpartKpp, counterpartAdress)
				.contains("СЧЕТ-ФАКТУРА"));
	}
	
	@Test (description = "SRTE-37. Печать товарной накладной")
	public void testPrintInvoice(){
		Assert.assertTrue("Не выводится название отчета \"Тованая накладная\"", 
				checkContent.generateReportWithCounterpart(LINK_INVOICE, counterpartName, counterpartInn, counterpartKpp, counterpartAdress)
				.contains("ТОВАРНАЯ НАКЛАДНАЯ"));
	}
	
	
}
