package ru.crystals.set10.test.weight;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.crystals.scales.tech.core.scales.virtual.xml.PluType;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.utils.GoodGenerator;
import ru.crystals.set10.utils.SoapRequestSender;
import ru.crystals.setretailx.products.catalog.Good;


@Test(groups = {"retail"})
public class WeightFieldsOnLabelTest extends WeightAbstractTest { 
	
	
	SoapRequestSender soapSender = new SoapRequestSender();
	GoodGenerator goodGenerator = new GoodGenerator();
	
	@BeforeClass
	public void initData(){
		soapSender.setSoapServiceIP(Config.RETAIL_HOST);
	}
	
	@Test (description = "SRL-739. Обновление типа сертификации в весах")
	public void testCertificationTypeUpdatedTest(){
		int pluNum = pluNumber++;
		int certificationTypeBefore = 1;
		int certificationTypeAfter = 2;
		PluType plu;
		
		Good weightGood = goodGenerator.generateWeightGood(String.valueOf(pluNum));
		weightGood.setCertificationType(certificationTypeBefore);
		soapSender.sendGood(weightGood);
		Assert.assertTrue(scales.waitPluLoaded(pluNum), "Товар не загрузился в весы. PLU = " + pluNum);

		plu = scales.getPlu(pluNum);
		Assert.assertEquals(plu.getCertificationType().intValue(), certificationTypeBefore, "Не загрузился тип сертификации. PLU = " + pluNum);
		
		/* меняем тип сертификации и проверяем, что он обновился в весах*/
		weightGood.setCertificationType(certificationTypeAfter);
		soapSender.sendGood(weightGood);
		plu = scales.getPluUpdated(plu);
		
		Assert.assertEquals(plu.getCertificationType().intValue(), certificationTypeAfter, "Тип сертификации не обновился в весах. PLU = " + pluNum); 
		
	}

	@Test (enabled = false, description = "SRTE-86. Обновление состава товара в весах") 
	public void test(){

	}
	
	@Test (enabled = false, description = "SRTE-86. Отображение срока годности в карточке товара") 
	public void test1(){

	}
	
}
