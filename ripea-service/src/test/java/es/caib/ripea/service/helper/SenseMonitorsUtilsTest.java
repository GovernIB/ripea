package es.caib.ripea.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Tests unitaris per a SenseMonitorsUtils: l'indicador només és actiu mentre s'executa l'acció, al mateix fil.
 */
class SenseMonitorsUtilsTest {

	@AfterEach
	void netejar() {
		SenseMonitorsUtils.netejar();
	}

	@Test
	void foraDeLExecucioNoEstaActiu() {
		assertThat(SenseMonitorsUtils.isActiu()).isFalse();
	}

	@Test
	void dinsLExecucioEstaActiuIRetornaElResultat() throws Exception {
		String resultat = SenseMonitorsUtils.executar(() -> SenseMonitorsUtils.isActiu() ? "actiu" : "inactiu");
		assertThat(resultat).isEqualTo("actiu");
		assertThat(SenseMonitorsUtils.isActiu()).isFalse();
	}

	@Test
	void siLAccioFallaEsPropagaLErrorIEsDesactiva() {
		assertThatThrownBy(() -> SenseMonitorsUtils.executar(() -> {
			throw new IllegalStateException("error de l'element");
		})).isInstanceOf(IllegalStateException.class).hasMessage("error de l'element");
		assertThat(SenseMonitorsUtils.isActiu()).isFalse();
	}

	@Test
	void unaExecucioNiadaNoDesactivaLaDeFora() throws Exception {
		boolean actiuDespresDeLaNiada = SenseMonitorsUtils.executar(() -> {
			SenseMonitorsUtils.executar(() -> null);
			return SenseMonitorsUtils.isActiu();
		});
		assertThat(actiuDespresDeLaNiada).isTrue();
		assertThat(SenseMonitorsUtils.isActiu()).isFalse();
	}

	@Test
	void netejarTreuLIndicador() throws Exception {
		boolean actiu = SenseMonitorsUtils.executar(() -> {
			SenseMonitorsUtils.netejar();
			return SenseMonitorsUtils.isActiu();
		});
		assertThat(actiu).isFalse();
	}

	@Test
	void unAltreFilNoHeretaLIndicador() throws Exception {
		AtomicBoolean actiuAltreFil = new AtomicBoolean(true);
		SenseMonitorsUtils.executar(() -> {
			Thread fil = new Thread(() -> actiuAltreFil.set(SenseMonitorsUtils.isActiu()));
			fil.start();
			fil.join();
			return null;
		});
		assertThat(actiuAltreFil.get()).isFalse();
	}

}
