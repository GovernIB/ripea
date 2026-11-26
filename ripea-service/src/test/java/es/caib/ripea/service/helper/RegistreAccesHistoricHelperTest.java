package es.caib.ripea.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.historic.AccesHistoricDto;

/**
 * Tests unitaris per a RegistreAccesHistoricHelper: una línia per accés al CSV del mes i del node, només amb el
 * registre actiu, i sense propagar mai cap error.
 */
@ExtendWith(MockitoExtension.class)
class RegistreAccesHistoricHelperTest {

	@TempDir Path directoriDades;

	@Mock private ConfigHelper configHelper;
	@InjectMocks private RegistreAccesHistoricHelper helper;

	@BeforeEach
	void setUp() {
		lenient().when(configHelper.getConfig(PropertyConfig.APP_DATA_DIR)).thenReturn(directoriDades.toString());
	}

	@Test
	void registrar_actiu_escriuCapcaleraIUnaLiniaPerAcces() throws IOException {
		when(configHelper.getAsBoolean(PropertyConfig.HISTORIC_REGISTRE_ACCES_ACTIU)).thenReturn(true);
		helper.registrar(acces(LocalDateTime.of(2026, 11, 26, 10, 15, 30), "/ripeaapi/interna/api/historic/entitat/1"));
		helper.registrar(acces(LocalDateTime.of(2026, 11, 26, 10, 16, 0), "/ripeaapi/interna/api/historic/usuaris"));

		Path fitxer = directoriDades.resolve(RegistreAccesHistoricHelper.DIRECTORI).resolve(
				RegistreAccesHistoricHelper.nomFitxer(RegistreAccesHistoricHelper.nomNode(), LocalDateTime.of(2026, 11, 1, 0, 0)));
		List<String> linies = Files.readAllLines(fitxer, StandardCharsets.UTF_8);
		assertThat(linies).hasSize(3);
		assertThat(linies.get(0)).startsWith("﻿\"Data\";\"Aplicació\"");
		assertThat(linies.get(1)).isEqualTo("\"26/11/2026 10:15:30\";\"API_INTERNA\";\"GET\";"
				+ "\"/ripeaapi/interna/api/historic/entitat/1\";\"dataInici=01/01/2026\";\"10.0.0.1\";\"1.2.3.4\";"
				+ "\"usuari_api\";\"\";\"curl/8.0\";\"200\";\"35\";\"\"");
		assertThat(linies.get(2)).contains("/ripeaapi/interna/api/historic/usuaris");
	}

	@Test
	void registrar_mesDiferent_escriuEnUnFitxerNou() throws IOException {
		when(configHelper.getAsBoolean(PropertyConfig.HISTORIC_REGISTRE_ACCES_ACTIU)).thenReturn(true);
		helper.registrar(acces(LocalDateTime.of(2026, 11, 30, 23, 59, 59), "/ripeaback/historic"));
		helper.registrar(acces(LocalDateTime.of(2026, 12, 1, 0, 0, 1), "/ripeaback/historic"));

		try (var fitxers = Files.list(directoriDades.resolve(RegistreAccesHistoricHelper.DIRECTORI))) {
			assertThat(fitxers.map(f -> f.getFileName().toString()))
					.hasSize(2)
					.allMatch(nom -> nom.matches("acces_historic_.+_2026(11|12)\\.csv"));
		}
	}

	@Test
	void registrar_inactiu_noEscriuRes() {
		when(configHelper.getAsBoolean(PropertyConfig.HISTORIC_REGISTRE_ACCES_ACTIU)).thenReturn(false);
		helper.registrar(acces(LocalDateTime.of(2026, 11, 26, 10, 0), "/ripeaback/historic"));

		assertThat(directoriDades.resolve(RegistreAccesHistoricHelper.DIRECTORI)).doesNotExist();
	}

	@Test
	void registrar_senseDirectoriDeDades_noLlancaExcepcio() {
		when(configHelper.getAsBoolean(PropertyConfig.HISTORIC_REGISTRE_ACCES_ACTIU)).thenReturn(true);
		when(configHelper.getConfig(PropertyConfig.APP_DATA_DIR)).thenReturn(null);

		assertThatCode(() -> helper.registrar(acces(LocalDateTime.of(2026, 11, 26, 10, 0), "/ripeaback/historic")))
				.doesNotThrowAnyException();
	}

	@Test
	void registrar_errorDeConfiguracio_noLlancaExcepcio() {
		when(configHelper.getAsBoolean(PropertyConfig.HISTORIC_REGISTRE_ACCES_ACTIU)).thenThrow(new RuntimeException("BD"));

		assertThatCode(() -> helper.registrar(acces(LocalDateTime.of(2026, 11, 26, 10, 0), "/ripeaback/historic")))
				.doesNotThrowAnyException();
	}

	@Test
	void nomNode_nomesCaractersValidsEnUnNomDeFitxer() {
		assertThat(RegistreAccesHistoricHelper.nomNode()).matches("[A-Za-z0-9._-]+");
	}

	private static AccesHistoricDto acces(LocalDateTime data, String ruta) {
		AccesHistoricDto acces = new AccesHistoricDto();
		acces.setData(Date.from(data.atZone(ZoneId.systemDefault()).toInstant()));
		acces.setAplicacio(AccesHistoricDto.APLICACIO_API_INTERNA);
		acces.setMetodeHttp("GET");
		acces.setRuta(ruta);
		acces.setParametres("dataInici=01/01/2026");
		acces.setIpRemota("10.0.0.1");
		acces.setXForwardedFor("1.2.3.4");
		acces.setUsuari("usuari_api");
		acces.setUserAgent("curl/8.0");
		acces.setEstatHttp(200);
		acces.setDuradaMs(35L);
		return acces;
	}

}
