package es.caib.ripea.plugin.registre;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació sobre l'interessat d'una anotació de registre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class RegistreDadesInteressat {

	private RegistreInteressat interessat;
	private RegistreInteressat representant;

}
