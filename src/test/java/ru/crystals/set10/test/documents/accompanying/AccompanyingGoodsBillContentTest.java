package ru.crystals.set10.test.documents.accompanying;

import junit.framework.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static ru.crystals.set10.pages.operday.searchcheck.CheckContentPage.*;


public class AccompanyingGoodsBillContentTest extends AccompanyingDocumentsBasicTest{
	
	String reportResult ="";
	
	@BeforeClass
	public void prepareData() {
		super.navigateToCheckSearchPage();
		reportResult = checkContent.generateReportWithCounterpart(LINK_GOODS_BILL, counterpartName, counterpartInn, counterpartKpp, counterpartAdress);
	}	
	
	@DataProvider (name = "Счет-фактура")
	private static Object[][] reportData(){
		return new Object[][] {
				/*
				 * TODO: добавить:
				 * 	- проверку номера документа
				 * 	- данные отправителя
				 */
//				{"Корректно отображается шапка таблицы", "Поз. Штрих-код Название Ед. изм В уп. Кол-во Цена Сумма", true },
				{"Содержит имя товара, разрешенного к печати", allowPrintName, true },
				{"Не содержит товар, запрещенный к печати", denyPrintName, false },
				{"Строка \"Всего к оплате\" расчитана верно", denyPrintSumTotalGoodsBill, true },
				{"В документе содержится Грузополучатель и его адрес ", counterpartName+ ", " + counterpartAdress, true },
				//TODO: разобраться с транспортом юридического лица в магазин
//				{"В документе содержится Грузопоотправитель и его адрес ", shopJuristicName + ", " + shopJuristicAdress, true },
//				{"В документе содержится ИНН/КПП продавца ", shopJuristicINN + "/" + shopJuristicKPP, true }, 
				{"В документе содержится ИНН/КПП покупателя ", counterpartInn + "/" + counterpartKpp, true }, 
				{"В документе содержится адресс покупателя ", counterpartAdress, true }
		};
	}
	
	@Test (description = "SRTE-38. Печать счета-фактуры. Проверка содержания документа",
			dataProvider = "Счет-фактура", 
			dependsOnGroups = "SMOKE_accompanying")
	public void testGoodsCheckReport(String dataToCheck, String expectedValue, boolean condition){
		log.info(dataToCheck);
		String message = String.format("\"Печать счета-фактуры\". Отсутсвуют или некорректно отображаются данные: %s ", dataToCheck);
		Assert.assertTrue(message, 
				reportResult.contains(expectedValue) == condition );
	}
	
	@Test (description = "SRTE-38. Печать счет-фактуры. Документ выводится на печать и содержит верный заголовок", 
			groups = "SMOKE_accompanying")
	public void testGoodsBillReport(){
		Assert.assertTrue("Не выводится название отчета \"Счет-фактура\"", 
				reportResult.contains("СЧЕТ-ФАКТУРА"));
	}
	
}
