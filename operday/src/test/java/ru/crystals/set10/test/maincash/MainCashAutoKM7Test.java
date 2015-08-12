package ru.crystals.set10.test.maincash;


import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/*
 * Создание автоматического документа ПКО Выручка магазина
 */
@Test (groups= "retail")
public class MainCashAutoKM7Test extends MainCashConfigTest {
	

	
	@BeforeClass
	public void setup(){
		
		
	}
	
	@Test( enabled=false,
			description = "SRTE-181. Закрытая смена (Z-отчет) создаёт документ АКТ КМ-7")
	public void testKM7CreatedOnZRepost(){
	}
	
	@Test( enabled=false,
			description = "SRTE-181. АКТ КМ-7 содержит информацию по всем закрытым сменам")
	public void testKM7ContainsAllClosedShifts(){
	}
	
	@Test( enabled=false,
			description = "SRTE-181. Акт КМ7 содержит данные актов КМ6.???")
	public void testKM7ContainsKM6AsData(){
	}
	
	@Test( enabled=false,
			description = "SRTE-181. Акт КМ7 недоступен для редактирования")
	public void testKM7UnableToEdit(){
	}
	
	@Test( enabled=false,
			description = "SRTE-181. Номер акта КМ-7  в рамках типа документов Акт КМ-7")
	public void testKM7Number(){
	}
	
	
}
