package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.service.intf.config.BaseConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entitat de base de dades que representa un expedient.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "expedient")
@Getter
@Setter
@NoArgsConstructor
public class ExpedientResourceEntity extends NodeResourceEntity {

	@Column(name = "codi", nullable = false)
	protected String codi;
	@Column(name = "numero", length = 64, nullable = false)
	protected String numero;

}
