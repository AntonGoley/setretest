package ru.crystals.set10.pages.sales.externalsystems;

import org.openqa.selenium.WebDriver;
import ru.crystals.set10.pages.basic.SalesPage;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.utils.FlexMediator.*;

public class NewBankPage extends SalesPage {
	
	static final String BUTTON_BACK = "id:buttonBack";
	static final String LOCATOR_BANK_FILTER = "id:bankAddView/id:filter;className:FilterField";
	static final String LOCATOR_BANK_TABLE = "id:bankAddView/id:bankTable";
	static final String BUTTON_REGISTER_BANK= "label:Зарегистрировать новый банк";
	
	public NewBankPage(WebDriver driver) {
		super(driver);
	}
	
	public ExternalSystemsPage addBank(String processingName){
		typeText(getDriver(), ID_SALESSWF, LOCATOR_BANK_FILTER, processingName);
		DisinsectorTools.delay(1000);
		// ждем, пока количество найденных объектов = 1
		waitForProperty(getDriver(), ID_SALESSWF, LOCATOR_BANK_TABLE, new String[]{"length", "1"});
		// выбираем первый в списке (и единственный) объект
		doFlexProperty(getDriver(), ID_SALESSWF, LOCATOR_BANK_TABLE, new String[] {"selectedIndex", "0"});
		waitForProperty(getDriver(), ID_SALESSWF, BUTTON_REGISTER_BANK, new String[] {"enabled", "true"});
		clickElement(getDriver(), ID_SALESSWF, BUTTON_REGISTER_BANK);
		/* задержка, потому что не успевает сохраняться значение после добавления*/
		DisinsectorTools.delay(1000);
		return new ExternalSystemsPage(getDriver());
	}
}
