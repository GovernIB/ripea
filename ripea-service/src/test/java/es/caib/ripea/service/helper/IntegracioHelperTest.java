package es.caib.ripea.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import es.caib.ripea.persistence.entity.resourceentity.IntegracioResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.IntegracioResourceRepository;
import es.caib.ripea.service.intf.dto.IntegracioAccioDto;
import es.caib.ripea.service.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.ripea.service.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.ripea.service.intf.dto.IntegracioCodiEnum;

/**
 * Tests unitaris per a IntegracioHelper: les accions executades sense monitors no es registren, i la consulta de les
 * darreres accions limita les files a la BD.
 */
@ExtendWith(MockitoExtension.class)
class IntegracioHelperTest {

	private static final String ARXIU = IntegracioCodiEnum.ARXIU.name();
	private static final IntegracioAccioTipusEnumDto ENVIAMENT = IntegracioAccioTipusEnumDto.ENVIAMENT;

	@Mock private IntegracioResourceRepository integracioResourceRepository;
	@Mock private CacheHelper cacheHelper;
	@Mock private ConfigHelper configHelper;
	@Mock private MessageHelper messageHelper;
	/** Proxy de Spring per on passa el registre (REQUIRES_NEW). */
	@Mock private IntegracioHelper self;

	private IntegracioHelper helper;

	@BeforeEach
	void setUp() {
		helper = new IntegracioHelper(integracioResourceRepository, cacheHelper, configHelper, messageHelper);
		ReflectionTestUtils.setField(helper, "self", self);
	}

	@AfterEach
	void netejar() {
		SenseMonitorsUtils.netejar();
	}

	// ---------------------------------------------------------------- execució sense monitors

	@Test
	void accioOkEsRegistra() {
		helper.addAccioOk(ARXIU, "Consulta d'un document", "endpoint", null, ENVIAMENT, 10L);
		verify(self).guardarAccioOk(ARXIU, "Consulta d'un document", "endpoint", null, ENVIAMENT, 10L);
	}

	@Test
	void accioOkSenseMonitorsNoEsRegistra() throws Exception {
		SenseMonitorsUtils.executar(() -> {
			helper.addAccioOk(ARXIU, "Consulta d'un document", "endpoint", null, ENVIAMENT, 10L);
			return null;
		});
		verifyNoInteractions(self, integracioResourceRepository);
	}

	@Test
	void accioErrorSenseMonitorsNoEsRegistra() throws Exception {
		RuntimeException error = new RuntimeException("permission of Write");
		SenseMonitorsUtils.executar(() -> {
			helper.addAccioError(ARXIU, "Moure document", "endpoint", null, ENVIAMENT, 10L, "Error al moure el document", error);
			return null;
		});
		verifyNoInteractions(self, integracioResourceRepository);
	}

	@Test
	void accioErrorDespresDeLExecucioSenseMonitorsEsRegistra() throws Exception {
		RuntimeException error = new RuntimeException("permission of Write");
		SenseMonitorsUtils.executar(() -> "element processat");
		helper.addAccioError(ARXIU, "Moure document", "endpoint", null, ENVIAMENT, 10L, "Error al moure el document", error);
		verify(self).guardarAccioError(ARXIU, "Moure document", "endpoint", null, ENVIAMENT, 10L, "Error al moure el document", error);
	}

	// ---------------------------------------------------------------- darreres accions (salut)

	@Test
	void darreresAccionsLimitaLaConsulta() {
		IntegracioResourceEntity accio = new IntegracioResourceEntity();
		accio.setCodi(IntegracioCodiEnum.ARXIU);
		accio.setEstat(IntegracioAccioEstatEnumDto.ERROR);
		when(integracioResourceRepository.findByCodiOrderByDataDesc(IntegracioCodiEnum.ARXIU, PageRequest.of(0, 10)))
				.thenReturn(List.of(accio));

		List<IntegracioAccioDto> accions = helper.findDarreresAccionsByIntegracioCodi(ARXIU, 10);

		assertThat(accions).extracting(IntegracioAccioDto::getEstat).containsExactly(IntegracioAccioEstatEnumDto.ERROR);
		verify(integracioResourceRepository, never()).findByCodiOrderByDataDesc(IntegracioCodiEnum.ARXIU);
	}

	@Test
	void darreresAccionsDUnCodiDesconegutNoConsulta() {
		assertThat(helper.findDarreresAccionsByIntegracioCodi("PFIRMA", 10)).isEmpty();
		verifyNoInteractions(integracioResourceRepository);
	}

	@Test
	void capDarreraAccioNoConsulta() {
		assertThat(helper.findDarreresAccionsByIntegracioCodi(ARXIU, 0)).isEmpty();
		verifyNoInteractions(integracioResourceRepository);
	}

}
