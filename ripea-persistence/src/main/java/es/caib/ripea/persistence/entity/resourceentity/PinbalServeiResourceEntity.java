package es.caib.ripea.persistence.entity.resourceentity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.PinbalServeiResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "pinbal_servei")
@Getter
@Setter
@NoArgsConstructor
public class PinbalServeiResourceEntity extends BaseAuditableEntity<PinbalServeiResource> {

	@Column(name = "codi", length = 64, nullable = false)
	private String codi;
	@Column(name = "nom", length = 256)
	private String nom;
	@Column(name = "doc_permes_dni", nullable = false)
	private boolean pinbalServeiDocPermesDni;
	@Column(name = "doc_permes_nif", nullable = false)
	private boolean pinbalServeiDocPermesNif;
	@Column(name = "doc_permes_cif", nullable = false)
	private boolean pinbalServeiDocPermesCif;
	@Column(name = "doc_permes_nie", nullable = false)
	private boolean pinbalServeiDocPermesNie;
	@Column(name = "doc_permes_pas", nullable = false)
	private boolean pinbalServeiDocPermesPas;
	@Column(name = "actiu", nullable = false)
	private boolean actiu;
}
