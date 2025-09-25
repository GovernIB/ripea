package es.caib.ripea.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import es.caib.ripea.service.intf.config.BaseConfig;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "explot_dim")
@Getter
@Setter
public class ExplotacioDimensioEntity extends RipeaPersistable<Long> {
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "entitat_id")
	protected EntitatEntity entitat;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "procediment_id")
	protected MetaExpedientEntity procediment;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "organ_id")
	protected OrganGestorEntity organGestor;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "usuari_codi")
	protected UsuariEntity usuari;
}