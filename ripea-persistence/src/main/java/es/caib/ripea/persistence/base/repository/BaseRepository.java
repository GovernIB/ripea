package es.caib.ripea.persistence.base.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * Repositori base.
 * 
 * @author Límit Tecnologies
 */
@NoRepositoryBean
public interface BaseRepository<E, PK extends Serializable> extends JpaRepository<E, PK>, JpaSpecificationExecutor<E> {

	/**
	 * Refresca la informació de l'entitat.
	 *
	 * @param entity
	 *            l'entitat a refrescar.
	 */
	void refresh(E entity);

	/**
	 * Deslliga l'entitat de la sessió actual.
	 *
	 * @param entity
	 *            l'entitat a deslligar.
	 */
	void detach(E entity);

	/**
	 * Fusiona l'entitat amb la sessió actual.
	 *
	 * @param entity
	 *            l'entitat a fusionar.
	 */
	void merge(E entity);

}
