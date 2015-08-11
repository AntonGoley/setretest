package ru.crystals.set10.test.tablereports;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.operday.HTMLRepotResultPage;
import ru.crystals.set10.pages.operday.tablereports.WrongAdverstingPriceConfigPage;
import ru.crystals.set10.test.dataproviders.TableReportsDataprovider;
import ru.crystals.set10.utils.GoodGenerator;
import ru.crystals.set10.utils.SoapRequestSender;
import ru.crystals.setretailx.products.catalog.Good;
import ru.crystals.setretailx.products.catalog.Price;
import static ru.crystals.set10.pages.operday.tablereports.ReportConfigPage.EXCELREPORT;
import static ru.crystals.set10.pages.operday.tablereports.TableReportPage.*;
import static ru.crystals.set10.utils.GoodGenerator.*;


@Test (groups = {"centrum", "retail"})
public class WrongAdverstingPriceTest extends AbstractReportTest{
	
	WrongAdverstingPriceConfigPage reportConfigPage;
	SoapRequestSender soapSender = new SoapRequestSender();
	GoodGenerator goodGenerator = new GoodGenerator();
	String reportNamePattern = "IncorrectActionPrice*.xls";
	
	private Good goodPrice4BiggerThanPrice2;
	private Good goodPrice3BiggerThanPrice1;
	private Good goodPrice3BiggerThanPrice2;;
	private Good goodPrice2BiggerThanPrice1;
	
	List<Price> emptyPrices = new ArrayList<Price>();
	
	@BeforeClass
	public void navigateToReport() {
		reportConfigPage =  navigateToReportConfig(
				TARGET_HOST_URL, 
				Config.MANAGER,
				Config.MANAGER_PASSWORD,
				WrongAdverstingPriceConfigPage.class, 
				TAB_ADVERSTING, 
				REPORT_NAME_WRONG_ADVERSTING_PRICE);
				
		doHTMLReport(reportConfigPage, false);
		soapSender.setSoapServiceIP(TARGET_HOST);
	}	
	
	private Price getPriceByNumber(Good good, Long priceNumber){
		List<Price> prices = good.getPrices();
		for (int i=0; i<prices.size(); i++ ) {
			if (prices.get(i).getNumber().equals(priceNumber)){
				return prices.get(i);
			}
		}
		return new Price();
	}
	
	private void setInputData(){
		
		goodPrice4BiggerThanPrice2 = goodGenerator.generateGood(GOODTYPE_PIECE);
			Price price4 = goodGenerator.generatePrice(4L);
			getPriceByNumber(goodPrice4BiggerThanPrice2, 1L).setPrice(new BigDecimal("120.79"));
			getPriceByNumber(goodPrice4BiggerThanPrice2, 2L).setPrice(new BigDecimal("100.89"));
			price4.setPrice(new BigDecimal("110.99"));
			
			price4.setDiscountIdentifier("Price_4 bigger than  price_2");
			goodPrice4BiggerThanPrice2.getPrices().add(price4);
			goodPrice4BiggerThanPrice2.getShopIndices().add(new BigInteger((TARGET_SHOP)));
		
		goodPrice3BiggerThanPrice1 = goodGenerator.generateGood(GOODTYPE_PIECE);
			Price price3 = goodGenerator.generatePrice(3L);
			getPriceByNumber(goodPrice3BiggerThanPrice1, 1L).setPrice(new BigDecimal("99.79"));
			getPriceByNumber(goodPrice3BiggerThanPrice1, 2L).setPrice(new BigDecimal("89.89"));
			price3.setPrice(new BigDecimal("101.99"));
			
			price3.setDiscountIdentifier("Price_3 bigger than price_1");
			goodPrice3BiggerThanPrice1.getPrices().add(price3);
			goodPrice3BiggerThanPrice1.getShopIndices().add(new BigInteger((TARGET_SHOP)));
		
		goodPrice3BiggerThanPrice2 = goodGenerator.generateGood(GOODTYPE_PIECE);
			Price _price3 = goodGenerator.generatePrice(3L);
			getPriceByNumber(goodPrice3BiggerThanPrice2, 1L).setPrice(new BigDecimal("299.79"));
			getPriceByNumber(goodPrice3BiggerThanPrice2, 2L).setPrice(new BigDecimal("289.89"));
			_price3.setPrice(new BigDecimal("290.99"));
			
			_price3.setDiscountIdentifier("Price_3 bigger than price_2");
			goodPrice3BiggerThanPrice2.getPrices().add(_price3);
			goodPrice3BiggerThanPrice2.getShopIndices().add(new BigInteger((TARGET_SHOP)));
			
		goodPrice2BiggerThanPrice1 = goodGenerator.generateGood(GOODTYPE_PIECE);
			getPriceByNumber(goodPrice2BiggerThanPrice1, 1L).setPrice(new BigDecimal("10.99"));
			getPriceByNumber(goodPrice2BiggerThanPrice1, 2L).setPrice(new BigDecimal("12.99"));
			goodPrice2BiggerThanPrice1.getShopIndices().add(new BigInteger((TARGET_SHOP)));
	}
	
	@DataProvider(name = "Цены")
	private Object[][] priceData(){
		setInputData();
		return new Object[][]{
				{"Price_4> price_2", goodPrice4BiggerThanPrice2, getPriceByNumber(goodPrice4BiggerThanPrice2, 4L).getDiscountIdentifier()},
				{"Price_3> price_1", goodPrice3BiggerThanPrice1, getPriceByNumber(goodPrice3BiggerThanPrice1, 3L).getDiscountIdentifier()},
				{"Price_3> price_2", goodPrice3BiggerThanPrice2, getPriceByNumber(goodPrice3BiggerThanPrice2, 3L).getDiscountIdentifier()},
				{"Price_2> price_1", goodPrice2BiggerThanPrice1, ""},
		};
	}
	
	@Test (	priority = 1,
			description = "SRTE-67. Проверить условие попадания рекламной цены в отчет на ТК",
			dataProvider = "Цены"
			)
	public void testAdverstingPrice(String description, Good good, String advIdentifier){
		ArrayList<String> reportRow;
		
		soapSender.sendGood(good);
		getDriver().navigate().refresh();
		htmlReportResults = new HTMLRepotResultPage(getDriver());
		reportRow = htmlReportResults.getLineValuesByCellValue(good.getMarkingOfTheGood());

		Assert.assertTrue(reportRow.contains(good.getMarkingOfTheGood()), "В отчете не отображается значение кода товара");
		Assert.assertTrue(reportRow.contains(good.getGroup().getCode()), "В отчете не отображается значение для группы товара" );
		Assert.assertTrue(reportRow.contains(advIdentifier), "В отчете неверно отображается название рекламной акции");
	}
	
	@Test (	priority = 1,
			description = "SRTE-67. Проверить названия отчета и название колонок в шапке таблицы отчета \"Некорректная акционная цена\"", 
			dataProvider = "Некорректная акционная цена", 
			dataProviderClass = TableReportsDataprovider.class)
	public void testAdverstingPriceHTMLReportTableHead(String fieldName){
		log.info(fieldName);
		Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}
	
	@Test (	priority = 2,
			alwaysRun = true,
			description = "SRTE-67. Проверить, что \"Некорректная акционная цена\" доступен для скачивания в формате xls"
			)
	public void testAdverstingPriceReportSaveFormats(){
		reportConfigPage.switchWindow(true);
		long fileSize = 0;
		fileSize =  reportConfigPage.exportFileData(chromeDownloadPath, reportNamePattern, reportConfigPage, EXCELREPORT).length();
		log.info("Размер сохраненного файла: " + reportNamePattern + " равен " +  fileSize);
		Assert.assertTrue(fileSize > 0, "Файл отчета сохранился некорректно");
	}
	
	@Test ( priority = 1,
			description = "SRL-799. 1я цена (ц1<ц2) не должна попасть в отчет, если при переоценке приходит новая ц1>ц2 ")
	public void testTwoPricesAndFirstUpdated(){
		ArrayList<String> reportRow;
		
		Good pieceGood = goodGenerator.generateGood(GOODTYPE_PIECE);
		getPriceByNumber(pieceGood, 1L).setPrice(new BigDecimal("36.01"));
		getPriceByNumber(pieceGood, 2L).setPrice(new BigDecimal("37.49"));
		soapSender.sendGood(pieceGood);
		
		getDriver().navigate().refresh();
		reportRow = htmlReportResults.getLineValuesByCellValue(pieceGood.getMarkingOfTheGood());
		
		Assert.assertTrue(reportRow.contains(pieceGood.getMarkingOfTheGood()), "В отчете не отображается значение кода товара");
		Assert.assertTrue(reportRow.contains(pieceGood.getGroup().getCode()), "В отчете не отображается значение для группы товара" );
		
		getPriceByNumber(pieceGood, 1L).setPrice(new BigDecimal("39.39"));
		soapSender.sendGood(pieceGood);
		
		getDriver().navigate().refresh();
		htmlReportResults = new HTMLRepotResultPage(getDriver());
		
		/* Проверить, что markingofthegood изчез из отчета*/
		Assert.assertFalse(htmlReportResults.containsValue(pieceGood.getMarkingOfTheGood()), "Цена недолжна попадать в отчет после переоценки товара!" );
	}
	
}
