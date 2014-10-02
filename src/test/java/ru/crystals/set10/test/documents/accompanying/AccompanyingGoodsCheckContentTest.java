package ru.crystals.set10.test.documents.accompanying;

import junit.framework.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static ru.crystals.set10.pages.operday.searchcheck.CheckContentPage.*;


public class AccompanyingGoodsCheckContentTest extends AccompanyingDocumentsAbstractTest{
	
	String reportResult ="";
	
	@BeforeClass
	public void prepareData() {
		super.navigateToCheckSearchPage();
		reportResult = checkContent.generateReport(LINK_GOODS_CHECK);
	}	
	
	@DataProvider (name = "Товарный чек")
	private static Object[][] reportData(){
		return new Object[][] {
				/*
				 * TODO: добавить:
				 * 	- проверку номера
				 * 	- inn магазина
				 */
				{"Корректно отображается шапка таблицы", "Поз. Штрих-код Название Ед. изм В уп. Кол-во Цена Сумма", true },
				{"Содержит fullName товара, разрешенного к печати", allowPrintFullName, true },
				{"Не содержит товар, запрещенный к печати", denyPrintFullName, false },
				{"Сумма прописью указана верно", denyPrintSumTotalInWords, true },
				{"Строка Итого расчитана верно", denyPrintSumTotal, true }
//				{"В документе содержится информация о продавце: графа ИНН", "ИНН " + Config.SHOP_INN, true }, 
//				{"В документе содержится информация о продавце: графа \"От кого\"", "От кого: " + Config.SHOP_NAME, true }
		};
	}
	
	@Test (description = "SRTE-36. Печать товарного чека. Проверка содержания документа",
			dataProvider = "Товарный чек", 
			dependsOnGroups = "SMOKE_accompanying")
	public void testGoodsCheckReport(String dataToCheck, String expectedValue, boolean condition){
		log.info(dataToCheck);
		String message = String.format("\"Печать товарного чека\": ошибка данных отчета: %s ", dataToCheck);
		Assert.assertTrue(message, 
				reportResult.contains(expectedValue) == condition );
	}
	
	@Test (description = "SRTE-36. Печать товарного чека. Документ выводится на печать и содержит верный заголовок", 
			groups = "SMOKE_accompanying")
	public void testGoodsCheckReport(){
		Assert.assertTrue("Не выводится название отчета \"Товарный чек\"", 
				reportResult.contains("Товарный чек"));
	}
	
}
