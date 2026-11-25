package es.caib.ripea.back.diag;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import es.caib.ripea.service.intf.utils.TempsTraca;

/**
 * Obre el pas de serialització de la resposta a la traça de temps (veure {@link TempsTraca}). Spring el crida just
 * abans d'escriure el cos de la resposta; el tanca {@link TempsTracaInterceptor} quan la resposta ja és escrita.
 * No modifica la resposta.
 */
@ControllerAdvice
public class TempsTracaResponseBodyAdvice implements ResponseBodyAdvice<Object> {

	@Override
	public boolean supports(
			MethodParameter returnType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		return TempsTraca.isActiva();
	}

	@Override
	public Object beforeBodyWrite(
			Object body,
			MethodParameter returnType,
			MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request,
			ServerHttpResponse response) {
		TempsTraca.iniciaFase("serialitzacio de la resposta (JSON/HAL)");
		return body;
	}

}
