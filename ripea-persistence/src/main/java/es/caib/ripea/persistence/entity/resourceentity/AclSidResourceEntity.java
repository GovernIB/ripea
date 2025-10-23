package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.PrincipalTipusEnumDto;
import es.caib.ripea.service.intf.model.AclSidResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * Entitat de base de dades que representa un node.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "acl_sid")
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class AclSidResourceEntity extends BaseEntity<AclSidResource> {

    @Column(name = "principal", nullable = false)
    private PrincipalTipusEnumDto principal;
    @Column(name = "sid", length = 100, nullable = false)
    private String sid;
    @OneToMany(
            mappedBy = "sid", fetch = FetchType.EAGER)
    private List<AclEntryResourceEntity> entries;
}
