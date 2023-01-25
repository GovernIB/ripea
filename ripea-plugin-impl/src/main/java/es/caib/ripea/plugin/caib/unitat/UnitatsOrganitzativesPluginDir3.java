/**
 * 
 */
package es.caib.ripea.plugin.caib.unitat;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import es.caib.dir3caib.ws.api.unidad.Dir3CaibObtenerUnidadesWs;
import es.caib.dir3caib.ws.api.unidad.Dir3CaibObtenerUnidadesWsService;
import es.caib.dir3caib.ws.api.unidad.UnidadTF;
import es.caib.ripea.plugin.PropertiesHelper;
import es.caib.ripea.plugin.RipeaAbstractPluginProperties;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.unitat.NodeDir3;
import es.caib.ripea.plugin.unitat.UnitatOrganitzativa;
import es.caib.ripea.plugin.unitat.UnitatsOrganitzativesPlugin;

/**
 * Implementació de proves del plugin d'unitats organitzatives.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UnitatsOrganitzativesPluginDir3 extends RipeaAbstractPluginProperties implements UnitatsOrganitzativesPlugin {

    private static final String SERVEI_ORGANIGRAMA = "rest/organigrama/";
    private static final String SERVEI_OBTENIR_UNITATS = "ws/Dir3CaibObtenerUnidades";

	public UnitatsOrganitzativesPluginDir3() {
		super();
	}
	public UnitatsOrganitzativesPluginDir3(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}

    public Map<String, NodeDir3> organigrama(String codi) throws SistemaExternException {

        Map<String, NodeDir3> organigrama = new HashMap<String, NodeDir3>();
        try {
            URL url = new URL(getServiceUrl() + SERVEI_ORGANIGRAMA + "?codigo=" + codi);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            byte[] response = IOUtils.toByteArray(httpConnection.getInputStream());
            if (response != null && response.length > 0) {
                NodeDir3 arrel = mapper.readValue(response, NodeDir3.class);
                nodeToOrganigrama(arrel, organigrama);
            }
            return organigrama;
        } catch (Exception ex) {
            throw new SistemaExternException("No s'ha pogut consultar l'organigrama de unitats organitzatives via REST (codiEntitat=" + codi + ")", ex);
        }
    }

    @Override
    public List<UnitatOrganitzativa> findAmbPare(String pareCodi) throws SistemaExternException {

        try {
        	Dir3CaibObtenerUnidadesWs service = getObtenerUnidadesService();
            UnidadTF unidadPare = service.obtenerUnidad(pareCodi, null, null);
            if (unidadPare == null) {
                throw new SistemaExternException("No s'han trobat la unitat pare (pareCodi=" + pareCodi + ")");
            }
            List<UnitatOrganitzativa> unitats = new ArrayList<UnitatOrganitzativa>();
            
            List<UnidadTF> unidades = getObtenerUnidadesService().obtenerArbolUnidades(pareCodi, null, null);
            
            
            if (unidades == null) {
                unitats.add(toUnitatOrganitzativa(unidadPare));
                return unitats;
            }
            unidades.add(0, unidadPare);
            for (UnidadTF unidad : unidades) {
                if ("V".equalsIgnoreCase(unidad.getCodigoEstadoEntidad())) {
                    unitats.add(toUnitatOrganitzativa(unidad));
                }
            }
            return unitats;
        } catch (Exception ex) {
            throw new SistemaExternException("No s'han pogut consultar les unitats organitzatives via WS (pareCodi=" + pareCodi + ")", ex);
        }
    }

    @Override
    public List<UnitatOrganitzativa> findAmbPare(
            String pareCodi,
            Date dataActualitzacio,
            Date dataSincronitzacio) throws SistemaExternException {
        try {
            List<UnitatOrganitzativa> unitats = new ArrayList<UnitatOrganitzativa>();

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
			List<UnidadRest> unidades = getUnitatsOrganitzativesRestClient().obtenerArbolUnidades(
					pareCodi,
					dataActualitzacio != null ? dateFormat.format(dataActualitzacio) : null,
					dataSincronitzacio != null ? dateFormat.format(dataSincronitzacio) : null);

            if (unidades != null) {
                for (UnidadRest unidad : unidades) {
                    unitats.add(toUnitatOrganitzativa(unidad));
                }
            }
            return unitats;
        } catch (Exception ex) {
            throw new SistemaExternException("No s'han pogut consultar les unitats organitzatives via WS ("
                    + "pareCodi=" + pareCodi + ")", ex);
        }
    }

    @Override
    public UnitatOrganitzativa findAmbCodi(String codi) throws SistemaExternException {

        try {
            UnidadTF unidad = getObtenerUnidadesService().obtenerUnidad(codi, null, null);
            if (unidad == null || !"V".equalsIgnoreCase(unidad.getCodigoEstadoEntidad())) {
                throw new SistemaExternException("La unitat organitzativa no està vigent (" + "codi=" + codi + ")");
            }
            return toUnitatOrganitzativa(unidad);
        } catch (SistemaExternException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new SistemaExternException("No s'ha pogut consultar la unitat organitzativa (" + "codi=" + codi + ")", ex);
        }
    }

    @Override
    public UnitatOrganitzativa findAmbCodi(
            String pareCodi,
            Date dataActualitzacio,
            Date dataSincronitzacio) throws MalformedURLException {

            UnidadTF unidad = getObtenerUnidadesService().obtenerUnidad(
                    pareCodi,
                    dataActualitzacio != null ? new Timestamp(dataActualitzacio.getTime()) : null,
                    dataSincronitzacio != null ? new Timestamp(dataSincronitzacio.getTime()) : null);
            return unidad != null ? toUnitatOrganitzativa(unidad) : null;
    }

    public List<UnitatOrganitzativa> cercaUnitats(String codi, String denominacio, Long nivellAdministracio,
                                                  Long comunitatAutonoma, Boolean ambOficines,
                                                  Boolean esUnitatArrel, Long provincia,
                                                  String municipi) throws SistemaExternException {

        List<UnitatOrganitzativa> unitats = new ArrayList<>();
        try {
            URL url = new URL(getServiceCercaUrl() + "?codigo=" + codi + "&denominacion=" + denominacio
                    + "&codNivelAdministracion=" + (nivellAdministracio != null ? nivellAdministracio : "-1")
                    + "&codComunidadAutonoma=" + (comunitatAutonoma != null ? comunitatAutonoma : "-1")
                    + "&conOficinas=" + (ambOficines != null && ambOficines ? "true" : "false")
                    + "&unidadRaiz=" + (esUnitatArrel != null && esUnitatArrel ? "true" : "false")
                    + "&provincia=" + (provincia != null ? provincia : "-1") + "&localidad="
                    + (municipi != null ? municipi : "-1") + "&vigentes=true");
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            unitats = mapper.readValue(httpConnection.getInputStream(), TypeFactory.defaultInstance().constructCollectionType(List.class, UnitatOrganitzativa.class));
            Collections.sort(unitats);
            return unitats;
        } catch (JsonMappingException e) {
            // No results
        } catch (Exception ex) {
            throw new SistemaExternException("No s'han pogut consultar les unitats organitzatives via REST ("
                    + "codi=" + codi + ", " + "denominacio=" + denominacio + ", " + "nivellAdministracio="
                    + nivellAdministracio + ", " + "comunitatAutonoma=" + comunitatAutonoma + ", "
                    + "ambOficines=" + ambOficines + ", " + "esUnitatArrel=" + esUnitatArrel + ", "
                    + "provincia=" + provincia + ", " + "municipi=" + municipi + ")", ex);
        }
        return unitats;
    }


    // Mètodes SOAP per sincronització
    // //////////////////////////////////////////////////////////////////////////////////////

    @Override
    public UnitatOrganitzativa findUnidad(
            String pareCodi,
            Timestamp fechaActualizacion,
            Timestamp fechaSincronizacion) throws MalformedURLException {

        UnidadTF unidad = getObtenerUnidadesService().obtenerUnidad(
                pareCodi,
                fechaActualizacion,
                fechaSincronizacion);
        if (unidad != null) {
            return toUnitatOrganitzativa(unidad);
        } else {
            return null;
        }

    }

    @Override
    public List<UnitatOrganitzativa> findAmbPare(
            String pareCodi,
            Timestamp fechaActualizacion,
            Timestamp fechaSincronizacion) throws SistemaExternException {
        try {
            List<UnitatOrganitzativa> unitatOrganitzativa = new ArrayList<UnitatOrganitzativa>();
            List<UnidadTF> arbol = getObtenerUnidadesService().obtenerArbolUnidades(
                    pareCodi,
                    fechaActualizacion,
                    fechaSincronizacion);

            for(UnidadTF unidadTF: arbol){
                unitatOrganitzativa.add(toUnitatOrganitzativa(unidadTF));
            }
            return unitatOrganitzativa;
        } catch (Exception ex) {
            throw new SistemaExternException(
                    "No s'han pogut consultar les unitats organitzatives via WS (pareCodi=" + pareCodi + ")",
                    ex);
        }
    }



    private Dir3CaibObtenerUnidadesWs getObtenerUnidadesService() throws MalformedURLException {

        Dir3CaibObtenerUnidadesWs client = null;
        String urlServei = getServiceUrl() + SERVEI_OBTENIR_UNITATS;
        URL url = new URL(urlServei + "?wsdl");
        Dir3CaibObtenerUnidadesWsService service = new Dir3CaibObtenerUnidadesWsService(url,
                new QName("http://unidad.ws.dir3caib.caib.es/", "Dir3CaibObtenerUnidadesWsService"));
        client = service.getDir3CaibObtenerUnidadesWs();
        BindingProvider bp = (BindingProvider) client;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, urlServei);
        String username = getServiceUsername();
        if (username != null && !username.isEmpty()) {
            bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
            bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, getServicePassword());
        }
        if (isLogMissatgesActiu()) {
            @SuppressWarnings("rawtypes")
            List<Handler> handlerChain = new ArrayList<Handler>();
            handlerChain.add(new LogMessageHandler());
            bp.getBinding().setHandlerChain(handlerChain);
        }
        Integer connectTimeout = getServiceTimeout();
        if (connectTimeout != null) {
            bp.getRequestContext().put("org.jboss.ws.timeout", connectTimeout);
        }
        return client;
    }
    

	
	private UnitatsOrganitzativesRestClient getUnitatsOrganitzativesRestClient() {
		UnitatsOrganitzativesRestClient unitatsOrganitzativesRestClient = new UnitatsOrganitzativesRestClient(
				getServiceUrl(),
				getServiceUsername(),
				getServicePassword());

		return unitatsOrganitzativesRestClient;
	}
	
    
	
    private UnitatOrganitzativa toUnitatOrganitzativa(UnidadRest unidad) {
        UnitatOrganitzativa unitat = UnitatOrganitzativa.builder()
                .codi(unidad.getCodigo())
                .denominacio(unidad.getDenominacion())
                .nifCif(unidad.getCodigo())
                .dataCreacioOficial(unidad.getFechaAltaOficial())
                .estat(unidad.getCodigoEstadoEntidad())
                .codiUnitatSuperior(unidad.getCodUnidadSuperior())
                .codiUnitatArrel(unidad.getCodUnidadRaiz())
                .codiPais(unidad.getCodigoAmbPais() != null ? unidad.getCodigoAmbPais().toString() : "")
                .codiComunitat(unidad.getCodAmbComunidad() != null ? unidad.getCodAmbComunidad().toString() : "")
                .codiProvincia(unidad.getCodAmbProvincia() != null ? unidad.getCodAmbProvincia().toString() : "")
                .codiPostal(unidad.getCodPostal())
                .nomLocalitat(unidad.getDescripcionLocalidad())
                .tipusVia(unidad.getCodigoTipoVia())
                .nomVia(unidad.getNombreVia())
                .numVia(unidad.getNumVia())
                .historicosUO(unidad.getHistoricosUO())
                .nifCif(unidad.getNifCif())
                .build();

        return unitat;
    }
	

    private UnitatOrganitzativa toUnitatOrganitzativa(UnidadTF unidad) {
        UnitatOrganitzativa unitat = UnitatOrganitzativa.builder()
                .codi(unidad.getCodigo())
                .denominacio(unidad.getDenominacion())
                .nifCif(unidad.getCodigo())
                .dataCreacioOficial(unidad.getFechaAltaOficial())
                .estat(unidad.getCodigoEstadoEntidad())
                .codiUnitatSuperior(unidad.getCodUnidadSuperior())
                .codiUnitatArrel(unidad.getCodUnidadRaiz())
                .codiPais(unidad.getCodigoAmbPais() != null ? unidad.getCodigoAmbPais().toString() : "")
                .codiComunitat(unidad.getCodAmbComunidad() != null ? unidad.getCodAmbComunidad().toString() : "")
                .codiProvincia(unidad.getCodAmbProvincia() != null ? unidad.getCodAmbProvincia().toString() : "")
                .codiPostal(unidad.getCodPostal())
                .nomLocalitat(unidad.getDescripcionLocalidad())
                .tipusVia(unidad.getCodigoTipoVia())
                .nomVia(unidad.getNombreVia())
                .numVia(unidad.getNumVia())
                .historicosUO(unidad.getHistoricosUO())
                .build();

        return unitat;
    }

    private class LogMessageHandler implements SOAPHandler<SOAPMessageContext> {
        public boolean handleMessage(SOAPMessageContext messageContext) {
            log(messageContext);
            return true;
        }

        public Set<QName> getHeaders() {
            return Collections.emptySet();
        }

        public boolean handleFault(SOAPMessageContext messageContext) {
            log(messageContext);
            return true;
        }

        public void close(MessageContext context) {
        }

        private void log(SOAPMessageContext messageContext) {

            try {
                SOAPMessage msg = messageContext.getMessage();
                Boolean outboundProperty = (Boolean) messageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
                System.out.print(outboundProperty ? "Missatge SOAP petició: " : "Missatge SOAP resposta: ");
                msg.writeTo(System.out);
                System.out.println();
            } catch (SOAPException ex) {
                Logger.getLogger(LogMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(LogMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private String getServiceUrl() {

        String url = getProperty("plugin.unitats.organitzatives.dir3.service.url");
		if (!url.endsWith("/")) {
			url = url + "/";
		}
        return url;
    }

    private String getServiceUsername() {
        return getProperty("plugin.unitats.organitzatives.dir3.service.username");
    }

    private String getServicePassword() {
        return getProperty("plugin.unitats.organitzatives.dir3.service.password");
    }

    private boolean isLogMissatgesActiu() {
        return getAsBoolean("plugin.unitats.organitzatives.dir3.service.log.actiu");
    }

    private Integer getServiceTimeout() {

        String key = "plugin.unitats.organitzatives.dir3.service.timeout";
        return getProperty(key) != null ? getAsInt(key) : null;
    }

    private String getServiceCercaUrl() {

        String serviceUrl = getProperty("plugin.unitats.organitzatives.dir3.consulta.rest.service.url");
        if (serviceUrl == null) {
            serviceUrl = PropertiesHelper.getProperties()
                    .getProperty("es.caib.ripea.plugin.unitats.cerca.dir3.service.url");
        }
        return serviceUrl;

    }

    private void nodeToOrganigrama(NodeDir3 unitat, Map<String, NodeDir3> organigrama) {

        organigrama.put(unitat.getCodi(), unitat);
        if (unitat.getFills() == null) {
            return;
        }
        for (NodeDir3 fill : unitat.getFills()) {
            nodeToOrganigrama(fill, organigrama);
        }
    }
}
