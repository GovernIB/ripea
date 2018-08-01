/**
 * 
 */
package es.caib.ripea.core.api.dto;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Detalls de log d'una accio realitzada damunt un node.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ContingutLogDetallsDto extends ContingutLogDto {

	private ContingutMovimentDto contingutMoviment;
	private ContingutLogDto pare;
	private String objecteNom;



	public ContingutMovimentDto getContenidorMoviment() {
		return contingutMoviment;
	}
	public void setContenidorMoviment(ContingutMovimentDto contingutMoviment) {
		this.contingutMoviment = contingutMoviment;
	}
	public ContingutMovimentDto getContingutMoviment() {
		return contingutMoviment;
	}
	public void setContingutMoviment(ContingutMovimentDto contingutMoviment) {
		this.contingutMoviment = contingutMoviment;
	}
	public ContingutLogDto getPare() {
		return pare;
	}
	public void setPare(ContingutLogDto pare) {
		this.pare = pare;
	}
	public String getObjecteNom() {
		return objecteNom;
	}
	public void setObjecteNom(String objecteNom) {
		this.objecteNom = objecteNom;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
