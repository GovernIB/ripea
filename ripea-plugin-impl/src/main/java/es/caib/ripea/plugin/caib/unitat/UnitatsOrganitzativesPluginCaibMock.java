/**
 * 
 */
package es.caib.ripea.plugin.caib.unitat;

import es.caib.ripea.plugin.RipeaAbstractPluginProperties;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.unitat.NodeDir3;
import es.caib.ripea.plugin.unitat.UnitatOrganitzativa;
import es.caib.ripea.plugin.unitat.UnitatsOrganitzativesPlugin;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
	public List<UnitatOrganitzativa> findAmbPare(String pareCodi, Date dataActualitzacio, Date dataSincronitzacio) throws SistemaExternException {
		return findAmbPare(pareCodi);
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
}
