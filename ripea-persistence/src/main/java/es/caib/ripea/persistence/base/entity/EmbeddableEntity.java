package es.caib.ripea.persistence.base.entity;

import org.springframework.data.domain.Persistable;

import java.io.Serializable;

/**
 * Interfície que han d'implementar totes les entitats que incorporin el recurs
 * com a camp embedded.
 * 
 * @author Límit Tecnologies
 */
public interface EmbeddableEntity<R, PK extends Serializable> extends Persistable<PK> {

	/**
	 * Retorna el valor del camp embedded (recurs).
	 * 
	 * @return el recurs amb la informació.
	 */
	public R getEmbedded();

	/**
	 * Estableix el valor del camp embedded (recurs).
	 *
	 * @param resource
	 *            el valor del recurs.
	 */
	public void setEmbedded(R resource);

}