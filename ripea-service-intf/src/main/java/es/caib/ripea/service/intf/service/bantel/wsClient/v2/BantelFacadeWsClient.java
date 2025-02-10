package es.caib.ripea.service.intf.service.bantel.wsClient.v2;

import es.caib.ripea.service.intf.service.bantel.wsClient.v2.model.ObjectFactory;
import es.caib.ripea.service.intf.service.bantel.wsClient.v2.model.ReferenciasEntrada;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * Interfície per al client del WS de Bantel per a notificar anotacions de registre a 
 * backoffices tipus sistra.
 * Quan es defineix una regla de tipus backoffice a Ripea es pot escollir si el backoffice és
 * de tipus Ripea o Sistra. En el cas que sigui tipus Sistra Ripea notificarà mitjançant
 * aquesta interfície les noves entrades als backoffices de tipus Sistra.
 * 
 */
 
@WebService(targetNamespace = "urn:es:caib:bantel:ws:v2:services", name = "BantelFacade")
@XmlSeeAlso({ObjectFactory.class})
public interface BantelFacadeWsClient {

    @ResponseWrapper(localName = "avisoEntradasResponse", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BantelFacade", className = "es.caib.bantel.wsClient.v2.model.AvisoEntradasResponse")
    @RequestWrapper(localName = "avisoEntradas", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BantelFacade", className = "es.caib.bantel.wsClient.v2.model.AvisoEntradas")
    @WebMethod
    public void avisoEntradas(
        @WebParam(name = "numeroEntradas", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BantelFacade")
        ReferenciasEntrada numeroEntradas
    ) throws BantelFacadeException;
}
