/**
 * 
 */
package es.caib.ripea.core.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Classe del model de dades que representa un meta-expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(
		name = "ipa_metaexpedient",
		uniqueConstraints = {
				@UniqueConstraint(name = "ipa_metaexp_entitat_codi_uk", columnNames = {"entitat_id", "codi"})
		}
)
@EntityListeners(AuditingEntityListener.class)
public class MetaExpedientEntity extends MetaNodeEntity {

	@Column(name = "clasif_sia", length = 30, nullable = false)
	private String classificacioSia;
	@Column(name = "serie_doc", length = 30, nullable = false)
	private String serieDocumental;
	@Column(name = "expressio_numero", length = 100)
	private String expressioNumero;
	@Column(name = "not_activa", nullable = false)
	private boolean notificacioActiva;
	@ManyToOne(
			optional = true,
			fetch = FetchType.EAGER)
	@JoinColumn(name = "pare_id")
	@ForeignKey(name = "ipa_pare_metaexp_fk")
	private MetaExpedientEntity pare;

	@OneToMany(mappedBy = "metaExpedient", cascade = {CascadeType.ALL})
	protected Set<MetaExpedientSequenciaEntity> sequencies;
		
	@OneToMany(mappedBy = "metaExpedient", cascade = {CascadeType.ALL})
	protected Set<MetaDocumentEntity> metaDocuments;
	
	@OneToMany(mappedBy = "metaExpedient", cascade = {CascadeType.ALL})
	protected Set<ExpedientEstatEntity> estats;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "entitat_id")
	@ForeignKey(name = "ipa_entitat_metaexp_fk")
	private EntitatEntity entitatPropia;
	@Column(name = "codi", length = 64, nullable = false)
	private String codiPropi;

	public String getClassificacioSia() {
		return classificacioSia;
	}
	public String getSerieDocumental() {
		return serieDocumental;
	}
	public String getExpressioNumero() {
		return expressioNumero;
	}
	public boolean isNotificacioActiva() {
		return notificacioActiva;
	}
	public MetaExpedientEntity getPare() {
		return pare;
	}

	public void update(
			String codi,
			String nom,
			String descripcio,
			String classificacioSia,
			String serieDocumental,
			String expressioNumero,
			boolean notificacioActiva,
			MetaExpedientEntity pare) {
		super.update(
				codi,
				nom,
				descripcio);
		this.classificacioSia = classificacioSia;
		this.serieDocumental = serieDocumental;
		this.expressioNumero = expressioNumero;
		this.notificacioActiva = notificacioActiva;
		this.pare = pare;
	}

	public static Builder getBuilder(
			String codi,
			String nom,
			String descripcio,
			String serieDocumental,
			String classificacioSia,
			boolean notificacioActiva,
			EntitatEntity entitat,
			MetaExpedientEntity pare) {
		return new Builder(
				codi,
				nom,
				descripcio,
				serieDocumental,
				classificacioSia,
				entitat,
				pare,
				notificacioActiva);
	}

	public static class Builder {
		MetaExpedientEntity built;
		Builder(
				String codi,
				String nom,
				String descripcio,
				String serieDocumental,
				String classificacioSia,
				EntitatEntity entitat,
				MetaExpedientEntity pare,
				boolean notificacioActiva) {
			built = new MetaExpedientEntity();
			built.codi = codi;
			built.nom = nom;
			built.descripcio = descripcio;
			built.serieDocumental = serieDocumental;
			built.classificacioSia = classificacioSia;
			built.entitat = entitat;
			built.tipus = MetaNodeTipusEnum.EXPEDIENT;
			built.pare = pare;
			built.notificacioActiva = notificacioActiva;
			built.codiPropi = codi;
			built.entitatPropia = entitat;
		}
		public Builder expressioNumero(String expressioNumero) {
			built.expressioNumero = expressioNumero;
			return this;
		}

		public MetaExpedientEntity build() {
			return built;
		}
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
