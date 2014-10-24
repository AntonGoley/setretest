package ru.crystals.set10.test.tablereports;

import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.crystals.httpclient.HttpClient;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.operday.tablereports.ReportConfigPage;
import ru.crystals.set10.test.dataproviders.TableReportsDataprovider;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.setretailx.cash.CashManagerRemote;
import ru.crystals.setretailx.cash.CashVO;
import static ru.crystals.set10.pages.operday.tablereports.TableReportPage.*;
import static ru.crystals.set10.pages.operday.tablereports.ReportConfigPage.*;

public class CashRegNumbersReportTest extends AbstractReportTest{
	
	private CashManagerRemote cashManager;
	private final String  GLOBAL_SERVLET_PATH = "/SET-OperDay-Web/TransportServlet";
	HttpClient httpConnect = new HttpClient();
	ReportConfigPage cashNumbersConfigPage;
	
	long date = new Date().getTime();
	String prefix = String.valueOf(date);
	
	//factoryNum_141234
	private String factoryNum = "fact" + prefix;
	private String fiscalNum  = "fisc" + prefix;
	private String eklzNum  = "ek" + prefix;
	private String fiscalDate  = DisinsectorTools.getDate("dd.MM.yyyy", date - 86400*100*100 );
	
	
	@DataProvider (name = "CashData")
	public Object[][] cashData(){
		return new Object[][] {
				{"Поле: Заводской номер", factoryNum},
				{"Поле: Регистрационный номер", fiscalNum},
				{"Поле: Номер ЭКЛЗ", eklzNum},
				{"Поле: Дата фискализации", fiscalDate.replace("-", ".")}
		};
	}
	
	@BeforeClass
	public void navigateCashRegNumsReport() throws Exception {
		// добавить данные для всех 1 касс
		for (int i=1; i<=5; i++) {
			setCashVO(i);
			fiscalDate  = DisinsectorTools.getDate("dd.MM.yyyy", date);
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
		fileSize =  cashNumbersConfigPage.saveReportFile(EXCELREPORT, chromeDownloadPath, reportNamePattern).length();
		log.info("Размер сохраненного файла: " + reportNamePattern + " равен " +  fileSize);
		Assert.assertTrue(fileSize > 0, "Файл отчета сохранился некорректно");
	}
	
	@Test ( groups = "CashRegNumbers_Report_Smoke",
			description = "SRL-137. Проверить, что \"Отчет о регистрационных номерах касс на ТК\" доступен для скачивания в формате pdf"
			)
	public void testCashRegReportSavePdf(){
		long fileSize = 0;
		String reportNamePattern = "CashRegNumber*.pdf";
		fileSize =  cashNumbersConfigPage.saveReportFile(PDFREPORT, chromeDownloadPath, reportNamePattern).length();
		log.info("Размер сохраненного файла: " + reportNamePattern + " равен " +  fileSize);
		Assert.assertTrue(fileSize > 0, "Файл отчета сохранился некорректно");
	}
	
	private void setCashVO(int cashNumber) throws Exception{
		CashVO cashVo = new CashVO();
			cashVo.setNumber(cashNumber);
			cashVo.setShopNumber(Integer.valueOf(Config.SHOP_NUMBER));
			cashVo.setEklzNum(eklzNum);
			cashVo.setFactoryNum(factoryNum);
			cashVo.setFiscalNum(fiscalNum);
			cashVo.setFiscalDate(fiscalDate);
			cashVo.setHardwareName("Beetle");
			
		httpConnect.setUrl("http://" + Config.RETAIL_HOST + ":8090" + GLOBAL_SERVLET_PATH);
		cashManager = httpConnect.find(CashManagerRemote.class, CashManagerRemote.SERVER_EJB_NAME);
		cashManager.updateCashParams(cashVo, true);
		log.info(String.format("Отправлена информация по кассе 1 на сервер: заводской номер - %s, рег. номер - %s, номер ЭКЛЗ - %s, дата фискализации - %s", 
				factoryNum, fiscalNum, eklzNum, fiscalDate )  );
	}
	
}


