package ru.crystals.set10.pages.basic;

import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.utils.FlexMediator.*;


public class SalesPage extends AbstractPage{
	
	protected static final String ID_SALESSWF = "Sales";
	static final String LOCATOR_MENUITEM = "topList";

	public static final String MENU_ELEMENT_LOCATOR = "className:ElementMenuRenderer|";

	
	public SalesPage(WebDriver driver) {
		super(driver);
		//switchWindow(true);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	public <T> T navigateMenu(int menuItemPosition, Class<T> page){
		//TODO: убрать, когда будет нормальный выбор из меню
		DisinsectorTools.delay(1000);
		// + 1 из-за hiddenObject, размещенного в начале
		//Sales0.mainDisplay.mainContainer.stack.listView.HBox48.leftMenu.menuStack.vs_work.menu.content.topList.ListBaseContentHolder98.hiddenItem
		doFlexMouseDown(getDriver(), ID_SALESSWF, MENU_ELEMENT_LOCATOR + Integer.toString(menuItemPosition + 1));
		clickElement(getDriver(), ID_SALESSWF, MENU_ELEMENT_LOCATOR + Integer.toString(menuItemPosition + 1));
		DisinsectorTools.delay(1000);
		return PageFactory.initElements(getDriver(), page);
	}
	
}
