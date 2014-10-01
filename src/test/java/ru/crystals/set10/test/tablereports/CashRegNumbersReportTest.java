package ru.crystals.set10.test.tablereports;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.operday.tablereports.AbstractReportConfigPage;
import ru.crystals.set10.test.dataproviders.TableReportsDataprovider;
import static ru.crystals.set10.pages.operday.tablereports.TableReportPage.*;

public class CashRegNumbersReportTest extends AbstractReportTest{
	
	AbstractReportConfigPage cashNumbersConfigPage;
	
	@BeforeClass
	public void navigateToPLUReport() {
		cashNumbersConfigPage =  navigateToReportConfig(
				Config.RETAIL_URL, 
				Config.MANAGER,
				Config.MANAGER_PASSWORD,
				AbstractReportConfigPage.class, 
				TAB_OTHER, 
				REPORT_NAME_CASH_REGNUMBERS);
		doHTMLReport(cashNumbersConfigPage);
	}	
	
	@Test (	groups = "CashRegNumbers_Report_Smoke",
			description = "Проверить названия отчета и название колонок в шапке таблицы отчета о регистрационных номерах касс на ТК", 
			dataProvider = "Шапка отчета о рег. номерах", dataProviderClass = TableReportsDataprovider.class)
	public void testMRCHTMLReportTableHead(String fieldName){
		log.info(fieldName);
		Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}
	
	@Test ( groups = "CashRegNumbers_Report_Smoke",
			description = "Проверить, что \"Отчет о регистрационных номерах касс на ТК\" доступен для скачивания в формате xls"
			)
	public void testMRCReportSaveXls(){
		long fileSize = 0;
		String reportNamePattern = "CashRegNumber*.xls";
		fileSize =  cashNumbersConfigPage.saveReportFile(AbstractReportConfigPage.EXCELREPORT, chromeDownloadPath, reportNamePattern).length();
		log.info("Размер сохраненного файла: " + reportNamePattern + " равен " +  fileSize);
		Assert.assertTrue(fileSize > 0, "Файл отчета сохранился некорректно");
	}
	
	@Test ( groups = "CashRegNumbers_Report_Smoke",
			description = "Проверить, что \"Отчет о регистрационных номерах касс на ТК\" доступен для скачивания в формате pdf"
			)
	public void testMRCReportSavePdf(){
		long fileSize = 0;
		String reportNamePattern = "CashRegNumber*.pdf";
		fileSize =  cashNumbersConfigPage.saveReportFile(AbstractReportConfigPage.PDFREPORT, chromeDownloadPath, reportNamePattern).length();
		log.info("Размер сохраненного файла: " + reportNamePattern + " равен " +  fileSize);
		Assert.assertTrue(fileSize > 0, "Файл отчета сохранился некорректно");
	}
}


