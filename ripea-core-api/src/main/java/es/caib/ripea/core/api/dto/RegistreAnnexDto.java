/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.text.DecimalFormat;
import java.util.Date;

import es.caib.ripea.core.api.utils.Utils;
import lombok.Getter;
import lombok.Setter;


/**
 * Informació d'un registre annex.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class RegistreAnnexDto {

	private Long id;
	private String firmaPerfil;
	private String firmaTipus;
	private Date ntiFechaCaptura;
	
	private String ntiOrigen;
	private String ntiTipoDocumental;
	private String ntiEstadoElaboracion;
	private String observacions;
	private String sicresTipoDocumento;
	private String sicresValidezDocumento;
	private long tamany;
	private String tipusMime;
	private String titol;
	private String uuid;

	private String nom;


	private Date createdDate;
	private String registreNumero;
	private Long expedientId;
	private String expedientNumeroNom;
	private Date expedientCreatedDate;

	private Long documentId;
	private RegistreAnnexEstatEnumDto estat;
	private String error;
	private Long expedientPeticioId;

	private boolean validacioFirmaCorrecte;
	private String validacioFirmaErrorMsg;
	private ArxiuEstatEnumDto annexArxiuEstat;
	

	public boolean isAmbFirma() {
		return firmaTipus != null;
	}
	public boolean isRowSelectable() {
		return this.documentId != null;
	}
	
	
	public String getTamanyStr() {
		return Utils.getTamanyString(this.tamany);
	}

}
