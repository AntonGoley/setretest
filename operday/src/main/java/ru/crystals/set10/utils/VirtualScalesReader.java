package ru.crystals.set10.utils;


import static ru.crystals.set10.config.Config.VIRTUAL_WEIGHT_PATH;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import ru.crystals.scales.tech.core.scales.virtual.xml.LinkToPluType;
import ru.crystals.scales.tech.core.scales.virtual.xml.Links;
import ru.crystals.scales.tech.core.scales.virtual.xml.PluType;

public class VirtualScalesReader {
	
	private static final Logger log = Logger.getLogger(VirtualScalesReader.class);
	private URL virtualScales;
	
	public static final int FILE_EXIST_RESPONSE = 200;
	public static final int FILE_DELETED_RESPONSE = 404;
	private static final int defaultTimeout = 60;
	private Iterator<LinkToPluType> pluOnScales;
	
	HttpURLConnection  connection;
	StringBuffer vScalesFileContent = new StringBuffer();

	public VirtualScalesReader(){
		try {
			virtualScales = new URL(VIRTUAL_WEIGHT_PATH);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Iterator<LinkToPluType> readVirtualScales(){
		Iterator<LinkToPluType> result = null;
		try {
			Links links = new Links();
			Unmarshaller unmarchaller;
			JAXBContext context = null;
            context = JAXBContext.newInstance(Links.class.getPackage().getName());
            unmarchaller = context.createUnmarshaller();
            links = (Links)unmarchaller.unmarshal(virtualScales);
            
            result = links.getLinkToPlu().iterator();

	        } catch (JAXBException e) {
	            e.printStackTrace();
			}
		return result;
	}
	
	/*
	 * Метод возвращает true, если товар с pluNumber 
	 * загружен в весы
	 */
	public boolean waitPluLoaded(int pluNumber){
		int timeout = defaultTimeout;
		
		try {
			getExpectedFileStatus(FILE_EXIST_RESPONSE);
		} catch (Exception e){
			return false;
		}
		
		log.info("Ожидание загрузки PLU с номером " + pluNumber + " в весы");
		while (timeout > 0){
			timeout--;
			pluOnScales = readVirtualScales();
			
			/* если в весах пусто*/
			if (pluOnScales == null) {
				continue;
			}
			
			while(pluOnScales.hasNext()){
				if (pluOnScales.next().getPlu().getNumber() == pluNumber){
					return true;
				}
			}
			DisinsectorTools.delay(1000);
		}
		log.info("Превышено время ожидания загрузки PLU с номером " + pluNumber + " в весы: " + defaultTimeout);
		return false;
	}
	
	/*
	 * Метод возвращает true, если товар с pluNumber 
	 * удален из весов
	 */
	public boolean waitPluUnLoaded(int pluNumber){
		int timeout = defaultTimeout;
		
		try {
			getExpectedFileStatus(FILE_EXIST_RESPONSE);
		} catch (Exception e){
			return false;
		}
		
		log.info("Ожидание выгрузки PLU с номером " + pluNumber + " из весов");
		while (timeout > 0){
			timeout--;
			pluOnScales = readVirtualScales();
			
			/* если в весах пусто, значит товар выгрузился*/
			if (!pluOnScales.hasNext()) {
				return true;
			}
			
			while(pluOnScales.hasNext()){
				if (pluOnScales.next().getPlu().getNumber() == pluNumber){
					break;
				}
				if (!pluOnScales.hasNext()){
					return true;
				}
			}
			DisinsectorTools.delay(1000);
		}
		log.info("Превышено время ожидания выгрузки PLU с номером " + pluNumber + " из весов: " + defaultTimeout);
		return false;
	}
	
	public PluType getPlu(int pluNumber){
		PluType plu;
		
		if(!waitPluLoaded(pluNumber)){
			log.info("PLU с номером " + pluNumber + " не загружено в весы!");
			return null;
		};
		
		pluOnScales = readVirtualScales();
		while(pluOnScales.hasNext()){
			plu = pluOnScales.next().getPlu();
			if (plu.getNumber() == pluNumber){
				return plu;
			}
		}
		log.info("PLU с номером " + pluNumber + " не загружено в весы!");
		return null;
	}
	
	/*
	 * Метод возващает plu,
	 * когда переданный plu неравен resultPlu в весах с таким же номером.
	 * Необходимо для понимания, что plu в весах обновился
	 */
	public PluType getPluUpdated(PluType plu){
		log.info("Ожидание обновления PLU = " +  plu.getNumber() + " в виртуальных весах..");
		PluType resultPlu = plu;
		int timeout = defaultTimeout;
		
		while (timeout > 0){
			timeout--;
			pluOnScales = readVirtualScales();
			while(pluOnScales.hasNext()){
				resultPlu = pluOnScales.next().getPlu();
				if (resultPlu.getNumber() == plu.getNumber()){
					//TODO: сравнить объекты
					if ( resultPlu.getPrice() != plu.getPrice() || 
						 resultPlu.getExPrice() != plu.getExPrice() )
					{
						log.info("PLU = " +  plu.getNumber() + " успешно обновлен");
						return resultPlu;
					}
				}
			}
			DisinsectorTools.delay(1000);
		}
		log.info("Товар в весах не обновился в течение " + defaultTimeout + ". PLU = " + plu.getNumber());
		return resultPlu;
	}
	
	
	
	/*
	 * Проверить, статус файла виртуальных весов
	 * 404 - удален
	 * 200 - создан
	 */
	public boolean getExpectedFileStatus(int responseCode){
		boolean result =  false;
		long delay = 1000;
		long timeout = defaultTimeout;
		try {
			//log.info("Ожидание респонс кода для файла виртуальных весов: " + responseCode);
			while (timeout > 0){
				connection = (HttpURLConnection) virtualScales.openConnection();
				connection.setDoInput(true);
				connection.setRequestMethod("GET");
				
				if (connection.getResponseCode() == responseCode){
					//log.info("Время ожидания обновления файла виртуальных весов: " + timeout);
					return true;
				}
				connection.connect();
				timeout--;
				DisinsectorTools.delay(delay);
			}

			throw new Exception("Время ожидания обновления файла виртуальных весов  превысило допустимое: " + defaultTimeout + ". Ожидаемый код ответа: " + responseCode);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public void clearVScalesFileData(){
		try {
			connection = (HttpURLConnection) virtualScales.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("DELETE");
			connection.connect();
			log.info("Код ответа серверва: " + connection.getResponseCode());
			connection.disconnect();
			/*
			 * Проверить, что файл весов удален
			 */
			getExpectedFileStatus(FILE_DELETED_RESPONSE);
			
			log.info("Файл " + VIRTUAL_WEIGHT_PATH + " удален!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
