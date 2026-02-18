package es.caib.ripea.plugin.registre;

import com.google.common.base.Strings;

import lombok.Getter;
import lombok.Setter;

/**
 * Resposta base del plugin de registre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class RespostaBase {

	private String errorCodi;
	private String errorDescripcio;

	public boolean isError() {
		return !Strings.isNullOrEmpty(errorCodi);
	}

}
