/**
 * 
 */
package es.caib.ripea.core.persistence;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.dto.ExpedientPeticioAccioEnumDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto;
import es.caib.ripea.core.audit.RipeaAuditable;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe del model de dades que representa una petició de creació d’expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@Entity
@Table(name = "ipa_expedient_peticio")
@EntityListeners(AuditingEntityListener.class)
public class ExpedientPeticioEntity extends RipeaAuditable<Long> {

	@Column(name = "identificador", nullable = false)
	String identificador;
	@Column(name = "clau_acces", nullable = false)
	private String clauAcces;
	@Column(name = "data_alta", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataAlta;
	@Enumerated(EnumType.STRING)
	@Column(name = "estat", nullable = false, length = 40)
	private ExpedientPeticioEstatEnumDto estat;
	@Column(name = "data_actualitzacio")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataActualitzacio;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "usuari_actualitzacio")
	@ForeignKey(name = "ipa_usuari_actual_exp_pet_fk")
	private UsuariEntity usuariActualitzacio;
	
	@Column(name = "observacions", length = 4000)
	private String observacions;
	
	
	// these fields are filled if error occurs while getting anotacio from DISTRIBUCIO and saving it in DB
	@Column(name = "consulta_ws_error")
	private boolean consultaWsError = false;
	@Column(name = "consulta_ws_error_desc", length = 4000)
	private String consultaWsErrorDesc;
	@Column(name = "consulta_ws_error_date")
	private Date consultaWsErrorDate;
	
	@Column(name = "notifica_dist_error", length = 4000)
	private String notificaDistError;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "metaexpedient_id")
	@ForeignKey(name = "ipa_exp_pet_metaexp_fk")
	private MetaExpedientEntity metaExpedient;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "exp_peticio_accio", length = 20)
	private ExpedientPeticioAccioEnumDto expedientPeticioAccioEnumDto;

	@ManyToOne(optional = true)
	@JoinColumn(name = "registre_id")
	protected RegistreEntity registre;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "expedient_id")
	@ForeignKey(name = "ipa_expedient_registre_fk")
	private ExpedientEntity expedient;

	@Column(name = "pendent_canvi_estat_dis")
	private boolean pendentCanviEstatDistribucio;
	@Column(name = "reintents_canvi_estat_dis")
	private int reintentsCanviEstatDistribucio;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "grup_id")
	@ForeignKey(name = "ipa_grup_exp_pet_fk")
	private GrupEntity grup;
	
	
	public void setEstatCanviatDistribucio(boolean canviat) {
		setEstatCanviatDistribucio(
				canviat,
				null);
	}
	
	public void setEstatCanviatDistribucio(
			boolean canviat,
			Boolean incrementReintents) {
		if (canviat) {
			this.pendentCanviEstatDistribucio = false;
			this.reintentsCanviEstatDistribucio = 0;
		} else {
			this.pendentCanviEstatDistribucio = true;
			if (incrementReintents != null && incrementReintents) {
				this.reintentsCanviEstatDistribucio++;
			}
		}
	}

	
	public void updateNotificaDistError(String notificaDistError) {
		this.notificaDistError = StringUtils.abbreviate(notificaDistError, 4000);
	}

	public void updateExpedient(ExpedientEntity expedient) {
		this.expedient = expedient;
	}

	public boolean isConsultaWsError() {
		return consultaWsError;
	}

	public void updateConsultaWsError(boolean consultaWsError) {
		this.consultaWsError = consultaWsError;
	}

	public void updateConsultaWsErrorDesc(
			String consultaWsErrorDesc) {
		this.consultaWsErrorDesc = consultaWsErrorDesc;
	}

	public void updateConsultaWsErrorDate(
			Date consultaWsErrorDate) {
		this.consultaWsErrorDate = consultaWsErrorDate;
	}

	public void updateIdentificador(
			String identificador) {
		this.identificador = identificador;
	}

	public void updateRegistre(
			RegistreEntity registre) {
		this.registre = registre;
	}

	public void updateClauAcces(
			String clauAcces) {
		this.clauAcces = clauAcces;
	}

	public void updateExpedientPeticioAccioEnumDto(
			ExpedientPeticioAccioEnumDto expedientPeticioAccioEnumDto) {
		this.expedientPeticioAccioEnumDto = expedientPeticioAccioEnumDto;
	}

	public void updateEstat(
			ExpedientPeticioEstatEnumDto estat) {
		this.estat = estat;
	}

	public void updateMetaExpedient(MetaExpedientEntity metaExpedient) {
		this.metaExpedient = metaExpedient;
	}

	public static Builder getBuilder(
			String identificador,
			String clauAcces,
			Date dataAlta,
			ExpedientPeticioEstatEnumDto estat) {
		return new Builder(
				identificador,
				clauAcces,
				dataAlta,
				estat);
	}
	
	public static class Builder {
		ExpedientPeticioEntity built;
		Builder(
				String identificador,
				String clauAcces,
				Date dataAlta,
				ExpedientPeticioEstatEnumDto estat) {
			built = new ExpedientPeticioEntity();
			built.identificador = identificador;
			built.clauAcces = clauAcces;
			built.dataAlta = dataAlta;
			built.estat = estat;
		}
		
		public Builder expedientPeticioAccioEnumDto(ExpedientPeticioAccioEnumDto expedientPeticioAccioEnumDto) {
			built.expedientPeticioAccioEnumDto = expedientPeticioAccioEnumDto;
			return this;
		}

		public ExpedientPeticioEntity build() {
			return built;
		}
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
