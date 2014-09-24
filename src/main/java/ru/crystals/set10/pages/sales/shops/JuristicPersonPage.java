package ru.crystals.set10.pages.sales.shops;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.basic.AbstractPage;
import ru.crystals.set10.pages.basic.BasicElements;
import static ru.crystals.set10.pages.basic.BasicElements.*;
import static ru.crystals.set10.utils.FlexMediator.*;


public class JuristicPersonPage extends AbstractPage {
	
	static final String LOCATOR_INPUT_NAME = "juristicPersonName";
	static final String LOCATOR_INPUT_ADRESS = "physicalAddress";
	static final String LOCATOR_INPUT_PHONE = "telephoneNumber";
	
	static final String LOCATOR_INPUT_INN = "inn";
	static final String LOCATOR_INPUT_KPP = "kpp";
	static final String LOCATOR_INPUT_OKPO = "okpo";
	static final String LOCATOR_INPUT_OKDP = "okdp";
	static final String LOCATOR_BACK_BUTTON = "label=К настройкам магазина";
	
	public JuristicPersonPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));

	}
	
	public JuristicPersonPage setName(String name){
		typeText(getDriver(), ID_SALESSWF, LOCATOR_INPUT_NAME, name);
		return this;
	}
	
	public JuristicPersonPage setAdress(String adress){
		typeText(getDriver(), ID_SALESSWF, LOCATOR_INPUT_ADRESS, adress);
		return this;
	}
	
	public JuristicPersonPage setPhone(String phone){
		typeText(getDriver(), ID_SALESSWF, LOCATOR_INPUT_PHONE, phone);
		return this;
	}
	
	public JuristicPersonPage setINN(String inn){
		typeText(getDriver(), ID_SALESSWF, LOCATOR_INPUT_INN , inn);
		return this;
	}
	
	public JuristicPersonPage setKPP(String kpp){
		typeText(getDriver(), ID_SALESSWF, LOCATOR_INPUT_KPP, kpp);
		return this;
	}
	
	public JuristicPersonPage setOKPO(String okpo){
		typeText(getDriver(), ID_SALESSWF, LOCATOR_INPUT_OKPO, okpo);
		return this;
	}
	
	public JuristicPersonPage setOKDP(String okdp){
		typeText(getDriver(), ID_SALESSWF, LOCATOR_INPUT_OKDP, okdp);
		return this;
	}
	
	public ShopPreferencesPage goBack(){
		return BasicElements.goBack(getDriver(), ShopPreferencesPage.class, ID_SALESSWF, LOCATOR_BACK_BUTTON);
	}
}
