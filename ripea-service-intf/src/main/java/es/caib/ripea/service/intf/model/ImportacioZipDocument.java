package es.caib.ripea.service.intf.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.Data;

@Data
public class ImportacioZipDocument implements Serializable, Comparable<ImportacioZipDocument> {
	private static final long serialVersionUID = 6560559906432257920L;
	private String id; //ID para el front, no es ningun ID de BBDD
	private boolean importar;
	private String ruta;
	@NotNull
	private String nom;
	private String extensio;
	private byte[] contingut;
	private long mida;
	@NotNull
	private ResourceReference<MetaDocumentResource, Long> tipusDocument;
	
	@Override
	public int compareTo(ImportacioZipDocument other) {
	    // Manejar nulls y blancos en ruta - ponerlos al final
	    String thisRuta = (this.ruta == null || this.ruta.trim().isEmpty()) ? null : this.ruta;
	    String otherRuta = (other.ruta == null || other.ruta.trim().isEmpty()) ? null : other.ruta;
	    
	    // Si ambos son null/blank, comparar por nom
	    if (thisRuta == null && otherRuta == null) {
	        return this.nom.compareTo(other.nom);
	    }
	    // Si solo this es null/blank, va al final (devolver positivo)
	    if (thisRuta == null) {
	        return 1;
	    }
	    // Si solo other es null/blank, va al final (devolver negativo)
	    if (otherRuta == null) {
	        return -1;
	    }
	    
	    // Ambos tienen valor, comparar normalmente
	    int rutaComparison = thisRuta.compareTo(otherRuta);
	    if (rutaComparison != 0) {
	        return rutaComparison;
	    }
	    
	    // Si las rutas son iguales, comparar por nom
	    return this.nom.compareTo(other.nom);
	}
}