package ru.crystals.set10.test.weight;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.utils.GoodGenerator;
import ru.crystals.set10.utils.SoapRequestSender;
import ru.crystals.setretailx.products.catalog.BarCode;
import ru.crystals.setretailx.products.catalog.Good;
import static ru.crystals.set10.utils.GoodGenerator.GOODTYPE_WEIGHT;

@Test(groups = {"retail"})
public class WeightAutoPLUGenerationTest extends WeightAbstractTest { 
	
	
	SoapRequestSender soapSender = new SoapRequestSender();
	GoodGenerator goodGenerator = new GoodGenerator();
	
	/* Смещения в ЛЕНТЕ:
	 * Штрих код 2300003 PLU = 300003
	 * Штрих код 2400004 PLU = 100004
	 * Штрих код 2700005 PLU = 700005
	 * Штрих код 2800006 PLU = 6
	 */
	
	/*
	 * Генерация штрихкодов происходит в случае, если у товара свойство plu-number = 0 (или не указано плагинное свойство plu-number)
	 * и в настройке весового товара установлен способ генерации PLU -  "приходит из внешней системы и по штриховому коду" ("на основе штрихового кода")
	 */
	@BeforeClass
	public void initData(){
		soapSender.setSoapServiceIP(Config.RETAIL_HOST);
	}
	
	@Test (description = "SRTE-153. Формирование PLU для весового товара с учетом префикса. Генерация PLU со смещением")
	public void testBarCodeGenerationWithOfset(){
		Good weightGood;
		int autoGenPLU = 0;
		
		weightGood = goodGenerator.generateWeightGoodWithNoBarCode("0");
		BarCode weightCode = goodGenerator.generateWeightBarCode(Config.WEIGHT_BARCODEGENERATION_PREFIX, 7);
		
		/* добавляем баркод с префиксом Config.WEIGHT_BARCODEGENERATION_PREFIX*/
		weightGood.getBarCodes().add(weightCode);
		
		autoGenPLU =  goodGenerator.getWeightAutoGenPlu(weightCode, Config.WEIGHT_BARCODEGENERATION_OFSET);
		
		soapSender.sendGood(weightGood);
		Assert.assertTrue(scales.waitPluLoaded(autoGenPLU) , "Товар не загрузился в весы. PLU =  " + autoGenPLU);
	}
	
	@Test (description = "SRTE-153. Если не приходит поле \"plu-number\" из ERP, происходит автогенерация PLU с учетом смещения.")
	public void testBarCodeGenerationIfNoPluFromERP(){
		Good weightGood;
		int autoGenPLU = 0;
		
		/* сгенерить весовой товар без плагинного свойства plu-number**/
		weightGood = goodGenerator.generateGood(GOODTYPE_WEIGHT);
		BarCode weightCode = goodGenerator.generateWeightBarCode(Config.WEIGHT_BARCODEGENERATION_PREFIX, 7);
		weightGood.getBarCodes().add(weightCode);
		
		autoGenPLU = goodGenerator.getWeightAutoGenPlu(weightCode, Config.WEIGHT_BARCODEGENERATION_OFSET);
		
		soapSender.sendGood(weightGood);
		Assert.assertTrue(scales.waitPluLoaded(autoGenPLU) , "Товар не загрузился в весы. PLU =  " + autoGenPLU);
		
	}
	
	@Test (description = "SRTE-153. Формирование PLU для весового товара с учетом префикса. Генерация PLU без смещения")
	public void testBarCodeGenerationWithoutOfset(){
		Good weightGood;
		int autoGenPLU = 0;
		
		weightGood = goodGenerator.generateWeightGoodWithNoBarCode("0");
		BarCode weightCode = goodGenerator.generateWeightBarCode(Config.WEIGHT_BARCODE_PREFIX, 7);
		weightGood.getBarCodes().add(weightCode);
		
		/*в баркоде отбрасываем префикс; бар код не должен начинаться с 0*/
		autoGenPLU = Integer.valueOf(weightCode.getCode().substring(2).replaceFirst("^0+", ""));
		
		soapSender.sendGood(weightGood);
		
		Assert.assertTrue(scales.waitPluLoaded(autoGenPLU) , "Товар не загрузился в весы. PLU =  " + autoGenPLU);
	}
}
