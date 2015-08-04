package ru.crystals.set10.test.maincash;


import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/*
 * Создание автоматического документа ПКО Выручка магазина
 */
@Test (groups= "retail")
public class MainCashAutoLKKTest extends MainCashConfigTest {
	

	
	@BeforeClass
	public void setup(){
	}
	
	@Test( enabled=false,
			description = "SRTE-177. ЛКК формируется при закрытии ОД")
	public void testLKKCreatedOnODClose(){
	}
	
	@Test( enabled=false,
			description = "SRTE-177. ЛКК формируется на основании всех документов ПКО и РКО.")
	public void testLKKContainsAllPKOandRKOsums(){
	}
	
	@Test( enabled=false,
			description = "SRTE-177. ЛКК недоступен для редактирования")
	public void testLKKUnableToEdit(){
	}
	
	@Test( enabled=false,
			description = "SRTE-177. Номер ЛКК формируется в рамках типа документов ЛКК")
	public void testLKKNumber(){
	}
}
