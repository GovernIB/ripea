package es.caib.ripea.core.api.dto;

public class GrupDto {

    private Long id;
    private String rol;
    private String descripcio;
    
    private Long entitatId;
    
    private boolean relacionat;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getRol() {
        return rol;
    }
    public void setRol(String rol) {
        this.rol = rol;
    }
    public String getDescripcio() {
        return descripcio;
    }
    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }
	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
	public boolean isRelacionat() {
		return relacionat;
	}
	public void setRelacionat(boolean relacionat) {
		this.relacionat = relacionat;
	}
}
