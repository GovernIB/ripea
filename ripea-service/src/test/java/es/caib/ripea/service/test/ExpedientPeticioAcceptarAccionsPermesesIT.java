package es.caib.ripea.service.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;

import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.MetaNodeEntity;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.ExpedientPeticioAccioEnumDto;
import es.caib.ripea.service.intf.model.ExpedientPeticioResource;
import es.caib.ripea.service.intf.model.ExpedientPeticioResource.AcceptarAnotacioForm;
import es.caib.ripea.service.intf.model.MetaExpedientResource;
import es.caib.ripea.service.intf.resourceservice.ExpedientPeticioResourceService;
import es.caib.ripea.service.permission.ExtendedPermission;
import es.caib.ripea.service.test.config.BaseServiceIT;

/**
 * Tests de les accions que el formulari d'acceptar anotació ofereix segons els permisos que
 * l'usuari té sobre el procediment seleccionat: crear un expedient nou requereix permís CREATE i
 * incorporar l'anotació a un existent, permís WRITE.
 *
 * És l'equivalent REACT de les crides comprovarPermisCreate/comprovarPermisWrite que el JSP fa
 * cada vegada que es canvia de procediment (ExpedientPeticioController i expedientPeticioAccept.jsp),
 * i aquests tests fixen que el resultat arribi al front pels camps potCrear/potIncorporar i que,
 * quan només se'n permet una, quedi seleccionada.
 *
 * S'utilitza PROC_01 (actiu, sense òrgan gestor i no comú) perquè de les cinc vies de permís que
 * comprova EntityComprovarHelper.comprovarPermisMetaExpedient només en queda activa la del permís
 * directe sobre el procediment, que és la que estubeja cada test.
 */
public class ExpedientPeticioAcceptarAccionsPermesesIT extends BaseServiceIT {

    @Autowired private ExpedientPeticioResourceService expedientPeticioResourceService;

    private MetaExpedientEntity procediment;

    @BeforeEach
    @Override
    public void setUpTestData() {
        super.setUpTestData();
        Mockito.lenient().when(configHelper.getEntitatActualCodi()).thenReturn(testData.entitat.getCodi());
        //Rol sense drecera d'administrador: obliga a passar per la comprovació de permisos.
        Mockito.lenient().when(configHelper.getRolActual()).thenReturn(BaseConfig.ROLE_USER);
        procediment = testData.metaExpedients.get(0);
    }

    @Test
    void donatsPermisosCreateIWrite_sOfereixenLesDuesAccions() {
        stubPermisDirecteProcediment(true, true);

        Map<String, Object> canvis = onChangeMetaExpedient(ExpedientPeticioAccioEnumDto.CREAR);

        assertThat(canvis).containsEntry(AcceptarAnotacioForm.Fields.potCrear, true);
        assertThat(canvis).containsEntry(AcceptarAnotacioForm.Fields.potIncorporar, true);
        //Amb totes dues accions permeses no s'ha de tocar la que l'usuari ha triat.
        assertThat(canvis).doesNotContainKey(AcceptarAnotacioForm.Fields.accio);
    }

    @Test
    void donatNomesPermisWrite_nomesSOfereixIncorporarIQuedaSeleccionada() {
        stubPermisDirecteProcediment(false, true);

        Map<String, Object> canvis = onChangeMetaExpedient(ExpedientPeticioAccioEnumDto.CREAR);

        assertThat(canvis).containsEntry(AcceptarAnotacioForm.Fields.potCrear, false);
        assertThat(canvis).containsEntry(AcceptarAnotacioForm.Fields.potIncorporar, true);
        assertThat(canvis).containsEntry(AcceptarAnotacioForm.Fields.accio, ExpedientPeticioAccioEnumDto.INCORPORAR);
    }

    @Test
    void donatNomesPermisCreate_nomesSOfereixCrearIQuedaSeleccionada() {
        stubPermisDirecteProcediment(true, false);

        Map<String, Object> canvis = onChangeMetaExpedient(ExpedientPeticioAccioEnumDto.INCORPORAR);

        assertThat(canvis).containsEntry(AcceptarAnotacioForm.Fields.potCrear, true);
        assertThat(canvis).containsEntry(AcceptarAnotacioForm.Fields.potIncorporar, false);
        assertThat(canvis).containsEntry(AcceptarAnotacioForm.Fields.accio, ExpedientPeticioAccioEnumDto.CREAR);
    }

    @Test
    void donatCapPermis_sOfereixenLesDuesAccionsPerNoDeixarElFormulariSenseOpcions() {
        stubPermisDirecteProcediment(false, false);

        Map<String, Object> canvis = onChangeMetaExpedient(ExpedientPeticioAccioEnumDto.CREAR);

        assertThat(canvis).containsEntry(AcceptarAnotacioForm.Fields.potCrear, true);
        assertThat(canvis).containsEntry(AcceptarAnotacioForm.Fields.potIncorporar, true);
        assertThat(canvis).doesNotContainKey(AcceptarAnotacioForm.Fields.accio);
    }

    @Test
    void donatRolAdministradorDEntitat_sOfereixenLesDuesAccionsSenseComprovarPermisos() {
        Mockito.when(configHelper.getRolActual()).thenReturn(BaseConfig.ROLE_ADMIN);
        stubPermisDirecteProcediment(false, false);

        Map<String, Object> canvis = onChangeMetaExpedient(ExpedientPeticioAccioEnumDto.CREAR);

        assertThat(canvis).containsEntry(AcceptarAnotacioForm.Fields.potCrear, true);
        assertThat(canvis).containsEntry(AcceptarAnotacioForm.Fields.potIncorporar, true);
    }

    // =========================================================================
    // Auxiliars
    // =========================================================================

    /**
     * Estubeja la via del permís directe sobre el procediment
     * (EntityComprovarHelper.isGrantedPermisProcediment), que és l'única via viva per a PROC_01.
     */
    private void stubPermisDirecteProcediment(boolean create, boolean write) {
        Mockito.when(permisosHelper.isGrantedAll(any(), eq(MetaNodeEntity.class), any(Permission[].class)))
                .thenAnswer(invocation -> {
                    Permission[] permisos = invocation.getArgument(2);
                    int mask = permisos[0].getMask();
                    if (mask == ExtendedPermission.CREATE.getMask()) {
                        return create;
                    }
                    if (mask == ExtendedPermission.WRITE.getMask()) {
                        return write;
                    }
                    return true;
                });
    }

    private Map<String, Object> onChangeMetaExpedient(ExpedientPeticioAccioEnumDto accioInicial) {
        AcceptarAnotacioForm form = new AcceptarAnotacioForm();
        form.setAccio(accioInicial);
        ResourceReference<MetaExpedientResource, Long> referencia =
                ResourceReference.toResourceReference(procediment.getId());
        return expedientPeticioResourceService.artifactOnChange(
                ResourceArtifactType.ACTION,
                ExpedientPeticioResource.ACTION_ACCEPTAR_ANOTACIO,
                null,
                form,
                AcceptarAnotacioForm.Fields.metaExpedient,
                referencia,
                null);
    }
}
