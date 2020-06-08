/**
 * 
 */
package es.caib.ripea.core.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.plugins.arxiu.api.IArxiuPlugin;
import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;
import es.caib.ripea.core.api.dto.NtiOrigenEnumDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.dto.PrincipalTipusEnumDto;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.api.service.MetaDadaService;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.portafirmes.PortafirmesDocument;
import es.caib.ripea.plugin.portafirmes.PortafirmesPlugin;
import es.caib.ripea.plugin.portafirmes.PortafirmesPrioritatEnum;
import es.caib.ripea.plugin.usuari.DadesUsuariPlugin;

/**
 * Classe que es pot utilitzar com a base dels tests que requereixen la creació
 * prèvia d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseExpedientServiceTest extends BaseServiceTest {

	@Autowired
	protected MetaDadaService metaDadaService;
	@Autowired
	protected ContingutService contingutService;
	@Autowired
	protected ExpedientService expedientService;
	@Autowired
	protected PluginHelper pluginHelper;

	private EntitatDto entitat;
	private MetaDadaDto metaDada;
	private MetaDocumentDto metaDocument;
	private MetaExpedientDto metaExpedient;

	protected ExpedientDto expedientCreate;
	protected ExpedientDto expedientUpdate;
	//private PermisDto permisUserRead;

	protected IArxiuPlugin arxiuPluginMock;
	protected PortafirmesPlugin portafirmesPluginMock;
	protected DadesUsuariPlugin dadesUsuariPluginMock;

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
		metaDocument.setFirmaPortafirmesActiva(true);
		metaDocument.setPortafirmesDocumentTipus("1234");
		metaDocument.setPortafirmesFluxId("1234");
		metaDocument.setPortafirmesResponsables(new String[] {"123456789Z"});
		metaDocument.setPortafirmesFluxTipus(MetaDocumentFirmaFluxTipusEnumDto.SIMPLE);
		metaDocument.setPortafirmesSequenciaTipus(MetaDocumentFirmaSequenciaTipusEnumDto.SERIE);
		metaDocument.setPortafirmesCustodiaTipus("1234");
		metaDocument.setFirmaPassarelaCustodiaTipus("1234");
		metaDocument.setMultiplicitat(MultiplicitatEnumDto.M_1);
		metaDocument.setNtiOrigen(NtiOrigenEnumDto.O0);
		metaDocument.setNtiTipoDocumental("TD99");
		metaDocument.setNtiEstadoElaboracion(DocumentNtiEstadoElaboracionEnumDto.EE01);
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
	}

	protected void testAmbElementsIExpedient(
			final TestAmbElementsCreats testAmbExpedientCreat,
			String descripcioTest) {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) throws Exception {
						configureMockArxiuPlugin();
						configureMockPortafirmesPlugin();
						configureMockDadesUsuariPlugin();
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
				descripcioTest,
				entitat,
				metaExpedient,
				metaDocument,
				metaDada);
	}

	protected void testAmbElementsIExpedient(
			final TestAmbElementsCreats testAmbExpedientCreat) {
		testAmbElementsIExpedient(testAmbExpedientCreat, null);
	}

	private void configureMockArxiuPlugin() throws IOException {
		arxiuPluginMock = Mockito.mock(IArxiuPlugin.class);
		Expedient expedientArxiu = new Expedient();
		expedientArxiu.setIdentificador(UUID.randomUUID().toString());
		expedientArxiu.setNom("nom");
		expedientArxiu.setVersio("1");
		Document documentArxiu = new Document();
		documentArxiu.setIdentificador(UUID.randomUUID().toString());
		documentArxiu.setNom("nom");
		documentArxiu.setVersio("1");
		Document documentArxiuAmbContingut = new Document();
		documentArxiuAmbContingut.setIdentificador(UUID.randomUUID().toString());
		documentArxiuAmbContingut.setNom("nom");
		documentArxiuAmbContingut.setVersio("1");
		DocumentContingut documentContingut = new DocumentContingut();
		documentContingut.setArxiuNom("arxiu.pdf");
		documentContingut.setTipusMime("application/pdf");
		FitxerDto fitxer = getFitxerPdfDeTest();
		documentContingut.setArxiuNom(fitxer.getNom());
		documentContingut.setTipusMime(fitxer.getContentType());
		documentContingut.setContingut(fitxer.getContingut());
		documentContingut.setTamany(fitxer.getTamany());
		documentArxiuAmbContingut.setContingut(documentContingut);
		Mockito.when(arxiuPluginMock.expedientCrear(Mockito.any(Expedient.class))).thenReturn(expedientArxiu);
		Mockito.when(arxiuPluginMock.expedientCrear(null)).thenThrow(NullPointerException.class);
		Mockito.when(arxiuPluginMock.expedientDetalls(Mockito.anyString(), Mockito.nullable(String.class))).thenReturn(expedientArxiu);
		Mockito.when(arxiuPluginMock.documentCrear(Mockito.any(Document.class), Mockito.anyString())).thenReturn(documentArxiu);
		Mockito.when(arxiuPluginMock.documentCrear(null, null)).thenThrow(NullPointerException.class);
		Mockito.when(arxiuPluginMock.documentDetalls(Mockito.anyString(), Mockito.nullable(String.class), Mockito.eq(true))).thenReturn(documentArxiuAmbContingut);
		pluginHelper.setArxiuPlugin(arxiuPluginMock);
	}

	@SuppressWarnings("unchecked")
	private void configureMockPortafirmesPlugin() throws IOException, SistemaExternException {
		portafirmesPluginMock = Mockito.mock(PortafirmesPlugin.class);
		Mockito.when(portafirmesPluginMock.upload(
				Mockito.any(PortafirmesDocument.class),
				Mockito.anyString(),
				Mockito.anyString(),
				Mockito.anyString(),
				Mockito.any(PortafirmesPrioritatEnum.class),
				Mockito.any(Date.class),
				Mockito.nullable(List.class),
				Mockito.nullable(String.class),
				Mockito.nullable(List.class),
				Mockito.anyBoolean(),
				Mockito.nullable(String.class))).thenReturn(Integer.toString(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)));
		PortafirmesDocument portafirmesDocument = new PortafirmesDocument();
		FitxerDto pdfFirmat = getFitxerPdfFirmatDeTest();
		portafirmesDocument.setTitol("Titol doc. portafirmes");
		portafirmesDocument.setDescripcio("Descripció doc. portafirmes");
		portafirmesDocument.setFirmat(true);
		portafirmesDocument.setArxiuNom(pdfFirmat.getNom());
		portafirmesDocument.setArxiuContingut(pdfFirmat.getContingut());
		Mockito.when(portafirmesPluginMock.download(Mockito.anyString())).thenReturn(portafirmesDocument);
		pluginHelper.setPortafirmesPlugin(portafirmesPluginMock);
	}

	private void configureMockDadesUsuariPlugin() throws IOException, SistemaExternException {
		dadesUsuariPluginMock = Mockito.mock(DadesUsuariPlugin.class);
		//Mockito.when(portafirmesMock.download(Mockito.anyString())).thenReturn(portafirmesDocument);
		pluginHelper.setDadesUsuariPlugin(dadesUsuariPluginMock);
	}

	protected FitxerDto getFitxerPdfDeTest() throws IOException {
		FitxerDto dto = new FitxerDto();
		dto.setNom("arxiu.pdf");
		dto.setContentType("application/pdf");
		dto.setContingut(
				IOUtils.toByteArray(getClass().getResourceAsStream("/es/caib/ripea/core/arxiu.pdf")));
		dto.setTamany(dto.getContingut().length);
		return dto;
	}

	protected FitxerDto getFitxerPdfFirmatDeTest() throws IOException {
		FitxerDto dto = new FitxerDto();
		dto.setNom("firma.pdf");
		dto.setContentType("application/pdf");
		dto.setContingut(
				IOUtils.toByteArray(getClass().getResourceAsStream("/es/caib/ripea/core/firma.pdf")));
		dto.setTamany(dto.getContingut().length);
		return dto;
	}

}