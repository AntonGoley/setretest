package ru.crystals.set10.pages.sales.externalsystems;


import org.openqa.selenium.WebDriver;
import ru.crystals.set10.pages.basic.SalesPage;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.utils.FlexMediator.*;

public class NewERPPage extends SalesPage {
	
	static final String BUTTON_BACK = "id:buttonBack";
	static final String BUTTON_REGISTER_ERP= "label:Зарегистрировать новую ERP";
	static final String LOCATOR_ERP_FILTER = "id:addEpr/className:FilterField";
	static final String LOCATOR_ERP_TABLE = "id:addEpr/id:table";
	
	public NewERPPage(WebDriver driver) {
		super(driver);
	}
	
	public ExternalSystemsPage addERP(String erpName){
		typeText(getDriver(), ID_SALESSWF, LOCATOR_ERP_FILTER, erpName);
		DisinsectorTools.delay(1000);
		// ждем, пока количество найденных объектов = 1
		waitForProperty(getDriver(), ID_SALESSWF, LOCATOR_ERP_TABLE, new String[]{"realDataLength", "1"});
		// выбираем первый в списке (и единственный) объект
		doFlexProperty(getDriver(), ID_SALESSWF, LOCATOR_ERP_TABLE, new String[] {"selectedIndex", "0"});
		waitForProperty(getDriver(), ID_SALESSWF, BUTTON_REGISTER_ERP, new String[] {"enabled", "true"});
		clickElement(getDriver(), ID_SALESSWF, BUTTON_REGISTER_ERP);
		/* задержка, потому что не успевает сохраняться значение после добавления*/
		DisinsectorTools.delay(1000);
		return new ExternalSystemsPage(getDriver());
	}
}
