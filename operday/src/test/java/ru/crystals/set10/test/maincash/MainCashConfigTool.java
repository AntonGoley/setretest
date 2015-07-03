package ru.crystals.set10.test.maincash;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.utils.DbAdapter;
import ru.crystals.set10.utils.OdGenerator;
import static ru.crystals.set10.utils.DbAdapter.DB_RETAIL_OPERDAY;
import static ru.crystals.set10.utils.DbAdapter.DB_RETAIL_SET;



public class MainCashConfigTool {
	
	
	private static final Logger log = Logger.getLogger(AbstractTest.class);
	
	private static DbAdapter dbAdapter = new DbAdapter();
	
	private static  String clearOd = "delete from od_maincash_document_km7; "
			+ "delete from od_maincash_document_pko; "
			+ "delete from od_maincash_document_rko; "
			+ "delete from od_maincash_document_rko_encashment; "
			+ "delete from od_maincash_document; "
			+ "delete from od_operday; "
			+ "delete from od_km3; "
			+ "delete from od_km3_row; "
			+ "delete from od_km6; ";
	
	private static  String turnMCash = "update sales_management_properties set property_value = %s where property_key  = 'main.cash.enabled'";
	
	
	/*
	 * Удалить ОД и все документы Главной кассы:
	 * КМ3, КМ6, КМ7, ПКО, РКО, ДДС, ЛКК
	 */
	public static void clearOD(){
		dbAdapter.updateDb(DB_RETAIL_OPERDAY, clearOd);
		log.info("Удалены все опер.дни и документы ГК!");
	}
	
	/*
	 * Выключить главную кассу (через DB)
	 */
	public static void turnOfMainCash(){
		dbAdapter.updateDb(DB_RETAIL_SET, String.format(turnMCash, "false"));
	}
	
	/*
	 * Включить главную кассу (через DB)
	 */
	public static void turnOnMainCash(){
		dbAdapter.updateDb(DB_RETAIL_SET, String.format(turnMCash, "true"));
	}
	
	/*
	 * Создать операционные дни в прошлом со смещением относительно сегодняшнего дня
	 * создать чек
	 * закрыть смену
	 */
	public static void createODWithCashDocs(Long[] args){
		
		ExecutorService es = Executors.newCachedThreadPool();
		
		for (int i=0; i<args.length; i++) { 
			OdGenerator odGen = new OdGenerator(args[i]);
			es.execute(odGen);
		}	
		
		es.shutdown();
		
		try {
			es.awaitTermination(5, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			log.error("Чеки не дошли в ОД в течение 5 минут!");
			e.printStackTrace();
		}

	}
	
}
