package es.caib.ripea.persistence.base.entity;

import org.springframework.data.domain.Persistable;

/**
 * Interfície que han d'implementar totes les entitats per a poder identificar
 * el recurs al qual fan referència.
 * 
 * @author Límit Tecnologies
 */
public interface ResourceEntity<R, PK> extends Persistable<PK> {
}