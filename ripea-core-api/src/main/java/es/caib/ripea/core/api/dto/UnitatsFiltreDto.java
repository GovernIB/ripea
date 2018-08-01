/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Informació del filtre de continguts.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UnitatsFiltreDto implements Serializable {

	private String denominacio;
	private String codi;
	private String nivellAdministracio;
	private String comunitat;
	private String provincia;
	private String localitat;
	private boolean unitatArrel;

	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;



	public String getCodi() {
		return codi;
	}

	public void setCodi(String codi) {
		this.codi = codi;
	}

	public String getNivellAdministracio() {
		return nivellAdministracio;
	}

	public void setNivellAdministracio(String nivellAdministracio) {
		this.nivellAdministracio = nivellAdministracio;
	}

	public String getComunitat() {
		return comunitat;
	}

	public void setComunitat(String comunitat) {
		this.comunitat = comunitat;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getLocalitat() {
		return localitat;
	}

	public void setLocalitat(String localitat) {
		this.localitat = localitat;
	}

	public boolean isUnitatArrel() {
		return unitatArrel;
	}

	public void setUnitatArrel(boolean unitatArrel) {
		this.unitatArrel = unitatArrel;
	}

	public String getDenominacio() {
		return denominacio;
	}

	public void setDenominacio(String denominacio) {
		this.denominacio = denominacio;
	}

}
