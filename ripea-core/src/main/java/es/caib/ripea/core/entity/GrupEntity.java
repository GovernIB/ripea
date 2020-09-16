/**
 *
 */
package es.caib.ripea.core.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.audit.RipeaAuditable;

/**
 * Classe del model de dades que representa un grup.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "ipa_grup")
@EntityListeners(AuditingEntityListener.class)
public class GrupEntity extends RipeaAuditable<Long> {


	@Column(name = "rol", length = 50, nullable = false)
	private String rol;
	@Column(name = "descripcio", length = 512, nullable = false)
	private String descripcio;
	
	
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "entitat_id")
	@ForeignKey(name = "ipa_entitat_ipa_grup_fk")
	protected EntitatEntity entitat;

	@ManyToMany(mappedBy = "grups", fetch = FetchType.EAGER)
	protected List<MetaExpedientEntity> metaExpedients = new ArrayList<MetaExpedientEntity>();

	public List<MetaExpedientEntity> getMetaExpedients() {
		return metaExpedients;
	}
	public void setMetaExpedients(List<MetaExpedientEntity> metaExpedients) {
		this.metaExpedients = metaExpedients;
	}
	public String getRol() {
	    return rol;
	}
	public String getDescripcio() {
	    return descripcio;
	}
	public EntitatEntity getEntitat() {
	    return entitat;
	}
	public static Builder getBuilder(
			String rol,
			String descripcio,
			EntitatEntity entitat) {
		return new Builder(
				rol,
				descripcio,
				entitat);
	}
	
	public static class Builder {

	    GrupEntity built;

	    Builder(String rol, String descripcio, EntitatEntity entitat) {
	        built = new GrupEntity();
	        built.rol = rol;
	        built.descripcio = descripcio;
	        built.entitat = entitat;
	    }

	    public GrupEntity build() {
	        return built;
	    }
	}
	public void update(String rol, String descripcio) {
	    this.rol = rol;
	    this.descripcio = descripcio;
	}

	private static final long serialVersionUID = -8765569320503898715L;

}