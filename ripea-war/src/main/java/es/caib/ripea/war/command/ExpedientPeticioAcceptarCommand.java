/**
 * 
 */
package es.caib.ripea.war.command;

import es.caib.ripea.core.api.dto.ExpedientPeticioAccioEnumDto;
import es.caib.ripea.core.api.dto.PrioritatEnumDto;
import es.caib.ripea.core.api.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Command per al expedient peticio rebutjar
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ExpedientPeticioAcceptarCommand {

	private Long id;
	private Long metaExpedientId;
	private Long expedientId;
	
	private String newExpedientTitol;
	private PrioritatEnumDto prioritat;
	private String prioritatMotiu;
	private int any;
	private Long sequencia;
	private boolean associarInteressats;
	private ExpedientPeticioAccioEnumDto accio;
	private boolean agafarExpedient;
	
	private Long organGestorId;
    private List<RegistreAnnexCommand> annexos = new ArrayList<>();
	private List<RegistreInteressatsCommand> interessats = new ArrayList<>();
    
	private Long grupId;
	private boolean gestioAmbGrupsActiva;
	
	public void setNewExpedientTitol(String newExpedientTitol) {
		this.newExpedientTitol = Utils.trim(newExpedientTitol);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(
				this);
	}
}
