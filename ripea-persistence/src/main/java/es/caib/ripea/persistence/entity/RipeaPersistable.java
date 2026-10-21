/**
 * 
 */
package es.caib.ripea.persistence.entity;

import es.caib.ripea.service.intf.config.BaseConfig;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.springframework.data.domain.Persistable;
import org.springframework.data.util.ProxyUtils;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Classe basse d'on estendre per a les entitats que requereixen persistència bàsica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@MappedSuperclass
public abstract class RipeaPersistable<PK extends Serializable> implements Persistable<PK> {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
	@SequenceGenerator(name = "default_seq", sequenceName = BaseConfig.DB_PREFIX + "hibernate_seq", allocationSize = 1)
	private @Nullable PK id;

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Persistable#getId()
	 */
	@Nullable
	@Override
	public PK getId() {
		return id;
	}

	/**
	 * Sets the id of the entity.
	 *
	 * @param id the id to set
	 */
	protected void setId(@Nullable PK id) {
		this.id = id;
	}

	/**
	 * Must be {@link Transient} in order to ensure that no JPA provider complains because of a missing setter.
	 *
	 * @see Persistable#isNew()
	 */
	@Transient // DATAJPA-622
	@Override
	public boolean isNew() {
		return null == getId();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("Entity of type %s with id: %s", this.getClass().getName(), getId());
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (!getClass().equals(ProxyUtils.getUserClass(obj))) {
			return false;
		}
		RipeaPersistable<?> that = (RipeaPersistable<?>) obj;
		return null == this.getId() ? false : this.getId().equals(that.getId());
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hashCode = 17;
		hashCode += null == getId() ? 0 : getId().hashCode() * 31;
		return hashCode;
	}

	/**
	 * Retorna l'objecte real d'un proxy de Hibernate ja inicialitzat, o el mateix objecte si no és un proxy.
	 * Si és un proxy sense inicialitzar retorna null i no l'inicialitza (pot no haver-hi sessió oberta).
	 *
	 * L'han d'emprar els equals que comparen getClass() o accedeixen directament als camps de l'altre objecte:
	 * un proxy és una subclasse generada, amb els camps sense inicialitzar, i Hibernate el retorna en lloc de
	 * l'objecte real quan l'entitat ja és a la sessió com a proxy (p.ex. creat per una relació LAZY).
	 */
	protected static Object desferProxy(Object obj) {
		if (obj instanceof HibernateProxy) {
			LazyInitializer lazyInitializer = ((HibernateProxy) obj).getHibernateLazyInitializer();
			return lazyInitializer.isUninitialized() ? null : lazyInitializer.getImplementation();
		}
		return obj;
	}

}
