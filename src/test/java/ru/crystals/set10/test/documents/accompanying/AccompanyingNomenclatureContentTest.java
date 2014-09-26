package ru.crystals.set10.test.documents.accompanying;

import junit.framework.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ru.crystals.set10.config.Config;
import static ru.crystals.set10.pages.operday.searchcheck.CheckContentPage.*;


public class AccompanyingNomenclatureContentTest extends AccompanyingDocumentsAbstractTest{
	
	String reportResult ="";
	
	@BeforeClass
	public void prepareData() {
		super.navigateToCheckSearchPage();
		reportResult = checkContent.generateReport(LINK_NOMENCLATURE);
	}	
	
	@DataProvider (name = "Номенклатура чека")
	private static Object[][] reportData(){
		return new Object[][] {
				/*
				 * TODO: добавить:
				 * 	- проверку номера
				 * 	- 
				 */
				{"Корректно отображается шапка таблицы", "Поз. Штрих-код Название Ед. изм В уп. Кол-во Цена Сумма", true },
				{"Содержит fullName товара, разрешенного к печати", allowPrintFullName, true },
				{"Содержит fullName товара, запрещенного к печати", denyPrintFullName, true },
				{"Сумма прописью указана верно", allowPrintSumTotalInWords, true },
				{"Строка Итого расчитана верно", allowPrintSumTotal, true }
//				{"В документе содержится информация о продавце: графа ИНН", "ИНН " + Config.SHOP_INN, true }, 
//				{"В документе содержится информация о продавце: графа \"От кого\"", "От кого: " + Config.SHOP_NAME, true }
		};
	}
	
	@Test (description = "SRTE-35. Печать номенклатуры кассового чека. Проверка содержания документа", 
			dataProvider = "Номенклатура чека")
	public void testNomenclatureCheckReport(String dataToCheck, String expectedValue, boolean condition){
		log.info(dataToCheck);
		String message = String.format("\"Номенклатура кассового чека\": ошибка данных отчета: %s ", dataToCheck);
		Assert.assertTrue(message, 
				reportResult.contains(expectedValue) == condition );
	}
	
}
