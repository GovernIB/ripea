package es.caib.ripea.persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Tests unitaris del comptador d'intents de guardar un interessat a l'Arxiu (InteressatEntity.updateArxiuIntent).
 *
 * arxiuReintents compta els intents fallits des del darrer èxit: el procés en segon pla tria els interessats
 * no propagats amb 0 &lt; arxiuReintents &lt; màxim, de manera que els èxits no han de consumir reintents.
 */
class InteressatEntityArxiuIntentTest {

	@Test
	void unErrorIncrementaElComptadorIDeixaLInteressatPendent() {
		InteressatEntity interessat = new InteressatPersonaFisicaEntity();

		interessat.updateArxiuIntent(false);

		assertThat(interessat.isArxiuPropagat()).isFalse();
		assertThat(interessat.getArxiuReintents()).isEqualTo(1);
		assertThat(interessat.getArxiuIntentData()).isNotNull();
	}

	@Test
	void errorsConsecutiusSumen() {
		InteressatEntity interessat = new InteressatPersonaFisicaEntity();

		interessat.updateArxiuIntent(false);
		interessat.updateArxiuIntent(false);
		interessat.updateArxiuIntent(false);

		assertThat(interessat.isArxiuPropagat()).isFalse();
		assertThat(interessat.getArxiuReintents()).isEqualTo(3);
	}

	@Test
	void unExitReiniciaElComptador() {
		InteressatEntity interessat = new InteressatPersonaFisicaEntity();
		interessat.updateArxiuIntent(false);
		interessat.updateArxiuIntent(false);

		interessat.updateArxiuIntent(true);

		assertThat(interessat.isArxiuPropagat()).isTrue();
		assertThat(interessat.getArxiuReintents()).isZero();
		assertThat(interessat.getArxiuIntentData()).isNotNull();
	}

	@Test
	void elsExitsNoConsumeixenReintents() {
		// Cas que motiva el canvi: modificacions correctes repetides i després un error
		InteressatEntity interessat = new InteressatPersonaFisicaEntity();
		interessat.updateArxiuIntent(true);
		interessat.updateArxiuIntent(true);
		interessat.updateArxiuIntent(true);
		interessat.updateArxiuIntent(true);

		interessat.updateArxiuIntent(false);

		// Amb el comptador acumulat hauria quedat a 5 (fora del procés en segon pla amb màxim 5)
		assertThat(interessat.isArxiuPropagat()).isFalse();
		assertThat(interessat.getArxiuReintents()).isEqualTo(1);
	}

	@Test
	void unErrorDespresDUnExitTornaAComencarDesDe1() {
		InteressatEntity interessat = new InteressatPersonaFisicaEntity();
		interessat.updateArxiuIntent(false);
		interessat.updateArxiuIntent(true);

		interessat.updateArxiuIntent(false);

		assertThat(interessat.getArxiuReintents()).isEqualTo(1);
	}

}
