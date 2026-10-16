package es.caib.ripea.service.resourcehelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.persistence.entity.resourceentity.UsuariResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.UsuariResourceRepository;
import es.caib.ripea.persistence.repository.ExecucioMassivaContingutRepository;
import es.caib.ripea.service.base.service.BaseReadonlyResourceService.PerspectiveApplicator;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.dto.ElementTipusEnumDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaTipusDto;
import lombok.RequiredArgsConstructor;

/**
 * Utilitats per aplicar perspectives a tots els recursos d'una pàgina alhora (applyMultiple) amb consultes
 * per lots, en lloc de fer una o més consultes per fila (applySingle).
 *
 * Les perspectives AUDITORIA i EN_PROCES_* es repetien igual a diversos serveis: aquí hi ha una única
 * implementació per lots que els serveis registren amb {@link #auditoria()} i {@link #execucioMassivaEnCurs}.
 */
@Component
@RequiredArgsConstructor
public class PerspectivesLotHelper {

	/** Màxim d'elements en una clàusula IN (límit d'Oracle). */
	public static final int MAX_IDS_CONSULTA = 1000;

	private final UsuariResourceRepository usuariResourceRepository;
	private final ExecucioMassivaContingutRepository execucioMassivaContingutRepository;

	/** Divideix la llista en lots de com a màxim {@link #MAX_IDS_CONSULTA} elements per a les consultes amb IN. */
	public static <T> List<List<T>> lots(Collection<T> elements) {
		return ListUtils.partition(new ArrayList<>(elements), MAX_IDS_CONSULTA);
	}

	public static <E extends BaseAuditableEntity<?>> List<Long> ids(List<E> entities) {
		return entities.stream().map(BaseAuditableEntity::getId).collect(Collectors.toList());
	}

	/** Executa una consulta de [id, Long] per lots i en retorna el resultat com a mapa id → valor. */
	public static Map<Long, Long> mapPerLots(Collection<Long> ids, Function<List<Long>, List<Object[]>> consulta) {
		Map<Long, Long> resultat = new HashMap<>();
		for (List<Long> lot: lots(ids)) {
			for (Object[] fila: consulta.apply(lot)) {
				resultat.put((Long)fila[0], ((Number)fila[1]).longValue());
			}
		}
		return resultat;
	}

	/** Executa una consulta que retorna ids per lots i en retorna el conjunt. */
	public static Set<Long> setPerLots(Collection<Long> ids, Function<List<Long>, List<Long>> consulta) {
		Set<Long> resultat = new HashSet<>();
		for (List<Long> lot: lots(ids)) {
			resultat.addAll(consulta.apply(lot));
		}
		return resultat;
	}

	/** Usuaris pel codi, amb una consulta per lot. Els codis inexistents no hi són. */
	public Map<String, UsuariResourceEntity> findUsuaris(Collection<String> codis) {
		Map<String, UsuariResourceEntity> usuaris = new HashMap<>();
		for (List<String> lot: lots(new HashSet<>(codis))) {
			for (UsuariResourceEntity usuari: usuariResourceRepository.findAllById(lot)) {
				usuaris.put(usuari.getId(), usuari);
			}
		}
		return usuaris;
	}

	/** Execució massiva en curs (sense data de fi) del tipus indicat per a cada element que en té: elementId → execucioMassivaId. */
	public Map<Long, Long> findExecucionsMassivesEnCurs(
			Collection<Long> elementIds,
			ElementTipusEnumDto elementTipus,
			ExecucioMassivaTipusDto execucioMassivaTipus) {
		return mapPerLots(
				elementIds,
				lot -> execucioMassivaContingutRepository.findExecucioMassivaEnCursByElementIds(lot, elementTipus, execucioMassivaTipus));
	}

	/**
	 * Perspectiva AUDITORIA: nom complet ("Nom (codi)") dels usuaris de creació i de darrera modificació.
	 * Un usuari que no existeix a la taula d'usuaris es deixa sense nom complet (el recurs mostra el codi).
	 */
	public <E extends BaseAuditableEntity<R>, R extends BaseAuditableResource<Long>> PerspectiveApplicator<E, R> auditoria() {
		return new PerspectiveApplicator<E, R>() {
			@Override
			public boolean applyMultiple(String code, List<E> entities, List<R> resources) throws PerspectiveApplicationException {
				Set<String> codis = new HashSet<>();
				for (E entity: entities) {
					if (entity.getCreatedBy() != null) codis.add(entity.getCreatedBy());
					if (entity.getLastModifiedBy() != null) codis.add(entity.getLastModifiedBy());
				}
				Map<String, UsuariResourceEntity> usuaris = codis.isEmpty() ? Collections.emptyMap() : findUsuaris(codis);
				for (int i = 0; i < entities.size(); i++) {
					E entity = entities.get(i);
					R resource = resources.get(i);
					UsuariResourceEntity createdBy = entity.getCreatedBy() != null ? usuaris.get(entity.getCreatedBy()) : null;
					if (createdBy != null) {
						resource.setCreatedByFullName(createdBy.getNom() + " (" + createdBy.getCodi() + ")");
					}
					UsuariResourceEntity lastModifiedBy = entity.getLastModifiedBy() != null ? usuaris.get(entity.getLastModifiedBy()) : null;
					if (lastModifiedBy != null) {
						resource.setLastModifiedByFullName(lastModifiedBy.getNom() + " (" + lastModifiedBy.getCodi() + ")");
					}
				}
				return true;
			}
			@Override
			public void applySingle(String code, E entity, R resource) throws PerspectiveApplicationException {
				applyMultiple(code, Collections.singletonList(entity), Collections.singletonList(resource));
			}
		};
	}

	/**
	 * Perspectives EN_PROCES_*: assigna al recurs (amb el setter indicat) l'id de l'execució massiva del tipus
	 * indicat que encara té l'element pendent, si n'hi ha.
	 */
	public <E extends BaseAuditableEntity<R>, R extends BaseAuditableResource<Long>> PerspectiveApplicator<E, R> execucioMassivaEnCurs(
			ElementTipusEnumDto elementTipus,
			ExecucioMassivaTipusDto execucioMassivaTipus,
			BiConsumer<R, Long> setExecucioMassivaId) {
		return new PerspectiveApplicator<E, R>() {
			@Override
			public boolean applyMultiple(String code, List<E> entities, List<R> resources) throws PerspectiveApplicationException {
				Map<Long, Long> execucions = findExecucionsMassivesEnCurs(ids(entities), elementTipus, execucioMassivaTipus);
				for (int i = 0; i < entities.size(); i++) {
					Long execucioMassivaId = execucions.get(entities.get(i).getId());
					if (execucioMassivaId != null) {
						setExecucioMassivaId.accept(resources.get(i), execucioMassivaId);
					}
				}
				return true;
			}
			@Override
			public void applySingle(String code, E entity, R resource) throws PerspectiveApplicationException {
				applyMultiple(code, Collections.singletonList(entity), Collections.singletonList(resource));
			}
		};
	}
}
