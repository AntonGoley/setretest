package ru.crystals.set10.test.maincash;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import ru.crystals.set10.pages.operday.cashes.MainCashDoc;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.utils.DbAdapter;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.OdGenerator;
import static ru.crystals.set10.utils.DbAdapter.DB_RETAIL_OPERDAY;
import static ru.crystals.set10.utils.DbAdapter.DB_RETAIL_SET;


public class MainCashConfigTool {
	
	private static final Logger log = Logger.getLogger(AbstractTest.class);
	
	private static DbAdapter dbAdapter = new DbAdapter();
	
	// TODO: убрать лишние делиты
	private static  String clearOd = "delete from od_maincash_document_km7; "
			+ "delete from od_maincash_document_pko; "
			+ "delete from od_maincash_document_rko; "
			+ "delete from od_maincash_document_rko_encashment; "
			+ "delete from od_maincash_document; "
			+ "delete from od_operday; "
			+ "delete from od_km3; "
			+ "delete from od_km3_row; "
			+ "delete from od_km6; ";
	
	private static  String clearRKODocs = "delete from od_maincash_document_rko;"
			+ " delete from od_maincash_document_rko_encashment;"
			+ " delete from od_maincash_document  where doc_type = 'RKO'";

	private static  String clearPKODocs = "delete from od_maincash_document_pko;"
			+ " delete from od_maincash_document  where doc_type = 'PKO'";
	
	private static  String clearAutoGeneratedDocs = "delete from od_maincash_document  where doc_type = 'LKK' and operday = '%s'; "
			+ " delete from od_maincash_document  where doc_type = 'DDC' and operday = '%s'";
	
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
	
	private static  String makeShiftsGreen = "update od_shift set state = 1 where shiftcreate > '%s 00:00:00' and shiftcreate < '%s 23:59:59.999'"; 
	
	private static String ifOdExist = "select count(*) from od_operday where day = '%s' ";
	
	
	public static void checkOd(long date){
		String dateFormatted = DisinsectorTools.getDate("yyyy-MM-dd", date);
		log.info("Проверить, существует ли ОД на дату: " + dateFormatted);
		int result =  dbAdapter.queryForInt(DB_RETAIL_OPERDAY, String.format(ifOdExist , dateFormatted));
		if (result == 0){
			Long[] operdays = new Long[1];
			operdays[0] = 0L;
			createODWithCashDocs(operdays);
		} 
	}
	
	/*
	 * Удалить ОД и все документы Главной кассы:
	 * КМ3, КМ6, КМ7, ПКО, РКО, ДДС, ЛКК
	 */
	public static void clearOD(){
		dbAdapter.updateDb(DB_RETAIL_OPERDAY, clearOd);
		log.info("Удалены все опер.дни и документы ГК!");
	}
	
	/*
	 * удалить все документы РКО
	 */
	public static void clearRKODocs(){
		dbAdapter.updateDb(DB_RETAIL_OPERDAY, clearRKODocs);
		log.info("Удалены все документы РКО");
	}
	
	/*
	 * удалить все документы ПКО
	 */
	public static void clearPKODocs(){
		dbAdapter.updateDb(DB_RETAIL_OPERDAY, clearPKODocs);
		log.info("Удалены все документы РКО");
	}
	
	/*
	 * Следующий номер документа в рамках типа
	 */
	public static int getNexDocNumberForType(String docType){
		return dbAdapter.queryForInt(DB_RETAIL_OPERDAY, String.format("select max(doc_number) from od_maincash_document where doc_type = '%s'", getDbDoctype(docType))) + 1;
	}
	
	/*
	 * удалить последний документ ПКО Выручка
	 */
	public static void clearLastPKORevenuDoc(Long date){
		String dateFormatted = DisinsectorTools.getDate("yyyy-MM-dd", date);
		Integer id = dbAdapter.queryForInt(DB_RETAIL_OPERDAY, 
				String.format("select id from od_maincash_document where id in (select id from od_maincash_document_pko where doc_sub_type = 'PKO_REVENUE_STORE') and operday = '%s'", dateFormatted ));
		dbAdapter.updateDb(DB_RETAIL_OPERDAY, String.format("delete from od_maincash_document_pko  where id = %s", id));
		dbAdapter.updateDb(DB_RETAIL_OPERDAY, String.format("delete from od_maincash_document   where id = %s", id));
		
		log.info("Удален последний документ ПКО Выручка");
	}
	
	/*
	 * удалить автоматически создаваемые документы  ДДС, ЛКК за дату
	 */
	public static void clearLastAutoDocs(Long date){
		String dateFormatted = DisinsectorTools.getDate("yyyy-MM-dd", date);
		dbAdapter.updateDb(DB_RETAIL_OPERDAY, String.format(clearAutoGeneratedDocs, dateFormatted, dateFormatted));
		log.info("Удалены последние автоматические документы:  ДДС, ЛКК");
	}
	
	/*
	 * удалить  документ КМ7 за дату
	 */
	public static void clearLastKM7Doc(Long date){
		String dateFormatted = DisinsectorTools.getDate("yyyy-MM-dd", date);
		dbAdapter.updateDb(DB_RETAIL_OPERDAY, String.format("delete from od_maincash_document  where doc_type = 'KM7' and  operday = '%s'", dateFormatted));
		log.info("Удалены автоматические документы КМ7 за " + dateFormatted);
	}
	
	/*
	 * Принудительно позеленить все смены на дату
	 */
	public static void makeShiftsGreenForDate(Long date){
		String dateFormatted = DisinsectorTools.getDate("yyyy-MM-dd", date);
		dbAdapter.updateDb(DB_RETAIL_OPERDAY, String.format(makeShiftsGreen, dateFormatted, dateFormatted));
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
			log.info("Добавить привелегии Админу на просмотр и включение главной кассы");
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
	
	/* TODO: исправить метод, чтобы обрабатывал все типы документов
	 * 
	 */
	private static String getDbDoctype(String docType){
		String db_docType = "PKO";
		switch (docType) {
			case MainCashDoc.DOC_TYPE_DDS : db_docType = "DDC"; break;
			case MainCashDoc.DOC_TYPE_KM7 : db_docType = "KM7"; break;
			case MainCashDoc.DOC_TYPE_LKK : db_docType = "LKK"; break;
			case MainCashDoc.DOC_TYPE_PKO_REVENUE : db_docType = "PKO"; break;
		};
		
		return db_docType;
	}
	
}
