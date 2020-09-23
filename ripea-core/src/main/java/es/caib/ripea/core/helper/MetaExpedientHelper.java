/**
 * 
 */
package es.caib.ripea.core.helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientSequenciaEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.helper.PermisosHelper.ListObjectIdentifiersExtractor;
import es.caib.ripea.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.repository.MetaExpedientSequenciaRepository;
import es.caib.ripea.core.repository.OrganGestorRepository;
import es.caib.ripea.core.security.ExtendedPermission;

/**
 * Utilitats comunes pels meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class MetaExpedientHelper {

	@Autowired
	private MetaExpedientSequenciaRepository metaExpedientSequenciaRepository;
	@Autowired
	private MetaExpedientRepository metaExpedientRepository;
	@Autowired
	private OrganGestorRepository organGestorRepository;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private PermisosHelper permisosHelper;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public synchronized long obtenirProximaSequenciaExpedient(
			MetaExpedientEntity metaExpedient,
			Integer any,
			boolean incrementar) {
		int anyExpedient;
		if (any != null)
			anyExpedient = any.intValue();
		else
			anyExpedient = Calendar.getInstance().get(Calendar.YEAR);
		MetaExpedientSequenciaEntity sequencia = metaExpedientSequenciaRepository.findByMetaExpedientAndAny(
				metaExpedient,
				anyExpedient);
		if (sequencia == null) {
			sequencia = MetaExpedientSequenciaEntity.getBuilder(anyExpedient, metaExpedient).build();
			metaExpedientSequenciaRepository.save(sequencia);
			return sequencia.getValor();
		} else if (incrementar) {
			sequencia.incrementar();
			return sequencia.getValor();
		} else {
			return sequencia.getValor() + 1;
		}
	}

	public List<Long> findMetaExpedientIdsFiltratsAmbPermisosOrganGestor(Long entitatId, Long organGestorId) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (organGestorId == null) {
			List<MetaExpedientEntity> metaExpedients = metaExpedientRepository.findByEntitat(entitat);			
			permisosHelper.filterGrantedAnyList(
					metaExpedients,
					new ListObjectIdentifiersExtractor<MetaExpedientEntity>() {

						public List<Long> getObjectIdentifiers(MetaExpedientEntity metaExpedient) {
							List<Long> ids = new ArrayList<Long>();

							OrganGestorEntity organGestor = metaExpedient.getOrganGestor();
							while (organGestor != null) {
								ids.add(organGestor.getId());

								organGestor = organGestor.getPare();
							}
							return ids;
						}

					},
					OrganGestorEntity.class,
					new Permission[] { ExtendedPermission.ADMINISTRATION },
					auth);
			List<Long> ids = new ArrayList<Long>();
			for (MetaExpedientEntity me : metaExpedients) {
				ids.add(me.getId());
			}
			return ids;
		} else {
			if (!permisosHelper.isGrantedAny(
							organGestorId,
							OrganGestorEntity.class,
							new Permission[] { ExtendedPermission.ADMINISTRATION },
							auth)) {
				return new ArrayList<Long>();
			}
			
			OrganGestorEntity organGestor = organGestorRepository.findOne(organGestorId);			
			return metaExpedientRepository.findByOrgansGestors(organGestor.getAllChildren());
		}

	}
	
	
	
	public List<MetaExpedientEntity> findActiusAmbOrganGestorPermisLectura(
			Long entitatId,
			Long organGestorId, 
			String filtre) {

		return findAmbOrganGestorPermis(
				entitatId,
				organGestorId,
				new Permission[] {ExtendedPermission.READ},
				true,
				filtre);
		

	}
	
	
	public List<MetaExpedientEntity> findAmbOrganGestorPermis(
			Long entitatId,
			Long organGestorId,
			Permission[] permisos,
			boolean nomesActius,
			String filtre) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);

		OrganGestorEntity organGestorEntity = organGestorRepository.findOne(organGestorId);
		List<MetaExpedientEntity> metaExpedients;
		if (nomesActius) {
			metaExpedients = metaExpedientRepository.findByOrganGestorAndActiuAndFiltreTrueOrderByNomAsc(
					organGestorEntity,
					filtre == null || "".equals(filtre.trim()),
					filtre == null ? "" : filtre);
		} else {
			metaExpedients = metaExpedientRepository.findByOrganGestorOrderByNomAsc(organGestorEntity);
		}
		
		permisosHelper.filterGrantedAll(
				metaExpedients,
				new ObjectIdentifierExtractor<MetaNodeEntity>() {
					public Long getObjectIdentifier(MetaNodeEntity metaNode) {
						return metaNode.getId();
					}
				},
				MetaNodeEntity.class,
				permisos,
				auth);
		return metaExpedients;
		

	}
	
	
	
	public List<MetaExpedientEntity> findActiusAmbEntitatPermis(
			Long entitatId,
			Permission[] permisos,
			String filtre) {
		return findAmbEntitatPermis(
				entitatId,
				permisos,
				true,
				filtre);		
	}
	
	
	public List<MetaExpedientEntity> findAmbEntitatPermis(
			Long entitatId,
			Permission[] permisos,
			boolean nomesActius,
			String filtre) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		
		List<MetaExpedientEntity> metaExpedients;
		if (nomesActius) {
			metaExpedients = metaExpedientRepository.findByEntitatAndActiuTrueAndFiltreOrderByNomAsc(
					entitat,
					filtre == null || "".equals(filtre.trim()),
					filtre == null ? "" : filtre);
		} else {
			metaExpedients = metaExpedientRepository.findByEntitatOrderByNomAsc(entitat);
		}

		permisosHelper.filterGrantedAll(
				metaExpedients,
				new ObjectIdentifierExtractor<MetaNodeEntity>() {
					public Long getObjectIdentifier(MetaNodeEntity metaNode) {
						return metaNode.getId();
					}
				},
				MetaNodeEntity.class,
				permisos,
				auth);
		return metaExpedients;		
	}
	
	
	
	
	
	
	

}
