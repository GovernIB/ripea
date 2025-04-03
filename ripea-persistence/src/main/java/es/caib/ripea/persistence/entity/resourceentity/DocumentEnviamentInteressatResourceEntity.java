package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.persistence.entity.DocumentNotificacioEntity;
import es.caib.ripea.persistence.entity.InteressatEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.DocumentEnviamentInteressatResource;
import es.caib.ripea.service.intf.model.DocumentNotificacioResource;
import es.caib.ripea.service.intf.model.InteressatResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "document_enviament_inter")
@Getter
@Setter
@NoArgsConstructor
public class DocumentEnviamentInteressatResourceEntity extends BaseAuditableEntity<DocumentEnviamentInteressatResource> {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "interessat_id")
    protected InteressatResourceEntity interessat;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "document_enviament_id")
    protected DocumentNotificacioResourceEntity notificacio;

    @Column(name = "not_env_ref", length = 100)
    private String enviamentReferencia;


    @Column(name = "not_env_dat_estat", length = 20)
    private String enviamentDatatEstat; //Notib: notificaEstat
    @Column(name = "not_env_dat_data")
    private Date enviamentDatatData; //Notib: notificaEstatData
    @Column(name = "not_env_dat_orig", length = 20)
    private String enviamentDatatOrigen;
    @Column(name = "not_env_cert_data")
    @Temporal(TemporalType.DATE)
    private Date enviamentCertificacioData; //Notib: notificaCertificacioData
    @Column(name = "not_env_cert_orig", length = 20)
    private String enviamentCertificacioOrigen;

    @Column(name="not_env_registre_data")
    @Temporal(TemporalType.TIMESTAMP)
    private Date registreData;
    @Column(name="not_env_registre_numero", length = 19)
    private Integer registreNumero;
    @Column(name="not_env_registre_num_formatat", length = 50)
    private String registreNumeroFormatat;

    @Column(name = "error")
    protected Boolean error;
    @Column(name = "error_desc", length = ERROR_DESC_TAMANY)
    protected String errorDescripcio;

    private static final int ERROR_DESC_TAMANY = 2000;

}