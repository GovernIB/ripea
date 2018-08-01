/**
 * 
 */
package es.caib.ripea.core.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.dto.PortafirmesPrioritatEnumDto;
import es.caib.ripea.core.audit.RipeaAuditable;


@Entity
@Table(	name = "ipa_execucio_massiva")
@EntityListeners(AuditingEntityListener.class)
public class ExecucioMassivaEntity extends RipeaAuditable<Long> {

	private static final int MOTIU_TAMANY = 256;

	public enum ExecucioMassivaTipus {
		PORTASIGNATURES
	}

	@Column(name = "tipus")
	@Enumerated(EnumType.STRING)
	private ExecucioMassivaTipus tipus;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_inici")
	private Date dataInici;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_fi")
	private Date dataFi;
	@Column(name = "pfirmes_motiu", length = MOTIU_TAMANY)
	private String motiu;
	@Column(name = "pfirmes_priori")
	@Enumerated(EnumType.STRING)
	private PortafirmesPrioritatEnumDto prioritat = PortafirmesPrioritatEnumDto.NORMAL;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "pfirmes_datcad")
	private Date dataCaducitat;
	@Column(name = "enviar_correu")
	private Boolean enviarCorreu;
	@OneToMany(
			mappedBy = "execucioMassiva",
			cascade = {CascadeType.ALL},
			fetch = FetchType.EAGER)
	private List<ExecucioMassivaContingutEntity> continguts = new ArrayList<ExecucioMassivaContingutEntity>();
	@Column(name = "entitat_id")
	private Long entitatId;

	public ExecucioMassivaTipus getTipus() {
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
	public Long getEntitatId() {
		return entitatId;
	}

	public void addContingut(ExecucioMassivaContingutEntity contingut) {
		getContinguts().add(contingut);
	}
	public void removeContingut(ExecucioMassivaContingutEntity contingut) {
		getContinguts().remove(contingut);
	}

	public void updateDataFi(
			Date dataFi) {
		this.dataFi = dataFi;
	}

	public static Builder getBuilder(
			ExecucioMassivaTipus tipus,
			Date dataInici,
			String motiu,
			PortafirmesPrioritatEnumDto prioritat,
			Date dataCaducitat,
			boolean enviarCorreu,
			Long entitatId) {
		return new Builder(
				tipus,
				dataInici,
				motiu,
				prioritat,
				dataCaducitat,
				enviarCorreu,
				entitatId);
	}
	public static class Builder {
		ExecucioMassivaEntity built;
		Builder(ExecucioMassivaTipus tipus,
				Date dataInici,
				String motiu,
				PortafirmesPrioritatEnumDto prioritat,
				Date dataCaducitat,
				boolean enviarCorreu,
				Long entitatId) {
			built = new ExecucioMassivaEntity();
			built.tipus = tipus;
			built.dataInici = dataInici;
			built.motiu = motiu;
			built.prioritat = prioritat;
			built.dataCaducitat = dataCaducitat;
			built.enviarCorreu = enviarCorreu;
			built.entitatId = entitatId;
		}
		public ExecucioMassivaEntity build() {
			return built;
		}
	}

	private static final long serialVersionUID = -2077000626779456363L;

}
