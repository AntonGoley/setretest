package ru.crystals.set10.test.weight;

import java.util.Iterator;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.utils.GoodGenerator;
import ru.crystals.set10.utils.SoapRequestSender;
import ru.crystals.setretailx.products.catalog.BarCode;
import ru.crystals.setretailx.products.catalog.Good;

@Test(groups = {"retail"})
public class WeightBaseTest extends WeightAbstractTest { 
	
	
	Good weightUnLoadBarcode;
	Good weightUnLoadDeleteCash;
	
	int plu_weightLoad = pluNumber++;
	int plu_weightUnLoad = pluNumber++;
	
	SoapRequestSender soapSender = new SoapRequestSender();
	GoodGenerator goodGenerator = new GoodGenerator();
	
	@BeforeClass
	public void initData(){
		soapSender.setSoapServiceIP(Config.RETAIL_HOST);
		
		weightUnLoadBarcode = goodGenerator.generateWeightGood(String.valueOf(plu_weightLoad));
		weightUnLoadDeleteCash = goodGenerator.generateWeightGood(String.valueOf(plu_weightUnLoad));
		
		soapSender.sendGood(weightUnLoadBarcode);
		soapSender.sendGood(weightUnLoadDeleteCash);
	}
	
	@Test (description = "Весовой товар загружается в весы")
	public void testGoodWeightLoadToScales(){
		
		Assert.assertTrue(scales.waitPluLoaded(plu_weightLoad) , "Товар не загрузился в весы. PLU = " + plu_weightLoad);
		Assert.assertTrue(scales.waitPluLoaded(plu_weightUnLoad) , "Товар не загрузился в весы. PLU = " + plu_weightUnLoad);
	}
	
	@Test (description = "Весовой товар выгружается из весов, если при импорте товара, у весового баркода поле do-not-send-to-scales=true",
			dependsOnMethods = "testGoodWeightLoadToScales")
	public void testGoodWeightUnloadFromScalesBarCode(){
		
		Iterator<BarCode>  it = weightUnLoadBarcode.getBarCodes().iterator();
		while (it.hasNext()){
			BarCode code = it.next();
			if (code.getCode().substring(0, 2).equals(Config.WEIGHT_BARCODE_PREFIX)) {
				code.setDeleted(true);
				break;
			}
		}
		
		soapSender.sendGood(weightUnLoadBarcode);
		Assert.assertTrue(scales.waitPluUnLoaded(plu_weightLoad) , "Товар не выгрузился из весов, если do-not-send-to-scales=true. PLU = " + plu_weightLoad);
	}
	
	@Test (description = "Весовой товар выгружается из весов, если у товара поле delete-from-cash=true",
			dependsOnMethods = "testGoodWeightLoadToScales")
	public void testGoodWeightUnloadFromScalesIfDeleteFromCache(){
		weightUnLoadDeleteCash.setDeleteFromCash(true);
		soapSender.sendGood(weightUnLoadDeleteCash);
		Assert.assertTrue(scales.waitPluUnLoaded(plu_weightUnLoad) , "Товар не выгрузился из весов, если delete-from-cash=true. PLU = " + plu_weightUnLoad);
	}
	
}
