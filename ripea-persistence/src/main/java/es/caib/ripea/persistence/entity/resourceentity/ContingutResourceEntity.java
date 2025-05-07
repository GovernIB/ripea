package es.caib.ripea.persistence.entity.resourceentity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.ContingutTipusEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public abstract class ContingutResourceEntity<R> extends BaseAuditableEntity<R> {

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

    @Version
    private long version = 0;

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

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "expedient_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "expedient_contingut_fk")
    protected ExpedientResourceEntity expedient;

	@OneToMany(
			mappedBy = "pare",
			fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	protected Set<ContingutResourceEntity> fills;

    @OneToMany(
            mappedBy = "contingut",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.REMOVE}
    )
    protected List<AlertaResourceEntity> alertes = new ArrayList<>();
}
