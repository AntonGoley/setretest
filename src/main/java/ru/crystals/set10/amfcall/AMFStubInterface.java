/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.crystals.set10.amfcall;

import flex.messaging.io.amf.client.AMFConnection;

/**
 *
 * @author A.Dashkovskiy
 */
public interface AMFStubInterface {
    public void _setRequester(AMFRequester requester);
    public void _setServerConnector(AMFConnection connection);
}
