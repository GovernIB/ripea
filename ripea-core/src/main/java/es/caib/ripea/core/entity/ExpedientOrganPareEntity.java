/**
 * 
 */
package es.caib.ripea.core.persistence;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.audit.RipeaAuditable;
import lombok.Getter;

/**
 * Classe del model de dades que representa un òrgan pare d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(name = "ipa_expedient_organpare")
@EntityListeners(AuditingEntityListener.class)
public class ExpedientOrganPareEntity extends RipeaAuditable<Long>{

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "expedient_id")
	@ForeignKey(name = "ipa_exporgpare_expedient_fk")
	private ExpedientEntity expedient;

	@ManyToOne(
			optional = false,
			fetch = FetchType.EAGER)
	@JoinColumn(name = "meta_expedient_organ_id")
	@ForeignKey(name = "ipa_exporgpare_metaexporg_fk")
	private MetaExpedientOrganGestorEntity metaExpedientOrganGestor;

	public static Builder getBuilder(
			ExpedientEntity expedient,
			MetaExpedientOrganGestorEntity metaExpedientOrganGestor) {
		return new Builder(
				expedient,
				metaExpedientOrganGestor);
	}

	public static class Builder {
		ExpedientOrganPareEntity built;
		Builder(
				ExpedientEntity expedient,
				MetaExpedientOrganGestorEntity metaExpedientOrganGestor) {
			built = new ExpedientOrganPareEntity();
			built.expedient = expedient;
			built.metaExpedientOrganGestor = metaExpedientOrganGestor;
		}
		public ExpedientOrganPareEntity build() {
			return built;
		}
	}

	private static final long serialVersionUID = 2049469376271209018L;

}
