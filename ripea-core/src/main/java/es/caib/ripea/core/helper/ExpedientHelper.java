/**
 * 
 */
package es.caib.ripea.core.helper;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import es.caib.distribucio.ws.backofficeintegracio.DocumentTipus;
import es.caib.distribucio.ws.backofficeintegracio.NtiEstadoElaboracion;
import es.caib.distribucio.ws.backofficeintegracio.NtiOrigen;
import es.caib.distribucio.ws.backofficeintegracio.NtiTipoDocumento;
import es.caib.plugins.arxiu.api.Carpeta;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.ContingutTipus;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.ripea.core.api.dto.CarpetaDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.InteressatAdministracioDto;
import es.caib.ripea.core.api.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.InteressatPersonaFisicaDto;
import es.caib.ripea.core.api.dto.InteressatPersonaJuridicaDto;
import es.caib.ripea.core.api.dto.InteressatTipusEnumDto;
import es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.NtiOrigenEnumDto;
import es.caib.ripea.core.api.dto.RegistreAnnexEstatEnumDto;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DadaEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientEstatEntity;
import es.caib.ripea.core.entity.ExpedientPeticioEntity;
import es.caib.ripea.core.entity.InteressatEntity;
import es.caib.ripea.core.entity.MetaDadaEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.RegistreAnnexEntity;
import es.caib.ripea.core.entity.RegistreInteressatEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.repository.CarpetaRepository;
import es.caib.ripea.core.repository.DadaRepository;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.ExpedientEstatRepository;
import es.caib.ripea.core.repository.ExpedientPeticioRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.MetaDadaRepository;
import es.caib.ripea.core.repository.RegistreAnnexRepository;

/**
 * Mètodes comuns per a la gestió d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ExpedientHelper {

	@Autowired
	private ExpedientEstatRepository expedientEstatRepository;
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private CarpetaRepository carpetaRepository;
	@Autowired
	private ExpedientRepository expedientRepository;
	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private ExpedientPeticioRepository expedientPeticioRepository;
	@Autowired
	private RegistreAnnexRepository registreAnnexRepository;
	@Autowired
	private CarpetaHelper carpetaHelper;
	@Autowired
	private DocumentHelper documentHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private ContingutHelper contingutHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private UsuariHelper usuariHelper;
	@Autowired
	private EmailHelper emailHelper;
	@Autowired
	private ExpedientInteressatHelper expedientInteressatHelper;
	@Autowired
	private MetaDadaRepository metaDadaRepository;
	@Autowired
	private DadaRepository dadaRepository;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	
	public ExpedientEntity create(
			Long entitatId,
			Long metaExpedientId,
			Long metaExpedientDominiId,
			Long pareId,
			Integer any,
			Long sequencia,
			String nom,
			Long expedientPeticioId,
			boolean associarInteressats,
			Long grupId) {
		if (metaExpedientId == null) {
			throw new ValidationException(
					"<creacio>",
					ExpedientEntity.class,
					"No es pot crear un expedient sense un meta-expedient associat");
		}
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, true, false, false, false);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
				metaExpedientId,
				false,
				false,
				true,
				false);

//		if (metaExpedientDominiId != null) {
//			metaExpedientDomini = metaExpedientDominiRepository.findOne(metaExpedientDominiId);
//		}

		ContingutEntity contingutPare = null;
		if (pareId != null) {
			contingutPare = contingutHelper.comprovarContingutDinsExpedientModificable(
					entitatId,
					pareId,
					false,
					false,
					true,
					false);
		}
		contingutHelper.comprovarNomValid(contingutPare, nom, null, ExpedientEntity.class);
//		comprovarSiExpedientAmbMateixNom(
//				metaExpedient,
//				contingutPare,
//				nom,
//				null,
//				ExpedientEntity.class);
		ExpedientEntity expedient = contingutHelper.crearNouExpedient(
				nom,
				metaExpedient,
				contingutPare,
				metaExpedient.getEntitat(),
				"1.0",
				metaExpedient.getEntitat().getUnitatArrel(),
				new Date(),
				any,
				sequencia,
				true,
				grupId);
		contingutLogHelper.logCreacio(expedient, false, false);
		
		
		crearDadesPerDefecte(
				metaExpedient,
				expedient);

		
		


		
		
		
		List<ExpedientEstatEntity> expedientEstats = expedientEstatRepository.findByMetaExpedientOrderByOrdreAsc(expedient.getMetaExpedient());
		//find inicial state if exists
		ExpedientEstatEntity estatInicial = null;
		for (ExpedientEstatEntity expedientEstat : expedientEstats) {
			if (expedientEstat.isInicial()) {
				estatInicial = expedientEstat;
			}
		}
		// set inicial estat if exists
		if (estatInicial != null) {
			expedient.updateExpedientEstat(estatInicial);

			// if estat has usuari responsable agafar expedient by this user
			if (estatInicial.getResponsableCodi() != null) {
				agafar(expedient, estatInicial.getResponsableCodi());
				
			}
		}

		// if expedient comes from distribucio
		if (expedientPeticioId != null) {
			relateExpedientWithPeticioAndSetAnnexosPendent(expedientPeticioId, expedient.getId());

			if (associarInteressats) {
				associateInteressats(expedient.getId(), entitat.getId(), expedientPeticioId);
			}
		}
		
		return expedient;
	}
	
	
	

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void associateInteressats(Long expedientId, Long entitatId, Long expedientPeticioId) {
		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		ExpedientEntity expedientEntity = expedientRepository.findOne(expedientId);
		Set<InteressatEntity> existingInteressats = expedientEntity.getInteressats();
		for (RegistreInteressatEntity registreInteressatEntity : expedientPeticioEntity.getRegistre().getInteressats()) {
			boolean alreadyExists = false;
			for (InteressatEntity existingInteressat : existingInteressats) {
				if (existingInteressat.getDocumentNum().equals(registreInteressatEntity.getDocumentNumero()))
					alreadyExists = true;
			}
			if (!alreadyExists) {
				InteressatDto createdInteressat = expedientInteressatHelper.create(
						entitatId,
						expedientId,
						null,
						toInteressatDto(registreInteressatEntity),
						true);
				if (registreInteressatEntity.getRepresentant() != null) {
					expedientInteressatHelper.create(
							entitatId,
							expedientId,
							createdInteressat.getId(),
							toInteressatDto(registreInteressatEntity.getRepresentant()),
							true);
				}
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void relateExpedientWithPeticioAndSetAnnexosPendentNewTransaction(
			Long expedientPeticioId,
			Long expedientId) {
		relateExpedientWithPeticioAndSetAnnexosPendent(expedientPeticioId, expedientId);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateNotificarError(Long expedientPeticioId, String error) {
		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		expedientPeticioEntity.updateNotificaDistError(error);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateRegistreAnnexError(Long registreAnnexId, String error) {
		RegistreAnnexEntity registreAnnexEntity = registreAnnexRepository.findOne(registreAnnexId);
		registreAnnexEntity.updateError(error);

	}

	/**
	 * Creates document from registre annex
	 * 
	 * @param registreAnnexId
	 * @param expedientPeticioId
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public DocumentEntity crearDocFromAnnex(Long registreAnnexId, Long expedientPeticioId) {
		ExpedientPeticioEntity expedientPeticioEntity;
		ExpedientEntity expedientEntity;
		RegistreAnnexEntity registreAnnexEntity = new RegistreAnnexEntity();
		EntitatEntity entitat;
		CarpetaEntity carpetaEntity = null;
		expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		expedientEntity = expedientRepository.findOne(expedientPeticioEntity.getExpedient().getId());
		registreAnnexEntity = registreAnnexRepository.findOne(registreAnnexId);
		entitat = entitatRepository.findByUnitatArrel(expedientPeticioEntity.getRegistre().getEntitatCodi());
		logger.debug(
				"Creant carpeta i documents de expedient peticio (" + "expedientId=" +
						expedientPeticioEntity.getExpedient().getId() + ", " + "registreAnnexId=" + registreAnnexId +
						", " + "expedientPeticioId=" + expedientPeticioId + ")");

		// ############################## CREATE CARPETA IN DB AND IN ARXIU
		// ##########################################
		boolean isCarpetaActive = Boolean.parseBoolean(
				PropertiesHelper.getProperties().getProperty("es.caib.ripea.creacio.carpetes.activa"));
		if (isCarpetaActive) {
			// create carpeta ind db and arxiu if doesnt already exists
			Long carpetaId = createCarpetaFromExpPeticio(
					expedientEntity,
					entitat.getId(),
					expedientPeticioEntity.getRegistre().getIdentificador());
			carpetaEntity = carpetaRepository.findOne(carpetaId);
		}

		// ############################## CREATE DOCUMENT IN DB
		// ####################################
		DocumentDto documentDto = toDocumentDto(registreAnnexEntity);
		contingutHelper.comprovarNomValid(
				isCarpetaActive ? carpetaEntity : expedientEntity,
				documentDto.getNom(),
				null,
				DocumentEntity.class);
		DocumentEntity docEntity = documentHelper.crearDocumentDB(
				documentDto.getDocumentTipus(),
				documentDto.getNom(),
				documentDto.getDescripcio(),
				documentDto.getData(),
				documentDto.getDataCaptura(),
				documentDto.getNtiOrgano(),
				documentDto.getNtiOrigen(),
				documentDto.getNtiEstadoElaboracion(),
				documentDto.getNtiTipoDocumental(),
				null,
				isCarpetaActive ? carpetaEntity : expedientEntity,
				expedientEntity.getEntitat(),
				expedientEntity,
				documentDto.getUbicacio(),
				documentDto.getNtiIdDocumentoOrigen());
		FitxerDto fitxer = new FitxerDto();
		fitxer.setNom(documentDto.getFitxerNom());
		fitxer.setContentType(documentDto.getFitxerContentType());
		fitxer.setContingut(documentDto.getFitxerContingut());
		if (documentDto.getFitxerContingut() != null) {
			documentHelper.actualitzarFitxerDocument(docEntity, fitxer);
			if (documentDto.isAmbFirma()) {
				documentHelper.validaFirmaDocument(docEntity, fitxer, documentDto.getFirmaContingut());
			}
		} else {
			docEntity.updateFitxer(fitxer.getNom(), fitxer.getContentType(), fitxer.getContingut());

		}

		docEntity.updateEstat(DocumentEstatEnumDto.CUSTODIAT);
		
		// ############################## MOVE DOCUMENT IN ARXIU
		// ##########################################
		// put arxiu uuid of annex
		docEntity.updateArxiu(documentDto.getArxiuUuid());
		documentRepository.saveAndFlush(docEntity);
		if (isCarpetaActive) {
			Carpeta carpeta = pluginHelper.arxiuCarpetaConsultar(carpetaEntity);
			boolean documentExistsInArxiu = false;
			String documentUuid = null;
			if (carpeta.getContinguts() != null) {
				for (ContingutArxiu contingutArxiu : carpeta.getContinguts()) {
					if (contingutArxiu.getTipus() == ContingutTipus.DOCUMENT &&
							contingutArxiu.getNom().equals(docEntity.getNom())) {
						documentExistsInArxiu = true;
						documentUuid = contingutArxiu.getIdentificador();
					}
				}
			}
			if (documentExistsInArxiu && carpetaEntity.getArxiuUuid() == null) {
				carpetaEntity.updateArxiu(documentUuid);
			}
			if (!documentExistsInArxiu) {
				String uuidDesti = contingutHelper.arxiuPropagarMoviment(
						docEntity,
						carpetaEntity,
						expedientEntity.getArxiuUuid());
				// if document was dispatched, update uuid to new document
				if (uuidDesti != null) {
					docEntity.updateArxiu(uuidDesti);
				}
			}
		} else {
			Expedient expedient = pluginHelper.arxiuExpedientConsultar(expedientEntity);
			boolean documentExistsInArxiu = false;
			String documentUuid = null;
			if (expedient.getContinguts() != null) {
				for (ContingutArxiu contingutArxiu : expedient.getContinguts()) {
					if (contingutArxiu.getTipus() == ContingutTipus.DOCUMENT &&
							contingutArxiu.getNom().equals(docEntity.getNom())) {
						documentExistsInArxiu = true;
						documentUuid = contingutArxiu.getIdentificador();
					}
				}
			}
			if (documentExistsInArxiu && carpetaEntity.getArxiuUuid() == null) {
				expedientEntity.updateArxiu(documentUuid);
			}
			if (!documentExistsInArxiu) {
				String uuidDesti = contingutHelper.arxiuPropagarMoviment(
						docEntity,
						expedientEntity,
						expedientEntity.getArxiuUuid());
				// if document was dispatched, update uuid to new document
				if (uuidDesti != null) {
					docEntity.updateArxiu(uuidDesti);
				}
			}
		}
		// save ntiIdentitficador generated in arxiu in db
		Document documentDetalls = pluginHelper.arxiuDocumentConsultar(docEntity, null, null, true, false);
		documentDetalls.getMetadades().getIdentificadorOrigen();
		docEntity.updateNtiIdentificador(documentDetalls.getMetadades().getIdentificador());
		documentRepository.save(docEntity);
		contingutLogHelper.logCreacio(docEntity, true, true);
		return docEntity;
	}
	
	public ExpedientEntity updateNomExpedient(ExpedientEntity expedient, String nom) {
		contingutHelper.comprovarNomValid(expedient.getPare(), nom, expedient.getId(), ExpedientEntity.class);
		String nomOriginal = expedient.getNom();
		expedient.update(nom);
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.MODIFICACIO,
				(!nomOriginal.equals(expedient.getNom())) ? expedient.getNom() : null,
				null,
				false,
				false);
		
		return expedient;
	}
	
	public ExpedientEntity updateAnyExpedient(ExpedientEntity expedient, int any) {
		int anyOriginal = expedient.getAny();
		expedient.updateAny(any);
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.MODIFICACIO,
				(anyOriginal != (expedient.getAny())) ? String.valueOf(expedient.getAny()) : null,
				null,
				false,
				false);
		return expedient;
	}
	
	
	private MustacheFactory mustacheFactory = new DefaultMustacheFactory();

	public String calcularNumero(ExpedientEntity expedient) {
		MetaExpedientEntity metaExpedient = expedient.getMetaExpedient();
		String expressioNumero = metaExpedient.getExpressioNumero();
		if (expressioNumero != null && !expressioNumero.isEmpty()) {
			Mustache mustache = mustacheFactory.compile(new StringReader(expressioNumero), "expressioNumero");
			StringWriter writer = new StringWriter();
			HashMap<String, Object> model = new HashMap<String, Object>();
			model.put("codi", expedient.getCodi());
			model.put("seq", expedient.getSequencia());
			model.put("any", expedient.getAny());
			mustache.execute(writer, model);
			writer.flush();
			return writer.toString();
		} else {
			return expedient.getCodi() + "/" + expedient.getSequencia() + "/" + expedient.getAny();
		}
	}

	public ExpedientDto toExpedientDto(ExpedientEntity expedient, boolean ambPathIPermisos) {
		return (ExpedientDto)contingutHelper.toContingutDto(
				expedient,
				ambPathIPermisos,
				false,
				false,
				false,
				ambPathIPermisos,
				false,
				false);
	}

	public void agafar(ExpedientEntity expedient, String usuariCodi) {

		ExpedientEntity expedientSuperior = contingutHelper.getExpedientSuperior(expedient, false, false, false);
		if (expedientSuperior != null) {
			logger.error("No es pot agafar un expedient no arrel (id=" + expedient.getId() + ")");
			throw new ValidationException(expedient.getId(), ExpedientEntity.class, "No es pot agafar un expedient no arrel");
		}
		// Agafa l'expedient. Si l'expedient pertany a un altre usuari li pren
		UsuariEntity usuariOriginal = expedient.getAgafatPer();
		UsuariEntity usuariNou = usuariHelper.getUsuariByCodi(usuariCodi);
		expedient.updateAgafatPer(usuariNou);
		if (usuariOriginal != null) {
			// Avisa a l'usuari que li han pres
			emailHelper.contingutAgafatPerAltreUsusari(expedient, usuariOriginal, usuariNou);
		}
		contingutLogHelper.log(expedient, LogTipusEnumDto.AGAFAR, usuariCodi, null, false, false);
	}
	
	public void alliberar(ExpedientEntity expedient) {
		UsuariEntity prevUserAgafat = expedient.getAgafatPer();
		expedient.updateAgafatPer(null);
		contingutLogHelper.log(expedient, LogTipusEnumDto.ALLIBERAR, prevUserAgafat.getCodi(), null, false, false);
	}
	
	private void crearDadesPerDefecte(MetaExpedientEntity metaExpedient, ExpedientEntity expedient) {
		
		
		List<MetaDadaEntity> metaDades = metaDadaRepository.findByMetaNodeOrderByOrdreAsc(metaExpedient);
		
		for (int i = 0; i < metaDades.size(); i++) {
			
			if (metaDades.get(i).getValor()!= null && !metaDades.get(i).getValor().isEmpty()) {
				
				Object valor;
				switch (metaDades.get(i).getTipus()) {
				case BOOLEA:
					valor = (Boolean) DadaEntity.getDadaValorPerRetornar(metaDades.get(i), metaDades.get(i).getValor());
					break;
				case DATA:
					valor = (Date) DadaEntity.getDadaValorPerRetornar(metaDades.get(i), metaDades.get(i).getValor());
					break;
				case FLOTANT:
					valor = (Double) DadaEntity.getDadaValorPerRetornar(metaDades.get(i), metaDades.get(i).getValor());
					break;
				case IMPORT:
					valor = (BigDecimal) DadaEntity.getDadaValorPerRetornar(metaDades.get(i), metaDades.get(i).getValor());
					break;
				case SENCER:
					valor = (Long) DadaEntity.getDadaValorPerRetornar(metaDades.get(i), metaDades.get(i).getValor());
					break;
				case TEXT:
					valor = (String) DadaEntity.getDadaValorPerRetornar(metaDades.get(i), metaDades.get(i).getValor());
					break;
				default:
					valor = (String) DadaEntity.getDadaValorPerRetornar(metaDades.get(i), metaDades.get(i).getValor());
					break;
				}
				
				DadaEntity dada = DadaEntity.getBuilder(
						metaDades.get(i),
						expedient,
						valor,
						i).build();
				
				dadaRepository.save(dada);
				contingutLogHelper.log(
						expedient,
						LogTipusEnumDto.MODIFICACIO,
						dada,
						LogObjecteTipusEnumDto.DADA,
						LogTipusEnumDto.CREACIO,
						metaDades.get(i).getCodi(),
						dada.getValorComString(),
						false,
						false);
				
			}
			
		}
	}

	private void relateExpedientWithPeticioAndSetAnnexosPendent(Long expedientPeticioId, Long expedientId) {
		ExpedientEntity expedient = expedientRepository.findOne(expedientId);
		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		expedientPeticioEntity.updateExpedient(expedient);
		expedient.addExpedientPeticio(expedientPeticioEntity);
		// set annexos as pending to create in db and to move in arxiu
		for (RegistreAnnexEntity registreAnnex : expedientPeticioEntity.getRegistre().getAnnexos()) {
			registreAnnex.updateEstat(RegistreAnnexEstatEnumDto.PENDENT);
		}

	}

	private DocumentDto toDocumentDto(RegistreAnnexEntity registreAnnexEntity) {
		DocumentDto document = new DocumentDto();
		document.setDocumentTipus(DocumentTipusEnumDto.DIGITAL);
		document.setEstat(DocumentEstatEnumDto.CUSTODIAT);
		document.setData(new Date());
		document.setNom(registreAnnexEntity.getTitol());
		document.setFitxerNom(registreAnnexEntity.getNom());
		document.setArxiuUuid(registreAnnexEntity.getUuid());
		document.setDataCaptura(registreAnnexEntity.getNtiFechaCaptura());
		document.setNtiOrigen(toNtiOrigenEnumDto(registreAnnexEntity.getNtiOrigen()));
		document.setNtiTipoDocumental(toDocumentNtiTipoDocumentalEnumDto(registreAnnexEntity.getNtiTipoDocumental()));
		document.setNtiEstadoElaboracion(
				toDocumentNtiEstadoElaboracionEnumDto(registreAnnexEntity.getNtiEstadoElaboracion()));
		document.setFitxerContentType(registreAnnexEntity.getTipusMime());
		document.setNtiVersion("1.0");
		String codiDir3 = entitatRepository.findByUnitatArrel(
				registreAnnexEntity.getRegistre().getEntitatCodi()).getUnitatArrel();
		document.setNtiOrgano(codiDir3);
		return document;
	}

	private InteressatDto toInteressatDto(RegistreInteressatEntity registreInteressatEntity) {
		InteressatDto interessatDto = null;
		switch (registreInteressatEntity.getTipus()) {
		case PERSONA_FISICA:
			InteressatPersonaFisicaDto interessatPersonaFisicaDto = new InteressatPersonaFisicaDto();
			interessatPersonaFisicaDto.setDocumentTipus(
					toInteressatDocumentTipusEnumDto(registreInteressatEntity.getDocumentTipus()));
			interessatPersonaFisicaDto.setDocumentNum(registreInteressatEntity.getDocumentNumero());
			interessatPersonaFisicaDto.setPais(registreInteressatEntity.getPaisCodi());
			interessatPersonaFisicaDto.setProvincia(registreInteressatEntity.getProvinciaCodi());
			interessatPersonaFisicaDto.setMunicipi(registreInteressatEntity.getMunicipiCodi());
			interessatPersonaFisicaDto.setAdresa(registreInteressatEntity.getAdresa());
			interessatPersonaFisicaDto.setCodiPostal(registreInteressatEntity.getCp());
			interessatPersonaFisicaDto.setEmail(registreInteressatEntity.getEmail());
			interessatPersonaFisicaDto.setTelefon(registreInteressatEntity.getTelefon());
			interessatPersonaFisicaDto.setObservacions(registreInteressatEntity.getObservacions());
			interessatPersonaFisicaDto.setNotificacioAutoritzat(false);
			interessatPersonaFisicaDto.setTipus(InteressatTipusEnumDto.PERSONA_FISICA);
			interessatPersonaFisicaDto.setNom(registreInteressatEntity.getNom());
			interessatPersonaFisicaDto.setLlinatge1(registreInteressatEntity.getLlinatge1());
			interessatPersonaFisicaDto.setLlinatge2(registreInteressatEntity.getLlinatge2());
			interessatDto = interessatPersonaFisicaDto;
			break;
		case PERSONA_JURIDICA:
			InteressatPersonaJuridicaDto interessatPersonaJuridicaDto = new InteressatPersonaJuridicaDto();
			interessatPersonaJuridicaDto.setDocumentTipus(
					toInteressatDocumentTipusEnumDto(registreInteressatEntity.getDocumentTipus()));
			interessatPersonaJuridicaDto.setDocumentNum(registreInteressatEntity.getDocumentNumero());
			interessatPersonaJuridicaDto.setPais(registreInteressatEntity.getPaisCodi());
			interessatPersonaJuridicaDto.setProvincia(registreInteressatEntity.getProvinciaCodi());
			interessatPersonaJuridicaDto.setMunicipi(registreInteressatEntity.getMunicipiCodi());
			interessatPersonaJuridicaDto.setAdresa(registreInteressatEntity.getAdresa());
			interessatPersonaJuridicaDto.setCodiPostal(registreInteressatEntity.getCp());
			interessatPersonaJuridicaDto.setEmail(registreInteressatEntity.getEmail());
			interessatPersonaJuridicaDto.setTelefon(registreInteressatEntity.getTelefon());
			interessatPersonaJuridicaDto.setObservacions(registreInteressatEntity.getObservacions());
			interessatPersonaJuridicaDto.setNotificacioAutoritzat(false);
			interessatPersonaJuridicaDto.setTipus(InteressatTipusEnumDto.PERSONA_JURIDICA);
			interessatPersonaJuridicaDto.setRaoSocial(registreInteressatEntity.getRaoSocial());
			interessatDto = interessatPersonaJuridicaDto;
			break;
		case ADMINISTRACIO:
			InteressatAdministracioDto interessatAdministracioDto = new InteressatAdministracioDto();
			interessatAdministracioDto.setDocumentTipus(
					toInteressatDocumentTipusEnumDto(registreInteressatEntity.getDocumentTipus()));
			interessatAdministracioDto.setDocumentNum(registreInteressatEntity.getDocumentNumero());
			interessatAdministracioDto.setPais(registreInteressatEntity.getPaisCodi());
			interessatAdministracioDto.setProvincia(registreInteressatEntity.getProvinciaCodi());
			interessatAdministracioDto.setMunicipi(registreInteressatEntity.getMunicipiCodi());
			interessatAdministracioDto.setAdresa(registreInteressatEntity.getAdresa());
			interessatAdministracioDto.setCodiPostal(registreInteressatEntity.getCp());
			interessatAdministracioDto.setEmail(registreInteressatEntity.getEmail());
			interessatAdministracioDto.setTelefon(registreInteressatEntity.getTelefon());
			interessatAdministracioDto.setObservacions(registreInteressatEntity.getObservacions());
			interessatAdministracioDto.setNotificacioAutoritzat(false);
			interessatAdministracioDto.setTipus(InteressatTipusEnumDto.ADMINISTRACIO);
			interessatAdministracioDto.setOrganCodi(registreInteressatEntity.getOrganCodi());
			interessatDto = interessatAdministracioDto;
			break;
		}
		return interessatDto;
	}

	private InteressatDocumentTipusEnumDto toInteressatDocumentTipusEnumDto(DocumentTipus documentTipus) {
		InteressatDocumentTipusEnumDto interessatDocumentTipusEnumDto = null;
		if (documentTipus != null) {
			switch (documentTipus) {
			case NIF:
				interessatDocumentTipusEnumDto = InteressatDocumentTipusEnumDto.NIF;
				break;
			case CIF:
				interessatDocumentTipusEnumDto = InteressatDocumentTipusEnumDto.CIF;
				break;
			case PASSAPORT:
				interessatDocumentTipusEnumDto = InteressatDocumentTipusEnumDto.PASSAPORT;
				break;
			case NIE:
				interessatDocumentTipusEnumDto = InteressatDocumentTipusEnumDto.DOCUMENT_IDENTIFICATIU_ESTRANGERS;
				break;
			case ALTRES:
				interessatDocumentTipusEnumDto = InteressatDocumentTipusEnumDto.ALTRES_DE_PERSONA_FISICA;
				break;
			case CODI_ORIGEN:
				interessatDocumentTipusEnumDto = InteressatDocumentTipusEnumDto.CODI_ORIGEN;
				break;
			}
		}
		return interessatDocumentTipusEnumDto;
	}

	private NtiOrigenEnumDto toNtiOrigenEnumDto(NtiOrigen ntiOrigen) {
		NtiOrigenEnumDto ntiOrigenEnumDto = null;
		if (ntiOrigen != null) {
			switch (ntiOrigen) {
			case CIUTADA:
				ntiOrigenEnumDto = NtiOrigenEnumDto.O0;
				break;
			case ADMINISTRACIO:
				ntiOrigenEnumDto = NtiOrigenEnumDto.O1;
				break;
			}
		}
		return ntiOrigenEnumDto;
	}

	private DocumentNtiEstadoElaboracionEnumDto toDocumentNtiEstadoElaboracionEnumDto(
			NtiEstadoElaboracion ntiEstadoElaboracion) {
		DocumentNtiEstadoElaboracionEnumDto documentNtiEstadoElaboracionEnumDto = null;
		if (ntiEstadoElaboracion != null) {
			switch (ntiEstadoElaboracion) {
			case ORIGINAL:
				documentNtiEstadoElaboracionEnumDto = DocumentNtiEstadoElaboracionEnumDto.EE01;
				break;
			case COPIA_ELECT_AUTENTICA_CANVI_FORMAT:
				documentNtiEstadoElaboracionEnumDto = DocumentNtiEstadoElaboracionEnumDto.EE02;
				break;
			case COPIA_ELECT_AUTENTICA_PAPER:
				documentNtiEstadoElaboracionEnumDto = DocumentNtiEstadoElaboracionEnumDto.EE03;
				break;
			case COPIA_ELECT_AUTENTICA_PARCIAL:
				documentNtiEstadoElaboracionEnumDto = DocumentNtiEstadoElaboracionEnumDto.EE04;
				break;
			case ALTRES:
				documentNtiEstadoElaboracionEnumDto = DocumentNtiEstadoElaboracionEnumDto.EE99;
				break;
			}
		}
		return documentNtiEstadoElaboracionEnumDto;
	}

	private String toDocumentNtiTipoDocumentalEnumDto(NtiTipoDocumento ntiTipoDocumento) {
		String documentNtiTipoDocumental = null;
		if (ntiTipoDocumento != null) {
			switch (ntiTipoDocumento) {
			case RESOLUCIO:
				documentNtiTipoDocumental = "TD01";
				break;
			case ACORD:
				documentNtiTipoDocumental = "TD02";
				break;
			case CONTRACTE:
				documentNtiTipoDocumental = "TD03";
				break;
			case CONVENI:
				documentNtiTipoDocumental = "TD04";
				break;
			case DECLARACIO:
				documentNtiTipoDocumental = "TD05";
				break;
			case COMUNICACIO:
				documentNtiTipoDocumental = "TD06";
				break;
			case NOTIFICACIO:
				documentNtiTipoDocumental = "TD07";
				break;
			case PUBLICACIO:
				documentNtiTipoDocumental = "TD08";
				break;
			case JUSTIFICANT_RECEPCIO:
				documentNtiTipoDocumental = "TD09";
				break;
			case ACTA:
				documentNtiTipoDocumental = "TD10";
				break;
			case CERTIFICAT:
				documentNtiTipoDocumental = "TD11";
				break;
			case DILIGENCIA:
				documentNtiTipoDocumental = "TD12";
				break;
			case INFORME:
				documentNtiTipoDocumental = "TD13";
				break;
			case SOLICITUD:
				documentNtiTipoDocumental = "TD14";
				break;
			case DENUNCIA:
				documentNtiTipoDocumental = "TD15";
				break;
			case ALEGACIO:
				documentNtiTipoDocumental = "TD16";
				break;
			case RECURS:
				documentNtiTipoDocumental = "TD17";
				break;
			case COMUNICACIO_CIUTADA:
				documentNtiTipoDocumental = "TD18";
				break;
			case FACTURA:
				documentNtiTipoDocumental = "TD19";
				break;
			case ALTRES_INCAUTATS:
				documentNtiTipoDocumental = "TD20";
				break;
			case ALTRES:
				documentNtiTipoDocumental = "TD99";
				break;
			}
		}
		return documentNtiTipoDocumental;
	}

	private Long createCarpetaFromExpPeticio(ExpedientEntity expedientEntity, Long entitatId, String nom) {
		// check if already exists in db
		boolean carpetaExistsInDB = false;
		Long carpetaId = null;
		CarpetaEntity carpetaEntity = null;
		for (ContingutEntity contingut : expedientEntity.getFills()) {
			if (contingut instanceof CarpetaEntity && contingut.getNom().equals(nom)) {
				carpetaExistsInDB = true;
				carpetaId = contingut.getId();
				carpetaEntity = (CarpetaEntity)contingut;
			}
		}
		// check if already exists in arxiu
		Expedient expedient = pluginHelper.arxiuExpedientConsultar(expedientEntity);
		boolean carpetaExistsInArxiu = false;
		String carpetaUuid = null;
		if (expedient.getContinguts() != null) {
			for (ContingutArxiu contingutArxiu : expedient.getContinguts()) {
				String replacedNom = nom.replace("/", "_");
				if (contingutArxiu.getTipus() == ContingutTipus.CARPETA &&
						contingutArxiu.getNom().equals(replacedNom)) {
					carpetaExistsInArxiu = true;
					carpetaUuid = contingutArxiu.getIdentificador();
				}
			}
		}
		if (carpetaExistsInDB && carpetaExistsInArxiu && carpetaEntity.getArxiuUuid() == null) {
			carpetaEntity.updateArxiu(carpetaUuid);
		}
		if (!carpetaExistsInDB || !carpetaExistsInArxiu) {
			CarpetaDto carpetaDto = carpetaHelper.create(
					entitatId,
					expedientEntity.getId(),
					nom,
					carpetaExistsInDB,
					carpetaId,
					carpetaExistsInArxiu,
					carpetaUuid);
			carpetaId = carpetaDto.getId();
		}
		return carpetaId;
	}

//	public void comprovarSiExpedientAmbMateixNom(
//			MetaExpedientEntity metaExpedient,
//			ContingutEntity contingutPare,
//			String nom,
//			Long id,
//			Class<?> objectClass) {
//		ExpedientEntity expedient = expedientRepository.findByMetaExpedientAndPareAndNomAndEsborrat(
//				metaExpedient,
//				contingutPare,
//				nom,
//				0);
//		if (expedient != null) {
//			throw new ValidationException(
//					id,
//					objectClass,
//					"Ja existeix un altre expedient amb el mateix tipus i nom");
//		}
//	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientHelper.class);

}