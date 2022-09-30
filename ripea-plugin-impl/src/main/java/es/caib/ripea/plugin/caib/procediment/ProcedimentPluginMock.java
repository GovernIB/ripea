package es.caib.ripea.plugin.caib.procediment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import es.caib.ripea.core.api.dto.ProcedimentDto;
import es.caib.ripea.plugin.RipeaAbstractPluginProperties;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.caib.unitat.UnitatsOrganitzativesPluginMock;
import es.caib.ripea.plugin.procediment.ProcedimentPlugin;


public class ProcedimentPluginMock extends RipeaAbstractPluginProperties implements ProcedimentPlugin {

	public static int callMethodCounter = 0;
	
	public static List<String> organsCodis = Arrays.asList(UnitatsOrganitzativesPluginMock.CODI_UNITAT_TO_SPLIT, UnitatsOrganitzativesPluginMock.CODI_UNITAT_TO_MERGE1, UnitatsOrganitzativesPluginMock.CODI_UNITAT_TO_MERGE2, UnitatsOrganitzativesPluginMock.CODI_UNITAT_TO_SUBSTITUTE);
	
	public Map<String, String> usedCodisSias = new HashMap<String, String>();
	public static boolean mockStateAfterRolsacSyncronization = false;
	
	public static String forceReturnedCodiDir3 = null;//forceReturnedCodiDir3="A00000111";
	
	@Override
	public ProcedimentDto findAmbCodiSia(
			String codiDir3, 
			String codiSia) throws SistemaExternException {
		
		
		if (!codiDir3.equals(UnitatsOrganitzativesPluginMock.CODI_UNITAT_ARREL)) {
			throw new RuntimeException("Use or create entitat with codiDir3=" + UnitatsOrganitzativesPluginMock.CODI_UNITAT_ARREL);
		}
		
		ProcedimentDto dto = new ProcedimentDto();
		dto.setCodi("codi " + codiSia);
		dto.setCodiSia(codiSia);
		dto.setNom("Nom del procediment amb codi SIA: " + codiSia);
		dto.setResum("Resumen del procediment amb codi SIA: " + codiSia);
		
		dto.setComu(getUnitatOrganitzativaCodi(false, codiSia) == null);
		dto.setUnitatOrganitzativaCodi(getUnitatOrganitzativaCodi(true, codiSia));


		return dto;
	}
	
	private String getUnitatOrganitzativaCodi(boolean increment, String codiSia) {

		if (forceReturnedCodiDir3 != null) {
			return forceReturnedCodiDir3;
		} else {
			
			if (!mockStateAfterRolsacSyncronization) {//mockStateAfterRolsacSyncronization=true;
			
				if (usedCodisSias.containsKey(codiSia)) {
					return usedCodisSias.get(codiSia);
				} else {
					if (callMethodCounter < organsCodis.size()) {
						 
						String codi = organsCodis.get(callMethodCounter);
						if (increment) {
							callMethodCounter++;
							usedCodisSias.put(codiSia, codi);
						}
						
						return codi;
					} else {
						return null;
					}
				}
			} else {
				if (usedCodisSias.containsKey(codiSia)) {
					return getCodiAfterTransformation(usedCodisSias.get(codiSia));
				} else {
					throw new RuntimeException("Can't mock rolsac synchronization. Codi SIA doesn't exist in memory.");
				}
			}
		}
	}
	
	private String getCodiAfterTransformation(String codiDir3) {
		if (codiDir3.equals(UnitatsOrganitzativesPluginMock.CODI_UNITAT_TO_SPLIT)) {
			codiDir3 = UnitatsOrganitzativesPluginMock.CODI_UNITAT_SPLITTED1;
		} else if (codiDir3.equals(UnitatsOrganitzativesPluginMock.CODI_UNITAT_TO_MERGE1) || (codiDir3.equals(UnitatsOrganitzativesPluginMock.CODI_UNITAT_TO_MERGE2))) {
			codiDir3 = UnitatsOrganitzativesPluginMock.CODI_UNITAT_MERGED;
		} else if (codiDir3.equals(UnitatsOrganitzativesPluginMock.CODI_UNITAT_TO_SUBSTITUTE)) {
			codiDir3 = UnitatsOrganitzativesPluginMock.CODI_UNITAT_SUBSTITUTED;
		}
		return codiDir3;
	}
	
	
	@Override
	public String getUnitatAdministrativa(String codi) throws SistemaExternException {
		throw new RuntimeException("Method not supported");
	}

	public ProcedimentPluginMock() {
		super();
	}
	public ProcedimentPluginMock(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}
	

}
