package es.caib.ripea.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import es.caib.ripea.persistence.entity.AclSidEntity;
import es.caib.ripea.persistence.repository.AclClassRepository;
import es.caib.ripea.persistence.repository.AclEntryRepository;
import es.caib.ripea.persistence.repository.AclObjectIdentityRepository;
import es.caib.ripea.persistence.repository.AclSidRepository;
import es.caib.ripea.service.intf.dto.PermisDto;
import es.caib.ripea.service.intf.dto.PrincipalTipusEnumDto;
import es.caib.ripea.service.permission.ExtendedPermission;

/**
 * Tests unitaris per a PermisosHelper.
 *
 * Cobreix els mètodes públics: findPermisos (per objecte i per llista),
 * updatePermis i deletePermis, tant per a permisos per usuari com per rol.
 * No arrenca cap context Spring: totes les dependències es proporcionen com a mocks de Mockito.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PermisosHelperTest {

    // ── Dependències mockejades ──────────────────────────────────────────────

    @Mock private LookupStrategy lookupStrategy;
    @Mock private MutableAclService aclService;
    @Mock private AclSidRepository aclSidRepository;
    @Mock private AclEntryRepository aclEntryRepository;
    @Mock private AclClassRepository aclClassRepository;
    @Mock private AclObjectIdentityRepository aclObjectIdentityRepository;
    @Mock private MessageHelper messageHelper;
    @Mock private CacheHelper cacheHelper;
    @Mock private ConfigHelper configHelper;

    @InjectMocks
    private PermisosHelper permisosHelper;

    private static final Long OBJECT_ID = 1L;

    /** Classe fantasma per als ObjectIdentityImpl de proves. */
    static class EntidadProva {}

    @AfterEach
    void netejatSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // =========================================================================
    // findPermisos(Serializable, Class)
    // =========================================================================

    @Test
    void findPermisos_retornaLlistaBuilda_quanNoExisteixAcl() {
        when(aclService.readAclById(any(ObjectIdentity.class))).thenThrow(new NotFoundException("ACL no trobada"));

        List<PermisDto> result = permisosHelper.findPermisos(OBJECT_ID, EntidadProva.class);

        assertThat(result).isEmpty();
    }

    @Test
    void findPermisos_retornaPermisUsuari_ambPermisLectura() {
        MutableAcl acl = preparaAclMock(Collections.singletonList(
                buildAce(new PrincipalSid("usuari1"), 100L, ExtendedPermission.READ)));
        when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(acl);

        List<PermisDto> result = permisosHelper.findPermisos(OBJECT_ID, EntidadProva.class);

        assertThat(result).hasSize(1);
        PermisDto permis = result.get(0);
        assertThat(permis.getPrincipalNom()).isEqualTo("usuari1");
        assertThat(permis.getPrincipalTipus()).isEqualTo(PrincipalTipusEnumDto.USUARI);
        assertThat(permis.isRead()).isTrue();
        assertThat(permis.isWrite()).isFalse();
        assertThat(permis.isAdministration()).isFalse();
    }

    @Test
    void findPermisos_retornaPermisRol_ambPermisEscriptura() {
        MutableAcl acl = preparaAclMock(Collections.singletonList(
                buildAce(new GrantedAuthoritySid("ROL_GESTOR"), 200L, ExtendedPermission.WRITE)));
        when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(acl);

        List<PermisDto> result = permisosHelper.findPermisos(OBJECT_ID, EntidadProva.class);

        assertThat(result).hasSize(1);
        PermisDto permis = result.get(0);
        assertThat(permis.getPrincipalNom()).isEqualTo("ROL_GESTOR");
        assertThat(permis.getPrincipalTipus()).isEqualTo(PrincipalTipusEnumDto.ROL);
        assertThat(permis.isWrite()).isTrue();
        assertThat(permis.isRead()).isFalse();
    }

    @Test
    void findPermisos_agrupaTotesPermissionsDelMateixUsuari() {
        PrincipalSid sid = new PrincipalSid("usuari1");
        MutableAcl acl = preparaAclMock(Arrays.asList(
                buildAce(sid, 100L, ExtendedPermission.READ),
                buildAce(sid, 101L, ExtendedPermission.WRITE),
                buildAce(sid, 102L, ExtendedPermission.ADMINISTRATION)));
        when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(acl);

        List<PermisDto> result = permisosHelper.findPermisos(OBJECT_ID, EntidadProva.class);

        assertThat(result).hasSize(1);
        PermisDto permis = result.get(0);
        assertThat(permis.isRead()).isTrue();
        assertThat(permis.isWrite()).isTrue();
        assertThat(permis.isAdministration()).isTrue();
        assertThat(permis.isDelete()).isFalse();
    }

    @Test
    void findPermisos_retornaPermisosPerUsuariIRolSeparats() {
        MutableAcl acl = preparaAclMock(Arrays.asList(
                buildAce(new PrincipalSid("usuari1"), 100L, ExtendedPermission.READ),
                buildAce(new GrantedAuthoritySid("ROL_ADMIN"), 200L, ExtendedPermission.ADMINISTRATION)));
        when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(acl);

        List<PermisDto> result = permisosHelper.findPermisos(OBJECT_ID, EntidadProva.class);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(PermisDto::getPrincipalTipus)
                .containsExactlyInAnyOrder(PrincipalTipusEnumDto.USUARI, PrincipalTipusEnumDto.ROL);
    }

    @Test
    void findPermisos_mappejaTotsTipusDePermis() {
        PrincipalSid sid = new PrincipalSid("usuari1");
        MutableAcl acl = preparaAclMock(Arrays.asList(
                buildAce(sid, 1L, ExtendedPermission.READ),
                buildAce(sid, 2L, ExtendedPermission.WRITE),
                buildAce(sid, 3L, ExtendedPermission.CREATE),
                buildAce(sid, 4L, ExtendedPermission.DELETE),
                buildAce(sid, 5L, ExtendedPermission.ADMINISTRATION),
                buildAce(sid, 6L, ExtendedPermission.ADMINISTRATION_READ),
                buildAce(sid, 7L, ExtendedPermission.STATISTICS),
                buildAce(sid, 8L, ExtendedPermission.COMU),
                buildAce(sid, 9L, ExtendedPermission.ADM_COMU),
                buildAce(sid, 10L, ExtendedPermission.DISSENY)));
        when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(acl);

        List<PermisDto> result = permisosHelper.findPermisos(OBJECT_ID, EntidadProva.class);

        assertThat(result).hasSize(1);
        PermisDto permis = result.get(0);
        assertThat(permis.isRead()).isTrue();
        assertThat(permis.isWrite()).isTrue();
        assertThat(permis.isCreate()).isTrue();
        assertThat(permis.isDelete()).isTrue();
        assertThat(permis.isAdministration()).isTrue();
        assertThat(permis.isAdministrationLectura()).isTrue();
        assertThat(permis.isStatistics()).isTrue();
        assertThat(permis.isProcedimentsComuns()).isTrue();
        assertThat(permis.isAdministrationComuns()).isTrue();
        assertThat(permis.isDisseny()).isTrue();
    }

    // =========================================================================
    // findPermisos(List<Serializable>, Class)
    // =========================================================================

    @Test
    void findPermisosLlista_retornaMapaBuit_quanLlistaBuilda() {
        Map<Serializable, List<PermisDto>> result = permisosHelper.findPermisos(Collections.emptyList(), EntidadProva.class);

        assertThat(result).isEmpty();
        verifyNoInteractions(lookupStrategy);
    }

    @Test
    void findPermisosLlista_retornaMapaAmbPermisosPerObjecte() {
        List<Serializable> ids = Arrays.asList(1L, 2L);

        ObjectIdentity oid1 = buildOid(1L);
        ObjectIdentity oid2 = buildOid(2L);
        Acl acl1 = buildAclAmbEntrada(new PrincipalSid("usuari1"), 100L, ExtendedPermission.READ);
        Acl acl2 = buildAclAmbEntrada(new GrantedAuthoritySid("ROL_GESTOR"), 200L, ExtendedPermission.WRITE);

        Map<ObjectIdentity, Acl> aclsMap = new HashMap<>();
        aclsMap.put(oid1, acl1);
        aclsMap.put(oid2, acl2);
        when(lookupStrategy.readAclsById(anyList(), isNull())).thenReturn(aclsMap);

        Map<Serializable, List<PermisDto>> result = permisosHelper.findPermisos(ids, EntidadProva.class);

        assertThat(result).hasSize(2);
        assertThat(result.get(1L)).hasSize(1);
        assertThat(result.get(1L).get(0).getPrincipalTipus()).isEqualTo(PrincipalTipusEnumDto.USUARI);
        assertThat(result.get(2L)).hasSize(1);
        assertThat(result.get(2L).get(0).getPrincipalTipus()).isEqualTo(PrincipalTipusEnumDto.ROL);
    }

    @Test
    void findPermisosLlista_retornaMapaBuit_quanNotFoundException() {
        List<Serializable> ids = Collections.singletonList(1L);
        when(lookupStrategy.readAclsById(anyList(), isNull())).thenThrow(new NotFoundException("ACL no trobada"));

        Map<Serializable, List<PermisDto>> result = permisosHelper.findPermisos(ids, EntidadProva.class);

        assertThat(result).isEmpty();
    }

    // =========================================================================
    // updatePermis (create / replace)
    // =========================================================================

    @Test
    void updatePermis_creaAclIAssignaPermisLectura_perUsuari() {
        PermisDto permis = buildPermisDto("usuari1", PrincipalTipusEnumDto.USUARI, true, false, false);
        MutableAcl acl = preparaAclMock(new ArrayList<>());
        when(aclService.readAclById(any(ObjectIdentity.class))).thenThrow(new NotFoundException("ACL no trobada"));
        when(aclService.createAcl(any(ObjectIdentity.class))).thenReturn(acl);

        permisosHelper.updatePermis(OBJECT_ID, EntidadProva.class, permis);

        verify(aclService).createAcl(any(ObjectIdentity.class));
        verify(acl).insertAce(anyInt(), eq(ExtendedPermission.READ), any(PrincipalSid.class), eq(true));
    }

    @Test
    void updatePermis_assignaMultiplesPermisos_perUsuari() {
        PermisDto permis = buildPermisDto("usuari1", PrincipalTipusEnumDto.USUARI, true, true, false);
        permis.setDelete(true);
        MutableAcl acl = preparaAclMock(new ArrayList<>());
        when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(acl);

        permisosHelper.updatePermis(OBJECT_ID, EntidadProva.class, permis);

        // READ + WRITE + DELETE = 3 insertAce
        verify(acl, times(3)).insertAce(anyInt(), any(Permission.class), any(Sid.class), eq(true));
    }

    @Test
    void updatePermis_assignaPermisAdministracio_perRol() {
        PermisDto permis = buildPermisDto("ROL_GESTOR", PrincipalTipusEnumDto.ROL, false, false, true);
        when(configHelper.getEnvironmentProperty(eq("es.caib.ripea.mapeig.rol.ROL_GESTOR"), isNull())).thenReturn(null);
        MutableAcl acl = preparaAclMock(new ArrayList<>());
        when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(acl);

        permisosHelper.updatePermis(OBJECT_ID, EntidadProva.class, permis);

        verify(acl).insertAce(anyInt(), eq(ExtendedPermission.ADMINISTRATION), any(GrantedAuthoritySid.class), eq(true));
    }

    @Test
    void updatePermis_netejaPermisosPrevisDelMateixUsuari_abansAssignar() {
        PermisDto permis = buildPermisDto("usuari1", PrincipalTipusEnumDto.USUARI, true, false, false);
        AccessControlEntry aceExistent = buildAce(new PrincipalSid("usuari1"), 50L, ExtendedPermission.READ);
        MutableAcl acl = preparaAclMock(new ArrayList<>(Collections.singletonList(aceExistent)));
        when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(acl);

        permisosHelper.updatePermis(OBJECT_ID, EntidadProva.class, permis);

        verify(acl).deleteAce(0);
    }

    @Test
    void updatePermis_noNetejaPermisosDAltreUsuari() {
        PermisDto permis = buildPermisDto("usuari1", PrincipalTipusEnumDto.USUARI, true, false, false);
        AccessControlEntry aceAltre = buildAce(new PrincipalSid("altreUsuari"), 60L, ExtendedPermission.READ);
        MutableAcl acl = preparaAclMock(new ArrayList<>(Collections.singletonList(aceAltre)));
        when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(acl);

        permisosHelper.updatePermis(OBJECT_ID, EntidadProva.class, permis);

        verify(acl, never()).deleteAce(anyInt());
    }

    @Test
    void updatePermis_netejaPermisosPrevisDelMateixRol_abansAssignar() {
        PermisDto permis = buildPermisDto("ROL_GESTOR", PrincipalTipusEnumDto.ROL, true, false, false);
        when(configHelper.getEnvironmentProperty(eq("es.caib.ripea.mapeig.rol.ROL_GESTOR"), isNull())).thenReturn(null);
        AccessControlEntry aceExistent = buildAce(new GrantedAuthoritySid("ROL_GESTOR"), 70L, ExtendedPermission.WRITE);
        MutableAcl acl = preparaAclMock(new ArrayList<>(Collections.singletonList(aceExistent)));
        when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(acl);

        permisosHelper.updatePermis(OBJECT_ID, EntidadProva.class, permis);

        verify(acl).deleteAce(0);
    }

    @Test
    void updatePermis_aplicaMapeigRol_quanHiHaConfiguracio() {
        PermisDto permis = buildPermisDto("ROL_ORIGINAL", PrincipalTipusEnumDto.ROL, true, false, false);
        when(configHelper.getEnvironmentProperty(eq("es.caib.ripea.mapeig.rol.ROL_ORIGINAL"), isNull())).thenReturn("ROL_MAPEJAT");
        MutableAcl acl = preparaAclMock(new ArrayList<>());
        when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(acl);

        permisosHelper.updatePermis(OBJECT_ID, EntidadProva.class, permis);

        verify(acl).insertAce(
                anyInt(),
                eq(ExtendedPermission.READ),
                argThat(sid -> sid instanceof GrantedAuthoritySid
                        && "ROL_MAPEJAT".equals(((GrantedAuthoritySid) sid).getGrantedAuthority())),
                eq(true));
    }

    // =========================================================================
    // deletePermis
    // =========================================================================

    @Test
    void deletePermis_noFaRes_quanNoExisteixAcl() {
        when(aclService.readAclById(any(ObjectIdentity.class))).thenThrow(new NotFoundException("ACL no trobada"));

        permisosHelper.deletePermis(OBJECT_ID, EntidadProva.class, 100L);

        verify(aclService, never()).updateAcl(any());
    }

    @Test
    void deletePermis_noFaRes_quanPermisIdNoCoincideix() {
        AccessControlEntry ace = buildAce(new PrincipalSid("usuari1"), 100L, ExtendedPermission.READ);
        MutableAcl acl = preparaAclMock(new ArrayList<>(Collections.singletonList(ace)));
        when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(acl);

        permisosHelper.deletePermis(OBJECT_ID, EntidadProva.class, 999L);

        verify(aclService, never()).updateAcl(any());
    }

    @Test
    void deletePermis_esborraTotesPermissionsDelSid_quanPermisIdCoincideixPerUsuari() {
        AccessControlEntry ace = buildAce(new PrincipalSid("usuari1"), 100L, ExtendedPermission.READ);
        MutableAcl acl = preparaAclMock(new ArrayList<>(Collections.singletonList(ace)));
        when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(acl);
        doNothing().when(cacheHelper).evictReadAclById(any(ObjectIdentity.class));

        permisosHelper.deletePermis(OBJECT_ID, EntidadProva.class, 100L);

        verify(acl).deleteAce(0);
        verify(aclService, times(2)).updateAcl(acl);
    }

    @Test
    void deletePermis_esborraTotesPermissionsDelSid_quanPermisIdCoincideixPerRol() {
        AccessControlEntry ace = buildAce(new GrantedAuthoritySid("ROL_GESTOR"), 200L, ExtendedPermission.WRITE);
        MutableAcl acl = preparaAclMock(new ArrayList<>(Collections.singletonList(ace)));
        when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(acl);
        doNothing().when(cacheHelper).evictReadAclById(any(ObjectIdentity.class));

        permisosHelper.deletePermis(OBJECT_ID, EntidadProva.class, 200L);

        verify(acl).deleteAce(0);
        verify(aclService, times(2)).updateAcl(acl);
    }

    // =========================================================================
    // getObjectsIdsWithPermission
    // =========================================================================

    @Test
    void getObjectsIdsWithPermission_retornaLlistaBuilda_quanAuthNull() {
        configurarSecurityContext(null);

        List<Long> result = permisosHelper.getObjectsIdsWithPermission(EntidadProva.class, ExtendedPermission.READ);

        assertThat(result).isEmpty();
    }

    @Test
    void getObjectsIdsWithPermission_retornaObjectes_quanAuthAmbUsuariIRol() {
        Authentication auth = buildAuth("usuari1", "ROL_GESTOR");
        configurarSecurityContext(auth);
        AclSidEntity userSid = mock(AclSidEntity.class);
        AclSidEntity rolSid = mock(AclSidEntity.class);
        when(aclSidRepository.getUserSid("usuari1")).thenReturn(userSid);
        when(aclSidRepository.findRolesSid(Collections.singletonList("ROL_GESTOR"))).thenReturn(Collections.singletonList(rolSid));
        when(aclObjectIdentityRepository.findObjectsWithPermissions(
                EntidadProva.class.getName(), Arrays.asList(userSid, rolSid), ExtendedPermission.READ.getMask()))
                .thenReturn(Arrays.asList(10L, 20L));

        List<Long> result = permisosHelper.getObjectsIdsWithPermission(EntidadProva.class, ExtendedPermission.READ);

        assertThat(result).containsExactly(10L, 20L);
    }

    @Test
    void getObjectsIdsWithPermission_retornaLlistaBuilda_quanNoHiHaSids() {
        Authentication auth = buildAuth("usuari1");
        configurarSecurityContext(auth);
        when(aclSidRepository.getUserSid("usuari1")).thenReturn(null);
        when(aclSidRepository.findRolesSid(anyList())).thenReturn(Collections.emptyList());

        List<Long> result = permisosHelper.getObjectsIdsWithPermission(EntidadProva.class, ExtendedPermission.READ);

        assertThat(result).isEmpty();
        verify(aclObjectIdentityRepository, never()).findObjectsWithPermissions(any(), anyList(), any(Integer.class));
    }

    @Test
    void getObjectsIdsWithPermission_perParametresExplicits_retornaObjectes() {
        configurarSecurityContext(buildAuth("qualsevol"));
        AclSidEntity userSid = mock(AclSidEntity.class);
        AclSidEntity rolSid = mock(AclSidEntity.class);
        when(aclSidRepository.getUserSid("usuari_exp")).thenReturn(userSid);
        when(aclSidRepository.findRolesSid(Collections.singletonList("ROL_EXP"))).thenReturn(Collections.singletonList(rolSid));
        when(aclObjectIdentityRepository.findObjectsWithPermissions(
                EntidadProva.class.getName(), Arrays.asList(userSid, rolSid), ExtendedPermission.WRITE.getMask()))
                .thenReturn(Collections.singletonList(5L));

        List<Long> result = permisosHelper.getObjectsIdsWithPermission(
                EntidadProva.class, ExtendedPermission.WRITE, "usuari_exp", "ROL_EXP");

        assertThat(result).containsExactly(5L);
    }

    // =========================================================================
    // getObjectsIdsWithTwoPermissions
    // =========================================================================

    @Test
    void getObjectsIdsWithTwoPermissions_retornaLlistaBuilda_quanAuthNull() {
        configurarSecurityContext(null);

        List<Long> result = permisosHelper.getObjectsIdsWithTwoPermissions(
                EntidadProva.class, ExtendedPermission.READ, ExtendedPermission.WRITE);

        assertThat(result).isEmpty();
    }

    @Test
    void getObjectsIdsWithTwoPermissions_retornaObjectes_quanAuthAmbUserISid() {
        Authentication auth = buildAuth("usuari1", "ROL_GESTOR");
        configurarSecurityContext(auth);
        AclSidEntity userSid = mock(AclSidEntity.class);
        AclSidEntity rolSid = mock(AclSidEntity.class);
        when(aclSidRepository.getUserSid("usuari1")).thenReturn(userSid);
        when(aclSidRepository.findRolesSid(Collections.singletonList("ROL_GESTOR"))).thenReturn(Collections.singletonList(rolSid));
        when(aclObjectIdentityRepository.findObjectsWithPermissionsBoth(
                EntidadProva.class.getName(),
                Arrays.asList(userSid, rolSid),
                ExtendedPermission.READ.getMask(),
                ExtendedPermission.WRITE.getMask()))
                .thenReturn(Arrays.asList(1L, 2L, 3L));

        List<Long> result = permisosHelper.getObjectsIdsWithTwoPermissions(
                EntidadProva.class, ExtendedPermission.READ, ExtendedPermission.WRITE);

        assertThat(result).containsExactly(1L, 2L, 3L);
    }

    @Test
    void getObjectsIdsWithTwoPermissions_retornaLlistaBuilda_quanNoHiHaSids() {
        Authentication auth = buildAuth("usuari1");
        configurarSecurityContext(auth);
        when(aclSidRepository.getUserSid("usuari1")).thenReturn(null);
        when(aclSidRepository.findRolesSid(anyList())).thenReturn(Collections.emptyList());

        List<Long> result = permisosHelper.getObjectsIdsWithTwoPermissions(
                EntidadProva.class, ExtendedPermission.READ, ExtendedPermission.WRITE);

        assertThat(result).isEmpty();
        verify(aclObjectIdentityRepository, never()).findObjectsWithPermissionsBoth(
                any(), anyList(), any(Integer.class), any(Integer.class));
    }

    // =========================================================================
    // filterGrantedAny
    // =========================================================================

    @Test
    @SuppressWarnings("unchecked")
    void filterGrantedAny_conservaObjectesAmbPermis_perAuth() {
        Authentication auth = buildAuth("usuari1");
        Acl acl = mock(Acl.class);
        when(cacheHelper.readAclById(any(ObjectIdentity.class))).thenReturn(acl);
        when(acl.isGranted(anyList(), anyList(), eq(false))).thenReturn(true);

        List<Long> items = new ArrayList<>(Arrays.asList(1L, 2L));
        permisosHelper.filterGrantedAny(items,
                (PermisosHelper.ObjectIdentifierExtractor<Long>) id -> id,
                EntidadProva.class,
                new Permission[]{ ExtendedPermission.READ },
                auth);

        assertThat(items).containsExactly(1L, 2L);
    }

    @Test
    @SuppressWarnings("unchecked")
    void filterGrantedAny_eliminaObjectesSensePermis_perAuth() {
        Authentication auth = buildAuth("usuari1");
        Acl acl = mock(Acl.class);
        when(cacheHelper.readAclById(any(ObjectIdentity.class)))
                .thenReturn(acl)   // item 1L → has permission
                .thenReturn(null); // item 2L → null ACL → no permission
        when(acl.isGranted(anyList(), anyList(), eq(false))).thenReturn(true);

        List<Long> items = new ArrayList<>(Arrays.asList(1L, 2L));
        permisosHelper.filterGrantedAny(items,
                (PermisosHelper.ObjectIdentifierExtractor<Long>) id -> id,
                EntidadProva.class,
                new Permission[]{ ExtendedPermission.READ },
                auth);

        assertThat(items).containsExactly(1L);
    }

    @Test
    @SuppressWarnings("unchecked")
    void filterGrantedAny_eliminaObjectesAmbIdentificadorNull() {
        Authentication auth = buildAuth("usuari1");

        List<Long> items = new ArrayList<>(Arrays.asList(1L, null));
        permisosHelper.filterGrantedAny(items,
                (PermisosHelper.ObjectIdentifierExtractor<Long>) id -> id,
                EntidadProva.class,
                new Permission[]{ ExtendedPermission.READ },
                auth);

        // null identifier is removed unconditionally; 1L has no ACL → also removed
        assertThat(items).doesNotContain((Long) null);
    }

    @Test
    void filterGrantedAny_ambIds_eliminaIdsSensePermis_perAuth() {
        Authentication auth = buildAuth("usuari1");
        Acl acl = mock(Acl.class);
        when(cacheHelper.readAclById(any(ObjectIdentity.class)))
                .thenReturn(acl)
                .thenReturn(null);
        when(acl.isGranted(anyList(), anyList(), eq(false))).thenReturn(true);

        List<Serializable> ids = new ArrayList<>(Arrays.asList(1L, 2L));
        permisosHelper.filterGrantedAny(ids, EntidadProva.class,
                new Permission[]{ ExtendedPermission.READ }, auth);

        assertThat(ids).containsExactly(1L);
    }

    @Test
    @SuppressWarnings("unchecked")
    void filterGrantedAny_ambUsuariCodi_conservaObjectesAmbPermis() {
        when(cacheHelper.findRolsAmbCodi("usuari1")).thenReturn(Collections.emptyList());
        Acl acl = mock(Acl.class);
        when(cacheHelper.readAclById(any(ObjectIdentity.class))).thenReturn(acl);
        when(acl.isGranted(anyList(), anyList(), eq(false))).thenReturn(true);

        List<Long> items = new ArrayList<>(Collections.singletonList(1L));
        permisosHelper.filterGrantedAny(items,
                (PermisosHelper.ObjectIdentifierExtractor<Long>) id -> id,
                EntidadProva.class,
                new Permission[]{ ExtendedPermission.READ },
                "usuari1");

        assertThat(items).containsExactly(1L);
    }

    // =========================================================================
    // filterGrantedAll
    // =========================================================================

    @Test
    @SuppressWarnings("unchecked")
    void filterGrantedAll_conservaObjectesAmbTotsElsPermisos_perAuth() {
        Authentication auth = buildAuth("usuari1");
        Acl acl = mock(Acl.class);
        when(cacheHelper.readAclById(any(ObjectIdentity.class))).thenReturn(acl);
        when(acl.isGranted(anyList(), anyList(), eq(false))).thenReturn(true);

        List<Long> items = new ArrayList<>(Arrays.asList(1L, 2L));
        permisosHelper.filterGrantedAll(items,
                (PermisosHelper.ObjectIdentifierExtractor<Long>) id -> id,
                EntidadProva.class,
                new Permission[]{ ExtendedPermission.READ, ExtendedPermission.WRITE },
                auth);

        assertThat(items).containsExactly(1L, 2L);
    }

    @Test
    @SuppressWarnings("unchecked")
    void filterGrantedAll_eliminaObjectesAmbSolsAlguns_perAuth() {
        Authentication auth = buildAuth("usuari1");
        Acl acl = mock(Acl.class);
        when(cacheHelper.readAclById(any(ObjectIdentity.class))).thenReturn(acl);
        // READ granted, WRITE not granted
        when(acl.isGranted(anyList(), anyList(), eq(false))).thenReturn(true, false);

        List<Long> items = new ArrayList<>(Collections.singletonList(1L));
        permisosHelper.filterGrantedAll(items,
                (PermisosHelper.ObjectIdentifierExtractor<Long>) id -> id,
                EntidadProva.class,
                new Permission[]{ ExtendedPermission.READ, ExtendedPermission.WRITE },
                auth);

        assertThat(items).isEmpty();
    }

    @Test
    void filterGrantedAll_ambIds_eliminaIdsSenseTots_perAuth() {
        Authentication auth = buildAuth("usuari1");
        Acl acl = mock(Acl.class);
        when(cacheHelper.readAclById(any(ObjectIdentity.class)))
                .thenReturn(acl)   // 1L → has all
                .thenReturn(null); // 2L → no ACL
        when(acl.isGranted(anyList(), anyList(), eq(false))).thenReturn(true);

        List<Serializable> ids = new ArrayList<>(Arrays.asList(1L, 2L));
        permisosHelper.filterGrantedAll(ids, EntidadProva.class,
                new Permission[]{ ExtendedPermission.READ }, auth);

        assertThat(ids).containsExactly(1L);
    }

    @Test
    @SuppressWarnings("unchecked")
    void filterGrantedAll_ambUsuariCodi_eliminaObjectesSensePermis() {
        when(cacheHelper.findRolsAmbCodi("usuari1")).thenReturn(Collections.emptyList());
        when(cacheHelper.readAclById(any(ObjectIdentity.class))).thenReturn(null);

        List<Long> items = new ArrayList<>(Collections.singletonList(1L));
        permisosHelper.filterGrantedAll(items,
                (PermisosHelper.ObjectIdentifierExtractor<Long>) id -> id,
                EntidadProva.class,
                new Permission[]{ ExtendedPermission.READ },
                "usuari1");

        assertThat(items).isEmpty();
    }

    // =========================================================================
    // isGrantedAll
    // =========================================================================

    @Test
    void isGrantedAll_retornaTrue_quanTotsElsPermisosAtorgats_perAuth() {
        Authentication auth = buildAuth("usuari1");
        Acl acl = mock(Acl.class);
        when(cacheHelper.readAclById(any(ObjectIdentity.class))).thenReturn(acl);
        when(acl.isGranted(anyList(), anyList(), eq(false))).thenReturn(true);

        boolean result = permisosHelper.isGrantedAll(
                OBJECT_ID, EntidadProva.class,
                new Permission[]{ ExtendedPermission.READ, ExtendedPermission.WRITE },
                auth);

        assertThat(result).isTrue();
    }

    @Test
    void isGrantedAll_retornaFalse_quanUnPermisNoAtorgat_perAuth() {
        Authentication auth = buildAuth("usuari1");
        Acl acl = mock(Acl.class);
        when(cacheHelper.readAclById(any(ObjectIdentity.class))).thenReturn(acl);
        // READ ok, WRITE denied
        when(acl.isGranted(anyList(), anyList(), eq(false))).thenReturn(true, false);

        boolean result = permisosHelper.isGrantedAll(
                OBJECT_ID, EntidadProva.class,
                new Permission[]{ ExtendedPermission.READ, ExtendedPermission.WRITE },
                auth);

        assertThat(result).isFalse();
    }

    @Test
    void isGrantedAll_retornaFalse_quanNoHiHaAcl_perAuth() {
        Authentication auth = buildAuth("usuari1");
        when(cacheHelper.readAclById(any(ObjectIdentity.class))).thenReturn(null);

        boolean result = permisosHelper.isGrantedAll(
                OBJECT_ID, EntidadProva.class,
                new Permission[]{ ExtendedPermission.READ },
                auth);

        assertThat(result).isFalse();
    }

    @Test
    void isGrantedAll_retornaTrue_quanPermisAtorgat_perUsuariCodi() {
        when(cacheHelper.findRolsAmbCodi("usuari1")).thenReturn(Collections.emptyList());
        Acl acl = mock(Acl.class);
        when(cacheHelper.readAclById(any(ObjectIdentity.class))).thenReturn(acl);
        when(acl.isGranted(anyList(), anyList(), eq(false))).thenReturn(true);

        boolean result = permisosHelper.isGrantedAll(
                OBJECT_ID, EntidadProva.class,
                new Permission[]{ ExtendedPermission.READ },
                "usuari1");

        assertThat(result).isTrue();
    }

    @Test
    void isGrantedAll_retornaTrue_quanPermisAtorgat_perUsuariCodiAmbRol() {
        when(cacheHelper.findRolsAmbCodi("usuari1")).thenReturn(Collections.singletonList("ROL_GESTOR"));
        when(configHelper.getEnvironmentProperty(eq("es.caib.ripea.mapeig.rol.ROL_GESTOR"), isNull())).thenReturn(null);
        Acl acl = mock(Acl.class);
        when(cacheHelper.readAclById(any(ObjectIdentity.class))).thenReturn(acl);
        when(acl.isGranted(anyList(), anyList(), eq(false))).thenReturn(true);

        boolean result = permisosHelper.isGrantedAll(
                OBJECT_ID, EntidadProva.class,
                new Permission[]{ ExtendedPermission.READ },
                "usuari1");

        assertThat(result).isTrue();
    }

    @Test
    void isGrantedAll_retornaFalse_quanPermisNoAtorgat_perUsuariCodi() {
        when(cacheHelper.findRolsAmbCodi("usuari1")).thenReturn(Collections.emptyList());
        Acl acl = mock(Acl.class);
        when(cacheHelper.readAclById(any(ObjectIdentity.class))).thenReturn(acl);
        when(acl.isGranted(anyList(), anyList(), eq(false))).thenReturn(false);

        boolean result = permisosHelper.isGrantedAll(
                OBJECT_ID, EntidadProva.class,
                new Permission[]{ ExtendedPermission.READ },
                "usuari1");

        assertThat(result).isFalse();
    }

    @Test
    void isGrantedAll_usaSecurityContextHolder_quanSenseAuth() {
        Authentication auth = buildAuth("usuari1");
        configurarSecurityContext(auth);
        Acl acl = mock(Acl.class);
        when(cacheHelper.readAclById(any(ObjectIdentity.class))).thenReturn(acl);
        when(acl.isGranted(anyList(), anyList(), eq(false))).thenReturn(true);

        boolean result = permisosHelper.isGrantedAll(
                OBJECT_ID, EntidadProva.class,
                new Permission[]{ ExtendedPermission.READ });

        assertThat(result).isTrue();
    }

    // =========================================================================
    // Mètodes auxiliars
    // =========================================================================

    private AccessControlEntry buildAce(Sid sid, Long id, Permission permission) {
        AccessControlEntry ace = mock(AccessControlEntry.class);
        when(ace.getSid()).thenReturn(sid);
        when(ace.getId()).thenReturn(id);
        when(ace.getPermission()).thenReturn(permission);
        return ace;
    }

    private MutableAcl preparaAclMock(List<AccessControlEntry> entries) {
        MutableAcl acl = mock(MutableAcl.class);
        when(acl.getEntries()).thenReturn(entries);
        return acl;
    }

    private Acl buildAclAmbEntrada(Sid sid, Long aceId, Permission permission) {
        AccessControlEntry ace = buildAce(sid, aceId, permission);
        Acl acl = mock(Acl.class);
        when(acl.getEntries()).thenReturn(Collections.singletonList(ace));
        return acl;
    }

    private ObjectIdentity buildOid(Long id) {
        ObjectIdentity oid = mock(ObjectIdentity.class);
        when(oid.getIdentifier()).thenReturn(id);
        return oid;
    }

    private PermisDto buildPermisDto(String nom, PrincipalTipusEnumDto tipus, boolean read, boolean write, boolean admin) {
        PermisDto permis = new PermisDto();
        permis.setPrincipalNom(nom);
        permis.setPrincipalTipus(tipus);
        permis.setRead(read);
        permis.setWrite(write);
        permis.setAdministration(admin);
        return permis;
    }

    @SuppressWarnings("unchecked")
    private Authentication buildAuth(String username, String... roles) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(username);
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            GrantedAuthority ga = mock(GrantedAuthority.class);
            when(ga.getAuthority()).thenReturn(role);
            authorities.add(ga);
        }
        when(auth.getAuthorities()).thenReturn((Collection) authorities);
        return auth;
    }

    private void configurarSecurityContext(Authentication auth) {
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);
    }
}
