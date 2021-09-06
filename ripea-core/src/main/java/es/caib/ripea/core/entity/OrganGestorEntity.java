package es.caib.ripea.core.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.audit.RipeaAuditable;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe de model de dades que conté la informació dels organs gestors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "ipa_organ_gestor", uniqueConstraints = @UniqueConstraint(name = "ipa_organ_gestor_uk", columnNames = {
        "codi", "entitat_id" }))
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class OrganGestorEntity extends RipeaAuditable<Long> {

    @Column(name = "codi", length = 64, nullable = false)
    private String codi;

    @Column(name = "nom", length = 1000)
    private String nom;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "entitat_id")
    @ForeignKey(name = "ipa_entitat_organ_gestor_fk")
    private EntitatEntity entitat;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "pare_id")
    private OrganGestorEntity pare;

    @Column(name = "actiu")
    private boolean actiu;
    
    @Column(name = "gestio_direct")
    private boolean gestioDirect;
    
    @OneToMany(			
    		mappedBy = "organGestor",
			fetch = FetchType.LAZY)
    private List<MetaExpedientEntity> metaExpedients;
    
    @OneToMany(			
    		mappedBy = "pare",
			fetch = FetchType.LAZY,
			cascade= { CascadeType.PERSIST })
    private List<OrganGestorEntity> fills;        
    
    @PreRemove
    private void preRemove() {
        if (this.getFills() != null) {
            for (OrganGestorEntity fill : this.getFills()) {
                fill.setPare(null);
            }
        }
    }
    
    public List<OrganGestorEntity> getAllChildren(){
    	List<OrganGestorEntity> result = new ArrayList<OrganGestorEntity>();
    	result.add(this);
    	this.getAllChildren(result);
    	return result;
    }
    
    public void getAllChildren(List<OrganGestorEntity> result) {
    	for (OrganGestorEntity fill : this.getFills()) {
			result.add(fill);
			fill.getAllChildren(result);
		} 
    }
    
    

    public static Builder getBuilder(String codi) {
        return new Builder(codi);
    }
    public static class Builder {

        OrganGestorEntity built;
        Builder(String codi) {
            built = new OrganGestorEntity();
            built.codi = codi;
        }

        public OrganGestorEntity build() {
            return built;
        }
        public Builder nom(String nom) {
            built.nom = nom;
            return this;
        }
        public Builder entitat(EntitatEntity entitat) {
            built.entitat = entitat;
            return this;
        }
        public Builder pare(OrganGestorEntity pare) {
            built.pare = pare;
            return this;
        }
        public Builder actiu(boolean actiu) {
            built.actiu = actiu;
            return this;
        }
        public Builder gestioDirect(boolean gestioDirect) {
            built.gestioDirect = gestioDirect;
            return this;
        }
    }

	public void update(
			String codi,
			String nom,
			OrganGestorEntity pare,
			boolean gestioDirect) {
		this.codi = codi;
		this.nom = nom;
		this.pare = pare;
		this.gestioDirect = gestioDirect;
	}
    
    
    
    
    private static final long serialVersionUID = 458331024861203562L;

}
