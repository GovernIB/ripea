
/**
 * 
 */
package es.caib.ripea.core.helper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class SynchronizationHelper {

	public static Map<Long, Object> locksMoureDocumentArxiu = new ConcurrentHashMap<>();
	public static Map<Long, Object> locksGuardarExpedientArxiu = new ConcurrentHashMap<>();
	public static Map<Long, Object> locksGuardarDocumentArxiu = new ConcurrentHashMap<>();

	
	
	
	
	/**
	 * This method ensures that map of lock objects will not grow infinitely, so there is no need to remove lock objects from map after processing 
	 * there is some possibility of unnecessary locking (for example concurrent processing of elements with ids: 1, 101, 1001, etc.) but negligible
	 */
	public static Object get0To99Lock(Long id, Map<Long, Object> locks) {
		Long num = id % 100;

		if (!locks.containsKey(num))
			locks.put(num, new Object());
		return locks.get(num);

	}
	


}