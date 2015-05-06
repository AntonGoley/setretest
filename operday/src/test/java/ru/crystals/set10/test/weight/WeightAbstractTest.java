package ru.crystals.set10.test.weight;

import java.util.HashMap;

import org.testng.annotations.BeforeSuite;

import ru.crystals.set10.config.Config;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.utils.DbAdapter;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.VirtualScalesReader;

public class WeightAbstractTest extends AbstractTest{
	
	static VirtualScalesReader scales = new VirtualScalesReader();
	
	protected String barCodePrefix = Config.WEIGHT_BARCODE_PREFIX;
	protected String WEIGHT_GOOD_FILE = "weight/weight.txt";
	protected String WEIGHT_LECOND_FILE = "/lecond.txt";
	
	protected long day = 86400*100;
	protected static final String LECOND_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	
	/*
	 * Товар
	 */
	protected static String MARKING_OF_THE_GOOD_PARAM = "${marking-of-the-good}";
	protected static String GOOD_ERP_CODE_PARAM = "${erp-code}";
	protected static String GOOD_NAME_PARAM = "${name}";
	protected static String GOOD_BARCODE_PARAM = "${barcode}";
	protected static String DELETE_FROM_CASHE_PARAM = "${delete-from-cash}";
	protected static String DONT_SENT_TO_SCALES_PARAM = "${do-not-send-to-scales}";
	protected static String PLU_NUMBER_PARAM = "${plu-number}";
	
	/*
	 *	Тены на товар 
	 */
	public static String GOOD_PRICE1_PARAM = "${price1}";
	protected static String GOOD_PRICE1_BEGIN_DATE_PARAM = "${price1_begin_date}";
	protected static String GOOD_PRICE1_END_DATE_PARAM = "${price1_end_date}";
	
	public static String GOOD_PRICE2_PARAM = "${price2}";
	protected static String GOOD_PRICE2_BEGIN_DATE_PARAM = "${price2_begin_date}";
	protected static String GOOD_PRICE2_END_DATE_PARAM = "${price2_end_date}";
	
	/*
	 * Леконды
	 */
	protected static String LECOND_SINCE_DATE_PARAM = "${since_date}";
	protected static String LECOND_TILL_DATE_PARAM = "${till_date}";
	
	/*
	 * ACTION_TYPE для plu, выгружаемых/загружаемых в весы
	 */
	protected static String ACTION_TYPE_CLEAR = "ClearPLU";
	protected static String ACTION_TYPE_LOAD = "LoadPLU";
	
	protected static int plu = 3;
	
	protected static int pluNumber = 1;
	
	@BeforeSuite
	public void prerareSuite(){
		log.info("Удаление из базы магазина " + Config.SHOP_NUMBER + " set всех весовых товаров и привязок");
		dbAdapter.batchUpdateDb(DbAdapter.DB_RETAIL_SET, 
				new String[] {"delete from scales_linktoplu",
							  "delete from scales_plues",
							  "delete from un_cg_product_weight",
							  "delete from un_cg_product where markingofthegood in (select productcode from scales_productentity)"});
	}
	
	protected HashMap<String, String> generateGoodData(){
		HashMap<String, String> weightGood = new HashMap<String, String>();	
		
		weightGood.put(MARKING_OF_THE_GOOD_PARAM, String.valueOf(System.currentTimeMillis()));
		weightGood.put(GOOD_ERP_CODE_PARAM, weightGood.get(MARKING_OF_THE_GOOD_PARAM));
		weightGood.put(GOOD_NAME_PARAM, "Весовой товар " + weightGood.get(MARKING_OF_THE_GOOD_PARAM));
		weightGood.put(GOOD_BARCODE_PARAM, barCodePrefix + generateBarCode());
		weightGood.put(DELETE_FROM_CASHE_PARAM, "false");
		weightGood.put(DONT_SENT_TO_SCALES_PARAM , "false");
		weightGood.put(PLU_NUMBER_PARAM, String.valueOf(plu++));
		
		long price1 = DisinsectorTools.random(10000);
		long now = System.currentTimeMillis(); 
		/*
		 * 2 я цена меньше первой, все цены действуют
		 */
		weightGood.put(GOOD_PRICE1_PARAM, String.valueOf(price1) + ".99");
		weightGood.put(GOOD_PRICE2_PARAM, String.valueOf(price1 - 10L) + ".79");
		weightGood.put(GOOD_PRICE1_BEGIN_DATE_PARAM, DisinsectorTools.getDate(LECOND_DATE_FORMAT, now - day ));
		weightGood.put(GOOD_PRICE1_END_DATE_PARAM, DisinsectorTools.getDate(LECOND_DATE_FORMAT, now + 2*day));
		weightGood.put(GOOD_PRICE2_BEGIN_DATE_PARAM, DisinsectorTools.getDate(LECOND_DATE_FORMAT, now - day ));
		weightGood.put(GOOD_PRICE2_END_DATE_PARAM, DisinsectorTools.getDate(LECOND_DATE_FORMAT, now + 2*day));
		
		/*
		 * Задержка, на случай, если подряд генерим много товаров
		 */
		DisinsectorTools.delay(9);
		return weightGood;
	}
	
	private String generateBarCode(){
		/*
		 * Берем последние 5 символов 
		 */
		return String.valueOf(System.currentTimeMillis()).substring(8, 13);
	}
}
