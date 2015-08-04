package ru.crystals.set10.test.search;


import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage.*;

@Test (groups={"centrum", "retail"})
public class SearchCheckForRequestPerformanceTest extends SearchCheckAbstractTest{
	
	@BeforeClass
	public void send1stCheck(){
		super.openSearchPage();
		searchCheck.openFilter();
	}
	
	
	@DataProvider (name = "cashAndSum")
	private Object[][] cashAndSum(){
		return new Object[][]{
				{DisinsectorTools.random(10) + 10, "2000"},
				{DisinsectorTools.random(10) + 10, "2500"},
				{DisinsectorTools.random(10) + 10, "3000"}
		};
	}
	
	@Test (priority = 1,
			dataProvider = "cashAndSum")
	public void cashAndSumSearchTest(Long cashNumber, String sum ){
		searchCheck.deleteAllFilters();
		searchCheck.addFilter();
		searchCheck.setFilterMultiText(FILTER_CATEGORY_CASH_NUMBER, String.valueOf(cashNumber));
		searchCheck.addFilter();
		searchCheck.setFilterSelectSum(FILTER_CATEGORY_SUM_CHECK, FILTER_CATEGORY_SELECT_GREATER, sum);
		searchCheck.doSearch();
	}
	
	@DataProvider (name = "cashAndShifAndCheck")
	private Object[][] cashAndShift(){
		return new Object[][]{
				{DisinsectorTools.random(10) + 10, DisinsectorTools.random(10) + 40, DisinsectorTools.random(50) + 10},
				{DisinsectorTools.random(10) + 10, DisinsectorTools.random(10) + 40, DisinsectorTools.random(50) + 10},
				{DisinsectorTools.random(10) + 10, DisinsectorTools.random(10) + 40, DisinsectorTools.random(50) + 10}
		};
	}
	
	@Test (priority = 2,
			dataProvider = "cashAndShifAndCheck")
	public void cashAndShiftSearchTest(Long cashNumber, Long shift, Long checkNum){
		searchCheck.deleteAllFilters();
		searchCheck.addFilter();
		searchCheck.setFilterMultiText(FILTER_CATEGORY_CASH_NUMBER, String.valueOf(cashNumber));
		searchCheck.addFilter();
		searchCheck.setFilterText(FILTER_CATEGORY_SHIFT_NUMBER, String.valueOf(shift));
		searchCheck.addFilter();
		searchCheck.setFilterText(FILTER_CATEGORY_CHECK_NUMBER, String.valueOf(checkNum));
		searchCheck.doSearch();
	}
	
	
	@DataProvider (name = "productCode")
	private Object[][] productCode(){
		return new Object[][]{
			{"343433_ST\\n080074_ST\\n217010_ST\\n016091_ST\\n243111_ST"},
			{"011461_KG\\n339410KAR\\n233813_ST\\n071739_ST\\n062696_ST"},
			{"230889_ST" + "\\n" + "181823_ST"},

		};
	}
	
	@Test (priority = 3, 
			dataProvider = "productCode")
	public void productCodeTest(String codes){
		searchCheck.deleteAllFilters();
		searchCheck.addFilter();
		searchCheck.setFilterMultiText(FILTER_CATEGORY_GOOD_CODE, codes);
		searchCheck.doSearch();
	}
}
