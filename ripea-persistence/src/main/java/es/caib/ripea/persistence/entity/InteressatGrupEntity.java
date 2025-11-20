package es.caib.ripea.persistence.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.service.intf.config.BaseConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Classe del model de dades que representa un grup d'interessats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Entity
@Table(name = BaseConfig.DB_PREFIX + "interessat_grup")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@EntityListeners(AuditingEntityListener.class)
public class InteressatGrupEntity extends RipeaAuditable<Long> {

	@Column(name = "nom", length = 255)
	private String nom;
	
	@Column(name = "descripcio", length = 1024)
	private String descripcio;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "expedient_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "interessat_grupexped_fk"))
	protected ExpedientEntity expedient;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
	    name = BaseConfig.DB_PREFIX + "interessat_grup_rel",
	    joinColumns = @JoinColumn(name = "grup_id", referencedColumnName = "id"),
	    inverseJoinColumns = @JoinColumn(name = "interessat_id", referencedColumnName = "id"),
	    foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "inter_gruprel_grup_fk"),
	    inverseForeignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "inter_gruprel_inter_fk")
	)
	protected List<InteressatEntity> interessats;
	
	public void update(String nom, String descripcio, List<InteressatEntity> interessats) {
		this.nom = nom;
		this.descripcio = descripcio;
		this.interessats = interessats;
	}
	
}
