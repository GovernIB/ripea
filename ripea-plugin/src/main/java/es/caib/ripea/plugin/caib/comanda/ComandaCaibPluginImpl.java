package es.caib.ripea.plugin.caib.comanda;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import es.caib.comanda.model.management.Avis;
import es.caib.comanda.model.management.Tasca;
import es.caib.comanda.model.management.TascaPage;
import es.caib.comanda.model.server.monitoring.EstatSalutEnum;
import es.caib.comanda.model.server.monitoring.SalutInfo;
import es.caib.comanda.service.management.AppComandaClient;
import es.caib.ripea.plugin.RipeaAbstractPluginProperties;
import es.caib.ripea.plugin.comanda.ComandaCaibPlugin;
import es.caib.ripea.plugin.comanda.ComandaResultatListener;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ComandaCaibPluginImpl extends RipeaAbstractPluginProperties implements ComandaCaibPlugin {

	/** Temps màxim (ms) per establir la connexió amb comanda quan la propietat no està configurada. */
	private static final int CONNECT_TIMEOUT_DEFECTE_MS = 5000;
	/** Temps màxim (ms) d'espera de dades de comanda quan la propietat no està configurada. */
	private static final int READ_TIMEOUT_DEFECTE_MS = 10000;

	/**
	 * Un sol fil expressament: la cua és FIFO, així que amb un únic consumidor les crides arriben a comanda en el
	 * mateix ordre en què s'han demanat. Amb més fils, dues modificacions seguides de la mateixa tasca es podrien
	 * avançar l'una a l'altra i comanda es quedaria amb l'estat antic.
	 */
	private static final int ASINCRON_NOMBRE_FILS = 1;
	private static final int ASINCRON_CUA_MAXIMA = 200;

	/**
	 * Pool per a les crides que no necessiten la resposta. És estàtic perquè es comparteixi entre totes les
	 * instàncies del plugin (n'hi ha una per entitat) i així el nombre de fils que poden estar esperant comanda
	 * quedi acotat globalment. El fil és daemon i mor quan està inactiu, de manera que en repòs el plugin no en
	 * manté cap de viu.
	 */
	private static final ExecutorService EXECUTOR_ASINCRON = crearExecutorAsincron();

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
		AppComandaClient clientComanda = crearClient();
		try {
			String resultat = clientComanda.crearTasca(tasca);
			return ResponseEntity.ok(resultat);
		} finally {
			tancarClient(clientComanda);
		}
	}

	@Override
	public ResponseEntity<String> deleteTasca(String idTasca) throws Exception {
		AppComandaClient clientComanda = crearClient();
		try {
			String appCodi 	= getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_APP_CODI));
			String entorn 	= getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_PLUGIN_ENTORN));
			String resultat = clientComanda.eliminarTasca(idTasca, appCodi, entorn);
			return ResponseEntity.ok(resultat);
		} finally {
			tancarClient(clientComanda);
		}
	}

	@Override
	public ResponseEntity<String> sendAvis(Avis avis) throws Exception {
		AppComandaClient clientComanda = crearClient();
		try {
			String resultat = clientComanda.crearAvis(avis);
			return ResponseEntity.ok(resultat);
		} finally {
			tancarClient(clientComanda);
		}
	}

	@Override
	public ResponseEntity<String> deleteAvis(String idAvis) throws Exception {
		AppComandaClient clientComanda = crearClient();
		try {
			String appCodi 	= getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_APP_CODI));
			String entorn 	= getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_PLUGIN_ENTORN));
			String resultat = clientComanda.eliminarAvis(idAvis, appCodi, entorn);
			return ResponseEntity.ok(resultat);
		} finally {
			tancarClient(clientComanda);
		}
	}

	@Override
	public TascaPage getLlistatTasques(String quickFilter) throws Exception {
		AppComandaClient clientComanda = crearClient();
		try {
			return clientComanda.obtenirLlistatTasques(quickFilter, null, "0", 1);
		} finally {
			tancarClient(clientComanda);
		}
	}

	@Override
	public EstatSalutEnum getSalutComanda() throws Exception {
		String url		= getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_PLUGIN_URL));
		String username	= getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_PLUGIN_USR));
		String password	= getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_PLUGIN_PWR));
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(username, password);
		// Un RestTemplate sense request factory pròpia tampoc no té cap timeout: s'hi apliquen els mateixos que
		// a la resta de crides perquè una consulta de salut no pugui deixar el fil esperant indefinidament.
		ResponseEntity<SalutInfo> response = new RestTemplate(crearRequestFactory()).exchange(
				url + "/salut/v1",
				HttpMethod.GET,
				new HttpEntity<>(headers),
				SalutInfo.class);
		SalutInfo salutInfo = response.getBody();
		if (salutInfo != null && salutInfo.getEstatGlobal() != null) {
			return salutInfo.getEstatGlobal().getEstat();
		}
		return null;
	}

	@Override
	public void sendTascaAsync(Tasca tasca, ComandaResultatListener listener) {
		executarSenseEsperarResposta("enviar una tasca", () -> sendTasca(tasca), listener);
	}

	@Override
	public void deleteTascaAsync(String idTasca, ComandaResultatListener listener) {
		executarSenseEsperarResposta("eliminar la tasca " + idTasca, () -> deleteTasca(idTasca), listener);
	}

	@Override
	public void sendAvisAsync(Avis avis, ComandaResultatListener listener) {
		executarSenseEsperarResposta("enviar un avís", () -> sendAvis(avis), listener);
	}

	@Override
	public void deleteAvisAsync(String idAvis, ComandaResultatListener listener) {
		executarSenseEsperarResposta("eliminar l'avís " + idAvis, () -> deleteAvis(idAvis), listener);
	}

	/**
	 * Crea el client de comanda amb els timeouts configurats.
	 *
	 * La llibreria no permet indicar-los: AppComandaClient (generat amb openapi-generator) construeix el seu
	 * javax.ws.rs.client.Client amb ClientBuilder.newClient(...) i, amb la implementació de RESTEasy del servidor
	 * d'aplicacions, els valors per defecte de socketTimeout i establishConnectionTimeout són -1, és a dir, espera
	 * infinita. Amb comanda aturat o penjat, la crida bloquejaria el fil que la fa (i, si ve d'una petició, també
	 * la seva transacció) fins que el sistema operatiu tallés la connexió.
	 *
	 * Per això es substitueix el client per un de construït amb els timeouts, on s'hi registra el mateix proveïdor
	 * JSON que hi registra la llibreria (ApiClient.buildHttpClient) perquè la serialització no canviï. L'autenticació
	 * no es veu afectada: el constructor l'afegeix com a capçalera per defecte de l'ApiClient, no del client HTTP.
	 */
	private AppComandaClient crearClient() {
		String url 		= getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_PLUGIN_URL));
		String username = getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_PLUGIN_USR));
		String password = getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_PLUGIN_PWR));
		AppComandaClient clientComanda = new AppComandaClient(url, username, password);
		Client clientSenseTimeouts = clientComanda.getHttpClient();
		try {
			clientComanda.setHttpClient(ClientBuilder.newBuilder()
					.register(clientComanda.getJSON())
					.connectTimeout(getConnectTimeout(), TimeUnit.MILLISECONDS)
					.readTimeout(getReadTimeout(), TimeUnit.MILLISECONDS)
					.build());
		} finally {
			tancarClientHttp(clientSenseTimeouts);
		}
		return clientComanda;
	}

	/**
	 * Allibera el client HTTP quan s'ha acabat la crida. Cada AppComandaClient crea el seu propi pool de connexions
	 * i, si no es tanca, les connexions queden obertes fins que comanda o el sistema operatiu les tallen.
	 */
	private void tancarClient(AppComandaClient clientComanda) {
		if (clientComanda != null) {
			tancarClientHttp(clientComanda.getHttpClient());
		}
	}

	private void tancarClientHttp(Client client) {
		if (client == null) {
			return;
		}
		try {
			client.close();
		} catch (Exception ex) {
			// El tancament no ha de fer fallar una crida que ha anat bé
			log.warn("No s'ha pogut tancar el client de comanda", ex);
		}
	}

	private SimpleClientHttpRequestFactory crearRequestFactory() {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setConnectTimeout(getConnectTimeout());
		requestFactory.setReadTimeout(getReadTimeout());
		return requestFactory;
	}

	private int getConnectTimeout() {
		return getTimeout(PropertyConfig.COMANDA_PLUGIN_CONNECT_TIMEOUT, CONNECT_TIMEOUT_DEFECTE_MS);
	}

	private int getReadTimeout() {
		return getTimeout(PropertyConfig.COMANDA_PLUGIN_READ_TIMEOUT, READ_TIMEOUT_DEFECTE_MS);
	}

	/**
	 * Llegeix un timeout en mil·lisegons. Si la propietat no està informada o no conté un número s'aplica el valor
	 * per defecte, perquè una configuració incorrecta no impedeixi enviar res a comanda.
	 */
	private int getTimeout(String propertyKey, int valorDefecte) {
		String valor = getProperty(PropertyConfig.getPropertySuffix(propertyKey));
		if (!Utils.hasValue(valor)) {
			return valorDefecte;
		}
		try {
			return Integer.parseInt(valor.trim());
		} catch (NumberFormatException ex) {
			log.warn("El valor de la propietat {} no és un número ({}), s'aplica el valor per defecte de {} ms",
					propertyKey,
					valor,
					valorDefecte);
			return valorDefecte;
		}
	}

	/**
	 * Executa una crida a comanda en segon pla. Els errors no es propaguen (qui ha demanat la crida ja no
	 * l'espera): es deixen al log i s'ofereixen al listener perquè en pugui deixar constància on toqui.
	 */
	private void executarSenseEsperarResposta(String descripcio, CridaComanda crida, ComandaResultatListener listener) {
		try {
			EXECUTOR_ASINCRON.execute(() -> {
				long t0 = System.currentTimeMillis();
				Exception error = null;
				try {
					crida.executar();
				} catch (Exception ex) {
					error = ex;
					log.error("Error al " + descripcio + " a comanda en segon pla", ex);
				}
				notificarResultat(listener, System.currentTimeMillis() - t0, error, descripcio);
			});
		} catch (RejectedExecutionException ex) {
			// Si comanda no respon i la cua s'omple, les crides noves es descarten: són informatives i no han de
			// fer créixer la memòria ni bloquejar qui les demana. Es notifica com a error perquè el descartament
			// no passi desapercebut.
			log.error("La cua de crides a comanda en segon pla és plena (" + ASINCRON_CUA_MAXIMA
					+ " pendents): es descarta " + descripcio, ex);
			notificarResultat(listener, 0, ex, descripcio);
		}
	}

	/**
	 * Comunica el resultat de la crida. Si falla el registre del resultat no s'ha de tombar el fil de segon pla,
	 * que ha de continuar atenent la resta de crides de la cua.
	 */
	private void notificarResultat(ComandaResultatListener listener, long tempsResposta, Exception error, String descripcio) {
		if (listener == null) {
			return;
		}
		try {
			if (error == null) {
				listener.onOk(tempsResposta);
			} else {
				listener.onError(tempsResposta, error);
			}
		} catch (Exception ex) {
			log.error("Error al registrar el resultat de " + descripcio + " a comanda", ex);
		}
	}

	private static ExecutorService crearExecutorAsincron() {
		ThreadPoolExecutor executor = new ThreadPoolExecutor(
				ASINCRON_NOMBRE_FILS,
				ASINCRON_NOMBRE_FILS,
				60L,
				TimeUnit.SECONDS,
				new LinkedBlockingQueue<>(ASINCRON_CUA_MAXIMA),
				runnable -> {
					Thread fil = new Thread(runnable, "ripea-comanda-async");
					fil.setDaemon(true);
					return fil;
				});
		executor.allowCoreThreadTimeOut(true);
		return executor;
	}

	/** Crida a comanda que es pot executar en segon pla. */
	@FunctionalInterface
	private interface CridaComanda {
		void executar() throws Exception;
	}
}
