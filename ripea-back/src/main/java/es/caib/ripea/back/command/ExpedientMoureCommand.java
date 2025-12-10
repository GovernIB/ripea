package es.caib.ripea.back.command;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ExpedientMoureCommand {

	@NotNull
	protected Long expedientOrigenId;
	@NotNull
	protected Long expedientDestiId;
	
}
