package es.caib.ripea.service.helper;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import es.caib.pinbal.client.recobriment.ClientRecobriment;
import es.caib.pinbal.client.recobriment.v2.DadaEspecifica;
import es.caib.pinbal.client.recobriment.v2.DadesComunes;
import es.caib.pinbal.client.recobriment.v2.DadesComunes.Consentiment;
import es.caib.pinbal.client.recobriment.v2.EstatEnum;
import es.caib.pinbal.client.recobriment.v2.Funcionari;
import es.caib.pinbal.client.recobriment.v2.GrupDadesEspecifiques;
import es.caib.pinbal.client.recobriment.v2.PeticioRespostaSincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioSincrona;
import es.caib.pinbal.client.recobriment.v2.SolicitudSimple;
import es.caib.pinbal.client.recobriment.v2.Titular;
import es.caib.pinbal.client.recobriment.v2.ValorEnum;
import es.caib.pinbal.client.recobriment.legacy.ecot103.ClientEcot103;
import es.caib.pinbal.client.recobriment.legacy.ecot103.ClientEcot103.SolicitudEcot103;
import es.caib.pinbal.client.recobriment.model.ScspFuncionario;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.model.ScspSolicitante.ScspConsentimiento;
import es.caib.pinbal.client.recobriment.model.ScspTitular;
import es.caib.pinbal.client.recobriment.model.ScspTitular.ScspTipoDocumentacion;
import es.caib.pinbal.client.recobriment.model.SolicitudBase;
import es.caib.pinbal.client.recobriment.model.SolicitudBaseSvdrrcc;
import es.caib.pinbal.client.recobriment.model.SolicitudBaseSvdrrcc.FetRegistral;
import es.caib.pinbal.client.recobriment.model.SolicitudBaseSvdrrcc.Lloc;
import es.caib.pinbal.client.recobriment.model.SolicitudBaseSvdrrcc.TitularDadesAdicionals;
import es.caib.pinbal.client.recobriment.v2.Entitat;
import es.caib.pinbal.client.serveis.ServeiBasic;
import es.caib.pinbal.client.recobriment.legacy.nivrenti.ClientNivrenti;
import es.caib.pinbal.client.recobriment.legacy.nivrenti.ClientNivrenti.SolicitudNivrenti;
import es.caib.pinbal.client.recobriment.legacy.q2827003atgss001.ClientQ2827003atgss001;
import es.caib.pinbal.client.recobriment.legacy.q2827003atgss001.ClientQ2827003atgss001.SolicitudQ2827003atgss001;
import es.caib.pinbal.client.recobriment.legacy.scdcpaju.ClientScdcpaju;
import es.caib.pinbal.client.recobriment.legacy.scdcpaju.ClientScdcpaju.SolicitudScdcpaju;
import es.caib.pinbal.client.recobriment.legacy.scdhpaju.ClientScdhpaju;
import es.caib.pinbal.client.recobriment.legacy.scdhpaju.ClientScdhpaju.SolicitudScdhpaju;
import es.caib.pinbal.client.recobriment.legacy.svdbecaws01.ClientSvdbecaws01;
import es.caib.pinbal.client.recobriment.legacy.svdbecaws01.ClientSvdbecaws01.SolicitudSvdbecaws01;
import es.caib.pinbal.client.recobriment.legacy.svdccaacpasws01.ClientSvdccaacpasws01;
import es.caib.pinbal.client.recobriment.legacy.svdccaacpasws01.ClientSvdccaacpasws01.SolicitudSvdccaacpasws01;
import es.caib.pinbal.client.recobriment.legacy.svdccaacpcws01.ClientSvdccaacpcws01;
import es.caib.pinbal.client.recobriment.legacy.svdccaacpcws01.ClientSvdccaacpcws01.SolicitudSvdccaacpcws01;
import es.caib.pinbal.client.recobriment.legacy.svddelsexws01.ClientSvddelsexws01;
import es.caib.pinbal.client.recobriment.legacy.svddelsexws01.ClientSvddelsexws01.SolicitudSvddelsexws01;
import es.caib.pinbal.client.recobriment.legacy.svddelsexws01.ClientSvddelsexws01.SolicitudSvddelsexws01.Sexe;
import es.caib.pinbal.client.recobriment.legacy.svddgpciws02.ClientSvddgpciws02;
import es.caib.pinbal.client.recobriment.legacy.svddgpciws02.ClientSvddgpciws02.SolicitudSvddgpciws02;
import es.caib.pinbal.client.recobriment.legacy.svddgpresidencialegaldocws01.ClientSvddgpresidencialegaldocws01;
import es.caib.pinbal.client.recobriment.legacy.svddgpresidencialegaldocws01.ClientSvddgpresidencialegaldocws01.SolicitudSvddgpresidencialegaldocws01;
import es.caib.pinbal.client.recobriment.legacy.svddgpresidencialegaldocws01.ClientSvddgpresidencialegaldocws01.SolicitudSvddgpresidencialegaldocws01.TipusPassaport;
import es.caib.pinbal.client.recobriment.legacy.svddgpviws02.ClientSvddgpviws02;
import es.caib.pinbal.client.recobriment.legacy.svddgpviws02.ClientSvddgpviws02.SolicitudSvddgpviws02;
import es.caib.pinbal.client.recobriment.legacy.svdrrccdefuncionws01.ClientSvdrrccdefuncionws01;
import es.caib.pinbal.client.recobriment.legacy.svdrrccdefuncionws01.ClientSvdrrccdefuncionws01.SolicitudSvdrrccdefuncionws01;
import es.caib.pinbal.client.recobriment.legacy.svdrrccmatrimoniows01.ClientSvdrrccmatrimoniows01;
import es.caib.pinbal.client.recobriment.legacy.svdrrccmatrimoniows01.ClientSvdrrccmatrimoniows01.SolicitudSvdrrccmatrimoniows01;
import es.caib.pinbal.client.recobriment.legacy.svdrrccnacimientows01.ClientSvdrrccnacimientows01;
import es.caib.pinbal.client.recobriment.legacy.svdrrccnacimientows01.ClientSvdrrccnacimientows01.SolicitudSvdrrccnacimientows01;
import es.caib.pinbal.client.recobriment.legacy.svdscddws01.ClientSvdscddws01;
import es.caib.pinbal.client.recobriment.legacy.svdscddws01.ClientSvdscddws01.SolicitudSvdscddws01;
import es.caib.pinbal.client.recobriment.legacy.svdsctfnws01.ClientSvdsctfnws01;
import es.caib.pinbal.client.recobriment.legacy.svdsctfnws01.ClientSvdsctfnws01.SolicitudSvdsctfnws01;
import es.caib.ripea.persistence.entity.ConsultaPinbalEntity;
import es.caib.ripea.persistence.entity.ContingutEntity;
import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.InteressatAdministracioEntity;
import es.caib.ripea.persistence.entity.InteressatEntity;
import es.caib.ripea.persistence.entity.InteressatPersonaFisicaEntity;
import es.caib.ripea.persistence.entity.InteressatPersonaJuridicaEntity;
import es.caib.ripea.persistence.entity.MetaDocumentEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.PinbalServeiEntity;
import es.caib.ripea.persistence.entity.UsuariEntity;
import es.caib.ripea.persistence.repository.ConsultaPinbalRepository;
import es.caib.ripea.persistence.repository.DocumentRepository;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.persistence.repository.InteressatRepository;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.ConsultaPinbalEstatEnumDto;
import es.caib.ripea.service.intf.dto.DiagnosticFiltreDto;
import es.caib.ripea.service.intf.dto.DocumentDto;
import es.caib.ripea.service.intf.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.service.intf.dto.DocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.ripea.service.intf.dto.InteressatTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaNodeDto;
import es.caib.ripea.service.intf.dto.NtiOrigenEnumDto;
import es.caib.ripea.service.intf.dto.PinbalConsentimentEnumDto;
import es.caib.ripea.service.intf.dto.PinbalConsultaDto;
import es.caib.ripea.service.intf.dto.PinbalFormulariDto;
import es.caib.ripea.service.intf.dto.PinbalServeiDocPermesEnumDto;
import es.caib.ripea.service.intf.dto.SiNoEnumDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.exception.PinbalException;
import es.caib.ripea.service.intf.exception.PinbalValidacioException;
import es.caib.ripea.service.intf.exception.ValidationException;
import es.caib.ripea.service.intf.utils.PinbalTitularUtils;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PinbalHelper {

	/** Caches de l'API v2 de recobriment (caducitat per defecte de CacheConfig: 12 hores). */
	private static final String CACHE_DADES_ESPECIFIQUES = "pinbalDadesEspecifiques";
	private static final String CACHE_GRUPS = "pinbalGrupsDadesEspecifiques";
	private static final String CACHE_VALORS_ENUM = "pinbalValorsEnum";

	/** Timeouts del client de l'API v2: consultes de dades (formulari, llistes, serveis) i peticions. */
	private static final int TIMEOUT_CONNEXIO_MS = 10 * 1000;
	private static final int TIMEOUT_LECTURA_CONSULTA_MS = 30 * 1000;
	private static final int TIMEOUT_LECTURA_PETICIO_MS = 5 * 60 * 1000;

	/** Longituds màximes de l'API v2: departament (validació de PINBAL) i expedient (també als clients antics). */
	private static final int LONGITUD_DEPARTAMENT_V2 = 250;
	private static final int LONGITUD_EXPEDIENT = 25;

	@Autowired private CacheManager cacheManager;
	@Autowired private UsuariHelper usuariHelper;
	@Autowired private IntegracioHelper integracioHelper;
	@Autowired private ApplicationHelper applicationHelper;
	@Autowired private ConfigHelper configHelper;
	@Autowired private OrganGestorHelper organGestorHelper;
	@Autowired private ContingutHelper contingutHelper;
	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private DocumentHelper documentHelper;
	@Autowired private MessageHelper messageHelper;

	@Autowired private EntitatRepository entitatRepository;
	@Autowired private InteressatRepository interessatRepository;
	@Autowired private DocumentRepository documentRepository;
	@Autowired private ConsultaPinbalRepository consultaPinbalRepository;

	/**
	 * Consulta PINBAL des del formulari de la interfície JSP: només els serveis amb camps propis al formulari
	 * ({@link PinbalConsultaDto#SERVEIS_FORMULARI_JSP}), amb els clients antics de PINBAL (un per servei).
	 *
	 * @return null si la consulta i el document s'han creat, o l'excepció de la consulta (que queda registrada a les
	 *         consultes PINBAL amb estat ERROR).
	 */
	public Exception pinbalNovaConsulta(
			Long entitatId,
			Long pareId,
			Long metaDocumentId,
			PinbalConsultaDto consulta,
			String rolActual) {
		return ferConsulta(
				entitatId,
				pareId,
				metaDocumentId,
				consulta.getInteressatId(),
				rolActual,
				(expedient, metaDocument, interessat) -> novaPeticioFormulariJsp(expedient, metaDocument, interessat, consulta));
	}

	/**
	 * Consulta PINBAL des del formulari dinàmic de la interfície REACT, per a qualsevol servei, amb la petició
	 * síncrona de l'API v2 de recobriment.
	 *
	 * @param dadesEspecifiques
	 *            valors del formulari per path de la dada específica a PINBAL.
	 * @return null si la consulta i el document s'han creat, o l'excepció de la consulta (que queda registrada a les
	 *         consultes PINBAL amb estat ERROR).
	 */
	public Exception pinbalNovaConsultaDinamica(
			Long entitatId,
			Long pareId,
			Long metaDocumentId,
			Long interessatId,
			String finalitat,
			PinbalConsentimentEnumDto consentiment,
			Map<String, Object> dadesEspecifiques,
			String rolActual) {
		return ferConsulta(
				entitatId,
				pareId,
				metaDocumentId,
				interessatId,
				rolActual,
				(expedient, metaDocument, interessat) -> novaPeticioDinamica(
						expedient,
						metaDocument,
						interessat,
						finalitat,
						consentiment,
						dadesEspecifiques));
	}

	/**
	 * Comprova que el servei admet el tipus de document del titular (el mateix criteri que el filtre de titulars dels
	 * formularis). Si no l'admet, PINBAL o l'emissor rebutjarien la petició amb un error d'esquema poc entenedor.
	 */
	private void comprovarTitularPermes(InteressatEntity interessat, PinbalServeiEntity pinbalServei) {
		List<PinbalServeiDocPermesEnumDto> docsPermesos = pinbalServei.getPinbalServeiDocsPermesos();
		InteressatTipusEnumDto tipus = interessat instanceof InteressatPersonaFisicaEntity
				? InteressatTipusEnumDto.PERSONA_FISICA
				: InteressatTipusEnumDto.PERSONA_JURIDICA;
		if (!PinbalTitularUtils.isTitularPermes(tipus, interessat.getDocumentTipus(), docsPermesos)) {
			String documentTitular = interessat.getDocumentTipus() != null
					? messageHelper.getMessage("interessat.document.tipus.enum." + interessat.getDocumentTipus().name())
					: "-";
			String documentsAdmesos = docsPermesos.stream()
					.map(doc -> messageHelper.getMessage("pinbal.servei.doc.permes.enum." + doc.name()))
					.collect(Collectors.joining(", "));
			throw new ValidationException(
					"<creacio>",
					DocumentEntity.class,
					messageHelper.getMessage(
							"pinbal.titular.document.no.permes",
							new Object[] { pinbalServei.getCodi(), documentTitular, documentsAdmesos }));
		}
	}

	/** Petició a PINBAL d'una consulta. */
	@FunctionalInterface
	private interface PeticioPinbal {
		/** @return l'identificador de la petició a PINBAL. */
		String executar(
				ExpedientEntity expedient,
				MetaDocumentEntity metaDocument,
				InteressatEntity interessat) throws Exception;
	}

	/**
	 * Comprova l'expedient, el tipus de document i l'interessat, fa la petició i, si va bé, crea el document amb el
	 * justificant. La consulta queda registrada amb estat TRAMITADA o ERROR.
	 */
	private Exception ferConsulta(
			Long entitatId,
			Long pareId,
			Long metaDocumentId,
			Long interessatId,
			String rolActual,
			PeticioPinbal peticio) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(pareId));
		ContingutEntity pare = contingutHelper.comprovarContingutDinsExpedientModificable(
				entitatId,
				pareId,
				false,
				false,
				false,
				false, 
				false, 
				true, 
				rolActual);
		ExpedientEntity expedient = pare.getExpedientPare();
		MetaDocumentEntity metaDocument = null;
		if (metaDocumentId != null) {
			metaDocument = entityComprovarHelper.comprovarMetaDocument(
					pare.getEntitat(),
					expedient.getMetaExpedient(),
					metaDocumentId,
					true,
					true);
		} else {
			throw new ValidationException(
					"<creacio>",
					DocumentEntity.class,
					"No es pot fer una petició PINBAL sense un meta-document associat");
		}
		if (!metaDocument.isPinbalActiu() || !metaDocument.getPinbalServei().isActiu()) {
			throw new ValidationException(
					"<creacio>",
					DocumentEntity.class,
					"No es pot fer una petició PINBAL sense un meta-document amb la integració PINBAL activa o el servei PINBAL actiu");
		}
		InteressatEntity interessat = interessatRepository.findByExpedientAndId(expedient, interessatId);
		if (interessat == null) {
			throw new NotFoundException(interessatId, InteressatEntity.class);
		} else if (interessat instanceof InteressatAdministracioEntity) {
			throw new ValidationException(
					"<creacio>",
					DocumentEntity.class,
					"S'ha especificat un interessat que no és una persona física o juridica");
		}
		comprovarTitularPermes(interessat, metaDocument.getPinbalServei());

		ConsultaPinbalEntity.ConsultaPinbalEntityBuilder consultaPinbalBuilder = ConsultaPinbalEntity.builder();
		try {
			String idPeticion = peticio.executar(expedient, metaDocument, interessat);

			FitxerDto justificant = getJustificante(idPeticion);
			DocumentDto document = new DocumentDto();
			document.setDocumentTipus(DocumentTipusEnumDto.DIGITAL);
			
			if (interessat instanceof InteressatPersonaFisicaEntity) {
				InteressatPersonaFisicaEntity interessatPf = (InteressatPersonaFisicaEntity)interessat;
				StringBuilder nomSencer = new StringBuilder(interessatPf.getNom());
				if (interessatPf.getLlinatge1() != null) {
					nomSencer.append(" ");
					nomSencer.append(interessatPf.getLlinatge1().trim());
				}
				if (interessatPf.getLlinatge2() != null) {
					nomSencer.append(" ");
					nomSencer.append(interessatPf.getLlinatge2().trim());
				}
				document.setNom(idPeticion + " - " + nomSencer.toString());
			} else {
				document.setNom(idPeticion + " - " + interessat.getIdentificador());
			}
			
			
			document.setData(new Date());
			document.setNtiOrgano(expedient.getNtiOrgano());
			document.setNtiOrigen(NtiOrigenEnumDto.O1);
			document.setNtiEstadoElaboracion(DocumentNtiEstadoElaboracionEnumDto.EE01);
			document.setNtiTipoDocumental("TD99");
			document.setFitxerNom(justificant.getNom());
			document.setFitxerContentType(justificant.getContentType());
			document.setFitxerContingut(justificant.getContingut());
			document.setFitxerTamany(justificant.getContingut() != null ? Long.valueOf(justificant.getContingut().length) : null);
			document.setAmbFirma(true);
			document.setFirmaSeparada(false);
			document.setPinbalIdpeticion(idPeticion);
			MetaNodeDto metaNode = new MetaNodeDto();
			metaNode.setId(metaDocumentId);
			document.setMetaNode(metaNode);

			DocumentDto docum = documentHelper.crearDocument(
					entitatId,
					document,
					pare,
					true,
					true,
					true);
			
			DocumentEntity doc = documentRepository.getOne(docum.getId());
			
			consultaPinbalBuilder
			.entitat(entitatRepository.getOne(entitatId))
			.servei(metaDocument.getPinbalServei().getCodi())
			.estat(ConsultaPinbalEstatEnumDto.TRAMITADA)
			.pinbalIdpeticion(idPeticion)
			.expedient(expedient)
			.metaExpedient(expedient.getMetaExpedient())
			.document(doc);
			
			consultaPinbalRepository.save(consultaPinbalBuilder.build());
			
			return null;
			
		} catch (Exception e) {
			
			consultaPinbalBuilder
			.entitat(entitatRepository.getOne(entitatId))
			.servei(metaDocument.getPinbalServei().getCodi())
			.estat(ConsultaPinbalEstatEnumDto.ERROR)
			.error(Utils.abbreviate(Utils.getRootMsg(e), 4000))
			.expedient(expedient)
			.metaExpedient(expedient.getMetaExpedient());
			
			consultaPinbalRepository.save(consultaPinbalBuilder.build());
			
			return e;
		}
	}
	
	/** Petició amb el client antic del servei, a partir dels camps propis del formulari JSP. */
	private String novaPeticioFormulariJsp(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			PinbalConsultaDto consulta) throws PinbalException {
		String serveiCodi = metaDocument.getPinbalServei().getCodi();
		if ("SVDDGPCIWS02".equals(serveiCodi)) {
			return novaPeticioSvddgpciws02(
					expedient,
					metaDocument,
					interessat,
					consulta.getFinalitat(),
					consulta.getConsentiment());
		} else if ("SVDDGPVIWS02".equals(serveiCodi)) {
			return novaPeticioSvddgpviws02(
					expedient,
					metaDocument,
					interessat,
					consulta.getFinalitat(),
					consulta.getConsentiment());
		} else if ("SVDCCAACPASWS01".equals(serveiCodi)) {
			return novaPeticioSvdccaacpasws01(
					expedient,
					metaDocument,
					interessat,
					consulta.getFinalitat(),
					consulta.getConsentiment(),
					consulta.getComunitatAutonomaCodi(),
					consulta.getProvinciaCodi());
		} else if ("SVDSCDDWS01".equals(serveiCodi)) {
			return novaPeticioSvdscddws01(
					expedient,
					metaDocument,
					interessat,
					consulta.getFinalitat(),
					consulta.getConsentiment(),
					consulta.getComunitatAutonomaCodi(),
					consulta.getProvinciaCodi(),
					consulta.getDataConsulta(),
					consulta.getDataNaixement(),
					consulta.getConsentimentTipusDiscapacitat());
		} else if ("SCDCPAJU".equals(serveiCodi)) {
			return novaPeticioScdcpaju(
					expedient,
					metaDocument,
					interessat,
					consulta.getFinalitat(),
					consulta.getConsentiment(),
					consulta.getProvinciaCodi(),
					consulta.getMunicipiCodi());
		} else if ("SVDSCTFNWS01".equals(serveiCodi)) {
			return novaPeticioSvdsctfnws01(expedient, metaDocument, interessat, consulta);
		} else if ("SVDCCAACPCWS01".equals(serveiCodi)) {
			return novaPeticioSvdccaacpcws01(expedient, metaDocument, interessat, consulta);
		} else if ("Q2827003ATGSS001".equals(serveiCodi)) {
			return novaPeticioQ2827003atgss001(expedient, metaDocument, interessat, consulta);
		} else if ("SVDDELSEXWS01".equals(serveiCodi)) {
			return novaPeticioSvddelsexws01(expedient, metaDocument, interessat, consulta);
		} else if ("SCDHPAJU".equals(serveiCodi)) {
			return novaPeticioScdhpaju(expedient, metaDocument, interessat, consulta);
		} else if ("NIVRENTI".equals(serveiCodi)) {
			return novaPeticioNivrenti(expedient, metaDocument, interessat, consulta);
		} else if ("ECOT103".equals(serveiCodi)) {
			return novaPeticioEcot103(expedient, metaDocument, interessat, consulta);
		} else if ("SVDDGPRESIDENCIALEGALDOCWS01".equals(serveiCodi)) {
			return novaPeticioSvddgpresidencialegaldocws01(expedient, metaDocument, interessat, consulta);
		} else if ("SVDRRCCNACIMIENTOWS01".equals(serveiCodi)) {
			return novaPeticioSvdrrccnacimientows01(expedient, metaDocument, interessat, consulta);
		} else if ("SVDRRCCMATRIMONIOWS01".equals(serveiCodi)) {
			return novaPeticioSvdrrccmatrimoniows01(expedient, metaDocument, interessat, consulta);
		} else if ("SVDRRCCDEFUNCIONWS01".equals(serveiCodi)) {
			return novaPeticioSvdrrccdefuncionws01(expedient, metaDocument, interessat, consulta);
		} else if ("SVDBECAWS01".equals(serveiCodi)) {
			return novaPeticioSvdbecaws01(expedient, metaDocument, interessat, consulta);
		} else {
			// Els serveis d'aquesta llista han de coincidir amb PinbalConsultaDto.SERVEIS_FORMULARI_JSP
			throw new ValidationException(
					"<creacio>",
					DocumentEntity.class,
					"S'ha especificat un servei PINBAL no suportat: " + serveiCodi);
		}
	}

	/**
	 * Petició síncrona de l'API v2 de recobriment amb les dades específiques del formulari dinàmic. Les dades
	 * comunes (sol·licitant, procediment, funcionari, titular...) són les mateixes que les dels clients antics.
	 */
	private String novaPeticioDinamica(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			String finalitat,
			PinbalConsentimentEnumDto consentiment,
			Map<String, Object> valorsFormulari) throws PinbalException {
		long t0 = System.currentTimeMillis();
		String serveiCodi = metaDocument.getPinbalServei().getCodi();
		List<DadaEspecifica> dades = getDadesEspecifiques(serveiCodi);
		DadesSolicitant solicitant = getDadesSolicitant(expedient, metaDocument);
		ScspFuncionario funcionari = getFuncionariActual();
		ScspTitular titular = getTitularFromInteressat(interessat, false, metaDocument.getPinbalServei());
		PeticioSincrona peticio = PeticioSincrona.builder()
				.dadesComunes(DadesComunes.builder()
						.entitatCif(solicitant.getIdentificador())
						.procedimentCodi(getCodiProcediment(metaDocument.getMetaExpedient()))
						.serveiCodi(serveiCodi)
						.funcionari(Funcionari.builder()
								.nom(funcionari.getNombreCompletoFuncionario())
								.nif(funcionari.getNifFuncionario())
								.build())
						.departament(Utils.left(expedient.getOrganGestor().getNom(), LONGITUD_DEPARTAMENT_V2))
						.consentiment(PinbalConsentimentEnumDto.LLEI.equals(consentiment) ? Consentiment.Ley : Consentiment.Si)
						.finalitat(netejarFinalitat(finalitat))
						.build())
				.solicitud(SolicitudSimple.builder()
						.titular(Titular.builder()
								.documentTipus(titular.getTipoDocumentacion() != null
										? Titular.DocumentTipus.valueOf(titular.getTipoDocumentacion().name())
										: null)
								.documentNumero(titular.getDocumentacion())
								.nom(titular.getNombre())
								.llinatge1(titular.getApellido1())
								.llinatge2(titular.getApellido2())
								.nomComplet(titular.getNombreCompleto())
								.build())
						.expedient(Utils.left(expedient.getNumero(), LONGITUD_EXPEDIENT))
						.dadesEspecifiques(PinbalFormulariUtils.construirDadesEspecifiques(dades, valorsFormulari))
						.build())
				.build();
		String accioDescripcio = "Petició síncrona (API v2)";
		Map<String, String> accioParams = getAccioParams(peticio, solicitant);
		PeticioRespostaSincrona resposta;
		try {
			resposta = getClientRecobriment(TIMEOUT_LECTURA_PETICIO_MS).peticioSincrona(serveiCodi, peticio);
		} catch (Exception ex) {
			String errorDescripcio = "Excepció en la petició a PINBAL: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PINBAL,
					accioDescripcio,
					getPinbalEndpointName(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			registrarMetrica(false, t0);
			throw new PinbalException(ex.getMessage(), ex, "peticioSincrona");
		}
		String idPeticio = resposta != null && resposta.getDadesComunes() != null ? resposta.getDadesComunes().getIdPeticio() : null;
		if (resposta == null || resposta.isError() || !EstatEnum.TRAMITADA.equals(resposta.getEstat()) || idPeticio == null) {
			String errorDescripcio = PinbalFormulariUtils.descripcioError(resposta, dades);
			if (idPeticio != null) {
				accioParams.put("idPeticion", idPeticio);
			}
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PINBAL,
					accioDescripcio,
					getPinbalEndpointName(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					null);
			registrarMetrica(false, t0);
			// Errors de validació de camps del formulari: la interfície REACT els marca sota cada camp
			Map<String, List<String>> errorsCamps = PinbalFormulariUtils.errorsCampsFormulari(resposta, dades);
			if (!errorsCamps.isEmpty()) {
				throw new PinbalValidacioException(
						errorDescripcio,
						PinbalFormulariUtils.descripcioError(resposta, dades, true),
						errorsCamps);
			}
			throw new PinbalException(errorDescripcio);
		}
		accioParams.put("idPeticion", idPeticio);
		// PINBAL dona la petició per bona (TRAMITADA) encara que l'emissor l'hagi rebutjada dins les dades específiques
		// (p.ex. Justícia: CodigoEstado=400). Mentre PINBAL no ho controli, es comprova aquí.
		String[] errorEmissor = PinbalFormulariUtils.errorEstatEmissor(
				resposta.getResposta() != null ? resposta.getResposta().getDadesEspecifiques() : null);
		if (errorEmissor != null) {
			String errorDescripcio = messageHelper.getMessage(
					"pinbal.emissor.rebuig",
					new Object[] { errorEmissor[0], errorEmissor[1] != null ? errorEmissor[1] : "-" });
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PINBAL,
					accioDescripcio,
					getPinbalEndpointName(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					null);
			registrarMetrica(false, t0);
			throw new PinbalException(errorDescripcio);
		}
		integracioHelper.addAccioOk(
				IntegracioHelper.INTCODI_PINBAL,
				accioDescripcio,
				getPinbalEndpointName(),
				accioParams,
				IntegracioAccioTipusEnumDto.ENVIAMENT,
				System.currentTimeMillis() - t0);
		registrarMetrica(true, t0);
		return idPeticio;
	}

	/**
	 * Formulari de dades específiques d'un servei per al modal de nova consulta PINBAL de la interfície REACT.
	 *
	 * @param serveiCodi
	 *            codi del servei a PINBAL (columna CODI de IPA_PINBAL_SERVEI).
	 */
	public PinbalFormulariDto getFormulari(String serveiCodi) throws PinbalException {
		return PinbalFormulariUtils.construirFormulari(
				serveiCodi,
				getDadesEspecifiques(serveiCodi),
				getGrups(serveiCodi));
	}

	/**
	 * Dades específiques d'un servei (API v2 de recobriment). Es guarden a la cache (12 hores) perquè els
	 * formularis dels serveis canvien poc.
	 *
	 * @return les dades específiques, o null si el servei no en té.
	 */
	public List<DadaEspecifica> getDadesEspecifiques(String serveiCodi) throws PinbalException {
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("serveiCodi", serveiCodi);
		return llegirCache(
				CACHE_DADES_ESPECIFIQUES,
				clauCache(serveiCodi),
				"Consulta de les dades específiques d'un servei",
				accioParams,
				() -> getClientRecobriment(TIMEOUT_LECTURA_CONSULTA_MS).getDadesEspecifiques(serveiCodi));
	}

	/**
	 * Arbre de grups de les dades específiques d'un servei (API v2 de recobriment), amb cache de 12 hores.
	 *
	 * @return els grups de primer nivell, amb els seus subgrups, o null si el servei no en té.
	 */
	public List<GrupDadesEspecifiques> getGrups(String serveiCodi) throws PinbalException {
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("serveiCodi", serveiCodi);
		return llegirCache(
				CACHE_GRUPS,
				clauCache(serveiCodi),
				"Consulta dels grups de dades específiques d'un servei",
				accioParams,
				() -> getClientRecobriment(TIMEOUT_LECTURA_CONSULTA_MS).getGrups(serveiCodi));
	}

	/**
	 * Valors d'un camp de llista de les dades específiques d'un servei (API v2 de recobriment), amb cache de 12
	 * hores.
	 *
	 * @param campCodi
	 *            path de la dada específica.
	 * @param enumTipus
	 *            tipus de llista (ENUM, PAIS, PROVINCIA, MUNICIPI_3 o MUNICIPI_5).
	 * @param filtre
	 *            per a països i províncies, l'idioma (CA o ES); per als municipis, el codi de la província; per a
	 *            la resta, un text que han de contenir les descripcions (o null).
	 * @return els valors, o null si no n'hi ha cap.
	 */
	public List<ValorEnum> getValorsEnum(
			String serveiCodi,
			String campCodi,
			String enumTipus,
			String filtre) throws PinbalException {
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("serveiCodi", serveiCodi);
		accioParams.put("campCodi", campCodi);
		accioParams.put("enumTipus", enumTipus);
		accioParams.put("filtre", filtre);
		return llegirCache(
				CACHE_VALORS_ENUM,
				clauCache(serveiCodi, campCodi, enumTipus, filtre),
				"Consulta dels valors d'un camp de llista",
				accioParams,
				() -> getClientRecobriment(TIMEOUT_LECTURA_CONSULTA_MS).getValorsEnum(serveiCodi, campCodi, enumTipus, filtre));
	}

	/** Consulta a PINBAL que pot llançar qualsevol excepció. */
	@FunctionalInterface
	private interface ConsultaPinbal<T> {
		T consultar() throws Exception;
	}

	/**
	 * Valor de la cache o, si no hi és, el de la consulta a PINBAL, que es guarda a la cache (també si és null: un
	 * servei sense dades específiques no es torna a consultar fins que caduqui). Els errors no es guarden. Només
	 * les consultes a PINBAL queden al monitor d'integracions.
	 */
	@SuppressWarnings("unchecked")
	private <T> T llegirCache(
			String cacheNom,
			String clau,
			String accioDescripcio,
			Map<String, String> accioParams,
			ConsultaPinbal<T> consulta) throws PinbalException {
		Cache cache = cacheManager.getCache(cacheNom);
		Cache.ValueWrapper valorCache = cache != null ? cache.get(clau) : null;
		if (valorCache != null) {
			return (T)valorCache.get();
		}
		long t0 = System.currentTimeMillis();
		try {
			T valor = consulta.consultar();
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_PINBAL,
					accioDescripcio,
					getPinbalEndpointName(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			registrarMetrica(true, t0);
			if (cache != null) {
				cache.put(clau, valor);
			}
			return valor;
		} catch (Exception ex) {
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PINBAL,
					accioDescripcio,
					getPinbalEndpointName(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					"Excepció en la consulta a PINBAL: " + ex.getMessage(),
					ex);
			registrarMetrica(false, t0);
			throw new PinbalException(ex, accioDescripcio);
		}
	}

	/** Clau de cache: les parts precedides de la URL de PINBAL (que pot dependre de l'entitat actual). */
	private String clauCache(String... parts) {
		StringBuilder clau = new StringBuilder(String.valueOf(getPinbalBaseUrl()));
		for (String part: parts) {
			clau.append('|').append(part);
		}
		return clau.toString();
	}

	/** SVDDGPCIWS02 - Consulta de datos de identidad */
	private String novaPeticioSvddgpciws02(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			String finalitat,
			PinbalConsentimentEnumDto consentiment) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudSvddgpciws02 solicitud = new SolicitudSvddgpciws02();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				finalitat,
				consentiment);
		try {
			ScspRespuesta respuesta = getClientSvddgpciws02().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SVDDGPCIWS02", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SVDDGPCIWS02", t0);
		}
	}
	
	/** SVDDGPVIWS02 - Verificación de datos de identidad */
	private String novaPeticioSvddgpviws02(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			String finalitat,
			PinbalConsentimentEnumDto consentiment) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudSvddgpviws02 solicitud = new SolicitudSvddgpviws02();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				finalitat,
				consentiment);
		try {
			ScspRespuesta respuesta = getClientSvddgpviws02().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SVDDGPVIWS02", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SVDDGPVIWS02", t0);
		}
	}
	
	/** SVDCCAACPASWS01 - Estar al corriente de obligaciones tributarias para solicitud de subvenciones y ayudas de la CCAA */
	private String novaPeticioSvdccaacpasws01(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			String finalitat,
			PinbalConsentimentEnumDto consentiment,
			String comunitatAutonomaCodi,
			String provinciaCodi) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudSvdccaacpasws01 solicitud = new SolicitudSvdccaacpasws01();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				finalitat,
				consentiment);
		solicitud.setCodigoComunidadAutonoma(comunitatAutonomaCodi);
		solicitud.setCodigoProvincia(provinciaCodi);
		try {
			ScspRespuesta respuesta = getClientSvdccaacpasws01().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SVDCCAACPASWS01", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SVDCCAACPASWS01", t0);
		}
	}
	
	/** SVDSCDDWS01 - Servei de consulta de dades de discapacitat */
	private String novaPeticioSvdscddws01(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			String finalitat,
			PinbalConsentimentEnumDto consentiment,
			String comunitatAutonomaCodi,
			String provinciaCodi,
			String dataConsulta,
			String dataNaixement,
			SiNoEnumDto consentimentTipusDiscapacitat) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudSvdscddws01 solicitud = new SolicitudSvdscddws01();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				finalitat,
				consentiment);
		solicitud.setCodigoComunidadAutonoma(comunitatAutonomaCodi);
		solicitud.setCodigoProvincia(provinciaCodi);
		solicitud.setFechaConsulta(dataConsulta);
		solicitud.setFechaNacimiento(dataNaixement);
		solicitud.setConsentimientoTiposDiscapacidad(toSNString(consentimentTipusDiscapacitat));
		try {
			ScspRespuesta respuesta = getClientSvdscddws01().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SVDSCDDWS01", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SVDSCDDWS01", t0);
		}
	}
	
	/** SCDCPAJU - Servei de consulta de padró de convivència */
	private String novaPeticioScdcpaju(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			String finalitat,
			PinbalConsentimentEnumDto consentiment,
			String provinciaCodi,
			String municipiCodi) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudScdcpaju solicitud = new SolicitudScdcpaju();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				finalitat,
				consentiment);
		solicitud.setLlocSolicitud(
				darrersCaracters(provinciaCodi, LONGITUD_PROVINCIA_SOLICITUD),
				darrersCaracters(municipiCodi, LONGITUD_MUNICIPI_SOLICITUD));
		solicitud.setConsultaPerDocumentIdentitat(interessat.getDocumentTipus().toString(), interessat.getDocumentNum(), null);

		try {
			ScspRespuesta respuesta = getClientScdcpaju().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SCDCPAJU", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SCDCPAJU", t0);
		}
	}
	
	/** SVDSCTFNWS01 - Servei de consulta de família nombrosa */
	private String novaPeticioSvdsctfnws01(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			PinbalConsultaDto pinbalConsulta) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudSvdsctfnws01 solicitud = new SolicitudSvdsctfnws01();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				pinbalConsulta.getFinalitat(),
				pinbalConsulta.getConsentiment());
		
		solicitud.setCodigoComunidadAutonoma(pinbalConsulta.getComunitatAutonomaCodi());
		solicitud.setFechaConsulta(pinbalConsulta.getDataConsulta());
		solicitud.setFechaNacimiento(pinbalConsulta.getDataNaixement());
		solicitud.setNumeroTitulo(pinbalConsulta.getNumeroTitol());

		try {
			ScspRespuesta respuesta = getClientSvdsctfnws01().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SVDSCTFNWS01", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SVDSCTFNWS01", t0);
		}
	}
	
	/** SVDCCAACPCWS01 - Estar al corriente de obligaciones tributarias para contratación con la CCAA */
	private String novaPeticioSvdccaacpcws01(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			PinbalConsultaDto pinbalConsulta) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudSvdccaacpcws01 solicitud = new SolicitudSvdccaacpcws01();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				pinbalConsulta.getFinalitat(),
				pinbalConsulta.getConsentiment());
		
		solicitud.setCodigoComunidadAutonoma(pinbalConsulta.getComunitatAutonomaCodi());
		solicitud.setCodigoProvincia(pinbalConsulta.getProvinciaCodi());

		try {
			ScspRespuesta respuesta = getClientSvdccaacpcws01().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SVDCCAACPCWS01", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SVDCCAACPCWS01", t0);
		}
	}
	
	/** Q2827003ATGSS001  - Estar al corriente de pago con la Seguridad Social */
	private String novaPeticioQ2827003atgss001(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			PinbalConsultaDto pinbalConsulta) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudQ2827003atgss001 solicitud = new SolicitudQ2827003atgss001();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				pinbalConsulta.getFinalitat(),
				pinbalConsulta.getConsentiment());
		

		try {
			ScspRespuesta respuesta = getClientQ2827003atgss001().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "Q2827003ATGSS001", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "Q2827003ATGSS001", t0);
		}
	}
	
	/** SVDDELSEXWS01  - Consulta de inexistencia de delitos sexuales por datos de filiación */
	private String novaPeticioSvddelsexws01(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			PinbalConsultaDto pinbalConsulta) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudSvddelsexws01 solicitud = new SolicitudSvddelsexws01();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				pinbalConsulta.getFinalitat(),
				pinbalConsulta.getConsentiment());
		

		solicitud.setNacionalidad(pinbalConsulta.getCodiNacionalitat());
		solicitud.setPaisNacimiento(pinbalConsulta.getPaisNaixament());
		solicitud.setProvinciaNacimiento(pinbalConsulta.getProvinciaNaixament());
		solicitud.setPoblacionNacimiento(pinbalConsulta.getPoblacioNaixament());
		
		if (pinbalConsulta.getPaisNaixament().equals("724")) {
			solicitud.setCodPoblacionNacimiento(pinbalConsulta.getProvinciaNaixament() + pinbalConsulta.getMunicipiNaixamentSVDDELSEXWS01());
		} else {
			solicitud.setCodPoblacionNacimiento(pinbalConsulta.getCodiPoblacioNaixament());
		}

		solicitud.setSexo(pinbalConsulta.getSexe() != null ? Sexe.valueOf(pinbalConsulta.getSexe().toString()) : null);
		solicitud.setNombrePadre(pinbalConsulta.getNomPare());
		solicitud.setNombreMadre(pinbalConsulta.getNomMare());
		solicitud.setFechaNacimiento(pinbalConsulta.getDataNaixement());
		solicitud.setTelefono(pinbalConsulta.getTelefon());
		solicitud.setMail(pinbalConsulta.getEmail());
		try {
			ScspRespuesta respuesta = getClientSvddelsexws01().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SVDDELSEXWS01", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SVDDELSEXWS01", t0);
		}
	}
	
	/** SCDHPAJU - Servei de consulta de padró històric */
	private String novaPeticioScdhpaju(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			PinbalConsultaDto pinbalConsulta) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudScdhpaju solicitud = new SolicitudScdhpaju();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				pinbalConsulta.getFinalitat(),
				pinbalConsulta.getConsentiment());
		
		solicitud.setProvinciaSolicitud(darrersCaracters(pinbalConsulta.getProvinciaCodi(), LONGITUD_PROVINCIA_SOLICITUD));
		solicitud.setMunicipioSolicitud(darrersCaracters(pinbalConsulta.getMunicipiCodi(), LONGITUD_MUNICIPI_SOLICITUD));
		if (pinbalConsulta.getNombreAnysHistoric() != null) {
			solicitud.setNumeroAnyos(String.valueOf(pinbalConsulta.getNombreAnysHistoric()));
		}
		solicitud.setConsultaPerDocumentIdentitat(
				solicitud.getTitular().getTipoDocumentacion().toString(),
				solicitud.getTitular().getDocumentacion(),
				null);

		try {
			ScspRespuesta respuesta = getClientScdhpaju().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SCDHPAJU", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SCDHPAJU", t0);
		}
	}
	
	/** NIVRENTI - Consulta del nivel de renta */
	private String novaPeticioNivrenti(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			PinbalConsultaDto pinbalConsulta) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudNivrenti solicitud = new SolicitudNivrenti();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				pinbalConsulta.getFinalitat(),
				pinbalConsulta.getConsentiment());

		solicitud.setEjercicio(pinbalConsulta.getExercici());

		try {
			ScspRespuesta respuesta = getClientNivrenti().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SCDHPAJU", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SCDHPAJU", t0);
		}
	}
	
	/** ECOT103 - Estar al corriente de obligaciones tributarias para solicitud de subvenciones y ayudas con indicación de incumplimientos */
	private String novaPeticioEcot103(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			PinbalConsultaDto pinbalConsulta) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudEcot103 solicitud = new SolicitudEcot103();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				pinbalConsulta.getFinalitat(),
				pinbalConsulta.getConsentiment());


		try {
			ScspRespuesta respuesta = getClientEcot103().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "ECOT103", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "ECOT103", t0);
		}
	}
	
	/** SVDDGPRESIDENCIALEGALDOCWS01 - Servei de consulta de dades de residència legal d'estrangers per documentació */
	private String novaPeticioSvddgpresidencialegaldocws01(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			PinbalConsultaDto pinbalConsulta) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudSvddgpresidencialegaldocws01 solicitud = new SolicitudSvddgpresidencialegaldocws01();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				pinbalConsulta.getFinalitat(),
				pinbalConsulta.getConsentiment());

		solicitud.setNumeroSoporte(pinbalConsulta.getNumeroSoporte());
		solicitud.setTipo(Utils.convertEnum(pinbalConsulta.getTipusPassaport(), TipusPassaport.class));
		solicitud.setFechaCaducidad(pinbalConsulta.getDataCaducidad());
		solicitud.setNacionalidad(pinbalConsulta.getCodiNacionalitat());
		solicitud.setFechaExpedicion(pinbalConsulta.getDataExpedicion());
		
		try {
			ScspRespuesta respuesta = getClientSvddgpresidencialegaldocws01().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SVDDGPRESIDENCIALEGALDOCWS01", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SVDDGPRESIDENCIALEGALDOCWS01", t0);
		}
	}
	
	/** SVDRRCCNACIMIENTOWS01 - Servei de consulta de naixement */
	private String novaPeticioSvdrrccnacimientows01(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			PinbalConsultaDto pinbalConsulta) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudSvdrrccnacimientows01 solicitud = new SolicitudSvdrrccnacimientows01();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				pinbalConsulta.getFinalitat(),
				pinbalConsulta.getConsentiment());
		
		
        solicitud.setDadesRegistrals(SolicitudBaseSvdrrcc.DadesRegistrals.builder()
                .registreCivil(pinbalConsulta.getRegistreCivil())
                .tom(pinbalConsulta.getTom())
                .pagina(pinbalConsulta.getPagina())
                .build());
        

        solicitud.setTitularDadesAdicionals(TitularDadesAdicionals.builder()
                .fetregistral(FetRegistral.builder()
                        .data(pinbalConsulta.getDataRegistre())
                        .municipi(Lloc.builder()
                                .codi(Utils.isNotEmpty(pinbalConsulta.getMunicipiRegistreSVDRRCCNACIMIENTOWS01()) ? "07" + pinbalConsulta.getMunicipiRegistreSVDRRCCNACIMIENTOWS01() : null)
                                .build())
                        .build())
                .naixement(SolicitudBaseSvdrrcc.Naixement.builder()
                        .data(Utils.convertStringToDate(pinbalConsulta.getDataNaixement(), "dd/MM/yyyy"))
                        .municipi(Lloc.builder()
                                .codi(Utils.isNotEmpty(pinbalConsulta.getMunicipiNaixamentSVDRRCCNACIMIENTOWS01()) ? "07" + pinbalConsulta.getMunicipiNaixamentSVDRRCCNACIMIENTOWS01() : null)
                                .build())
                        .build())
                .ausenciaSegonLlinatge(pinbalConsulta.isAusenciaSegundoApellido())
                .nomMare(pinbalConsulta.getNomMare())
                .nomPare(pinbalConsulta.getNomPare())
                .sexe(Utils.convertEnum(pinbalConsulta.getSexe(), es.caib.pinbal.client.recobriment.model.Sexe.class))
                .build());
        
		
		try {
			ScspRespuesta respuesta = getClientSvdrrccnacimientows01().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SSVDRRCCNACIMIENTOWS01", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SSVDRRCCNACIMIENTOWS01", t0);
		}
	}
	
	
	/** SVDRRCCMATRIMONIOWS01 - Servei de consulta de matrimoni  */
	private String novaPeticioSvdrrccmatrimoniows01(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			PinbalConsultaDto pinbalConsulta) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudSvdrrccmatrimoniows01 solicitud = new SolicitudSvdrrccmatrimoniows01();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				pinbalConsulta.getFinalitat(),
				pinbalConsulta.getConsentiment());
		
        solicitud.setDadesRegistrals(SolicitudBaseSvdrrcc.DadesRegistrals.builder()
                .registreCivil(pinbalConsulta.getRegistreCivil())
                .tom(pinbalConsulta.getTom())
                .pagina(pinbalConsulta.getPagina())
                .build());
        

        solicitud.setTitularDadesAdicionals(TitularDadesAdicionals.builder()
                .fetregistral(FetRegistral.builder()
                        .data(pinbalConsulta.getDataRegistre())
                        .municipi(Lloc.builder()
                                .codi(Utils.isNotEmpty(pinbalConsulta.getMunicipiRegistreSVDRRCCMATRIMONIOWS01()) ? "07" + pinbalConsulta.getMunicipiRegistreSVDRRCCMATRIMONIOWS01() : null)
                                .build())
                        .build())
                .naixement(SolicitudBaseSvdrrcc.Naixement.builder()
                        .data(Utils.convertStringToDate(pinbalConsulta.getDataNaixement(), "dd/MM/yyyy"))
                        .municipi(Lloc.builder()
                                .codi(Utils.isNotEmpty(pinbalConsulta.getMunicipiNaixamentSVDRRCCMATRIMONIOWS01()) ? "07" + pinbalConsulta.getMunicipiNaixamentSVDRRCCMATRIMONIOWS01() : null)
                                .build())
                        .build())
                .ausenciaSegonLlinatge(pinbalConsulta.isAusenciaSegundoApellido())
                .nomMare(pinbalConsulta.getNomMare())
                .nomPare(pinbalConsulta.getNomPare())
                .sexe(Utils.convertEnum(pinbalConsulta.getSexe(), es.caib.pinbal.client.recobriment.model.Sexe.class))
                .build());

        
		try {
			ScspRespuesta respuesta = getClientSvdrrccmatrimoniows01().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SVDRRCCMATRIMONIOWS01", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SVDRRCCMATRIMONIOWS01", t0);
		}
	}
	
	
	/** SVDRRCCDEFUNCIONWS01 - Servei de consulta de defunció */
	private String novaPeticioSvdrrccdefuncionws01(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			PinbalConsultaDto pinbalConsulta) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudSvdrrccdefuncionws01 solicitud = new SolicitudSvdrrccdefuncionws01();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				pinbalConsulta.getFinalitat(),
				pinbalConsulta.getConsentiment());
		
		
        solicitud.setDadesRegistrals(SolicitudBaseSvdrrcc.DadesRegistrals.builder()
                .registreCivil(pinbalConsulta.getRegistreCivil())
                .tom(pinbalConsulta.getTom())
                .pagina(pinbalConsulta.getPagina())
                .build());
        

        solicitud.setTitularDadesAdicionals(TitularDadesAdicionals.builder()
                .fetregistral(FetRegistral.builder()
                        .data(pinbalConsulta.getDataRegistre())
                        .municipi(Lloc.builder()
                                .codi(Utils.isNotEmpty(pinbalConsulta.getMunicipiRegistreSVDRRCCDEFUNCIONWS01()) ? "07" + pinbalConsulta.getMunicipiRegistreSVDRRCCDEFUNCIONWS01() : null)
                                .build())
                        .build())
                .naixement(SolicitudBaseSvdrrcc.Naixement.builder()
                        .data(Utils.convertStringToDate(pinbalConsulta.getDataNaixement(), "dd/MM/yyyy"))
                        .municipi(Lloc.builder()
                                .codi(Utils.isNotEmpty(pinbalConsulta.getMunicipiNaixamentSVDRRCCDEFUNCIONWS01()) ? "07" + pinbalConsulta.getMunicipiNaixamentSVDRRCCDEFUNCIONWS01() : null)
                                .build())
                        .build())
                .ausenciaSegonLlinatge(pinbalConsulta.isAusenciaSegundoApellido())
                .nomMare(pinbalConsulta.getNomMare())
                .nomPare(pinbalConsulta.getNomPare())
                .sexe(Utils.convertEnum(pinbalConsulta.getSexe(), es.caib.pinbal.client.recobriment.model.Sexe.class))
                .build());
        
		try {
			ScspRespuesta respuesta = getClientSvdrrccdefuncionws01().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SVDRRCCDEFUNCIONWS01", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SVDRRCCDEFUNCIONWS01", t0);
		}
	}
	
	/** SVDBECAWS01 - Servei de consulta de condició de becat */
	private String novaPeticioSvdbecaws01(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			PinbalConsultaDto pinbalConsulta) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudSvdbecaws01 solicitud = new SolicitudSvdbecaws01();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				pinbalConsulta.getFinalitat(),
				pinbalConsulta.getConsentiment());
		
		solicitud.setCurso(pinbalConsulta.getCurs().toString());
        
		try {
			ScspRespuesta respuesta = getClientSvdbecaws01().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SVDRRCSVDBECAWS01CDEFUNCIONWS01", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SVDBECAWS01", t0);
		}
	}
	
	private String toSNString(SiNoEnumDto consentimentTipusDiscapacitat) {
		String sn = null;
		if (consentimentTipusDiscapacitat != null) {
			if (consentimentTipusDiscapacitat == SiNoEnumDto.SI) {
				sn = "S";
			} else if (consentimentTipusDiscapacitat == SiNoEnumDto.NO) {
				sn = "N";
			}
		}
		return sn;
	}

	public Exception pinbalDiagnostic(DiagnosticFiltreDto filtre) {
		try {
			Map<Integer, String> resultat = Utils.peticioRest(
					getPinbalBaseUrl(filtre.getEntitatCodi(), filtre.getOrganCodi())+"/interna/recobriment/test",
					getPinbalUser(filtre.getEntitatCodi(), filtre.getOrganCodi()),
					getPinbalPassword(filtre.getEntitatCodi(), filtre.getOrganCodi()));
			if (resultat!=null && resultat.get(200)!=null) {
				return null;
			} else {
				return new Exception("La resposta del mètode test no ha estat l'esperada: "+resultat.toString());
			}
		} catch (Exception ex) {
			return ex;
		}
	}
	
	public FitxerDto getJustificante(String idPeticion) throws PinbalException {
		long t0 = System.currentTimeMillis();
		String accioDescripcio = "Consulta del justificant";
		String errorDescripcio = "Excepció en la petició a PINBAL";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("idPeticion", idPeticion);
		try {
			ScspJustificante justificante = getClientSvdccaacpasws01().getJustificante(idPeticion);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_PINBAL,
					accioDescripcio,
					getPinbalEndpointName(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			registrarMetrica(true, t0);
			return new FitxerDto(
					justificante.getNom(),
					justificante.getContentType(),
					justificante.getContingut());
		} catch (Exception ex) {
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PINBAL,
					accioDescripcio,
					getPinbalEndpointName(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					null);
			registrarMetrica(false, t0);
			throw new PinbalException(ex, "getJustificante");
		}
	}

	/**
	 * Consulta les entitats donades d'alta a PINBAL (API v2 de recobriment).
	 *
	 * @return les entitats amb el seu codi de PINBAL, nom i CIF.
	 * @throws PinbalException si la consulta falla.
	 */
	public List<Entitat> getEntitats() throws PinbalException {
		long t0 = System.currentTimeMillis();
		String accioDescripcio = "Consulta de les entitats";
		Map<String, String> accioParams = new HashMap<String, String>();
		try {
			List<Entitat> entitats = getClientRecobriment(TIMEOUT_LECTURA_CONSULTA_MS).getEntitats();
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_PINBAL,
					accioDescripcio,
					getPinbalEndpointName(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			registrarMetrica(true, t0);
			return entitats;
		} catch (Exception ex) {
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PINBAL,
					accioDescripcio,
					getPinbalEndpointName(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					"Excepció en la consulta de les entitats a PINBAL",
					ex);
			registrarMetrica(false, t0);
			throw new PinbalException(ex, "getEntitats");
		}
	}

	/**
	 * Consulta els serveis d'una entitat de PINBAL (API v2 de recobriment), indicant per a cadascun si l'usuari
	 * de RIPEA hi té permís.
	 *
	 * @param entitatCodi
	 *            codi de l'entitat a PINBAL (no és el codi de l'entitat a RIPEA).
	 * @return els serveis de l'entitat.
	 * @throws PinbalException si la consulta falla.
	 */
	public List<ServeiBasic> getServeisPerEntitat(String entitatCodi) throws PinbalException {
		long t0 = System.currentTimeMillis();
		String accioDescripcio = "Consulta dels serveis d'una entitat";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("entitatCodi", entitatCodi);
		try {
			List<ServeiBasic> serveis = getClientRecobriment(TIMEOUT_LECTURA_CONSULTA_MS).getServeisPerEntitat(entitatCodi, true);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_PINBAL,
					accioDescripcio,
					getPinbalEndpointName(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			registrarMetrica(true, t0);
			return serveis;
		} catch (Exception ex) {
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PINBAL,
					accioDescripcio,
					getPinbalEndpointName(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					"Excepció en la consulta dels serveis de l'entitat " + entitatCodi + " a PINBAL",
					ex);
			registrarMetrica(false, t0);
			throw new PinbalException(ex, "getServeisPerEntitat");
		}
	}

	/**
	 * Consulta tots els serveis de PINBAL (API v2 de recobriment) amb els tipus de document permesos de cadascun. Els
	 * tipus permesos són de la configuració del servei a PINBAL i no depenen de l'entitat.
	 *
	 * @return els serveis de PINBAL.
	 * @throws PinbalException si la consulta falla.
	 */
	public List<ServeiBasic> getServeis() throws PinbalException {
		long t0 = System.currentTimeMillis();
		String accioDescripcio = "Consulta dels serveis";
		Map<String, String> accioParams = new HashMap<String, String>();
		try {
			List<ServeiBasic> serveis = getClientRecobriment(TIMEOUT_LECTURA_CONSULTA_MS).getServeis(true);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_PINBAL,
					accioDescripcio,
					getPinbalEndpointName(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			registrarMetrica(true, t0);
			return serveis;
		} catch (Exception ex) {
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PINBAL,
					accioDescripcio,
					getPinbalEndpointName(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					"Excepció en la consulta dels serveis a PINBAL",
					ex);
			registrarMetrica(false, t0);
			throw new PinbalException(ex, "getServeis");
		}
	}

	/** Si hi ha l'adreça de PINBAL configurada (la de l'entitat actual o, si no n'hi ha, la general). */
	public boolean isPinbalConfigurat() {
		return !Utils.isEmpty(getPinbalBaseUrl());
	}

	/** Longituds màximes de ProvinciaSolicitud i MunicipioSolicitud segons l'esquema SCSP de SCDCPAJU i SCDHPAJU */
	private static final int LONGITUD_PROVINCIA_SOLICITUD = 2;
	private static final int LONGITUD_MUNICIPI_SOLICITUD = 3;

	/** Si el codi supera la longitud indicada, retorna els darrers caràcters (p.ex. municipi "07040" -> "040") */
	private static String darrersCaracters(String codi, int longitud) {
		if (codi == null) {
			return null;
		}
		String codiNet = codi.trim();
		return codiNet.length() > longitud ? codiNet.substring(codiNet.length() - longitud) : codiNet;
	}

	private void emplenarSolicitudBase(
			SolicitudBase solicitud,
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			String finalitat,
			PinbalConsentimentEnumDto consentiment) {
		DadesSolicitant solicitant = getDadesSolicitant(expedient, metaDocument);
		solicitud.setIdentificadorSolicitante(solicitant.getIdentificador());
		solicitud.setNombreSolicitante(solicitant.getNom());

		solicitud.setCodigoProcedimiento(getCodiProcediment(metaDocument.getMetaExpedient()));
		solicitud.setUnidadTramitadora(expedient.getOrganGestor().getNom());

		solicitud.setFinalidad(netejarFinalitat(finalitat));
		switch (consentiment) {
		case LLEI:
			solicitud.setConsentimiento(ScspConsentimiento.Ley);
			break;
		case SI:
			solicitud.setConsentimiento(ScspConsentimiento.Si);
			break;
		}
		solicitud.setIdExpediente(Utils.left(expedient.getNumero(), 25));
		solicitud.setFuncionario(getFuncionariActual());
		solicitud.setTitular(getTitularFromInteressat(interessat, false, metaDocument.getPinbalServei()));
	}

	/** Sol·licitant de la petició: identificador (CIF) i nom. */
	private static final class DadesSolicitant {
		private final String identificador;
		private final String nom;
		private DadesSolicitant(String identificador, String nom) {
			this.identificador = identificador;
			this.nom = nom;
		}
		private String getIdentificador() {
			return identificador;
		}
		private String getNom() {
			return nom;
		}
	}

	/**
	 * Sol·licitant de la petició: el primer òrgan de la jerarquia de l'òrgan de l'expedient que fa servir el seu CIF
	 * per a PINBAL, si el tipus de document ho indica, o l'entitat.
	 */
	private DadesSolicitant getDadesSolicitant(ExpedientEntity expedient, MetaDocumentEntity metaDocument) {
		if (metaDocument.isPinbalUtilitzarCifOrgan()) {
			List<OrganGestorEntity> organsAmbPares = organGestorHelper.findPares(expedient.getOrganGestor(), true);
			for (OrganGestorEntity organ : organsAmbPares) {
				if (organ.isUtilitzarCifPinbal() && Utils.isNotEmpty(organ.getCif())) {
					return new DadesSolicitant(organ.getCif(), organ.getNom());
				}
			}
		}
		//Per proves en local cap a SE, utilitzar: S0711001H - Govern de les Illes Balears
		EntitatEntity entitat = metaDocument.getEntitat();
		return new DadesSolicitant(entitat.getCif(), entitat.getNom());
	}

	/** Codi del procediment a PINBAL: el codi SIA configurat per a les peticions o la classificació del procediment. */
	private String getCodiProcediment(MetaExpedientEntity metaExpedient) {
		String codiSia = getPinbalDefaultSia();
		return (codiSia != null && !codiSia.trim().isEmpty()) ? codiSia : metaExpedient.getClassificacio();
	}

	private static String netejarFinalitat(String finalitat) {
		// If there is character: ’ in finalitat field, some of the services (for example: SVDCCAACPASWS01, SVDCCAACPCWS01) return an error:
		// [0227] S'ha produït un error al intentar generar la resposta a la consulta: Error processant petició síncrona: SoapFault: System Error.|||El servidor ha devuelto un mensaje SOAP Fault. Error al generar la respuesta. BackofficeException: Error processant petició síncrona: SoapFault: System Error
		// if ’ (UNICODE: U+2019) is replaced by ' (UNICODE: U+0027) the problem doesn't happen
		return finalitat != null ? finalitat.replace("’", "'") : null;
	}

	private ScspFuncionario getFuncionariActual() {
		ScspFuncionario funcionario = new ScspFuncionario();
		UsuariEntity funcionari = usuariHelper.getUsuariAutenticat();
		funcionario.setNifFuncionario(funcionari.getNif());
		funcionario.setNombreCompletoFuncionario(funcionari.getNom());
		return funcionario;
	}

	private ScspTitular getTitularFromInteressat(
			InteressatEntity interessat,
			boolean ambNomSencer,
			PinbalServeiEntity pinbalServei) {
		
		List<PinbalServeiDocPermesEnumDto> pinbalServeiDocsPermesos = pinbalServei.getPinbalServeiDocsPermesos();
		
		ScspTitular titular = new ScspTitular();

		titular.setDocumentacion(interessat.getDocumentNum());
		
		if (interessat instanceof InteressatPersonaFisicaEntity) {
			
			switch (interessat.getDocumentTipus()) {
			case DOCUMENT_IDENTIFICATIU_ESTRANGERS:
				titular.setTipoDocumentacion(ScspTipoDocumentacion.NIE);
				break;
			case NIF:
				if (CollectionUtils.isEmpty(pinbalServeiDocsPermesos) || pinbalServeiDocsPermesos.contains(PinbalServeiDocPermesEnumDto.DNI)) {
					titular.setTipoDocumentacion(ScspTipoDocumentacion.DNI);
				} else {
					titular.setTipoDocumentacion(ScspTipoDocumentacion.NIF);
				}
				break;
			case PASSAPORT:
				titular.setTipoDocumentacion(ScspTipoDocumentacion.Pasaporte);
				break;
			default:
				titular.setTipoDocumentacion(ScspTipoDocumentacion.Otros);
			}
			
			InteressatPersonaFisicaEntity interessatPersonaFisica = (InteressatPersonaFisicaEntity) interessat;
			titular.setNombre(interessatPersonaFisica.getNom());
			titular.setApellido1(interessatPersonaFisica.getLlinatge1());
			titular.setApellido2(interessatPersonaFisica.getLlinatge2());
			if (ambNomSencer) {
				StringBuilder nomSencer = new StringBuilder(interessatPersonaFisica.getNom());
				if (interessatPersonaFisica.getLlinatge1() != null) {
					nomSencer.append(" ");
					nomSencer.append(interessatPersonaFisica.getLlinatge1().trim());
				}
				if (interessatPersonaFisica.getLlinatge2() != null) {
					nomSencer.append(" ");
					nomSencer.append(interessatPersonaFisica.getLlinatge2().trim());
				}
				titular.setNombreCompleto(nomSencer.toString());
			}
		} else if (interessat instanceof InteressatPersonaJuridicaEntity) {
			InteressatPersonaJuridicaEntity interessatPersonaJuridica = (InteressatPersonaJuridicaEntity) interessat;
			
			if (CollectionUtils.isEmpty(pinbalServeiDocsPermesos) || pinbalServeiDocsPermesos.contains(PinbalServeiDocPermesEnumDto.CIF)) {
				titular.setTipoDocumentacion(ScspTipoDocumentacion.CIF);
			} else {
				titular.setTipoDocumentacion(ScspTipoDocumentacion.NIF);
			}
			titular.setNombreCompleto(interessatPersonaJuridica.getRaoSocial());
			titular.setDocumentacion(interessatPersonaJuridica.getDocumentNum());
			
		}
		return titular;
	}

	private String processarScspRespuesta(
			SolicitudBase solicitud,
			ScspRespuesta respuesta,
			String serveiScsp,
			long t0) throws PinbalException {
		String accioDescripcio = "Petició síncrona";
		if (respuesta.getAtributos().getEstado().getCodigoEstado().equals("0003")) {
			Map<String, String> accioParams = getAccioParams(solicitud, serveiScsp);
			accioParams.put("idPeticion", respuesta.getAtributos().getIdPeticion());
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_PINBAL,
					accioDescripcio,
					getPinbalEndpointName(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			registrarMetrica(true, t0);
			return respuesta.getAtributos().getIdPeticion();
		} else {
			String errorDescripcio = "[" + respuesta.getAtributos().getEstado().getCodigoEstado() + "] " + respuesta.getAtributos().getEstado().getLiteralError();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PINBAL,
					accioDescripcio,
					getPinbalEndpointName(),
					getAccioParams(solicitud, serveiScsp),
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					null);
			registrarMetrica(false, t0);
			throw new PinbalException(errorDescripcio);
		}
	}

	private PinbalException processarException(
			SolicitudBase solicitud,
			Exception ex, 
			String serveiScsp,
			long t0) {
		// Una PinbalException dins el try de novaPeticioXxx només pot venir de processarScspRespuesta,
		// que ja ha registrat l'error al monitor i a les mètriques: no s'ha de tornar a registrar.
		if (ex instanceof PinbalException) {
			return (PinbalException)ex;
		}
		String recobrimentExceptionPrefix = "es.caib.pinbal.client.recobriment.RecobrimentException: ";
		String accioDescripcio = "Petició síncrona";
		String missatge;
		if (ex.getMessage() != null && ex.getMessage().indexOf(recobrimentExceptionPrefix) != -1) {
			missatge = ex.getMessage().substring(ex.getMessage().indexOf(recobrimentExceptionPrefix) + recobrimentExceptionPrefix.length());
		} else {
			missatge = ex.getMessage();
		}
		String errorDescripcio = "Excepció en la petició a PINBAL: " + missatge;
		integracioHelper.addAccioError(
				IntegracioHelper.INTCODI_PINBAL,
				accioDescripcio,
				getPinbalEndpointName(),
				getAccioParams(solicitud, serveiScsp),
				IntegracioAccioTipusEnumDto.ENVIAMENT,
				System.currentTimeMillis() - t0,
				errorDescripcio,
				ex);
		registrarMetrica(false, t0);
		return new PinbalException(
				missatge,
				ex,
				"peticionSincrona");
	}

	/** Mètrica que SalutServiceImpl.checkIntegracions envia a COMANDA (codi PBL), igual que PluginHelper per a la resta d'integracions. */
	private void registrarMetrica(boolean ok, long t0) {
		String endpoint = getPinbalEndpointName();
		applicationHelper.recordTimer(
				System.currentTimeMillis() - t0,
				TimeUnit.MILLISECONDS,
				"METRICS@Integracions.pinbal",
				"resultado", ok ? "exito" : "error",
				"endpoint", Utils.hasValue(endpoint) ? endpoint : "N/D");
	}

	private Map<String, String> getAccioParams(
			SolicitudBase solicitud,
			String serveiScsp) {
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("serveiScsp", serveiScsp);
		accioParams.put("identificadorSolicitante", solicitud.getIdentificadorSolicitante());
		accioParams.put("nombreSolicitante", solicitud.getNombreSolicitante());
		accioParams.put("codigoProcedimiento", solicitud.getCodigoProcedimiento());
		accioParams.put("codigoUnidadTramitadora", solicitud.getCodigoUnidadTramitadora());
		accioParams.put("unidadTramitadora", solicitud.getUnidadTramitadora());
		accioParams.put("idExpediente", solicitud.getIdExpediente());
		if (solicitud.getFuncionario() != null) {
			accioParams.put(
					"funcionario.nif",
					solicitud.getFuncionario().getNifFuncionario());
			accioParams.put(
					"funcionario.nombre",
					solicitud.getFuncionario().getNombreCompletoFuncionario());
		}
		if (solicitud.getTitular() != null) {
			if (solicitud.getTitular().getTipoDocumentacion() != null) {
				accioParams.put(
						"titular.tipoDocumentacion",
						solicitud.getTitular().getTipoDocumentacion().name());
			}
			accioParams.put(
					"titular.documentacion",
					solicitud.getTitular().getDocumentacion());
			accioParams.put(
					"titular.nombre",
					solicitud.getTitular().getNombre());
			accioParams.put(
					"titular.apellido1",
					solicitud.getTitular().getApellido1());
			accioParams.put(
					"titular.apellido2",
					solicitud.getTitular().getApellido2());
			if (solicitud.getTitular().getNombreCompleto() != null) {
				accioParams.put(
						"titular.nombreCompleto",
						solicitud.getTitular().getNombreCompleto());
			}
		}
		return accioParams;
	}

	/**
	 * Paràmetres del monitor d'integracions d'una petició de l'API v2: els mateixos que els dels clients antics i,
	 * de les dades específiques, només els paths (els valors poden ser dades personals).
	 */
	private Map<String, String> getAccioParams(PeticioSincrona peticio, DadesSolicitant solicitant) {
		Map<String, String> accioParams = new HashMap<String, String>();
		DadesComunes dadesComunes = peticio.getDadesComunes();
		SolicitudSimple solicitud = peticio.getSolicitud();
		accioParams.put("serveiScsp", dadesComunes.getServeiCodi());
		accioParams.put("identificadorSolicitante", dadesComunes.getEntitatCif());
		accioParams.put("nombreSolicitante", solicitant.getNom());
		accioParams.put("codigoProcedimiento", dadesComunes.getProcedimentCodi());
		accioParams.put("unidadTramitadora", dadesComunes.getDepartament());
		accioParams.put("idExpediente", solicitud.getExpedient());
		if (dadesComunes.getFuncionari() != null) {
			accioParams.put("funcionario.nif", dadesComunes.getFuncionari().getNif());
			accioParams.put("funcionario.nombre", dadesComunes.getFuncionari().getNom());
		}
		Titular titular = solicitud.getTitular();
		if (titular != null) {
			if (titular.getDocumentTipus() != null) {
				accioParams.put("titular.tipoDocumentacion", titular.getDocumentTipus().name());
			}
			accioParams.put("titular.documentacion", titular.getDocumentNumero());
			accioParams.put("titular.nombre", titular.getNom());
			accioParams.put("titular.apellido1", titular.getLlinatge1());
			accioParams.put("titular.apellido2", titular.getLlinatge2());
			if (titular.getNomComplet() != null) {
				accioParams.put("titular.nombreCompleto", titular.getNomComplet());
			}
		}
		if (solicitud.getDadesEspecifiques() != null && !solicitud.getDadesEspecifiques().isEmpty()) {
			accioParams.put("dadesEspecifiques", String.join(", ", solicitud.getDadesEspecifiques().keySet()));
		}
		return accioParams;
	}

	private ClientSvddgpciws02 getClientSvddgpciws02() {
		String  baseURL = getPinbalBaseUrl();
		String  baseUsr = getPinbalUser();
		String  basePas = getPinbalPassword();
		boolean baseAut = getPinbalBasicAuth();
		ClientSvddgpciws02 clientSvddgpciws02 = new ClientSvddgpciws02(baseURL, baseUsr, basePas, baseAut, null, null);
		if (log.isDebugEnabled()) clientSvddgpciws02.enableLogginFilter();
		return clientSvddgpciws02;
	}

	private ClientSvddgpviws02 getClientSvddgpviws02() {
		ClientSvddgpviws02 clientSvddgpviws02 = new ClientSvddgpviws02(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			clientSvddgpviws02.enableLogginFilter();
		return clientSvddgpviws02;
	}

	private ClientSvdccaacpasws01 getClientSvdccaacpasws01() {
		ClientSvdccaacpasws01 clientSvdccaacpasws01 = new ClientSvdccaacpasws01(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			clientSvdccaacpasws01.enableLogginFilter();
		return clientSvdccaacpasws01;
	}
	
	private ClientSvdscddws01 getClientSvdscddws01() {
		ClientSvdscddws01 clientSvdscddws01 = new ClientSvdscddws01(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			clientSvdscddws01.enableLogginFilter();
		return clientSvdscddws01;
	}
	
	private ClientScdcpaju getClientScdcpaju() {
		ClientScdcpaju clientScdcpaju = new ClientScdcpaju(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			clientScdcpaju.enableLogginFilter();
		return clientScdcpaju;
	}
	
	
	private ClientSvdsctfnws01 getClientSvdsctfnws01() {
		ClientSvdsctfnws01 clientSvdsctfnws01 = new ClientSvdsctfnws01(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			clientSvdsctfnws01.enableLogginFilter();
		return clientSvdsctfnws01;
	}
	
	
	private ClientSvdccaacpcws01 getClientSvdccaacpcws01() {
		ClientSvdccaacpcws01 client = new ClientSvdccaacpcws01(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			client.enableLogginFilter();
		return client;
	}

	private ClientQ2827003atgss001 getClientQ2827003atgss001() {
		ClientQ2827003atgss001 client = new ClientQ2827003atgss001(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			client.enableLogginFilter();
		return client;
	}

	private ClientSvddelsexws01 getClientSvddelsexws01() {
		ClientSvddelsexws01 client = new ClientSvddelsexws01(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			client.enableLogginFilter();
		return client;
	}
	
	private ClientScdhpaju getClientScdhpaju() {
		ClientScdhpaju client = new ClientScdhpaju(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			client.enableLogginFilter();
		return client;
	}
	
	private ClientNivrenti getClientNivrenti() {
		ClientNivrenti client = new ClientNivrenti(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			client.enableLogginFilter();
		return client;
	}
	
	private ClientEcot103 getClientEcot103() {
		ClientEcot103 client = new ClientEcot103(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			client.enableLogginFilter();
		return client;
	}
	
	private ClientSvddgpresidencialegaldocws01 getClientSvddgpresidencialegaldocws01() {
		ClientSvddgpresidencialegaldocws01 client = new ClientSvddgpresidencialegaldocws01(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			client.enableLogginFilter();
		return client;
	}
	
	private ClientSvdrrccnacimientows01 getClientSvdrrccnacimientows01() {
		ClientSvdrrccnacimientows01 client = new ClientSvdrrccnacimientows01(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			client.enableLogginFilter();
		return client;
	}
	
	private ClientSvdrrccmatrimoniows01 getClientSvdrrccmatrimoniows01() {
		ClientSvdrrccmatrimoniows01 client = new ClientSvdrrccmatrimoniows01(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			client.enableLogginFilter();
		return client;
	}
	
	private ClientSvdrrccdefuncionws01 getClientSvdrrccdefuncionws01() {
		ClientSvdrrccdefuncionws01 client = new ClientSvdrrccdefuncionws01(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			client.enableLogginFilter();
		return client;
	}
	
	private ClientSvdbecaws01 getClientSvdbecaws01() {
		ClientSvdbecaws01 client = new ClientSvdbecaws01(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			client.enableLogginFilter();
		return client;
	}

	/**
	 * Client de l'API v2 de recobriment. Sempre s'autentica amb HTTP basic (el client no permet triar-ho),
	 * a diferència dels clients de consulta, que segueixen la propietat {@link PropertyConfig#PINBAL_BASIC_AUTH}.
	 *
	 * @param timeoutLecturaMs
	 *            temps màxim d'espera de la resposta. Per a les peticions ha de ser llarg: si s'esgota abans que
	 *            PINBAL respongui, RIPEA registraria com a error una consulta que PINBAL sí ha fet.
	 */
	private ClientRecobriment getClientRecobriment(int timeoutLecturaMs) {
		return new ClientRecobriment(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				TIMEOUT_CONNEXIO_MS,
				timeoutLecturaMs);
	}
	
	private String getPinbalBaseUrl() {
		return configHelper.getConfig(PropertyConfig.PINBAL_BASE_URL);
	}
	private String getPinbalBaseUrl(String entitatCodi, String organCodi) {
		return configHelper.getConfig(PropertyConfig.PINBAL_BASE_URL, entitatCodi, organCodi);
	}
	private String getPinbalEndpointName() {
		String resultat = configHelper.getConfig(PropertyConfig.PINBAL_ENDPOINT_DESC);
		if (Utils.isEmpty(resultat)) {
			resultat = configHelper.getConfig(PropertyConfig.PINBAL_BASE_URL);
		}
		return resultat;
	}
	private String getPinbalUser() {
		return configHelper.getConfig(PropertyConfig.PINBAL_USER);
	}
	private String getPinbalPassword() {
		return configHelper.getConfig(PropertyConfig.PINBAL_PASS);
	}
	private String getPinbalUser(String entitatCodi, String organCodi) {
		return configHelper.getConfig(PropertyConfig.PINBAL_USER, entitatCodi, organCodi);
	}
	private String getPinbalPassword(String entitatCodi, String organCodi) {
		return configHelper.getConfig(PropertyConfig.PINBAL_PASS, entitatCodi, organCodi);
	}	
	private boolean getPinbalBasicAuth() {
		return configHelper.getAsBoolean(PropertyConfig.PINBAL_BASIC_AUTH);
	}
	private String getPinbalDefaultSia() {
		return configHelper.getConfig(PropertyConfig.PINBAL_DEFAULT_SIA);
	}

}
