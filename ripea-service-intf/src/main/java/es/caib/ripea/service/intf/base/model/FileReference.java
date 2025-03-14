package es.caib.ripea.service.intf.base.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Referència a un arxiu.
 * 
 * @author Límit Tecnologies
 */
@Getter @Setter
@RequiredArgsConstructor
public class FileReference {

	protected final String name;
	protected final byte[] content;
	protected final String contentType;
	protected final Long contentLength;

}
