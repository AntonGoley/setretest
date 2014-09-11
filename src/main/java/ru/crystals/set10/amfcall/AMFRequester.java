/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.crystals.set10.amfcall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import flex.messaging.io.amf.client.AMFConnection;
import flex.messaging.io.amf.client.exceptions.ClientStatusException;

/**
 *
 * @author A.Dashkovskiy
 */
public class AMFRequester {

    public static final Logger LOG = LoggerFactory.getLogger(AMFRequester.class);
//    private String amfServletUrl;//"http://172.16.2.230:8090/SET-Equipment-Web/messagebroker/amf"
    private AMFConnection amfConnection = new AMFConnection();
//    private AMFConnectorStubGenerator stubGenerator = new AMFConnectorStubGenerator();

    public AMFRequester(String amfServletUrl) throws ClientStatusException {
        this.amfConnection.connect(amfServletUrl);
    }

//    public <T> T getStub(Class<T> remoteInterface, String serviceName) throws Exception {
//        return stubGenerator.getStub(this, remoteInterface, serviceName);
//    }

    public Object call(String serviceName, String methodName, Object[] params, String[] types) {
        Object result = null;
        try {
            result = amfConnection.call(serviceName + "." + methodName, params);
            LOG.info("AMFRequester.call return : {}", result);
        } catch (Exception ex) {
           throw new AMFServerConnectorException(methodName, ex);
        }
        return result;
    }
}
