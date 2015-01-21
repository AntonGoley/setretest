package ru.crystals.set10.test.weight;

import java.util.HashMap;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.VirtualScalesReader;

public class WeightAbstractTest extends AbstractTest{
	
	String barCodePrefix = Config.WEIGHT_BARCODE_PREFIX;
	static VirtualScalesReader scales = new VirtualScalesReader();
	String WEIGHT_GOOD_FILE = "/weight/weight.txt";
	
	protected static String MARKING_OF_THE_GOOD_PARAM = "${marking-of-the-good}";
	protected static String GOOD_NAME_PARAM = "${name}";
	protected static String GOOD_BARCODE_PARAM = "${barcode}";
	protected static String DELETE_FROM_CASHE_PARAM = "${delete-from-cash}";
	protected static String DONT_SENT_TO_SCALES_PARAM = "${do-not-send-to-scales}";
	protected static String PLU_NUMBER_PARAM = "${plu-number}";
	
	protected static String ACTION_TYPE_CLEAR = "ClearPLU";
	protected static String ACTION_TYPE_LOAD = "LoadPLU";
	
	protected static int plu = 1;
	
	protected HashMap<String, String> generateGoodData(){
		HashMap<String, String> weightGood = new HashMap<String, String>();	
		
		weightGood.put(MARKING_OF_THE_GOOD_PARAM, String.valueOf(System.currentTimeMillis()));
		weightGood.put(GOOD_NAME_PARAM, "Весовой товар " + weightGood.get("${marking-of-the-good}"));
		weightGood.put(GOOD_BARCODE_PARAM, barCodePrefix + generateBarCode());
		weightGood.put(DELETE_FROM_CASHE_PARAM, "false");
		weightGood.put(DONT_SENT_TO_SCALES_PARAM , "false");
		weightGood.put(PLU_NUMBER_PARAM, String.valueOf(plu++));
		/*
		 * Задержка, на случай, если подряд генерим много товаров
		 */
		DisinsectorTools.delay(10);
		return weightGood;
	}
	
	private String generateBarCode(){
		/*
		 * Берем последние 5 символов 
		 */
		return String.valueOf(System.currentTimeMillis()).substring(8, 13);
	}
	
}
