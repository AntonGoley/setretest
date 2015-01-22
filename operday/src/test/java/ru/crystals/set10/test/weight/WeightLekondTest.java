package ru.crystals.set10.test.weight;

import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ru.crystals.set10.config.Config;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.SoapRequestSender;
import ru.crystals.set10.utils.VirtualScalesReader;


public class WeightLekondTest extends WeightAbstractTest { 
	
	private long day = 86400*100*24;
	private static final String LECOND_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	
	SoapRequestSender soapSender = new SoapRequestSender();


	@BeforeClass
	public void initData(){
		soapSender.setSoapServiceIP(Config.RETAIL_HOST);
	}
	
	@BeforeMethod
	public void clearScales(){
		scales.clearVScalesFileData();
	}
	
	@Test (description = "SRTE-119. Весовой товар выгружается из весов, если загружен леконд запрещающий продажу товара (вчера)")
	public void testGoodUnloadIfLecondBanSales(){
		/*
		 * Отправить товар и проверить, что он прогрузился в весы
		 */
		HashMap<String, String> weightGood = new HashMap<String, String>();
		weightGood = generateGoodData();
		weightGood = soapSender.sendGoods(DisinsectorTools.getFileContentAsString(WEIGHT_GOOD_FILE), weightGood);
		
		Assert.assertEquals(scales.getPluActionType(weightGood.get(PLU_NUMBER_PARAM)), 
				ACTION_TYPE_LOAD, "Товар 1 не выгружен из весов");
		
		long now = System.currentTimeMillis(); 
		HashMap<String, String> lecondData = new HashMap<String, String>();
		
		lecondData = generateLecondData(
				DisinsectorTools.getDate(LECOND_DATE_FORMAT, now - 2*day ), 
				DisinsectorTools.getDate(LECOND_DATE_FORMAT, now - day ), 
				weightGood);
		soapSender.sendGoods(DisinsectorTools.getFileContentAsString(WEIGHT_LECOND_FILE), lecondData);
		
		scales.clearVScalesFileData();
		
		Assert.assertEquals(scales.getPluActionType(weightGood.get(PLU_NUMBER_PARAM)), 
				ACTION_TYPE_CLEAR, "Товар 1 не выгружен из весов");
	}
	
	@Test (description = "SRTE-119. Весовой товар не загружается на весы, если загружен леконд запрещающий продажу товара (вчера)")
	public void testGoodNotLoadedIfLecondBanSales(){
		/*
		 * Сгенерить товар, который отправим после леконда
		 */
		HashMap<String, String> weightGood = new HashMap<String, String>();
		weightGood = generateGoodData();
		
		long now = System.currentTimeMillis(); 
		HashMap<String, String> lecondData = new HashMap<String, String>();
		lecondData = generateLecondData(
				DisinsectorTools.getDate(LECOND_DATE_FORMAT, now - 2*day ), 
				DisinsectorTools.getDate(LECOND_DATE_FORMAT, now - day ), 
				weightGood);
		/*
		 * Отправляем леконд, затем товар
		 */
		soapSender.sendGoods(DisinsectorTools.getFileContentAsString(WEIGHT_LECOND_FILE), lecondData);
		weightGood = soapSender.sendGoods(DisinsectorTools.getFileContentAsString(WEIGHT_GOOD_FILE), weightGood);

		/*
		 * Ждем минуту, потом проверяем, что файл весов не создался (т.е товар не выгрузился) 
		 */
		DisinsectorTools.delay(60000);
		Assert.assertTrue(scales.getExpectedFileStatus(VirtualScalesReader.FILE_DELETED_RESPONSE),  "Товар не должен быть выгружен в весы");
	}
	
	
	@Test (description = "SRTE-119. Весовой товар загружается на весы, если загружен новый леконд (который отменяет действие леконда, загруженного прежде), "
									+ "разрешающий продажу товара (с сегодня).")
	public void testGoodLoadedIfLecondAllowSalesAfterBan(){
		
		/*
		 * Сгенерить товар, который отправим после леконда
		 */
		HashMap<String, String> weightGood = new HashMap<String, String>();
		weightGood = generateGoodData();
		
		long now = System.currentTimeMillis(); 
		HashMap<String, String> lecondData = new HashMap<String, String>();
		lecondData = generateLecondData(
				DisinsectorTools.getDate(LECOND_DATE_FORMAT, now - 2*day ), 
				DisinsectorTools.getDate(LECOND_DATE_FORMAT, now - day ), 
				weightGood);
		/*
		 * Отправляем леконд, затем товар
		 */
		soapSender.sendGoods(DisinsectorTools.getFileContentAsString(WEIGHT_LECOND_FILE), lecondData);
		weightGood = soapSender.sendGoods(DisinsectorTools.getFileContentAsString(WEIGHT_GOOD_FILE), weightGood);
		
		DisinsectorTools.delay(60000);
		Assert.assertTrue(scales.getExpectedFileStatus(VirtualScalesReader.FILE_DELETED_RESPONSE),  "Товар не должен быть выгружен в весы");
		
		/*
		 * Генерим новый леконд, разрешающий продажу товара
		 */
		
		HashMap<String, String> newLecondData = new HashMap<String, String>();
		newLecondData = generateLecondData(
				DisinsectorTools.getDate(LECOND_DATE_FORMAT, now - day ), 
				DisinsectorTools.getDate(LECOND_DATE_FORMAT, now + day ), 
				weightGood);
		
		soapSender.sendGoods(DisinsectorTools.getFileContentAsString(WEIGHT_LECOND_FILE), newLecondData);
		
		Assert.assertEquals(scales.getPluActionType(weightGood.get(PLU_NUMBER_PARAM)), 
				ACTION_TYPE_LOAD, "Товар 1 выгружен в весы");

	}
	
	@Test (description = "SRTE-119. Весовой товар выгружается на весы, если загружен леконд разрешающий продажу товара (со вчера на сегодня)")
	public void testGoodLoadedIfLecondallowSales(){
		/*
		 * Сгенерить товар, который отправим после леконда
		 */
		HashMap<String, String> weightGood = new HashMap<String, String>();
		weightGood = generateGoodData();
		
		long now = System.currentTimeMillis(); 
		HashMap<String, String> lecondData = new HashMap<String, String>();
		lecondData = generateLecondData(
				DisinsectorTools.getDate(LECOND_DATE_FORMAT, now - day ), 
				DisinsectorTools.getDate(LECOND_DATE_FORMAT, now + day ), 
				weightGood);
		/*
		 * Отправляем леконд разрешающий продажу, затем товар
		 */
		soapSender.sendGoods(DisinsectorTools.getFileContentAsString(WEIGHT_LECOND_FILE), lecondData);
		weightGood = soapSender.sendGoods(DisinsectorTools.getFileContentAsString(WEIGHT_GOOD_FILE), weightGood);
		
		Assert.assertEquals(scales.getPluActionType(weightGood.get(PLU_NUMBER_PARAM)), 
				ACTION_TYPE_LOAD, "Товар 1 не выгружен в весы");
	}
	
}
