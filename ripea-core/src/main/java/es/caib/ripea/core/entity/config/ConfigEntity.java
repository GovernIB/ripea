package es.caib.ripea.core.entity.config;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.entity.UsuariEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Classe del model de dades que representa una alerta d'error en segón pla.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@Entity
@Table(	name = "IPA_CONFIG")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ConfigEntity {
    @Id
    @Column(name = "KEY", length = 256, nullable = false)
    private String key;

    @Column(name = "VALUE", length = 2048, nullable = true)
    private String value;

    @Column(name = "DESCRIPTION", length = 2048, nullable = true)
    private String description;

    @Column(name = "JBOSS_PROPERTY", nullable = false)
    private boolean jbossProperty;

    @Column(name = "GROUP_CODE", length = 2048, nullable = true)
    private String groupCode;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE_CODE", insertable = false, updatable = false)
    @ForeignKey(name = "NOT_CONFIG_TYPE_FK")
    private ConfigTypeEntity type;
    
    @Column(name = "ENTITAT_CODI", length = 64)
    private String entitatCodi;
    
    @Column(name = "CONFIGURABLE")
    private boolean configurable;
     

    @Column(name = "POSITION")
    private int position;

    @ManyToOne
    private UsuariEntity lastModifiedBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    public ConfigEntity(String key, String value) {
        this.key = key;
        this.value = value;
    }
    /**
     * Per a mapejar el Dto de la vista.
     *
     * @return El llistat de possibles valors que pot prendre la propietat
     */
    public List<String> getValidValues() {
       return type == null ? Collections.<String>emptyList() : type.getValidValues();
    }
    public String getTypeCode() {
        return type == null ? "" : type.getCode();
    }

    public void update(String value) {
        this.value = value;
    }
    
    public void crearConfigNova(String key, String entitatCodi, ConfigEntity entitat) {

        this.key = key;
        this.value = null;
        this.description = entitat.getDescription();
        this.jbossProperty = entitat.isJbossProperty();
        this.groupCode = entitat.getGroupCode();
        this.type = entitat.getType();
        this.entitatCodi = entitatCodi;
    }
    
}
