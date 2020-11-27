package es.caib.ripea.plugin.viafirma;

import java.io.Serializable;
import java.util.Date;

/**
 * Resposta amb l'enllaç del document firmat de viaFirma
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ViaFirmaDocument implements Serializable {

	private String link;
	private String nomFitxer;
	private Date expiracio;
	private ViaFirmaError viaFirmaError;
	
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getNomFitxer() {
		return nomFitxer;
	}
	public void setNomFitxer(String nomFitxer) {
		this.nomFitxer = nomFitxer;
	}
	public Date getExpiracio() {
		return expiracio;
	}
	public void setExpiracio(Date expiracio) {
		this.expiracio = expiracio;
	}
	public ViaFirmaError getViaFirmaError() {
		return viaFirmaError;
	}
	public void setViaFirmaError(ViaFirmaError viaFirmaError) {
		this.viaFirmaError = viaFirmaError;
	}
	
	private static final long serialVersionUID = -5973417502723664198L;

}
