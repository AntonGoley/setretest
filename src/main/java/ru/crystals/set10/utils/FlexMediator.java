package ru.crystals.set10.utils;

import org.apache.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;


public class FlexMediator {
	
	protected static final Logger log = Logger.getLogger(FlexMediator.class);
	
	public static void clickElement(WebDriver driver, String swfSrc, String flexId) {
		waitForElement(driver, swfSrc, flexId);
		ecxecute(driver, String.format("document.getElementById('%s').doFlexClick('%s', '')", swfSrc, flexId));	
	}
	
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
	
	/*
	 * args - [property value] 
	 */
	public static void doFlexProperty(WebDriver driver, String swfSrc, String flexId, String[] args) {
		waitForElement(driver, swfSrc, flexId);
		ecxecute(driver, String.format("document.getElementById('%s').doFlexProperty('%s', '%s', '%s')", swfSrc, flexId, args[0], args[1]));	
	}
	
	
	public static void waitForElement(WebDriver driver, String swfSrc, String flexId) {
		//TODO
		// doFlexWaitForElement falls if no delay before execution
		//sleep(500);
		
		ecxecute(driver, String.format("document.getElementById('%s').doFlexWaitForElement('%s', '10000')", swfSrc, flexId));
		// TODO: магическое дублирование ожидание элемента - разобраться и убрать!
		//ecxecute(driver, String.format("document.getElementById('%s').doFlexWaitForElement('%s', '15')", swfSrc, flexId));
	}
	
	public static boolean waitForElementVisible(WebDriver driver, String swfSrc, String flexId) {
		String result = "";
		//TODO
		// doFlexWaitForElement falls if no delay before execution
		sleep(500);
		result = ecxecuteAndReturnString(driver, String.format("return document.getElementById('%s').doFlexWaitForElementVisible('%s', '15')", swfSrc, flexId));
		if ( result.equals("true")) return true;
		else return false;
	}
	 
	/*
	 * args - [property expectedValue] 
	 */
	public static boolean waitForProperty(WebDriver driver, String swfSrc, String flexId, String[] args) {
		String result;
		int timeout = 0;
		// TODO: заменить параметром
		while (timeout < 15000 ){
			sleep(1000);
			result = (String) ecxecuteAndReturnString(driver, String.format("return document.getElementById('%s').getFlexProperty('%s', '%s')", swfSrc, flexId, args[0]));
			if (result.equals(args[1])) {
				return true;
			}	
			timeout+=1000;
		}	
		return false;
	}
	
	private static void sleep(int timeout) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String ecxecuteAndReturnString(WebDriver driver, String command) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		return (String) js.executeScript(command);
	}
	
	private static void ecxecute(WebDriver driver, String command) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(command);
	}
	
	
	
}
