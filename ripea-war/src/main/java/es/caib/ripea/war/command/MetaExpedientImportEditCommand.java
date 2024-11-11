package es.caib.ripea.war.command;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.TipusClassificacioEnumDto;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.war.validation.CodiMetaExpedientNoRepetit;
import lombok.Getter;

@Getter
@CodiMetaExpedientNoRepetit(campId = "id", campCodi = "codi", campEntitatId = "entitatId")
public class MetaExpedientImportEditCommand {

	private Long id;
	private Long entitatId;
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
	private Long organGestorId;
	private boolean comu;
	private boolean isRolAdminOrgan;
    private List<MetaDocumentCommand> metaDocuments = new ArrayList<>();
    private List<ExpedientEstatCommand> estats = new ArrayList<>();
    private List<MetaExpedientTascaCommand> tasques = new ArrayList<>();
    private boolean interessatObligatori;

    
	public void setId(Long id) {
		this.id = id;
	}
	public void setOrganGestorId(Long organGestorId) {
		this.organGestorId = organGestorId;
	}
	public void setComu(boolean comu) {
		this.comu = comu;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
	public void setCodi(String codi) {
		this.codi = codi != null ? codi.trim() : null;
	}
	public void setMetaDocuments(List<MetaDocumentCommand> metaDocuments) {
		this.metaDocuments = metaDocuments;
	}
	public void setEstats(List<ExpedientEstatCommand> estats) {
		this.estats = estats;
	}
	public void setTasques(List<MetaExpedientTascaCommand> tasques) {
		this.tasques = tasques;
	}
	public void setNom(String nom) {
		this.nom = StringUtils.trim(nom);
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = StringUtils.trim(descripcio);
	}
	public void setSerieDocumental(String serieDocumental) {
		this.serieDocumental = StringUtils.trim(serieDocumental);
	}
	public void setClassificacioSia(String classificacioSia) {
		this.classificacioSia = Utils.trim(classificacioSia);
	}
	public void setClassificacioId(String classificacioId) {
		this.classificacioId = classificacioId;
	}
	public void setTipusClassificacio(TipusClassificacioEnumDto tipusClassificacio) {
		this.tipusClassificacio = tipusClassificacio;
	}
	public void setInteressatObligatori(boolean interessatObligatori) {
		this.interessatObligatori = interessatObligatori;
	}
}