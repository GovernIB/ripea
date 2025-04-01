package es.caib.ripea.service.intf.base.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'un camp per a l'exportació.
 *
 * @author Josep Gayà
 */
@Getter
@Setter
@AllArgsConstructor
public class ExportField {

	private String name;
	private String label;

}
