package es.caib.ripea.persistence.entity;

import es.caib.ripea.service.intf.config.BaseConfig;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Getter
@Builder(builderMethodName = "hiddenBuilder")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = BaseConfig.DB_PREFIX + "processos_inicials")
public class ProcesosInicialsEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "codi", nullable = false)
    private ProcessosInicialsEnum codi;

    @Setter
    @Column(name = "init", nullable = false)
    private boolean init;

    public void setId(Long id) {
        this.id = id;
    }

    public enum ProcessosInicialsEnum implements Serializable {
        PROPIETATS_CONFIG_ENTITATS,
        GENERAR_EXPEDIENT_NUMERO,
        GENERAR_MISSING_HISTORICS,
        ORGANS_DESCARREGAR_NOM_CATALA,
        GRUPS_PERMISOS;
    }

}