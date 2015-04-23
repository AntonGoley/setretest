package ru.crystals.set10.test.documents.accompanying;

import java.io.File;

import junit.framework.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.pages.operday.searchcheck.CheckContentPage.*;

@Test (groups={"centrum", "retail"})
public class AccompanyingGoodsCheckContentTest extends AccompanyingDocumentsBasicTest{
	
	String reportResult ="";
	
	@BeforeClass
	public void prepareData() {
		super.navigateToCheckSearchPage();
		DisinsectorTools.removeOldReport(chromeDownloadPath, PDF_GOODS_CHEQUE);
		checkContent = checkContent.generateReport(LINK_GOODS_CHECK);
		File file = DisinsectorTools.getDownloadedFile(chromeDownloadPath, PDF_GOODS_CHEQUE);
		reportResult = checkContent.getPDFContent(file);
	}	
	
	@DataProvider (name = "Товарный чек")
	private static Object[][] reportData(){
		return new Object[][] {
				/*
				 * TODO: добавить в тест:
				 * 	- проверку номера
				 * 	- inn магазина
				 */
				{"Отображаение названия отчета", "Товарный чек", true },
				{"Отображение шапки таблицы", "Штрих-кодПоз. Ед. изм В уп. Кол-во Цена СуммаНазвание", true },
				{"Содержит fullName товара, разрешенного к печати", allowPrintFullName, true },
				{"Не содержит товар, запрещенный к печати", denyPrintFullName, false },
				{"Сумма прописью ", denyPrintSumTotalInWords, true },
				{"Строка Итого", denyPrintSumTotal, true }
//				{"В документе содержится информация о продавце: графа ИНН", "ИНН " + Config.SHOP_INN, true }, 
//				{"В документе содержится информация о продавце: графа \"От кого\"", "От кого: " + Config.SHOP_NAME, true }
		};
	}
	
	@Test (description = "SRTE-36. Печать товарного чека. Проверка содержания документа",
			dataProvider = "Товарный чек")
	public void testGoodsCheckReport(String dataToCheck, String expectedValue, boolean condition){
		log.info(dataToCheck);
		String message = String.format("\"Печать товарного чека\": ошибка данных отчета: %s ", dataToCheck);
		Assert.assertTrue(message, 
				reportResult.contains(expectedValue) == condition );
	}
}
