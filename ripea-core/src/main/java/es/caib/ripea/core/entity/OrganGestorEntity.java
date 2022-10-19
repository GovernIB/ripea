package es.caib.ripea.core.entity;

import es.caib.ripea.core.api.dto.OrganEstatEnumDto;
import es.caib.ripea.core.api.dto.TipusTransicioEnumDto;
import es.caib.ripea.core.audit.RipeaAuditable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
@ToString
public class OrganGestorEntity extends RipeaAuditable<Long> {

    @Column(name = "codi", length = 64, nullable = false)
    private String codi;

    @Column(name = "nom", length = 1000)
    private String nom;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "entitat_id")
    @ForeignKey(name = "ipa_entitat_organ_gestor_fk")
    private EntitatEntity entitat;

    @ToString.Exclude
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "pare_id")
    private OrganGestorEntity pare;

    @Column(name = "actiu")
    private boolean actiu;
    
    @Column(name = "gestio_direct")
    private boolean gestioDirect;

    @Column(name = "estat", length = 1)
    @Enumerated(EnumType.STRING)
    private OrganEstatEnumDto estat;

    @ToString.Exclude
    @OneToMany(			
    		mappedBy = "organGestor",
			fetch = FetchType.LAZY)
    private List<MetaExpedientEntity> metaExpedients;

    @ToString.Exclude
    @OneToMany(			
    		mappedBy = "pare",
			fetch = FetchType.LAZY,
			cascade= { CascadeType.PERSIST })
    private List<OrganGestorEntity> fills;

    @JoinTable(name = "ipa_og_sinc_rel",
            joinColumns = { @JoinColumn(name = "antic_og", referencedColumnName = "id", nullable = false) },
            inverseJoinColumns = { @JoinColumn(name = "nou_og", referencedColumnName = "id", nullable = false) })
    @ToString.Exclude
    @ManyToMany
    private List<OrganGestorEntity> nous = new ArrayList<>();

    @ToString.Exclude
    @ManyToMany(mappedBy = "nous")
    private List<OrganGestorEntity> antics = new ArrayList<>();

    @Column(name = "tipus_transicio", length = 12)
    @Enumerated(EnumType.STRING)
    private TipusTransicioEnumDto tipusTransicio;

    
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

    public void addNou(OrganGestorEntity nou) {
        nous.add(nou);
    }
    public void addAntic(OrganGestorEntity antic) {
        antics.add(antic);
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
        public Builder estat(String estat) {
            built.estat = OrganGestorEntity.getEstat(estat);
            return this;
        }
        public Builder estat(OrganEstatEnumDto estat) {
            built.estat = estat;
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
	
	public void updateEstat(
			OrganEstatEnumDto estat) {
		this.estat = estat;
	}

    public void update(
            String nom,
            String estat,
            OrganGestorEntity pare) {
        this.nom = nom;
        this.estat = getEstat(estat);
        this.pare = pare;
        this.gestioDirect = false;
    }

    public static OrganEstatEnumDto getEstat(String estat) {
        switch (estat) {
            case "E": return OrganEstatEnumDto.E;
            case "A": return OrganEstatEnumDto.A;
            case "T": return OrganEstatEnumDto.T;
            case "V":
            default:
                return OrganEstatEnumDto.V;
        }
    }
    
    
    
    private static final long serialVersionUID = 458331024861203562L;

}
