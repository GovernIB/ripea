/**
 * 
 */
package es.caib.ripea.core.service;

import es.caib.plugins.arxiu.api.ContingutTipus;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.ripea.core.api.dto.CarpetaDto;
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
import es.caib.ripea.core.api.dto.PrioritatEnumDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.CarpetaService;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.service.ExpedientEstatService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

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
	@Autowired
	private CarpetaService carpetaService;	
	@Autowired
	private ExpedientEstatService expedientEstatService;
	
	@Test
    public void create() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						MetaExpedientDto metaExpedient = (MetaExpedientDto)elementsCreats.get(2);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);
						//ExpedientDto expedientDtoFromDB = expedientService.findById(((EntitatEntity)elementsCreats.get(0)).getId(), expedientCreat.getId());
						assertNotNull(expedientCreat);
						assertNotNull(expedientCreat.getId());
						comprovarExpedientCoincideix(
								expedientCreate,
								expedientCreat);
						
						Mockito.verify(arxiuPluginMock, Mockito.times(1)).expedientCrear((Mockito.any(Expedient.class)));
						ArgumentCaptor<Expedient> argument = ArgumentCaptor.forClass(Expedient.class);
						Mockito.verify(arxiuPluginMock).expedientCrear(argument.capture());
						assertEquals(null, argument.getValue().getContinguts());
						assertEquals(null, argument.getValue().getFirmes());
						assertEquals(null, argument.getValue().getIdentificador());
						assertEquals(null, argument.getValue().getVersio());
						assertEquals(ContingutTipus.EXPEDIENT, argument.getValue().getTipus());
						
						assertEquals(metaExpedient.getClassificacio(), argument.getValue().getMetadades().getClassificacio());
						assertEquals(metaExpedient.getSerieDocumental(), argument.getValue().getMetadades().getSerieDocumental());
					}
				},
				"Creació d'un expedient");
	}



	@Test
    public void update() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						MetaExpedientDto metaExpedient = (MetaExpedientDto)elementsCreats.get(2);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);
						ExpedientDto modificat = expedientService.update(
								entitatCreada.getId(),
								expedientCreat.getId(),
								expedientUpdate.getNom(),
								0,
								null, 
								null, 
								null,
								null,
								PrioritatEnumDto.NORMAL);
						assertNotNull(modificat);
						assertNotNull(modificat.getId());
						assertEquals(
								expedientCreat.getId(),
								modificat.getId());
						comprovarExpedientCoincideix(
								expedientUpdate,
								modificat);
						
						Mockito.verify(arxiuPluginMock, Mockito.times(1)).expedientModificar((Mockito.any(Expedient.class)));
						ArgumentCaptor<Expedient> argument = ArgumentCaptor.forClass(Expedient.class);
						Mockito.verify(arxiuPluginMock).expedientModificar(argument.capture());
						assertEquals(null, argument.getValue().getContinguts());
						assertEquals(null, argument.getValue().getFirmes());
						assertNotNull(argument.getValue().getIdentificador());
						assertEquals(null, argument.getValue().getVersio());
						assertEquals(ContingutTipus.EXPEDIENT, argument.getValue().getTipus());
						
						assertEquals(metaExpedient.getClassificacio(), argument.getValue().getMetadades().getClassificacio());
						assertEquals(metaExpedient.getSerieDocumental(), argument.getValue().getMetadades().getSerieDocumental());
						
					}
				},
				"Modificació d'un expedient");
	}

	@Test
    public void deleteReversible() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);
						try {
							contingutService.deleteReversible(
									entitatCreada.getId(),
									expedientCreat.getId(), null, null);
;
							try {
								autenticarUsuari("user");
								expedientService.findById(
										entitatCreada.getId(),
										expedientCreat.getId(), null);
								fail("L'expedient esborrat no s'hauria d'haver trobat");
							} catch (NotFoundException expected) {
							}
							elementsCreats.remove(expedientCreat);
							
							Mockito.verify(arxiuPluginMock, Mockito.times(1)).expedientEsborrar(Mockito.any(String.class));
							
						} catch (IOException ex) {
							fail("S'han produit errors inesperats: " + ex);
						} finally {
							autenticarUsuari("admin");
							contingutService.deleteDefinitiu(
									entitatCreada.getId(),
									expedientCreat.getId());
						}
					}
				},
				"Eliminació reversible d'un expedient");
	}

	@Test
    public void deleteDefinitiu() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);
						autenticarUsuari("admin");
						contingutService.deleteDefinitiu(
								entitatCreada.getId(),
								expedientCreat.getId());
						try {
							autenticarUsuari("user");
							expedientService.findById(
									entitatCreada.getId(),
									expedientCreat.getId(), null);
							fail("L'expe`dient esborrat no s'hauria d'haver trobat");
						} catch (NotFoundException expected) {
						}
						elementsCreats.remove(expedientCreat);
//						Mockito.verify(mock, Mockito.times(1)).expedientEsborrar(Mockito.any(String.class));
					}
				},
				"Eliminació definitiva d'un expedient");
	}

	@Test
    public void alliberarUser() {
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
								expedientCreat.getId(), null);
						assertTrue(
								!alliberat.isAgafat());
						assertNull(alliberat.getAgafatPer());
					}
				},
				"Alliberació d'un expedient per un usuari");
	}

    
	@Test
    public void agafarUser() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);
						autenticarUsuari("user");
						expedientService.alliberarUser(
								entitatCreada.getId(),
								expedientCreat.getId());
						/*ExpedientDto alliberat = */expedientService.findById(
								entitatCreada.getId(),
								expedientCreat.getId(), null);
						expedientService.agafarUser(
								entitatCreada.getId(),
								expedientCreat.getId());
						ExpedientDto agafat = expedientService.findById(
								entitatCreada.getId(),
								expedientCreat.getId(), null);
						assertTrue(
								agafat.isAgafat());
						assertEquals("user", agafat.getAgafatPer().getCodi());
					}
				},
				"Agafar un expedient per un usuari");
	}

//=========================================================================

//	@Test
//    public void tancarReobrir() {
//		testAmbElementsIExpedient(
//				new TestAmbElementsCreats() {
//					@Override
//					public void executar(List<Object> elementsCreats) {
//						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
//						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(4);
//						autenticarUsuari("user");
//						assertEquals(
//								ExpedientEstatEnumDto.OBERT, expedientCreat.getEstat());
//						String motiu = "Motiu de tancament de test";
//						expedientService.tancar(
//								entitatCreada.getId(),
//								expedientCreat.getId(),
//								motiu);
//						ExpedientDto tancat = expedientService.findById(
//								entitatCreada.getId(),
//								expedientCreat.getId());
//						assertEquals(
//								ExpedientEstatEnumDto.TANCAT, tancat.getEstat());
//						assertEquals(motiu, tancat.getTancatMotiu());
//						expedientService.reobrir(
//								entitatCreada.getId(),
//								expedientCreat.getId());
//						ExpedientDto reobert = expedientService.findById(
//								entitatCreada.getId(),
//								expedientCreat.getId());
//						assertEquals(
//								ExpedientEstatEnumDto.OBERT, reobert.getEstat());
//						assertNull(reobert.getTancatMotiu());
//					}
//				});
//	}
//
//    
//    
//	
//	@Test
//    public void alliberarAdminAgafarUser() {
//		testAmbElementsIExpedient(
//				new TestAmbElementsCreats() {
//					@Override
//					public void executar(List<Object> elementsCreats) {
//						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
//						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(4);
//						assertTrue(
//								expedientCreat.isAgafat());
//						assertEquals("user", expedientCreat.getAgafatPer().getCodi());
//						autenticarUsuari("admin");
//						expedientService.alliberarAdmin(
//								entitatCreada.getId(),
//								expedientCreat.getId());
//						autenticarUsuari("user");
//						ExpedientDto alliberat = expedientService.findById(
//								entitatCreada.getId(),
//								expedientCreat.getId());
//						assertTrue(
//								!alliberat.isAgafat());
//						assertNull(alliberat.getAgafatPer());
//						expedientService.agafarUser(
//								entitatCreada.getId(),
//								expedientCreat.getId());
//						ExpedientDto agafat = expedientService.findById(
//								entitatCreada.getId(),
//								expedientCreat.getId());
//						assertTrue(
//								agafat.isAgafat());
//						assertEquals("user", agafat.getAgafatPer().getCodi());
//					}
//				});
//	}
//    
//	@Test
//	public void findById() {
//		testAmbElementsIExpedient(
//				new TestAmbElementsCreats() {
//					@Override
//					public void executar(List<Object> elementsCreats) {
//						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
//						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(4);
//						ExpedientDto trobat = expedientService.findById(
//								entitatCreada.getId(),
//								expedientCreat.getId());
//						assertNotNull(trobat);
//						assertNotNull(trobat.getId());
//						comprovarExpedientCoincideix(
//								expedientCreat,
//								trobat);
//					}
//				});
//    }

    

	@Test
	public void comentaris() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);
						String comentariText = "Comentari d'un usuari: amb accents i símbols $%&·\"'";
						List<ExpedientComentariDto> comentaris0 = expedientService.findComentarisPerContingut(
								entitatCreada.getId(),
								expedientCreat.getId());
						assertNotNull(comentaris0);
						assertTrue(comentaris0.isEmpty());
						expedientService.publicarComentariPerExpedient(
								entitatCreada.getId(),
								expedientCreat.getId(),
								comentariText, null);
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
						MetaExpedientDto metaExpedientCreat = (MetaExpedientDto)elementsCreats.get(2);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);
						List<ExpedientEstatDto> estats0 = expedientEstatService.findExpedientEstats(
								entitatCreada.getId(),
								expedientCreat.getId(), null);
						assertNotNull(estats0);
						assertTrue(estats0.isEmpty());
						autenticarUsuari("admin");
						ExpedientEstatDto estatPerCrear = new ExpedientEstatDto();
						estatPerCrear.setCodi("TST");
						estatPerCrear.setNom("Test");
						estatPerCrear.setMetaExpedientId(metaExpedientCreat.getId());
						expedientEstatService.createExpedientEstat(
								entitatCreada.getId(),
								estatPerCrear, "tothom", null);
						autenticarUsuari("user");
						List<ExpedientEstatDto> estats1 = expedientEstatService.findExpedientEstats(
								entitatCreada.getId(),
								expedientCreat.getId(), null);
						assertNotNull(estats1);
						assertTrue(estats1.size() == 1);
						ExpedientEstatDto estatCreat = estats1.get(0);
						assertEquals(estatPerCrear.getCodi(), estatCreat.getCodi());
						assertEquals(estatPerCrear.getNom(), estatCreat.getNom());
						ExpedientDto expedientAmbEstat = expedientEstatService.changeExpedientEstat(
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
						MetaExpedientDto metaExpedientCreat = (MetaExpedientDto)elementsCreats.get(2);
						ExpedientDto expedientCreat1 = (ExpedientDto)elementsCreats.get(5);
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
								"Expedient de test2 (" + System.currentTimeMillis() + ")",
								null,
								false,
								null,
								null, 
								null, 
								null,
								null,
								PrioritatEnumDto.NORMAL);
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
								expedientCreat2.getId(), null);
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
//						expedientService.relacioCreate(
//								entitatCreada.getId(),
//								expedientCreat2.getId(),
//								expedientCreat1.getId());
//						List<ExpedientDto> relacionats12 = expedientService.relacioFindAmbExpedient(
//								entitatCreada.getId(),
//								expedientCreat1.getId());
//						assertNotNull(relacionats12);
//						assertTrue(relacionats12.size() == 1);
//						List<ExpedientDto> relacionats22 = expedientService.relacioFindAmbExpedient(
//								entitatCreada.getId(),
//								expedientCreat2.getId());
//						assertNotNull(relacionats22);
//						assertTrue(relacionats22.size() == 1);
						/**/
						expedientService.relacioDelete(
								entitatCreada.getId(),
								expedientCreat2.getId(),
								expedientCreat1.getId(), null);
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
						MetaDocumentDto metaDocumentCreat = (MetaDocumentDto)elementsCreats.get(3);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);
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
						DocumentDto documentCreat = documentService.create(
								entitatCreada.getId(),
								expedientCreat.getId(),
								dto,
								true, 
								null, null);
						assertNotNull(documentCreat);
						try {
							expedientService.tancar(
									entitatCreada.getId(),
									expedientCreat.getId(),
									"Motiu de tancament",
									null, false);
							fail("No s'ha de poder tancar un expedient sense documents definitius");
						} catch (ValidationException ignored) {
						}
						String identificador = documentService.generarIdentificadorFirmaClient(
								entitatCreada.getId(),
								documentCreat.getId());
						assertNotNull(identificador);
						documentService.processarFirmaClient(
								null,
								null,
								"firma.pdf", dto.getFitxerContingut(), null, null);
						expedientService.tancar(
								entitatCreada.getId(),
								expedientCreat.getId(),
								"Motiu de tancament",
								null, false);
						ExpedientDto expedientTancat = expedientService.findById(
								entitatCreada.getId(),
								expedientCreat.getId(), null);
						assertNotNull(expedientTancat);
						assertEquals(ExpedientEstatEnumDto.TANCAT, expedientTancat.getEstat());
					}
				},
				"Tancar expedient");
	}
	
	@Test
	public void generarIndex() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) throws IOException {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						MetaDocumentDto metaDocumentCreat = (MetaDocumentDto)elementsCreats.get(3);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);
						assertNotNull(expedientCreat);
						CarpetaDto carpetaCreada = carpetaService.create(
								entitatCreada.getId(), 
								expedientCreat.getId(), 
								"Carpeta Test");
						CarpetaDto subCarpetaCreada = carpetaService.create(
								entitatCreada.getId(), 
								carpetaCreada.getId(), 
								"SubCarpeta Test 1");
						CarpetaDto subCarpetaCreada2 = carpetaService.create(
								entitatCreada.getId(), 
								subCarpetaCreada.getId(), 
								"SubCarpeta Test 2");
						
						for (int i = 0; i < 8; i++) {
							DocumentDto dto = new DocumentDto();
							dto.setNom("Test_" + i);
							dto.setData(new Date());
							dto.setDocumentTipus(DocumentTipusEnumDto.DIGITAL);
							dto.setNtiEstadoElaboracion(DocumentNtiEstadoElaboracionEnumDto.EE01);
							dto.setNtiOrigen(NtiOrigenEnumDto.O0);
							emplenarDocumentArxiu(dto);
							dto.setFirmaSeparada(false);
							MetaDocumentDto metaDocument = new MetaDocumentDto();
							metaDocument.setId(metaDocumentCreat.getId());
							dto.setMetaNode(metaDocument);
							DocumentDto documentCreat = null;
							
							//Pare = expedient
							if (i == 0 || i == 1 || i == 2) {
								documentCreat = documentService.create(
										entitatCreada.getId(),
										expedientCreat.getId(),
										dto,
										true, 
										null, null);
								assertNotNull(documentCreat);
							}
							//Pare = carpeta
							if (i == 3) {
								documentCreat = documentService.create(
										entitatCreada.getId(),
										carpetaCreada.getId(),
										dto,
										true, 
										null, null);
								assertNotNull(documentCreat);
							}
							//Pare = sub carpeta
							if (i == 4 || i == 5) {
								documentCreat = documentService.create(
										entitatCreada.getId(),
										subCarpetaCreada.getId(),
										dto,
										true, 
										null, null);
								assertNotNull(documentCreat);
							}
							if (i == 6 || i == 7) {
								documentCreat = documentService.create(
										entitatCreada.getId(),
										subCarpetaCreada2.getId(),
										dto,
										true, 
										null, null);
								assertNotNull(documentCreat);
							}
						}
						
						FitxerDto index = expedientService.exportIndexExpedient(
								entitatCreada.getId(), 
								new HashSet<>(Arrays.asList(expedientCreat.getId())),
								false,
								"PDF");
						
						assertNotNull(index);
						assertNotNull(index.getContingut());
						assertTrue(index.getContingut().length > 0);
						
					}
				},
				"Generar índex expedient");
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
