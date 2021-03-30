/**
 * 
 */
package es.caib.ripea.core.api.exception;

/**
 * Excepció que es llança quan s'intenta importar un document importat prèviament
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentAlreadyImportedException extends ValidationException {
	
	public DocumentAlreadyImportedException () {
		super("El document ha estat importat prèviament");
	}
	
	private static final long serialVersionUID = 7773092676312182180L;
	
}
