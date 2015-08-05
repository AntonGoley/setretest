package ru.crystals.set10.pages.sales.shops;


import org.openqa.selenium.WebDriver;
import ru.crystals.set10.pages.basic.SalesPage;
import static ru.crystals.set10.utils.FlexMediator.*;

public class JuristicPersonPage extends SalesPage {
	
	static final String LOCATOR_INPUT_NAME = "id:juristicPersonName";
	static final String LOCATOR_INPUT_ADRESS = "id:physicalAddress";
	static final String LOCATOR_INPUT_PHONE = "id:telephoneNumber";
	
	static final String LOCATOR_INPUT_INN = "id:inn";
	static final String LOCATOR_INPUT_KPP = "id:kpp";
	static final String LOCATOR_INPUT_OKPO = "id:okpo";
	static final String LOCATOR_INPUT_OKDP = "id:okdp";
	static final String LOCATOR_BACK_BUTTON = "label:К настройкам магазина";
	
	public JuristicPersonPage(WebDriver driver) {
		super(driver);
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
		return goBack(getDriver(), ShopPreferencesPage.class, ID_SALESSWF, LOCATOR_BACK_BUTTON);
	}
}
