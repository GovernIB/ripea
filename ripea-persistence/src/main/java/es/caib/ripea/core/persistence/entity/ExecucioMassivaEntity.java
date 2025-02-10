package es.caib.ripea.core.persistence.entity;

import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.ExecucioMassivaTipusDto;
import es.caib.ripea.service.intf.dto.FileNameOption;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.service.intf.dto.PortafirmesPrioritatEnumDto;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "execucio_massiva")
@EntityListeners(AuditingEntityListener.class)
public class ExecucioMassivaEntity extends RipeaAuditable<Long> {

	private static final int MOTIU_TAMANY = 256;

	@Column(name = "tipus")
	@Enumerated(EnumType.STRING)
	private ExecucioMassivaTipusDto tipus;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_inici")
	private Date dataInici;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_fi")
	private Date dataFi;
//	Paràmetres generació de ZIP
	@Column(name = "zip_carpetes")
	private Boolean carpetes;
	@Column(name = "zip_imprimible")
	private Boolean versioImprimible;
	@Column(name = "zip_nomFitxer")
	@Enumerated(EnumType.STRING)
	private FileNameOption nomFitxer;	
	// Enviament a Portafirmes
	@Column(name = "pfirmes_motiu", length = MOTIU_TAMANY)
	private String motiu;
	@Column(name = "pfirmes_priori")
	@Enumerated(EnumType.STRING)
	private PortafirmesPrioritatEnumDto prioritat = PortafirmesPrioritatEnumDto.NORMAL;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "pfirmes_datcad")
	private Date dataCaducitat;
	@Column(name = "pfirmes_responsables")
	private String portafirmesResponsables;
	@Column(name = "pfirmes_seqtipus")
	@Enumerated(EnumType.STRING)
	private MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSequenciaTipus;
	@Column(name = "pfirmes_fluxid")
	private String portafirmesFluxId;
	@Column(name = "pfirmes_transid")
	private String portafirmesTransaccioId;
	@Column(name = "enviar_correu")
	private Boolean enviarCorreu;
	@OneToMany(
			mappedBy = "execucioMassiva",
			cascade = {CascadeType.ALL},
			fetch = FetchType.EAGER)
	private List<ExecucioMassivaContingutEntity> continguts = new ArrayList<ExecucioMassivaContingutEntity>();
	@ManyToOne(optional = false)
	@JoinColumn(name = "entitat_id")
	private EntitatEntity entitat;
	@Column(name = "rol_actual")
	private String rolActual;
	@Column(name = "pfirmes_avis_firma_parcial")
	private Boolean portafirmesAvisFirmaParcial;
	@Column(name = "document_nom")
	private String documentNom;

	@Column(name = "pfirmes_firma_parcial")
	private Boolean portafirmesFirmaParcial;
	
	public ExecucioMassivaTipusDto getTipus() {
		return tipus;
	}
	public Date getDataInici() {
		return dataInici;
	}
	public Date getDataFi() {
		return dataFi;
	}
	public String getMotiu() {
		return motiu;
	}
	public PortafirmesPrioritatEnumDto getPrioritat() {
		return prioritat;
	}
	public Date getDataCaducitat() {
		return dataCaducitat;
	}
	public Boolean getEnviarCorreu() {
		return enviarCorreu;
	}
	public List<ExecucioMassivaContingutEntity> getContinguts() {
		return continguts;
	}
	public EntitatEntity getEntitat() {
		return entitat;
	}
	public String getPortafirmesResponsables() {
		return portafirmesResponsables;
	}
	public MetaDocumentFirmaSequenciaTipusEnumDto getPortafirmesSequenciaTipus() {
		return portafirmesSequenciaTipus;
	}
	public String getPortafirmesFluxId() {
		return portafirmesFluxId;
	}
	public String getPortafirmesTransaccioId() {
		return portafirmesTransaccioId;
	}
	public String getRolActual() {
		return rolActual;
	}
	public Boolean getPortafirmesAvisFirmaParcial() {
		return portafirmesAvisFirmaParcial;
	}
	public String getDocumentNom() { return documentNom; }
	public void setDocumentNom(String documentNom) { this.documentNom = documentNom; }

	public Boolean getPortafirmesFirmaParcial() {
		return portafirmesFirmaParcial;
	}
	
	public void addContingut(ExecucioMassivaContingutEntity contingut) {
		getContinguts().add(contingut);
	}
	public void removeContingut(ExecucioMassivaContingutEntity contingut) {
		getContinguts().remove(contingut);
	}
	public void updateDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}

	public Boolean getCarpetes() {
		if (carpetes!=null) return carpetes; else return Boolean.FALSE;
	}
	public void setCarpetes(Boolean carpetes) {
		this.carpetes = carpetes;
	}
	public Boolean getVersioImprimible() {
		if (versioImprimible!=null) return versioImprimible; else return Boolean.FALSE;
	}
	public void setVersioImprimible(Boolean versioImprimible) {
		this.versioImprimible = versioImprimible;
	}
	public FileNameOption getNomFitxer() {
		return nomFitxer;
	}
	public void setNomFitxer(
			FileNameOption nomFitxer) {
		this.nomFitxer = nomFitxer;
	}
	public static Builder getBuilder(
			ExecucioMassivaTipusDto tipus,
			Date dataInici,
			String motiu,
			PortafirmesPrioritatEnumDto prioritat,
			String portafirmesResponsables,
			MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSequenciaTipus,
			String portafirmesFluxId,
			String portafirmesTransaccioId,
			Date dataCaducitat,
			boolean enviarCorreu,
			EntitatEntity entitat,
			String rolActual,
			boolean portafirmesAvisFirmaParcial,
			boolean portafirmesFirmaParcial) {
		return new Builder(
				tipus,
				dataInici,
				motiu,
				prioritat,
				portafirmesResponsables,
				portafirmesSequenciaTipus,
				portafirmesFluxId,
				portafirmesTransaccioId,
				dataCaducitat,
				enviarCorreu,
				entitat,
				rolActual,
				portafirmesAvisFirmaParcial,
				portafirmesFirmaParcial);
	}
	public static class Builder {
		ExecucioMassivaEntity built;
		Builder(ExecucioMassivaTipusDto tipus,
				Date dataInici,
				String motiu,
				PortafirmesPrioritatEnumDto prioritat,
				String portafirmesResponsables,
				MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSequenciaTipus,
				String portafirmesFluxId,
				String portafirmesTransaccioId,
				Date dataCaducitat,
				boolean enviarCorreu,
				EntitatEntity entitat,
				String rolActual,
				boolean portafirmesAvisFirmaParcial,
				boolean portafirmesFirmaParcial) {
			built = new ExecucioMassivaEntity();
			built.tipus = tipus;
			built.dataInici = dataInici;
			built.motiu = motiu;
			built.prioritat = prioritat;
			built.portafirmesResponsables = portafirmesResponsables;
			built.portafirmesSequenciaTipus = portafirmesSequenciaTipus;
			built.portafirmesFluxId = portafirmesFluxId;
			built.portafirmesTransaccioId = portafirmesTransaccioId;
			built.dataCaducitat = dataCaducitat;
			built.enviarCorreu = enviarCorreu;
			built.entitat = entitat;
			built.rolActual = rolActual;
			built.portafirmesAvisFirmaParcial = portafirmesAvisFirmaParcial;
			built.portafirmesFirmaParcial = portafirmesFirmaParcial;
		}
		
		Builder(ExecucioMassivaTipusDto tipus,
				Date dataInici,
				Date dataFi,
				EntitatEntity entitat,
				String rolActual) {
			built = new ExecucioMassivaEntity();
			built.tipus = tipus;
			built.dataInici = dataInici;
			built.dataFi = dataFi;
			built.entitat = entitat;
			built.rolActual = rolActual;
		}

		public ExecucioMassivaEntity build() {
			return built;
		}
	}

	public static Builder getBuilder(
			ExecucioMassivaTipusDto tipus,
			Date dataInici,
			Date dataFi,
			EntitatEntity entitat,
			String rolActual) {
		return new Builder(
				tipus,
				dataInici,
				dataFi,
				entitat,
				rolActual);
	}

	private static final long serialVersionUID = -2077000626779456363L;
}