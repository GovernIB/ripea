/**
 * 
 */
package es.caib.ripea.war.command;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.ripea.core.api.dto.ExpedientPeticioAccioEnumDto;
import es.caib.ripea.core.api.utils.Utils;
import lombok.Getter;

/**
 * Command per al expedient peticio rebutjar
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
public class ExpedientPeticioAcceptarCommand {

	private Long id;
	private Long metaExpedientId;
	private Long expedientId;
	
	private String newExpedientTitol;
	private int any;
	private Long sequencia;
	private boolean associarInteressats;
	private ExpedientPeticioAccioEnumDto accio;
	private boolean agafarExpedient;
	
	private Long organGestorId;
    private List<RegistreAnnexCommand> annexos = new ArrayList<>();
    
	private Long grupId;
	private boolean gestioAmbGrupsActiva;
	
    
	public void setAccio(ExpedientPeticioAccioEnumDto accio) {
		this.accio = accio;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setMetaExpedientId(Long metaExpedientId) {
		this.metaExpedientId = metaExpedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	public void setNewExpedientTitol(String newExpedientTitol) {
		this.newExpedientTitol = Utils.trim(newExpedientTitol);
	}
	public void setAny(int any) {
		this.any = any;
	}
	public void setAssociarInteressats(boolean associarInteressats) {
		this.associarInteressats = associarInteressats;
	}
	public void setOrganGestorId(Long organGestorId) {
		this.organGestorId = organGestorId;
	}
	public void setAnnexos(List<RegistreAnnexCommand> annexos) {
		this.annexos = annexos;
	}
	public void setAgafarExpedient(boolean agafarExpedient) {
		this.agafarExpedient = agafarExpedient;
	}
	public void setSequencia(Long sequencia) {
		this.sequencia = sequencia;
	}
	public void setGrupId(Long grupId) {
		this.grupId = grupId;
	}
	public void setGestioAmbGrupsActiva(boolean gestioAmbGrupsActiva) {
		this.gestioAmbGrupsActiva = gestioAmbGrupsActiva;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(
				this);
	}
}
