package es.caib.ripea.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;

import es.caib.plugins.arxiu.caib.ArxiuCaibException;

/**
 * Tests unitaris per a IncorporacioDocumentsSegonPlaHelper: classificació dels errors de disponibilitat i ús del
 * darrer id desat. Els escenaris amb més d'un element no s'hi inclouen perquè el bucle espera 5 s entre elements.
 */
@ExtendWith(MockitoExtension.class)
class IncorporacioDocumentsSegonPlaHelperTest {

	private static final String PROPIETAT_CRON = "prop.cron";
	private static final String PROPIETAT_DARRER_ID = "prop.darrerId";

	@Mock private ConfigHelper configHelper;
	@InjectMocks private IncorporacioDocumentsSegonPlaHelper helper;

	// ---------------------------------------------------------------- isErrorDisponibilitat

	@Test
	void timeoutEmbolcallatEsErrorDisponibilitat() {
		Exception error = new Exception("Error al afegir", new RuntimeException(new SocketTimeoutException("Read timed out")));
		assertThat(IncorporacioDocumentsSegonPlaHelper.isErrorDisponibilitat(error)).isTrue();
	}

	@Test
	void connexioRebutjadaEsErrorDisponibilitat() {
		Exception error = new Exception("Error al afegir", new ConnectException("Connection refused"));
		assertThat(IncorporacioDocumentsSegonPlaHelper.isErrorDisponibilitat(error)).isTrue();
	}

	@Test
	void respostaNotib5xxEsErrorDisponibilitat() {
		assertThat(IncorporacioDocumentsSegonPlaHelper.isErrorDisponibilitat(
				new RuntimeException(new UniformInterfaceException(respostaAmbEstat(503))))).isTrue();
	}

	@Test
	void respostaNotib4xxNoEsErrorDisponibilitat() {
		assertThat(IncorporacioDocumentsSegonPlaHelper.isErrorDisponibilitat(
				new RuntimeException(new UniformInterfaceException(respostaAmbEstat(404))))).isFalse();
	}

	@Test
	void respostaArxiu500SenseCodiEsErrorDisponibilitat() {
		Exception error = new Exception(new ArxiuCaibException("crearDocument", 500, null, null));
		assertThat(IncorporacioDocumentsSegonPlaHelper.isErrorDisponibilitat(error)).isTrue();
	}

	@Test
	void respostaArxiu4xxSenseCodiNoEsErrorDisponibilitat() {
		// L'estat només queda al missatge ("[HTTP_404,null]null"): getHttpStatus() de la llibreria torna 0
		Exception error = new Exception(new ArxiuCaibException("crearDocument", 404, null, null));
		assertThat(IncorporacioDocumentsSegonPlaHelper.isErrorDisponibilitat(error)).isFalse();
	}

	@Test
	void respostaArxiu500AmbCodiEsErrorDeNegoci() {
		Exception error = new Exception(new ArxiuCaibException("crearDocument", 500, "COD_021", "not found"));
		assertThat(IncorporacioDocumentsSegonPlaHelper.isErrorDisponibilitat(error)).isFalse();
	}

	@Test
	void errorDeDadesNoEsErrorDisponibilitat() {
		Exception error = new Exception("L'expedient no està guardat a l'arxiu digital", new IOException("fitxer buit"));
		assertThat(IncorporacioDocumentsSegonPlaHelper.isErrorDisponibilitat(error)).isFalse();
	}

	@Test
	void causaCiclicaNoPenjaLaClassificacio() {
		CiclicaException error = new CiclicaException();
		assertThat(IncorporacioDocumentsSegonPlaHelper.isErrorDisponibilitat(error)).isFalse();
	}

	// ---------------------------------------------------------------- executar

	@Test
	void senseDarrerIdConsultaTotsElsPendentsIProcessaElPrimer() {
		when(configHelper.getConfig(PROPIETAT_CRON)).thenReturn("0 0 21 * * *");
		when(configHelper.getConfig(PROPIETAT_DARRER_ID)).thenReturn(null);
		List<Long> darrersIdsConsultats = new ArrayList<>();

		helper.executar("proces", PROPIETAT_CRON, PROPIETAT_DARRER_ID, "element",
				darrerId -> { darrersIdsConsultats.add(darrerId); return Collections.singletonList(900L); },
				id -> "ok",
				progres -> {});

		assertThat(darrersIdsConsultats).containsExactly(Long.MAX_VALUE);
		verify(configHelper).updateConfigNewTransaction(PROPIETAT_DARRER_ID, "900");
	}

	@Test
	void ambDarrerIdConsultaElsAnteriorsIDesaElCursorEncaraQueFalli() {
		when(configHelper.getConfig(PROPIETAT_CRON)).thenReturn("0 0 21 * * *");
		when(configHelper.getConfig(PROPIETAT_DARRER_ID)).thenReturn("500");
		List<Long> darrersIdsConsultats = new ArrayList<>();

		helper.executar("proces", PROPIETAT_CRON, PROPIETAT_DARRER_ID, "element",
				darrerId -> { darrersIdsConsultats.add(darrerId); return Collections.singletonList(499L); },
				id -> { throw new Exception("error de l'element"); },
				progres -> {});

		assertThat(darrersIdsConsultats).containsExactly(500L);
		verify(configHelper).updateConfigNewTransaction(PROPIETAT_DARRER_ID, "499");
	}

	@Test
	void senseElementsPendentsNoProcessaNiDesaRes() {
		when(configHelper.getConfig(PROPIETAT_DARRER_ID)).thenReturn("10");
		List<String> progressos = new ArrayList<>();

		helper.executar("proces", PROPIETAT_CRON, PROPIETAT_DARRER_ID, "element",
				darrerId -> Collections.emptyList(),
				id -> { throw new AssertionError("No s'ha de processar cap element"); },
				progressos::add);

		verify(configHelper, never()).updateConfigNewTransaction(anyString(), anyString());
		assertThat(progressos).hasSize(1);
		assertThat(progressos.get(0)).contains("completat");
	}

	@Test
	void darrerIdNoNumericAturaElProces() {
		when(configHelper.getConfig(PROPIETAT_DARRER_ID)).thenReturn("abc");

		assertThatThrownBy(() -> helper.executar("proces", PROPIETAT_CRON, PROPIETAT_DARRER_ID, "element",
				darrerId -> Collections.singletonList(1L),
				id -> "ok",
				progres -> {}))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining(PROPIETAT_DARRER_ID);
	}

	@Test
	void cronBuitAturaAbansDeProcessar() {
		when(configHelper.getConfig(PROPIETAT_CRON)).thenReturn("");
		when(configHelper.getConfig(PROPIETAT_DARRER_ID)).thenReturn(null);

		helper.executar("proces", PROPIETAT_CRON, PROPIETAT_DARRER_ID, "element",
				darrerId -> Collections.singletonList(1L),
				id -> { throw new AssertionError("No s'ha de processar cap element"); },
				progres -> {});

		verify(configHelper, never()).updateConfigNewTransaction(anyString(), anyString());
	}

	private static ClientResponse respostaAmbEstat(int estat) {
		ClientResponse resposta = mock(ClientResponse.class);
		when(resposta.getStatus()).thenReturn(estat);
		return resposta;
	}

	/** Excepció que és la seva pròpia causa a través d'una altra, per comprovar que el recorregut no és infinit. */
	private static class CiclicaException extends Exception {
		private final Exception altra = new Exception("altra", this);
		@Override
		public synchronized Throwable getCause() {
			return altra;
		}
	}

}
