///**
// * 
// */
//package es.caib.ripea.war.escaneig;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Properties;
//import java.util.Set;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.fundaciobit.plugins.scanweb.api.IScanWebPlugin;
//import org.fundaciobit.plugins.scanweb.api.ScanWebMode;
//import org.fundaciobit.plugins.scanweb.api.ScanWebStatus;
//import org.fundaciobit.plugins.utils.Metadata;
//import org.fundaciobit.plugins.utils.PluginsManager;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import es.caib.ripea.core.api.service.AplicacioService;
//import es.caib.ripea.war.command.DocumentCommand;
//
///**
// * Classes s'ajuda per a les accions relacionades amb l'escaneig de documents.
// * 
// * @author Limit Tecnologies <limit@limit.es>
// */
//@Component
//public class EscaneigHelper {
//
//	public static final String CONTEXTWEB = "/scanweb";
//
//	@Autowired
//	private AplicacioService aplicacioService;
//
//	private static final Map<Long, EscaneigConfig> scanWebConfigMap = new HashMap<Long, EscaneigConfig>();
//	private static long lastCheckScanProcessCaducades = 0;
//
//
//
//	public String iniciarEscaneig(
//			HttpServletRequest request,
//			String scanType,
//			Set<String> flags,
//			List<Metadata> metadades,
//			ScanWebMode mode,
//			String languageUI,
//			DocumentCommand command,
//			String urlFinalRipea) {
//		Long scanWebId = generateUniqueScanWebId();
//		Calendar caducitat = Calendar.getInstance();
//		caducitat.add(Calendar.MINUTE, 40);
//		final String urlFinal = getRelativeControllerBase(request, CONTEXTWEB) + "/final/" + scanWebId;
//		EscaneigConfig swc = new EscaneigConfig(
//				scanWebId,
//				scanType,
//				flags,
//				metadades,
//				mode,
//				languageUI,
//				command,
//				urlFinal,
//				caducitat.getTimeInMillis(),
//				urlFinalRipea);
//		startScanWebProcess(swc);
//		return CONTEXTWEB + "/selectscanwebmodule/" + scanWebId;
//	}
//
//	public List<EscaneigPlugin> getAllPlugins(
//			HttpServletRequest request,
//			long scanWebId) throws Exception {
//		EscaneigConfig scanWebConfig = getScanWebConfig(request, scanWebId);
//		List<EscaneigPlugin> plugins = getAllPluginsFromProperties();
//		if (plugins == null || plugins.size() == 0) {
//			String msg = "S'ha produit un error llegint els plugins o no se n'han definit";
//			throw new Exception(msg);
//		}
//		List<EscaneigPlugin> pluginsFiltered = new ArrayList<EscaneigPlugin>();
//		IScanWebPlugin scanWebPlugin;
//		for (EscaneigPlugin pluginDeScanWeb : plugins) {
//			// 1.- Es pot instanciar el plugin ?
//			scanWebPlugin = getInstanceByPluginId(pluginDeScanWeb.getPluginId());
//			if (scanWebPlugin == null) {
//				throw new Exception("No s'ha pogut instanciar plugin amb id " + pluginDeScanWeb.getPluginId());
//			}
//			// 2.- Passa el filtre ...
//			if (scanWebPlugin.filter(request, scanWebConfig)) {
//				pluginsFiltered.add(pluginDeScanWeb);
//			} else {
//				log.info("Exclos plugin [" + pluginDeScanWeb.getNom() + "]: NO PASSA FILTRE");
//			}
//		}
//		return pluginsFiltered;
//	}
//
//	public String scanDocument(
//			HttpServletRequest request,
//			long scanWebId) throws Exception {
//		EscaneigConfig swc = getScanWebConfig(request, scanWebId);
//		String pluginId = swc.getPluginId();
//		IScanWebPlugin scanWebPlugin = getInstanceByPluginId(pluginId);
//		if (scanWebPlugin == null) {
//			String msg = "plugin.scanweb.noexist: " + String.valueOf(pluginId);
//			throw new Exception(msg);
//		}
//		String urlToPluginWebPage = scanWebPlugin.startScanWebTransaction(
//				getRequestPluginBasePath(
//						getAbsoluteControllerBase(
//								request,
//								CONTEXTWEB),
//						scanWebId),
//				getRequestPluginBasePath(
//						getRelativeControllerBase(
//								request,
//								CONTEXTWEB),
//						scanWebId),
//				request,
//				swc);
//		return urlToPluginWebPage;
//	}
//
//	public void requestPlugin(
//			HttpServletRequest request,
//			HttpServletResponse response,
//			long scanWebId,
//			String query) throws Exception {
//		EscaneigConfig swc = getScanWebConfig(request, scanWebId);
//		if (swc == null) {
//			response.sendRedirect("/index.jsp");
//			return;
//		}
//		String pluginId = swc.getPluginId();
//		IScanWebPlugin scanWebPlugin;
//		try {
//			scanWebPlugin = getInstanceByPluginId(pluginId);
//		} catch (Exception e) {
//			throw new Exception(
//					"plugin.scanweb.noexist: " + pluginId);
//		}
//		if (scanWebPlugin == null) {
//			throw new Exception(
//					"plugin.scanweb.noexist: " + pluginId);
//		}
//		String absoluteRequestPluginBasePath = getRequestPluginBasePath(
//				getAbsoluteControllerBase(
//						request,
//						CONTEXTWEB),
//				scanWebId);
//		String relativeRequestPluginBasePath = getRequestPluginBasePath(
//				getRelativeControllerBase(
//						request,
//						CONTEXTWEB),
//				scanWebId);
//		if ("POST".equals(request.getMethod())) {
//			scanWebPlugin.requestPOST(
//					absoluteRequestPluginBasePath,
//					relativeRequestPluginBasePath,
//					scanWebId,
//					query,
//					request,
//					response);
//		} else {
//			scanWebPlugin.requestGET(
//					absoluteRequestPluginBasePath,
//					relativeRequestPluginBasePath,
//					scanWebId,
//					query,
//					request,
//					response);
//		}
//	}
//
//	public EscaneigConfig finalitzarEscaneig(
//			HttpServletRequest request,
//			long scanWebId) {
//		EscaneigConfig swc = getScanWebConfig(request, scanWebId);
//		// Check pss is null
//		if (swc == null) {
//			String msg = "moduldefirma.caducat: " + scanWebId;
//			throw new RuntimeException(msg);
//		}
//		if (	swc.getStatus().getStatus() == ScanWebStatus.STATUS_INITIALIZING ||
//				swc.getStatus().getStatus() == ScanWebStatus.STATUS_IN_PROGRESS) {
//			ScanWebStatus statusOk = new ScanWebStatus();
//			statusOk.setStatus(ScanWebStatus.STATUS_FINAL_OK);
//			swc.setStatus(statusOk);
//		}
//		return swc;
//	}
//
//	public EscaneigConfig getScanWebConfig(
//			HttpServletRequest request,
//			long scanWebId) {
//		// Fer net peticions caducades
//		// Check si existeix algun proces de escaneig caducat s'ha d'esborrar
//		// Com a mínim cada minut es revisa si hi ha caducats
//		Long now = System.currentTimeMillis();
//		final long un_minut_en_ms = 60 * 60 * 1000;
//		if (now + un_minut_en_ms > lastCheckScanProcessCaducades) {
//			lastCheckScanProcessCaducades = now;
//			Map<Long, EscaneigConfig> keysToDelete = new HashMap<Long, EscaneigConfig>();
//			Set<Long> ids = scanWebConfigMap.keySet();
//			for (Long id : ids) {
//				EscaneigConfig ss = scanWebConfigMap.get(id);
//				if (now > ss.getExpiryTransaction()) {
//					keysToDelete.put(id, ss);
//					SimpleDateFormat sdf = new SimpleDateFormat();
//					log.info("Tancant ScanWebConfig amb ID = " + id + " a causa de que està caducat " + "( ARA: "
//							+ sdf.format(new Date(now)) + " | CADUCITAT: "
//							+ sdf.format(new Date(ss.getExpiryTransaction())) + ")");
//				}
//			}
//			if (keysToDelete.size() != 0) {
//				synchronized (scanWebConfigMap) {
//					for (Entry<Long, EscaneigConfig> pss: keysToDelete.entrySet()) {
//						closeScanWebProcess(request, pss.getValue());
//					}
//				}
//			}
//		}
//		return scanWebConfigMap.get(scanWebId);
//	}
//
//	public void closeScanWebProcess(
//			HttpServletRequest request,
//			EscaneigConfig swc) {
//		long scanWebId = swc.getScanWebID();
//		String pluginId = swc.getPluginId();
//		if (pluginId != null) {
//			IScanWebPlugin scanWebPlugin = null;
//			try {
//				scanWebPlugin = getInstanceByPluginId(pluginId);
//			} catch (Exception e) {
//				log.error(e.getMessage(), e);
//				return;
//			}
//			if (scanWebPlugin == null) {
//				log.error("plugin.scanweb.noexist: " + pluginId);
//			}
//			try {
//				scanWebPlugin.endScanWebTransaction(scanWebId, request);
//			} catch (Exception e) {
//				log.error("Error borrant dades d'un Proces d'escaneig " + scanWebId + ": " + e.getMessage(), e);
//			}
//		}
//		scanWebConfigMap.remove(scanWebId);
//	}
//
//	public Set<String> getDefaultFlags(EscaneigConfig swc) throws Exception {
//		String pluginId = swc.getPluginId();
//		IScanWebPlugin scanWebPlugin;
//		try {
//			scanWebPlugin = getInstanceByPluginId(pluginId);
//		} catch (Exception e) {
//			throw new Exception(
//					"plugin.scanweb.noexist: " + pluginId);
//		}
//		if (scanWebPlugin == null) {
//			throw new Exception(
//					"plugin.scanweb.noexist: " + pluginId);
//		}
//		List<Set<String>> supFlags = scanWebPlugin.getSupportedFlagsByScanType(
//				swc.getScanType());
//		return supFlags.get(0);
//	}
//
//	// -------------------------------------------------------------------------
//	// -------------------------------------------------------------------------
//	// ----------------------------- U T I L I T A T S ----------------------
//	// -------------------------------------------------------------------------
//	// -------------------------------------------------------------------------
//
//	private void startScanWebProcess(EscaneigConfig scanWebConfig) {
//		Long scanWebId = scanWebConfig.getScanWebID();
//		synchronized (scanWebConfigMap) {
//			scanWebConfigMap.put(scanWebId, scanWebConfig);
//		}
//	}
//
//	private static long generateUniqueScanWebId() {
//	    long id;
//	    synchronized (CONTEXTWEB) {
//	      id = (System.currentTimeMillis() * 1000000L) + System.nanoTime() % 1000000L;
//	      try {
//	        Thread.sleep(10);
//	      } catch (InterruptedException e) {
//	      }
//	    }
//	    return id;
//	}
//
//	private List<EscaneigPlugin> plugins;
//	private static final String PROPERTIES_BASE = "es.caib.ripea.plugin.escaneig.";
//	private List<EscaneigPlugin> getAllPluginsFromProperties() {
//		if (plugins != null) {
//			return plugins;
//		}
//		String idsStr = aplicacioService.propertyPluginEscaneigIds();
//		String[] ids = idsStr.split(",");
//		plugins = new ArrayList<EscaneigPlugin>();
//		for (String id: ids) {
//			String base = PROPERTIES_BASE + id + ".";
//			Properties pluginProperties = aplicacioService.propertyFindByGroup("ESCANEIG-" + id);
//			String nom = pluginProperties.getProperty(base + "nom");
//			log.debug("Carregant plugin escaneig [" + base + "nom" + "]: " + nom);
//			String classe = pluginProperties.getProperty(base + "class");
//			String descripcioCurta = pluginProperties.getProperty(base + "desc");
//			Properties pluginPropertiesProcessat = new Properties();
//			for (Object property: pluginProperties.keySet()) {
//				String propertyStr = property.toString();
//				String value = pluginProperties.getProperty(propertyStr);
//				String nomFinal = propertyStr.substring(base.length());
//				pluginPropertiesProcessat.put(
//						PROPERTIES_BASE + nomFinal,
//						value);
//			}
//			log.debug("  descripcioCurta: " + descripcioCurta);
//			log.debug("  classe: " + classe);
//			log.debug("  properties: " + pluginPropertiesProcessat);
//			plugins.add(
//					new EscaneigPlugin(
//							id,
//							nom,
//							descripcioCurta,
//							classe,
//							pluginPropertiesProcessat));
//		}
//		return plugins;
//	}
//
//	private Map<String, IScanWebPlugin> instancesCache = new HashMap<String, IScanWebPlugin>();
//	private Map<String, EscaneigPlugin> pluginsCache = new HashMap<String, EscaneigPlugin>();
//	private IScanWebPlugin getInstanceByPluginId(
//			String pluginId) throws Exception {
//		IScanWebPlugin instance = instancesCache.get(pluginId);
//		if (instance == null) {
//			EscaneigPlugin plugin = getPluginFromCache(pluginId);
//			if (plugin == null) {
//				plugin = getPluginById(pluginId);
//				if (plugin == null) {
//					return null;
//				}
//				addPluginToCache(pluginId, plugin);
//			}
//			instance = (IScanWebPlugin)PluginsManager.instancePluginByClassName(plugin.getClasse(),
//					PROPERTIES_BASE,
//					plugin.getProperties());
//			if (instance == null) {
//				throw new Exception("plugin.donotinstantiate: " + plugin.getNom() + " (" + plugin.getClasse() + ")");
//			}
//			instancesCache.put(pluginId, instance);
//		}
//		return instance;
//	}
//	private void addPluginToCache(
//			String pluginId,
//			EscaneigPlugin pluginInstance) {
//		synchronized (pluginsCache) {
//			pluginsCache.put(pluginId, pluginInstance);
//		}
//	}
//	private EscaneigPlugin getPluginFromCache(
//			String pluginId) {
//		synchronized (pluginsCache) {
//			return pluginsCache.get(pluginId);
//		}
//	}
//	private EscaneigPlugin getPluginById(String pluginId) {
//		for (EscaneigPlugin plugin: getAllPluginsFromProperties()) {
//			if (plugin.getPluginId().equals(pluginId)) {
//				return plugin;
//			}
//		}
//		return null;
//	}
//
//	private String getRequestPluginBasePath(
//			String base,
//			long scanWebId) {
//		return base + "/requestPlugin/" + scanWebId;
//	}
//	private String getRelativeControllerBase(
//			HttpServletRequest request,
//			String webContext) {
//		return request.getContextPath() + webContext;
//	}
//	private String getAbsoluteControllerBase(
//			HttpServletRequest request,
//			String webContext) {
//		return	request.getScheme() + "://" +
//				request.getServerName() + ":" +
//				request.getServerPort() +
//				request.getContextPath() +
//				webContext;
//	}
//
//	private static Logger log = LoggerFactory.getLogger(EscaneigHelper.class);
//
//}
