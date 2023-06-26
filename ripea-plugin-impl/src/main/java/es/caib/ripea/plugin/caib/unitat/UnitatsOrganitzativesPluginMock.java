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
import es.caib.ripea.plugin.caib.procediment.ProcedimentPluginMock;
import es.caib.ripea.plugin.unitat.NodeDir3;
import es.caib.ripea.plugin.unitat.UnitatOrganitzativa;
import es.caib.ripea.plugin.unitat.UnitatsOrganitzativesPlugin;


public class UnitatsOrganitzativesPluginMock extends RipeaAbstractPluginProperties implements UnitatsOrganitzativesPlugin {

	public static final String CODI_UNITAT_ARREL = "A00000000";
	public static final String CODI_UNITAT_SUPERIOR = "A00000001";
	
	//SPLIT
	public static final String CODI_UNITAT_TO_SPLIT = "A00000002";
	public static final String CODI_UNITAT_SPLITTED1 = "A99999901";
	public static final String CODI_UNITAT_SPLITTED2 = "A99999902";
	
	//MERGE
	public static final String CODI_UNITAT_TO_MERGE1 = "A00000003";
	public static final String CODI_UNITAT_TO_MERGE2 = "A00000004";
	public static final String CODI_UNITAT_MERGED = "A99999903";
	
	//SUBSTITUTION
	public static final String CODI_UNITAT_TO_SUBSTITUTE = "A00000005";
	public static final String CODI_UNITAT_SUBSTITUTED = "A99999904";
	
	//CUMULATIVE CHANGES
	public static final String CODI_UNITAT_TO_CUMULATIVE_CHANGES = "A00000006";
	public static final String CODI_UNITAT_CUMULATIVE_CHANGES1 = "A00000007";
	public static final String CODI_UNITAT_CUMULATIVE_CHANGES2 = "A99999906";
	
	//CANVI EN ATRIBUTS
	public static final String CODI_UNITAT_PROPS_CHANGED = "A00000008";

	//NEW
	public static final String CODI_UNITAT_NEW1 = "A99999907";
	
	//EXTINCT
	public static final String CODI_UNITAT_EXTINCT = "A00000009";
	
	//SUBSTITUTION BY ITSLEF
	public static final String CODI_UNITAT_TO_SUBSTITUTE_BY_ITSELF = "A00000010";
	

	//script to clear syncronization
//	delete from ipa_metaexpedient where entitat_id = (select id from ipa_entitat where unitat_arrel = 'A00000000');
//	delete from ipa_og_sinc_rel where antic_og in (select id from ipa_organ_gestor where entitat_id = (select id from ipa_entitat where unitat_arrel = 'A00000000')) or nou_og in (select id from ipa_organ_gestor where entitat_id = (select id from ipa_entitat where unitat_arrel = 'A00000000'));
//	delete from ipa_organ_gestor where entitat_id = (select id from ipa_entitat where unitat_arrel = 'A00000000');
//	update ipa_entitat set data_sincronitzacio = null, data_actualitzacio = null where id = (select id from ipa_entitat where unitat_arrel = 'A00000000');

	
	@Override
	public List<UnitatOrganitzativa> findAmbPare(String pareCodi, Date dataActualitzacio, Date dataSincronitzacio) throws SistemaExternException {
		
		if (!pareCodi.equals(CODI_UNITAT_ARREL)) {
			throw new RuntimeException("To run correctly this mock syncronization use or create entitat with codiDir3=" + CODI_UNITAT_ARREL);
		}
		
		SincronizationIterationEnum sincronizationIterationEnum = null;
		if (dataActualitzacio == null && dataSincronitzacio == null) {
			sincronizationIterationEnum = SincronizationIterationEnum.FIRST;
		} else {
			sincronizationIterationEnum = SincronizationIterationEnum.SECOND;
		}
		
		
		List<UnitatOrganitzativa> unitats = new ArrayList<>();
		if (sincronizationIterationEnum == SincronizationIterationEnum.FIRST) {
			
			//UNITAT ARREL I UNITAT SUPERIOR
			unitats.add(getUnitatOrganitzativa(CODI_UNITAT_ARREL, name(CODI_UNITAT_ARREL), null, null, "V", null));
			unitats.add(getUnitatOrganitzativa(CODI_UNITAT_SUPERIOR, name(CODI_UNITAT_SUPERIOR), CODI_UNITAT_ARREL, CODI_UNITAT_ARREL, "V", null));
			
			ProcedimentPluginMock.secondSyncronization = false;

		} else {
			ProcedimentPluginMock.secondSyncronization = true;
		}
		
	//	standardTest(sincronizationIterationEnum, unitats);

		test20230621(sincronizationIterationEnum, unitats);
//		testMerge3(sincronizationIterationEnum, unitats);
		
		//test20230615(sincronizationIterationEnum, unitats);
		//test20230621OnlyProblematicMerge(sincronizationIterationEnum, unitats);
		return unitats;
		
		
	}
	

	

	
	
	
	private void test20230621(SincronizationIterationEnum sincronizationIterationEnum, List<UnitatOrganitzativa> unitats) {
		
		if (sincronizationIterationEnum == SincronizationIterationEnum.FIRST) {
			
			unitats.add(getUnitatOrganitzativa("A04068484", "V", null));
			unitats.add(getUnitatOrganitzativa("A04032340", "E", null));
			unitats.add(getUnitatOrganitzativa("A04052825", "V", null));
			unitats.add(getUnitatOrganitzativa("A04031599", "E", null));
			unitats.add(getUnitatOrganitzativa("A04032335", "V", null));
			unitats.add(getUnitatOrganitzativa("A04032374", "V", null));
			unitats.add(getUnitatOrganitzativa("A04032343", "V", null));
			unitats.add(getUnitatOrganitzativa("A04031607", "V", null));
			unitats.add(getUnitatOrganitzativa("A04068486", "V", null));
			unitats.add(getUnitatOrganitzativa("A04032342", "E", null));
			unitats.add(getUnitatOrganitzativa("A04052826", "V", null));
			unitats.add(getUnitatOrganitzativa("A04032358", "V", null));
			unitats.add(getUnitatOrganitzativa("A04032359", "V", null));
			

		} else {
			
			unitats.add(getUnitatOrganitzativa("A04068564", "V", null));
			unitats.add(getUnitatOrganitzativa("A04068565", "V", null));
			unitats.add(getUnitatOrganitzativa("A04068484", "E", Arrays.asList("A04068565", "A04068564")));
			unitats.add(getUnitatOrganitzativa("A04032340", "E", Arrays.asList("A04068484")));
			unitats.add(getUnitatOrganitzativa("A04032340", "E", Arrays.asList("A04068484")));
			unitats.add(getUnitatOrganitzativa("A04052825", "E", null));
			unitats.add(getUnitatOrganitzativa("A04031599", "E", Arrays.asList("A04068484")));
			unitats.add(getUnitatOrganitzativa("A04068544", "E", Arrays.asList("A04068548")));
			unitats.add(getUnitatOrganitzativa("A04032335", "V", null));
			unitats.add(getUnitatOrganitzativa("A04032374", "V", null));
			unitats.add(getUnitatOrganitzativa("A04068547", "V", null));
			unitats.add(getUnitatOrganitzativa("A04068545", "V", null));
			unitats.add(getUnitatOrganitzativa("A04068546", "V", null));
			unitats.add(getUnitatOrganitzativa("A04068548", "V", null));
			unitats.add(getUnitatOrganitzativa("A04032343", "E", Arrays.asList("A04068547")));
			unitats.add(getUnitatOrganitzativa("A04031607", "E", Arrays.asList("A04068546", "A04068545")));
			unitats.add(getUnitatOrganitzativa("A04068486", "E", Arrays.asList("A04068548")));
			unitats.add(getUnitatOrganitzativa("A04032335", "E", Arrays.asList("A04032335")));
			unitats.add(getUnitatOrganitzativa("A04032342", "E", Arrays.asList("A04068486")));
			unitats.add(getUnitatOrganitzativa("A04032335", "E", Arrays.asList("A04032335")));
			unitats.add(getUnitatOrganitzativa("A04031607", "E", Arrays.asList("A04031607")));
			unitats.add(getUnitatOrganitzativa("A04032374", "E", Arrays.asList("A04032374")));
			unitats.add(getUnitatOrganitzativa("A04052826", "E", Arrays.asList("A04068547")));
			unitats.add(getUnitatOrganitzativa("A04032358", "E", Arrays.asList("A04032358", "A04068486")));
			unitats.add(getUnitatOrganitzativa("A04032359", "E", Arrays.asList("A04032359", "A04068486")));
			unitats.add(getUnitatOrganitzativa("A04032359", "E", Arrays.asList("A04068544")));
			unitats.add(getUnitatOrganitzativa("A04032358", "E", Arrays.asList("A04068544")));
			
		}
		
	}
	
	
	private void testMerge3(SincronizationIterationEnum sincronizationIterationEnum, List<UnitatOrganitzativa> unitats) {
		
		if (sincronizationIterationEnum == SincronizationIterationEnum.FIRST) {
			
			unitats.add(getUnitatOrganitzativa("TOMERGE1", "V", null));
			unitats.add(getUnitatOrganitzativa("TOMERGE2", "V", null));
			unitats.add(getUnitatOrganitzativa("TOMERGE3", "V", null));
			

		} else {
			
			unitats.add(getUnitatOrganitzativa("TOMERGE1", "E", Arrays.asList("MERGED")));
			unitats.add(getUnitatOrganitzativa("TOMERGE2", "E", Arrays.asList("MERGED")));
			unitats.add(getUnitatOrganitzativa("TOMERGE3", "E", Arrays.asList("MERGED")));
			unitats.add(getUnitatOrganitzativa("MERGED", "V", null));

			
		}
		
	}
	

	
	
	private void test20230621OnlyProblematicMerge(SincronizationIterationEnum sincronizationIterationEnum, List<UnitatOrganitzativa> unitats) {
		
		//1.- FUSIÓ: OK
		//A04032359 
		//A04032358 
		//-A04068544  
		//2.- FUSIÓ: OK
		//A04068544 
		//A04068486 
		//-A04068548 
		
		if (sincronizationIterationEnum == SincronizationIterationEnum.FIRST) {
			
//			unitats.add(getUnitatOrganitzativa("A04068486", "V", null));
//			unitats.add(getUnitatOrganitzativa("A04032358", "E", null));
//			unitats.add(getUnitatOrganitzativa("A04032359", "E", null));
			
			unitats.add(getUnitatOrganitzativa("A04068486", "V", null));
			unitats.add(getUnitatOrganitzativa("A04032358", "V", null));
			unitats.add(getUnitatOrganitzativa("A04032359", "V", null));
			
			// syncronization 20230615
			unitats.add(getUnitatOrganitzativa("A04068486", "V", null));
			unitats.add(getUnitatOrganitzativa("A04032342", "E", Arrays.asList("A04068486")));
			unitats.add(getUnitatOrganitzativa("A04032359", "V", null));
			unitats.add(getUnitatOrganitzativa("A04032358", "V", null));
			unitats.add(getUnitatOrganitzativa("A04032359", "E", Arrays.asList("A04032359", "A04068486")));
			unitats.add(getUnitatOrganitzativa("A04032358", "E", Arrays.asList("A04032358", "A04068486")));
			
			unitats.add(getUnitatOrganitzativa("A04068486", "V", null));
			unitats.add(getUnitatOrganitzativa("A04032358", "V", null));
			unitats.add(getUnitatOrganitzativa("A04032359", "V", null));
			
			
		} else {
			
			unitats.add(getUnitatOrganitzativa("A04068544", "E", Arrays.asList("A04068548")));
			unitats.add(getUnitatOrganitzativa("A04068548", "V", null));
			unitats.add(getUnitatOrganitzativa("A04068486", "E", Arrays.asList("A04068548")));
			unitats.add(getUnitatOrganitzativa("A04032358", "E", Arrays.asList("A04032358", "A04068486")));
			unitats.add(getUnitatOrganitzativa("A04032359", "E", Arrays.asList("A04032359", "A04068486")));
			unitats.add(getUnitatOrganitzativa("A04032359", "E", Arrays.asList("A04068544")));
			unitats.add(getUnitatOrganitzativa("A04032358", "E", Arrays.asList("A04068544")));
			
		}
		
	}
	private void test20230615(SincronizationIterationEnum sincronizationIterationEnum, List<UnitatOrganitzativa> unitats) {
		
		if (sincronizationIterationEnum == SincronizationIterationEnum.FIRST) {
			
			unitats.add(getUnitatOrganitzativa("A04032342", "V", null));
			unitats.add(getUnitatOrganitzativa("A04032358", "V", null));
			unitats.add(getUnitatOrganitzativa("A04032359", "V", null));
		} else {
			
			unitats.add(getUnitatOrganitzativa("A04068486", "V", null));
			unitats.add(getUnitatOrganitzativa("A04032342", "E", Arrays.asList("A04068486")));
			unitats.add(getUnitatOrganitzativa("A04032359", "V", null));
			unitats.add(getUnitatOrganitzativa("A04032358", "V", null));
			unitats.add(getUnitatOrganitzativa("A04032359", "E", Arrays.asList("A04032359", "A04068486")));
			unitats.add(getUnitatOrganitzativa("A04032358", "E", Arrays.asList("A04032358", "A04068486")));
			
		}
		
	}
	
	
	
	
	private void standardTest(SincronizationIterationEnum sincronizationIterationEnum, List<UnitatOrganitzativa> unitats) {
		
		if (sincronizationIterationEnum == SincronizationIterationEnum.FIRST) {
			// NEW
			unitats.add(getUnitatOrganitzativa(CODI_UNITAT_TO_SPLIT, "V", null));
			unitats.add(getUnitatOrganitzativa(CODI_UNITAT_TO_MERGE1, "V", null));
			unitats.add(getUnitatOrganitzativa(CODI_UNITAT_TO_MERGE2, "V", null));
			unitats.add(getUnitatOrganitzativa(CODI_UNITAT_TO_SUBSTITUTE, "V", null));
			unitats.add(getUnitatOrganitzativa(CODI_UNITAT_TO_CUMULATIVE_CHANGES, "V", null));
			unitats.add(getUnitatOrganitzativa(CODI_UNITAT_PROPS_CHANGED, "V", null));
			unitats.add(getUnitatOrganitzativa(CODI_UNITAT_EXTINCT, "V", null));
			unitats.add(getUnitatOrganitzativa(CODI_UNITAT_TO_SUBSTITUTE_BY_ITSELF, "V", null));
		} else {
			
			//SPLIT
			unitats.add(getUnitatOrganitzativa(CODI_UNITAT_TO_SPLIT, "E", Arrays.asList(CODI_UNITAT_SPLITTED1, CODI_UNITAT_SPLITTED2)));
			unitats.add(getUnitatOrganitzativa(CODI_UNITAT_SPLITTED1, "V", null));
			unitats.add(getUnitatOrganitzativa(CODI_UNITAT_SPLITTED2, "V", null));
			
			//MERGE
			unitats.add(getUnitatOrganitzativa(CODI_UNITAT_TO_MERGE1, "E", Arrays.asList(CODI_UNITAT_MERGED)));
			unitats.add(getUnitatOrganitzativa(CODI_UNITAT_TO_MERGE2, "E", Arrays.asList(CODI_UNITAT_MERGED)));
			unitats.add(getUnitatOrganitzativa(CODI_UNITAT_MERGED, "V", null));
			
			//SUBSTITUTION
			unitats.add(getUnitatOrganitzativa(CODI_UNITAT_TO_SUBSTITUTE, "E", Arrays.asList(CODI_UNITAT_SUBSTITUTED)));
			unitats.add(getUnitatOrganitzativa(CODI_UNITAT_SUBSTITUTED, "V", null));
			
			//CUMULATIVE CHANGES
			unitats.add(getUnitatOrganitzativa(CODI_UNITAT_TO_CUMULATIVE_CHANGES, "E", Arrays.asList(CODI_UNITAT_CUMULATIVE_CHANGES1)));
			unitats.add(getUnitatOrganitzativa(CODI_UNITAT_CUMULATIVE_CHANGES1, "E", Arrays.asList(CODI_UNITAT_CUMULATIVE_CHANGES2)));
			unitats.add(getUnitatOrganitzativa(CODI_UNITAT_CUMULATIVE_CHANGES2, "V", null));

			//CANVI EN ATRIBUTS
			unitats.add(getUnitatOrganitzativa(CODI_UNITAT_PROPS_CHANGED, name(CODI_UNITAT_PROPS_CHANGED) + " changed", CODI_UNITAT_SUPERIOR,CODI_UNITAT_ARREL, "V", null));
			
			//NEW
			unitats.add(getUnitatOrganitzativa(CODI_UNITAT_NEW1, "V", null));
			
			//EXTINCT
			unitats.add(getUnitatOrganitzativa(CODI_UNITAT_EXTINCT, "E", null));
			
			//SUBSTITUTION BY ITSLEF
			unitats.add(getUnitatOrganitzativa(CODI_UNITAT_TO_SUBSTITUTE_BY_ITSELF, "E", Arrays.asList(CODI_UNITAT_TO_SUBSTITUTE_BY_ITSELF)));
			unitats.add(getUnitatOrganitzativa(CODI_UNITAT_TO_SUBSTITUTE_BY_ITSELF, name(CODI_UNITAT_TO_SUBSTITUTE_BY_ITSELF) + " substituted", CODI_UNITAT_SUPERIOR,CODI_UNITAT_ARREL, "V", null));
			
			
		}
		
	}

	@Override
	public UnitatOrganitzativa findAmbCodi(
			String codi) throws SistemaExternException {
		return UnitatOrganitzativa.builder().codi(codi).build();
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
		} else if (name.equals(CODI_UNITAT_SPLITTED1) || (name.equals(CODI_UNITAT_SPLITTED2))) {
			additonal = "splitted";			
		} else if (name.equals(CODI_UNITAT_TO_MERGE1) || (name.equals(CODI_UNITAT_TO_MERGE2))) {
			additonal = "to merge";
		} else if (name.equals(CODI_UNITAT_MERGED)) {
			additonal = "merged";
		} else if (name.equals(CODI_UNITAT_TO_SUBSTITUTE)) {
			additonal = "to substitute";
		} else if (name.equals(CODI_UNITAT_SUBSTITUTED)) {
			additonal = "substituted";
		} else if (name.equals(CODI_UNITAT_TO_CUMULATIVE_CHANGES)) {
			additonal = "to cumulative changes";
		} else if (name.equals(CODI_UNITAT_CUMULATIVE_CHANGES2)) {
			additonal = "to cumulative changes";
		} else if (name.equals(CODI_UNITAT_PROPS_CHANGED)) {
			additonal = "props change";
		} else if (name.equals(CODI_UNITAT_EXTINCT)) {
			additonal = "to extinct";
		} else if (name.equals(CODI_UNITAT_NEW1) || name.equals(CODI_UNITAT_NEW1)) {
			additonal = "new";
		} else if (name.equals(CODI_UNITAT_TO_SUBSTITUTE_BY_ITSELF)) {
			additonal = "substitute by itself";
		}  
		
		
		if (!additonal.isEmpty()) {
			additonal = " (" + additonal + ")";
		}
		
		return "Unitat amb codi " + name + additonal;
	}
	
	public String nameCatalan(String name) {
//		return name(name) + " [catalan]";
		return name(name);
	}
	
	private UnitatOrganitzativa getUnitatOrganitzativa(
			String codi,
			String estat,
			List<String> historicosUO) {
		return new UnitatOrganitzativa(
				codi,
				name(codi),
				nameCatalan(codi),
				CODI_UNITAT_SUPERIOR,
				CODI_UNITAT_ARREL,
				estat,
				historicosUO);
	}
	
	private UnitatOrganitzativa getUnitatOrganitzativa(
			String codi,
			String denominacio,
			String codiUnitatSuperior,
			String codiUnitatArrel,
			String estat, 
			List<String> historicosUO) {
		return new UnitatOrganitzativa(
				codi,
				denominacio,
				nameCatalan(codi),
				codiUnitatSuperior,
				codiUnitatArrel,
				estat,
				historicosUO);
	}

	public UnitatsOrganitzativesPluginMock() {
		super();
	}
	public UnitatsOrganitzativesPluginMock(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}
	
	enum SincronizationIterationEnum {
	    FIRST,
	    SECOND;
	}
	
}
