package es.caib.ripea.core.api.service.bantel.ws.v2;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import es.caib.ripea.core.api.service.bantel.ws.v2.BantelBackofficeWsException;

/**
 * WS per emular la interfície Bantel de Sistra per a que els backoffices tipus
 * Sistra pugin consultar a Ripea les entrades notificades a través anteriorment.
 * Aquests tres mètodes permeten obtenir les dades de les entrades en el registre
 * i establir el resultat del procés si han processat les dades del tràmit.
 * 
 */
 
@WebService (
		targetNamespace = "urn:es:caib:bantel:ws:v2:services", 
		name = "BackofficeFacade")
@XmlSeeAlso({es.caib.ripea.core.api.service.bantel.ws.v2.model.ObjectFactory.class})
public interface BantelBackofficeWs {

	/**
	 * Obté les dades per a un número d'entrada específic.
	 * 
	 * @param numeroEntrada
	 * @return
	 * @throws BantelBackofficeWsException
	 */
    @ResponseWrapper(localName = "obtenerEntradaResponse", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade", className = "es.caib.ripea.core.api.service.bantel.ws.v2.ObtenerEntradaResponse")
    @RequestWrapper(localName = "obtenerEntrada", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade", className = "es.caib.ripea.core.api.service.bantel.ws.v2.ObtenerEntrada")
    @WebResult(name = "obtenerEntradaReturn", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade")
    @WebMethod
    public es.caib.ripea.core.api.service.bantel.ws.v2.model.TramiteBTE obtenerEntrada(
        @WebParam(name = "numeroEntrada", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade")
        es.caib.ripea.core.api.service.bantel.ws.v2.model.ReferenciaEntrada numeroEntrada
    ) throws BantelBackofficeWsException;

    
    /** 
     * Estableix el resultat per al tràmit identificat per número. El resultat del procés
     * pot ser processat (S), no processat (N) o processat amb error (X).
     * 
     * @param numeroEntrada
     * @param resultado
     * @param resultadoProcesamiento
     * @throws BantelBackofficeWsException
     */
    @ResponseWrapper(localName = "establecerResultadoProcesoResponse", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade", className = "es.caib.ripea.core.api.service.bantel.ws.v2.model.EstablecerResultadoProcesoResponse")
    @RequestWrapper(localName = "establecerResultadoProceso", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade", className = "es.caib.ripea.core.api.service.bantel.ws.v2.model.EstablecerResultadoProceso")
    @WebMethod
    public void establecerResultadoProceso(
        @WebParam(name = "numeroEntrada", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade")
        es.caib.ripea.core.api.service.bantel.ws.v2.model.ReferenciaEntrada numeroEntrada,
        @WebParam(name = "resultado", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade")
        java.lang.String resultado,
        @WebParam(name = "resultadoProcesamiento", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade")
        java.lang.String resultadoProcesamiento
    ) throws BantelBackofficeWsException;

    
    /**
     * Mètode de consulta per obtenir diferents números d'entrada filtrant per identificador 
     * de procés, tràmit, procesada i dates desde fins.
     * 
     * @param identificadorProcedimiento
     * @param identificadorTramite
     * @param procesada
     * @param desde
     * @param hasta
     * @return
     * @throws BantelBackofficeWsException
     */
    @ResponseWrapper(localName = "obtenerNumerosEntradasResponse", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade", className = "es.caib.ripea.core.api.service.bantel.ws.v2.ObtenerNumerosEntradasResponse")
    @RequestWrapper(localName = "obtenerNumerosEntradas", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade", className = "es.caib.ripea.core.api.service.bantel.ws.v2.ObtenerNumerosEntradas")
    @WebResult(name = "obtenerNumerosEntradasReturn", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade")
    @WebMethod
    public es.caib.ripea.core.api.service.bantel.ws.v2.model.ReferenciasEntrada obtenerNumerosEntradas(
        @WebParam(name = "identificadorProcedimiento", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade")
        java.lang.String identificadorProcedimiento,
        @WebParam(name = "identificadorTramite", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade")
        java.lang.String identificadorTramite,
        @WebParam(name = "procesada", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade")
        java.lang.String procesada,
        @WebParam(name = "desde", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade")
        javax.xml.datatype.XMLGregorianCalendar desde,
        @WebParam(name = "hasta", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade")
        javax.xml.datatype.XMLGregorianCalendar hasta
    ) throws BantelBackofficeWsException;
}
