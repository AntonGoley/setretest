package ru.crystals.set10.test.tablereports;

import java.util.Date;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.operday.tablereports.ReportConfigPage;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.GoodGenerator;
import ru.crystals.set10.utils.SoapRequestSender;
import ru.crystals.setretailx.products.catalog.Good;
import ru.crystals.setretailx.products.catalog.Likond;
import ru.crystals.setretailx.products.catalog.PluginProperty;
import static ru.crystals.set10.pages.operday.tablereports.TableReportPage.*;
import static ru.crystals.set10.utils.GoodGenerator.GOODTYPE_CIGGY;;

@Test(groups = "retail")
public class MRCPriceReportWithLekondsTest extends AbstractReportTest{
	
	ReportConfigPage MRCConfigPage;
	SoapRequestSender soapRequestSender = new SoapRequestSender(Config.RETAIL_HOST);
	Good good;
	GoodGenerator goodGenerator = new GoodGenerator();
	
	
	@BeforeClass
	public void navigateToMRCReport() {
		MRCConfigPage =  navigateToReportConfig(
				Config.RETAIL_URL, 
				Config.MANAGER,
				Config.MANAGER_PASSWORD,
				ReportConfigPage.class, 
				TAB_OTHER, 
				REPORT_NAME_MRC_PRICE);
		
		/*
		 * Создать табачный товар и добавить MRC
		 * и отправить на ретейл
		 */
		good = goodGenerator.generateGood(GOODTYPE_CIGGY);
		
		PluginProperty mrc = new PluginProperty();
		mrc.setKey("price");
		mrc.setValue(DisinsectorTools.randomMoney(100, "."));
		
		PluginProperty property = new PluginProperty();
		property.getProperties().add(mrc);
		property.setKey("mrc");
		
		good.getPluginProperties().add(property);
		soapRequestSender.sendGood(good);
		
	}	
	
	@DataProvider (name = "Даты ограничений")
	private static Object[][] lecondDates(){
		long today = new Date().getTime();
		long oneDay = 60*60*24*1000;
		
		return new Object[][]{
				{today - 3*3600*1000, today + oneDay, true},
				{today - oneDay*2, today - oneDay, false},
				{today + oneDay, today + oneDay*2, false}, 
				{today - oneDay, today + 3*3600*1000, true},
				{today - oneDay, today + oneDay, true},
		};
	}
	
	@Test (	description = "SRL-360. В прейскуранте на табачные изделия проверить действие лекондов", 
			dataProvider = "Даты ограничений")
	public void testMRCLeconds(long dateStart, long dateEnd, boolean expected){

		Likond likond = new Likond();
		likond.setBeginDate(goodGenerator.getDate(dateStart));
		likond.setEndDate(goodGenerator.getDate(dateEnd));
		likond.setMarking(good.getErpCode());
		
		soapRequestSender.sendLicond(likond);
		
		log.info("Период действий леконда: " + dateStart + "-" + dateEnd);
		doHTMLReport(MRCConfigPage, true);
		Assert.assertTrue(htmlReportResults.containsValue(good.getName()) == expected, "Не найден товар в отчете " + good.getName());
	}

}


