/**
 * 
 */
package es.caib.ripea.service.helper;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;


/**
 * Helper per a operacions amb usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HibernateHelper {

	@SuppressWarnings("unchecked")
	public static <T>T deproxy(T obj) {
		if (obj == null)
			return obj;
		if (obj instanceof HibernateProxy) {
			HibernateProxy proxy = (HibernateProxy) obj;
			LazyInitializer li = proxy.getHibernateLazyInitializer();
			return (T)li.getImplementation();
		} 
		return obj;
	}

	public static boolean isProxy(Object obj) {
		if (obj instanceof HibernateProxy)
			return true;
		return false;
	}

	public static boolean isEqual(Object o1, Object o2) {
		if (o1 == o2)
			return true;
		if (o1 == null || o2 == null) 
			return false;
		Object d1 = deproxy(o1);
		Object d2 = deproxy(o2);
		if (d1 == d2 || d1.equals(d2))
			return true;
		return false;
	}

	public static boolean isSame(Object o1, Object o2) {
		if (o1 == o2)
			return true;
		if (o1 == null || o2 == null) 
			return false;
		Object d1 = deproxy(o1);
		Object d2 = deproxy(o2);
		if (d1 == d2)
			return true;
		return false;
	}

	public static Class<?> getClassWithoutInitializingProxy(Object obj) {
		if (obj instanceof HibernateProxy) {
			HibernateProxy proxy = (HibernateProxy)obj;
			LazyInitializer li = proxy.getHibernateLazyInitializer();
			return li.getPersistentClass();
		} 
		return obj.getClass();
	}

}
