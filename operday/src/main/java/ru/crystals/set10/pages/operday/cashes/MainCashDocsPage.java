package ru.crystals.set10.pages.operday.cashes;



import static ru.crystals.set10.utils.FlexMediator.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;


public class  MainCashDocsPage extends CashDocsAbstractPage {
	
	private static final String BUTTON_SELECT_ALL = "label:Выбрать все"; 
	private static final String BUTTON_ADD_DOC = "label:Добавить";
	private static final String BUTTON_DELETE_DOC = "label:Удалить";
	private static final String BUTTON_EDIT_DOC = "label:Редактировать";
	private static final String ID = "id:mainCashDeskDocuments/";
	
	public static final String BALANCE_START = "id:startBalanceLabel";
	public static final String BALANCE_END = "id:balanceLabel";
	

	public MainCashDocsPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	public MainCashManualDocPage editDoc(){
		//TODO: добавить выбор документа
		clickElement(getDriver(), ID_OPERDAYSWF, ID + BUTTON_EDIT_DOC);
		return new MainCashManualDocPage(getDriver());
	}
	
	public MainCashDocsPage deleteDoc(){
		//TODO: добавить выбор документа
		clickElement(getDriver(), ID_OPERDAYSWF, ID + BUTTON_DELETE_DOC);
		return this;
	}
	
	public MainCashManualDocPage addDoc(){
		clickElement(getDriver(), ID_OPERDAYSWF, ID + BUTTON_ADD_DOC);
		return new MainCashManualDocPage(getDriver());
	}
	
	public BigDecimal getBalance(String balance){
		String balanceOnScreen = getElementProperty(getDriver(), ID_OPERDAYSWF, balance, "text");
		log.info("Баланс главной кассы = " + balanceOnScreen);
		return new BigDecimal(balanceOnScreen.replace(" ", "").replace(",", "."));
		
	}
	
	/*
	 * Получить номер документа
	 */
	private int getFlexRowTableDocNum(String rowLocator){
		int result = 0;
		result = Integer.valueOf(
				getElementProperty(getDriver(), ID_OPERDAYSWF, String.format("id:documentsTable/name:%s/id:subContainer/className:Text|0", rowLocator), "text")); 
		return result;
	}
	
	/*
	 * Последний номер документа для типа документа (ПКО, РКО, ДДС, ЛКК, КМ7)
	 */
	public int getLastDocNumber(String tableDoctype){

		ArrayList<Integer> result = getDocNumbersForType(tableDoctype);
		
		if (result.isEmpty()){
			return 0;
		}
		return result.get(result.size() - 1);
	}
	
	/*
	 * Получить список имен FlexTable2RowRenderer для документов определенного типа 
	 */
	private ArrayList<String> getRowsWithDocType(String docType){
		
		ArrayList<String> resultDocs = new ArrayList<String>();

		/*
		 * Все строки в таблице документов
		 */
		ArrayList<String> docs = findElements(getDriver(), ID_OPERDAYSWF, "id:documentsTable/className:FlexTable2RowRenderer;visible:true");
		
		/*
		 * Паттерн для поиска конкретной строки таблицы
		 */
		Pattern pattern = Pattern.compile("FlexTable2RowRenderer(\\d+)");
		
		Iterator<String> i = docs.iterator();
		while(i.hasNext()){
			/* tableRow представляет локатор строки таблицы*/
			String tableRow = i.next();
			Matcher matcher = pattern.matcher(tableRow);
			if(matcher.find()){
				String rowLocator = matcher.group();
				/*
				 *  Проверить, не является ли это разделителем
				 *  у строки с документов, количество полей Text=3
				 */
				int separatorProps = getElementsNum(getDriver(), ID_OPERDAYSWF, String.format("id:documentsTable/name:%s/id:subContainer/className:Text", rowLocator));
				if (separatorProps == 3) {
					String docTypeValue = getElementProperty(getDriver(), ID_OPERDAYSWF, String.format("id:documentsTable/name:%s/id:subContainer/className:Text|1", rowLocator), "text");
					if (docTypeValue.contains(docType)){
						resultDocs.add(rowLocator);
					}
				};	
			};
		}
		return resultDocs;
	}
	
	/*
	 * Получить свойство документа:
	 * 
	 */
	public String getDocProperty(String docType, Integer number, String property){
		
		ArrayList<String> docs = getRowsWithDocType(docType);
		
		if (docs.isEmpty()){
			new Throwable(String.format("Документ %s главной кассы с номером %s не найден! ", docType, number  )); 
		}
		
		if (docs.size()>1){
			new Throwable(String.format("Найден больше чем один документ %s главной кассы с номером %s не найден! ", docType, number  )); 
		}
		
		return getElementProperty(getDriver(), ID_OPERDAYSWF, String.format("id:documentsTable/name:%s/id:subContainer/className:Text|1", docs.get(0)), "text");
	}
	
	/*
	 * Получить все номера для типа документа (ПКО, РКО, ДДС, ЛКК, КМ7)
	 */
	public ArrayList<Integer> getDocNumbersForType(String docType){
		ArrayList<Integer> docnums = new ArrayList<Integer>();
		
		/*
		 * Все документы типа docType
		 */
		ArrayList<String> docs = getRowsWithDocType(docType);
		
		Iterator<String> i = docs.iterator();
		while(i.hasNext()){
			int num = getFlexRowTableDocNum(i.next());
			if (num >0 ){ 
				docnums.add(num);
			}	
		}
		
		//TODO: сделать нормальную сортировку
		Collections.sort(docnums);
		return docnums;
	}
	
	
	public void getdocStatus(String tableDoctype, String docNumber){
	}
	
	
	public void getdocSum(String tableDoctype, String docNumber){
	}
	
	public void getdocPrintedStatus(String tableDoctype, String docNumber){
	}
	
	public void selectDocForPrinting(String tableDoctype, String docNumber){
	}
	
	public void selectDoc(String tableDoctype, String docNumber){
	}
	
}
