package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.RegistreInteressatResource;
import es.caib.ripea.service.intf.registre.RegistreInteressatDocumentTipusEnum;
import es.caib.ripea.service.intf.registre.RegistreInteressatTipusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "registre_interessat")
@Getter
@Setter
@NoArgsConstructor
public class RegistreInteressatResourceEntity extends BaseAuditableEntity<RegistreInteressatResource> {

    @Column(name = "adresa", length = 160)
    private String adresa;
    @Column(name = "canal", length = 30)
    private String canal;
    @Column(name = "cp", length = 5)
    private String cp;
    @Column(name = "doc_numero", length = 17)
    private String documentNumero;
    @Enumerated(EnumType.STRING)
    @Column(name = "doc_tipus", length = 15)
    private RegistreInteressatDocumentTipusEnum documentTipus;
    @Column(name = "email", length = 160)
    private String email;
    @Column(name = "llinatge1", length = 30)
    private String llinatge1;
    @Column(name = "llinatge2", length = 30)
    private String llinatge2;
    @Column(name = "nom", length = 30)
    private String nom;
    @Column(name = "observacions", length = 160)
    private String observacions;
    @Column(name = "municipi_codi", length = 100)
    private String municipiCodi;
    @Column(name = "pais_codi", length = 4)
    private String paisCodi;
    @Column(name = "provincia_codi", length = 100)
    private String provinciaCodi;
    @Column(name = "municipi", length = 200)
    private String municipi;
    @Column(name = "pais", length = 200)
    private String pais;
    @Column(name = "provincia", length = 200)
    private String provincia;
    @Column(name = "rao_social", length = 80)
    private String raoSocial;
    @Column(name = "telefon", length = 20)
    private String telefon;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipus", length = 40, nullable = false)
    private RegistreInteressatTipusEnum tipus;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "representant_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "interessat_representant_fk")
    private RegistreInteressatResourceEntity representant;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registre_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "interessat_registre_fk")
    private RegistreResourceEntity registre;
    @Column(name = "organ_codi", length = 9)
    private String organCodi;

}
