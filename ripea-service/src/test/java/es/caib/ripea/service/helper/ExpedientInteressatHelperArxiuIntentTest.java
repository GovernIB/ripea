package es.caib.ripea.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.InteressatEntity;
import es.caib.ripea.persistence.repository.ExpedientRepository;

/**
 * Tests unitaris del registre d'intents de guardar els interessats a l'Arxiu
 * (ExpedientInteressatHelper.updateArxiuIntentInteressats, arxiuPropagarInteressats i guardarInteressatsArxiu).
 *
 * El procés en segon pla tria els interessats pendents (no propagats i 0 &lt; reintents &lt; màxim) pel seu propi
 * comptador: un intent fallit de guardar els interessats de tot l'expedient l'ha d'incrementar; si només
 * s'incrementa el de l'expedient, l'interessat es reintenta indefinidament.
 */
@ExtendWith(MockitoExtension.class)
class ExpedientInteressatHelperArxiuIntentTest {

	private static final Long EXPEDIENT_ID = 100L;

	@Mock private ContingutHelper contingutHelper;
	@Mock private ConfigHelper configHelper;
	@Mock private ExpedientRepository expedientRepository;
	@Mock private ExpedientHelper expedientHelper;
	@InjectMocks private ExpedientInteressatHelper helper;

	private ExpedientEntity expedient;
	/** No propagat i amb intents previs: el que tria el procés en segon pla. */
	private InteressatEntity pendent;
	/** No propagat però sense cap intent: el procés en segon pla no el tria. */
	private InteressatEntity senseIntents;
	private InteressatEntity propagat;

	@BeforeEach
	void setUp() {
		expedient = mock(ExpedientEntity.class);
		pendent = interessat(false, 3);
		senseIntents = interessat(false, 0);
		propagat = interessat(true, 1);
		Set<InteressatEntity> interessats = new HashSet<>();
		interessats.add(pendent);
		interessats.add(senseIntents);
		interessats.add(propagat);
		lenient().when(expedient.getId()).thenReturn(EXPEDIENT_ID);
		lenient().when(expedient.getInteressatsORepresentants()).thenReturn(interessats);
	}

	private static InteressatEntity interessat(boolean arxiuPropagat, int arxiuReintents) {
		InteressatEntity interessat = mock(InteressatEntity.class);
		lenient().when(interessat.isArxiuPropagat()).thenReturn(arxiuPropagat);
		lenient().when(interessat.getArxiuReintents()).thenReturn(arxiuReintents);
		return interessat;
	}

	// ---------------------------------------------------------------- updateArxiuIntentInteressats

	@Test
	void errorDeTotsElsInteressatsIncrementaElsPendents() {

		helper.updateArxiuIntentInteressats(expedient, null, false);

		verify(expedient).updateArxiuIntent(false);
		verify(pendent).updateArxiuIntent(false);
		verify(senseIntents, never()).updateArxiuIntent(anyBoolean());
		verify(propagat, never()).updateArxiuIntent(anyBoolean());
	}

	@Test
	void exitDeTotsElsInteressatsMarcaTotsElsInteressatsPropagats() {

		helper.updateArxiuIntentInteressats(expedient, null, true);

		verify(expedient).updateArxiuIntent(true);
		verify(pendent).updateArxiuIntent(true);
		verify(senseIntents).updateArxiuIntent(true);
		verify(propagat).updateArxiuIntent(true);
	}

	@Test
	void errorDUnInteressatConcretNomesIncrementaAquestInteressat() {

		helper.updateArxiuIntentInteressats(expedient, pendent, false);

		verify(pendent).updateArxiuIntent(false);
		verify(expedient, never()).updateArxiuIntent(anyBoolean());
		verify(senseIntents, never()).updateArxiuIntent(anyBoolean());
		verify(propagat, never()).updateArxiuIntent(anyBoolean());
	}

	// ---------------------------------------------------------------- guardarInteressatsArxiu

	@Test
	void guardarInteressatsAmbErrorDeLArxiuIncrementaElsPendents() {
		when(expedientRepository.getOne(EXPEDIENT_ID)).thenReturn(expedient);
		when(expedient.getArxiuUuid()).thenReturn("uuid-expedient");
		RuntimeException errorArxiu = new RuntimeException("[HTTP_500, COD_021] Exception in Transaction.");
		doThrow(errorArxiu).when(contingutHelper).arxiuPropagarModificacio(expedient);

		Exception resultat = helper.guardarInteressatsArxiu(EXPEDIENT_ID);

		assertThat(resultat).isSameAs(errorArxiu);
		verify(pendent).updateArxiuIntent(false);
		verify(senseIntents, never()).updateArxiuIntent(anyBoolean());
		verify(propagat, never()).updateArxiuIntent(anyBoolean());
	}

	@Test
	void guardarInteressatsAmbExpedientSenseArxiuIncrementaElsPendents() {
		when(expedientRepository.getOne(EXPEDIENT_ID)).thenReturn(expedient);
		when(expedient.getArxiuUuid()).thenReturn(null);

		Exception resultat = helper.guardarInteressatsArxiu(EXPEDIENT_ID);

		assertThat(resultat).isNotNull();
		verify(contingutHelper, never()).arxiuPropagarModificacio(any(ExpedientEntity.class));
		verify(pendent).updateArxiuIntent(false);
	}

	@Test
	void guardarInteressatsAmbExpedientTancatIncrementaElsPendents() {
		when(expedientRepository.getOne(EXPEDIENT_ID)).thenReturn(expedient);
		doThrow(new RuntimeException("Expedient tancat")).when(expedientHelper).concurrencyCheckExpedientJaTancat(expedient);

		Exception resultat = helper.guardarInteressatsArxiu(EXPEDIENT_ID);

		assertThat(resultat).isNotNull();
		verify(contingutHelper, never()).arxiuPropagarModificacio(any(ExpedientEntity.class));
		verify(pendent).updateArxiuIntent(false);
	}

	@Test
	void guardarInteressatsAmbExitMarcaTotsElsInteressatsPropagats() {
		when(expedientRepository.getOne(EXPEDIENT_ID)).thenReturn(expedient);
		when(expedient.getArxiuUuid()).thenReturn("uuid-expedient");

		Exception resultat = helper.guardarInteressatsArxiu(EXPEDIENT_ID);

		assertThat(resultat).isNull();
		verify(pendent).updateArxiuIntent(true);
		verify(senseIntents).updateArxiuIntent(true);
		verify(propagat).updateArxiuIntent(true);
	}

}
