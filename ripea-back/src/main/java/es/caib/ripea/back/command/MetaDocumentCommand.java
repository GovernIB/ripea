/**
 * 
 */
package es.caib.ripea.back.command;

import es.caib.ripea.back.helper.ConversioTipusHelper;
import es.caib.ripea.back.validation.CodiMetaDocumentNoRepetit;
import es.caib.ripea.back.validation.ResponsableNotEmpty;
import es.caib.ripea.service.intf.dto.*;
import lombok.Getter;
import javax.validation.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * Command per al manteniment de meta-documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@ResponsableNotEmpty
@CodiMetaDocumentNoRepetit(
		campId = "id",
		campCodi = "codi",
		campEntitatId = "entitatId",
		campMetaExpedientId = "metaExpedientId")
public class MetaDocumentCommand {

	private Long id;
	@NotEmpty @Size(max=64)
	private String codi;
	@NotEmpty @Size(max=256)
	private String nom;
	@Size(max=1024)
	private String descripcio;
	private boolean globalExpedient;
	@NotNull
	private MultiplicitatEnumDto multiplicitat;
	private boolean firmaPortafirmesActiva;
	@Size(max=64)
	private String portafirmesDocumentTipus;
	@Size(max=64)
	private String portafirmesFluxId;
	private String[] portafirmesResponsables;
	private MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSequenciaTipus;
	@Size(max=64)
	private String portafirmesCustodiaTipus;
	private boolean firmaPassarelaActiva;
	@Size(max=64)
	private String firmaPassarelaCustodiaTipus;
	protected MultipartFile plantilla;
	private Long entitatId;
	private Long metaExpedientId;
	@NotNull
	private NtiOrigenEnumDto ntiOrigen;
	@NotEmpty
	private String ntiTipoDocumental;
	private DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion;	
	private boolean firmaBiometricaActiva;
	private boolean biometricaLectura;
	private MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus;
	private String plantillaNom;
	private boolean pinbalActiu;
	private PinbalServeiDto pinbalServei;
	private String pinbalFinalitat;
	private boolean pinbalUtilitzarCifOrgan;
	
	private boolean comu;

	public static List<MetaDocumentCommand> toEntitatCommands(
			List<MetaDocumentDto> dtos) {
		List<MetaDocumentCommand> commands = new ArrayList<MetaDocumentCommand>();
		for (MetaDocumentDto dto: dtos) {
			commands.add(
					ConversioTipusHelper.convertir(
							dto,
							MetaDocumentCommand.class));
		}
		return commands;
	}

	public static MetaDocumentCommand asCommand(MetaDocumentDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				MetaDocumentCommand.class);
	}
	public static MetaDocumentDto asDto(MetaDocumentCommand command) {
		if (command.getPortafirmesFluxTipus() == MetaDocumentFirmaFluxTipusEnumDto.SIMPLE)
			command.setPortafirmesFluxId(null);
		
		MetaDocumentDto resultat = ConversioTipusHelper.convertir(command, MetaDocumentDto.class);
//		if (command.getPinbalServei()!=null && command.getPinbalServei()>0) {
//			PinbalServeiDto psDto = new PinbalServeiDto();
//			psDto.setId(command.getPinbalServei());
//			resultat.setPinbalServei(psDto);
//		}
		return resultat;
	}

	public boolean isComu() {
		return comu;
	}

	public void setComu(boolean isComu) {
		this.comu = isComu;
	}

	
	public void setId(Long id) {
		this.id = id;
	}

	public void setCodi(String codi) {
		this.codi = codi != null ? codi.trim() : null;
	}

	public void setNom(String nom) {
		this.nom = nom != null ? nom.trim() : null;
	}

	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio != null ? descripcio.trim() : null;
	}

	public void setGlobalExpedient(boolean globalExpedient) {
		this.globalExpedient = globalExpedient;
	}

	public void setMultiplicitat(MultiplicitatEnumDto multiplicitat) {
		this.multiplicitat = multiplicitat;
	}

	public void setFirmaPortafirmesActiva(boolean firmaPortafirmesActiva) {
		this.firmaPortafirmesActiva = firmaPortafirmesActiva;
	}

	public void setPortafirmesDocumentTipus(String portafirmesDocumentTipus) {
		this.portafirmesDocumentTipus = portafirmesDocumentTipus != null ? portafirmesDocumentTipus.trim() : null;
	}

	public void setPortafirmesFluxId(String portafirmesFluxId) {
		this.portafirmesFluxId = portafirmesFluxId != null ? portafirmesFluxId.trim() : null;
	}

	public void setPortafirmesResponsables(String[] portafirmesResponsables) {
		this.portafirmesResponsables = portafirmesResponsables;
	}

	public void setPortafirmesSequenciaTipus(MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSequenciaTipus) {
		this.portafirmesSequenciaTipus = portafirmesSequenciaTipus;
	}

	public void setPortafirmesCustodiaTipus(String portafirmesCustodiaTipus) {
		this.portafirmesCustodiaTipus = portafirmesCustodiaTipus != null ? portafirmesCustodiaTipus.trim() : null;
	}

	public void setFirmaPassarelaActiva(boolean firmaPassarelaActiva) {
		this.firmaPassarelaActiva = firmaPassarelaActiva;
	}

	public void setFirmaPassarelaCustodiaTipus(String firmaPassarelaCustodiaTipus) {
		this.firmaPassarelaCustodiaTipus = firmaPassarelaCustodiaTipus != null ? firmaPassarelaCustodiaTipus.trim() : null;
	}

	public void setPlantilla(MultipartFile plantilla) {
		this.plantilla = plantilla;
	}

	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}

	public void setMetaExpedientId(Long metaExpedientId) {
		this.metaExpedientId = metaExpedientId;
	}

	public void setNtiOrigen(NtiOrigenEnumDto ntiOrigen) {
		this.ntiOrigen = ntiOrigen;
	}

	public void setNtiTipoDocumental(String ntiTipoDocumental) {
		this.ntiTipoDocumental = ntiTipoDocumental != null ? ntiTipoDocumental.trim() : null;
	}

	public void setNtiEstadoElaboracion(DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion) {
		this.ntiEstadoElaboracion = ntiEstadoElaboracion;
	}

	public void setFirmaBiometricaActiva(boolean firmaBiometricaActiva) {
		this.firmaBiometricaActiva = firmaBiometricaActiva;
	}

	public void setBiometricaLectura(boolean biometricaLectura) {
		this.biometricaLectura = biometricaLectura;
	}

	public void setPortafirmesFluxTipus(MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus) {
		this.portafirmesFluxTipus = portafirmesFluxTipus;
	}

	public void setPlantillaNom(String plantillaNom) {
		this.plantillaNom = plantillaNom != null ? plantillaNom.trim() : null;
	}

	public void setPinbalActiu(boolean pinbalActiu) {
		this.pinbalActiu = pinbalActiu;
	}

	public void setPinbalServei(PinbalServeiDto pinbalServei) {
		this.pinbalServei = pinbalServei;
	}

	public void setPinbalFinalitat(String pinbalFinalitat) {
		this.pinbalFinalitat = pinbalFinalitat;
	}

	public void setPinbalUtilitzarCifOrgan(boolean pinbalUtilitzarCifOrgan) {
		this.pinbalUtilitzarCifOrgan = pinbalUtilitzarCifOrgan;
	}
}