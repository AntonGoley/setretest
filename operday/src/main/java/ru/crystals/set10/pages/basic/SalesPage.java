package ru.crystals.set10.pages.basic;

import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.utils.FlexMediator.*;


public class SalesPage extends AbstractPage {
	
	protected static final String ID_SALESSWF = "Sales";
	static final String LOCATOR_MENUITEM = "id:topList";

	public static final String MENU_ELEMENT_LOCATOR = "className:ElementMenuRenderer|";

	
	public SalesPage(WebDriver driver) {
		super(driver);
		//switchWindow(true);
		isSWFReady();
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	public <T> T navigateMenu(int menuItemPosition, Class<T> page){
		//TODO: убрать, когда будет нормальный выбор из меню
		DisinsectorTools.delay(1000);
		// + 1 из-за hiddenObject, размещенного в начале
		doFlexMouseDown(getDriver(), ID_SALESSWF, MENU_ELEMENT_LOCATOR + Integer.toString(menuItemPosition + 1));
		clickElement(getDriver(), ID_SALESSWF, MENU_ELEMENT_LOCATOR + Integer.toString(menuItemPosition + 1));
		log.info("Ожидание спинера..");
		waitForProperty(getDriver(), ID_SALESSWF, SPINNER, new String[] {"isRunning", "false"});
		DisinsectorTools.delay(1000);
		return PageFactory.initElements(getDriver(), page);
	}
	
}
