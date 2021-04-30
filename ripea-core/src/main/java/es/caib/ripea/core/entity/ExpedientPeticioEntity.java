/**
 * 
 */
package es.caib.ripea.core.entity;

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

/**
 * Classe del model de dades que representa una petició de creació d’expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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
	
	
	// these fields are filled if error occurs while getting anotacio from DISTRIBUCIO and saving it in DB
	@Column(name = "consulta_ws_error")
	private boolean consultaWsError = false;
	@Column(name = "consulta_ws_error_desc", length = 4000)
	private String consultaWsErrorDesc;
	@Column(name = "consulta_ws_error_date")
	private Date consultaWsErrorDate;
	
	@Column(name = "notifica_dist_error", length = 4000)
	private String notificaDistError;
	
	

	@Column(name = "meta_expedient_nom", length = 256)
	private String metaExpedientNom;
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
	
	
	

	public String getNotificaDistError() {
		return notificaDistError;
	}
	public void updateNotificaDistError(String notificaDistError) {
		this.notificaDistError = StringUtils.abbreviate(
				notificaDistError,
				4000);
	}
	public ExpedientEntity getExpedient() {
		return expedient;
	}
	public void updateExpedient(ExpedientEntity expedient) {
		this.expedient = expedient;
	}
	public boolean isConsultaWsError() {
		return consultaWsError;
	}
	public void updateConsultaWsError(
			boolean consultaWsError) {
		this.consultaWsError = consultaWsError;
	}

	public String getConsultaWsErrorDesc() {
		return consultaWsErrorDesc;
	}
	public void updateConsultaWsErrorDesc(
			String consultaWsErrorDesc) {
		this.consultaWsErrorDesc = consultaWsErrorDesc;
	}
	public Date getConsultaWsErrorDate() {
		return consultaWsErrorDate;
	}
	public void updateConsultaWsErrorDate(
			Date consultaWsErrorDate) {
		this.consultaWsErrorDate = consultaWsErrorDate;
	}
	public String getMetaExpedientNom() {
		return metaExpedientNom;
	}
	public ExpedientPeticioAccioEnumDto getExpedientPeticioAccioEnumDto() {
		return expedientPeticioAccioEnumDto;
	}
	public String getIdentificador() {
		return identificador;
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
	public void updateMetaExpedientNom(
			String metaExpedientNom) {
		this.metaExpedientNom = metaExpedientNom;
	}
	public void updateExpedientPeticioAccioEnumDto(
			ExpedientPeticioAccioEnumDto expedientPeticioAccioEnumDto) {
		this.expedientPeticioAccioEnumDto = expedientPeticioAccioEnumDto;
	}
	public void updateDataAlta(
			Date dataAlta) {
		this.dataAlta = dataAlta;
	}
	public Date getDataAlta() {
		return dataAlta;
	}
	public void updateEstat(
			ExpedientPeticioEstatEnumDto estat) {
		this.estat = estat;
	}
	public ExpedientPeticioEstatEnumDto getEstat() {
		return estat;
	}

	public RegistreEntity getRegistre() {
		return registre;
	}
	public String getClauAcces() {
		return clauAcces;
	}

	public static Builder getBuilder(
			String identificador,
			String clauAcces,
			Date dataAlta,
			ExpedientPeticioEstatEnumDto estat) {
		return new Builder(identificador, clauAcces, dataAlta, estat);
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
		
		public Builder metaExpedientNom(String metaExpedientNom) {
			built.metaExpedientNom = metaExpedientNom;
			return this;
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
