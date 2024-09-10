/**
 * 
 */
package es.caib.ripea.core.helper;

import es.caib.ripea.core.api.dto.ExcepcioLogDto;
import lombok.Synchronized;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * Mètodes per a la gestió del log d'excepcions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ExcepcioLogHelper {

	public static final int DEFAULT_MAX_EXCEPCIONS = 20;

	private LinkedList<ExcepcioLogDto> excepcions = new LinkedList<>();



	public List<ExcepcioLogDto> findAll() {
		int index = 0;
		for (ExcepcioLogDto excepcio: excepcions) {
			excepcio.setIndex(new Long(index++));
		}
		return excepcions;
	}

	@Synchronized
	public void addExcepcio(Throwable exception) {
		if (exception == null) return;

		while (excepcions.size() >= DEFAULT_MAX_EXCEPCIONS) {
			excepcions.removeLast();
		}
		excepcions.addFirst(new ExcepcioLogDto(exception));
	}

}
