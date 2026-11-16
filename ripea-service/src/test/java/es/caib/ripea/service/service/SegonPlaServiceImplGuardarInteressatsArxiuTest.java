package es.caib.ripea.service.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.InteressatEntity;
import es.caib.ripea.persistence.repository.InteressatRepository;
import es.caib.ripea.service.helper.CacheHelper;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.ConversioTipusHelper;
import es.caib.ripea.service.helper.ExpedientInteressatHelper;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.EntitatDto;

/**
 * Tests unitaris del procés en segon pla que reintenta guardar a l'Arxiu els interessats pendents
 * (SegonPlaServiceImpl.guardarInteressatsArxiu).
 *
 * guardarInteressatsArxiu(expedientId) desa tots els interessats de l'expedient d'una vegada i, si falla, suma un
 * reintent a cada interessat pendent: per això s'ha de cridar un sol cop per expedient i execució, encara que
 * l'expedient tingui diversos interessats pendents.
 */
@ExtendWith(MockitoExtension.class)
class SegonPlaServiceImplGuardarInteressatsArxiuTest {

	private static final int MAX_REINTENTS = 5;

	@Mock private InteressatRepository interessatRepository;
	@Mock private ExpedientInteressatHelper expedientInteressatHelper;
	@Mock private ConversioTipusHelper conversioTipusHelper;
	@Mock private ConfigHelper configHelper;
	@Mock private CacheHelper cacheHelper;
	@InjectMocks private SegonPlaServiceImpl service;

	@BeforeEach
	void setUp() {
		when(configHelper.getConfig(PropertyConfig.MAX_REINTENTS_INTERESSATS)).thenReturn(String.valueOf(MAX_REINTENTS));
		lenient().when(conversioTipusHelper.convertir(any(), eq(EntitatDto.class))).thenReturn(new EntitatDto());
	}

	@AfterEach
	void tearDown() {
		ConfigHelper.getEntitat().remove();
	}

	private static ExpedientEntity expedient(Long id) {
		ExpedientEntity expedient = mock(ExpedientEntity.class);
		lenient().when(expedient.getId()).thenReturn(id);
		return expedient;
	}

	private static InteressatEntity interessat(ExpedientEntity expedient) {
		InteressatEntity interessat = mock(InteressatEntity.class);
		lenient().when(interessat.getExpedient()).thenReturn(expedient);
		return interessat;
	}

	@Test
	void unaSolaCridaPerExpedientEncaraQueTinguiDiversosInteressatsPendents() {
		ExpedientEntity expedientA = expedient(1L);
		ExpedientEntity expedientB = expedient(2L);
		// Els mocks dels interessats es creen abans del when(): crear-los (i fer-ne stubs) dins el thenReturn()
		// deixa a mitges l'stub de findInteressatsPendentsArxiu (UnfinishedStubbingException).
		List<InteressatEntity> pendents = Arrays.asList(
				interessat(expedientA),
				interessat(expedientB),
				interessat(expedientA));
		when(interessatRepository.findInteressatsPendentsArxiu(MAX_REINTENTS)).thenReturn(pendents);

		service.guardarInteressatsArxiu();

		// Es respecta l'ordre de la consulta (el primer interessat de cada expedient)
		InOrder ordre = inOrder(expedientInteressatHelper);
		ordre.verify(expedientInteressatHelper).guardarInteressatsArxiu(1L);
		ordre.verify(expedientInteressatHelper).guardarInteressatsArxiu(2L);
		verifyNoMoreInteractions(expedientInteressatHelper);
	}

	@Test
	void unErrorDUnExpedientNoAturaElsSeguents() {
		ExpedientEntity expedientA = expedient(1L);
		ExpedientEntity expedientB = expedient(2L);
		List<InteressatEntity> pendents = Arrays.asList(
				interessat(expedientA),
				interessat(expedientB));
		when(interessatRepository.findInteressatsPendentsArxiu(MAX_REINTENTS)).thenReturn(pendents);
		when(expedientInteressatHelper.guardarInteressatsArxiu(1L)).thenReturn(new RuntimeException("Error de l'Arxiu"));

		service.guardarInteressatsArxiu();

		verify(expedientInteressatHelper).guardarInteressatsArxiu(1L);
		verify(expedientInteressatHelper).guardarInteressatsArxiu(2L);
	}

}
