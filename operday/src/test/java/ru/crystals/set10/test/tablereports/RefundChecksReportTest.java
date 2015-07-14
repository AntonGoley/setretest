package ru.crystals.set10.test.tablereports;

import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.pos.check.PositionEntity;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.operday.HTMLRepotResultPage;
import ru.crystals.set10.pages.operday.tablereports.ReportConfigPage;
import ru.crystals.set10.test.dataproviders.TableReportsDataprovider;
import static ru.crystals.set10.pages.operday.tablereports.TableReportPage.*;

@Test(groups = "retail")
public class RefundChecksReportTest extends AbstractReportTest{
	
	static final String FIELD_RETURNCHECK_DATE = "Чек Возврата: Дата";
	static final String FIELD_RETURNCHECK_CASHNUMBER = "Чек Возврата: № кассы";
	static final String FIELD_RETURNCHECK_SHIFT = "Чек Возврата: № смены";
	static final String FIELD_RETURNCHECK_CHECKNUMBER = "Чек Возврата: № чека";
	static final String FIELD_RETURNCHECK_CASHIER = "Чек Возврата: ФИО В. кассира";
	static final String FIELD_RETURNCHECK_SUM = "Чек Возврата: Сумма (руб.)";
	
	static final String FIELD_SALECHECK_DATE = "Чек Продажи: Дата";
	static final String FIELD_SALECHECK_CASHNUMBER = "Чек Продажи: № кассы";
	static final String FIELD_SALECHECK_SHIFT = "Чек Продажи: № смены";
	static final String FIELD_SALECHECK_CHECKNUMBER = "Чек Продажи: № чека";
	
	//static final String FIELD_GOOD_GOODNAME = "Товар: Наименование товара";
	static final String FIELD_GOOD_GOODCODE  = "Товар: Код товара";

	ReportConfigPage refundChecksConfigPage;
	PurchaseEntity refundCheck;
	HashMap<String, String> purchaseEntityData;
	
	private HashMap<Long, Long>  returnPositions = new HashMap<Long, Long>(); 
	
	// TODO: добавить проверку дат и бар кода 
	/*
	 * Датапровайдер, описывающий колонки таблицы
	 */
	@DataProvider (name = "Данные последней записи отчета")
	private static Object[][] lastLineReportData(){
		return new Object[][]{
//				{FIELD_RETURNCHECK_DATE, 4},
				{FIELD_RETURNCHECK_CASHNUMBER, 5},
				{FIELD_RETURNCHECK_SHIFT, 6},
				{FIELD_RETURNCHECK_CHECKNUMBER, 7},
				// TODO: сейчас поле обрезается, подумать как сделать проверку
				// {FIELD_RETURNCHECK_CASHIER, 8},
				{FIELD_RETURNCHECK_SUM, 9},
				
//				{FIELD_SALECHECK_DATE , 10},
				{FIELD_SALECHECK_CASHNUMBER , 11},
				{FIELD_SALECHECK_SHIFT , 12},
				{FIELD_SALECHECK_CHECKNUMBER , 13},
//				{FIELD_GOOD_GOODCODE , 15},
				
		};
	}
	
	@BeforeClass
	public void navigateToRefundReport() {
		refundChecksConfigPage =  navigateToReportConfig(
				Config.RETAIL_URL, 
				Config.MANAGER,
				Config.MANAGER_PASSWORD,
				ReportConfigPage.class, 
				TAB_OTHER, 
				REPORT_NAME_REFUND_CHECKS);
		doHTMLReport(refundChecksConfigPage, false);
	}	

	@Test (	priority = 2,
			description = "SRL-80. Проверить названия отчета и название колонок в шапке таблицы отчета по возвратам", 
			dataProvider = "Шапка отчета по возвратам", dataProviderClass = TableReportsDataprovider.class)
	public void testRefundReportTableHead(String fieldName){
		log.info(fieldName);
		Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}
	
	@Test (	priority = 3,
			description = "SRL-80. Проверить, что в отчет по возвратам попал новый возвратный чек: возврат позиции") 
	public void testRefundReportContainsRefunds(){
		int reportSizeBefore = htmlReportResults.getReportSize();
		Assert.assertTrue(reportSizeBefore < sendRefundCheck(false).getReportSize(), "В отчете не отображается новый возвратный чек");
	}
	
	@Test (	description = "SRL-80. Проверить, что данные возврата позиции отображаются корректно", 
			dependsOnMethods = "testRefundReportContainsRefunds",
			dataProvider = "Данные последней записи отчета")
	public void testReportContainsData(String tableColumnName, int columnNumber){
		log.info("Поле: " + tableColumnName);
		Assert.assertEquals(htmlReportResults.getLastLineColumnValue(columnNumber), purchaseEntityData.get(tableColumnName));
	}
	
	@Test ( description = "SRL-80. Проверить, что в отчет по возвратам попал новый возвратный чек: произвольный возврат", 
			alwaysRun = true,
			dependsOnMethods = {"testRefundReportContainsRefunds", "testReportContainsData"}
			) 
	public void testRefundReportContainsArbitraryRefunds(){
		int reportSizeBefore = htmlReportResults.getReportSize();
		Assert.assertTrue(reportSizeBefore < sendRefundCheck(true).getReportSize(), 
				"В отчете не отображается новый возвратный чек");
	}
	
	@Test (	description = "SRL-80. Проверить, что данные отчета произвольного возврата отображаются корректно", 
			dependsOnMethods = "testRefundReportContainsArbitraryRefunds",
			dataProvider = "Данные последней записи отчета" )
	public void testReportContainsArbitraryReportData(String tableColumnName, int columnNumber){
		log.info("Поле: " + tableColumnName);
		Assert.assertEquals(htmlReportResults.getLastLineColumnValue(columnNumber), 
				purchaseEntityData.get(tableColumnName));
	}
	
	@Test (	description = "SRL-80. В отчет не попадают аннулированые возвратные чеки", 
			enabled = false)
	public void testCanceledRefundNotInReport(String tableColumnName, int columnNumber){

	}
	
	
//	@Test (	priority = 1,
//			description = "Проверить, что отчет \"Отчёт по возвратам\" доступен для скачивания в формате xls"
//			)
//	public void testRefundReportSaveFormats(){
//		refundChecksConfigPage.switchWindow(true);
//		long fileSize = 0;
//		String pattern = "RefundReport_*.xls";
//		fileSize =  refundChecksConfigPage.saveReportFile(AbstractReportConfigPage.EXCELREPORT, chromeDownloadPath, pattern).length();
//		log.info("Размер сохраненного файла: " + pattern + " равен " +  fileSize);
//		Assert.assertTrue(fileSize > 0, "Файл отчета сохранился некорректно");
//	}
	
	
	private HTMLRepotResultPage sendRefundCheck(boolean refundCheckType){
		PurchaseEntity pe = (PurchaseEntity) cashEmulator.nextPurchase();
		PositionEntity pos = pe.getPositions().get(0);
		returnPositions.put(1L, pos.getQnty());
		refundCheck = (PurchaseEntity) cashEmulator.nextRefundPositions(pe, returnPositions, refundCheckType);
		
		setPurchaseEntityData(refundCheck);
		getDriver().navigate().refresh();
		return new HTMLRepotResultPage(getDriver());
	} 
	
	private void setPurchaseEntityData(PurchaseEntity purchase){
		purchaseEntityData = new HashMap<String, String>();
		
		String sum = String.valueOf(purchase.getCheckSumEnd());
		
		purchaseEntityData.put(FIELD_RETURNCHECK_DATE, "29.08.14");
		purchaseEntityData.put(FIELD_RETURNCHECK_CASHNUMBER, String.valueOf(purchase.getShift().getCashNum()));
		purchaseEntityData.put(FIELD_RETURNCHECK_SHIFT, String.valueOf(purchase.getShift().getNumShift()));
		purchaseEntityData.put(FIELD_RETURNCHECK_CHECKNUMBER, String.valueOf(purchase.getNumber()));
		purchaseEntityData.put(FIELD_RETURNCHECK_CASHIER, purchase.getSession().getUser().getStringView());
		purchaseEntityData.put(FIELD_RETURNCHECK_SUM, "-"  + sum.substring(0, sum.length() - 2) + "," + sum.substring(sum.length() - 2, sum.length()));
		purchaseEntityData.put(FIELD_GOOD_GOODCODE, purchase.getPositions().get(0).getProduct().getBarCode().getBarCode());

		// если произвольный возврат, то чек продажи должен быть пустой
		if (purchase.getSuperPurchase()!=null) {
			purchaseEntityData.put(FIELD_SALECHECK_DATE, "29.08.14");
			purchaseEntityData.put(FIELD_SALECHECK_CASHNUMBER, String.valueOf(purchase.getSuperPurchase().getShift().getCashNum()));
			purchaseEntityData.put(FIELD_SALECHECK_SHIFT, String.valueOf(purchase.getSuperPurchase().getShift().getNumShift()));
			purchaseEntityData.put(FIELD_SALECHECK_CHECKNUMBER, String.valueOf(purchase.getSuperPurchase().getNumber()));
		} else {
			purchaseEntityData.put(FIELD_SALECHECK_DATE, "");
			purchaseEntityData.put(FIELD_SALECHECK_CASHNUMBER, "");
			purchaseEntityData.put(FIELD_SALECHECK_SHIFT, "");
			purchaseEntityData.put(FIELD_SALECHECK_CHECKNUMBER, "");
		}
	}
}
