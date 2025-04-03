package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.ExpedientPeticioAccioEnumDto;
import es.caib.ripea.service.intf.dto.ExpedientPeticioEstatEnumDto;
import es.caib.ripea.service.intf.model.ExpedientPeticioResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "expedient_peticio")
@Getter
@Setter
@NoArgsConstructor
public class ExpedientPeticioResourceEntity extends BaseAuditableEntity<ExpedientPeticioResource> {

    @Column(name = "identificador", nullable = false)
    String identificador;
    @Column(name = "clau_acces", nullable = false)
    private String clauAcces;
    @Column(name = "data_alta", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAlta;
    @Enumerated(EnumType.STRING)
    @Column(name = "estat", nullable = false, length = 40)
    private ExpedientPeticioEstatEnumDto estat;
    @Column(name = "data_actualitzacio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataActualitzacio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuari_actualitzacio")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "usuari_actual_exp_pet_fk")
    private UsuariResourceEntity usuariActualitzacio;

    @Column(name = "observacions", length = 4000)
    private String observacions;


    // these fields are filled if error occurs while getting anotacio from DISTRIBUCIO and saving it in DB
    @Column(name = "consulta_ws_error")
    private boolean consultaWsError = false;
    @Column(name = "consulta_ws_error_desc", length = 4000)
    private String consultaWsErrorDesc;
    @Column(name = "consulta_ws_error_date")
    private Date consultaWsErrorDate;

    @Column(name = "notifica_dist_error", length = 4000)
    private String notificaDistError;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metaexpedient_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "exp_pet_metaexp_fk")
    private MetaExpedientResourceEntity metaExpedient;

    @Enumerated(EnumType.STRING)
    @Column(name = "exp_peticio_accio", length = 20)
    private ExpedientPeticioAccioEnumDto expedientPeticioAccioEnumDto;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "registre_id")
    protected RegistreResourceEntity registre;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "expedient_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "expedient_registre_fk")
    private ExpedientResourceEntity expedient;

    @Column(name = "pendent_canvi_estat_dis")
    private boolean pendentCanviEstatDistribucio;
    @Column(name = "reintents_canvi_estat_dis")
    private int reintentsCanviEstatDistribucio;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "grup_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "grup_exp_pet_fk")
    private GrupResourceEntity grup;
}
