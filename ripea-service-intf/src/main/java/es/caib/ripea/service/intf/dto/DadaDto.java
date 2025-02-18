/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Informació d'una dada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadaDto extends AuditoriaDto {

	private Long id;
	private MetaDadaDto metaDada;
	protected Object valor;
	protected int ordre;



	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public MetaDadaDto getMetaDada() {
		return metaDada;
	}
	public void setMetaDada(MetaDadaDto metaDada) {
		this.metaDada = metaDada;
	}
	public void setValor(Object valor) {
		this.valor = valor;
	}
	public Object getValor() {
		return valor;
	}
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}

	public String getValorMostrar() {
		if (valor == null)
			return null;
		if (metaDada != null) {
			if (metaDada.getTipus().equals(MetaDadaTipusEnumDto.TEXT)) {
				return valor.toString();
			} else if (metaDada.getTipus().equals(MetaDadaTipusEnumDto.DATA)) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				return sdf.format((Date)valor);
			} else if (metaDada.getTipus().equals(MetaDadaTipusEnumDto.SENCER)) {
				DecimalFormat decimalFormat = new DecimalFormat("###0");
				return decimalFormat.format((Long)valor);
			} else if (metaDada.getTipus().equals(MetaDadaTipusEnumDto.FLOTANT)) {
				DecimalFormat decimalFormat = new DecimalFormat("###0.##");
				return decimalFormat.format((Double)valor);
			} else if (metaDada.getTipus().equals(MetaDadaTipusEnumDto.IMPORT)) {
				DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
				return decimalFormat.format((BigDecimal)valor);
			} else if (metaDada.getTipus().equals(MetaDadaTipusEnumDto.BOOLEA)) {
				return ((Boolean)valor).toString();
			}
		}
		return valor.toString();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
