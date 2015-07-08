package ru.crystals.set10.test.weight;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ru.crystals.set10.config.Config;
import ru.crystals.set10.utils.GoodGenerator;
import ru.crystals.set10.utils.SoapRequestSender;
import ru.crystals.setretailx.products.catalog.Good;
import ru.crystals.setretailx.products.catalog.GoodsCatalog;
import ru.crystals.setretailx.products.catalog.Likond;

@Test(groups = {"retail"})
public class WeightLekondTest extends WeightAbstractTest { 
	
	protected static final Logger log = Logger.getLogger(WeightLekondTest.class);
	
	SoapRequestSender soapSender = new SoapRequestSender();
	GoodGenerator goodGenerator = new GoodGenerator();
	
	Good weightGood;
	Likond likond;
	long likondFrom;
	long likondTo;

	@BeforeClass
	public void initData(){
		soapSender.setSoapServiceIP(Config.RETAIL_HOST);
	}
	
	@BeforeMethod
	public void setUpLikond(){
		likond = new Likond();
	}
	
	/*
	 * В товаре необходимо передавайть ERP код,
	 * т.к далее, в ликонде, на самом деле, высылается ERP код товара, а не marking-of-the-good товара.
	 * По ERP коду товара, переданному в ликонде товар выгрузится из весов.
	 */
	@Test (description = "SRTE-119. Весовой товар выгружается из весов, если загружен леконд запрещающий продажу товара (время продажи закончилось вчера)")
	public void testGoodUnloadIfLecondBanSales(){
		int pluNum = pluNumber++;
		
		/* Отправить товар и проверить, что он прогрузился в весы */
		weightGood = goodGenerator.generateWeightGood(String.valueOf(pluNum));
		soapSender.sendGood(weightGood);
		
		Assert.assertTrue(scales.waitPluLoaded(pluNum), "Товар не загрузился в весы. PLU = " + pluNum);
		
		/* сгенерить и отправить леконд, запрещающий продажу */
		likondFrom = System.currentTimeMillis() - 3600 * 12 * 1000;
		likondTo = System.currentTimeMillis() - 3600 * 6 * 1000;
		
		likond.setBeginDate(goodGenerator.getDate(likondFrom));
		likond.setEndDate(goodGenerator.getDate(likondTo));
		likond.setMarking(weightGood.getErpCode());
		
		soapSender.sendLicond(likond);
		Assert.assertTrue(scales.waitPluUnLoaded(pluNum), "Товар НЕ выгрузился из весов после загрузки ликонда, запрещающего продажу. PLU = " + pluNum);
	}
	
	
	@Test (description = "SRTE-119. Весовой товар не загружается на весы, если загружен леконд запрещающий продажу товара (вчера)",
			/* тест не должен запускаться первый, т.к должен быть создан файл виртуальных весов*/
			groups = "loadUnload",
			priority = 1)
	public void testGoodNotLoadedIfLecondBanSales(){
		int pluNum = pluNumber++;
		
		weightGood = goodGenerator.generateWeightGood(String.valueOf(pluNum));
		
		/* сгенерить и отправить леконд, запрещающий продажу */
		likondFrom = System.currentTimeMillis() - 3600 * 12 * 1000;
		likondTo = System.currentTimeMillis() - 3600 * 6 * 1000;
		
		likond.setBeginDate(goodGenerator.getDate(likondFrom));
		likond.setEndDate(goodGenerator.getDate(likondTo));
		likond.setMarking(weightGood.getErpCode());
		soapSender.sendLicond(likond);
		
		/* отправить товар*/
		soapSender.sendGood(weightGood);
		Assert.assertFalse(scales.waitPluLoaded(pluNum),  "Товар не должен быть выгружен в весы, т.к ликонд, загруженый ранее запрещает продажу. PLU = " + pluNum);
	}
	
	@Test (description = "SRTE-119. Весовой товар загружается на весы, если загружен новый леконд, разрешающий продажу товара"
			+ " (новый леконд отменяет действие леконда, загруженного прежде)",
			/* тест должен запускаться после указанного в  dependsOnMethods*/
			groups = "loadUnload",
			priority = 1,
			dependsOnMethods = "testGoodNotLoadedIfLecondBanSales")
	public void testGoodLoadedIfLecondAllowSalesAfterBan(){
		/* берем PLU из товра предыдущего теста: testGoodNotLoadedIfLecondBanSales*/
		int pluNum = goodGenerator.getWeightPluNumber(weightGood);

		/* сгенерить и отправить леконд, разрешающий продажу товара, созданного в тесте testGoodNotLoadedIfLecondBanSales*/
		likondFrom = System.currentTimeMillis() - 3600 * 2 * 1000;
		likondTo = System.currentTimeMillis() + 3600 * 6 * 1000;
		
		likond.setBeginDate(goodGenerator.getDate(likondFrom));
		likond.setEndDate(goodGenerator.getDate(likondTo));
		likond.setMarking(weightGood.getErpCode());
		soapSender.sendLicond(likond);
		
		Assert.assertTrue(scales.waitPluLoaded(pluNum), "Товар не выгружен на весы, после загрузки ликонда, разрешающего продажу товара. PLU = " + pluNum);
	}
	
	@Test (description = "SRTE-119. Весовой товар выгружается на весы, если загружен леконд разрешающий продажу товара (со вчера до завтра)", 
			/* тест не должен запускаться первый, т.к должен быть создан файл виртуальных весов*/
			dependsOnGroups = "loadUnload",
			alwaysRun = true)
	public void testGoodLoadedIfLecondallowSales(){
		
		int pluNum = pluNumber++;
		
		weightGood = goodGenerator.generateWeightGood(String.valueOf(pluNum));
		
		/* сгенерить и отправить леконд, разрешающий продажу */
		likondFrom = System.currentTimeMillis() - 3600 * 24 * 1000;
		likondTo = System.currentTimeMillis() + 3600 * 24 * 1000;
		
		likond.setBeginDate(goodGenerator.getDate(likondFrom));
		likond.setEndDate(goodGenerator.getDate(likondTo));
		likond.setMarking(weightGood.getErpCode());
		soapSender.sendLicond(likond);
		
		/* отправить товар*/
		soapSender.sendGood(weightGood);
		
		Assert.assertTrue(scales.waitPluLoaded(pluNum),  "Товар не загрузился на весы, после загрузки ликонда, разрешающего продажу. PLU = " + pluNum);
	}
	
	
	@Test (description = "SRTE-119. Весовой товар не выгружается на весы, если леконд разрешает продажу товара в будущем (через 2 часа)", 
			/* тест не должен запускаться первый, т.к должен быть создан файл виртуальных весов*/
			dependsOnGroups = "loadUnload",
			alwaysRun = true)
	public void testGoodNotLoadedIfLecondallowSalesInFuture(){

		int pluNum = pluNumber++;
		
		weightGood = goodGenerator.generateWeightGood(String.valueOf(pluNum));
		
		/* сгенерить и отправить леконд, не разрешающий продажу */
		likondFrom = System.currentTimeMillis() + 3600 * 24 * 1000;
		likondTo = System.currentTimeMillis() + 3600 * 48 * 1000;
		
		likond.setBeginDate(goodGenerator.getDate(likondFrom));
		likond.setEndDate(goodGenerator.getDate(likondTo));
		likond.setMarking(weightGood.getErpCode());
		soapSender.sendLicond(likond);
		
		/* отправить товар*/
		soapSender.sendGood(weightGood);
		
		Assert.assertFalse(scales.waitPluLoaded(pluNum),  "Товар не загрузился на весы, после загрузки ликонда, разрешающего продажу. PLU = " + pluNum);
	}
	
	@Test (description = "Товар должен выгружаться из весов, если приходит обновление цены и леконд на запрет продажи товара в одной транзакции импорта", 
			dependsOnGroups = "loadUnload",
			alwaysRun = true)
	public void testGoodUnloadedIfLekondBanSalesAndImportedWithGood(){
		int pluNum = pluNumber++;
		weightGood = goodGenerator.generateWeightGood(String.valueOf(pluNum));
		
		/* сгенерить леконд, не разрешающий продажу */
		likondFrom = System.currentTimeMillis() + 3600 * 24 * 1000;
		likondTo = System.currentTimeMillis() + 3600 * 48 * 1000;
		
		likond.setBeginDate(goodGenerator.getDate(likondFrom));
		likond.setEndDate(goodGenerator.getDate(likondTo));
		likond.setMarking(weightGood.getErpCode());
		
		/* апдейтим 1ю цену у весового товара */
		for(int i=0; i<weightGood.getPrices().size(); i++){
			if (weightGood.getPrices().get(i).equals(1L)){
				weightGood.getPrices().get(i).setPrice(new BigDecimal("1.00")); // задаем 1р, потому что по умолчанию 1я цена генерится большая
			}
		}
		
		GoodsCatalog goodsCatalog = new GoodsCatalog();
		goodsCatalog.getLikonds().add(likond);
		goodsCatalog.getGoods().add(weightGood);
		
		/* отправляем новую 1ю цену и леконд в одном запросе*/
		soapSender.send(goodsCatalog);
		
		Assert.assertTrue(scales.waitPluUnLoaded(pluNum),  "Товар не не выгрузился из весов, при существующем леконде, запрещающем продажу. PLU = " + pluNum);
		Assert.assertFalse(scales.waitPluLoaded(pluNum),  "Товар загрузился на весы, ри существующем леконде, запрещающем продажу. PLU = " + pluNum);
		
	}
	
	/*
	 *  как синхронизировать время сервера (получить время сервера)?
	 */
	//@Test (description = "SRTE-119. Весовой товар загружается на весы при наступлении времени начала продажи, заданное в леконде",
	//		enabled = false)
//	public void testGoodLoadedIfLecondStartTimeIsOn(){
//		
//	}
}
