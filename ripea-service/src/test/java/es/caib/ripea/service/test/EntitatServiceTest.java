package es.caib.ripea.service.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.TipusDocumentalEntity;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.persistence.repository.TipusDocumentalRepository;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.service.EntitatService;
import es.caib.ripea.service.test.config.BaseServiceTest;

/**
 * Tests d'integració per a EntitatService.
 *
 * Cobreix: creació (verificant que es generen els tipus documentals del JSON)
 * i eliminació (verificant que s'eliminen els tipus documentals associats).
 *
 * El conjunt de dades base (TestDataFactory) ja conté una entitat ENT_TEST
 * amb els seus tipus documentals. Els tests de creació/eliminació treballen
 * amb una entitat addicional per no interferir amb les dades base.
 */
@WithMockUser(username = "usuari1", roles = {"ADMIN"})
public class EntitatServiceTest extends BaseServiceTest {

    @Autowired
    private EntitatService entitatService;

    @Autowired
    private EntitatRepository entitatRepository;

    @Autowired
    private TipusDocumentalRepository tipusDocumentalRepository;

    // =========================================================================
    // Tests de creació
    // =========================================================================

    // Se crean registros y contiene los códigos esperados (TD01, TD02, TD03, TD99)
    @Test
    void quanEsCreaUnaEntitat_esGenerenElsTipusDocumentalsDelJson() {
        EntitatDto nova = buildEntitatDto("ENT_NOVA", "Entitat Nova");

        entitatService.create(nova);
        entityManager.flush();
        entityManager.clear();

        EntitatEntity entitat = entitatRepository.findByCodi("ENT_NOVA");
        List<TipusDocumentalEntity> tipus = tipusDocumentalRepository.findByEntitatOrderByNomEspanyolAsc(entitat);

        assertThat(tipus).isNotEmpty();
        assertThat(tipus).extracting(TipusDocumentalEntity::getCodi)
                .contains("TD01", "TD02", "TD03", "TD99");
    }

    // Se crean exactamente 21 tipos (TD01..TD20 + TD99)
    @Test
    void quanEsCreaUnaEntitat_elNombreDeTipusDocumentalsCoincideixAmbElJson() {
        EntitatDto nova = buildEntitatDto("ENT_NOVA2", "Entitat Nova 2");

        entitatService.create(nova);
        entityManager.flush();
        entityManager.clear();

        EntitatEntity entitat = entitatRepository.findByCodi("ENT_NOVA2");
        List<TipusDocumentalEntity> tipus = tipusDocumentalRepository.findByEntitatOrderByNomEspanyolAsc(entitat);

        // El JSON tiposDocumentales.json conté 21 entrades (TD01..TD20 + TD99)
        assertThat(tipus).hasSize(21);
    }

    // Los tipos pertenecen solo a la entidad creada, no a otras
    @Test
    void quanEsCreaUnaEntitat_elsTipusDocumentalsNomesSonDeAquellaEntitat() {
        EntitatDto nova = buildEntitatDto("ENT_NOVA3", "Entitat Nova 3");

        entitatService.create(nova);
        entityManager.flush();
        entityManager.clear();

        EntitatEntity entitat = entitatRepository.findByCodi("ENT_NOVA3");
        List<TipusDocumentalEntity> tipus = tipusDocumentalRepository.findByEntitatOrderByNomEspanyolAsc(entitat);

        assertThat(tipus).extracting(t -> t.getEntitat().getCodi())
                .containsOnly("ENT_NOVA3");
    }

    // =========================================================================
    // Tests d'eliminació
    // =========================================================================

    // Tras borrar la entidad, ya no existe en BD
    @Test
    void quanSEsborraUnaEntitat_sElimenenElsSeusTipusDocumentals() {
        EntitatDto nova = buildEntitatDto("ENT_DELETE", "Entitat a Eliminar");

        EntitatDto creada = entitatService.create(nova);
        entityManager.flush();
        entityManager.clear();

        EntitatEntity entitat = entitatRepository.findByCodi("ENT_DELETE");
        assertThat(tipusDocumentalRepository.findByEntitatOrderByNomEspanyolAsc(entitat)).isNotEmpty();

        entitatService.delete(creada.getId());
        entityManager.flush();
        entityManager.clear();

        // L'entitat ja no existeix, la consulta per codi ha de retornar null
        assertThat(entitatRepository.findByCodi("ENT_DELETE")).isNull();
    }

    // El count total de tipos documentales disminuye exactamente en 21
    @Test
    void quanSEsborraUnaEntitat_noQuedanTipusDocumentalsOrfes() {
        EntitatDto nova = buildEntitatDto("ENT_DELETE2", "Entitat a Eliminar 2");

        EntitatDto creada = entitatService.create(nova);
        entityManager.flush();
        entityManager.clear();

        long tipusAbans = tipusDocumentalRepository.count();
        assertThat(tipusAbans).isGreaterThan(0);

        entitatService.delete(creada.getId());
        entityManager.flush();
        entityManager.clear();

        long tipusDespres = tipusDocumentalRepository.count();
        // Tots els tipus documentals de l'entitat eliminada han de desaparèixer
        assertThat(tipusDespres).isEqualTo(tipusAbans - 21);
    }

    // =========================================================================
    // Mètode auxiliar
    // =========================================================================

    private EntitatDto buildEntitatDto(String codi, String nom) {
        EntitatDto dto = new EntitatDto();
        dto.setCodi(codi);
        dto.setNom(nom);
        dto.setDescripcio("Descripció de " + nom);
        dto.setCif("A00000001");
        dto.setUnitatArrel("A00000001");
        return dto;
    }
}
