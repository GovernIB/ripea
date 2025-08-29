package es.caib.ripea.persistence.entity.resourceentity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.AlertaResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "document_enviament_dis")
@Getter
@Setter
@NoArgsConstructor
public class DispositiuEnviamentResourceEntity extends BaseAuditableEntity<AlertaResource> {

	@Column(name = "codi")
	private String codi;
	@Column(name = "codi_aplicacio")
	private String codiAplicacio;
	@Column(name = "codi_usuari")
	private String codiUsuari;
	@Column(name = "descripcio")
	private String descripcio;
	@Column(name = "locale")
	private String local;
	@Column(name = "estat")
	private String estat;
	@Column(name = "token")
	private String token;
	@Column(name = "identificador")
	private String identificador;
	@Column(name = "tipus")
	private String tipus;
	@Column(name = "email_usuari")
	private String emailUsuari;
	@Column(name = "identificador_nac")
	private String identificadorNacional;
}