package es.caib.ripea.war.command;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class TascaDataLimitCommand {

	private Date dataLimit;
	
	public interface Create {}
	public interface Update {}

}
