package es.caib.ripea.persistence.entity.resourceentity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import es.caib.ripea.service.intf.config.BaseConfig;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "carpeta_usuari_rel")
@Getter
@Setter
public class CarpetaRestriccioResourceEntity {

    @EmbeddedId
    private CarpetaRestriccioUsuariResourceId id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("carpeta_id")
    private CarpetaResourceEntity carpeta;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuari_codi")
    private UsuariResourceEntity usuari;
    
}
