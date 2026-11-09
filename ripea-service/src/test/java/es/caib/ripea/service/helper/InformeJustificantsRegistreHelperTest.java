package es.caib.ripea.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.ExpedientPeticioEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.RegistreEntity;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.service.helper.FilaInformeJustificantRegistre.AccioRipea;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.exception.SistemaExternException;

/**
 * Tests unitaris per a InformeJustificantsRegistreHelper: un CSV per entitat escrit a mesura que es tracten les
 * anotacions, enviat comprimit en acabar l'execució i, si no s'ha pogut enviar, a l'execució següent.
 */
@ExtendWith(MockitoExtension.class)
class InformeJustificantsRegistreHelperTest {

	@TempDir Path directoriDades;

	@Mock private ConfigHelper configHelper;
	@Mock private EmailHelper emailHelper;
	@Mock private EntitatRepository entitatRepository;
	@InjectMocks private InformeJustificantsRegistreHelper helper;

	@BeforeEach
	void setUp() {
		lenient().when(configHelper.getConfig(PropertyConfig.APP_DATA_DIR)).thenReturn(directoriDades.toString());
		lenient().when(entitatRepository.findByCodi("A")).thenReturn(entitat(1L));
		lenient().when(entitatRepository.findByCodi("B")).thenReturn(entitat(2L));
	}

	@AfterEach
	void netejar() {
		FilaInformeJustificantRegistre.treure();
	}

	@Test
	void enviaUnInformeComprimitPerEntitatIElDesaAEnviats() throws Exception {
		when(emailHelper.enviarInformeJustificantsRegistre(anyLong(), anyString(), anyString(), anyString(), any())).thenReturn(1);

		helper.iniciarExecucio();
		helper.processarElement(10L, () -> {
			FilaInformeJustificantRegistre.actualitzar(fila -> {
				fila.setAnotacio(peticio("A", "GOIBE10/2026"));
				fila.setErrorMoure(new SistemaExternException("ARXIU", "permission of Write"), true);
				fila.setAccioRipea(AccioRipea.CREAT_A_CARPETA_EXISTENT);
			});
			return "incorporat";
		});
		helper.processarElement(11L, () -> {
			FilaInformeJustificantRegistre.actualitzar(fila -> fila.setAnotacio(peticio("B", "GOIBE11/2026")));
			return "ja és a la carpeta";
		});
		helper.finalitzarExecucio("Procés finalitzat: 2 elements processats");

		ArgumentCaptor<String> text = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<byte[]> adjunt = ArgumentCaptor.forClass(byte[].class);
		verify(emailHelper).enviarInformeJustificantsRegistre(eq(1L), contains("entitat A"), text.capture(), anyString(), adjunt.capture());
		verify(emailHelper).enviarInformeJustificantsRegistre(eq(2L), contains("entitat B"), anyString(), anyString(), any());
		assertThat(text.getValue())
				.contains("Procés finalitzat: 2 elements processats")
				.contains("Anotacions d'aquesta entitat tractades: 1")
				.contains("per falta de permisos: 1");
		assertThat(contingutZip(adjunt.getValue()))
				.contains("\"GOIBE10/2026\"")
				.contains("\"ERROR_PERMISOS (WARN)\"")
				.doesNotContain("GOIBE11/2026");
		assertThat(fitxers(InformeJustificantsRegistreHelper.PENDENTS)).isEmpty();
		assertThat(fitxers(InformeJustificantsRegistreHelper.ENVIATS)).hasSize(2).allMatch(nom -> nom.endsWith(".zip"));
	}

	@Test
	void unErrorDelProcessadorEsPropagaIQuedaALInforme() throws Exception {
		when(emailHelper.enviarInformeJustificantsRegistre(anyLong(), anyString(), anyString(), anyString(), any())).thenReturn(1);

		helper.iniciarExecucio();
		assertThatThrownBy(() -> helper.processarElement(10L, () -> {
			FilaInformeJustificantRegistre.actualitzar(fila -> fila.setAnotacio(peticio("A", "GOIBE10/2026")));
			throw new IllegalStateException("conflicte de nom");
		})).hasMessage("conflicte de nom");
		helper.finalitzarExecucio("resum");

		ArgumentCaptor<byte[]> adjunt = ArgumentCaptor.forClass(byte[].class);
		verify(emailHelper).enviarInformeJustificantsRegistre(eq(1L), anyString(), anyString(), anyString(), adjunt.capture());
		assertThat(contingutZip(adjunt.getValue())).contains("\"ERROR\"").contains("\"conflicte de nom\"");
	}

	@Test
	void siNoEsPotEnviarEsQuedaPendentIEsEnviaALExecucioSeguent() throws Exception {
		when(emailHelper.enviarInformeJustificantsRegistre(anyLong(), anyString(), anyString(), anyString(), any()))
				.thenThrow(new MailSendException("SMTP aturat"))
				.thenReturn(1);

		helper.iniciarExecucio();
		helper.processarElement(10L, () -> {
			FilaInformeJustificantRegistre.actualitzar(fila -> fila.setAnotacio(peticio("A", "GOIBE10/2026")));
			return "incorporat";
		});
		helper.finalitzarExecucio("resum");

		assertThat(fitxers(InformeJustificantsRegistreHelper.PENDENTS)).hasSize(1);
		assertThat(fitxers(InformeJustificantsRegistreHelper.ENVIATS)).isEmpty();

		helper.iniciarExecucio();

		verify(emailHelper, times(2)).enviarInformeJustificantsRegistre(eq(1L), anyString(), anyString(), anyString(), any());
		verify(emailHelper).enviarInformeJustificantsRegistre(eq(1L), anyString(), contains("no es va poder enviar"), anyString(), any());
		assertThat(fitxers(InformeJustificantsRegistreHelper.PENDENTS)).isEmpty();
		assertThat(fitxers(InformeJustificantsRegistreHelper.ENVIATS)).hasSize(1);
	}

	@Test
	void sensePropietatDeDadesNoEscriuNiEnvia() throws Exception {
		when(configHelper.getConfig(PropertyConfig.APP_DATA_DIR)).thenReturn(null);

		helper.iniciarExecucio();
		String resultat = helper.processarElement(10L, () -> "incorporat");
		helper.finalitzarExecucio("resum");

		assertThat(resultat).isEqualTo("incorporat");
		verify(emailHelper, never()).enviarInformeJustificantsRegistre(anyLong(), anyString(), anyString(), anyString(), any());
	}

	@Test
	void lesLiniesDelCsvEsTornenALlegirIgual() {
		String[] camps = { "A", null, "amb \"cometes\"", "amb ; separador", "amb\nsalt" };

		List<String> llegits = InformeJustificantsRegistreHelper.llegirCamps(
				InformeJustificantsRegistreHelper.liniaCsv(camps).replace("\r\n", ""));

		assertThat(llegits).containsExactly("A", "", "amb \"cometes\"", "amb ; separador", "amb salt");
	}

	private List<String> fitxers(String subdirectori) throws IOException {
		Path directori = directoriDades.resolve(InformeJustificantsRegistreHelper.DIRECTORI).resolve(subdirectori);
		if (!Files.exists(directori)) {
			return Arrays.asList();
		}
		try (Stream<Path> fitxers = Files.list(directori)) {
			return fitxers.map(fitxer -> fitxer.getFileName().toString()).collect(Collectors.toList());
		}
	}

	private static String contingutZip(byte[] zip) throws IOException {
		try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zip))) {
			ZipEntry entrada = zis.getNextEntry();
			assertThat(entrada).isNotNull();
			return new String(zis.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

	private static EntitatEntity entitat(Long id) {
		EntitatEntity entitat = mock(EntitatEntity.class);
		lenient().when(entitat.getId()).thenReturn(id);
		return entitat;
	}

	private static ExpedientPeticioEntity peticio(String entitatCodi, String identificador) {
		EntitatEntity entitat = mock(EntitatEntity.class);
		when(entitat.getCodi()).thenReturn(entitatCodi);
		MetaExpedientEntity procediment = mock(MetaExpedientEntity.class);
		when(procediment.getId()).thenReturn(30L);
		when(procediment.getNom()).thenReturn("Procediment");
		ExpedientEntity expedient = mock(ExpedientEntity.class);
		when(expedient.getId()).thenReturn(20L);
		when(expedient.getNumero()).thenReturn("EXP/1/2026");
		when(expedient.getMetaExpedient()).thenReturn(procediment);
		when(expedient.getEntitat()).thenReturn(entitat);
		RegistreEntity registre = mock(RegistreEntity.class);
		when(registre.getJustificantArxiuUuid()).thenReturn("uuid-distribucio");
		ExpedientPeticioEntity peticio = mock(ExpedientPeticioEntity.class);
		when(peticio.getIdentificador()).thenReturn(identificador);
		when(peticio.getRegistre()).thenReturn(registre);
		when(peticio.getExpedient()).thenReturn(expedient);
		return peticio;
	}

}
