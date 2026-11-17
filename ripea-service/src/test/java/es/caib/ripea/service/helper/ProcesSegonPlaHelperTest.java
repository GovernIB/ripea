package es.caib.ripea.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import es.caib.ripea.persistence.repository.UsuariRepository;
import es.caib.ripea.service.intf.dto.ElementTipusEnumDto;
import es.caib.ripea.service.intf.dto.ProcesSegonPlaDto;
import es.caib.ripea.service.intf.dto.ProcesSegonPlaEstatEnumDto;
import es.caib.ripea.service.intf.dto.ProcesSegonPlaPasDto;
import es.caib.ripea.service.intf.dto.ProcesSegonPlaPasEstatEnumDto;
import es.caib.ripea.service.intf.dto.ProcesSegonPlaTipusEnumDto;
import es.caib.ripea.service.intf.exception.ValidationException;

/**
 * Tests unitaris del registre de processos en segon pla: passos, bloqueig de l'element i exempció del propi fil.
 */
class ProcesSegonPlaHelperTest {

	private static final Long EXPEDIENT_ID = 1L;
	private static final long TIMEOUT_S = 10;

	private ProcesSegonPlaHelper helper;

	@BeforeEach
	void setUp() {
		helper = new ProcesSegonPlaHelper();
		UsuariRepository usuariRepository = mock(UsuariRepository.class);
		when(usuariRepository.findById(anyString())).thenReturn(Optional.empty());
		MessageHelper messageHelper = mock(MessageHelper.class);
		when(messageHelper.getMessage(anyString(), any())).thenAnswer(inv -> inv.getArgument(0));
		when(messageHelper.getMissatgeError(any())).thenAnswer(inv -> ((Exception)inv.getArgument(0)).getMessage());
		ReflectionTestUtils.setField(helper, "usuariRepository", usuariRepository);
		ReflectionTestUtils.setField(helper, "messageHelper", messageHelper);
		ReflectionTestUtils.setField(helper, "excepcioLogHelper", mock(ExcepcioLogHelper.class));
		ReflectionTestUtils.setField(helper, "eventHelper", mock(EventHelper.class));
		ReflectionTestUtils.setField(helper, "configHelper", mock(ConfigHelper.class));
		helper.init();
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("usuari1", null));
	}

	@AfterEach
	void tearDown() {
		helper.destroy();
		SecurityContextHolder.clearContext();
	}

	@Test
	void passosFasesIAvisosEsRegistrenAmbElSeuEstat() throws Exception {
		CountDownLatch fi = new CountDownLatch(1);
		ProcesSegonPlaDto inicial = helper.iniciar(
				ProcesSegonPlaTipusEnumDto.TANCAMENT_EXPEDIENT,
				ElementTipusEnumDto.EXPEDIENT,
				EXPEDIENT_ID,
				() -> {
					try {
						ProcesSegonPlaContext.pas("PAS_OK", null, () -> ProcesSegonPlaContext.fase("FASE", null));
						ProcesSegonPlaContext.avis("AVIS", ProcesSegonPlaContext.params("document", "doc.pdf"), "detall");
						try {
							ProcesSegonPlaContext.pas("PAS_ERROR", null, () -> { throw new IllegalStateException("ha fallat"); });
						} catch (IllegalStateException ignorada) {
							// el procés continua
						}
					} finally {
						fi.countDown();
					}
				});
		assertThat(fi.await(TIMEOUT_S, TimeUnit.SECONDS)).isTrue();
		ProcesSegonPlaDto proces = esperarFinal(inicial.getOperacioId());

		assertThat(proces.getEstat()).isEqualTo(ProcesSegonPlaEstatEnumDto.FINALITZAT);
		assertThat(proces.getPassos()).extracting(ProcesSegonPlaPasDto::getCodi).containsExactly("PAS_OK", "AVIS", "PAS_ERROR");
		assertThat(proces.getPassos()).extracting(ProcesSegonPlaPasDto::getEstat).containsExactly(
				ProcesSegonPlaPasEstatEnumDto.OK,
				ProcesSegonPlaPasEstatEnumDto.AVIS,
				ProcesSegonPlaPasEstatEnumDto.ERROR);
		// La fase només és visible mentre el pas és en curs
		assertThat(proces.getPassos().get(0).getFase()).isNull();
		assertThat(proces.getPassos().get(1).getParams()).containsEntry("document", "doc.pdf");
		assertThat(proces.getPassos().get(2).getDetall()).isEqualTo("ha fallat");
		// En acabar, l'element ja no està bloquejat
		assertThat(helper.getEnCurs(ElementTipusEnumDto.EXPEDIENT, EXPEDIENT_ID)).isNull();
	}

	@Test
	void errorDeLaTascaMarcaElProcesComAError() {
		ProcesSegonPlaDto inicial = helper.iniciar(
				ProcesSegonPlaTipusEnumDto.TANCAMENT_EXPEDIENT,
				ElementTipusEnumDto.EXPEDIENT,
				EXPEDIENT_ID,
				() -> { throw new IllegalStateException("error general"); });
		ProcesSegonPlaDto proces = esperarFinal(inicial.getOperacioId());
		assertThat(proces.getEstat()).isEqualTo(ProcesSegonPlaEstatEnumDto.ERROR);
		assertThat(proces.getErrorMsg()).isEqualTo("error general");
	}

	@Test
	void elementBloquejatPerAltriPeroNoPelPropiFil() throws Exception {
		CountDownLatch iniciat = new CountDownLatch(1);
		CountDownLatch continuar = new CountDownLatch(1);
		AtomicReference<Boolean> bloquejatDesDelPropiFil = new AtomicReference<>();
		helper.iniciar(
				ProcesSegonPlaTipusEnumDto.TANCAMENT_EXPEDIENT,
				ElementTipusEnumDto.EXPEDIENT,
				EXPEDIENT_ID,
				() -> {
					bloquejatDesDelPropiFil.set(helper.isBloquejatPerAltri(ElementTipusEnumDto.EXPEDIENT, EXPEDIENT_ID));
					iniciat.countDown();
					continuar.await(TIMEOUT_S, TimeUnit.SECONDS);
				});
		assertThat(iniciat.await(TIMEOUT_S, TimeUnit.SECONDS)).isTrue();
		try {
			assertThat(bloquejatDesDelPropiFil.get()).isFalse();
			assertThat(helper.isBloquejatPerAltri(ElementTipusEnumDto.EXPEDIENT, EXPEDIENT_ID)).isTrue();
			assertThat(helper.getEnCurs(ElementTipusEnumDto.EXPEDIENT, EXPEDIENT_ID).getUsuariCodi()).isEqualTo("usuari1");
			assertThatThrownBy(() -> helper.comprovarNoBloquejat(ElementTipusEnumDto.EXPEDIENT, EXPEDIENT_ID))
					.isInstanceOf(ValidationException.class);
			// No es pot llançar un altre procés sobre el mateix element
			assertThatThrownBy(() -> helper.iniciar(
					ProcesSegonPlaTipusEnumDto.TANCAMENT_EXPEDIENT,
					ElementTipusEnumDto.EXPEDIENT,
					EXPEDIENT_ID,
					() -> {})).isInstanceOf(ValidationException.class);
			// Un altre element no hi té res a veure
			assertThat(helper.isBloquejatPerAltri(ElementTipusEnumDto.EXPEDIENT, 2L)).isFalse();
		} finally {
			continuar.countDown();
		}
	}

	@Test
	void nomesElPotConsultarQuiLHaLlancat() {
		ProcesSegonPlaDto inicial = helper.iniciar(
				ProcesSegonPlaTipusEnumDto.TANCAMENT_EXPEDIENT,
				ElementTipusEnumDto.EXPEDIENT,
				EXPEDIENT_ID,
				() -> {});
		esperarFinal(inicial.getOperacioId());
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("usuari2", null));
		assertThat(helper.consultar(inicial.getOperacioId())).isNull();
		assertThat(helper.consultar("no-existeix")).isNull();
	}

	@Test
	void foraDeCapProcesElContextNoFaRes() {
		assertThat(ProcesSegonPlaContext.getOperacioActual()).isNull();
		assertThat(ProcesSegonPlaContext.pas("PAS", null, () -> "resultat")).isEqualTo("resultat");
		ProcesSegonPlaContext.fase("FASE", null);
		ProcesSegonPlaContext.avis("AVIS", null, null);
	}

	/** Espera que el procés acabi (el consulta l'usuari que l'ha llançat) i en retorna l'estat. */
	private ProcesSegonPlaDto esperarFinal(String operacioId) {
		long limit = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(TIMEOUT_S);
		ProcesSegonPlaDto proces = helper.consultar(operacioId);
		while (proces != null && proces.getEstat() == ProcesSegonPlaEstatEnumDto.EN_CURS && System.currentTimeMillis() < limit) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
			proces = helper.consultar(operacioId);
		}
		assertThat(proces).isNotNull();
		assertThat(proces.getEstat()).isNotEqualTo(ProcesSegonPlaEstatEnumDto.EN_CURS);
		return proces;
	}

}
