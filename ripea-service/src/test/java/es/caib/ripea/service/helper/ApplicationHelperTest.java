package es.caib.ripea.service.helper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

/**
 * Tests unitaris per a ApplicationHelper.stopTimer: les accions executades sense monitors no compten a les mètriques
 * d'integracions (COMANDA), però sí a les dels subsistemes.
 */
class ApplicationHelperTest {

	private static final String METRICA_ARXIU = "METRICS@Integracions.arxiu";
	private static final String METRICA_SEGON_PLA = "METRICS@Subsystem_Background.justificants";

	private final SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
	private ApplicationHelper helper;

	@BeforeEach
	void setUp() {
		helper = new ApplicationHelper();
		ReflectionTestUtils.setField(helper, "meterRegistry", meterRegistry);
	}

	@AfterEach
	void netejar() {
		SenseMonitorsUtils.netejar();
	}

	@Test
	void metricaIntegracioEsRegistra() {
		helper.stopTimer(Timer.start(meterRegistry), METRICA_ARXIU, "resultado", "exito");
		assertThat(meterRegistry.find(METRICA_ARXIU).timer().count()).isEqualTo(1);
	}

	@Test
	void metricaIntegracioSenseMonitorsNoEsRegistra() throws Exception {
		SenseMonitorsUtils.executar(() -> {
			helper.stopTimer(Timer.start(meterRegistry), METRICA_ARXIU, "resultado", "error");
			return null;
		});
		assertThat(meterRegistry.find(METRICA_ARXIU).timer()).isNull();
	}

	@Test
	void metricaSubsistemaSenseMonitorsEsRegistra() throws Exception {
		SenseMonitorsUtils.executar(() -> {
			helper.stopTimer(Timer.start(meterRegistry), METRICA_SEGON_PLA, "resultado", "exito");
			return null;
		});
		assertThat(meterRegistry.find(METRICA_SEGON_PLA).timer().count()).isEqualTo(1);
	}

}
