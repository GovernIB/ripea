/**
 * 
 */
package es.caib.ripea.core.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jopendocument.dom.spreadsheet.SpreadSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Persistable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientEstatEnumDto;
import es.caib.ripea.core.api.dto.ExpedientFiltreDto;
import es.caib.ripea.core.api.dto.ExpedientSelectorDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DadaEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.MetaDadaEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.helper.ContingutHelper;
import es.caib.ripea.core.helper.ContingutLogHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.CsvHelper;
import es.caib.ripea.core.helper.EmailHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.MessageHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PaginacioHelper.Converter;
import es.caib.ripea.core.helper.PermisosHelper;
import es.caib.ripea.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.helper.UsuariHelper;
import es.caib.ripea.core.repository.AlertaRepository;
import es.caib.ripea.core.repository.ContingutRepository;
import es.caib.ripea.core.repository.DadaRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.security.ExtendedPermission;

/**
 * Implementació dels mètodes per a gestionar expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ExpedientServiceImpl implements ExpedientService {

	@Autowired
	private MetaExpedientRepository metaExpedientRepository;
	@Autowired
	private ExpedientRepository expedientRepository;
	@Autowired
	private DadaRepository dadaRepository;
	@Autowired
	private AlertaRepository alertaRepository;
	@Autowired
	private ContingutRepository contingutRepository;

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private ContingutHelper contingutHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private UsuariHelper usuariHelper;
	@Autowired
	private EmailHelper emailHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private CsvHelper csvHelper;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private MessageHelper messageHelper;

	@Transactional
	@Override
	public ExpedientDto create(
			Long entitatId,
			Long metaExpedientId,
			Long pareId,
			Integer any,
			String nom) {
		logger.debug("Creant nou expedient (" +
				"entitatId=" + entitatId + ", " +
				"metaExpedientId=" + metaExpedientId + ", " +
				"pareId=" + pareId + ", " +
				"any=" + any + ", " +
				"nom=" + nom + ")");
		if (metaExpedientId == null) {
			throw new ValidationException(
					"<creacio>",
					ExpedientEntity.class,
					"No es pot crear un expedient sense un meta-expedient associat");
		}
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
				metaExpedientId,
				false,
				false,
				true,
				false);
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
		contingutHelper.comprovarNomValid(
				contingutPare,
				nom,
				"<creacio>",
				ExpedientEntity.class);
		ExpedientEntity expedient = contingutHelper.crearNouExpedient(
				nom,
				metaExpedient,
				contingutPare,
				metaExpedient.getEntitat(),
				"1.0",
				metaExpedient.getEntitat().getUnitatArrel(),
				new Date(),
				any,
				true);
		contingutLogHelper.logCreacio(
				expedient,
				false,
				false);
		ExpedientDto dto = toExpedientDto(
				expedient,
				true);
		contingutHelper.arxiuPropagarModificacio(
				expedient,
				null);
		return dto;
	}

	@Transactional
	@Override
	public ExpedientDto update(
			Long entitatId,
			Long id,
			String nom) {
		logger.debug("Actualitzant dades de l'expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ", "
				+ "nom=" + nom + ")");
		contingutHelper.comprovarContingutDinsExpedientModificable(
				entitatId,
				id,
				false,
				true,
				false,
				false);
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				false,
				false,
				true,
				false,
				false);
		contingutHelper.comprovarNomValid(
				expedient.getPare(),
				nom,
				id,
				ExpedientEntity.class);
		String nomOriginal = expedient.getNom();
		expedient.update(nom);
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.MODIFICACIO,
				(!nomOriginal.equals(expedient.getNom())) ? expedient.getNom() : null,
				null,
				false,
				false);
		ExpedientDto dto = toExpedientDto(
				expedient,
				true);
		contingutHelper.arxiuPropagarModificacio(
				expedient,
				null);
		return dto;
	}

	@Transactional(readOnly = true)
	@Override
	public ExpedientDto findById(
			Long entitatId,
			Long id) {
		logger.debug("Obtenint l'expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				false,
				true,
				false,
				false,
				false);
		return toExpedientDto(
				expedient,
				true);
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ExpedientDto> findAmbFiltreAdmin(
			Long entitatId,
			ExpedientFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consultant els expedients segons el filtre per admins ("
				+ "entitatId=" + entitatId + ", "
				+ "filtre=" + filtre + ", "
				+ "paginacioParams=" + paginacioParams + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		return findAmbFiltrePaginat(
				entitatId,
				filtre,
				paginacioParams,
				true,
				false);
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ExpedientDto> findAmbFiltreUser(
			Long entitatId,
			ExpedientFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consultant els expedients segons el filtre per usuaris ("
				+ "entitatId=" + entitatId + ", "
				+ "filtre=" + filtre + ", "
				+ "paginacioParams=" + paginacioParams + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		return findAmbFiltrePaginat(
				entitatId,
				filtre,
				paginacioParams,
				false,
				true);
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ExpedientDto> findAmbFiltreNoRelacionat(
			Long entitatId,
			ExpedientFiltreDto filtre,
			Long expedientId,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consultant els expedients segons el filtre per usuaris ("
				+ "entitatId=" + entitatId + ", "
				+ "filtre=" + filtre + ", "
				+ "paginacioParams=" + paginacioParams + 
				"id del expedient relacionat" + expedientId +")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		
		return findAmbFiltrePaginat(
				entitatId,
				filtre,
				paginacioParams,
				false,
				true,
				expedientId);
	}
	
	
	
	
	@Transactional
	@Override
	public List<ExpedientDto> findByEntitatAndMetaExpedient(
			Long entitatId,
			Long metaExpedientId) {
		logger.debug("Consultant els expedients("
				+ "entitatId=" + entitatId + ", "
				+ "metaExpedientId=" + metaExpedientId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		MetaExpedientEntity metaExpedient = null;
		if (metaExpedientId != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
					entitat,
					metaExpedientId,
					false,
					true,
					false,
					false);
		}

		
		List<ExpedientEntity> expedientsEnt = expedientRepository.findByEntitatAndMetaExpedient(
				entitat, 
				metaExpedient);
		
		List<ExpedientDto> expedientsDto = new ArrayList<>(); 
		for(ExpedientEntity exp: expedientsEnt){
			expedientsDto.add(toExpedientDto(
					exp,
					true));
		}
		
		return expedientsDto;
		
	}
	

	@Transactional(readOnly = true)
	@Override
	public List<ExpedientSelectorDto> findPerUserAndTipus(
			Long entitatId,
			Long metaExpedientId) {
		logger.debug("Consultant els expedients segons el tipus per usuaris ("
				+ "entitatId=" + entitatId + ", "
				+ "metaExpedientId=" + metaExpedientId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		MetaExpedientEntity metaExpedient = null;
		if (metaExpedientId != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
					entitat,
					metaExpedientId,
					false,
					true,
					false,
					false);
		}
		List<MetaExpedientEntity> metaExpedientsPermesos = metaExpedientRepository.findByEntitatOrderByNomAsc(
				entitat);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		permisosHelper.filterGrantedAll(
				metaExpedientsPermesos,
				new ObjectIdentifierExtractor<MetaExpedientEntity>() {
					@Override
					public Long getObjectIdentifier(MetaExpedientEntity metaExpedient) {
						return metaExpedient.getId();
					}
				},
				MetaNodeEntity.class,
				new Permission[] {ExtendedPermission.READ},
				auth);
		if (!metaExpedientsPermesos.isEmpty()) {
			return conversioTipusHelper.convertirList(
					expedientRepository.findByEntitatAndMetaExpedientOrderByNomAsc(
							entitat, 
							metaExpedientsPermesos,
							metaExpedient == null,
							metaExpedient), 
					ExpedientSelectorDto.class);
		} else {
			return new ArrayList<ExpedientSelectorDto>();
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<Long> findIdsAmbFiltre(
			Long entitatId,
			ExpedientFiltreDto filtre) throws NotFoundException {
		logger.debug("Consultant els ids d'expedient segons el filtre ("
				+ "entitatId=" + entitatId + ", "
				+ "filtre=" + filtre + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		return findIdsAmbFiltrePaginat(
				entitatId,
				filtre,
				false,
				true);
	}

	@Transactional
	@Override
	public void agafarUser(
			Long entitatId,
			Long id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Agafant l'expedient com a usuari ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ", "
				+ "usuari=" + auth.getName() + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				false,
				false,
				true,
				false,
				false);
		ExpedientEntity expedientSuperior = contingutHelper.getExpedientSuperior(
				expedient,
				false,
				false,
				false);
		if (expedientSuperior != null) {
			logger.error("No es pot agafar un expedient no arrel (id=" + id + ")");
			throw new ValidationException(
					id,
					ExpedientEntity.class,
					"No es pot agafar un expedient no arrel");
		}
		// Agafa l'expedient. Si l'expedient pertany a un altre usuari li pren
		UsuariEntity usuariOriginal = expedient.getAgafatPer();
		UsuariEntity usuariNou = usuariHelper.getUsuariAutenticat();
		expedient.updateAgafatPer(usuariNou);
		if (usuariOriginal != null) {
			// Avisa a l'usuari que li han pres
			emailHelper.emailUsuariContingutAgafatSensePermis(
					expedient,
					usuariOriginal,
					usuariNou);
		}
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.AGAFAR,
				null,
				null,
				false,
				false);
	}
	
	@Transactional
	@Override
	public void agafarAdmin(
			Long entitatId,
			Long arxiuId,
			Long id,
			String usuariCodi) {
		logger.debug("Agafant l'expedient com a administrador ("
				+ "entitatId=" + entitatId + ", "
				+ "arxiuId=" + arxiuId + ", "
				+ "id=" + id + ", "
				+ "usuariCodi=" + usuariCodi + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				false,
				false,
				false,
				false,
				false);
		ExpedientEntity expedientSuperior = contingutHelper.getExpedientSuperior(
				expedient,
				false,
				false,
				false);
		if (expedientSuperior != null) {
			throw new ValidationException(
					id,
					ExpedientEntity.class,
					"No es pot agafar un expedient no arrel");
		}
		// Agafa l'expedient. Si l'expedient pertany a un altre usuari li pren
		UsuariEntity usuariOriginal = expedient.getAgafatPer();
		UsuariEntity usuariNou = usuariHelper.getUsuariAutenticat();
		expedient.updateAgafatPer(usuariNou);
		if (usuariOriginal != null) {
			// Avisa a l'altre l'usuari que li han pres
			emailHelper.emailUsuariContingutAgafatSensePermis(
					expedient,
					usuariOriginal,
					usuariNou);
		}
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.AGAFAR,
				null,
				null,
				false,
				false);
	}

	@Transactional
	@Override
	public void alliberarUser(
			Long entitatId,
			Long id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Alliberant l'expedient com a usuari ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ", "
				+ "usuari=" + auth.getName() + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				true,
				false,
				false,
				false,
				false);
		expedient.updateAgafatPer(null);
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.ALLIBERAR,
				null,
				null,
				false,
				false);
	}

	@Transactional
	@Override
	public void alliberarAdmin(
			Long entitatId,
			Long id) {
		logger.debug("Alliberant l'expedient com a administrador ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				false,
				false,
				false,
				false,
				false);
		expedient.updateAgafatPer(null);
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.ALLIBERAR,
				null,
				null,
				false,
				false);
	}

	@Transactional
	@Override
	public void tancar(
			Long entitatId,
			Long id,
			String motiu) {
		logger.debug("Tancant l'expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ","
				+ "motiu=" + motiu + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				true,
				false,
				true,
				false,
				false);
		expedient.updateEstat(
				ExpedientEstatEnumDto.TANCAT,
				motiu);
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.TANCAMENT,
				null,
				null,
				false,
				false);
		if (pluginHelper.isArxiuPluginActiu()) {
			pluginHelper.arxiuExpedientTancar(expedient);
		}
	}

	@Transactional
	@Override
	public void reobrir(
			Long entitatId,
			Long id) {
		logger.debug("Reobrint l'expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				true,
				false,
				true,
				false,
				false);
		expedient.updateEstat(
				ExpedientEstatEnumDto.OBERT,
				null);
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.REOBERTURA,
				null,
				null,
				false,
				false);
		if (pluginHelper.isArxiuPluginActiu()) {
			pluginHelper.arxiuExpedientReobrir(expedient);
		}
	}

	@Transactional
	@Override
	@SuppressWarnings("serial")
	public void relacioCreate(
			Long entitatId,
			final Long id,
			final Long relacionatId) {
		logger.debug("Relacionant l'expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ", "
				+ "relacionatId=" + relacionatId + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				true,
				false,
				true,
				false,
				false);
		ExpedientEntity relacionat = entityComprovarHelper.comprovarExpedient(
				entitatId,
				relacionatId,
				true,
				false,
				true,
				false,
				false);
		expedient.addRelacionat(relacionat);
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.MODIFICACIO,
				new Persistable<String>() {
					@Override
					public String getId() {
						return id + "#" + relacionatId;
					}
					@Override
					public boolean isNew() {
						return false;
					}
				},
				LogObjecteTipusEnumDto.RELACIO,
				LogTipusEnumDto.CREACIO,
				id.toString(),
				relacionatId.toString(),
				false,
				false);
	}

	@Transactional
	@Override
	@SuppressWarnings("serial")
	public boolean relacioDelete(
			Long entitatId,
			final Long id,
			final Long relacionatId) {
		logger.debug("Esborrant la relació de l'expedient amb un altre expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ", "
				+ "relacionatId=" + relacionatId + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				true,
				false,
				true,
				false,
				false);
		ExpedientEntity relacionat = entityComprovarHelper.comprovarExpedient(
				entitatId,
				relacionatId,
				true,
				false,
				true,
				false,
				false);
		boolean trobat = true;
		if (expedient.getRelacionatsAmb().contains(relacionat)) {
			expedient.removeRelacionat(relacionat);
		} else if (relacionat.getRelacionatsAmb().contains(expedient)) {
			relacionat.removeRelacionat(expedient);
		} else {
			trobat = false;
		}
		if (trobat) {
			contingutLogHelper.log(
					expedient,
					LogTipusEnumDto.MODIFICACIO,
					new Persistable<String>() {
						@Override
						public String getId() {
							return id + "#" + relacionatId;
						}
						@Override
						public boolean isNew() {
							return false;
						}
					},
					LogObjecteTipusEnumDto.RELACIO,
					LogTipusEnumDto.ELIMINACIO,
					id.toString(),
					relacionatId.toString(),
					false,
					false);
		}
		return trobat;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ExpedientDto> relacioFindAmbExpedient(
			Long entitatId,
			Long expedientId) {
		logger.debug("Obtenint la llista d'expedients relacionats (" +
				"entitatId=" + entitatId + ", " +
				"expedientId=" + expedientId + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				false,
				true,
				false,
				false);
		List<ExpedientEntity> relacionats = new ArrayList<ExpedientEntity>();
		relacionats.addAll(expedient.getRelacionatsAmb());
		relacionats.addAll(expedient.getRelacionatsPer());
		Collections.sort(
				relacionats, 
				new Comparator<ExpedientEntity>() {
				    @Override
				    public int compare(ExpedientEntity e1, ExpedientEntity e2) {
				        return e1.getNom().compareTo(e2.getNom());
				    }
				});
		List<ExpedientDto> relacionatsDto = new ArrayList<ExpedientDto>();
		for (ExpedientEntity e: relacionats)
			relacionatsDto.add(toExpedientDto(e, false));		
		return relacionatsDto;
	}

	@Transactional(readOnly = true)
	@Override
	public FitxerDto exportacio(
			Long entitatId,
			Long metaExpedientId,
			Collection<Long> expedientIds,
			String format) throws IOException {
		logger.debug("Exportant informació dels expedients (" +
				"entitatId=" + entitatId + ", " +
				"metaExpedientId=" + metaExpedientId + ", " +
				"expedientIds=" + expedientIds + ", " +
				"format=" + format + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
				metaExpedientId,
				true,
				false,
				false,
				false);
		List<ExpedientEntity> expedients = expedientRepository.findByEntitatAndAndMetaNodeAndIdInOrderByIdAsc(
				metaExpedient.getEntitat(),
				metaExpedient,
				expedientIds);
		List<MetaDadaEntity> metaDades = dadaRepository.findDistinctMetaDadaByNodeIdInOrderByMetaDadaCodiAsc(expedientIds);
		List<DadaEntity> dades = dadaRepository.findByNodeIdInOrderByNodeIdAscMetaDadaCodiAsc(expedientIds);
		int numColumnes = 5 + metaDades.size();
		String[] columnes = new String[numColumnes];
		columnes[0] = messageHelper.getMessage("expedient.service.exportacio.numero");
		columnes[1] = messageHelper.getMessage("expedient.service.exportacio.titol");
		columnes[2] = messageHelper.getMessage("expedient.service.exportacio.estat");
		columnes[3] = messageHelper.getMessage("expedient.service.exportacio.datcre");
		columnes[4] = messageHelper.getMessage("expedient.service.exportacio.idnti");
		for (int i = 0; i < metaDades.size(); i++) {
			MetaDadaEntity metaDada = metaDades.get(i);
			columnes[5 + i] = metaDada.getNom() + " (" + metaDada.getCodi() + ")";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		List<String[]> files = new ArrayList<String[]>();
		int dadesIndex = 0;
		for (ExpedientEntity expedient: expedients) {
			String[] fila = new String[numColumnes];
			fila[0] = expedient.getNumero();
			fila[1] = expedient.getNom();
			fila[2] = expedient.getEstat().name();
			fila[3] = sdf.format(expedient.getCreatedDate().toDate());
			fila[4] = expedient.getNtiIdentificador();
			if (!dades.isEmpty()) {
				DadaEntity dadaActual = dades.get(dadesIndex);
				if (dadaActual.getNode().getId().equals(expedient.getId())) {
					for (int i = 0; i < metaDades.size(); i++) {
						MetaDadaEntity metaDada = metaDades.get(i);
						int dadesIndexIncrement = 1;
						while (dadaActual.getNode().getId().equals(expedient.getId())) {
							if (dadaActual.getMetaDada().getCodi().equals(metaDada.getCodi())) {
								break;
							}
							dadaActual = dades.get(dadesIndex + dadesIndexIncrement++);
						}
						if (dadaActual.getMetaDada().getCodi().equals(metaDada.getCodi())) {
							fila[5 + i] = dadaActual.getValorComString();
						}
					}
				}
			}
			files.add(fila);
		}
		FitxerDto fitxer = new FitxerDto();
		if ("ODS".equalsIgnoreCase(format)) {
			Object[][] filesArray = files.toArray(new Object[files.size()][numColumnes]);
			TableModel model = new DefaultTableModel(filesArray, columnes);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			SpreadSheet.createEmpty(model).getPackage().save(baos);
			fitxer.setNom("exportacio.ods");
			fitxer.setContentType("application/vnd.oasis.opendocument.spreadsheet");
			fitxer.setContingut(baos.toByteArray());
		} else if ("CSV".equalsIgnoreCase(format)) {
			fitxer.setNom("exportacio.csv");
			fitxer.setContentType("text/csv");
			StringBuilder sb = new StringBuilder();
			csvHelper.afegirLinia(sb, columnes, ';');
			for (String[] fila: files) {
				csvHelper.afegirLinia(sb, fila, ';');
			}
			fitxer.setContingut(sb.toString().getBytes());
		} else {
			throw new ValidationException("Format de fitxer no suportat: " + format);
		}
		return fitxer;
	}

	
	private PaginaDto<ExpedientDto> findAmbFiltrePaginat(
			Long entitatId,
			ExpedientFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			boolean accesAdmin,
			boolean comprovarAccesMetaExpedients) {
	
		return findAmbFiltrePaginat(
				entitatId,
				filtre,
				paginacioParams,
				accesAdmin,
				comprovarAccesMetaExpedients,
				null
				);
	}
	
	
		


	private PaginaDto<ExpedientDto> findAmbFiltrePaginat(
			Long entitatId,
			ExpedientFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			boolean accesAdmin,
			boolean comprovarAccesMetaExpedients,
			Long expedientId) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				(!accesAdmin),
				accesAdmin,
				false);
		MetaExpedientEntity metaExpedient = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
					entitat,
					filtre.getMetaExpedientId(),
					true,
					false,
					false,
					false);
		}
		List<MetaExpedientEntity> metaExpedientsPermesos = metaExpedientRepository.findByEntitatOrderByNomAsc(
				entitat);
		if (comprovarAccesMetaExpedients) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			permisosHelper.filterGrantedAll(
					metaExpedientsPermesos,
					new ObjectIdentifierExtractor<MetaExpedientEntity>() {
						@Override
						public Long getObjectIdentifier(MetaExpedientEntity metaExpedient) {
							return metaExpedient.getId();
						}
					},
					MetaNodeEntity.class,
					new Permission[] {ExtendedPermission.READ},
					auth);
		}
		if (!metaExpedientsPermesos.isEmpty()) {
			UsuariEntity agafatPer = null;
			if (filtre.isMeusExpedients()) {
				agafatPer = usuariHelper.getUsuariAutenticat();
			}
			
			// id of expedient clicked to get "Relacionar expedient" view, in this view only list of expedients not already related with clicked expedient should be shown 
			ExpedientEntity expedient=null;
			List <ExpedientEntity> expedientRelacionats=new ArrayList<>();
			if (expedientId!=null){
				expedient = entityComprovarHelper.comprovarExpedient(
						entitatId,
						expedientId,
						false,
						false,
						true,
						false,
						false);
				
				expedientRelacionats = expedientRepository.findExpedientsRelacionats(expedient);
				expedientRelacionats.add(expedient);
			}
			
			
			Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
			ordenacioMap.put("numero", new String[] {"codi", "any", "sequencia"});
			Page<ExpedientEntity> paginaExpedients;
			
			if(!expedientRelacionats.isEmpty()){
				paginaExpedients = expedientRepository.findByEntitatAndFiltre(
						entitat,
						metaExpedientsPermesos,
						metaExpedient == null,
						metaExpedient,
						filtre.getNumero() == null || "".equals(filtre.getNumero().trim()),
						filtre.getNumero() == null ? "" : filtre.getNumero(),
						filtre.getNom() == null || filtre.getNom().isEmpty(),
						filtre.getNom() == null ? "" : filtre.getNom(),
						filtre.getDataCreacioInici() == null,
						filtre.getDataCreacioInici(),
						filtre.getDataCreacioFi() == null,
						filtre.getDataCreacioFi(),
						filtre.getDataTancatInici() == null,
						filtre.getDataTancatInici(),
						filtre.getDataTancatFi() == null,
						filtre.getDataTancatFi(),
						filtre.getEstat() == null,
						filtre.getEstat(),
						agafatPer == null,
						agafatPer,
						filtre.getSearch() == null,
						filtre.getSearch() == null ? "" : filtre.getSearch(),
						filtre.getTipusId() == null,
						filtre.getTipusId(),
						expedientRelacionats,
						paginacioHelper.toSpringDataPageable(
								paginacioParams,
								ordenacioMap));
				
			} else {
				paginaExpedients = expedientRepository.findByEntitatAndFiltre(
						entitat,
						metaExpedientsPermesos,
						metaExpedient == null,
						metaExpedient,
						filtre.getNumero() == null || "".equals(filtre.getNumero().trim()),
						filtre.getNumero() == null ? "" : filtre.getNumero(),
						filtre.getNom() == null || filtre.getNom().isEmpty(),
						filtre.getNom() == null ? "" : filtre.getNom(),
						filtre.getDataCreacioInici() == null,
						filtre.getDataCreacioInici(),
						filtre.getDataCreacioFi() == null,
						filtre.getDataCreacioFi(),
						filtre.getDataTancatInici() == null,
						filtre.getDataTancatInici(),
						filtre.getDataTancatFi() == null,
						filtre.getDataTancatFi(),
						filtre.getEstat() == null,
						filtre.getEstat(),
						agafatPer == null,
						agafatPer,
						filtre.getSearch() == null,
						filtre.getSearch() == null ? "" : filtre.getSearch(),
						filtre.getTipusId() == null,
						filtre.getTipusId(),
						paginacioHelper.toSpringDataPageable(
								paginacioParams,
								ordenacioMap));
			}
			
			
			PaginaDto<ExpedientDto> result = paginacioHelper.toPaginaDto(
					paginaExpedients,
					ExpedientDto.class,
					new Converter<ExpedientEntity, ExpedientDto>() {
						@Override
						public ExpedientDto convert(ExpedientEntity source) {
							return toExpedientDto(
									source,
									true);
						}
					});
			
			for (ExpedientDto e: result) {
				boolean enAlerta = alertaRepository.countByLlegidaAndContingutId(
						false,
						e.getId()
						) > 0;
				List<ContingutEntity> continguts = contingutRepository.findRegistresByPareId(e.getId());
				if(!continguts.isEmpty() && alertaRepository.countByLlegidaAndContinguts(
						false,
						continguts
						) > 0) enAlerta = true;
				
				e.setAlerta(enAlerta);
			}
			
			return result;
		} else {
			return paginacioHelper.getPaginaDtoBuida(
					ExpedientDto.class);
		}
	}

	private List<Long> findIdsAmbFiltrePaginat(
			Long entitatId,
			ExpedientFiltreDto filtre,
			boolean accesAdmin,
			boolean comprovarAccesMetaExpedients) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				(!accesAdmin),
				accesAdmin,
				false);
		MetaExpedientEntity metaExpedient = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
					entitat,
					filtre.getMetaExpedientId(),
					true,
					false,
					false,
					false);
		}
		List<MetaExpedientEntity> metaExpedientsPermesos = metaExpedientRepository.findByEntitatOrderByNomAsc(
				entitat);
		if (comprovarAccesMetaExpedients) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			permisosHelper.filterGrantedAll(
					metaExpedientsPermesos,
					new ObjectIdentifierExtractor<MetaExpedientEntity>() {
						@Override
						public Long getObjectIdentifier(MetaExpedientEntity metaExpedient) {
							return metaExpedient.getId();
						}
					},
					MetaNodeEntity.class,
					new Permission[] {ExtendedPermission.READ},
					auth);
		}
		if (!metaExpedientsPermesos.isEmpty()) {
			return expedientRepository.findIdByEntitatAndFiltre(
					entitat,
					metaExpedientsPermesos,
					metaExpedient == null,
					metaExpedient,
					filtre.getNumero() == null || "".equals(filtre.getNumero().trim()),
					filtre.getNumero(),
					filtre.getNom() == null || filtre.getNom().isEmpty(),
					filtre.getNom(),
					filtre.getDataCreacioInici() == null,
					filtre.getDataCreacioInici(),
					filtre.getDataCreacioFi() == null,
					filtre.getDataCreacioFi(),
					filtre.getDataTancatInici() == null,
					filtre.getDataTancatInici(),
					filtre.getDataTancatFi() == null,
					filtre.getDataTancatFi(),
					filtre.getEstat() == null,
					filtre.getEstat());
		} else {
			return new ArrayList<Long>();
		}
	}

	private ExpedientDto toExpedientDto(
			ExpedientEntity expedient,
			boolean ambPathIPermisos) {
		ExpedientDto expedientDto = (ExpedientDto) contingutHelper.toContingutDto(
				expedient,
				ambPathIPermisos,
				false,
				false,
				false,
				ambPathIPermisos,
				false,
				false);
		return expedientDto;
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientServiceImpl.class);

}
