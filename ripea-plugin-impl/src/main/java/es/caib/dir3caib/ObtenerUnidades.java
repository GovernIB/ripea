package es.caib.dir3caib;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.3.9
 * 2014-09-29T08:19:59.817+02:00
 * Generated source version: 2.3.9
 * 
 */
@WebService(targetNamespace = "http://www.caib.es/dir3caib", name = "ObtenerUnidades")
@XmlSeeAlso({ObjectFactory.class})
public interface ObtenerUnidades {

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "obtenerUnidad", targetNamespace = "http://www.caib.es/dir3caib", className = "es.caib.dir3caib.ObtenerUnidad")
    @WebMethod
    @ResponseWrapper(localName = "obtenerUnidadResponse", targetNamespace = "http://www.caib.es/dir3caib", className = "es.caib.dir3caib.ObtenerUnidadResponse")
    public es.caib.dir3caib.UnidadTF obtenerUnidad(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        java.lang.String arg1
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "obtenerArbolUnidades", targetNamespace = "http://www.caib.es/dir3caib", className = "es.caib.dir3caib.ObtenerArbolUnidades")
    @WebMethod
    @ResponseWrapper(localName = "obtenerArbolUnidadesResponse", targetNamespace = "http://www.caib.es/dir3caib", className = "es.caib.dir3caib.ObtenerArbolUnidadesResponse")
    public java.util.List<es.caib.dir3caib.UnidadTF> obtenerArbolUnidades(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        java.lang.String arg1
    );
}
