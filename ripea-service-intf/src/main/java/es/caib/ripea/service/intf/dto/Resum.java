package es.caib.ripea.service.intf.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class Resum implements Serializable {
	
	private String error;
    private String resum;
    private String titol;

    public Resum() {
		super();
	}
}