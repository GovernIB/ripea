package es.caib.ripea.plugin.caib.digitalitzacio;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import es.caib.ripea.plugin.RipeaAbstractPluginProperties;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.digitalitzacio.DigitalitzacioEstat;
import es.caib.ripea.plugin.digitalitzacio.DigitalitzacioPerfil;
import es.caib.ripea.plugin.digitalitzacio.DigitalitzacioPlugin;
import es.caib.ripea.plugin.digitalitzacio.DigitalitzacioResultat;
import es.caib.ripea.plugin.digitalitzacio.DigitalitzacioTransaccioResposta;
import es.caib.ripea.service.intf.dto.UsuariDto;

public class DigitalitzacioPluginMock extends RipeaAbstractPluginProperties implements DigitalitzacioPlugin {

	public DigitalitzacioPluginMock() {
		super();
	}
	public DigitalitzacioPluginMock(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}
	
	@Override
	public String getEndpointURL() {
		return "DigitalitzacioPluginMock";
	}

	@Override
	public List<DigitalitzacioPerfil> recuperarPerfilsDisponibles(String idioma) throws SistemaExternException {
		List<DigitalitzacioPerfil> perfilsDisponibles = new ArrayList<DigitalitzacioPerfil>();
		DigitalitzacioPerfil dp1 = new DigitalitzacioPerfil();
		dp1.setCodi("DP1");dp1.setNom("Perfil digitalització 1");dp1.setDescripcio("Descripció del perfil digitalització 1");dp1.setTipus(1);
		DigitalitzacioPerfil dp2 = new DigitalitzacioPerfil();
		dp2.setCodi("DP2");dp2.setNom("Perfil digitalització 2");dp2.setDescripcio("Descripció del perfil digitalització 2");dp2.setTipus(2);
		DigitalitzacioPerfil dp3 = new DigitalitzacioPerfil();
		dp3.setCodi("DP3");dp3.setNom("Perfil digitalització 3");dp3.setDescripcio("Descripció del perfil digitalització 3");dp3.setTipus(3);
		DigitalitzacioPerfil dp4 = new DigitalitzacioPerfil();
		dp4.setCodi("DP4");dp4.setNom("Perfil digitalització 4");dp4.setDescripcio("Descripció del perfil digitalització 4");dp4.setTipus(4);
		DigitalitzacioPerfil dp5 = new DigitalitzacioPerfil();
		dp5.setCodi("DP5");dp5.setNom("Perfil digitalització 5");dp5.setDescripcio("Descripció del perfil digitalització 5");dp5.setTipus(5);
		perfilsDisponibles.add(dp1);
		perfilsDisponibles.add(dp2);
		perfilsDisponibles.add(dp3);
		perfilsDisponibles.add(dp4);
		perfilsDisponibles.add(dp5);
		return perfilsDisponibles;
	}

	@Override
	public DigitalitzacioTransaccioResposta iniciarProces(String codiPerfil, String idioma, UsuariDto funcionari, String returnUrl) throws SistemaExternException {
		DigitalitzacioTransaccioResposta resposta = new DigitalitzacioTransaccioResposta();
		String idTransaccio = String.valueOf(System.currentTimeMillis());
		resposta.setIdTransaccio(idTransaccio);
		resposta.setReturnScannedFile(true);
		resposta.setReturnSignedFile(false);
		String[] partes = returnUrl.split("/");
		String ultimaParte = partes[partes.length - 1];
		resposta.setUrlRedireccio("http://localhost:8080/ripeaback/modal/digitalitzacio/mock?idExpedient="+ultimaParte+"&idTransaccio="+idTransaccio);
		return resposta;
	}

	@Override
	public DigitalitzacioResultat recuperarResultat(String idTransaccio, boolean returnScannedFile, boolean returnSignedFile) throws SistemaExternException {
		DigitalitzacioResultat resultat = new DigitalitzacioResultat();
		resultat.setError(false);
		resultat.setEstat(DigitalitzacioEstat.FINAL_OK);
		resultat.setMimeType("text/plain");
		resultat.setContingut("Hola, simulo ser un fichero escaneado.".getBytes());
		resultat.setIdioma("es");
		resultat.setNomDocument("ScannedFile.txt");
		resultat.setResolucion(600);
		return resultat;
	}

	@Override
	public void tancarTransaccio(String idTransaccio) throws SistemaExternException {}
}