/**
 *
 */
package es.caib.ripea.core.persistence.entity;

import es.caib.ripea.service.intf.config.BaseConfig;
import lombok.Getter;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * Classe del model de dades que representa els fluxos de firma d'un usuari.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(name = BaseConfig.DB_PREFIX + "flux_firma_usuari")
@EntityListeners(AuditingEntityListener.class)
public class FluxFirmaUsuariEntity extends RipeaAuditable<Long> {

	@Column(name = "nom")
	private String nom;
	
	@Column(name = "descripcio")
	private String descripcio;
	
	@Column(name = "portafirmes_fluxid")
	private String portafirmesFluxId;
	
	@Column(name = "destinataris")
	private String destinataris;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "entitat_id")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "flux_firma_usuari_ent_fk")
	protected EntitatEntity entitat;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "usuari_codi")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "flux_firma_usuari_usu_fk")
	protected UsuariEntity usuari;

	public void update(
			String destinataris) {
		this.destinataris = destinataris;
	}

	public static Builder getBuilder(
			String nom,
			String descripcio,
			String portafirmesFluxId,
			EntitatEntity entitat,
			UsuariEntity usuari) {
		return new Builder(
				nom,
				descripcio,
				portafirmesFluxId,
				entitat,
				usuari);
	}
	public static class Builder {
		FluxFirmaUsuariEntity built;
		Builder(
				String nom,
				String descripcio,
				String portafirmesFluxId,
				EntitatEntity entitat,
				UsuariEntity usuari) {
			built = new FluxFirmaUsuariEntity();
			built.nom = nom;
			built.descripcio = descripcio;
			built.portafirmesFluxId = portafirmesFluxId;
			built.entitat = entitat;
			built.usuari = usuari;
		}
		public FluxFirmaUsuariEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((portafirmesFluxId == null) ? 0 : portafirmesFluxId.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		FluxFirmaUsuariEntity other = (FluxFirmaUsuariEntity) obj;
		if (portafirmesFluxId == null) {
			if (other.portafirmesFluxId != null)
				return false;
		}
		return true;
	}

	private static final long serialVersionUID = 510479572788141646L;

}