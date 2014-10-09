package ru.crystals.set10.test.configuration;


import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.*;
import ru.crystals.set10.pages.sales.shops.JuristicPersonPage;
import ru.crystals.set10.pages.sales.shops.ShopPage;
import ru.crystals.set10.pages.sales.shops.ShopPreferencesPage;
import ru.crystals.set10.pages.sales.topology.CityPage;
import ru.crystals.set10.pages.sales.topology.RegionPage;
import ru.crystals.set10.pages.sales.topology.TopologyPage;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.utils.DbAdapter;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.SoapRequestSender;
import static ru.crystals.set10.pages.basic.SalesPage.*;

public class SetTopologyTest extends AbstractTest{
	
	MainPage mainPage;
	TopologyPage topologyPage;
	SalesPage salesPage;
	RegionPage regionPage;
	CityPage cityPage;
	ShopPage shopPage;
	ShopPreferencesPage shopPreferences;
	JuristicPersonPage juristicPerson;
	DbAdapter dba = new DbAdapter();
	
	@Test (	priority = 1,
			groups = "Config",
			description = "Настроить топологию: создать регион, город, магазин, юридическое лицо, добавить кассы, создать кассира")
	public void setTopology() {
		DisinsectorTools.delay(3000);
		mainPage = new LoginPage(getDriver(), Config.CENTRUM_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		// таймаут для певрого запуска
		DisinsectorTools.delay(5000);
		salesPage = mainPage.openSales();
		addRegionAndCity();
		addShop(false);
		sendGoods();
		addCash();
	}
	
	@Test (	
			description = "Добавить права пользователю manager на центруме"
			)
	public void setUpPrevilegesCentrum(){
		// Добавить роль админа пользователю Config.MANAGER на центруме
		dba.updateDb(DbAdapter.DB_CENTRUM_SET , String.format("update users_server_user_users_server_user_role " + 
				"set roles_id = '10' " +
				"where serveruserentities_id = (select id from users_server_user where login = '%s')", Config.MANAGER));
		dba.updateDb(DbAdapter.DB_CENTRUM_SET, String.format("update users_server_user " +
				"set firstname = '%s', lastname='%s', middlename='%s' ", Config.MANAGER_NAME, Config.MANAGER_LASTNAME, Config.MANAGER_MIDDLENAME ));
	}	

	@Test (	
			description = "Добавить все роли пользователю manager на ретейле"
			 )
	public void setUpPrevilegesRetail(){
		//Добавить все роли на ритейле
		dba.updateDb(DbAdapter.DB_RETAIL_SET, "delete from users_server_user_users_server_user_role");
		
		for (int i=1; i<=7; i++) {
			dba.updateDb(DbAdapter.DB_RETAIL_SET, String.format("INSERT INTO users_server_user_users_server_user_role(serveruserentities_id, roles_id)" +
										" VALUES (%s, %s)", 1, i));
		}
		
		dba.updateDb(DbAdapter.DB_RETAIL_SET, String.format("update users_server_user " +
				"set firstname = '%s', lastname='%s', middlename='%s' ", Config.MANAGER_NAME, Config.MANAGER_LASTNAME, Config.MANAGER_MIDDLENAME ));
	}
	
	private void addRegionAndCity() {
		topologyPage = salesPage.navigateMenu(SALES_MENU_TOPOLOGY, "1", TopologyPage.class);
		regionPage = topologyPage.addRegion().setRegionName("TestRegion");
		cityPage = regionPage.addCity();
		cityPage.setCityName("TestCity");
		topologyPage = cityPage.goBack().goBack();
	}
	
	private void addShop(boolean shopUseOwnServer) {
		salesPage = new SalesPage(getDriver());
		shopPage = salesPage.navigateMenu(SALES_MENU_SHOPS, "0", ShopPage.class);
		shopPreferences = shopPage.addShop();
		shopPreferences.setName("Shop" + Config.SHOP_NUMBER).
						setShopNumber(Config.SHOP_NUMBER).
						ifShopUseOwnServer(shopUseOwnServer);
		juristicPerson = shopPreferences.addJuristicPerson();
		addJuristicPerson();
		shopPage = shopPreferences.goBack();
	}
	
	private void addJuristicPerson(){
		juristicPerson.setName(Config.SHOP_NAME)
						.setAdress(Config.SHOP_ADRESS)
						.setPhone(Config.SHOP_PHONE)
						.setINN(Config.SHOP_INN)
						.setKPP(Config.SHOP_KPP)
						.setOKPO(Config.SHOP_OKPO)
						.setOKDP(Config.SHOP_OKDP);
		juristicPerson.goBack();
						
	}
	
	private void addCash(){
		getDriver().navigate().refresh();
		salesPage = new SalesPage(getDriver());
		shopPage = salesPage.navigateMenu(SALES_MENU_SHOPS, "0", ShopPage.class);
		shopPreferences = shopPage.openShopPreferences(Config.SHOP_NAME);
		shopPreferences.addCashes(5);
	}
	
	private void sendGoods(){
		SoapRequestSender soapSender  = new SoapRequestSender();
		soapSender.sendGoodsToStartTesting(Config.CENTRUM_HOST, "goods.txt");
	}
}
