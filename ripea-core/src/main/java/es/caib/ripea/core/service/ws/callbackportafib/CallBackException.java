
package es.caib.ripea.core.service.ws.callbackportafib;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@SuppressWarnings("serial")
@WebFault(name = "CallBackFault", targetNamespace = "http://v1.server.callback.ws.portafib.caib.es/")
public class CallBackException
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private CallBackFault faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public CallBackException(String message, CallBackFault faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public CallBackException(String message, CallBackFault faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: es.caib.portafib.ws.callback.server.v1.CallBackFault
     */
    public CallBackFault getFaultInfo() {
        return faultInfo;
    }

}
