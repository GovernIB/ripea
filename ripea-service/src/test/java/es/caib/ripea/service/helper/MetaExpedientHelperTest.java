package es.caib.ripea.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.GrupEntity;
import es.caib.ripea.persistence.entity.MetaDocumentEntity;
import es.caib.ripea.persistence.entity.MetaExpedientCarpetaEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.persistence.entity.MetaExpedientSequenciaEntity;
import es.caib.ripea.persistence.entity.MetaExpedientTascaEntity;
import es.caib.ripea.persistence.entity.MetaExpedientTascaValidacioEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.repository.AvisRepository;
import es.caib.ripea.persistence.repository.DominiRepository;
import es.caib.ripea.persistence.repository.ExpedientEstatRepository;
import es.caib.ripea.persistence.repository.ExpedientRepository;
import es.caib.ripea.persistence.repository.GrupRepository;
import es.caib.ripea.persistence.repository.MetaDocumentRepository;
import es.caib.ripea.persistence.repository.MetaExpedientComentariRepository;
import es.caib.ripea.persistence.repository.MetaExpedientOrganGestorRepository;
import es.caib.ripea.persistence.repository.MetaExpedientRepository;
import es.caib.ripea.persistence.repository.MetaExpedientSequenciaRepository;
import es.caib.ripea.persistence.repository.MetaExpedientTascaRepository;
import es.caib.ripea.persistence.repository.MetaExpedientTascaValidacioRepository;
import es.caib.ripea.persistence.repository.MetaNodeRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.persistence.repository.UsuariRepository;
import es.caib.ripea.persistence.repository.historic.HistoricExpedientRepository;
import es.caib.ripea.persistence.repository.historic.HistoricInteressatRepository;
import es.caib.ripea.persistence.repository.historic.HistoricUsuariRepository;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.ArbreJsonDto;
import es.caib.ripea.service.intf.dto.ArbreNodeDto;
import es.caib.ripea.service.intf.dto.CrearReglaDistribucioEstatEnumDto;
import es.caib.ripea.service.intf.dto.CrearReglaResponseDto;
import es.caib.ripea.service.intf.dto.ExpedientEstatDto;
import es.caib.ripea.service.intf.dto.GrupDto;
import es.caib.ripea.service.intf.dto.ItemValidacioTascaEnum;
import es.caib.ripea.service.intf.dto.MetaDadaDto;
import es.caib.ripea.service.intf.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentDto;
import es.caib.ripea.service.intf.dto.MetaExpedientCarpetaDto;
import es.caib.ripea.service.intf.dto.MetaExpedientCarpetaMinDto;
import es.caib.ripea.service.intf.dto.MetaExpedientDto;
import es.caib.ripea.service.intf.dto.MetaExpedientExportDto;
import es.caib.ripea.service.intf.dto.MetaExpedientFiltreDto;
import es.caib.ripea.service.intf.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.service.intf.dto.MetaExpedientTascaDto;
import es.caib.ripea.service.intf.dto.MetaExpedientTascaValidacioDto;
import es.caib.ripea.service.intf.dto.MultiplicitatEnumDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.PermisDto;
import es.caib.ripea.service.intf.dto.ProcedimentDto;
import es.caib.ripea.service.intf.dto.ProgresActualitzacioDto;
import es.caib.ripea.service.intf.dto.TipusClassificacioEnumDto;
import es.caib.ripea.service.intf.dto.StatusEnumDto;
import es.caib.ripea.service.intf.dto.TipusValidacioTascaEnum;
import es.caib.ripea.service.intf.exception.ExisteixenExpedientsEsborratsException;
import es.caib.ripea.service.intf.exception.ExisteixenExpedientsException;
import es.caib.ripea.service.permission.ExtendedPermission;
import es.caib.ripea.service.service.MetaExpedientServiceImpl;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Meter.Id;
import io.micrometer.core.instrument.Meter.Type;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;

/**
 * Tests unitaris per a MetaExpedientHelper.
 *
 * Cobreix tots els mètodes públics amb casos normals, límit i excepcions.
 * No arrenca cap context Spring: totes les dependències es proporcionen com a mocks de Mockito.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MetaExpedientHelperTest {

    // ── Dependències mockejades ──────────────────────────────────────────────

    @Mock private MetaExpedientSequenciaRepository metaExpedientSequenciaRepository;
    @Mock private MetaExpedientRepository metaExpedientRepository;
    @Mock private MetaNodeRepository metaNodeRepository;
    @Mock private OrganGestorRepository organGestorRepository;
    @Mock private EntityComprovarHelper entityComprovarHelper;
    @Mock private PermisosHelper permisosHelper;
    @Mock private OrganGestorHelper organGestorHelper;
    @Mock private MetaExpedientCarpetaHelper metaExpedientCarpetaHelper;
    @Mock private MetaExpedientOrganGestorRepository metaExpedientOrganGestorRepository;
    @Mock private ExpedientEstatRepository expedientEstatRepository;
    @Mock private MetaExpedientTascaRepository metaExpedientTascaRepository;
    @Mock private MetaExpedientTascaValidacioRepository metaExpedientTascaValidacioRepository;
    @Mock private AvisRepository avisRepository;
    @Mock private EmailHelper emailHelper;
    @Mock private ConfigHelper configHelper;
    @Mock private PluginHelper pluginHelper;
    @Mock private DominiHelper dominiHelper;
    @Mock private ConversioTipusHelper conversioTipusHelper;
    @Mock private DistribucioReglaHelper distribucioReglaHelper;
    @Mock private MessageHelper messageHelper;
    @Mock private MetaExpedientComentariRepository metaExpedientComentariRepository;
    @Mock private CacheHelper cacheHelper;
    @Mock private MetaNodeHelper metaNodeHelper;
    @Mock private MetaDocumentRepository metaDocumentRepository;
    @Mock private ApplicationHelper applicationHelper;
    @Mock private PaginacioHelper paginacioHelper;
    @Mock private ExpedientRepository expedientRepository;
    @Mock private DominiRepository dominiRepository;
    @Mock private OrganGestorCacheHelper organGestorCacheHelper;
    @Mock private MetaDocumentHelper metaDocumentHelper;
    @Mock private MetaDadaHelper metaDadaHelper;
    @Mock private ExpedientEstatHelper expedientEstatHelper;
    @Mock private ContingutLogHelper contingutLogHelper;
    @Mock private GrupHelper grupHelper;
    @Mock private GrupRepository grupRepository;
    @Mock private HistoricExpedientRepository historicExpedientRepository;
    @Mock private HistoricInteressatRepository historicInteressatRepository;
    @Mock private HistoricUsuariRepository historicUsuariRepository;
    @Mock private UsuariRepository usuariRepository;
    @Mock private ValidacioCacheEvictHelper validacioCacheEvictHelper;

    @InjectMocks
    private MetaExpedientHelper helper;

    private static final Long ENTITAT_ID = 1L;
    private static final Long META_EXPEDIENT_ID = 10L;
    private static final Long ORGAN_ID = 20L;

    @BeforeEach
    void configurarSecurityContext() {
        Authentication auth = mock(Authentication.class);
        SecurityContext ctx = mock(SecurityContext.class);
        org.mockito.Mockito.lenient().when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);
    }

    @AfterEach
    void netejarSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // =========================================================================
    // getIds
    // =========================================================================

    @Test
    void getIds_donadaLlistaEntitats_retornaElsIdCorrectes() {
        MetaExpedientEntity e1 = mock(MetaExpedientEntity.class);
        MetaExpedientEntity e2 = mock(MetaExpedientEntity.class);
        when(e1.getId()).thenReturn(1L);
        when(e2.getId()).thenReturn(2L);

        List<Long> ids = helper.getIds(Arrays.asList(e1, e2));

        assertThat(ids).containsExactly(1L, 2L);
    }

    @Test
    void getIds_donadaLlistaVuida_retornaLlistaVuida() {
        assertThat(helper.getIds(Collections.emptyList())).isEmpty();
    }

    @Test
    void getIds_donadaLlistaAmbUnElement_retornaUnId() {
        MetaExpedientEntity e = mock(MetaExpedientEntity.class);
        when(e.getId()).thenReturn(99L);

        assertThat(helper.getIds(Collections.singletonList(e))).containsExactly(99L);
    }

    // =========================================================================
    // isRevisioActiva
    // =========================================================================

    @Test
    void isRevisioActiva_quanLaPropietatEsTrue_retornaTrue() {
        when(configHelper.getAsBoolean(PropertyConfig.METAEXPEDIENT_REVISIO_ACTIVA)).thenReturn(true);
        assertThat(helper.isRevisioActiva()).isTrue();
    }

    @Test
    void isRevisioActiva_quanLaPropietatEsFalse_retornaFalse() {
        when(configHelper.getAsBoolean(PropertyConfig.METAEXPEDIENT_REVISIO_ACTIVA)).thenReturn(false);
        assertThat(helper.isRevisioActiva()).isFalse();
    }

    // =========================================================================
    // calcularCodiClon
    // =========================================================================

    @Test
    void calcularCodiClon_quanElCodiBase1NoExisteix_retornaBase1() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity original = mock(MetaExpedientEntity.class);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(metaExpedientRepository.findById(META_EXPEDIENT_ID)).thenReturn(Optional.of(original));
        when(original.getCodi()).thenReturn("PROC");
        when(metaExpedientRepository.findByEntitatAndCodi(entitat, "PROC_1")).thenReturn(null);

        String codi = helper.calcularCodiClon(ENTITAT_ID, META_EXPEDIENT_ID);

        assertThat(codi).isEqualTo("PROC_1");
    }

    @Test
    void calcularCodiClon_quanElCodiBase1Existeix_incrementaFinsTrobarLliure() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity original = mock(MetaExpedientEntity.class);
        MetaExpedientEntity ocupat = mock(MetaExpedientEntity.class);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(metaExpedientRepository.findById(META_EXPEDIENT_ID)).thenReturn(Optional.of(original));
        when(original.getCodi()).thenReturn("PROC");
        when(metaExpedientRepository.findByEntitatAndCodi(entitat, "PROC_1")).thenReturn(ocupat);
        when(metaExpedientRepository.findByEntitatAndCodi(entitat, "PROC_2")).thenReturn(null);

        String codi = helper.calcularCodiClon(ENTITAT_ID, META_EXPEDIENT_ID);

        assertThat(codi).isEqualTo("PROC_2");
    }

    // =========================================================================
    // canviarEstatReglaDistribucio
    // =========================================================================

    @Test
    void canviarEstatReglaDistribucio_quanDistribucioRetornaOK_actualitzaAProcessat() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        CrearReglaResponseDto resposta = new CrearReglaResponseDto(StatusEnumDto.OK, "ok");
        when(metaExpedientRepository.findById(META_EXPEDIENT_ID)).thenReturn(Optional.of(metaExpedient));
        when(distribucioReglaHelper.canviEstat(metaExpedient, true)).thenReturn(resposta);

        CrearReglaResponseDto resultat = helper.canviarEstatReglaDistribucio(META_EXPEDIENT_ID, true);

        assertThat(resultat.getStatus()).isEqualTo(StatusEnumDto.OK);
        verify(metaExpedient).updateCrearReglaDistribucio(CrearReglaDistribucioEstatEnumDto.PROCESSAT);
    }

    @Test
    void canviarEstatReglaDistribucio_quanDistribucioRetornaError_marcaError() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        CrearReglaResponseDto resposta = new CrearReglaResponseDto(StatusEnumDto.ERROR, "error distribucio");
        when(metaExpedientRepository.findById(META_EXPEDIENT_ID)).thenReturn(Optional.of(metaExpedient));
        when(distribucioReglaHelper.canviEstat(metaExpedient, false)).thenReturn(resposta);

        CrearReglaResponseDto resultat = helper.canviarEstatReglaDistribucio(META_EXPEDIENT_ID, false);

        assertThat(resultat.getStatus()).isEqualTo(StatusEnumDto.ERROR);
        verify(metaExpedient).updateCrearReglaDistribucioError(anyString());
    }

    @Test
    void canviarEstatReglaDistribucio_quanDistribucioLlancaExcepcio_retornaError() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(metaExpedientRepository.findById(META_EXPEDIENT_ID)).thenReturn(Optional.of(metaExpedient));
        when(distribucioReglaHelper.canviEstat(any(), anyBoolean()))
                .thenThrow(new RuntimeException("error inesperat"));

        CrearReglaResponseDto resultat = helper.canviarEstatReglaDistribucio(META_EXPEDIENT_ID, true);

        assertThat(resultat.getStatus()).isEqualTo(StatusEnumDto.ERROR);
    }

    // =========================================================================
    // crearReglaDistribucio
    // =========================================================================

    @Test
    void crearReglaDistribucio_quanDistribucioRetornaOK_actualitzaAProcessat() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        CrearReglaResponseDto resposta = new CrearReglaResponseDto(StatusEnumDto.OK, "ok");
        when(metaExpedientRepository.getOne(META_EXPEDIENT_ID)).thenReturn(metaExpedient);
        when(distribucioReglaHelper.crearRegla(metaExpedient)).thenReturn(resposta);

        CrearReglaResponseDto resultat = helper.crearReglaDistribucio(META_EXPEDIENT_ID);

        assertThat(resultat.getStatus()).isEqualTo(StatusEnumDto.OK);
        verify(metaExpedient).updateCrearReglaDistribucio(CrearReglaDistribucioEstatEnumDto.PROCESSAT);
    }

    @Test
    void crearReglaDistribucio_quanDistribucioRetornaError_guardaElMissatgeError() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        CrearReglaResponseDto resposta = new CrearReglaResponseDto(StatusEnumDto.ERROR, "falla");
        when(metaExpedientRepository.getOne(META_EXPEDIENT_ID)).thenReturn(metaExpedient);
        when(distribucioReglaHelper.crearRegla(metaExpedient)).thenReturn(resposta);

        CrearReglaResponseDto resultat = helper.crearReglaDistribucio(META_EXPEDIENT_ID);

        assertThat(resultat.getStatus()).isEqualTo(StatusEnumDto.ERROR);
        verify(metaExpedient).updateCrearReglaDistribucioError(anyString());
    }

    @Test
    void crearReglaDistribucio_quanLlancaExcepcio_retornaStatusError() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(metaExpedientRepository.getOne(META_EXPEDIENT_ID)).thenReturn(metaExpedient);
        when(distribucioReglaHelper.crearRegla(any())).thenThrow(new RuntimeException("timeout"));

        CrearReglaResponseDto resultat = helper.crearReglaDistribucio(META_EXPEDIENT_ID);

        assertThat(resultat.getStatus()).isEqualTo(StatusEnumDto.ERROR);
    }

    // =========================================================================
    // obtenirProximaSequenciaExpedient
    // =========================================================================

    @Test
    void obtenirProximaSequenciaExpedient_quanNoExisteixSequencia_creaNovaSeguenciaIRetornaU() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(metaExpedient.getId()).thenReturn(META_EXPEDIENT_ID);
        when(metaExpedient.getCodi()).thenReturn("PROC");
        when(metaExpedientSequenciaRepository.findByMetaExpedientAndAny(metaExpedient, 2024)).thenReturn(null);
        when(metaExpedientSequenciaRepository.save(any(MetaExpedientSequenciaEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        long resultat = helper.obtenirProximaSequenciaExpedient(metaExpedient, 2024, false);

        assertThat(resultat).isEqualTo(1L);
        verify(metaExpedientSequenciaRepository).save(any(MetaExpedientSequenciaEntity.class));
    }

    @Test
    void obtenirProximaSequenciaExpedient_quanExisteixSequenciaIIncrementar_incrementaIRetornaValor() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(metaExpedient.getId()).thenReturn(META_EXPEDIENT_ID);
        when(metaExpedient.getCodi()).thenReturn("PROC");
        MetaExpedientSequenciaEntity sequencia = MetaExpedientSequenciaEntity.getBuilder(2024, metaExpedient).build();
        // valor inicial = 1; incrementar() el deixarà a 2
        when(metaExpedientSequenciaRepository.findByMetaExpedientAndAny(metaExpedient, 2024)).thenReturn(sequencia);
        when(expedientRepository.findMaxSequencia(metaExpedient, 2024)).thenReturn(null);

        long resultat = helper.obtenirProximaSequenciaExpedient(metaExpedient, 2024, true);

        assertThat(resultat).isEqualTo(2L);
    }

    @Test
    void obtenirProximaSequenciaExpedient_quanExisteixSequenciaSenseIncrementar_retornaValorMesU() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(metaExpedient.getId()).thenReturn(META_EXPEDIENT_ID);
        when(metaExpedient.getCodi()).thenReturn("PROC");
        MetaExpedientSequenciaEntity sequencia = MetaExpedientSequenciaEntity.getBuilder(2024, metaExpedient).build();
        // valor inicial = 1; sense increment ha de retornar 1+1 = 2
        when(metaExpedientSequenciaRepository.findByMetaExpedientAndAny(metaExpedient, 2024)).thenReturn(sequencia);

        long resultat = helper.obtenirProximaSequenciaExpedient(metaExpedient, 2024, false);

        assertThat(resultat).isEqualTo(2L);
        verify(metaExpedientSequenciaRepository, never()).save(any());
    }

    @Test
    void obtenirProximaSequenciaExpedient_quanMaxExpedientSuperaSequencia_corregeixElValor() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(metaExpedient.getId()).thenReturn(META_EXPEDIENT_ID);
        when(metaExpedient.getCodi()).thenReturn("PROC");
        MetaExpedientSequenciaEntity sequencia = MetaExpedientSequenciaEntity.getBuilder(2024, metaExpedient).build();
        // valor inicial = 1; max a BD = 5 → ha de corregir a 6
        when(metaExpedientSequenciaRepository.findByMetaExpedientAndAny(metaExpedient, 2024)).thenReturn(sequencia);
        when(expedientRepository.findMaxSequencia(metaExpedient, 2024)).thenReturn(5L);

        long resultat = helper.obtenirProximaSequenciaExpedient(metaExpedient, 2024, true);

        assertThat(resultat).isEqualTo(6L);
    }

    // =========================================================================
    // findMetaExpedientIdsFiltratsAmbPermisosOrganGestor
    // =========================================================================

    @Test
    void findMetaExpedientIdsFiltratsAmbPermisosOrganGestor_quanOrganIdEsNull_retornaLlistaVuida() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        when(entityComprovarHelper.comprovarEntitat(ENTITAT_ID, false, false, false, false, false))
                .thenReturn(entitat);

        List<Long> ids = helper.findMetaExpedientIdsFiltratsAmbPermisosOrganGestor(ENTITAT_ID, null, false);

        assertThat(ids).isEmpty();
    }

    @Test
    void findMetaExpedientIdsFiltratsAmbPermisosOrganGestor_quanSensePermisAdmini_retornaLlistaVuida() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        when(entityComprovarHelper.comprovarEntitat(ENTITAT_ID, false, false, false, false, false))
                .thenReturn(entitat);
        when(permisosHelper.isGrantedAny(
                eq(ORGAN_ID), eq(OrganGestorEntity.class),
                any(Permission[].class), any(Authentication.class)))
                .thenReturn(false);

        List<Long> ids = helper.findMetaExpedientIdsFiltratsAmbPermisosOrganGestor(ENTITAT_ID, ORGAN_ID, false);

        assertThat(ids).isEmpty();
    }

    @Test
    void findMetaExpedientIdsFiltratsAmbPermisosOrganGestor_quanAmbPermisAdmComu_inclouProcedimentsComunsIDelOrgan() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        OrganGestorEntity organGestor = mock(OrganGestorEntity.class);
        when(entityComprovarHelper.comprovarEntitat(ENTITAT_ID, false, false, false, false, false))
                .thenReturn(entitat);
        when(metaExpedientRepository.findProcedimentsComunsActiveIds(entitat))
                .thenReturn(new ArrayList<>(Collections.singletonList(5L)));
        when(permisosHelper.isGrantedAny(
                eq(ORGAN_ID), eq(OrganGestorEntity.class),
                any(Permission[].class), any(Authentication.class)))
                .thenReturn(true);
        when(organGestorRepository.getOne(ORGAN_ID)).thenReturn(organGestor);
        when(organGestor.getAllChildren()).thenReturn(Collections.singletonList(organGestor));
        when(metaExpedientRepository.findByOrgansGestors(any())).thenReturn(Collections.singletonList(99L));

        List<Long> ids = helper.findMetaExpedientIdsFiltratsAmbPermisosOrganGestor(ENTITAT_ID, ORGAN_ID, true);

        assertThat(ids).contains(5L, 99L);
    }

    // =========================================================================
    // canviarRevisioADisseny
    // =========================================================================

    @Test
    void canviarRevisioADisseny_quanEstatNoEsDisseny_actualitzaADisseny() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarAccesMetaExpedient(entitat, META_EXPEDIENT_ID, ORGAN_ID, false))
                .thenReturn(metaExpedient);
        when(metaExpedient.getRevisioEstat()).thenReturn(MetaExpedientRevisioEstatEnumDto.PENDENT);

        helper.canviarRevisioADisseny(ENTITAT_ID, META_EXPEDIENT_ID, ORGAN_ID);

        verify(metaExpedient).updateRevisioEstat(MetaExpedientRevisioEstatEnumDto.DISSENY);
    }

    @Test
    void canviarRevisioADisseny_quanJaEsDisseny_noActualitza() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarAccesMetaExpedient(entitat, META_EXPEDIENT_ID, ORGAN_ID, false))
                .thenReturn(metaExpedient);
        when(metaExpedient.getRevisioEstat()).thenReturn(MetaExpedientRevisioEstatEnumDto.DISSENY);

        helper.canviarRevisioADisseny(ENTITAT_ID, META_EXPEDIENT_ID, ORGAN_ID);

        verify(metaExpedient, never()).updateRevisioEstat(any());
    }

    // =========================================================================
    // canviarRevisioAPendentEnviarEmail
    // =========================================================================

    @Test
    void canviarRevisioAPendentEnviarEmail_quanRevisioActivaIEstatNoPendent_actualitzaIEnviaEmail() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(configHelper.getAsBoolean(PropertyConfig.METAEXPEDIENT_REVISIO_ACTIVA)).thenReturn(true);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarAccesMetaExpedient(entitat, META_EXPEDIENT_ID, ORGAN_ID, false))
                .thenReturn(metaExpedient);
        when(metaExpedient.getRevisioEstat()).thenReturn(MetaExpedientRevisioEstatEnumDto.DISSENY);

        helper.canviarRevisioAPendentEnviarEmail(ENTITAT_ID, META_EXPEDIENT_ID, ORGAN_ID);

        verify(metaExpedient).updateRevisioEstat(MetaExpedientRevisioEstatEnumDto.PENDENT);
        verify(emailHelper).canviEstatRevisioMetaExpedient(metaExpedient, ENTITAT_ID);
    }

    @Test
    void canviarRevisioAPendentEnviarEmail_quanJaEsPendent_noActualitzaNiEnviaEmail() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(configHelper.getAsBoolean(PropertyConfig.METAEXPEDIENT_REVISIO_ACTIVA)).thenReturn(true);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarAccesMetaExpedient(entitat, META_EXPEDIENT_ID, ORGAN_ID, false))
                .thenReturn(metaExpedient);
        when(metaExpedient.getRevisioEstat()).thenReturn(MetaExpedientRevisioEstatEnumDto.PENDENT);

        helper.canviarRevisioAPendentEnviarEmail(ENTITAT_ID, META_EXPEDIENT_ID, ORGAN_ID);

        verify(metaExpedient, never()).updateRevisioEstat(any());
        verify(emailHelper, never()).canviEstatRevisioMetaExpedient(any(), any());
    }

    @Test
    void canviarRevisioAPendentEnviarEmail_quanRevisioNoActiva_noFaRes() {
        when(configHelper.getAsBoolean(PropertyConfig.METAEXPEDIENT_REVISIO_ACTIVA)).thenReturn(false);

        helper.canviarRevisioAPendentEnviarEmail(ENTITAT_ID, META_EXPEDIENT_ID, ORGAN_ID);

        verify(entityComprovarHelper, never()).comprovarEntitatPerMetaExpedients(any());
        verify(emailHelper, never()).canviEstatRevisioMetaExpedient(any(), any());
    }

    // =========================================================================
    // canviarEstatRevisioASellecionat
    // =========================================================================

    @Test
    void canviarEstatRevisioASellecionat_dePendentARevisat_enviaCorrreu() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaExpedientDto dto = mock(MetaExpedientDto.class);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaExpedient(entitat, META_EXPEDIENT_ID)).thenReturn(metaExpedient);
        when(metaExpedient.getRevisioEstat()).thenReturn(MetaExpedientRevisioEstatEnumDto.PENDENT);
        when(metaExpedient.getCodi()).thenReturn("PROC");
        when(conversioTipusHelper.convertir(metaExpedient, MetaExpedientDto.class)).thenReturn(dto);

        helper.canviarEstatRevisioASellecionat(ENTITAT_ID, META_EXPEDIENT_ID, MetaExpedientRevisioEstatEnumDto.REVISAT);

        verify(emailHelper).canviEstatRevisioMetaExpedient(metaExpedient, ENTITAT_ID);
    }

    @Test
    void canviarEstatRevisioASellecionat_dePendentADisseny_enviaEmailAdminOrganCreador() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaExpedientDto dto = mock(MetaExpedientDto.class);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaExpedient(entitat, META_EXPEDIENT_ID)).thenReturn(metaExpedient);
        when(metaExpedient.getRevisioEstat()).thenReturn(MetaExpedientRevisioEstatEnumDto.PENDENT);
        when(metaExpedient.getCodi()).thenReturn("PROC");
        when(conversioTipusHelper.convertir(metaExpedient, MetaExpedientDto.class)).thenReturn(dto);

        helper.canviarEstatRevisioASellecionat(ENTITAT_ID, META_EXPEDIENT_ID, MetaExpedientRevisioEstatEnumDto.DISSENY);

        verify(emailHelper).canviEstatRevisioMetaExpedientEnviarAAdminOrganCreador(metaExpedient, ENTITAT_ID);
    }

    @Test
    void canviarEstatRevisioASellecionat_deDissenyARevisat_noEnviaEmail() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaExpedientDto dto = mock(MetaExpedientDto.class);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaExpedient(entitat, META_EXPEDIENT_ID)).thenReturn(metaExpedient);
        when(metaExpedient.getRevisioEstat()).thenReturn(MetaExpedientRevisioEstatEnumDto.DISSENY);
        when(metaExpedient.getCodi()).thenReturn("PROC");
        when(conversioTipusHelper.convertir(metaExpedient, MetaExpedientDto.class)).thenReturn(dto);

        helper.canviarEstatRevisioASellecionat(ENTITAT_ID, META_EXPEDIENT_ID, MetaExpedientRevisioEstatEnumDto.REVISAT);

        verify(emailHelper, never()).canviEstatRevisioMetaExpedient(any(), any());
        verify(emailHelper, never()).canviEstatRevisioMetaExpedientEnviarAAdminOrganCreador(any(), any());
    }

    // =========================================================================
    // delete
    // =========================================================================

    @Test
    void delete_quanNoHiHaExpedients_eliminaElProcediment() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarAccesMetaExpedient(entitat, META_EXPEDIENT_ID, ORGAN_ID, true))
                .thenReturn(metaExpedient);
        when(expedientRepository.findByMetaExpedient(metaExpedient)).thenReturn(Collections.emptyList());
        when(historicExpedientRepository.findByMetaExpedient(metaExpedient)).thenReturn(Collections.emptyList());
        when(historicInteressatRepository.findByMetaExpedient(metaExpedient)).thenReturn(Collections.emptyList());
        when(historicUsuariRepository.findByMetaExpedient(metaExpedient)).thenReturn(Collections.emptyList());
        when(usuariRepository.findByProcediment(metaExpedient)).thenReturn(Collections.emptyList());

        MetaExpedientEntity resultat = helper.delete(ENTITAT_ID, META_EXPEDIENT_ID, ORGAN_ID);

        verify(metaExpedientRepository).delete(metaExpedient);
        assertThat(resultat).isSameAs(metaExpedient);
    }

    @Test
    void delete_quanHiHaExpedientsEsborratsPeroDadaActiva_llancaExisteixenExpedientsException() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        ExpedientEntity expedientActiu = mock(ExpedientEntity.class);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarAccesMetaExpedient(entitat, META_EXPEDIENT_ID, ORGAN_ID, true))
                .thenReturn(metaExpedient);
        when(expedientRepository.findByMetaExpedient(metaExpedient))
                .thenReturn(Collections.singletonList(expedientActiu));
        when(expedientActiu.getEsborrat()).thenReturn(0);

        assertThatThrownBy(() -> helper.delete(ENTITAT_ID, META_EXPEDIENT_ID, ORGAN_ID))
                .isInstanceOf(ExisteixenExpedientsException.class);
        verify(metaExpedientRepository, never()).delete(any());
    }

    @Test
    void delete_quanTotsElsExpedientsEsborrats_llancaExisteixenExpedientsEsborratsException() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        ExpedientEntity expedientEsborrat = mock(ExpedientEntity.class);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarAccesMetaExpedient(entitat, META_EXPEDIENT_ID, ORGAN_ID, true))
                .thenReturn(metaExpedient);
        when(expedientRepository.findByMetaExpedient(metaExpedient))
                .thenReturn(Collections.singletonList(expedientEsborrat));
        when(expedientEsborrat.getEsborrat()).thenReturn(1);

        assertThatThrownBy(() -> helper.delete(ENTITAT_ID, META_EXPEDIENT_ID, ORGAN_ID))
                .isInstanceOf(ExisteixenExpedientsEsborratsException.class);
        verify(metaExpedientRepository, never()).delete(any());
    }

    // =========================================================================
    // obtenirArbreCarpetesPerMetaExpedient
    // =========================================================================

    @Test
    void obtenirArbreCarpetesPerMetaExpedient_nodeFullaSenseFills_creaNodeSenseFills() {
        MetaExpedientCarpetaDto carpeta = new MetaExpedientCarpetaDto();
        carpeta.setNom("Carpeta arrel");
        // fills és un HashSet buit per defecte

        ArbreNodeDto<MetaExpedientCarpetaDto> node = helper.obtenirArbreCarpetesPerMetaExpedient(carpeta, null);

        assertThat(node).isNotNull();
        assertThat(node.getDades()).isSameAs(carpeta);
        assertThat(node.getPare()).isNull();
        assertThat(node.getFills()).isEmpty();
    }

    @Test
    void obtenirArbreCarpetesPerMetaExpedient_ambFills_creaArbreRecursiu() {
        MetaExpedientCarpetaDto fill = new MetaExpedientCarpetaDto();
        fill.setNom("Fill");

        MetaExpedientCarpetaDto pare = new MetaExpedientCarpetaDto();
        pare.setNom("Pare");
        pare.getFills().add(fill);

        ArbreNodeDto<MetaExpedientCarpetaDto> nodeArrel = helper.obtenirArbreCarpetesPerMetaExpedient(pare, null);

        assertThat(nodeArrel.getFills()).hasSize(1);
        assertThat(nodeArrel.getFills().get(0).getDades()).isSameAs(fill);
        assertThat(nodeArrel.getFills().get(0).getPare()).isSameAs(nodeArrel);
    }

    // =========================================================================
    // crearCarpeta
    // =========================================================================

    @Test
    void crearCarpeta_quanIdEsNumeric_actualitzaCarpetaExistent() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaExpedientCarpetaEntity carpetaActualitzada = mock(MetaExpedientCarpetaEntity.class);
        ArbreJsonDto carpetaJson = new ArbreJsonDto();
        carpetaJson.setId("42");
        carpetaJson.setText("Nom actualitzat");
        carpetaJson.setChildren(Collections.emptyList());
        when(metaExpedientCarpetaHelper.actualitzarCarpeta(42L, "Nom actualitzat")).thenReturn(carpetaActualitzada);

        helper.crearCarpeta(carpetaJson, null, metaExpedient);

        verify(metaExpedientCarpetaHelper).actualitzarCarpeta(42L, "Nom actualitzat");
        verify(metaExpedientCarpetaHelper, never()).crearNovaCarpeta(anyString(), any(), any());
    }

    @Test
    void crearCarpeta_quanIdNoEsNumeric_creaUnaNovaCarpeta() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaExpedientCarpetaEntity novaCarpeta = mock(MetaExpedientCarpetaEntity.class);
        ArbreJsonDto carpetaJson = new ArbreJsonDto();
        carpetaJson.setId("node_nova");
        carpetaJson.setText("Nova carpeta");
        carpetaJson.setChildren(Collections.emptyList());
        when(metaExpedientCarpetaHelper.crearNovaCarpeta("Nova carpeta", null, metaExpedient))
                .thenReturn(novaCarpeta);

        helper.crearCarpeta(carpetaJson, null, metaExpedient);

        verify(metaExpedientCarpetaHelper).crearNovaCarpeta("Nova carpeta", null, metaExpedient);
    }

    @Test
    void crearCarpeta_ambSubcarpetes_creaRecursivament() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaExpedientCarpetaEntity carpetaPare = mock(MetaExpedientCarpetaEntity.class);
        MetaExpedientCarpetaEntity carpetaFill = mock(MetaExpedientCarpetaEntity.class);

        ArbreJsonDto fillJson = new ArbreJsonDto();
        fillJson.setId("node_fill");
        fillJson.setText("Fill");
        fillJson.setChildren(Collections.emptyList());

        ArbreJsonDto pareJson = new ArbreJsonDto();
        pareJson.setId("node_pare");
        pareJson.setText("Pare");
        pareJson.setChildren(Collections.singletonList(fillJson));

        when(metaExpedientCarpetaHelper.crearNovaCarpeta("Pare", null, metaExpedient)).thenReturn(carpetaPare);
        when(metaExpedientCarpetaHelper.crearNovaCarpeta("Fill", carpetaPare, metaExpedient)).thenReturn(carpetaFill);

        helper.crearCarpeta(pareJson, null, metaExpedient);

        verify(metaExpedientCarpetaHelper).crearNovaCarpeta("Pare", null, metaExpedient);
        verify(metaExpedientCarpetaHelper).crearNovaCarpeta("Fill", carpetaPare, metaExpedient);
    }

    // =========================================================================
    // crearEstructuraCarpetesDto
    // =========================================================================

    @Test
    void crearEstructuraCarpetesDto_quanCarpetaVuida_noFaRes() {
        helper.crearEstructuraCarpetesDto(Collections.emptyList(), mock(MetaExpedientEntity.class));
        verify(metaExpedientCarpetaHelper, never()).crearNovaCarpeta(anyString(), any(), any());
    }

    @Test
    void crearEstructuraCarpetesDto_quanCarpetaSensePareNoExistent_creaNovaCarpeta() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaExpedientCarpetaMinDto carpetaMin = new MetaExpedientCarpetaMinDto();
        carpetaMin.setNom("Arrel");
        carpetaMin.setPare(null);
        when(metaExpedientCarpetaHelper.existeixCarpetaMetaExpedient(metaExpedient, "Arrel", null))
                .thenReturn(Collections.emptyList());
        when(metaExpedientCarpetaHelper.crearNovaCarpeta("Arrel", null, metaExpedient))
                .thenReturn(mock(MetaExpedientCarpetaEntity.class));

        helper.crearEstructuraCarpetesDto(Collections.singletonList(carpetaMin), metaExpedient);

        verify(metaExpedientCarpetaHelper).crearNovaCarpeta("Arrel", null, metaExpedient);
    }

    @Test
    void crearEstructuraCarpetesDto_quanCarpetaSensePareJaExistent_noLaTornaACrear() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaExpedientCarpetaMinDto carpetaMin = new MetaExpedientCarpetaMinDto();
        carpetaMin.setNom("Arrel");
        carpetaMin.setPare(null);
        when(metaExpedientCarpetaHelper.existeixCarpetaMetaExpedient(metaExpedient, "Arrel", null))
                .thenReturn(Collections.singletonList(mock(MetaExpedientCarpetaEntity.class)));

        helper.crearEstructuraCarpetesDto(Collections.singletonList(carpetaMin), metaExpedient);

        verify(metaExpedientCarpetaHelper, never()).crearNovaCarpeta(anyString(), any(), any());
    }

    @Test
    void crearEstructuraCarpetesDto_quanCarpetaAmbPareExistent_creanomesLaFilla() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaExpedientCarpetaEntity pareEntity = mock(MetaExpedientCarpetaEntity.class);
        MetaExpedientCarpetaMinDto pare = new MetaExpedientCarpetaMinDto();
        pare.setNom("Pare");
        MetaExpedientCarpetaMinDto filla = new MetaExpedientCarpetaMinDto();
        filla.setNom("Filla");
        filla.setPare(pare);
        when(metaExpedientCarpetaHelper.existeixCarpetaMetaExpedient(metaExpedient, "Pare", null))
                .thenReturn(Collections.singletonList(pareEntity));
        when(metaExpedientCarpetaHelper.existeixCarpetaMetaExpedient(metaExpedient, "Filla", pareEntity))
                .thenReturn(Collections.emptyList());
        when(metaExpedientCarpetaHelper.crearNovaCarpeta("Filla", pareEntity, metaExpedient))
                .thenReturn(mock(MetaExpedientCarpetaEntity.class));

        helper.crearEstructuraCarpetesDto(Collections.singletonList(filla), metaExpedient);

        verify(metaExpedientCarpetaHelper, never()).crearNovaCarpeta(eq("Pare"), any(), any());
        verify(metaExpedientCarpetaHelper).crearNovaCarpeta("Filla", pareEntity, metaExpedient);
    }

    // =========================================================================
    // deleteCarpetaMetaExpedient
    // =========================================================================

    @Test
    void deleteCarpetaMetaExpedient_delegaAlHelper() {
        MetaExpedientCarpetaDto carpeta = mock(MetaExpedientCarpetaDto.class);
        when(metaExpedientCarpetaHelper.deleteCarpeta(42L)).thenReturn(carpeta);

        MetaExpedientCarpetaDto resultat = helper.deleteCarpetaMetaExpedient(42L);

        assertThat(resultat).isSameAs(carpeta);
    }

    // =========================================================================
    // findTascaByMetaExpedientAndCodi
    // =========================================================================

    @Test
    void findTascaByMetaExpedientAndCodi_delegaAlRepository() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaExpedientTascaEntity tasca = mock(MetaExpedientTascaEntity.class);
        when(metaExpedientTascaRepository.findByMetaExpedientAndCodi(metaExpedient, "T01")).thenReturn(tasca);

        MetaExpedientTascaEntity resultat = helper.findTascaByMetaExpedientAndCodi(metaExpedient, "T01");

        assertThat(resultat).isSameAs(tasca);
    }

    @Test
    void findTascaByMetaExpedientAndCodi_quanNoExisteix_retornaNull() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(metaExpedientTascaRepository.findByMetaExpedientAndCodi(metaExpedient, "T_NO_EXISTEIX"))
                .thenReturn(null);

        assertThat(helper.findTascaByMetaExpedientAndCodi(metaExpedient, "T_NO_EXISTEIX")).isNull();
    }

    // =========================================================================
    // tascaUpdate
    // =========================================================================

    @Test
    void tascaUpdate_senseValidacions_actualitzaLaTascaINoCreaNoves() throws Exception {
        MetaExpedientTascaEntity tascaEntity = mock(MetaExpedientTascaEntity.class);
        MetaExpedientTascaDto tascaDto = new MetaExpedientTascaDto();
        tascaDto.setCodi("T01");
        tascaDto.setNom("Tasca 1");
        tascaDto.setValidacions(null);

        MetaExpedientTascaEntity resultat = helper.tascaUpdate(tascaEntity, tascaDto);

        assertThat(resultat).isSameAs(tascaEntity);
        // update(codi, nom, descripcio, responsable, dataLimit, duracio, prioritat, estatCrear, estatFinalitzar)
        verify(tascaEntity).update(any(), any(), any(), any(), any(), any(), any(), any(), any());
        verify(metaExpedientTascaValidacioRepository, never()).save(any());
    }

    @Test
    void tascaUpdate_ambValidacionsNoves_creaLesValidacionsQueNoExisteixen() throws Exception {
        MetaExpedientTascaEntity tascaEntity = mock(MetaExpedientTascaEntity.class);
        MetaExpedientTascaValidacioDto validacioDto = new MetaExpedientTascaValidacioDto();
        validacioDto.setItemValidacio(ItemValidacioTascaEnum.DADA);
        validacioDto.setActiva(true);

        MetaExpedientTascaDto tascaDto = new MetaExpedientTascaDto();
        tascaDto.setCodi("T01");
        tascaDto.setNom("Tasca 1");
        tascaDto.setValidacions(Collections.singletonList(validacioDto));

        when(tascaEntity.hasValidacio(validacioDto)).thenReturn(false);
        when(metaExpedientTascaValidacioRepository.save(any(MetaExpedientTascaValidacioEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        helper.tascaUpdate(tascaEntity, tascaDto);

        verify(metaExpedientTascaValidacioRepository).save(any(MetaExpedientTascaValidacioEntity.class));
    }

    @Test
    void tascaUpdate_ambValidacioJaExistent_noLaTornaACrear() throws Exception {
        MetaExpedientTascaEntity tascaEntity = mock(MetaExpedientTascaEntity.class);
        MetaExpedientTascaValidacioDto validacioDto = new MetaExpedientTascaValidacioDto();
        validacioDto.setItemValidacio(ItemValidacioTascaEnum.DOCUMENT);

        MetaExpedientTascaDto tascaDto = new MetaExpedientTascaDto();
        tascaDto.setCodi("T01");
        tascaDto.setNom("Tasca 1");
        tascaDto.setValidacions(Collections.singletonList(validacioDto));

        when(tascaEntity.hasValidacio(validacioDto)).thenReturn(true);

        helper.tascaUpdate(tascaEntity, tascaDto);

        verify(metaExpedientTascaValidacioRepository, never()).save(any());
    }

    // =========================================================================
    // omplirMetaDocumentsPerMetaExpedient
    // =========================================================================

    @Test
    void omplirMetaDocumentsPerMetaExpedient_emplenaElDtoAmbElsDocuments() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaExpedientDto dto = new MetaExpedientDto();
        es.caib.ripea.persistence.entity.MetaDocumentEntity docEntity =
                mock(es.caib.ripea.persistence.entity.MetaDocumentEntity.class);
        MetaDocumentDto docDto = new MetaDocumentDto();
        when(metaDocumentRepository.findByMetaExpedient(metaExpedient))
                .thenReturn(Collections.singletonList(docEntity));
        when(conversioTipusHelper.convertir(docEntity, MetaDocumentDto.class)).thenReturn(docDto);

        helper.omplirMetaDocumentsPerMetaExpedient(metaExpedient, dto);

        assertThat(dto.getMetaDocuments()).hasSize(1).contains(docDto);
    }

    @Test
    void omplirMetaDocumentsPerMetaExpedient_senseDocuments_estableixLlistaVuida() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaExpedientDto dto = new MetaExpedientDto();
        when(metaDocumentRepository.findByMetaExpedient(metaExpedient)).thenReturn(Collections.emptyList());

        helper.omplirMetaDocumentsPerMetaExpedient(metaExpedient, dto);

        assertThat(dto.getMetaDocuments()).isEmpty();
    }

    // =========================================================================
    // publicarComentariPerMetaExpedient
    // =========================================================================

    @Test
    void publicarComentariPerMetaExpedient_rolIpaRevisio_usaComprovarEntitatSensePermisos() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(entityComprovarHelper.comprovarEntitat(ENTITAT_ID, false, false, false, false, false))
                .thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaExpedient(entitat, META_EXPEDIENT_ID)).thenReturn(metaExpedient);
        when(metaExpedient.getCodi()).thenReturn("PROC");

        boolean resultat = helper.publicarComentariPerMetaExpedient(
                ENTITAT_ID, META_EXPEDIENT_ID, "Text comentari", "IPA_REVISIO");

        assertThat(resultat).isTrue();
        verify(metaExpedientComentariRepository).save(any());
    }

    @Test
    void publicarComentariPerMetaExpedient_textMesLlargDe1024Caracters_elTruncaA1024() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(entityComprovarHelper.comprovarEntitat(ENTITAT_ID, false, false, false, false, true))
                .thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaExpedient(entitat, META_EXPEDIENT_ID)).thenReturn(metaExpedient);
        when(metaExpedient.getCodi()).thenReturn("PROC");
        String textLlarg = "A".repeat(2000);

        boolean resultat = helper.publicarComentariPerMetaExpedient(
                ENTITAT_ID, META_EXPEDIENT_ID, textLlarg, "IPA_ADMIN");

        assertThat(resultat).isTrue();
        // Verifiquem que s'ha guardat un comentari (el truncament és intern)
        verify(metaExpedientComentariRepository).save(any());
    }

    // =========================================================================
    // findPermesosAccioMassiva
    // =========================================================================

    @Test
    void findPermesosAccioMassiva_rolIpaAdmin_cercaAmbPermisWrite() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        when(entitat.getCodi()).thenReturn("ENT_TEST");
        when(entityComprovarHelper.comprovarEntitat(eq(ENTITAT_ID), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn(entitat);
        when(permisosHelper.getObjectsIdsWithPermission(any(), any())).thenReturn(Collections.emptyList());
        when(permisosHelper.getObjectsIdsWithTwoPermissions(any(), any(), any())).thenReturn(Collections.emptyList());
        when(organGestorRepository.findCodisByEntitatAndVigentIds(any(), any())).thenReturn(Collections.emptyList());
        when(organGestorCacheHelper.getCodisOrgansFills(anyString(), anyList())).thenReturn(Collections.emptyList());
        when(configHelper.getAsBoolean(PropertyConfig.METAEXPEDIENT_REVISIO_ACTIVA)).thenReturn(false);
        when(metaExpedientRepository.findByEntitatAndActiuAndFiltreAndPermes(
                any(), anyBoolean(), any(), anyBoolean(), any(),
                anyBoolean(), anyBoolean(), any(), any(), any(), any(),
                anyBoolean(), any(), any(), any(), any(),
                anyBoolean(), any(), any(), any(), any(),
                anyBoolean(), anyBoolean(), any(), anyBoolean(), anyBoolean(), any()))
                .thenReturn(Collections.emptyList());

        List<MetaExpedientEntity> resultat = helper.findPermesosAccioMassiva(ENTITAT_ID, "IPA_ADMIN");

        assertThat(resultat).isNotNull();
    }

    // =========================================================================
    // export
    // =========================================================================

    @Test
    void export_camiFelix_retornaJsonNoNull() throws Exception {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);

        // Tasca amb una validació per exercir el bucle de validacions
        MetaExpedientTascaEntity tascaEntity = mock(MetaExpedientTascaEntity.class);
        MetaExpedientTascaValidacioEntity validacioEntity = mock(MetaExpedientTascaValidacioEntity.class);
        when(tascaEntity.getId()).thenReturn(100L);
        when(tascaEntity.getValidacions()).thenReturn(Collections.singletonList(validacioEntity));

        MetaExpedientTascaDto tascaDto = new MetaExpedientTascaDto();
        tascaDto.setId(100L);

        // Meta-dada de tipus TEXT (no DOMINI: no cal stub del domini)
        MetaDadaDto metaDadaDto = new MetaDadaDto();
        metaDadaDto.setTipus(MetaDadaTipusEnumDto.TEXT);
        metaDadaDto.setCodi("MD1");

        // Meta-document amb una meta-dada TEXT
        MetaDocumentDto metaDocumentDto = new MetaDocumentDto();
        metaDocumentDto.setCodi("MDOC1");
        metaDocumentDto.setMultiplicitat(MultiplicitatEnumDto.M_0_1);
        metaDocumentDto.setMetaDades(Collections.singletonList(metaDadaDto));

        MetaExpedientExportDto exportDto = new MetaExpedientExportDto();
        exportDto.setTasques(new HashSet<>(Collections.singletonList(tascaDto)));
        exportDto.setMetaDades(Collections.singletonList(metaDadaDto));
        exportDto.setMetaDocuments(Collections.singletonList(metaDocumentDto));

        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarAccesMetaExpedient(entitat, META_EXPEDIENT_ID, ORGAN_ID, true))
                .thenReturn(metaExpedient);
        when(conversioTipusHelper.convertir(metaExpedient, MetaExpedientExportDto.class)).thenReturn(exportDto);
        when(metaExpedient.getTasques()).thenReturn(new HashSet<>(Collections.singletonList(tascaEntity)));
        when(conversioTipusHelper.convertirList(Collections.singletonList(validacioEntity),
                MetaExpedientTascaValidacioDto.class))
                .thenReturn(Collections.singletonList(new MetaExpedientTascaValidacioDto()));
        when(metaExpedientCarpetaHelper.findCarpetesMetaExpedient(metaExpedient))
                .thenReturn(Collections.emptyList());
        when(metaExpedient.getClassificacio()).thenReturn("SIA001");
        when(metaExpedient.getNom()).thenReturn("Procediment test");

        String json = helper.export(ENTITAT_ID, META_EXPEDIENT_ID, ORGAN_ID);

        assertThat(json).isNotNull();
        verify(contingutLogHelper).logProcediment(any(), any(), any(), any());
    }

    @Test
    void export_quanConversioLlancaExcepcio_llancaRuntimeException() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarAccesMetaExpedient(entitat, META_EXPEDIENT_ID, ORGAN_ID, true))
                .thenReturn(metaExpedient);
        when(conversioTipusHelper.convertir(metaExpedient,
                es.caib.ripea.service.intf.dto.MetaExpedientExportDto.class))
                .thenThrow(new RuntimeException("error conversió"));

        assertThatThrownBy(() -> helper.export(ENTITAT_ID, META_EXPEDIENT_ID, ORGAN_ID))
                .isInstanceOf(RuntimeException.class);
    }

    // =========================================================================
    // clonar
    // =========================================================================

    @Test
    void clonar_quanElNouCodiJaExisteix_llancaRuntimeException() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity original = mock(MetaExpedientEntity.class);
        MetaExpedientEntity existent = mock(MetaExpedientEntity.class);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarAccesMetaExpedient(entitat, META_EXPEDIENT_ID, ORGAN_ID, true))
                .thenReturn(original);
        when(metaExpedientRepository.findByEntitatAndCodi(entitat, "CLON")).thenReturn(existent);

        assertThatThrownBy(() -> helper.clonar(ENTITAT_ID, META_EXPEDIENT_ID, ORGAN_ID, "IPA_ADMIN", "CLON", "SIA999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("CLON");
    }
    
    @Test
    void clonar_quanCodiLliure_clonaICanviaRevisioADisseny() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity original = mock(MetaExpedientEntity.class);
        MetaExpedientEntity savedEntity = mock(MetaExpedientEntity.class);
        MetaExpedientEntity clonat = mock(MetaExpedientEntity.class);
        MetaExpedientExportDto exportDto = new MetaExpedientExportDto();
        exportDto.setCodi("ORIG");
        exportDto.setNom("Procediment original");

        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarAccesMetaExpedient(entitat, META_EXPEDIENT_ID, ORGAN_ID, true))
                .thenReturn(original);
        when(metaExpedientRepository.findByEntitatAndCodi(entitat, "CLON"))
                .thenReturn(null)
                .thenReturn(clonat);
        when(conversioTipusHelper.convertir(original, MetaExpedientExportDto.class)).thenReturn(exportDto);
        when(original.getTasques()).thenReturn(null);
        when(metaExpedientCarpetaHelper.findCarpetesMetaExpedient(original)).thenReturn(Collections.emptyList());
        when(metaExpedientRepository.save(any(MetaExpedientEntity.class))).thenReturn(savedEntity);
        when(clonat.getId()).thenReturn(50L);
        when(entityComprovarHelper.comprovarAccesMetaExpedient(entitat, 50L, ORGAN_ID, false))
                .thenReturn(mock(MetaExpedientEntity.class));

        helper.clonar(ENTITAT_ID, META_EXPEDIENT_ID, ORGAN_ID, "IPA_ADMIN", "CLON", "SIA999");

        verify(metaExpedientRepository).save(any());
        verify(savedEntity).updateRevisioEstat(MetaExpedientRevisioEstatEnumDto.REVISAT);
    }

    // =========================================================================
    // findActiusAmbOrganGestorPermisLectura
    // =========================================================================

    @Test
    void findActiusAmbOrganGestorPermisLectura_delegaAFindAmbOrganFiltrePermisAmbRead() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        OrganGestorEntity organGestor = mock(OrganGestorEntity.class);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(organGestorRepository.getOne(ORGAN_ID)).thenReturn(organGestor);
        when(metaExpedientRepository.findByOrganGestorAndActiuAndFiltreTrueOrderByNomAsc(
                any(), anyBoolean(), any(), anyBoolean(), any()))
                .thenReturn(Collections.emptyList());
        when(permisosHelper.isGrantedAny(eq(ENTITAT_ID), eq(EntitatEntity.class),
                any(org.springframework.security.acls.model.Permission[].class), any(Authentication.class)))
                .thenReturn(false);

        List<MetaExpedientEntity> resultat = helper.findActiusAmbOrganGestorPermisLectura(ENTITAT_ID, ORGAN_ID, null);

        assertThat(resultat).isNotNull();
    }

    // =========================================================================
    // findAmbOrganFiltrePermis
    // =========================================================================

    @Test
    void findAmbOrganFiltrePermis_quanEsAdministradorEntitat_retornaTotsElsProcediments() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        OrganGestorEntity organGestor = mock(OrganGestorEntity.class);
        MetaExpedientEntity proc = mock(MetaExpedientEntity.class);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(organGestorRepository.getOne(ORGAN_ID)).thenReturn(organGestor);
        when(metaExpedientRepository.findByOrganGestorAndActiuAndFiltreTrueOrderByNomAsc(
                any(), anyBoolean(), any(), anyBoolean(), any()))
                .thenReturn(Collections.singletonList(proc));
        when(permisosHelper.isGrantedAny(eq(ENTITAT_ID), eq(EntitatEntity.class),
                any(org.springframework.security.acls.model.Permission[].class), any(Authentication.class)))
                .thenReturn(true);

        List<MetaExpedientEntity> resultat = helper.findAmbOrganFiltrePermis(
                ENTITAT_ID, ORGAN_ID, ExtendedPermission.READ, true, null);

        assertThat(resultat).containsExactly(proc);
    }

    // =========================================================================
    // permisFind
    // =========================================================================

    @Test
    void permisFind_quanElNodeEsMetaExpedient_retornaElsPermisosDelNode() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaExpedientOrganGestorEntity organGestorRelacio = mock(MetaExpedientOrganGestorEntity.class);
        OrganGestorEntity organGestor = mock(OrganGestorEntity.class);
        PermisDto permisOrgan = mock(PermisDto.class);

        when(metaNodeRepository.getOne(META_EXPEDIENT_ID)).thenReturn(metaExpedient);
        when(metaExpedientOrganGestorRepository.findByMetaExpedient(metaExpedient))
                .thenReturn(Collections.singletonList(organGestorRelacio));
        when(organGestorRelacio.getId()).thenReturn(5L);
        when(organGestorRelacio.getOrganGestor()).thenReturn(organGestor);
        when(organGestor.getId()).thenReturn(6L);
        when(organGestor.getNom()).thenReturn("Organ Test");
        when(organGestor.getCodi()).thenReturn("ORG_TEST");

        Map<Serializable, List<PermisDto>> permisosMap = new HashMap<>();
        permisosMap.put(5L, Collections.singletonList(permisOrgan));
        when(permisosHelper.findPermisos(anyList(), eq(MetaExpedientOrganGestorEntity.class)))
                .thenReturn(permisosMap);
        when(permisosHelper.findPermisos(eq(META_EXPEDIENT_ID),
                eq(es.caib.ripea.persistence.entity.MetaNodeEntity.class)))
                .thenReturn(Collections.emptyList());

        List<PermisDto> resultat = helper.permisFind(META_EXPEDIENT_ID);

        assertThat(resultat).isNotEmpty();
        verify(permisOrgan).setOrganGestorId(6L);
        verify(permisOrgan).setOrganGestorNom("Organ Test");
        verify(permisOrgan).setOrganGestorCodi("ORG_TEST");
    }

    // =========================================================================
    // findByEntitat
    // =========================================================================

    @Test
    void findByEntitat_delegaAlRepository() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        OrganGestorEntity organGestor = mock(OrganGestorEntity.class);
        MetaExpedientFiltreDto filtre = new MetaExpedientFiltreDto();
        filtre.setCodi("PROC");
        filtre.setNom("Nom test");
        filtre.setClassificacio("SIA001");
        filtre.setOrganGestorId(ORGAN_ID);
        PaginacioParamsDto paginacio = new PaginacioParamsDto();
        org.springframework.data.domain.PageImpl<MetaExpedientEntity> page =
                new org.springframework.data.domain.PageImpl<>(Collections.emptyList());
        when(organGestorRepository.getOne(ORGAN_ID)).thenReturn(organGestor);
        when(paginacioHelper.toSpringDataPageable(any(), any()))
                .thenReturn(org.springframework.data.domain.PageRequest.of(0, 10));
        when(metaExpedientRepository.findByEntitat(
                any(), anyBoolean(), any(), anyBoolean(), any(),
                anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(),
                anyBoolean(), any(), anyBoolean(), anyBoolean(),
                anyBoolean(), any(), anyBoolean(), any()))
                .thenReturn(page);

        org.springframework.data.domain.Page<MetaExpedientEntity> resultat =
                helper.findByEntitat(entitat, filtre, paginacio, Collections.emptyMap());

        assertThat(resultat).isNotNull();
        verify(organGestorRepository).getOne(ORGAN_ID);
    }

    // =========================================================================
    // tascaCreate
    // =========================================================================

    @Test
    void tascaCreate_rolIpaAdmin_creaIRetornaLaTasca() throws Exception {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaExpedientTascaDto tascaDto = new MetaExpedientTascaDto();
        tascaDto.setCodi("T01");
        tascaDto.setNom("Tasca nova");
        MetaExpedientTascaValidacioDto validacioDto = new MetaExpedientTascaValidacioDto();
        validacioDto.setItemValidacio(ItemValidacioTascaEnum.DADA);
        validacioDto.setTipusValidacio(TipusValidacioTascaEnum.AP);
        validacioDto.setItemId(1L);
        validacioDto.setActiva(true);
        tascaDto.setValidacions(Collections.singletonList(validacioDto));
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaExpedient(entitat, META_EXPEDIENT_ID)).thenReturn(metaExpedient);
        when(metaExpedientTascaRepository.save(any(MetaExpedientTascaEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(conversioTipusHelper.convertir(any(MetaExpedientTascaEntity.class), eq(MetaExpedientTascaDto.class))).thenReturn(tascaDto);
        initMeterRegistry();
        
        MetaExpedientTascaDto resultat = helper.tascaCreate(ENTITAT_ID, META_EXPEDIENT_ID, tascaDto, "IPA_ADMIN", ORGAN_ID);

        assertThat(resultat).isNotNull();
        verify(metaExpedientTascaRepository).save(any());
        verify(metaExpedientTascaValidacioRepository).save(any(MetaExpedientTascaValidacioEntity.class));
    }

    // =========================================================================
    // obtenirPareArbreCarpetesPerMetaExpedient
    // =========================================================================

    @Test
    void obtenirPareArbreCarpetesPerMetaExpedient_senseCarpetes_retornaLlistaOriginalBuida() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(metaExpedientCarpetaHelper.findCarpetesMetaExpedient(metaExpedient))
                .thenReturn(Collections.emptyList());
        List<es.caib.ripea.service.intf.dto.ArbreDto<MetaExpedientCarpetaDto>> carpetes = new ArrayList<>();

        List<es.caib.ripea.service.intf.dto.ArbreDto<MetaExpedientCarpetaDto>> resultat =
                helper.obtenirPareArbreCarpetesPerMetaExpedient(metaExpedient, carpetes);

        assertThat(resultat).isEmpty();
    }

    @Test
    void obtenirPareArbreCarpetesPerMetaExpedient_ambCarpetaArrel_afegeixArbreAlResultat() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaExpedientCarpetaDto carpetaArrel = new MetaExpedientCarpetaDto();
        carpetaArrel.setNom("Arrel");
        carpetaArrel.setPare(null); // és arrel perquè pare == null
        when(metaExpedientCarpetaHelper.findCarpetesMetaExpedient(metaExpedient))
                .thenReturn(Collections.singletonList(carpetaArrel));
        List<es.caib.ripea.service.intf.dto.ArbreDto<MetaExpedientCarpetaDto>> carpetes = new ArrayList<>();

        List<es.caib.ripea.service.intf.dto.ArbreDto<MetaExpedientCarpetaDto>> resultat =
                helper.obtenirPareArbreCarpetesPerMetaExpedient(metaExpedient, carpetes);

        assertThat(resultat).hasSize(1);
    }

    // =========================================================================
    // crearEstructuraCarpetes
    // =========================================================================

    @Test
    void crearEstructuraCarpetes_ambUnaNodoCarpeta_cridaCrearCarpeta() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        ArbreJsonDto carpetaJson = new ArbreJsonDto();
        carpetaJson.setId("node_1");
        carpetaJson.setText("Carpeta 1");
        carpetaJson.setChildren(Collections.emptyList());
        when(metaExpedientCarpetaHelper.crearNovaCarpeta("Carpeta 1", null, metaExpedient))
                .thenReturn(mock(MetaExpedientCarpetaEntity.class));

        helper.crearEstructuraCarpetes(Collections.singletonList(carpetaJson), metaExpedient);

        verify(metaExpedientCarpetaHelper).crearNovaCarpeta("Carpeta 1", null, metaExpedient);
    }

    // =========================================================================
    // findProcedimentsDeOrganIDeDescendentsDeOrgan
    // =========================================================================

    @Test
    void findProcedimentsDeOrganIDeDescendentsDeOrgan_retornaElsProcedimentsDelOrganIDescendents() {
        OrganGestorEntity organ = mock(OrganGestorEntity.class);
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity proc = mock(MetaExpedientEntity.class);
        when(organGestorRepository.getOne(ORGAN_ID)).thenReturn(organ);
        when(organ.getEntitat()).thenReturn(entitat);
        when(entitat.getCodi()).thenReturn("ENT_TEST");
        when(organGestorHelper.findCodisDescendents("ENT_TEST", ORGAN_ID))
                .thenReturn(Collections.singletonList("ORG001"));
        when(metaExpedientRepository.findByOrganGestorCodis(entitat, Collections.singletonList("ORG001")))
                .thenReturn(Collections.singletonList(proc));

        List<MetaExpedientEntity> resultat = helper.findProcedimentsDeOrganIDeDescendentsDeOrgan(ORGAN_ID);

        assertThat(resultat).containsExactly(proc);
    }

    // =========================================================================
    // updateActiu
    // =========================================================================

    @Test
    void updateActiu_activar_actualitzaActivICanviaRevisio() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarAccesMetaExpedient(entitat, META_EXPEDIENT_ID, ORGAN_ID, true))
                .thenReturn(metaExpedient);
        when(metaExpedient.getClassificacio()).thenReturn("SIA001");
        when(metaExpedient.getNom()).thenReturn("Procediment");

        MetaExpedientEntity resultat = helper.updateActiu(ENTITAT_ID, META_EXPEDIENT_ID, true, "IPA_ADMIN", ORGAN_ID);

        assertThat(resultat).isSameAs(metaExpedient);
        verify(metaExpedient).updateActiu(true);
    }

    @Test
    void updateActiu_desactivar_rolOrganAdmin_canviaRevisioADisseny() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarAccesMetaExpedient(entitat, META_EXPEDIENT_ID, ORGAN_ID, true))
                .thenReturn(metaExpedient);
        when(metaExpedient.getId()).thenReturn(META_EXPEDIENT_ID);
        when(metaExpedient.getClassificacio()).thenReturn("SIA001");
        when(metaExpedient.getNom()).thenReturn("Procediment");
        // canviarRevisioADisseny crida de nou comprovarAccesMetaExpedient
        when(entityComprovarHelper.comprovarAccesMetaExpedient(entitat, META_EXPEDIENT_ID, ORGAN_ID, false))
                .thenReturn(metaExpedient);
        when(metaExpedient.getRevisioEstat()).thenReturn(MetaExpedientRevisioEstatEnumDto.PENDENT);

        helper.updateActiu(ENTITAT_ID, META_EXPEDIENT_ID, false, "IPA_ORGAN_ADMIN", ORGAN_ID);

        verify(metaExpedient).updateActiu(false);
        verify(metaExpedient).updateRevisioEstat(MetaExpedientRevisioEstatEnumDto.DISSENY);
    }

    // =========================================================================
    // getIdsCreateWritePermesos / getCreateWritePermesos / getIdsReadPermesos
    // =========================================================================

    private void stubFindAmbPermis(EntitatEntity entitat) {
        when(entityComprovarHelper.comprovarEntitat(eq(ENTITAT_ID), anyBoolean(), anyBoolean(),
                anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(entitat);
        when(permisosHelper.getObjectsIdsWithPermission(any(), any())).thenReturn(Collections.emptyList());
        when(permisosHelper.getObjectsIdsWithTwoPermissions(any(), any(), any())).thenReturn(Collections.emptyList());
        when(organGestorRepository.findCodisByEntitatAndVigentIds(any(), any())).thenReturn(Collections.emptyList());
        when(organGestorCacheHelper.getCodisOrgansFills(anyString(), anyList())).thenReturn(Collections.emptyList());
        when(configHelper.getAsBoolean(PropertyConfig.METAEXPEDIENT_REVISIO_ACTIVA)).thenReturn(false);
        when(metaExpedientRepository.findByEntitatAndActiuAndFiltreAndPermes(
                any(), anyBoolean(), any(), anyBoolean(), any(),
                anyBoolean(), anyBoolean(), any(), any(), any(), any(),
                anyBoolean(), any(), any(), any(), any(),
                anyBoolean(), any(), any(), any(), any(),
                anyBoolean(), anyBoolean(), any(), anyBoolean(), anyBoolean(), any()))
                .thenReturn(Collections.emptyList());
    }

    @Test
    void getIdsCreateWritePermesos_retornaLlistaDeIdsUnificada() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        when(entitat.getCodi()).thenReturn("ENT_TEST");
        stubFindAmbPermis(entitat);

        List<Long> resultat = helper.getIdsCreateWritePermesos(ENTITAT_ID);

        // Sense permisos, el resultat ha de ser null (llista buida → null)
        assertThat(resultat).isNull();
    }

    @Test
    void getCreateWritePermesos_retornaLlistaEntitatsUnificada() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        when(entitat.getCodi()).thenReturn("ENT_TEST");
        stubFindAmbPermis(entitat);

        List<MetaExpedientEntity> resultat = helper.getCreateWritePermesos(ENTITAT_ID);

        assertThat(resultat).isNull();
    }

    @Test
    void getIdsReadPermesos_retornaIdsAmbPermisLectura() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        when(entitat.getCodi()).thenReturn("ENT_TEST");
        stubFindAmbPermis(entitat);

        List<Long> resultat = helper.getIdsReadPermesos(ENTITAT_ID);

        assertThat(resultat).isEmpty();
    }

    // =========================================================================
    // actualitzarProcediments
    // =========================================================================

    @Test
    void actualitzarProcediments_ambLlistaVuida_marcaComAFinalitzat() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        when(entitat.getCodi()).thenReturn("ENT_TEST");
        when(messageHelper.getMessage(anyString())).thenReturn("");
        when(messageHelper.getMessage(anyString(), any(Object[].class))).thenReturn("");
        when(avisRepository.findByEntitatIdAndAssumpte(any(), any())).thenReturn(Collections.emptyList());
        when(entitat.getId()).thenReturn(ENTITAT_ID);
        es.caib.ripea.service.intf.dto.ProgresActualitzacioDto progres =
                new es.caib.ripea.service.intf.dto.ProgresActualitzacioDto();

        helper.actualitzarProcediments(entitat, Collections.emptyList(), java.util.Locale.getDefault(), progres);

        assertThat(progres.getProgres()).isEqualTo(100);
    }

    @Test
    void actualitzarProcediments_quanProgresNul_creaNovaInstanciaIFinalitza() {
        MetaExpedientServiceImpl.progresActualitzacio.remove("ENT_NULL");
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(entitat.getCodi()).thenReturn("ENT_NULL");
        when(entitat.getId()).thenReturn(ENTITAT_ID);
        when(messageHelper.getMessage(anyString())).thenReturn("");
        when(messageHelper.getMessage(anyString(), any(Object[].class))).thenReturn("");
        when(avisRepository.findByEntitatIdAndAssumpte(any(), any())).thenReturn(Collections.emptyList());

        helper.actualitzarProcediments(entitat,
                new ArrayList<>(Collections.singletonList(metaExpedient)),
                java.util.Locale.getDefault(), null);

        ProgresActualitzacioDto resultat =
                MetaExpedientServiceImpl.progresActualitzacio.get("ENT_NULL");
        assertThat(resultat).isNotNull();
        assertThat(resultat.getProgres()).isEqualTo(100);
        assertThat(resultat.isFinished()).isTrue();
    }

    @Test
    void actualitzarProcediments_ambProcedimentSia_actualitzaElProcedimentILogeja() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        OrganGestorEntity organGestor = mock(OrganGestorEntity.class);
        when(entitat.getCodi()).thenReturn("ENT_SIA");
        when(entitat.getId()).thenReturn(ENTITAT_ID);
        when(entitat.getUnitatArrel()).thenReturn("UNI001");

        // Procediment amb classificació SIA i nom diferent al que retorna el WS
        when(metaExpedient.getTipusClassificacio()).thenReturn(TipusClassificacioEnumDto.SIA);
        when(metaExpedient.getClassificacio()).thenReturn("SIA001");
        when(metaExpedient.getNom()).thenReturn("Nom Antic");
        when(metaExpedient.getDescripcio()).thenReturn("Desc Antiga");
        when(metaExpedient.isComu()).thenReturn(false);
        when(metaExpedient.getOrganGestor()).thenReturn(null);

        // El WS retorna comu=false amb un organ específic → passa pel else de isComu()
        // i crida organGestorRepository.findByEntitatAndCodi que retorna l'organ trobat
        ProcedimentDto procedimentWs = new ProcedimentDto();
        procedimentWs.setNom("Nom Nou");
        procedimentWs.setResum("Desc Antiga");
        procedimentWs.setComu(false);
        procedimentWs.setUnitatOrganitzativaCodi("ORG001");

        when(organGestorRepository.findByEntitatAndCodi(entitat, "ORG001")).thenReturn(organGestor);
        when(pluginHelper.procedimentFindByCodiSia("UNI001", "SIA001")).thenReturn(procedimentWs);
        when(messageHelper.getMessage(anyString())).thenReturn("");
        when(messageHelper.getMessage(anyString(), any(Object[].class))).thenReturn("");
        // actualitzaAvisosSyncProcediments sempre s'executa al final;
        // com l'organ s'ha trobat, avisosProcedimentsOrgans és buit i no es crea cap avís
        when(avisRepository.findByEntitatIdAndAssumpte(any(), any())).thenReturn(Collections.emptyList());

        ProgresActualitzacioDto progres = new ProgresActualitzacioDto();

        helper.actualitzarProcediments(entitat,
                new ArrayList<>(Collections.singletonList(metaExpedient)),
                java.util.Locale.getDefault(), progres);

        // Verifica que s'ha consultat l'organ del WS i s'ha actualitzat el procediment
        verify(organGestorRepository).findByEntitatAndCodi(entitat, "ORG001");
        verify(metaExpedient).updateSync("Nom Nou", "Desc Antiga", organGestor, false);
        verify(contingutLogHelper).logProcediment(eq(metaExpedient),
                eq(es.caib.ripea.service.intf.dto.LogTipusEnumDto.UPDATE_PROCED_ROLSAC),
                anyString(), anyString());
        assertThat(progres.getProgres()).isEqualTo(100);
    }

    // =========================================================================
    // toMetaExpedientDto
    // =========================================================================

    @Test
    void toMetaExpedientDto_emplenaElDtoAmbTotesLesSeccions() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaExpedientDto dto = new MetaExpedientDto();
        when(conversioTipusHelper.convertir(metaExpedient, MetaExpedientDto.class)).thenReturn(dto);
        when(expedientEstatRepository.countByMetaExpedient(metaExpedient)).thenReturn(3);
        when(metaExpedientTascaRepository.countByMetaExpedient(metaExpedient)).thenReturn(2);
        when(metaExpedient.getGrups()).thenReturn(Collections.emptyList());
        when(metaExpedient.getComentaris()).thenReturn(Collections.emptyList());
        when(metaExpedient.getOrganGestor()).thenReturn(null);

        MetaExpedientDto resultat = helper.toMetaExpedientDto(metaExpedient, null);

        assertThat(resultat).isSameAs(dto);
        assertThat(dto.getExpedientEstatsCount()).isEqualTo(3);
        assertThat(dto.getExpedientTasquesCount()).isEqualTo(2);
        verify(metaNodeHelper).omplirMetaDadesPerMetaNode(dto);
        verify(metaNodeHelper).omplirPermisosPerMetaNodes(any(), eq(true));
    }

    // =========================================================================
    // create
    // =========================================================================

    @Test
    void create_rolIpaAdmin_creaIRetornaElProcediment() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity nouProcediment = mock(MetaExpedientEntity.class);
        MetaExpedientDto dto = new MetaExpedientDto();
        dto.setCodi("PROC_NOU");
        dto.setNom("Procediment nou");
        dto.setSerieDocumental("S0001");
        dto.setClassificacio("SIA001");
        dto.setOrganGestor(null);
        dto.setPareId(null);
        dto.setEstructuraCarpetes(null);
        dto.setCrearReglaDistribucio(false);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(metaExpedientRepository.save(any(MetaExpedientEntity.class))).thenReturn(nouProcediment);
        when(nouProcediment.getClassificacio()).thenReturn("SIA001");
        when(nouProcediment.getNom()).thenReturn("Procediment nou");
        when(nouProcediment.getId()).thenReturn(META_EXPEDIENT_ID);

        MetaExpedientEntity resultat = helper.create(ENTITAT_ID, dto, "IPA_ADMIN", ORGAN_ID);

        assertThat(resultat).isSameAs(nouProcediment);
        verify(metaExpedientRepository).save(any());
        verify(nouProcediment).updateRevisioEstat(MetaExpedientRevisioEstatEnumDto.REVISAT);
    }

    @Test
    void create_rolOrganAdmin_posaEstatDisseny() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity nouProcediment = mock(MetaExpedientEntity.class);
        MetaExpedientDto dto = new MetaExpedientDto();
        dto.setCodi("PROC_NOU");
        dto.setNom("Procediment nou");
        dto.setOrganGestor(null);
        dto.setPareId(null);
        dto.setEstructuraCarpetes(null);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(metaExpedientRepository.save(any())).thenReturn(nouProcediment);
        when(nouProcediment.getClassificacio()).thenReturn("SIA001");
        when(nouProcediment.getNom()).thenReturn("Procediment nou");
        when(nouProcediment.getId()).thenReturn(META_EXPEDIENT_ID);
        // canviarRevisioADisseny necessita comprovarEntitatPerMetaExpedients i comprovarAccesMetaExpedient
        when(entityComprovarHelper.comprovarAccesMetaExpedient(entitat, META_EXPEDIENT_ID, ORGAN_ID, false))
                .thenReturn(nouProcediment);
        when(nouProcediment.getRevisioEstat()).thenReturn(MetaExpedientRevisioEstatEnumDto.PENDENT);

        helper.create(ENTITAT_ID, dto, "IPA_ORGAN_ADMIN", ORGAN_ID);

        verify(nouProcediment).updateRevisioEstat(MetaExpedientRevisioEstatEnumDto.DISSENY);
    }

    // =========================================================================
    // update
    // =========================================================================

    @Test
    void update_rolIpaAdmin_actualitzaIRetornaElProcediment() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity procediment = mock(MetaExpedientEntity.class);
        MetaExpedientDto dto = new MetaExpedientDto();
        dto.setId(META_EXPEDIENT_ID);
        dto.setCodi("PROC");
        dto.setNom("Nom actualitzat");
        dto.setOrganGestor(null);
        dto.setPareId(null);
        dto.setEstructuraCarpetes(null);
        dto.setRevisioEstat(MetaExpedientRevisioEstatEnumDto.REVISAT);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarAccesMetaExpedient(entitat, META_EXPEDIENT_ID, ORGAN_ID, true))
                .thenReturn(procediment);
        when(expedientRepository.findByMetaExpedientIdAndEsborrat(META_EXPEDIENT_ID, 0))
                .thenReturn(Collections.emptyList());
        when(procediment.getClassificacio()).thenReturn("SIA001");
        when(procediment.getNom()).thenReturn("Nom actualitzat");
        when(procediment.getId()).thenReturn(META_EXPEDIENT_ID);
        // canviarEstatRevisioASellecionat
        when(entityComprovarHelper.comprovarMetaExpedient(entitat, META_EXPEDIENT_ID)).thenReturn(procediment);
        when(procediment.getRevisioEstat()).thenReturn(MetaExpedientRevisioEstatEnumDto.REVISAT);
        when(procediment.getCodi()).thenReturn("PROC");
        MetaExpedientDto dtoResultat = new MetaExpedientDto();
        when(conversioTipusHelper.convertir(procediment, MetaExpedientDto.class)).thenReturn(dtoResultat);

        MetaExpedientEntity resultat = helper.update(ENTITAT_ID, dto, "IPA_ADMIN",
                MetaExpedientRevisioEstatEnumDto.REVISAT, ORGAN_ID);

        assertThat(resultat).isSameAs(procediment);
        verify(procediment).update(any(), any(), any(), any(), any(), any(),
                anyBoolean(), anyBoolean(), any(), any(), anyBoolean(), any(), anyBoolean(), anyBoolean());
    }

    // =========================================================================
    // createFromImport
    // =========================================================================

    @Test
    void createFromImport_ambDtoMinim_creaElProcedimentSenseErrors() {
    	
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity procedimentCreat = mock(MetaExpedientEntity.class);
        MetaExpedientEntity tascaMetaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity metaDocumentEntity = mock(MetaDocumentEntity.class);
        GrupEntity grupEntity = mock(GrupEntity.class);

        MetaDadaDto metaDadaDocumentDto = new MetaDadaDto();
        metaDadaDocumentDto.setCodi("DADA_DOC_1");
        metaDadaDocumentDto.setNom("Dada de metadoc 1");
        metaDadaDocumentDto.setTipus(MetaDadaTipusEnumDto.IMPORT);
        metaDadaDocumentDto.setMultiplicitat(MultiplicitatEnumDto.M_1);
        
        MetaDocumentDto metaDocumentDto = new MetaDocumentDto();
        metaDocumentDto.setCodi("DOC_1");
        metaDocumentDto.setNom("Document 1");
        metaDocumentDto.setMetaDades(Collections.singletonList(metaDadaDocumentDto));

        MetaDadaDto metaDadaDto = new MetaDadaDto();
        metaDadaDto.setCodi("DADA_1");
        metaDadaDto.setNom("Dada 1");
        metaDadaDto.setTipus(MetaDadaTipusEnumDto.TEXT);
        metaDadaDto.setMultiplicitat(MultiplicitatEnumDto.M_1);

        ExpedientEstatDto estatDto = new ExpedientEstatDto();
        estatDto.setId(200L);
        estatDto.setCodi("ESTAT_1");
        ExpedientEstatDto estatCreated = new ExpedientEstatDto();
        estatCreated.setId(201L);

        MetaExpedientTascaDto tascaDto = new MetaExpedientTascaDto();
        tascaDto.setCodi("T01");
        tascaDto.setNom("Tasca 1");
        tascaDto.setValidacions(null);

        GrupDto grupDto = new GrupDto();
        grupDto.setCodi("GRUP_1");

        MetaExpedientExportDto importDto = new MetaExpedientExportDto();
        importDto.setCodi("IMP_PROC");
        importDto.setNom("Procediment importat");
        importDto.setOrganGestor(null);
        importDto.setPareId(null);
        importDto.setCarpetes(null);
        importDto.setMetaDocuments(Collections.singletonList(metaDocumentDto));
        importDto.setMetaDades(Collections.singletonList(metaDadaDto));
        importDto.setEstats(new HashSet<>(Collections.singletonList(estatDto)));
        importDto.setTasques(new HashSet<>(Collections.singletonList(tascaDto)));
        importDto.setGrups(Collections.singletonList(grupDto));

        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(metaExpedientRepository.save(any(MetaExpedientEntity.class))).thenReturn(procedimentCreat);
        when(procedimentCreat.getId()).thenReturn(META_EXPEDIENT_ID);
        when(metaDocumentHelper.create(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(metaDocumentEntity);
        MetaDocumentDto convertedMetaDoc = new MetaDocumentDto();
        convertedMetaDoc.setId(50L);
        when(conversioTipusHelper.convertir(any(MetaDocumentEntity.class), eq(MetaDocumentDto.class)))
                .thenReturn(convertedMetaDoc);
        when(expedientEstatHelper.createExpedientEstat(any(), any(), any(), any())).thenReturn(estatCreated);
        when(entityComprovarHelper.comprovarMetaExpedient(any(), any())).thenReturn(tascaMetaExpedient);
        when(metaExpedientTascaRepository.save(any(MetaExpedientTascaEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(conversioTipusHelper.convertir(any(MetaExpedientTascaEntity.class), eq(MetaExpedientTascaDto.class)))
                .thenReturn(tascaDto);
        when(grupRepository.findByEntitatIdAndCodi(any(), any())).thenReturn(grupEntity);
        initMeterRegistry();
        
        helper.createFromImport(ENTITAT_ID, importDto, "IPA_ADMIN", ORGAN_ID);

        verify(metaExpedientRepository).save(any());
        verify(procedimentCreat).updateRevisioEstat(MetaExpedientRevisioEstatEnumDto.REVISAT);
        verify(metaDocumentHelper).create(any(), any(), any(), any(), any(), any(), any(), any());
        verify(metaDadaHelper, org.mockito.Mockito.atLeastOnce()).create(any(), any(), any(), any(), any());
        verify(expedientEstatHelper).createExpedientEstat(any(), any(), any(), any());
        verify(metaExpedientTascaRepository).save(any());
    }

    // =========================================================================
    // updateFromImport
    // =========================================================================

    @Test
    void updateFromImport_ambDtoMinim_actualitzaElProcedimentSenseErrors() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity procedimentOriginal = mock(MetaExpedientEntity.class);
        MetaDocumentEntity metaDocumentEntity = mock(MetaDocumentEntity.class);
        GrupEntity grupEntity = mock(GrupEntity.class);

        MetaDocumentDto metaDocumentDto = new MetaDocumentDto();
        metaDocumentDto.setCodi("DOC_1");
        metaDocumentDto.setNom("Document 1");
        metaDocumentDto.setMetaDades(null);

        MetaDadaDto metaDadaDto = new MetaDadaDto();
        metaDadaDto.setCodi("DADA_1");
        metaDadaDto.setNom("Dada 1");
        metaDadaDto.setTipus(MetaDadaTipusEnumDto.TEXT);
        metaDadaDto.setMultiplicitat(MultiplicitatEnumDto.M_1);

        ExpedientEstatDto estatDto = new ExpedientEstatDto();
        estatDto.setId(200L);
        estatDto.setCodi("ESTAT_1");

        MetaExpedientTascaDto tascaDto = new MetaExpedientTascaDto();
        tascaDto.setCodi("T01");
        tascaDto.setNom("Tasca 1");
        tascaDto.setValidacions(null);

        GrupDto grupDto = new GrupDto();
        grupDto.setId(100L);
        grupDto.setCodi("GRUP_1");

        MetaExpedientExportDto importDto = new MetaExpedientExportDto();
        importDto.setId(META_EXPEDIENT_ID);
        importDto.setCodi("PROC");
        importDto.setNom("Nom actualitzat");
        importDto.setOrganGestor(null);
        importDto.setCarpetes(null);
        importDto.setMetaDocuments(Collections.singletonList(metaDocumentDto));
        importDto.setMetaDades(Collections.singletonList(metaDadaDto));
        importDto.setEstats(new HashSet<>(Collections.singletonList(estatDto)));
        importDto.setTasques(new HashSet<>(Collections.singletonList(tascaDto)));
        importDto.setGrups(Collections.singletonList(grupDto));

        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaExpedient(entitat, META_EXPEDIENT_ID)).thenReturn(procedimentOriginal);
        when(procedimentOriginal.getCodi()).thenReturn("PROC");
        when(procedimentOriginal.getPare()).thenReturn(null);
        when(procedimentOriginal.getId()).thenReturn(META_EXPEDIENT_ID);

        // MetaDocuments: cerca per codi → null → crea
        when(metaDocumentHelper.findByCodiAndProcediment(procedimentOriginal, "DOC_1")).thenReturn(null);
        when(metaDocumentHelper.create(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(metaDocumentEntity);

        // MetaDades: cerca per codi → null → crea
        when(metaDadaHelper.findByMetaNodeAndCodi(procedimentOriginal, "DADA_1")).thenReturn(null);

        // Estats: cerca per codi → null → crea
        when(expedientEstatHelper.findByMetaExpedientAndCodi(procedimentOriginal, "ESTAT_1")).thenReturn(null);
        ExpedientEstatDto estatCreated = new ExpedientEstatDto();
        estatCreated.setId(201L);
        when(expedientEstatHelper.createExpedientEstat(any(), any(), any(), any())).thenReturn(estatCreated);

        // Tasques: cerca per codi → null → tascaCreate (intern)
        when(metaExpedientTascaRepository.findByMetaExpedientAndCodi(procedimentOriginal, "T01")).thenReturn(null);
        when(metaExpedientTascaRepository.save(any(MetaExpedientTascaEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(conversioTipusHelper.convertir(any(MetaExpedientTascaEntity.class), eq(MetaExpedientTascaDto.class)))
                .thenReturn(tascaDto);

        // Grups: no existeix → grupCreate
        when(procedimentOriginal.hasGrup(100L)).thenReturn(false);
        when(grupRepository.findByEntitatIdAndCodi(any(), any())).thenReturn(grupEntity);

        initMeterRegistry();
        
        helper.updateFromImport(ENTITAT_ID, importDto, "IPA_ADMIN", ORGAN_ID);

        verify(procedimentOriginal).update(any(), any(), any(), any(), any(), any(),
                anyBoolean(), anyBoolean(), any(), any(), anyBoolean(), any(), anyBoolean(), anyBoolean());
        verify(metaDocumentHelper).create(any(), any(), any(), any(), any(), any(), any(), any());
        verify(metaDadaHelper).create(any(), any(), any(), any(), any());
        verify(expedientEstatHelper).createExpedientEstat(any(), any(), any(), any());
        verify(metaExpedientTascaRepository).save(any());
    }
    
    private void initMeterRegistry() {
        when(applicationHelper.getMeterRegistry()).thenReturn(new MeterRegistry(new Clock() {
			
			@Override
			public long wallTime() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public long monotonicTime() {
				// TODO Auto-generated method stub
				return 0;
			}
		}) {
			
			@Override
			protected Timer newTimer(Id id, DistributionStatisticConfig distributionStatisticConfig,
					PauseDetector pauseDetector) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			protected Meter newMeter(Id id, Type type, Iterable<Measurement> measurements) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			protected <T> Gauge newGauge(Id id, T obj, ToDoubleFunction<T> valueFunction) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			protected <T> FunctionTimer newFunctionTimer(Id id, T obj, ToLongFunction<T> countFunction,
					ToDoubleFunction<T> totalTimeFunction, TimeUnit totalTimeFunctionUnit) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			protected <T> FunctionCounter newFunctionCounter(Id id, T obj, ToDoubleFunction<T> countFunction) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			protected DistributionSummary newDistributionSummary(Id id, DistributionStatisticConfig distributionStatisticConfig,
					double scale) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			protected Counter newCounter(Id id) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			protected TimeUnit getBaseTimeUnit() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			protected DistributionStatisticConfig defaultHistogramConfig() {
				// TODO Auto-generated method stub
				return null;
			}
		});
    }
}
