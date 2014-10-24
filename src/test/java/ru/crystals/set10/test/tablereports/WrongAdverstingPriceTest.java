package ru.crystals.set10.test.tablereports;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.operday.tablereports.WrongAdverstingPriveConfigPage;
import ru.crystals.set10.test.dataproviders.TableReportsDataprovider;
import static ru.crystals.set10.pages.operday.tablereports.TableReportPage.*;
import static ru.crystals.set10.pages.operday.tablereports.ReportConfigPage.*;


public class WrongAdverstingPriceTest extends AbstractReportTest{
	
	WrongAdverstingPriveConfigPage reportConfigPage;

	@BeforeClass
	public void navigateToReport() {
		reportConfigPage =  navigateToReportConfig(
				Config.RETAIL_URL, 
				Config.MANAGER,
				Config.MANAGER_PASSWORD,
				WrongAdverstingPriveConfigPage.class, 
				TAB_ADVERSTING, 
				REPORT_NAME_WRONG_ADVERSTING_PRICE);
		doHTMLReport(reportConfigPage, true);
	}	
	
	
	
	
	@Test (	description = "SRTE-67. Проверить условие попадания рекламной цены в отчет на ТК"
			)
	public void testAdverstingPrice(){
		
	}

	
	@Test (	description = "SRTE-67. Проверить названия отчета и название колонок в шапке таблицы отчета \"Некорректная акционная цена\"", 
			dataProvider = "Некорректная акционная цена", dataProviderClass = TableReportsDataprovider.class)
	public void testAdverstingPriceHTMLReportTableHead(String fieldName){
		log.info(fieldName);
		Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}
	
	@Test (	description = "SRTE-67. Проверить, что \"Некорректная акционная цена\" доступен для скачивания в формате xls"
			)
	public void testAdverstingPriceReportSaveFormats(){
		long fileSize = 0;
		String reportNamePattern = "IncorrectActionPrice*.xls";
		fileSize =  reportConfigPage.saveReportFile(EXCELREPORT, chromeDownloadPath, reportNamePattern).length();
		log.info("Размер сохраненного файла: " + reportNamePattern + " равен " +  fileSize);
		Assert.assertTrue(fileSize > 0, "Файл отчета сохранился некорректно");
	}
	
}
