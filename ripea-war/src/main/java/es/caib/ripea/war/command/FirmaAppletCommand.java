/**
 * 
 */
package es.caib.ripea.war.command;

import org.springframework.web.multipart.MultipartFile;

import es.caib.ripea.war.validation.ArxiuNoBuit;

/**
 * Command per a la firma de documents via applet.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FirmaAppletCommand {

	protected String identificador;
	@ArxiuNoBuit
	protected MultipartFile arxiu;

	public FirmaAppletCommand(String identificador) {
		super();
		this.identificador = identificador;
	}

	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador != null ? identificador.trim() : null;
	}
	public MultipartFile getArxiu() {
		return arxiu;
	}
	public void setArxiu(MultipartFile arxiu) {
		this.arxiu = arxiu;
	}

}
