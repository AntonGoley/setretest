package ru.crystals.set10.pages.operday.cashes;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.*;

import edu.emory.mathcs.backport.java.util.Collections;
import ru.crystals.set10.pages.basic.WarningPopUpMessage;
import ru.crystals.set10.test.maincash.MainCashDoc;
import ru.crystals.set10.test.maincash.MainCashDoc.MainCashDocStatus;
import static ru.crystals.set10.utils.FlexMediator.*;


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
	
	public BigDecimal getBalanceEnd(){
		return parseBalance(BALANCE_END);
	}
	
	public BigDecimal getBalanceStart(){
		return parseBalance(BALANCE_START);
	}
	
	private BigDecimal parseBalance(String balanceType){
		String balanceOnScreen = getElementProperty(getDriver(), ID_OPERDAYSWF, balanceType, "text");
		log.info("Баланс главной кассы = " + balanceOnScreen);
		return new BigDecimal(balanceOnScreen.replace(" ", "").replace(",", "."));
	}
	
	public MainCashDocsPage closeOperdayAndSwitchBack(long date){
		selectODFromCalendar(date);
		closeOperday(date);
		return openTab(MainCashDocsPage.class, LOCATOR_MAINCASH_TAB);
	}
	
	public CashOperDayTabPage closeOperday(long date){
		try {
			return openTab(CashOperDayTabPage.class, LOCATOR_OPERDAY_TAB).closeOperDay();
		} catch (Exception e) {

			e.printStackTrace();
		}
		return new CashOperDayTabPage(getDriver());
	}
	
	public MainCashDocsPage reopenOperDayAndSwitchBack(long date){
		try {
			openTab(CashOperDayTabPage.class, LOCATOR_OPERDAY_TAB)
					.reopenOperDay()
					.makeDecision(WarningPopUpMessage.BUTTON_YES);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new CashesPage(getDriver()).openTab(MainCashDocsPage.class, MainCashDocsPage.LOCATOR_MAINCASH_TAB);
	}
	
	
	public void getDocsOnPage(){
		
		/* отключить обновление таблицы Документов ГК */
		doFlexProperty(getDriver(), ID_OPERDAYSWF, "id:mainCashDeskTab", new String[]{"docsUpdatingEnable", "false"});
		documents.clear();

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
					
//					String outcome = getElementProperty(getDriver(), ID_OPERDAYSWF, String.format("id:documentsTable/name:%s/className:DocumentPriceItemRenderer/id:textLabel|1", rowLocator), "text");
//					if(!outcome.equals("")){
//						doc.setOutcome(new BigDecimal(outcome.replace(",", ".")));
//					}
					
					String parsedStatus = getElementProperty(getDriver(), ID_OPERDAYSWF, String.format("id:documentsTable/name:%s/className:DocumentStatusItemRenderer/id:iconImage", rowLocator), "source");
					MainCashDocStatus status = MainCashDocStatus.GREY;
					if (parsedStatus.contains("green")) {
						status = MainCashDocStatus.GREEN;
					}
					if (parsedStatus.contains("yellow")) {
						status =MainCashDocStatus.YELLOW;
					}
					if (parsedStatus.contains("grey")) {
						status = MainCashDocStatus.GREY;
					}
					if (parsedStatus.contains("red")) {
						status = MainCashDocStatus.RED;
					}
					doc.setStatus(status);
					
					
//					Boolean printable = Boolean.valueOf(
//							getElementProperty(getDriver(), ID_OPERDAYSWF, String.format("id:documentsTable/name:%s/className:DocumentStatusItemRenderer/id:selectionCheckBox", rowLocator), "visible"));
//					doc.setPrinable(printable);
					
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
		log.info(String.format("Документ типа %s с номером %s не найден в таблице Документы!!!", type, number));
		
		return null;
	}
	
	public List<MainCashDoc> getDocByType(String type) throws Exception{
		if ( documents.isEmpty()) {
			getDocsOnPage();
		}
		
		List<MainCashDoc> resultList = new ArrayList<MainCashDoc>(0);
		
		MainCashDoc doc = new MainCashDoc();
		Iterator<MainCashDoc> docs = documents.iterator();
		while (docs.hasNext()){
			doc = docs.next();
			if (doc.getType().contains(type)){
				resultList.add(doc);
			}
		}
		return resultList;
	}
	
	
	public List<Integer> getNumbersByDocType(String type){
		List<Integer> result = new ArrayList<Integer>(0);
		
		if ( documents.isEmpty()) {
			getDocsOnPage();
		}	
		
		MainCashDoc doc = new MainCashDoc();
		Iterator<MainCashDoc> docs = documents.iterator();
		while (docs.hasNext()){
			doc = docs.next();
			if (doc.getType().contains(type)) {
				result.add(doc.getNumber());
			}
		}
		
		if (result.isEmpty()) {
			result.add(0);
		}
		
		Collections.sort(result);
		return result;
	}
	
	public void selectDocForPrinting(MainCashDoc doc){
		checkBoxValue(getDriver(), ID_OPERDAYSWF, String.format("id:documentsTable/name:%s/className:DocumentStatusItemRenderer/id:selectionCheckBox", doc.getFlexTableRowLocatorName()), true);
	}
	
	public void printDoc(MainCashDoc doc){
		selectDocForPrinting(doc);
		clickElement(getDriver(), ID_OPERDAYSWF, ID + BUTTON_PRINT_SELECTED);
	}
	
	public void selectDoc(String tableDoctype, String docNumber){
	}
	
}
