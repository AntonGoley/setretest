package ru.crystals.set10.test.maincash;


import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.operday.cashes.MainCashDoc;
import ru.crystals.set10.pages.operday.cashes.MainCashManualDocPage;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.pages.operday.cashes.MainCashManualDocPage.*;


/*
 * Общие тесты ПКО/РКО
 */
@Test (groups= "retail")
public class MainCashPKOandRKOCommonTest extends MainCashConfigTest {
	
	MainCashManualDocPage manualDoc;

	String personReceived = "Вручалова Г.Г";
	
	
	@DataProvider (name = "Documents")
	private Object[][] setUpData(){
		return new Object[][]{
				{MainCashDoc.DOC_TYPE_PKO_EXCHANGE_INCOME},
				{MainCashDoc.DOC_TYPE_RKO_EXCHANGE_WITHDRAWAL},
		};
	}
	
	@BeforeClass
	public void setup(){
		openMainDocsPage();
	}
	
	@Test(description = "SRTE-175. Автомтическое заполнение полей документов ПКО/РКО при создании (автор, дата и время создания, номер документа)",
			dataProvider = "Documents")
	public void testRKOAutoGeneratedFields(String doctype){
		manualDoc = docs.addDoc();
		String date_and_time_create = manualDoc.getAutogeneratedFieldValue(FIELD_DATE_CREATE);
		String author = manualDoc.getAutogeneratedFieldValue(FIELD_AUTHOR);
		String do_number = manualDoc.getAutogeneratedFieldValue(FIELD_DOC_NUMBER);
		
		Assert.assertEquals(date_and_time_create, "-", "Начальное значение поля " + FIELD_DATE_CREATE + " не равно -" );
		Assert.assertEquals(author, "-", "Начальное значение поля " + FIELD_AUTHOR + " не равно -" );
		Assert.assertEquals(do_number, "-", "Начальное значение поля " + FIELD_DOC_NUMBER + " не равно -" );
		
		manualDoc = manualDoc.selectDocType(doctype)
				.setTextField(FIELD_DOC_SUM, DisinsectorTools.randomMoney(1000, ","))
				.saveChanges();
		
		Pattern pattern = Pattern.compile("(\\d+):[0-5][0-9]:[0-5][0-9]$");
		Matcher matcher = pattern.matcher(manualDoc.getAutogeneratedFieldValue(FIELD_DATE_CREATE));
		
		Assert.assertTrue(matcher.find(),  "Поле " + FIELD_DATE_CREATE + " не содержит время создания!" );
		Assert.assertTrue(manualDoc.getAutogeneratedFieldValue(FIELD_DATE_CREATE).contains(DisinsectorTools.getDate("dd.MM.yyyy", new Date().getTime())),  "Поле " + FIELD_DATE_CREATE + " не содержит дату создания!" );
		Assert.assertTrue(manualDoc.getAutogeneratedFieldValue(FIELD_AUTHOR).contains(Config.MANAGER_LASTNAME), "Не заполнилось поле Автор!" );
		Assert.assertTrue(manualDoc.getAutogeneratedFieldValue(FIELD_DOC_NUMBER).matches("(\\d+)"), "Не заполнилось поле  " + FIELD_DOC_NUMBER );
		
		manualDoc.backToMainCash();
	}
	
}
