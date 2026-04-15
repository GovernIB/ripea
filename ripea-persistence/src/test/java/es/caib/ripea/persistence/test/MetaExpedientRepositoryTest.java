package es.caib.ripea.persistence.test;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import es.caib.ripea.persistence.entity.MetaDocumentEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.repository.MetaDocumentRepository;
import es.caib.ripea.persistence.repository.MetaExpedientRepository;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'integració per a MetaExpedientRepository.
 *
 * Cobreix les operacions bàsiques de CRUD i les consultes més habituals
 * sobre procediments i els seus meta-documents associats.
 */
@WithMockUser(username = "usuari1", roles = {"ADMIN"})
public class MetaExpedientRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private MetaExpedientRepository metaExpedientRepository;

    @Autowired
    private MetaDocumentRepository metaDocumentRepository;

    @Test
    void donatElConjuntDeDadesBase_quanRecuperemTotsElsProcediments_nEnTornaExactamentTres() {
        List<MetaExpedientEntity> procediments = metaExpedientRepository.findAll();
        assertThat(procediments).hasSize(3);
    }

    @Test
    void donatUnProcedimentExistent_quanBusquemPerId_elTrobem() {
        MetaExpedientEntity primer = testData.metaExpedients.get(0);

        Optional<MetaExpedientEntity> resultat = metaExpedientRepository.findById(primer.getId());

        assertThat(resultat).isPresent();
        assertThat(resultat.get().getCodiPropi()).isEqualTo("PROC_01");
    }

    @Test
    void donatUnProcedimentExistent_quanBusquemPerEntitatICodi_elTrobem() {
        MetaExpedientEntity trobat = metaExpedientRepository.findByEntitatAndCodi(
                testData.entitat, "PROC_02"
        );

        assertThat(trobat).isNotNull();
        assertThat(trobat.getNom()).isEqualTo("Procediment 2");
    }

    @Test
    void donatElConjuntDeDadesBase_quanRecuperemTotsElsMetaDocuments_nEnTornaNouEnTotal() {
        // 3 procediments x 3 meta-documents = 9 meta-documents
        List<MetaDocumentEntity> metaDocuments = metaDocumentRepository.findAll();
        assertThat(metaDocuments).hasSize(9);
    }

    @Test
    void donatUnProcediment_quanComprovemLaSevaEntitat_corresponALEntitatDeTest() {
        MetaExpedientEntity proc = testData.metaExpedients.get(0);

        // Refresquem des de BBDD per verificar la relació
        MetaExpedientEntity fromDb = metaExpedientRepository.findById(proc.getId()).get();

        assertThat(fromDb.getEntitatPropia().getCodi()).isEqualTo("ENT_TEST");
    }

    @Test
    void donatUnProcedimentExistent_quanElBorrem_jaNoEsTroba() {
        MetaExpedientEntity aEsborrar = testData.metaExpedients.get(2);
        Long id = aEsborrar.getId();

        metaExpedientRepository.deleteById(id);
        entityManager.flush();

        assertThat(metaExpedientRepository.findById(id)).isEmpty();
    }
}
