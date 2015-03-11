package ru.crystals.set10.utils;

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
	
	public static void checkBoxValue(WebDriver driver, String swfSrc, String flexId, boolean checkBoxValue) {
		waitForElement(driver, swfSrc, flexId);
		ecxecute(driver, String.format("document.getElementById('%s').doFlexCheckBox('%s', '%s')", swfSrc, flexId, checkBoxValue));	
	}
	
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
	 * args - [property value] 
	 */
	public static void doFlexProperty(WebDriver driver, String swfSrc, String flexId, String[] args) {
		waitForElement(driver, swfSrc, flexId);
		ecxecute(driver, String.format("document.getElementById('%s').doFlexProperty('%s', '%s', '%s')", swfSrc, flexId, args[0], args[1]));	
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
		String result = "false";
		while (timeout < 15000 ){
			result = (String) ecxecuteAndReturnString(driver, String.format("return document.getElementById('%s').findElement('%s')", swfSrc, flexId));
			if (result.equals("true")) {
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
		while (timeout < 15000 ){
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
	
	private static void ecxecute(WebDriver driver, String command) {
		String result;
;		JavascriptExecutor js = (JavascriptExecutor) driver;
		result = (String)js.executeScript(command);
		if (result!=null) {
			log.info(result);
		};
	}
	
}
