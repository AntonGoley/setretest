package ru.crystals.set10.pages.operday.cashes;


import static ru.crystals.set10.utils.FlexMediator.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static ru.crystals.set10.utils.FlexMediator.clickElement;
import static ru.crystals.set10.utils.FlexMediator.getElementProperty;
import org.openqa.selenium.*;


import ru.crystals.set10.utils.DisinsectorTools;


/*
 *  Страница Документы на вкладке Кассы в опердне
 */
public class  MainCashDocsPage extends CashDocsAbstractPage {
	
	private static final String BUTTON_SELECT_ALL = "label:Выбрать все";
	private static final String BUTTON_PRINT_SELECTED= "label:Распечатать выбранные"; 
	private static final String BUTTON_ADD_DOC = "label:Добавить";
	private static final String BUTTON_DELETE_DOC = "label:Удалить";
	private static final String BUTTON_EDIT_DOC = "label:Редактировать";
	private static final String ID = "id:mainCashDeskDocuments/";
	
	public static final String BALANCE_START = "id:startBalanceLabel";
	public static final String BALANCE_END = "id:balanceLabel";
	
	
	List<MainCashDoc> documents = new ArrayList<MainCashDoc>();
	
	public MainCashDocsPage(WebDriver driver) {
		super(driver);
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
	
	private void getDocsOnPage(){
		documents.clear();
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
				 *  у строки с документом, количество полей Text=3
				 */
				int separatorProps = getElementsNum(getDriver(), ID_OPERDAYSWF, String.format("id:documentsTable/name:%s/id:subContainer/className:Text", rowLocator));
				
				if (separatorProps == 3) {
					
					MainCashDoc doc = new MainCashDoc();
					
					doc.setFlexTableRowLocatorName(rowLocator);
					
					String docTypeValue = getElementProperty(getDriver(), ID_OPERDAYSWF, String.format("id:documentsTable/name:%s/id:subContainer/className:Text|1", rowLocator), "text");
					doc.setType(docTypeValue);
					
					Integer docNumber = Integer.valueOf(
							getElementProperty(getDriver(), ID_OPERDAYSWF, String.format("id:documentsTable/name:%s/id:subContainer/className:Text|0", rowLocator), "text"));
					doc.setNumber(docNumber);
					
					String employee = getElementProperty(getDriver(), ID_OPERDAYSWF, String.format("id:documentsTable/name:%s/id:subContainer/className:Text|2", rowLocator), "text");
					doc.setEmployee(employee);
					
					String income = getElementProperty(getDriver(), ID_OPERDAYSWF, String.format("id:documentsTable/name:%s/className:DocumentPriceItemRenderer/id:textLabel|0", rowLocator), "text");
					if (!income.equals("")){
					 doc.setIncome(new BigDecimal(income.replace(",", ".")));
					} 
					
					String outcome = getElementProperty(getDriver(), ID_OPERDAYSWF, String.format("id:documentsTable/name:%s/className:DocumentPriceItemRenderer/id:textLabel|1", rowLocator), "text");
					if(!outcome.equals("")){
						doc.setIncome(new BigDecimal(outcome.replace(",", ".")));
					}
					
					String status = getElementProperty(getDriver(), ID_OPERDAYSWF, String.format("id:documentsTable/name:%s/className:DocumentStatusItemRenderer/id:iconImage", rowLocator), "source");
					doc.setStatus(status);
					
					documents.add(doc);
					
				};	
			};
		}
	}
	
	public MainCashDoc getDocByTypeAndNumber(String type, Integer number) throws Exception{
		if ( documents.isEmpty()) {
			getDocsOnPage();
		}
		
		MainCashDoc doc = new MainCashDoc();
		Iterator<MainCashDoc> docs = documents.iterator();
		while (docs.hasNext()){
			doc = docs.next();
			if (doc.getType().contains(type) &&
				 doc.getNumber().equals(number)){
					return doc;
			}
		}
		// TODO: эксепшн
		log.info(String.format("Документ типа %s с номерном %s не найден в таблице Документы!!!", type, number));
		
		return null;
	}
	
	public void getNumbersByDocType(String type){
		
		List<MainCashDoc> result = new ArrayList<MainCashDoc>();
		
		if ( documents.isEmpty()) {
			getDocsOnPage();
		}	
		
		MainCashDoc doc = new MainCashDoc();
		Iterator<MainCashDoc> docs = documents.iterator();
		while (docs.hasNext()){
			doc = docs.next();
			if (doc.getType().contains(type)) {
				result.add(doc);
			}
		}
	}
	
	public boolean ifPrintEnable(MainCashDoc doc){
		return Boolean.valueOf(
				getElementProperty(getDriver(), ID_OPERDAYSWF, String.format("id:documentsTable/name:%s/className:DocumentStatusItemRenderer/id:selectionCheckBox", doc.getFlexTableRowLocatorName()), "visible"));
	}
	
	public void selectDocForPrinting(MainCashDoc doc){
		checkBoxValue(getDriver(), ID_OPERDAYSWF, String.format("id:documentsTable/name:%s/className:DocumentStatusItemRenderer/id:selectionCheckBox", doc.getFlexTableRowLocatorName()), true);
	}
	
	public void printDoc(MainCashDoc doc){
		selectDocForPrinting(doc);
		clickElement(getDriver(), ID_OPERDAYSWF, ID + BUTTON_PRINT_SELECTED);
	}
	
	
	public void getdocStatus(String tableDoctype, String docNumber){
	}
	
	
	public void getdocSum(String tableDoctype, String docNumber){
	}
	
	public void getdocPrintedStatus(String tableDoctype, String docNumber){
	}
	
	public void selectDoc(String tableDoctype, String docNumber){
	}
	
}
