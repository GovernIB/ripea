package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.ContingutTipusEnumDto;
import es.caib.ripea.service.intf.model.ExpedientResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.Set;

/**
 * Entitat de base de dades que representa un contingut.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "contingut")
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ContingutResourceEntity extends BaseAuditableEntity<ExpedientResource> {

	@Column(name = "nom", length = 256, nullable = false)
	protected String nom;
	@Column(name = "tipus", nullable = false)
	protected ContingutTipusEnumDto tipus;

	@Column(name = "esborrat")
	protected int esborrat = 0;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "esborrat_data")
	protected Date esborratData;
	@Column(name = "arxiu_uuid", length = 36)
	protected String arxiuUuid;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "arxiu_data_act")
	protected Date arxiuDataActualitzacio;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "arxiu_intent_data")
	protected Date arxiuIntentData;
	@Column(name = "arxiu_reintents")
	protected int arxiuReintents;
	@Column(name = "ordre")
	protected int ordre;

	@Column(name = "numero_registre")
	protected String numeroRegistre;

	@Column(name = "arxiu_propagat")
	protected boolean arxiuPropagat;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(
			name = "pare_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "pare_contingut_fk"))
	protected ContingutResourceEntity pare;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "entitat_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "entitat_contingut_fk"))
	protected EntitatResourceEntity entitat;

	@OneToMany(
			mappedBy = "pare",
			fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	protected Set<ContingutResourceEntity> fills;


}
