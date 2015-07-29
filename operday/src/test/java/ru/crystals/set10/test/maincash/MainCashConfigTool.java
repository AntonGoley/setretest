package ru.crystals.set10.test.maincash;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.utils.DbAdapter;
import ru.crystals.set10.utils.DisinsectorTools;
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
	
	private static  String enableMainCash = "update sales_management_properties set property_value = %s where property_key  = 'main.cash.enabled'";
	private static  String turnMainCash = "update od_operday set initial_balance = %s where day = '%s'";
	private static String propertyBalance = "update sales_management_properties set property_value = '%s' where property_key = 'main.cash.initial.balance'";
	
	private static String adminPrivelegesView = "insert into users_server_user_role_users_privileges values "
			+ "((select id from users_server_user_role where rolename='Администратор'), (select id from users_privileges where name = 'MAIN_CASH_DESK_DOCUMENTS_PRIVILEGE' limit 1))";
	
	private static String adminPrivelegesManage = "insert into users_server_user_role_users_privileges   values "
			+ "((select id from users_server_user_role where rolename='Администратор'),  (select id from users_privileges where name = 'MAIN_CASH_DESK_SETTINGS_PRIVILEGE' limit 1));";

	private static String adminPrivelegesCount = "  select count(*) from users_server_user_role_users_privileges as rolewithprivileges  "
			+ "join users_privileges as p on p.id=rolewithprivileges.privileges_id   "
			+ "join users_server_user_role as role on role.id=rolewithprivileges.roles_id   "
			+ "where p.name in ('MAIN_CASH_DESK_DOCUMENTS_PRIVILEGE', 'MAIN_CASH_DESK_SETTINGS_PRIVILEGE')   "
			+ "and role.rolename = 'Администратор'";
	
	
	/*
	 * Удалить ОД и все документы Главной кассы:
	 * КМ3, КМ6, КМ7, ПКО, РКО, ДДС, ЛКК
	 */
	public static void clearOD(){
		dbAdapter.updateDb(DB_RETAIL_OPERDAY, clearOd);
		log.info("Удалены все опер.дни и документы ГК!");
	}
	
	/*
	 * Включить/выключить главную кассу (через DB)
	 */
	public static void enableMainCash(Boolean enabled){
		dbAdapter.updateDb(DB_RETAIL_SET, String.format(enableMainCash, enabled.toString()));
	}
	
	/*
	 * Включить главную кассу 
	 */
	public static void turnMainCash(long date, int balance){
		log.info(String.format(turnMainCash, balance, DisinsectorTools.getDate("yyyy-MM-dd", date)));
		dbAdapter.updateDb(DB_RETAIL_OPERDAY, String.format(turnMainCash, balance, DisinsectorTools.getDate("yyyy-MM-dd", date)));
		dbAdapter.updateDb(DB_RETAIL_SET, String.format(propertyBalance, balance));
	}
	
	/*
	 * Добавить права Администратору
	 */
	public static void addPrivileges(){
		int privilegesCount = 0;
		privilegesCount = dbAdapter.queryForInt(DB_RETAIL_SET, adminPrivelegesCount);
		if (privilegesCount == 0){
			dbAdapter.updateDb(DB_RETAIL_SET, adminPrivelegesManage);
			dbAdapter.updateDb(DB_RETAIL_SET, adminPrivelegesView);
		}
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
