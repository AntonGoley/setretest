package ru.crystals.set10.test.maincash;


import java.math.BigDecimal;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.crystals.set10.test.maincash.MainCashDoc.MainCashDocStatus;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.pages.operday.cashes.CashDocsAbstractPage.*;


/*
 * Создание автоматического документа ПКО Выручка магазина
 */
@Test (groups= "retail")
public class MainCashAutoPKORevenueTest extends MainCashConfigTest {
	
	private Integer pkoRevenueExpectedNumber = 0;
	private MainCashDoc pkoRevenue;
	private BigDecimal cashIn = new BigDecimal("100.99");
	private BigDecimal shifDelta = new BigDecimal("200.99");
	private BigDecimal pkoSum = new BigDecimal("0.00");
	
	private BigDecimal balanceBefore;
	
	/* Long для передачи в Z отчет*/
	private long cashInLong = cashIn.multiply(new BigDecimal("100")).longValueExact();
	private long shifDeltaLong = shifDelta.multiply(new BigDecimal("100")).longValueExact();
	
	@BeforeClass
	public void setup(){
		MainCashConfigTool.clearLastPKORevenuDoc();
		pkoRevenueExpectedNumber = MainCashConfigTool.getNexDocNumberForType(MainCashDoc.DOC_TYPE_PKO_REVENUE);
		openMainDocsPage();
		MainCashConfigTool.makeShiftsGreenForDate(today);
		docs.reopenOperDayAndSwitchBack(today);
		balanceBefore = docs.getBalanceEnd();
	}
	
	@Test(	priority = 0,
			description = "SRTE-176. ПКО Выручка формируется при приходе первого Z-отчета")
	public void testPKORevenueWhenFirstZReport() throws Exception{
		Assert.assertTrue(docs.getDocByType(MainCashDoc.DOC_TYPE_PKO_REVENUE).size()==0, "Документ " + MainCashDoc.DOC_TYPE_PKO_REVENUE + " не должен существовать на текущую дату перед началом теста!");
		sendZReport();
		Assert.assertTrue(docs.getDocByType(MainCashDoc.DOC_TYPE_PKO_REVENUE).size()==1, "Документ " + MainCashDoc.DOC_TYPE_PKO_REVENUE + " не создался после первого Z-отчета");
	}
	
	@Test( priority = 1,
			description = "SRTE-176. Номер документа ПКО Выручка генерируется в рамках нумерации для типа ПКО")
	public void testPKORevenueNumberInPKOSequence() throws Exception{
		pkoRevenue = docs.getDocByType(MainCashDoc.DOC_TYPE_PKO_REVENUE).get(0);
		Assert.assertEquals(pkoRevenue.getNumber(),
				pkoRevenueExpectedNumber,
				"Неверно сгенерился номер документа");
	}

	@Test( 	priority = 1,
			description = "SRTE-176. Каждая новая пришедшая закрытая смена (Z-отчёт) с любой кассы, меняет баланс главной кассы")
	public void testPKORevenueChangeBalanceOnNewCheck(){
		Assert.assertEquals(docs.getBalanceEnd(), balanceBefore.add(shifDelta), "Неверная сумма документа " + MainCashDoc.DOC_TYPE_PKO_REVENUE);
	}
	
	@Test( priority = 1,
			description = "SRTE-176. Сумма документа «ПКО выручка» = Сумма всех изъятий - сумма всех внесений по всем закрытым сменам текущего ОД")
	public void testPKORevenueSum() throws Exception{
		sendZReport();
		pkoRevenue = docs.getDocByType(MainCashDoc.DOC_TYPE_PKO_REVENUE).get(0);
		DisinsectorTools.delay(1000);
		Assert.assertEquals(pkoRevenue.getIncome(), pkoSum, "Неверная сумма документа " + MainCashDoc.DOC_TYPE_PKO_REVENUE);
	}
	
	@Test( priority = 2,
			description = "SRTE-176. Документ ПКО Выручка в сером статусе, если ОД не закрыт")
	public void testPKORevenueGreyStatus(){
		Assert.assertTrue(pkoRevenue.getStatus().equals(MainCashDocStatus.GREY),  "Документ ПКО Выручка должен быть в сером статусе, если ОД не закрыт!");
	}
	
	@Test( 	priority = 3,
			description = "SRTE-176. При закрытии ОД ПКО Выручка переходит в зеленый статус")
	public void testPKORevenueGreenStatusOnCloseOd() throws Exception{
		docs.closeOperdayAndSwitchBack(today);
		docs.getDocsOnPage();
		pkoRevenue = docs.getDocByTypeAndNumber(MainCashDoc.DOC_TYPE_PKO_REVENUE, pkoRevenueExpectedNumber);
		Assert.assertTrue(pkoRevenue.getStatus().equals(MainCashDocStatus.GREEN),  "Документ ПКО Выручка должен быть в зеленом статусе, если ОД нзакрыт!");
	}
	
	@Test( 	priority = 3,
			description = "SRTE-176. Печать документа ПКО Выручка после закрытия ОД и проверка заполнения полей печатной формы")
	public void testPKORevenuePrint(){
		removeFileReports();
		docs.printDoc(pkoRevenue);
		String pageContent = getFileContent(1);
		Assert.assertTrue(pageContent.contains("Основание:\n" + MainCashDoc.DOC_TYPE_PKO_REVENUE.substring(4)), "Печатная форма не содержит название документа ПКО: " + MainCashDoc.DOC_TYPE_PKO_REVENUE);
		Assert.assertTrue(pageContent.contains(pkoRevenue.getIncome().toString()), "Печатная форма не содержит сумму в таблице документа " + pkoRevenue.getIncome().toEngineeringString());
	}
	
	@Test( 	enabled = false, dependsOnMethods = "testPKORevenueWhenFirstZReport",
			description = "SRTE-176. Документ ПКО Выручка можно распечатать только после закрытия ОД")
	public void testPKORevenuePrintEnable(){
		Assert.assertFalse(pkoRevenue.getPrinable(),  "Документ не должен быть доступен для печати, если ОД не закрыт!");
	}
	
	@Test( enabled=false,
			description = "SRTE-176. Документ ПКО Выручка невозможно редактировать, при открытом ОД")
	public void testPKORevenueEdit(){
	}
	
	private void sendZReport(){
		cashEmulator.nextPurchase();
		cashEmulator.nextZReport(cashInLong, cashInLong + shifDeltaLong);
		pkoSum = pkoSum.add(shifDelta);
		/* обновить страницу*/
		docs.switchToTable(LOCATOR_KM6);
		docs.switchToTable(LOCATOR_DOCS);
		docs.getDocsOnPage();
	}
	
}
