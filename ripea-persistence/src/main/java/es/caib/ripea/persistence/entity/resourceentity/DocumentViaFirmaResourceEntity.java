package es.caib.ripea.persistence.entity.resourceentity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.ViaFirmaCallbackEstatEnumDto;
import es.caib.ripea.service.intf.dto.ViaFirmaTipusDestinatariEnum;
import es.caib.ripea.service.intf.model.DocumentViaFirmaResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("DocumentViaFirmaEntity")
public class DocumentViaFirmaResourceEntity extends DocumentEnviamentResourceEntity<DocumentViaFirmaResource> {
	
	@Column(name = "vf_codi_usuari", length = 64)
	String codiUsuari;
	@Column(name = "vf_contrasenya_usuari", length = 64)
	String contrasenyaUsuariViaFirma;
	@Column(name = "vf_titol", length = 256)
	String titol;
	@Column(name = "vf_descripcio", length = 256)
	String descripcio;
	@Column(name = "vf_codi_dispositiu", length = 64)
	String codiDispositiu;
	@Column(name = "vf_lectura_obligatoria")
	boolean lecturaObligatoria;
	@Column(name = "vf_message_code", length = 64, unique = true)
	private String messageCode;
	@Column(name = "vf_callback_estat")
	private ViaFirmaCallbackEstatEnumDto callbackEstat;
	@Column(name = "vf_tipus_destinatari")
	@Enumerated(EnumType.STRING)
	private ViaFirmaTipusDestinatariEnum tipusDestinatari;
	@Column(name = "vf_signant_nif")
	private String signantNif;
	@Column(name = "vf_signant_nom")
	private String signantNom;
	@Column(name = "vf_signant_email")
	private String signantEmail;
	@Column(name = "vf_observacions")
	private String observacions;
	@Column(name = "vf_validate_code_enabled")
	private Boolean validateCodeEnabled;
	@Column(name = "vf_validate_code")
	private String validateCode;
	
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vf_viafirma_dispositiu")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "document_enviament_dis_fk")
    protected DispositiuEnviamentResourceEntity dispositiuEnviament;
}
