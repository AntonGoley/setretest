package ru.crystals.set10.test.documents.accompanying;

import java.io.File;

import junit.framework.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.pages.operday.searchcheck.CheckContentPage.*;

@Test (groups={"centrum", "retail"})
public class AccompanyingNomenclatureContentTest extends AccompanyingDocumentsBasicTest{
	
	String reportResult ="";
	
	@BeforeClass
	public void prepareData() {
		super.navigateToCheckSearchPage();
		DisinsectorTools.removeOldReport(chromeDownloadPath, PDF_GOODS_NOMENCLATURE);
		checkContent = checkContent.generateReport(LINK_NOMENCLATURE);
		File file = DisinsectorTools.getDownloadedFile(chromeDownloadPath, PDF_GOODS_NOMENCLATURE);
		reportResult = checkContent.getPDFFileContent(file);
	}	
	
	@DataProvider (name = "Номенклатура чека")
	private static Object[][] reportData(){
		return new Object[][] {
				/*
				 * TODO: добавить:
				 * 	- проверку номера
				 * 	- 
				 */
				{"Отображаение названия отчета", "Номенклатура кассового чека", true },
				{"Отображение шапки таблицы ", "Штрих-кодПоз. Ед. изм В уп. Кол-во Цена СуммаНазвание", true },
				{"Содержит fullName товара, разрешенного к печати", allowPrintFullName, true },
				{"Содержит fullName товара, запрещенного к печати", denyPrintFullName, true },
				{"Сумма прописью ", allowPrintSumTotalInWords, true },
				{"Строка Итого ", allowPrintSumTotal, true }
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
