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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientEstatEnumDto;
import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.dto.PrincipalTipusEnumDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.helper.PropertiesHelper;

/**
 * Tests per al servei d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/ripea/core/application-context-test.xml"})
@Transactional
public class ExpedientServiceTest extends BaseServiceTest {

	@Autowired
	private ContingutService contingutService;
	@Autowired
	private ExpedientService expedientService;

	private EntitatDto entitat;
	private MetaDadaDto metaDada;
	private MetaDocumentDto metaDocument;
	private MetaExpedientDto metaExpedient;
	private ExpedientDto expedientCreate;
	private ExpedientDto expedientUpdate;
	private PermisDto permisUserRead;

	@Before
	public void setUp() {
		PropertiesHelper.getProperties("classpath:es/caib/ripea/core/test.properties");
		entitat = new EntitatDto();
		entitat.setCodi("LIMIT");
		entitat.setNom("Limit Tecnologies");
		entitat.setCif("00000000T");
		entitat.setUnitatArrel(CODI_UNITAT_ARREL);
		List<PermisDto> permisosEntitat = new ArrayList<PermisDto>();
		PermisDto permisAdminAdmin = new PermisDto();
		permisAdminAdmin.setAdministration(true);
		permisAdminAdmin.setPrincipalTipus(PrincipalTipusEnumDto.USUARI);
		permisAdminAdmin.setPrincipalNom("admin");
		permisosEntitat.add(permisAdminAdmin);
		PermisDto permisReadUser = new PermisDto();
		permisReadUser.setRead(true);
		permisReadUser.setPrincipalTipus(PrincipalTipusEnumDto.USUARI);
		permisReadUser.setPrincipalNom("user");
		permisosEntitat.add(permisReadUser);
		entitat.setPermisos(permisosEntitat);
		metaDada = new MetaDadaDto();
		metaDada.setCodi("TEST1");
		metaDada.setNom("Metadada de test");
		metaDada.setDescripcio("Descripció de test");
		metaDada.setTipus(MetaDadaTipusEnumDto.TEXT);
		/*metaDada.setGlobalExpedient(false);
		metaDada.setGlobalDocument(false);
		metaDada.setGlobalMultiplicitat(MultiplicitatEnumDto.M_0_1);
		metaDada.setGlobalReadOnly(false);*/
		metaDocument = new MetaDocumentDto();
		metaDocument.setCodi("TEST1");
		metaDocument.setNom("Metadocument de test");
		metaDocument.setDescripcio("Descripció de test");
		/*metaDocument.setGlobalExpedient(false);
		metaDocument.setGlobalMultiplicitat(MultiplicitatEnumDto.M_0_1);
		metaDocument.setGlobalReadOnly(false);*/
		metaDocument.setFirmaPortafirmesActiva(false);
		metaDocument.setPortafirmesDocumentTipus("1234");
		metaDocument.setPortafirmesFluxId("1234");
		metaDocument.setPortafirmesResponsables(new String[] {"123456789Z"});
		metaDocument.setPortafirmesFluxTipus(MetaDocumentFirmaFluxTipusEnumDto.SERIE);
		metaDocument.setPortafirmesCustodiaTipus("1234");
		metaDocument.setFirmaPassarelaCustodiaTipus("1234");
		metaExpedient = new MetaExpedientDto();
		metaExpedient.setCodi("TEST1");
		metaExpedient.setNom("Metadocument de test");
		metaExpedient.setDescripcio("Descripció de test");
		metaExpedient.setSerieDocumental("1234");
		metaExpedient.setClassificacioSia("1234");
		metaExpedient.setNotificacioActiva(false);
		/*metaExpedient.setNotificacioSeuProcedimentCodi("1234");
		metaExpedient.setNotificacioSeuRegistreLlibre("1234");
		metaExpedient.setNotificacioSeuRegistreOficina("1234");
		metaExpedient.setNotificacioSeuRegistreOrgan("1234");
		metaExpedient.setNotificacioSeuExpedientUnitatOrganitzativa("1234");
		metaExpedient.setNotificacioAvisTitol("1234");
		metaExpedient.setNotificacioAvisText("1234");
		metaExpedient.setNotificacioAvisTextMobil("1234");
		metaExpedient.setNotificacioOficiTitol("1234");
		metaExpedient.setNotificacioOficiText("1234");*/
		metaExpedient.setPareId(null);
		List<PermisDto> permisosExpedient = new ArrayList<PermisDto>();
		PermisDto permisUser = new PermisDto();
		permisUser.setRead(true);
		permisUser.setWrite(true);
		permisUser.setCreate(true);
		permisUser.setDelete(true);
		permisUser.setPrincipalTipus(PrincipalTipusEnumDto.USUARI);
		permisUser.setPrincipalNom("user");
		permisosExpedient.add(permisUser);
		metaExpedient.setPermisos(permisosExpedient);
		expedientCreate = new ExpedientDto();
		expedientCreate.setAny(Calendar.getInstance().get(Calendar.YEAR));
		expedientCreate.setNom("Expedient de test");
		expedientUpdate = new ExpedientDto();
		expedientUpdate.setAny(Calendar.getInstance().get(Calendar.YEAR));
		expedientUpdate.setNom("Expedient de test2");
		permisUserRead = new PermisDto();
		permisUserRead.setRead(true);
		permisUserRead.setPrincipalTipus(PrincipalTipusEnumDto.USUARI);
		permisUserRead.setPrincipalNom("user");
	}

	@Test
    public void create() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);
						assertNotNull(expedientCreat);
						assertNotNull(expedientCreat.getId());
						comprovarExpedientCoincideix(
								expedientCreate,
								expedientCreat);
					}
				});
	}

	@Test
	public void findById() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);
						ExpedientDto trobat = expedientService.findById(
								entitatCreada.getId(),
								expedientCreat.getId());
						assertNotNull(trobat);
						assertNotNull(trobat.getId());
						comprovarExpedientCoincideix(
								expedientCreat,
								trobat);
					}
				});
    }

	@Test
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

	@Test
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

	@Test
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

	@Test
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

	@Test
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

	@Test
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

	private void testAmbElementsIExpedient(
			final TestAmbElementsCreats testAmbExpedientCreat) {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						autenticarUsuari("user");
//						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
//						MetaExpedientDto metaExpedientCreat = (MetaExpedientDto)elementsCreats.get(3);
//						ExpedientDto creat = expedientService.create(
//								entitatCreada.getId(),
//								metaExpedientCreat.getId(),
//								null,
//								expedientCreate.getAny(),
//								null,
//								expedientCreate.getNom(),
//								null,
//								false);
//						try {
//							elementsCreats.add(creat);
//							testAmbExpedientCreat.executar(
//									elementsCreats);
//						} catch (Exception ex) {
//							System.out.println("El test ha produït una excepció:");
//							ex.printStackTrace(System.out);
//						} finally {
//							for (Object element: elementsCreats) {
//								if (element instanceof ExpedientDto) {
//									autenticarUsuari("admin");
//									contingutService.deleteDefinitiu(
//											entitatCreada.getId(),
//											((ExpedientDto)element).getId());
//								}
//							}
//							elementsCreats.remove(creat);
//						}
					}
				},
				entitat,
				metaDada,
				metaDocument,
				metaExpedient);
	}

	class TestAmbElementsIExpedient extends TestAmbElementsCreats {
		@Override
		public void executar(List<Object> elementsCreats) {
			
		}
	}

}
