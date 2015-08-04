package ru.crystals.set10.test.maincash;


import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/*
 * Создание автоматического документа ПКО Выручка магазина
 */
@Test (groups= "retail")
public class MainCashAutoPKORevenueTest extends MainCashConfigTest {
	

	
	@BeforeClass
	public void setup(){
	}
	
	@Test( enabled=false,
			description = "SRTE-176. ПКО Выручка формируется при приходе первого Z-отчета")
	public void testPKORevenueWhenFirstZReport(){
	}
	
	@Test( enabled=false,
			description = "SRTE-176. Номер документа ПКО Выручка в рамках документов ПКО")
	public void testPKORevenueNumberInPKOSequence(){
	}
	
	@Test( enabled=false,
			description = "SRTE-176. Сумма документа «ПКО выручка» = Сумма всех изъятий - сумма всех внесений по всем закрытым сменам текущего ОД")
	public void testPKORevenueSum(String doctype){
	}
	
	@Test( enabled=false,
			description = "SRTE-176. Каждая новая пришедшая закрытая смена (Z-отчёт) с любой кассы, автоматически учитывается в документе «ПКО выручка»")
	public void testPKORevenueChangeSumOnNewCheck(){
	}
	
	@Test( enabled=false,
			description = "SRTE-176. Каждая новая пришедшая закрытая смена (Z-отчёт) с любой кассы, меняет баланс главной кассы")
	public void testPKORevenueChangeBalanceOnNewCheck(){
	}
	
	@Test( enabled=false,
			description = "SRTE-176. Документ ПКО Выручка можно распечатать только после закрытия ОД")
	public void testPKORevenuePrintEnable(){
	}
	
	@Test( enabled=false,
			description = "SRTE-176. Документ ПКО Выручка невозможно редактировать")
	public void testPKORevenueEdit(String doctype){
	}
	
}
