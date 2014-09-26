package ru.crystals.set10.test.documents.accompanying;

import junit.framework.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static ru.crystals.set10.pages.operday.searchcheck.CheckContentPage.*;


public class AccompanyingInvoiceContentTest extends AccompanyingDocumentsAbstractTest{
	
	String reportResult ="";
	
	@BeforeClass
	public void prepareData() {
		super.navigateToCheckSearchPage();
		reportResult = checkContent.generateReportWithCounterpart(LINK_INVOICE, counterpartName, counterpartInn, counterpartKpp, counterpartAdress);
	}	
	
	@DataProvider (name = "Товарная накладная")
	private static Object[][] reportData(){
		return new Object[][] {
				/*
				 * TODO: добавить:
				 * 	- проверку номера
				 * 	- 
				 */
//				{"Корректно отображается шапка таблицы", "Поз. Штрих-код Название Ед. изм В уп. Кол-во Цена Сумма", true },
				//TODO: разобраться с транспортом юридического лица в магазин
//				{"В документе содержится информация (адрес) Грузопоотправителя", shopJuristicAdress, true },
				{"В документе содержится ИНН Грузополучателя ", "ИНН " + counterpartInn, true }, 
				{"В документе содержится КПП Грузополучателя ", "КПП " + counterpartKpp, true },
				{"В документе содержится Грузополучатель и его адрес ", counterpartName+ ", " + counterpartAdress, true },
				{"Содержит имя товара, разрешенного к печати", allowPrintName, true },
				{"Не содержит товар, запрещенный к печати", denyPrintName, false },
				{"Строка \"Всего по накладной\" расчитана верно", denyPrintSumTotalInvoice, true },
		};
	}
	
	@Test (description = "SRTE-35. Печать товарной накладной. Проверка содержания документа", 
			dataProvider = "Товарная накладная")
	public void testNomenclatureCheckReport(String dataToCheck, String expectedValue, boolean condition){
		log.info(dataToCheck);
		String message = String.format("\"Товарная накладная\". Отсутсвуют или некорректно отображаются данные: %s ", dataToCheck);
		Assert.assertTrue(message, 
				reportResult.contains(expectedValue) == condition );
		reportResult.replaceFirst(expectedValue, "");
	}
	
}
