/**
 * 
 */
package es.caib.ripea.plugin.caib.unitat;

import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import es.caib.ripea.plugin.RipeaAbstractPluginProperties;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.unitat.NodeDir3;
import es.caib.ripea.plugin.unitat.UnitatOrganitzativa;
import es.caib.ripea.plugin.unitat.UnitatsOrganitzativesPlugin;


public class UnitatsOrganitzativesPluginMock extends RipeaAbstractPluginProperties implements UnitatsOrganitzativesPlugin {

	public static final String CODI_UNITAT_ARREL = "A00000000";
	public static final String CODI_UNITAT_SUPERIOR = "A00000001";
	
	//SPLIT
	public static final String CODI_UNITAT_TO_SPLIT = "A00000002";
	public static final String CODI_UNITAT_SPLIT1 = "A99999901";
	public static final String CODI_UNITAT_SPLIT2 = "A99999902";
	
	//MERGE
	public static final String CODI_UNITAT_TO_MERGE1 = "A00000003";
	public static final String CODI_UNITAT_TO_MERGE2 = "A00000004";
	public static final String CODI_UNITAT_MERGE = "A99999903";
	
	//SUBSTITUTION
	public static final String CODI_UNITAT_TO_SUBSTITUTE = "A00000005";
	public static final String CODI_UNITAT_SUNSTITUTE = "A99999904";
	
	//CUMULATIVE CHANGES
	public static final String CODI_UNITAT_TO_CUMULATIVE_CHANGES = "A00000006";
	public static final String CODI_UNITAT_CUMULATIVE_CHANGES1 = "A99999905";
	public static final String CODI_UNITAT_CUMULATIVE_CHANGES2 = "A99999906";
	
	//PROPS CHANGED
	public static final String CODI_UNITAT_TO_PROPS_CHANGED = "A00000007";

	//NEW
	public static final String CODI_UNITAT_NEW1 = "A99999907";
	public static final String CODI_UNITAT_NEW2 = "A99999908";

	//script to clear entitat
//	delete from ipa_metaexpedient where entitat_id = (select id from ipa_entitat where unitat_arrel = 'A00000000');
//	delete from ipa_og_sinc_rel where antic_og in (select id from ipa_organ_gestor where entitat_id = (select id from ipa_entitat where unitat_arrel = 'A00000000')) or nou_og in (select id from ipa_organ_gestor where entitat_id = (select id from ipa_entitat where unitat_arrel = 'A00000000'));
//	delete from ipa_organ_gestor where entitat_id = (select id from ipa_entitat where unitat_arrel = 'A00000000');
//	update ipa_entitat set data_sincronitzacio = null, data_actualitzacio = null where id = (select id from ipa_entitat where unitat_arrel = 'A00000000');

	
	@Override
	public List<UnitatOrganitzativa> findAmbPare(String pareCodi, Date dataActualitzacio, Date dataSincronitzacio) throws SistemaExternException {
		
		if (!pareCodi.equals(CODI_UNITAT_ARREL)) {
			throw new RuntimeException("Use or create entitat with codiDir3=" + CODI_UNITAT_ARREL);
		}
		
		List<UnitatOrganitzativa> unitats = new ArrayList<>();
		
		if (dataActualitzacio == null && dataSincronitzacio == null) {
			
			//NEW
			unitats.add(new UnitatOrganitzativa(CODI_UNITAT_ARREL, name(CODI_UNITAT_ARREL), null, null,"V", null));
			unitats.add(new UnitatOrganitzativa(CODI_UNITAT_SUPERIOR, name(CODI_UNITAT_SUPERIOR), CODI_UNITAT_ARREL, CODI_UNITAT_ARREL,"V", null));
			unitats.add(new UnitatOrganitzativa(CODI_UNITAT_TO_SPLIT, name(CODI_UNITAT_TO_SPLIT), CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", null));
			unitats.add(new UnitatOrganitzativa(CODI_UNITAT_TO_MERGE1, name(CODI_UNITAT_TO_MERGE1), CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", null));
			unitats.add(new UnitatOrganitzativa(CODI_UNITAT_TO_MERGE2, name(CODI_UNITAT_TO_MERGE2), CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", null));
			unitats.add(new UnitatOrganitzativa(CODI_UNITAT_TO_SUBSTITUTE, name(CODI_UNITAT_TO_SUBSTITUTE), CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", null));
			unitats.add(new UnitatOrganitzativa(CODI_UNITAT_TO_CUMULATIVE_CHANGES, name(CODI_UNITAT_TO_CUMULATIVE_CHANGES), CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", null));
			unitats.add(new UnitatOrganitzativa(CODI_UNITAT_TO_PROPS_CHANGED, name(CODI_UNITAT_TO_PROPS_CHANGED), CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", null));

		} else {
			
			//SPLIT
			unitats.add(new UnitatOrganitzativa(CODI_UNITAT_TO_SPLIT, name(CODI_UNITAT_TO_SPLIT), CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"E", new ArrayList<>(Arrays.asList(CODI_UNITAT_SPLIT1, CODI_UNITAT_SPLIT2))));
			unitats.add(new UnitatOrganitzativa(CODI_UNITAT_SPLIT1, name(CODI_UNITAT_SPLIT1), CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", null));
			unitats.add(new UnitatOrganitzativa(CODI_UNITAT_SPLIT2, name(CODI_UNITAT_SPLIT2), CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", null));
			
			//MERGE
			unitats.add(new UnitatOrganitzativa(CODI_UNITAT_TO_MERGE1, name(CODI_UNITAT_TO_MERGE1), CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"E", new ArrayList<>(Arrays.asList(CODI_UNITAT_MERGE))));
			unitats.add(new UnitatOrganitzativa(CODI_UNITAT_TO_MERGE2, name(CODI_UNITAT_TO_MERGE2), CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"E", new ArrayList<>(Arrays.asList(CODI_UNITAT_MERGE))));
			unitats.add(new UnitatOrganitzativa(CODI_UNITAT_MERGE, name(CODI_UNITAT_MERGE), CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", null));
			
			//SUBSTITUTION
			unitats.add(new UnitatOrganitzativa(CODI_UNITAT_TO_SUBSTITUTE, name(CODI_UNITAT_TO_SUBSTITUTE), CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"E", new ArrayList<>(Arrays.asList(CODI_UNITAT_SUNSTITUTE))));
			unitats.add(new UnitatOrganitzativa(CODI_UNITAT_SUNSTITUTE, name(CODI_UNITAT_SUNSTITUTE), CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", null));
			
			//CUMULATIVE CHANGES
			unitats.add(new UnitatOrganitzativa(CODI_UNITAT_TO_CUMULATIVE_CHANGES, name(CODI_UNITAT_TO_CUMULATIVE_CHANGES), CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"E", new ArrayList<>(Arrays.asList(CODI_UNITAT_CUMULATIVE_CHANGES1))));
			unitats.add(new UnitatOrganitzativa(CODI_UNITAT_CUMULATIVE_CHANGES1, name(CODI_UNITAT_CUMULATIVE_CHANGES1), CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"E", new ArrayList<>(Arrays.asList(CODI_UNITAT_CUMULATIVE_CHANGES2))));
			unitats.add(new UnitatOrganitzativa(CODI_UNITAT_CUMULATIVE_CHANGES2, name(CODI_UNITAT_CUMULATIVE_CHANGES2), CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", null));
			
			//PROPS CHANGED
			unitats.add(new UnitatOrganitzativa(CODI_UNITAT_TO_PROPS_CHANGED, name(CODI_UNITAT_TO_PROPS_CHANGED), CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", new ArrayList<String>()));
			
			//NEW
			unitats.add(new UnitatOrganitzativa(CODI_UNITAT_NEW1, name(CODI_UNITAT_NEW1), CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", null));
			unitats.add(new UnitatOrganitzativa(CODI_UNITAT_NEW2, name(CODI_UNITAT_NEW2), CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", null));
			
			
		}

		return unitats;
		
		
	}

	@Override
	public UnitatOrganitzativa findAmbCodi(
			String codi) throws SistemaExternException {
		throw new RuntimeException("Method not supported");
	}

	@Override
	public UnitatOrganitzativa findAmbCodi(String pareCodi, Date dataActualitzacio, Date dataSincronitzacio) throws MalformedURLException {
		throw new RuntimeException("Method not supported");
	}


	@Override
	public List<UnitatOrganitzativa> cercaUnitats(
			String codiUnitat,
			String denominacioUnitat,
			Long codiNivellAdministracio,
			Long codiComunitat,
			Boolean ambOficines,
			Boolean esUnitatArrel,
			Long codiProvincia,
			String codiLocalitat) throws SistemaExternException {
		throw new RuntimeException("Method not supported");
	}

	@Override
	public Map<String, NodeDir3> organigrama(String codiEntitat) throws SistemaExternException {
		throw new RuntimeException("Method not supported");
	}

	@Override
	public UnitatOrganitzativa findUnidad(
			String pareCodi,
			Timestamp fechaActualizacion,
			Timestamp fechaSincronizacion) throws MalformedURLException {
		throw new RuntimeException("Method not supported");
	}

	@Override
	public List<UnitatOrganitzativa> findAmbPare(
			String pareCodi,
			Timestamp fechaActualizacion,
			Timestamp fechaSincronizacion) throws SistemaExternException {
		throw new RuntimeException("Method not supported");
	}
	

	@Override
	public List<UnitatOrganitzativa> findAmbPare(
			String pareCodi) throws SistemaExternException {
		throw new RuntimeException("Method not supported");
	}
	
	public String name(String name) {
		String additonal = "";
		if (name.equals(CODI_UNITAT_TO_SPLIT)) {
			additonal = "to split";
		} else if (name.equals(CODI_UNITAT_SPLIT1) || (name.equals(CODI_UNITAT_SPLIT2))) {
			additonal = "splitted";			
		} else if (name.equals(CODI_UNITAT_TO_MERGE1) || (name.equals(CODI_UNITAT_TO_MERGE2))) {
			additonal = "to merge";
		} else if (name.equals(CODI_UNITAT_MERGE)) {
			additonal = "merged";
		} else if (name.equals(CODI_UNITAT_TO_SUBSTITUTE)) {
			additonal = "to substitute";
		} else if (name.equals(CODI_UNITAT_SUNSTITUTE)) {
			additonal = "substituted";
		} else if (name.equals(CODI_UNITAT_TO_CUMULATIVE_CHANGES)) {
			additonal = "to cumulative changes";
		} else if (name.equals(CODI_UNITAT_CUMULATIVE_CHANGES2)) {
			additonal = "to cumulative changes";
		} else if (name.equals(CODI_UNITAT_TO_PROPS_CHANGED)) {
			additonal = "to props changed";
		} 
		if (!additonal.isEmpty()) {
			additonal = " (" + additonal + ")";
		}
		
		return "Unitat amb codi " + name + additonal;
	}


	public UnitatsOrganitzativesPluginMock() {
		super();
	}
	public UnitatsOrganitzativesPluginMock(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}
}
