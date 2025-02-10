package es.caib.ripea.back.command;

import lombok.Getter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;


@Getter
public class MetaExpedientImportRolsacCommand {

	@NotEmpty
	@Size(max = 30)
	private String classificacioSia;

	public void setClassificacioSia(String classificacioSia) {
		this.classificacioSia = classificacioSia != null ? classificacioSia.trim() : null;
	}

}
