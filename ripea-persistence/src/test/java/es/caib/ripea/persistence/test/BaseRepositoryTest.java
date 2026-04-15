package es.caib.ripea.persistence.test;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

/**
 * Classe base per a tots els tests d'integració del mòdul de persistència.
 *
 * Cada test s'executa dins d'una transacció que es reverteix al final (@Rollback),
 * garantint l'aïllament complet entre tests.
 *
 * @BeforeEach crea el conjunt bàsic de dades de test (via TestDataFactory) i
 * neteja la caché de primer nivell de Hibernate per assegurar que les consultes
 * posteriors accedeixen realment a la BBDD.
 *
 * @WithMockUser simula l'usuari autenticat necessari per a l'auditoria JPA
 * (AuditingConfig llegeix el nom d'usuari del SecurityContextHolder).
 */
@SpringBootTest(classes = RipeaTestApplication.class)
@Transactional
@Rollback
@WithMockUser(username = "usuari1", roles = {"ADMIN"})
public abstract class BaseRepositoryTest {

    @PersistenceContext(unitName = "ripeaPU")
    protected EntityManager entityManager;

    @Autowired
    protected TestDataFactory testDataFactory;

    protected TestDataFactory.TestData testData;

    @BeforeEach
    void setUpTestData() {
        testData = testDataFactory.createBaseTestData();
        // Buidem la caché de primer nivell perquè les consultes dels tests
        // llegeixin de la BBDD i no de la caché en memòria.
        entityManager.flush();
        entityManager.clear();
    }
}
