package es.caib.ripea.war.command;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Getter;


@Getter
public class MetaExpedientImportRolsacCommand {

	@NotEmpty
	@Size(max = 30)
	private String classificacioSia;

	public void setClassificacioSia(String classificacioSia) {
		this.classificacioSia = classificacioSia != null ? classificacioSia.trim() : null;
	}

}
