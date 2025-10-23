package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseEntity;
import es.caib.ripea.persistence.entity.config.ExtendedPermissionEnumConverter;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.ExtendedPermissionEnum;
import es.caib.ripea.service.intf.model.AclEntryResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Entitat de base de dades que representa un node.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "acl_entry")
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class AclEntryResourceEntity extends BaseEntity<AclEntryResource> {

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "acl_object_identity", nullable = false)
    private AclObjIdentityResourceEntity aclObjectIdentity;
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "sid", nullable = false)
    private AclSidResourceEntity sid;
    @Column(name = "ace_order", nullable = false)
    private Integer order;
    @Column(name = "mask", nullable = false)
    @Convert(converter = ExtendedPermissionEnumConverter.class)
    private ExtendedPermissionEnum mask;
    @Column(name = "granting", nullable = false)
    private Boolean granting;

    @Column(name = "audit_success", nullable = false)
    private boolean auditSuccess = false;
    @Column(name = "audit_failure", nullable = false)
    private boolean auditFailure = false;

}
