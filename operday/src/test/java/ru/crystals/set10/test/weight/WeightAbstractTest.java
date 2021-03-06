package ru.crystals.set10.test.weight;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import ru.crystals.set10.config.Config;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.utils.DbAdapter;
import ru.crystals.set10.utils.VirtualScalesReader;


@Test(groups = {"retail"})
public class WeightAbstractTest extends AbstractTest{
	
	protected static final Logger log = Logger.getLogger(WeightAbstractTest.class);
	protected static VirtualScalesReader scales = new VirtualScalesReader();
	
	protected static int pluNumber = 1;
	
	/* выполняется перед сьютом тестов весового модуля
	 * 	все тесты весового модуля объединены в один сьют 
	 */
	@BeforeSuite
	public void prerareSuite(){
		log.info("Удаление из базы магазина " + Config.SHOP_NUMBER + " set всех весовых товаров и привязок");
		dbAdapter.batchUpdateDb(DbAdapter.DB_RETAIL_SET, 
				new String[] {"delete from scales_linktoplu",
							  "delete from scales_plues",
							  "delete from un_cg_product_weight",
							  "delete from un_cg_product where markingofthegood in (select productcode from scales_productentity)"});
		scales.clearVScalesFileData();
	}
	
}
