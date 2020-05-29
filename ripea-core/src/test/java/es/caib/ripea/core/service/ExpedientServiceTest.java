/**
 * 
 */
package es.caib.ripea.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientComentariDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientEstatDto;
import es.caib.ripea.core.api.dto.ExpedientEstatEnumDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.NtiOrigenEnumDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.DocumentService;

/**
 * Tests per al servei de gestió d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/ripea/core/application-context-test.xml"})
public class ExpedientServiceTest extends BaseExpedientServiceTest {

	@Autowired
	private DocumentService documentService;

	@Test
    public void create() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(4);
						//ExpedientDto expedientDtoFromDB = expedientService.findById(((EntitatEntity)elementsCreats.get(0)).getId(), expedientCreat.getId());
						assertNotNull(expedientCreat);
						assertNotNull(expedientCreat.getId());
						comprovarExpedientCoincideix(
								expedientCreate,
								expedientCreat);
					}
				},
				"Creació d'un expedient");
	}

	@Test
	public void findById() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(4);
						ExpedientDto trobat = expedientService.findById(
								entitatCreada.getId(),
								expedientCreat.getId());
						assertNotNull(trobat);
						assertNotNull(trobat.getId());
						comprovarExpedientCoincideix(
								expedientCreat,
								trobat);
					}
				},
				"Consulta d'un expedient");
    }

//	@Test
    public void update() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);
						ExpedientDto modificat = expedientService.update(
								entitatCreada.getId(),
								expedientCreat.getId(),
								expedientUpdate.getNom());
						assertNotNull(modificat);
						assertNotNull(modificat.getId());
						assertEquals(
								expedientCreat.getId(),
								modificat.getId());
						comprovarExpedientCoincideix(
								expedientUpdate,
								modificat);
					}
				});
	}

//	@Test
    public void deleteReversible() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);
						try {
							ContingutDto esborrat = contingutService.deleteReversible(
									entitatCreada.getId(),
									expedientCreat.getId());
							assertTrue(esborrat instanceof ExpedientDto);
							comprovarExpedientCoincideix(
									expedientCreate,
									(ExpedientDto)esborrat);
							try {
								autenticarUsuari("user");
								expedientService.findById(
										entitatCreada.getId(),
										expedientCreat.getId());
								fail("L'expedient esborrat no s'hauria d'haver trobat");
							} catch (NotFoundException expected) {
							}
							elementsCreats.remove(expedientCreat);
						} catch (IOException ex) {
							fail("S'han produit errors inesperats: " + ex);
						}
					}
				});
	}

//	@Test
    public void deleteDefinitiu() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);
						autenticarUsuari("admin");
						ContingutDto esborrat = contingutService.deleteDefinitiu(
								entitatCreada.getId(),
								expedientCreat.getId());
						assertTrue(esborrat instanceof ExpedientDto);
						comprovarExpedientCoincideix(
								expedientCreate,
								(ExpedientDto)esborrat);
						try {
							autenticarUsuari("user");
							expedientService.findById(
									entitatCreada.getId(),
									expedientCreat.getId());
							fail("L'expedient esborrat no s'hauria d'haver trobat");
						} catch (NotFoundException expected) {
						}
						elementsCreats.remove(expedientCreat);
					}
				});
	}

//	@Test
    public void tancarReobrir() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);
						autenticarUsuari("user");
						assertEquals(
								ExpedientEstatEnumDto.OBERT, expedientCreat.getEstat());
						String motiu = "Motiu de tancament de test";
						expedientService.tancar(
								entitatCreada.getId(),
								expedientCreat.getId(),
								motiu);
						ExpedientDto tancat = expedientService.findById(
								entitatCreada.getId(),
								expedientCreat.getId());
						assertEquals(
								ExpedientEstatEnumDto.TANCAT, tancat.getEstat());
						assertEquals(motiu, tancat.getTancatMotiu());
						expedientService.reobrir(
								entitatCreada.getId(),
								expedientCreat.getId());
						ExpedientDto reobert = expedientService.findById(
								entitatCreada.getId(),
								expedientCreat.getId());
						assertEquals(
								ExpedientEstatEnumDto.OBERT, reobert.getEstat());
						assertNull(reobert.getTancatMotiu());
					}
				});
	}

//	@Test
    public void alliberarAgafarUser() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);
						autenticarUsuari("user");
						assertTrue(
								expedientCreat.isAgafat());
						assertEquals("user", expedientCreat.getAgafatPer().getCodi());
						expedientService.alliberarUser(
								entitatCreada.getId(),
								expedientCreat.getId());
						ExpedientDto alliberat = expedientService.findById(
								entitatCreada.getId(),
								expedientCreat.getId());
						assertTrue(
								!alliberat.isAgafat());
						assertNull(alliberat.getAgafatPer());
						expedientService.agafarUser(
								entitatCreada.getId(),
								expedientCreat.getId());
						ExpedientDto agafat = expedientService.findById(
								entitatCreada.getId(),
								expedientCreat.getId());
						assertTrue(
								agafat.isAgafat());
						assertEquals("user", agafat.getAgafatPer().getCodi());
					}
				});
	}

//	@Test
	public void alliberarAdminAgafarUser() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);
						assertTrue(
								expedientCreat.isAgafat());
						assertEquals("user", expedientCreat.getAgafatPer().getCodi());
						autenticarUsuari("admin");
						expedientService.alliberarAdmin(
								entitatCreada.getId(),
								expedientCreat.getId());
						autenticarUsuari("user");
						ExpedientDto alliberat = expedientService.findById(
								entitatCreada.getId(),
								expedientCreat.getId());
						assertTrue(
								!alliberat.isAgafat());
						assertNull(alliberat.getAgafatPer());
						expedientService.agafarUser(
								entitatCreada.getId(),
								expedientCreat.getId());
						ExpedientDto agafat = expedientService.findById(
								entitatCreada.getId(),
								expedientCreat.getId());
						assertTrue(
								agafat.isAgafat());
						assertEquals("user", agafat.getAgafatPer().getCodi());
					}
				});
	}

	@Test
	public void comentaris() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(4);
						String comentariText = "Comentari d'un usuari: amb accents i símbols $%&·\"'";
						List<ExpedientComentariDto> comentaris0 = expedientService.findComentarisPerContingut(
								entitatCreada.getId(),
								expedientCreat.getId());
						assertNotNull(comentaris0);
						assertTrue(comentaris0.isEmpty());
						expedientService.publicarComentariPerExpedient(
								entitatCreada.getId(),
								expedientCreat.getId(),
								comentariText);
						List<ExpedientComentariDto> comentaris1 = expedientService.findComentarisPerContingut(
								entitatCreada.getId(),
								expedientCreat.getId());
						assertNotNull(comentaris1);
						assertTrue(comentaris1.size() == 1);
						ExpedientComentariDto comentari = comentaris1.get(0);
						assertEquals(comentariText, comentari.getText());
						assertNotNull(comentari.getCreatedBy());
						assertEquals("user", comentari.getCreatedBy().getCodi());
						assertNotNull(comentari.getCreatedDate());
					}
				},
				"Gestionar comentaris d'un expedient");
	}

	@Test
	public void canviEstat() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						MetaExpedientDto metaExpedientCreat = (MetaExpedientDto)elementsCreats.get(1);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(4);
						List<ExpedientEstatDto> estats0 = expedientService.findExpedientEstats(
								entitatCreada.getId(),
								expedientCreat.getId());
						assertNotNull(estats0);
						assertTrue(estats0.isEmpty());
						autenticarUsuari("admin");
						ExpedientEstatDto estatPerCrear = new ExpedientEstatDto();
						estatPerCrear.setCodi("TST");
						estatPerCrear.setNom("Test");
						estatPerCrear.setMetaExpedientId(metaExpedientCreat.getId());
						expedientService.createExpedientEstat(
								entitatCreada.getId(),
								estatPerCrear);
						autenticarUsuari("user");
						List<ExpedientEstatDto> estats1 = expedientService.findExpedientEstats(
								entitatCreada.getId(),
								expedientCreat.getId());
						assertNotNull(estats1);
						assertTrue(estats1.size() == 1);
						ExpedientEstatDto estatCreat = estats1.get(0);
						assertEquals(estatPerCrear.getCodi(), estatCreat.getCodi());
						assertEquals(estatPerCrear.getNom(), estatCreat.getNom());
						ExpedientDto expedientAmbEstat = expedientService.changeEstatOfExpedient(
								entitatCreada.getId(),
								expedientCreat.getId(),
								estatCreat.getId());
						ExpedientEstatDto estatExpedient = expedientAmbEstat.getExpedientEstat();
						assertNotNull(estatExpedient);
						assertEquals(estatPerCrear.getCodi(), estatExpedient.getCodi());
						assertEquals(estatPerCrear.getNom(), estatExpedient.getNom());
					}
				},
				"Canvi d'estat d'un expedient");
	}

	@Test
	public void relacionar() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						MetaExpedientDto metaExpedientCreat = (MetaExpedientDto)elementsCreats.get(1);
						ExpedientDto expedientCreat1 = (ExpedientDto)elementsCreats.get(4);
						List<ExpedientDto> relacionats10 = expedientService.relacioFindAmbExpedient(
								entitatCreada.getId(),
								expedientCreat1.getId());
						assertNotNull(relacionats10);
						assertTrue(relacionats10.isEmpty());
						ExpedientDto expedientCreat2 = expedientService.create(
								entitatCreada.getId(),
								metaExpedientCreat.getId(),
								null,
								null,
								expedientCreate.getAny(),
								null,
								"Expedient de test2 (" + System.currentTimeMillis() + ")",
								null,
								false);
						elementsCreats.add(expedientCreat2);
						List<ExpedientDto> relacionats20 = expedientService.relacioFindAmbExpedient(
								entitatCreada.getId(),
								expedientCreat2.getId());
						assertNotNull(relacionats20);
						assertTrue(relacionats20.isEmpty());
						/**/
						expedientService.relacioCreate(
								entitatCreada.getId(),
								expedientCreat1.getId(),
								expedientCreat2.getId());
						List<ExpedientDto> relacionats11 = expedientService.relacioFindAmbExpedient(
								entitatCreada.getId(),
								expedientCreat1.getId());
						assertNotNull(relacionats11);
						assertTrue(relacionats11.size() == 1);
						List<ExpedientDto> relacionats21 = expedientService.relacioFindAmbExpedient(
								entitatCreada.getId(),
								expedientCreat2.getId());
						assertNotNull(relacionats21);
						assertTrue(relacionats21.size() == 1);
						/**/
						expedientService.relacioCreate(
								entitatCreada.getId(),
								expedientCreat2.getId(),
								expedientCreat1.getId());
						List<ExpedientDto> relacionats12 = expedientService.relacioFindAmbExpedient(
								entitatCreada.getId(),
								expedientCreat1.getId());
						assertNotNull(relacionats12);
						assertTrue(relacionats12.size() == 1);
						List<ExpedientDto> relacionats22 = expedientService.relacioFindAmbExpedient(
								entitatCreada.getId(),
								expedientCreat2.getId());
						assertNotNull(relacionats22);
						assertTrue(relacionats22.size() == 1);
						/**/
						expedientService.relacioDelete(
								entitatCreada.getId(),
								expedientCreat2.getId(),
								expedientCreat1.getId());
						List<ExpedientDto> relacionats13 = expedientService.relacioFindAmbExpedient(
								entitatCreada.getId(),
								expedientCreat1.getId());
						assertNotNull(relacionats13);
						assertTrue(relacionats13.isEmpty());
						List<ExpedientDto> relacionats23 = expedientService.relacioFindAmbExpedient(
								entitatCreada.getId(),
								expedientCreat2.getId());
						assertNotNull(relacionats23);
						assertTrue(relacionats23.isEmpty());
					}
				},
				"Relacionar expedients");
	}

	@Test
	public void tancar() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) throws IOException {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						MetaDocumentDto metaDocumentCreat = (MetaDocumentDto)elementsCreats.get(2);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(4);
						assertNotNull(expedientCreat);
						DocumentDto dto = new DocumentDto();
						dto.setNom("Test");
						dto.setData(new Date());
						dto.setDocumentTipus(DocumentTipusEnumDto.DIGITAL);
						dto.setNtiEstadoElaboracion(DocumentNtiEstadoElaboracionEnumDto.EE01);
						dto.setNtiOrigen(NtiOrigenEnumDto.O0);
						emplenarDocumentArxiu(dto);
						dto.setFirmaSeparada(false);
						MetaDocumentDto metaDocument = new MetaDocumentDto();
						metaDocument.setId(metaDocumentCreat.getId());
						dto.setMetaNode(metaDocument);
						try {
							expedientService.tancar(
									entitatCreada.getId(),
									expedientCreat.getId(),
									"Motiu de tancament");
							fail("No s'ha de poder tancar un expedient sense documents definitius");
						} catch (ValidationException ignored) {
						}
						DocumentDto documentCreat = documentService.create(
								entitatCreada.getId(),
								expedientCreat.getId(),
								dto,
								true);
						assertNotNull(documentCreat);
						try {
							expedientService.tancar(
									entitatCreada.getId(),
									expedientCreat.getId(),
									"Motiu de tancament");
							fail("No s'ha de poder tancar un expedient amb documents en estat d'esborrany");
						} catch (ValidationException ignored) {
						}
						String identificador = documentService.generarIdentificadorFirmaClient(
								entitatCreada.getId(),
								documentCreat.getId());
						assertNotNull(identificador);
						documentService.processarFirmaClient(
								identificador,
								"firma.pdf",
								dto.getFitxerContingut());
						expedientService.tancar(
								entitatCreada.getId(),
								expedientCreat.getId(),
								"Motiu de tancament");
						ExpedientDto expedientTancat = expedientService.findById(
								entitatCreada.getId(),
								expedientCreat.getId());
						assertNotNull(expedientTancat);
						assertEquals(ExpedientEstatEnumDto.TANCAT, expedientTancat.getEstat());
					}
				},
				"Tancar expedient");
	}

	private void comprovarExpedientCoincideix(
			ExpedientDto original,
			ExpedientDto perComprovar) {
		assertEquals(
				original.getAny(),
				perComprovar.getAny());
		assertEquals(
				original.getNom(),
				perComprovar.getNom());
	}

	private void emplenarDocumentArxiu(DocumentDto dto) throws IOException {
		FitxerDto fitxerPdf = getFitxerPdfDeTest();
		dto.setFitxerNom(fitxerPdf.getNom());
		dto.setFitxerContentType(fitxerPdf.getContentType());
		dto.setFitxerContingut(fitxerPdf.getContingut());
	}

}
