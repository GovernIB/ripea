package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientPeticioEntity;
import es.caib.ripea.persistence.entity.RegistreAnnexEntity;
import es.caib.ripea.persistence.entity.RegistreInteressatEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import es.caib.ripea.service.intf.dto.TascaEstatEnumDto;
import es.caib.ripea.service.intf.model.ExpedientTascaResource;
import es.caib.ripea.service.intf.model.RegistreResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import org.hibernate.annotations.Formula;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "registre")
@Getter
@Setter
@NoArgsConstructor
public class RegistreResourceEntity extends BaseAuditableEntity<RegistreResource> {

    @Column(name = "aplicacio_codi", length = 20)
    private String aplicacioCodi;
    @Column(name = "aplicacio_versio", length = 15)
    private String aplicacioVersio;
    @Column(name = "assumpte_codi_codi", length = 16)
    private String assumpteCodiCodi;
    @Column(name = "assumpte_codi_desc", length = 100)
    private String assumpteCodiDescripcio;
    @Column(name = "assumpte_tipus_codi", length = 16)
    private String assumpteTipusCodi;
    @Column(name = "assumpte_tipus_desc", length = 100)
    private String assumpteTipusDescripcio;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data", nullable = false)
    private Date data;
    @Column(name = "doc_fisica_codi", length = 1)
    private String docFisicaCodi;
    @Column(name = "doc_fisica_desc", length = 100)
    private String docFisicaDescripcio;
    @Column(name = "entitat_codi", length = 21, nullable = false)
    private String entitatCodi;
    @Column(name = "entitat_desc", length = 100)
    private String entitatDescripcio;
    @Column(name = "expedient_numero", length = 80)
    private String expedientNumero;
    @Column(name = "exposa", length = 4000)
    private String exposa;
    @Column(name = "extracte", length = 240)
    private String extracte;
    @Column(name = "procediment_codi", length = 20)
    private String procedimentCodi;
    @Column(name = "identificador", length = 100, nullable = false)
    private String identificador;
    @Column(name = "idioma_codi", length = 2, nullable = false)
    private String idiomaCodi;
    @Column(name = "idioma_desc", length = 100)
    private String idiomaDescripcio;
    @Column(name = "llibre_codi", length = 4, nullable = false)
    private String llibreCodi;
    @Column(name = "llibre_desc", length = 100)
    private String llibreDescripcio;
    @Column(name = "observacions", length = 50)
    private String observacions;
    @Column(name = "oficina_codi", length = 21, nullable = false)
    private String oficinaCodi;
    @Column(name = "oficina_desc", length = 100)
    private String oficinaDescripcio;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "origen_data")
    private Date origenData;
    @Column(name = "origen_registre_num", length = 80)
    private String origenRegistreNumero;
    @Column(name = "ref_externa", length = 16)
    private String refExterna;
    @Column(name = "solicita", length = 4000)
    private String solicita;
    @Column(name = "transport_num", length = 20)
    private String transportNumero;
    @Column(name = "transport_tipus_codi", length = 2)
    private String transportTipusCodi;
    @Column(name = "transport_tipus_desc", length = 100)
    private String transportTipusDescripcio;
    @Column(name = "usuari_codi", length = 20)
    private String usuariCodi;
    @Column(name = "usuari_nom", length = 80)
    private String usuariNom;
    @Column(name = "desti_codi", length = 21, nullable = false)
    private String destiCodi;
    @Column(name = "desti_descripcio", length = 100)
    private String destiDescripcio;
    @Column(name = "justificant_arxiu_uuid", length = 256)
    private String justificantArxiuUuid;
    @Formula("DESTI_CODI||' - '||DESTI_DESCRIPCIO")
    private String destiCodiINom;

    @OneToMany(
            mappedBy = "registre",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<RegistreInteressatResourceEntity> interessats = new ArrayList<RegistreInteressatResourceEntity>();

//    @OneToMany(
//            mappedBy = "registre",
//            cascade = CascadeType.ALL,
//            orphanRemoval = true)
//    private List<RegistreAnnexEntity> annexos = new ArrayList<RegistreAnnexEntity>();


    // removed "cascade = CascadeType.ALL, orphanRemoval = true" because registreRepository.delete(registre) in ExpedientPeticioHelper.crearExpedientsPeticions() was removing also expedientPeticio
//    @OneToMany(
//            mappedBy = "registre",
//            fetch = FetchType.LAZY)
//    private List<ExpedientPeticioEntity> expedientPeticions = new ArrayList<ExpedientPeticioEntity>();



    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "entitat_id")
    protected EntitatResourceEntity entitat;
}
