package es.caib.ripea.persistence.entity.resourceentity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.InteressatGrupResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Classe del model de dades que representa un grup d'interessats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "interessat_grup")
@Getter
@Setter
@NoArgsConstructor
public class InteressatGrupResourceEntity extends BaseAuditableEntity<InteressatGrupResource> {

	@Column(name = "nom", length = 255)
	private String nom;
	
	@Column(name = "descripcio", length = 1024)
	private String descripcio;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "expedient_id")
	protected ExpedientResourceEntity expedient;
	
	@ManyToMany(mappedBy = "grups", fetch = FetchType.LAZY)
	protected List<InteressatResourceEntity> interessats;
	
}
