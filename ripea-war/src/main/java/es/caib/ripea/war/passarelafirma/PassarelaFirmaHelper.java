package es.caib.ripea.war.passarelafirma;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.fundaciobit.plugins.signature.api.CommonInfoSignature;
import org.fundaciobit.plugins.signature.api.FileInfoSignature;
import org.fundaciobit.plugins.signature.api.ITimeStampGenerator;
import org.fundaciobit.plugins.signature.api.PdfVisibleSignature;
import org.fundaciobit.plugins.signature.api.PolicyInfoSignature;
import org.fundaciobit.plugins.signature.api.SecureVerificationCodeStampInfo;
import org.fundaciobit.plugins.signature.api.SignaturesTableHeader;
import org.fundaciobit.plugins.signature.api.StatusSignaturesSet;
import org.fundaciobit.plugins.signatureweb.api.ISignatureWebPlugin;
import org.fundaciobit.pluginsib.core.utils.PluginsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.ConfigService;
import es.caib.ripea.core.api.service.OrganGestorService;

/**
 * Classes s'ajuda per a les accions de la passarel·la de firma.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PassarelaFirmaHelper {

	public static final String CONTEXTWEB = "/firmapassarela";


	@Autowired
	private ConfigService configService;
	@Autowired
	private OrganGestorService organGestorService;
	@Autowired
	private AplicacioService aplicacioService;
	private long lastCheckFirmesCaducades = 0;
	private static final Object lock = new Object();
	
	private Map<String, SignaturesSetExtend> signaturesSets = new HashMap<String, SignaturesSetExtend>();
	private Map<String, ISignatureWebPluginWrapper> plugins = new HashMap<String, ISignatureWebPluginWrapper>();



	public String generateSignaturesSet(
			HttpServletRequest request,
			FitxerDto fitxerPerFirmar,
			String destinatariNif,
			String motiu,
			String llocFirma,
			String emailFirmant,
			String idiomaCodi,
			String urlFinalRipea,
			boolean navegadorSuportaJava) throws IOException {
		long signaturaId = generateUniqueSignaturesSetId();
		String signaturesSetId = new Long(signaturaId).toString();
		Calendar caducitat = Calendar.getInstance();
		caducitat.add(Calendar.MINUTE, 40);
		CommonInfoSignature commonInfoSignature;
		final String urlFinal = getRelativeControllerBase(request, CONTEXTWEB) + "/final/" + signaturesSetId;
		// TODO Veure manual de MiniApplet
		final String filtreCertificats = "filters.1=nonexpired:";
		// TODO Definir politica de Firma (opcional)
		PolicyInfoSignature pis = null;
		commonInfoSignature = new CommonInfoSignature(
				idiomaCodi,
				filtreCertificats,
				request.getUserPrincipal().getName(),
				destinatariNif);
		File filePerFirmar = getFitxerAFirmarPath(signaturaId);
		FileUtils.writeByteArrayToFile(
				filePerFirmar,
				fitxerPerFirmar.getContingut());
		FileInfoSignature fis = getFileInfoSignature(
				signaturesSetId,
				filePerFirmar, // File amb el fitxer a firmar
				fitxerPerFirmar.getContentType(), // Tipus mime del fitxer a firmar
				fitxerPerFirmar.getNom(), // Nom del fitxer a firmar
				0, // posició taula firmes: 0, 1, -1 (sense, primera pag., darrera pag.)
				null, // SignaturesTableHeader 
				motiu,
				llocFirma,
				emailFirmant,
				1, // Nombre de firmes (nomes en suporta una)
				idiomaCodi,
				FileInfoSignature.SIGN_TYPE_PADES,
				FileInfoSignature.SIGN_ALGORITHM_SHA1,
				FileInfoSignature.SIGN_MODE_IMPLICIT,
				false, // userRequiresTimeStamp,
				null, // timeStampGenerator,
				null); // svcsi
		SignaturesSetExtend signaturesSet = new SignaturesSetExtend(
				signaturesSetId,
				caducitat.getTime(),
				commonInfoSignature,
				new FileInfoSignature[] {fis},
				urlFinal,
				urlFinalRipea);
		
		synchronized (lock) {
			signaturesSets.put(signaturesSetId, signaturesSet);
		}
		
		return CONTEXTWEB + "/selectsignmodule/" + signaturesSetId;
	}

	public List<ISignatureWebPluginWrapper> instanciatePlugins(
			HttpServletRequest request,
			String signaturesSetId) throws Exception {
		
		SignaturesSetExtend signaturesSet = getSignaturesSet(request, signaturesSetId);
		
		List<ISignatureWebPluginWrapper> pluginsWrappers = new ArrayList<ISignatureWebPluginWrapper>();
		String idsStr = aplicacioService.propertyPluginPassarelaFirmaIds();
		String[] ids = idsStr.split(",");
		if (ids == null ) {
			String msg = "No se n'han definit els plugins.";
			throw new Exception(msg);
		}
		
		for (String id: ids) {
			
			// 1.- Instanciar el plugin
			ISignatureWebPluginWrapper pluginWrapper = getPluginWrapper(id);
			ISignatureWebPlugin plugin = pluginWrapper.getPlugin();
			
			// 2.- Passa el filtre
			String filter = plugin.filter(request, signaturesSet, null);
			if (filter == null) {
				pluginsWrappers.add(pluginWrapper);
			} else {
				log.info("Exclos plugin [" + pluginWrapper.getNom() + "]: NO PASSA FILTRE: " + filter);
			}
		}
		
		return pluginsWrappers;
	}

	public String openTransactionInWS(
			HttpServletRequest request,
			String signaturesSetId) throws Exception {
		SignaturesSetExtend signaturesSet = getSignaturesSet(request, signaturesSetId);
		String pluginId = signaturesSet.getPluginId();

		ISignatureWebPlugin signaturePlugin = getPluginWrapper(pluginId).getPlugin();

		// Open transaction in WS
		// put SignaturesSet in map in plugin
		String pluginUrl = signaturePlugin.signDocuments(
				request,
				getRequestPluginBaseUrl(
						getAbsoluteControllerBase(
								request,
								PassarelaFirmaHelper.CONTEXTWEB),
						signaturesSetId,
						-1),
				getRequestPluginBaseUrl(
						getRelativeControllerBase(
								request,
								PassarelaFirmaHelper.CONTEXTWEB),
						signaturesSetId,
						-1),
				signaturesSet,
				null);
		return pluginUrl;
	}

	public void loadResultFromWS(
			HttpServletRequest request,
			HttpServletResponse response,
			String signaturesSetId,
			int signatureIndex,
			String query) throws Exception {
		SignaturesSetExtend ss = getSignaturesSet(request, signaturesSetId);
		String pluginId = ss.getPluginId();
		ISignatureWebPlugin signaturePlugin = getPluginWrapper(pluginId).getPlugin();

		String absoluteRequestPluginBasePath = getRequestPluginBaseUrl(
				getAbsoluteControllerBase(
						request,
						PassarelaFirmaHelper.CONTEXTWEB),
				signaturesSetId,
				signatureIndex);
		String relativeRequestPluginBasePath = getRequestPluginBaseUrl(
				getRelativeControllerBase(
						request,
						PassarelaFirmaHelper.CONTEXTWEB),
				signaturesSetId,
				signatureIndex);
		if ("POST".equals(request.getMethod())) {
			signaturePlugin.requestPOST(
					absoluteRequestPluginBasePath,
					relativeRequestPluginBasePath,
					query,
					signaturesSetId,
					signatureIndex,
					request,
					response);
		} else {
			signaturePlugin.requestGET(
					absoluteRequestPluginBasePath,
					relativeRequestPluginBasePath,
					query,
					signaturesSetId,
					signatureIndex,
					request,
					response);
		}
	}

	
	public SignaturesSetExtend setStatusFinalitzat(
			SignaturesSetExtend pss) {

		StatusSignaturesSet sss = pss.getStatusSignaturesSet();
		if (sss.getStatus() == StatusSignaturesSet.STATUS_INITIALIZING
				|| sss.getStatus() == StatusSignaturesSet.STATUS_IN_PROGRESS) {
			// Vull presuposar que si i que el mòdul de firma s'ha oblidat
			// d'indicar aquest fet ???
			sss.setStatus(StatusSignaturesSet.STATUS_FINAL_OK);
		}
		return pss;
	}

	public SignaturesSetExtend getSignaturesSet(
			HttpServletRequest request,
			String signaturesSetId) {
		
		clearExpiredSignaturesSet(request, signaturesSetId);
		
		SignaturesSetExtend pss = signaturesSets.get(signaturesSetId);
		
		if (pss == null) {
			throw new RuntimeException("moduldefirma.caducat: " + signaturesSetId);
		}
		return pss;
	}
	
	private void clearExpiredSignaturesSet(
			HttpServletRequest request,
			String signaturesSetId) {
		
		// Fer net peticions caducades SignaturesSet.getExpiryDate()
		// Check si existeix algun proces de firma caducat s'ha d'esborrar
		// Com a mínim cada minut es revisa si hi ha caducats
		Long now = System.currentTimeMillis();
		final long un_minut_en_ms = 60 * 60 * 1000;
		if (now + un_minut_en_ms > lastCheckFirmesCaducades) {
			lastCheckFirmesCaducades = now;
			List<SignaturesSetExtend> keysToDelete = new ArrayList<SignaturesSetExtend>();
			Set<String> ids = signaturesSets.keySet();
			for (String id : ids) {
				SignaturesSetExtend ss = signaturesSets.get(id);
				if (now > ss.getExpiryDate().getTime()) {
					keysToDelete.add(ss);
					SimpleDateFormat sdf = new SimpleDateFormat();
					log.debug("Tancant Signature SET amb id = " + id + " a causa de que està caducat " + "( ARA: "
							+ sdf.format(new Date(now)) + " | CADUCITAT: " + sdf.format(ss.getExpiryDate()) + ")");
				}
			}
			if (keysToDelete.size() != 0) {
				synchronized (lock) {

					for (SignaturesSetExtend pss : keysToDelete) {
						closeTransactionInWS(request, pss);
					}
				}
			}
		}
	}
	


	public void closeTransactionInWS(HttpServletRequest request, SignaturesSetExtend pss) {
		String pluginId = pss.getPluginId();
		final String signaturesSetId = pss.getSignaturesSetID();
		if (pluginId != null) {
			ISignatureWebPlugin signaturePlugin = getPluginWrapper(pluginId).getPlugin();

			try {
				// Close transaction in WS
				// Remove SignaturesSet from map in plugin
				signaturePlugin.closeSignaturesSet(request, signaturesSetId);
			} catch (Exception e) {
				log.error("Error borrant dades d'un SignaturesSet " + signaturesSetId + ": " + e.getMessage(), e);
			}
		}
		signaturesSets.remove(signaturesSetId);
	}

	// -------------------------------------------------------------------------
	// -------------------------------------------------------------------------
	// ----------------------------- U T I L I T A T S ----------------------
	// -------------------------------------------------------------------------
	// -------------------------------------------------------------------------


	private long generateUniqueSignaturesSetId() {
		long id;
		synchronized (PassarelaFirmaHelper.CONTEXTWEB) {
			id = (System.currentTimeMillis() * 1000000L) + System.nanoTime() % 1000000L;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
		}
		return id;
	}

	private static final String AUTOFIRMA = "AUTOFIRMA";
	private static final String autofirmaBasePath;
	static {
		String tempDir = System.getProperty("java.io.tmpdir");
		final File base = new File(tempDir, AUTOFIRMA);
		base.mkdirs();
		autofirmaBasePath = base.getAbsolutePath();
	}
	private File getFitxerAFirmarPath(long id) {
		File f = new File(
				autofirmaBasePath + File.separatorChar + id,
				String.valueOf(id) + "_original");
		f.getParentFile().mkdirs();
		return f;
	}

	@SuppressWarnings("deprecation")
	private FileInfoSignature getFileInfoSignature(
			String signatureId,
			File fileToSign,
			String mimeType,
			String idname,
			int locationSignTableId,
			SignaturesTableHeader signaturesTableHeader,
			String reason,
			String location,
			String signerEmail,
			int signNumber,
			String languageSign,
			String signType,
			String signAlgorithm,
			int signModeUncheck,
			boolean userRequiresTimeStamp,
			ITimeStampGenerator timeStampGenerator,
			SecureVerificationCodeStampInfo csvStampInfo) {
		final int signMode = ((signModeUncheck == FileInfoSignature.SIGN_MODE_IMPLICIT)
				? FileInfoSignature.SIGN_MODE_IMPLICIT : FileInfoSignature.SIGN_MODE_EXPLICIT);
		PdfVisibleSignature pdfInfoSignature = null;
		if (FileInfoSignature.SIGN_TYPE_PADES.equals(signType)) {
			// PDF Visible
			pdfInfoSignature = new PdfVisibleSignature();
			if (locationSignTableId != FileInfoSignature.SIGNATURESTABLELOCATION_WITHOUT) {
				// No tenim generadors en aquest APP
				pdfInfoSignature.setRubricGenerator(null);
				pdfInfoSignature.setPdfRubricRectangle(null);
			}
		} else if (FileInfoSignature.SIGN_TYPE_CADES.equals(signType)) {
		} else if (FileInfoSignature.SIGN_TYPE_XADES.equals(signType)) {
		} else {
			// TODO Traduir
			throw new RuntimeException("Tipus de firma no suportada: " + signType);
		}
		if (FileInfoSignature.SIGN_ALGORITHM_SHA1.equals(signAlgorithm)
				|| FileInfoSignature.SIGN_ALGORITHM_SHA256.equals(signAlgorithm)
				|| FileInfoSignature.SIGN_ALGORITHM_SHA384.equals(signAlgorithm)
				|| FileInfoSignature.SIGN_ALGORITHM_SHA512.equals(signAlgorithm)) {
			// OK
		} else {
			// TODO Traduir
			throw new RuntimeException("Tipus d'algorisme no suportat " + signAlgorithm);
		}
		FileInfoSignature fis = new FileInfoSignature(
				signatureId,
				fileToSign,
				mimeType,
				idname,
				reason,
				location,
				signerEmail,
				signNumber,
				languageSign,
				signType,
				signAlgorithm,
				signMode,
				locationSignTableId,
				signaturesTableHeader,
				pdfInfoSignature,
				csvStampInfo,
				userRequiresTimeStamp,
				timeStampGenerator);
		return fis;
	}


	public static final String PROPERTIES_BASE = "es.caib.ripea.plugin.passarelafirma.";
	


	private Properties removePluginIdFromKey (Properties pluginProperties, String base) {

		Properties pluginPropertiesProcessat = new Properties();
		for (Object propertyObj: pluginProperties.keySet()) {
			String propertyKey = propertyObj.toString();
			String value = pluginProperties.getProperty(propertyKey);
			String nomFinal = propertyKey.substring(base.length());
			pluginPropertiesProcessat.put(
					PROPERTIES_BASE + nomFinal,
					value);
			log.debug(
					"Afegint propietat al plugin (" +
					"propertyOriginal=" + propertyKey + ", " +
					"propertyProcessat=" + (PROPERTIES_BASE + nomFinal) + ", " +
					"valor=" + value + ")");
		}

		return pluginPropertiesProcessat;
	}
	
	

	private ISignatureWebPluginWrapper getPluginWrapper(
			String pluginId) {
		
		String entitatCodi = aplicacioService.getEntitatActualCodi();
		if (entitatCodi == null) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}
		String propertyBaseAmbPluginId = PROPERTIES_BASE + pluginId;
		
		ISignatureWebPluginWrapper pluginWrapper = null;
		// ORGAN PLUGIN
		String organCodi = organGestorService.getOrganCodi();
		if (organCodi != null) {
			pluginWrapper = plugins.get(pluginId + "." + entitatCodi + "." + organCodi);
			if (pluginWrapper != null) {
				return pluginWrapper;
			}
			String pluginClassOrgan = aplicacioService.getValueForOrgan(entitatCodi, organCodi, propertyBaseAmbPluginId + ".class");
			if (StringUtils.isNotEmpty(pluginClassOrgan)) {
					Properties pluginProperties = aplicacioService.getGroupPropertiesOrganOrEntitatOrGeneral("FIRMA_PASSARELA-" + pluginId, entitatCodi, organCodi);
					ISignatureWebPlugin plugin = (ISignatureWebPlugin)PluginsManager.instancePluginByClassName(
							pluginClassOrgan,
							propertyBaseAmbPluginId + ".",
							pluginProperties);
					pluginWrapper = new ISignatureWebPluginWrapper(
							pluginProperties,
							pluginId, 
							plugin);
					if (plugin == null) {
						throw new RuntimeException("plugin.donotinstantiate: " + pluginWrapper.getNom() + " (" + pluginClassOrgan + ")");
					}
					plugins.put(pluginId + "." + entitatCodi + "." + organCodi, pluginWrapper);
					return pluginWrapper;
			}
		}
		
		
		// ENTITAT/GENERAL PLUGIN
		pluginWrapper = plugins.get(pluginId + "." + entitatCodi);
		if (pluginWrapper != null) {
			return pluginWrapper;
		}
		String pluginClass = aplicacioService.propertyFindByNom(propertyBaseAmbPluginId + ".class");
		if (Strings.isNullOrEmpty(pluginClass)) {
			throw new RuntimeException("No està configurada la classe per al plugin: " + propertyBaseAmbPluginId + ".class");
		}
		Properties pluginProperties = aplicacioService.getGroupPropertiesEntitatOrGeneral("FIRMA_PASSARELA-" + pluginId, entitatCodi);
		ISignatureWebPlugin plugin = (ISignatureWebPlugin)PluginsManager.instancePluginByClassName(
				pluginClass,
				propertyBaseAmbPluginId + ".",
				pluginProperties);
		pluginWrapper = new ISignatureWebPluginWrapper(
				pluginProperties,
				pluginId, 
				plugin);
		if (plugin == null) {
			throw new RuntimeException("plugin.donotinstantiate: " + pluginWrapper.getNom() + " (" + pluginClass + ")");
		}
		plugins.put(pluginId + "." + entitatCodi, pluginWrapper);
		return pluginWrapper;
	}
	
	
//	private ISignatureWebPlugin getInstanceByPlugin(
//			PassarelaFirmaPlugin pluginDeFirma) throws Exception {
//		ISignatureWebPlugin instance = instancesCache.get(pluginDeFirma.getPluginId());
//		if (instance == null) {
//			instance = createInstanceFromPlugin(pluginDeFirma);
//		}
//		return instance;
//	}

	
//	private ISignatureWebPlugin createInstanceFromPlugin(
//			PassarelaFirmaPlugin pluginDeFirma) throws Exception {
//
//		ISignatureWebPlugin instance = (ISignatureWebPlugin)PluginsManager.instancePluginByClassName(
//				pluginDeFirma.getClasse(),
//				PROPERTIES_BASE,
//				pluginDeFirma.getProperties());
//		if (instance == null) {
//			throw new Exception("plugin.donotinstantiate: " + pluginDeFirma.getNom() + " (" + pluginDeFirma.getClasse() + ")");
//		}
//		instancesCache.put(pluginDeFirma.getPluginId(), instance);
//		return instance;
//	}


	private String getRequestPluginBaseUrl(
			String base,
			String signaturesSetId,
			int signatureIndex) {
		String absoluteRequestPluginBasePath = base + "/requestPlugin/" + signaturesSetId + "/" + signatureIndex;
		return absoluteRequestPluginBasePath;
	}
	private String getRelativeControllerBase(
			HttpServletRequest request,
			String webContext) {
		return request.getContextPath() + webContext;
	}
	private String getAbsoluteControllerBase(
			HttpServletRequest request,
			String webContext) {
		String baseUrl = getBaseUrlProperty();
		if (baseUrl != null && !baseUrl.isEmpty()) {
			if (baseUrl.endsWith("/")) {
				return baseUrl.substring(0, baseUrl.length() - 1) + webContext;
			} else {
				return baseUrl + webContext;
			}
		} else {
			return	request.getScheme() + "://" +
					request.getServerName() + ":" +
					request.getServerPort() +
					request.getContextPath() +
					webContext;
		}
	}

	private String getBaseUrlProperty() {
		return aplicacioService.propertyBaseUrl();
	}
	
	public void resetPlugin() {
		plugins = new HashMap<String, ISignatureWebPluginWrapper>();
	}

	private static Logger log = LoggerFactory.getLogger(PassarelaFirmaHelper.class);

}
