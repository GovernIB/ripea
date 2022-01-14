/**
 * 
 */
package es.caib.ripea.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import es.caib.ripea.core.api.dto.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.MetaDadaService;

/**
 * Tests per al servei d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/ripea/core/application-context-test.xml"})
@Transactional
public class MetaDadaServiceTest extends BaseServiceTest {

	@Autowired
	private MetaDadaService metaDadaService;

	private EntitatDto entitat;
	protected OrganGestorDto organGestorDto;
	private MetaExpedientDto metaExpedient;
	private MetaDadaDto metaDadaCreate;
	private MetaDadaDto metaDadaUpdate;
	private PermisDto permisUserRead;

	@Before
	public void setUp() {
		setDefaultConfigs();
		entitat = new EntitatDto();
		entitat.setCodi("LIMIT");
		entitat.setNom("Limit Tecnologies");
		entitat.setCif("00000000T");
		entitat.setUnitatArrel(CODI_UNITAT_ARREL);
		List<PermisDto> permisos = new ArrayList<PermisDto>();
		PermisDto permisAdminAdmin = new PermisDto();
		permisAdminAdmin.setAdministration(true);
		permisAdminAdmin.setPrincipalTipus(PrincipalTipusEnumDto.USUARI);
		permisAdminAdmin.setPrincipalNom("admin");
		permisos.add(permisAdminAdmin);
		entitat.setPermisos(permisos);
		metaExpedient = new MetaExpedientDto();
		metaExpedient.setCodi("TEST1");
		metaExpedient.setNom("Metaexpedient de test");
		metaExpedient.setDescripcio("Descripció de test");
		metaExpedient.setSerieDocumental("1234");
		metaExpedient.setClassificacioSia("1234");
		metaExpedient.setNotificacioActiva(false);
		metaExpedient.setPareId(null);
		metaDadaCreate = new MetaDadaDto();
		metaDadaCreate.setCodi("TEST1");
		metaDadaCreate.setNom("Metadada de test");
		metaDadaCreate.setDescripcio("Descripció de test");
		metaDadaCreate.setTipus(MetaDadaTipusEnumDto.TEXT);
		metaDadaCreate.setMultiplicitat(MultiplicitatEnumDto.M_1);
		/*metaDadaCreate.setGlobalExpedient(false);
		metaDadaCreate.setGlobalDocument(false);
		metaDadaCreate.setGlobalMultiplicitat(MultiplicitatEnumDto.M_0_1);
		metaDadaCreate.setGlobalReadOnly(false);*/
		metaDadaUpdate = new MetaDadaDto();
		metaDadaUpdate.setCodi("TEST2");
		metaDadaUpdate.setNom("Metadada de test2");
		metaDadaUpdate.setDescripcio("Descripció de test2");
		metaDadaUpdate.setTipus(MetaDadaTipusEnumDto.SENCER);
		metaDadaCreate.setMultiplicitat(MultiplicitatEnumDto.M_0_1);
		/*metaDadaUpdate.setGlobalExpedient(true);
		metaDadaUpdate.setGlobalDocument(true);
		metaDadaUpdate.setGlobalMultiplicitat(MultiplicitatEnumDto.M_1);
		metaDadaUpdate.setGlobalReadOnly(true);*/
		permisUserRead = new PermisDto();
		permisUserRead.setRead(true);
		permisUserRead.setPrincipalTipus(PrincipalTipusEnumDto.USUARI);
		permisUserRead.setPrincipalNom("user");

		organGestorDto = new OrganGestorDto();
		organGestorDto.setCodi("A000000000");
		organGestorDto.setNom("Òrgan 0");
	}

	@Test
    public void create() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						MetaDadaDto metadadaCreada = (MetaDadaDto)elementsCreats.get(3);
						assertNotNull(metadadaCreada);
						assertNotNull(metadadaCreada.getId());
						comprovarMetaDadaCoincideix(
								metaDadaCreate,
								metadadaCreada);
						assertEquals(true, metadadaCreada.isActiva());
					}
				},
				"Creació d'una meta-dada a dins un meta-expedient",
				entitat,
				organGestorDto,
				metaExpedient,
				metaDadaCreate);
	}

	@Test
	public void findById() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						autenticarUsuari("admin");
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						MetaExpedientDto expedientCreat = (MetaExpedientDto)elementsCreats.get(2);
						MetaDadaDto metadadaCreada = (MetaDadaDto)elementsCreats.get(3);
						MetaDadaDto trobada = metaDadaService.findById(
								entitatCreada.getId(),
								expedientCreat.getId(),
								metadadaCreada.getId());
						assertNotNull(trobada);
						assertNotNull(trobada.getId());
						comprovarMetaDadaCoincideix(
								metadadaCreada,
								trobada);
					}
				},
				"Consulta d'una meta-dada a dins un meta-expedient",
				entitat,
				organGestorDto,
				metaExpedient,
				metaDadaCreate);
    }

	@Test
    public void update() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						autenticarUsuari("admin");
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						MetaExpedientDto expedientCreat = (MetaExpedientDto)elementsCreats.get(2);
						MetaDadaDto metadadaCreada = (MetaDadaDto)elementsCreats.get(3);
						metaDadaUpdate.setId(metadadaCreada.getId());
						MetaDadaDto modificada = metaDadaService.update(
								entitatCreada.getId(),
								expedientCreat.getId(),
								metaDadaUpdate, "tothom", null);
						assertNotNull(modificada);
						assertNotNull(modificada.getId());
						assertEquals(
								metadadaCreada.getId(),
								modificada.getId());
						comprovarMetaDadaCoincideix(
								metaDadaUpdate,
								modificada);
						assertEquals(true, modificada.isActiva());
					}
				},
				"Modificació d'una meta-dada a dins un meta-expedient",
				entitat,
				organGestorDto,
				metaExpedient,
				metaDadaCreate);
	}

	@Test
	public void delete() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						autenticarUsuari("admin");
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						MetaExpedientDto expedientCreat = (MetaExpedientDto)elementsCreats.get(2);
						MetaDadaDto metadadaCreada = (MetaDadaDto)elementsCreats.get(3);
						MetaDadaDto esborrada = metaDadaService.delete(
								entitatCreada.getId(),
								expedientCreat.getId(),
								metadadaCreada.getId(), "tothom", null);
						comprovarMetaDadaCoincideix(
								metaDadaCreate,
								esborrada);
						try {
							metaDadaService.findById(
									entitatCreada.getId(),
									expedientCreat.getId(),
									metadadaCreada.getId());
							fail("La meta-dada esborrada no s'hauria d'haver trobat");
						} catch (NotFoundException expected) {
						}
						elementsCreats.remove(metadadaCreada);
					}
				},
				"Eliminació d'una meta-dada de dins un meta-expedient",
				entitat,
				organGestorDto,
				metaExpedient,
				metaDadaCreate);
	}

	@Test
	public void updateActiva() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						autenticarUsuari("admin");
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						MetaExpedientDto expedientCreat = (MetaExpedientDto)elementsCreats.get(2);
						MetaDadaDto metadadaCreada = (MetaDadaDto)elementsCreats.get(3);
						MetaDadaDto desactivada = metaDadaService.updateActiva(
								entitatCreada.getId(),
								expedientCreat.getId(),
								metadadaCreada.getId(),
								false, "tothom", null);
						assertEquals(
								false,
								desactivada.isActiva());
						MetaDadaDto activada = metaDadaService.updateActiva(
								entitatCreada.getId(),
								expedientCreat.getId(),
								metadadaCreada.getId(),
								true, "tothom", null);
						assertEquals(
								true,
								activada.isActiva());
					}
				},
				"Activació/desactivació d'una meta-dada a dins un meta-expedient",
				entitat,
				organGestorDto,
				metaExpedient,
				metaDadaCreate);
	}

	@Test
	public void findByEntitatCodi() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						autenticarUsuari("admin");
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						MetaExpedientDto expedientCreat = (MetaExpedientDto)elementsCreats.get(2);
						MetaDadaDto metadadaCreada = (MetaDadaDto)elementsCreats.get(3);
						MetaDadaDto trobada = metaDadaService.findByCodi(
								entitatCreada.getId(),
								expedientCreat.getId(),
								metadadaCreada.getCodi());
						comprovarMetaDadaCoincideix(
								metadadaCreada,
								trobada);
					}
				},
				"Consulta per codi d'una meta-dada a dins un meta-expedient",
				entitat,
				organGestorDto,
				metaExpedient,
				metaDadaCreate);
	}

	@Test
	public void errorSiCodiDuplicat() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						autenticarUsuari("admin");
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						MetaExpedientDto expedientCreat = (MetaExpedientDto)elementsCreats.get(2);
						try {
							metaDadaService.create(
									entitatCreada.getId(),
									expedientCreat.getId(),
									metaDadaCreate, "tothom", null);
						} catch (DataIntegrityViolationException ex) {
							// Excepció esperada
						}
					}
				},
				"Verificació de que no es pot crear una meta-dada amb el codi duplicat a dins un mateix meta-expedient",
				entitat,
				organGestorDto,
				metaExpedient,
				metaDadaCreate);
	}



	private void comprovarMetaDadaCoincideix(
			MetaDadaDto original,
			MetaDadaDto perComprovar) {
		assertEquals(
				original.getCodi(),
				perComprovar.getCodi());
		assertEquals(
				original.getNom(),
				perComprovar.getNom());
		assertEquals(
				original.getDescripcio(),
				perComprovar.getDescripcio());
		assertEquals(
				original.getTipus(),
				perComprovar.getTipus());
		assertEquals(
				original.getMultiplicitat(),
				perComprovar.getMultiplicitat());
		/*assertEquals(
				original.isGlobalExpedient(),
				perComprovar.isGlobalExpedient());
		assertEquals(
				original.isGlobalDocument(),
				perComprovar.isGlobalDocument());
		assertEquals(
				original.getGlobalMultiplicitat(),
				perComprovar.getGlobalMultiplicitat());
		assertEquals(
				original.isGlobalReadOnly(),
				perComprovar.isGlobalReadOnly());*/
	}

}
