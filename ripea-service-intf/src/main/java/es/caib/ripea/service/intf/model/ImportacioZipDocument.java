package es.caib.ripea.service.intf.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.Data;

@Data
public class ImportacioZipDocument implements Serializable {
	private static final long serialVersionUID = 6560559906432257920L;
	private boolean importar;
	private String ruta;
	@NotNull
	private String nom;
	private String extensio;
	private byte[] contingut;
	private long mida;
	@NotNull
	private ResourceReference<MetaDocumentResource, Long> tipusDocument; 
}