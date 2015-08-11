package ru.crystals.set10.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;


import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Logger;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;


public class DisinsectorTools {
	
	protected static final Logger log = Logger.getLogger(DisinsectorTools.class);
	
	/*
	 * Копирует в буфер обмена содержание страницы браузера
	 */
	public static String getConsoleOutput(WebDriver driver)  {
		String result = "";
		try {
		driver.manage().window().setPosition(new Point(1, 1));
		
		Actions action = new Actions(driver);
		action.keyDown(Keys.CONTROL).perform();
		action.sendKeys("a").perform();
		action.sendKeys("c").perform();
		action.keyUp(Keys.CONTROL).perform();
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		
	
		result = (String) clipboard.getData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.info("Report text contains: \n" + result);
		return result;
	}
	
	public static String getFileContentAsString(String filePath) {
		String result = "";
		try (
				InputStream input = DisinsectorTools.class.getClassLoader().getResourceAsStream("datafiles/" + filePath);
				InputStreamReader ir =new InputStreamReader(input, "UTF-8");	
				BufferedReader br = new BufferedReader(ir);
		)		
		{	
			String line = "";
			while ((line = br.readLine())!=null){
				result+=line;
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			
		}
		return result;
	}
	
	public static void removeOldReport(String chromeDownloadPath, String filePattern){
		File[] fileNames = fileFilter(chromeDownloadPath, filePattern);
		
		for (int i=0; i<fileNames.length; i++) {
			fileNames[i].delete();
		}
	}
	
	/*
	 * возвращает файлы по маске @filter
	 */
	public static File[] fileFilter(String directory, String filter){
		File dir = new File(directory);
		FileFilter fileFilter = new WildcardFileFilter(filter);
		File[] files = dir.listFiles(fileFilter);
		return files;
	}	
	
	/*
	 * Метод проверяет есть ли файлы, совпадающие по маске @filter
	 *  и если есть, возвращает первый файл в списке
	 */
	public static File getDownloadedFile(String directory, String filter){
		long waitTime = 0;
		while (waitTime < 30000) {
			if (fileFilter(directory, filter).length == 0) {
				delay(200);
				waitTime += 200;	
			} else {
				log.info("Файл отчета загрузился. Примерное время загрузки " + waitTime);
				return fileFilter(directory, filter)[0];
			}
		}
		log.info(String.format("Файлы, соответсвующие маске %s не найдены!", filter));
		return new File("");
	}

	public static void delay(long timeOut){
		try {
			Thread.sleep(timeOut);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public  static String getDate(String format, long date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}
	
	public static long random(int max) {
	    return Math.round(Math.random() * max);
	}
	
	/*
	 * Генерация рандомного значения суммы денег
	 */
	public static String randomMoney(int max, String delimiter) {
		return String.valueOf(Math.round(Math.random() * max) + 1)
				+ delimiter
				+ String.valueOf(Math.round(Math.random() * 88) + 11);
	}
	
}
