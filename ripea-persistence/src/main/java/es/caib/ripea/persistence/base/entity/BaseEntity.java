package es.caib.ripea.persistence.base.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;

import javax.persistence.*;

/**
 * Base per a definir les demés entitats de l'aplicació.
 *
 * @param <E> classe del recurs associat.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity<E> implements ResourceEntity<E, Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private @Nullable Long id;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public boolean isNew() {
		return null == getId();
	}

}