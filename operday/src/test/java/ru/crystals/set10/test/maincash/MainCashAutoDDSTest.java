package ru.crystals.set10.test.maincash;


import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/*
 * Создание автоматического документа ПКО Выручка магазина
 */
@Test (groups= "retail")
public class MainCashAutoDDSTest extends MainCashConfigTest {
	

	
	@BeforeClass
	public void setup(){
	}
	
	@Test( enabled=false,
			description = "SRTE-178. Отчёт ДДС формируется при закрытии ОД")
	public void testDDSCreatedOnODClose(){
	}
	
	@Test( enabled=false,
			description = "SRTE-178. Отчёт ДДС формируется на основании всех документов ПКО и РКО.")
	public void testDDSContainsAllPKOandRKOsums(){
	}
	
	@Test( enabled=false,
			description = "SRTE-178. Отчёт ДДС недоступен для редактирования")
	public void testDDSUnableToEdit(){
	}
}
