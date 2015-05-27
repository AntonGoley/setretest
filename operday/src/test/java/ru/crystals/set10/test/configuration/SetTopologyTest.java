 package ru.crystals.set10.test.configuration;


import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.*;
import ru.crystals.set10.pages.sales.cashiers.CashiersMainPage;
import ru.crystals.set10.pages.sales.cashiers.CashierConfigPage;
import ru.crystals.set10.pages.sales.equipment.NewEquipmentPage;
import ru.crystals.set10.pages.sales.externalsystems.ExternalSystemsPage;
import ru.crystals.set10.pages.sales.externalsystems.NewBankPage;
import ru.crystals.set10.pages.sales.externalsystems.NewERPPage;
import ru.crystals.set10.pages.sales.externalsystems.NewExternalProcessingPage;
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
import static ru.crystals.set10.pages.sales.externalsystems.ExternalSystemsPage.*;



public class SetTopologyTest extends AbstractTest{
	
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
	
	@Test (	priority = 1,
			groups = "Config",
			description = "Настроить топологию: создать регион, город, магазин, юридическое лицо, добавить кассы, создать кассира")
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
			priority = 2)
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
			priority = 3)
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
			priority = 4)
	public void addCashesTest(String shopName){
		addCash(shopName);
	}
	
	@Test ( priority = 5)
	public void addCashierTest(){
		addCashier();
	}
	
	
//	@Test (	
//			description = "Добавить права пользователю manager на центруме"
//			)
	private void setUpPrevilegesCentrum(){
		log.info("Добавить права пользователю manager на центруме");
		dbAdapter.updateDb(DbAdapter.DB_CENTRUM_SET , String.format("update users_server_user_users_server_user_role " + 
				"set roles_id = '10' " +
				"where serveruserentities_id = (select id from users_server_user where login = '%s')", Config.MANAGER));
		dbAdapter.updateDb(DbAdapter.DB_CENTRUM_SET, String.format("update users_server_user " +
				"set firstname = '%s', lastname='%s', middlename='%s' ", Config.MANAGER_NAME, Config.MANAGER_LASTNAME, Config.MANAGER_MIDDLENAME ));
	}	

//	@Test (	
//			description = "Добавить все роли пользователю manager на ретейле"
//			 )
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
		getDriver().navigate().refresh();
		DisinsectorTools.delay(1000);
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
		getDriver().navigate().refresh();
		DisinsectorTools.delay(1000);
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
	
	
	/*
	 * Добавление банков
	 */
	@DataProvider(name="banks")
	public Object[][] banks() {
		return new Object[][]{
				{Config.BANK_NAME_1},
				{Config.BANK_NAME_2}
		};
	}
	
	@Test (dataProvider = "banks", 
			description = "Добавление банка")
	public void addBank(String bankName){
		getDriver().navigate().refresh();
		DisinsectorTools.delay(1000);
		log.info("Добавление банка: " + bankName);
		externalSystemPage = salesPage
				.navigateMenu(3, ExternalSystemsPage.class);
		externalSystemPage.navigateTab(TAB_NAME_BANKS);
		newBankPage = externalSystemPage.addEntity(NewBankPage.class);
		newBankPage.addBank(bankName);
	}
	
	/*
	 * Добавление ERP
	 */
	@Test (description = "Добавление ERP")
	public void addERP(){
		int erpSystemsBefore = 0;
		getDriver().navigate().refresh();
		DisinsectorTools.delay(1000);
		log.info("Добавление ERP: Протокол Set Retail 10: файлы");
		externalSystemPage = salesPage
				.navigateMenu(3, ExternalSystemsPage.class);
		externalSystemPage.navigateTab(TAB_NAME_ERP);
		erpSystemsBefore = externalSystemPage.getERPsCount();
		
		newERPPage = externalSystemPage.addEntity(NewERPPage.class);
		externalSystemPage = newERPPage.addERP("Протокол Set Retail 10: файлы");
		Assert.assertEquals(externalSystemPage.getERPsCount(), erpSystemsBefore + 1, "Новая ERP система не добавлена!");
	}
	
	
	/*
	 * Добавление процессингов
	 */
	@DataProvider(name="processing")
	public Object[][] processing() {
		return new Object[][]{
				//TODO: вынести в конфиг?
				{"Подарочные карты", "Подарочные карты ЦФТ"},
				{"Бонусные процессинги", "Спасибо от Сбербанка"}
		};
	}
	
	@Test (dataProvider = "processing",
			description = "Добавление внешнего процессинга")
	public void addExternalProcessing(String processingType, String processingValue){
		int extSystemsBefore = 0;
		log.info("Добавление внешней системы: " + processingValue);
		getDriver().navigate().refresh();
		//DisinsectorTools.delay(1000);
		
		externalSystemPage = salesPage
				.navigateMenu(3, ExternalSystemsPage.class);
		
		externalSystemPage.navigateTab(TAB_EXTERNAL_PROCESSINGS);
		extSystemsBefore = externalSystemPage.getExternalProcessingCount();
		newExternalProcessingPage = externalSystemPage.addEntity(NewExternalProcessingPage.class);
		externalSystemPage = newExternalProcessingPage.addProcessing(processingType, processingValue);
		
		Assert.assertTrue(extSystemsBefore < externalSystemPage.getExternalProcessingCount(), String.format("Внешняя система %s не добавлена!", processingValue));
		
	}
	
	
	
	
	//@AfterClass
	private void sendGoods(){
		SoapRequestSender soapSender  = new SoapRequestSender();
		soapSender.sendGoodsToStartTesting(Config.CENTRUM_HOST, "goods.txt");
	}
}
