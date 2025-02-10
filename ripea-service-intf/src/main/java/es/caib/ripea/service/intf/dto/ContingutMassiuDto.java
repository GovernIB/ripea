package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;



@Getter
@Setter
public class ContingutMassiuDto implements Serializable {


	private Long id;
	private Long expedientId;
	private String nom;
	private String tipusDocumentNom;
	private String expedientNumeroNom;
	private String createdByCodiAndNom;
	private Date createdDate;


	
	
	private static final long serialVersionUID = 1L;

}
