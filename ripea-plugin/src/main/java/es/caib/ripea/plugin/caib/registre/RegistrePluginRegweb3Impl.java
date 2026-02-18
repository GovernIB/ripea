package es.caib.ripea.plugin.caib.registre;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import es.caib.regweb3.ws.api.v3.AsientoRegistralWs;
import es.caib.regweb3.ws.api.v3.DatosInteresadoWs;
import es.caib.regweb3.ws.api.v3.InteresadoWs;
import es.caib.regweb3.ws.api.v3.WsI18NException;
import es.caib.regweb3.ws.api.v3.WsValidationException;
import es.caib.ripea.plugin.registre.RegistreDadesInteressat;
import es.caib.ripea.plugin.registre.RegistreInteressat;
import es.caib.ripea.plugin.registre.RegistreInteressatDocumentTipusEnum;
import es.caib.ripea.plugin.registre.RegistrePlugin;
import es.caib.ripea.plugin.registre.RespostaConsultaRegistre;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegistrePluginRegweb3Impl extends RegistrePluginUtils implements RegistrePlugin {

	public static final String METODE_ASIENTO_REGISTRAL = "RegWebAsientoRegistral";
	
	public RegistrePluginRegweb3Impl() {
		super();
	}
	
	public RegistrePluginRegweb3Impl(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}
	
	@Override
	public RespostaConsultaRegistre obtenerAsientoRegistral(
			String codiDir3Entitat, 
			String numeroRegistre,
			Long tipusOperacio, 
			boolean ambAnnexos) {
		var resposta = new RespostaConsultaRegistre();
		
//		try {
//			log.info("[REGISTRE] Creant assentament registral codiDir3Entitat {} numeroRegistre {} tipusOperacio {} ambAnnexos {}", 
//					codiDir3Entitat,
//					numeroRegistre, 
//					tipusOperacio, 
//					ambAnnexos);
//			
//			var asientoRegistralWs = new AsientoRegistralWs();
//			var interesados = new ArrayList<InteresadoWs>();
//			var interesadoWs = new InteresadoWs();
//			var datosInteresadoWs = new DatosInteresadoWs();
//			datosInteresadoWs.setTipoInteresado(2L);
//			datosInteresadoWs.setRazonSocial("Raó social");
//			datosInteresadoWs.setTipoDocumentoIdentificacion("C");
//			datosInteresadoWs.setDocumento("12345678Z");
//			
//			interesadoWs.setInteresado(datosInteresadoWs);
//			interesados.add(interesadoWs);
//			
//			var interesado2Ws = new InteresadoWs();
//			var datosInteresado2Ws = new DatosInteresadoWs();
//			datosInteresado2Ws.setTipoInteresado(1L);
//			datosInteresado2Ws.setNombre("Nom física");
//			datosInteresado2Ws.setApellido1("Llinatges 1");
//			datosInteresado2Ws.setApellido2("Llinatges 2");
//			datosInteresado2Ws.setTipoDocumentoIdentificacion("N");
//			datosInteresado2Ws.setDocumento("12345679Z");
//
//			interesado2Ws.setInteresado(datosInteresado2Ws);
//			
//			var datosRepersentanteWs = new DatosInteresadoWs();
//			datosRepersentanteWs.setTipoInteresado(2L);
//			datosRepersentanteWs.setRazonSocial("Raó social representant");
//			datosRepersentanteWs.setTipoDocumentoIdentificacion("C");
//			datosRepersentanteWs.setDocumento("12345674Z");
//			
//			interesado2Ws.setRepresentante(datosRepersentanteWs);
//			
//			
//			interesados.add(interesado2Ws);
//			
//			asientoRegistralWs.getInteresados().addAll(interesados);
//			
//			log.info("[REGISTRE] Assentament registral {}", asientoRegistralWs);
//			var respostaConsulta = toRespostaConsultaRegistre(asientoRegistralWs);
//			return respostaConsulta;
//		} catch (Exception e) {
//			log.error("Error no controlat toRespostaConsultaRegistre", e);
//			resposta.setErrorCodi("2");
//			resposta.setErrorDescripcio(e.getMessage());
//			return resposta;
//		}
		
		try {
			log.info("[REGISTRE] Creant assentament registral codiDir3Entitat {} numeroRegistre {} tipusOperacio {} ambAnnexos {}", 
					codiDir3Entitat,
					numeroRegistre, 
					tipusOperacio, 
					ambAnnexos);
			
			var asientoRegistralWs = getAsientoRegistralApi().obtenerAsientoRegistral(
					codiDir3Entitat, 
					numeroRegistre,
					tipusOperacio, 
					ambAnnexos);
			
			log.debug("[REGISTRE] Assentament registral {}", asientoRegistralWs);
			return toRespostaConsultaRegistre(asientoRegistralWs);
		} catch (WsI18NException e) {
			resposta.setErrorCodi("0");
			resposta.setErrorDescripcio(e.getMessage());
			return resposta;
		} catch (WsValidationException e) {
			resposta.setErrorCodi("1");
			resposta.setErrorDescripcio(e.getMessage());
			return resposta;
		} catch (Exception e) {
			log.error("Error no controlat toRespostaConsultaRegistre", e);
			resposta.setErrorCodi("2");
			resposta.setErrorDescripcio(e.getMessage());
			return resposta;
		}
	}

	private RespostaConsultaRegistre toRespostaConsultaRegistre(AsientoRegistralWs asientoRegistralWs) {
		RespostaConsultaRegistre resposta = new RespostaConsultaRegistre();
		List<RegistreDadesInteressat> interessats = toRegistreInteressats(asientoRegistralWs.getInteresados());
		resposta.setInteressats(interessats);
		return resposta;
	}

	private List<RegistreDadesInteressat> toRegistreInteressats(List<InteresadoWs> interesadosWs) {
		List<RegistreDadesInteressat> interessats = new ArrayList<RegistreDadesInteressat>();
		
		for (InteresadoWs interesadoWs : interesadosWs) {
			RegistreDadesInteressat dadesInteressat = new RegistreDadesInteressat();
			
			dadesInteressat.setInteressat(toRegistreInteressats(interesadoWs.getInteresado()));
			
			if (interesadoWs.getRepresentante() != null)
				dadesInteressat.setRepresentant(toRegistreInteressats(interesadoWs.getRepresentante()));
			
			interessats.add(dadesInteressat);
		}
	
		return interessats;
	}
	
	private RegistreInteressat toRegistreInteressats(DatosInteresadoWs datosInteresadoWs) {
		RegistreInteressat interessat = new RegistreInteressat();
		interessat.setTipusInteressat(datosInteresadoWs.getTipoInteresado());
		interessat.setNom(datosInteresadoWs.getNombre() != null ? datosInteresadoWs.getNombre() : datosInteresadoWs.getRazonSocial());
		interessat.setLlinatge1(datosInteresadoWs.getApellido1());
		interessat.setLlinatge2(datosInteresadoWs.getApellido2());
		interessat.setTipusDocumentIdentificacio(RegistreInteressatDocumentTipusEnum.valorAsEnum(datosInteresadoWs.getTipoDocumentoIdentificacion()));
		interessat.setDocumentNum(datosInteresadoWs.getDocumento());
		interessat.setCodiPostal(datosInteresadoWs.getCp());
		interessat.setAdresa(datosInteresadoWs.getDireccion());
		interessat.setEmail(datosInteresadoWs.getEmail());
		interessat.setMunicipi(safeString(datosInteresadoWs.getLocalidad()));
		interessat.setPais(safeString(datosInteresadoWs.getPais()));
		interessat.setProvincia(safeString(datosInteresadoWs.getProvincia()));
		interessat.setTelefon(datosInteresadoWs.getTelefono());
		interessat.setObservacions(datosInteresadoWs.getObservaciones());
		interessat.setEmailHabilitat(datosInteresadoWs.getDireccionElectronica());
//		interessat.setCodiPostal(datosInteresadoWs.getCodigoDire());
		interessat.setCanalPreferent(safeString(datosInteresadoWs.getCanal()));
		
		return interessat;
	}
 	
	private static String safeString(Long value) {
	    return value != null ? String.valueOf(value) : null;
	}

	@Override
	public String getEndpointURL() {
		return getServiceUrl();
	}
	
}
