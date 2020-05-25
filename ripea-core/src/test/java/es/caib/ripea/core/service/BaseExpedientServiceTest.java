/**
 * 
 */
package es.caib.ripea.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import es.caib.plugins.arxiu.api.IArxiuPlugin;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.dto.PrincipalTipusEnumDto;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.ExpedientService;

/**
 * Classe que es pot utilitzar com a base dels tests que requereixen la creació
 * prèvia d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseExpedientServiceTest extends BaseServiceTest {

	@Mock
    private IArxiuPlugin iArxiuplugin;

	@Autowired
	protected ContingutService contingutService;
	@Autowired
	@InjectMocks
	protected ExpedientService expedientService;

	private EntitatDto entitat;
	private MetaDadaDto metaDada;
	private MetaDocumentDto metaDocument;
	private MetaExpedientDto metaExpedient;

	protected ExpedientDto expedientCreate;
	protected ExpedientDto expedientUpdate;
	//private PermisDto permisUserRead;

	@Before
	public void setUp() {
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
		permisAdminAdmin.setRead(true);
		permisAdminAdmin.setWrite(true);
		permisAdminAdmin.setCreate(true);
		permisAdminAdmin.setDelete(true);
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
		metaDada.setMultiplicitat(MultiplicitatEnumDto.M_0_N);
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
		metaDocument.setPortafirmesFluxTipus(MetaDocumentFirmaFluxTipusEnumDto.SIMPLE);
		metaDocument.setPortafirmesSequenciaTipus(MetaDocumentFirmaSequenciaTipusEnumDto.SERIE);
		metaDocument.setPortafirmesCustodiaTipus("1234");
		metaDocument.setFirmaPassarelaCustodiaTipus("1234");
		metaDocument.setMultiplicitat(MultiplicitatEnumDto.M_1);
		metaExpedient = new MetaExpedientDto();
		metaExpedient.setCodi("TEST1");
		metaExpedient.setNom("Metadocument de test");
		metaExpedient.setDescripcio("Descripció de test");
		metaExpedient.setSerieDocumental("S0001");
		metaExpedient.setClassificacioSia("00000");
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
		expedientCreate.setNom("Expedient de test (" + System.currentTimeMillis() + ")");
		expedientUpdate = new ExpedientDto();
		expedientUpdate.setAny(Calendar.getInstance().get(Calendar.YEAR));
		expedientUpdate.setNom("Expedient de test2");
		/*permisUserRead = new PermisDto();
		permisUserRead.setRead(true);
		permisUserRead.setPrincipalTipus(PrincipalTipusEnumDto.USUARI);
		permisUserRead.setPrincipalNom("user");*/
		//usuariRepository.findAll();
	}

	protected void testAmbElementsIExpedient(
			final TestAmbElementsCreats testAmbExpedientCreat) {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						autenticarUsuari("user");
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						MetaExpedientDto metaExpedientCreat = (MetaExpedientDto)elementsCreats.get(1);
						ExpedientDto creat = expedientService.create(
								entitatCreada.getId(),
								metaExpedientCreat.getId(),
								null,
								null,
								expedientCreate.getAny(),
								null,
								expedientCreate.getNom(),
								null,
								false);
						try {
							elementsCreats.add(creat);
							testAmbExpedientCreat.executar(elementsCreats);
						} finally {
							for (Object element: elementsCreats) {
								if (element instanceof ExpedientDto) {
									autenticarUsuari("admin");
									contingutService.deleteDefinitiu(
											entitatCreada.getId(),
											((ExpedientDto)element).getId());
								}
							}
							elementsCreats.remove(creat);
						}
					}
				},
				entitat,
				metaExpedient,
				metaDocument,
				metaDada);
	}

	class TestAmbElementsIExpedient extends TestAmbElementsCreats {
		@Override
		public void executar(List<Object> elementsCreats) {
		}
	}

}
