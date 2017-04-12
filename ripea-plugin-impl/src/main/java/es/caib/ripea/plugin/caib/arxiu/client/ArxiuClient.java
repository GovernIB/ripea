/**
 * 
 */
package es.caib.ripea.plugin.caib.arxiu.client;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import es.caib.arxiudigital.apirest.constantes.EstadosElaboracion;
import es.caib.arxiudigital.apirest.constantes.EstadosExpediente;
import es.caib.arxiudigital.apirest.constantes.ExtensionesFichero;
import es.caib.arxiudigital.apirest.constantes.FormatosFichero;
import es.caib.arxiudigital.apirest.constantes.OrigenesContenido;
import es.caib.arxiudigital.apirest.constantes.PerfilesFirma;
import es.caib.arxiudigital.apirest.constantes.TiposDocumentosENI;
import es.caib.arxiudigital.apirest.constantes.TiposFirma;

/**
 * Interfície del client per a accedir a la funcionalitat de
 * l'arxiu digital.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ArxiuClient {

	public ArxiuFile fileCreate(
			String titol,
			OrigenesContenido origen,
			Date dataObertura,
			String classificacio,
			EstadosExpediente estat,
			List<String> organs,
			List<String> interessats,
			String serieDocumental,
			Map<String, Object> metadadesAddicionals,
			ArxiuHeader capsalera) throws ArxiuException;
	public void fileUpdate(
			String nodeId,
			String titol,
			OrigenesContenido origen,
			Date dataObertura,
			String classificacio,
			EstadosExpediente estat,
			List<String> organs,
			List<String> interessats,
			String serieDocumental,
			Map<String, Object> metadadesAddicionals,
			ArxiuHeader capsalera);
	public void fileDelete(
			String nodeId,
			ArxiuHeader capsalera);
	public ArxiuFile fileGet(
			String nodeId,
			ArxiuHeader capsalera);
	public void fileClose(
			String nodeId,
			ArxiuHeader capsalera);
	public void fileReopen(
			String nodeId,
			ArxiuHeader capsalera);
	public String fileExport(
			String nodeId,
			ArxiuHeader capsalera);
	public void fileEasySearch();
	public void fileLink();
	public void fileLock();
	public void fileUnlock();
	public void fileVersionList();
	public void filePermissionsGrant();
	public void filePermissionsCancel();
	public void fileIndexGenerate();
	public void fileChildCreate();
	public void fileChildMove();

	public ArxiuDocument documentDraftCreate(
			String titol,
			OrigenesContenido origen,
			Date dataCaptura,
			EstadosElaboracion estatElaboracio,
			TiposDocumentosENI documentTipus,
			FormatosFichero formatNom,
			List<String> organs,
			String serieDocumental,
			InputStream contingut,
			String pareNodeId,
			ExtensionesFichero formatExtensio,
			String tipusMime,
			Map<String, Object> metadadesAddicionals,
			ArxiuHeader capsalera);
	public ArxiuDocument documentCreate();
	public void documentUpdate(
			String nodeId,
			String titol,
			OrigenesContenido origen,
			Date dataCaptura,
			EstadosElaboracion estatElaboracio,
			TiposDocumentosENI documentTipus,
			FormatosFichero formatNom,
			List<String> organs,
			String serieDocumental,
			InputStream contingut,
			ExtensionesFichero formatExtensio,
			String tipusMime,
			Map<String, Object> metadadesAddicionals,
			ArxiuHeader capsalera);
	public void documentDelete(
			String nodeId,
			ArxiuHeader capsalera);
	public ArxiuDocument documentGet(
			String nodeId,
			String csv,
			boolean ambContingut,
			ArxiuHeader capsalera);
	public List<ArxiuDocumentVersio> documentVersionList(
			String nodeId,
			ArxiuHeader capsalera);
	public String documentCsvGenerate(
			String nodeId,
			ArxiuHeader capsalera);
	public void documentFinalSet(
			String nodeId,
			String titol,
			OrigenesContenido origen,
			Date dataCaptura,
			EstadosElaboracion estatElaboracio,
			TiposDocumentosENI documentTipus,
			FormatosFichero formatNom,
			List<String> organs,
			String serieDocumental,
			InputStream contingut,
			TiposFirma firmaTipus,
			PerfilesFirma firmaPerfil,
			String csv,
			ExtensionesFichero formatExtensio,
			String tipusMime,
			Map<String, Object> metadadesAddicionals,
			ArxiuHeader capsalera);
	public String documentEniGet(
			String nodeId,
			ArxiuHeader capsalera);
	public void documentCopy(
			String nodeId,
			String nodeDestiId,
			ArxiuHeader capsalera);
	public void documentMove(
			String nodeId,
			String nodeDestiId,
			ArxiuHeader capsalera);
	public void documentSearch();
	public void documentLink();
	public void documentLock();
	public void documentUnlock();
	public void documentPermissionsGrant();
	public void documentPermissionsCancel();
	public void documentValidate();
	public void documentDispatch();

	public ArxiuFolder folderCreate(
			String name,
			String pareNodeId,
			ArxiuHeader capsalera);
	public void folderUpdate(
			String nodeId,
			String name,
			ArxiuHeader capsalera);
	public void folderDelete(
			String nodeId,
			ArxiuHeader capsalera);
	public ArxiuFolder folderGet(
			String nodeId,
			ArxiuHeader capsalera);
	public void folderCopy(
			String nodeId,
			String nodeDestiId,
			ArxiuHeader capsalera);
	public void folderMove(
			String nodeId,
			String nodeDestiId,
			ArxiuHeader capsalera);
	public void folderLink();
	public void folderLock();
	public void folderUnlock();
	public void folderPermissionsGrant();
	public void folderPermissionsCancel();

}
