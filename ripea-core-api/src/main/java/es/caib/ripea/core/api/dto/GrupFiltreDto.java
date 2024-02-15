package es.caib.ripea.core.api.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;


@Getter 
@Setter
public class GrupFiltreDto implements Serializable {

	private String codi;
	private String descripcio;
	private Long organGestorId;
	private Long organGestorAscendentId;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	private static final long serialVersionUID = -139254994389509932L;

}
