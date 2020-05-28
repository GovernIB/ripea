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
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import es.caib.plugins.arxiu.api.ContingutTipus;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientComentariDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientEstatEnumDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.exception.NotFoundException;

/**
 * Tests per al servei de gestió d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/ripea/core/application-context-test.xml"})
public class ExpedientServiceTest extends BaseExpedientServiceTest {


	@Test
    public void create() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						MetaExpedientDto metaExpedient = (MetaExpedientDto)elementsCreats.get(1);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(4);
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
						
						assertEquals(metaExpedient.getClassificacioSia(), argument.getValue().getMetadades().getClassificacio());
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
						MetaExpedientDto metaExpedient = (MetaExpedientDto)elementsCreats.get(1);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(4);
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
						
						Mockito.verify(arxiuPluginMock, Mockito.times(1)).expedientModificar((Mockito.any(Expedient.class)));
						ArgumentCaptor<Expedient> argument = ArgumentCaptor.forClass(Expedient.class);
						Mockito.verify(arxiuPluginMock).expedientModificar(argument.capture());
						assertEquals(null, argument.getValue().getContinguts());
						assertEquals(null, argument.getValue().getFirmes());
						assertNotNull(argument.getValue().getIdentificador());
						assertEquals(null, argument.getValue().getVersio());
						assertEquals(ContingutTipus.EXPEDIENT, argument.getValue().getTipus());
						
						assertEquals(metaExpedient.getClassificacioSia(), argument.getValue().getMetadades().getClassificacio());
						assertEquals(metaExpedient.getSerieDocumental(), argument.getValue().getMetadades().getSerieDocumental());
						
					}
				});
	}

	@Test
    public void deleteReversible() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(4);
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
				});
	}

	@Test
    public void deleteDefinitiu() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(4);
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
							fail("L'expe`dient esborrat no s'hauria d'haver trobat");
						} catch (NotFoundException expected) {
						}
						elementsCreats.remove(expedientCreat);
						
//						Mockito.verify(mock, Mockito.times(1)).expedientEsborrar(Mockito.any(String.class));
						
					}
				});
	}

	@Test
    public void alliberarUser() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(4);
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
					}
				});
	}

    
	@Test
    public void agafarUser() {

		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(4);
						autenticarUsuari("user");
						expedientService.alliberarUser(
								entitatCreada.getId(),
								expedientCreat.getId());
						ExpedientDto alliberat = expedientService.findById(
								entitatCreada.getId(),
								expedientCreat.getId());
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

}
