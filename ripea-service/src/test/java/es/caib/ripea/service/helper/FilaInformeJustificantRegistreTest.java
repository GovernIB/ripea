package es.caib.ripea.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import es.caib.plugins.arxiu.api.ArxiuNotFoundException;
import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.service.helper.FilaInformeJustificantRegistre.AccioConsulta;
import es.caib.ripea.service.helper.FilaInformeJustificantRegistre.AccioMoure;
import es.caib.ripea.service.helper.FilaInformeJustificantRegistre.AccioRipea;
import es.caib.ripea.service.intf.exception.SistemaExternException;

/**
 * Tests unitaris per a FilaInformeJustificantRegistre: la fila només existeix dins el procés, la classificació dels
 * errors de l'Arxiu i el detall final.
 */
class FilaInformeJustificantRegistreTest {

	@AfterEach
	void netejar() {
		FilaInformeJustificantRegistre.treure();
	}

	@Test
	void foraDelProcesActualitzarNoFaRes() {
		FilaInformeJustificantRegistre.actualitzar(fila -> {
			throw new AssertionError("Fora del procés no hi ha cap fila");
		});
	}

	@Test
	void dinsElProcesActualitzaLaFilaDelFil() {
		FilaInformeJustificantRegistre fila = FilaInformeJustificantRegistre.iniciar(7L);

		FilaInformeJustificantRegistre.actualitzar(f -> f.setAccioRipea(AccioRipea.JA_A_LA_CARPETA));

		assertThat(fila.getAccioRipea()).isEqualTo(AccioRipea.JA_A_LA_CARPETA);
		assertThat(fila.getAccioConsulta()).isEqualTo(AccioConsulta.NO_CONSULTAT);
		assertThat(fila.getAccioMoure()).isEqualTo(AccioMoure.NO_APLICA);
	}

	@Test
	void errorDePermisosAMitjaCadenaDeCauses() {
		SistemaExternException error = new SistemaExternException(
				"ARXIU",
				"Error moguent el document",
				new RuntimeException("GdibException: The user does not have permission of Write"));
		FilaInformeJustificantRegistre fila = new FilaInformeJustificantRegistre(7L);

		fila.setErrorMoure(error, true);

		assertThat(fila.getAccioMoure()).isEqualTo(AccioMoure.ERROR_PERMISOS_WARN);
		assertThat(fila.getErrorMoure()).contains("Error moguent el document").contains("permission of Write");
	}

	@Test
	void accessDeniedTambeEsErrorDePermisosISenseAvisNoEsMou() {
		FilaInformeJustificantRegistre fila = new FilaInformeJustificantRegistre(7L);

		fila.setErrorMoure(new SistemaExternException("ARXIU", "Access Denied"), false);

		assertThat(fila.getAccioMoure()).isEqualTo(AccioMoure.ERROR_PERMISOS_NO_MOGUT);
	}

	@Test
	void unAltreErrorEnMoureNoEsDePermisos() {
		FilaInformeJustificantRegistre fila = new FilaInformeJustificantRegistre(7L);

		fila.setErrorMoure(new SistemaExternException("ARXIU", "Read timed out"), true);

		assertThat(fila.getAccioMoure()).isEqualTo(AccioMoure.ERROR_WARN);
		assertThat(fila.getAccioMoure().getText()).isEqualTo("ERROR (WARN)");
	}

	@Test
	void consultaNoTrobadaIAltresErrors() {
		FilaInformeJustificantRegistre noTrobat = new FilaInformeJustificantRegistre(7L);
		noTrobat.setErrorConsulta(new SistemaExternException("ARXIU", "Error consultant", new ArxiuNotFoundException()));
		FilaInformeJustificantRegistre timeout = new FilaInformeJustificantRegistre(8L);
		timeout.setErrorConsulta(new SistemaExternException("ARXIU", "Read timed out"));

		assertThat(noTrobat.getAccioConsulta()).isEqualTo(AccioConsulta.NO_TROBAT);
		assertThat(timeout.getAccioConsulta()).isEqualTo(AccioConsulta.ERROR);
	}

	@Test
	void siFallaUnElementAmbDocumentNouNoEnDonaLesDades() {
		FilaInformeJustificantRegistre fila = new FilaInformeJustificantRegistre(7L);
		fila.setDocument(document(), true);

		fila.finalitzarError(new IllegalStateException("error en desar"));

		assertThat(fila.getAccioRipea()).isEqualTo(AccioRipea.ERROR);
		assertThat(fila.getDocumentId()).isNull();
		assertThat(fila.getDocumentUuid()).isNull();
		assertThat(fila.getDetall()).isEqualTo("error en desar");
	}

	@Test
	void siFallaUnElementAmbDocumentExistentEnConservaLesDades() {
		FilaInformeJustificantRegistre fila = new FilaInformeJustificantRegistre(7L);
		fila.setDocument(document(), false);

		fila.finalitzarError(new IllegalStateException("conflicte de nom"));

		assertThat(fila.getDocumentId()).isEqualTo(1L);
		assertThat(fila.getDocumentUuid()).isEqualTo("uuid-document");
	}

	@Test
	void elDetallDUnElementCorrecteEsLErrorEnMoureSiNHiHa() {
		FilaInformeJustificantRegistre ambErrorMoure = new FilaInformeJustificantRegistre(7L);
		ambErrorMoure.setErrorMoure(new SistemaExternException("ARXIU", "permission of Write"), true);
		ambErrorMoure.finalitzarOk("incorporat");
		FilaInformeJustificantRegistre senseAccio = new FilaInformeJustificantRegistre(8L);
		senseAccio.finalitzarOk("l'anotació no té expedient");

		assertThat(ambErrorMoure.getDetall()).isEqualTo("Moure: permission of Write");
		assertThat(senseAccio.getAccioRipea()).isEqualTo(AccioRipea.NO_TRACTAT);
		assertThat(senseAccio.getDetall()).isEqualTo("l'anotació no té expedient");
	}

	@Test
	void elsCampsSegueixenLaCapcalera() {
		FilaInformeJustificantRegistre fila = new FilaInformeJustificantRegistre(7L);
		fila.setErrorMoure(new SistemaExternException("ARXIU", "permission of Write"), true);
		fila.finalitzarOk("incorporat");

		String[] camps = fila.getCamps();

		assertThat(camps).hasSize(FilaInformeJustificantRegistre.CAPCALERA.length);
		assertThat(camps[1]).isEqualTo("7");
		assertThat(camps[FilaInformeJustificantRegistre.COLUMNA_ACCIO_CONSULTA]).isEqualTo("NO_CONSULTAT");
		assertThat(camps[FilaInformeJustificantRegistre.COLUMNA_ACCIO_MOURE]).isEqualTo("ERROR_PERMISOS (WARN)");
		assertThat(camps[FilaInformeJustificantRegistre.COLUMNA_ACCIO_RIPEA]).isEqualTo("NO_TRACTAT");
	}

	private static DocumentEntity document() {
		DocumentEntity document = mock(DocumentEntity.class);
		when(document.getId()).thenReturn(1L);
		when(document.getNom()).thenReturn("Justificant del registre GOIBE1/2026");
		when(document.getFitxerNom()).thenReturn("REGISTRE_JUSTIFICANT_ENTRADA_7_GOIBE1_2026.pdf");
		when(document.getArxiuUuid()).thenReturn("uuid-document");
		return document;
	}

}
