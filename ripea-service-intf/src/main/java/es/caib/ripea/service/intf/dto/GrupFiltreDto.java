package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;


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
