package es.caib.ripea.persistence.entity.resourceentity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import es.caib.ripea.persistence.base.entity.ResourceEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.CarpetaResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "carpeta")
@Getter
@Setter
@NoArgsConstructor
public class CarpetaResourceEntity extends ContingutResourceEntity<CarpetaResource> implements ResourceEntity<CarpetaResource, Long> {

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "expedient_relacionat",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "carpeta_exprel_fk"))
	private ExpedientResourceEntity expedientRelacionat;
	
	@Column(name = "restringida", nullable = false)
	private Boolean restringida = false;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_restriccio_codi")
    private UsuariResourceEntity responsableRestriccio;    
    
	@Column(name = "motiu_restriccio")
	private String motiuRestriccio;
    
	@OneToMany(mappedBy = "carpeta", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CarpetaRestriccioResourceEntity> restriccions;
	
}
