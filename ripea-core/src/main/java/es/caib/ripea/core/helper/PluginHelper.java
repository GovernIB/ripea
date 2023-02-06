package es.caib.ripea.core.helper;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
//import org.fundaciobit.pluginsib.validatecertificate.InformacioCertificat;
import org.fundaciobit.plugins.certificate.InformacioCertificat;
import org.fundaciobit.plugins.validatesignature.api.IValidateSignaturePlugin;
import org.fundaciobit.plugins.validatesignature.api.SignatureDetailInfo;
import org.fundaciobit.plugins.validatesignature.api.SignatureRequestedInformation;
import org.fundaciobit.plugins.validatesignature.api.TimeStampInfo;
import org.fundaciobit.plugins.validatesignature.api.ValidateSignatureRequest;
import org.fundaciobit.plugins.validatesignature.api.ValidateSignatureResponse;
import org.fundaciobit.plugins.validatesignature.api.ValidationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.tool.xml.Experimental;

import es.caib.plugins.arxiu.api.Carpeta;
import es.caib.plugins.arxiu.api.ConsultaFiltre;
import es.caib.plugins.arxiu.api.ConsultaOperacio;
import es.caib.plugins.arxiu.api.ConsultaResultat;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.DocumentEstat;
import es.caib.plugins.arxiu.api.DocumentExtensio;
import es.caib.plugins.arxiu.api.DocumentFormat;
import es.caib.plugins.arxiu.api.DocumentMetadades;
import es.caib.plugins.arxiu.api.DocumentTipusAddicional;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.plugins.arxiu.api.ExpedientEstat;
import es.caib.plugins.arxiu.api.ExpedientMetadades;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.FirmaTipus;
import es.caib.plugins.arxiu.api.IArxiuPlugin;
import es.caib.plugins.arxiu.caib.ArxiuConversioHelper;
import es.caib.ripea.core.api.dto.ArbreDto;
import es.caib.ripea.core.api.dto.ArbreNodeDto;
import es.caib.ripea.core.api.dto.ArxiuEstatEnumDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaDetallDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaPerfilEnumDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaTipusEnumDto;
import es.caib.ripea.core.api.dto.ArxiuOperacioEnumDto;
import es.caib.ripea.core.api.dto.DigitalitzacioEstatDto;
import es.caib.ripea.core.api.dto.DigitalitzacioPerfilDto;
import es.caib.ripea.core.api.dto.DigitalitzacioResultatDto;
import es.caib.ripea.core.api.dto.DigitalitzacioTransaccioRespostaDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentFirmaTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioDto;
import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiTipoFirmaEnumDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientEstatEnumDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.ImportacioDto;
import es.caib.ripea.core.api.dto.IntegracioAccioDto;
import es.caib.ripea.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.ripea.core.api.dto.InteressatTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.core.api.dto.MunicipiDto;
import es.caib.ripea.core.api.dto.NivellAdministracioDto;
import es.caib.ripea.core.api.dto.NtiOrigenEnumDto;
import es.caib.ripea.core.api.dto.PaisDto;
import es.caib.ripea.core.api.dto.PortafirmesBlockDto;
import es.caib.ripea.core.api.dto.PortafirmesBlockInfoDto;
import es.caib.ripea.core.api.dto.PortafirmesCarrecDto;
import es.caib.ripea.core.api.dto.PortafirmesDocumentTipusDto;
import es.caib.ripea.core.api.dto.PortafirmesFluxEstatDto;
import es.caib.ripea.core.api.dto.PortafirmesFluxInfoDto;
import es.caib.ripea.core.api.dto.PortafirmesFluxRespostaDto;
import es.caib.ripea.core.api.dto.PortafirmesIniciFluxRespostaDto;
import es.caib.ripea.core.api.dto.ProcedimentDto;
import es.caib.ripea.core.api.dto.ProvinciaDto;
import es.caib.ripea.core.api.dto.SignatureInfoDto;
import es.caib.ripea.core.api.dto.TipusDocumentalDto;
import es.caib.ripea.core.api.dto.TipusImportEnumDto;
import es.caib.ripea.core.api.dto.TipusViaDto;
import es.caib.ripea.core.api.dto.UnitatOrganitzativaDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.dto.ViaFirmaDispositiuDto;
import es.caib.ripea.core.api.dto.config.ConfigDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.SistemaExternException;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DispositiuEnviamentEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentEnviamentInteressatEntity;
import es.caib.ripea.core.entity.DocumentNotificacioEntity;
import es.caib.ripea.core.entity.DocumentPortafirmesEntity;
import es.caib.ripea.core.entity.DocumentViaFirmaEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.InteressatAdministracioEntity;
import es.caib.ripea.core.entity.InteressatEntity;
import es.caib.ripea.core.entity.InteressatPersonaFisicaEntity;
import es.caib.ripea.core.entity.InteressatPersonaJuridicaEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.plugin.PropertiesHelper;
import es.caib.ripea.plugin.conversio.ConversioArxiu;
import es.caib.ripea.plugin.conversio.ConversioPlugin;
import es.caib.ripea.plugin.dadesext.ComunitatAutonoma;
import es.caib.ripea.plugin.dadesext.DadesExternesPlugin;
import es.caib.ripea.plugin.dadesext.Municipi;
import es.caib.ripea.plugin.dadesext.Pais;
import es.caib.ripea.plugin.dadesext.Provincia;
import es.caib.ripea.plugin.digitalitzacio.DigitalitzacioPerfil;
import es.caib.ripea.plugin.digitalitzacio.DigitalitzacioPlugin;
import es.caib.ripea.plugin.digitalitzacio.DigitalitzacioResultat;
import es.caib.ripea.plugin.digitalitzacio.DigitalitzacioTransaccioResposta;
import es.caib.ripea.plugin.firmaservidor.FirmaServidorPlugin;
import es.caib.ripea.plugin.firmaservidor.SignaturaResposta;
import es.caib.ripea.plugin.gesdoc.GestioDocumentalPlugin;
import es.caib.ripea.plugin.notificacio.EntregaPostalTipus;
import es.caib.ripea.plugin.notificacio.Enviament;
import es.caib.ripea.plugin.notificacio.EnviamentTipus;
import es.caib.ripea.plugin.notificacio.Notificacio;
import es.caib.ripea.plugin.notificacio.NotificacioPlugin;
import es.caib.ripea.plugin.notificacio.Persona;
import es.caib.ripea.plugin.notificacio.RespostaConsultaEstatEnviament;
import es.caib.ripea.plugin.notificacio.RespostaConsultaEstatNotificacio;
import es.caib.ripea.plugin.notificacio.RespostaConsultaInfoRegistre;
import es.caib.ripea.plugin.notificacio.RespostaEnviar;
import es.caib.ripea.plugin.notificacio.RespostaJustificantEnviamentNotib;
import es.caib.ripea.plugin.portafirmes.PortafirmesBlockInfo;
import es.caib.ripea.plugin.portafirmes.PortafirmesBlockSignerInfo;
import es.caib.ripea.plugin.portafirmes.PortafirmesCarrec;
import es.caib.ripea.plugin.portafirmes.PortafirmesDocument;
import es.caib.ripea.plugin.portafirmes.PortafirmesDocumentTipus;
import es.caib.ripea.plugin.portafirmes.PortafirmesFluxBloc;
import es.caib.ripea.plugin.portafirmes.PortafirmesFluxInfo;
import es.caib.ripea.plugin.portafirmes.PortafirmesFluxResposta;
import es.caib.ripea.plugin.portafirmes.PortafirmesIniciFluxResposta;
import es.caib.ripea.plugin.portafirmes.PortafirmesPlugin;
import es.caib.ripea.plugin.portafirmes.PortafirmesPrioritatEnum;
import es.caib.ripea.plugin.procediment.ProcedimentPlugin;
import es.caib.ripea.plugin.unitat.NodeDir3;
import es.caib.ripea.plugin.unitat.UnitatOrganitzativa;
import es.caib.ripea.plugin.unitat.UnitatsOrganitzativesPlugin;
import es.caib.ripea.plugin.usuari.DadesUsuari;
import es.caib.ripea.plugin.usuari.DadesUsuariPlugin;
import es.caib.ripea.plugin.viafirma.ViaFirmaDispositiu;
import es.caib.ripea.plugin.viafirma.ViaFirmaDocument;
import es.caib.ripea.plugin.viafirma.ViaFirmaParams;
import es.caib.ripea.plugin.viafirma.ViaFirmaPlugin;
import es.caib.ripea.plugin.viafirma.ViaFirmaResponse;

/**
 * Helper per a interactuar amb els plugins.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PluginHelper {

	public static final String GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_DOC_TMP = "anotacions_registre_doc_tmp";
	public static final String GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_FIR_TMP = "anotacions_registre_fir_tmp";
	public static final String GESDOC_AGRUPACIO_CERTIFICACIONS = "certificacions";
	public static final String GESDOC_AGRUPACIO_NOTIFICACIONS = "notificacions";
	public static final String GESDOC_AGRUPACIO_DOCS_FIRMATS_PORTAFIB = "docsFirmats"; //documents signed by portafib that haven't been saved in arxiu  
	public static final String GESDOC_AGRUPACIO_DOCS_ADJUNTS = "docsAdjunts"; // documents adjunts when creating document that haven't been saved in arxiu
	
	public static final String GESDOC_AGRUPACIO_DOCS_ESBORRANYS = "docsEsborranys"; // firma separada of documents which are saved in arxiu as esborannys


	private DadesUsuariPlugin dadesUsuariPlugin;
	private Map<String, UnitatsOrganitzativesPlugin> unitatsOrganitzativesPlugins = new HashMap<>();
	private Map<String, PortafirmesPlugin> portafirmesPlugins = new HashMap<>();
	private Map<String, DigitalitzacioPlugin> digitalitzacioPlugins = new HashMap<>();
	private Map<String, ConversioPlugin> conversioPlugins = new HashMap<>();
	private Map<String, DadesExternesPlugin> dadesExternesPlugins = new HashMap<>();
	private Map<String, DadesExternesPlugin> dadesExternesPinbalPlugins = new HashMap<>();
	private Map<String, IArxiuPlugin> arxiuPlugins = new HashMap<>();
	private Map<String, IValidateSignaturePlugin> validaSignaturaPlugins = new HashMap<>();
	private Map<String, NotificacioPlugin> notificacioPlugins = new HashMap<>();
	private Map<String, GestioDocumentalPlugin> gestioDocumentalPlugins = new HashMap<>();
	private Map<String, FirmaServidorPlugin> firmaServidorPlugins = new HashMap<>();
	private Map<String, ViaFirmaPlugin> viaFirmaPlugins = new HashMap<>();
	private Map<String, ProcedimentPlugin> procedimentPlugins = new HashMap<>();
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private IntegracioHelper integracioHelper;
	@Autowired
	private DocumentHelper documentHelper;
	@Autowired
	private DadesExternesHelper dadesExternesHelper;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private ExpedientRepository expedientRepository;
	@Autowired
	private OrganGestorHelper organGestorHelper;

	public List<String> rolsUsuariFindAmbCodi(String usuariCodi) {

		String accioDescripcio = "Consulta rols a partir del codi d'usuari";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codi", usuariCodi);
		long t0 = System.currentTimeMillis();
		try {
			List<String> rolsDisponibles = getDadesUsuariPlugin().findRolsAmbCodi(usuariCodi);
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_USUARIS, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			return rolsDisponibles;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_USUARIS, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_USUARIS, errorDescripcio, ex);
		}
	}

	public DadesUsuari dadesUsuariFindAmbCodi(String usuariCodi) {

		String accioDescripcio = "Consulta d'usuari amb codi";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codi", usuariCodi);
		long t0 = System.currentTimeMillis();
		try {
			DadesUsuari dadesUsuari = getDadesUsuariPlugin().findAmbCodi(usuariCodi);
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_USUARIS, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			return dadesUsuari;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_USUARIS, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_USUARIS, errorDescripcio, ex);
		}
	}
	public List<DadesUsuari> dadesUsuariFindAmbGrup(String grupCodi) {

		String accioDescripcio = "Consulta d'usuaris d'un grup";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("grup", grupCodi);
		long t0 = System.currentTimeMillis();
		try {
			List<DadesUsuari> dadesUsuari = getDadesUsuariPlugin().findAmbGrup(grupCodi);
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_USUARIS, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			return dadesUsuari;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_USUARIS, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_USUARIS, errorDescripcio, ex);
		}
	}

	public List<DadesUsuari> findAmbFiltre(String filtre) throws SistemaExternException {

		String accioDescripcio = "Consulta d'usuaris d'un filtre";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("filtre", filtre);
		long t0 = System.currentTimeMillis();
		try {
			List<DadesUsuari> dadesUsuari = getDadesUsuariPlugin().findAmbFiltre(filtre);
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_USUARIS, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			return dadesUsuari;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_USUARIS, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_USUARIS, errorDescripcio, ex);
		}
	}

	// UNITATS ORGANITZATIVES
	// /////////////////////////////////////////////////////////////////////////////////////
	public Map<String, NodeDir3> getOrganigramaOrganGestor(String codiDir3) throws SistemaExternException {

		long t0 = System.currentTimeMillis();
		String accioDescripcio = "Obtenir organigrama per entitat";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("Codi Dir3 de l'entitat", codiDir3);
		Map<String, NodeDir3> organigrama = null;
		try {
			organigrama = getUnitatsOrganitzativesPlugin().organigrama(codiDir3);
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_UNITATS, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al obtenir l'organigrama per entitat";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_UNITATS, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			logger.error(errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorDescripcio, ex);
		}
		return organigrama;
	}

	public List<UnitatOrganitzativa> unitatsOrganitzativesFindByPare(
			String pareCodi,
			Date dataActualitzacio,
			Date dataSincronitzacio) {

		String accioDescripcio = "Consulta llista d'unitats donat un pare";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("unitatPare", pareCodi);
		accioParams.put("fechaActualizacion", dataActualitzacio == null ? null : dataActualitzacio.toString());
		accioParams.put("fechaSincronizacion", dataSincronitzacio == null ? null : dataSincronitzacio.toString());
		long t0 = System.currentTimeMillis();

		List<UnitatOrganitzativa> unitatsOrganitzatives = null;
		try {
			unitatsOrganitzatives = getUnitatsOrganitzativesPlugin().findAmbPare(
					pareCodi,
					dataActualitzacio,
					dataSincronitzacio);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'unitats organitzatives";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_UNITATS, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorDescripcio, ex);
		}
		
		
		if (unitatsOrganitzatives == null || unitatsOrganitzatives.isEmpty()) {
			try {
				getUnitatsOrganitzativesPlugin().findAmbCodi(pareCodi);
			} catch (Exception e) {
				String errorMissatge = "No s'ha trobat la unitat organitzativa llistat (codi=" + pareCodi + ")";
				integracioHelper.addAccioError(
						IntegracioHelper.INTCODI_UNITATS,
						accioDescripcio,
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0,
						errorMissatge,
						null);
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_UNITATS,
						errorMissatge);
			}
		}
		
		integracioHelper.addAccioOk(
				IntegracioHelper.INTCODI_UNITATS,
				accioDescripcio,
				accioParams,
				IntegracioAccioTipusEnumDto.ENVIAMENT,
				System.currentTimeMillis() - t0);
		return unitatsOrganitzatives;


	}
	
	public ArbreDto<UnitatOrganitzativaDto> unitatsOrganitzativesFindArbreByPare(
			String pareCodi) {

		String accioDescripcio = "Consulta de l'arbre d'unitats donat un pare";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("unitatPare", pareCodi);
		long t0 = System.currentTimeMillis();
		try {
			List<UnitatOrganitzativa> unitatsOrganitzatives = getUnitatsOrganitzativesPlugin().findAmbPare(pareCodi);
			ArbreDto<UnitatOrganitzativaDto> resposta = new ArbreDto<UnitatOrganitzativaDto>(false);
			// Cerca l'unitat organitzativa arrel
			UnitatOrganitzativa unitatOrganitzativaArrel = null;
			for (UnitatOrganitzativa unitatOrganitzativa: unitatsOrganitzatives) {
				if (pareCodi.equalsIgnoreCase(unitatOrganitzativa.getCodi())) {
					unitatOrganitzativaArrel = unitatOrganitzativa;
					break;
				}
			}
			if (unitatOrganitzativaArrel != null) {
				// Omple l'arbre d'unitats organitzatives
				resposta.setArrel(getNodeArbreUnitatsOrganitzatives(unitatOrganitzativaArrel, unitatsOrganitzatives, null));
				integracioHelper.addAccioOk(IntegracioHelper.INTCODI_UNITATS, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
				return resposta;
			} else {
				String errorMissatge = "No s'ha trobat la unitat organitzativa arrel (codi=" + pareCodi + ")";
				integracioHelper.addAccioError(IntegracioHelper.INTCODI_UNITATS, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorMissatge, null);
				throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorMissatge);
			}
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'unitats organitzatives";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_UNITATS, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorDescripcio, ex);
		}
	}
	public UnitatOrganitzativaDto unitatsOrganitzativesFindByCodi(String codi) {

		String accioDescripcio = "Consulta d'unitat organitzativa donat el seu codi";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codi", codi);
		long t0 = System.currentTimeMillis();
		try {
			UnitatOrganitzativaDto unitatOrganitzativa = conversioTipusHelper.convertir(getUnitatsOrganitzativesPlugin().findAmbCodi(codi), UnitatOrganitzativaDto.class);
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_UNITATS, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			return unitatOrganitzativa;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'unitats organitzatives";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_UNITATS, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorDescripcio, ex);
		}
	}

	public List<UnitatOrganitzativaDto> unitatsOrganitzativesFindByFiltre(String codiUnitat, String denominacioUnitat, String codiNivellAdministracio, String codiComunitat,
																		  String codiProvincia, String codiLocalitat, Boolean esUnitatArrel) {

		String accioDescripcio = "Consulta d'unitats organitzatives donat un filtre";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codiUnitat", codiUnitat);
		accioParams.put("denominacioUnitat", denominacioUnitat);
		accioParams.put("codiNivellAdministracio", codiNivellAdministracio);
		accioParams.put("codiComunitat", codiComunitat);
		accioParams.put("codiProvincia", codiProvincia);
		accioParams.put("codiLocalitat", codiLocalitat);
		accioParams.put("esUnitatArrel", esUnitatArrel == null ? "null" : esUnitatArrel.toString() );
		long t0 = System.currentTimeMillis();
		try {
			List<UnitatOrganitzativa> units = getUnitatsOrganitzativesPlugin().cercaUnitats(codiUnitat, denominacioUnitat, toLongValue(codiNivellAdministracio),
																							toLongValue(codiComunitat), false, esUnitatArrel,
																							toLongValue(codiProvincia), codiLocalitat);
			List<UnitatOrganitzativaDto> unitatsOrganitzatives = conversioTipusHelper.convertirList(units, UnitatOrganitzativaDto.class);
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_UNITATS, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			return unitatsOrganitzatives;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al realitzar la cerca de unitats organitzatives";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_UNITATS, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorDescripcio, ex);
		}
	}

	public boolean isArxiuPluginActiu() {
		return getArxiuPlugin() != null;
	}
	public boolean arxiuSuportaVersionsExpedients() {
		return getArxiuPlugin().suportaVersionatExpedient();
	}
	public boolean arxiuSuportaVersionsDocuments() {
		return getArxiuPlugin().suportaVersionatDocument();
	}

	public void arxiuExpedientActualitzar(ExpedientEntity expedient) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(expedient.getId()));
		boolean throwExceptionExpedientArxiu = false;
		if (throwExceptionExpedientArxiu) { // throwExceptionExpedientArxiu = true;
			throw new RuntimeException("Mock excepcion al actualitzar expedient al arxiu");
		}

		String accioDescripcio = "Actualització de les dades d'un expedient";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", expedient.getId().toString());
		accioParams.put("títol", expedient.getNom());
		MetaExpedientEntity metaExpedient = expedient.getMetaExpedient();
		accioParams.put("tipus", metaExpedient.getNom());
		accioParams.put("classificacio", metaExpedient.getClassificacioSia());
		accioParams.put("serieDocumental", metaExpedient.getSerieDocumental());
		String organCodiDir3 = expedient.getOrganGestor().getCodi();
		accioParams.put("organ", organCodiDir3);
		accioParams.put("estat", expedient.getEstat().name());
		long t0 = System.currentTimeMillis();
		try {
			String classificacio;
			if (metaExpedient.getClassificacioSia() != null) {
				classificacio = metaExpedient.getClassificacioSia();
			} else {
				classificacio = organCodiDir3 + "_PRO_RIP" + String.format("%027d", metaExpedient.getId());
			}
			List<String> interessats = new ArrayList<String>();
			for (InteressatEntity interessat: expedient.getInteressats()) {
				if (interessat.getDocumentNum() != null) {
					interessats.add(interessat.getDocumentNum());
				}
			}
			if (expedient.getArxiuUuid() == null) {
				
				String nomArxiu = ArxiuConversioHelper.revisarContingutNom(expedient.getNom());
				List<ConsultaFiltre> filtre = new ArrayList<ConsultaFiltre>();
				ConsultaFiltre consultaFiltre = new ConsultaFiltre();
				consultaFiltre.setOperacio(ConsultaOperacio.IGUAL);
				consultaFiltre.setMetadada("cm:name");
				consultaFiltre.setValorOperacio1(nomArxiu);
				filtre.add(consultaFiltre);
				ConsultaFiltre consultaFiltre2 = new ConsultaFiltre();
				consultaFiltre2.setOperacio(ConsultaOperacio.IGUAL);
				consultaFiltre2.setMetadada("eni:cod_clasificacion");
				consultaFiltre2.setValorOperacio1(expedient.getMetaExpedient().getSerieDocumental());
				filtre.add(consultaFiltre2);
				ConsultaFiltre consultaFiltre3 = new ConsultaFiltre();
				consultaFiltre3.setOperacio(ConsultaOperacio.IGUAL);
				consultaFiltre3.setMetadada("eni:id_tramite");
				consultaFiltre3.setValorOperacio1(expedient.getMetaExpedient().getClassificacioSia());
				filtre.add(consultaFiltre3);
				ConsultaResultat consultaResultat = getArxiuPlugin().expedientConsulta(filtre, 0, 10);
				List<ContingutArxiu> contingutsArxiu = consultaResultat.getResultats();
				if (contingutsArxiu != null && !contingutsArxiu.isEmpty()) {
					String arxiuUuidsNoms = "";
					for (ContingutArxiu contingutArxiu : contingutsArxiu) {
						arxiuUuidsNoms += contingutArxiu.getNom() + "=" + contingutArxiu.getIdentificador() + ", ";
					}
					logger.info("Arxius trobats per nom " + nomArxiu + ": " + arxiuUuidsNoms);
					Iterator<ContingutArxiu> it = contingutsArxiu.iterator();
					while (it.hasNext()) {
						ContingutArxiu i = it.next();
						if (!i.getNom().equals(nomArxiu)) {
							it.remove();
						}
					}
				}
				
				if (contingutsArxiu != null && !contingutsArxiu.isEmpty()) {
					if (contingutsArxiu.size() > 1) {
						String arxiuUuids = "";
						for (ContingutArxiu contingutArxiu : contingutsArxiu) {
							arxiuUuids += contingutArxiu.getIdentificador() + ", ";
						}
						logger.error("Hi ha multiple expedients amb aquest nom en arxiu: id=" + expedient.getId() + ", titol=" + expedient.getNom() + ", arxiuUuids=" + arxiuUuids);
						throw new RuntimeException("Hi ha multiple expedients amb aquest nom en arxiu: " + arxiuUuids);
					}
					
					ContingutArxiu contingutArxiu = contingutsArxiu.get(0);
					List<ExpedientEntity> expedients = expedientRepository.findByArxiuUuid(contingutArxiu.getIdentificador());
					if (expedients != null && !expedients.isEmpty()) {
						throw new RuntimeException("Expedient amb aquest nom ja s'ha creat en ripea. Per favor, canvieu el nom d'expedient en ripea"); // this should never happen, if it happens there is some problem of concurrency in ripea
					}
					expedient.updateArxiu(contingutArxiu.getIdentificador());
					logger.info("Expedient ja s'ha creat en arxiu. Enllaçant existent en arxiu amb existent en db: id=" + expedient.getId() + ",idArxiu=" + contingutArxiu.getIdentificador() + ", titol=" + expedient.getNom());
				} else {
					try {
						ContingutArxiu expedientCreat = getArxiuPlugin().expedientCrear(
								toArxiuExpedient(
										null,
										expedient.getNom(),
										null,
										Arrays.asList(organCodiDir3),
										expedient.getCreatedDate().toDate(),
										classificacio,
										expedient.getEstat(),
										interessats,
										metaExpedient.getSerieDocumental()));
						Expedient expedientDetalls = getArxiuPlugin().expedientDetalls(expedientCreat.getIdentificador(), null);
						propagarMetadadesExpedient(expedientDetalls, expedient);
						expedient.updateArxiu(expedientCreat.getIdentificador());
					} catch (Exception e) {
						if (e.getMessage().contains("Duplicate child name not allowed")) {
							logger.error("Error al crear expedient en arxiu. Duplicate child name not allowed", e);
							throw new RuntimeException("Ja s'ha creat un expedient amb el mateix nom a l'arxiu. Per restriccions pròpies de l'Arxiu, no és possible crear expedients amb el mateix nom el mateix dia. Per favor, canvieu el nom de l'expedient a Ripea, o proveu de guardar l'arxiu més tard.");
						}
						throw e;
					}
				}
			} else {
				if (interessats.isEmpty()) {
					interessats = null;
				}
				getArxiuPlugin().expedientModificar(
						toArxiuExpedient(
								expedient.getArxiuUuid(),
								expedient.getNom(),
								expedient.getNtiIdentificador(),
								Arrays.asList(organCodiDir3),
								expedient.getCreatedDate().toDate(),
								classificacio,
								expedient.getEstat(),
								interessats,
								metaExpedient.getSerieDocumental()));
				expedient.updateArxiu(null);
			}
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public Expedient arxiuExpedientConsultarPerUuid(String uuid) {

		String accioDescripcio = "Consulta d'un expedient per uuid";
		Map<String, String> accioParams = new HashMap<String, String>();
		long t0 = System.currentTimeMillis();
		try {
			Expedient arxiuExpedient = getArxiuPlugin().expedientDetalls(uuid, null);
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			return arxiuExpedient;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public Expedient arxiuExpedientConsultar(ExpedientEntity expedient) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(expedient.getId()));
		String accioDescripcio = "Consulta d'un expedient";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", expedient.getId().toString());
		accioParams.put("títol", expedient.getNom());
		accioParams.put("tipus", expedient.getMetaExpedient().getNom());
		long t0 = System.currentTimeMillis();
		try {
			Expedient arxiuExpedient = getArxiuPlugin().expedientDetalls(expedient.getArxiuUuid(), null);
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			return arxiuExpedient;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public void arxiuExpedientEsborrar(ExpedientEntity expedient) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(expedient.getId()));
		String accioDescripcio = "Eliminació d'un expedient";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", expedient.getId().toString());
		accioParams.put("títol", expedient.getNom());
		accioParams.put("tipus", expedient.getMetaExpedient().getNom());
		long t0 = System.currentTimeMillis();
		try {
			getArxiuPlugin().expedientEsborrar(expedient.getArxiuUuid());
			expedient.updateArxiuEsborrat();
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public void arxiuExpedientEsborrarPerUuid(String uuid) {

		String accioDescripcio = "Eliminació d'un expedient";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("uuid", uuid);
		long t0 = System.currentTimeMillis();
		try {
			getArxiuPlugin().expedientEsborrar(uuid);
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public void arxiuExpedientTancar(ExpedientEntity expedient) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(expedient.getId()));
		String accioDescripcio = "Tancament d'un expedient";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", expedient.getId().toString());
		accioParams.put("títol", expedient.getNom());
		accioParams.put("tipus", expedient.getMetaExpedient().getNom());
		long t0 = System.currentTimeMillis();
		try {
			String arxiuUuid = getArxiuPlugin().expedientTancar(expedient.getArxiuUuid());
			if (arxiuUuid != null) {
				expedient.updateArxiu(arxiuUuid);
			}
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}



	public void arxiuExpedientReobrir(ExpedientEntity expedient) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(expedient.getId()));
		String accioDescripcio = "Reobertura d'un expedient";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", expedient.getId().toString());
		accioParams.put("títol", expedient.getNom());
		accioParams.put("tipus", expedient.getMetaExpedient().getNom());
		long t0 = System.currentTimeMillis();
		try {
			getArxiuPlugin().expedientReobrir(expedient.getArxiuUuid());
			expedient.updateArxiuEsborrat();
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public String arxiuExpedientExportar(ExpedientEntity expedient) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(expedient.getId()));
		String accioDescripcio = "Exportar expedient en format ENI";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", expedient.getId().toString());
		accioParams.put("títol", expedient.getNom());
		long t0 = System.currentTimeMillis();
		try {
			String exportacio = getArxiuPlugin().expedientExportarEni(expedient.getArxiuUuid());
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			return exportacio;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}
	
	public void arxiuExpedientEnllacar(ExpedientEntity expedientFill, ExpedientEntity expedientPare) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(expedientPare.getId()));
		String accioDescripcio = "Enllaçant dos expedients (expedientUuidPare=" + expedientPare.getId() + ", expedientUuidFill=" + expedientFill.getId() + ")";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("idExpedientPare", expedientPare.getId().toString());
		accioParams.put("titolExpedientPare", expedientPare.getNom());
		accioParams.put("idExpedientFill", expedientFill.getId().toString());
		accioParams.put("titolExpedientFill", expedientFill.getNom());
		long t0 = System.currentTimeMillis();
		try {
			getArxiuPlugin().expedientLligar(expedientPare.getArxiuUuid(), expedientFill.getArxiuUuid());
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public void arxiuExpedientDesenllacar(ExpedientEntity expedientFill, ExpedientEntity expedientPare) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(expedientPare.getId()));
		String accioDescripcio = "Desenllaçant dos expedients (expedientUuidPare=" + expedientPare.getId() + ", expedientUuidFill=" + expedientFill.getId() + ")";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("idExpedientPare", expedientPare.getId().toString());
		accioParams.put("titolExpedientPare", expedientPare.getNom());
		accioParams.put("idExpedientFill", expedientFill.getId().toString());
		accioParams.put("titolExpedientFill", expedientFill.getNom());
		long t0 = System.currentTimeMillis();
		try {
			getArxiuPlugin().expedientDeslligar(expedientPare.getArxiuUuid(), expedientFill.getArxiuUuid());
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}
	
	public void arxiuDocumentActualitzar(
			DocumentEntity document,
			FitxerDto fitxer,
			DocumentFirmaTipusEnumDto documentFirmaTipus, 
			List<ArxiuFirmaDto> firmes,	
			ArxiuEstatEnumDto arxiuEstat) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(document.getId()));
		ContingutArxiu documentArxiuCreatOModificat;
		
		boolean throwExceptionDocumentArxiu = false;
		if (throwExceptionDocumentArxiu) { // throwExceptionDocumentArxiu = true;
			throw new RuntimeException("Mock excepcion al actualitzar document al arxiu");
		}

		IntegracioAccioDto integracioAccio = getIntegracioAccio(
				document, 
				"Actualització de les dades d'un document");
		
		
		ContingutEntity contingutPare = getContingutPare(document);
		
		try {
			if (document.getArxiuUuid() == null) {
				// ===============  CREAR DOCUMENT EN ARXIU ===================
				Document documentArxiu = toArxiuDocument(
						document,
						contingutPare,
						fitxer,
						documentFirmaTipus,
						firmes,
						ArxiuOperacioEnumDto.CREACIO,
						arxiuEstat);
				
				documentArxiuCreatOModificat = getArxiuPlugin().documentCrear(
						documentArxiu,
						contingutPare.getArxiuUuid());
				document.updateArxiu(documentArxiuCreatOModificat.getIdentificador());
				document.updateArxiuEstat(arxiuEstat);

			} else {
				// ===============  MODIFICAR DOCUMENT EN ARXIU ===================
				Document documentArxiu = toArxiuDocument(
						document,
						contingutPare,
						fitxer,
						documentFirmaTipus,
						firmes,
						ArxiuOperacioEnumDto.MODIFICACIO,
						arxiuEstat);
				
				documentArxiuCreatOModificat = getArxiuPlugin().documentModificar(documentArxiu);
				document.updateArxiu(null);
				document.updateArxiuEstat(arxiuEstat);

				
			}
			Document documentDetalls = getArxiuPlugin().documentDetalls(documentArxiuCreatOModificat.getIdentificador(), null, false);
			propagarMetadadesDocument(documentDetalls, document);
		
			
			arxiuEnviamentOk(
					integracioAccio);
		} catch (Exception ex) {
			throw arxiuEnviamentError(
					integracioAccio,
					ex);
		}
	}
	

	
	
	public void arxiuPropagarFirmaSeparada(
			DocumentEntity document,
			FitxerDto fitxerFirma) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(document.getId()));
		boolean throwExceptionDocumentArxiu = false;
		if (throwExceptionDocumentArxiu) { // throwExceptionDocumentArxiu = true;
			throw new RuntimeException("Mock excepcion al actualitzar firma al arxiu");
		}
		
		IntegracioAccioDto integracioAccio = getIntegracioAccio(
				document,
				"Actualització de les dades d'una firma separada del document esboranny");
		
		ContingutEntity contingutPare = getContingutPare(document);

		Document documentArxiu = new Document(); 
		documentArxiu.setMetadades(new DocumentMetadades()); 
		documentArxiu.setEstat(DocumentEstat.ESBORRANY);
		
		String documentNomInArxiu = documentNomInArxiu(
				document.getNom() + "_firma_separada",
				contingutPare.getArxiuUuid());
		documentArxiu.setNom(documentNomInArxiu);
		
		documentArxiu.setContingut(
				getDocumentContingut(
						fitxerFirma.getNom(),
						fitxerFirma.getContentType(),
						fitxerFirma.getContingut()));

		try {

			if (StringUtils.isEmpty(document.getArxiuUuidFirma())) {
				ContingutArxiu contingutArxiu = getArxiuPlugin().documentCrear(
						documentArxiu,
						contingutPare.getArxiuUuid());
				
				document.setArxiuUuidFirma(contingutArxiu.getIdentificador());
			} else {
				documentArxiu.setIdentificador(document.getArxiuUuidFirma());
				getArxiuPlugin().documentModificar(documentArxiu);
			}

			
			arxiuEnviamentOk(
					integracioAccio);
		} catch (Exception ex) {
			throw arxiuEnviamentError(
					integracioAccio,
					ex);
		}
	}
	
	
	private void arxiuEnviamentOk(IntegracioAccioDto integracioAccio){
		integracioHelper.addAccioOk(
				IntegracioHelper.INTCODI_ARXIU,
				integracioAccio.getDescripcio(),
				integracioAccio.getParametres(),
				IntegracioAccioTipusEnumDto.ENVIAMENT,
				System.currentTimeMillis() - integracioAccio.getTempsInici());
	}
	
	private SistemaExternException arxiuEnviamentError(IntegracioAccioDto integracioAccio, Exception ex){
		String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
		integracioHelper.addAccioError(
				IntegracioHelper.INTCODI_ARXIU,
				integracioAccio.getDescripcio(),
				integracioAccio.getParametres(),
				IntegracioAccioTipusEnumDto.ENVIAMENT,
				System.currentTimeMillis() - integracioAccio.getTempsInici(),
				errorDescripcio,
				ex);
		return new SistemaExternException(
				IntegracioHelper.INTCODI_ARXIU,
				errorDescripcio,
				ex);
	}
	
	
	private ContingutEntity getContingutPare(DocumentEntity document){
		boolean utilitzarCarpetesEnArxiu = !isCarpetaLogica();
		ContingutEntity contingutPare = utilitzarCarpetesEnArxiu ? document.getPare() : document.getExpedient();
		return contingutPare;
	}
	

	
	
	/**
	 * 
	 * doesn't work correctly with ArxiuPluginCaib, changes firma type in arxiu to CSV
	 */
	@Experimental
	public void arxiuDocumentSetDefinitiu(  
			DocumentEntity document) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(document.getId()));
		try {
			Document documentArxiu = new Document();
			documentArxiu.setIdentificador(document.getArxiuUuid());
			documentArxiu.setEstat(DocumentEstat.DEFINITIU);
			getArxiuPlugin().documentModificar(documentArxiu);
			document.updateArxiu(null);
			document.updateArxiuEstat(ArxiuEstatEnumDto.DEFINITIU);
		} catch (Exception ex) {
		}
	}
	
	private DocumentContingut getDocumentContingut(
			String nom,
			String contentType,
			byte[] contingut) {
		DocumentContingut documentContingut = new DocumentContingut();
		documentContingut.setArxiuNom(nom);
		documentContingut.setTipusMime(contentType);
		documentContingut.setContingut(contingut);

		return documentContingut;
	}
	
	
	
//	private Firma getFirma(ArxiuFirmaDto firmaDto) {
//		Firma firma = new Firma();
//		firma.setFitxerNom(firmaDto.getFitxerNom());
//		firma.setContingut(firmaDto.getContingut());
//		firma.setTipusMime(firmaDto.getTipusMime());
//		firma.setTipus(getFirmaTipus(firmaDto.getTipus()));
//		firma.setPerfil(getFirmaPerfil(firmaDto.getPerfil()));
//		firma.setCsvRegulacio(firmaDto.getCsvRegulacio());
//		return firma;
//	}
	
	
	private Firma getFirma(
			String nom,
			String contentType,
			byte[] contingut,
			ArxiuFirmaTipusEnumDto tipus,
			ArxiuFirmaPerfilEnumDto perfil,
			String csvRegulacio) {
		Firma firma = new Firma();
		firma.setFitxerNom(nom);

		firma.setTipusMime(contentType);
		firma.setContingut(contingut);
		firma.setTipus(ArxiuConversions.getFirmaTipus(tipus));
		firma.setPerfil(ArxiuConversions.getFirmaPerfil(perfil));
		firma.setCsvRegulacio(csvRegulacio);
		return firma;
	}
	
	private void setContingutIFirmes(
			Document documentArxiu,
			FitxerDto fitxer,
			DocumentFirmaTipusEnumDto documentFirmaTipus,
			List<ArxiuFirmaDto> firmes,
			ArxiuOperacioEnumDto arxiuAccio, 
			ArxiuEstatEnumDto arxiuEstat) {


		if (documentFirmaTipus == DocumentFirmaTipusEnumDto.SENSE_FIRMA) {

			documentArxiu.setContingut(
					getDocumentContingut(
							fitxer.getNom(),
							fitxer.getContentType(),
							fitxer.getContingut()));
			
		} else if (documentFirmaTipus == DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA) {
			
			ArxiuFirmaDto primeraFirma = firmes.get(0);
			Firma firma = getFirma(
					primeraFirma.getFitxerNom(),
					primeraFirma.getTipusMime(),
					primeraFirma.getContingut(),
					primeraFirma.getTipus(),
					primeraFirma.getPerfil(),
					primeraFirma.getCsvRegulacio());
			documentArxiu.setFirmes(Arrays.asList(firma));

		} else if (documentFirmaTipus == DocumentFirmaTipusEnumDto.FIRMA_SEPARADA) {
			
			if (fitxer != null) {
				documentArxiu.setContingut(
						getDocumentContingut(
								fitxer.getNom(),
								fitxer.getContentType(),
								fitxer.getContingut()));
			}
			
			ArrayList<Firma> arxiuFirmes = new ArrayList<Firma>();
			for (ArxiuFirmaDto firmaDto: firmes) {
				Firma firma = getFirma(
						firmaDto.getFitxerNom(),
						firmaDto.getTipusMime(),
						firmaDto.getContingut(),
						firmaDto.getTipus(),
						firmaDto.getPerfil(),
						firmaDto.getCsvRegulacio());
				arxiuFirmes.add(firma);
			}
			documentArxiu.setFirmes(arxiuFirmes);

		}

	}
	
	
	
	
	private IntegracioAccioDto getIntegracioAccio(
			DocumentEntity document, 
			String accioDescripcio) {
		
		String serieDocumental = document.getExpedient().getMetaExpedient().getSerieDocumental();
		ContingutEntity contingutPare = getContingutPare(document);
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", document.getId().toString());
		accioParams.put("títol", document.getNom());
		if (contingutPare != null) {
			accioParams.put("contingutPareId", contingutPare.getId().toString());
			accioParams.put("contingutPareNom", contingutPare.getNom());
		}
		if (serieDocumental != null) {
			accioParams.put("serieDocumental", serieDocumental);
		}
		
		return new IntegracioAccioDto(
				accioDescripcio,
				accioParams,
				System.currentTimeMillis());
	}
	
	
	private IntegracioAccioDto getIntegracioAccio(
			String uuid, 
			String accioDescripcio) {
		
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("uuid", uuid);
		
		return new IntegracioAccioDto(
				accioDescripcio,
				accioParams,
				System.currentTimeMillis());
	}
	

	private String documentNomInArxiu(String nomPerComprovar, String expedientUuid) {

		List<ContingutArxiu> continguts = arxiuExpedientConsultarPerUuid(expedientUuid).getContinguts();
		nomPerComprovar = ArxiuConversioHelper.revisarContingutNom(nomPerComprovar);
		int ocurrences = 0;
		if (continguts != null) {
			List<String> noms = new ArrayList<String>();
			for(ContingutArxiu contingut : continguts) {
				noms.add(contingut.getNom());
			}
			String newName = new String(nomPerComprovar);
			while(noms.indexOf(newName) >= 0) {
				ocurrences ++;
				newName = nomPerComprovar + " (" + ocurrences + ")";
			}
			return newName;
		}
		return nomPerComprovar;
	}


	public Document arxiuDocumentConsultar(String arxiuUuid) {

		
		IntegracioAccioDto integracioAccio = getIntegracioAccio(
				arxiuUuid,
				"Consulta d'un document");

		try {
			Document documentDetalls = getArxiuPlugin().documentDetalls(
					arxiuUuid,
					null,
					true);

			arxiuEnviamentOk(
					integracioAccio);

			return documentDetalls;
		} catch (Exception ex) {
			throw arxiuEnviamentError(
					integracioAccio,
					ex);
		}
	}
	
	
	public void arxiuDocumentEsborrar(String arxiuUuid) {

		IntegracioAccioDto integracioAccio = getIntegracioAccio(
				arxiuUuid,
				"Eliminació d'un document");
		try {
			getArxiuPlugin().documentEsborrar(arxiuUuid);

			arxiuEnviamentOk(integracioAccio);
		} catch (Exception ex) {
			throw arxiuEnviamentError(
					integracioAccio,
					ex);
		}
	}
	
	public Document arxiuDocumentConsultar(DocumentEntity contingut, String nodeId, String versio, boolean ambContingut) {
		return arxiuDocumentConsultar(contingut, nodeId, versio, ambContingut, false);
	}

	public Document arxiuDocumentConsultar(DocumentEntity document, String arxiuUuid, String versio, boolean ambContingut, boolean ambVersioImprimible) {

		
		String accioDescripcio = "Consulta d'un document";
		Map<String, String> accioParams = new HashMap<String, String>();
		if (document != null) {
			accioParams.put("contingutId", document.getId().toString());
			accioParams.put("contingutNom", document.getNom());
		}
		if (arxiuUuid != null) {
			accioParams.put("arxiuUuid", arxiuUuid);
		}
		accioParams.put("versio", versio);
		accioParams.put("ambContingut", new Boolean(ambContingut).toString());
		accioParams.put("ambVersioImprimible", new Boolean(ambVersioImprimible).toString());
		long t0 = System.currentTimeMillis();
		try {
			String arxiuUuidConsulta = (document != null && document instanceof DocumentEntity) ? document.getArxiuUuid() : arxiuUuid;
			accioParams.put("arxiuUuidConsulta", arxiuUuidConsulta);
			Document documentDetalls = getArxiuPlugin().documentDetalls(arxiuUuidConsulta, versio, ambContingut);
			if (ambContingut && documentDetalls.getContingut() == null) {
				logger.error("El plugin no ha retornat el contingut del document ({})", accioParams.toString());
			}
			boolean generarVersioImprimible = false;
			if (ambVersioImprimible && ambContingut && documentDetalls.getFirmes() != null && !documentDetalls.getFirmes().isEmpty()) {
				for (Firma firma : documentDetalls.getFirmes()) {
					if (documentDetalls.getContingut().getTipusMime().equals("application/pdf") && (firma.getTipus() == FirmaTipus.PADES || firma.getTipus() == FirmaTipus.CADES_ATT || firma.getTipus() == FirmaTipus.CADES_DET)) {
						generarVersioImprimible = true;
					}
				}
			}
			if (generarVersioImprimible) {
				documentDetalls.setContingut(getArxiuPlugin().documentImprimible(documentDetalls.getIdentificador()));
			}
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			return documentDetalls;
		} catch (Exception ex) {
			String msg = "";
			if (ex.getCause() != null && !ex.getCause().equals(ex)) {
				msg = ex.getMessage() + ": " + ex.getCause().getMessage();
			} else {
				msg = ex.getMessage();
			}
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + msg;
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
		/*String accioDescripcio = "Consulta d'un document";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("contingutId", contingut.getId().toString());
		accioParams.put("contingutNom", contingut.getNom());
		accioParams.put("nodeId", nodeId);
		String arxiuUuid = null;
		if (contingut instanceof DocumentEntity) {
			arxiuUuid = contingut.getArxiuUuid();
		} else {
			arxiuUuid = nodeId;
		}
		accioParams.put("arxiuUuidCalculat", arxiuUuid);
		accioParams.put("versio", versio);
		accioParams.put("ambContingut", new Boolean(ambContingut).toString());
		long t0 = System.currentTimeMillis();
		try {
			Document documentDetalls = getArxiuPlugin().documentDetalls(
					arxiuUuid,
					versio,
					ambContingut);
			if (ambVersioImprimible && ambContingut && documentDetalls.getFirmes() != null && !documentDetalls.getFirmes().isEmpty()) {
				boolean isPdf = false;
				for (Firma firma : documentDetalls.getFirmes()) {
					if (firma.getTipus() == FirmaTipus.PADES) {
						isPdf = true;
					}
				}
				if (isPdf) {
					documentDetalls.setContingut(getArxiuPlugin().documentImprimible(documentDetalls.getIdentificador()));
				}
			}
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return documentDetalls;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_ARXIU,
					errorDescripcio,
					ex);
		}*/
	}
	
	
	
	public byte[] arxiuFirmaSeparadaConsultar(DocumentEntity document) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(document.getId()));
		IntegracioAccioDto integracioAccio = getIntegracioAccio(
				document,
				"Consulta d'una firma separada del document esboranny");

		try {
			Document documentDetalls = getArxiuPlugin().documentDetalls(
					document.getArxiuUuidFirma(),
					null,
					true);

			arxiuEnviamentOk(
					integracioAccio);
			return documentDetalls.getContingut().getContingut();
		} catch (Exception ex) {
			throw arxiuEnviamentError(
					integracioAccio,
					ex);
		}
	}
	

	public void arxiuDocumentEsborrar(DocumentEntity document) {

		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(document.getId()));
		String accioDescripcio = "Eliminació d'un document";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", document.getId().toString());
		accioParams.put("títol", document.getNom());
		long t0 = System.currentTimeMillis();
		try {
			getArxiuPlugin().documentEsborrar(document.getArxiuUuid());
			document.updateArxiuEsborrat();
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public boolean arxiuDocumentExtensioPermesa(String extensio) {
		return getArxiuFormatExtensio(extensio) != null;
	}

	public List<ContingutArxiu> arxiuDocumentObtenirVersions(DocumentEntity document) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(document.getId()));
		String accioDescripcio = "Obtenir versions del document";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", document.getId().toString());
		accioParams.put("títol", document.getNom());
		long t0 = System.currentTimeMillis();
		try {
			List<ContingutArxiu> versions = getArxiuPlugin().documentVersions(document.getArxiuUuid());
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			return versions;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	


	
	public List<TipusDocumentalDto> documentTipusAddicionals() {

		String accioDescripcio = "Consulta de tipus de documents addicionals";
		long t0 = System.currentTimeMillis();
		try {
			List<DocumentTipusAddicional> documentTipusAddicionals = getArxiuPlugin().documentTipusAddicionals();
			List<TipusDocumentalDto> tipusDocumentalsDto = new ArrayList<>();
			if (documentTipusAddicionals != null && !documentTipusAddicionals.isEmpty()) {
				for (DocumentTipusAddicional documentTipusAddicional : documentTipusAddicionals) {
					TipusDocumentalDto tipusDocumentalDto =  new TipusDocumentalDto();
					tipusDocumentalDto.setCodi(documentTipusAddicional.getCodi());
					tipusDocumentalDto.setNom(documentTipusAddicional.getDescripcio());
					tipusDocumentalsDto.add(tipusDocumentalDto);
				}
			}
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, null, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			return tipusDocumentalsDto;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, null, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}



	public void arxiuDocumentCopiar(DocumentEntity document, String arxiuUuidDesti) {

		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(document.getId()));
		String accioDescripcio = "Copiar document";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", document.getId().toString());
		accioParams.put("títol", document.getNom());
		accioParams.put("arxiuUuidDesti", arxiuUuidDesti);
		long t0 = System.currentTimeMillis();
		try {
			getArxiuPlugin().documentCopiar(document.getArxiuUuid(), arxiuUuidDesti);
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}
	public ContingutArxiu arxiuDocumentLink(DocumentEntity document, String arxiuUuidDesti) {

		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(document.getId()));
		String accioDescripcio = "Enllaçar document";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", document.getId().toString());
		accioParams.put("títol", document.getNom());
		accioParams.put("arxiuUuidDesti", arxiuUuidDesti);
		long t0 = System.currentTimeMillis();
		try {
			//Empram el mètode carpetaCopiar per no disposar d'un mètode específic per vincular.
			ContingutArxiu nouContingut = getArxiuPlugin().carpetaCopiar(document.getArxiuUuid(), arxiuUuidDesti);
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			return nouContingut;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public String arxiuDocumentMoure(String uuid, String uuidDesti, String uuidExpedientDesti) {

		String accioDescripcio = "Moure document";
		Map<String, String> accioParams = new HashMap<String, String>();

		accioParams.put("arxiuUuidOrigen", uuid);
		accioParams.put("uuidDesti", uuidDesti);
		accioParams.put("uuidExpedientDesti", uuidExpedientDesti);
		long t0 = System.currentTimeMillis();
		try {
			boolean throwException = false;//throwException = true
			if (throwException) {
				throw new RuntimeException("Mock excepcion moving document ");
			}
			ContingutArxiu nouDocumentArxiu = getArxiuPlugin().documentMoure(uuid, uuidDesti, uuidExpedientDesti);
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			if (nouDocumentArxiu != null) {
				return nouDocumentArxiu.getIdentificador();
			} else {
				return null;
			}
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public String arxiuDocumentExportar(DocumentEntity document) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(document.getId()));
		
		String accioDescripcio = "Exportar document en format ENI";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", document.getId().toString());
		accioParams.put("títol", document.getNom());
		long t0 = System.currentTimeMillis();
		try {
			String exportacio = getArxiuPlugin().documentExportarEni(document.getArxiuUuid());
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			return exportacio;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public FitxerDto arxiuDocumentVersioImprimible(DocumentEntity document) {

		String accioDescripcio = "Obtenir versió imprimible del document";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", document.getId().toString());
		accioParams.put("títol", document.getNom());
		long t0 = System.currentTimeMillis();
		try {
			DocumentContingut documentContingut = getArxiuPlugin().documentImprimible(document.getArxiuUuid());
			FitxerDto fitxer = new FitxerDto();
			
			String titol = document.getFitxerNom().replace(".pdf", "_imprimible.pdf");
			fitxer.setNom(titol);
			fitxer.setContentType(documentContingut.getTipusMime());
			fitxer.setTamany(documentContingut.getTamany());
			fitxer.setContingut(documentContingut.getContingut());
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			return fitxer;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public void arxiuCarpetaActualitzar(CarpetaEntity carpeta, ContingutEntity contingutPare) {

		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(carpeta.getId()));
		
		String accioDescripcio = "Actualització de les dades d'una carpeta";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", carpeta.getId().toString());
		accioParams.put("nom", carpeta.getNom());
		accioParams.put("contingutPareId", contingutPare.getId().toString());
		accioParams.put("contingutPareNom", contingutPare.getNom());
		long t0 = System.currentTimeMillis();
		try {
			if (carpeta.getArxiuUuid() == null) {
				ContingutArxiu carpetaCreada = getArxiuPlugin().carpetaCrear(toArxiuCarpeta(null, carpeta.getNom()), contingutPare.getArxiuUuid());
				carpeta.updateArxiu(carpetaCreada.getIdentificador());
			} else {
				getArxiuPlugin().carpetaModificar(toArxiuCarpeta(carpeta.getArxiuUuid(), carpeta.getNom()));
				carpeta.updateArxiu(null);
			}
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public Carpeta arxiuCarpetaConsultar(CarpetaEntity carpeta) {

		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(carpeta.getId()));
		String accioDescripcio = "Consulta d'una carpeta";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", carpeta.getId().toString());
		accioParams.put("nom", carpeta.getNom());
		long t0 = System.currentTimeMillis();
		try {
			Carpeta carpetaDetalls = getArxiuPlugin().carpetaDetalls(carpeta.getArxiuUuid());
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			return carpetaDetalls;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public void arxiuCarpetaEsborrar(CarpetaEntity carpeta) {

		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(carpeta.getId()));
		String accioDescripcio = "Eliminació d'una carpeta";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", carpeta.getId().toString());
		accioParams.put("nom", carpeta.getNom());
		long t0 = System.currentTimeMillis();
		try {
			getArxiuPlugin().carpetaEsborrar(carpeta.getArxiuUuid());
			carpeta.updateArxiuEsborrat();
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public void arxiuCarpetaCopiar(CarpetaEntity carpeta, String arxiuUuidDesti) {

		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(carpeta.getId()));
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(carpeta.getId()));
		String accioDescripcio = "Copiar carpeta";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", carpeta.getId().toString());
		accioParams.put("nom", carpeta.getNom());
		accioParams.put("arxiuUuidDesti", arxiuUuidDesti);
		long t0 = System.currentTimeMillis();
		try {
			getArxiuPlugin().carpetaCopiar(carpeta.getArxiuUuid(), arxiuUuidDesti);
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public void arxiuCarpetaMoure(CarpetaEntity carpeta, String arxiuUuidDesti) {

		String accioDescripcio = "Moure carpeta";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", carpeta.getId().toString());
		accioParams.put("nom", carpeta.getNom());
		accioParams.put("arxiuUuidDesti", arxiuUuidDesti);
		long t0 = System.currentTimeMillis();
		try {
			getArxiuPlugin().carpetaMoure(carpeta.getArxiuUuid(), arxiuUuidDesti);
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public List<ContingutArxiu> importarDocumentsArxiu(ImportacioDto params) {

		String accioDescripcio = "Importar documents";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("numeroRegistre", params.getNumeroRegistre());
		long t0 = System.currentTimeMillis();
		try {
			String tipusRegistreLabel = null;
			String dataPresentacioStr = null;
			String numeroRegistreStr = null;
			String codiEniStr = null;
			if (params.getTipusImportacio().equals(TipusImportEnumDto.NUMERO_REGISTRE)) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");  
				dataPresentacioStr = "'" + dateFormat.format(params.getDataPresentacioFormatted()) + "'";
				tipusRegistreLabel = "'" + params.getTipusRegistre().getLabel() + "'";
				numeroRegistreStr = "'" + params.getNumeroRegistre() + "'";
			} else {
				codiEniStr = "'" + params.getCodiEni() + "'";
			}
			// Aprofitam el mètode documentVersions per fer la importació
			String paramsJson = "{" + 
									"'tipusImportacio' : '" + params.getTipusImportacio().name() + "'," +
									"'numeroRegistre' : " + numeroRegistreStr + "," +
									"'tipusRegistre' : " + tipusRegistreLabel + "," +
									"'dataPresentacio' : " + dataPresentacioStr + "," +
									"'codiEni' : " + codiEniStr + "" +
								 "}";
			List<ContingutArxiu> contingutArxiu = getArxiuPlugin().documentVersions(paramsJson);
			return contingutArxiu;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public Document importarDocument(String arxiuUuidPare, String arxiuUuid, boolean moureDocument) {

		String accioDescripcio = "Importar documents";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("arxiuUuid", arxiuUuid);
		long t0 = System.currentTimeMillis();
		try {
			Document document = getArxiuPlugin().documentDetalls(arxiuUuid, null, false);
			document.setIdentificador(arxiuUuid);
			if (moureDocument) {
				// Si és de registre moure el document
				getArxiuPlugin().documentCopiar(arxiuUuidPare, arxiuUuid);
			} else {
				// Si és una importació amb ENI fer un linkdocument
				//Empram el mètode carpetaCopiar per no disposar d'un mètode específic per vincular.
				ContingutArxiu nouContingut = getArxiuPlugin().carpetaCopiar(arxiuUuid, arxiuUuidPare);
				document = getArxiuPlugin().documentDetalls(nouContingut.getIdentificador(), null, false);
				return document;
			}
			return document;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_ARXIU, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}
	public String portafirmesUpload(DocumentEntity document, String motiu, PortafirmesPrioritatEnum prioritat, Date dataCaducitat, String documentTipus,
									String[] responsables, MetaDocumentFirmaSequenciaTipusEnumDto fluxTipus, String fluxId, List<DocumentEntity> annexos, String transaccioId) {

		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(document.getId()));
		long t0 = System.currentTimeMillis();
		Map<String, String> accioParams = getAccioParamsPerPortaFirmesUpload(document, motiu, prioritat, dataCaducitat, documentTipus, responsables, fluxTipus, fluxId, annexos);
		List<PortafirmesDocument> portafirmesAnnexos = null;
		PortafirmesDocument portafirmesDocument = new PortafirmesDocument();
		portafirmesDocument.setExpedientUuid(document.getExpedient().getArxiuUuid());
		portafirmesDocument.setTitol(document.getNom());
		portafirmesDocument.setDescripcio(document.getDescripcio());
		portafirmesDocument.setFirmat(false);

		FitxerDto fitxerOriginal = documentHelper.getFitxerAssociat(document, null);
		FitxerDto fitxerConvertit = this.conversioConvertirPdf(fitxerOriginal, null);
		portafirmesDocument.setArxiuNom(fitxerConvertit.getNom());
		portafirmesDocument.setArxiuContingut(fitxerConvertit.getContingut());
		portafirmesDocument.setArxiuUuid(document.getArxiuUuid());
		if (annexos != null && ! annexos.isEmpty()) {
			portafirmesAnnexos = new ArrayList<PortafirmesDocument>();
			for (DocumentEntity annex: annexos) {
				PortafirmesDocument portafirmesAnnex = new PortafirmesDocument();
				portafirmesAnnex.setTitol(annex.getNom());
				portafirmesAnnex.setFirmat(false);
				FitxerDto annexFitxerOriginal = documentHelper.getFitxerAssociat(annex, null);
				FitxerDto annexFitxerConvertit = this.conversioConvertirPdf(annexFitxerOriginal, null);
				portafirmesAnnex.setArxiuNom(annexFitxerConvertit.getNom());
				portafirmesAnnex.setArxiuContingut(annexFitxerConvertit.getContingut());
				portafirmesAnnexos.add(portafirmesAnnex);
			}
		}
		List<PortafirmesFluxBloc> flux = new ArrayList<PortafirmesFluxBloc>();
		if (fluxId == null) {
			if (MetaDocumentFirmaSequenciaTipusEnumDto.SERIE.equals(fluxTipus)) {
				for (String responsable: responsables) {
					PortafirmesFluxBloc bloc = new PortafirmesFluxBloc();
					bloc.setMinSignataris(1);
					bloc.setDestinataris(new String[] {responsable});
					bloc.setObligatorietats(new boolean[] {true});
					flux.add(bloc);
				}
			} else if (MetaDocumentFirmaSequenciaTipusEnumDto.PARALEL.equals(fluxTipus)) {
				PortafirmesFluxBloc bloc = new PortafirmesFluxBloc();
				bloc.setMinSignataris(responsables.length);
				bloc.setDestinataris(responsables);
				boolean[] obligatorietats = new boolean[responsables.length];
				Arrays.fill(obligatorietats, true);
				bloc.setObligatorietats(obligatorietats);
				flux.add(bloc);
			}
		}
		try {
			String portafirmesEnviamentId = getPortafirmesPlugin().upload(
					portafirmesDocument,
					documentTipus,
					motiu,
					"Aplicació RIPEA",
					prioritat,
					null,
					flux,
					fluxId,
					portafirmesAnnexos,
					false,
					transaccioId);
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_PFIRMA, "Enviament de document a firmar", accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			return portafirmesEnviamentId;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de portafirmes";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_PFIRMA, "Enviament de document a firmar", accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, errorDescripcio, ex);
		}
	}


	public PortafirmesDocument portafirmesDownload(DocumentPortafirmesEntity documentPortafirmes) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(documentPortafirmes.getDocument().getId()));
		
		String accioDescripcio = "Descarregar document firmat";
		Map<String, String> accioParams = new HashMap<String, String>();
		DocumentEntity document = documentPortafirmes.getDocument();
		accioParams.put("documentVersioId", document.getId().toString());
		accioParams.put("documentPortafirmesId", documentPortafirmes.getId().toString());
		accioParams.put("portafirmesId", new Long(documentPortafirmes.getPortafirmesId()).toString());
		long t0 = System.currentTimeMillis();
		PortafirmesDocument portafirmesDocument = null;
		try {
			portafirmesDocument = getPortafirmesPlugin().download(documentPortafirmes.getPortafirmesId());
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_PFIRMA, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			return portafirmesDocument;
		} catch (Exception ex) {
			String errorDescripcio = "Error al descarregar el document firmat";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_PFIRMA, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, errorDescripcio, ex);
		}
	}

	public void portafirmesDelete(DocumentPortafirmesEntity documentPortafirmes) {

		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(documentPortafirmes.getDocument().getId()));
		
		String accioDescripcio = "Esborrar document enviat a firmar";
		Map<String, String> accioParams = new HashMap<String, String>();
		DocumentEntity document = documentPortafirmes.getDocument();
		accioParams.put("documentId", document.getId().toString());
		accioParams.put("documentPortafirmesId", documentPortafirmes.getId().toString());
		accioParams.put("portafirmesId", new Long(documentPortafirmes.getPortafirmesId()).toString());
		long t0 = System.currentTimeMillis();
		try {
			getPortafirmesPlugin().delete(documentPortafirmes.getPortafirmesId());
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_PFIRMA, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de portafirmes";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_PFIRMA, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, errorDescripcio, ex);
		}
	}

	public List<PortafirmesDocumentTipusDto> portafirmesFindDocumentTipus() {

		String accioDescripcio = "Consulta de tipus de document";
		long t0 = System.currentTimeMillis();
		try {
			List<PortafirmesDocumentTipus> tipus = getPortafirmesPlugin().findDocumentTipus();
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_PFIRMA, accioDescripcio, null, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			if (tipus != null) {
				List<PortafirmesDocumentTipusDto> resposta = new ArrayList<PortafirmesDocumentTipusDto>();
				for (PortafirmesDocumentTipus t: tipus) {
					PortafirmesDocumentTipusDto dto = new PortafirmesDocumentTipusDto();
					dto.setId(t.getId());
					dto.setCodi(t.getCodi());
					dto.setNom(t.getNom());
					resposta.add(dto);
				}
				return resposta;
			} else {
				return null;
			}
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de portafirmes";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_PFIRMA, accioDescripcio, null, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, errorDescripcio, ex);
		}
	}

	public boolean portafirmesEnviarDocumentEstampat() {
		return !getPortafirmesPlugin().isCustodiaAutomatica();
	}

	public PortafirmesIniciFluxRespostaDto portafirmesIniciarFluxDeFirma(String idioma, boolean isPlantilla, String nom, String descripcio, boolean descripcioVisible,
																		 String urlReturn) throws SistemaExternException {
		String accioDescripcio = "Iniciant flux de firma";
		long t0 = System.currentTimeMillis();
		PortafirmesIniciFluxRespostaDto transaccioResponseDto = new PortafirmesIniciFluxRespostaDto();
		try {
			PortafirmesIniciFluxResposta transaccioResponse = getPortafirmesPlugin().iniciarFluxDeFirma(idioma, isPlantilla, nom, descripcio, descripcioVisible, urlReturn);
			if (transaccioResponse != null) {
				transaccioResponseDto.setIdTransaccio(transaccioResponse.getIdTransaccio());
				transaccioResponseDto.setUrlRedireccio(transaccioResponse.getUrlRedireccio());
			}
		} catch (Exception ex) {
			String errorDescripcio = ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_PFIRMA, accioDescripcio, null, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, errorDescripcio, ex);
		}
		return transaccioResponseDto;
	}
	
	public PortafirmesFluxRespostaDto portafirmesRecuperarFluxDeFirma(String idTransaccio) {

		String accioDescripcio = "Recuperant flux de firma";
		long t0 = System.currentTimeMillis();
		PortafirmesFluxRespostaDto respostaDto;
		try {
			respostaDto = new PortafirmesFluxRespostaDto();
			PortafirmesFluxResposta resposta = getPortafirmesPlugin().recuperarFluxDeFirmaByIdTransaccio(idTransaccio);
			
			if (resposta != null) {
				respostaDto.setError(resposta.isError());
				respostaDto.setFluxId(resposta.getFluxId());
				respostaDto.setNom(resposta.getNom());
				respostaDto.setDescripcio(resposta.getDescripcio());
				respostaDto.setEstat(resposta.getEstat() != null ? PortafirmesFluxEstatDto.valueOf(resposta.getEstat().toString()) : null);
			}
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de portafirmes";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_PFIRMA, accioDescripcio, null, IntegracioAccioTipusEnumDto.RECEPCIO, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, errorDescripcio, ex);
		}
		return respostaDto;
	}
	
	public List<PortafirmesCarrecDto> portafirmesRecuperarCarrecs() {

		String accioDescripcio = "Recuperant els càrrecs disponibles";
		long t0 = System.currentTimeMillis();
		List<PortafirmesCarrecDto> carrecsDto = new ArrayList<PortafirmesCarrecDto>();
		try {
			List<PortafirmesCarrec> portafirmesCarrecs = getPortafirmesPlugin().recuperarCarrecs();
			for (PortafirmesCarrec portafirmesCarrec : portafirmesCarrecs) {
				PortafirmesCarrecDto carrecDto = new PortafirmesCarrecDto();
				carrecDto.setCarrecId(portafirmesCarrec.getCarrecId());
				carrecDto.setCarrecName(portafirmesCarrec.getCarrecName());
				carrecDto.setEntitatId(portafirmesCarrec.getEntitatId());
				carrecDto.setUsuariPersonaId(portafirmesCarrec.getUsuariPersonaId());
				carrecDto.setUsuariPersonaNif(portafirmesCarrec.getUsuariPersonaNif());
				carrecDto.setUsuariPersonaEmail(portafirmesCarrec.getUsuariPersonaEmail());
				carrecDto.setUsuariPersonaNom(portafirmesCarrec.getUsuariPersonaNom());
				carrecsDto.add(carrecDto);
			}
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de portafirmes";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_PFIRMA, accioDescripcio, null, IntegracioAccioTipusEnumDto.RECEPCIO, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, errorDescripcio, ex);
		}
		return carrecsDto;
	}
	
	public PortafirmesCarrecDto portafirmesRecuperarCarrec(String carrecId) {

		String accioDescripcio = "Recuperan un càrrec a partir del seu ID";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("carrecId", carrecId);
		long t0 = System.currentTimeMillis();
		PortafirmesCarrecDto carrecDto = new PortafirmesCarrecDto();
		try {
			PortafirmesCarrec portafirmesCarrec = getPortafirmesPlugin().recuperarCarrec(carrecId);		
			carrecDto.setCarrecId(portafirmesCarrec.getCarrecId());
			carrecDto.setCarrecName(portafirmesCarrec.getCarrecName());
			carrecDto.setEntitatId(portafirmesCarrec.getEntitatId());
			carrecDto.setUsuariPersonaId(portafirmesCarrec.getUsuariPersonaId());
			carrecDto.setUsuariPersonaNif(portafirmesCarrec.getUsuariPersonaNif());
			carrecDto.setUsuariPersonaEmail(portafirmesCarrec.getUsuariPersonaEmail());
			carrecDto.setUsuariPersonaNom(portafirmesCarrec.getUsuariPersonaNom());
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de portafirmes";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_PFIRMA, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.RECEPCIO, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, errorDescripcio, ex);
		}
		return carrecDto;
	}
	
	public void portafirmesTancarFluxDeFirma(String idTransaccio) {

		String accioDescripcio = "Tancant flux de firma";
		long t0 = System.currentTimeMillis();
		try {
			getPortafirmesPlugin().tancarTransaccioFlux(idTransaccio);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de portafirmes";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_PFIRMA, accioDescripcio, null, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, errorDescripcio, ex);
		}
	}
	
	public List<DigitalitzacioPerfilDto> digitalitzacioPerfilsDisponibles(String idioma) {

		String accioDescripcio = "Recuperant perfils disponibles";
		long t0 = System.currentTimeMillis();
		List<DigitalitzacioPerfilDto> perfilsDto = new ArrayList<DigitalitzacioPerfilDto>();;
		try {
			List<DigitalitzacioPerfil> perfils = getDigitalitzacioPlugin().recuperarPerfilsDisponibles(idioma);
			if (perfils != null) {
				for (DigitalitzacioPerfil perfil : perfils) {
					DigitalitzacioPerfilDto perfilDto = new DigitalitzacioPerfilDto();
					perfilDto.setCodi(perfil.getCodi());
					perfilDto.setNom(perfil.getNom());
					perfilDto.setDescripcio(perfil.getDescripcio());
					perfilDto.setTipus(perfil.getTipus());
					perfilsDto.add(perfilDto);
				}
			}
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de digitalitzacio";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_DIGITALITZACIO, accioDescripcio, null, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			if (ex.getClass() == SistemaExternException.class) {
				throw (SistemaExternException) ex;
			}
			throw new SistemaExternException(IntegracioHelper.INTCODI_DIGITALITZACIO, errorDescripcio, ex);
		}
		return perfilsDto;
	}
	
	public DigitalitzacioTransaccioRespostaDto digitalitzacioIniciarProces(String idioma, String codiPerfil, UsuariDto funcionari, String urlReturn) {

		String accioDescripcio = "Iniciant procés digitalització";
		long t0 = System.currentTimeMillis();
		DigitalitzacioTransaccioRespostaDto respostaDto = new DigitalitzacioTransaccioRespostaDto();
		try {
			DigitalitzacioTransaccioResposta resposta = getDigitalitzacioPlugin().iniciarProces(codiPerfil, idioma, funcionari, urlReturn);
			if (resposta != null) {
				respostaDto.setIdTransaccio(resposta.getIdTransaccio());
				respostaDto.setUrlRedireccio(resposta.getUrlRedireccio());
				respostaDto.setReturnScannedFile(resposta.isReturnScannedFile());
				respostaDto.setReturnSignedFile(resposta.isReturnSignedFile());
			}
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de digitalitzacio";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_DIGITALITZACIO, accioDescripcio, null, IntegracioAccioTipusEnumDto.RECEPCIO, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_DIGITALITZACIO, errorDescripcio, ex);
		}
		return respostaDto;
	}
	
	public DigitalitzacioResultatDto digitalitzacioRecuperarResultat(String idTransaccio, boolean returnScannedFile, boolean returnSignedFile) {

		String accioDescripcio = "Recuperant resultat digitalització";
		long t0 = System.currentTimeMillis();
		DigitalitzacioResultatDto resultatDto = new DigitalitzacioResultatDto();
		try {
			DigitalitzacioResultat resultat = getDigitalitzacioPlugin().recuperarResultat(idTransaccio, returnScannedFile, returnSignedFile);
			if (resultat != null) {
				resultatDto.setError(resultat.isError());
				resultatDto.setErrorDescripcio(resultat.getErrorDescripcio());
				resultatDto.setEstat(resultat.getEstat() != null ? DigitalitzacioEstatDto.valueOf(resultat.getEstat().toString()) : null);
				resultatDto.setContingut(resultat.getContingut());
				resultatDto.setNomDocument(resultat.getNomDocument());
				resultatDto.setMimeType(resultat.getMimeType());
				resultatDto.setEniTipoFirma(resultat.getEniTipoFirma());
			}
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de digitalitzacio";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_DIGITALITZACIO, accioDescripcio, null, IntegracioAccioTipusEnumDto.RECEPCIO, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_DIGITALITZACIO, errorDescripcio, ex);
		}
		return resultatDto;
	}
	
	public void digitalitzacioTancarTransaccio(String idTransaccio) {

		String accioDescripcio = "Tancant transacció digitalització";
		long t0 = System.currentTimeMillis();
		try {
			getDigitalitzacioPlugin().tancarTransaccio(idTransaccio);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de digitalitzacio";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_DIGITALITZACIO, accioDescripcio, null, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_DIGITALITZACIO, errorDescripcio, ex);
		}
	}
	public PortafirmesFluxInfoDto portafirmesRecuperarInfoFluxDeFirma(String plantillaFluxId, String idioma) {

		String accioDescripcio = "Recuperant detall flux de firma";
		long t0 = System.currentTimeMillis();
		PortafirmesFluxInfoDto respostaDto;
		try {
			respostaDto = new PortafirmesFluxInfoDto();
			PortafirmesFluxInfo resposta = getPortafirmesPlugin().recuperarFluxDeFirmaByIdPlantilla(plantillaFluxId, idioma);
			if (resposta != null) {
				respostaDto.setNom(resposta.getNom());
				respostaDto.setDescripcio(resposta.getDescripcio());
			}
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de portafirmes";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_PFIRMA, accioDescripcio, null, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, errorDescripcio, ex);
		}
		return respostaDto;
	}
	
	public String portafirmesRecuperarUrlPlantilla(String plantillaFluxId, String idioma, String returnUrl, boolean edicio) {

		String accioDescripcio = "Recuperant url flux de firma";
		long t0 = System.currentTimeMillis();
		String resposta = null;
		try {
			resposta = getPortafirmesPlugin().recuperarUrlViewEditPlantilla(plantillaFluxId, idioma, returnUrl, edicio);} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de portafirmes";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_PFIRMA, accioDescripcio, null, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, errorDescripcio, ex);
		}
		return resposta;
	}
	
	public List<PortafirmesFluxRespostaDto> portafirmesRecuperarPlantillesDisponibles(UsuariDto usuariActual, boolean filtrar) {

		String accioDescripcio = "Recuperant flux de firma";
		long t0 = System.currentTimeMillis();
		List<PortafirmesFluxRespostaDto> respostesDto = new ArrayList<PortafirmesFluxRespostaDto>();
		try {
			List<PortafirmesFluxResposta> plantilles = null;
			 if (filtrar) {
				plantilles = getPortafirmesPlugin().recuperarPlantillesPerFiltre(usuariActual.getIdioma(), usuariActual.getCodi());
			} else {
				plantilles = getPortafirmesPlugin().recuperarPlantillesDisponibles(usuariActual.getIdioma());
			}
			
			if (plantilles != null) {
				for (PortafirmesFluxResposta plantilla : plantilles) {
					PortafirmesFluxRespostaDto resposta = new PortafirmesFluxRespostaDto();
					resposta.setFluxId(plantilla.getFluxId());
					resposta.setNom(plantilla.getNom());
					respostesDto.add(resposta);
				}
			}
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de portafirmes";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_PFIRMA, accioDescripcio, null, IntegracioAccioTipusEnumDto.RECEPCIO, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, errorDescripcio, ex);
		}
		return respostesDto;
	}
	
	public boolean portafirmesEsborrarPlantillaFirma(String idioma, String plantillaFluxId) {

		String accioDescripcio = "Esborrant flux de firma";
		long t0 = System.currentTimeMillis();
		boolean esborrat;
		try {
			esborrat = getPortafirmesPlugin().esborrarPlantillaFirma(idioma, plantillaFluxId);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de portafirmes";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_PFIRMA, accioDescripcio, null, IntegracioAccioTipusEnumDto.RECEPCIO, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, errorDescripcio, ex);
		}
		return esborrat;
	}
	
	public List<PortafirmesBlockDto> portafirmesRecuperarBlocksFirma(String idPlantilla, String idTransaccio, boolean portafirmesFluxAsync, String portafirmesId, String idioma) {

		List<PortafirmesBlockDto> blocksDto = null;
		String accioDescripcio = "Tancant flux de firma";
		long t0 = System.currentTimeMillis();
		try {
			List<PortafirmesBlockInfo> portafirmesBlocks = getPortafirmesPlugin().recuperarBlocksFirmes(idPlantilla, idTransaccio, portafirmesFluxAsync,
																										new Long(portafirmesId), idioma);
			if (portafirmesBlocks != null) {
				blocksDto = new ArrayList<PortafirmesBlockDto>();
				for (PortafirmesBlockInfo portafirmesBlockInfo : portafirmesBlocks) {
					PortafirmesBlockDto blockDto = new PortafirmesBlockDto();
					List<PortafirmesBlockInfoDto> signersInfoDto = new ArrayList<PortafirmesBlockInfoDto>();
					if (portafirmesBlockInfo.getSigners() != null) {
						for (PortafirmesBlockSignerInfo portafirmesBlockSignerInfo : portafirmesBlockInfo.getSigners()) {
							PortafirmesBlockInfoDto signerInfoDto = new PortafirmesBlockInfoDto();
							signerInfoDto.setSignerCodi(portafirmesBlockSignerInfo.getSignerCodi());
							signerInfoDto.setSignerId(portafirmesBlockSignerInfo.getSignerId());
							signerInfoDto.setSignerNom(portafirmesBlockSignerInfo.getSignerNom());
							signersInfoDto.add(signerInfoDto);
						}
					}
					blockDto.setSigners(signersInfoDto);
					blocksDto.add(blockDto);
				}
			}
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de portafirmes";
			this.integracioHelper.addAccioError("PFIRMA", accioDescripcio, null, IntegracioAccioTipusEnumDto.ENVIAMENT,System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException("PFIRMA", errorDescripcio, ex);
		}
		return blocksDto;
	}

	public String conversioConvertirPdfArxiuNom(String nomOriginal) {
		return getConversioPlugin().getNomArxiuConvertitPdf(nomOriginal);
	}
	
	public FitxerDto conversioConvertirPdf(FitxerDto original, String urlPerEstampar) {

		String accioDescripcio = "Conversió de document a PDF";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("arxiuOriginalNom", original.getNom());
		accioParams.put("arxiuOriginalTamany", new Integer(original.getContingut().length).toString());
		long t0 = System.currentTimeMillis();
		try {
			ConversioArxiu convertit = getConversioPlugin().convertirPdfIEstamparUrl(new ConversioArxiu(original.getNom(), original.getContingut()), urlPerEstampar);
			accioParams.put("arxiuConvertitNom", convertit.getArxiuNom());
			accioParams.put("arxiuConvertitTamany", new Integer(convertit.getArxiuContingut().length).toString());
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_CONVERT, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			FitxerDto resposta = new FitxerDto();
			resposta.setNom(convertit.getArxiuNom());
			resposta.setContingut(convertit.getArxiuContingut());
			return resposta;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de conversió de documents: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_CONVERT, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_CONVERT, errorDescripcio, ex);
		}
	}

	public ProcedimentDto procedimentFindByCodiSia(String codiDir3, String codiSia) {

		String accioDescripcio = "Consulta del procediment pel codi SIA";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codiDir3", codiDir3);
		accioParams.put("codiSia", codiSia);
		long t0 = System.currentTimeMillis();
		try {
			ProcedimentDto procediment = getProcedimentPlugin().findAmbCodiSia(codiDir3, codiSia);
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_PROCEDIMENT, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			return procediment;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de procediments: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_PROCEDIMENT, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PROCEDIMENT, errorDescripcio, ex);
		}
	}

//	public RegistreAnotacioResposta registreEntradaConsultar(
//			String identificador,
//			String entitatCodi) {
//		String accioDescripcio = "Consulta d'una anotació d'entrada";
//		Map<String, String> accioParams = new HashMap<String, String>();
//		accioParams.put("identificador", identificador);
//		long t0 = System.currentTimeMillis();
//		try {
//			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//			RegistreAnotacioResposta resposta = getRegistrePlugin().entradaConsultar(
//					identificador,
//					auth.getName(),
//					entitatCodi);
//			integracioHelper.addAccioOk(
//					IntegracioHelper.INTCODI_REGISTRE,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0);
//			return resposta;
//		} catch (Exception ex) {
//			String errorDescripcio = "Error al accedir al plugin de registre";
//			integracioHelper.addAccioError(
//					IntegracioHelper.INTCODI_REGISTRE,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0,
//					errorDescripcio,
//					ex);
//			throw new SistemaExternException(
//					IntegracioHelper.INTCODI_REGISTRE,
//					errorDescripcio,
//					ex);
//		}
//	}

	/*public CiutadaExpedientInformacio ciutadaExpedientCrear(
			ExpedientEntity expedient,
			InteressatEntity destinatari) {
		MetaExpedientEntity metaExpedient = expedient.getMetaExpedient();
		String accioDescripcio = "Creació d'un expedient a la zona personal del ciutadà";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("expedientId", expedient.getId().toString());
		accioParams.put("expedientNumero", expedient.getNumero());
		accioParams.put("expedientTitol", expedient.getNom());
		accioParams.put("expedientTipusId", expedient.getMetaNode().getId().toString());
		accioParams.put("expedientTipusNom", expedient.getMetaNode().getNom());
		accioParams.put("unitatAdministrativa", metaExpedient.getUnitatAdministrativa());
		String idioma = getIdiomaPerPluginCiutada(destinatari.getPreferenciaIdioma());
		accioParams.put("idioma", idioma);
		accioParams.put("destinatari", destinatari.getIdentificador());
		long t0 = System.currentTimeMillis();
		try {
			String descripcio = "[" + expedient.getNumero() + "] " + expedient.getNom();
			String interessatMobil = null;
			if (destinatari.getTelefon() != null && isTelefonMobil(destinatari.getTelefon())) {
				interessatMobil = destinatari.getTelefon();
			}
			CiutadaExpedientInformacio expedientInfo = getCiutadaPlugin().expedientCrear(
					expedient.getNtiIdentificador(),
					metaExpedient.getUnitatAdministrativa(),
					metaExpedient.getClassificacioDocumental(),
					idioma,
					descripcio,
					toPluginCiutadaPersona(destinatari),
					null,
					expedient.getSistraBantelNum(),
					destinatari.isNotificacioAutoritzat(),
					destinatari.getEmail(),
					interessatMobil);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_CIUTADA,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return expedientInfo;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de comunicació amb el ciutadà";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_CIUTADA,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_CIUTADA,
					errorDescripcio,
					ex);
		}
	}

	public void ciutadaAvisCrear(
			ExpedientEntity expedient,
			String titol,
			String text,
			String textMobil) {
		MetaExpedientEntity metaExpedient = expedient.getMetaExpedient();
		String accioDescripcio = "Creació d'un avis a la zona personal del ciutadà";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("expedientId", expedient.getId().toString());
		accioParams.put("expedientNumero", expedient.getNumero());
		accioParams.put("expedientTitol", expedient.getNom());
		accioParams.put("expedientTipusId", expedient.getMetaNode().getId().toString());
		accioParams.put("expedientTipusNom", expedient.getMetaNode().getNom());
		accioParams.put("titol", titol);
		accioParams.put("text", text);
		accioParams.put("textMobil", textMobil);
		long t0 = System.currentTimeMillis();
		try {
			getCiutadaPlugin().avisCrear(
					expedient.getNtiIdentificador(),
					metaExpedient.getUnitatAdministrativa(),
					titol,
					text,
					textMobil,
					null);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_CIUTADA,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de comunicació amb el ciutadà";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_CIUTADA,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_CIUTADA,
					errorDescripcio,
					ex);
		}
	}

	public CiutadaNotificacioResultat ciutadaNotificacioEnviar(
			ExpedientEntity expedient,
			InteressatEntity destinatari,
			String oficiTitol,
			String oficiText,
			String avisTitol,
			String avisText,
			String avisTextMobil,
			InteressatIdiomaEnumDto idioma,
			boolean confirmarRecepcio,
			List<DocumentEntity> annexos) {
		MetaExpedientEntity metaExpedient = expedient.getMetaExpedient();
		String accioDescripcio = "Enviament d'una notificació electrònica al ciutadà";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("expedientId", expedient.getId().toString());
		accioParams.put("expedientNumero", expedient.getNumero());
		accioParams.put("expedientTitol", expedient.getNom());
		accioParams.put("expedientTipusId", expedient.getMetaNode().getId().toString());
		accioParams.put("expedientTipusNom", expedient.getMetaNode().getNom());
		accioParams.put("unitatAdministrativa", metaExpedient.getUnitatAdministrativa());
		accioParams.put("llibreCodi", metaExpedient.getNotificacioLlibreCodi());
		accioParams.put("organCodi", metaExpedient.getNotificacioOrganCodi());
		accioParams.put("destinatari", (destinatari != null) ? destinatari.getIdentificador() : "<null>");
		accioParams.put("idioma", idioma.name());
		accioParams.put("oficiTitol", oficiTitol);
		accioParams.put("avisTitol", avisTitol);
		accioParams.put("confirmarRecepcio", new Boolean(confirmarRecepcio).toString());
		if (annexos != null)
			accioParams.put("annexos (núm.)", new Integer(annexos.size()).toString());
		if (annexos != null) {
			StringBuilder annexosIds = new StringBuilder();
			StringBuilder annexosTitols = new StringBuilder();
			boolean primer = true;
			for (DocumentEntity annex: annexos) {
				if (!primer) {
					annexosIds.append(", ");
					annexosTitols.append(", ");
				}
				annexosIds.append(annex.getId());
				annexosTitols.append(annex.getNom());
				primer = false;
			}
			accioParams.put("annexosIds", annexosIds.toString());
			accioParams.put("annexosTitols", annexosTitols.toString());
		}
		long t0 = System.currentTimeMillis();
		try {
			List<CiutadaDocument> ciutadaAnnexos = null;
			if (annexos != null) {
				ciutadaAnnexos = new ArrayList<CiutadaDocument>();
				for (DocumentEntity annex: annexos) {
					if (DocumentTipusEnumDto.FISIC.equals(annex.getDocumentTipus())) {
						throw new ValidationException(
								annex.getId(),
								DocumentEntity.class,
								"No espoden emprar documents físics com annexos d'una notificació telemàtica");
					}
					CiutadaDocument cdoc = new CiutadaDocument();
					cdoc.setTitol(annex.getNom());
					FitxerDto fitxer = documentHelper.getFitxerAssociat(annex);
					cdoc.setArxiuNom(fitxer.getNom());
					cdoc.setArxiuContingut(fitxer.getContingut());
					ciutadaAnnexos.add(cdoc);
				}
			}
			CiutadaNotificacioResultat resultat = getCiutadaPlugin().notificacioCrear(
					expedient.getNtiIdentificador(),
					expedient.getSistraUnitatAdministrativa(),
					metaExpedient.getNotificacioLlibreCodi(),
					metaExpedient.getNotificacioOrganCodi(),
					toPluginCiutadaPersona(destinatari),
					null,
					getIdiomaPerPluginCiutada(idioma),
					oficiTitol,
					oficiText,
					avisTitol,
					avisText,
					avisTextMobil,
					confirmarRecepcio,
					ciutadaAnnexos);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_CIUTADA,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return resultat;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de comunicació amb el ciutadà";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_CIUTADA,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_CIUTADA,
					errorDescripcio,
					ex);
		}
	}

	public CiutadaNotificacioEstat ciutadaNotificacioComprovarEstat(
			ExpedientEntity expedient,
			String registreNumero) {
		String accioDescripcio = "Comprovació de l'estat de la notificació";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("expedientId", expedient.getId().toString());
		accioParams.put("expedientNumero", expedient.getNumero());
		accioParams.put("expedientTitol", expedient.getNom());
		accioParams.put("expedientTipusId", expedient.getMetaNode().getId().toString());
		accioParams.put("expedientTipusNom", expedient.getMetaNode().getNom());
		accioParams.put("registreNumero", registreNumero);
		long t0 = System.currentTimeMillis();
		try {
			CiutadaNotificacioEstat justificant = getCiutadaPlugin().notificacioObtenirJustificantRecepcio(
					registreNumero);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_CIUTADA,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return justificant;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de comunicació amb el ciutadà";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_CIUTADA,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_CIUTADA,
					errorDescripcio,
					ex);
		}
	}*/

	public List<Pais> dadesExternesPaisosFindAll() {

		String accioDescripcio = "Consulta de tots els paisos";
		long t0 = System.currentTimeMillis();
		try {
			List<Pais> paisos = getDadesExternesPlugin().paisFindAll();
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_DADESEXT, accioDescripcio, null, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			return paisos;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_DADESEXT, accioDescripcio, null, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_DADESEXT, errorDescripcio, ex);
		}
	}

	public List<ComunitatAutonoma> dadesExternesComunitatsFindAll() {

		String accioDescripcio = "Consulta de totes les comunitats";
		long t0 = System.currentTimeMillis();
		try {
			List<ComunitatAutonoma> comunitats = getDadesExternesPlugin().comunitatFindAll();
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_DADESEXT, accioDescripcio, null, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			return comunitats;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_DADESEXT, accioDescripcio, null, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_DADESEXT, errorDescripcio, ex);
		}
	}

	public List<Provincia> dadesExternesProvinciesFindAll() {

		String accioDescripcio = "Consulta de totes les províncies";
		long t0 = System.currentTimeMillis();
		try {
			List<Provincia> provincies = getDadesExternesPlugin().provinciaFindAll();
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_DADESEXT, accioDescripcio, null, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			return provincies;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_DADESEXT, accioDescripcio, null, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_DADESEXT, errorDescripcio, ex);
		}
	}

	public List<Provincia> dadesExternesProvinciesFindAmbComunitat(String comunitatCodi) {

		String accioDescripcio = "Consulta de les províncies d'una comunitat";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("comunitatCodi", comunitatCodi);
		long t0 = System.currentTimeMillis();
		try {
			List<Provincia> provincies = getDadesExternesPlugin().provinciaFindByComunitat(comunitatCodi);
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_DADESEXT, accioDescripcio, null, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			return provincies;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_DADESEXT, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_DADESEXT, errorDescripcio, ex);
		}
	}

	public List<Municipi> dadesExternesMunicipisFindAmbProvincia(String provinciaCodi) {

		String accioDescripcio = "Consulta dels municipis d'una província";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("provinciaCodi", provinciaCodi);
		long t0 = System.currentTimeMillis();
		try {
			List<Municipi> municipis = getDadesExternesPlugin().municipiFindByProvincia(provinciaCodi);
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_DADESEXT, accioDescripcio, null, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			return municipis;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_DADESEXT, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_DADESEXT, errorDescripcio, ex);
		}
	}
	
	public List<Municipi> dadesExternesMunicipisFindAmbProvinciaPinbal(String provinciaCodi) {

		String accioDescripcio = "Consulta dels municipis d'una província per consultes a PINBAL";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("provinciaCodi", provinciaCodi);
		long t0 = System.currentTimeMillis();
		try {
			List<Municipi> municipis = getDadesExternesPinbalPlugin().municipiFindByProvincia(provinciaCodi);
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_DADESEXT, accioDescripcio, null, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			return municipis;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_DADESEXT, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_DADESEXT, errorDescripcio, ex);
		}
	}

	public List<NivellAdministracioDto> dadesExternesNivellsAdministracioAll() {

		String accioDescripcio = "Consulta de nivells d'administració";
		Map<String, String> accioParams = new HashMap<String, String>();
		long t0 = System.currentTimeMillis();
		try {
			List<NivellAdministracioDto> nivellAdministracio = conversioTipusHelper.convertirList(getDadesExternesPlugin().nivellAdministracioFindAll(), NivellAdministracioDto.class);
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_DADESEXT, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.RECEPCIO, System.currentTimeMillis() - t0);
			return nivellAdministracio;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_DADESEXT, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.RECEPCIO, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_DADESEXT, errorDescripcio, ex);
		}
	}

	public List<TipusViaDto> dadesExternesTipusViaAll() {

		String accioDescripcio = "Consulta de tipus de via";
		Map<String, String> accioParams = new HashMap<String, String>();
		long t0 = System.currentTimeMillis();
		try {
			List<TipusViaDto> tipusVies = conversioTipusHelper.convertirList(getDadesExternesPlugin().tipusViaFindAll(), TipusViaDto.class);
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_DADESEXT, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.RECEPCIO, System.currentTimeMillis() - t0);
			return tipusVies;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_DADESEXT, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.RECEPCIO, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_DADESEXT, errorDescripcio, ex);
		}
	}

	public boolean isRegistreSignarAnnexos() {
		return this.getPropertyPluginRegistreSignarAnnexos();
	}
	
	public SignatureInfoDto detectSignedAttachedUsingPdfReader(byte[] documentContingut, String contentType) {
		
		boolean isSigned = isFitxerSigned(documentContingut, contentType);
		boolean validationError = false; // !isSigned;
		String validationErrorMsg = ""; // "error error error error error error ";

		return new SignatureInfoDto(isSigned, validationError, validationErrorMsg);
	}
	
	private boolean isFitxerSigned(byte[] contingut, String contentType) {

		if (contentType.equals("application/pdf")) {
			PdfReader reader;
			try {
				reader = new PdfReader(contingut);
				AcroFields acroFields = reader.getAcroFields();
				List<String> signatureNames = acroFields.getSignatureNames();
				if (signatureNames != null && !signatureNames.isEmpty()) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			return false;
		}
	}	
	
	public SignatureInfoDto detectSignedAttachedUsingValidateSignaturePlugin(

			byte[] documentContingut,
			String firmaContentType) {
		try {
			ValidateSignatureRequest validationRequest = new ValidateSignatureRequest();
			validationRequest.setSignatureData(documentContingut);
			SignatureRequestedInformation sri = new SignatureRequestedInformation();
			sri.setReturnSignatureTypeFormatProfile(true);
			sri.setReturnCertificateInfo(true);
			sri.setReturnValidationChecks(false);
			sri.setValidateCertificateRevocation(false);
			sri.setReturnCertificates(false);
			sri.setReturnTimeStampInfo(true);
			validationRequest.setSignatureRequestedInformation(sri);
			ValidateSignatureResponse validateSignatureResponse = getValidaSignaturaPlugin().validateSignature(validationRequest);

			ValidationStatus validationStatus = validateSignatureResponse.getValidationStatus();
			if (validationStatus.getStatus() == 1) {
				return new SignatureInfoDto(true, false, null);
			} else {
				return new SignatureInfoDto(true, true, validationStatus.getErrorMsg());
			}
		} catch (Exception e) {
			Throwable throwable = ExceptionHelper.getRootCauseOrItself(e);
			if (throwable.getMessage().contains("El formato de la firma no es valido(urn:oasis:names:tc:dss:1.0:resultmajor:RequesterError)") || throwable.getMessage().contains("El formato de la firma no es válido(urn:oasis:names:tc:dss:1.0:resultmajor:RequesterError)") || throwable.getMessage().contains("El documento OOXML no está firmado(urn:oasis:names:tc:dss:1.0:resultmajor:ResponderError)")) {
				return new SignatureInfoDto(false, false, null);
			} else {
				logger.error("Error al detectar firma de document", e);
				return new SignatureInfoDto(false, true, e.getMessage());
			}
		}
	}
	
	
	
	

	public List<ArxiuFirmaDto> validaSignaturaObtenirFirmes(byte[] documentContingut, byte[] firmaContingut, String firmaContentType, boolean throwExceptionIfNotValid) {

		String accioDescripcio = "Obtenir informació de document firmat";
		Map<String, String> accioParams = new HashMap<String, String>();
		if (documentContingut != null) {
			accioParams.put("documentContingut", documentContingut.length + " bytes");
		} else {
			accioParams.put("documentContingut", "<null>");
		}
		if (firmaContingut != null) {
			accioParams.put("firmaContingut", firmaContingut.length + " bytes");
		} else {
			accioParams.put("firmaContingut", "<null>");
		}
		accioParams.put("firmaContentType", firmaContentType);
		long t0 = System.currentTimeMillis();
		try {
			ValidateSignatureRequest validationRequest = new ValidateSignatureRequest();
			if (firmaContingut != null) {
				validationRequest.setSignedDocumentData(documentContingut);
				validationRequest.setSignatureData(firmaContingut);
			} else {
				validationRequest.setSignatureData(documentContingut);
			}
			SignatureRequestedInformation sri = new SignatureRequestedInformation();
			sri.setReturnSignatureTypeFormatProfile(true);
			sri.setReturnCertificateInfo(true);
			sri.setReturnValidationChecks(false);
			sri.setValidateCertificateRevocation(false);
			sri.setReturnCertificates(false);
			sri.setReturnTimeStampInfo(true);
			validationRequest.setSignatureRequestedInformation(sri);
			ValidateSignatureResponse validateSignatureResponse = getValidaSignaturaPlugin().validateSignature(validationRequest);

			ValidationStatus validationStatus = validateSignatureResponse.getValidationStatus();
			if (validationStatus.getStatus() != 1 && throwExceptionIfNotValid) {
				throw new RuntimeException(validationStatus.getErrorMsg());
			}

			List<ArxiuFirmaDetallDto> detalls = new ArrayList<ArxiuFirmaDetallDto>();
			List<ArxiuFirmaDto> firmes = new ArrayList<ArxiuFirmaDto>();
			ArxiuFirmaDto firma = new ArxiuFirmaDto();
			if (validateSignatureResponse.getSignatureDetailInfo() != null) {
				for (SignatureDetailInfo signatureInfo: validateSignatureResponse.getSignatureDetailInfo()) {
					ArxiuFirmaDetallDto detall = new ArxiuFirmaDetallDto();
					signatureInfo.getSignDate();
					TimeStampInfo timeStampInfo = signatureInfo.getTimeStampInfo();
					if (timeStampInfo != null) {
						detall.setData(timeStampInfo.getCreationTime());
					} else {
						detall.setData(signatureInfo.getSignDate());
					}
					InformacioCertificat certificateInfo = signatureInfo.getCertificateInfo();
					if (certificateInfo != null) {
						if (certificateInfo.getNifResponsable() != null)
							detall.setResponsableNif(certificateInfo.getNifResponsable());
						else
							detall.setResponsableNif(certificateInfo.getEntitatSubscriptoraNif());
						if (certificateInfo.getNomCompletResponsable() != null)
							detall.setResponsableNom(certificateInfo.getNomCompletResponsable());
						else
							detall.setResponsableNom(certificateInfo.getEntitatSubscriptoraNom());
						detall.setEmissorCertificat(certificateInfo.getEmissorOrganitzacio());
					}
					detalls.add(detall);
				}
				firma.setAutofirma(false);
				if (firmaContingut != null) {
					firma.setContingut(firmaContingut);
				} else {
					firma.setContingut(documentContingut);
				}
				firma.setDetalls(detalls);
				firma.setPerfil(ArxiuConversions.toArxiuFirmaPerfilEnum(validateSignatureResponse.getSignProfile()));
				firma.setTipus(ArxiuConversions.toArxiuFirmaTipusEnum(
						validateSignatureResponse.getSignType(),
						validateSignatureResponse.getSignFormat()));
				firma.setTipusMime(firmaContentType);
				firmes.add(firma);
			}
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_VALIDASIG, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			return firmes;
		} catch (Exception ex) {
			String errorDescripcio = "Error validant la firma del document: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_VALIDASIG, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.RECEPCIO, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_VALIDASIG, errorDescripcio, ex);
		}
	}

	public RespostaEnviar notificacioEnviar(DocumentNotificacioDto notificacioDto, ExpedientEntity expedientEntity, DocumentEntity documentEntity, InteressatEntity interessat) {

		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(expedientEntity.getId()));
		
		MetaExpedientEntity metaExpedient = expedientEntity.getMetaExpedient();
		String accioDescripcio = "Enviament d'una notificació electrònica";
		Map<String, String> accioParams = getNotificacioAccioParams(notificacioDto, expedientEntity, documentEntity, interessat);
		long t0 = System.currentTimeMillis();
		try {
			Notificacio notificacio = new Notificacio();
			String forsarEntitat = getPropertyNotificacioForsarEntitat();
			if (forsarEntitat != null) {
				notificacio.setEmisorDir3Codi(forsarEntitat);
			} else {
				notificacio.setEmisorDir3Codi(expedientEntity.getEntitat().getUnitatArrel());
			}
			OrganGestorEntity organGestor = expedientEntity.getOrganGestor();
			if (organGestor != null) {
				notificacio.setOrganGestor(organGestor.getCodi());
			} else {
				notificacio.setOrganGestor(notificacio.getEmisorDir3Codi());
			}
			notificacio.setEnviamentTipus(notificacioDto.getTipus() != null ? EnviamentTipus.valueOf(notificacioDto.getTipus().toString()) : null);
			notificacio.setConcepte(notificacioDto.getAssumpte());
			notificacio.setDescripcio(notificacioDto.getObservacions());
			notificacio.setEnviamentDataProgramada(notificacioDto.getDataProgramada());
			notificacio.setRetard((notificacioDto.getRetard() != null) ? notificacioDto.getRetard() : getPropertyNotificacioRetardNumDies());
			if (notificacioDto.getDataCaducitat() != null) {
				notificacio.setCaducitat(notificacioDto.getDataCaducitat());
			} else {
				Integer numDies = getPropertyNotificacioCaducitatNumDies();
				if (numDies != null) {
					Date dataInicial = (notificacioDto.getDataProgramada() != null) ? notificacioDto.getDataProgramada() : new Date();
					Calendar cal = Calendar.getInstance();
					cal.setTime(dataInicial);
					cal.add(Calendar.DAY_OF_MONTH, numDies);
					notificacio.setCaducitat(cal.getTime());
				}
			}
			if (documentEntity.getDocumentTipus().equals(DocumentTipusEnumDto.VIRTUAL)) {
				FitxerDto fitxer = documentHelper.getFitxerAssociat(documentEntity, null);
				notificacio.setDocumentArxiuNom(fitxer.getNom());
				notificacio.setDocumentArxiuContingut(fitxer.getContingut());
			} else {
				// fitxer = arxiuDocumentVersioImprimible(documentEntity);
				notificacio.setDocumentArxiuNom(documentEntity.getFitxerNom());
				notificacio.setDocumentArxiuUuid(documentEntity.getArxiuUuid());
			}
			notificacio.setProcedimentCodi(metaExpedient.getClassificacioSia());
			notificacio.setNumExpedient(expedientEntity.getNumero());
			UsuariDto usuari = aplicacioService.getUsuariActual();
			List<Enviament> enviaments = new ArrayList<>();

			// ===== INTERESSAT TO ENVIAMENT
			Enviament enviament = new Enviament();
			enviament.setTitular(convertirAmbPersona(interessat));

//			if (interessat.getIncapacitat() != null && interessat.getIncapacitat() == true) {
				if (interessat.getRepresentant() != null) {
					enviament.setDestinataris(Arrays.asList(convertirAmbPersona(interessat.getRepresentant())));
				}
//			}

			// ########## ENTREGA POSTAL  ###############
			if (notificacioDto.isEntregaPostal()) {
				enviament.setEntregaPostalActiva(true);
				enviament.setEntregaPostalTipus(EntregaPostalTipus.SENSE_NORMALITZAR);
				InteressatEntity interessatPerAdresa = interessat;
				if (interessat.getRepresentant() != null) {
					interessatPerAdresa = interessat.getRepresentant();
				}
				PaisDto pais = dadesExternesHelper.getPaisAmbCodi(interessatPerAdresa.getPais());
				if (pais == null) {
					throw new NotFoundException(interessatPerAdresa.getPais(), PaisDto.class);
				}

				ProvinciaDto provincia = dadesExternesHelper.getProvinciaAmbCodi(interessatPerAdresa.getProvincia());
				if (provincia == null) {
					throw new NotFoundException(interessatPerAdresa.getProvincia(), ProvinciaDto.class);
				}
				MunicipiDto municipi = null;
				if (interessatPerAdresa.getMunicipi() != null) {
					municipi = dadesExternesHelper.getMunicipiAmbCodi(interessatPerAdresa.getProvincia(), interessatPerAdresa.getMunicipi());
				}
				if (municipi == null) {
					throw new NotFoundException(interessatPerAdresa.getMunicipi(), MunicipiDto.class);
				}

				enviament.setEntregaPostalCodiPostal(interessatPerAdresa.getCodiPostal());
				enviament.setEntregaPostalPaisCodi(pais.getAlfa2());
				enviament.setEntregaPostalProvinciaCodi(provincia.getCodi());
				enviament.setEntregaPostalMunicipiCodi(provincia.getCodi() + String.format("%04d", Integer.parseInt(municipi.getCodi())));
				enviament.setEntregaPostalLinea1(interessatPerAdresa.getAdresa() + ", " + interessatPerAdresa.getCodiPostal() + ", " + municipi.getNom());
				enviament.setEntregaPostalLinea2(provincia.getNom() + ", " + pais.getNom());
			}
			// ########## ENVIAMENT DEH  ###############
			if (interessat.getEntregaDeh() != null && interessat.getEntregaDeh()) {
				enviament.setEntregaDehActiva(true);
				enviament.setEntregaDehObligat(interessat.getEntregaDehObligat());
				enviament.setEntregaDehProcedimentCodi(metaExpedient.getClassificacioSia());
				enviament.setEntregaNif(usuari.getNif());
			}
			enviaments.add(enviament);
			notificacio.setEnviaments(enviaments);
			notificacio.setUsuariCodi(usuari.getCodi());
			notificacio.setServeiTipusEnum(notificacioDto.getServeiTipusEnum());

			// ############## ALTA NOTIFICACIO #######################
			RespostaEnviar respostaEnviar = getNotificacioPlugin().enviar(notificacio);
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_NOTIFICACIO, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			return respostaEnviar;

		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de notificacions";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_NOTIFICACIO, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.RECEPCIO, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_NOTIFICACIO, errorDescripcio, ex);
		}
	}

	public byte[] notificacioConsultarIDescarregarCertificacio(
			DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity) {

		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(documentEnviamentInteressatEntity.getNotificacio().getExpedient().getId()));
		
		RespostaConsultaEstatEnviament resposta;
		try {
			resposta = getNotificacioPlugin().consultarEnviament(documentEnviamentInteressatEntity.getEnviamentReferencia());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		byte[] certificacio = resposta.getCertificacioContingut();
		return certificacio;
	}

	public RespostaConsultaInfoRegistre notificacioConsultarIDescarregarJustificant(DocumentEnviamentInteressatEntity documentEnviamentEtity) {

		try {
			return getNotificacioPlugin().consultarRegistreInfo(null, documentEnviamentEtity.getEnviamentReferencia(), true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public RespostaJustificantEnviamentNotib notificacioDescarregarJustificantEnviamentNotib(String identificador) {

		try {
			return getNotificacioPlugin().consultaJustificantEnviament(identificador);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	

	public RespostaConsultaEstatEnviament notificacioConsultarIActualitzarEstat(DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity) {
		ConfigHelper.setEntitat(conversioTipusHelper.convertir(documentEnviamentInteressatEntity.getNotificacio().getExpedient().getEntitat(), EntitatDto.class));
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(documentEnviamentInteressatEntity.getNotificacio().getExpedient().getId()));
		
		DocumentNotificacioEntity notificacio = documentEnviamentInteressatEntity.getNotificacio();
		RespostaConsultaEstatEnviament resposta = null;
		String accioDescripcio = "Consulta d'estat d'una notificació electrònica";
		Map<String, String> accioParams = getAccioParams(documentEnviamentInteressatEntity);
		long t0 = System.currentTimeMillis();
		try {
			resposta = getNotificacioPlugin().consultarEnviament(documentEnviamentInteressatEntity.getEnviamentReferencia());
			String gestioDocumentalId = notificacio.getEnviamentCertificacioArxiuId();
			if (!getPropertyGuardarCertificacioExpedient() && resposta.getCertificacioData() != null) {
				byte[] certificacio = resposta.getCertificacioContingut();
				if (gestioDocumentalId != null && documentEnviamentInteressatEntity.getEnviamentCertificacioData().before(resposta.getCertificacioData())) {
					gestioDocumentalDelete(notificacio.getEnviamentCertificacioArxiuId(), GESDOC_AGRUPACIO_CERTIFICACIONS);
				}
				if (gestioDocumentalId == null || documentEnviamentInteressatEntity.getEnviamentCertificacioData().before(resposta.getCertificacioData())) {
					gestioDocumentalId = gestioDocumentalCreate(PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS, new ByteArrayInputStream(certificacio));
				}
			}
			documentEnviamentInteressatEntity.updateEnviamentEstat(
					resposta.getEstat(),
					resposta.getEstatData(),
					resposta.getEstatOrigen(),
					documentEnviamentInteressatEntity.getEnviamentCertificacioData(),
					resposta.getCertificacioOrigen(),
					resposta.isError(),
					resposta.getErrorDescripcio());

			actualitzarDadesRegistre(documentEnviamentInteressatEntity);
			RespostaConsultaEstatNotificacio respostaNotificioEstat = getNotificacioPlugin().consultarNotificacio(documentEnviamentInteressatEntity.getNotificacio().getEnviamentIdentificador());
			notificacio.updateNotificacioEstat(respostaNotificioEstat.getEstat(), resposta.getEstatData(), respostaNotificioEstat.isError(),
												respostaNotificioEstat.getErrorDescripcio(), gestioDocumentalId);
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_NOTIFICACIO, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de notificacions";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_NOTIFICACIO, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.RECEPCIO, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_NOTIFICACIO, errorDescripcio, ex);
		}
		return resposta;
	}

	public void actualitzarDadesRegistre(DocumentEnviamentInteressatEntity enviament) {

		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(enviament.getNotificacio().getExpedient().getId()));
		
		String accioDescripcio = "Consulta dades registre de l'enviament amb referència: " + enviament.getEnviamentReferencia();
		Map<String, String> accioParams = getAccioParams(enviament);
		long t0 = System.currentTimeMillis();
		try {
			RespostaConsultaInfoRegistre respostaInfoRegistre = getNotificacioPlugin().consultarRegistreInfo(null, enviament.getEnviamentReferencia(),false);
			if (respostaInfoRegistre != null) {
				enviament.updateEnviamentInfoRegistre(respostaInfoRegistre.getDataRegistre(), respostaInfoRegistre.getNumRegistre(), respostaInfoRegistre.getNumRegistreFormatat());
			}
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_NOTIFICACIO, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de notificacions";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_NOTIFICACIO, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.RECEPCIO, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_NOTIFICACIO, errorDescripcio, ex);
		}
	}
	public String gestioDocumentalCreate(String agrupacio, InputStream contingut) {

		try {
			String gestioDocumentalId = getGestioDocumentalPlugin().create(agrupacio, contingut);
			return gestioDocumentalId;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de gestió documental";
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESDOC, errorDescripcio, ex);
		}
	}

	public void gestioDocumentalUpdate(String id, String agrupacio, InputStream contingut) {
		try {
			getGestioDocumentalPlugin().update(id, agrupacio, contingut);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de gestió documental";
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESDOC, errorDescripcio, ex);
		}
	}
	public void gestioDocumentalDelete(String id, String agrupacio) {
		try {
			getGestioDocumentalPlugin().delete(id, agrupacio);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de gestió documental";
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESDOC, errorDescripcio, ex);
		}
	}
	public void gestioDocumentalGet(String id, String agrupacio, OutputStream contingutOut) {

		try {
			getGestioDocumentalPlugin().get(id, agrupacio, contingutOut);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de gestió documental";
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESDOC, errorDescripcio, ex);
		}
	}

	public SignaturaResposta firmaServidorFirmar(DocumentEntity document, FitxerDto fitxer, String motiu, String idioma) {

		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(document.getId()));
		String accioDescripcio = "Firma en servidor d'un document";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", document.getId().toString());
		accioParams.put("títol", document.getNom());
		long t0 = System.currentTimeMillis();
		try {
			SignaturaResposta resposta = getFirmaServidorPlugin().firmar(fitxer.getNom(), motiu, fitxer.getContingut(), idioma);
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_FIRMASERV, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT,System.currentTimeMillis() - t0);
			return resposta;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de firma en servidor: " + ex.getMessage();
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_FIRMASERV, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_FIRMASERV, errorDescripcio, ex);
		}
	}

	public String viaFirmaUpload(DocumentEntity document, DocumentViaFirmaEntity documentViaFirmaEntity) {

		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(document.getId()));
		ViaFirmaParams parametresViaFirma = new ViaFirmaParams();
		ViaFirmaDispositiu viaFirmaDispositiu = new ViaFirmaDispositiu();
		ViaFirmaResponse viaFirmaResponse;
		String accioDescripcio = "Enviament de document a firmar";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("documentId", document.getId().toString());
		accioParams.put("documentTitol", document.getNom());
		long t0 = System.currentTimeMillis();
		FitxerDto fitxerOriginal = documentHelper.getFitxerAssociat(document, null);
		FitxerDto fitxerConvertit = this.conversioConvertirPdf(fitxerOriginal, null);
		try {
			DispositiuEnviamentEntity dispositiu = documentViaFirmaEntity.getDispositiuEnviament();
			if (dispositiu != null) {
				viaFirmaDispositiu.setCodi(dispositiu.getCodi());
				viaFirmaDispositiu.setCodiAplicacio(dispositiu.getCodiAplicacio());
				viaFirmaDispositiu.setCodiUsuari(dispositiu.getCodiUsuari());
				viaFirmaDispositiu.setDescripcio(dispositiu.getDescripcio());
				viaFirmaDispositiu.setEmailUsuari(dispositiu.getEmailUsuari());
				viaFirmaDispositiu.setEstat(dispositiu.getEstat());
				viaFirmaDispositiu.setIdentificador(dispositiu.getIdentificador());
				viaFirmaDispositiu.setIdentificadorNacional(dispositiu.getIdentificadorNacional());
				viaFirmaDispositiu.setLocal(dispositiu.getLocal());
				viaFirmaDispositiu.setTipus(dispositiu.getTipus());
				viaFirmaDispositiu.setToken(dispositiu.getToken());
			}
			String encodedBase64 = new String(Base64.encodeBase64(fitxerConvertit.getContingut()));
			parametresViaFirma.setContingut(encodedBase64);
			parametresViaFirma.setCodiUsuari(documentViaFirmaEntity.getCodiUsuari());
			parametresViaFirma.setContrasenya(documentViaFirmaEntity.getContrasenyaUsuariViaFirma());
			parametresViaFirma.setDescripcio(documentViaFirmaEntity.getDescripcio());
			parametresViaFirma.setLecturaObligatoria(documentViaFirmaEntity.isLecturaObligatoria());
			parametresViaFirma.setTitol(documentViaFirmaEntity.getTitol());
			parametresViaFirma.setViaFirmaDispositiu(viaFirmaDispositiu);
			parametresViaFirma.setExpedientCodi(document.getExpedient().getNumero());
			parametresViaFirma.setSignantNif(documentViaFirmaEntity.getSignantNif());
			parametresViaFirma.setSignantNom(documentViaFirmaEntity.getSignantNom());
			parametresViaFirma.setObservaciones(documentViaFirmaEntity.getObservacions());
			parametresViaFirma.setValidateCodeEnabled(documentViaFirmaEntity.isValidateCodeEnabled());
			parametresViaFirma.setValidateCode(documentViaFirmaEntity.getValidateCode());
			parametresViaFirma.setDeviceEnabled(getPropertyViaFirmaDispositius());
			viaFirmaResponse = getViaFirmaPlugin().uploadDocument(parametresViaFirma);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de viaFirma";
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_VIAFIRMA, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_VIAFIRMA, errorDescripcio, ex);
		}
		return viaFirmaResponse.getCodiMissatge();
	}

	public ViaFirmaDocument viaFirmaDownload(DocumentViaFirmaEntity documentViaFirma) {

		String accioDescripcio = "Descarregar document firmat";
		Map<String, String> accioParams = new HashMap<String, String>();
		DocumentEntity document = documentViaFirma.getDocument();
		accioParams.put("documentVersioId", document.getId().toString());
		accioParams.put("documentPortafirmesId", documentViaFirma.getId().toString());
		accioParams.put("messageCode", documentViaFirma.getMessageCode());
		long t0 = System.currentTimeMillis();
		ViaFirmaDocument viaFirmaDocument = null;
		try {
			viaFirmaDocument = getViaFirmaPlugin().downloadDocument(documentViaFirma.getCodiUsuari(), documentViaFirma.getContrasenyaUsuariViaFirma(), documentViaFirma.getMessageCode());
			integracioHelper.addAccioOk(IntegracioHelper.INTCODI_VIAFIRMA, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0);
			return viaFirmaDocument;
		} catch (Exception ex) {
			String errorDescripcio = "Error al descarregar el document firmat";
			document.updateEstat(DocumentEstatEnumDto.FIRMA_PENDENT_VIAFIRMA);
			integracioHelper.addAccioError(IntegracioHelper.INTCODI_VIAFIRMA, accioDescripcio, accioParams, IntegracioAccioTipusEnumDto.ENVIAMENT, System.currentTimeMillis() - t0, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_VIAFIRMA, errorDescripcio, ex);
		}
	}

	public List<ViaFirmaDispositiuDto> getDeviceUser(String codiUsuari, String contasenya) {

		List<ViaFirmaDispositiuDto> viaFirmaDispositiusDto = new ArrayList<>();
		try {
			List<ViaFirmaDispositiu> viaFirmaDispositius = getViaFirmaPlugin().getDeviceUser(codiUsuari, contasenya);
			viaFirmaDispositiusDto = conversioTipusHelper.convertirList(viaFirmaDispositius, ViaFirmaDispositiuDto.class);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de viaFirma";
			throw new SistemaExternException(IntegracioHelper.INTCODI_VIAFIRMA, errorDescripcio, ex);
		}
		return viaFirmaDispositiusDto;
	}

	private ArbreNodeDto<UnitatOrganitzativaDto> getNodeArbreUnitatsOrganitzatives(UnitatOrganitzativa unitatOrganitzativa, List<UnitatOrganitzativa> unitatsOrganitzatives,
																				   ArbreNodeDto<UnitatOrganitzativaDto> pare) {

		ArbreNodeDto<UnitatOrganitzativaDto> resposta = new ArbreNodeDto<UnitatOrganitzativaDto>(pare, conversioTipusHelper.convertir(unitatOrganitzativa, UnitatOrganitzativaDto.class));
		String codiUnitat = (unitatOrganitzativa != null) ? unitatOrganitzativa.getCodi() : null;
		for (UnitatOrganitzativa uo: unitatsOrganitzatives) {
			if ((codiUnitat == null && uo.getCodiUnitatSuperior() == null) || (uo.getCodiUnitatSuperior() != null && uo.getCodiUnitatSuperior().equals(codiUnitat))) {
				resposta.addFill(getNodeArbreUnitatsOrganitzatives(uo, unitatsOrganitzatives, resposta));
			}
		}
		return resposta;
	}

	/*private CiutadaPersona toPluginCiutadaPersona(
			InteressatEntity interessat) {
		if (interessat == null)
			return null;
		if (	!InteressatDocumentTipusEnumDto.NIF.equals(interessat.getDocumentTipus()) &&
				!InteressatDocumentTipusEnumDto.CIF.equals(interessat.getDocumentTipus())) {
			throw new ValidationException(
					interessat.getId(),
					InteressatEntity.class,
					"No es pot notificar a interessats amb el tipus de document " + interessat.getDocumentTipus());
		}
		CiutadaPersona persona = new CiutadaPersona();
		if (interessat instanceof InteressatPersonaFisicaEntity) {
			InteressatPersonaFisicaEntity interessatPf = (InteressatPersonaFisicaEntity)interessat;
			persona.setNif(interessatPf.getDocumentNum());
			persona.setNom(interessatPf.getNom());
			persona.setLlinatge1(interessatPf.getLlinatge1());
			persona.setLlinatge2(interessatPf.getLlinatge2());
			persona.setPaisCodi(interessat.getPais());
			persona.setProvinciaCodi(interessat.getProvincia());
			persona.setMunicipiCodi(interessat.getMunicipi());
		} else if (interessat instanceof InteressatPersonaJuridicaEntity) {
			InteressatPersonaFisicaEntity interessatPj = (InteressatPersonaFisicaEntity)interessat;
			persona.setNif(interessatPj.getDocumentNum());
			persona.setNom(interessatPj.getNom());
			persona.setPaisCodi(interessat.getPais());
			persona.setProvinciaCodi(interessat.getProvincia());
			persona.setMunicipiCodi(interessat.getMunicipi());
		} else if (interessat instanceof InteressatAdministracioEntity) {
			throw new ValidationException(
					interessat.getId(),
					InteressatEntity.class,
					"Els interessats de les notificacions només poden ser persones físiques o jurídiques");
		}
		return persona;
	}

	private String getIdiomaPerPluginCiutada(InteressatIdiomaEnumDto idioma) {
		switch (idioma) {
		case CA:
			return "ca";
		case ES:
			return "es";
		default:
			return "ca";
		}
	}

	private static final Pattern MOBIL_PATTERN = Pattern.compile("(\\+34|0034|34)?[ -]*(6|7)([0-9]){2}[ -]?(([0-9]){2}[ -]?([0-9]){2}[ -]?([0-9]){2}|([0-9]){3}[ -]?([0-9]){3})");
	private boolean isTelefonMobil(String telefon) {
		return MOBIL_PATTERN.matcher(telefon).matches();
	}*/

	private Long toLongValue(String text) {
		return text == null || text.isEmpty() ? null : Long.parseLong(text);
	}

	private Expedient toArxiuExpedient(String identificador, String nom, String ntiIdentificador, List<String> ntiOrgans, Date ntiDataObertura, String ntiClassificacio,
									   ExpedientEstatEnumDto ntiEstat, List<String> ntiInteressats, String serieDocumental) {

		Expedient expedient = new Expedient();
		expedient.setNom(nom);
		expedient.setIdentificador(identificador);
		ExpedientMetadades metadades = new ExpedientMetadades();
		metadades.setIdentificador(ntiIdentificador);
		metadades.setDataObertura(ntiDataObertura);
		metadades.setClassificacio(ntiClassificacio);
		if (ntiEstat != null) {
			switch (ntiEstat) {
			case OBERT:
				metadades.setEstat(ExpedientEstat.OBERT);
				break;
			case TANCAT:
				metadades.setEstat(ExpedientEstat.TANCAT);
				break;
			}
		}
		metadades.setOrgans(ntiOrgans);
		metadades.setInteressats(ntiInteressats);
		metadades.setSerieDocumental(serieDocumental);
		expedient.setMetadades(metadades);
		return expedient;
	}

	private Carpeta toArxiuCarpeta(String identificador, String nom) {

		Carpeta carpeta = new Carpeta();
		carpeta.setIdentificador(identificador);
		carpeta.setNom(nom);
		return carpeta;
	}

	
	private Document toArxiuDocument(
			DocumentEntity documentEntity,
			ContingutEntity contingutPare,
			FitxerDto fitxer,
			DocumentFirmaTipusEnumDto documentFirmaTipus,
			List<ArxiuFirmaDto> firmes,
			ArxiuOperacioEnumDto arxiuOperacio,
			ArxiuEstatEnumDto arxiuEstat) {
		
		Document documentArxiu = new Document();

		String documentNomInArxiu = documentEntity.getNom();
		if (!DocumentTipusEnumDto.IMPORTAT.equals(documentEntity.getDocumentTipus()) && !isComprovacioNomsDesactivada()) {
			documentNomInArxiu = documentNomInArxiu(documentEntity.getNom(), contingutPare.getArxiuUuid());
		}
		documentArxiu.setNom(documentNomInArxiu);
		documentArxiu.setDescripcio(documentEntity.getDescripcio());
		documentArxiu.setIdentificador(arxiuOperacio == ArxiuOperacioEnumDto.MODIFICACIO ? documentEntity.getArxiuUuid() : null);
		documentArxiu.setEstat(DocumentEstat.valueOf(arxiuEstat.toString()));
	
		if (!DocumentTipusEnumDto.FISIC.equals(documentEntity.getDocumentTipus())) {
			setContingutIFirmes(
					documentArxiu,
					fitxer,
					documentFirmaTipus,
					firmes,
					arxiuOperacio, 
					arxiuEstat);
		}
		
		
		documentArxiu.setMetadades(
				getMetadades(
						documentEntity,
						fitxer,
						documentFirmaTipus, 
						firmes, arxiuEstat));
		
		return documentArxiu;

	}
	
	
	private DocumentMetadades getMetadades(
			DocumentEntity documentEntity,
			FitxerDto fitxer,
			DocumentFirmaTipusEnumDto documentFirmaTipus, 
			List<ArxiuFirmaDto> firmes, 
			ArxiuEstatEnumDto arxiuEstat){

		
		DocumentMetadades metadades = new DocumentMetadades();
		
		// ============= METADADES ===============
		metadades.setOrigen(ArxiuConversions.getOrigen(documentEntity.getNtiOrigen()));
		metadades.setDataCaptura(documentEntity.getDataCaptura());
		metadades.setEstatElaboracio(ArxiuConversions.getDocumentEstatElaboracio(documentEntity.getNtiEstadoElaboracion()));
		metadades.setIdentificadorOrigen(documentEntity.getNtiIdDocumentoOrigen());
		ArxiuConversions.setTipusDocumental(metadades, documentEntity.getNtiTipoDocumental());
		DocumentExtensio extensio = getDocumentExtensio(fitxer, documentFirmaTipus, firmes);
		metadades.setExtensio(extensio);
		metadades.setFormat(getDocumentFormat(extensio));
		metadades.setOrgans(Arrays.asList(documentEntity.getNtiOrgano()));
		metadades.setSerieDocumental(documentEntity.getExpedient().getMetaExpedient().getSerieDocumental());
		
		
		// ========== METADADES ADDICIONALS ============
		Map<String, Object> metadadesAddicionals = new HashMap<String, Object>();
		if (getPropertyArxiuMetadadesAddicionalsActiu()) {
			metadadesAddicionals.put("tipusDocumentNom", documentEntity.getMetaDocument().getNom());
			metadadesAddicionals.put("isImportacio", DocumentTipusEnumDto.IMPORTAT.equals(documentEntity.getDocumentTipus()));
			
			
			if (firmes != null && ! firmes.isEmpty() && arxiuEstat == ArxiuEstatEnumDto.DEFINITIU ) {
				metadadesAddicionals.put("detallsFirma", firmes.get(0).getDetalls());
			}
		}
		metadadesAddicionals.put("eni:descripcion", documentEntity.getDescripcio());
		metadades.setMetadadesAddicionals(metadadesAddicionals);
		
		return metadades;
	}


	
	private DocumentExtensio getDocumentExtensio(FitxerDto fitxer, DocumentFirmaTipusEnumDto documentFirmaTipus, List<ArxiuFirmaDto> firmes) {
		DocumentExtensio extensio = null;
		
		String fitxerNom = null;
		if (documentFirmaTipus == DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA) {
			ArxiuFirmaDto primeraFirma = firmes.get(0);
			fitxerNom = primeraFirma.getFitxerNom();
		} else {
			if (fitxer != null) {
				fitxerNom = fitxer.getNom();
			}
		}
		
		extensio = DocumentExtensio.toEnum(getExtensio(fitxerNom));
		
		return extensio;
	}
	
	private String getExtensio(String nom) {

		String extensio = null;
		if (nom != null) {
			int indexPunt = nom.lastIndexOf(".");
			if (indexPunt != -1 && indexPunt < nom.length() - 1) {
				extensio = nom.substring(indexPunt).toLowerCase();

			}
		}
		return extensio;
	}
	
	
	private DocumentFormat getDocumentFormat(DocumentExtensio extensio) {
		
		DocumentFormat format = null;
		if (extensio != null) {
			switch (extensio) {
			case AVI:
				format = DocumentFormat.AVI;
				break;
			case CSS:
				format = DocumentFormat.CSS;
				break;
			case CSV:
				format = DocumentFormat.CSV;
				break;
			case DOCX:
				format = DocumentFormat.SOXML;
				break;
			case GML:
				format = DocumentFormat.GML;
				break;
			case GZ:
				format = DocumentFormat.GZIP;
				break;
			case HTM:
				format = DocumentFormat.XHTML; // HTML o XHTML!!!
				break;
			case HTML:
				format = DocumentFormat.XHTML; // HTML o XHTML!!!
				break;
			case JPEG:
				format = DocumentFormat.JPEG;
				break;
			case JPG:
				format = DocumentFormat.JPEG;
				break;
			case MHT:
				format = DocumentFormat.MHTML;
				break;
			case MHTML:
				format = DocumentFormat.MHTML;
				break;
			case MP3:
				format = DocumentFormat.MP3;
				break;
			case MP4:
				format = DocumentFormat.MP4V; // MP4A o MP4V!!!
				break;
			case MPEG:
				format = DocumentFormat.MP4V; // MP4A o MP4V!!!
				break;
			case ODG:
				format = DocumentFormat.OASIS12;
				break;
			case ODP:
				format = DocumentFormat.OASIS12;
				break;
			case ODS:
				format = DocumentFormat.OASIS12;
				break;
			case ODT:
				format = DocumentFormat.OASIS12;
				break;
			case OGA:
				format = DocumentFormat.OGG;
				break;
			case OGG:
				format = DocumentFormat.OGG;
				break;
			case PDF:
				format = DocumentFormat.PDF; // PDF o PDFA!!!
				break;
			case PNG:
				format = DocumentFormat.PNG;
				break;
			case PPTX:
				format = DocumentFormat.SOXML;
				break;
			case RTF:
				format = DocumentFormat.RTF;
				break;
			case SVG:
				format = DocumentFormat.SVG;
				break;
			case TIFF:
				format = DocumentFormat.TIFF;
				break;
			case TXT:
				format = DocumentFormat.TXT;
				break;
			case WEBM:
				format = DocumentFormat.WEBM;
				break;
			case XLSX:
				format = DocumentFormat.SOXML;
				break;
			case ZIP:
				format = DocumentFormat.ZIP;
				break;
			case CSIG:
				format = DocumentFormat.CSIG;
				break;
			case XSIG:
				format = DocumentFormat.XSIG;
				break;
			case XML:
				format = DocumentFormat.XML;
				break;
			}
		}
		return format;
	}
	
	
	private DocumentExtensio getArxiuFormatExtensio(String extensio) {
		String extensioAmbPunt = (extensio.startsWith(".")) ? extensio.toLowerCase() : "." + extensio.toLowerCase();
		return DocumentExtensio.toEnum(extensioAmbPunt);
	}

	private Persona convertirAmbPersona(InteressatEntity interessat) {

		interessat = HibernateHelper.deproxy(interessat);
		Persona persona = new Persona();
		persona.setNif(interessat.getDocumentNum());
		if (interessat instanceof InteressatPersonaFisicaEntity) {
			InteressatPersonaFisicaEntity interessatPf = (InteressatPersonaFisicaEntity)interessat;
			persona.setNom(interessatPf.getNom());
			persona.setLlinatge1(interessatPf.getLlinatge1());
			persona.setLlinatge2(interessatPf.getLlinatge2());
			persona.setInteressatTipus(InteressatTipusEnumDto.PERSONA_FISICA);
		} else if (interessat instanceof InteressatPersonaJuridicaEntity) {
			InteressatPersonaJuridicaEntity interessatPj = (InteressatPersonaJuridicaEntity)interessat;
			persona.setRaoSocial(interessatPj.getRaoSocial());
			persona.setInteressatTipus(InteressatTipusEnumDto.PERSONA_JURIDICA);
		} else if (interessat instanceof InteressatAdministracioEntity) {
			InteressatAdministracioEntity interessatA = (InteressatAdministracioEntity)interessat;
			persona.setInteressatTipus(InteressatTipusEnumDto.ADMINISTRACIO);
			UnitatOrganitzativaDto unitatOrganitzativaDto = unitatOrganitzativaHelper.findAmbCodi(interessatA.getOrganCodi());
			persona.setNif(unitatOrganitzativaDto.getNifCif());
			persona.setNom(unitatOrganitzativaDto.getNom());
		}
		persona.setTelefon(interessat.getTelefon());
		persona.setEmail(interessat.getEmail());
		persona.setIncapacitat(interessat.getIncapacitat());
		return persona;
	}

	/*private ArxiuCapsalera generarCapsaleraArxiu(
			ContingutEntity contingut) {
		ArxiuCapsalera capsaleraTest = new ArxiuCapsalera();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		capsaleraTest.setFuncionariNom(auth.getName());
		capsaleraTest.setFuncionariOrgan(
				contingut.getEntitat().getUnitatArrel());
		return capsaleraTest;
	}*/

	private void propagarMetadadesExpedient(Expedient expedientArxiu, ExpedientEntity expedientDb) {

		List<String> metadadaOrgans = expedientArxiu.getMetadades().getOrgans();
		String organs = null;
		if (expedientArxiu.getMetadades().getOrgans() != null) {
			StringBuilder organsSb = new StringBuilder();
			boolean primer = true;
			for (String organ: metadadaOrgans) {
				organsSb.append(organ);
				if (primer || metadadaOrgans.size() == 1) {
					primer = false;
				} else {
					organsSb.append(",");
				}
			}
			organs = organsSb.toString();
		}
		expedientDb.updateNti(
				obtenirNumeroVersioEniExpedient(expedientArxiu.getMetadades().getVersioNti()),
				expedientArxiu.getMetadades().getIdentificador(),
				organs,
				expedientArxiu.getMetadades().getDataObertura(),
				expedientArxiu.getMetadades().getClassificacio());
	}

	private static final String ENI_EXPEDIENT_PREFIX = "http://administracionelectronica.gob.es/ENI/XSD/v";
	private String obtenirNumeroVersioEniExpedient(String versio) {
		if (versio != null) {
			if (versio.startsWith(ENI_EXPEDIENT_PREFIX)) {
				int indexBarra = versio.indexOf("/", ENI_EXPEDIENT_PREFIX.length());
				return versio.substring(ENI_EXPEDIENT_PREFIX.length(), indexBarra);
			}
		}
		return null;
	}
	
	
	private void propagarMetadadesDocument(Document documentArxiu, DocumentEntity documentDb) {

		
		String organs = StringUtils.join(documentArxiu.getMetadades().getOrgans(), ',');
		
		NtiOrigenEnumDto origen = ArxiuConversions.getOrigen(documentArxiu);
		DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion = ArxiuConversions.getEstatElaboracio(documentArxiu);
		String ntiTipoDocumental = ArxiuConversions.getTipusDocumental(documentArxiu);
		DocumentNtiTipoFirmaEnumDto ntiTipoFirma = ArxiuConversions.getNtiTipoFirma(documentArxiu);
		
		String ntiCsvFirma = ArxiuConversions.getNtiCsv(documentArxiu)[0];
		String ntiCsvRegulacion = ArxiuConversions.getNtiCsv(documentArxiu)[1];
		
		documentDb.updateNti(
				obtenirNumeroVersioEniDocument(documentArxiu.getMetadades().getVersioNti()),
				documentArxiu.getMetadades().getIdentificador(),
				organs,
				origen,
				ntiEstadoElaboracion,
				ntiTipoDocumental,
				documentArxiu.getMetadades().getIdentificadorOrigen(),
				ntiTipoFirma,
				ntiCsvFirma != null ? ntiCsvFirma : documentArxiu.getMetadades().getCsv(),
				ntiCsvRegulacion);
	}

	private static final String ENI_DOCUMENT_PREFIX = "http://administracionelectronica.gob.es/ENI/XSD/v";

	private String obtenirNumeroVersioEniDocument(String versio) {
		if (versio != null) {
			if (versio.startsWith(ENI_DOCUMENT_PREFIX)) {
				int indexBarra = versio.indexOf("/", ENI_DOCUMENT_PREFIX.length());
				return versio.substring(ENI_DOCUMENT_PREFIX.length(), indexBarra);
			}
		}
		return null;
	}

	private Map<String, String> getAccioParamsPerPortaFirmesUpload(DocumentEntity document, String motiu, PortafirmesPrioritatEnum prioritat,
																	Date dataCaducitat, String documentTipus, String[] responsables,
																   	MetaDocumentFirmaSequenciaTipusEnumDto fluxTipus, String fluxId, List<DocumentEntity> annexos) {

		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("documentId", document.getId().toString());
		accioParams.put("documentTitol", document.getNom());
		accioParams.put("motiu", motiu);
		accioParams.put("prioritat", prioritat.toString());
//		accioParams.put(
//				"dataCaducitat",
//				new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dataCaducitat));
		accioParams.put("documentTipus", documentTipus);
		if (responsables != null) {
			accioParams.put("responsables", Arrays.toString(responsables));
		}
		if (fluxTipus != null) {
			accioParams.put("fluxTipus", fluxTipus.toString());
		}
		if (fluxId != null) {
			accioParams.put("fluxId", fluxId);
		}

		if (annexos != null) {
			StringBuilder annexosIds = new StringBuilder();
			StringBuilder annexosTitols = new StringBuilder();
			boolean primer = true;
			for (DocumentEntity annex: annexos) {
				if (!primer) {
					annexosIds.append(", ");
					annexosTitols.append(", ");
				}
				annexosIds.append(annex.getId());
				annexosTitols.append(annex.getNom());
				primer = false;
			}
			accioParams.put("annexosIds", annexosIds.toString());
			accioParams.put("annexosTitols", annexosTitols.toString());
		}
		return accioParams;
	}

	private Map<String, String> getAccioParams(DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity) {

		DocumentNotificacioEntity notificacio = documentEnviamentInteressatEntity.getNotificacio();
		ExpedientEntity expedient = notificacio.getExpedient();
		DocumentEntity document = notificacio.getDocument();

		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("setEmisorDir3Codi", expedient.getEntitat().getUnitatArrel());
		accioParams.put("expedientId", expedient.getId().toString());
		accioParams.put("expedientTitol", expedient.getNom());
		accioParams.put("expedientTipusId", expedient.getMetaNode().getId().toString());
		accioParams.put("expedientTipusNom", expedient.getMetaNode().getNom());
		accioParams.put("documentNom", document.getNom());
		if (notificacio.getTipus() != null) {
			accioParams.put("enviamentTipus", notificacio.getTipus().name());
		}
		accioParams.put("concepte", notificacio.getAssumpte());
		accioParams.put("referencia", documentEnviamentInteressatEntity.getEnviamentReferencia());
		return accioParams;
	}

	private DadesUsuariPlugin getDadesUsuariPlugin() {

		loadPluginProperties("USUARIS");
		if (dadesUsuariPlugin != null) {
			return dadesUsuariPlugin;
		}
		String pluginClass = getPropertyPluginDadesUsuari();
		if (Strings.isNullOrEmpty(pluginClass)) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_USUARIS, "No està configurada la classe per al plugin de dades d'usuari");
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			dadesUsuariPlugin = (DadesUsuariPlugin)clazz.getDeclaredConstructor(String.class, Properties.class)
									.newInstance("es.caib.ripea.", PropertiesHelper.getProperties());
			return dadesUsuariPlugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_USUARIS, "Error al crear la instància del plugin de dades d'usuari", ex);
		}
	}

	private UnitatsOrganitzativesPlugin getUnitatsOrganitzativesPlugin() {

		String entitatCodi = configHelper.getEntitatActualCodi();
		if (Strings.isNullOrEmpty(entitatCodi)) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}
		UnitatsOrganitzativesPlugin plugin = unitatsOrganitzativesPlugins.get(entitatCodi);
//		loadPluginProperties("ORGANISMES");
		if (plugin != null) {
			return plugin;
		}
		String pluginClass = getPropertyPluginUnitatsOrganitzatives();
		if (Strings.isNullOrEmpty(pluginClass)) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, "No està configurada la classe per al plugin d'unitats organitzatives");
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (UnitatsOrganitzativesPlugin)clazz.getDeclaredConstructor(String.class, Properties.class)
						.newInstance(ConfigDto.prefix + ".", configHelper.getGroupPropertiesEntitatOrGeneral("ORGANISMES", entitatCodi));
			unitatsOrganitzativesPlugins.put(entitatCodi, plugin);
			return plugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, "Error al crear la instància del plugin d'unitats organitzatives", ex);
		}
	}

	public IArxiuPlugin getArxiuPlugin() {
		

		String entitatCodi = configHelper.getEntitatActualCodi();
		if (entitatCodi == null) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}
		
		IArxiuPlugin plugin = null;
		// ORGAN PLUGIN
		String organCodi = configHelper.getOrganActualCodi();
		if (organCodi != null) {
			plugin = arxiuPlugins.get(entitatCodi + "." + organCodi);
			if (plugin != null) {
				return plugin;
			}
			String pluginClassOrgan = configHelper.getValueForOrgan(entitatCodi, organCodi, "es.caib.ripea.plugin.arxiu.class");
			if (StringUtils.isNotEmpty(pluginClassOrgan)) {
				try {
					Class<?> clazz = Class.forName(pluginClassOrgan);
					plugin = (IArxiuPlugin)clazz.getDeclaredConstructor(String.class, Properties.class)
								.newInstance(ConfigDto.prefix + ".", configHelper.getGroupPropertiesOrganOrEntitatOrGeneral("ARXIU", entitatCodi, organCodi));
					arxiuPlugins.put(entitatCodi + "." + organCodi, plugin);
					return plugin;
				} catch (Exception ex) {
					throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, "Error al crear la instància del plugin d'arxiu digital ("+organCodi+")", ex);
				}
			}
		}

		// ENTITAT/GENERAL PLUGIN
		plugin = arxiuPlugins.get(entitatCodi);
		if (plugin != null) {
			return plugin;
		}
		String pluginClass = getPropertyPluginArxiu();
		if (Strings.isNullOrEmpty(pluginClass)) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, "No està configurada la classe per al plugin d'arxiu digital");
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (IArxiuPlugin)clazz.getDeclaredConstructor(String.class, Properties.class)
						.newInstance(ConfigDto.prefix + ".", configHelper.getGroupPropertiesEntitatOrGeneral("ARXIU", entitatCodi));
			arxiuPlugins.put(entitatCodi, plugin);
			return plugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, "Error al crear la instància del plugin d'arxiu digital", ex);
		}
		
	}

	
	



	private PortafirmesPlugin getPortafirmesPlugin() {
		
		String entitatCodi = configHelper.getEntitatActualCodi();
		if (entitatCodi == null) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}
		
		PortafirmesPlugin plugin = null;
		// ORGAN PLUGIN
		String organCodi = configHelper.getOrganActualCodi();
		if (organCodi != null) {
			plugin = portafirmesPlugins.get(entitatCodi + "." + organCodi);
			if (plugin != null) {
				return plugin;
			}
			String pluginClassOrgan = configHelper.getValueForOrgan(entitatCodi, organCodi, "es.caib.ripea.plugin.portafirmes.class");
			if (StringUtils.isNotEmpty(pluginClassOrgan)) {
				try {
					Class<?> clazz = Class.forName(pluginClassOrgan);
					plugin = (PortafirmesPlugin)clazz.getDeclaredConstructor(String.class, Properties.class)
								.newInstance(ConfigDto.prefix + ".", configHelper.getGroupPropertiesOrganOrEntitatOrGeneral("PORTAFIRMES", entitatCodi, organCodi));
					portafirmesPlugins.put(entitatCodi + "." + organCodi, plugin);
					return plugin;
				} catch (Exception ex) {
					throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, "Error al crear la instància del plugin de portafirmes ("+organCodi+")", ex);
				}
			}
		}

		// ENTITAT/GENERAL PLUGIN
		plugin = portafirmesPlugins.get(entitatCodi);
//		loadPluginProperties("PORTAFIRMES");
		if (plugin != null) {
			return plugin;
		}
		String pluginClass = getPropertyPluginPortafirmes();
		if (Strings.isNullOrEmpty(pluginClass)) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, "No està configurada la classe per al plugin de portafirmes");
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (PortafirmesPlugin)clazz.getDeclaredConstructor(String.class, Properties.class).
						newInstance(ConfigDto.prefix + ".", configHelper.getGroupPropertiesEntitatOrGeneral("PORTAFIRMES", entitatCodi));
			portafirmesPlugins.put(entitatCodi, plugin);
			return plugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, "Error al crear la instància del plugin de portafirmes", ex);
		}
	}

	private ConversioPlugin getConversioPlugin() {

		String entitatCodi = configHelper.getEntitatActualCodi();
		if (entitatCodi == null) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}
		
		ConversioPlugin plugin = null;
		// ORGAN PLUGIN
		String organCodi = configHelper.getOrganActualCodi();
		if (organCodi != null) {
			plugin = conversioPlugins.get(entitatCodi + "." + organCodi);
			if (plugin != null) {
				return plugin;
			}
			String pluginClassOrgan = configHelper.getValueForOrgan(entitatCodi, organCodi, "es.caib.ripea.plugin.conversio.class");
			if (StringUtils.isNotEmpty(pluginClassOrgan)) {
				try {
					Class<?> clazz = Class.forName(pluginClassOrgan);
					plugin = (ConversioPlugin)clazz.getDeclaredConstructor(String.class, Properties.class)
								.newInstance(ConfigDto.prefix + ".", configHelper.getGroupPropertiesOrganOrEntitatOrGeneral("CONVERSIO", entitatCodi, organCodi));
					conversioPlugins.put(entitatCodi + "." + organCodi, plugin);
					return plugin;
				} catch (Exception ex) {
					throw new SistemaExternException(IntegracioHelper.INTCODI_CONVERT, "Error al crear la instància del plugin de conversió de documents ("+organCodi+")", ex);
				}
			}
		}

		// ENTITAT/GENERAL PLUGIN
		plugin = conversioPlugins.get(entitatCodi);
//		loadPluginProperties("CONVERSIO");
		if (plugin != null) {
			return plugin;
		}
		String pluginClass = getPropertyPluginConversio();
		if (Strings.isNullOrEmpty(pluginClass)) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_CONVERT, "No està configurada la classe per al plugin de conversió de documents");
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (ConversioPlugin)clazz.getDeclaredConstructor(String.class, Properties.class)
								.newInstance(ConfigDto.prefix + ".", configHelper.getGroupPropertiesEntitatOrGeneral("CONVERSIO", entitatCodi));
			conversioPlugins.put(entitatCodi, plugin);
			return plugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_CONVERT, "Error al crear la instància del plugin de conversió de documents", ex);
		}
	}

	private DigitalitzacioPlugin getDigitalitzacioPlugin() {

		String entitatCodi = configHelper.getEntitatActualCodi();
		if (entitatCodi == null) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}
		DigitalitzacioPlugin plugin = digitalitzacioPlugins.get(entitatCodi);
//		loadPluginProperties("DIGITALITZACIO");
		if (plugin != null) {
			return plugin;
		}
		String pluginClass = getPropertyPluginDigitalitzacio();
		if (Strings.isNullOrEmpty(pluginClass)) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_DIGITALITZACIO, "No està configurada la classe per al plugin de digitalització");
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (DigitalitzacioPlugin)clazz.getDeclaredConstructor(String.class, Properties.class)
					.newInstance(ConfigDto.prefix + ".", configHelper.getAllPropertiesEntitatOrGeneral(entitatCodi));
			digitalitzacioPlugins.put(entitatCodi, plugin);
			return plugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_DIGITALITZACIO, "Error al crear la instància del plugin de digitalització", ex);
		}
	}

//	private RegistrePlugin getRegistrePlugin() {
//		if (registrePlugin == null) {
//			String pluginClass = getPropertyPluginRegistre();
//			if (pluginClass != null && pluginClass.length() > 0) {
//				try {
//					Class<?> clazz = Class.forName(pluginClass);
//					registrePlugin = (RegistrePlugin)clazz.newInstance();
//				} catch (Exception ex) {
//					throw new SistemaExternException(
//							IntegracioHelper.INTCODI_REGISTRE,
//							"Error al crear la instància del plugin de registre",
//							ex);
//				}
//			} else {
//				throw new SistemaExternException(
//						IntegracioHelper.INTCODI_REGISTRE,
//						"No està configurada la classe per al plugin de registre");
//			}
//		}
//		return registrePlugin;
//	}
	/*private CiutadaPlugin getCiutadaPlugin() {
		if (ciutadaPlugin == null) {
			String pluginClass = getPropertyPluginCiutada();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					ciutadaPlugin = (CiutadaPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_CIUTADA,
							"Error al crear la instància del plugin de comunicació amb el ciutadà",
							ex);
				}
			}
		}
		return ciutadaPlugin;
	}*/

	private Map<String, String> getNotificacioAccioParams(DocumentNotificacioDto notificacio, ExpedientEntity expedientEntity, DocumentEntity documentEntity, InteressatEntity interessat) {

		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("setEmisorDir3Codi", expedientEntity.getEntitat().getUnitatArrel());
		accioParams.put("expedientId", expedientEntity.getId().toString());
		accioParams.put("expedientTitol", expedientEntity.getNom());
		accioParams.put("expedientTipusId", expedientEntity.getMetaNode().getId().toString());
		accioParams.put("expedientTipusNom", expedientEntity.getMetaNode().getNom());
		accioParams.put("documentNom", documentEntity.getNom());
		String intressatsString = "";
		intressatsString += interessat.getIdentificador();
		accioParams.put("interessats", intressatsString);
		if (notificacio.getTipus() != null) {
			accioParams.put("enviamentTipus", notificacio.getTipus().name());
		}
		accioParams.put("concepte", notificacio.getAssumpte());
		accioParams.put("descripcio", notificacio.getObservacions());
		if (notificacio.getDataProgramada() != null) {
			accioParams.put("dataProgramada", notificacio.getDataProgramada().toString());
		}
		if (notificacio.getRetard() != null) {
			accioParams.put("retard", notificacio.getRetard().toString());
		}
		if (notificacio.getDataCaducitat() != null) {
			accioParams.put("dataCaducitat", notificacio.getDataCaducitat().toString());
		}
		return accioParams;
	}

	private DadesExternesPlugin getDadesExternesPlugin() {

		String entitatCodi = configHelper.getEntitatActualCodi();
		if (entitatCodi == null) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}
		DadesExternesPlugin plugin = dadesExternesPlugins.get(entitatCodi);
//		loadPluginProperties("DADES_EXT");
		if (plugin != null) {
			return plugin;
		}
		String pluginClass = getPropertyPluginDadesExternes();
		if (Strings.isNullOrEmpty(pluginClass)) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_DADESEXT, "No està configurada la classe per al plugin de dades externes");
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (DadesExternesPlugin)clazz.getDeclaredConstructor(String.class, Properties.class)
						.newInstance(ConfigDto.prefix + ".", configHelper.getGroupPropertiesEntitatOrGeneral("DADES_EXT", entitatCodi));;
			dadesExternesPlugins.put(entitatCodi, plugin);
			return plugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_DADESEXT, "Error al crear la instància del plugin de consulta de dades externes", ex);
		}
	}
	
	private DadesExternesPlugin getDadesExternesPinbalPlugin() {

		String entitatCodi = configHelper.getEntitatActualCodi();
		if (entitatCodi == null) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}
		DadesExternesPlugin plugin = dadesExternesPinbalPlugins.get(entitatCodi);
//		loadPluginProperties("DADES_EXT");
		if (plugin != null) {
			return plugin;
		}
		String pluginClass = getPropertyPluginDadesExternesPinbal();
		if (Strings.isNullOrEmpty(pluginClass)) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_DADESEXT, "No està configurada la classe per al plugin de dades externes per a consultes a PINBAL");
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (DadesExternesPlugin)clazz.getDeclaredConstructor(String.class, Properties.class)
						.newInstance(ConfigDto.prefix + ".", configHelper.getGroupPropertiesEntitatOrGeneral("DADES_EXT_PINBAL", entitatCodi));
			dadesExternesPinbalPlugins.put(entitatCodi, plugin);
			return plugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_DADESEXT, "Error al crear la instància del plugin de consulta de dades externes per a consultes a PINBAL", ex);
		}
	}

	private IValidateSignaturePlugin getValidaSignaturaPlugin() {

		String entitatCodi = configHelper.getEntitatActualCodi();
		if (entitatCodi == null) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}
		
		IValidateSignaturePlugin plugin = null;
		// ORGAN PLUGIN
		String organCodi = configHelper.getOrganActualCodi();
		if (organCodi != null) {
			plugin = validaSignaturaPlugins.get(entitatCodi + "." + organCodi);
			if (plugin != null) {
				return plugin;
			}
			String pluginClassOrgan = configHelper.getValueForOrgan(entitatCodi, organCodi, "es.caib.ripea.plugin.validatesignature.class");
			if (StringUtils.isNotEmpty(pluginClassOrgan)) {
				try {
					Class<?> clazz = Class.forName(pluginClassOrgan);
					plugin = (IValidateSignaturePlugin)clazz.getDeclaredConstructor(String.class, Properties.class)
								.newInstance(ConfigDto.prefix + ".", configHelper.getGroupPropertiesOrganOrEntitatOrGeneral("VALIDATE_SIGNATURE", entitatCodi, organCodi));
					validaSignaturaPlugins.put(entitatCodi + "." + organCodi, plugin);
					return plugin;
				} catch (Exception ex) {
					throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, "Error al crear la instància del plugin de validació de signatures ("+organCodi+")", ex);
				}
			}
		}

		// ENTITAT/GENERAL PLUGIN
		plugin = validaSignaturaPlugins.get(entitatCodi);
//		loadPluginProperties("VALIDATE_SIGNATURE");
		if (plugin != null) {
			return plugin;
		}
		String pluginClass = getPropertyPluginValidaSignatura();
		if (Strings.isNullOrEmpty(pluginClass)) {
			return null;
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (IValidateSignaturePlugin)clazz.getDeclaredConstructor(String.class, Properties.class)
						.newInstance(ConfigDto.prefix + ".", configHelper.getGroupPropertiesEntitatOrGeneral("VALIDATE_SIGNATURE", entitatCodi));
			validaSignaturaPlugins.put(entitatCodi, plugin);
			return plugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_VALIDASIG, "Error al crear la instància del plugin de validació de signatures", ex);
		}
	}

	private NotificacioPlugin getNotificacioPlugin() {

		String entitatCodi = configHelper.getEntitatActualCodi();
		if (entitatCodi == null) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}
		
		NotificacioPlugin plugin = null;
		// ORGAN PLUGIN
		String organCodi = configHelper.getOrganActualCodi();
		if (organCodi != null) {
			plugin = notificacioPlugins.get(entitatCodi + "." + organCodi);
			if (plugin != null) {
				return plugin;
			}
			String pluginClassOrgan = configHelper.getValueForOrgan(entitatCodi, organCodi, "es.caib.ripea.plugin.notificacio.class");
			if (StringUtils.isNotEmpty(pluginClassOrgan)) {
				try {
					Class<?> clazz = Class.forName(pluginClassOrgan);
					plugin = (NotificacioPlugin)clazz.getDeclaredConstructor(String.class, Properties.class)
								.newInstance(ConfigDto.prefix + ".", configHelper.getGroupPropertiesOrganOrEntitatOrGeneral("NOTIB", entitatCodi, organCodi));
					notificacioPlugins.put(entitatCodi + "." + organCodi, plugin);
					return plugin;
				} catch (Exception ex) {
					throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, "Error al crear la instància del plugin de notificació ("+organCodi+")", ex);
				}
			}
		}

		// ENTITAT/GENERAL PLUGIN
		plugin = notificacioPlugins.get(entitatCodi);
//		loadPluginProperties("NOTIB");
		if (plugin != null) {
			return plugin;
		}
		String pluginClass = getPropertyPluginNotificacio();
		if (Strings.isNullOrEmpty(pluginClass)) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_NOTIFICACIO, "No està configurada la classe per al plugin de notificació");
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (NotificacioPlugin)clazz.getDeclaredConstructor(String.class, Properties.class)
						.newInstance(ConfigDto.prefix + ".", configHelper.getGroupPropertiesEntitatOrGeneral("NOTIB", entitatCodi));
			notificacioPlugins.put(entitatCodi, plugin);
			return plugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_NOTIFICACIO, "Error al crear la instància del plugin de notificació", ex);
		}
	}

	private FirmaServidorPlugin getFirmaServidorPlugin() {

		String entitatCodi = configHelper.getEntitatActualCodi();
		if (entitatCodi == null) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}
		
		FirmaServidorPlugin plugin = null;
		// ORGAN PLUGIN
		String organCodi = configHelper.getOrganActualCodi();
		if (organCodi != null) {
			plugin = firmaServidorPlugins.get(entitatCodi + "." + organCodi);
			if (plugin != null) {
				return plugin;
			}
			String pluginClassOrgan = configHelper.getValueForOrgan(entitatCodi, organCodi, "es.caib.ripea.plugin.firmaservidor.class");
			if (StringUtils.isNotEmpty(pluginClassOrgan)) {
				try {
					Class<?> clazz = Class.forName(pluginClassOrgan);
					plugin = (FirmaServidorPlugin)clazz.getDeclaredConstructor(String.class, Properties.class)
								.newInstance(ConfigDto.prefix + ".", configHelper.getGroupPropertiesOrganOrEntitatOrGeneral("FIRMA_SERVIDOR", entitatCodi, organCodi));
					firmaServidorPlugins.put(entitatCodi + "." + organCodi, plugin);
					return plugin;
				} catch (Exception ex) {
					throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, "Error al crear la instància del plugin de firma en servidor (" + organCodi + ")", ex);
				}
			}
		}

		// ENTITAT/GENERAL PLUGIN
		plugin = firmaServidorPlugins.get(entitatCodi);
//		loadPluginProperties("FIRMA_SERVIDOR");
		if (plugin != null) {
			return plugin;
		}
		String pluginClass = getPropertyPluginFirmaServidor();
		if (Strings.isNullOrEmpty(pluginClass)) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_FIRMASERV, "No està configurada la classe per al plugin de firma en servidor");
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (FirmaServidorPlugin)clazz.getDeclaredConstructor(String.class, Properties.class)
						.newInstance(ConfigDto.prefix + ".", configHelper.getGroupPropertiesEntitatOrGeneral("FIRMA_SERVIDOR", entitatCodi));
			firmaServidorPlugins.put(entitatCodi, plugin);
			return plugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_FIRMASERV, "Error al crear la instància del plugin de firma en servidor", ex);
		}
	}
	private ViaFirmaPlugin getViaFirmaPlugin() {

		String entitatCodi = configHelper.getEntitatActualCodi();
		if (entitatCodi == null) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}
		ViaFirmaPlugin plugin = viaFirmaPlugins.get(entitatCodi);
//		loadPluginProperties("FIRMA_VIAFIRMA");
		boolean viaFirmaPluginConfiguracioProvada = false;
		if (plugin != null || viaFirmaPluginConfiguracioProvada) {
			return plugin;
		}
		viaFirmaPluginConfiguracioProvada = true;
		String pluginClass = getPropertyPluginViaFirma();
		if (Strings.isNullOrEmpty(pluginClass)) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_USUARIS, "La classe del plugin de via firma no està configurada");
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (ViaFirmaPlugin)clazz.getDeclaredConstructor(String.class, Properties.class)
								.newInstance(ConfigDto.prefix + ".", configHelper.getAllPropertiesEntitatOrGeneral(entitatCodi));
			viaFirmaPlugins.put(entitatCodi, plugin);
			return plugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_VIAFIRMA, "Error al crear la instància del plugin de via firma", ex);
		}
	}

	private ProcedimentPlugin getProcedimentPlugin() {

		String entitatCodi = configHelper.getEntitatActualCodi();
		if (entitatCodi == null) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}
		ProcedimentPlugin procedimentPlugin = procedimentPlugins.get(entitatCodi);
//		loadPluginProperties("GESCONADM");
		if (procedimentPlugin != null) {
			return procedimentPlugin;
		}
		String pluginClass = getPropertyPluginProcediment();
		if (Strings.isNullOrEmpty(pluginClass)) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_PROCEDIMENT, "No està configurada la classe per al plugin de procediments");
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			procedimentPlugin = (ProcedimentPlugin)clazz.getDeclaredConstructor(String.class, Properties.class)
					.newInstance(ConfigDto.prefix + ".", configHelper.getGroupPropertiesEntitatOrGeneral("GESCONADM", entitatCodi));
			procedimentPlugins.put(entitatCodi, procedimentPlugin);
			return procedimentPlugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_PROCEDIMENT, "Error al crear la instància del plugin de procediments", ex);
		}
	}

	private GestioDocumentalPlugin getGestioDocumentalPlugin() {

		String entitatCodi = configHelper.getEntitatActualCodi();
		if (entitatCodi == null) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}
		GestioDocumentalPlugin plugin = gestioDocumentalPlugins.get(entitatCodi);
//		loadPluginProperties("GES_DOC");
		if (plugin != null) {
			return plugin;
		}
		String pluginClass = getPropertyPluginGestioDocumental();
		if (Strings.isNullOrEmpty(pluginClass)) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_USUARIS, "La classe del plugin de gestió documental no està configurada");
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (GestioDocumentalPlugin)clazz.getDeclaredConstructor(String.class, Properties.class)
						.newInstance(ConfigDto.prefix + ".", configHelper.getGroupPropertiesEntitatOrGeneral("GES_DOC", entitatCodi));
			gestioDocumentalPlugins.put(entitatCodi, plugin);
			return plugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESDOC, "Error al crear la instància del plugin de gestió documental", ex);
		}
	}

	private final static Map<String, Boolean> propertiesLoaded = new HashMap<>();
	private synchronized void loadPluginProperties(String codeProperties) {
		if (!propertiesLoaded.containsKey(codeProperties) || !propertiesLoaded.get(codeProperties)) {
			propertiesLoaded.put(codeProperties, true);
			Properties pluginProps = configHelper.getPropertiesByGroup(codeProperties);
			for (Map.Entry<Object, Object> entry : pluginProps.entrySet() ) {
				String value = entry.getValue() == null ? "" : (String) entry.getValue();
				PropertiesHelper.getProperties().setProperty((String) entry.getKey(), value);
			}
		}
	}

	/**
	 * Esborra les properties del grup indicat per paràmetre de la memòria.
	 *
	 * @param codeProperties Codi del grup de propietats que vols esborrar de memòria.
	 */
	public void reloadProperties(String codeProperties) {
		if (propertiesLoaded.containsKey(codeProperties))
			propertiesLoaded.put(codeProperties, false);
	}

	public void resetPlugins() {
		dadesUsuariPlugin = null;
		unitatsOrganitzativesPlugins = new HashMap<>();
		portafirmesPlugins  = new HashMap<>();
		digitalitzacioPlugins  = new HashMap<>();
		conversioPlugins  = new HashMap<>();
		dadesExternesPlugins  = new HashMap<>();
		dadesExternesPinbalPlugins  = new HashMap<>();
		arxiuPlugins = new HashMap<>();
		validaSignaturaPlugins  = new HashMap<>();
		notificacioPlugins  = new HashMap<>();
		gestioDocumentalPlugins  = new HashMap<>();
		firmaServidorPlugins  = new HashMap<>();
		viaFirmaPlugins  = new HashMap<>();
		procedimentPlugins  = new HashMap<>();
	}

	private String getPropertyPluginDadesUsuari() {
		return configHelper.getConfig("es.caib.ripea.plugin.dades.usuari.class");
	}
	private String getPropertyPluginUnitatsOrganitzatives() {
		return configHelper.getConfig("es.caib.ripea.plugin.unitats.organitzatives.class");
	}
	private String getPropertyPluginArxiu() {
		return configHelper.getConfig("es.caib.ripea.plugin.arxiu.class");
	}
	private String getPropertyPluginPortafirmes() {
		return configHelper.getConfig("es.caib.ripea.plugin.portafirmes.class");
	}
	private String getPropertyPluginDigitalitzacio() {
		return configHelper.getConfig("es.caib.ripea.plugin.digitalitzacio.class");
	}
	private String getPropertyPluginConversio() {
		return configHelper.getConfig("es.caib.ripea.plugin.conversio.class");
	}

	private String getPropertyPluginDadesExternes() {
		return configHelper.getConfig("es.caib.ripea.plugin.dadesext.class");
	}
	private String getPropertyPluginDadesExternesPinbal() {
		return configHelper.getConfig("es.caib.ripea.plugin.dadesextpinbal.class");
	}
	private String getPropertyPluginProcediment() {
		return configHelper.getConfig("es.caib.ripea.plugin.procediment.class");
	}
	private String getPropertyPluginValidaSignatura() {
		return configHelper.getConfig("es.caib.ripea.plugin.validatesignature.class");
	}
	private String getPropertyPluginNotificacio() {
		return configHelper.getConfig("es.caib.ripea.plugin.notificacio.class");
	}
	private String getPropertyPluginGestioDocumental() {
		return configHelper.getConfig("es.caib.ripea.plugin.gesdoc.class");
	}
	private String getPropertyPluginFirmaServidor() {
		return configHelper.getConfig("es.caib.ripea.plugin.firmaservidor.class");
	}
	private String getPropertyPluginViaFirma() {
		return configHelper.getConfig("es.caib.ripea.plugin.viafirma.class");
	}
	private boolean getPropertyPluginRegistreSignarAnnexos() {
		return configHelper.getAsBoolean("es.caib.ripea.plugin.signatura.signarAnnexos");
	}

	public boolean getPropertyArxiuMetadadesAddicionalsActiu() {
		return configHelper.getAsBoolean("es.caib.ripea.arxiu.metadades.addicionals.actiu");
	}
	
	public boolean getPropertyArxiuFirmaDetallsActiu() {
		return configHelper.getAsBoolean("es.caib.ripea.arxiu.firma.detalls.actiu");
	}

	private Integer getPropertyNotificacioRetardNumDies() {
		return configHelper.getAsInt("es.caib.ripea.notificacio.retard.num.dies");
	}
	private Integer getPropertyNotificacioCaducitatNumDies() {
		return configHelper.getAsInt("es.caib.ripea.notificacio.caducitat.num.dies");
	}
	private String getPropertyNotificacioForsarEntitat() {
		return configHelper.getConfig("es.caib.ripea.notificacio.forsar.entitat");
	}

	private boolean getPropertyGuardarCertificacioExpedient() {
		return configHelper.getAsBoolean("es.caib.ripea.notificacio.guardar.certificacio.expedient");
	}
	private boolean getPropertyViaFirmaDispositius() {
		return configHelper.getAsBoolean("es.caib.ripea.plugin.viafirma.caib.dispositius.enabled");
	}
	public boolean getPropertyPropagarConversioDefinitiuActiu() {
		return configHelper.getAsBoolean("es.caib.ripea.conversio.definitiu.propagar.arxiu");
	}
	private boolean isComprovacioNomsDesactivada() {
		return configHelper.getAsBoolean("es.caib.ripea.desactivar.comprovacio.duplicat.nom.arxiu");
	}
	
	public boolean isCarpetaLogica() {
		return configHelper.getAsBoolean("es.caib.ripea.carpetes.logiques");
	}
	
	
	public void setArxiuPlugin(String entitatCodi, IArxiuPlugin arxiuPlugin) {
		arxiuPlugins.put(entitatCodi, arxiuPlugin);
	}
	
	public void setUnitatsOrganitzativesPlugin(String entitatCodi, UnitatsOrganitzativesPlugin unitatsOrganitzativesPlugin) {
		unitatsOrganitzativesPlugins.put(entitatCodi, unitatsOrganitzativesPlugin);
	}

	public void setPortafirmesPlugin(String entitatCodi, PortafirmesPlugin portafirmesPlugin) {
		portafirmesPlugins.put(entitatCodi, portafirmesPlugin);
	}
	public void setDadesUsuariPlugin(DadesUsuariPlugin dadesUsuariPlugin) {
		this.dadesUsuariPlugin = dadesUsuariPlugin;
	}
	private static final Logger logger = LoggerFactory.getLogger(PluginHelper.class);
}
