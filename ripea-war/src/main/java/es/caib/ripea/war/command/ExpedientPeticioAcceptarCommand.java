/**
 * 
 */
package es.caib.ripea.war.command;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.ripea.core.api.dto.ExpedientPeticioAccioEnumDto;
import es.caib.ripea.war.command.ContenidorCommand.Create;
import es.caib.ripea.war.command.ContenidorCommand.Update;
import es.caib.ripea.war.validation.ExpedientODocumentNom;

/**
 * Command per al expedient peticio rebutjar
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExpedientODocumentNom(groups = {Create.class, Update.class})
public class ExpedientPeticioAcceptarCommand {

	private Long id;
	private Long metaExpedientId;
	private Long expedientId;
	
	private String newExpedientTitol;
	private int any;
	private boolean associarInteressats;
	private ExpedientPeticioAccioEnumDto accio;
	
	private Long organGestorId;
    private List<RegistreAnnexCommand> annexos = new ArrayList<>();
	
	

	public ExpedientPeticioAccioEnumDto getAccio() {
		return accio;
	}
	public void setAccio(ExpedientPeticioAccioEnumDto accio) {
		this.accio = accio;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getMetaExpedientId() {
		return metaExpedientId;
	}
	public void setMetaExpedientId(Long metaExpedientId) {
		this.metaExpedientId = metaExpedientId;
	}
	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	public String getNewExpedientTitol() {
		return newExpedientTitol;
	}
	public void setNewExpedientTitol(String newExpedientTitol) {
		this.newExpedientTitol = newExpedientTitol != null ? newExpedientTitol.trim() : null;
	}
	public int getAny() {
		return any;
	}
	public void setAny(int any) {
		this.any = any;
	}
	public boolean isAssociarInteressats() {
		return associarInteressats;
	}
	public void setAssociarInteressats(boolean associarInteressats) {
		this.associarInteressats = associarInteressats;
	}
	public Long getOrganGestorId() {
		return organGestorId;
	}
	public void setOrganGestorId(Long organGestorId) {
		this.organGestorId = organGestorId;
	}
	public List<RegistreAnnexCommand> getAnnexos() {
		return annexos;
	}
	public void setAnnexos(List<RegistreAnnexCommand> annexos) {
		this.annexos = annexos;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(
				this);
	}

}
