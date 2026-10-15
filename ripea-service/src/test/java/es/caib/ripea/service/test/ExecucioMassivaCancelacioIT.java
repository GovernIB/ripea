package es.caib.ripea.service.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExecucioMassivaContingutEntity;
import es.caib.ripea.persistence.entity.ExecucioMassivaEntity;
import es.caib.ripea.persistence.repository.ExecucioMassivaContingutRepository;
import es.caib.ripea.persistence.repository.ExecucioMassivaRepository;
import es.caib.ripea.service.helper.ExecucioMassivaCuaHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.ElementTipusEnumDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaEstatDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaProcesEstatDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaTipusDto;
import es.caib.ripea.service.intf.exception.PermissionDeniedException;
import es.caib.ripea.service.intf.exception.ValidationException;
import es.caib.ripea.service.test.config.BaseServiceIT;

/**
 * Tests d'integració de la cua d'execucions massives: reclamació atòmica per node i cancel·lació.
 *
 * Els mètodes REQUIRES_NEW del helper no veurien les dades del test (que van dins la transacció amb rollback
 * de BaseServiceIT), així que la reclamació es prova directament contra les consultes del repositori, que són
 * les que garanteixen l'atomicitat. La cancel·lació (REQUIRED) es prova a través del helper.
 */
public class ExecucioMassivaCancelacioIT extends BaseServiceIT {

    private static final String ROL_USUARI = "tothom";
    private static final String ALTRE_USUARI = "altre";

    @Autowired private ExecucioMassivaCuaHelper execucioMassivaCuaHelper;
    @Autowired private ExecucioMassivaRepository execucioMassivaRepository;
    @Autowired private ExecucioMassivaContingutRepository execucioMassivaContingutRepository;

    // =========================================================================
    // Cancel·lació
    // =========================================================================

    @Test
    void quanEsCancelaUnaPendent_esTancaAlActeIElsElementsQuedenCancelats() {
        Long id = crearExecucio(ExecucioMassivaTipusDto.TANCAMENT, null, 2);

        ExecucioMassivaProcesEstatDto resultat = execucioMassivaCuaHelper.cancelar(entitatId(), id, ROL_USUARI);

        assertThat(resultat).isEqualTo(ExecucioMassivaProcesEstatDto.CANCELADA);
        ExecucioMassivaEntity execucio = recarregar(id);
        assertThat(execucio.getEstat()).isEqualTo(ExecucioMassivaProcesEstatDto.CANCELADA);
        assertThat(execucio.getDataFi()).isNotNull();
        assertThat(execucio.getCancelacioUsuari()).isEqualTo("usuari1");
        assertThat(execucio.getContinguts())
                .extracting(ExecucioMassivaContingutEntity::getEstat)
                .containsOnly(ExecucioMassivaEstatDto.ESTAT_CANCELAT);
        assertThat(execucio.getContinguts()).allMatch(c -> c.getDataFi() != null);
    }

    @Test
    void quanEsCancelaUnaImportacioPendent_esborraElZipTemporalIBuidaDocumentNom() {
        Long id = crearExecucio(ExecucioMassivaTipusDto.IMPORTAR_DOCS, null, 1);
        ExecucioMassivaEntity importacio = recarregar(id);
        importacio.setDocumentNom("1792060372555");
        execucioMassivaRepository.save(importacio);
        entityManager.flush();
        entityManager.clear();

        execucioMassivaCuaHelper.cancelar(entitatId(), id, ROL_USUARI);

        //documentNom és el ZIP amb les dades d'entrada, no un resultat: no s'ha de mostrar com a descarregable
        assertThat(recarregar(id).getDocumentNom()).isNull();
        Mockito.verify(pluginHelper).gestioDocumentalDelete("1792060372555", PluginHelper.GESDOC_AGRUPACIO_DOCS_ESBORRANYS);
    }

    @Test
    void quanEsCancelaUnaEnCurs_nomesEsSolicitaIElsElementsNoEsToquen() {
        Long id = crearExecucio(ExecucioMassivaTipusDto.TANCAMENT, null, 2);
        marcarPrimerElementFinalitzat(id);
        reclamar(id, "node1");

        ExecucioMassivaProcesEstatDto resultat = execucioMassivaCuaHelper.cancelar(entitatId(), id, ROL_USUARI);

        assertThat(resultat).isEqualTo(ExecucioMassivaProcesEstatDto.EN_CURS);
        ExecucioMassivaEntity execucio = recarregar(id);
        assertThat(execucio.getEstat()).isEqualTo(ExecucioMassivaProcesEstatDto.EN_CURS);
        assertThat(execucio.getDataFi()).isNull();
        assertThat(execucio.getCancelacioData()).isNotNull();
        //Els elements només els toca el procés que la té en curs, en acabar l'element actual
        assertThat(execucio.getContinguts())
                .extracting(ExecucioMassivaContingutEntity::getEstat)
                .containsExactlyInAnyOrder(ExecucioMassivaEstatDto.ESTAT_FINALITZAT, ExecucioMassivaEstatDto.ESTAT_PENDENT);
    }

    @Test
    void quanEsTornaACancelarUnaEnCurs_esResponIgual() {
        Long id = crearExecucio(ExecucioMassivaTipusDto.TANCAMENT, null, 1);
        reclamar(id, "node1");
        execucioMassivaCuaHelper.cancelar(entitatId(), id, ROL_USUARI);

        ExecucioMassivaProcesEstatDto resultat = execucioMassivaCuaHelper.cancelar(entitatId(), id, ROL_USUARI);

        assertThat(resultat).isEqualTo(ExecucioMassivaProcesEstatDto.EN_CURS);
    }

    @Test
    void quanElTipusNoEsCancelable_llancaValidationException() {
        Long exportacio = crearExecucio(ExecucioMassivaTipusDto.EXPORTAR_ZIP, null, 1);
        Long moure = crearExecucio(ExecucioMassivaTipusDto.MOURE_EXPEDIENT, null, 1);

        assertThatThrownBy(() -> execucioMassivaCuaHelper.cancelar(entitatId(), exportacio, ROL_USUARI))
                .isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> execucioMassivaCuaHelper.cancelar(entitatId(), moure, ROL_USUARI))
                .isInstanceOf(ValidationException.class);
        assertThat(recarregar(exportacio).getEstat()).isEqualTo(ExecucioMassivaProcesEstatDto.PENDENT);
    }

    @Test
    void quanJaHaFinalitzat_llancaValidationException() {
        Long id = crearExecucio(ExecucioMassivaTipusDto.TANCAMENT, new Date(), 1);

        assertThatThrownBy(() -> execucioMassivaCuaHelper.cancelar(entitatId(), id, ROL_USUARI))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void quanNoEsElCreadorNiAdministrador_llancaPermissionDenied() {
        Long id = crearExecucio(ExecucioMassivaTipusDto.TANCAMENT, null, 1);
        canviarCreador(id, ALTRE_USUARI);

        assertThatThrownBy(() -> execucioMassivaCuaHelper.cancelar(entitatId(), id, ROL_USUARI))
                .isInstanceOf(PermissionDeniedException.class);
        //L'administrador d'òrgan tampoc pot cancel·lar les d'altres
        assertThatThrownBy(() -> execucioMassivaCuaHelper.cancelar(entitatId(), id, BaseConfig.ROLE_ORGAN_ADMIN))
                .isInstanceOf(PermissionDeniedException.class);
        assertThat(recarregar(id).getEstat()).isEqualTo(ExecucioMassivaProcesEstatDto.PENDENT);
    }

    @Test
    void quanEsAdministradorDEntitat_potCancelarLaDUnAltre() {
        Long id = crearExecucio(ExecucioMassivaTipusDto.TANCAMENT, null, 1);
        canviarCreador(id, ALTRE_USUARI);

        ExecucioMassivaProcesEstatDto resultat = execucioMassivaCuaHelper.cancelar(entitatId(), id, BaseConfig.ROLE_ADMIN);

        assertThat(resultat).isEqualTo(ExecucioMassivaProcesEstatDto.CANCELADA);
    }

    @Test
    void quanTeElRolAdministradorPeroNoElPermisDAdministracio_llancaPermissionDenied() {
        Long id = crearExecucio(ExecucioMassivaTipusDto.TANCAMENT, null, 1);
        canviarCreador(id, ALTRE_USUARI);
        Mockito.when(permisosHelper.isGrantedAll(
                        Mockito.any(), Mockito.any(), Mockito.any(), (org.springframework.security.core.Authentication) Mockito.any()))
                .thenReturn(false);

        assertThatThrownBy(() -> execucioMassivaCuaHelper.cancelar(entitatId(), id, BaseConfig.ROLE_ADMIN))
                .isInstanceOf(PermissionDeniedException.class);
    }

    // =========================================================================
    // Reclamació atòmica
    // =========================================================================

    @Test
    void unaExecucioNomesLaPotReclamarUnNode() {
        Long id = crearExecucio(ExecucioMassivaTipusDto.TANCAMENT, null, 1);

        assertThat(reclamar(id, "node1")).isEqualTo(1);
        //El batec del node1 és recent: el node2 no la pot agafar
        assertThat(reclamar(id, "node2")).isZero();
        //El mateix node sí (en arrencar, la seva execució EN_CURS ha quedat abandonada)
        assertThat(reclamar(id, "node1")).isEqualTo(1);
    }

    @Test
    void unaExecucioEnCursSenseBatecRecent_laPotReprendreUnAltreNode() {
        Long id = crearExecucio(ExecucioMassivaTipusDto.TANCAMENT, null, 1);
        reclamar(id, "node1");

        Date ara = new Date();
        //Límit de batec posterior al darrer batec del node1: es considera abandonada
        int reclamades = execucioMassivaRepository.reclamar(
                id,
                ara,
                ExecucioMassivaProcesEstatDto.PENDENT,
                ExecucioMassivaProcesEstatDto.EN_CURS,
                "node2",
                new Date(ara.getTime() + 1000));

        assertThat(reclamades).isEqualTo(1);
        assertThat(recarregar(id).getNode()).isEqualTo("node2");
    }

    @Test
    void lesCancelades_finalitzades_iLesFutures_noSonProcessables() {
        Long pendent = crearExecucio(ExecucioMassivaTipusDto.TANCAMENT, null, 1);
        Long cancelada = crearExecucio(ExecucioMassivaTipusDto.TANCAMENT, null, 1);
        execucioMassivaCuaHelper.cancelar(entitatId(), cancelada, ROL_USUARI);
        Long finalitzada = crearExecucio(ExecucioMassivaTipusDto.TANCAMENT, new Date(), 1);

        Date ara = new Date();
        List<Long> ids = execucioMassivaRepository.findIdsProcessables(
                ara,
                ExecucioMassivaProcesEstatDto.PENDENT,
                ExecucioMassivaProcesEstatDto.EN_CURS,
                "node1",
                new Date(ara.getTime() - 300000),
                PageRequest.of(0, 50));

        assertThat(ids).contains(pendent).doesNotContain(cancelada, finalitzada);
    }

    @Test
    void lesAccionsSincronesEsDesenFinalitzades() {
        Long id = crearExecucio(ExecucioMassivaTipusDto.TANCAMENT, new Date(), 1);

        assertThat(recarregar(id).getEstat()).isEqualTo(ExecucioMassivaProcesEstatDto.FINALITZADA);
    }

    // =========================================================================
    // Utilitats
    // =========================================================================

    private Long entitatId() {
        return testData.entitat.getId();
    }

    private Long crearExecucio(ExecucioMassivaTipusDto tipus, Date dataFi, int numElements) {
        EntitatEntity entitat = entityManager.find(EntitatEntity.class, entitatId());
        //Data d'inici en el passat perquè ja sigui processable
        ExecucioMassivaEntity execucio = ExecucioMassivaEntity.getBuilder(
                tipus,
                new Date(System.currentTimeMillis() - 60000),
                dataFi,
                entitat,
                ROL_USUARI).build();
        for (int i = 0; i < numElements; i++) {
            ExecucioMassivaContingutEntity element = ExecucioMassivaContingutEntity.getBuilder(
                    execucio,
                    (long) (i + 1),
                    "Element " + i,
                    ElementTipusEnumDto.EXPEDIENT,
                    i).build();
            element.updateEstatDataFi(ExecucioMassivaEstatDto.ESTAT_PENDENT, null);
            execucio.addContingut(element);
        }
        execucio = execucioMassivaRepository.save(execucio);
        entityManager.flush();
        entityManager.clear();
        return execucio.getId();
    }

    private int reclamar(Long id, String node) {
        Date ara = new Date();
        int reclamades = execucioMassivaRepository.reclamar(
                id,
                ara,
                ExecucioMassivaProcesEstatDto.PENDENT,
                ExecucioMassivaProcesEstatDto.EN_CURS,
                node,
                new Date(ara.getTime() - 300000));
        entityManager.flush();
        entityManager.clear();
        return reclamades;
    }

    private void marcarPrimerElementFinalitzat(Long id) {
        ExecucioMassivaContingutEntity primer = recarregar(id).getContinguts().get(0);
        primer.updateFinalitzat(new Date());
        execucioMassivaContingutRepository.save(primer);
        entityManager.flush();
        entityManager.clear();
    }

    private void canviarCreador(Long id, String creador) {
        entityManager.createQuery("update ExecucioMassivaEntity e set e.createdBy = :creador where e.id = :id")
                .setParameter("creador", creador)
                .setParameter("id", id)
                .executeUpdate();
        entityManager.clear();
    }

    private ExecucioMassivaEntity recarregar(Long id) {
        entityManager.flush();
        entityManager.clear();
        return execucioMassivaRepository.findById(id).get();
    }
}
