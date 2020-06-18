/**
 * 
 */
package es.caib.ripea.core.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.dto.PrincipalTipusEnumDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.EntitatService;
import es.caib.ripea.core.helper.PropertiesHelper;

/**
 * Tests per al servei d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/ripea/core/application-context-test.xml"})
@Transactional
public class EntitatServiceTest extends BaseServiceTest {

	@Autowired
	private EntitatService entitatService;

	private EntitatDto entitatCreate;
	private EntitatDto entitatUpdate;
	private PermisDto permisUserRead;
	private PermisDto permisAdminAdmin;

	@Before
	public void setUp() {
		PropertiesHelper.getProperties("classpath:es/caib/ripea/core/test.properties");
		entitatCreate = new EntitatDto();
		entitatCreate.setCodi("LIMIT");
		entitatCreate.setNom("Limit Tecnologies");
		entitatCreate.setCif("00000000T");
		entitatCreate.setUnitatArrel(CODI_UNITAT_ARREL);
		entitatUpdate = new EntitatDto();
		entitatUpdate.setId(new Long(1));
		entitatUpdate.setCodi("LIMITE");
		entitatUpdate.setNom("Limit Tecnologies 0");
		entitatUpdate.setCif("12345678T");
		entitatUpdate.setUnitatArrel("LIM000002");
		permisUserRead = new PermisDto();
		permisUserRead.setRead(true);
		permisUserRead.setPrincipalTipus(PrincipalTipusEnumDto.USUARI);
		permisUserRead.setPrincipalNom("user");
		permisAdminAdmin = new PermisDto();
		permisAdminAdmin.setAdministration(true);
		permisAdminAdmin.setPrincipalTipus(PrincipalTipusEnumDto.USUARI);
		permisAdminAdmin.setPrincipalNom("admin");
	}

	@Test
    public void create() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto creada = (EntitatDto)elementsCreats.get(0);
						assertNotNull(creada);
						assertNotNull(creada.getId());
						comprovarEntitatCoincideix(
								entitatCreate,
								creada);
						assertEquals(true, creada.isActiva());
					}
				},
				entitatCreate);
	}

	@Test
	public void findById() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						autenticarUsuari("super");
						EntitatDto creada = (EntitatDto)elementsCreats.get(0);
						EntitatDto trobada = entitatService.findById(creada.getId());
						assertNotNull(trobada);
						assertNotNull(trobada.getId());
						comprovarEntitatCoincideix(
								entitatCreate,
								trobada);
					}
				},
				entitatCreate);
    }

	@Test
	public void update() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						autenticarUsuari("super");
						EntitatDto creada = (EntitatDto)elementsCreats.get(0);
						entitatUpdate.setId(creada.getId());
						EntitatDto modificada = entitatService.update(entitatUpdate);
						assertNotNull(modificada);
						assertNotNull(modificada.getId());
						assertEquals(
								creada.getId(),
								modificada.getId());
						comprovarEntitatCoincideix(
								entitatUpdate,
								modificada);
						assertEquals(true, modificada.isActiva());
					}
				},
				entitatCreate);
    }

	@Test
	public void delete() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						autenticarUsuari("super");
						EntitatDto creada = (EntitatDto)elementsCreats.get(0);
						EntitatDto esborrada = entitatService.delete(creada.getId());
						comprovarEntitatCoincideix(
								entitatCreate,
								esborrada);
						try {
							entitatService.findById(creada.getId());
							fail("La entitat esborrada no s'hauria d'haver trobat");
						} catch (NotFoundException expected) {
						}
						elementsCreats.remove(creada);
					}
				},
				entitatCreate);
	}

	@Test
	public void updateActiva() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						autenticarUsuari("super");
						EntitatDto creada = (EntitatDto)elementsCreats.get(0);
						EntitatDto desactivada = entitatService.updateActiva(
								creada.getId(),
								false);
						assertEquals(
								false,
								desactivada.isActiva());
						EntitatDto activada = entitatService.updateActiva(
								creada.getId(),
								true);
						assertEquals(
								true,
								activada.isActiva());
					}
				},
				entitatCreate);
	}

	@Test
	public void findByCodi() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						autenticarUsuari("super");
						EntitatDto trobada = entitatService.findByCodi(
								entitatCreate.getCodi());
						comprovarEntitatCoincideix(
								entitatCreate,
								trobada);
					}
				},
				entitatCreate);
	}

	@Test
	public void managePermisSuper() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto creada = (EntitatDto)elementsCreats.get(0);
						autenticarUsuari("user");
						List<EntitatDto> entitatsAccessibles = entitatService.findAccessiblesUsuariActual();
						assertThat(
								entitatsAccessibles.size(),
								is(0));
						autenticarUsuari("super");
						List<PermisDto> permisos = entitatService.findPermisSuper(creada.getId());
						assertThat(
								permisos.size(),
								is(0));
						entitatService.updatePermisSuper(
								creada.getId(),
								permisUserRead);
						permisos = entitatService.findPermisSuper(creada.getId());
						assertThat(
								permisos.size(),
								is(1));
						comprovarPermisCoincideix(
								permisUserRead,
								permisos.get(0));
						autenticarUsuari("user");
						entitatsAccessibles = entitatService.findAccessiblesUsuariActual();
						assertThat(
								entitatsAccessibles.size(),
								is(1));
						assertThat(
								entitatsAccessibles.get(0).getId(),
								is(creada.getId()));
						autenticarUsuari("super");
						entitatService.deletePermisSuper(
								creada.getId(),
								permisos.get(0).getId());
						permisos = entitatService.findPermisSuper(creada.getId());
						assertThat(
								permisos.size(),
								is(0));
						autenticarUsuari("user");
						entitatsAccessibles = entitatService.findAccessiblesUsuariActual();
						assertThat(
								entitatsAccessibles.size(),
								is(0));
					}
				},
				entitatCreate);
	}

	@Test
	public void managePermisAdmin() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto creada = (EntitatDto)elementsCreats.get(0);
						autenticarUsuari("user");
						List<EntitatDto> entitatsAccessibles = entitatService.findAccessiblesUsuariActual();
						assertThat(
								entitatsAccessibles.size(),
								is(0));
						autenticarUsuari("super");
						List<PermisDto> permisosSuper = entitatService.findPermisSuper(creada.getId());
						assertThat(
								permisosSuper.size(),
								is(0));
						entitatService.updatePermisSuper(
								creada.getId(),
								permisAdminAdmin);
						permisosSuper = entitatService.findPermisSuper(creada.getId());
						assertThat(
								permisosSuper.size(),
								is(1));
						comprovarPermisCoincideix(
								permisAdminAdmin,
								permisosSuper.get(0));
						autenticarUsuari("admin");
						entitatsAccessibles = entitatService.findAccessiblesUsuariActual();
						assertThat(
								entitatsAccessibles.size(),
								is(1));
						assertThat(
								entitatsAccessibles.get(0).getId(),
								is(creada.getId()));
						List<PermisDto> permisosAdmin = entitatService.findPermisAdmin(creada.getId());
						assertThat(
								permisosAdmin.size(),
								is(1));
						entitatService.updatePermisAdmin(
								creada.getId(),
								permisUserRead);
						permisosAdmin = entitatService.findPermisAdmin(creada.getId());
						PermisDto permisCreatPerAdmin = null;
						for (PermisDto permis: permisosAdmin) {
							if ("user".equals(permis.getPrincipalNom())) {
								permisCreatPerAdmin = permis;
								break;
							}
						}
						assertNotNull(permisCreatPerAdmin);
						assertThat(
								permisosAdmin.size(),
								is(2));
						comprovarPermisCoincideix(
								permisUserRead,
								permisCreatPerAdmin);
						autenticarUsuari("user");
						entitatsAccessibles = entitatService.findAccessiblesUsuariActual();
						assertThat(
								entitatsAccessibles.size(),
								is(1));
						assertThat(
								entitatsAccessibles.get(0).getId(),
								is(creada.getId()));
						autenticarUsuari("admin");
						entitatService.deletePermisAdmin(
								creada.getId(),
								permisCreatPerAdmin.getId());
						permisosAdmin = entitatService.findPermisAdmin(creada.getId());
						assertThat(
								permisosAdmin.size(),
								is(1));
						autenticarUsuari("user");
						entitatsAccessibles = entitatService.findAccessiblesUsuariActual();
						assertThat(
								entitatsAccessibles.size(),
								is(0));
						autenticarUsuari("super");
						permisosSuper = entitatService.findPermisSuper(creada.getId());
						entitatService.deletePermisSuper(
								creada.getId(),
								permisosSuper.get(0).getId());
						permisosSuper = entitatService.findPermisSuper(creada.getId());
						assertThat(
								permisosSuper.size(),
								is(0));
						autenticarUsuari("admin");
						entitatsAccessibles = entitatService.findAccessiblesUsuariActual();
						assertThat(
								entitatsAccessibles.size(),
								is(0));
					}
				},
				entitatCreate);
	}

	@Test
	public void errorSiCodiDuplicat() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						autenticarUsuari("super");
						try {
							entitatService.create(entitatCreate);
						} catch (DataIntegrityViolationException ex) {
							// Excepció esperada
						}
					}
				},
				entitatCreate);
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminCreate() {
		autenticarUsuari("admin");
		entitatService.create(entitatCreate);
	}
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserCreate() {
		autenticarUsuari("user");
		entitatService.create(entitatCreate);
	}
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminUpdate() {
		autenticarUsuari("admin");
		entitatService.update(entitatCreate);
	}
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserUpdate() {
		autenticarUsuari("user");
		entitatService.update(entitatCreate);
	}
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminDelete() {
		autenticarUsuari("admin");
		entitatService.delete(new Long(1));
	}
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserDelete() {
		autenticarUsuari("user");
		entitatService.delete(new Long(1));
	}



	private void comprovarEntitatCoincideix(
			EntitatDto original,
			EntitatDto perComprovar) {
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
				original.getCif(),
				perComprovar.getCif());
		assertEquals(
				original.getCif(),
				perComprovar.getCif());
		assertEquals(
				original.getUnitatArrel(),
				perComprovar.getUnitatArrel());
	}

	private void comprovarPermisCoincideix(
			PermisDto original,
			PermisDto perComprovar) {
		assertEquals(
				original.getPrincipalNom(),
				perComprovar.getPrincipalNom());
		assertEquals(
				original.getPrincipalTipus(),
				perComprovar.getPrincipalTipus());
		assertEquals(
				original.isRead(),
				perComprovar.isRead());
		assertEquals(
				original.isWrite(),
				perComprovar.isWrite());
		assertEquals(
				original.isCreate(),
				perComprovar.isCreate());
		assertEquals(
				original.isDelete(),
				perComprovar.isDelete());
		assertEquals(
				original.isAdministration(),
				perComprovar.isAdministration());
	}

}
