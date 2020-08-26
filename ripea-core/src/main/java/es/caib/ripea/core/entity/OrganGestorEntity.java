package es.caib.ripea.core.entity;

import java.util.List;

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
import org.hibernate.annotations.Formula;
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
    
    @OneToMany(			
    		mappedBy = "organGestor",
			fetch = FetchType.LAZY)
    private List<MetaExpedientEntity> metaExpedients;
    
//    @Formula("count(metaExpedients)")
//    private int nMetaExpedients;
        
    
    private static final long serialVersionUID = 458331024861203562L;

}
