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
import es.caib.ripea.service.intf.model.URLInstruccioResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "url_instruccio")
@Getter
@Setter
@NoArgsConstructor
public class URLInstruccioResourceEntity extends BaseAuditableEntity<URLInstruccioResource> {
	
	@Column(name = "codi")
	private String codi;
	@Column(name = "nom")
	private String nom;
	@Column(name = "descripcio")
	private String descripcio;
	@Column(name = "url")
	private String url;
	
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(
			name = "entitat_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "url_instruccion_ent_fk"))
	private EntitatResourceEntity entitat;
}
