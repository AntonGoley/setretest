package ru.crystals.set10.test.tablereports;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.basic.MainPage;
import ru.crystals.set10.pages.operday.HTMLRepotResultPage;
import ru.crystals.set10.pages.operday.tablereports.AbstractReportConfigPage;
import ru.crystals.set10.pages.operday.tablereports.TableReportPage;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.test.dataproviders.TableReportsDataprovider;
import static ru.crystals.set10.pages.operday.tablereports.AbstractReportConfigPage.HTMLREPORT;
import static ru.crystals.set10.pages.operday.tablereports.TableReportPage.*;


public class PLUinWeightReportTest extends AbstractTest{
	LoginPage loginPage;
	MainPage mainPage;
	TableReportPage tableReportsPage;
	AbstractReportConfigPage PLUConfigPage;
	HTMLRepotResultPage htmlReportResults;

	
	@BeforeClass
	public void navigateToPLUReport() {
		mainPage = new LoginPage(getDriver(), Config.RETAIL_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		tableReportsPage = mainPage.openOperDay().openTableReports();
		PLUConfigPage = tableReportsPage.openReportConfigPage(AbstractReportConfigPage.class, TAB_OTHER, REPORT_NAME_PLU_ON_WEIGHT);
		doReport();
	}	
	
	public void doReport(){
		htmlReportResults = PLUConfigPage.generateReport(HTMLREPORT);
		PLUConfigPage.switchWindow(true);
	}
	
	@Test (	description = "Проверить названия отчета и название колонок в шапке таблицы отчета по количеству PLU в весах", 
			dataProvider = "Шапка отчета по количеству PLU в весах", dataProviderClass = TableReportsDataprovider.class)
	public void testPLUTKHTMLReportTableHead(String fieldName){
		log.info(fieldName);
		Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}
	
	@Test (	description = "Проверить, что \"Отчёт по количеству PLU в весах на ТК\" доступен для скачивания в формате xls",
			dataProvider = "Доступные форматы для скачивания"
			)
	public void testPLUReportSaveFormats(String reportFormat, String reportNamePattern){
		long fileSize = 0;
		fileSize =  PLUConfigPage.saveReportFile(reportFormat, chromeDownloadPath, reportNamePattern).length();
		log.info("Размер сохраненного файла: " + reportNamePattern + " равен " +  fileSize);
		Assert.assertTrue(fileSize > 0, "Файл отчета сохранился некорректно");
	}
	
	@DataProvider (name = "Доступные форматы для скачивания")
	public static Object[][] reportFormats(){
		return new  Object[][] {
			{AbstractReportConfigPage.EXCELREPORT, "PluInScales_*.xls"}
		};
	}
	
}
