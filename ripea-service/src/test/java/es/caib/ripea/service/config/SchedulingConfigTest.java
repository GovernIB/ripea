package es.caib.ripea.service.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.intf.config.PropertyConfig;

/**
 * Tests unitaris per a SchedulingConfig.getDiesConservacioMonitor: els dies que es conserven les dades dels monitors
 * d'integracions i d'excepcions surten de la propietat i, si no té un valor vàlid, del valor per defecte.
 */
@ExtendWith(MockitoExtension.class)
class SchedulingConfigTest {

	private static final String PROPIETAT = PropertyConfig.MONITOR_EXCEPCIONS_DIES;

	@Mock private ConfigHelper configHelper;

	private SchedulingConfig schedulingConfig;

	@BeforeEach
	void setUp() {
		schedulingConfig = new SchedulingConfig();
		ReflectionTestUtils.setField(schedulingConfig, "configHelper", configHelper);
	}

	@ParameterizedTest
	@CsvSource({"1,1", "7,7", "' 45 ',45"})
	void diesValidsDeLaPropietat(String valor, int diesEsperats) {
		when(configHelper.getConfig(PROPIETAT)).thenReturn(valor);
		assertThat(schedulingConfig.getDiesConservacioMonitor(PROPIETAT)).isEqualTo(diesEsperats);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {" ", "abc", "30 dies", "0", "-3", "99999999999"})
	void valorNoValidConservaElsDiesPerDefecte(String valor) {
		when(configHelper.getConfig(PROPIETAT)).thenReturn(valor);
		assertThat(schedulingConfig.getDiesConservacioMonitor(PROPIETAT)).isEqualTo(SchedulingConfig.DIES_CONSERVACIO_MONITOR_DEFECTE);
	}

}
