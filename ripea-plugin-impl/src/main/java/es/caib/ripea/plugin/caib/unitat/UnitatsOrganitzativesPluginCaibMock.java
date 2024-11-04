package es.caib.ripea.plugin.caib.unitat;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.ripea.plugin.RipeaAbstractPluginProperties;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.unitat.NodeDir3;
import es.caib.ripea.plugin.unitat.UnitatOrganitzativa;
import es.caib.ripea.plugin.unitat.UnitatsOrganitzativesPlugin;
import lombok.SneakyThrows;

/**
 * Implementació de proves del plugin d'unitats organitzatives que
 * consulta una istantània de les unitats de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UnitatsOrganitzativesPluginCaibMock extends RipeaAbstractPluginProperties implements UnitatsOrganitzativesPlugin {

	public UnitatsOrganitzativesPluginCaibMock() {
		super();
	}
	public UnitatsOrganitzativesPluginCaibMock(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UnitatOrganitzativa> findAmbPare(
			String pareCodi) throws SistemaExternException {
		try {
			return (List<UnitatOrganitzativa>)deserialize(
					"/es/caib/ripea/plugin/unitat/ArbreUnitatsCaib.ser");
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar les unitats organitzatives via WS (" +
					"pareCodi=" + pareCodi + ")",
					ex);
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
			List<UnidadRest> unidades = obtenerArbolUnidades(
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
    

	public static String readFileAsString(String file) throws Exception {
		return new String(Files.readAllBytes(Paths.get(file)));
	}
	

	
    
	public List<UnidadRest> obtenerArbolUnidades(String codigo, String fechaActualizacion, String fechaSincronizacion) {
		try {
			
			

	        //doesn't work
	       // String json = new String(Files.readAllBytes(Paths.get(getClass().getResource("/es/caib/ripea/plugin/unitat/changes.json").toURI())));
	        
	        File file = new File("C:\\Users\\Ula\\Desktop\\20230626.json");
	        String json = FileUtils.readFileToString(file, "UTF-8");
	        

			


			
			return getMapper().readValue(json, new TypeReference<List<UnidadRest>>(){});
		} catch (Exception ex) { 
			throw new RuntimeException(ex);
		}
	}
	
	protected ObjectMapper getMapper() {
		return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
    private UnitatOrganitzativa toUnitatOrganitzativa(UnidadRest unidad) {
        UnitatOrganitzativa unitat = UnitatOrganitzativa.builder()
                .codi(unidad.getCodigo())
                .denominacio(unidad.getDenominacion())
                .denominacioCooficial(unidad.getDenominacionCooficial())
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

	@Override
	public UnitatOrganitzativa findAmbCodi(
			String codi) throws SistemaExternException {
		List<UnitatOrganitzativa> unitats = findAmbPare(null);
		for (UnitatOrganitzativa unitat: unitats) {
			if (unitat.getCodi().equals(codi))
				return unitat;
		}
		return null;
	}

	@SneakyThrows
	@Override
	public UnitatOrganitzativa findAmbCodi(String pareCodi, Date dataActualitzacio, Date dataSincronitzacio) throws MalformedURLException {
		return findAmbCodi(pareCodi);
	}

	@Override
	public List<UnitatOrganitzativa> cercaUnitats(String codiUnitat, String denominacioUnitat,
			Long codiNivellAdministracio, Long codiComunitat, Boolean ambOficines, Boolean esUnitatArrel,
			Long codiProvincia, String codiLocalitat) throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	private Object deserialize(String resource) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(
				getClass().getResourceAsStream(resource));
		Object obj = ois.readObject();
		ois.close();
		return obj;
	}

	@Override
	public Map<String, NodeDir3> organigrama(String codiEntitat) throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@SneakyThrows
	@Override
	public UnitatOrganitzativa findUnidad(
			String pareCodi,
			Timestamp fechaActualizacion,
			Timestamp fechaSincronizacion) throws MalformedURLException {
		return findAmbCodi(pareCodi);
	}

	@Override
	public List<UnitatOrganitzativa> findAmbPare(
			String pareCodi,
			Timestamp fechaActualizacion,
			Timestamp fechaSincronizacion) throws SistemaExternException {
		return findAmbPare(pareCodi);
	}
	@Override
	public String getEndpointURL() {
		return "UnitatsOrganitzativesPluginCaibMock";
	}

}