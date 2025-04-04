package es.caib.ripea.persistence.entity.resourceentity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.ArxiuEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentFirmaTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNtiTipoFirmaEnumDto;
import es.caib.ripea.service.intf.dto.DocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.NtiOrigenEnumDto;
import es.caib.ripea.service.intf.model.DocumentResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "document")
@Getter
@Setter
@NoArgsConstructor
public class DocumentResourceEntity extends NodeResourceEntity<DocumentResource> {

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
	
	//ID del contingut original guardat al sistema de fitxers
	@Column(name = "uuid_distribucio")
	private String gesDocOriginalId;

	// firma separada of document saved as esborrany in arxiu
	@Column(name = "arxiu_uuid_firma", length = 36)
	private String arxiuUuidFirma;
	
	@Column(name = "pinbal_idpeticion", length = 64)
	private String pinbalIdpeticion;

	@Column(name = "val_ok")
	private boolean validacioFirmaCorrecte;
	@Column(name = "val_error", length = 1000)
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
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "expedient_estat_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "expestat_document_fk"))	
	private ExpedientEstatResourceEntity expedientEstatAdditional;
	
	public MetaDocumentResourceEntity getMetaDocument() {
		return (MetaDocumentResourceEntity)getMetaNode();
	}
	
	public void updateEstat(DocumentEstatEnumDto estat) {
		this.estat = estat;
		if (!validacioFirmaCorrecte && !DocumentEstatEnumDto.REDACCIO.equals(estat)) {
			this.validacioFirmaCorrecte = true;
		}
	}
}