/**
 * 
 */
package es.caib.ripea.core.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.RegistreAnotacioDto;
import es.caib.ripea.core.api.dto.RegistreDocumentDto;
import es.caib.ripea.core.api.dto.RegistreInteressatDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.RegistreService;
import es.caib.ripea.core.entity.BustiaEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.RegistreAccioEnum;
import es.caib.ripea.core.entity.RegistreDocumentEntity;
import es.caib.ripea.core.entity.RegistreDocumentTipusEnum;
import es.caib.ripea.core.entity.RegistreDocumentValidesaEnum;
import es.caib.ripea.core.entity.RegistreDocumentacioFisicaTipusEnum;
import es.caib.ripea.core.entity.RegistreEntity;
import es.caib.ripea.core.entity.RegistreInteressatCanalEnum;
import es.caib.ripea.core.entity.RegistreInteressatDocumentTipusEnum;
import es.caib.ripea.core.entity.RegistreInteressatEntity;
import es.caib.ripea.core.entity.RegistreMovimentEntity;
import es.caib.ripea.core.entity.RegistreTipusEnum;
import es.caib.ripea.core.entity.RegistreTransportTipusEnum;
import es.caib.ripea.core.helper.BustiaHelper;
import es.caib.ripea.core.helper.CacheHelper;
import es.caib.ripea.core.helper.EmailHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.PermisosHelper;
import es.caib.ripea.core.helper.UsuariHelper;
import es.caib.ripea.core.repository.BustiaRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.RegistreDocumentRepository;
import es.caib.ripea.core.repository.RegistreInteressatRepository;
import es.caib.ripea.core.repository.RegistreMovimentRepository;
import es.caib.ripea.core.repository.RegistreRepository;

/**
 * Implementació dels mètodes per a gestionar anotacions
 * de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class RegistreServiceImpl implements RegistreService {

	@Resource
	private RegistreRepository registreRepository;
	@Resource
	private RegistreInteressatRepository registreInteressatRepository;
	@Resource
	private RegistreDocumentRepository registreDocumentRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private BustiaRepository bustiaRepository;
	@Resource
	private RegistreMovimentRepository registreMovimentRepository;

	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private EmailHelper emailHelper;
	@Resource
	private CacheHelper cacheHelper;
	@Resource
	private BustiaHelper bustiaHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private UsuariHelper usuariHelper;



	@Transactional
	@Override
	public void create(
			Long entitatId,
			RegistreAnotacioDto registre) {
		logger.debug("Creant nova anotació de registre ("
				+ "entitatId=" + entitatId + ", "
				+ "registre=" + registre + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false);
		// Cercam la bústia destinatària de l'anotació
		BustiaEntity bustia = comprovarBustiaByUnitatCodiAndPerDefecteTrue(
				entitat,
				registre.getEntitatCodi());
		// Donam d'alta el registre
		RegistreEntity registreEntity = RegistreEntity.getBuilder(
				(registre.getAccio() != null) ? RegistreAccioEnum.valueOf(registre.getAccio().name()) : null,
				(registre.getTipus() != null) ? RegistreTipusEnum.valueOf(registre.getTipus().name()) : null,
				registre.getEntitatCodi(),
				registre.getEntitatNom(),
				registre.getNumero(),
				registre.getData(),
				registre.getAssumpteResum(),
				registre.getAssumpteCodi(),
				registre.getAssumpteReferencia(),
				registre.getAssumpteNumExpedient(),
				(registre.getTransportTipus() != null) ? RegistreTransportTipusEnum.valueOf(registre.getTransportTipus().name()) : null,
				registre.getTransportNumero(),
				registre.getUsuariNom(),
				registre.getUsuariContacte(),
				registre.getAplicacioEmissora(),
				(registre.getDocumentacioFisica() != null) ? RegistreDocumentacioFisicaTipusEnum.valueOf(registre.getDocumentacioFisica().name()) : null,
				registre.getObservacions(),
				entitat,
				registre.isProva()).build();
		registreEntity.updateContenidor(bustia);
		registreRepository.save(registreEntity);
		// Donam d'alta els seus interessats
		if (registre.getInteressats() != null) {
			for (RegistreInteressatDto interessat: registre.getInteressats()) {
				RegistreInteressatEntity interessatEntity = RegistreInteressatEntity.getBuilder(
						(interessat.getDocumentTipus() != null) ? RegistreInteressatDocumentTipusEnum.valueOf(interessat.getDocumentTipus().name()) : null,
						interessat.getDocumentNum(),
						interessat.getNom(),
						interessat.getLlinatge1(),
						interessat.getLlinatge2(),
						interessat.getRaoSocial(),
						interessat.getPais(),
						interessat.getProvincia(),
						interessat.getMunicipi(),
						interessat.getAdresa(),
						interessat.getCodiPostal(),
						interessat.getEmail(),
						interessat.getTelefon(),
						interessat.getEmailHabilitat(),
						(interessat.getCanalPreferent() != null) ? RegistreInteressatCanalEnum.valueOf(interessat.getCanalPreferent().name()) : null,
						interessat.getObservacions(),
						registreEntity).build();
				if (interessat.getRepresentantNom() != null || interessat.getRepresentantDocumentNum() != null) {
					interessatEntity.updateDadesRepresentant(
							(interessat.getRepresentantDocumentTipus() != null) ? RegistreInteressatDocumentTipusEnum.valueOf(interessat.getRepresentantDocumentTipus().name()) : null,
							interessat.getRepresentantDocumentNum(),
							interessat.getRepresentantNom(),
							interessat.getRepresentantLlinatge1(),
							interessat.getRepresentantLlinatge2(),
							interessat.getRepresentantRaoSocial(),
							interessat.getRepresentantPais(),
							interessat.getRepresentantProvincia(),
							interessat.getRepresentantMunicipi(),
							interessat.getRepresentantAdresa(),
							interessat.getRepresentantCodiPostal(),
							interessat.getRepresentantEmail(),
							interessat.getRepresentantTelefon(),
							interessat.getRepresentantEmailHabilitat(),
							(interessat.getRepresentantCanalPreferent() != null) ? RegistreInteressatCanalEnum.valueOf(interessat.getRepresentantCanalPreferent().name()) : null);
				}
				registreInteressatRepository.save(interessatEntity);
			}
		}
		// Donam d'alta els seus annexos
		if (registre.getAnnexos() != null) {
			for (RegistreDocumentDto annex: registre.getAnnexos()) {
				RegistreDocumentEntity documentEntity = RegistreDocumentEntity.getBuilder(
						annex.getFitxerNom(),
						annex.getIdentificador(),
						(annex.getValidesa() != null) ? RegistreDocumentValidesaEnum.valueOf(annex.getValidesa().name()) : null,
						(annex.getTipus() != null) ? RegistreDocumentTipusEnum.valueOf(annex.getTipus().name()) : null,
						annex.getGestioDocumentalId(),
						annex.getIndentificadorDocumentFirmat(),
						annex.getObservacions(),
						registreEntity).build();
				registreDocumentRepository.save(documentEntity);
			}
		}
		// Registra el moviment
		RegistreMovimentEntity registreMoviment = RegistreMovimentEntity.getBuilder(
				registreEntity,
				bustia,
				usuariHelper.getUsuariAutenticat(),
				null).build();
		registreMovimentRepository.save(registreMoviment);
		// Refrescam cache usuaris bústia
		bustiaHelper.evictElementsPendentsBustia(
				entitat,
				bustia);
		// Avisam per correu als responsables
		emailHelper.emailBustiaPendentRegistre(
				bustia,
				registreEntity);
	}

	@Transactional
	@Override
	public void afegirAExpedient(
			Long entitatId,
			Long expedientId,
			Long registreId) {
		logger.debug("Afegir anotació de registre a l'expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "expedientId=" + expedientId + ", "
				+ "registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitat,
				null,
				expedientId);
		RegistreEntity registre = entityComprovarHelper.comprovarRegistre(
				entitat,
				registreId,
				null);
		bustiaHelper.evictElementsPendentsBustiaPerRegistre(
				entitat,
				registre);
		registre.updateContenidor(expedient);
	}

	@Transactional
	@Override
	public void rebutjar(
			Long entitatId,
			Long bustiaId,
			Long registreId,
			String motiu) {
		logger.debug("Rebutjar anotació de registre a la bústia ("
				+ "entitatId=" + entitatId + ", "
				+ "bustiaId=" + bustiaId + ", "
				+ "registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
				entitat,
				bustiaId,
				true);
		RegistreEntity registre = entityComprovarHelper.comprovarRegistre(
				entitat,
				registreId,
				null);
		if (!registre.getContenidor().equals(bustia)) {
			logger.error("No s'ha trobat el registre a dins la bústia (" +
					"bustiaId=" + bustiaId + "," +
					"registreId=" + registreId + ")");
			throw new ValidationException(
					registreId,
					RegistreEntity.class,
					"La bústia especificada (id=" + bustiaId + ") no coincideix amb la bústia de l'anotació de registre");
		}
		registre.updateMotiuRebuig(motiu);
		// Refrescam cache usuaris bústia
		bustiaHelper.evictElementsPendentsBustia(
				entitat,
				bustia);
	}



	private BustiaEntity comprovarBustiaByUnitatCodiAndPerDefecteTrue(
			EntitatEntity entitat,
			String unitatCodi) {
		BustiaEntity bustia = bustiaRepository.findByEntitatAndUnitatCodiAndPerDefecteTrue(
				entitat,
				unitatCodi);
		if (bustia == null) {
			logger.error("No s'ha trobat la bústia per defecte (" +
					"entitatId=" + entitat.getId() + ", " + 
					"unitatCodi=" + unitatCodi + ")");
			throw new NotFoundException(
					"(entitatId=" + entitat.getId() + ", unitatCodi=" + unitatCodi + ")",
					BustiaEntity.class);
		}
		return bustia;
	}

	

	private static final Logger logger = LoggerFactory.getLogger(RegistreServiceImpl.class);

}
