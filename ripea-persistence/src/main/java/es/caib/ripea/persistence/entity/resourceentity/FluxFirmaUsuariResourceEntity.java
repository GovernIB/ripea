package es.caib.ripea.persistence.entity.resourceentity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.FluxFirmaUsuariResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entitat de recurs dels fluxos de firma d'un usuari.
 *
 * Mapeja la mateixa taula que FluxFirmaUsuariEntity: si es modifiquen els camps s'han
 * d'actualitzar les dues entitats.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "flux_firma_usuari")
@Getter
@Setter
@NoArgsConstructor
public class FluxFirmaUsuariResourceEntity extends BaseAuditableEntity<FluxFirmaUsuariResource> {

	@Column(name = "nom")
	private String nom;
	@Column(name = "descripcio")
	private String descripcio;
	@Column(name = "portafirmes_fluxid")
	private String portafirmesFluxId;
	@Column(name = "destinataris")
	private String destinataris;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "entitat_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "flux_firma_usuari_ent_fk"))
	private EntitatResourceEntity entitat;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "usuari_codi",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "flux_firma_usuari_usu_fk"))
	private UsuariResourceEntity usuari;

}
