package ru.crystals.set10.utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import ru.crystals.scales.tech.core.scales.virtual.xml.LinkToPluType;
import ru.crystals.scales.tech.core.scales.virtual.xml.Links;
import ru.crystals.set10.test.weight.LinkToPluProcessor;
import ru.crystals.set10.test.weight.WeightAbstractTest;
import static ru.crystals.set10.config.Config.VIRTUAL_WEIGHT_PATH;

public class VirtualScalesReader {
	
	private static final Logger log = Logger.getLogger(VirtualScalesReader.class);
	private URL virtualScales;
	
	public static final int FILE_EXIST_RESPONSE = 200;
	public static final int FILE_DELETED_RESPONSE = 404;
	
	HttpURLConnection  connection;
	StringBuffer vScalesFileContent = new StringBuffer();

	public VirtualScalesReader(){
		try {
			virtualScales = new URL(VIRTUAL_WEIGHT_PATH);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getPluActionType(String pluNumber){
		long timeout = 0;
		vScalesFileContent.setLength(0);
		while (timeout < 60000) {	
			Iterator<LinkToPluType> iterator = readVirtualScales(); 
			LinkToPluType linkToPlu;
			while (iterator.hasNext()){
				linkToPlu = iterator.next();
				if (linkToPlu.getPlu().getNumber() == Integer.valueOf(pluNumber)){
					log.info(vScalesFileContent);
					return linkToPlu.getActionType();
				}
			}
			timeout+=500;
		}	
		log.info("PLU " + pluNumber + " не найден в заданиях на загрузку/выгрузку");
		log.info(vScalesFileContent);
		return "";
	}
	
	
	public String getPluPriceValue(String pluNumber, String priceNumber, String expectedValue){
		String result = "";
		long timeout = 0;
		vScalesFileContent.setLength(0);
		while (timeout < 60000) {	
			Iterator<LinkToPluType> iterator = readVirtualScales(); 
			LinkToPluType linkToPlu;
			while (iterator.hasNext()){
				linkToPlu = iterator.next();
				
				if (linkToPlu.getPlu().getNumber() == Integer.valueOf(pluNumber)){
					switch (priceNumber) {
						case  "price1" : result = String.valueOf(linkToPlu.getPlu().getPrice());
							break;
						case  "price2" : result = String.valueOf(linkToPlu.getPlu().getExPrice());		
							break;
					}
					if (result.equals(expectedValue)){
						log.info(vScalesFileContent);
						return result;
					};
				}
			}
			timeout+=500;
		}	
		log.info("PLU " + pluNumber + " не найден в заданиях на загрузку/выгрузку");
		log.info(vScalesFileContent);
		return result;
	}
	
	public Iterator<LinkToPluType> readVirtualScales(){
		Iterator<LinkToPluType> result = null;
		
		/*
		 * Проверить, что файл весов существует 
		 */
		if (!getExpectedFileStatus(FILE_EXIST_RESPONSE)) {
			try {
				throw new Exception("Файл весов не найден!!!");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		};
		
		try {	
			Links links = new Links();
			Unmarshaller unmarchaller;
			JAXBContext context = null;
            context = JAXBContext.newInstance(Links.class.getPackage().getName());
            unmarchaller = context.createUnmarshaller();
            links = (Links)unmarchaller.unmarshal(virtualScales);
            
            /*
             * Читаме файл виртуальных весов и записываем содержимое в 
             * vScalesFileContent
             */
            BufferedReader br = new BufferedReader(new InputStreamReader(virtualScales.openStream()));
            String fileContent;
            while ((fileContent = br.readLine())!=null) {
            	vScalesFileContent.append(fileContent).append("\n");
            }
            
            result = links.getLinkToPlu().iterator();

	        } catch (JAXBException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		return result;
	}
	
	public void getLinkByLPUNumber(String pluNumber){
			
	}
	
	/*
	 * Проверить, статус файла виртуальных весов
	 * 404 - удален
	 * 200 - существует
	 */
	public boolean getExpectedFileStatus(int responseCode){
		boolean result =  false;
		long timeout = 0 ;
		long delay = 1000;
		try {
			log.info("Ожидание респонс кода для файла виртуальных весов: " + responseCode);
			while (timeout < 60000){
				connection = (HttpURLConnection) virtualScales.openConnection();
				connection.setDoInput(true);
				connection.setRequestMethod("GET");
				
				
				DisinsectorTools.delay(delay);
				if (connection.getResponseCode() == responseCode){
					log.info("Время ожидания обновления файла виртуальных весов: " + timeout);
					return true;
				}
				timeout +=1000;
				connection.connect();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.info("Время ожидания обновления файла виртуальных весов: " + timeout);
		return result;
	}
	
	public void clearVScalesFileData(){
		try {
			connection = (HttpURLConnection) virtualScales.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("DELETE");
			connection.connect();
			log.info("При удалении файла весов код ответа серверва: " + connection.getResponseCode());
			connection.disconnect();
			/*
			 * Проверит, что файл весов удален
			 */
			getExpectedFileStatus(FILE_DELETED_RESPONSE);
			
			log.info("Файл " + VIRTUAL_WEIGHT_PATH + " удален!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
