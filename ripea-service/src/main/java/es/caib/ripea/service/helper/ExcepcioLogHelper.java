package es.caib.ripea.service.helper;

import es.caib.ripea.service.intf.dto.ExcepcioLogDto;
import lombok.Synchronized;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class ExcepcioLogHelper {

	public static final int DEFAULT_MAX_EXCEPCIONS = 20;

	private LinkedList<ExcepcioLogDto> excepcions = new LinkedList<>();

	public List<ExcepcioLogDto> findAll() {
		int index = 0;
		for (ExcepcioLogDto excepcio: excepcions) {
			excepcio.setIndex(Long.valueOf(index++));
		}
		return excepcions;
	}

	@Synchronized
	public void addExcepcio(String uri, Throwable exception) {
		if (exception == null) return;

		while (excepcions.size() >= DEFAULT_MAX_EXCEPCIONS) {
			excepcions.removeLast();
		}
		excepcions.addFirst(new ExcepcioLogDto(uri, exception));
	}
	
	@Synchronized
	public void addExcepcio(String uri, Throwable exception, String param1, String param2) {
		if (exception == null) return;

		while (excepcions.size() >= DEFAULT_MAX_EXCEPCIONS) {
			excepcions.removeLast();
		}
		ExcepcioLogDto exc = new ExcepcioLogDto(uri, exception);
		exc.setParam1(param1);
		exc.setParam2(param2);
		excepcions.addFirst(exc);
	}
}