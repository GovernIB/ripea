package es.caib.ripea.core.api.dto;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientEstatDto  {


	private String codi;
	private String nom;
	private int ordre;
	private String color;
	private Long metaExpedientId;
	private Long id;
	private boolean inicial;
	private String responsableCodi;


	public String getResponsableCodi() {
		return responsableCodi;
	}

	public void setResponsableCodi(String responsableCodi) {
		this.responsableCodi = responsableCodi;
	}

	public boolean isInicial() {
		return inicial;
	}

	public void setInicial(boolean inicial) {
		this.inicial = inicial;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMetaExpedientId() {
		return metaExpedientId;
	}

	public void setMetaExpedientId(Long metaExpedientId) {
		this.metaExpedientId = metaExpedientId;
	}

	public String getCodi() {
		return codi;
	}

	public void setCodi(String codi) {
		this.codi = codi;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public int getOrdre() {
		return ordre;
	}

	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	public ExpedientEstatDto(String nom, Long id) {
		super();
		this.nom = nom;
		this.id = id;
	}	
	
	public ExpedientEstatDto() {
		super();
	}

}
