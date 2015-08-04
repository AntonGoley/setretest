package ru.crystals.set10.utils;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;


public class FlexMediator {

	protected static final Logger log = Logger.getLogger(FlexMediator.class);
	
	public static void clickElement(WebDriver driver, String swfSrc, String flexId) {
		waitForElement(driver, swfSrc, flexId);
		ecxecute(driver, String.format("document.getElementById('%s').doFlexClick('%s')", swfSrc, flexId));
	}

	public static void typeText(WebDriver driver, String swfSrc, String flexId, String text) {
		waitForElement(driver, swfSrc, flexId);
		ecxecute(driver, String.format("document.getElementById('%s').doFlexType('%s', '%s')", swfSrc, flexId, text));	
	}
	
	public static void doFlexMouseDown(WebDriver driver, String swfSrc, String flexId) {
		waitForElement(driver, swfSrc, flexId);
		ecxecute(driver, String.format("document.getElementById('%s').doFlexMouseDown('%s')", swfSrc, flexId));
	}
	
	public static void doFlexMouseUp(WebDriver driver, String swfSrc, String flexId, Boolean passToParents) {
		waitForElement(driver, swfSrc, flexId);
		ecxecute(driver, String.format("document.getElementById('%s').doFlexMouseUp('%s')", swfSrc, flexId, passToParents.toString()));
	}
	
	public static void checkBoxValue(WebDriver driver, String swfSrc, String flexId, boolean checkBoxValue) {
		waitForElement(driver, swfSrc, flexId);
		ecxecute(driver, String.format("document.getElementById('%s').doFlexCheckBox('%s', '%s')", swfSrc, flexId, checkBoxValue));	
	}
	
	public static void radioButtonValue(WebDriver driver, String swfSrc, String flexId, boolean value) {
		waitForElement(driver, swfSrc, flexId);
		ecxecute(driver, String.format("document.getElementById('%s').doFlexProperty('%s', 'selected', '%s')", swfSrc, flexId, value));	
	}
	
	
	/*
	 * Вернуть свойство элемента, который представлен на странице и visible
	 */
	public static String getElementProperty(WebDriver driver, String swfSrc, String flexId, String propertyName) {
		waitForElement(driver, swfSrc, flexId);
		return ecxecuteAndReturnString(driver, String.format("return document.getElementById('%s').getFlexProperty('%s', '%s')", swfSrc, flexId, propertyName));	
	}
	
	public static void selectElement(WebDriver driver, String swfSrc, String flexId, String element) {
		waitForElement(driver, swfSrc, flexId);
		ecxecute(driver, String.format("document.getElementById('%s').doFlexSelect('%s', '%s')", swfSrc, flexId, element));	
	}
	
	public static String getSelectedElement(WebDriver driver, String swfSrc, String flexId) {
		waitForElement(driver, swfSrc, flexId);
		return ecxecuteAndReturnString(driver, String.format("return document.getElementById('%s').getFlexSelection('%s', '')", swfSrc, flexId));	
	}
	
	public static int getElementsNum(WebDriver driver, String swfSrc, String flexId){
		return  Integer.valueOf(
				ecxecuteAndReturnString(driver, String.format("return document.getElementById('%s').getElementsNum('%s')", swfSrc, flexId)));
	}
	/*
	 * Возвращает список найденных объектов флекс
	 * Каждый элемент списка содержит полный путь к объекту флекс
	 */
	public static ArrayList<String> findElements(WebDriver driver, String swfSrc, String flexId){
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<Object> objects = ecxecuteAndReturnArray(driver, String.format("return document.getElementById('%s').findElement('%s')", swfSrc, flexId));
		Iterator<Object> i = objects.iterator();
		while (i.hasNext()){
			String element = (String) i.next();
			result.add(element);
		}
		return result;
		
	}
	
	/*
	 * args - [property value] 
	 */
	public static void doFlexProperty(WebDriver driver, String swfSrc, String flexId, String[] args) {
		waitForElement(driver, swfSrc, flexId);
		ecxecute(driver, String.format("document.getElementById('%s').doFlexProperty('%s', '%s', '%s')", swfSrc, flexId, args[0], args[1]));	
	}
	
	public static void scrollTableDown(WebDriver driver, String swfSrc, String flexId){
		String maxVerticalScrollPosition = getElementProperty(driver, swfSrc, flexId, "maxVerticalScrollPosition"); 
		doFlexProperty(driver, swfSrc, flexId, new String[]{"verticalScrollPosition", maxVerticalScrollPosition});
	}
	
	
	public static void  waitForElement(WebDriver driver, String swfSrc, String flexId) {
		
		if (!waitForElementPresent(driver, swfSrc, flexId)) {
			throw new NoSuchElementException("Не найден элемент: " + flexId);
		}
		
		if (!waitForElementVisible(driver, swfSrc, flexId)){
			throw new NoSuchElementException("Не найден элемент: " + flexId);
		};
	}
	
	public static boolean  waitForElementPresent(WebDriver driver, String swfSrc, String flexId){
		DisinsectorTools.delay(100);
		int timeout = 0;

		while (timeout < 25000 ){
			
			ArrayList<Object> result = ecxecuteAndReturnArray(driver, String.format("return document.getElementById('%s').findElement('%s')", swfSrc, flexId));
			if (result.size() > 0) {
				return true;
			}
			
			DisinsectorTools.delay(200);
			timeout+=200;
		}
		return false;
	}
	
	public static boolean waitForElementVisible(WebDriver driver, String swfSrc, String flexId) {
		return waitForProperty(driver, swfSrc, flexId, new String[]{"visible", "true"});
	}
	 
	/*
	 * args - [property expectedValue] 
	 * ожидание 15 сек
	 */
	public static boolean waitForProperty(WebDriver driver, String swfSrc, String flexId, String[] args) {
		String result;
		int timeout = 0;
		while (timeout < 120000 ){
			result = (String) ecxecuteAndReturnString(driver, String.format("return document.getElementById('%s').getFlexProperty('%s', '%s')", swfSrc, flexId, args[0]));
			if (result != null && result.equals(args[1])) {
				return true;
			}	
			DisinsectorTools.delay(200);
			timeout+=200;
		}	
		return false;
	}
	
	private static String ecxecuteAndReturnString(WebDriver driver, String command) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		return (String) js.executeScript(command);
	}
	
	/*
	 * TODO: сделать обрабоку для findElement
	 */
	private static ArrayList<Object> ecxecuteAndReturnArray(WebDriver driver, String command) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		ArrayList<Object> result = (ArrayList<Object>) js.executeScript(command);
		return result;
	}
	
	
	private static void ecxecute(WebDriver driver, String command) {
		String result;
;		JavascriptExecutor js = (JavascriptExecutor) driver;
		result = (String)js.executeScript(command);
		if (result!=null) {
			log.info(result);
		};
	}
	
}
