
package es.caib.ripea.plugin.caib.bantel;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b14002
 * Generated source version: 2.2
 * 
 */
@WebService(name = "BackofficeFacade", targetNamespace = "urn:es:caib:bantel:ws:v2:services")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface BackofficeFacade {


    /**
     * 
     * @param identificadorTramite
     * @param procesada
     * @param identificadorProcedimiento
     * @param hasta
     * @param desde
     * @return
     *     returns es.caib.ripea.plugin.caib.bantel.ReferenciasEntrada
     * @throws BackofficeFacadeException_Exception
     */
    @WebMethod
    @WebResult(name = "obtenerNumerosEntradasReturn", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade")
    @RequestWrapper(localName = "obtenerNumerosEntradas", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade", className = "es.caib.ripea.plugin.caib.bantel.ObtenerNumerosEntradas")
    @ResponseWrapper(localName = "obtenerNumerosEntradasResponse", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade", className = "es.caib.ripea.plugin.caib.bantel.ObtenerNumerosEntradasResponse")
    public ReferenciasEntrada obtenerNumerosEntradas(
        @WebParam(name = "identificadorProcedimiento", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade")
        String identificadorProcedimiento,
        @WebParam(name = "identificadorTramite", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade")
        String identificadorTramite,
        @WebParam(name = "procesada", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade")
        String procesada,
        @WebParam(name = "desde", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade")
        XMLGregorianCalendar desde,
        @WebParam(name = "hasta", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade")
        XMLGregorianCalendar hasta)
        throws BackofficeFacadeException_Exception
    ;

    /**
     * 
     * @param numeroEntrada
     * @param resultado
     * @param resultadoProcesamiento
     * @throws BackofficeFacadeException_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "establecerResultadoProceso", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade", className = "es.caib.ripea.plugin.caib.bantel.EstablecerResultadoProceso")
    @ResponseWrapper(localName = "establecerResultadoProcesoResponse", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade", className = "es.caib.ripea.plugin.caib.bantel.EstablecerResultadoProcesoResponse")
    public void establecerResultadoProceso(
        @WebParam(name = "numeroEntrada", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade")
        ReferenciaEntrada numeroEntrada,
        @WebParam(name = "resultado", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade")
        String resultado,
        @WebParam(name = "resultadoProcesamiento", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade")
        String resultadoProcesamiento)
        throws BackofficeFacadeException_Exception
    ;

    /**
     * 
     * @param numeroEntrada
     * @return
     *     returns es.caib.ripea.plugin.caib.bantel.TramiteBTE
     * @throws BackofficeFacadeException_Exception
     */
    @WebMethod
    @WebResult(name = "obtenerEntradaReturn", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade")
    @RequestWrapper(localName = "obtenerEntrada", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade", className = "es.caib.ripea.plugin.caib.bantel.ObtenerEntrada")
    @ResponseWrapper(localName = "obtenerEntradaResponse", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade", className = "es.caib.ripea.plugin.caib.bantel.ObtenerEntradaResponse")
    public TramiteBTE obtenerEntrada(
        @WebParam(name = "numeroEntrada", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade")
        ReferenciaEntrada numeroEntrada)
        throws BackofficeFacadeException_Exception
    ;

}
