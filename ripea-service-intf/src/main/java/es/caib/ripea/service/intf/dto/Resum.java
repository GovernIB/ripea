package es.caib.ripea.service.intf.dto;

import lombok.Data;

@Data
public class Resum {
	
	private String error;
    private String resum;
    private String titol;

    public Resum() {
		super();
	}
}