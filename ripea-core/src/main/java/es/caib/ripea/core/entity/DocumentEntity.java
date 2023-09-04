/**
 * 
 */
package es.caib.ripea.core.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.dto.ArxiuEstatEnumDto;
import es.caib.ripea.core.api.dto.ContingutTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentFirmaTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiTipoFirmaEnumDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.NtiOrigenEnumDto;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe del model de dades que representa un document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "ipa_document")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
public class DocumentEntity extends NodeEntity {

	@Column(name = "tipus", nullable = false)
	private DocumentTipusEnumDto documentTipus;
	@Column(name = "estat", nullable = false)
	private DocumentEstatEnumDto estat;
	@Column(name = "ubicacio", length = 255)
	private String ubicacio;
	@Column(name = "data", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date data;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_captura", nullable = false)
	private Date dataCaptura;
	@Column(name = "custodia_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date custodiaData;
	@Column(name = "custodia_id", length = 256)
	private String custodiaId;
	@Column(name = "custodia_csv", length = 256)
	private String custodiaCsv;
	@Column(name = "fitxer_nom", length = 256)
	private String fitxerNom;
	@Column(name = "fitxer_content_type", length = 256)
	private String fitxerContentType;
	@Column(name = "fitxer_tamany")
	private Long fitxerTamany;
	//@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "fitxer_contingut")
	private byte[] fitxerContingut;
	@Column(name = "versio_darrera", length = 32)
	private String versioDarrera;
	@Column(name = "versio_count", nullable = false)
	private int versioCount;
	@Column(name = "nti_version", length = 5, nullable = false)
	private String ntiVersion;
	@Column(name = "nti_identif", length = 48, nullable = false)
	private String ntiIdentificador;
	@Column(name = "nti_organo", length = 9, nullable = false)
	private String ntiOrgano;
	@Column(name = "nti_origen", length = 2, nullable = false)
	@Enumerated(EnumType.STRING)
	private NtiOrigenEnumDto ntiOrigen;
	@Column(name = "nti_estela", length = 4, nullable = false)
	@Enumerated(EnumType.STRING)
	private DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
	@Column(name = "nti_tipdoc", length = 4, nullable = false)
//	@Enumerated(EnumType.STRING)
	private String ntiTipoDocumental;
	@Column(name = "nti_idorig", length = 48)
	private String ntiIdDocumentoOrigen;
	@Column(name = "nti_tipfir", length = 4)
	@Enumerated(EnumType.STRING)
	private DocumentNtiTipoFirmaEnumDto ntiTipoFirma;
	@Column(name = "nti_csv", length = 256)
	private String ntiCsv;
	@Column(name = "nti_csvreg", length = 512)
	private String ntiCsvRegulacion;
	@Column(name = "descripcio", length = 512)
	protected String descripcio;

	
	//Tipus de document firmat:
		//Document firmat putjat manualment
		//Document firmat des dels navegador
		//Document firmat que es rep des del portafirmes callback
		//Document que vene d'una anotació de registre
		//Document generat de les resposta de PINBAL
	
	// document signed in portafirmes that arrived in callback and was not saved in arxiu 
	@Column(name = "ges_doc_firmat_id", length = 256)
	private String gesDocFirmatId;
	@Column(name = "nom_fitxer_firmat", length = 512)
	private String nomFitxerFirmat;
	
	//document uploaded manually in ripea that was not saved in arxiu
	// document sense firma o amb firma adjunta
	@Column(name = "ges_doc_adjunt_id", length = 256)
	private String gesDocAdjuntId;
	// firma separada
	@Column(name = "ges_doc_adjunt_firma_id", length = 256)
	private String gesDocAdjuntFirmaId;
	
	
//	// firma separada of document saved as esborrany in arxiu
//	@Column(name = "ges_doc_esborrany_firma_id", length = 256)
//	private String gesDocEsborranyFirmaSeparadaId;
	
	
	// firma separada of document saved as esborrany in arxiu
	@Column(name = "arxiu_uuid_firma", length = 36)
	private String arxiuUuidFirma;
	
	@Column(name = "pinbal_idpeticion", length = 64)
	private String pinbalIdpeticion;

	@Column(name = "val_ok")
	private boolean validacioFirmaCorrecte;
	@Column(name = "val_error")
	private String validacioFirmaErrorMsg;
	@Enumerated(EnumType.STRING)
	@Column(name = "annex_estat")
	private ArxiuEstatEnumDto annexArxiuEstat;
	
	
	@Enumerated(EnumType.STRING)
	@Column(name = "arxiu_estat", length = 16)
	private ArxiuEstatEnumDto arxiuEstat;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "doc_firma_tipus", length = 16)
	private DocumentFirmaTipusEnumDto documentFirmaTipus;
	
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "expedient_estat_id")
	@ForeignKey(name = "ipa_expestat_document_fk")
	private ExpedientEstatEntity expedientEstatAdditional;
	
	
	@OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
	@OrderBy("createdDate DESC")
	protected Set<DocumentEnviamentEntity> enviaments;
	@OneToMany(
			mappedBy = "document",
			fetch = FetchType.LAZY, targetEntity = DocumentEnviamentEntity.class)
	@OrderBy("createdDate DESC")
	protected List<DocumentNotificacioEntity> notificacions;
	
	
	@OneToMany(mappedBy = "document")
	private List<RegistreAnnexEntity> annexos = new ArrayList<RegistreAnnexEntity>();
	
	@OneToOne
	@JoinColumn(name = "id")
	private ContingutEntity contingut;
	
	@Transient
	protected boolean ambNotificacions;
	@Transient
	protected String estatDarreraNotificacio;
	@Transient
	protected boolean errorDarreraNotificacio;
	@Transient
	protected boolean errorEnviamentPortafirmes;
	
	public Long getPareId() {
		return pare.getId();
	}
	
	
    /**

     * @deprecated
     * DocumentEntity already has field expedient, there is no need to calculate it
     * <p> Use {@link #getExpedient()} instead.
     */
    @Deprecated
	public ExpedientEntity getExpedientPare() {
		ContingutEntity contingutPare = this.pare;
		while(contingutPare != null && !(contingutPare instanceof ExpedientEntity)) {
			contingutPare = contingutPare.getPare();
		}
		
		return contingutPare != null ? (ExpedientEntity) contingutPare : null;
		
	}

	public MetaDocumentEntity getMetaDocument() {
		return (MetaDocumentEntity)getMetaNode();
	}
	@Transient
	public boolean isFirmat() {
		return	DocumentEstatEnumDto.FIRMAT.equals(estat) ||
				DocumentEstatEnumDto.CUSTODIAT.equals(estat);
	}

	public void updateTipusDocument(
			MetaDocumentEntity metaDocument, 
			NtiOrigenEnumDto ntiOrigen,
			DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			String ntiTipoDocumental) {
		this.metaNode = metaDocument;
		if (ntiOrigen != null) {
			this.ntiOrigen = ntiOrigen;
		}
		if (ntiTipoDocumental != null) {
			this.ntiTipoDocumental = ntiTipoDocumental;
		}
		if (ntiEstadoElaboracion != null) {
			this.ntiEstadoElaboracion = ntiEstadoElaboracion;
		}
	}

	public void updateTipusDocument(
			MetaDocumentEntity metaDocument) {
		this.metaNode = metaDocument;
	}
	
	public void updateArxiuEstat(ArxiuEstatEnumDto arxiuEstat) {
		this.arxiuEstat = arxiuEstat;
	}
	
	public void updateDocumentFirmaTipus(DocumentFirmaTipusEnumDto documentFirmaTipus) {
		this.documentFirmaTipus = documentFirmaTipus;
	}

	public void update(
			MetaDocumentEntity metaDocument,
			String nom,
			String descripcio,
			Date data,
			String ubicacio,
			Date dataCaptura,
			String ntiOrgano,
			NtiOrigenEnumDto ntiOrigen,
			DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			String ntiTipoDocumental,
			String ntiIdDocumentoOrigen,
			DocumentNtiTipoFirmaEnumDto ntiTipoFirma,
			String ntiCsv,
			String ntiCsvRegulacion, 
			DocumentFirmaTipusEnumDto documentFirmaTipus) {
		this.metaNode = metaDocument;
		this.nom = nom;
		this.descripcio = descripcio;
		this.data = data;
		this.ubicacio = ubicacio;
		this.dataCaptura = dataCaptura;
		this.ntiOrgano = ntiOrgano;
		this.ntiOrigen = ntiOrigen;
		this.ntiEstadoElaboracion = ntiEstadoElaboracion;
		this.ntiTipoDocumental = ntiTipoDocumental;
		this.ntiIdDocumentoOrigen = ntiIdDocumentoOrigen;
		this.ntiTipoFirma = ntiTipoFirma;
		this.ntiCsv = ntiCsv;
		this.ntiCsvRegulacion = ntiCsvRegulacion;
		this.documentFirmaTipus = documentFirmaTipus;
	}

	public void updateNtiIdentificador(
			String ntiIdentificador) {
		this.ntiIdentificador = ntiIdentificador;
	}
	public void updateNti(
			String ntiVersion,
			String ntiIdentificador,
			String ntiOrgano,
			NtiOrigenEnumDto ntiOrigen,
			DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			String ntiTipoDocumental,
			String ntiIdDocumentoOrigen,
			DocumentNtiTipoFirmaEnumDto ntiTipoFirma,
			String ntiCsv,
			String ntiCsvRegulacion) {
		this.ntiVersion = ntiVersion;
		this.ntiIdentificador = ntiIdentificador;
		this.ntiOrgano = ntiOrgano;
		this.ntiOrigen = ntiOrigen;
		this.ntiEstadoElaboracion = ntiEstadoElaboracion;
		this.ntiTipoDocumental = ntiTipoDocumental;
		this.ntiIdDocumentoOrigen = ntiIdDocumentoOrigen;
		this.ntiTipoFirma = ntiTipoFirma;
		this.ntiCsv = ntiCsv;
		this.ntiCsvRegulacion = ntiCsvRegulacion;
	}

	public void updateEstat(
			DocumentEstatEnumDto estat) {
		this.estat = estat;
		if (!validacioFirmaCorrecte && !DocumentEstatEnumDto.REDACCIO.equals(estat)) {
			this.validacioFirmaCorrecte = true;
		}
	}
	public void updateInformacioCustodia(
			Date custodiaData,
			String custodiaId,
			String custodiaCsv) {
		this.custodiaData = custodiaData;
		this.custodiaId = custodiaId;
		this.custodiaCsv = custodiaCsv;
	}
	public void updateFitxer(
			String fitxerNom,
			String fitxerContentType,
			byte[] fitxerContingut, 
			Long fitxerTamany) {
		this.fitxerNom = fitxerNom;
		this.fitxerContentType = fitxerContentType;
		this.fitxerContingut = fitxerContingut;
		this.fitxerTamany = fitxerTamany;
	}
	public void updateFitxerTamany(
			Long fitxerTamany) {
		this.fitxerTamany = fitxerTamany;
	}
	public void updateVersio(
			String versioDarrera,
			int versioCount) {
		this.versioDarrera = versioDarrera;
		this.versioCount = versioCount;
	}
	
	public boolean isAmbNotificacions() {
		return (notificacions != null && !notificacions.isEmpty() && notificacions.get(0) instanceof DocumentNotificacioEntity) ? true : false;
	}

	public boolean isErrorDarreraNotificacio() {
		if (notificacions != null && !notificacions.isEmpty() && notificacions.get(0) instanceof DocumentNotificacioEntity) {
			DocumentNotificacioEntity lastNofificacio = notificacions.get(0);
			return lastNofificacio.isError();
		}
		return false;
	}
	
	public String getEstatDarreraNotificacio() {
		if (notificacions != null && !notificacions.isEmpty() && notificacions.get(0) instanceof DocumentNotificacioEntity) {
			DocumentNotificacioEntity lastNofificacio = (DocumentNotificacioEntity) notificacions.get(0);
			return lastNofificacio.getNotificacioEstat() != null ? lastNofificacio.getNotificacioEstat().name() : "";
		}
		return "";
	}

	public boolean isArxiuEstatDefinitiu() {
		return arxiuEstat != null && arxiuEstat == ArxiuEstatEnumDto.DEFINITIU;
	}
	
	public boolean isErrorEnviamentPortafirmes() {
		DocumentPortafirmesEntity docPortLast = null;
		if (enviaments != null) {
			Iterator<DocumentEnviamentEntity> iter = enviaments.iterator();
			while (iter.hasNext()) {
				DocumentEnviamentEntity docEnv = iter.next();
				if (docEnv instanceof DocumentPortafirmesEntity) {
					docPortLast = (DocumentPortafirmesEntity) docEnv;
					break;
				}
			}
		}
		if (docPortLast != null && docPortLast.isError()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isDocFromAnnex() {
		return this.getExpedientPare().getPeticions() != null && !this.getExpedientPare().getPeticions().isEmpty() && this.getPare() instanceof CarpetaEntity && this.getPare().getNom().startsWith("Registre entrada:");
	}

	
	public static Builder getBuilder(
			DocumentTipusEnumDto documentTipus,
			DocumentEstatEnumDto estat,
			String nom,
			String descripcio,
			Date data,
			Date dataCaptura,
			String ntiIdDocumentoOrigen,
			String ntiVersion,
			String ntiOrgano,
			NtiOrigenEnumDto ntiOrigen,
			DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			String ntiTipoDocumental,
			MetaNodeEntity metaNode,
			ContingutEntity pare,
			EntitatEntity entitat,
			ExpedientEntity expedient, 
			DocumentFirmaTipusEnumDto documentFirmaTipus, 
			ExpedientEstatEntity expedientEstatAdditional) {
		return new Builder(
				documentTipus,
				estat,
				nom,
				descripcio,
				data,
				dataCaptura,
				ntiIdDocumentoOrigen,
				ntiVersion,
				ntiOrgano,
				ntiOrigen,
				ntiEstadoElaboracion,
				ntiTipoDocumental,
				metaNode,
				pare,
				entitat,
				expedient, 
				documentFirmaTipus, 
				expedientEstatAdditional);
	}
	public static class Builder {
		DocumentEntity built;
		Builder(
				DocumentTipusEnumDto documentTipus,
				DocumentEstatEnumDto estat,
				String nom,
				String descripcio,
				Date data,
				Date dataCaptura,
				String ntiIdDocumentoOrigen,
				String ntiVersion,
				String ntiOrgano,
				NtiOrigenEnumDto ntiOrigen,
				DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
				String ntiTipoDocumental,
				MetaNodeEntity metaNode,
				ContingutEntity pare,
				EntitatEntity entitat,
				ExpedientEntity expedient, 
				DocumentFirmaTipusEnumDto documentFirmaTipus, 
				ExpedientEstatEntity expedientEstatAdditional) {
			built = new DocumentEntity();
			built.documentTipus = documentTipus;
			built.estat = estat;
			built.nom = nom;
			built.descripcio = descripcio;
			built.data = data;
			built.dataCaptura = dataCaptura;
			built.ntiIdDocumentoOrigen = ntiIdDocumentoOrigen;
			built.ntiVersion = ntiVersion;
			built.ntiIdentificador = new Long(System.currentTimeMillis()).toString();
			built.ntiOrgano = ntiOrgano;
			built.ntiOrigen = ntiOrigen;
			built.ntiEstadoElaboracion = ntiEstadoElaboracion;
			built.ntiTipoDocumental = ntiTipoDocumental;
			built.metaNode = metaNode;
			built.pare = pare;
			built.entitat = entitat;
			built.expedient = expedient;
			built.tipus = ContingutTipusEnumDto.DOCUMENT;
			built.versioCount = 0;
			built.validacioFirmaCorrecte = true;
			built.documentFirmaTipus = documentFirmaTipus;
			built.expedientEstatAdditional = expedientEstatAdditional;
			
		}
		public Builder ubicacio(String ubicacio) {
			built.ubicacio = ubicacio;
			return this;
		}

		public Builder ntiTipoFirma(DocumentNtiTipoFirmaEnumDto ntiTipoFirma) {
			built.ntiTipoFirma = ntiTipoFirma;
			return this;
		}
		public Builder ntiCsv(String ntiCsv) {
			built.ntiCsv = ntiCsv;
			return this;
		}
		public Builder ntiCsvRegulacion(String ntiCsvRegulacion) {
			built.ntiCsvRegulacion = ntiCsvRegulacion;
			return this;
		}
		public Builder pinbalIdpeticion(String pinbalIdpeticion) {
			built.pinbalIdpeticion = pinbalIdpeticion;
			return this;
		}
		public Builder validacioFirmaCorrecte(Boolean validacioFirmaCorrecte) {
			built.validacioFirmaCorrecte = validacioFirmaCorrecte != null ? validacioFirmaCorrecte : true;
			return this;
		}
		public Builder validacioFirmaErrorMsg(String validacioFirmaErrorMsg) {
			built.validacioFirmaErrorMsg = validacioFirmaErrorMsg;
			return this;
		}
		public Builder annexArxiuEstat(ArxiuEstatEnumDto annexArxiuEstat) {
			built.annexArxiuEstat = annexArxiuEstat;
			return this;
		}
		public DocumentEntity build() {
			return built;
		}
	}
	
	@Override
	public String toString() {
		return "DocumentEntity: [" +
				"node: " + super.toString() + ", " +
				"id: " + this.getId() + ", " +
				"nom: " + this.nom + ", " +
				"descripcio: " + this.descripcio + ", " +
				"data: " + this.data + ", " +
				"dataCaptura: " + this.dataCaptura + ", " +
				"ntiIdDocumentoOrigen: " + this.ntiIdDocumentoOrigen + ", " +
				"ntiVersion: " + this.ntiVersion + ", " +
				"ntiIdentificador: " + this.ntiIdentificador + ", " +
				"ntiOrgano: " + this.ntiOrgano + ", " +
				"ntiOrigen: " + this.ntiOrigen + ", " +
				"ntiEstadoElaboracion: " + this.ntiEstadoElaboracion + ", " +
				"ntiTipoDocumental: " + this.ntiTipoDocumental + ", " +
				"metanode: " + (this.metaNode != null ? this.metaNode.toString() : null) + "]";
	}
	
	private static final long serialVersionUID = -2299453443943600172L;

}
