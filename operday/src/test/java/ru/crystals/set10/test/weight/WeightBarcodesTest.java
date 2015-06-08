package ru.crystals.set10.test.weight;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ru.crystals.set10.config.Config;
import ru.crystals.set10.utils.GoodGenerator;
import ru.crystals.set10.utils.SoapRequestSender;
import ru.crystals.setretailx.products.catalog.BarCode;
import ru.crystals.setretailx.products.catalog.BarcodeExt;
import ru.crystals.setretailx.products.catalog.Good;

@Test(groups = {"retail"})
public class WeightBarcodesTest extends WeightAbstractTest { 
	
	protected static final Logger log = Logger.getLogger(WeightBarcodesTest.class);
	
	SoapRequestSender soapSender = new SoapRequestSender();
	GoodGenerator goodGenerator = new GoodGenerator();
	
	Good weightGood;
	//баркод, который выгрузим
	BarCode barCode1; 
	//баркод должен остаться в весах
	BarCode barCode2; 
	// plu, которые должны сгенериться на основе кодов
	int plu1;  
	int plu2;  
	
	@BeforeClass
	public void initData(){
		soapSender.setSoapServiceIP(Config.RETAIL_HOST);
	}
	
	@Test (description = "Для каждого из двух весовых баркодов, пришедших из ERP при импорте товара, генерится свой PLU. ")
	public void testGeneratePLUForBarcodes(){
		
		/*Сгеренить товар с двумя весовыми баркодами */
		weightGood = goodGenerator.generateWeightGoodWithNoBarCode("0");
		barCode1 = goodGenerator.generateWeightBarCode(Config.WEIGHT_BARCODEGENERATION_PREFIX, 7);
		barCode2 = goodGenerator.generateWeightBarCode(Config.WEIGHT_BARCODEGENERATION_PREFIX, 7);
		
		weightGood.getBarCodes().add(barCode1);
		weightGood.getBarCodes().add(barCode2);
		
		/* устанавливаем, какие PLU должны сгенериться*/
		plu1 = goodGenerator.getWeightAutoGenPlu(barCode1, Config.WEIGHT_BARCODEGENERATION_OFSET);
		plu2 = goodGenerator.getWeightAutoGenPlu(barCode2, Config.WEIGHT_BARCODEGENERATION_OFSET);
		
		soapSender.sendGood(weightGood);
		
		Assert.assertTrue(scales.waitPluLoaded(plu1), "Товар не загрузился в весы. PLU = ");
		Assert.assertTrue(scales.waitPluLoaded(plu2), "Товар не загрузился в весы. PLU = ");
	}
	
	@Test (description = "Товар имеет 2 баркода. При прогрузке из ERP 2 баркодов, из весов удалится только тот баркод, который помечен как удаленный (deleted = true).",
			dependsOnMethods = "testGeneratePLUForBarcodes" )
	public void testWeightDeleteBarcode(){
		/* Отправить удаление одного весового баркода*/
		BarcodeExt barcode = new BarcodeExt();
		barcode.setMarking(weightGood.getMarkingOfTheGood());
		barcode.setDeleted(true);
		barcode.setCode(barCode1.getCode());
		barcode.setDefaultCode(false);
		
		soapSender.sendBarcode(barcode);
		
		Assert.assertTrue(scales.waitPluUnLoaded(plu1), "Удаленный баркод не выгрузился из весов. PLU = ");
		Assert.assertTrue(scales.waitPluLoaded(plu2), "Баркод (неудаленный) выгрузился из весов. PLU = ");

	}
}
