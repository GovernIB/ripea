package es.caib.ripea.service.helper;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.GrupEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.repository.GrupRepository;
import es.caib.ripea.persistence.repository.MetaExpedientRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.persistence.repository.command.GrupRepositoryCommnand;
import es.caib.ripea.service.intf.dto.GrupDto;
import es.caib.ripea.service.intf.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.service.intf.dto.LogTipusEnumDto;
import es.caib.ripea.service.intf.dto.PermisDto;
import es.caib.ripea.service.intf.dto.PrincipalTipusEnumDto;
import es.caib.ripea.service.intf.exception.NotFoundException;

@Component
public class GrupHelper {

	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private GrupRepository grupRepository;
	@Autowired private PermisosHelper permisosHelper;
	@Autowired private OrganGestorRepository organGestorRepository;
	@Autowired private MetaExpedientRepository metaExpedientRepository;
	@Autowired private OrganGestorHelper organGestorHelper;
	@Autowired private MetaExpedientHelper metaExpedientHelper;
	@Autowired private OrganGestorCacheHelper organGestorCacheHelper;
	@Autowired private GrupRepositoryCommnand grupRepositoryCommnand;
	@Autowired private ContingutLogHelper contingutLogHelper;

	public void relacionarAmbMetaExpedient(Long metaExpedientId, Long grupId, boolean marcarPerDefecte) {
		
		MetaExpedientEntity metaExpedientEntity = metaExpedientRepository.findById(metaExpedientId).get();
		GrupEntity grupEntity = grupRepository.findById(grupId).get();
		metaExpedientEntity.addGrup(grupEntity);
		if (marcarPerDefecte) {
			metaExpedientEntity.setGrupPerDefecte(grupEntity);
		}
		
		contingutLogHelper.logProcedimentObjecte(
				metaExpedientEntity.getId(),
				LogTipusEnumDto.VINCULAR_GRUP,
				grupEntity,
				LogObjecteTipusEnumDto.GRUP,
				LogTipusEnumDto.VINCULAR_GRUP,
				grupEntity.getCodi(),
				(marcarPerDefecte)?"El grup està assignat per defecte.":"El grup no està assignat per defecte.");
	}
	
	public void desvincularAmbMetaExpedient(Long entitatId, Long metaExpedientId, Long idGrup, String rolActual, Long organId) {
		MetaExpedientEntity metaExpedientEntity = metaExpedientRepository.findById(metaExpedientId).get();
		GrupEntity grupEntity = grupRepository.findById(idGrup).get();
		metaExpedientEntity.removeGrup(grupEntity);
		if (metaExpedientEntity.getGrupPerDefecte() != null && grupEntity.getId().equals(metaExpedientEntity.getGrupPerDefecte().getId())) {
			metaExpedientEntity.setGrupPerDefecte(null);
		}
		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedientEntity.getId(), organId);
		}
		contingutLogHelper.logProcedimentObjecte(
				metaExpedientEntity.getId(),
				LogTipusEnumDto.DESVINCULAR_GRUP,
				grupEntity,
				LogObjecteTipusEnumDto.GRUP,
				LogTipusEnumDto.DESVINCULAR_GRUP,
				grupEntity.getCodi(),
				(metaExpedientEntity.getGrupPerDefecte()==null)?"El procediment ha quedat sense grup per defecte.":null);
	}
	
	public void canviarGrupPerDefecte(Long metaExpedientId, Long idGrup) {
		MetaExpedientEntity metaExpedientEntity = metaExpedientRepository.findById(metaExpedientId).get();
		GrupEntity grupEntity = null;
		if (idGrup!=null) {
			grupEntity = grupRepository.findById(idGrup).get();
		}
		metaExpedientEntity.setGrupPerDefecte(grupEntity);
		contingutLogHelper.logProcedimentObjecte(
				metaExpedientEntity.getId(),
				LogTipusEnumDto.CANVI_GRUP_DEFAULT,
				grupEntity,
				LogObjecteTipusEnumDto.GRUP,
				LogTipusEnumDto.CANVI_GRUP_DEFAULT,
				(grupEntity==null)?null:grupEntity.getCodi(),
				(grupEntity==null)?"El procediment ha quedat sense grup per defecte.":null);
	}
	
	public GrupDto create(Long entitatId, GrupDto grupDto) throws NotFoundException {
		
		logger.debug("Creant un nou grup per l'entitat (entitatId=" + entitatId + ")");
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				false, 
				true);
		
		GrupEntity enitity = GrupEntity.getBuilder(
				grupDto.getCodi(),
				grupDto.getDescripcio(),
				entitat, 
				grupDto.getOrganGestorId() != null ?  organGestorRepository.findById(grupDto.getOrganGestorId()).orElse(null) : null).build();

		GrupDto dto = conversioTipusHelper.convertir(
				grupRepository.save(enitity),
				GrupDto.class);
		return dto;
	}
	
	@Transactional
	public void crearPermisosDeGrup(
			Long grupId, 
			PermisDto permis)  {
		GrupEntity grup = grupRepository.findById(grupId).get();
		PermisDto dto = new PermisDto();
		dto.setRead(true);
		dto.setPrincipalTipus(PrincipalTipusEnumDto.ROL);
		dto.setPrincipalNom(grup.getCodi());
		permisosHelper.updatePermis(grup.getId(), GrupEntity.class, dto);
	}
	
	public List<GrupEntity> findGrups(Long entitatId, Long organGestorId, Long metaExpedientId) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
		List<String> codisOrgansFills = null;

		if (organGestorId != null) {
			OrganGestorEntity organ = organGestorRepository.findById(organGestorId).get();
			codisOrgansFills = organGestorCacheHelper.getCodisOrgansFills(entitat.getCodi(), organ.getCodi());
		}
		
		return grupRepositoryCommnand.findByEntitatAndOrgan(
				entitat,
				metaExpedientId,
				codisOrgansFills);
	}
	
	public List<GrupEntity> findGrupsNoRelacionatAmbMetaExpedient(Long entitatId, Long metaExpedientId, Long adminOrganId) {
		
		List<GrupEntity> grups = grupRepository.findByEntitatId(entitatId);
		
		MetaExpedientEntity metaExpedient = metaExpedientRepository.findById(metaExpedientId).get();
		Long procedimentOrganId = metaExpedient.getOrganGestor() != null ? metaExpedient.getOrganGestor().getId() : null;
		List<GrupEntity> grupsProcedimentExisting = metaExpedient.getGrups();
		
		// remove grups already related to procediment
		for (Iterator<GrupEntity> iter = grups.iterator(); iter.hasNext();) {
			GrupEntity grup = iter.next();
			
			boolean contains = false;
			for (GrupEntity gr : grupsProcedimentExisting) {
				if (gr.getId().equals(grup.getId())) {
					contains = true;
					break;
				}
			}
			if (contains) {
				iter.remove();
			}
		}
		
		// if is called by administador d'organ only leave grups assigned to organ or descendents
		if (adminOrganId != null) {
			for (Iterator<GrupEntity> iter = grups.iterator(); iter.hasNext();) {
				GrupEntity grup = iter.next();
				if (grup.getOrganGestor() == null || !organGestorHelper.findParesIds(grup.getOrganGestor().getId(), true).contains(adminOrganId)) {
					iter.remove();
				}
			}
		} 
		
		// if procediment belongs to organ remove grups that belong to other organ
		if (procedimentOrganId != null) {
			for (Iterator<GrupEntity> iter = grups.iterator(); iter.hasNext();) {
				GrupEntity grup = iter.next();
				if (grup.getOrganGestor() != null && !organGestorHelper.findParesIds(grup.getOrganGestor().getId(), true).contains(procedimentOrganId)) {
					iter.remove();
				}
			}
		}
		
		return grups;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(GrupHelper.class);
}