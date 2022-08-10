package es.caib.ripea.core.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.OrganEstatEnumDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.repository.OrganGestorRepository;
import es.caib.ripea.core.test.AuthenticationTest;
import es.caib.ripea.plugin.unitat.UnitatOrganitzativa;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/ripea/core/application-context-sync-test.xml"})
@Transactional
public class OrganGestorSyncHelperIT {

    @Autowired
    @InjectMocks
    private OrganGestorService organGestorService;

    @Autowired
    private OrganGestorRepository organGestorRepository;
    @Autowired
    private PermisosHelper permisosHelper;
    @Autowired
    private CacheHelper cacheHelper;

    @Autowired
    protected AuthenticationTest authenticationTest;

    @Mock
    private PluginHelper pluginHelper;
    @Mock
    private MetaExpedientHelper metaExpedientHelper;

    private static final String UNITATS_JSON = "[" +
            "{\"codigo\": \"A002\", \"denominacion\": \"B002\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A003\", \"denominacion\": \"A003\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A101\", \"A102\"]}," +
            "{\"codigo\": \"A004\", \"denominacion\": \"A004\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A103\"]}," +
            "{\"codigo\": \"A005\", \"denominacion\": \"A005\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A103\"]}," +
            "{\"codigo\": \"A006\", \"denominacion\": \"A006\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A104\"]}," +
            "{\"codigo\": \"A007\", \"denominacion\": \"A007\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A008\", \"denominacion\": \"A008\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A106\", \"A107\", \"A108\"]}," +
            "{\"codigo\": \"A009\", \"denominacion\": \"A009\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A111\"]}," +
            "{\"codigo\": \"A010\", \"denominacion\": \"A010\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A111\"]}," +
            "{\"codigo\": \"A011\", \"denominacion\": \"A011\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A112\", \"A113\"]}," +
            "{\"codigo\": \"A012\", \"denominacion\": \"A012\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A117\", \"A118\"]}," +
            "{\"codigo\": \"A013\", \"denominacion\": \"A013\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [{\"codigo\": \"A014\", \"denominacion\": \"A014\", \"descripcionEstado\":\"V\", \"superior\": \"A013\", \"hijos\": [], \"historicosUO\": []}], \"historicosUO\": [\"A119\"]}," +
            "{\"codigo\": \"A014\", \"denominacion\": \"A014\", \"descripcionEstado\":\"V\", \"superior\": \"A119\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A015\", \"denominacion\": \"A015\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [{\"codigo\": \"A016\", \"denominacion\": \"A016\", \"descripcionEstado\":\"V\", \"superior\": \"A015\", \"hijos\": [], \"historicosUO\": []}], \"historicosUO\": [\"A120\"]}," +
            "{\"codigo\": \"A016\", \"denominacion\": \"A016\", \"descripcionEstado\":\"V\", \"superior\": \"A121\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A101\", \"denominacion\": \"A101\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A102\", \"denominacion\": \"A102\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A103\", \"denominacion\": \"A103\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A104\", \"denominacion\": \"A104\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A105\", \"denominacion\": \"A105\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A106\", \"denominacion\": \"A106\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A109\"]}," +
            "{\"codigo\": \"A107\", \"denominacion\": \"A107\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A110\"]}," +
            "{\"codigo\": \"A108\", \"denominacion\": \"A108\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A110\"]}," +
            "{\"codigo\": \"A109\", \"denominacion\": \"A109\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A110\", \"denominacion\": \"A110\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A111\", \"denominacion\": \"A111\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A114\"]}," +
            "{\"codigo\": \"A112\", \"denominacion\": \"A112\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A114\"]}," +
            "{\"codigo\": \"A113\", \"denominacion\": \"A113\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A115\", \"A116\"]}," +
            "{\"codigo\": \"A114\", \"denominacion\": \"A114\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A115\", \"denominacion\": \"A115\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A116\", \"denominacion\": \"A116\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A117\", \"denominacion\": \"A117\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A118\", \"denominacion\": \"A118\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A119\", \"denominacion\": \"A119\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [{\"codigo\": \"A014\", \"denominacion\": \"A014\", \"descripcionEstado\":\"V\", \"superior\": \"A119\", \"hijos\": [], \"historicosUO\": []}], \"historicosUO\": []}," +
            "{\"codigo\": \"A120\", \"denominacion\": \"A120\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [{\"codigo\": \"A016\", \"denominacion\": \"A016\", \"descripcionEstado\":\"V\", \"superior\": \"A120\", \"hijos\": [], \"historicosUO\": []}], \"historicosUO\": [\"A121\"]}," +
            "{\"codigo\": \"A121\", \"denominacion\": \"A121\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [{\"codigo\": \"A014\", \"denominacion\": \"A016\", \"descripcionEstado\":\"V\", \"superior\": \"A121\", \"hijos\": [], \"historicosUO\": []}], \"historicosUO\": []}" +
            "]";


    @BeforeClass
    public static void beforeClass() {
        ConfigHelper.JBossPropertiesHelper.getProperties("classpath:es/caib/ripea/core/test.properties");
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        organGestorService.setServicesForSynctest(metaExpedientHelper, pluginHelper);
        cacheHelper.setPluginHelper(pluginHelper);
        ObjectMapper mapper = new ObjectMapper();

        Mockito.when(pluginHelper.unitatsOrganitzativesFindByPare(Mockito.anyString(), Mockito.nullable(Date.class), Mockito.nullable(Date.class))).thenReturn((List<UnitatOrganitzativa>) mapper.readValue(UNITATS_JSON, new TypeReference<List<UnitatOrganitzativa>>(){}));
        Mockito.doNothing().when(metaExpedientHelper).actualitzarProcediments(Mockito.any(EntitatDto.class), Mockito.any(Locale.class));
        Mockito.when(pluginHelper.dadesUsuariFindAmbCodi(Mockito.anyString())).thenReturn(null);

    }

    @After
    public final void tearDown() {

    }


    @Test
    public void whenOrganGestorSync() throws Exception {
        authenticationTest.autenticarUsuari("admin");
        EntitatDto entitatDto = new EntitatDto();
        entitatDto.setId(1L);
        entitatDto.setCodi("ENTITAT_TESTS");
        List<OrganGestorEntity>[] result = (List<OrganGestorEntity>[])organGestorService.syncDir3OrgansGestors(entitatDto, new Locale("ca"));
        Assert.assertNotNull(result);
        Assert.assertEquals(4, result.length);

//        EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(1L);
        List<OrganGestorEntity> organsVigents = organGestorRepository.findByEntitatIdAndEstat(1L, OrganEstatEnumDto.V);
        List<OrganGestorEntity> obsoleteUnitats = result[0];
        List<OrganGestorEntity> organsDividits = result[1];
        List<OrganGestorEntity> organsFusionats = result[2];
        List<OrganGestorEntity> organsSubstituits = result[3];

        // ÒRGANS
        // Vigents
        Assert.assertEquals(18, organsVigents.size());
        Assert.assertTrue(conteOrgan(organsVigents, "A000"));
        Assert.assertTrue(conteOrgan(organsVigents, "A001"));
        Assert.assertTrue(conteOrgan(organsVigents, "A002"));
        Assert.assertTrue(conteOrgan(organsVigents, "A101"));
        Assert.assertTrue(conteOrgan(organsVigents, "A102"));
        Assert.assertTrue(conteOrgan(organsVigents, "A103"));
        Assert.assertTrue(conteOrgan(organsVigents, "A104"));
        Assert.assertTrue(conteOrgan(organsVigents, "A105"));
        Assert.assertTrue(conteOrgan(organsVigents, "A109"));
        Assert.assertTrue(conteOrgan(organsVigents, "A110"));
        Assert.assertTrue(conteOrgan(organsVigents, "A114"));
        Assert.assertTrue(conteOrgan(organsVigents, "A115"));
        Assert.assertTrue(conteOrgan(organsVigents, "A116"));
        Assert.assertTrue(conteOrgan(organsVigents, "A118"));
        Assert.assertTrue(conteOrgan(organsVigents, "A119"));
        Assert.assertTrue(conteOrgan(organsVigents, "A014"));
        Assert.assertTrue(conteOrgan(organsVigents, "A121"));
        Assert.assertTrue(conteOrgan(organsVigents, "A016"));

        // Obsolets
        Assert.assertEquals(20, obsoleteUnitats.size());

        // Dividits
        Assert.assertEquals(5, organsDividits.size());
        Assert.assertTrue(conteOrgan(organsDividits, "A003"));
        Assert.assertTrue(conteOrgan(organsDividits, "A008"));
        Assert.assertTrue(conteOrgan(organsDividits, "A011"));
        Assert.assertTrue(conteOrgan(organsDividits, "A113"));
        Assert.assertTrue(conteOrgan(organsDividits, "A012"));

        // Fusionats
        Assert.assertEquals(8, organsFusionats.size());
        Assert.assertTrue(conteOrgan(organsFusionats, "A004"));
        Assert.assertTrue(conteOrgan(organsFusionats, "A005"));
        Assert.assertTrue(conteOrgan(organsFusionats, "A107"));
        Assert.assertTrue(conteOrgan(organsFusionats, "A108"));
        Assert.assertTrue(conteOrgan(organsFusionats, "A009"));
        Assert.assertTrue(conteOrgan(organsFusionats, "A010"));
        Assert.assertTrue(conteOrgan(organsFusionats, "A111"));
        Assert.assertTrue(conteOrgan(organsFusionats, "A112"));

        // Substituits
        Assert.assertEquals(5, organsSubstituits.size());
        Assert.assertTrue(conteOrgan(organsSubstituits, "A006"));
        Assert.assertTrue(conteOrgan(organsSubstituits, "A106"));
        Assert.assertTrue(conteOrgan(organsSubstituits, "A013"));
        Assert.assertTrue(conteOrgan(organsSubstituits, "A015"));
        Assert.assertTrue(conteOrgan(organsSubstituits, "A120"));


        // Permisos
        for (OrganGestorEntity organ: organsVigents) {
            List<PermisDto> permisos = permisosHelper.findPermisos(organ.getId(), OrganGestorEntity.class);
            PermisDto permisAdmin = null;
            PermisDto permisRole = null;
            for (PermisDto permis: permisos) {
                if ("admin".equals(permis.getPrincipalNom())) {
                    permisAdmin = permis;
                } else if ("ROLE".equals(permis.getPrincipalNom())) {
                    permisRole = permis;
                }
            }
            switch (organ.getCodi()) {
                case "A000":
                    Assert.assertEquals(0, permisos.size());
                    break;
                case "A001":
                    Assert.assertEquals(1, permisos.size());
                    Assert.assertNotNull(permisAdmin);
                    Assert.assertTrue(permisAdmin.isRead());
                    break;
                case "A002":
                    Assert.assertEquals(2, permisos.size());
                    Assert.assertNotNull(permisAdmin);
                    Assert.assertNotNull(permisRole);
                    Assert.assertTrue(permisAdmin.isRead());
                    Assert.assertTrue(permisAdmin.isAdministration());
                    Assert.assertTrue(permisRole.isAdministrationComuns());
                    break;
                case "A101":
                case "A102":
                    Assert.assertEquals(1, permisos.size());
                    Assert.assertNotNull(permisRole);
                    Assert.assertTrue(permisRole.isRead());
                    Assert.assertTrue(permisRole.isWrite());
                    break;
                case "A103":
                    Assert.assertEquals(2, permisos.size());
                    Assert.assertNotNull(permisAdmin);
                    Assert.assertNotNull(permisRole);
                    Assert.assertTrue(permisAdmin.isAdministrationComuns());
                    Assert.assertTrue(permisAdmin.isCreate());
                    Assert.assertTrue(permisAdmin.isProcedimentsComuns());
                    Assert.assertTrue(permisAdmin.isStatistics());
                    Assert.assertTrue(permisRole.isRead());
                    Assert.assertTrue(permisRole.isAdministrationComuns());
                    Assert.assertTrue(permisRole.isProcedimentsComuns());
                    break;
                case "A104":
                    Assert.assertEquals(1, permisos.size());
                    Assert.assertNotNull(permisRole);
                    Assert.assertTrue(permisRole.isAdministration());
                    Assert.assertTrue(permisRole.isWrite());
                    break;
                case "A105":
                    Assert.assertEquals(0, permisos.size());
                    break;
                case "A109":
                case "A110":
                    Assert.assertEquals(2, permisos.size());
                    Assert.assertNotNull(permisAdmin);
                    Assert.assertNotNull(permisRole);
                    Assert.assertTrue(permisAdmin.isRead());
                    Assert.assertTrue(permisAdmin.isAdministration());
                    Assert.assertTrue(permisAdmin.isAdministrationComuns());
                    Assert.assertTrue(permisRole.isAdministrationComuns());
                    Assert.assertTrue(permisRole.isWrite());
                    break;
                case "A114":
                    Assert.assertEquals(2, permisos.size());
                    Assert.assertNotNull(permisAdmin);
                    Assert.assertNotNull(permisRole);
                    Assert.assertTrue(permisAdmin.isRead());
                    Assert.assertTrue(permisAdmin.isAdministration());
                    Assert.assertTrue(permisAdmin.isWrite());
                    Assert.assertTrue(permisAdmin.isCreate());
                    Assert.assertTrue(permisRole.isAdministrationComuns());
                    Assert.assertTrue(permisRole.isWrite());
                    Assert.assertTrue(permisRole.isProcedimentsComuns());
                    Assert.assertTrue(permisRole.isStatistics());
                    break;
                case "A115":
                case "A116":
                    Assert.assertEquals(2, permisos.size());
                    Assert.assertNotNull(permisAdmin);
                    Assert.assertNotNull(permisRole);
                    Assert.assertTrue(permisAdmin.isAdministration());
                    Assert.assertTrue(permisAdmin.isWrite());
                    Assert.assertTrue(permisRole.isAdministrationComuns());
                    break;
                case "A118":
                    Assert.assertEquals(1, permisos.size());
                    Assert.assertNotNull(permisAdmin);
                    Assert.assertTrue(permisAdmin.isAdministrationComuns());
                    Assert.assertTrue(permisAdmin.isWrite());
                    break;
                case "A119":
                    Assert.assertEquals(1, permisos.size());
                    Assert.assertNotNull(permisRole);
                    Assert.assertTrue(permisRole.isRead());
                    Assert.assertTrue(permisRole.isCreate());
                    break;
                case "A014":
                    Assert.assertEquals(1, permisos.size());
                    Assert.assertNotNull(permisRole);
                    Assert.assertTrue(permisRole.isRead());
                    break;
                case "A121":
                    Assert.assertEquals(1, permisos.size());
                    Assert.assertNotNull(permisAdmin);
                    Assert.assertTrue(permisAdmin.isAdministration());
                    Assert.assertTrue(permisAdmin.isAdministrationComuns());
                    break;
                case "A016":
                    Assert.assertEquals(1, permisos.size());
                    Assert.assertNotNull(permisAdmin);
                    Assert.assertTrue(permisAdmin.isAdministration());
                    break;
                default:
                    Assert.fail("L'òrgan " + organ.getCodi() + " no hauria d'estar vigent!");
            }
        }

    }

    private boolean conteOrgan(List<OrganGestorEntity> llista, String codi) {
        for (OrganGestorEntity organ: llista) {
            if (organ.getCodi().equals(codi))
                return true;
        }
        return false;
    }
}
