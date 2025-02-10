package es.caib.ripea.core.persistence.entity;

import es.caib.ripea.service.intf.config.BaseConfig;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe del model de dades que representa una carpeta per defecte d'un procediment
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@Entity
@Table(	name = BaseConfig.DB_PREFIX + "metaexpedient_carpeta",
		uniqueConstraints = {
				@UniqueConstraint(
						name = BaseConfig.DB_PREFIX + "metaexpedient_carpeta_mult_uk",
						columnNames = {
								"nom",
								"meta_expedient_id"})})
@EntityListeners(AuditingEntityListener.class)
public class MetaExpedientCarpetaEntity extends RipeaAuditable<Long> {

	@Version
	@Column(name = "version")
	private int version;

	@Column(name = "nom", length = 256, nullable = false)
	protected String nom;
	
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "pare_id")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "metaexpedient_carpeta_pare_fk")
	protected MetaExpedientCarpetaEntity pare;
	
	
	@OneToMany(
			mappedBy = "pare",
			fetch = FetchType.LAZY,
			orphanRemoval = true)
	protected Set<MetaExpedientCarpetaEntity> fills = new HashSet<MetaExpedientCarpetaEntity>();
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "meta_expedient_id")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "metaexp_metaexpcarp_fk")
	private MetaExpedientEntity metaExpedient;
	
	public void update(String nom) {
		this.nom = nom;
	}
	
	public static Builder getBuilder(
			String nom,
			MetaExpedientCarpetaEntity pare,
			MetaExpedientEntity metaExpedient) {
		return new Builder(
				nom,
				pare,
				metaExpedient);
	}

    public static class Builder {
    	MetaExpedientCarpetaEntity built;

		Builder(
				String nom,
				MetaExpedientCarpetaEntity pare,
				MetaExpedientEntity metaExpedient) {
            built = new MetaExpedientCarpetaEntity();
            built.nom = nom;
            built.pare = pare;
            built.metaExpedient = metaExpedient;
        }
		
		public MetaExpedientCarpetaEntity build() {
            return built;
        }
    }
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((pare == null) ? 0 : pare.hashCode());
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		result = prime * result + ((metaExpedient == null) ? 0 : metaExpedient.hashCode());
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
		MetaExpedientCarpetaEntity other = (MetaExpedientCarpetaEntity) obj;
		if (pare == null) {
			if (other.pare != null)
				return false;
		} else if (!pare.equals(other.pare))
			return false;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		if (metaExpedient == null) {
			if (other.metaExpedient != null)
				return false;
		} else if (!metaExpedient.equals(other.metaExpedient))
			return false;
		return true;
	}
	
	private static final long serialVersionUID = -4953686559706244177L;

}
