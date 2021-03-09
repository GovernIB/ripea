/**
 * 
 */
package es.caib.ripea.war.command;

import java.util.Date;

import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.MetaExpedientTascaDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import es.caib.ripea.war.validation.CodiMetaExpedientTascaNoRepetit;
import lombok.Getter;

/**
 * Command per a les tasques del meta-expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@CodiMetaExpedientTascaNoRepetit(campId = "id", campCodi = "codi", campEntitatId = "entitatId", campMetaExpedientId = "metaExpedientId")
public class MetaExpedientTascaCommand {

    private Long id;
    @NotEmpty
    private String codi;
    @NotEmpty
    private String nom;
    @NotEmpty
    private String descripcio;
    private String responsable;
    private boolean activa;
    private Date dataLimit;
    private Long estatIdCrearTasca;
    private Long estatIdFinalitzarTasca;

    private Long entitatId;
    private Long metaExpedientId;

    public static MetaExpedientTascaCommand asCommand(MetaExpedientTascaDto dto) {
        MetaExpedientTascaCommand command = ConversioTipusHelper.convertir(dto,
                MetaExpedientTascaCommand.class);
        return command;
    }

    public static MetaExpedientTascaDto asDto(MetaExpedientTascaCommand command) {
        return ConversioTipusHelper.convertir(command, MetaExpedientTascaDto.class);
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

	public void setResponsable(String responsable) {
		this.responsable = responsable != null ? responsable.trim() : null;
	}

	public void setActiva(boolean activa) {
		this.activa = activa;
	}

	public void setDataLimit(Date dataLimit) {
		this.dataLimit = dataLimit;
	}

	public void setEstatIdCrearTasca(Long estatIdCrearTasca) {
		this.estatIdCrearTasca = estatIdCrearTasca;
	}

	public void setEstatIdFinalitzarTasca(Long estatIdFinalitzarTasca) {
		this.estatIdFinalitzarTasca = estatIdFinalitzarTasca;
	}

	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}

	public void setMetaExpedientId(Long metaExpedientId) {
		this.metaExpedientId = metaExpedientId;
	}

	public interface Create {
    }

    public interface Update {
    }

}
