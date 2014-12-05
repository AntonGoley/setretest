package ru.crystals.set10.test.tablereports;

import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.operday.tablereports.ReportConfigPage;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.SoapRequestSender;
import static ru.crystals.set10.pages.operday.tablereports.TableReportPage.*;
import static ru.crystals.set10.utils.SoapRequestSender.RETURN_MESSAGE_CORRECT;

public class MRCPriceReportWithLekondsTest extends AbstractReportTest{
	
	ReportConfigPage MRCConfigPage;
	SoapRequestSender soapRequestSender;
	String name = "${name}";
	String since_date = "${since_date}";
	String till_date = "${till_date}";
	String lecondRequest = DisinsectorTools.getFileContentAsString("MRC_Report/mrc_lecond.txt");
	/*
	 *  файл товара с marking-of-the-good отличным от marking-of-the-good
	 *  для других тестов на mrc, чтобы не учитывались леконды при отображении в отчете
	 */
	String goodRequest = DisinsectorTools.getFileContentAsString("MRC_Report/mrc_lecond_good.txt"); 
	/*
	 * отсылаем товар, на который будем импортировать леконды
	 * marking-of-the-good в файлах данных должен совпадать
	 */
	String goodName = sendRequest(goodRequest, "", "");
	private static final String LECOND_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	
	@BeforeClass
	public void navigateToMRCReport() {
		MRCConfigPage =  navigateToReportConfig(
				Config.RETAIL_URL, 
				Config.MANAGER,
				Config.MANAGER_PASSWORD,
				ReportConfigPage.class, 
				TAB_OTHER, 
				REPORT_NAME_MRC_PRICE);
	}	
	
	@DataProvider (name = "Даты ограничений")
	public static Object[][] lecondDates(){
		long today = new Date().getTime();
		long oneDay = 60*60*24*1000;
		
		return new Object[][]{
				{getLecondDate(today - 3600*1000), getLecondDate(today + oneDay), true},
				{getLecondDate(today - oneDay*2), getLecondDate(today - oneDay), false},
				{getLecondDate(today + oneDay), getLecondDate(today + oneDay*2), false}, 
				{getLecondDate(today - oneDay), getLecondDate(today + 3600*1000), true},
				{getLecondDate(today - oneDay), getLecondDate(today + oneDay), true},
				
		};
	}
	
	
	@Test (	description = "SRL-360. В прейскуранте на табачные изделия проверить действие лекондов", 
			dataProvider = "Даты ограничений")
	public void testMRCLeconds(String dateStart, String dateEnd, boolean expected){
		sendRequest(lecondRequest, dateStart, dateEnd);
		log.info("Период действий леконда: " + dateStart + "-" + dateEnd);
		doHTMLReport(MRCConfigPage, true);
		Assert.assertTrue(htmlReportResults.containsValue(goodName) == expected, "Не найден товар в отчете " + goodName);
	}
	
	
	private String sendRequest(String request, String dateStart, String dateEnd){
		String mrc_good_name = "Tabaco_" + String.valueOf(new Date().getTime());
		soapRequestSender = new SoapRequestSender();
		soapRequestSender.setSoapServiceIP(Config.RETAIL_HOST);
		soapRequestSender.sendGoods(request
				.replace(name, mrc_good_name)
				.replace(since_date, dateStart)
				.replace(till_date, dateEnd)
				, soapRequestSender.generateTI());
		return mrc_good_name;
	}
	
	private static String getLecondDate(long date){
		return DisinsectorTools.getDate(LECOND_DATE_FORMAT, date);
	}
}


