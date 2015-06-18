 package ru.crystals.set10.test.configuration;


import org.testng.ITestResult;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.*;
import ru.crystals.set10.pages.sales.cashiers.CashiersMainPage;
import ru.crystals.set10.pages.sales.cashiers.CashierConfigPage;
import ru.crystals.set10.pages.sales.equipment.EquipmentPage;
import ru.crystals.set10.pages.sales.equipment.NewEquipmentPage;
import ru.crystals.set10.pages.sales.externalsystems.ExternalSystemsPage;
import ru.crystals.set10.pages.sales.externalsystems.NewBankPage;
import ru.crystals.set10.pages.sales.externalsystems.NewERPPage;
import ru.crystals.set10.pages.sales.externalsystems.NewExternalProcessingPage;
import ru.crystals.set10.pages.sales.preferences.SalesGoodsTypesAndPaymentsTabPage;
import ru.crystals.set10.pages.sales.preferences.SalesPreferencesPage;
import ru.crystals.set10.pages.sales.shops.JuristicPersonPage;
import ru.crystals.set10.pages.sales.shops.ShopPage;
import ru.crystals.set10.pages.sales.shops.ShopPreferencesPage;
import ru.crystals.set10.pages.sales.topology.CityPage;
import ru.crystals.set10.pages.sales.topology.RegionPage;
import ru.crystals.set10.pages.sales.topology.TopologyPage;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.utils.DbAdapter;
import ru.crystals.set10.utils.DisinsectorTools;


public class SetTopologyTest extends AbstractTest {
	
	MainPage mainPage;
	TopologyPage topologyPage;
	SalesPage salesPage;
	RegionPage regionPage;
	CityPage cityPage;
	ShopPage shopPage;
	ShopPreferencesPage shopPreferences;
	JuristicPersonPage juristicPerson;
	CashierConfigPage cashierConfig;
	NewEquipmentPage  newEqupment;
	NewBankPage newBankPage;
	NewERPPage newERPPage;
	NewExternalProcessingPage newExternalProcessingPage;
	ExternalSystemsPage externalSystemPage;
	EquipmentPage equipmentPage;
	SalesPreferencesPage salesPreferencesPage;
	SalesGoodsTypesAndPaymentsTabPage salesGoodsTypesPage;
	
	@BeforeClass
	public void doLogin(){
		// таймаут для певрого запуска
		DisinsectorTools.delay(3000);
		setUpPrevilegesCentrum();
		setUpPrevilegesRetail();
		
		mainPage = new LoginPage(getDriver(), Config.CENTRUM_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		// таймаут для певрого запуска
		DisinsectorTools.delay(1000);
		salesPage = mainPage.openSales();		
	}
	
//	@AfterClass
//	private void sendGoods(){
//		SoapRequestSender soapSender  = new SoapRequestSender();
//		soapSender.sendGoodsToStartTesting(Config.CENTRUM_HOST, "goods.txt");
//	}
	
	@BeforeMethod (firstTimeOnly = false)
	public void refreshBeforeRun(ITestResult result){
		getDriver().navigate().refresh();
		DisinsectorTools.delay(1000);
	}
	
//	@AfterMethod
//	public void refreshOnError(ITestResult result){
//		if (result.getStatus() == ITestResult.FAILURE) {
//			getDriver().navigate().refresh();
//			DisinsectorTools.delay(1000);
//		}
//	}
	
	@Test (	priority = 1,
			groups = "Config",
			description = "Настроить топологию: создать регион и город")
	public void setTopology() {
		addRegionAndCity();
	}
	
	
	@DataProvider(name="shops")
	public Object[][] shops() {
		return new Object[][]{
				{Config.SHOP_NAME, Config.SHOP_NUMBER, false},
				{Config.VIRTUAL_SHOP_NAME, Config.VIRTUAL_SHOP_NUMBER, true}
		};
	}
	
	@Test (dataProvider = "shops",
			priority = 2, 
			description = "Добавить виртуальный и магазин для ретейла.")
	public void addShopsTest(String shopName, String shopNumber, boolean isVirtual){
		addShop(shopName, shopNumber, isVirtual);
	}
	
	@DataProvider(name="juristicPerson")
	public Object[][] juristicPerson() {
		return new Object[][]{
				{Config.SHOP_NAME, Config.SHOP_ADRESS, Config.SHOP_PHONE, Config.SHOP_INN, Config.SHOP_KPP, Config.SHOP_OKPO, Config.SHOP_OKDP},
				{Config.VIRTUAL_SHOP_NAME, Config.VIRTUAL_SHOP_ADRESS, Config.VIRTUAL_SHOP_PHONE, Config.VIRTUAL_SHOP_INN, Config.VIRTUAL_SHOP_KPP, Config.VIRTUAL_SHOP_OKPO, Config.VIRTUAL_SHOP_OKDP}
		};
	}
	
	@Test (dataProvider = "juristicPerson",
			priority = 3, 
			description = "Добавить юридическое лицо")
	public void addJuristicPersonTest( 
			String shopName, 
			String shopAdress, 
			String shopPhone, 
			String shopInn, 
			String shopKpp,
			String shopOkpo,
			String shopOkdp ){
		addJuristicPerson(shopName, shopAdress, shopPhone, shopInn, shopKpp, shopOkpo, shopOkdp);
	}
	
	@DataProvider(name="cashes")
	public Object[][] cash() {
		return new Object[][]{
				{Config.SHOP_NAME},
				{Config.VIRTUAL_SHOP_NAME}
		};
	}
	
	@Test (dataProvider = "cashes",
			priority = 4, 
			description = "Добавить кассы")
	public void addCashesTest(String shopName){
		addCash(shopName);
	}
	
	@Test ( priority = 4, 
			description = "")
	public void addCashierTest(){
		addCashier();
	}

	
//	@Test
//	public void testAddRegionAndCity(){
//		int totalRegions = 0;
//		int totalCities = 0;
//		
//		topologyPage = salesPage.navigateMenu(1, TopologyPage.class);
//		totalRegions = topologyPage.getRegionsCount();
//		regionPage = topologyPage.addRegion().setRegionName("TestRegion");
//		
//		totalCities = regionPage.getCitiesCount();
//		cityPage = regionPage.addCity();
//		regionPage = cityPage.goBack();
//		
//		Assert.assertEquals(regionPage.getCitiesCount(), totalCities + 1, "Город не добавился");
//		
//		topologyPage = regionPage.goBack();
//		
//		Assert.assertEquals(topologyPage.getRegionsCount(), totalRegions + 1, "Регион не добавился");
//		
//	}
	
	private void addRegionAndCity() {
		topologyPage = salesPage.navigateMenu(1, TopologyPage.class);
		regionPage = topologyPage.addRegion().setRegionName("TestRegion");
		cityPage = regionPage.addCity();
		cityPage.setCityName("TestCity");
		topologyPage = cityPage.goBack().goBack();
	}
	
	private void addShop(String shopName, String shopNumber, boolean useOwnServer) {
		salesPage = new SalesPage(getDriver());
		shopPage = salesPage.navigateMenu(0, ShopPage.class);
		shopPreferences = shopPage.addShop();
		shopPreferences.setName(shopName).
						setShopNumber(shopNumber).
						ifShopUseOwnServer(useOwnServer);
		shopPreferences.goBack();
		// после нажатия Назад, ждем, чтобы сохранился магазин
		DisinsectorTools.delay(500);
	}
	
	private void addJuristicPerson(
			String shopName, 
			String shopAdress, 
			String shopPhone, 
			String shopInn, 
			String shopKpp,
			String shopOkpo,
			String shopOkdp) {
		openShopPreferences(shopName);
		juristicPerson = shopPreferences.addJuristicPerson();
		juristicPerson.setName(shopName)
						.setAdress(shopAdress)
						.setPhone(shopPhone)
						.setINN(shopInn)
						.setKPP(shopKpp)
						.setOKPO(shopOkpo)
						.setOKDP(shopOkdp);
		juristicPerson.goBack().goBack();
	}
	
	private void openShopPreferences(String shopName){
		salesPage = new SalesPage(getDriver());
		shopPage = salesPage.navigateMenu(0, ShopPage.class);
		shopPreferences = shopPage.openShopPreferences(shopName);
	}
	
	private void addCash(String shopName){
		openShopPreferences(shopName);
		shopPreferences.addCashes(5);
		DisinsectorTools.delay(1000);
		shopPreferences.goBack();
	}
	
	private void addCashier(){
			cashierConfig = salesPage
				.navigateMenu(5, CashiersMainPage.class)
				.addCashier();
		cashierConfig.addNewCashier(
				Config.CASHIER_ADMIN_NAME,
				Config.CASHIER_ADMIN_LAST_NAME,
				Config.CASHIER_ADMIN_MIDDLE_NAME,
				Config.CASHIER_ADMIN_TAB_NUM,
				Config.CASHIER_ADMIN_PASSWORD, 
				Config.CASHIER_ADMIN_ROLE);
	}
	
	private void setUpPrevilegesCentrum(){
		log.info("Добавить права пользователю manager на центруме");
		dbAdapter.updateDb(DbAdapter.DB_CENTRUM_SET , String.format("update users_server_user_users_server_user_role " + 
				"set roles_id = '10' " +
				"where serveruserentities_id = (select id from users_server_user where login = '%s')", Config.MANAGER));
		dbAdapter.updateDb(DbAdapter.DB_CENTRUM_SET, String.format("update users_server_user " +
				"set firstname = '%s', lastname='%s', middlename='%s' ", Config.MANAGER_NAME, Config.MANAGER_LASTNAME, Config.MANAGER_MIDDLENAME ));
	}	

	private void setUpPrevilegesRetail(){
		//Добавить все роли на ритейле
		log.info("Добавить все роли пользователю manager на ретейле");
		dbAdapter.updateDb(DbAdapter.DB_RETAIL_SET, "delete from users_server_user_users_server_user_role");
		
		for (int i=1; i<=7; i++) {
			dbAdapter.updateDb(DbAdapter.DB_RETAIL_SET, String.format("INSERT INTO users_server_user_users_server_user_role(serveruserentities_id, roles_id)" +
										" VALUES (%s, %s)", 1, i));
		}
		
		dbAdapter.updateDb(DbAdapter.DB_RETAIL_SET, String.format("update users_server_user " +
				"set firstname = '%s', lastname='%s', middlename='%s' ", Config.MANAGER_NAME, Config.MANAGER_LASTNAME, Config.MANAGER_MIDDLENAME ));
	}

}
