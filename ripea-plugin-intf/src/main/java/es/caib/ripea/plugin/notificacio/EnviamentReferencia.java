/**
 * 
 */
package es.caib.ripea.plugin.notificacio;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació de referència d'un enviament retornada per Notifica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@JsonAutoDetect
@XmlRootElement
@Getter 
@Setter
public class EnviamentReferencia {

	private String titularNif;
	private String referencia;
}
