package es.caib.ripea.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.NaturalId;
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
@Table(name = "ipa_organ_gestor", uniqueConstraints = @UniqueConstraint(name = "ipa_oge_dir3_uk", columnNames = {
        "codi" }))
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class OrganGestorEntity extends RipeaAuditable<Long> {

    @NaturalId
    @Column(name = "codi", length = 64, nullable = false, unique = true)
    private String codi;

    @Column(name = "nom", length = 1000)
    private String nom;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "entitat_id")
    @ForeignKey(name = "ipa_entitat_organ_gestor_fk")
    private EntitatEntity entitat;

    @Column(name = "pare_codi", length = 64)
    private String pare;

    private static final long serialVersionUID = 458331024861203562L;

}