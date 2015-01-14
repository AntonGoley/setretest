package ru.crystals.set10.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
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
				InputStreamReader ir =new InputStreamReader(new FileInputStream("target/test-classes/datafiles/" + filePath), "UTF-8");	
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
	
	// Удалить все файлы отчетов (*.xls; *.pdf) из папки загрузок хрома
	public  void removeOldDownloadedReports(String chromeDownloadPath){
		GenericExtFilter xlsFilter = new GenericExtFilter(".xls");
		GenericExtFilter xlsxFilter = new GenericExtFilter(".xlsx");
		GenericExtFilter pdfFilter = new GenericExtFilter(".pdf");
		
		File dir = new File(chromeDownloadPath);
		String[] xlsReportFileName = dir.list(xlsFilter);
		String[] xlsxReportFileName = dir.list(xlsxFilter);
		String[] pdfReportFileName = dir.list(pdfFilter);
		
		
		for (String filePath:xlsReportFileName) {
			new File(chromeDownloadPath + "/" + filePath).delete();
		}
		
		for (String filePath:pdfReportFileName) {
			new File(chromeDownloadPath + "/" + filePath).delete();
		}
		
		for (String filePath:xlsxReportFileName) {
			new File(chromeDownloadPath + "/" + filePath).delete();
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
				delay(500);
				waitTime += 500;	
			} else {
				log.info("Файл отчета загрузился. Примерное время загрузки " + waitTime);
				return fileFilter(directory, filter)[0];
			}
		}
		log.info(String.format("Файлы, соответсвующие маске %s не найдены!", filter));
		return new File("");
	}
	
	
	public class GenericExtFilter implements FilenameFilter {
		String ext;
		public GenericExtFilter(String ext) {
			this.ext = ext;
		}
 
		public boolean accept(File dir, String name) {
			return (name.endsWith(ext));
		}
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
	
	
}
