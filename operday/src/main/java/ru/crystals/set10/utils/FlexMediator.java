package ru.crystals.set10.utils;

import org.apache.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;


public class FlexMediator {
	
	protected static final Logger log = Logger.getLogger(FlexMediator.class);
	
	public static void clickElement(WebDriver driver, String swfSrc, String flexId) {
		waitForElement(driver, swfSrc, flexId);
		ecxecute(driver, String.format("document.getElementById('%s').doFlexClick('%s', '')", swfSrc, flexId));	
	}
	
	/*
	 * Клик по табу и др. элементам, где передаются аргументы
	 */
	public static void clickElement(WebDriver driver, String swfSrc, String flexId, String arg) {
		waitForElement(driver, swfSrc, flexId);
		ecxecute(driver, String.format("document.getElementById('%s').doFlexClick('%s', '%s')", swfSrc, flexId, arg));	
	}

	public static void typeText(WebDriver driver, String swfSrc, String flexId, String text) {
		waitForElement(driver, swfSrc, flexId);
		ecxecute(driver, String.format("document.getElementById('%s').doFlexType('%s', '%s')", swfSrc, flexId, text));	
	}
	
	public static void doFlexMouseDown(WebDriver driver, String swfSrc, String flexId) {
		waitForElement(driver, swfSrc, flexId);
		ecxecute(driver, String.format("document.getElementById('%s').doFlexMouseDown('%s', '')", swfSrc, flexId));	
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
	
	/*
	 * args - [property value] 
	 */
	public static void doFlexProperty(WebDriver driver, String swfSrc, String flexId, String[] args) {
		waitForElement(driver, swfSrc, flexId);
		ecxecute(driver, String.format("document.getElementById('%s').doFlexProperty('%s', '%s', '%s')", swfSrc, flexId, args[0], args[1]));	
	}
	
	public static void waitForElement(WebDriver driver, String swfSrc, String flexId) {
		DisinsectorTools.delay(200);
		ecxecute(driver, String.format("document.getElementById('%s').doFlexWaitForElement('%s', '10000')", swfSrc, flexId));
		if (!waitForElementVisible(driver, swfSrc, flexId)){
			throw new NoSuchElementException("Не найден элемент: " + flexId);

		};
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
			if (result.equals(args[1])) {
				return true;
			}	
			sleep(200);
			timeout+=200;
		}	
		return false;
	}
	
	private static void sleep(int timeout) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
