package es.caib.ripea.plugin.caib.comanda;

import java.util.Properties;

import org.springframework.http.ResponseEntity;

import es.caib.comanda.model.management.Avis;
import es.caib.comanda.model.management.Tasca;
import es.caib.comanda.model.management.TascaPage;
import es.caib.comanda.service.management.AppComandaClient;
import es.caib.ripea.plugin.RipeaAbstractPluginProperties;
import es.caib.ripea.plugin.comanda.ComandaCaibPlugin;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ComandaCaibPluginImpl extends RipeaAbstractPluginProperties implements ComandaCaibPlugin {

	public ComandaCaibPluginImpl(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}
	
	@Override
	public String getEndpointURL() {
		String resultat = getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_PLUGIN_ENDPOINT));
		if (!Utils.hasValue(resultat)) {
			resultat = getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_PLUGIN_URL));
			if (Utils.hasValue(resultat)) {
				resultat = resultat.replace("http://", "").replace("https://", "");
			}
		}
		return resultat;
	}

	@Override
	public ResponseEntity<String> sendTasca(Tasca tasca) throws Exception {
		String url 		= getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_PLUGIN_URL));
		String username = getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_PLUGIN_USR));
		String password = getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_PLUGIN_PWR));
		AppComandaClient clientcomanda = new AppComandaClient(url, username, password);
		String resultat = clientcomanda.crearTasca(tasca);
		return ResponseEntity.ok(resultat);
	}

	@Override
	public ResponseEntity<String> sendAvis(Avis avis) throws Exception {
		String url 		= getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_PLUGIN_URL));
		String username = getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_PLUGIN_USR));
		String password = getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_PLUGIN_PWR));
		AppComandaClient clientcomanda = new AppComandaClient(url, username, password);
		String resultat = clientcomanda.crearAvis(avis);
		return ResponseEntity.ok(resultat);
	}

	@Override
	public TascaPage getLlistatTasques(String quickFilter) throws Exception {
		String url 		= getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_PLUGIN_URL));
		String username = getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_PLUGIN_USR));
		String password = getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_PLUGIN_PWR));
		AppComandaClient clientcomanda = new AppComandaClient(url, username, password);
		return clientcomanda.obtenirLlistatTasques(quickFilter, null, "0", 1);
	}
}