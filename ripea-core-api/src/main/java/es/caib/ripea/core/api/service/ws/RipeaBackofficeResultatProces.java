/**
 * 
 */
package es.caib.ripea.core.api.service.ws;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Resultat d'una petició al backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ripeaBackofficeResultatProces", propOrder = {
    "error",
    "errorCodi",
    "errorDescripcio"})
public class RipeaBackofficeResultatProces implements Serializable {

	@XmlElement(required = true)
	private boolean error;
	@XmlElement
	private String errorCodi;
	@XmlElement
	private String errorDescripcio;



	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public String getErrorCodi() {
		return errorCodi;
	}
	public void setErrorCodi(String errorCodi) {
		this.errorCodi = errorCodi;
	}
	public String getErrorDescripcio() {
		return errorDescripcio;
	}
	public void setErrorDescripcio(String errorDescripcio) {
		this.errorDescripcio = errorDescripcio;
	}

	private static final long serialVersionUID = -7784812872539907429L;

}
