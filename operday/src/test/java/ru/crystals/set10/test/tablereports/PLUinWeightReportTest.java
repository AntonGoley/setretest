package ru.crystals.set10.test.tablereports;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.operday.tablereports.ReportConfigPage;
import ru.crystals.set10.test.dataproviders.TableReportsDataprovider;
import static ru.crystals.set10.pages.operday.tablereports.TableReportPage.*;
import static ru.crystals.set10.pages.operday.tablereports.ReportConfigPage.*;

@Test(groups = "retail")
public class PLUinWeightReportTest extends AbstractReportTest{
	
	ReportConfigPage PLUConfigPage;

	@BeforeClass
	public void navigateToPLUReport() {
		PLUConfigPage =  navigateToReportConfig(
				TARGET_HOST, 
				Config.MANAGER,
				Config.MANAGER_PASSWORD,
				ReportConfigPage.class, 
				TAB_OTHER, 
				REPORT_NAME_PLU_ON_WEIGHT);
		doHTMLReport(PLUConfigPage, true);
	}	
	
	
	@Test (	description = "SRL-177. Проверить названия отчета и название колонок в шапке таблицы отчета по количеству PLU в весах", 
			dataProvider = "Шапка отчета по количеству PLU в весах", dataProviderClass = TableReportsDataprovider.class)
	public void testPLUTKHTMLReportTableHead(String fieldName){
		log.info(fieldName);
		Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}
	
	@Test (	description = "SRL-177. Проверить, что \"Отчёт по количеству PLU в весах на ТК\" доступен для скачивания в формате xls",
			dataProvider = "Доступные форматы для скачивания"
			)
	public void testPLUReportSaveFormats(String reportFormat, String reportNamePattern){
		long fileSize = 0;
		fileSize =  PLUConfigPage.exportFileData(chromeDownloadPath, reportNamePattern, PLUConfigPage, reportFormat).length();
		log.info("Размер сохраненного файла: " + reportNamePattern + " равен " +  fileSize);
		Assert.assertTrue(fileSize > 0, "Файл отчета сохранился некорректно");
	}
	
	@DataProvider (name = "Доступные форматы для скачивания")
	public static Object[][] reportFormats(){
		return new  Object[][] {
			{EXCELREPORT, "PluInScales_*.xls"}
		};
	}
	
}
