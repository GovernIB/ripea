/**
 * 
 */
package es.caib.ripea.core.entity;

import java.util.Date;
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
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.dto.ContingutTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
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
	@Lob
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

	@OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
	protected Set<DocumentEnviamentEntity> enviaments;
	@OneToMany(
			mappedBy = "document",
			fetch = FetchType.LAZY, targetEntity = DocumentEnviamentEntity.class)
	protected List<DocumentNotificacioEntity> notificacions;
	
	public Long getPareId() {
		return pare.getId();
	}
	
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
			String ntiCsvRegulacion) {
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
			byte[] fitxerContingut) {
		this.fitxerNom = fitxerNom;
		this.fitxerContentType = fitxerContentType;
		this.fitxerContingut = fitxerContingut;
	}
	public void updateVersio(
			String versioDarrera,
			int versioCount) {
		this.versioDarrera = versioDarrera;
		this.versioCount = versioCount;
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
			ExpedientEntity expedient) {
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
				expedient);
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
				ExpedientEntity expedient) {
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
				"metanode: " + this.metaNode.toString() + "]";
	}
	
	private static final long serialVersionUID = -2299453443943600172L;

}
