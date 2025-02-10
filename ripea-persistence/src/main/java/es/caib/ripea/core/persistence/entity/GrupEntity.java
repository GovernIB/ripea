/**
 *
 */
package es.caib.ripea.core.persistence.entity;

import es.caib.ripea.service.intf.config.BaseConfig;
import lombok.Getter;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe del model de dades que representa un grup.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "grup")
@EntityListeners(AuditingEntityListener.class)
@Getter
public class GrupEntity extends RipeaAuditable<Long> {
	

	@Column(name = "rol", length = 50, nullable = false)
	private String rol;
	@Column(name = "codi", length = 50, nullable = false)
	private String codi;
	@Column(name = "descripcio", length = 512, nullable = false)
	private String descripcio;
	
	
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "entitat_id")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "entitat_ipa_grup_fk")
	protected EntitatEntity entitat;

    
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "organ_id")
    @ForeignKey(name = BaseConfig.DB_PREFIX + "organ_grup_fk")
    private OrganGestorEntity organGestor;

	@ManyToMany(mappedBy = "grups", fetch = FetchType.EAGER)
	protected List<MetaExpedientEntity> metaExpedients = new ArrayList<MetaExpedientEntity>();

	public List<MetaExpedientEntity> getMetaExpedients() {
		return metaExpedients;
	}
	public void setMetaExpedients(List<MetaExpedientEntity> metaExpedients) {
		this.metaExpedients = metaExpedients;
	}

	public void updateOrganGestor(OrganGestorEntity organGestor) {
		this.organGestor = organGestor;
	}

	public static Builder getBuilder(
			String codi,
			String descripcio,
			EntitatEntity entitat, 
			OrganGestorEntity organGestor) {
		return new Builder(
				codi,
				descripcio,
				entitat, 
				organGestor);
	}
	
	public static class Builder {

	    GrupEntity built;

		Builder(
				String codi,
				String descripcio,
				EntitatEntity entitat,
				OrganGestorEntity organGestor) {
	        built = new GrupEntity();
	        built.codi = codi;
	        built.descripcio = descripcio;
	        built.entitat = entitat;
	        built.organGestor = organGestor;
	    }

	    public GrupEntity build() {
	        return built;
	    }
	}

	public void update(
			String codi,
			String descripcio,
			OrganGestorEntity organGestor) {
	    this.codi = codi;
	    this.descripcio = descripcio;
	    this.organGestor = organGestor;
	}

	private static final long serialVersionUID = -8765569320503898715L;

}