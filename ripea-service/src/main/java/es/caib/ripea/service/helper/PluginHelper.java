package es.caib.ripea.service.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.fundaciobit.plugins.validatesignature.api.IValidateSignaturePlugin;
import org.fundaciobit.plugins.validatesignature.api.SignatureDetailInfo;
import org.fundaciobit.plugins.validatesignature.api.SignatureRequestedInformation;
import org.fundaciobit.plugins.validatesignature.api.TimeStampInfo;
import org.fundaciobit.plugins.validatesignature.api.ValidateSignatureRequest;
import org.fundaciobit.plugins.validatesignature.api.ValidateSignatureResponse;
import org.fundaciobit.plugins.validatesignature.api.ValidationStatus;
import org.fundaciobit.pluginsib.validatecertificate.InformacioCertificat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.tool.xml.Experimental;

import es.caib.distribucio.rest.client.integracio.domini.Annex;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreEntrada;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId;
import es.caib.distribucio.rest.client.integracio.domini.Estat;
import es.caib.distribucio.rest.client.integracio.domini.NtiEstadoElaboracion;
import es.caib.distribucio.rest.client.integracio.domini.NtiOrigen;
import es.caib.distribucio.rest.client.integracio.domini.NtiTipoDocumento;
import es.caib.plugins.arxiu.api.Carpeta;
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
import es.caib.ripea.persistence.entity.CarpetaEntity;
import es.caib.ripea.persistence.entity.ContingutEntity;
import es.caib.ripea.persistence.entity.DispositiuEnviamentEntity;
import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.DocumentEnviamentInteressatEntity;
import es.caib.ripea.persistence.entity.DocumentNotificacioEntity;
import es.caib.ripea.persistence.entity.DocumentPortafirmesEntity;
import es.caib.ripea.persistence.entity.DocumentViaFirmaEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.ExpedientPeticioEntity;
import es.caib.ripea.persistence.entity.InteressatAdministracioEntity;
import es.caib.ripea.persistence.entity.InteressatEntity;
import es.caib.ripea.persistence.entity.InteressatPersonaFisicaEntity;
import es.caib.ripea.persistence.entity.InteressatPersonaJuridicaEntity;
import es.caib.ripea.persistence.entity.MetaDadaEntity;
import es.caib.ripea.persistence.entity.MetaDocumentEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.repository.ExpedientPeticioRepository;
import es.caib.ripea.persistence.repository.MetaDocumentRepository;
import es.caib.ripea.plugin.PropertiesHelper;
import es.caib.ripea.plugin.RipeaAbstractPluginProperties;
import es.caib.ripea.plugin.SistemaExternNoTrobatException;
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
import es.caib.ripea.plugin.distribucio.DistribucioPlugin;
import es.caib.ripea.plugin.firmaservidor.FirmaServidorPlugin;
import es.caib.ripea.plugin.firmaservidor.SignaturaResposta;
import es.caib.ripea.plugin.firmaweb.FirmaWebPlugin;
import es.caib.ripea.plugin.gesdoc.GestioDocumentalPlugin;
import es.caib.ripea.plugin.notificacio.EntregaPostalTipus;
import es.caib.ripea.plugin.notificacio.Enviament;
import es.caib.ripea.plugin.notificacio.EnviamentTipus;
import es.caib.ripea.plugin.notificacio.Notificacio;
import es.caib.ripea.plugin.notificacio.NotificacioPlugin;
import es.caib.ripea.plugin.notificacio.Persona;
import es.caib.ripea.plugin.notificacio.RespostaConsultaEstatEnviament;
import es.caib.ripea.plugin.notificacio.RespostaConsultaEstatNotificacio;
import es.caib.ripea.plugin.notificacio.RespostaEnviar;
import es.caib.ripea.plugin.notificacio.RespostaJustificantEnviamentNotib;
import es.caib.ripea.plugin.portafirmes.PortafirmesCarrec;
import es.caib.ripea.plugin.portafirmes.PortafirmesDocument;
import es.caib.ripea.plugin.portafirmes.PortafirmesDocumentTipus;
import es.caib.ripea.plugin.portafirmes.PortafirmesFluxBloc;
import es.caib.ripea.plugin.portafirmes.PortafirmesFluxInfo;
import es.caib.ripea.plugin.portafirmes.PortafirmesFluxResposta;
import es.caib.ripea.plugin.portafirmes.PortafirmesFluxReviser;
import es.caib.ripea.plugin.portafirmes.PortafirmesFluxSigner;
import es.caib.ripea.plugin.portafirmes.PortafirmesIniciFluxResposta;
import es.caib.ripea.plugin.portafirmes.PortafirmesPlugin;
import es.caib.ripea.plugin.portafirmes.PortafirmesPrioritatEnum;
import es.caib.ripea.plugin.procediment.ProcedimentPlugin;
import es.caib.ripea.plugin.summarize.SummarizePlugin;
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
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.AmpliarPlazoForm;
import es.caib.ripea.service.intf.dto.ArbreDto;
import es.caib.ripea.service.intf.dto.ArbreNodeDto;
import es.caib.ripea.service.intf.dto.ArxiuEstatEnumDto;
import es.caib.ripea.service.intf.dto.ArxiuFirmaDetallDto;
import es.caib.ripea.service.intf.dto.ArxiuFirmaDto;
import es.caib.ripea.service.intf.dto.ArxiuFirmaPerfilEnumDto;
import es.caib.ripea.service.intf.dto.ArxiuFirmaTipusEnumDto;
import es.caib.ripea.service.intf.dto.ArxiuOperacioEnumDto;
import es.caib.ripea.service.intf.dto.ContingutTipusEnumDto;
import es.caib.ripea.service.intf.dto.DiagnosticFiltreDto;
import es.caib.ripea.service.intf.dto.DigitalitzacioEstatDto;
import es.caib.ripea.service.intf.dto.DigitalitzacioPerfilDto;
import es.caib.ripea.service.intf.dto.DigitalitzacioResultatDto;
import es.caib.ripea.service.intf.dto.DigitalitzacioTransaccioRespostaDto;
import es.caib.ripea.service.intf.dto.DocumentDto;
import es.caib.ripea.service.intf.dto.DocumentEnviamentInteressatDto;
import es.caib.ripea.service.intf.dto.DocumentEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentFirmaTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNotificacioDto;
import es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNtiTipoFirmaEnumDto;
import es.caib.ripea.service.intf.dto.DocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.ExpedientEstatEnumDto;
import es.caib.ripea.service.intf.dto.FirmaResultatDto;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.dto.ImportacioDto;
import es.caib.ripea.service.intf.dto.IntegracioAccioDto;
import es.caib.ripea.service.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.ripea.service.intf.dto.InteressatTipusEnumDto;
import es.caib.ripea.service.intf.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.service.intf.dto.LogTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentTipusGenericEnumDto;
import es.caib.ripea.service.intf.dto.MunicipiDto;
import es.caib.ripea.service.intf.dto.NivellAdministracioDto;
import es.caib.ripea.service.intf.dto.NtiOrigenEnumDto;
import es.caib.ripea.service.intf.dto.PaisDto;
import es.caib.ripea.service.intf.dto.PortafirmesCarrecDto;
import es.caib.ripea.service.intf.dto.PortafirmesDocumentTipusDto;
import es.caib.ripea.service.intf.dto.PortafirmesFluxEstatDto;
import es.caib.ripea.service.intf.dto.PortafirmesFluxInfoDto;
import es.caib.ripea.service.intf.dto.PortafirmesFluxRespostaDto;
import es.caib.ripea.service.intf.dto.PortafirmesFluxReviserDto;
import es.caib.ripea.service.intf.dto.PortafirmesFluxSignerDto;
import es.caib.ripea.service.intf.dto.PortafirmesIniciFluxRespostaDto;
import es.caib.ripea.service.intf.dto.ProcedimentDto;
import es.caib.ripea.service.intf.dto.ProvinciaDto;
import es.caib.ripea.service.intf.dto.RespostaAmpliarPlazo;
import es.caib.ripea.service.intf.dto.Resum;
import es.caib.ripea.service.intf.dto.SignatureInfoDto;
import es.caib.ripea.service.intf.dto.TipusClassificacioEnumDto;
import es.caib.ripea.service.intf.dto.TipusDocumentalDto;
import es.caib.ripea.service.intf.dto.TipusImportEnumDto;
import es.caib.ripea.service.intf.dto.TipusViaDto;
import es.caib.ripea.service.intf.dto.UnitatOrganitzativaDto;
import es.caib.ripea.service.intf.dto.UsuariDto;
import es.caib.ripea.service.intf.dto.ViaFirmaDispositiuDto;
import es.caib.ripea.service.intf.dto.config.ConfigDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.exception.SistemaExternException;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.utils.Utils;

@Component
public class PluginHelper {

    private final UsuariHelper usuariHelper;

	public static final String GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_DOC_TMP = "anotacions_registre_doc_tmp";
	public static final String GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_FIR_TMP = "anotacions_registre_fir_tmp";
	public static final String GESDOC_AGRUPACIO_CERTIFICACIONS = "certificacions";
	public static final String GESDOC_AGRUPACIO_NOTIFICACIONS = "notificacions";
	public static final String GESDOC_AGRUPACIO_DOCS_FIRMATS_PORTAFIB = "docsFirmats"; //documents signed by portafib that haven't been saved in arxiu  
	public static final String GESDOC_AGRUPACIO_DOCS_ADJUNTS = "docsAdjunts"; // documents adjunts when creating document that haven't been saved in arxiu
	public static final String GESDOC_AGRUPACIO_DOCS_ORIGINALS = "docsOriginals"; // documents amb firma inválida
	public static final String GESDOC_AGRUPACIO_DOCS_ESBORRANYS = "docsEsborranys"; // firma separada of documents which are saved in arxiu as esborannys

	private DadesUsuariPlugin dadesUsuariPlugin;
	private Map<String, UnitatsOrganitzativesPlugin> unitatsOrganitzativesPlugins = new HashMap<>();
	private Map<String, PortafirmesPlugin> portafirmesPlugins = new HashMap<>();
	private Map<String, DigitalitzacioPlugin> digitalitzacioPlugins = new HashMap<>();
	private Map<String, ConversioPlugin> conversioPlugins = new HashMap<>();
	private Map<String, DadesExternesPlugin> dadesExternesPlugins = new HashMap<>();
	private Map<String, DadesExternesPlugin> dadesExternesPinbalPlugins = new HashMap<>();
	private Map<String, IArxiuPluginWrapper> arxiuPlugins = new HashMap<>();
	private Map<String, IValidateSignaturePluginWrapper> validaSignaturaPlugins = new HashMap<>();
	private Map<String, NotificacioPlugin> notificacioPlugins = new HashMap<>();
	private Map<String, GestioDocumentalPlugin> gestioDocumentalPlugins = new HashMap<>();
	private Map<String, FirmaServidorPlugin> firmaServidorPlugins = new HashMap<>();
	private Map<String, ViaFirmaPlugin> viaFirmaPlugins = new HashMap<>();
	private Map<String, ProcedimentPlugin> procedimentPlugins = new HashMap<>();
	private Map<String, FirmaWebPlugin> firmaSimpleWebPlugins = new HashMap<>();
	private Map<String, SummarizePlugin> summarizePlugins = new HashMap<>();
	private Map<String, DistribucioPlugin> distribucioPlugins = new HashMap<>();

	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private IntegracioHelper integracioHelper;
	@Autowired private DocumentHelper documentHelper;
	@Autowired private DadesExternesHelper dadesExternesHelper;
	@Autowired private AplicacioService aplicacioService;
	@Autowired private UnitatOrganitzativaHelper unitatOrganitzativaHelper;
	@Autowired private ConfigHelper configHelper;
	@Autowired private OrganGestorHelper organGestorHelper;
	@Autowired private CacheHelper cacheHelper;
	@Autowired private ContingutLogHelper contingutLogHelper;
	@Autowired private MetaDocumentRepository metaDocumentRepository;
	@Autowired private EmailHelper emailHelper;
	@Autowired private ContingutHelper contingutHelper;
	@Autowired private DocumentNotificacioHelper documentNotificacioHelper;
	@Autowired private ExpedientPeticioRepository expedientPeticioRepository;

    PluginHelper(UsuariHelper usuariHelper) {
        this.usuariHelper = usuariHelper;
    }

	public List<String> rolsUsuariFindAmbCodi(String usuariCodi) {

		long t0 = System.currentTimeMillis();
		String accioDescripcio = "Consulta rols a partir del codi d'usuari";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codi", usuariCodi);
		DadesUsuariPlugin dadesUsuariPlugin = getDadesUsuariPlugin();
		
		try {
			List<String> rolsDisponibles = dadesUsuariPlugin.findRolsAmbCodi(usuariCodi);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					dadesUsuariPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return rolsDisponibles;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					dadesUsuariPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_USUARIS, errorDescripcio, ex);
		}
	}

	public DadesUsuari dadesUsuariFindAmbCodi(
			String usuariCodi) {

		long t0 = System.currentTimeMillis();
		String accioDescripcio = "Consulta d'usuari amb codi";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codi", usuariCodi);
		DadesUsuariPlugin dadesUsuariPlugin = getDadesUsuariPlugin();
		
		try {
			DadesUsuari dadesUsuari = dadesUsuariPlugin.findAmbCodi(
					usuariCodi);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					dadesUsuariPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return dadesUsuari;
		} catch (SistemaExternNoTrobatException snte) {
			String errorDescripcio = "No s'ha trobat cap usuari al sistema extern";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					dadesUsuariPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					snte);
			return null;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					dadesUsuariPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_USUARIS, errorDescripcio, ex);
		}
	}

	public List<DadesUsuari> dadesUsuariFindAmbGrup(
			String grupCodi) {

		long t0 = System.currentTimeMillis();
		String accioDescripcio = "Consulta d'usuaris d'un grup";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("grup", grupCodi);
		DadesUsuariPlugin dadesUsuariPlugin = getDadesUsuariPlugin();

		try {
			List<DadesUsuari> dadesUsuari = dadesUsuariPlugin.findAmbGrup(
					grupCodi);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					dadesUsuariPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return dadesUsuari;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					dadesUsuariPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_USUARIS, errorDescripcio, ex);
		}
	}

	public List<DadesUsuari> findAmbFiltre(
			String filtre) throws SistemaExternException {

		long t0 = System.currentTimeMillis();
		String accioDescripcio = "Consulta d'usuaris d'un filtre";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("filtre", filtre);
		DadesUsuariPlugin dadesUsuariPlugin = getDadesUsuariPlugin();
		
		try {
			List<DadesUsuari> dadesUsuari = dadesUsuariPlugin.findAmbFiltre(filtre);
			
			if (dadesUsuari==null) {
				return usuariHelper.findDadesUsuariAmbText(filtre);
			}
			
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					dadesUsuariPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return dadesUsuari;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					dadesUsuariPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_USUARIS, errorDescripcio, ex);
		}
	}

	// UNITATS ORGANITZATIVES
	// /////////////////////////////////////////////////////////////////////////////////////
	public Map<String, NodeDir3> getOrganigramaOrganGestor(
			String codiDir3) throws SistemaExternException {

		long t0 = System.currentTimeMillis();
		String accioDescripcio = "Obtenir organigrama per entitat";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"Codi Dir3 de l'entitat",
				codiDir3);
		Map<String, NodeDir3> organigrama = null;
		UnitatsOrganitzativesPlugin unitatsOrganitzativesPlugin = getUnitatsOrganitzativesPlugin();
		
		try {
			organigrama = unitatsOrganitzativesPlugin.organigrama(codiDir3);
			
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_UNITATS,
					accioDescripcio,
					unitatsOrganitzativesPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al obtenir l'organigrama per entitat";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_UNITATS,
					accioDescripcio,
					unitatsOrganitzativesPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			logger.error(
					errorDescripcio,
					ex);
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
		accioParams.put(
				"unitatPare",
				pareCodi);
		accioParams.put(
				"fechaActualizacion",
				dataActualitzacio == null ? null : dataActualitzacio.toString());
		accioParams.put(
				"fechaSincronizacion",
				dataSincronitzacio == null ? null : dataSincronitzacio.toString());
		long t0 = System.currentTimeMillis();

		List<UnitatOrganitzativa> unitatsOrganitzatives = null;
		UnitatsOrganitzativesPlugin unitatsOrganitzativesPlugin = getUnitatsOrganitzativesPlugin();
		
		try {
			unitatsOrganitzatives = unitatsOrganitzativesPlugin.findAmbPare(
					pareCodi,
					dataActualitzacio,
					dataSincronitzacio);

			removeUnitatsSubstitutedByItself(
					unitatsOrganitzatives);

		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'unitats organitzatives";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_UNITATS,
					accioDescripcio,
					unitatsOrganitzativesPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorDescripcio, ex);
		}

		if (unitatsOrganitzatives == null || unitatsOrganitzatives.isEmpty()) {
			try {
				unitatsOrganitzativesPlugin.findAmbCodi(
						pareCodi);
			} catch (Exception e) {
				String errorMissatge = "No s'ha trobat la unitat organitzativa llistat (codi=" + pareCodi + ")";
				integracioHelper.addAccioError(
						IntegracioHelper.INTCODI_UNITATS,
						accioDescripcio,
						unitatsOrganitzativesPlugin.getEndpointURL(),
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0,
						errorMissatge,
						null);
				throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorMissatge);
			}
		}

		integracioHelper.addAccioOk(
				IntegracioHelper.INTCODI_UNITATS,
				accioDescripcio,
				unitatsOrganitzativesPlugin.getEndpointURL(),
				accioParams,
				IntegracioAccioTipusEnumDto.ENVIAMENT,
				System.currentTimeMillis() - t0);
		return unitatsOrganitzatives;

	}

	/**
	 * Remove from list unitats that are substituted by itself for example if
	 * webservice returns two elements:
	 * 
	 * UnitatOrganitzativa(codi=A00000010, estat=E, historicosUO=[A00000010])
	 * UnitatOrganitzativa(codi=A00000010, estat=V, historicosUO=null)
	 * 
	 * then remove the first one. That way this transition can be treated by
	 * application the same way as transition CANVI EN ATRIBUTS
	 */
	private void removeUnitatsSubstitutedByItself(
			List<UnitatOrganitzativa> unitatsOrganitzatives) {
		if (CollectionUtils.isNotEmpty(
				unitatsOrganitzatives)) {
			Iterator<UnitatOrganitzativa> i = unitatsOrganitzatives.iterator();
			while (i.hasNext()) {
				UnitatOrganitzativa unitatOrganitzativa = i.next();
				if (CollectionUtils.isNotEmpty(
						unitatOrganitzativa.getHistoricosUO())
						&& unitatOrganitzativa.getHistoricosUO().size() == 1
						&& unitatOrganitzativa.getHistoricosUO().get(
								0).equals(
										unitatOrganitzativa.getCodi())) {
					i.remove();
				}
			}
		}
	}

	public ArbreDto<UnitatOrganitzativaDto> unitatsOrganitzativesFindArbreByPare(
			String pareCodi) {

		String accioDescripcio = "Consulta de l'arbre d'unitats donat un pare";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("unitatPare",pareCodi);
		long t0 = System.currentTimeMillis();
		UnitatsOrganitzativesPlugin unitatsOrganitzativesPlugin = getUnitatsOrganitzativesPlugin();
		
		try {
			
			List<UnitatOrganitzativa> unitatsOrganitzatives = unitatsOrganitzativesPlugin.findAmbPare(
					pareCodi);
			ArbreDto<UnitatOrganitzativaDto> resposta = new ArbreDto<UnitatOrganitzativaDto>(false);
			// Cerca l'unitat organitzativa arrel
			UnitatOrganitzativa unitatOrganitzativaArrel = null;
			for (UnitatOrganitzativa unitatOrganitzativa : unitatsOrganitzatives) {
				if (pareCodi.equalsIgnoreCase(
						unitatOrganitzativa.getCodi())) {
					unitatOrganitzativaArrel = unitatOrganitzativa;
					break;
				}
			}
			if (unitatOrganitzativaArrel != null) {
				// Omple l'arbre d'unitats organitzatives
				resposta.setArrel(
						getNodeArbreUnitatsOrganitzatives(
								unitatOrganitzativaArrel,
								unitatsOrganitzatives,
								null));
				integracioHelper.addAccioOk(
						IntegracioHelper.INTCODI_UNITATS,
						accioDescripcio,
						unitatsOrganitzativesPlugin.getEndpointURL(),
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0);
				return resposta;
			} else {
				String errorMissatge = "No s'ha trobat la unitat organitzativa arrel (codi=" + pareCodi + ")";
				integracioHelper.addAccioError(
						IntegracioHelper.INTCODI_UNITATS,
						accioDescripcio,
						unitatsOrganitzativesPlugin.getEndpointURL(),
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0,
						errorMissatge,
						null);
				throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorMissatge);
			}
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'unitats organitzatives";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_UNITATS,
					accioDescripcio,
					unitatsOrganitzativesPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorDescripcio, ex);
		}
	}

	public UnitatOrganitzativaDto unitatsOrganitzativesFindByCodi(
			String codi) {

		String accioDescripcio = "Consulta d'unitat organitzativa donat el seu codi";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codi",	codi);
		long t0 = System.currentTimeMillis();
		UnitatsOrganitzativesPlugin unitatsOrganitzativesPlugin = getUnitatsOrganitzativesPlugin();
		
		try {
			
			UnitatOrganitzativaDto unitatOrganitzativa = conversioTipusHelper.convertir(
					unitatsOrganitzativesPlugin.findAmbCodi(
							codi),
					UnitatOrganitzativaDto.class);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_UNITATS,
					accioDescripcio,
					unitatsOrganitzativesPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return unitatOrganitzativa;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'unitats organitzatives";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_UNITATS,
					accioDescripcio,
					unitatsOrganitzativesPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorDescripcio, ex);
		}
	}

	public List<UnitatOrganitzativaDto> unitatsOrganitzativesFindByFiltre(
			String codiUnitat,
			String denominacioUnitat,
			String codiNivellAdministracio,
			String codiComunitat,
			String codiProvincia,
			String codiLocalitat,
			Boolean esUnitatArrel) {

		String accioDescripcio = "Consulta d'unitats organitzatives donat un filtre";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"codiUnitat",
				codiUnitat);
		accioParams.put(
				"denominacioUnitat",
				denominacioUnitat);
		accioParams.put(
				"codiNivellAdministracio",
				codiNivellAdministracio);
		accioParams.put(
				"codiComunitat",
				codiComunitat);
		accioParams.put(
				"codiProvincia",
				codiProvincia);
		accioParams.put(
				"codiLocalitat",
				codiLocalitat);
		accioParams.put(
				"esUnitatArrel",
				esUnitatArrel == null ? "null" : esUnitatArrel.toString());
		long t0 = System.currentTimeMillis();
		UnitatsOrganitzativesPlugin unitatsOrganitzativesPlugin = getUnitatsOrganitzativesPlugin();
		
		try {
			List<UnitatOrganitzativa> units = unitatsOrganitzativesPlugin.cercaUnitats(
					codiUnitat,
					denominacioUnitat,
					toLongValue(
							codiNivellAdministracio),
					toLongValue(
							codiComunitat),
					false,
					esUnitatArrel,
					toLongValue(
							codiProvincia),
					codiLocalitat);
			List<UnitatOrganitzativaDto> unitatsOrganitzatives = conversioTipusHelper.convertirList(
					units,
					UnitatOrganitzativaDto.class);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_UNITATS,
					accioDescripcio,
					unitatsOrganitzativesPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return unitatsOrganitzatives;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al realitzar la cerca de unitats organitzatives";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_UNITATS,
					accioDescripcio,
					unitatsOrganitzativesPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorDescripcio, ex);
		}
	}

	public boolean isArxiuPluginActiu() {
		return getArxiuPlugin() != null;
	}

	public boolean arxiuSuportaVersionsExpedients() {
		return getArxiuPlugin().getPlugin().suportaVersionatExpedient();
	}

	public boolean arxiuSuportaVersionsDocuments() {
		return getArxiuPlugin().getPlugin().suportaVersionatDocument();
	}

	public void arxiuExpedientActualitzar(ExpedientEntity expedient) {
		
		//Si venim de un procés en segon pla, no tenim sessió hibernate. Aquest metode fa un deproxy.
		String organCodiDir3 = organGestorHelper.getOrganCodiFromContingutId(expedient.getId());
		organGestorHelper.actualitzarOrganCodi(organCodiDir3);

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
		accioParams.put("classificacio", metaExpedient.getClassificacio());
		accioParams.put("serieDocumental", metaExpedient.getSerieDocumental());
		accioParams.put("organ", organCodiDir3);
		accioParams.put("estat", expedient.getEstat().name());
		
		long t0 = System.currentTimeMillis();
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();

		try {
			String classificacio = metaExpedient.getClassificacio();
			List<String> interessats = new ArrayList<String>();
			for (InteressatEntity interessat : expedient.getInteressatsORepresentants()) {
				if (interessat.getDocumentNum() != null) {
					interessats.add(
							interessat.getDocumentNum());
				}
			}

			if (expedient.getArxiuUuid() == null) {

				ContingutArxiu expedientCreat = arxiuPluginWrapper.getPlugin().expedientCrear(
						toArxiuExpedient(
								null,
								expedient.getNom() + " (" + System.currentTimeMillis() + ")",
								null,
								Arrays.asList(
										organCodiDir3),
								Date.from(expedient.getCreatedDate().get().atZone(ZoneId.systemDefault()).toInstant()),
								classificacio,
								expedient.getEstat(),
								interessats,
								metaExpedient.getSerieDocumental(),
								expedient.getNumero()));
				Expedient expedientDetalls = arxiuPluginWrapper.getPlugin().expedientDetalls(
						expedientCreat.getIdentificador(),
						null);
				propagarMetadadesExpedient(
						expedientDetalls,
						expedient);
				expedient.updateArxiu(
						expedientCreat.getIdentificador());

			} else {
				if (interessats.isEmpty()) { interessats = null; }
				arxiuPluginWrapper.getPlugin().expedientModificar(
						toArxiuExpedient(
								expedient.getArxiuUuid(),
								expedient.getNom() + " (" + System.currentTimeMillis() + ")",
								expedient.getNtiIdentificador(),
								Arrays.asList(organCodiDir3),
								Date.from(expedient.getCreatedDate().get().atZone(ZoneId.systemDefault()).toInstant()),
								classificacio,
								expedient.getEstat(),
								interessats,
								metaExpedient.getSerieDocumental(),
								expedient.getNumero()));
				expedient.updateArxiu(null);
			}
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public void arxiuExpedientMetadadesActualitzar(
			ExpedientEntity expedient,
			MetaDadaEntity metaDada,
			String valor) {
		String accioDescripcio = "Actualització de les meta-dades d'un expedient";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"id",
				expedient.getId().toString());
		accioParams.put(
				"títol",
				expedient.getNom());
		accioParams.put(
				"metaDada",
				metaDada.getNom());
		accioParams.put(
				"dadaValor",
				valor);
		MetaExpedientEntity metaExpedient = expedient.getMetaExpedient();
		accioParams.put(
				"tipus",
				metaExpedient.getNom());
		accioParams.put(
				"estat",
				expedient.getEstat().name());
		long t0 = System.currentTimeMillis();
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		try {
			List<String> interessats = new ArrayList<String>();
			for (InteressatEntity interessat : expedient.getInteressatsORepresentants()) {
				if (interessat.getDocumentNum() != null) {
					interessats.add(
							interessat.getDocumentNum());
				}
			}

			arxiuPluginWrapper.getPlugin().expedientModificar(
					toArxiuExpedient(
							expedient.getArxiuUuid(),
							expedient.getNom(),
							metaDada,
							valor,
							interessats,
							expedient.getNumero()));
			expedient.updateArxiu(
					null);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public Expedient arxiuExpedientConsultarPerUuid(
			String uuid) {

		String accioDescripcio = "Consulta d'un expedient per uuid";
		Map<String, String> accioParams = new HashMap<String, String>();
		long t0 = System.currentTimeMillis();
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		try {
			Expedient arxiuExpedient = arxiuPluginWrapper.getPlugin().expedientDetalls(
					uuid,
					null);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return arxiuExpedient;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public Expedient arxiuExpedientConsultar(
			ExpedientEntity expedient) {
        return arxiuExpedientConsultar(expedient.getId(), expedient.getNom(), expedient.getMetaExpedient().getNom(), expedient.getArxiuUuid());
	}
	public Expedient arxiuExpedientConsultar(Long id, String nom, String metaExpedientNom, String arxiuUuid) {
		organGestorHelper.actualitzarOrganCodi(
				organGestorHelper.getOrganCodiFromContingutId(
						id));
		String accioDescripcio = "Consulta d'un expedient";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"id",
				id.toString());
		accioParams.put(
				"títol",
                nom);
		accioParams.put(
				"tipus",
                metaExpedientNom);
		long t0 = System.currentTimeMillis();
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		try {
			Expedient arxiuExpedient = arxiuPluginWrapper.getPlugin().expedientDetalls(
                    arxiuUuid,
					null);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return arxiuExpedient;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public void arxiuExpedientEsborrar(
			ExpedientEntity expedient) {
		organGestorHelper.actualitzarOrganCodi(
				organGestorHelper.getOrganCodiFromContingutId(
						expedient.getId()));
		String accioDescripcio = "Eliminació d'un expedient";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"id",
				expedient.getId().toString());
		accioParams.put(
				"títol",
				expedient.getNom());
		accioParams.put(
				"tipus",
				expedient.getMetaExpedient().getNom());
		long t0 = System.currentTimeMillis();
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		try {
			arxiuPluginWrapper.getPlugin().expedientEsborrar(
					expedient.getArxiuUuid());
			expedient.updateArxiuEsborrat();
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public void arxiuExpedientEsborrarPerUuid(
			String uuid) {

		String accioDescripcio = "Eliminació d'un expedient";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"uuid",
				uuid);
		long t0 = System.currentTimeMillis();
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		try {
			arxiuPluginWrapper.getPlugin().expedientEsborrar(
					uuid);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public void arxiuExpedientTancar(
			ExpedientEntity expedient) {
		organGestorHelper.actualitzarOrganCodi(
				organGestorHelper.getOrganCodiFromContingutId(
						expedient.getId()));
		String accioDescripcio = "Tancament d'un expedient";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"id",
				expedient.getId().toString());
		accioParams.put(
				"títol",
				expedient.getNom());
		accioParams.put(
				"tipus",
				expedient.getMetaExpedient().getNom());
		long t0 = System.currentTimeMillis();
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		try {
			String arxiuUuid = arxiuPluginWrapper.getPlugin().expedientTancar(
					expedient.getArxiuUuid());
			if (arxiuUuid != null) {
				expedient.updateArxiu(
						arxiuUuid);
			}
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public void arxiuExpedientReobrir(
			ExpedientEntity expedient) {
		organGestorHelper.actualitzarOrganCodi(
				organGestorHelper.getOrganCodiFromContingutId(
						expedient.getId()));
		String accioDescripcio = "Reobertura d'un expedient";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"id",
				expedient.getId().toString());
		accioParams.put(
				"títol",
				expedient.getNom());
		accioParams.put(
				"tipus",
				expedient.getMetaExpedient().getNom());
		long t0 = System.currentTimeMillis();
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		try {
			arxiuPluginWrapper.getPlugin().expedientReobrir(
					expedient.getArxiuUuid());
			expedient.updateArxiuEsborrat();
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public String arxiuExpedientExportar(
			ExpedientEntity expedient) {
		organGestorHelper.actualitzarOrganCodi(
				organGestorHelper.getOrganCodiFromContingutId(
						expedient.getId()));
		String accioDescripcio = "Exportar expedient en format ENI";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"id",
				expedient.getId().toString());
		accioParams.put(
				"títol",
				expedient.getNom());
		long t0 = System.currentTimeMillis();
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		try {
			String exportacio = arxiuPluginWrapper.getPlugin().expedientExportarEni(
					expedient.getArxiuUuid());
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return exportacio;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public void arxiuExpedientEnllacar(
			ExpedientEntity expedientFill,
			ExpedientEntity expedientPare) {
		organGestorHelper.actualitzarOrganCodi(
				organGestorHelper.getOrganCodiFromContingutId(
						expedientPare.getId()));
		String accioDescripcio = "Enllaçant dos expedients (expedientUuidPare=" + expedientPare.getId()
				+ ", expedientUuidFill=" + expedientFill.getId() + ")";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"idExpedientPare",
				expedientPare.getId().toString());
		accioParams.put(
				"titolExpedientPare",
				expedientPare.getNom());
		accioParams.put(
				"idExpedientFill",
				expedientFill.getId().toString());
		accioParams.put(
				"titolExpedientFill",
				expedientFill.getNom());
		long t0 = System.currentTimeMillis();
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		try {
			arxiuPluginWrapper.getPlugin().expedientLligar(
					expedientPare.getArxiuUuid(),
					expedientFill.getArxiuUuid());
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
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
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		try {
			arxiuPluginWrapper.getPlugin().expedientDeslligar(expedientPare.getArxiuUuid(), expedientFill.getArxiuUuid());
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio, 
					arxiuPluginWrapper.getEndpoint(),
					accioParams, 
					IntegracioAccioTipusEnumDto.ENVIAMENT, 
					System.currentTimeMillis() - t0, 
					errorDescripcio, 
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public void arxiuDocumentActualitzar(
			DocumentEntity document,
			FitxerDto fitxer,
			DocumentFirmaTipusEnumDto documentFirmaTipus,
			List<ArxiuFirmaDto> firmes,
			ArxiuEstatEnumDto arxiuEstat) {
		organGestorHelper.actualitzarOrganCodi(
				organGestorHelper.getOrganCodiFromContingutId(
						document.getId()));
		ContingutArxiu documentArxiuCreatOModificat = null;

		boolean throwExceptionDocumentArxiu = false;
		if (throwExceptionDocumentArxiu) { // throwExceptionDocumentArxiu = true;
			throw new RuntimeException("Excepcio provocada expressament al actualitzar document al arxiu");
		}

		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		IntegracioAccioDto integracioAccio = getIntegracioAccioArxiu(
				document,
				arxiuPluginWrapper.getEndpoint(),
				"Actualització de les dades d'un document");

		ContingutEntity contingutPare = getContingutPare(document);

		try {
			if (document.getArxiuUuid() == null) {
				// =============== CREAR DOCUMENT EN ARXIU ===================
				Document documentArxiu = toArxiuDocument(
						document,
						contingutPare,
						fitxer,
						documentFirmaTipus,
						firmes,
						ArxiuOperacioEnumDto.CREACIO,
						arxiuEstat);
				
				try {
					documentArxiuCreatOModificat = arxiuPluginWrapper.getPlugin().documentCrear(
							documentArxiu,
							contingutPare.getArxiuUuid());
				} catch (Exception ex) {
					if (ex.getMessage().contains("Duplicate child name not allowed")) {
						try {
							//#1631 Reintentam amb un altre nom, tot i que en principi, ha passat per el mètode documentNomInArxiu
							documentArxiu.setNom(documentArxiu.getNom()+"_"+System.currentTimeMillis());
							documentArxiuCreatOModificat = arxiuPluginWrapper.getPlugin().documentCrear(
									documentArxiu,
									contingutPare.getArxiuUuid());
						} catch (Exception ex2) {
							throw ex2;
						}
					} else if (ex.getMessage().contains("Petición mal formada")) {
						logger.error(
								"Error al crear o modificar el documento en el arxiu. Document a desar a l'arxiu ja existeix.");
						logger.error(
								"\t>>> Id pare=" + contingutPare.getArxiuUuid());
						logger.error(
								"\t>>> Nom pare=" + contingutPare.getNom());
						logger.error(
								"\t>>> Nom=" + documentArxiu.getNom());
						throw ex;
					} else {
						throw ex;
					}
				}
				
				document.updateArxiu(documentArxiuCreatOModificat.getIdentificador());
				document.updateArxiuEstat(arxiuEstat);

			} else {
				// =============== MODIFICAR DOCUMENT EN ARXIU ===================
				Document documentArxiu = toArxiuDocument(
						document,
						contingutPare,
						fitxer,
						documentFirmaTipus,
						firmes,
						ArxiuOperacioEnumDto.MODIFICACIO,
						arxiuEstat);
				try {
					documentArxiuCreatOModificat = arxiuPluginWrapper.getPlugin().documentModificar(documentArxiu);
				} catch (Exception ex) {
					if (ex.getMessage().contains("Duplicate child name not allowed")) {
						try {
							//#1631 Reintentam amb un altre nom, tot i que en principi, ha passat per el mètode documentNomInArxiu...
							documentArxiu.setNom(documentArxiu.getNom()+"_"+System.currentTimeMillis());
							documentArxiuCreatOModificat = arxiuPluginWrapper.getPlugin().documentModificar(documentArxiu);
						} catch (Exception ex2) {
							throw ex2;
						}
					} else {
						throw ex;
					}
				}
				
				document.updateArxiu(null);
				document.updateArxiuEstat(arxiuEstat);
			}
			
			Document documentDetalls = arxiuPluginWrapper.getPlugin().documentDetalls(
					documentArxiuCreatOModificat.getIdentificador(),
					null,
					false);
			
			propagarMetadadesDocument(documentDetalls, document);
			arxiuEnviamentOk(integracioAccio);
			
		} catch (Exception ex) {
			throw arxiuEnviamentError(integracioAccio, ex);
		}
	}

	// Plugin arxiu filesystem
	public String arxiuExpedientDistribucioCrear(
			String registreNumero,
			String expedientNumero,
			String unitatOrganitzativaCodi) {
		String accioDescripcio = "Creant contenidor per als documents annexos de Distribució";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"registreNumero",
				registreNumero);
		accioParams.put(
				"unitatOrganitzativaCodi",
				unitatOrganitzativaCodi);
		long t0 = System.currentTimeMillis();
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		try {
			String nomExpedient = "EXP_REG_" + expedientNumero + "_" + System.currentTimeMillis();

			logger.debug(
					"Creant contenidor annexos Distribució:" + nomExpedient);

			ContingutArxiu expedient = arxiuPluginWrapper.getPlugin().expedientCrear(
					toArxiuExpedient(
							null,
							nomExpedient,
							null,
							Arrays.asList(
									unitatOrganitzativaCodi),
							new Date(),
							null,
							ExpedientEstatEnumDto.OBERT,
							null,
							getPropertyPluginRegistreExpedientSerieDocumental(),
							null));

			logger.debug(
					"Contenidor annexos Distribució creat: " + expedient.getIdentificador());

			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0);
			return expedient.getIdentificador();
		} catch (Exception ex) {
			String errorDescripcio = "Error al crear contenidor per als documents annexos de Distribució";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public String arxiuAnnexDistribucioCrear(
			Annex annex,
			String unitatArrelCodi,
			String uuidExpedient) {
		String accioDescripcio = "Creant annex de Distribució";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"unitatArrelCodi",
				unitatArrelCodi);
		accioParams.put(
				"uuidExpedient",
				uuidExpedient);
		long t0 = System.currentTimeMillis();
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		String uuidDocument = null;
		try {

			logger.debug(
					"Creant annex Distribució:" + annex.getNom());

			FitxerDto fitxerContingut = new FitxerDto();
			fitxerContingut.setNom(
					annex.getNom());
			fitxerContingut.setContentType(
					annex.getTipusMime());
			fitxerContingut.setContingut(
					annex.getContingut());
			fitxerContingut.setTamany(
					annex.getTamany());

			List<ArxiuFirmaDto> firmes = new ArrayList<ArxiuFirmaDto>();
			if (annex.getFirmaTipus() != null && !"CSV".equals(
					annex.getFirmaTipusMime())) {
				logger.debug(
						"Validant firmes annex Distribució...");
				firmes = validaSignaturaObtenirFirmes(
						annex.getNom(),
						annex.getContingut(),
						annex.getFirmaContingut(),
						annex.getTipusMime(),
						false);
				logger.debug(
						"Total firmes annex Distribució preparades: " + firmes.size());
			}

			ContingutArxiu document = arxiuPluginWrapper.getPlugin().documentCrear(
					toArxiuDocument(
							null,
							annex.getNom(),
							annex.getTitol(),
							fitxerContingut,
							firmes,
							null,
							annex.getNtiOrigen(),
							Arrays.asList(
									unitatArrelCodi),
							annex.getNtiFechaCaptura(),
							annex.getNtiEstadoElaboracion(),
							annex.getNtiTipoDocumental(),
							DocumentEstat.valueOf(
									annex.getEstat().name()),
							annex.getFirmaTipus(),
							null),
					uuidExpedient);

			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0);

			uuidDocument = document.getIdentificador();

			logger.debug(
					"Annex Distribució creat:" + uuidDocument);

		} catch (Exception ex) {
			String errorDescripcio = "Error al crear annex de Distribució";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}

		return uuidDocument;
	}

	public void arxiuPropagarFirmaSeparada(
			DocumentEntity document,
			FitxerDto fitxerFirma) {
		
		organGestorHelper.actualitzarOrganCodi(
				organGestorHelper.getOrganCodiFromContingutId(
						document.getId()));
		
		boolean throwExceptionDocumentArxiu = false;
		if (throwExceptionDocumentArxiu) { // throwExceptionDocumentArxiu = true;
			throw new RuntimeException("Mock excepcion al actualitzar firma al arxiu");
		}
		
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		IntegracioAccioDto integracioAccio = getIntegracioAccioArxiu(
				document,
				arxiuPluginWrapper.getEndpoint(),
				"Actualització de les dades d'una firma separada del document esboranny");

		ContingutEntity contingutPare = getContingutPare(document);

		Document documentArxiu = new Document();
		documentArxiu.setMetadades(new DocumentMetadades());
		documentArxiu.setEstat(DocumentEstat.ESBORRANY);

		String documentNomInArxiu = documentNomInArxiu(
				document.getNom() + "_firma_separada",
				document.getPare());
		documentArxiu.setNom(documentNomInArxiu);

		documentArxiu.setContingut(
				getDocumentContingut(
						fitxerFirma.getNom(),
						fitxerFirma.getContentType(),
						fitxerFirma.getContingut()));

		try {

			if (StringUtils.isEmpty(document.getArxiuUuidFirma())) {
				
				ContingutArxiu contingutArxiu = arxiuPluginWrapper.getPlugin().documentCrear(
						documentArxiu,
						contingutPare.getArxiuUuid());

				document.setArxiuUuidFirma(contingutArxiu.getIdentificador());
			} else {
				documentArxiu.setIdentificador(document.getArxiuUuidFirma());
				arxiuPluginWrapper.getPlugin().documentModificar(documentArxiu);
			}

			arxiuEnviamentOk(integracioAccio);
			
		} catch (Exception ex) {
			throw arxiuEnviamentError(
					integracioAccio,
					ex);
		}
	}

	private void arxiuEnviamentOk(
			IntegracioAccioDto integracioAccio) {
		integracioHelper.addAccioOk(
				IntegracioHelper.INTCODI_ARXIU,
				integracioAccio.getDescripcio(),
				integracioAccio.getEndpoint(),
				integracioAccio.getParametres(),
				IntegracioAccioTipusEnumDto.ENVIAMENT,
				System.currentTimeMillis() - integracioAccio.getTempsInici());
	}

	private SistemaExternException arxiuEnviamentError(
			IntegracioAccioDto integracioAccio,
			Exception ex) {
		String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
		integracioHelper.addAccioError(
				IntegracioHelper.INTCODI_ARXIU,
				integracioAccio.getDescripcio(),
				integracioAccio.getEndpoint(),
				integracioAccio.getParametres(),
				IntegracioAccioTipusEnumDto.ENVIAMENT,
				System.currentTimeMillis() - integracioAccio.getTempsInici(),
				errorDescripcio,
				ex);
		return new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
	}

	private ContingutEntity getContingutPare(
			DocumentEntity document) {
		boolean utilitzarCarpetesEnArxiu = !isCarpetaLogica();
		ContingutEntity contingutPare = utilitzarCarpetesEnArxiu ? document.getPare() : document.getExpedient();
		return contingutPare;
	}

	/**
	 * 
	 * doesn't work correctly with ArxiuPluginCaib, changes firma type in arxiu to
	 * CSV
	 */
	@Experimental
	public void arxiuDocumentSetDefinitiu(
			DocumentEntity document) {
		organGestorHelper.actualitzarOrganCodi(
				organGestorHelper.getOrganCodiFromContingutId(
						document.getId()));
		try {
			Document documentArxiu = new Document();
			documentArxiu.setIdentificador(
					document.getArxiuUuid());
			documentArxiu.setEstat(
					DocumentEstat.DEFINITIU);
			getArxiuPlugin().getPlugin().documentModificar(
					documentArxiu);
			document.updateArxiu(
					null);
			document.updateArxiuEstat(
					ArxiuEstatEnumDto.DEFINITIU);
		} catch (Exception ex) {
		}
	}

	private DocumentContingut getDocumentContingut(
			String nom,
			String contentType,
			byte[] contingut) {
		DocumentContingut documentContingut = new DocumentContingut();
		documentContingut.setArxiuNom(
				nom);
		documentContingut.setTipusMime(
				contentType);
		documentContingut.setContingut(
				contingut);

		return documentContingut;
	}

	// private Firma getFirma(ArxiuFirmaDto firmaDto) {
	// Firma firma = new Firma();
	// firma.setFitxerNom(firmaDto.getFitxerNom());
	// firma.setContingut(firmaDto.getContingut());
	// firma.setTipusMime(firmaDto.getTipusMime());
	// firma.setTipus(getFirmaTipus(firmaDto.getTipus()));
	// firma.setPerfil(getFirmaPerfil(firmaDto.getPerfil()));
	// firma.setCsvRegulacio(firmaDto.getCsvRegulacio());
	// return firma;
	// }

	private Firma getFirma(
			String nom,
			String contentType,
			byte[] contingut,
			ArxiuFirmaTipusEnumDto tipus,
			ArxiuFirmaPerfilEnumDto perfil,
			String csvRegulacio) {
		Firma firma = new Firma();
		firma.setFitxerNom(
				nom);

		firma.setTipusMime(
				contentType);
		firma.setContingut(
				contingut);
		firma.setTipus(
				ArxiuConversions.getFirmaTipus(
						tipus));
		firma.setPerfil(
				ArxiuConversions.getFirmaPerfil(
						perfil));
		firma.setCsvRegulacio(
				csvRegulacio);
		return firma;
	}

	/**
	 * Denominación normalizada del tipo de firma. Los posibles valores asignables son los siguientes:
	 * TF01 - CSV.
	 * TF02 - XAdES internally detached signature.
	 * TF03 - XAdES enveloped signature.
	 * TF04 - CAdES detached/explicit signature.
	 * TF05 - CAdES attached/implicit signature.
	 * TF06 - PAdES.
	 * El tipo TF04 será establecido por defecto para documentos firmados, exceptuando los documentos en formato PDF o PDF/A, cuyo tipo será TF06.
	 */
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

			if (firmes != null) {
				ArxiuFirmaDto primeraFirma = firmes.get(
						0);
				Firma firma = getFirma(
						primeraFirma.getFitxerNom(),
						primeraFirma.getTipusMime(),
						primeraFirma.getContingut(),
						primeraFirma.getTipus(),
						primeraFirma.getPerfil(),
						primeraFirma.getCsvRegulacio());
				documentArxiu.setFirmes(
						Arrays.asList(
								firma));
			}

		} else if (documentFirmaTipus == DocumentFirmaTipusEnumDto.FIRMA_SEPARADA) {

			boolean inclourerContingutFitxer = true;

			if (firmes != null) {
				ArrayList<Firma> arxiuFirmes = new ArrayList<Firma>();
				for (ArxiuFirmaDto firmaDto : firmes) {
					Firma firma = getFirma(
							firmaDto.getFitxerNom(),
							firmaDto.getTipusMime(),
							firmaDto.getContingut(),
							firmaDto.getTipus(),
							firmaDto.getPerfil(),
							firmaDto.getCsvRegulacio());
					arxiuFirmes.add(firma);
					//Tipus de firma "separada" que ja inclouen dins la firma el contingut del document original
					if (FirmaTipus.CADES_ATT.equals(firma.getTipus()) ||
						FirmaTipus.XADES_DET.equals(firma.getTipus()) ||
						FirmaTipus.XADES_ENV.equals(firma.getTipus())) {
							inclourerContingutFitxer = false;
					}
				}

				documentArxiu.setFirmes(arxiuFirmes);
			}

			if (fitxer!=null && inclourerContingutFitxer) {
				documentArxiu.setContingut(
						getDocumentContingut(
								fitxer.getNom(),
								fitxer.getContentType(),
								fitxer.getContingut()));
			}
		}

		// És una modificació de metadades d'un document definitiu
		if (documentArxiu.getContingut() == null && fitxer != null && isModificacioCustodiatsActiva()) {
			documentArxiu.setContingut(
					getDocumentContingut(
							fitxer.getNom(),
							fitxer.getContentType(),
							fitxer.getContingut()));
		}

	}

	private IntegracioAccioDto getIntegracioAccioArxiu(
			DocumentEntity document,
			String endpoint,
			String accioDescripcio) {

		Map<String, String> accioParams = new HashMap<String, String>();

		accioParams.put(
				"id",
				document.getId().toString());
		accioParams.put(
				"títol",
				document.getNom());
		ContingutEntity contingutPare = getContingutPare(
				document);
		if (contingutPare != null) {
			accioParams.put(
					"contingutPareId",
					contingutPare.getId().toString());
			accioParams.put(
					"contingutPareNom",
					contingutPare.getNom());
			accioParams.put(
					"contingutPareTipus",
					contingutPare.getTipus().toString());

		}
		ExpedientEntity expedient = document.getExpedient();
		accioParams.put(
				"expedientId",
				expedient.getId().toString());
		accioParams.put(
				"expedientNom",
				expedient.getNom());

		String serieDocumental = expedient.getMetaExpedient().getSerieDocumental();
		if (serieDocumental != null) {
			accioParams.put(
					"serieDocumental",
					serieDocumental);
		}

		IntegracioAccioDto integracioAccioDto = new IntegracioAccioDto(accioDescripcio, accioParams, System.currentTimeMillis());
		integracioAccioDto.setEndpoint(endpoint);
		return integracioAccioDto;
	}

	private IntegracioAccioDto getIntegracioAccioArxiu(
			String uuid,
			String endpoint,
			String accioDescripcio) {
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"uuid",
				uuid);
		IntegracioAccioDto integracioAccioDto = new IntegracioAccioDto(accioDescripcio, accioParams,
				System.currentTimeMillis());
		integracioAccioDto.setEndpoint(
				endpoint);
		return integracioAccioDto;
	}

	private String documentNomInArxiu(String nomPerComprovar, ContingutEntity pare) {

		List<ContingutArxiu> continguts = null;
		if (ContingutTipusEnumDto.EXPEDIENT.equals(pare.getTipus()) || (ContingutTipusEnumDto.CARPETA.equals(pare.getTipus()) && isCarpetaLogica())) {
			//Cerca dins tot l'expedient a Arxiu, si el pare es expedient, o estan activades les carpetes lògiques (carpetes nomes a RIPEA)
			continguts = arxiuExpedientConsultarPerUuid(pare.getArxiuUuid()).getContinguts();
		} else {
			continguts = arxiuCarpetaConsultarPerUuid(pare.getArxiuUuid()).getContinguts();
		}

		nomPerComprovar = ArxiuConversioHelper.revisarContingutNom(nomPerComprovar);
		int ocurrences = 0;
		if (continguts != null) {
			List<String> noms = new ArrayList<String>();
			for (ContingutArxiu contingut : continguts) {
				noms.add(contingut.getNom());
			}
			String newName = new String(nomPerComprovar);
			while (noms.indexOf(newName) >= 0) {
				ocurrences++;
				newName = nomPerComprovar + " (" + ocurrences + ")";
			}
			return newName;
		}
		return nomPerComprovar;
	}

	public Document arxiuDocumentConsultar(String arxiuUuid) {
		boolean throwException = false; // throwException = true;
		if (throwException) {
			throw new RuntimeException("Mock exception al consultar document arxiu");
		}
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		IntegracioAccioDto integracioAccio = getIntegracioAccioArxiu(
				arxiuUuid,
				arxiuPluginWrapper.getEndpoint(),
				"Consulta d'un document");

		try {
			Document documentDetalls = arxiuPluginWrapper.getPlugin().documentDetalls(
					arxiuUuid,
					null,
					true);
			arxiuEnviamentOk(integracioAccio);
			return documentDetalls;
		} catch (Exception ex) {
			throw arxiuEnviamentError(integracioAccio, ex);
		}
	}

	public void arxiuDocumentEsborrar(String arxiuUuid) {

		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		IntegracioAccioDto integracioAccio = getIntegracioAccioArxiu(
				arxiuUuid,
				arxiuPluginWrapper.getEndpoint(),
				"Eliminació d'un document");
		try {
			getArxiuPlugin().getPlugin().documentEsborrar(arxiuUuid);
			arxiuEnviamentOk(integracioAccio);
		} catch (Exception ex) {
			throw arxiuEnviamentError(integracioAccio, ex);
		}
	}

	public Document arxiuDocumentConsultar(
			DocumentEntity contingut,
			String arxiuUuid,
			String versio,
			boolean ambContingut) {
		return arxiuDocumentConsultar(
				contingut,
				arxiuUuid,
				versio,
				ambContingut,
				false);
	}

	public Document arxiuDocumentConsultar(
			DocumentEntity document,
			String arxiuUuid,
			String versio,
			boolean ambContingut,
			boolean ambVersioImprimible) {
        return arxiuDocumentConsultar(
        		document!=null?document.getId():null,
        		document!=null?document.getNom():null,
        		document!=null?document.getArxiuUuid():null,
        		document!=null?document.getEntitat().getCodi():null,
                arxiuUuid, versio, ambContingut, ambVersioImprimible);
	}
	
	public Document arxiuDocumentConsultar(
			Long documentId, String documentNom, String documentArxiuUuid, String documentEntitatCodi,
			String arxiuUuid,
			String versio,
			boolean ambContingut,
			boolean ambVersioImprimible) {

		boolean throwException = false; // throwException = true;
		if (throwException) {
			throw new RuntimeException("Mock exception al consultar document arxiu");
		}

		String accioDescripcio = "Consulta d'un document";
        long t0 = System.currentTimeMillis();
        Map<String, String> accioParams = new HashMap<String, String>();
        IArxiuPluginWrapper arxiuPluginWrapper = null;
        String arxiuUuidConsulta = null;

        if (documentId != null) {
            accioParams.put("contingutId", documentId.toString());
            accioParams.put("contingutNom", documentNom);
            arxiuUuidConsulta  = documentArxiuUuid;
            String entitatCodi = configHelper.getEntitatActualCodi();
            if (entitatCodi!=null) {
                arxiuPluginWrapper = getArxiuPlugin(entitatCodi);
            } else {
                //Cas de enviament de email en segon pla, no disposam de entitat en sessió
                arxiuPluginWrapper = getArxiuPlugin(documentEntitatCodi);
            }
        } else {
            arxiuPluginWrapper = getArxiuPlugin();
            arxiuUuidConsulta  = arxiuUuid;
        }

        if (arxiuUuid != null) { accioParams.put("arxiuUuid", arxiuUuid); }
        accioParams.put("versio", versio);
        accioParams.put("ambContingut", new Boolean(ambContingut).toString());
        accioParams.put("ambVersioImprimible", new Boolean(ambVersioImprimible).toString());
        accioParams.put("arxiuUuidConsulta", arxiuUuidConsulta);

        try {
            Document documentDetalls = arxiuPluginWrapper.getPlugin().documentDetalls(arxiuUuidConsulta, versio, ambContingut);
            if (ambContingut && documentDetalls.getContingut() == null) {
				logger.error(
						"El plugin no ha retornat el contingut del document ({})",
						accioParams.toString());
			}
			boolean generarVersioImprimible = false;
			if (ambVersioImprimible && ambContingut && documentDetalls.getFirmes() != null
					&& !documentDetalls.getFirmes().isEmpty()) {
				for (Firma firma : documentDetalls.getFirmes()) {
					if (documentDetalls.getContingut().getTipusMime().equals(
							"application/pdf")
							&& (firma.getTipus() == FirmaTipus.PADES || firma.getTipus() == FirmaTipus.CADES_ATT
									|| firma.getTipus() == FirmaTipus.CADES_DET)) {
						generarVersioImprimible = true;
					}
				}
			}
			if (generarVersioImprimible) {
				documentDetalls.setContingut(
						arxiuPluginWrapper.getPlugin().documentImprimible(
								documentDetalls.getIdentificador()));
			}
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return documentDetalls;
		} catch (Exception ex) {
			String msg = "";
			if (ex.getCause() != null && !ex.getCause().equals(
					ex)) {
				msg = ex.getMessage() + ": " + ex.getCause().getMessage();
			} else {
				msg = ex.getMessage();
			}
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + msg;
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public byte[] arxiuFirmaSeparadaConsultar(
			DocumentEntity document) {
		organGestorHelper.actualitzarOrganCodi(
				organGestorHelper.getOrganCodiFromContingutId(
						document.getId()));
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		IntegracioAccioDto integracioAccio = getIntegracioAccioArxiu(
				document,
				arxiuPluginWrapper.getEndpoint(),
				"Consulta d'una firma separada del document esboranny");
		try {
			Document documentDetalls = arxiuPluginWrapper.getPlugin().documentDetalls(
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

	public void arxiuDocumentEsborrar(
			DocumentEntity document) {

		organGestorHelper.actualitzarOrganCodi(
				organGestorHelper.getOrganCodiFromContingutId(
						document.getId()));
		String accioDescripcio = "Eliminació d'un document";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"id",
				document.getId().toString());
		accioParams.put(
				"títol",
				document.getNom());
		long t0 = System.currentTimeMillis();
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		try {
			arxiuPluginWrapper.getPlugin().documentEsborrar(
					document.getArxiuUuid());
			document.updateArxiuEsborrat();
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public boolean arxiuDocumentExtensioPermesa(
			String extensio) {
		return getArxiuFormatExtensio(
				extensio) != null;
	}

	public List<ContingutArxiu> arxiuDocumentObtenirVersions(
			DocumentEntity document) {
		return arxiuDocumentObtenirVersions(
                document.getId(),
                document.getNom(),
                document.getArxiuUuid(),
                document.getExpedient().getArxiuUuid()
        );
	}
	public List<ContingutArxiu> arxiuDocumentObtenirVersions(
			Long documentId,
            String documentNom,
            String documentArxiuUuid,
            String expedientArxiuUuid
    ) {
		organGestorHelper.actualitzarOrganCodi(
				organGestorHelper.getOrganCodiFromContingutId(
                        documentId));
		String accioDescripcio = "Obtenir versions del document";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"id",
                documentId.toString());
		accioParams.put(
				"títol",
                documentNom);
		long t0 = System.currentTimeMillis();
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		try {
			Expedient arxiuExpedient = arxiuPluginWrapper.getPlugin().expedientDetalls(
                    expedientArxiuUuid,
					null);
			boolean isOpen = false;
			ExpedientMetadades metadades = arxiuExpedient.getMetadades();
			if (metadades != null && metadades.getEstat() != null && metadades.getEstat() == ExpedientEstat.OBERT) {
				isOpen = true;
			}
			List<ContingutArxiu> versions = new ArrayList<>();
			if (isOpen) { // currently it is not possible to get versions of documents from arxiu caib if
							// the expedient is closed
				versions = arxiuPluginWrapper.getPlugin().documentVersions(
                        documentArxiuUuid);
			}
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return versions;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public List<TipusDocumentalDto> documentTipusAddicionals() {

		String accioDescripcio = "Consulta de tipus de documents addicionals";
		long t0 = System.currentTimeMillis();
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		try {
			List<DocumentTipusAddicional> documentTipusAddicionals = arxiuPluginWrapper.getPlugin()
					.documentTipusAddicionals();
			List<TipusDocumentalDto> tipusDocumentalsDto = new ArrayList<>();
			if (documentTipusAddicionals != null && !documentTipusAddicionals.isEmpty()) {
				for (DocumentTipusAddicional documentTipusAddicional : documentTipusAddicionals) {
					TipusDocumentalDto tipusDocumentalDto = new TipusDocumentalDto();
					tipusDocumentalDto.setCodi(
							documentTipusAddicional.getCodi());
					tipusDocumentalDto.setNom(
							documentTipusAddicional.getDescripcio());
					tipusDocumentalsDto.add(
							tipusDocumentalDto);
				}
			}
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					null,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return tipusDocumentalsDto;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					null,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public void arxiuDocumentCopiar(
			DocumentEntity document,
			String arxiuUuidDesti) {

		organGestorHelper.actualitzarOrganCodi(
				organGestorHelper.getOrganCodiFromContingutId(
						document.getId()));
		String accioDescripcio = "Copiar document";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"id",
				document.getId().toString());
		accioParams.put(
				"títol",
				document.getNom());
		accioParams.put(
				"arxiuUuidDesti",
				arxiuUuidDesti);
		long t0 = System.currentTimeMillis();
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		try {
			arxiuPluginWrapper.getPlugin().documentCopiar(
					document.getArxiuUuid(),
					arxiuUuidDesti);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public ContingutArxiu arxiuDocumentLink(
			DocumentEntity document,
			String arxiuUuidDesti) {

		organGestorHelper.actualitzarOrganCodi(
				organGestorHelper.getOrganCodiFromContingutId(
						document.getId()));
		String accioDescripcio = "Enllaçar document";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"id",
				document.getId().toString());
		accioParams.put(
				"títol",
				document.getNom());
		accioParams.put(
				"arxiuUuidDesti",
				arxiuUuidDesti);
		long t0 = System.currentTimeMillis();
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		try {
			// Empram el mètode carpetaCopiar per no disposar d'un mètode específic per
			// vincular.
			ContingutArxiu nouContingut = arxiuPluginWrapper.getPlugin().carpetaCopiar(
					document.getArxiuUuid(),
					arxiuUuidDesti);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return nouContingut;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public String arxiuDocumentMoure(
			String uuid,
			String uuidDesti,
			String uuidExpedientDesti) {

		String accioDescripcio = "Moure document";
		Map<String, String> accioParams = new HashMap<String, String>();

		accioParams.put(
				"arxiuUuidOrigen",
				uuid);
		accioParams.put(
				"uuidDesti",
				uuidDesti);
		accioParams.put(
				"uuidExpedientDesti",
				uuidExpedientDesti);
		long t0 = System.currentTimeMillis();
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		try {
			boolean throwException = false;// throwException = true
			if (throwException) {
				throw new RuntimeException("Mock excepcion moving document ");
			}
			ContingutArxiu nouDocumentArxiu = null;
			try {
				nouDocumentArxiu = arxiuPluginWrapper.getPlugin().documentMoure(
						uuid,
						uuidDesti,
						uuidExpedientDesti);
			} catch (Exception e) {

				if (e.getMessage().contains(
						"Duplicate child name not allowed")
						|| e.getMessage().contains(
								"Petición mal formada")) {
					logger.info(
							"Document already moved in arxiu:" + e.getMessage());

					Document document = arxiuPluginWrapper.getPlugin().documentDetalls(
							uuid,
							null,
							false);
					logger.info(
							"Document to move name=" + document.getNom() + ", uuid=" + document.getIdentificador());
					Carpeta carpeta = arxiuPluginWrapper.getPlugin().carpetaDetalls(
							uuidDesti);

					String uuidDocumentMovido = null;
					for (ContingutArxiu contingutArxiu : carpeta.getContinguts()) {
						logger.info(
								"Searching document moved: name=" + contingutArxiu.getNom() + ", uuid="
										+ contingutArxiu.getIdentificador());
						if (document.getNom().equals(
								contingutArxiu.getNom())) {
							logger.info(
									"Document moved found: name=" + contingutArxiu.getNom() + ", uuid="
											+ contingutArxiu.getIdentificador());
							uuidDocumentMovido = contingutArxiu.getIdentificador();
							break;
						}
					}
					if (uuidDocumentMovido != null) {
						return uuidDocumentMovido;
					} else {
						throw e;
					}
				} else {
					throw e;
				}
			}

			boolean throwException1 = false;// throwException1 = true
			if (throwException1) {
				throw new RuntimeException("Mock excepcion after moving document ");
			}
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			if (nouDocumentArxiu != null) {
				return nouDocumentArxiu.getIdentificador();
			} else {
				return null;
			}
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public String arxiuDocumentExportar(
			DocumentEntity document) {
		organGestorHelper.actualitzarOrganCodi(
				organGestorHelper.getOrganCodiFromContingutId(
						document.getId()));

		String accioDescripcio = "Exportar document en format ENI";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"id",
				document.getId().toString());
		accioParams.put(
				"títol",
				document.getNom());
		long t0 = System.currentTimeMillis();
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		try {
			String exportacio = arxiuPluginWrapper.getPlugin().documentExportarEni(
					document.getArxiuUuid());
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return exportacio;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public FitxerDto arxiuDocumentVersioImprimible(
			DocumentEntity document) {

		String accioDescripcio = "Obtenir versió imprimible del document";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", document.getId().toString());
		accioParams.put("títol", document.getNom());
		long t0 = System.currentTimeMillis();
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		
		try {
			DocumentContingut documentContingut = arxiuPluginWrapper.getPlugin().documentImprimible(document.getArxiuUuid());
			FitxerDto fitxer = new FitxerDto();
			String titol = document.getFitxerNom().replace(".pdf", "_imprimible.pdf");
			fitxer.setNom(titol);
			fitxer.setContentType(documentContingut.getTipusMime());
			fitxer.setTamany(documentContingut.getTamany());
			fitxer.setContingut(documentContingut.getContingut());
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return fitxer;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public void arxiuCarpetaActualitzar(
			CarpetaEntity carpeta,
			ContingutEntity contingutPare) {

		organGestorHelper.actualitzarOrganCodi(
				organGestorHelper.getOrganCodiFromContingutId(
						carpeta.getId()));

		boolean throwExceptionCarpetaArxiu = false;
		if (throwExceptionCarpetaArxiu) { // throwExceptionCarpetaArxiu = true;
			throw new RuntimeException("Mock excepcion al actualitzar carpeta al arxiu");
		}

		String accioDescripcio = "Actualització de les dades d'una carpeta";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"id",
				carpeta.getId().toString());
		accioParams.put(
				"nom",
				carpeta.getNom());
		accioParams.put(
				"contingutPareId",
				contingutPare.getId().toString());
		accioParams.put(
				"contingutPareNom",
				contingutPare.getNom());
		long t0 = System.currentTimeMillis();
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();

		try {
			if (carpeta.getArxiuUuid() == null) {
				ContingutArxiu carpetaCreada = arxiuPluginWrapper.getPlugin().carpetaCrear(
						toArxiuCarpeta(
								null,
								carpeta.getNom()),
						contingutPare.getArxiuUuid());
				carpeta.updateArxiu(
						carpetaCreada.getIdentificador());
			} else {
				arxiuPluginWrapper.getPlugin().carpetaModificar(
						toArxiuCarpeta(
								carpeta.getArxiuUuid(),
								carpeta.getNom()));
				carpeta.updateArxiu(
						null);
			}
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);

		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public Carpeta arxiuCarpetaConsultar(
			CarpetaEntity carpeta) {

		organGestorHelper.actualitzarOrganCodi(
				organGestorHelper.getOrganCodiFromContingutId(
						carpeta.getId()));
		String accioDescripcio = "Consulta d'una carpeta";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"id",
				carpeta.getId().toString());
		accioParams.put(
				"nom",
				carpeta.getNom());
		long t0 = System.currentTimeMillis();
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		try {
			Carpeta carpetaDetalls = arxiuPluginWrapper.getPlugin().carpetaDetalls(
					carpeta.getArxiuUuid());
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return carpetaDetalls;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public Carpeta arxiuCarpetaConsultarPerUuid(
			String uuid) {

		String accioDescripcio = "Consulta d'una carpeta per uuid";
		Map<String, String> accioParams = new HashMap<String, String>();
		long t0 = System.currentTimeMillis();
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		try {
			Carpeta arxiuCarpeta = arxiuPluginWrapper.getPlugin().carpetaDetalls(
					uuid);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return arxiuCarpeta;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public void arxiuCarpetaEsborrar(
			CarpetaEntity carpeta) {

		organGestorHelper.actualitzarOrganCodi(
				organGestorHelper.getOrganCodiFromContingutId(
						carpeta.getId()));
		String accioDescripcio = "Eliminació d'una carpeta";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"id",
				carpeta.getId().toString());
		accioParams.put(
				"nom",
				carpeta.getNom());
		long t0 = System.currentTimeMillis();
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		try {
			arxiuPluginWrapper.getPlugin().carpetaEsborrar(
					carpeta.getArxiuUuid());
			carpeta.updateArxiuEsborrat();
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public void arxiuCarpetaCopiar(
			CarpetaEntity carpeta,
			String arxiuUuidDesti) {

		organGestorHelper.actualitzarOrganCodi(
				organGestorHelper.getOrganCodiFromContingutId(
						carpeta.getId()));
		organGestorHelper.actualitzarOrganCodi(
				organGestorHelper.getOrganCodiFromContingutId(
						carpeta.getId()));
		String accioDescripcio = "Copiar carpeta";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"id",
				carpeta.getId().toString());
		accioParams.put(
				"nom",
				carpeta.getNom());
		accioParams.put(
				"arxiuUuidDesti",
				arxiuUuidDesti);
		long t0 = System.currentTimeMillis();
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		try {
			arxiuPluginWrapper.getPlugin().carpetaCopiar(
					carpeta.getArxiuUuid(),
					arxiuUuidDesti);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public void arxiuCarpetaMoure(
			CarpetaEntity carpeta,
			String arxiuUuidDesti) {

		String accioDescripcio = "Moure carpeta";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"id",
				carpeta.getId().toString());
		accioParams.put(
				"nom",
				carpeta.getNom());
		accioParams.put(
				"arxiuUuidDesti",
				arxiuUuidDesti);
		long t0 = System.currentTimeMillis();
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		try {
			arxiuPluginWrapper.getPlugin().carpetaMoure(
					carpeta.getArxiuUuid(),
					arxiuUuidDesti);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public List<ContingutArxiu> importarDocumentsArxiu(
			ImportacioDto params) {

		String accioDescripcio = "Importar documents";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"numeroRegistre",
				params.getNumeroRegistre());
		long t0 = System.currentTimeMillis();
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		try {
			String tipusRegistreLabel = null;
			String dataPresentacioStr = null;
			String numeroRegistreStr = null;
			String codiEniStr = null;
			if (params.getTipusImportacio().equals(
					TipusImportEnumDto.NUMERO_REGISTRE)) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				dataPresentacioStr = "'" + dateFormat.format(
						params.getDataPresentacioFormatted()) + "'";
				tipusRegistreLabel = "'" + params.getTipusRegistre().getLabel() + "'";
				numeroRegistreStr = "'" + params.getNumeroRegistre() + "'";
			} else {
				codiEniStr = "'" + params.getCodiEni() + "'";
			}
			// Aprofitam el mètode documentVersions per fer la importació
			String paramsJson = "{" + "'tipusImportacio' : '" + params.getTipusImportacio().name() + "',"
					+ "'numeroRegistre' : " + numeroRegistreStr + "," + "'tipusRegistre' : " + tipusRegistreLabel + ","
					+ "'dataPresentacio' : " + dataPresentacioStr + "," + "'codiEni' : " + codiEniStr + "" + "}";
			List<ContingutArxiu> contingutArxiu = arxiuPluginWrapper.getPlugin().documentVersions(
					paramsJson);
			return contingutArxiu;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}

	public Document importarDocument(
			String arxiuUuidPare,
			String arxiuUuid,
			boolean moureDocument) {

		String accioDescripcio = "Importar documents";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"arxiuUuid",
				arxiuUuid);
		long t0 = System.currentTimeMillis();
		IArxiuPluginWrapper arxiuPluginWrapper = getArxiuPlugin();
		try {
			Document document = arxiuPluginWrapper.getPlugin().documentDetalls(
					arxiuUuid,
					null,
					false);
			document.setIdentificador(
					arxiuUuid);
			if (moureDocument) {
				// Si és de registre moure el document
				arxiuPluginWrapper.getPlugin().documentCopiar(
						arxiuUuidPare,
						arxiuUuid);
			} else {
				// Si és una importació amb ENI fer un linkdocument
				// Empram el mètode carpetaCopiar per no disposar d'un mètode específic per
				// vincular.
				ContingutArxiu nouContingut = arxiuPluginWrapper.getPlugin().carpetaCopiar(
						arxiuUuid,
						arxiuUuidPare);
				document = arxiuPluginWrapper.getPlugin().documentDetalls(
						nouContingut.getIdentificador(),
						null,
						false);
				return document;
			}
			return document;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					arxiuPluginWrapper.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}
	
	public String portafirmesUpload(
			DocumentEntity document,
			String motiu,
			PortafirmesPrioritatEnum prioritat,
			Date dataCaducitat,
			String documentTipus,
			String[] responsables,
			MetaDocumentFirmaSequenciaTipusEnumDto fluxTipus,
			String fluxId,
			List<DocumentEntity> annexos,
			String transaccioId) {

		organGestorHelper.actualitzarOrganCodi(
				organGestorHelper.getOrganCodiFromContingutId(
						document.getId()));
		long t0 = System.currentTimeMillis();
		PortafirmesPlugin portafirmesPlugin = getPortafirmesPlugin();
		Map<String, String> accioParams = getAccioParamsPerPortaFirmesUpload(
				document,
				motiu,
				prioritat,
				dataCaducitat,
				documentTipus,
				responsables,
				fluxTipus,
				fluxId,
				annexos);
		List<PortafirmesDocument> portafirmesAnnexos = null;
		PortafirmesDocument portafirmesDocument = new PortafirmesDocument();
		portafirmesDocument.setExpedientUuid(
				document.getExpedient().getArxiuUuid());
		portafirmesDocument.setTitol(
				document.getNom());
		portafirmesDocument.setDescripcio(
				document.getDescripcio());
		portafirmesDocument.setFirmat(
				false);

		FitxerDto fitxerOriginal = documentHelper.getFitxerAssociat(
				document,
				null);
		FitxerDto fitxerConvertit = this.conversioConvertirPdf(
				fitxerOriginal,
				null);
		portafirmesDocument.setArxiuNom(
				fitxerConvertit.getNom());
		portafirmesDocument.setArxiuContingut(
				fitxerConvertit.getContingut());
		portafirmesDocument.setArxiuUuid(
				document.getArxiuUuid());
		if (annexos != null && !annexos.isEmpty()) {
			portafirmesAnnexos = new ArrayList<PortafirmesDocument>();
			for (DocumentEntity annex : annexos) {
				PortafirmesDocument portafirmesAnnex = new PortafirmesDocument();
				portafirmesAnnex.setTitol(
						annex.getNom());
				portafirmesAnnex.setFirmat(
						false);
				FitxerDto annexFitxerOriginal = documentHelper.getFitxerAssociat(
						annex,
						null);
				FitxerDto annexFitxerConvertit = this.conversioConvertirPdf(
						annexFitxerOriginal,
						null);
				portafirmesAnnex.setArxiuNom(
						annexFitxerConvertit.getNom());
				portafirmesAnnex.setArxiuContingut(
						annexFitxerConvertit.getContingut());
				portafirmesAnnexos.add(
						portafirmesAnnex);
			}
		}
		List<PortafirmesFluxBloc> flux = new ArrayList<PortafirmesFluxBloc>();
		if (fluxId == null) {
			if (MetaDocumentFirmaSequenciaTipusEnumDto.SERIE.equals(
					fluxTipus)) {
				for (String responsable : responsables) {
					PortafirmesFluxBloc bloc = new PortafirmesFluxBloc();
					bloc.setMinSignataris(
							1);
					bloc.setDestinataris(
							new String[] { responsable });
					bloc.setObligatorietats(
							new boolean[] { true });
					flux.add(
							bloc);
				}
			} else if (MetaDocumentFirmaSequenciaTipusEnumDto.PARALEL.equals(
					fluxTipus)) {
				PortafirmesFluxBloc bloc = new PortafirmesFluxBloc();
				bloc.setMinSignataris(
						responsables.length);
				bloc.setDestinataris(
						responsables);
				boolean[] obligatorietats = new boolean[responsables.length];
				Arrays.fill(
						obligatorietats,
						true);
				bloc.setObligatorietats(
						obligatorietats);
				flux.add(
						bloc);
			}
		}
		try {
			String remitent = "Aplicació RIPEA";
			UsuariDto usuari = aplicacioService.getUsuariActual();
			if (usuari != null) {
				remitent += " - Gestor: " + usuari.getNom();
			}

			if (cacheHelper.mostrarLogsIntegracio()) {
				String entitatCodi = configHelper.getEntitatActualCodi();
				String organCodi = configHelper.getOrganActualCodi();
				logger.info(
						"[PFI] Enviament de document a firmar: " + portafirmesDocument.getTitol());
				logger.info(
						"[PFI] Entitat codi: " + entitatCodi);
				logger.info(
						"[PFI] Organ codi: " + organCodi);
				logger.info(
						"[PFI] DocumentTipus: " + documentTipus);
				logger.info(
						"[PFI] Motiu: " + motiu);
				if (portafirmesPlugin != null) {
					logger.info(
							"[PFI] Class: " + portafirmesPlugin.getClass().getCanonicalName());
					if (portafirmesPlugin instanceof RipeaAbstractPluginProperties) {
						logger.info(
								"[PFI] Url: " + ((RipeaAbstractPluginProperties) portafirmesPlugin).getProperty(
										"plugin.portafirmes.firmasimpleasync.url"));
						logger.info(
								"[PFI] User: " + ((RipeaAbstractPluginProperties) portafirmesPlugin).getProperty(
										"plugin.portafirmes.firmasimpleasync.username"));
					}
				} else {
					logger.info(
							"[PFI] : Plugin no instanciat!!!");
				}
			}

			String portafirmesEnviamentId = portafirmesPlugin.upload(
					portafirmesDocument,
					documentTipus,
					motiu,
					remitent,
					prioritat,
					null,
					flux,
					fluxId,
					portafirmesAnnexos,
					false,
					transaccioId);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_PFIRMA,
					"Enviament de document a firmar",
					portafirmesPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return portafirmesEnviamentId;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de portafirmes";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PFIRMA,
					"Enviament de document a firmar",
					portafirmesPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, errorDescripcio, ex);
		}
	}

	public PortafirmesDocument portafirmesDownload(
			DocumentPortafirmesEntity documentPortafirmes) {
		organGestorHelper.actualitzarOrganCodi(
				organGestorHelper.getOrganCodiFromContingutId(
						documentPortafirmes.getDocument().getId()));

		long t0 = System.currentTimeMillis();
		String accioDescripcio = "Descarregar document firmat";
		Map<String, String> accioParams = new HashMap<String, String>();
		DocumentEntity document = documentPortafirmes.getDocument();
		accioParams.put("documentId", document.getId().toString());
		accioParams.put("documentPortafirmesId", documentPortafirmes.getId().toString());
		accioParams.put("portafirmesId", Long.valueOf(documentPortafirmes.getPortafirmesId()).toString());
		PortafirmesPlugin portafirmesPlugin = getPortafirmesPlugin();
		PortafirmesDocument portafirmesDocument = null;
		
		try {
			portafirmesDocument = portafirmesPlugin.download(documentPortafirmes.getPortafirmesId());
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_PFIRMA,
					accioDescripcio,
					portafirmesPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return portafirmesDocument;
		} catch (Exception ex) {
			String errorDescripcio = "Error al descarregar el document firmat";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PFIRMA,
					accioDescripcio,
					portafirmesPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, errorDescripcio, ex);
		}
	}

	public void portafirmesDelete(DocumentPortafirmesEntity documentPortafirmes) {

		organGestorHelper.actualitzarOrganCodi(
				organGestorHelper.getOrganCodiFromContingutId(
						documentPortafirmes.getDocument().getId()));

		long t0 = System.currentTimeMillis();
		String accioDescripcio = "Esborrar document enviat a firmar";
		Map<String, String> accioParams = new HashMap<String, String>();
		DocumentEntity document = documentPortafirmes.getDocument();
		accioParams.put("documentId", document.getId().toString());
		accioParams.put("documentPortafirmesId", documentPortafirmes.getId().toString());
		accioParams.put("portafirmesId", Long.valueOf(documentPortafirmes.getPortafirmesId()).toString());
		PortafirmesPlugin portafirmesPlugin = getPortafirmesPlugin();
		
		try {
			portafirmesPlugin.delete(documentPortafirmes.getPortafirmesId());
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_PFIRMA,
					accioDescripcio,
					portafirmesPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de portafirmes";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PFIRMA,
					accioDescripcio,
					portafirmesPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, errorDescripcio, ex);
		}
	}

	public List<PortafirmesDocumentTipusDto> portafirmesFindDocumentTipus() {

		String accioDescripcio = "Consulta de tipus de document";
		long t0 = System.currentTimeMillis();
		PortafirmesPlugin portafirmesPlugin = getPortafirmesPlugin();
		try {
			List<PortafirmesDocumentTipus> tipus = portafirmesPlugin.findDocumentTipus();
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_PFIRMA,
					accioDescripcio,
					portafirmesPlugin.getEndpointURL(),
					null,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			if (tipus != null) {
				List<PortafirmesDocumentTipusDto> resposta = new ArrayList<PortafirmesDocumentTipusDto>();
				for (PortafirmesDocumentTipus t : tipus) {
					PortafirmesDocumentTipusDto dto = new PortafirmesDocumentTipusDto();
					dto.setId(
							t.getId());
					dto.setCodi(
							t.getCodi());
					dto.setNom(
							t.getNom());
					resposta.add(
							dto);
				}
				return resposta;
			} else {
				return null;
			}
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de portafirmes";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PFIRMA,
					accioDescripcio,
					portafirmesPlugin.getEndpointURL(),
					null,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, errorDescripcio, ex);
		}
	}

	public boolean portafirmesEnviarDocumentEstampat() {
		return !getPortafirmesPlugin().isCustodiaAutomatica();
	}

	public PortafirmesIniciFluxRespostaDto portafirmesIniciarFluxDeFirma(
			String idioma,
			boolean isPlantilla,
			String nom,
			String descripcio,
			boolean descripcioVisible,
			String urlReturn) throws SistemaExternException {
		String accioDescripcio = "Iniciant flux de firma";
		long t0 = System.currentTimeMillis();
		PortafirmesPlugin portafirmesPlugin = getPortafirmesPlugin();
		PortafirmesIniciFluxRespostaDto transaccioResponseDto = new PortafirmesIniciFluxRespostaDto();
		try {
			PortafirmesIniciFluxResposta transaccioResponse = portafirmesPlugin.iniciarFluxDeFirma(
					idioma,
					isPlantilla,
					nom,
					descripcio,
					descripcioVisible,
					urlReturn);
			if (transaccioResponse != null) {
				transaccioResponseDto.setIdTransaccio(
						transaccioResponse.getIdTransaccio());
				transaccioResponseDto.setUrlRedireccio(
						transaccioResponse.getUrlRedireccio());
			}
		} catch (Exception ex) {
			String errorDescripcio = ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PFIRMA,
					accioDescripcio,
					portafirmesPlugin.getEndpointURL(),
					null,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, errorDescripcio, ex);
		}
		return transaccioResponseDto;
	}

	public PortafirmesFluxRespostaDto portafirmesRecuperarFluxDeFirma(
			String idTransaccio) {

		String accioDescripcio = "Recuperant flux de firma";
		long t0 = System.currentTimeMillis();
		PortafirmesFluxRespostaDto respostaDto;
		PortafirmesPlugin portafirmesPlugin = getPortafirmesPlugin();
		
		try {
			respostaDto = new PortafirmesFluxRespostaDto();
			PortafirmesFluxResposta resposta = portafirmesPlugin.recuperarFluxDeFirmaByIdTransaccio(idTransaccio);

			if (resposta != null) {
				respostaDto.setError(
						resposta.isError());
				respostaDto.setFluxId(
						resposta.getFluxId());
				respostaDto.setNom(
						resposta.getNom());
				respostaDto.setDescripcio(
						resposta.getDescripcio());
				respostaDto.setEstat(
						resposta.getEstat() != null ? PortafirmesFluxEstatDto.valueOf(
								resposta.getEstat().toString()) : null);
			}
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de portafirmes";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PFIRMA,
					accioDescripcio,
					portafirmesPlugin.getEndpointURL(),
					null,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, errorDescripcio, ex);
		}
		return respostaDto;
	}

	public List<PortafirmesCarrecDto> portafirmesRecuperarCarrecs() {

		String accioDescripcio = "Recuperant els càrrecs disponibles";
		long t0 = System.currentTimeMillis();
		List<PortafirmesCarrecDto> carrecsDto = new ArrayList<PortafirmesCarrecDto>();
		PortafirmesPlugin portafirmesPlugin = getPortafirmesPlugin();
		try {
			List<PortafirmesCarrec> portafirmesCarrecs = portafirmesPlugin.recuperarCarrecs();
			for (PortafirmesCarrec portafirmesCarrec : portafirmesCarrecs) {
				PortafirmesCarrecDto carrecDto = new PortafirmesCarrecDto();
				carrecDto.setCarrecId(
						portafirmesCarrec.getCarrecId());
				carrecDto.setCarrecName(
						portafirmesCarrec.getCarrecName());
				carrecDto.setEntitatId(
						portafirmesCarrec.getEntitatId());
				carrecDto.setUsuariPersonaId(
						portafirmesCarrec.getUsuariPersonaId());
				carrecDto.setUsuariPersonaNif(
						portafirmesCarrec.getUsuariPersonaNif());
				carrecDto.setUsuariPersonaEmail(
						portafirmesCarrec.getUsuariPersonaEmail());
				carrecDto.setUsuariPersonaNom(
						portafirmesCarrec.getUsuariPersonaNom());
				carrecsDto.add(
						carrecDto);
			}
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de portafirmes";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PFIRMA,
					accioDescripcio,
					portafirmesPlugin.getEndpointURL(),
					null,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, errorDescripcio, ex);
		}
		return carrecsDto;
	}

	public PortafirmesCarrecDto portafirmesRecuperarCarrec(
			String carrecId) {

		String accioDescripcio = "Recuperan un càrrec a partir del seu ID";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("carrecId", carrecId);
		long t0 = System.currentTimeMillis();
		PortafirmesCarrecDto carrecDto = new PortafirmesCarrecDto();
		PortafirmesPlugin portafirmesPlugin = getPortafirmesPlugin();
		
		try {
			PortafirmesCarrec portafirmesCarrec = portafirmesPlugin.recuperarCarrec(carrecId);
			carrecDto.setCarrecId(
					portafirmesCarrec.getCarrecId());
			carrecDto.setCarrecName(
					portafirmesCarrec.getCarrecName());
			carrecDto.setEntitatId(
					portafirmesCarrec.getEntitatId());
			carrecDto.setUsuariPersonaId(
					portafirmesCarrec.getUsuariPersonaId());
			carrecDto.setUsuariPersonaNif(
					portafirmesCarrec.getUsuariPersonaNif());
			carrecDto.setUsuariPersonaEmail(
					portafirmesCarrec.getUsuariPersonaEmail());
			carrecDto.setUsuariPersonaNom(
					portafirmesCarrec.getUsuariPersonaNom());
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de portafirmes";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PFIRMA,
					accioDescripcio,
					portafirmesPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, errorDescripcio, ex);
		}
		return carrecDto;
	}

	public void portafirmesTancarFluxDeFirma(
			String idTransaccio) {

		String accioDescripcio = "Tancant flux de firma";
		long t0 = System.currentTimeMillis();
		PortafirmesPlugin portafirmesPlugin = getPortafirmesPlugin();
		
		try {
			portafirmesPlugin.tancarTransaccioFlux(idTransaccio);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de portafirmes";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PFIRMA,
					accioDescripcio,
					portafirmesPlugin.getEndpointURL(),
					null,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, errorDescripcio, ex);
		}
	}

	public List<DigitalitzacioPerfilDto> digitalitzacioPerfilsDisponibles(String idioma) {

		long t0 = System.currentTimeMillis();
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("idioma",idioma);
		List<DigitalitzacioPerfilDto> perfilsDto = new ArrayList<DigitalitzacioPerfilDto>();
		DigitalitzacioPlugin digitalitzacioPlugin = getDigitalitzacioPlugin();
		
		try {
			List<DigitalitzacioPerfil> perfils = digitalitzacioPlugin.recuperarPerfilsDisponibles(
					idioma);
			if (perfils != null) {
				for (DigitalitzacioPerfil perfil : perfils) {
					DigitalitzacioPerfilDto perfilDto = new DigitalitzacioPerfilDto();
					perfilDto.setCodi(
							perfil.getCodi());
					perfilDto.setNom(
							perfil.getNom());
					perfilDto.setDescripcio(
							perfil.getDescripcio());
					perfilDto.setTipus(
							perfil.getTipus());
					perfilsDto.add(
							perfilDto);
				}
			}

			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_DIGITALITZACIO,
					"Recuperant perfils disponibles",
					digitalitzacioPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);

		} catch (Exception ex) {
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_DIGITALITZACIO,
					"Recuperant perfils disponibles",
					digitalitzacioPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					"Error al accedir al plugin de digitalitzacio",
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_DIGITALITZACIO,
					"Error al accedir al plugin de digitalitzacio", ex);
		}
		return perfilsDto;
	}

	public DigitalitzacioTransaccioRespostaDto digitalitzacioIniciarProces(
			String idioma,
			String codiPerfil,
			UsuariDto funcionari,
			String urlReturn) {

		long t0 = System.currentTimeMillis();
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"idioma",
				idioma);
		accioParams.put(
				"codiPerfil",
				codiPerfil);
		accioParams.put(
				"funcionari",
				funcionari.getCodi());
		accioParams.put(
				"urlReturn",
				urlReturn);

		DigitalitzacioTransaccioRespostaDto respostaDto = new DigitalitzacioTransaccioRespostaDto();
		DigitalitzacioPlugin digitalitzacioPlugin = getDigitalitzacioPlugin();
		
		try {
			DigitalitzacioTransaccioResposta resposta = digitalitzacioPlugin.iniciarProces(
					codiPerfil,
					idioma,
					funcionari,
					urlReturn);
			if (resposta != null) {
				respostaDto.setIdTransaccio(
						resposta.getIdTransaccio());
				respostaDto.setUrlRedireccio(
						resposta.getUrlRedireccio());
				respostaDto.setReturnScannedFile(
						resposta.isReturnScannedFile());
				respostaDto.setReturnSignedFile(
						resposta.isReturnSignedFile());
			}

			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_DIGITALITZACIO,
					"Iniciant procés digitalització",
					digitalitzacioPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);

		} catch (Exception ex) {
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_DIGITALITZACIO,
					"Recuperant perfils disponibles",
					digitalitzacioPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					"Error al accedir al plugin de digitalitzacio",
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_DIGITALITZACIO,
					"Error al accedir al plugin de digitalitzacio", ex);
		}
		return respostaDto;
	}

	public DigitalitzacioResultatDto digitalitzacioRecuperarResultat(
			String idTransaccio,
			boolean returnScannedFile,
			boolean returnSignedFile) {

		long t0 = System.currentTimeMillis();
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"idTransaccio",
				idTransaccio);
		accioParams.put(
				"returnScannedFile",
				String.valueOf(
						returnScannedFile));
		accioParams.put(
				"returnSignedFile",
				String.valueOf(
						returnSignedFile));

		DigitalitzacioResultatDto resultatDto = new DigitalitzacioResultatDto();
		DigitalitzacioPlugin digitalitzacioPlugin = getDigitalitzacioPlugin();
		
		try {
			DigitalitzacioResultat resultat = digitalitzacioPlugin.recuperarResultat(
					idTransaccio,
					returnScannedFile,
					returnSignedFile);
			if (resultat != null) {
				resultatDto.setError(
						resultat.isError());
				resultatDto.setErrorDescripcio(
						resultat.getErrorDescripcio());
				resultatDto.setEstat(
						resultat.getEstat() != null ? DigitalitzacioEstatDto.valueOf(
								resultat.getEstat().toString()) : null);
				resultatDto.setContingut(
						resultat.getContingut());
				resultatDto.setNomDocument(
						resultat.getNomDocument());
				resultatDto.setMimeType(
						resultat.getMimeType());
				resultatDto.setEniTipoFirma(
						resultat.getEniTipoFirma());
				resultatDto.setIdioma(
						resultat.getIdioma());
				resultatDto.setResolucion(
						resultat.getResolucion());

				integracioHelper.addAccioOk(
						IntegracioHelper.INTCODI_DIGITALITZACIO,
						"Recuperant resultat digitalització",
						digitalitzacioPlugin.getEndpointURL(),
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0);
			}
		} catch (Exception ex) {
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_DIGITALITZACIO,
					"Recuperant perfils disponibles",
					digitalitzacioPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					"Error al accedir al plugin de digitalitzacio",
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_DIGITALITZACIO,
					"Error al accedir al plugin de digitalitzacio", ex);

		}
		return resultatDto;
	}

	public void digitalitzacioTancarTransaccio(
			String idTransaccio) {

		long t0 = System.currentTimeMillis();
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("idTransaccio",	idTransaccio);
		DigitalitzacioPlugin digitalitzacioPlugin = getDigitalitzacioPlugin();
		
		try {
			digitalitzacioPlugin.tancarTransaccio(
					idTransaccio);

			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_DIGITALITZACIO,
					"Tancant transacció digitalització",
					digitalitzacioPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_DIGITALITZACIO,
					"Recuperant perfils disponibles",
					digitalitzacioPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					"Error al accedir al plugin de digitalitzacio",
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_DIGITALITZACIO,
					"Error al accedir al plugin de digitalitzacio", ex);
		}
	}

	public PortafirmesFluxInfoDto portafirmesRecuperarInfoFluxDeFirma(
			String plantillaFluxId,
			String idioma,
			boolean signerInfo) {

		String accioDescripcio = "Recuperant detall flux de firma";
		long t0 = System.currentTimeMillis();
		PortafirmesFluxInfoDto respostaDto;
		PortafirmesPlugin portafirmesPlugin = getPortafirmesPlugin();
		try {
			respostaDto = new PortafirmesFluxInfoDto();
			PortafirmesFluxInfo resposta = portafirmesPlugin.recuperarFluxDeFirmaByIdPlantilla(
					plantillaFluxId,
					idioma,
					signerInfo);
			if (resposta != null) {
				respostaDto.setNom(
						resposta.getNom());
				respostaDto.setDescripcio(
						resposta.getDescripcio());
			}

			if (signerInfo) {
				List<PortafirmesFluxSignerDto> destinataris = new ArrayList<PortafirmesFluxSignerDto>();
				for (PortafirmesFluxSigner signer : resposta.getSigners()) {
					PortafirmesFluxSignerDto signerDto = new PortafirmesFluxSignerDto();
					signerDto.setNom(
							signer.getNom());
					signerDto.setLlinatges(
							signer.getLlinatges());
					signerDto.setNif(
							signer.getNif());
					signerDto.setObligat(
							signer.isObligat());

					for (PortafirmesFluxReviser revisor : signer.getRevisors()) {
						PortafirmesFluxReviserDto revisorDto = new PortafirmesFluxReviserDto();
						revisorDto.setNom(
								revisor.getNom());
						revisorDto.setLlinatges(
								revisor.getLlinatges());
						revisorDto.setNif(
								revisor.getNif());
						revisorDto.setObligat(
								revisor.isObligat());

						signerDto.getRevisors().add(
								revisorDto);
					}
					destinataris.add(
							signerDto);
				}

				respostaDto.setDestinataris(
						destinataris);
			}
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de portafirmes";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PFIRMA,
					accioDescripcio,
					portafirmesPlugin.getEndpointURL(),
					null,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, errorDescripcio, ex);
		}
		return respostaDto;
	}

	public String portafirmesRecuperarUrlPlantilla(
			String plantillaFluxId,
			String idioma,
			String returnUrl,
			boolean edicio) {

		String accioDescripcio = "Recuperant url flux de firma";
		long t0 = System.currentTimeMillis();
		String resposta = null;
		PortafirmesPlugin portafirmesPlugin = getPortafirmesPlugin();
		try {
			resposta = portafirmesPlugin.recuperarUrlViewEditPlantilla(
					plantillaFluxId,
					idioma,
					returnUrl,
					edicio);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de portafirmes";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PFIRMA,
					accioDescripcio,
					portafirmesPlugin.getEndpointURL(),
					null,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, errorDescripcio, ex);
		}
		return resposta;
	}

	public List<PortafirmesFluxRespostaDto> portafirmesRecuperarPlantillesDisponibles(
			UsuariDto usuariActual,
			boolean filtrar) {

		String accioDescripcio = "Recuperant flux de firma";
		long t0 = System.currentTimeMillis();
		List<PortafirmesFluxRespostaDto> respostesDto = new ArrayList<PortafirmesFluxRespostaDto>();
		PortafirmesPlugin portafirmesPlugin = getPortafirmesPlugin();
		try {
			List<PortafirmesFluxResposta> plantilles = null;
			if (filtrar) {
				plantilles = portafirmesPlugin.recuperarPlantillesPerFiltre(
						usuariActual.getIdioma(),
						usuariActual.getCodi());
			} else {
				plantilles = portafirmesPlugin.recuperarPlantillesDisponibles(
						usuariActual.getIdioma());
			}

			if (plantilles != null) {
				for (PortafirmesFluxResposta plantilla : plantilles) {
					PortafirmesFluxRespostaDto resposta = new PortafirmesFluxRespostaDto();
					resposta.setFluxId(
							plantilla.getFluxId());
					resposta.setNom(
							plantilla.getNom());
					respostesDto.add(
							resposta);
				}
			}
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de portafirmes";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PFIRMA,
					accioDescripcio,
					portafirmesPlugin.getEndpointURL(),
					null,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, errorDescripcio, ex);
		}
		return respostesDto;
	}

	public boolean portafirmesEsborrarPlantillaFirma(
			String idioma,
			String plantillaFluxId) {

		String accioDescripcio = "Esborrant flux de firma";
		long t0 = System.currentTimeMillis();
		PortafirmesPlugin portafirmesPlugin = getPortafirmesPlugin();
		boolean esborrat;
		try {
			esborrat = portafirmesPlugin.esborrarPlantillaFirma(idioma, plantillaFluxId);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de portafirmes";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PFIRMA,
					accioDescripcio,
					portafirmesPlugin.getEndpointURL(),
					null,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, errorDescripcio, ex);
		}
		return esborrat;
	}

	// public List<PortafirmesBlockDto> portafirmesRecuperarBlocksFirma(String
	// idPlantilla, String idTransaccio, boolean portafirmesFluxAsync, String
	// portafirmesId, String idioma) {
	//
	// List<PortafirmesBlockDto> blocksDto = null;
	// String accioDescripcio = "Tancant flux de firma";
	// long t0 = System.currentTimeMillis();
	// try {
	// List<PortafirmesBlockInfo> portafirmesBlocks =
	// portafirmesPluginrecuperarBlocksFirmes(idPlantilla, idTransaccio,
	// portafirmesFluxAsync,
	// new Long(portafirmesId), idioma);
	// if (portafirmesBlocks != null) {
	// blocksDto = new ArrayList<PortafirmesBlockDto>();
	// for (PortafirmesBlockInfo portafirmesBlockInfo : portafirmesBlocks) {
	// PortafirmesBlockDto blockDto = new PortafirmesBlockDto();
	// List<PortafirmesBlockInfoDto> signersInfoDto = new
	// ArrayList<PortafirmesBlockInfoDto>();
	// if (portafirmesBlockInfo.getSigners() != null) {
	// for (PortafirmesBlockSignerInfo portafirmesBlockSignerInfo :
	// portafirmesBlockInfo.getSigners()) {
	// PortafirmesBlockInfoDto signerInfoDto = new PortafirmesBlockInfoDto();
	// signerInfoDto.setSignerCodi(portafirmesBlockSignerInfo.getSignerCodi());
	// signerInfoDto.setSignerId(portafirmesBlockSignerInfo.getSignerId());
	// signerInfoDto.setSignerNom(portafirmesBlockSignerInfo.getSignerNom());
	// signersInfoDto.add(signerInfoDto);
	// }
	// }
	// blockDto.setSigners(signersInfoDto);
	// blocksDto.add(blockDto);
	// }
	// }
	// } catch (Exception ex) {
	// String errorDescripcio = "Error al accedir al plugin de portafirmes";
	// this.integracioHelper.addAccioError("PFIRMA", accioDescripcio, null,
	// IntegracioAccioTipusEnumDto.ENVIAMENT,System.currentTimeMillis() - t0,
	// errorDescripcio, ex);
	// throw new SistemaExternException("PFIRMA", errorDescripcio, ex);
	// }
	// return blocksDto;
	// }

	public String portafirmesRecuperarUrlEstatFluxFirmes(
			long portafirmesId,
			String idioma) {
		String accioDescripcio = "Recuperant url estat flux de firmes";
		long t0 = System.currentTimeMillis();
		String resposta = null;
		PortafirmesPlugin portafirmesPlugin = getPortafirmesPlugin();
		try {
			resposta = portafirmesPlugin.recuperarUrlViewEstatFluxDeFirmes(
					portafirmesId,
					idioma);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de portafirmes";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PFIRMA,
					accioDescripcio,
					portafirmesPlugin.getEndpointURL(),
					null,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA, errorDescripcio, ex);
		}
		return resposta;
	}

	public String conversioConvertirPdfArxiuNom(
			String nomOriginal) {
		return getConversioPlugin().getNomArxiuConvertitPdf(
				nomOriginal);
	}

	public FitxerDto conversioConvertirPdf(
			FitxerDto original,
			String urlPerEstampar) {

		String accioDescripcio = "Conversió de document a PDF";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("arxiuOriginalNom", original.getNom());
		accioParams.put("arxiuOriginalTamany", Integer.valueOf(original.getContingut().length).toString());
		ConversioPlugin conversioPlugin = getConversioPlugin();
		long t0 = System.currentTimeMillis();
		try {
			ConversioArxiu convertit = conversioPlugin.convertirPdfIEstamparUrl(
					new ConversioArxiu(original.getNom(), original.getContingut()),
					urlPerEstampar);
			accioParams.put(
					"arxiuConvertitNom",
					convertit.getArxiuNom());
			accioParams.put(
					"arxiuConvertitTamany",
					Integer.valueOf(convertit.getArxiuContingut().length).toString());
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_CONVERT,
					accioDescripcio,
					conversioPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			FitxerDto resposta = new FitxerDto();
			resposta.setNom(
					convertit.getArxiuNom());
			resposta.setContingut(
					convertit.getArxiuContingut());
			resposta.setContentType(
					"application/pdf");
			return resposta;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de conversió de documents: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_CONVERT,
					accioDescripcio,
					conversioPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_CONVERT, errorDescripcio, ex);
		}
	}

	public ProcedimentDto procedimentFindByCodiSia(
			String codiDir3,
			String codiSia) {

		String accioDescripcio = "Consulta del procediment per codi SIA";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codiDir3",	codiDir3);
		accioParams.put("codiSia", codiSia);
		ProcedimentPlugin procedimentPlugin = getProcedimentPlugin();
		long t0 = System.currentTimeMillis();
		try {
			ProcedimentDto procediment = procedimentPlugin.findAmbCodiSia(codiDir3, codiSia);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_PROCEDIMENT,
					accioDescripcio,
					procedimentPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return procediment;
		} catch (Exception ex) {
			String errorDescripcio = "Error al consultar el procediment per codi SIA: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PROCEDIMENT,
					accioDescripcio,
					procedimentPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_PROCEDIMENT, errorDescripcio, ex);
		}
	}

	public List<Pais> dadesExternesPaisosFindAll() {

		long t0 = System.currentTimeMillis();
		String accioDescripcio = "Consulta de tots els paisos";
		DadesExternesPlugin dadesExternesPlugin = getDadesExternesPlugin();
		
		try {
			List<Pais> paisos = dadesExternesPlugin.paisFindAll();
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_DADESEXT,
					accioDescripcio,
					dadesExternesPlugin.getEndpointURL(),
					null,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return paisos;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_DADESEXT,
					accioDescripcio,
					dadesExternesPlugin.getEndpointURL(),
					null,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_DADESEXT, errorDescripcio, ex);
		}
	}

	public List<ComunitatAutonoma> dadesExternesComunitatsFindAll() {

		long t0 = System.currentTimeMillis();
		String accioDescripcio = "Consulta de totes les comunitats";
		DadesExternesPlugin dadesExternesPlugin = getDadesExternesPlugin();

		try {
			List<ComunitatAutonoma> comunitats = dadesExternesPlugin.comunitatFindAll();
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_DADESEXT,
					accioDescripcio,
					dadesExternesPlugin.getEndpointURL(),
					null,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return comunitats;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_DADESEXT,
					accioDescripcio,
					dadesExternesPlugin.getEndpointURL(),
					null,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_DADESEXT, errorDescripcio, ex);
		}
	}

	public List<Provincia> dadesExternesProvinciesFindAll() {

		long t0 = System.currentTimeMillis();
		String accioDescripcio = "Consulta de totes les províncies";
		DadesExternesPlugin dadesExternesPlugin = getDadesExternesPlugin();

		try {
			List<Provincia> provincies = dadesExternesPlugin.provinciaFindAll();
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_DADESEXT,
					accioDescripcio,
					dadesExternesPlugin.getEndpointURL(),
					null,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return provincies;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_DADESEXT,
					accioDescripcio,
					dadesExternesPlugin.getEndpointURL(),
					null,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_DADESEXT, errorDescripcio, ex);
		}
	}

	public List<Provincia> dadesExternesProvinciesFindAmbComunitat(
			String comunitatCodi) {

		long t0 = System.currentTimeMillis();
		String accioDescripcio = "Consulta de les províncies d'una comunitat";
		Map<String, String> accioParams = new HashMap<String, String>();
		DadesExternesPlugin dadesExternesPlugin = getDadesExternesPlugin();
		accioParams.put("comunitatCodi", comunitatCodi);
		
		try {
			List<Provincia> provincies = dadesExternesPlugin.provinciaFindByComunitat(comunitatCodi);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_DADESEXT,
					accioDescripcio,
					dadesExternesPlugin.getEndpointURL(),
					null,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return provincies;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_DADESEXT,
					accioDescripcio,
					dadesExternesPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_DADESEXT, errorDescripcio, ex);
		}
	}

	public List<Municipi> dadesExternesMunicipisFindAmbProvincia(
			String provinciaCodi) {

		long t0 = System.currentTimeMillis();
		String accioDescripcio = "Consulta dels municipis d'una província";
		Map<String, String> accioParams = new HashMap<String, String>();
		DadesExternesPlugin dadesExternesPlugin = getDadesExternesPlugin();
		accioParams.put("provinciaCodi", provinciaCodi);

		try {
			List<Municipi> municipis = dadesExternesPlugin.municipiFindByProvincia(
					provinciaCodi);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_DADESEXT,
					accioDescripcio,
					dadesExternesPlugin.getEndpointURL(),
					null,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return municipis;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_DADESEXT,
					accioDescripcio,
					dadesExternesPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_DADESEXT, errorDescripcio, ex);
		}
	}

	public List<Municipi> dadesExternesMunicipisFindAmbProvinciaPinbal(
			String provinciaCodi) {

		long t0 = System.currentTimeMillis();
		String accioDescripcio = "Consulta dels municipis d'una província per consultes a PINBAL";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("provinciaCodi", provinciaCodi);
		DadesExternesPlugin dadesExternesPlugin = getDadesExternesPinbalPlugin();
		
		try {
			List<Municipi> municipis = dadesExternesPlugin.municipiFindByProvincia(provinciaCodi);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_DADESEXT,
					accioDescripcio,
					dadesExternesPlugin.getEndpointURL(),
					null,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return municipis;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes PINBAL";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_DADESEXT,
					accioDescripcio,
					dadesExternesPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_DADESEXT, errorDescripcio, ex);
		}
	}

	public List<NivellAdministracioDto> dadesExternesNivellsAdministracioAll() {

		long t0 = System.currentTimeMillis();
		String accioDescripcio = "Consulta de nivells d'administració";
		Map<String, String> accioParams = new HashMap<String, String>();		
		DadesExternesPlugin dadesExternesPlugin = getDadesExternesPlugin();
		
		try {
			List<NivellAdministracioDto> nivellAdministracio = conversioTipusHelper.convertirList(
					dadesExternesPlugin.nivellAdministracioFindAll(),
					NivellAdministracioDto.class);
			
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_DADESEXT,
					accioDescripcio,
					dadesExternesPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0);
			return nivellAdministracio;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_DADESEXT,
					accioDescripcio,
					dadesExternesPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_DADESEXT, errorDescripcio, ex);
		}
	}

	public List<TipusViaDto> dadesExternesTipusViaAll() {

		long t0 = System.currentTimeMillis();
		String accioDescripcio = "Consulta de tipus de via";
		Map<String, String> accioParams = new HashMap<String, String>();
		DadesExternesPlugin dadesExternesPlugin = getDadesExternesPlugin();

		try {
			List<TipusViaDto> tipusVies = conversioTipusHelper.convertirList(
					dadesExternesPlugin.tipusViaFindAll(),
					TipusViaDto.class);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_DADESEXT,
					accioDescripcio,
					dadesExternesPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0);
			return tipusVies;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_DADESEXT,
					accioDescripcio,
					dadesExternesPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_DADESEXT, errorDescripcio, ex);
		}
	}

	public SignatureInfoDto detectSignedAttachedUsingPdfReader(
			byte[] documentContingut,
			String contentType) {

		boolean isSigned = isFitxerSigned(
				documentContingut,
				contentType);
		boolean validationError = false; // !isSigned;
		String validationErrorMsg = ""; // "error error error error error error ";

		return new SignatureInfoDto(isSigned, validationError, validationErrorMsg);
	}

	private boolean isFitxerSigned(
			byte[] contingut,
			String contentType) {

		if (contentType.equals(
				"application/pdf")) {
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

	public SignatureInfoDto detectaFirmaDocument(byte[] contingut, String contentType) {
		if (aplicacioService.getBooleanJbossProperty(PropertyConfig.VALIDATE_SIGNATURE_ATTACHED, true)) {
			return detectSignedAttachedUsingValidateSignaturePlugin(contingut, contentType);
		} else {
			return detectSignedAttachedUsingPdfReader(contingut, contentType);
		}
	}
	
	/**
	 * Aquesta funció es crida només en el OnChange de la modal de document, al seleccionar un fitxer, 
	 * per saber si donar la opció de adjuntar firma separada o no.
	 * 
	 * Per tant el documentContingut es el del fitxer adjunt, nomes es comprova si ja té firma o no.
	 * En cas de no tenir-ne, el usuari podrà adjuntar el fitxer de firma, contingut el qual ja no passarà per aquesta funció.
	 * 
	 * Quant s'adjunta un segon fitxer que es el de la firma, no es valida per aqui, sino al guardar.
	 */
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
			
			ValidateSignatureResponse validateSignatureResponse = getValidaSignaturaPlugin().getPlugin().validateSignature(validationRequest);

			ValidationStatus validationStatus = validateSignatureResponse.getValidationStatus();
			if (validationStatus.getStatus() == 1) {
				return new SignatureInfoDto(true, false, null);
			} else {
				String missatge = validationStatus.getErrorMsg();
				if (missatge==null) {
					missatge = "Error no especificado al validar la firma del fichero.";
				} else if (missatge.indexOf("SignedDataNotProvided")>0) {
					missatge = "SignedDataNotProvided: Se debe aportar el fichero original.";
				} else if (!RolHelper.getRolsCurrentUser().contains("IPA_ADMIN")) {
                    missatge = "Error al detectar firma de document.";
                }
                return new SignatureInfoDto(true, true, missatge);
			}
		} catch (Exception e) {
			Throwable throwable = ExceptionHelper.getRootCauseOrItself(e);
			if (throwable.getMessage().contains("El formato de la firma no es valido(urn:oasis:names:tc:dss:1.0:resultmajor:RequesterError)")
					|| throwable.getMessage().contains("El formato de la firma no es válido(urn:oasis:names:tc:dss:1.0:resultmajor:RequesterError)")
					|| throwable.getMessage().contains("El documento OOXML no está firmado(urn:oasis:names:tc:dss:1.0:resultmajor:ResponderError)")
					|| throwable.getMessage().contains("El documento OOXML no está firmado.(urn:oasis:names:tc:dss:1.0:resultmajor:ResponderError)")
					|| throwable.getMessage().contains("La firma proporcionada no contiene un nodo <ds:Signature>")) {
				return new SignatureInfoDto(false, false, null);
			} else {
				logger.error(
						"Error al detectar firma de document",
						e);
				return new SignatureInfoDto(false, true,
                        RolHelper.getRolsCurrentUser().contains("IPA_ADMIN")
                        ? e.getMessage()
                        : "Error al detectar firma de document.");
			}
		}
	}

	public List<ArxiuFirmaDto> validaSignaturaObtenirFirmes(String uuid, boolean throwExceptionIfNotValid) {
		Document arxiuDocument = arxiuDocumentConsultar(null, uuid, null, true, false);
		//La firma sempre esta en el contingut, excepte per CADES_DET (TF04)
		return validaSignaturaObtenirFirmes(
				arxiuDocument.getNom(),
				documentHelper.getContingutFromArxiuDocument(arxiuDocument),
				documentHelper.getFirmaDetachedFromArxiuDocument(arxiuDocument),
				null,
				throwExceptionIfNotValid);
	}
	
	public List<ArxiuFirmaDto> validaSignaturaObtenirFirmes(
			String nomFitxer,
			byte[] documentContingut,
			byte[] firmaContingut,
			String firmaContentType,
			boolean throwExceptionIfNotValid) {
		
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
		IValidateSignaturePluginWrapper validaSignaturaPlugin = getValidaSignaturaPlugin();
		if (nomFitxer==null) {nomFitxer="ContingutFirma";}
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
			sri.setReturnCertificates(true);
			sri.setReturnTimeStampInfo(true);
			validationRequest.setSignatureRequestedInformation(sri);
			
			ValidateSignatureResponse validateSignatureResponse = validaSignaturaPlugin.getPlugin().validateSignature(validationRequest);

			ValidationStatus validationStatus = validateSignatureResponse.getValidationStatus();
			if (validationStatus.getStatus() != 1 && throwExceptionIfNotValid) {
				throw new RuntimeException(validationStatus.getErrorMsg());
			}

			List<ArxiuFirmaDetallDto> detalls = new ArrayList<ArxiuFirmaDetallDto>();
			List<ArxiuFirmaDto> firmes = new ArrayList<ArxiuFirmaDto>();
			ArxiuFirmaDto firma = new ArxiuFirmaDto();
			if (validateSignatureResponse.getSignatureDetailInfo() != null) {
				for (SignatureDetailInfo signatureInfo : validateSignatureResponse.getSignatureDetailInfo()) {
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

					if (isObtenirDataFirmaFromAtributDocument() && detall.getResponsableNif() != null) {
						try {
							PDDocument document = PDDocument.load(new ByteArrayInputStream(documentContingut));
							for (PDSignature signature : document.getSignatureDictionaries()) {
								if (signature.getName() != null && signature.getName().contains(detall.getResponsableNif())) {
									detall.setData(signature.getSignDate().getTime());
								}
							}
							document.close();
						} catch (IOException ex) {
							logger.error(
									"Hi ha hagut un problema recuperant l'hora de firma: " + ex.getMessage());
						}
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
				firma.setTipus(
						ArxiuConversions.toArxiuFirmaTipusEnum(
								validateSignatureResponse.getSignType(),
								validateSignatureResponse.getSignFormat()));
				firma.setTipusMime(firmaContentType);
				if (ArxiuFirmaTipusEnumDto.CADES_DET.equals(firma.getTipus())) {
					firma.setFitxerNom(nomFitxer+"_signature.csig");
				} else {
					firma.setFitxerNom(nomFitxer);
				}
				firmes.add(firma);
			}
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_VALIDASIG,
					accioDescripcio,
					validaSignaturaPlugin.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return firmes;
		} catch (Exception ex) {
			String errorDescripcio = "Error validant la firma del document: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_VALIDASIG,
					accioDescripcio,
					validaSignaturaPlugin.getEndpoint(),
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_VALIDASIG, errorDescripcio, ex);
		}
	}

	public List<RespostaAmpliarPlazo> ampliarPlazoEnviament(AmpliarPlazoForm documentNotificacioDto) {
		
		long t0 = System.currentTimeMillis();
		List<RespostaAmpliarPlazo> resultat = new ArrayList<RespostaAmpliarPlazo>();
		NotificacioPlugin notificacioPlugin = getNotificacioPlugin();
		String accioDescripcio = "Ampliar plaç de enviament notib";
		
		if (documentNotificacioDto!=null && documentNotificacioDto.getDocumentEnviamentInteressats()!=null) {
			for (DocumentEnviamentInteressatDto dei: documentNotificacioDto.getDocumentEnviamentInteressats()) {
				if (dei.getDiesAmpliacio()!=null && dei.getDiesAmpliacio()>0) {
				
					Map<String, String> accioParams = new HashMap<String, String>();
					accioParams.put("referencia", dei.getEnviamentReferencia());
					accioParams.put("motiu", dei.getMotiu());
					accioParams.put("dies", dei.getDiesAmpliacio().toString());
					
					List<String> refs = new ArrayList<String>();
					refs.add(dei.getEnviamentReferencia());
					
					try {

						RespostaAmpliarPlazo aux = notificacioPlugin.ampliarPlazo(refs, dei.getMotiu(), dei.getDiesAmpliacio());
						DocumentEnviamentInteressatEntity deiE = documentNotificacioHelper.findDocumentEnviamentInteressatById(dei.getId());
						aux.setDocumentNum(deiE.getInteressat().getDocumentNum());
						aux.setNomInteressat(deiE.getInteressat().getNomComplet());
						resultat.add(aux);
						integracioHelper.addAccioOk(
								IntegracioHelper.INTCODI_NOTIFICACIO,
								accioDescripcio,
								notificacioPlugin.getEndpointURL(),
								accioParams,
								IntegracioAccioTipusEnumDto.ENVIAMENT,
								System.currentTimeMillis() - t0);
	
					} catch (Exception ex) {
						String errorDescripcio = "Error al accedir al plugin de notificacions";
						integracioHelper.addAccioError(
								IntegracioHelper.INTCODI_NOTIFICACIO,
								accioDescripcio,
								notificacioPlugin.getEndpointURL(),
								accioParams,
								IntegracioAccioTipusEnumDto.ENVIAMENT,
								System.currentTimeMillis() - t0,
								errorDescripcio,
								ex);
						throw new SistemaExternException(IntegracioHelper.INTCODI_NOTIFICACIO, errorDescripcio, ex);
					}
				}
			}
		}
		
		return resultat;
	}
	
	public RespostaEnviar notificacioEnviar(
			DocumentNotificacioDto notificacioDto,
			ExpedientEntity expedientEntity,
			DocumentEntity documentEntity,
			InteressatEntity interessat) {

		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(expedientEntity.getId()));
		MetaExpedientEntity metaExpedient = expedientEntity.getMetaExpedient();
		
		String accioDescripcio = "Enviament d'una notificació electrònica";
		Map<String, String> accioParams = getNotificacioAccioParams(
				notificacioDto,
				expedientEntity,
				documentEntity,
				interessat);
		
		long t0 = System.currentTimeMillis();
		NotificacioPlugin notificacioPlugin = getNotificacioPlugin();
		
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
			notificacio.setRetard((notificacioDto.getRetard() != null) ? notificacioDto.getRetard()	: getPropertyNotificacioRetardNumDies());
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

			if (documentEntity.getDocumentTipus().equals(DocumentTipusEnumDto.VIRTUAL) ||
				Utils.notEquals(documentEntity.getFitxerContentType(),"application/pdf")) {
				FitxerDto fitxer = documentHelper.getFitxerAssociat(documentEntity,null);
				notificacio.setDocumentArxiuNom(fitxer.getNom());
				notificacio.setDocumentArxiuContingut(fitxer.getContingut());
			} else {
				// fitxer = arxiuDocumentVersioImprimible(documentEntity);
				notificacio.setDocumentArxiuNom(documentEntity.getFitxerNom());
				notificacio.setDocumentArxiuUuid(documentEntity.getArxiuUuid());
			}

			if (metaExpedient.getTipusClassificacio() == TipusClassificacioEnumDto.SIA) {
				notificacio.setProcedimentCodi(metaExpedient.getClassificacio());
			}

			notificacio.setNumExpedient(expedientEntity.getNumero());
			UsuariDto usuari = aplicacioService.getUsuariActual();
			List<Enviament> enviaments = new ArrayList<>();
			Enviament enviament = new Enviament();
			enviament.setTitular(convertirAmbPersona(interessat));

			if (interessat.getRepresentant() != null) {
				enviament.setDestinataris(Arrays.asList(convertirAmbPersona(interessat.getRepresentant())));
			}

			// ########## ENTREGA POSTAL ###############
			if (notificacioDto.isEntregaPostal()) {
				
				enviament.setEntregaPostalActiva(true);
				enviament.setEntregaPostalTipus(EntregaPostalTipus.SENSE_NORMALITZAR);
				InteressatEntity interessatPerAdresa = interessat;
				
				if (interessat.getRepresentant() != null) {
					interessatPerAdresa = interessat.getRepresentant();
				}
				
				PaisDto pais = null;
				if (interessatPerAdresa.getPais()!=null) {
					pais = dadesExternesHelper.getPaisAmbCodi(interessatPerAdresa.getPais());
					if (pais == null) {
						throw new NotFoundException(interessatPerAdresa.getPais(), PaisDto.class);
					}
				} else {
					throw new NotFoundException(interessatPerAdresa.getPais(), PaisDto.class);
				}

				ProvinciaDto provincia = null;
				if (interessatPerAdresa.getProvincia()!=null) {
					provincia = dadesExternesHelper.getProvinciaAmbCodi(interessatPerAdresa.getProvincia());
					if (provincia == null) {
						throw new NotFoundException(interessatPerAdresa.getProvincia(), ProvinciaDto.class);
					}
				} else {
					throw new NotFoundException(interessatPerAdresa.getProvincia(), ProvinciaDto.class);
				}					
				
				MunicipiDto municipi = null;
				if (interessatPerAdresa.getMunicipi() != null) {
					municipi = dadesExternesHelper.getMunicipiAmbCodi(interessatPerAdresa.getProvincia(), interessatPerAdresa.getMunicipi());
					if (municipi == null) {
						throw new NotFoundException(interessatPerAdresa.getMunicipi(), MunicipiDto.class);
					}
				} else {
					throw new NotFoundException(interessatPerAdresa.getMunicipi(), MunicipiDto.class);
				}

				enviament.setEntregaPostalCodiPostal(interessatPerAdresa.getCodiPostal());
				enviament.setEntregaPostalPaisCodi(pais.getAlfa2());
				enviament.setEntregaPostalProvinciaCodi(provincia.getCodi());
				enviament.setEntregaPostalMunicipiCodi(provincia.getCodi() + String.format("%04d",Integer.parseInt(municipi.getCodi())));
				enviament.setEntregaPostalLinea1(interessatPerAdresa.getAdresa() + ", " + interessatPerAdresa.getCodiPostal() + ", "+ municipi.getNom());
				enviament.setEntregaPostalLinea2(provincia.getNom() + ", " + pais.getNom());
			}
			// ########## ENVIAMENT DEH ###############
			if (interessat.getEntregaDeh() != null && interessat.getEntregaDeh() && Boolean.parseBoolean(
					configHelper.getConfig(PropertyConfig.NOTIB_PLUGIN_DEH_ACTIVA))) {
				enviament.setEntregaDehActiva(true);
				enviament.setEntregaDehObligat(interessat.getEntregaDehObligat());
				enviament.setEntregaDehProcedimentCodi(metaExpedient.getClassificacio());
				enviament.setEntregaNif(interessat.getDocumentNum());
			}
			enviaments.add(enviament);
			notificacio.setEnviaments(enviaments);
			notificacio.setUsuariCodi(usuari.getCodi());
			notificacio.setServeiTipusEnum(notificacioDto.getServeiTipusEnum());

			// ############## ALTA NOTIFICACIO #######################
			RespostaEnviar respostaEnviar = notificacioPlugin.enviar(notificacio);
			
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_NOTIFICACIO,
					accioDescripcio,
					notificacioPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			
			return respostaEnviar;

		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de notificacions";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_NOTIFICACIO,
					accioDescripcio,
					notificacioPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_NOTIFICACIO, errorDescripcio, ex);
		}
	}

	public byte[] notificacioConsultarIDescarregarCertificacio(
			DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity) {

		organGestorHelper.actualitzarOrganCodi(
				organGestorHelper.getOrganCodiFromContingutId(
						documentEnviamentInteressatEntity.getNotificacio().getExpedient().getId()));

		RespostaConsultaEstatEnviament resposta;
		try {
			resposta = getNotificacioPlugin().consultarEnviament(
					documentEnviamentInteressatEntity.getEnviamentReferencia());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		byte[] certificacio = resposta.getCertificacioContingut();
		return certificacio;
	}

	public RespostaJustificantEnviamentNotib notificacioDescarregarJustificantEnviamentNotib(
			String identificador) {

		try {
			return getNotificacioPlugin().consultaJustificantEnviament(
					identificador);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public RespostaConsultaEstatEnviament notificacioConsultarIActualitzarEstat(
			DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity) {

		long t0 = System.currentTimeMillis();
		DocumentNotificacioEntity notificacio = documentEnviamentInteressatEntity.getNotificacio();
		ExpedientEntity expedient = notificacio.getExpedient();
		DocumentEntity document = notificacio.getDocument();
		DocumentNotificacioEstatEnumDto estatAnterior = notificacio.getNotificacioEstat();
		ConfigHelper.setEntitat(
				conversioTipusHelper.convertir(
						expedient.getEntitat(),
						EntitatDto.class));
		organGestorHelper.actualitzarOrganCodi(
				organGestorHelper.getOrganCodiFromContingutId(
						expedient.getId()));

		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"setEmisorDir3Codi",
				expedient.getEntitat().getUnitatArrel());
		accioParams.put(
				"expedientId",
				expedient.getId().toString());
		accioParams.put(
				"expedientTitol",
				expedient.getNom());
		accioParams.put(
				"expedientTipusId",
				expedient.getMetaNode().getId().toString());
		accioParams.put(
				"expedientTipusNom",
				expedient.getMetaNode().getNom());
		accioParams.put(
				"documentNom",
				document.getNom());
		if (notificacio.getTipus() != null) {
			accioParams.put(
					"enviamentTipus",
					notificacio.getTipus().name());
		}
		accioParams.put("concepte", notificacio.getAssumpte());
		accioParams.put("referencia", documentEnviamentInteressatEntity.getEnviamentReferencia());
		NotificacioPlugin notificacioPlugin = getNotificacioPlugin();

		try {

			RespostaConsultaEstatEnviament resposta = null;
			
			if (documentEnviamentInteressatEntity.getEnviamentReferencia()!=null) {
			
				resposta = notificacioPlugin.consultarEnviament(documentEnviamentInteressatEntity.getEnviamentReferencia());
	
				documentEnviamentInteressatEntity.updateEnviamentEstat(
						resposta.getEstat(),
						resposta.getEstatData(),
						resposta.getEstatOrigen(),
						documentEnviamentInteressatEntity.getEnviamentCertificacioData(),
						resposta.getCertificacioOrigen(),
						resposta.isError(),
						resposta.getErrorDescripcio());
	
				documentEnviamentInteressatEntity.updateEnviamentInfoRegistre(
						resposta.getRegistreData(),
						resposta.getRegistreNumero(),
						resposta.getRegistreNumeroFormatat());
				
				guardarCertificacio(documentEnviamentInteressatEntity, resposta);
			}

			RespostaConsultaEstatNotificacio respostaNotificioEstat = notificacioPlugin.consultarNotificacio(
					notificacio.getNotificacioIdentificador());
			
			notificacio.updateNotificacioEstat(
					respostaNotificioEstat.getEstat(),
					resposta!=null?resposta.getEstatData():Calendar.getInstance().getTime(),
					respostaNotificioEstat.isError(),
					respostaNotificioEstat.getErrorDescripcio(),
					respostaNotificioEstat.getDataEnviada(),
					respostaNotificioEstat.getDataFinalitzada());

			DocumentNotificacioEstatEnumDto estatDespres = notificacio.getNotificacioEstat();
			
			if (estatAnterior != estatDespres &&
				estatAnterior != DocumentNotificacioEstatEnumDto.FINALITZADA &&
				estatDespres  != DocumentNotificacioEstatEnumDto.PROCESSADA) {
					emailHelper.canviEstatNotificacio(notificacio, estatAnterior);
			}

			cacheHelper.evictErrorsValidacioPerNode(
					expedient);
			cacheHelper.evictNotificacionsPendentsPerExpedient(
					expedient);

			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_NOTIFICACIO,
					"Consulta d'estat d'una notificació electrònica",
					notificacioPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0);

			return resposta;

		} catch (Exception ex) {
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_NOTIFICACIO,
					"Consulta d'estat d'una notificació electrònica",
					notificacioPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					"Error al accedir al plugin de notificacions",
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_NOTIFICACIO,
					"Error al accedir al plugin de notificacions", ex);
		}
	}

	public void guardarCertificacio(
			DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity,
			RespostaConsultaEstatEnviament resposta) {
		DocumentNotificacioEntity notificacio = documentEnviamentInteressatEntity.getNotificacio();
		ExpedientEntity expedient = notificacio.getExpedient();

		boolean certificacioRetornat = resposta.getCertificacioData() != null;

		if (certificacioRetornat) {

			if (getPropertyGuardarCertificacioExpedient()) {
				boolean certificacioJaGuardat = documentEnviamentInteressatEntity
						.getEnviamentCertificacioData() != null;
				if (!certificacioJaGuardat) {
					MetaDocumentEntity metaDocument = metaDocumentRepository.findByEntitatAndTipusGeneric(
							true,
							null,
							MetaDocumentTipusGenericEnumDto.ACUSE_RECIBO_NOTIFICACION);

					DocumentDto document = certificacioToDocumentDto(
							documentEnviamentInteressatEntity,
							metaDocument,
							resposta);
					documentHelper.crearDocument(
							null,
							document,
							notificacio.getDocument().getPare(),
							true,
							false);
				}

			} else {
				// saves in gestio documental but never uses it?
				byte[] certificacio = resposta.getCertificacioContingut();
				String gestioDocumentalId = notificacio.getEnviamentCertificacioArxiuId();
				if (gestioDocumentalId != null
						&& documentEnviamentInteressatEntity.getEnviamentCertificacioData().before(
								resposta.getCertificacioData())) {
					gestioDocumentalDelete(
							notificacio.getEnviamentCertificacioArxiuId(),
							GESDOC_AGRUPACIO_CERTIFICACIONS);
				}
				if (gestioDocumentalId == null
						|| documentEnviamentInteressatEntity.getEnviamentCertificacioData().before(
								resposta.getCertificacioData())) {
					gestioDocumentalId = gestioDocumentalCreate(
							PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS,
							new ByteArrayInputStream(certificacio));
				}
				notificacio.setEnviamentCertificacioArxiuId(
						gestioDocumentalId);
			}

		}

		if (resposta.getEstat() == es.caib.ripea.plugin.notificacio.EnviamentEstat.NOTIFICADA) {
			logAll(
					notificacio,
					LogTipusEnumDto.NOTIFICACIO_CERTIFICADA,
					null);
		} else if (resposta.getEstat() == es.caib.ripea.plugin.notificacio.EnviamentEstat.REBUTJADA) {
			logAll(
					notificacio,
					LogTipusEnumDto.NOTIFICACIO_REBUTJADA,
					null);
		}

		documentEnviamentInteressatEntity.updateEnviamentCertificacioData(
				resposta.getCertificacioData());

	}

	private DocumentDto certificacioToDocumentDto(
			DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity,
			MetaDocumentEntity metaDocument,
			RespostaConsultaEstatEnviament resposta) {
		return contingutHelper.generarDocumentDto(
				documentEnviamentInteressatEntity,
				metaDocument,
				resposta);
	}

	private void logAll(
			DocumentNotificacioEntity notificacioEntity,
			LogTipusEnumDto tipusLog,
			String param1) {
		logAll(
				notificacioEntity,
				tipusLog,
				param1,
				notificacioEntity.getAssumpte());
	}

	private void logAll(
			DocumentNotificacioEntity notificacioEntity,
			LogTipusEnumDto tipusLog,
			String param1,
			String param2) {
		contingutLogHelper.log(
				notificacioEntity.getDocument(),
				LogTipusEnumDto.MODIFICACIO,
				notificacioEntity,
				LogObjecteTipusEnumDto.NOTIFICACIO,
				tipusLog,
				param1,
				param2,
				false,
				false);
		contingutLogHelper.log(
				notificacioEntity.getDocument().getExpedient(),
				LogTipusEnumDto.MODIFICACIO,
				notificacioEntity,
				LogObjecteTipusEnumDto.NOTIFICACIO,
				tipusLog,
				param1,
				param2,
				false,
				false);

	}

	public String gestioDocumentalCreate(
			String agrupacio,
			InputStream contingut) {

		String accioDescripcio = "Crear document al gestor documental";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("agrupacio", agrupacio);
		
		long t0 = System.currentTimeMillis();
		GestioDocumentalPlugin gestioDocumentalPlugin = getGestioDocumentalPlugin();

		try {
			String resultat = gestioDocumentalPlugin.create(agrupacio, contingut);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					gestioDocumentalPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return resultat;
		} catch (Exception ex) {
			String errorDescripcio = "Error al crear document al gestor documental";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					gestioDocumentalPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESDOC, errorDescripcio, ex);
		}
	}

	public void gestioDocumentalUpdate(
			String id,
			String agrupacio,
			InputStream contingut) {
		
		String accioDescripcio = "Update document al gestor documental";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", id);
		accioParams.put("agrupacio", agrupacio);
		
		long t0 = System.currentTimeMillis();
		GestioDocumentalPlugin gestioDocumentalPlugin = getGestioDocumentalPlugin();

		try {
			gestioDocumentalPlugin.update(id, agrupacio, contingut);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					gestioDocumentalPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al update document al gestor documental";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					gestioDocumentalPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESDOC, errorDescripcio, ex);
		}
	}

	public void gestioDocumentalDelete(
			String id,
			String agrupacio) {
		if (id != null) {
			String accioDescripcio = "Delete document al gestor documental";
			Map<String, String> accioParams = new HashMap<String, String>();
			accioParams.put("id", id);
			accioParams.put("agrupacio", agrupacio);
			
			long t0 = System.currentTimeMillis();
			GestioDocumentalPlugin gestioDocumentalPlugin = getGestioDocumentalPlugin();

			try {
				gestioDocumentalPlugin.delete(id, agrupacio);
				integracioHelper.addAccioOk(
						IntegracioHelper.INTCODI_GESDOC,
						accioDescripcio,
						gestioDocumentalPlugin.getEndpointURL(),
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0);
			} catch (Exception ex) {
				String errorDescripcio = "Error al eliminar document al gestor documental";
				integracioHelper.addAccioError(
						IntegracioHelper.INTCODI_GESDOC,
						accioDescripcio,
						gestioDocumentalPlugin.getEndpointURL(),
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0,
						errorDescripcio,
						ex);
				throw new SistemaExternException(IntegracioHelper.INTCODI_GESDOC, errorDescripcio, ex);
			}
		}
	}

	public void gestioDocumentalGet(
			String id,
			String agrupacio,
			OutputStream contingutOut) {

		String accioDescripcio = "GET document al gestor documental";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", id);
		accioParams.put("agrupacio", agrupacio);
		
		long t0 = System.currentTimeMillis();
		GestioDocumentalPlugin gestioDocumentalPlugin = getGestioDocumentalPlugin();

		try {
			gestioDocumentalPlugin.get(id, agrupacio, contingutOut);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					gestioDocumentalPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al consultar document al gestor documental";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					gestioDocumentalPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESDOC, errorDescripcio, ex);
		}
	}

	public SignaturaResposta firmaServidorFirmar(
			DocumentEntity document,
			FitxerDto fitxer,
			String motiu,
			String idioma) {

		organGestorHelper.actualitzarOrganCodi(
				organGestorHelper.getOrganCodiFromContingutId(
						document.getId()));
		String accioDescripcio = "Firma en servidor d'un document";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"id",
				document.getId().toString());
		accioParams.put(
				"títol",
				document.getNom());
		long t0 = System.currentTimeMillis();
		FirmaServidorPlugin firmaServidorPlugin = getFirmaServidorPlugin();
		
		try {

			SignaturaResposta firma = firmaServidorPlugin.firmar(fitxer.getNom(), motiu, fitxer.getContingut(), idioma, fitxer.getContentType());
			
			if (StringUtils.isEmpty(firma.getTipusFirmaEni()) || StringUtils.isEmpty(firma.getPerfilFirmaEni())) {
				logger.warn("El tipus o perfil de firma s'ha retornat buit i això pot provocar error guardant a l'Arxiu [tipus: " + 
						firma.getTipusFirmaEni() + ", perfil: " + firma.getPerfilFirmaEni() + "]");
				if ("cades".equals(StringUtils.lowerCase(firma.getTipusFirma()))) {
					logger.warn("Fixant el tipus de firma a TF04 i perfil BES");
					if (StringUtils.isEmpty(firma.getTipusFirmaEni()))
						firma.setTipusFirmaEni("TF04");
					if (StringUtils.isEmpty(firma.getPerfilFirmaEni()))
						firma.setPerfilFirmaEni("AdES-BES");
				} else if ("pades".equals(StringUtils.lowerCase(firma.getTipusFirma()))) {
					logger.warn("Fixant el tipus de firma a TF06 i perfil BES");
					if (StringUtils.isEmpty(firma.getTipusFirmaEni()))
						firma.setTipusFirmaEni("TF06");
					if (StringUtils.isEmpty(firma.getPerfilFirmaEni()))
						firma.setPerfilFirmaEni("AdES-BES");
				}
			}

			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_FIRMASERV,
					accioDescripcio,
					firmaServidorPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			
			return firma;

		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de firma en servidor: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_FIRMASERV,
					accioDescripcio,
					firmaServidorPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_FIRMASERV, errorDescripcio, ex);
		}
	}

	public String viaFirmaUpload(
			DocumentEntity document,
			DocumentViaFirmaEntity documentViaFirmaEntity) {

		organGestorHelper.actualitzarOrganCodi(
				organGestorHelper.getOrganCodiFromContingutId(
						document.getId()));
		ViaFirmaParams parametresViaFirma = new ViaFirmaParams();
		ViaFirmaDispositiu viaFirmaDispositiu = new ViaFirmaDispositiu();
		ViaFirmaResponse viaFirmaResponse;
		String accioDescripcio = "Enviament de document a firmar";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"documentId",
				document.getId().toString());
		accioParams.put(
				"documentTitol",
				document.getNom());
		long t0 = System.currentTimeMillis();
		FitxerDto fitxerOriginal = documentHelper.getFitxerAssociat(document, null);
		FitxerDto fitxerConvertit = this.conversioConvertirPdf(fitxerOriginal, null);
		ViaFirmaPlugin viaFirmaPlugin = getViaFirmaPlugin();
		
		try {
			DispositiuEnviamentEntity dispositiu = documentViaFirmaEntity.getDispositiuEnviament();
			if (dispositiu != null) {
				viaFirmaDispositiu.setCodi(
						dispositiu.getCodi());
				viaFirmaDispositiu.setCodiAplicacio(
						dispositiu.getCodiAplicacio());
				viaFirmaDispositiu.setCodiUsuari(
						dispositiu.getCodiUsuari());
				viaFirmaDispositiu.setDescripcio(
						dispositiu.getDescripcio());
				viaFirmaDispositiu.setEmailUsuari(
						dispositiu.getEmailUsuari());
				viaFirmaDispositiu.setEstat(
						dispositiu.getEstat());
				viaFirmaDispositiu.setIdentificador(
						dispositiu.getIdentificador());
				viaFirmaDispositiu.setIdentificadorNacional(
						dispositiu.getIdentificadorNacional());
				viaFirmaDispositiu.setLocal(
						dispositiu.getLocal());
				viaFirmaDispositiu.setTipus(
						dispositiu.getTipus());
				viaFirmaDispositiu.setToken(
						dispositiu.getToken());
			}
			String encodedBase64 = new String(Base64.encodeBase64(
					fitxerConvertit.getContingut()));
			parametresViaFirma.setContingut(
					encodedBase64);
			parametresViaFirma.setCodiUsuari(
					documentViaFirmaEntity.getCodiUsuari());
			parametresViaFirma.setContrasenya(
					documentViaFirmaEntity.getContrasenyaUsuariViaFirma());
			parametresViaFirma.setDescripcio(
					documentViaFirmaEntity.getDescripcio());
			parametresViaFirma.setLecturaObligatoria(
					documentViaFirmaEntity.isLecturaObligatoria());
			parametresViaFirma.setTitol(
					documentViaFirmaEntity.getTitol());
			parametresViaFirma.setViaFirmaDispositiu(
					viaFirmaDispositiu);
			parametresViaFirma.setExpedientCodi(
					document.getExpedient().getNumero());
			parametresViaFirma.setSignantNif(
					documentViaFirmaEntity.getSignantNif());
			parametresViaFirma.setSignantNom(
					documentViaFirmaEntity.getSignantNom());
			parametresViaFirma.setObservaciones(
					documentViaFirmaEntity.getObservacions());
			parametresViaFirma.setValidateCodeEnabled(
					documentViaFirmaEntity.isValidateCodeEnabled());
			parametresViaFirma.setValidateCode(
					documentViaFirmaEntity.getValidateCode());
			parametresViaFirma.setDeviceEnabled(
					getPropertyViaFirmaDispositius());
			
			viaFirmaResponse = viaFirmaPlugin.uploadDocument(parametresViaFirma);
			
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de viaFirma";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_VIAFIRMA,
					accioDescripcio,
					viaFirmaPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_VIAFIRMA, errorDescripcio, ex);
		}
		return viaFirmaResponse.getCodiMissatge();
	}

	public ViaFirmaDocument viaFirmaDownload(
			DocumentViaFirmaEntity documentViaFirma) {

		String accioDescripcio = "Descarregar document firmat";
		Map<String, String> accioParams = new HashMap<String, String>();
		DocumentEntity document = documentViaFirma.getDocument();
		accioParams.put(
				"documentId",
				document.getId().toString());
		accioParams.put(
				"documentPortafirmesId",
				documentViaFirma.getId().toString());
		accioParams.put(
				"messageCode",
				documentViaFirma.getMessageCode());
		long t0 = System.currentTimeMillis();
		ViaFirmaDocument viaFirmaDocument = null;
		ViaFirmaPlugin viaFirmaPlugin = getViaFirmaPlugin();
		
		try {
			viaFirmaDocument = viaFirmaPlugin.downloadDocument(
					documentViaFirma.getCodiUsuari(),
					documentViaFirma.getContrasenyaUsuariViaFirma(),
					documentViaFirma.getMessageCode());
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_VIAFIRMA,
					accioDescripcio,
					viaFirmaPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return viaFirmaDocument;
		} catch (Exception ex) {
			String errorDescripcio = "Error al descarregar el document firmat";
			document.updateEstat(
					DocumentEstatEnumDto.FIRMA_PENDENT_VIAFIRMA);
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_VIAFIRMA,
					accioDescripcio,
					viaFirmaPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_VIAFIRMA, errorDescripcio, ex);
		}
	}

	public List<ViaFirmaDispositiuDto> getDeviceUser(
			String codiUsuari,
			String contasenya) {

		List<ViaFirmaDispositiuDto> viaFirmaDispositiusDto = new ArrayList<>();
		try {
			List<ViaFirmaDispositiu> viaFirmaDispositius = getViaFirmaPlugin().getDeviceUser(
					codiUsuari,
					contasenya);
			viaFirmaDispositiusDto = conversioTipusHelper.convertirList(
					viaFirmaDispositius,
					ViaFirmaDispositiuDto.class);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de viaFirma";
			throw new SistemaExternException(IntegracioHelper.INTCODI_VIAFIRMA, errorDescripcio, ex);
		}
		return viaFirmaDispositiusDto;
	}

	public String firmaSimpleWebStart(
			List<FitxerDto> fitxersPerFirmar,
			String motiu,
			UsuariDto usuariActual,
			String urlReturnToRipea) {

		String accioDescripcio = "Iniciant firma simple";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("motiu", motiu);
		if (usuariActual!=null)
			accioParams.put("usuariActual", usuariActual.getCodiAndNom());
		accioParams.put("urlReturnToRipea", urlReturnToRipea);
		accioParams.put("fitxersPerFirmar", Utils.getFileNames(fitxersPerFirmar));
		
		long t0 = System.currentTimeMillis();
		FirmaWebPlugin firmaWebPlugin = getFirmaSimpleWebPlugin();

		try {
			String resultat = firmaWebPlugin.firmaSimpleWebStart(
					fitxersPerFirmar,
					motiu,
					usuariActual,
					urlReturnToRipea);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_FIRMASIMPLE,
					accioDescripcio,
					firmaWebPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return resultat;
		} catch (Exception ex) {
			String errorDescripcio = "Error al iniciar la firma simple.";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_FIRMASIMPLE,
					accioDescripcio,
					firmaWebPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_FIRMASIMPLE, errorDescripcio, ex);
		}
	}

	public FirmaResultatDto firmaSimpleWebEnd(String transactionID) {

		String accioDescripcio = "Finalitzant firma simple";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("transactionID", transactionID);	
		long t0 = System.currentTimeMillis();
		FirmaWebPlugin firmaWebPlugin = getFirmaSimpleWebPlugin();

		try {
			FirmaResultatDto resultat = firmaWebPlugin.firmaSimpleWebEnd(transactionID);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_FIRMASIMPLE,
					accioDescripcio,
					firmaWebPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return resultat;
		} catch (Exception ex) {
			String errorDescripcio = "Error al finalitzar la firma simple.";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_FIRMASIMPLE,
					accioDescripcio,
					firmaWebPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_FIRMASIMPLE, errorDescripcio, ex);
		}
	}

	public AnotacioRegistreEntrada consultaAnotacio(AnotacioRegistreId anotacioRegistreId) {
		
		String accioDescripcio = "Consulta les dades d'una anotació";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("clauAcces", anotacioRegistreId.getClauAcces());
		accioParams.put("identificador", anotacioRegistreId.getIndetificador());
		DistribucioPlugin distribucioPlugin = getDistribucioPlugin();
		long t0 = System.currentTimeMillis();
		
		try {
			
			AnotacioRegistreEntrada resultat = distribucioPlugin.consulta(anotacioRegistreId);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_DISTRIBUCIO,
					accioDescripcio,
					distribucioPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return resultat;
		} catch (Exception ex) {
			String errorDescripcio = "Error al consulta les dades d'una anotació.";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_DISTRIBUCIO,
					accioDescripcio,
					distribucioPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_DISTRIBUCIO, errorDescripcio, ex);
		}
	}
	
	public void canviEstatAnotacio(AnotacioRegistreId anotacioRegistreId, Estat estat, String obs) {
		
		String accioDescripcio = "Canvi estat d'una anotació";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("clauAcces", anotacioRegistreId.getClauAcces());
		accioParams.put("identificador", anotacioRegistreId.getIndetificador());
		accioParams.put("estat", estat.toString());
		accioParams.put("observacions", obs);
		DistribucioPlugin distribucioPlugin = getDistribucioPlugin();
		long t0 = System.currentTimeMillis();
		
		try {
			distribucioPlugin.canviEstat(anotacioRegistreId, estat, obs);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_DISTRIBUCIO,
					accioDescripcio,
					distribucioPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al canviar estat d'una anotació.";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_DISTRIBUCIO,
					accioDescripcio,
					distribucioPlugin.getEndpointURL(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_DISTRIBUCIO, errorDescripcio, ex);
		}
	}
	
	private ArbreNodeDto<UnitatOrganitzativaDto> getNodeArbreUnitatsOrganitzatives(
			UnitatOrganitzativa unitatOrganitzativa,
			List<UnitatOrganitzativa> unitatsOrganitzatives,
			ArbreNodeDto<UnitatOrganitzativaDto> pare) {

		ArbreNodeDto<UnitatOrganitzativaDto> resposta = new ArbreNodeDto<UnitatOrganitzativaDto>(pare,
				conversioTipusHelper.convertir(
						unitatOrganitzativa,
						UnitatOrganitzativaDto.class));
		String codiUnitat = (unitatOrganitzativa != null) ? unitatOrganitzativa.getCodi() : null;
		for (UnitatOrganitzativa uo : unitatsOrganitzatives) {
			if ((codiUnitat == null && uo.getCodiUnitatSuperior() == null)
					|| (uo.getCodiUnitatSuperior() != null && uo.getCodiUnitatSuperior().equals(
							codiUnitat))) {
				resposta.addFill(
						getNodeArbreUnitatsOrganitzatives(
								uo,
								unitatsOrganitzatives,
								resposta));
			}
		}
		return resposta;
	}

	private Long toLongValue(
			String text) {
		return text == null || text.isEmpty() ? null
				: Long.parseLong(
						text);
	}

	private Expedient toArxiuExpedient(
			String identificador,
			String nom,
			MetaDadaEntity metaDada,
			Object valor,
			List<String> ntiInteressats,
			String numeroExpedient) {
		Expedient expedient = new Expedient();
		expedient.setIdentificador(identificador);
		expedient.setNom(nom);
		ExpedientMetadades metadades = new ExpedientMetadades();
		metadades.setInteressats(ntiInteressats);
		Map<String, Object> metadadesValors = new HashMap<String, Object>();
		metadadesValors.put(metaDada.getMetadadaArxiu(), valor);
		metadades.addMetadadaAddicional("metadades_expedient", metadadesValors);

		if (isPropagarNumeroExpedientActiu())
			metadades.addMetadadaAddicional(
					"numeroExpedient",
					numeroExpedient);

		expedient.setMetadades(metadades);

		return expedient;
	}

	private Expedient toArxiuExpedient(
			String identificador,
			String nom,
			String ntiIdentificador,
			List<String> ntiOrgans,
			Date ntiDataObertura,
			String ntiClassificacio,
			ExpedientEstatEnumDto ntiEstat,
			List<String> ntiInteressats,
			String serieDocumental,
			String numeroExpedient) {

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
				metadades.setEstat(
						ExpedientEstat.OBERT);
				break;
			case TANCAT:
				metadades.setEstat(
						ExpedientEstat.TANCAT);
				break;
			}
		}
		metadades.setOrgans(ntiOrgans);
		metadades.setInteressats(ntiInteressats);
		metadades.setSerieDocumental(serieDocumental);

		if (isPropagarNumeroExpedientActiu())
			metadades.addMetadadaAddicional(
					"numeroExpedient",
					numeroExpedient);

		expedient.setMetadades(metadades);
		return expedient;
	}

	private Carpeta toArxiuCarpeta(
			String identificador,
			String nom) {

		Carpeta carpeta = new Carpeta();
		carpeta.setIdentificador(
				identificador);
		carpeta.setNom(
				nom);
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
			documentNomInArxiu = documentNomInArxiu(documentEntity.getNom(), documentEntity.getPare());
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
						firmes,
						arxiuEstat));

		return documentArxiu;
	}

	/** Llistat de firmes attached */
	private static List<FirmaTipus> TIPUS_FIRMES_ATTACHED = Arrays.asList(
			FirmaTipus.CADES_ATT,
			FirmaTipus.PADES,
			FirmaTipus.XADES_ENV);

	private Document toArxiuDocument(
			String identificador,
			String nom,
			String descripcio,
			FitxerDto fitxer,
			List<ArxiuFirmaDto> firmes,
			String ntiIdentificador,
			NtiOrigen ntiOrigen,
			List<String> ntiOrgans,
			Date ntiDataCaptura,
			NtiEstadoElaboracion ntiEstadoElaboracion,
			NtiTipoDocumento ntiTipoDocumento,
			DocumentEstat estat,
			es.caib.distribucio.rest.client.integracio.domini.FirmaTipus firmaTipus,
			String metaDades) throws SistemaExternException {

		Document documentArxiu = new Document();

		documentArxiu.setNom(nom);
		documentArxiu.setDescripcio(descripcio);
		documentArxiu.setIdentificador(identificador);
		documentArxiu.setEstat(DocumentEstat.DEFINITIU);

		DocumentFirmaTipusEnumDto documentFirmaTipus = null;
		ArxiuFirmaDto primeraFirma = new ArxiuFirmaDto();

		if (firmes != null && !firmes.isEmpty()) {
			primeraFirma = firmes.get(0);
			if (primeraFirma.getTipus() != null && TIPUS_FIRMES_ATTACHED.contains(FirmaTipus.valueOf(primeraFirma.getTipus().name()))) {
				if (ArxiuFirmaTipusEnumDto.PADES.equals(primeraFirma.getTipus())) {
					documentFirmaTipus = DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA;
					// PluginArxiuFileSystem - linia 513
					firmes.get(0).setFitxerNom(fitxer.getNom());
				} else {
					documentFirmaTipus = DocumentFirmaTipusEnumDto.FIRMA_SEPARADA;
				}
			}
		} else {
			documentFirmaTipus = DocumentFirmaTipusEnumDto.SENSE_FIRMA;
		}

		setContingutIFirmes(
				documentArxiu,
				fitxer,
				documentFirmaTipus,
				firmes,
				null,
				null);

		DocumentContingut contingut = new DocumentContingut();
		if (fitxer != null) {
			contingut.setArxiuNom(fitxer.getNom());
			contingut.setContingut(fitxer.getContingut());
			contingut.setTipusMime(fitxer.getContentType());
		}
		DocumentExtensio extensio = getDocumentExtensio(
				fitxer,
				documentFirmaTipus,
				firmes);
		DocumentFormat format = getDocumentFormat(extensio);
		String serieDocumental = getPropertyPluginRegistreExpedientSerieDocumental();
		DocumentMetadades metadades = ArxiuConversions.getMetadadesArxiuDocumentAnotacio(
				ntiIdentificador,
				ntiDataCaptura,
				ntiOrigen,
				ntiEstadoElaboracion,
				ntiTipoDocumento,
				extensio,
				format,
				serieDocumental,
				ntiOrgans);

		documentArxiu.setMetadades(metadades);
		documentArxiu.setEstat(estat);
		documentArxiu.setContingut(contingut);

		return documentArxiu;
	}

	private DocumentMetadades getMetadades(
			DocumentEntity documentEntity,
			FitxerDto fitxer,
			DocumentFirmaTipusEnumDto documentFirmaTipus,
			List<ArxiuFirmaDto> firmes,
			ArxiuEstatEnumDto arxiuEstat) {

		DocumentMetadades metadades = new DocumentMetadades();

		// ============= METADADES ===============
		metadades.setOrigen(ArxiuConversions.getOrigen(documentEntity.getNtiOrigen()));
		metadades.setDataCaptura(documentEntity.getDataCaptura());
		metadades.setEstatElaboracio(ArxiuConversions.getDocumentEstatElaboracio(documentEntity.getNtiEstadoElaboracion()));
		metadades.setIdentificadorOrigen(documentEntity.getNtiIdDocumentoOrigen());
		ArxiuConversions.setTipusDocumental(
				metadades,
				documentEntity.getNtiTipoDocumental());
		DocumentExtensio extensio = getDocumentExtensio(
				fitxer,
				documentFirmaTipus,
				firmes);
		metadades.setExtensio(extensio);
		metadades.setFormat(getDocumentFormat(extensio));
		metadades.setOrgans(Arrays.asList(documentEntity.getNtiOrgano()));
		metadades.setSerieDocumental(documentEntity.getExpedient().getMetaExpedient().getSerieDocumental());

		// ========== METADADES ADDICIONALS ============
		Map<String, Object> metadadesAddicionals = new HashMap<String, Object>();
		if (getPropertyArxiuMetadadesAddicionalsActiu()) {
			if (documentEntity.getMetaDocument() != null)
				metadadesAddicionals.put(
						"tipusDocumentNom",
						documentEntity.getMetaDocument().getNom());
			metadadesAddicionals.put(
					"isImportacio",
					DocumentTipusEnumDto.IMPORTAT.equals(
							documentEntity.getDocumentTipus()));

			if (firmes != null && !firmes.isEmpty() && arxiuEstat == ArxiuEstatEnumDto.DEFINITIU) {
				metadadesAddicionals.put(
						"detallsFirma",
						firmes.get(
								0).getDetalls());
			}
		}
		metadadesAddicionals.put(
				"eni:descripcion",
				documentEntity.getDescripcio());
		if (documentEntity.getIdioma() != null) {
			metadadesAddicionals.put(
					"eni:idioma",
					documentEntity.getIdioma());
		}
		if (documentEntity.getResolucion() != null) {
			metadadesAddicionals.put(
					"eni:resolucion",
					documentEntity.getResolucion());
		}

		if (documentFirmaTipus == DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA) {
			if (firmes != null) {
				ArxiuFirmaDto primeraFirma = firmes.get(
						0);
				if (primeraFirma.getContingut() != null) {
					metadadesAddicionals.put(
							"eni:tamano_logico",
							primeraFirma.getContingut().length);
				}
			}
		} else {
			if (fitxer != null && fitxer.getContingut() != null) {
				metadadesAddicionals.put(
						"eni:tamano_logico",
						fitxer.getContingut().length);
			}
		}

		metadades.setMetadadesAddicionals(metadadesAddicionals);

		return metadades;
	}

	private DocumentExtensio getDocumentExtensio(
			FitxerDto fitxer,
			DocumentFirmaTipusEnumDto documentFirmaTipus,
			List<ArxiuFirmaDto> firmes) {
		DocumentExtensio extensio = null;

		String fitxerNom = null;
		if (documentFirmaTipus == DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA && !isModificacioCustodiatsActiva()) {
			ArxiuFirmaDto primeraFirma = firmes.get(
					0);
			fitxerNom = primeraFirma.getFitxerNom();
		}
		if (fitxerNom == null && fitxer != null) {
			fitxerNom = fitxer.getNom();
		}

		extensio = DocumentExtensio.toEnum(
				getExtensio(
						fitxerNom));

		return extensio;
	}

	private String getExtensio(
			String nom) {

		String extensio = null;
		if (nom != null) {
			int indexPunt = nom.lastIndexOf(
					".");
			if (indexPunt != -1 && indexPunt < nom.length() - 1) {
				extensio = nom.substring(
						indexPunt).toLowerCase();

			}
		}
		return extensio;
	}

	private DocumentFormat getDocumentFormat(
			DocumentExtensio extensio) {

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

	private DocumentExtensio getArxiuFormatExtensio(
			String extensio) {
		String extensioAmbPunt = (extensio.startsWith(
				".")) ? extensio.toLowerCase() : "." + extensio.toLowerCase();
		return DocumentExtensio.toEnum(
				extensioAmbPunt);
	}

	private Persona convertirAmbPersona(InteressatEntity interessat) {
		interessat = HibernateHelper.deproxy(interessat);
		Persona persona = new Persona();
		persona.setNif(interessat.getDocumentNum());
		persona.setDocumentTipus(interessat.getDocumentTipus());
		if (interessat instanceof InteressatPersonaFisicaEntity) {
			InteressatPersonaFisicaEntity interessatPf = (InteressatPersonaFisicaEntity) interessat;
			persona.setNom(interessatPf.getNom());
			persona.setLlinatge1(interessatPf.getLlinatge1());
			persona.setLlinatge2(interessatPf.getLlinatge2());
			persona.setInteressatTipus(InteressatTipusEnumDto.PERSONA_FISICA);
		} else if (interessat instanceof InteressatPersonaJuridicaEntity) {
			InteressatPersonaJuridicaEntity interessatPj = (InteressatPersonaJuridicaEntity) interessat;
			persona.setRaoSocial(interessatPj.getRaoSocial());
			persona.setInteressatTipus(InteressatTipusEnumDto.PERSONA_JURIDICA);
		} else if (interessat instanceof InteressatAdministracioEntity) {
			InteressatAdministracioEntity interessatA = (InteressatAdministracioEntity) interessat;
			persona.setInteressatTipus(InteressatTipusEnumDto.ADMINISTRACIO);
			UnitatOrganitzativaDto unitatOrganitzativaDto = unitatOrganitzativaHelper.findAmbCodi(interessatA.getOrganCodi());
			persona.setNif(unitatOrganitzativaDto.getNifCif());
			persona.setNom(unitatOrganitzativaDto.getDenominacioCooficial());
			persona.setCodiDir3(unitatOrganitzativaDto.getCodi());
		}
		persona.setTelefon(Utils.extractNumbers(interessat.getTelefon()));
		persona.setEmail(interessat.getEmail());
		persona.setIncapacitat(interessat.getIncapacitat());
		return persona;
	}

	/*
	 * private ArxiuCapsalera generarCapsaleraArxiu( ContingutEntity contingut) {
	 * ArxiuCapsalera capsaleraTest = new ArxiuCapsalera(); Authentication auth =
	 * SecurityContextHolder.getContext().getAuthentication();
	 * capsaleraTest.setFuncionariNom(auth.getName());
	 * capsaleraTest.setFuncionariOrgan( contingut.getEntitat().getUnitatArrel());
	 * return capsaleraTest; }
	 */

	private void propagarMetadadesExpedient(
			Expedient expedientArxiu,
			ExpedientEntity expedientDb) {

		List<String> metadadaOrgans = expedientArxiu.getMetadades().getOrgans();
		String organs = null;
		if (expedientArxiu.getMetadades().getOrgans() != null) {
			StringBuilder organsSb = new StringBuilder();
			boolean primer = true;
			for (String organ : metadadaOrgans) {
				organsSb.append(
						organ);
				if (primer || metadadaOrgans.size() == 1) {
					primer = false;
				} else {
					organsSb.append(
							",");
				}
			}
			organs = organsSb.toString();
		}
		expedientDb.updateNti(
				obtenirNumeroVersioEniExpedient(
						expedientArxiu.getMetadades().getVersioNti()),
				expedientArxiu.getMetadades().getIdentificador(),
				organs,
				expedientArxiu.getMetadades().getDataObertura(),
				expedientArxiu.getMetadades().getClassificacio());
	}

	private static final String ENI_EXPEDIENT_PREFIX = "http://administracionelectronica.gob.es/ENI/XSD/v";

	private String obtenirNumeroVersioEniExpedient(
			String versio) {
		if (versio != null) {
			if (versio.startsWith(
					ENI_EXPEDIENT_PREFIX)) {
				int indexBarra = versio.indexOf(
						"/",
						ENI_EXPEDIENT_PREFIX.length());
				return versio.substring(
						ENI_EXPEDIENT_PREFIX.length(),
						indexBarra);
			}
		}
		return null;
	}

	public void propagarMetadadesDocument(
			Document documentArxiu,
			DocumentEntity documentDb) {
		String organs = StringUtils.join(documentArxiu.getMetadades().getOrgans(),',');
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

	private String obtenirNumeroVersioEniDocument(
			String versio) {
		if (versio != null) {
			if (versio.startsWith(
					ENI_DOCUMENT_PREFIX)) {
				int indexBarra = versio.indexOf(
						"/",
						ENI_DOCUMENT_PREFIX.length());
				return versio.substring(
						ENI_DOCUMENT_PREFIX.length(),
						indexBarra);
			}
		}
		return null;
	}

	private Map<String, String> getAccioParamsPerPortaFirmesUpload(
			DocumentEntity document,
			String motiu,
			PortafirmesPrioritatEnum prioritat,
			Date dataCaducitat,
			String documentTipus,
			String[] responsables,
			MetaDocumentFirmaSequenciaTipusEnumDto fluxTipus,
			String fluxId,
			List<DocumentEntity> annexos) {

		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"documentId",
				document.getId().toString());
		accioParams.put(
				"documentTitol",
				document.getNom());
		accioParams.put(
				"motiu",
				motiu);
		accioParams.put(
				"prioritat",
				prioritat.toString());
		// accioParams.put(
		// "dataCaducitat",
		// new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dataCaducitat));
		accioParams.put(
				"documentTipus",
				documentTipus);
		if (responsables != null) {
			accioParams.put(
					"responsables",
					Arrays.toString(
							responsables));
		}
		if (fluxTipus != null) {
			accioParams.put(
					"fluxTipus",
					fluxTipus.toString());
		}
		if (fluxId != null) {
			accioParams.put(
					"fluxId",
					fluxId);
		}

		if (annexos != null) {
			StringBuilder annexosIds = new StringBuilder();
			StringBuilder annexosTitols = new StringBuilder();
			boolean primer = true;
			for (DocumentEntity annex : annexos) {
				if (!primer) {
					annexosIds.append(
							", ");
					annexosTitols.append(
							", ");
				}
				annexosIds.append(
						annex.getId());
				annexosTitols.append(
						annex.getNom());
				primer = false;
			}
			accioParams.put(
					"annexosIds",
					annexosIds.toString());
			accioParams.put(
					"annexosTitols",
					annexosTitols.toString());
		}
		return accioParams;
	}

	public Resum getSummarize(
			byte[] bytes,
			String contentType) {

		Resum resum = new Resum();

		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"tipus de document",
				contentType);

		// TODO: Afegir informació de monitor
		SummarizePlugin summarizePlugin = getSummarizePlugin();
		if (summarizePlugin != null && summarizePlugin.isActive()) {
			return resum;
		}

		String documentText = null;
		try {
			documentText = extractTextFromDocument(
					bytes,
					contentType);
		} catch (Exception e) {
			logger.error(
					"No s'ha pogut extreure el text del document.",
					e);
		}

		if (documentText != null) {
			// accioParams.put("text", documentText);
			try {
				// Segons tamanys de camps de BBDD ipa_document.DESCRIPCIO(512) i
				// ipa_contingut.nom(256)
				resum = summarizePlugin.getSummarize(
						documentText,
						500,
						50);
			} catch (es.caib.ripea.plugin.SistemaExternException e) {
				logger.error(
						"No s'ha pogut obtenir el resum del text.",
						e);
				resum.setError(
						e.getMessage());
			}
		}
		return resum;
	}

	public static String extractTextFromDocument(byte[] bytes, String contentType) {
		String documentText = null;
		if ("application/pdf".equalsIgnoreCase(contentType)) {
			documentText = extractTextFromPDF(bytes);
		} else if ("application/vnd.openxmlformats-officedocument.wordprocessingml.document".equalsIgnoreCase(contentType)) {
			documentText = extractTextFromDocx(bytes);
		} else if ("application/vnd.oasis.opendocument.text".equalsIgnoreCase(contentType)) {
//			documentText = extractTextFromOdt(bytes);
		}
		return documentText;
	}

	private static String extractTextFromPDF(
			byte[] bytes) throws SistemaExternException {
		String text = "";

		try (PDDocument document = PDDocument.load(
				bytes)) {
			if (!document.isEncrypted()) {
				/*
				 * PDFTextStripper pdfStripper = new PDFTextStripper() {
				 * 
				 * @Override protected void writeString(String text, List<TextPosition>
				 * textPositions) throws IOException { // Aquí puedes añadir lógica para ignorar
				 * cabeceras y pies de página if (!isHeaderOrFooter(text)) {
				 * super.writeString(text, textPositions); } }
				 * 
				 * private boolean isHeaderOrFooter(String text) { // Implementa tu lógica para
				 * identificar cabeceras y pies de página return text.contains("Header") ||
				 * text.contains("Footer"); } };
				 */
				PDFTextStripper pdfStripper = new PDFTextStripper();
				text = pdfStripper.getText(
						document);
			} else {
				throw new SistemaExternException("SUMMARIZE", "El document PDF està xifrat i no es pot llegir.");
			}
		} catch (IOException e) {
			logger.error(
					"No s'ha pogut obtenir el text del document PDF",
					e);
			throw new SistemaExternException("SUMMARIZE", "No s'ha pogut obtenir el text del document PDF", e);
		}

		return text;
	}

	private static String extractTextFromDocx(
			byte[] bytes) throws SistemaExternException {
		String text = "";

		try (ByteArrayInputStream fis = new ByteArrayInputStream(bytes);
				XWPFDocument document = new XWPFDocument(fis);
				XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
			text = extractor.getText();
		} catch (IOException e) {
			throw new SistemaExternException("SUMMARIZE", "No s'ha pogut obtenir el text del document DOCX", e);
		}
		return text;
	}
/*
	private static String extractTextFromOdt(
			byte[] bytes) throws SistemaExternException {
		StringBuilder text = new StringBuilder();

		ByteArrayInputStream bais = null;
		try {
			bais = new ByteArrayInputStream(bytes);
			ODPackage odPackage = new ODPackage(bais);
			TextDocument document = TextDocument.get(
					odPackage);

			Element rootElement = document.getContentDocument().getRootElement();
			extractTextFromElement(
					rootElement,
					text);

		} catch (Exception e) {
			throw new SistemaExternException("SUMMARIZE", "No s'ha pogut obtenir el text del document ODT", e);
		} finally {
			if (bais != null) {
				try {
					bais.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return text.toString();
	}

	@SuppressWarnings("unchecked")
	private static void extractTextFromElement(
			Element element,
			StringBuilder text) {
		List<Element> children = element.getChildren();

		for (Element child : children) {
			String name = child.getName();

			switch (name) {
			case "p":
				text.append(
						child.getText()).append(
								"\n");
				break;
			case "list":
				extractTextFromElement(
						child,
						text);
				break;
			case "list-item":
				text.append(
						"- ");
				extractTextFromElement(
						child,
						text);
				break;
			default:
				extractTextFromElement(
						child,
						text);
				break;
			}
		}
	}
*/
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
			Class<?> clazz = Class.forName( pluginClass);
			Properties props = configHelper.getGroupPropertiesGeneral(IntegracioHelper.INTCODI_USUARIS);
			dadesUsuariPlugin = (DadesUsuariPlugin) clazz.getDeclaredConstructor(
					String.class,
					Properties.class).newInstance("es.caib.ripea.plugin.dades.usuari.", props);
			return dadesUsuariPlugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_USUARIS, "Error al crear la instància del plugin de dades d'usuari", ex);
		}
	}

	private UnitatsOrganitzativesPlugin getUnitatsOrganitzativesPlugin() {

		String entitatCodi = configHelper.getEntitatActualCodi();
		if (StringUtils.isEmpty(entitatCodi)) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}
		UnitatsOrganitzativesPlugin plugin = unitatsOrganitzativesPlugins.get(
				entitatCodi);
		// loadPluginProperties("ORGANISMES");
		if (plugin != null) {
			return plugin;
		}
		String pluginClass = getPropertyPluginUnitatsOrganitzatives();
		if (StringUtils.isEmpty(
				pluginClass)) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS,
					"No està configurada la classe per al plugin d'unitats organitzatives");
		}
		try {
			Class<?> clazz = Class.forName(
					pluginClass);
			plugin = (UnitatsOrganitzativesPlugin) clazz.getDeclaredConstructor(
					String.class,
					Properties.class).newInstance(
							ConfigDto.prefix + ".",
							configHelper.getGroupPropertiesEntitatOrGeneral(
									IntegracioHelper.INTCODI_UNITATS,
									entitatCodi));
			unitatsOrganitzativesPlugins.put(
					entitatCodi,
					plugin);
			return plugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS,
					"Error al crear la instància del plugin d'unitats organitzatives", ex);
		}
	}

	public IArxiuPluginWrapper getArxiuPlugin() {
        return getArxiuPlugin(configHelper.getEntitatActualCodi());
    }
	
	public IArxiuPluginWrapper getArxiuPlugin(String entitatCodi) {
		if (entitatCodi == null) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}
        String organCodi = configHelper.getOrganActualCodi();
        return getArxiuPlugin(entitatCodi, organCodi);
	}
	
    public IArxiuPluginWrapper getArxiuPlugin(String entitatCodi, String organCodi) {

		IArxiuPluginWrapper plugin = null;
		if (organCodi != null) {
			
			plugin = arxiuPlugins.get(entitatCodi + "." + organCodi);
			if (plugin != null) { return plugin; }
			
			String pluginClassOrgan = configHelper.getValueForOrgan(entitatCodi, organCodi, PropertyConfig.ARXIU_PLUGIN_CLASS);
			
			if (StringUtils.isNotEmpty(pluginClassOrgan)) {
				try {
					Class<?> clazz = Class.forName(
							pluginClassOrgan);
					Properties propiedades = configHelper.getGroupPropertiesOrganOrEntitatOrGeneral(
							IntegracioHelper.INTCODI_ARXIU,
							entitatCodi,
							organCodi);
					IArxiuPlugin pluginInstance = (IArxiuPlugin) clazz.getDeclaredConstructor(
							String.class,
							Properties.class).newInstance(
									ConfigDto.prefix + ".",
									propiedades);
					plugin = new IArxiuPluginWrapper(
							pluginInstance, 
							Utils.getEndpointNameFromProperties(propiedades));
					arxiuPlugins.put(entitatCodi + "." + organCodi, plugin);
					return plugin;
				} catch (Exception ex) {
					throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU,
							"Error al crear la instància del plugin d'arxiu digital (" + organCodi + ")", ex);
				}
			}
		}

		// ENTITAT/GENERAL PLUGIN
		plugin = arxiuPlugins.get(
				entitatCodi);
		if (plugin != null) {
			return plugin;
		}
		String pluginClass = getPropertyPluginArxiu();
		if (StringUtils.isEmpty(pluginClass)) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU,
					"No està configurada la classe per al plugin d'arxiu digital");
		}
		try {
			Class<?> clazz = Class.forName(
					pluginClass);
			Properties propiedades = configHelper.getGroupPropertiesEntitatOrGeneral(
					IntegracioHelper.INTCODI_ARXIU,
					entitatCodi);
			IArxiuPlugin pluginInstance = (IArxiuPlugin) clazz.getDeclaredConstructor(
					String.class,
					Properties.class).newInstance(
							ConfigDto.prefix + ".",
							propiedades);
			plugin = new IArxiuPluginWrapper(
					pluginInstance,
					Utils.getEndpointNameFromProperties(propiedades));
			arxiuPlugins.put(entitatCodi + "." + organCodi, plugin);
			return plugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU,
					"Error al crear la instància del plugin d'arxiu digital", ex);
		}
	}

    private PortafirmesPlugin getPortafirmesPlugin(String entitatCodi, String organCodi) {
    	
		PortafirmesPlugin plugin = null;		
		if (cacheHelper.mostrarLogsIntegracio()) {
			logger.info(
					"[PFI] Obtenint plugin de portafirmes amb Entitat: '" + entitatCodi + "' i Organ: '"
							+ (organCodi != null ? organCodi : "null") + "'");
		}

		if (organCodi != null) {
			
			plugin = portafirmesPlugins.get(entitatCodi + "." + organCodi);
			if (plugin != null) { return plugin; }
			
			String pluginClassOrgan = configHelper.getValueForOrgan(entitatCodi, organCodi, PropertyConfig.PORTAFIB_PLUGIN_CLASS);
			if (StringUtils.isNotEmpty(
					pluginClassOrgan)) {
				try {
					Class<?> clazz = Class.forName(
							pluginClassOrgan);
					plugin = (PortafirmesPlugin) clazz.getDeclaredConstructor(
							String.class,
							Properties.class).newInstance(
									ConfigDto.prefix + ".",
									configHelper.getGroupPropertiesOrganOrEntitatOrGeneral(
											IntegracioHelper.INTCODI_PFIRMA,
											entitatCodi,
											organCodi));
					portafirmesPlugins.put(
							entitatCodi + "." + organCodi,
							plugin);
					return plugin;
				} catch (Exception ex) {
					throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA,
							"Error al crear la instància del plugin de portafirmes (" + organCodi + ")", ex);
				}
			}
		}

		plugin = portafirmesPlugins.get(entitatCodi);
		if (plugin != null) { return plugin; }
		
		String pluginClass = getPropertyPluginPortafirmes();
		
		if (StringUtils.isEmpty(pluginClass)) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA,
					"No està configurada la classe per al plugin de portafirmes");
		}
		try {
			Class<?> clazz = Class.forName(
					pluginClass);
			plugin = (PortafirmesPlugin) clazz.getDeclaredConstructor(
					String.class,
					Properties.class).newInstance(
							ConfigDto.prefix + ".",
							configHelper.getGroupPropertiesEntitatOrGeneral(
									IntegracioHelper.INTCODI_PFIRMA,
									entitatCodi));
			portafirmesPlugins.put(
					entitatCodi,
					plugin);
			return plugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_PFIRMA,
					"Error al crear la instància del plugin de portafirmes", ex);
		}
	}
    
	private PortafirmesPlugin getPortafirmesPlugin() {
		String entitatCodi = configHelper.getEntitatActualCodi();
		if (entitatCodi == null) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}
		String organCodi = configHelper.getOrganActualCodi();
		
		return getPortafirmesPlugin(entitatCodi, organCodi);
	}

	private ConversioPlugin getConversioPlugin() {
		String entitatCodi = configHelper.getEntitatActualCodi();
		if (entitatCodi == null) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}
		String organCodi = configHelper.getOrganActualCodi();
		return getConversioPlugin(entitatCodi, organCodi);
	}
	
	private ConversioPlugin getConversioPlugin(String entitatCodi, String organCodi) {

		ConversioPlugin plugin = null;
		
		if (organCodi != null) {			
			plugin = conversioPlugins.get(entitatCodi + "." + organCodi);
			if (plugin != null) { return plugin; }
			
			String pluginClassOrgan = configHelper.getValueForOrgan(entitatCodi, organCodi, PropertyConfig.CONVERSIO_PLUGIN_CLASS);
			
			if (StringUtils.isNotEmpty(pluginClassOrgan)) {
				
				try {
					Class<?> clazz = Class.forName(
							pluginClassOrgan);
					plugin = (ConversioPlugin) clazz.getDeclaredConstructor(
							String.class,
							Properties.class).newInstance(
									ConfigDto.prefix + ".",
									configHelper.getGroupPropertiesOrganOrEntitatOrGeneral(
											IntegracioHelper.INTCODI_CONVERT,
											entitatCodi,
											organCodi));
					conversioPlugins.put(
							entitatCodi + "." + organCodi,
							plugin);
					return plugin;
				} catch (Exception ex) {
					throw new SistemaExternException(IntegracioHelper.INTCODI_CONVERT,
							"Error al crear la instància del plugin de conversió de documents (" + organCodi + ")", ex);
				}
			}
		}

		// ENTITAT/GENERAL PLUGIN
		plugin = conversioPlugins.get(
				entitatCodi);
		if (plugin != null) {
			return plugin;
		}
		String pluginClass = getPropertyPluginConversio();
		if (StringUtils.isEmpty(pluginClass)) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_CONVERT,
					"No està configurada la classe per al plugin de conversió de documents");
		}
		try {
			Class<?> clazz = Class.forName(
					pluginClass);
			plugin = (ConversioPlugin) clazz.getDeclaredConstructor(
					String.class,
					Properties.class).newInstance(
							ConfigDto.prefix + ".",
							configHelper.getGroupPropertiesEntitatOrGeneral(
									IntegracioHelper.INTCODI_CONVERT,
									entitatCodi));
			conversioPlugins.put(
					entitatCodi,
					plugin);
			return plugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_CONVERT,
					"Error al crear la instància del plugin de conversió de documents", ex);
		}
	}

	private DigitalitzacioPlugin getDigitalitzacioPlugin() {
		String entitatCodi = configHelper.getEntitatActualCodi();
		if (entitatCodi == null) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}
		String organCodi = configHelper.getOrganActualCodi();
		return getDigitalitzacioPlugin(entitatCodi, organCodi);
	}
	
	private DigitalitzacioPlugin getDigitalitzacioPlugin(String entitatCodi, String organCodi) {

		DigitalitzacioPlugin plugin = digitalitzacioPlugins.get(entitatCodi);
		if (plugin != null) { return plugin; }
		
		String pluginClass = getPropertyPluginDigitalitzacio();
		if (StringUtils.isEmpty(pluginClass)) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_DIGITALITZACIO,
					"No està configurada la classe per al plugin de digitalització");
		}
		
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (DigitalitzacioPlugin) clazz.getDeclaredConstructor(
					String.class,
					Properties.class).newInstance(
							ConfigDto.prefix + ".",
							configHelper.getGroupPropertiesEntitatOrGeneral(
									IntegracioHelper.INTCODI_DIGITALITZACIO,
									entitatCodi));
			digitalitzacioPlugins.put(
					entitatCodi,
					plugin);
			return plugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_DIGITALITZACIO,
					"Error al crear la instància del plugin de digitalització", ex);
		}
	}

	private Map<String, String> getNotificacioAccioParams(
			DocumentNotificacioDto notificacio,
			ExpedientEntity expedientEntity,
			DocumentEntity documentEntity,
			InteressatEntity interessat) {

		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put(
				"setEmisorDir3Codi",
				expedientEntity.getEntitat().getUnitatArrel());
		accioParams.put(
				"expedientId",
				expedientEntity.getId().toString());
		accioParams.put(
				"expedientTitol",
				expedientEntity.getNom());
		accioParams.put(
				"expedientTipusId",
				expedientEntity.getMetaNode().getId().toString());
		accioParams.put(
				"expedientTipusNom",
				expedientEntity.getMetaNode().getNom());
		accioParams.put(
				"documentNom",
				documentEntity.getNom());
		String intressatsString = "";
		intressatsString += interessat.getIdentificador();
		accioParams.put(
				"interessats",
				intressatsString);
		if (notificacio.getTipus() != null) {
			accioParams.put(
					"enviamentTipus",
					notificacio.getTipus().name());
		}
		accioParams.put(
				"concepte",
				notificacio.getAssumpte());
		accioParams.put(
				"descripcio",
				notificacio.getObservacions());
		if (notificacio.getDataProgramada() != null) {
			accioParams.put(
					"dataProgramada",
					notificacio.getDataProgramada().toString());
		}
		if (notificacio.getRetard() != null) {
			accioParams.put(
					"retard",
					notificacio.getRetard().toString());
		}
		if (notificacio.getDataCaducitat() != null) {
			accioParams.put(
					"dataCaducitat",
					notificacio.getDataCaducitat().toString());
		}
		return accioParams;
	}

	private DadesExternesPlugin getDadesExternesPlugin() {
		String entitatCodi = configHelper.getEntitatActualCodi();
		if (entitatCodi == null) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}
		String organCodi = configHelper.getOrganActualCodi();
		return getDadesExternesPlugin(entitatCodi, organCodi);
	}
	
	private DistribucioPlugin getDistribucioPlugin() {
		String entitatCodi = configHelper.getEntitatActualCodi();
		if (entitatCodi == null) {
			entitatCodi = "_BACKGROUND_";
		}
		String organCodi = configHelper.getOrganActualCodi();
		return getDistribucioPlugin(entitatCodi, organCodi);
	}
	
	private DistribucioPlugin getDistribucioPlugin(String entitatCodi, String organCodi) {

		DistribucioPlugin plugin = distribucioPlugins.get(entitatCodi);
		if (plugin != null) { return plugin; }
		
		String pluginClass = getPropertyPluginDistribucioClass();
		
		if (StringUtils.isEmpty(pluginClass)) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_DISTRIBUCIO, "No està configurada la classe per al plugin de distribucio");
		}
		
		try {
			Class<?> clazz = Class.forName(pluginClass);
			
			Properties props = null;
			if (entitatCodi==null || entitatCodi.equals("_BACKGROUND_")) {
				props = configHelper.getGroupPropertiesGeneral(IntegracioHelper.INTCODI_DISTRIBUCIO);
			} else {
				props = configHelper.getGroupPropertiesEntitatOrGeneral(IntegracioHelper.INTCODI_DISTRIBUCIO, entitatCodi);
			}
			
			plugin = (DistribucioPlugin) clazz.getDeclaredConstructor(String.class, Properties.class).newInstance(ConfigDto.prefix + ".", props);
			distribucioPlugins.put(entitatCodi,plugin);
			return plugin;

		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_DISTRIBUCIO, "Error al crear la instància del plugin de consulta de distribucio", ex);
		}
	}
	
	private DadesExternesPlugin getDadesExternesPlugin(String entitatCodi, String organCodi) {

		DadesExternesPlugin plugin = dadesExternesPlugins.get(entitatCodi);
		if (plugin != null) { return plugin; }
		
		String pluginClass = getPropertyPluginDadesExternes();
		
		if (StringUtils.isEmpty(pluginClass)) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_DADESEXT,
					"No està configurada la classe per al plugin de dades externes");
		}
		
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (DadesExternesPlugin) clazz.getDeclaredConstructor(
					String.class,
					Properties.class).newInstance(
							ConfigDto.prefix + ".",
							configHelper.getGroupPropertiesEntitatOrGeneral(
									Arrays.asList(
											"DADES_EXT",
											"ORGANISMES"),
									entitatCodi));
			dadesExternesPlugins.put(entitatCodi,plugin);
			return plugin;

		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_DADESEXT,
					"Error al crear la instància del plugin de consulta de dades externes", ex);
		}
	}

	private DadesExternesPlugin getDadesExternesPinbalPlugin() {

		String entitatCodi = configHelper.getEntitatActualCodi();
		if (entitatCodi == null) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}
		DadesExternesPlugin plugin = dadesExternesPinbalPlugins.get(entitatCodi);
		if (plugin != null) {
			return plugin;
		}
		String pluginClass = getPropertyPluginDadesExternesPinbal();
		if (StringUtils.isEmpty(pluginClass)) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_DADESEXT,
					"No està configurada la classe per al plugin de dades externes per a consultes a PINBAL");
		}
		try {
			Class<?> clazz = Class.forName(
					pluginClass);
			plugin = (DadesExternesPlugin) clazz.getDeclaredConstructor(
					String.class,
					Properties.class).newInstance(
							ConfigDto.prefix + ".",
							configHelper.getGroupPropertiesEntitatOrGeneral(
									"DADES_EXT_PINBAL",
									entitatCodi));
			dadesExternesPinbalPlugins.put(
					entitatCodi,
					plugin);
			return plugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_DADESEXT,
					"Error al crear la instància del plugin de consulta de dades externes per a consultes a PINBAL",
					ex);
		}
	}

	private IValidateSignaturePluginWrapper getValidaSignaturaPlugin() {
		String entitatCodi = configHelper.getEntitatActualCodi();
		if (entitatCodi == null) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}
		String organCodi = configHelper.getOrganActualCodi();
		return getValidaSignaturaPlugin(entitatCodi, organCodi);
	}
	
	private IValidateSignaturePluginWrapper getValidaSignaturaPlugin(String entitatCodi, String organCodi) {

		IValidateSignaturePluginWrapper plugin = null;
		
		if (organCodi != null) {
			plugin = validaSignaturaPlugins.get(entitatCodi + "." + organCodi);
			if (plugin != null) { return plugin; }
			
			String pluginClassOrgan = configHelper.getValueForOrgan(entitatCodi, organCodi, PropertyConfig.VALIDA_FIRMA_PLUGIN_CLASS);
			
			if (StringUtils.isNotEmpty(pluginClassOrgan)) {
				
				try {
					Class<?> clazz = Class.forName(pluginClassOrgan);
					Properties propiedades = configHelper.getGroupPropertiesOrganOrEntitatOrGeneral(
							IntegracioHelper.INTCODI_VALIDASIG,
							entitatCodi,
							organCodi);
					IValidateSignaturePlugin pluginInstance = (IValidateSignaturePlugin) clazz.getDeclaredConstructor(
							String.class,
							Properties.class).newInstance(ConfigDto.prefix + ".", propiedades);
					plugin = new IValidateSignaturePluginWrapper(pluginInstance, Utils.getEndpointNameFromProperties(propiedades));
					validaSignaturaPlugins.put(entitatCodi + "." + organCodi, plugin);
					return plugin;
				} catch (Exception ex) {
					throw new SistemaExternException(IntegracioHelper.INTCODI_VALIDASIG,
							"Error al crear la instància del plugin de validació de signatures (" + organCodi + ")",
							ex);
				}
			}
		}

		// ENTITAT/GENERAL PLUGIN
		plugin = validaSignaturaPlugins.get(
				entitatCodi);
		// loadPluginProperties("VALIDATE_SIGNATURE");
		if (plugin != null) {
			return plugin;
		}
		String pluginClass = getPropertyPluginValidaSignatura();
		if (Strings.isNullOrEmpty(
				pluginClass)) {
			return null;
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			Properties propiedades = configHelper.getGroupPropertiesEntitatOrGeneral(
					IntegracioHelper.INTCODI_VALIDASIG,
					entitatCodi);
			IValidateSignaturePlugin pluginInstance = (IValidateSignaturePlugin) clazz.getDeclaredConstructor(
					String.class,
					Properties.class).newInstance(ConfigDto.prefix + ".", propiedades);
			plugin = new IValidateSignaturePluginWrapper(pluginInstance, Utils.getEndpointNameFromProperties(propiedades));
			validaSignaturaPlugins.put(entitatCodi + "." + organCodi, plugin);
			return plugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_VALIDASIG,
					"Error al crear la instància del plugin de validació de signatures", ex);
		}
	}

	private NotificacioPlugin getNotificacioPlugin() {
		String entitatCodi = configHelper.getEntitatActualCodi();
		if (entitatCodi == null) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}
		String organCodi = configHelper.getOrganActualCodi();
		return getNotificacioPlugin(entitatCodi, organCodi);
	}
	
	private NotificacioPlugin getNotificacioPlugin(String entitatCodi, String organCodi) {

		NotificacioPlugin plugin = null;

		if (organCodi != null) {
			
			plugin = notificacioPlugins.get(entitatCodi + "." + organCodi);
			if (plugin != null) { return plugin; }
			
			String pluginClassOrgan = configHelper.getValueForOrgan(entitatCodi, organCodi, PropertyConfig.NOTIB_PLUGIN_CLASS);
			
			if (StringUtils.isNotEmpty(pluginClassOrgan)) {
				
				try {
					Class<?> clazz = Class.forName(pluginClassOrgan);
					Properties propsPluingNotib = configHelper.getGroupPropertiesOrganOrEntitatOrGeneral(
							IntegracioHelper.INTCODI_NOTIFICACIO,
							entitatCodi,
							organCodi);
					propsPluingNotib.setProperty(PropertyConfig.ENTORN, configHelper.getConfig(PropertyConfig.ENTORN));
					plugin = (NotificacioPlugin) clazz.getDeclaredConstructor(
							String.class,
							Properties.class).newInstance(ConfigDto.prefix + ".", propsPluingNotib);
					notificacioPlugins.put(entitatCodi + "." + organCodi, plugin);
					return plugin;
				} catch (Exception ex) {
					throw new SistemaExternException(IntegracioHelper.INTCODI_NOTIFICACIO,
							"Error al crear la instància del plugin de notificació (" + organCodi + ")", ex);
				}
			}
		}

		plugin = notificacioPlugins.get(entitatCodi);
		if (plugin != null) {
			return plugin;
		}
		
		String pluginClass = getPropertyPluginNotificacio();
		if (StringUtils.isEmpty(pluginClass)) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_NOTIFICACIO,
					"No està configurada la classe per al plugin de notificació");
		}
		
		try {
			Class<?> clazz = Class.forName(pluginClass);
			Properties propsPluingNotib = configHelper.getGroupPropertiesEntitatOrGeneral(IntegracioHelper.INTCODI_NOTIFICACIO, entitatCodi);
			propsPluingNotib.setProperty(PropertyConfig.ENTORN, configHelper.getConfig(PropertyConfig.ENTORN));
			plugin = (NotificacioPlugin) clazz.getDeclaredConstructor(
					String.class,
					Properties.class).newInstance(ConfigDto.prefix + ".", propsPluingNotib);
			notificacioPlugins.put(
					entitatCodi,
					plugin);
			return plugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_NOTIFICACIO,
					"Error al crear la instància del plugin de notificació", ex);
		}
	}

	private FirmaServidorPlugin getFirmaServidorPlugin() {
		String entitatCodi = configHelper.getEntitatActualCodi();
		if (entitatCodi == null) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}
		String organCodi = configHelper.getOrganActualCodi();
		return getFirmaServidorPlugin(entitatCodi, organCodi);
	}
	
	private FirmaServidorPlugin getFirmaServidorPlugin(String entitatCodi, String organCodi) {

		FirmaServidorPlugin plugin = null;

		if (organCodi != null) {
			plugin = firmaServidorPlugins.get(
					entitatCodi + "." + organCodi);
			if (plugin != null) {
				return plugin;
			}
			String pluginClassOrgan = configHelper.getValueForOrgan(entitatCodi, organCodi, PropertyConfig.FIRMA_SERV_PLUGIN_CLASS);
			if (StringUtils.isNotEmpty(
					pluginClassOrgan)) {
				try {
					Class<?> clazz = Class.forName(
							pluginClassOrgan);
					plugin = (FirmaServidorPlugin) clazz.getDeclaredConstructor(
							String.class,
							Properties.class).newInstance(
									ConfigDto.prefix + ".",
									configHelper.getGroupPropertiesOrganOrEntitatOrGeneral(
											IntegracioHelper.INTCODI_FIRMASERV,
											entitatCodi,
											organCodi));
					firmaServidorPlugins.put(
							entitatCodi + "." + organCodi,
							plugin);
					return plugin;
				} catch (Exception ex) {
					throw new SistemaExternException(IntegracioHelper.INTCODI_FIRMASERV,
							"Error al crear la instància del plugin de firma en servidor (" + organCodi + ")", ex);
				}
			}
		}

		plugin = firmaServidorPlugins.get(entitatCodi);
		if (plugin != null) {
			return plugin;
		}
		String pluginClass = getPropertyPluginFirmaServidor();
		if (StringUtils.isEmpty(pluginClass)) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_FIRMASERV,
					"No està configurada la classe per al plugin de firma en servidor");
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (FirmaServidorPlugin) clazz.getDeclaredConstructor(
					String.class,
					Properties.class).newInstance(
							ConfigDto.prefix + ".",
							configHelper.getGroupPropertiesEntitatOrGeneral(
									IntegracioHelper.INTCODI_FIRMASERV,
									entitatCodi));
			firmaServidorPlugins.put(entitatCodi,plugin);
			return plugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_FIRMASERV,
					"Error al crear la instància del plugin de firma en servidor", ex);
		}
	}

	private ViaFirmaPlugin getViaFirmaPlugin() {
		String entitatCodi = configHelper.getEntitatActualCodi();
		if (entitatCodi == null) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}
		String organCodi = configHelper.getOrganActualCodi();
		return getViaFirmaPlugin(entitatCodi, organCodi);
	}
	
	private ViaFirmaPlugin getViaFirmaPlugin(String entitatCodi, String organCodi) {

		ViaFirmaPlugin plugin = viaFirmaPlugins.get(entitatCodi);

		boolean viaFirmaPluginConfiguracioProvada = false;
		if (plugin != null || viaFirmaPluginConfiguracioProvada) {
			return plugin;
		}
		viaFirmaPluginConfiguracioProvada = true;
		
		String pluginClass = getPropertyPluginViaFirma();
		if (StringUtils.isEmpty(pluginClass)) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_VIAFIRMA,
					"La classe del plugin de via firma no està configurada");
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (ViaFirmaPlugin) clazz.getDeclaredConstructor(
					String.class,
					Properties.class).newInstance(
							ConfigDto.prefix + ".",
							configHelper.getGroupPropertiesEntitatOrGeneral(
									IntegracioHelper.INTCODI_VIAFIRMA,
									entitatCodi));
			viaFirmaPlugins.put(entitatCodi,plugin);
			return plugin;

		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_VIAFIRMA,
					"Error al crear la instància del plugin de via firma", ex);
		}
	}

	private ProcedimentPlugin getProcedimentPlugin() {
		String entitatCodi = configHelper.getEntitatActualCodi();
		if (entitatCodi == null) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}
		String organCodi = configHelper.getOrganActualCodi();
		return getProcedimentPlugin(entitatCodi, organCodi);
	}
	
	private ProcedimentPlugin getProcedimentPlugin(String entitatCodi, String organCodi) {

		ProcedimentPlugin procedimentPlugin = procedimentPlugins.get(entitatCodi);
		if (procedimentPlugin != null) { return procedimentPlugin; }
		
		String pluginClass = getPropertyPluginProcediment();
		
		if (StringUtils.isEmpty(pluginClass)) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_PROCEDIMENT,
					"No està configurada la classe per al plugin de procediments");
		}
		
		try {
			Class<?> clazz = Class.forName(pluginClass);
			procedimentPlugin = (ProcedimentPlugin) clazz.getDeclaredConstructor(
					String.class,
					Properties.class).newInstance(
							ConfigDto.prefix + ".",
							configHelper.getGroupPropertiesEntitatOrGeneral(
									IntegracioHelper.INTCODI_PROCEDIMENT,
									entitatCodi));
			procedimentPlugins.put(entitatCodi,procedimentPlugin);
			return procedimentPlugin;
			
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_PROCEDIMENT,
					"Error al crear la instància del plugin de procediments", ex);
		}
	}
	
	private GestioDocumentalPlugin getGestioDocumentalPlugin() {
		String entitatCodi = configHelper.getEntitatActualCodi();
		if (entitatCodi == null) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}
		String organCodi = configHelper.getOrganActualCodi();
		
		return getGestioDocumentalPlugin(entitatCodi, organCodi);
	}
	
	private GestioDocumentalPlugin getGestioDocumentalPlugin(String entitatCodi, String organCodi) {

		GestioDocumentalPlugin plugin = gestioDocumentalPlugins.get(entitatCodi);
		if (plugin != null) { return plugin; }
		
		String pluginClass = getPropertyPluginGestioDocumental();
		if (StringUtils.isEmpty(pluginClass)) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESDOC, "La classe del plugin de gestió documental no està configurada");
		}
		
		try {
			Class<?> clazz = Class.forName(
					pluginClass);
			plugin = (GestioDocumentalPlugin) clazz.getDeclaredConstructor(
					String.class,
					Properties.class).newInstance(
							ConfigDto.prefix + ".",
							configHelper.getGroupPropertiesEntitatOrGeneral(
									IntegracioHelper.INTCODI_GESDOC,
									entitatCodi));
			gestioDocumentalPlugins.put(
					entitatCodi,
					plugin);
			return plugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESDOC,
					"Error al crear la instància del plugin de gestió documental", ex);
		}
	}

	private FirmaWebPlugin getFirmaSimpleWebPlugin() {
		String entitatCodi = configHelper.getEntitatActualCodi();
		if (entitatCodi == null) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}
		String organCodi = configHelper.getOrganActualCodi();
		
		return getFirmaSimpleWebPlugin(entitatCodi, organCodi);
	}
	
	private FirmaWebPlugin getFirmaSimpleWebPlugin(String entitatCodi, String organCodi) {

		FirmaWebPlugin plugin = null;
		
		if (organCodi != null) {
			plugin = firmaSimpleWebPlugins.get(
					entitatCodi + "." + organCodi);
			if (plugin != null) {
				return plugin;
			}
			String pluginClassOrgan = configHelper.getValueForOrgan(entitatCodi, organCodi, PropertyConfig.PORTAFIB_PLUGIN_FIRMAWEB_CLASS);
			if (Utils.isNotEmpty(
					pluginClassOrgan)) {
				try {
					Class<?> clazz = Class.forName(
							pluginClassOrgan);
					plugin = (FirmaWebPlugin) clazz.getDeclaredConstructor(
							String.class,
							Properties.class).newInstance(
									ConfigDto.prefix + ".",
									configHelper.getGroupPropertiesOrganOrEntitatOrGeneral(
											IntegracioHelper.INTCODI_FIRMASIMPLE,
											entitatCodi,
											organCodi));
					firmaSimpleWebPlugins.put(
							entitatCodi + "." + organCodi,
							plugin);
					return plugin;
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}
		}

		// ENTITAT/GENERAL PLUGIN
		plugin = firmaSimpleWebPlugins.get(
				entitatCodi);
		if (plugin != null) {
			return plugin;
		}
		String pluginClass = getPropertyPluginFirmaWeb();
		if (Utils.isEmpty(
				pluginClass)) {
			throw new RuntimeException("No està configurada la classe per al plugin de firma simple web");
		}
		try {
			Class<?> clazz = Class.forName(
					pluginClass);
			plugin = (FirmaWebPlugin) clazz.getDeclaredConstructor(
					String.class,
					Properties.class).newInstance(
							ConfigDto.prefix + ".",
							configHelper.getGroupPropertiesEntitatOrGeneral(
									IntegracioHelper.INTCODI_FIRMASIMPLE,
									entitatCodi));
			firmaSimpleWebPlugins.put(
					entitatCodi,
					plugin);
			return plugin;
		} catch (Exception ex) {
			throw new RuntimeException("Error al crear la instància del plugin de firma simple web", ex);
		}

	}

	private SummarizePlugin getSummarizePlugin() {

		String entitatCodi = configHelper.getEntitatActualCodi();
		if (entitatCodi == null) {
			throw new RuntimeException("El codi d'entitat actual no pot ser nul");
		}

		SummarizePlugin plugin = null;
		// ORGAN PLUGIN
		String organCodi = configHelper.getOrganActualCodi();
		if (organCodi != null) {
			plugin = summarizePlugins.get(
					entitatCodi + "." + organCodi);
			if (plugin != null) {
				return plugin;
			}
			String pluginClassOrgan = configHelper.getValueForOrgan(entitatCodi, organCodi, PropertyConfig.SUMMARIZE_PLUGIN_CLASS);
			if (Utils.isNotEmpty(
					pluginClassOrgan)) {
				try {
					Class<?> clazz = Class.forName(
							pluginClassOrgan);
					plugin = (SummarizePlugin) clazz.getDeclaredConstructor(
							String.class,
							Properties.class).newInstance(
									"es.caib.ripea.",
									configHelper.getGroupPropertiesEntitatOrGeneral(
											IntegracioHelper.INTCODI_SUMMARIZE,
											entitatCodi));
					summarizePlugins.put(
							entitatCodi + "." + organCodi,
							plugin);
					return plugin;
				} catch (Exception ex) {
					throw new SistemaExternException(IntegracioHelper.INTCODI_SUMMARIZE,
							"Error al crear la instància del plugin de servidor de IA.", ex);
				}
			}
		}

		// ENTITAT/GENERAL PLUGIN
		plugin = summarizePlugins.get(
				entitatCodi);
		if (plugin != null) {
			return plugin;
		}
		String pluginClass = getPropertyPluginSummarize();
		if (Utils.isEmpty(
				pluginClass)) {
			throw new RuntimeException("No està configurada la classe per al plugin de Summarize");
		}
		try {
			Class<?> clazz = Class.forName(
					pluginClass);
			plugin = (SummarizePlugin) clazz.getDeclaredConstructor(
					String.class,
					Properties.class).newInstance(
							"es.caib.ripea.",
							configHelper.getGroupPropertiesEntitatOrGeneral(
									IntegracioHelper.INTCODI_SUMMARIZE,
									entitatCodi));
			summarizePlugins.put(
					entitatCodi,
					plugin);
			return plugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_SUMMARIZE,
					"Error al crear la instància del plugin de servidor de IA.", ex);
		}
	}

	private final static Map<String, Boolean> propertiesLoaded = new HashMap<>();

	private synchronized void loadPluginProperties(String codeProperties) {
		if (!propertiesLoaded.containsKey(codeProperties) || !propertiesLoaded.get(codeProperties)) {
			propertiesLoaded.put(codeProperties, true);
			Properties pluginProps = configHelper.getPropertiesByGroup(codeProperties);
			for (Map.Entry<Object, Object> entry : pluginProps.entrySet()) {
				String value = entry.getValue() == null ? "" : (String) entry.getValue();
				PropertiesHelper.getProperties().setProperty(
						(String) entry.getKey(),
						value);
			}
		}
	}

	/**
	 * Esborra les properties del grup indicat per paràmetre de la memòria.
	 *
	 * @param codeProperties
	 *            Codi del grup de propietats que vols esborrar de memòria.
	 */
	public void reloadProperties(
			String codeProperties) {
		if (propertiesLoaded.containsKey(
				codeProperties))
			propertiesLoaded.put(
					codeProperties,
					false);
	}

	public void resetPlugins() {
		resetPlugins(
				"xx");
	}

	public void resetPlugins(
			String pluginCode) {
		if ("ax".equals(
				pluginCode)
				|| "xx".equals(
						pluginCode)) {
			arxiuPlugins = new HashMap<>();
		}
		if ("di".equals(
				pluginCode)
				|| "xx".equals(
						pluginCode)) {
			unitatsOrganitzativesPlugins = new HashMap<>();
		}
		if ("no".equals(
				pluginCode)
				|| "xx".equals(
						pluginCode)) {
			notificacioPlugins = new HashMap<>();
		}
		if ("cd".equals(
				pluginCode)
				|| "xx".equals(
						pluginCode)) {
			conversioPlugins = new HashMap<>();
		}
		if ("us".equals(
				pluginCode)
				|| "xx".equals(
						pluginCode)) {
			dadesUsuariPlugin = null;
		}
		if ("pi".equals(
				pluginCode)
				|| "xx".equals(
						pluginCode)) {
			dadesExternesPinbalPlugins = new HashMap<>();
		}
		if ("de".equals(
				pluginCode)
				|| "xx".equals(
						pluginCode)) {
			dadesExternesPlugins = new HashMap<>();
		}
		if ("ro".equals(
				pluginCode)
				|| "xx".equals(
						pluginCode)) {
			procedimentPlugins = new HashMap<>();
		}
		if ("dg".equals(
				pluginCode)
				|| "xx".equals(
						pluginCode)) {
			digitalitzacioPlugins = new HashMap<>();
		}
		if ("pf".equals(
				pluginCode)
				|| "xx".equals(
						pluginCode)) {
			portafirmesPlugins = new HashMap<>();
		}
		if ("vf".equals(
				pluginCode)
				|| "xx".equals(
						pluginCode)) {
			validaSignaturaPlugins = new HashMap<>();
		}
		if ("gd".equals(
				pluginCode)
				|| "xx".equals(
						pluginCode)) {
			gestioDocumentalPlugins = new HashMap<>();
		}
		if ("fs".equals(
				pluginCode)
				|| "xx".equals(
						pluginCode)) {
			firmaServidorPlugins = new HashMap<>();
		}
		if ("si".equals(
				pluginCode)
				|| "xx".equals(
						pluginCode)) {
			firmaSimpleWebPlugins = new HashMap<>();
		}
		if ("vi".equals(
				pluginCode)
				|| "xx".equals(
						pluginCode)) {
			viaFirmaPlugins = new HashMap<>();
		}
	}

	private String getPropertyPluginDadesUsuari() {
		return configHelper.getConfig(PropertyConfig.USUARIS_PLUGIN_CLASS);
	}

	private String getPropertyPluginUnitatsOrganitzatives() {
		return configHelper.getConfig(PropertyConfig.DIR3_PLUGIN_CLASS);
	}

	private String getPropertyPluginArxiu() {
		return configHelper.getConfig(PropertyConfig.ARXIU_PLUGIN_CLASS);
	}

	private String getPropertyPluginPortafirmes() {
		return configHelper.getConfig(PropertyConfig.PORTAFIB_PLUGIN_CLASS);
	}

	private String getPropertyPluginDigitalitzacio() {
		return configHelper.getConfig(PropertyConfig.DIGITALITZACIO_PLUGIN_CLASS);
	}

	private String getPropertyPluginConversio() {
		return configHelper.getConfig(PropertyConfig.CONVERSIO_PLUGIN_CLASS);
	}

	private String getPropertyPluginDadesExternes() {
		return configHelper.getConfig(PropertyConfig.DADESEXT_PLUGIN_DIR3_CLASS);
	}

	private String getPropertyPluginDadesExternesPinbal() {
		return configHelper.getConfig(PropertyConfig.DADESEXT_PLUGIN_PINBAL_CLASS);
	}

	private String getPropertyPluginProcediment() {
		return configHelper.getConfig(PropertyConfig.ROLSAC_PLUGIN_CLASS);
	}

	private String getPropertyPluginValidaSignatura() {
		return configHelper.getConfig(PropertyConfig.VALIDA_FIRMA_PLUGIN_CLASS);
	}

	private String getPropertyPluginNotificacio() {
		return configHelper.getConfig(PropertyConfig.NOTIB_PLUGIN_CLASS);
	}

	private String getPropertyPluginGestioDocumental() {
		return configHelper.getConfig(PropertyConfig.GESDOC_PLUGIN_FILESYSTEM_CLASS);
	}

	private String getPropertyPluginFirmaServidor() {
		return configHelper.getConfig(PropertyConfig.FIRMA_SERV_PLUGIN_CLASS);
	}

	private String getPropertyPluginFirmaWeb() {
		return configHelper.getConfig(PropertyConfig.PORTAFIB_PLUGIN_FIRMAWEB_CLASS);
	}

	private String getPropertyPluginSummarize() {
		return configHelper.getConfig(PropertyConfig.SUMMARIZE_PLUGIN_CLASS);
	}

	private String getPropertyPluginViaFirma() {
		return configHelper.getConfig(PropertyConfig.VIAFIRMA_PLUGIN_CLASS);
	}
	
	private String getPropertyPluginDistribucioClass() {
		return configHelper.getConfig(PropertyConfig.DISTRIBUCIO_PLUGIN_CLASS);
	}

	public boolean getPropertyArxiuMetadadesAddicionalsActiu() {
		return configHelper.getAsBoolean(PropertyConfig.ARXIU_PLUGIN_METADADES_ADICIONALS);
	}

	public boolean getPropertyArxiuFirmaDetallsActiu() {
		return configHelper.getAsBoolean(PropertyConfig.ARXIU_PLUGIN_FIRMA_DETALLS);
	}

	private Integer getPropertyNotificacioRetardNumDies() {
		return configHelper.getAsInt(PropertyConfig.NOTIB_PLUGIN_RETARD);
	}

	private Integer getPropertyNotificacioCaducitatNumDies() {
		return configHelper.getAsInt(PropertyConfig.NOTIB_PLUGIN_CADUCA);
	}

	private String getPropertyNotificacioForsarEntitat() {
		return configHelper.getConfig(PropertyConfig.NOTIB_PLUGIN_ENTITAT);
	}

	private boolean getPropertyGuardarCertificacioExpedient() {
		return configHelper.getAsBoolean(PropertyConfig.GUARDAR_CERTIFICACIO_EXPEDIENT);
	}

	private boolean getPropertyViaFirmaDispositius() {
		return configHelper.getAsBoolean(PropertyConfig.VIAFIRMA_PLUGIN_DISPOSITIUS_ENABLED);
	}

	public boolean getPropertyPropagarConversioDefinitiuActiu() {
		return configHelper.getAsBoolean(PropertyConfig.CONVERSIO_DEFINITIU_PROPAGAR_ARXIU);
	}

	private boolean isComprovacioNomsDesactivada() {
		return configHelper.getAsBoolean(PropertyConfig.DESACTIVAR_COMPROVACIO_NOMS_DUPLICATS);
	}

	private String getPropertyPluginRegistreExpedientSerieDocumental() {
		return configHelper.getConfig(PropertyConfig.REGISTRE_EXPEDIENT_SERIE_DOCUMENTAL);
	}

	public boolean isCarpetaLogica() {
		return configHelper.getAsBoolean(PropertyConfig.CARPETES_LOGIQUES_ACTIVES);
	}

	public boolean isPropagarNumeroExpedientActiu() {
		return configHelper.getAsBoolean(PropertyConfig.PROPAGAR_NUMERO_EXPEDIENT);
	}

	public boolean isModificacioCustodiatsActiva() {
		return configHelper.getAsBoolean(PropertyConfig.MODIFICAR_DOCUMENTS_CUSTODIATS);
	}

	private boolean isObtenirDataFirmaFromAtributDocument() {
		return configHelper.getAsBoolean(PropertyConfig.OBTENIR_DATA_FIRMA_FROM_ATRIBUT_DOC);
	}

	public void setArxiuPlugin(
			String entitatCodi,
			IArxiuPluginWrapper arxiuPlugin) {
		arxiuPlugins.put(
				entitatCodi,
				arxiuPlugin);
	}

	public void setUnitatsOrganitzativesPlugin(
			String entitatCodi,
			UnitatsOrganitzativesPlugin unitatsOrganitzativesPlugin) {
		unitatsOrganitzativesPlugins.put(
				entitatCodi,
				unitatsOrganitzativesPlugin);
	}

	public void setPortafirmesPlugin(
			String entitatCodi,
			PortafirmesPlugin portafirmesPlugin) {
		portafirmesPlugins.put(
				entitatCodi,
				portafirmesPlugin);
	}

	public void setDadesUsuariPlugin(
			DadesUsuariPlugin dadesUsuariPlugin) {
		this.dadesUsuariPlugin = dadesUsuariPlugin;
	}

	/**
	 * F U N C I O N S   D E   D I A G N O S T I C
	 * Cridades a les funcions mes lleugeres de cada integració.
	 */
	
	public String portafirmesDiagnostic(DiagnosticFiltreDto filtre) {
		try {
			PortafirmesPlugin portafirmesPlugin = getPortafirmesPlugin(filtre.getEntitatCodi(), filtre.getOrganCodi());
			//portafirmesPlugin.findDocumentTipus();
			portafirmesPlugin.recuperarCarrecs();
			return null;
		} catch (Exception ex) {
			return ex.getMessage();
		}
	}
	
	public String firmaSimpleDiagnostic(DiagnosticFiltreDto filtre) {
		try {
			FirmaWebPlugin firmaWebPlugin = getFirmaSimpleWebPlugin(filtre.getEntitatCodi(), filtre.getOrganCodi());
			firmaWebPlugin.firmaSimpleWebEnd("prova");
			return null;
		} catch (Exception ex) {
			return ex.getMessage();
		}
	}
	
	public String firmaServidorDiagnostic(DiagnosticFiltreDto filtre) {
		try {
			FirmaServidorPlugin firmaServidorPlugin = getFirmaServidorPlugin(filtre.getEntitatCodi(), filtre.getOrganCodi());
			List<String> extensions = new ArrayList<String>();
			extensions.add("application/pdf");
			DocumentEntity doc = documentHelper.findLastDocumentPujatArxiuByExtensio(extensions);
			if (doc!=null) {
				FitxerDto fitxerAmbContingut = documentHelper.getFitxerAssociat(doc, null);
				firmaServidorPlugin.firmar("prova.pdf", "test salut integracio RIPEA", fitxerAmbContingut.getContingut(), "ca", "application/pdf");
				return null;
			} else {
				return "No hi ha cap fitxer PDF al sistema per provar.";
			}
		} catch (Exception ex) {
			return ex.getMessage();
		}
	}
	
	public String arxiuDiagnostic(DiagnosticFiltreDto filtre) {
		try {
			DocumentEntity doc = documentHelper.findLastDocumentPujatArxiuByExtensio(null);
			if (doc!=null) {
				String uuid = doc.getArxiuUuid();
				getArxiuPlugin(filtre.getEntitatCodi(), filtre.getOrganCodi()).getPlugin().documentDetalls(uuid, null, false);
				return null;
			} else {
				return "No hi ha cap fitxer pujat a l'arxiu per poder consultar.";
			}
		} catch (Exception ex) {
			return ex.getMessage();
		}
	}
	
	public String gestorDocumentalDiagnostic(DiagnosticFiltreDto filtre) {
		try {
			GestioDocumentalPlugin gestioDocumentalPlugin = getGestioDocumentalPlugin(filtre.getEntitatCodi(), filtre.getOrganCodi());
			String id = gestioDocumentalPlugin.create("DIAGNOSTIC", llegirResourceCore("/samples/blank.pdf"));
			gestioDocumentalPlugin.delete(id, "DIAGNOSTIC");
			return null;
		} catch (Exception ex) {
			return ex.getMessage();
		}			
	}
	
	public String dadesUsuariDiagnostic(DiagnosticFiltreDto filtre) {
		try {
			DadesUsuariPlugin dadesUsuariPlugin = getDadesUsuariPlugin();
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			dadesUsuariPlugin.findAmbCodi(auth.getName());
			return null;
		} catch (Exception ex) {
			return ex.getMessage();
		}	
	}
	
	public String notibDiagnostic(DiagnosticFiltreDto filtre) {
		try {
			NotificacioPlugin notificacioPlugin = getNotificacioPlugin(filtre.getEntitatCodi(), filtre.getOrganCodi());
			notificacioPlugin.consultarNotificacio("1234");
			return null;
		} catch (Exception ex) {
			return ex.getMessage();
		}	
	}
	
	public String viaFirmaDiagnostic(DiagnosticFiltreDto filtre) {
		try {
			ViaFirmaPlugin viaFirmaPlugin = getViaFirmaPlugin(filtre.getEntitatCodi(), filtre.getOrganCodi());
			viaFirmaPlugin.getDeviceUser(
					configHelper.getConfig(PropertyConfig.VIAFIRMA_PLUGIN_CALLBACK_USR),
					configHelper.getConfig(PropertyConfig.VIAFIRMA_PLUGIN_CALLBACK_PAS));
//			ViaFirmaDocument resultat = viaFirmaPlugin.downloadDocument("user", "pass", "prova");
			return null;
		} catch (Exception ex) {
			return ex.getMessage();
		}	
	}
	
	public String digitalitzacioDiagnostic(DiagnosticFiltreDto filtre) {
		try {
			DigitalitzacioPlugin digitalitzacioPlugin = getDigitalitzacioPlugin(filtre.getEntitatCodi(), filtre.getOrganCodi());
			String idioma = aplicacioService.getUsuariActual().getIdioma();
			List<DigitalitzacioPerfil> resultat = digitalitzacioPlugin.recuperarPerfilsDisponibles(idioma);
			if (resultat==null || resultat.size()==0) {
				return "No s'ha pogut recuperar cap perfil de firma disponible per el idioma "+idioma;
			} else {
				return null;
			}
		} catch (Exception ex) {
			return ex.getMessage();
		}	
	}
	
	public String validaFirmaDiagnostic(DiagnosticFiltreDto filtre) {
		try {
			IValidateSignaturePluginWrapper validaSignaturaPlugin = getValidaSignaturaPlugin(filtre.getEntitatCodi(), filtre.getOrganCodi());
			validaSignaturaPlugin.getPlugin().getSupportedSignatureRequestedInformation();
			return null;
		} catch (Exception ex) {
			return ex.getMessage();
		}	
	}
	
	public String gesConDiagnostic(DiagnosticFiltreDto filtre) {
		try {
			ProcedimentPlugin procedimentPlugin = getProcedimentPlugin(filtre.getEntitatCodi(), filtre.getOrganCodi());
			procedimentPlugin.getUnitatAdministrativa("0000");
			return null;
		} catch (Exception ex) {
			return ex.getMessage();
		}	
	}
	
	public String conversioDocumentsDiagnostic(DiagnosticFiltreDto filtre) {
		try {
			List<String> extensions = new ArrayList<String>();
			extensions.add("application/msword");
			extensions.add("application/vnd.oasis.opendocument.text");
			DocumentEntity doc = documentHelper.findLastDocumentPujatArxiuByExtensio(extensions);
			if (doc!=null) {
	        	FitxerDto fitxerNoPdf = documentHelper.getFitxerAssociat(doc, null);
	    			ConversioArxiu convertit = getConversioPlugin(filtre.getEntitatCodi(), filtre.getOrganCodi()).convertirPdfIEstamparUrl(
	    					new ConversioArxiu(fitxerNoPdf.getNom(), fitxerNoPdf.getContingut()), null);
				if (convertit!=null && convertit.getArxiuContingut()!=null && convertit.getArxiuContingut().length>0) {
					return null;
				} else {
					return "El document convertit no té contingut o no es PDF com s'esperava.";
				}
			} else {
				return "No hi ha cap fitxer ODT o DOC pujat a l'arxiu per poder convertir.";
			}
		} catch (Exception ex) {
			return ex.getMessage();
		}	
	}
	
	public String dadesExternesDiagnostic(DiagnosticFiltreDto filtre) {
		try {
			DadesExternesPlugin dadesExternesPlugin = getDadesExternesPlugin(filtre.getEntitatCodi(), filtre.getOrganCodi());
			List<Provincia> provincies = dadesExternesPlugin.provinciaFindAll();
			if (provincies!=null && provincies.size()>0) {
				return null;
			} else {
				return "La consulta no ha retornat cap provincia.";
			}
		} catch (Exception ex) {
			return ex.getMessage();
		}	
	}
	
	public String distribucioDiagnostic(DiagnosticFiltreDto filtre) {
		try {
			DistribucioPlugin dP = getDistribucioPlugin(filtre.getEntitatCodi(), filtre.getOrganCodi());
			
			Pageable pageable = PageRequest.of(0, 1);
			List<ExpedientPeticioEntity> epe = expedientPeticioRepository.findLastAnotacioRebuda(pageable).getContent();
			
			if (epe!=null && epe.size()>0) {
				AnotacioRegistreId ar = new AnotacioRegistreId();
				ar.setClauAcces(epe.get(0).getClauAcces());
				ar.setIndetificador(epe.get(0).getIdentificador());
				
				AnotacioRegistreEntrada resultat = dP.consulta(ar);
				
				if (resultat!=null && resultat.getIdentificador()!=null) {
					return null;
				} else {
					return "La consulta no ha retornat cap anotació.";
				}
			} else {
				return "No hi ha dades de cap anotació per consultar.";
			}
		} catch (Exception ex) {
			return ex.getMessage();
		}	
	}
	
	//Els fitxers han de estar a la ruta src/main/resources/es/caib/ripea/core
	private InputStream llegirResourceCore(String finalPath) {
		ClassLoader classLoader = PluginHelper.class.getClassLoader();
		return classLoader.getResourceAsStream("es/caib/ripea/core"+finalPath);
	}
	
	private byte[] llegirBytesResourceCore(String finalPath) {
		InputStream inputStream = null;
		ByteArrayOutputStream bos = null;
		try {
			ClassLoader classLoader = PluginHelper.class.getClassLoader();
			inputStream = classLoader.getResourceAsStream("es/caib/ripea/core"+finalPath);
			bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            return buffer;
		} catch (Exception ex) {
			return null;
		} finally {
			if (bos!=null) { try {bos.close();} catch (Exception ex) {} }
			if (inputStream!=null) { try {inputStream.close();} catch (Exception ex) {} }
		}
	}
	
	private static final Logger logger = LoggerFactory.getLogger(PluginHelper.class);
}
