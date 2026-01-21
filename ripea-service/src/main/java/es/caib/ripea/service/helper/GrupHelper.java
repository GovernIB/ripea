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
import es.caib.ripea.persistence.repository.GrupRepository;
import es.caib.ripea.persistence.repository.MetaExpedientRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.service.intf.dto.GrupDto;
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

	public void relacionarAmbMetaExpedient(Long metaExpedientId, Long grupId, boolean marcarPerDefecte) {
		MetaExpedientEntity metaExpedientEntity = metaExpedientRepository.getOne(metaExpedientId);
		GrupEntity grupEntity = grupRepository.getOne(grupId);
		metaExpedientEntity.addGrup(grupEntity);
		if (marcarPerDefecte) {
			metaExpedientEntity.setGrupPerDefecte(grupEntity);
		}
	}
	
	public void desvincularAmbMetaExpedient(Long entitatId, Long metaExpedientId, Long idGrup, String rolActual, Long organId) {
		MetaExpedientEntity metaExpedientEntity = metaExpedientRepository.getOne(metaExpedientId);
		GrupEntity grupEntity = HibernateHelper.deproxy(grupRepository.getOne(idGrup));
		metaExpedientEntity.removeGrup(grupEntity);
		if (metaExpedientEntity.getGrupPerDefecte() != null && grupEntity.getId().equals(metaExpedientEntity.getGrupPerDefecte().getId())) {
			metaExpedientEntity.setGrupPerDefecte(null);
		}
		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedientEntity.getId(), organId);
		}
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
		GrupEntity grup = grupRepository.getOne(grupId);
		PermisDto dto = new PermisDto();
		dto.setRead(true);
		dto.setPrincipalTipus(PrincipalTipusEnumDto.ROL);
		dto.setPrincipalNom(grup.getCodi());
		permisosHelper.updatePermis(grup.getId(), GrupEntity.class, dto);
	}
	
	public List<GrupEntity> findGrupsNoRelacionatAmbMetaExpedient(Long entitatId, Long metaExpedientId, Long adminOrganId) {
		
		List<GrupEntity> grups = grupRepository.findByEntitatId(entitatId);
		
		MetaExpedientEntity metaExpedient = metaExpedientRepository.getOne(metaExpedientId);
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
