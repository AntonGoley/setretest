package ru.crystals.set10.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("Report text contains: \n" + result);
		return result;
	}
	
	public static String getFileContentAsString(String filePath) {
		String result = "";
		try (
				InputStreamReader ir =new InputStreamReader(new FileInputStream("src/test/resources/ru/crystals/dataFiles/" + filePath), "UTF-8");	
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
		GenericExtFilter pdfFilter = new GenericExtFilter(".pdf");
		
		File dir = new File(chromeDownloadPath);
		String[] xlsReportFileName = dir.list(xlsFilter);
		String[] pdfReportFileName = dir.list(pdfFilter);
		
		for (String filePath:xlsReportFileName) {
			new File(chromeDownloadPath + "/" + filePath).delete();
		}
		
		for (String filePath:pdfReportFileName) {
			new File(chromeDownloadPath + "/" + filePath).delete();
		}
	}
	
	public String getReportFileName(String path, String extension) {
		GenericExtFilter filter = new GenericExtFilter(extension);
		 
		File dir = new File(path);
		String[] reportFileName = dir.list(filter);
		if (reportFileName.length == 0) {
			log.info("no file report end with : " + extension);
			return "";
		}
		if (reportFileName.length > 1) {
			log.info("Previos report files won't deleted : " + extension);
			return "";
		}
		String result = new StringBuffer(path).append(File.separator)
				.append(reportFileName[0]).toString();
		return result;
	}
	
	// inner class, generic extension filter
	public class GenericExtFilter implements FilenameFilter {
		String ext;
		public GenericExtFilter(String ext) {
			this.ext = ext;
		}
 
		public boolean accept(File dir, String name) {
			return (name.endsWith(ext));
		}
	}
	
	public static void waitForDownloadComplete(File path) {
		long waitTime = 0;
		// ждем 30 секунд пока файл загрузится
		while (waitTime < 30000) {
			if (!path.exists()) {
				delay(500);
				waitTime += 500;	
			} else {
				log.info("Файл отчета загрузился. Примерное время загрузки " + waitTime);
				return;
			}
		}
		log.info("Файл отчета не загрузился! " + path);
	}
	
	
	
	public static void delay(long timeOut){
		try {
			Thread.sleep(timeOut);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
