/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.util.Date;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;



/**
 * Informació d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class ExpedientDto extends NodeDto {
	private Long id;
	private ExpedientEstatEnumDto estat;
	private MetaExpedientDominiDto metaExpedientDomini;
	private Date tancatData;
	private String tancatMotiu;
	private int any;
	private long sequencia;
	private String codi;
	private String ntiVersion;
	private String ntiIdentificador;
	private String ntiOrgano;
	private String ntiOrganoDescripcio;
	private Date ntiFechaApertura;
	private String ntiClasificacionSia;
	private boolean sistraPublicat;
	private String sistraUnitatAdministrativa;
	private String sistraClau;
	private UsuariDto agafatPer;
	private String numero;
	private boolean conteDocuments;
	private boolean conteDocumentsFirmats;
	private boolean conteDocumentsEnProcessDeFirma;
	private boolean conteDocumentsDePortafirmesNoCustodiats;
	private boolean conteDocumentsDeAnotacionesNoMogutsASerieFinal;
	private long numComentaris;
	private ExpedientEstatDto expedientEstat;
	private Long expedientEstatId;
	private Long expedientEstatNextInOrder;
	private boolean usuariActualWrite;
	private boolean usuariActualDelete;
	private boolean peticions;
	private boolean processatOk;
	private boolean expCreatArxiuOk = true;
	private boolean tasques;
	private boolean hasEsborranys;
	private List<DocumentDto> esborranys;
	private Set<InteressatDto> interessats;
	private List<InteressatDto> interessatsNotificable;
	private long numSeguidors;
	private boolean seguidor;
	private boolean errorLastEnviament;
	private boolean errorLastNotificacio;
	private boolean ambEnviamentsPendents;
	private boolean ambNotificacionsPendents;
	private Long grupId;
	private String grupNom;
	private boolean hasAllDocumentsDefinitiu;
	private Long organGestorId;
	private String organGestorText;

	private boolean hasNoFirmatsOAmbFirmaInvalida;
	
	private Date dataDarrerEnviament;
	
	public MetaExpedientDto getMetaExpedient() {
		return (MetaExpedientDto)getMetaNode();
	}

	public boolean isAgafat() {
		return agafatPer != null;
	}
	
	public String getTipusStr() {
		return this.getMetaNode() != null ? this.getMetaNode().getNom() + " - " + this.getMetaExpedient().getClassificacio() : null;
	}

	protected ExpedientDto copiarContenidor(ContingutDto original) {
		ExpedientDto copia = new ExpedientDto();
		copia.setId(original.getId());
		copia.setNom(original.getNom());
		return copia;
	}

	public String getNomINumero() {
		return this.nom + " (" + this.numero + ")";
	}
	
	public String getNumeroINom() {
		return this.numero + " - " + this.nom;
	}

	public Set<InteressatDto> getInteressats() {
		return interessats;
	}
	
	
}
