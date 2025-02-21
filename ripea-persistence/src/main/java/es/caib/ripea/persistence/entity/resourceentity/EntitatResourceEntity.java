package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.EntitatResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Entitat de base de dades que representa un node.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "entitat")
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class EntitatResourceEntity extends BaseAuditableEntity<EntitatResource> {

	@Column(name = "codi", length = 64, nullable = false, unique = true)
	private String codi;
	@Column(name = "nom", length = 256, nullable = false)
	private String nom;
	@Column(name = "descripcio", length = 1024)
	private String descripcio;
	@Column(name = "cif", length = 9, nullable = false)
	private String cif;
	@Column(name = "unitat_arrel", length = 9, nullable = false)
	private String unitatArrel;
	@Column(name = "activa")
	private boolean activa = true;
	@Column(name = "logo_img")
	private byte[] logoImgBytes;
	@Column(name = "capsalera_color_fons", length = 7)
	private String capsaleraColorFons;
	@Column(name = "capsalera_color_lletra", length = 7)
	private String capsaleraColorLletra;
	@Column(name = "data_sincronitzacio")
	@Temporal(TemporalType.TIMESTAMP)
	Date dataSincronitzacio;
	@Column(name = "data_actualitzacio")
	@Temporal(TemporalType.TIMESTAMP)
	Date dataActualitzacio;
	@Column(name = "perm_env_postal")
	private boolean permetreEnviamentPostal = true;

//	@OneToMany(mappedBy = "entitat", cascade = {CascadeType.ALL})
//	private Set<MetaNodeResourceEntity> metaNodes = new HashSet<>();

}
