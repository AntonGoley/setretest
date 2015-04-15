package ru.crystals.set10.test.documents.accompanying;

import java.io.File;

import junit.framework.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.pages.operday.searchcheck.CheckContentPage.*;

@Test (groups={"centrum", "retail"})
public class AccompanyingInvoiceContentTest extends AccompanyingDocumentsBasicTest{
	
	String reportResult ="";
	
	@BeforeClass
	public void prepareData() {
		super.navigateToCheckSearchPage();
		DisinsectorTools.removeOldReport(chromeDownloadPath, PDF_GOODS_INVOICE);
		checkContent = checkContent.generateReportWithCounterpart(
				LINK_INVOICE, 
				counterpartName, 
				counterpartInn, 
				counterpartKpp, 
				counterpartAdress);
		File file = DisinsectorTools.getDownloadedFile(chromeDownloadPath, PDF_GOODS_INVOICE);
		reportResult = checkContent.getPDFContent(file, 1) + checkContent.getPDFContent(file, 2);
	}	
	
	@DataProvider (name = "Товарная накладная")
	private static Object[][] reportData(){
		return new Object[][] {
				/*
				 * TODO: добавить в тест:
				 * 	- проверку номера
				 * 	- 
				 */
				{"Отображаение названия отчета", "ТОВАРНАЯ НАКЛАДНАЯ", true },
//				{"Корректно отображается шапка таблицы", "Поз. Штрих-код Название Ед. изм В уп. Кол-во Цена Сумма", true },
				//TODO: разобраться с транспортом юридического лица в магазин
//				{"В документе содержится информация (адрес) Грузопоотправителя", shopJuristicAdress, true },
				{"ИНН Грузополучателя ", "ИНН " + counterpartInn, true }, 
				{"КПП Грузополучателя ", "КПП " + counterpartKpp, true },
				{"Грузополучатель и его адрес ", counterpartName+ ", " + counterpartAdress, true },
				{"Содержит имя товара, разрешенного к печати", allowPrintName, true },
				{"Не содержит товар, запрещенный к печати", denyPrintName, false },
				{"Строка \"Всего по накладной\"", denyPrintSumTotalInvoice, true },
		};
	}
	
	@Test (description = "SRTE-37. Печать товарной накладной. Проверка содержания документа", 
			dataProvider = "Товарная накладная")
	public void testInvoiceReport(String dataToCheck, String expectedValue, boolean condition){
		log.info(dataToCheck);
		String message = String.format("\"Товарная накладная\". Отсутсвуют или некорректно отображаются данные: %s ", dataToCheck);
		Assert.assertTrue(message, 
				reportResult.contains(expectedValue) == condition );
		reportResult.replaceFirst(expectedValue, "");
	}

}
