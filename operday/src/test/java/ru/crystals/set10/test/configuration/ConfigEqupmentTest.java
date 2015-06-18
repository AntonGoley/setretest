 package ru.crystals.set10.test.configuration;


import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.*;
import ru.crystals.set10.pages.sales.equipment.EquipmentPage;
import ru.crystals.set10.pages.sales.equipment.NewEquipmentPage;
import ru.crystals.set10.pages.sales.externalsystems.ExternalSystemsPage;
import ru.crystals.set10.pages.sales.externalsystems.NewBankPage;
import ru.crystals.set10.pages.sales.externalsystems.NewERPPage;
import ru.crystals.set10.pages.sales.externalsystems.NewExternalProcessingPage;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.pages.sales.externalsystems.ExternalSystemsPage.*;


@Test(groups = {"retail", "centrum"})
public class ConfigEqupmentTest extends AbstractTest {
	
	MainPage mainPage;
	SalesPage salesPage;
	NewEquipmentPage  newEqupment;
	NewBankPage newBankPage;
	NewERPPage newERPPage;
	NewExternalProcessingPage newExternalProcessingPage;
	ExternalSystemsPage externalSystemPage;
	EquipmentPage equipmentPage;
	
	
	@BeforeClass
	public void doLogin(){
		mainPage = new LoginPage(getDriver(), TARGET_HOST_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		salesPage = mainPage.openSales();		
	}
	
	@BeforeMethod (firstTimeOnly = false)
	public void refreshBeforeRun(ITestResult result){
		getDriver().navigate().refresh();
		DisinsectorTools.delay(1000);
	}
	
	/*
	 * Добавление оборудования
	 */
	@DataProvider(name="equipment")
	private Object[][] equipment() {
		return new Object[][]{
				{"Клавиатуры", "QWERTY клавиатура"},
				{"Сканер", "Сканер штриховых кодов"},
				{"Принтеры A4", "Стандартный принтер А4"},
				{"VirtualScales", "VirtualScales"},
				
		};
	}
	
	@Test (priority = 4,
			dataProvider = "equipment",
			description = "Добавление оборудования")
	public void testAddEquipment(String equipmentGoup, String equipment){
		equipmentPage = salesPage.navigateMenu(2, EquipmentPage.class);
		newEqupment = equipmentPage.addNewEquipment();

		equipmentPage = newEqupment.addEquipment(equipmentGoup, equipment);
		
//		Assert.assertTrue(equipmentPage.getEqupmentTypeCount(scalesItem) > 0, 
//				"Новые весы " + scalesItem + " не добавлены в обородувание");
		
	}
	
	/*
	 * Добавление банков
	 */
	@DataProvider(name="banks")
	private Object[][] banks() {
		return new Object[][]{
				{Config.BANK_NAME_1},
				{Config.BANK_NAME_2}
		};
	}
	
	@Test (priority = 4,
			dataProvider = "banks", 
			description = "Добавление банка")
	public void addBank(String bankName){
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
	@Test (priority = 4,
			description = "Добавление ERP")
	public void addERP(){
		int erpSystemsBefore = 0;
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
	private Object[][] processing() {
		return new Object[][]{
				//TODO: вынести в конфиг?
				{"Подарочные карты", "Подарочные карты ЦФТ"},
				{"Бонусные процессинги", "Спасибо от Сбербанка"}
		};
	}
	
	@Test (priority = 4,
			dataProvider = "processing",
			description = "Добавление внешнего процессинга")
	public void addExternalProcessing(String processingType, String processingValue){
		int extSystemsBefore = 0;
		log.info("Добавление внешней системы: " + processingValue);
		
		externalSystemPage = salesPage
				.navigateMenu(3, ExternalSystemsPage.class);
		
		externalSystemPage.navigateTab(TAB_EXTERNAL_PROCESSINGS);
		extSystemsBefore = externalSystemPage.getExternalProcessingCount();
		newExternalProcessingPage = externalSystemPage.addEntity(NewExternalProcessingPage.class);
		externalSystemPage = newExternalProcessingPage.addProcessing(processingType, processingValue);
		
		Assert.assertTrue(extSystemsBefore < externalSystemPage.getExternalProcessingCount(), String.format("Внешняя система %s не добавлена!", processingValue));
	}

}
