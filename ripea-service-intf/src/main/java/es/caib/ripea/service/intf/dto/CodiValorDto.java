package es.caib.ripea.service.intf.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class CodiValorDto implements Serializable {
	private static final long serialVersionUID = 8122289794445362981L;
	private String codi;
	private String valor;
}