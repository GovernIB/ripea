/**
 * 
 */
package es.caib.ripea.war.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.ripea.core.api.dto.ArbreJsonDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.TipusClassificacioEnumDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import es.caib.ripea.war.validation.CodiMetaExpedientNoRepetit;
import es.caib.ripea.war.validation.MetaExpedientCodiSiaNoRepetit;
import es.caib.ripea.war.validation.OrganGestorMetaExpedientNotNull;
import lombok.Getter;

/**
 * Command per al manteniment de meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@CodiMetaExpedientNoRepetit(campId = "id", campCodi = "codi", campEntitatId = "entitatId")
@MetaExpedientCodiSiaNoRepetit
@OrganGestorMetaExpedientNotNull
public class MetaExpedientCommand {

	private Long id;

	@NotEmpty
	@Size(max = 64)
	private String codi;
	@NotEmpty
	@Size(max = 256)
	private String nom;
	@Size(max = 1024)
	private String descripcio;
    private TipusClassificacioEnumDto tipusClassificacio;
	@Size(max = 30)
	private String classificacioSia;
	private String classificacioId;
	@NotEmpty
	@Size(max = 30)
	private String serieDocumental;
	@Size(max = 100)
	private String expressioNumero;

	private Long organGestorId;

	private boolean notificacioActiva;

	private boolean permetMetadocsGenerals;

	private Long pareId;
	private Long entitatId;

	private boolean isRolAdminOrgan;
	
    private boolean gestioAmbGrupsActiva;

    private String estructuraCarpetesJson;
    
	private MetaExpedientRevisioEstatEnumDto revisioEstat;
	@Size(max = 1024)
	private String revisioComentari;
	
	private boolean comu = true;
	
	private boolean crearReglaDistribucio;
	
	
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

	public void setClassificacioSia(String classificacio) {
		this.classificacioSia = classificacio != null ? classificacio.trim() : null;
	}

	public void setSerieDocumental(String serieDocumental) {
		this.serieDocumental = serieDocumental != null ? serieDocumental.trim() : null;
	}

	public void setExpressioNumero(String expressioNumero) {
		this.expressioNumero = expressioNumero != null ? expressioNumero.trim() : null;
	}

	public void setOrganGestorId(Long organGestorId) {
		this.organGestorId = organGestorId;
	}

	public void setNotificacioActiva(boolean notificacioActiva) {
		this.notificacioActiva = notificacioActiva;
	}

	public void setPermetMetadocsGenerals(boolean permetMetadocsGenerals) {
		this.permetMetadocsGenerals = permetMetadocsGenerals;
	}

	public void setPareId(Long pareId) {
		this.pareId = pareId;
	}

	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}

	public void setRolAdminOrgan(boolean isRolAdminOrgan) {
		this.isRolAdminOrgan = isRolAdminOrgan;
	}

	public void setGestioAmbGrupsActiva(boolean gestioAmbGrupsActiva) {
		this.gestioAmbGrupsActiva = gestioAmbGrupsActiva;
	}

	public void setEstructuraCarpetesJson(String estructuraCarpetesJson) {
		this.estructuraCarpetesJson = estructuraCarpetesJson != null ? estructuraCarpetesJson.trim() : null;
	}

	public MetaExpedientCommand(boolean isRolOrgan) {
		this.isRolAdminOrgan = isRolOrgan;
	}

	public MetaExpedientCommand() {
		this.isRolAdminOrgan = true;
	}

	
	public static List<MetaExpedientCommand> toEntitatCommands(List<MetaExpedientDto> dtos) {
		List<MetaExpedientCommand> commands = new ArrayList<MetaExpedientCommand>();
		for (MetaExpedientDto dto : dtos) {
			commands.add(ConversioTipusHelper.convertir(dto, MetaExpedientCommand.class));
		}
		return commands;
	}

	public static MetaExpedientCommand asCommand(MetaExpedientDto dto) {
		MetaExpedientCommand command = ConversioTipusHelper.convertir(dto, MetaExpedientCommand.class);
		command.setOrganGestorId(dto.getOrganGestor() != null ? dto.getOrganGestor().getId() : null);
		return command;
	}

	public MetaExpedientDto asDto() throws JsonMappingException {
		MetaExpedientDto dto = ConversioTipusHelper.convertir(this, MetaExpedientDto.class);
		try {
			if (getEstructuraCarpetesJson() != null) {
				ObjectMapper objectMapper = new ObjectMapper();
				List<ArbreJsonDto> listCarpetes = objectMapper.readValue(getEstructuraCarpetesJson(), new TypeReference<List<ArbreJsonDto>>(){});
				dto.setEstructuraCarpetes(listCarpetes);
			}
		} catch (IOException ex) {
			throw new JsonMappingException("Hi ha hagut un error en la conversió del json de jstree a List<ArbreJsonDto>", ex);
		}
		
		if (this.getOrganGestorId() != null) {
			OrganGestorDto organ = new OrganGestorDto();
			organ.setId(this.getOrganGestorId());
			dto.setOrganGestor(organ);
		}
		
		if (dto.getTipusClassificacio() == TipusClassificacioEnumDto.SIA) {
			dto.setClassificacio(this.getClassificacioSia());
		} else {
			dto.setClassificacio(this.getClassificacioId());
		}
		return dto;
	}

	public void setRevisioEstat(MetaExpedientRevisioEstatEnumDto revisioEstat) {
		this.revisioEstat = revisioEstat;
	}

	public void setRevisioComentari(String revisioComentari) {
		this.revisioComentari = revisioComentari != null ? revisioComentari.trim() : null;
	}

	public void setComu(boolean comu) {
		this.comu = comu;
	}
	public void setCrearReglaDistribucio(boolean crearReglaDistribucio) {
		this.crearReglaDistribucio = crearReglaDistribucio;
	}
	public void setTipusClassificacio(
			TipusClassificacioEnumDto tipusClassificacio) {
		this.tipusClassificacio = tipusClassificacio;
	}
	public void setClassificacioId(String classificacioId) {
		this.classificacioId = classificacioId;
	}

	
}
