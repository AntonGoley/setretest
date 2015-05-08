package ru.crystals.set10.test.weight;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.utils.GoodGenerator;
import ru.crystals.set10.utils.SoapRequestSender;
import ru.crystals.setretailx.products.catalog.Good;
import ru.crystals.setretailx.products.catalog.Likond;

@Test(groups = {"retail"})
public class WeightLekondTest extends WeightAbstractTest { 
	
	SoapRequestSender soapSender = new SoapRequestSender();
	GoodGenerator goodGenerator = new GoodGenerator();
	
	Good weightGood;
	Likond likond;
	long likondFrom;
	long likondTo;

	@BeforeClass
	public void initData(){
		soapSender.setSoapServiceIP(Config.RETAIL_HOST);
		pluNumber++;
		prerareSuite();
		scales.clearVScalesFileData();
		
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
		
		/* Отправить товар и проверить, что он прогрузился в весы */
		weightGood = goodGenerator.generateWeightGood(String.valueOf(pluNumber));
		soapSender.sendGood(weightGood);
		
		Assert.assertTrue(scales.waitPluLoaded(pluNumber), "Товар не загрузился в весы. PLU = " + pluNumber);
		
		/* сгенерить и отправить леконд, запрещающий продажу */
		likondFrom = System.currentTimeMillis() - 3600 * 12 * 1000;
		likondTo = System.currentTimeMillis() - 3600 * 6 * 1000;
		
		likond.setBeginDate(goodGenerator.getDate(likondFrom));
		likond.setEndDate(goodGenerator.getDate(likondTo));
		likond.setMarking(weightGood.getErpCode());
		
		soapSender.sendLicond(likond);
		Assert.assertTrue(scales.waitPluUnLoaded(pluNumber), "Товар НЕ выгрузился из весов после загрузки ликонда, запрещающего продажу. PLU = " + pluNumber);
	}
	
	
	@Test (description = "SRTE-119. Весовой товар не загружается на весы, если загружен леконд запрещающий продажу товара (вчера)",
			/* тест не должен запускаться первый, т.к должен быть создан файл виртуальных весов*/
			groups = "loadUnload",
			priority = 1)
	public void testGoodNotLoadedIfLecondBanSales(){
		pluNumber++;
		weightGood = goodGenerator.generateWeightGood(String.valueOf(pluNumber));
		
		/* сгенерить и отправить леконд, запрещающий продажу */
		likondFrom = System.currentTimeMillis() - 3600 * 12 * 1000;
		likondTo = System.currentTimeMillis() - 3600 * 6 * 1000;
		
		likond.setBeginDate(goodGenerator.getDate(likondFrom));
		likond.setEndDate(goodGenerator.getDate(likondTo));
		likond.setMarking(weightGood.getErpCode());
		soapSender.sendLicond(likond);
		
		/* отправить товар*/
		soapSender.sendGood(weightGood);
		Assert.assertFalse(scales.waitPluLoaded(pluNumber),  "Товар не должен быть выгружен в весы, т.к ликонд, загруженый ранее запрещает продажу. PLU = " + pluNumber);
	}
	
	@Test (description = "SRTE-119. Весовой товар загружается на весы, если загружен новый леконд, разрешающий продажу товара"
			+ " (новый леконд отменяет действие леконда, загруженного прежде)",
			/* тест должен запускаться после указанного в  dependsOnMethods*/
			groups = "loadUnload",
			priority = 1,
			dependsOnMethods = "testGoodNotLoadedIfLecondBanSales")
	public void testGoodLoadedIfLecondAllowSalesAfterBan(){
		
		/* сгенерить и отправить леконд, разрешающий продажу товара, созданного в тесте testGoodNotLoadedIfLecondBanSales*/
		likondFrom = System.currentTimeMillis() - 3600 * 2 * 1000;
		likondTo = System.currentTimeMillis() + 3600 * 6 * 1000;
		
		likond.setBeginDate(goodGenerator.getDate(likondFrom));
		likond.setEndDate(goodGenerator.getDate(likondTo));
		likond.setMarking(weightGood.getErpCode());
		soapSender.sendLicond(likond);
		
		Assert.assertTrue(scales.waitPluLoaded(pluNumber), "Товар не выгружен на весы, после загрузки ликонда, разрешающего продажу товара. PLU = " + pluNumber);
	}
	
	@Test (description = "SRTE-119. Весовой товар выгружается на весы, если загружен леконд разрешающий продажу товара (со вчера до завтра)", 
			/* тест не должен запускаться первый, т.к должен быть создан файл виртуальных весов*/
			dependsOnGroups = "loadUnload",
			alwaysRun = true)
	public void testGoodLoadedIfLecondallowSales(){
		
		pluNumber++;
		weightGood = goodGenerator.generateWeightGood(String.valueOf(pluNumber));
		
		/* сгенерить и отправить леконд, разрешающий продажу */
		likondFrom = System.currentTimeMillis() - 3600 * 24 * 1000;
		likondTo = System.currentTimeMillis() + 3600 * 24 * 1000;
		
		likond.setBeginDate(goodGenerator.getDate(likondFrom));
		likond.setEndDate(goodGenerator.getDate(likondTo));
		likond.setMarking(weightGood.getErpCode());
		soapSender.sendLicond(likond);
		
		/* отправить товар*/
		soapSender.sendGood(weightGood);
		
		Assert.assertTrue(scales.waitPluLoaded(pluNumber),  "Товар не загрузился на весы, после загрузки ликонда, разрешающего продажу. PLU = " + pluNumber);
	}
	
	
	@Test (description = "SRTE-119. Весовой товар не выгружается на весы, если леконд разрешает продажу товара в будущем (через 2 часа)", 
			/* тест не должен запускаться первый, т.к должен быть создан файл виртуальных весов*/
			dependsOnGroups = "loadUnload",
			alwaysRun = true)
	public void testGoodNotLoadedIfLecondallowSalesInFuture(){

		pluNumber++;
		weightGood = goodGenerator.generateWeightGood(String.valueOf(pluNumber));
		
		/* сгенерить и отправить леконд, разрешающий продажу */
		likondFrom = System.currentTimeMillis() + 3600 * 24 * 1000;
		likondTo = System.currentTimeMillis() + 3600 * 48 * 1000;
		
		likond.setBeginDate(goodGenerator.getDate(likondFrom));
		likond.setEndDate(goodGenerator.getDate(likondTo));
		likond.setMarking(weightGood.getErpCode());
		soapSender.sendLicond(likond);
		
		/* отправить товар*/
		soapSender.sendGood(weightGood);
		
		Assert.assertFalse(scales.waitPluLoaded(pluNumber),  "Товар не загрузился на весы, после загрузки ликонда, разрешающего продажу. PLU = " + pluNumber);
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
