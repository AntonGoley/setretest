/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.crystals.set10.amfcall;

/**
 *
 * @author A.Dashkovskiy
 */
public class AMFServerConnectorException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AMFServerConnectorException(String message) {
        super(message);
    }

    public AMFServerConnectorException(String message, Throwable cause) {
        super(message, cause);
    }

}
