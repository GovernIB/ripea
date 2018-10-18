/**
 * 
 */
package es.caib.ripea.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.dto.PrincipalTipusEnumDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.MetaDadaService;
import es.caib.ripea.core.helper.PropertiesHelper;

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
	private MetaDadaDto metaDadaCreate;
	private MetaDadaDto metaDadaUpdate;
	private PermisDto permisUserRead;

	@Before
	public void setUp() {
		PropertiesHelper.getProperties("classpath:es/caib/ripea/core/test.properties");
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
		metaDadaCreate = new MetaDadaDto();
		metaDadaCreate.setCodi("TEST1");
		metaDadaCreate.setNom("Metadada de test");
		metaDadaCreate.setDescripcio("Descripció de test");
		metaDadaCreate.setTipus(MetaDadaTipusEnumDto.TEXT);
		/*metaDadaCreate.setGlobalExpedient(false);
		metaDadaCreate.setGlobalDocument(false);
		metaDadaCreate.setGlobalMultiplicitat(MultiplicitatEnumDto.M_0_1);
		metaDadaCreate.setGlobalReadOnly(false);*/
		metaDadaUpdate = new MetaDadaDto();
		metaDadaUpdate.setCodi("TEST2");
		metaDadaUpdate.setNom("Metadada de test2");
		metaDadaUpdate.setDescripcio("Descripció de test2");
		metaDadaUpdate.setTipus(MetaDadaTipusEnumDto.SENCER);
		/*metaDadaUpdate.setGlobalExpedient(true);
		metaDadaUpdate.setGlobalDocument(true);
		metaDadaUpdate.setGlobalMultiplicitat(MultiplicitatEnumDto.M_1);
		metaDadaUpdate.setGlobalReadOnly(true);*/
		permisUserRead = new PermisDto();
		permisUserRead.setRead(true);
		permisUserRead.setPrincipalTipus(PrincipalTipusEnumDto.USUARI);
		permisUserRead.setPrincipalNom("user");
	}

	@Test
    public void create() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						MetaDadaDto metadadaCreada = (MetaDadaDto)elementsCreats.get(1);
						assertNotNull(metadadaCreada);
						assertNotNull(metadadaCreada.getId());
						comprovarMetaDadaCoincideix(
								metaDadaCreate,
								metadadaCreada);
						assertEquals(true, metadadaCreada.isActiva());
					}
				},
				entitat,
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
						MetaDadaDto metadadaCreada = (MetaDadaDto)elementsCreats.get(1);
						MetaDadaDto trobada = metaDadaService.findById(
								entitatCreada.getId(),
								null, // TODO
								metadadaCreada.getId());
						assertNotNull(trobada);
						assertNotNull(trobada.getId());
						comprovarMetaDadaCoincideix(
								metadadaCreada,
								trobada);
					}
				},
				entitat,
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
						MetaDadaDto metadadaCreada = (MetaDadaDto)elementsCreats.get(1);
						metaDadaUpdate.setId(metadadaCreada.getId());
						MetaDadaDto modificada = metaDadaService.update(
								entitatCreada.getId(),
								null, // TODO
								metaDadaUpdate);
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
				entitat,
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
						MetaDadaDto metadadaCreada = (MetaDadaDto)elementsCreats.get(1);
						MetaDadaDto esborrada = metaDadaService.delete(
								entitatCreada.getId(),
								null, // TODO
								metadadaCreada.getId());
						comprovarMetaDadaCoincideix(
								metaDadaCreate,
								esborrada);
						try {
							metaDadaService.findById(
									entitatCreada.getId(),
									null, // TODO
									metadadaCreada.getId());
							fail("La meta-dada esborrada no s'hauria d'haver trobat");
						} catch (NotFoundException expected) {
						}
						elementsCreats.remove(metadadaCreada);
					}
				},
				entitat,
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
						MetaDadaDto metadadaCreada = (MetaDadaDto)elementsCreats.get(1);
						MetaDadaDto desactivada = metaDadaService.updateActiva(
								entitatCreada.getId(),
								null, // TODO
								metadadaCreada.getId(),
								false);
						assertEquals(
								false,
								desactivada.isActiva());
						MetaDadaDto activada = metaDadaService.updateActiva(
								entitatCreada.getId(),
								null, // TODO
								metadadaCreada.getId(),
								true);
						assertEquals(
								true,
								activada.isActiva());
					}
				},
				entitat,
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
						MetaDadaDto metadadaCreada = (MetaDadaDto)elementsCreats.get(1);
						MetaDadaDto trobada = metaDadaService.findByCodi(
								entitatCreada.getId(),
								null, // TODO
								metadadaCreada.getCodi());
						comprovarMetaDadaCoincideix(
								metadadaCreada,
								trobada);
					}
				},
				entitat,
				metaDadaCreate);
	}

	@Test(expected = DataIntegrityViolationException.class)
	public void errorSiCodiDuplicat() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						autenticarUsuari("admin");
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						metaDadaService.create(
								entitatCreada.getId(),
								null, // TODO
								metaDadaCreate);
					}
				},
				entitat,
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
