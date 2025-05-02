package es.caib.ripea.service.intf.base.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Arxiu per descarregar.
 * 
 * @author Limit Tecnologies
 */
@Getter @Setter
@AllArgsConstructor
public class DownloadableFile implements Serializable {

	private static final long serialVersionUID = 4675161839667172879L;
	private String name;
	private String contentType;
	private byte[] content;

	public Long getContentLength() {
		if (content != null) {
			return Long.valueOf(content.length);
		} else {
			return null;
		}
	}

}
