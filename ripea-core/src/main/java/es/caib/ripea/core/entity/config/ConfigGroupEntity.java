package es.caib.ripea.core.entity.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Getter
@Entity
@Table(	name = "IPA_CONFIG_GROUP")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigGroupEntity {

    @Id
    @Column(name = "CODE", length = 128, nullable = false)
    private String key;

    @Column(name = "DESCRIPTION", length = 512, nullable = true)
    private String description;

    @Column(name = "POSITION")
    private int position;

    @Column(name = "PARENT_CODE")
    private String parentCode;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_CODE")
    @OrderBy("position ASC")
    private Set<ConfigEntity> configs;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_CODE")
    @OrderBy("position ASC")
    private Set<ConfigGroupEntity> innerConfigs;
}
