package es.caib.ripea.service.intf.base.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Opció d'un camp del recurs.
 * 
 * @author Límit Tecnologies
 */
@Getter @Setter
@RequiredArgsConstructor
public class FieldOption {

	private final String value;
	private final String description;

}
