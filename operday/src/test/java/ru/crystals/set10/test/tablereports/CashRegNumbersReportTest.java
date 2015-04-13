package ru.crystals.set10.test.tablereports;

import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.operday.tablereports.ReportConfigPage;
import ru.crystals.set10.test.dataproviders.TableReportsDataprovider;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.setretailx.cash.CashVO;
import static ru.crystals.set10.pages.operday.tablereports.TableReportPage.*;
import static ru.crystals.set10.pages.operday.tablereports.ReportConfigPage.*;


@Test (groups = "retail")
public class CashRegNumbersReportTest extends AbstractReportTest{
	
	ReportConfigPage cashNumbersConfigPage;
	
	long date = new Date().getTime();
	
	private String factoryNum;
	private String fiscalNum;
	private String eklzNum;
	private String fiscalDate;

	/*
	 * Дата провайдер для валидации одной из касс
	 */
	@DataProvider (name = "CashData")
	private Object[][] cashData() throws Exception{
		return new Object[][] {
				{"Поле: Заводской номер", factoryNum},
				{"Поле: Регистрационный номер", fiscalNum},
				{"Поле: Номер ЭКЛЗ", eklzNum},
				{"Поле: Дата фискализации", fiscalDate.replace("-", ".")}
		};
	}
	
	@BeforeClass
	public void navigateCashRegNumsReport() throws Exception {
		/*
		 * Отправить данные по кассам на сервер
		 */
		for (int i=1; i<=5; i++) {
			setCashData(i, date - 86400*100*100*i);
		}	
		
		cashNumbersConfigPage =  navigateToReportConfig(
				Config.RETAIL_URL, 
				Config.MANAGER,
				Config.MANAGER_PASSWORD,
				ReportConfigPage.class, 
				TAB_OTHER, 
				REPORT_NAME_CASH_REGNUMBERS);
		doHTMLReport(cashNumbersConfigPage, true);
	}
	
	@Test (	description = "SRL-137. Проверить данные о кассе 1 в отчете о регистрационных номерах касс на ТК", 
			dataProvider = "CashData")
	public void testCashRegHTMLReportCash1Data(String field, String value){
		log.info(field);
		Assert.assertTrue(htmlReportResults.containsValue(value), "Неверное значение поля : " + field);
	}
	
	
	@Test (	description = "SRL-137. Проверить названия отчета и название колонок в шапке таблицы отчета о регистрационных номерах касс на ТК", 
			dataProvider = "Шапка отчета о рег. номерах", dataProviderClass = TableReportsDataprovider.class)
	public void testCashRegHTMLReportTableHead(String fieldName){
		log.info(fieldName);
		Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}
	
	@Test ( description = "SRL-137. Проверить, что \"Отчет о регистрационных номерах касс на ТК\" доступен для скачивания в формате xls"
			)
	public void testCashRegReportSaveXls(){
		long fileSize = 0;
		String reportNamePattern = "CashRegNumber*.xls";
		fileSize =  cashNumbersConfigPage.exportFileData(chromeDownloadPath, reportNamePattern, cashNumbersConfigPage, EXCELREPORT).length();
		log.info("Размер сохраненного файла: " + reportNamePattern + " равен " +  fileSize);
		Assert.assertTrue(fileSize > 0, "Файл отчета сохранился некорректно");
	}
	
	@Test (	description = "SRL-137. Проверить, что \"Отчет о регистрационных номерах касс на ТК\" доступен для скачивания в формате pdf"
			)
	public void testCashRegReportSavePdf(){
		long fileSize = 0;
		String reportNamePattern = "CashRegNumber*.pdf";
		fileSize =  cashNumbersConfigPage.exportFileData(chromeDownloadPath, reportNamePattern, cashNumbersConfigPage, PDFREPORT).length();
		log.info("Размер сохраненного файла: " + reportNamePattern + " равен " +  fileSize);
		Assert.assertTrue(fileSize > 0, "Файл отчета сохранился некорректно");
	}
	
	/*
	 * Задать данные по каждой кассе
	 */
	private void setCashData(int cashNumber, long date) throws Exception{
		CashVO cashVO = new CashVO();
		cashVO = cashEmulator.setCashVO(cashNumber, Config.SHOP_NUMBER, date);
		eklzNum = cashVO.getEklzNum();
		factoryNum = cashVO.getFactoryNum();
		fiscalNum = cashVO.getFiscalNum();
		fiscalDate = DisinsectorTools.getDate("dd.MM.YYYY", Long.valueOf(cashVO.getFiscalDate()));
		
		cashEmulator.sendCashVO(cashVO);	
	}
}


