package es.caib.ripea.persistence.entity.resourceentity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.ForeignKey;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.ArxiuEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentFirmaTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNtiTipoFirmaEnumDto;
import es.caib.ripea.service.intf.dto.DocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.NtiOrigenEnumDto;
import es.caib.ripea.service.intf.model.DominiResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "domini")
@Getter
@Setter
@NoArgsConstructor
public class DominiResourceEntity extends BaseAuditableEntity<DominiResource> {

	@Column(name = "codi")
	private String codi;
	@Column(name = "nom")
	private String nom;
	@Column(name = "descripcio")
	private String descripcio;
	@Column(name = "consulta")
	private String consulta;
	@Column(name = "cadena")
	private String cadena;
	@Column(name = "contrasenya")
	private String contrasenya;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(
			name = "entitat_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "domini_entitat_fk"))
	protected EntitatResourceEntity entitat;
}
