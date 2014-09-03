package ru.crystals.set10.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;


public class FlexMediator {
	
	
	public static void clickElement(WebDriver driver, String swfSrc, String flexId) {
		waitForElement(driver, swfSrc, flexId);
		ecxecute(driver, String.format("document.getElementById('%s').doFlexClick('%s', '')", swfSrc, flexId));	
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
		sleep(500);
		// 30000 - about 15 sec??
		ecxecute(driver, String.format("document.getElementById('%s').doFlexWaitForElement('%s', '15')", swfSrc, flexId));	
	}
	 
	/*
	 * args - [property expectedValue] 
	 */
	public static boolean waitForProperty(WebDriver driver, String swfSrc, String flexId, String[] args) {
		String result;
		int timeout = 0;
		// TODO: заменить параметром
		while (timeout < 10000 ){
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
