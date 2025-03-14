package es.caib.ripea.persistence.entity.resourceentity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.PinbalServeiEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentTipusGenericEnumDto;
import es.caib.ripea.service.intf.dto.MultiplicitatEnumDto;
import es.caib.ripea.service.intf.dto.NtiOrigenEnumDto;
import es.caib.ripea.service.intf.model.MetaDocumentResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "metadocument")
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy=InheritanceType.JOINED)
public class MetaDocumentResourceEntity extends MetaNodeResourceEntity<MetaDocumentResource> {
	
	@Column(name = "multiplicitat")
	private MultiplicitatEnumDto multiplicitat;
	@Column(name = "firma_pfirma")
	private boolean firmaPortafirmesActiva;
	@Column(name = "portafirmes_doctip", length = 64)
	private String portafirmesDocumentTipus;
	@Column(name = "portafirmes_fluxid", length = 64)
	private String portafirmesFluxId;
	@Column(name = "portafirmes_respons", length = 512)
	private String portafirmesResponsables;
	@Enumerated(EnumType.STRING)
	@Column(name = "portafirmes_seqtip", length = 256)
	private MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSequenciaTipus;
	@Column(name = "portafirmes_custip", length = 64)
	private String portafirmesCustodiaTipus;
	@Column(name = "firma_passarela")
	private boolean firmaPassarelaActiva;
	@Column(name = "passarela_custip", length = 64)
	private String firmaPassarelaCustodiaTipus;
	@Column(name = "plantilla_nom", length = 256)
	private String plantillaNom;
	@Column(name = "plantilla_content_type", length = 256)
	private String plantillaContentType;
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "plantilla_contingut")
	private byte[] plantillaContingut;	
	@Column(name = "ordre")
	private int ordre;
	@Column(name = "nti_origen", length = 2)
	@Enumerated(EnumType.STRING)
	private NtiOrigenEnumDto ntiOrigen;
	@Column(name = "nti_estela", length = 4)
	@Enumerated(EnumType.STRING)
	private DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
	@Column(name = "nti_tipdoc", length = 4)
	private String ntiTipoDocumental;
	@Column(name = "firma_biometrica")
	private boolean firmaBiometricaActiva;
	@Column(name = "biometrica_lectura")
	private boolean biometricaLectura;
	@Column(name = "meta_document_tipus_gen", length = 256)
	@Enumerated(EnumType.STRING)
	private MetaDocumentTipusGenericEnumDto metaDocumentTipusGeneric;
	@Enumerated(EnumType.STRING)
	@Column(name = "portafirmes_fluxtip", length = 256)
	private MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus;
	@Column(name = "codi", length = 64, nullable = false)
	private String codiPropi;
	@Column(name = "pinbal_actiu", nullable = false)
	private boolean pinbalActiu;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "meta_expedient_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "METADOC_METAEXP_FK"))
	private MetaExpedientEntity metaExpedient;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "pinbal_servei",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "metadoc_pinbal_fk"))
	private PinbalServeiEntity pinbalServei;
	
	@Column(name = "pinbal_finalitat", length = 512)
	protected String pinbalFinalitat;
	
	@Column(name = "pinbal_utilitzar_cif_organ", nullable = false)
	private boolean pinbalUtilitzarCifOrgan;
	
	
	@Column(name = "per_defecte")
	private boolean perDefecte;
}
