/**
 * 
 */
package es.caib.ripea.plugin.unitat;

import es.caib.ripea.plugin.SistemaExternException;

import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;


/**
 * Plugin per a obtenir l'arbre d'unitats organitzatives.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface UnitatsOrganitzativesPlugin {

    /**
     * Obté l'organigrama complet que conté la unitat organitzativa del codi donat
     * 
     * @param codi
     *            Codi de l'unitat organitzativa que cercam.
     * @return
     * @throws SistemaExternException
     * 		Si es produeix un error al consultar les unitats organitzatives.
     */
	public Map<String, NodeDir3> organigrama(String codi) throws SistemaExternException;
	
	/**
	 * Retorna la llista d'unitats organitzatives filles donada
	 * una unitat pare.
	 * 
	 * @param pareCodi
	 *            Codi de la unitat pare.
	 * @return La llista d'unitats organitzatives.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les unitats organitzatives.
	 */
	public List<UnitatOrganitzativa> findAmbPare(
			String pareCodi) throws SistemaExternException;

	/**
	 * Retorna l'unitat organitzativa donat el seu codi.
	 * 
	 * @param codi
	 *            Codi de l'unitat organitzativa.
	 * @return La unitat organitzativa.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les unitats organitzatives.
	 */
	public UnitatOrganitzativa findAmbCodi(
			String codi) throws SistemaExternException;

	/**
	 * Retorna la llista d'unitats organitzatives filles donat un filtre.
	 * 
	 * @param codi
	 *            Codi de la unitat.
	 * @param denominacio
	 *            Denominació de la unitat de la unitat
	 * @param nivellAdministracio
	 *            Nivell de administració de la unitat.
	 * @param comunitatAutonoma
	 *            Codi de la comunitat de la unitat.
	 * @param ambOficines
	 *            Indica si les unitats retornades tenen oficines.
	 * @param esUnitatArrel
	 *            Indica si les unitats retornades són unitats arrel.
	 * @param provincia
	 *            Codi de la provincia de la unitat.
	 * @param localitat
	 *            Codi de la localitat de la unitat.
	 *            
	 * @return La llista d'unitats organitzatives.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les unitats organitzatives.
	 */
	public List<UnitatOrganitzativa> cercaUnitats(
			String codi, 
			String denominacio,
			Long nivellAdministracio, 
			Long comunitatAutonoma, 
			Boolean ambOficines, 
			Boolean esUnitatArrel,
			Long provincia, 
			String municipi) throws SistemaExternException;



	// Mètodes SOAP per sincronització
	// //////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Retorna la unitat organtizativa donat el pareCodi
	 *
	 * @param pareCodi
	 *            Codi de la unitat pare.
	 * @param fechaActualizacion
	 *            Data de la darrera actualització.
	 * @param fechaSincronizacion
	 *            Data de la primera sincronització.
	 * @return La unitat organitzativa trobada.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les unitats organitzatives.
	 */
	public UnitatOrganitzativa findUnidad(
			String pareCodi,
			Timestamp fechaActualizacion,
			Timestamp fechaSincronizacion) throws MalformedURLException;

	/**
	 * Retorna la llista d'unitats organitzatives filles donada
	 * una unitat pare.
	 * If you put fechaActualizacion==null and fechaSincronizacion==null it returns all unitats that are now vigent (current tree)
	 * If you put fechaActualizacion!=null and fechaSincronizacion!=null it returns all the changes in unitats from the time of last syncronization (@param fechaActualizacion) to now
	 *
	 * @param pareCodi
	 *            Codi de la unitat pare. It doesnt have to be arrel
	 * @param fechaActualizacion
	 *            Data de la darrera actualització.
	 * @param fechaSincronizacion
	 *            Data de la primera sincronització.
	 * @return La llista d'unitats organitzatives.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les unitats organitzatives.
	 */
	public List<UnitatOrganitzativa> findAmbPare(
			String pareCodi,
			Timestamp fechaActualizacion,
			Timestamp fechaSincronizacion) throws SistemaExternException;

}
