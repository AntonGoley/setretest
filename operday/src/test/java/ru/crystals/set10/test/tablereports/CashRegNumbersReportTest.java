package ru.crystals.set10.test.tablereports;

import java.util.Calendar;
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
	private String replaceEklzDate;
	private String blockCashDate;
	private String dateBeforeBlockCash;
	
	private Calendar calendar;

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
	
	/*
	 * Дата провайдер для валидации одной из касс
	 */
	@DataProvider (name = "CashData")
	private Object[][] cashData() throws Exception{
		
		
		
		return new Object[][] {
				{"Поле: Заводской номер", factoryNum},
				{"Поле: Регистрационный номер", fiscalNum},
				{"Поле: Номер ЭКЛЗ", eklzNum},
				{"Поле: Дата фискализации", fiscalDate.replace("-", ".")},
				{"Поле: Заменить ЭКЛЗ с", replaceEklzDate},
				{"Поле: Дата блокировки кассы", blockCashDate},
				{"Поле: До блокировки кассы осталось (дней) ", dateBeforeBlockCash},
		};
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
		long currentDateMs = 0;
		long blockDateDateMs = 0;
		
		CashVO cashVO = new CashVO();
		cashVO = cashEmulator.setCashVO(cashNumber, Config.SHOP_NUMBER, date);
		eklzNum = cashVO.getEklzNum();
		factoryNum = cashVO.getFactoryNum();
		fiscalNum = cashVO.getFiscalNum();
		fiscalDate = cashVO.getFiscalDate();
		
		
		calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date);
		currentDateMs = calendar.getTimeInMillis();
		/** считаем дату начала замены эклз*/
		calendar.add(Calendar.YEAR, 1);
		replaceEklzDate = DisinsectorTools.getDate("dd.MM.yyyy", calendar.getTimeInMillis());	
		
		/** считаем дату блокировки кассы*/
		calendar.add(Calendar.MONTH, 2);
		calendar.set(Calendar.DATE, 1);
		
		blockCashDate = DisinsectorTools.getDate("dd.MM.yyyy", calendar.getTimeInMillis());
		blockDateDateMs = calendar.getTimeInMillis();
		/** сколько дней до блокировки кассы*/
		dateBeforeBlockCash = String.valueOf( ((blockDateDateMs - currentDateMs)/(24 * 60 * 60 * 1000) - 1));
		log.info("Количество дней до блокировки: " +  dateBeforeBlockCash);
		
		
		cashEmulator.sendCashVO(cashVO);	
	}
}


