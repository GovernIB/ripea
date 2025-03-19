/**
 * 
 */
package es.caib.ripea.plugin.arxiu;

/**
 * Capçalera amb informació comuna a totes les peticions a l'arxiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuCapsalera {

	private String interessatNom;
	private String interessatNif;
	private String funcionariNom;
	private String funcionariNif;
	private String funcionariOrgan;
	private String procedimentId;
	private String procedimentNom;
	private String expedientId;

	public String getInteressatNom() {
		return interessatNom;
	}
	public void setInteressatNom(String interessatNom) {
		this.interessatNom = interessatNom;
	}
	public String getInteressatNif() {
		return interessatNif;
	}
	public void setInteressatNif(String interessatNif) {
		this.interessatNif = interessatNif;
	}
	public String getFuncionariNom() {
		return funcionariNom;
	}
	public void setFuncionariNom(String funcionariNom) {
		this.funcionariNom = funcionariNom;
	}
	public String getFuncionariNif() {
		return funcionariNif;
	}
	public void setFuncionariNif(String funcionariNif) {
		this.funcionariNif = funcionariNif;
	}
	public String getFuncionariOrgan() {
		return funcionariOrgan;
	}
	public void setFuncionariOrgan(String funcionariOrgan) {
		this.funcionariOrgan = funcionariOrgan;
	}
	public String getProcedimentId() {
		return procedimentId;
	}
	public void setProcedimentId(String procedimentId) {
		this.procedimentId = procedimentId;
	}
	public String getProcedimentNom() {
		return procedimentNom;
	}
	public void setProcedimentNom(String procedimentNom) {
		this.procedimentNom = procedimentNom;
	}
	public String getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(String expedientId) {
		this.expedientId = expedientId;
	}

}
