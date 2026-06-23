package es.caib.ripea.service.test.config;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Validator;

import org.springframework.validation.SmartValidator;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.service.config.SchedulingConfig;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.PermisosHelper;
import es.caib.ripea.service.helper.PluginHelper;

/**
 * Classe base per als tests d'integració del mòdul de serveis.
 *
 * Arrenca el context Spring complet (excepte les configuracions problemàtiques
 * excloses a RipeaServiceTestApplication) contra una BBDD H2 in-memory.
 *
 * Cada test s'executa dins d'una transacció que es fa rollback al final,
 * deixant la BBDD neta per al test següent.
 *
 * Mocks proporcionats:
 *  - PermisosHelper: totes les comprovacions de permís retornen true
 *  - PluginHelper: totes les crides a plugins retornen null (sense efectes)
 *  - JavaMailSender: les crides a enviament de correu no fan res
 */
@SpringBootTest(classes = RipeaServiceTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@Rollback
@WithMockUser(username = "usuari1", roles = {"ADMIN"})
public abstract class BaseServiceIT {

    @PersistenceContext(unitName = "ripeaPU")
    protected EntityManager entityManager;

    @Autowired
    protected TestDataFactory testDataFactory;

    protected TestDataFactory.TestData testData;

    @MockBean
    protected PermisosHelper permisosHelper;

    @MockBean
    protected PluginHelper pluginHelper;

    @MockBean
    protected JavaMailSender javaMailSender;

    @MockBean
    protected Validator validator;

    @MockBean
    protected SchedulingConfig schedulingConfig;

    @MockBean
    protected SmartValidator smartValidator;

    @MockBean
    protected ConfigHelper configHelper;

    @BeforeEach
    public void setUpTestData() {
        // Permet totes les comprovacions de permisos sense necessitat de dades ACL reals
        Mockito.lenient()
                .when(permisosHelper.isGrantedAny(
                        Mockito.any(), Mockito.any(), Mockito.any(), (org.springframework.security.core.Authentication) Mockito.any()))
                .thenReturn(true);
        Mockito.lenient()
                .when(permisosHelper.isGrantedAny(
                        Mockito.any(), Mockito.any(), Mockito.any(), (String) Mockito.any()))
                .thenReturn(true);
        Mockito.lenient()
                .when(permisosHelper.isGrantedAll(
                        Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(true);
        Mockito.lenient()
                .when(permisosHelper.isGrantedAll(
                        Mockito.any(), Mockito.any(), Mockito.any(), (org.springframework.security.core.Authentication) Mockito.any()))
                .thenReturn(true);
        Mockito.lenient()
                .when(permisosHelper.isGrantedAll(
                        Mockito.any(), Mockito.any(), Mockito.any(), (String) Mockito.any()))
                .thenReturn(true);

        testData = testDataFactory.createBaseTestData();
        entityManager.flush();
        entityManager.clear();
    }
}
