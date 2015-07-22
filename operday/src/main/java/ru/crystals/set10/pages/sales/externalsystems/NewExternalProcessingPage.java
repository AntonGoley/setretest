package ru.crystals.set10.pages.sales.externalsystems;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.pages.basic.SalesPage;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.utils.FlexMediator.*;

public class NewExternalProcessingPage extends SalesPage {
	
	static final String BUTTON_BACK = "id:buttonBack";
	static final String LOCATOR_PROCESSING_GROUP = "id:serviceType";
	static final String LOCATOR_PROCESSING = "id:service";
	
	static final String BUTTON_REGISTER = "label:Зарегистрировать нового оператора";
	
	public NewExternalProcessingPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	public ExternalSystemsPage addProcessing(String processingGroup, String processingName){
		doFlexMouseDown(getDriver(),  ID_SALESSWF, LOCATOR_PROCESSING_GROUP + "/className:Text;text:" + processingGroup);
		clickElement(getDriver(),  ID_SALESSWF, LOCATOR_PROCESSING_GROUP + "/className:Text;text:" + processingGroup);

		doFlexMouseDown(getDriver(),  ID_SALESSWF, LOCATOR_PROCESSING + "/className:Text;text:" + processingName);
		doFlexMouseUp(getDriver(),  ID_SALESSWF, LOCATOR_PROCESSING + "/className:Text;text:" + processingName, false);
		
		waitForProperty(getDriver(), ID_SALESSWF, BUTTON_REGISTER, new String[]{"enabled", "true"});
		clickElement(getDriver(), ID_SALESSWF, BUTTON_REGISTER);
		DisinsectorTools.delay(1000);
		return new ExternalSystemsPage(getDriver());
	}
	
	public ExternalSystemsPage goBack(){
		clickElement(getDriver(), ID_SALESSWF, BUTTON_REGISTER);
		return new ExternalSystemsPage(getDriver());
	}
}
