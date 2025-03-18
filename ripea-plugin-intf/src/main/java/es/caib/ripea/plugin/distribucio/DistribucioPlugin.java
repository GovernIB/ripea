package es.caib.ripea.plugin.distribucio;

import es.caib.ripea.plugin.RipeaEndpointPluginInfo;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreEntrada;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId;
import es.caib.distribucio.rest.client.integracio.domini.Estat;

public interface DistribucioPlugin extends RipeaEndpointPluginInfo {

	public AnotacioRegistreEntrada consulta(AnotacioRegistreId anotacioRegistreId) throws SistemaExternException;
	
	public void canviEstat(AnotacioRegistreId anotacioRegistreId, Estat estat, String observacions) throws SistemaExternException;
}