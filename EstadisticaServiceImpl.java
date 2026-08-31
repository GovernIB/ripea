package es.caib.notib.logic.service;


import es.caib.comanda.model.server.monitoring.Dimensio;
import es.caib.comanda.model.server.monitoring.DimensioDesc;
import es.caib.comanda.model.server.monitoring.Fet;
import es.caib.comanda.model.server.monitoring.IndicadorDesc;
import es.caib.comanda.model.server.monitoring.RegistreEstadistic;
import es.caib.comanda.model.server.monitoring.RegistresEstadistics;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.intf.dto.explotacio.DiaSetmanaEnum;
import es.caib.notib.logic.intf.dto.explotacio.DimEnum;
import es.caib.notib.logic.intf.dto.explotacio.DimensioNotib;
import es.caib.notib.logic.intf.dto.explotacio.EnviamentOrigen;
import es.caib.notib.logic.intf.dto.explotacio.FetNotib;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.service.EstadisticaService;
import es.caib.notib.persist.entity.explotacio.ExplotDimensio;
import es.caib.notib.persist.entity.explotacio.ExplotDimensioEntity;
import es.caib.notib.persist.entity.explotacio.ExplotEnvBasicStatsEntity;
import es.caib.notib.persist.entity.explotacio.ExplotEnvStats;
import es.caib.notib.persist.entity.explotacio.ExplotFets;
import es.caib.notib.persist.entity.explotacio.ExplotFetsEntity;
import es.caib.notib.persist.entity.explotacio.ExplotFetsKey;
import es.caib.notib.persist.entity.explotacio.ExplotTempsEntity;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.ProcSerRepository;
import es.caib.notib.persist.repository.UsuariRepository;
import es.caib.notib.persist.repository.explotacio.ExplotDimensioRepository;
import es.caib.notib.persist.repository.explotacio.ExplotEnvBasicStatsRepository;
import es.caib.notib.persist.repository.explotacio.ExplotEnvInfoRepository;
import es.caib.notib.persist.repository.explotacio.ExplotFetsRepository;
import es.caib.notib.persist.repository.explotacio.ExplotTempsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import es.caib.comanda.model.server.monitoring.Format;

import static es.caib.notib.logic.intf.dto.explotacio.FetEnum.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstadisticaServiceImpl implements EstadisticaService {

    private final ExplotTempsRepository explotTempsRepository;
    private final ExplotDimensioRepository explotDimensioRepository;
    private final ExplotFetsRepository explotFetsRepository;
    private final ExplotEnvBasicStatsRepository explotEnvBasicStatsRepository;
    private final EntitatRepository entitatRepository;
    private final OrganGestorRepository organGestorRepository;
    private final ProcSerRepository procSerRepository;
    private final UsuariRepository usuariRepository;
    private final IntegracioHelper integracioHelper;
    private final JdbcTemplate jdbcTemplate;
    private final ExplotEnvInfoRepository explotEnvInfoRepository;

    @Value("${es.caib.notib.hibernate.dialect:es.caib.notib.persist.dialect.OracleCaibDialect}")
    private String hibernateDialect;

    private static Date firstEnvInfoDate;

    // Inicialitzar les consultes SQL segons el tipus de base de dades
    // Oracle (per defecte)
    private String SQL_INSERT_EXPLOT_TEMPS = "INSERT INTO not_explot_temps (" +
            "id, " +
            "data, " +
            "anualitat, " +
            "trimestre, " +
            "mes, " +
            "setmana, " +
            "dia, " +
            "dia_setmana) " +
            "VALUES (NOT_HIBERNATE_SEQ.nextval, ?, ?, ?, ?, ?, ?, ?)";
    private String SQL_INSERT_EXPLOT_FETS = "INSERT INTO not_explot_fet (" +
            "id, " +
            "dimensio_id, " +
            "temps_id, " +
            "tr_creades, " +
            "tr_registr, " +
            "tr_not_env, " +
            "tr_not_not, " +
            "tr_not_reb, " +
            "tr_not_exp, " +
            "tr_reg_err, " +
            "tr_not_err, " +
            "tr_sir_acc, " +
            "tr_sir_reb) " +
            "VALUES (NOT_HIBERNATE_SEQ.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    @PostConstruct
    public void init() {
        boolean isPostgres = isPostgresDialect();
        log.info("Inicialitzant consultes SQL per a base de dades: {}", isPostgres ? "PostgreSQL" : "Oracle");

        if (isPostgres) {
            SQL_INSERT_EXPLOT_TEMPS = "INSERT INTO not_explot_temps (" +
                    "id, " +
                    "data, " +
                    "anualitat, " +
                    "trimestre, " +
                    "mes, " +
                    "setmana, " +
                    "dia, " +
                    "dia_setmana) " +
                    "VALUES (NEXTVAL('NOT_HIBERNATE_SEQ'), ?, ?, ?, ?, ?, ?, ?)";
            SQL_INSERT_EXPLOT_FETS = "INSERT INTO not_explot_fet (" +
                    "id, " +
                    "dimensio_id, " +
                    "temps_id, " +
                    "tr_creades, " +
                    "tr_registr, " +
                    "tr_not_env, " +
                    "tr_not_not, " +
                    "tr_not_reb, " +
                    "tr_not_exp, " +
                    "tr_reg_err, " +
                    "tr_not_err," +
                    "tr_sir_acc, " +
                    "tr_sir_reb) " +
                    "VALUES (NEXTVAL('NOT_HIBERNATE_SEQ'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        }
    }

    private boolean isPostgresDialect() {
        return hibernateDialect != null && hibernateDialect.toLowerCase().contains("postgresql");
    }

    // Mida del lot per insercions en bloc
    private static final int BATCH_SIZE = 1000;

    @Override
    @Transactional(timeout = 3600)
    public boolean generarDadesExplotacio() {
        return generarDadesExplotacio(ahir());
    }

    @Override
    @Transactional(timeout = 3600)
    public boolean generarDadesExplotacio(LocalDate data) {

        // Generar dades d'explotació
        String accioDesc = "GenerarDadesExplotacio - Recupera dades per taules d'explotació.";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (data == null) data = ahir();
        var info = new IntegracioInfo(
                IntegracioCodi.EXPLOTACIO,
                accioDesc,
                IntegracioAccioTipusEnumDto.PROCESSAR,
                new AccioParam("Data de recollida de dades", formatter.format(data)));

        try {
            log.debug("Iniciant procés de generarDadesExplotacio. Data: {}", data);

            ExplotTempsEntity ete = explotTempsRepository.findFirstByData(data);

            if (ete == null) {
                ete = new ExplotTempsEntity(data);
                ete = explotTempsRepository.save(ete);
            }

            List<ExplotDimensioEntity> dimensions = obtenirDimensions();
            actualitzarDadesEstadistiques(ete, dimensions);

            log.debug("Finalitzant procés de generarDadesExplotacio, guardant a monitor de integracions.");

            integracioHelper.addAccioOk(info);

            log.debug("Finalitzat procés de generarDadesExplotacio.");
            return true;
        } catch (Exception ex) {
            log.error("Error generant informació estadística", ex);
            integracioHelper.addAccioError(info, "Error generant informació estadística", ex);
            return false;
        }
    }

    @Override
    @Transactional(timeout = 3600)
    public void generarDadesExplotacio(LocalDate dataInici, LocalDate dataFi) {

        if (dataFi == null) {
            dataFi = ahir();
        }
        if (dataInici.isAfter(dataFi)) {
            LocalDate temp = dataInici;
            dataInici = dataFi;
            dataFi = temp;
        }

        // Generar dades d'explotació
        String accioDesc = "GenerarDadesExplotacio - Recupera dades per taules d'explotació.";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        var info = new IntegracioInfo(
                IntegracioCodi.EXPLOTACIO,
                accioDesc,
                IntegracioAccioTipusEnumDto.PROCESSAR,
                new AccioParam("Data inici", formatter.format(dataInici)),
                new AccioParam("Data fi", formatter.format(dataFi)));

        try {
            log.debug("Iniciant procés de generarDadesExplotacio. Data inici: {}, Data fi: {}", dataInici, dataFi);

            List<LocalDate> missingDates = geMissingExplotTempsEntities(dataInici, dataFi);
            if (!missingDates.isEmpty()) {
                generateNativeSqlMissingExplotTempsEntities(dataInici, dataFi);
            }

            List<ExplotDimensioEntity> dimensions = obtenirDimensions();
            List<ExplotTempsEntity> etes = explotTempsRepository.findByDataIn(missingDates);
            if (etes == null || etes.isEmpty()) {
                return;
            }
            etes.forEach(ete -> actualitzarDadesEstadistiques(ete, dimensions));

            log.debug("Finalitzat procés de generarDadesExplotacio.");
            integracioHelper.addAccioOk(info);

        } catch (Exception ex) {
            log.error("Error generant informació estadística", ex);
            integracioHelper.addAccioError(info, "Error generant informació estadística", ex);
        }
    }

    private void generarDadesExplotacioBasiques(LocalDate fromDate, LocalDate toDate) {
        generarDadesExplotacioBasiques(fromDate, toDate, false);
    }

    @Transactional(timeout = 3600)
    @Override
    public void generarDadesExplotacioBasiques(LocalDate fromDate, LocalDate toDate, boolean regenerar) {

        var info = crearIntegracioInfo(fromDate);

        try {
            log.debug("Iniciant generació bàsica des de {} fins {}", fromDate, toDate);

            var dimensions = obtenirDimensions();
            var dimensionKeyCache = cachejarDimensions(dimensions);

            // Generar les entitats ExplotTempsEntity que faltes
            generateNativeSqlMissingExplotTempsEntities(fromDate, toDate);
            var dates = explotTempsRepository.findByDataBetween(fromDate, toDate);

            // Obtenir totes les estadístiques per al període complet
            List<ExplotEnvBasicStatsEntity> stats = explotEnvBasicStatsRepository.findByDiaBetween(fromDate, toDate);

            // Convertir les estadístiques a un mapa per data
            Map<LocalDate, Map<String, Map<ExplotFetsKey, Long>>> estadistiquesPerData = convertirEstadistiquesPerData(stats);

            processarDatesAmbEstadistiques(dates, dimensions, dimensionKeyCache, estadistiquesPerData, regenerar);

            integracioHelper.addAccioOk(info);
            log.debug("Finalitzat procés bàsic.");
        } catch (Exception ex) {
            log.error("Error generant dades simplificades", ex);
            integracioHelper.addAccioError(info, "Error generant dades simplificades", ex);
            throw new RuntimeException("S'ha produït un error al generar les dades estadístiques simplificades", ex);
        }
    }

    private IntegracioInfo crearIntegracioInfo(LocalDate fromDate) {

        var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return new IntegracioInfo(
                IntegracioCodi.EXPLOTACIO,
                "generarDadesExplotacioBasiques",
                IntegracioAccioTipusEnumDto.PROCESSAR,
                new AccioParam("Data inici", formatter.format(fromDate)),
                new AccioParam("Data fi", formatter.format(ahir()))
        );
    }

    private Map<Long, ExplotFetsKey> cachejarDimensions(List<ExplotDimensioEntity> dimensions) {
        return dimensions.stream().collect(Collectors.toMap(ExplotDimensioEntity::getId, this::createKeyFromDimension));
    }

    private void processarDatesAmbEstadistiques(List<ExplotTempsEntity> dates,
                                                List<ExplotDimensioEntity> dimensions,
                                                Map<Long, ExplotFetsKey> keyCache,
                                                Map<LocalDate, Map<String, Map<ExplotFetsKey, Long>>> estadistiquesPerData,
                                                boolean regenerar) {
        int processedDays = 0;
        int skippedDays = 0;

        List<Object[]> batchArgs = new ArrayList<>();

        for (ExplotTempsEntity temps : dates) {
            if (jaHiHaDades(temps)) {
                if (!regenerar) {
                    skippedDays++;
                    System.out.println("Skipped dia: " + temps.getData());
                    continue;
                } else {
                    // Eliminam les dades d'explotació de la data
                    explotFetsRepository.deleteAllByTemps(temps);
                }
            }

            Map<String, Map<ExplotFetsKey, Long>> estadistiques = estadistiquesPerData.getOrDefault(temps.getData(), new HashMap<>());
            batchArgs.addAll(prepararInsertions(dimensions, keyCache, temps, estadistiques));

            processedDays++;
            System.out.println("Processat dia: " + temps.getData());
        }
        insercioFetsEnBloc(batchArgs);

        log.debug("Dies processats: {}, Dies omesos: {}", processedDays, skippedDays);
    }

    private Map<LocalDate, Map<String, Map<ExplotFetsKey, Long>>> convertirEstadistiquesPerData(List<ExplotEnvBasicStatsEntity> stats) {

        Map<LocalDate, Map<String, Map<ExplotFetsKey, Long>>> result = new HashMap<>();
        processStats(stats, result);
        return result;
    }

    private void processStats(List<ExplotEnvBasicStatsEntity> statsList,
                              Map<LocalDate, Map<String, Map<ExplotFetsKey, Long>>> result) {
        int i = 0;
        for (ExplotEnvBasicStatsEntity stat : statsList) {
            result.computeIfAbsent(stat.getDia(), k -> new HashMap<>())
                    .computeIfAbsent(stat.getTipus(), k -> new HashMap<>())
                    .put(stat.getKey(), stat.getTotalEnviaments());
            System.out.println("Stat: " + i++);
        }
    }

    private boolean jaHiHaDades(ExplotTempsEntity temps) {
        return explotFetsRepository.existsByTemps(temps);
    }

    private List<Object[]> prepararInsertions(List<ExplotDimensioEntity> dimensions, Map<Long, ExplotFetsKey> keyCache, ExplotTempsEntity temps, Map<String, Map<ExplotFetsKey, Long>> statsMap) {

        List<Object[]> argsList = new ArrayList<>();
        for (ExplotDimensioEntity dim : dimensions) {
            ExplotFetsKey key = keyCache.get(dim.getId());

            long trCreades = statsMap.getOrDefault("CREADA", Map.of()).getOrDefault(key, 0L);
            long trRegistrades = statsMap.getOrDefault("REGISTRADA", Map.of()).getOrDefault(key, 0L);
            long trNotEnv = statsMap.getOrDefault("ENVIADA", Map.of()).getOrDefault(key, 0L);
            long trNotNotificacio = statsMap.getOrDefault("NOTIFICADA", Map.of()).getOrDefault(key, 0L);
            long trNotRebutjada = statsMap.getOrDefault("REBUTJADA", Map.of()).getOrDefault(key, 0L);
            long trNotExpirada = statsMap.getOrDefault("EXPIRADA", Map.of()).getOrDefault(key, 0L);
            long trNotRegError = statsMap.getOrDefault("REG_ERR", Map.of()).getOrDefault(key, 0L);
            long trNotNotError = statsMap.getOrDefault("NOT_ERR", Map.of()).getOrDefault(key, 0L);
            long trSirAcc = statsMap.getOrDefault("SIR_ACC", Map.of()).getOrDefault(key, 0L);
            long trSirReb = statsMap.getOrDefault("SIR_REB", Map.of()).getOrDefault(key, 0L);

            Object[] args = new Object[] {
                    dim.getId(),
                    temps.getId(),
                    trCreades,
                    trRegistrades,
                    trNotEnv,
                    trNotNotificacio,
                    trNotRebutjada,
                    trNotExpirada,
                    trNotRegError,
                    trNotNotError,
                    trSirAcc,
                    trSirReb
            };

            long total = Arrays.stream(args).skip(2).mapToLong(o -> (Long) o).sum();
            if (total > 0) argsList.add(args);
        }

        return argsList;
    }

    private void insercioFetsEnBloc(List<Object[]> batchArgs) {
        jdbcTemplate.batchUpdate(SQL_INSERT_EXPLOT_FETS, batchArgs, BATCH_SIZE, (ps, args) -> {
            ps.setObject(1, args[0]);
            ps.setObject(2, args[1]);
            ps.setObject(3, args[2]);
            ps.setObject(4, args[3]);
            ps.setObject(5, args[4]);
            ps.setObject(6, args[5]);
            ps.setObject(7, args[6]);
            ps.setObject(8, args[7]);
            ps.setObject(9, args[8]);
            ps.setObject(10, args[9]);
            ps.setObject(11, args[9]);
            ps.setObject(12, args[9]);
        });
    }

    /**
     * Crea una clau ExplotFetsKey a partir d'una dimensió
     */
    private ExplotFetsKey createKeyFromDimension(ExplotDimensioEntity dimension) {
        return new ExplotFetsKey(
            dimension.getEntitatId(),
            dimension.getProcedimentId(),
            dimension.getOrganCodi(),
            dimension.getUsuariCodi(),
            dimension.getTipus(),
            dimension.getOrigen()
        );
    }

    private void generateNativeSqlMissingExplotTempsEntities(LocalDate fromDate, LocalDate toDate) {

        if (toDate == null) {
            toDate = ahir();
        }
        // Obtenim totes les dates per al rang corresponent
        List<LocalDate> existingDates = explotTempsRepository.findDatesBetween(fromDate, toDate);
        List<LocalDate> missingDates = new ArrayList<>();

        // Iterem només pels dies que no estan a la base de dades
        while (!fromDate.isAfter(toDate)) {
            if (!existingDates.contains(fromDate)) {
                missingDates.add(fromDate);
            }
            fromDate = fromDate.plusDays(1);
        }

        if (!missingDates.isEmpty()) {
            tempsBulkInsert(missingDates);
        }
    }

    private void tempsBulkInsert(List<LocalDate> dates) {

        jdbcTemplate.batchUpdate(SQL_INSERT_EXPLOT_TEMPS, dates, BATCH_SIZE, (ps, data) -> {
            ps.setObject(1, java.sql.Date.valueOf(data));
            ps.setObject(2, data.getYear());
            ps.setObject(3, data.getMonthValue() / 3);
            ps.setObject(4, data.getMonthValue());
            ps.setObject(5, data.get(WeekFields.ISO.weekOfWeekBasedYear()));
            ps.setObject(6, data.getDayOfMonth());
            DiaSetmanaEnum diaSetmanaEnum = DiaSetmanaEnum.valueOfData(data.getDayOfWeek().name());
            ps.setObject(7, diaSetmanaEnum != null ? diaSetmanaEnum.name() : null);
        });

    }

    // Obtenir dates sense dades estadístiques
    private List<LocalDate> geMissingExplotTempsEntities(LocalDate fromDate, LocalDate toDate) {

        List<LocalDate> missingDates = new ArrayList<>();
        if (toDate == null) {
            toDate = ahir();
        }

        // Obtenim totes les dates per al rang corresponent
        List<LocalDate> existingDates = explotTempsRepository.findDatesBetween(fromDate, toDate);

        // Iterem només pels dies que no estan a la base de dades
        LocalDate currentDate = fromDate;
        while (!currentDate.isAfter(toDate)) {
            if (!existingDates.contains(currentDate)) {
                missingDates.add(currentDate);
            }
            currentDate = currentDate.plusDays(1);
        }

        return missingDates;
    }


    @Override
    @Transactional
    public RegistresEstadistics consultaUltimesEstadistiques() {

        return consultaEstadistiques(ahir());
    }

    @Override
    @Transactional
    public RegistresEstadistics consultaEstadistiques(LocalDate data) {

        ExplotTempsEntity temps = explotTempsRepository.findFirstByDataAndExisteixenFets(data);
        //TODO FALTA MIRAR SI HI HA FETS PER AQUEST TEMPS
        if (temps == null) {
            // Si no existeixen dades, les generam
            if (!data.isBefore(LocalDate.now())) {
                //No generar dades estadistiques futures
                var offSetDataTime = data.atStartOfDay().atOffset(ZoneId.systemDefault().getRules().getOffset(java.time.Instant.now()));
                return new RegistresEstadistics().temps(offSetDataTime).fets(List.of());
            }
            var firstInfoDate = getFirstInfoDate();
            if (!data.isBefore(firstInfoDate)) {
                generarDadesExplotacio(data);
            } else {
                // data es anterior a la primera data que te estadistiques torna les basiques
                generarDadesExplotacioBasiques(data, data);
            }
        }
        return getRegistresEstadistics(data);
    }

    private RegistresEstadistics getRegistresEstadistics(LocalDate data) {

        ExplotTempsEntity temps = explotTempsRepository.findFirstByData(data);
        if (temps == null) {
            var offSetDataTime = data.atStartOfDay().atOffset(ZoneId.systemDefault().getRules().getOffset(java.time.Instant.now()));
            return new RegistresEstadistics().temps(offSetDataTime).fets(List.of());
        }
        List<ExplotFetsEntity> fets = explotFetsRepository.findByTemps(temps);
        return toRegistresEstadistics(fets, data);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistresEstadistics> consultaEstadistiques(LocalDate dataInici, LocalDate dataFi) {

        List<RegistresEstadistics> result = new ArrayList<>();
        // Si no existeixen dades, les generam
        //TODO FALTA MIRAR SI HI HA FETS PER AQUEST TEMPS
//        List<LocalDate> localDates = geMissingExplotTempsEntities(dataInici, dataFi);
        List<LocalDate> localDates = explotTempsRepository.findBetweenDatesAndExisteixenFets(dataInici, dataFi);
        if (localDates.isEmpty()) {
            var localEnvInfoDate = getFirstInfoDate();
            if( localEnvInfoDate.isBefore(dataInici) ) {
                generarDadesExplotacio(dataInici, dataFi);
            } else if (localEnvInfoDate.isAfter(dataFi)) {
                generarDadesExplotacioBasiques(dataInici, dataFi);
            } else {
                generarDadesExplotacioBasiques(dataInici, localEnvInfoDate.minusDays(1));
                generarDadesExplotacio(localEnvInfoDate, dataFi);
            }
        }

        LocalDate currentDate = dataInici;
        while (!currentDate.isAfter(dataFi)) {
            result.add(getRegistresEstadistics(currentDate));
            currentDate = currentDate.plusDays(1);
        }
        return result;
    }

    private LocalDate getFirstInfoDate() {

        LocalDate localEnvInfoDate = LocalDate.now();
        if (firstEnvInfoDate == null) {
            firstEnvInfoDate = explotEnvInfoRepository.getFirstDate();
        }
        if (firstEnvInfoDate != null) {
            localEnvInfoDate = firstEnvInfoDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return localEnvInfoDate;
    }


    @Override
    public List<DimensioDesc> getDimensions() {

        List<String> entitatCodis = entitatRepository.findAllCodis();
        List<String> organCodis = organGestorRepository.findAllCodis();
        List<String> procedimentCodis = procSerRepository.findAllCodis();
        procedimentCodis.add("");
        List<String> usuariCodis = usuariRepository.findCodiAll();
        // Si no existeix, afegirem un usuari "DESCONEGUT"
        int index = Collections.binarySearch(usuariCodis, "DESCONEGUT");
        if (index < 0) {
            int insertionPoint = -(index + 1);
            usuariCodis.add(insertionPoint, "DESCONEGUT");
        }
        List<String> tipus = Arrays.stream(EnviamentTipus.values()).map(Enum::name).sorted().collect(Collectors.toList());
        List<String> origens = Arrays.stream(EnviamentOrigen.values()).map(Enum::name).sorted().collect(Collectors.toList());

        return List.of(
                new DimensioDesc().codi(DimEnum.ENT.name()).nom(DimEnum.ENT.getNom()).descripcio(DimEnum.ENT.getDescripcio()).valors(entitatCodis),
                new DimensioDesc().codi(DimEnum.ORG.name()).nom(DimEnum.ORG.getNom()).descripcio(DimEnum.ORG.getDescripcio()).valors(organCodis),
                new DimensioDesc().codi(DimEnum.PRC.name()).nom(DimEnum.PRC.getNom()).descripcio(DimEnum.PRC.getDescripcio()).valors(procedimentCodis),
                new DimensioDesc().codi(DimEnum.USU.name()).nom(DimEnum.USU.getNom()).descripcio(DimEnum.USU.getDescripcio()).valors(usuariCodis),
                new DimensioDesc().codi(DimEnum.TIP.name()).nom(DimEnum.TIP.getNom()).descripcio(DimEnum.TIP.getDescripcio()).valors(tipus),
                new DimensioDesc().codi(DimEnum.ORI.name()).nom(DimEnum.ORI.getNom()).descripcio(DimEnum.ORI.getDescripcio()).valors(origens)
        );
    }

    @Override
    public List<IndicadorDesc> getIndicadors() {
        return List.of(
                new IndicadorDesc().codi(PND.name()).nom(PND.getNom()).descripcio(PND.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(REG_ERR.name()).nom(REG_ERR.getNom()).descripcio(REG_ERR.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(REG.name()).nom(REG.getNom()).descripcio(REG.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(SIR_ACC.name()).nom(SIR_ACC.getNom()).descripcio(SIR_ACC.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(SIR_REB.name()).nom(SIR_REB.getNom()).descripcio(SIR_REB.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(NOT_ERR.name()).nom(NOT_ERR.getNom()).descripcio(NOT_ERR.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(NOT_ENV.name()).nom(NOT_ENV.getNom()).descripcio(NOT_ENV.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(NOT_ACC.name()).nom(NOT_ACC.getNom()).descripcio(NOT_ACC.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(NOT_REB.name()).nom(NOT_REB.getNom()).descripcio(NOT_REB.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(NOT_EXP.name()).nom(NOT_EXP.getNom()).descripcio(NOT_EXP.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(CIE_ERR.name()).nom(CIE_ERR.getNom()).descripcio(CIE_ERR.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(CIE_ENV.name()).nom(CIE_ENV.getNom()).descripcio(CIE_ENV.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(CIE_ACC.name()).nom(CIE_ACC.getNom()).descripcio(CIE_ACC.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(CIE_REB.name()).nom(CIE_REB.getNom()).descripcio(CIE_REB.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(CIE_FAL.name()).nom(CIE_FAL.getNom()).descripcio(CIE_FAL.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(PRC.name()).nom(PRC.getNom()).descripcio(PRC.getDescripcio()).format(Format.LONG),
                // Transicions
                new IndicadorDesc().codi(TR_CRE.name()).nom(TR_CRE.getNom()).descripcio(TR_CRE.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TR_REG_ERR.name()).nom(TR_REG_ERR.getNom()).descripcio(TR_REG_ERR.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TR_REG.name()).nom(TR_REG.getNom()).descripcio(TR_REG.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TR_SIR_ACC.name()).nom(TR_SIR_ACC.getNom()).descripcio(TR_SIR_ACC.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TR_SIR_REB.name()).nom(TR_SIR_REB.getNom()).descripcio(TR_SIR_REB.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TR_NOT_ERR.name()).nom(TR_NOT_ERR.getNom()).descripcio(TR_NOT_ERR.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TR_NOT_ENV.name()).nom(TR_NOT_ENV.getNom()).descripcio(TR_NOT_ENV.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TR_NOT_ACC.name()).nom(TR_NOT_ACC.getNom()).descripcio(TR_NOT_ACC.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TR_NOT_REB.name()).nom(TR_NOT_REB.getNom()).descripcio(TR_NOT_REB.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TR_NOT_EXP.name()).nom(TR_NOT_EXP.getNom()).descripcio(TR_NOT_EXP.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TR_NOT_FAL.name()).nom(TR_NOT_FAL.getNom()).descripcio(TR_NOT_FAL.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TR_CIE_ERR.name()).nom(TR_CIE_ERR.getNom()).descripcio(TR_CIE_ERR.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TR_CIE_ENV.name()).nom(TR_CIE_ENV.getNom()).descripcio(TR_CIE_ENV.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TR_CIE_ACC.name()).nom(TR_CIE_ACC.getNom()).descripcio(TR_CIE_ACC.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TR_CIE_REB.name()).nom(TR_CIE_REB.getNom()).descripcio(TR_CIE_REB.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TR_CIE_CAN.name()).nom(TR_CIE_CAN.getNom()).descripcio(TR_CIE_CAN.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TR_CIE_FAL.name()).nom(TR_CIE_FAL.getNom()).descripcio(TR_CIE_FAL.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TR_EML_ERR.name()).nom(TR_EML_ERR.getNom()).descripcio(TR_EML_ERR.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TR_EML_ENV.name()).nom(TR_EML_ENV.getNom()).descripcio(TR_EML_ENV.getDescripcio()).format(Format.LONG),

                // Temps mitjà en estat
                new IndicadorDesc().codi(TMP_PND.name()).nom(TMP_PND.getNom()).descripcio(TMP_PND.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TMP_REG.name()).nom(TMP_REG.getNom()).descripcio(TMP_REG.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TMP_NOT.name()).nom(TMP_NOT.getNom()).descripcio(TMP_NOT.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TMP_CIE.name()).nom(TMP_CIE.getNom()).descripcio(TMP_CIE.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TMP_TOT.name()).nom(TMP_TOT.getNom()).descripcio(TMP_TOT.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TMP_REG_SAC.name()).nom(TMP_REG_SAC.getNom()).descripcio(TMP_REG_SAC.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TMP_REG_SRB.name()).nom(TMP_REG_SRB.getNom()).descripcio(TMP_REG_SRB.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TMP_REG_NOT.name()).nom(TMP_REG_NOT.getNom()).descripcio(TMP_REG_NOT.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TMP_REG_EML.name()).nom(TMP_REG_EML.getNom()).descripcio(TMP_REG_EML.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TMP_NOT_NOT.name()).nom(TMP_NOT_NOT.getNom()).descripcio(TMP_NOT_NOT.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TMP_NOT_REB.name()).nom(TMP_NOT_REB.getNom()).descripcio(TMP_NOT_REB.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TMP_NOT_EXP.name()).nom(TMP_NOT_EXP.getNom()).descripcio(TMP_NOT_EXP.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TMP_NOT_FAL.name()).nom(TMP_NOT_FAL.getNom()).descripcio(TMP_NOT_FAL.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TMP_CIE_NOT.name()).nom(TMP_CIE_NOT.getNom()).descripcio(TMP_CIE_NOT.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TMP_CIE_REB.name()).nom(TMP_CIE_REB.getNom()).descripcio(TMP_CIE_REB.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TMP_CIE_CAN.name()).nom(TMP_CIE_CAN.getNom()).descripcio(TMP_CIE_CAN.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TMP_CIE_FAL.name()).nom(TMP_CIE_FAL.getNom()).descripcio(TMP_CIE_FAL.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TMP_TOT_NAC.name()).nom(TMP_TOT_NAC.getNom()).descripcio(TMP_TOT_NAC.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TMP_TOT_NRB.name()).nom(TMP_TOT_NRB.getNom()).descripcio(TMP_TOT_NRB.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TMP_TOT_NEX.name()).nom(TMP_TOT_NEX.getNom()).descripcio(TMP_TOT_NEX.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TMP_TOT_NFL.name()).nom(TMP_TOT_NFL.getNom()).descripcio(TMP_TOT_NFL.getDescripcio()).format(Format.LONG),
                new IndicadorDesc().codi(TMP_TOT_CAC.name()).nom(TMP_TOT_CAC.getNom()).descripcio(TMP_TOT_CAC.getDescripcio()).format(Format.LONG),

                // Intents
                new IndicadorDesc().codi(INT_REG.name()).nom(INT_REG.getNom()).descripcio(INT_REG.getDescripcio()).format(Format.DECIMAL),
                new IndicadorDesc().codi(INT_SIR.name()).nom(INT_SIR.getNom()).descripcio(INT_SIR.getDescripcio()).format(Format.DECIMAL),
                new IndicadorDesc().codi(INT_NOT.name()).nom(INT_NOT.getNom()).descripcio(INT_NOT.getDescripcio()).format(Format.DECIMAL),
                new IndicadorDesc().codi(INT_CIE.name()).nom(INT_CIE.getNom()).descripcio(INT_CIE.getDescripcio()).format(Format.DECIMAL),
                new IndicadorDesc().codi(INT_EML.name()).nom(INT_EML.getNom()).descripcio(INT_EML.getDescripcio()).format(Format.DECIMAL)
        );
    }


    private LocalDate ahir() {
        return LocalDate.now().minusDays(1);
    }

    private List<ExplotDimensioEntity> obtenirDimensions() {

        // Obtenim totes les dimensions per procedimentServei/usuari. Les que no existeixin les crearem
        List<ExplotDimensio> dimensionsPerEstadistiques = explotDimensioRepository.getDimensionsPerEstadistiques();
        List<ExplotDimensioEntity> dimensionsEnDb = explotDimensioRepository.findAllOrdered();

        return actualitzarDimensions(dimensionsEnDb, dimensionsPerEstadistiques);
    }

    private List<ExplotDimensioEntity> actualitzarDimensions(List<ExplotDimensioEntity> dimensionsEnDb, List<ExplotDimensio> dimensionsPerEstadistiques) {
        List<ExplotDimensioEntity> dimensions = new ArrayList<>();

        for (ExplotDimensio dimensioConsulta : dimensionsPerEstadistiques) {
            // Converteix la instància de ExplotConsultaDimensio a ExplotConsultaDimensioEntity
            ExplotDimensioEntity dimensioEntity = toConsultaDimensioEntity(dimensioConsulta);

            // Comprova si existeix a la base de dades, si no, la guarda
            int dimensioEntityIndex = dimensionsEnDb.indexOf(dimensioEntity);
            if (dimensioEntityIndex == -1) {
                dimensioEntity = explotDimensioRepository.save(dimensioEntity);
                dimensionsEnDb.add(dimensioEntity);
                dimensions.add(dimensioEntity);
            } else {
                dimensions.add(dimensionsEnDb.get(dimensioEntityIndex));
            }
        }

        return dimensions;
    }

    private ExplotDimensioEntity toConsultaDimensioEntity(ExplotDimensio dimensio) {
        return ExplotDimensioEntity.builder()
                .entitatId(dimensio.getEntitatId())
                .entitatCodi(dimensio.getEntitatCodi())
                .procedimentId(dimensio.getProcedimentId())
                .procedimentCodi(dimensio.getProcedimentCodi())
                .organCodi(dimensio.getOrganCodi())
                .usuariCodi(dimensio.getUsuariCodi())
                .tipus(dimensio.getTipus())
                .origen(dimensio.getOrigen())
                .build();
    }

    private void actualitzarDadesEstadistiques(ExplotTempsEntity ete, List<ExplotDimensioEntity> dimensions) {
        // Eliminam les dades d'explotació de la data
        explotFetsRepository.deleteAllByTemps(ete);

        List<ExplotFets> estadistiques = explotFetsRepository.getFetsPerEstadistiques(
                false,
                Date.from(ete.getData().plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));

        addAdditionalStats(estadistiques, ete.getData());

        int dadaIndex = 0;
        int dimensionIndex = 0;

        // Les dues llistes estan ordenades
        while (dadaIndex < estadistiques.size() && dimensionIndex < dimensions.size()) {
            ExplotFets estadistica = estadistiques.get(dadaIndex);
            ExplotDimensioEntity dimension = dimensions.get(dimensionIndex);

            int comparison = compareEstadistiquesAndDimensions(estadistica, dimension);
            if (comparison == 0) {
                saveToFetsEntity(estadistica, dimension, ete);
                dadaIndex++;
            } else if (comparison < 0) {
                dadaIndex++;
            } else {
                dimensionIndex++;
            }
        }
    }

    private void addAdditionalStats(List<ExplotFets> estadistiques, LocalDate localData) {
        Map<ExplotFetsKey, ExplotFets> statsMap = new HashMap<>();
        Date iniciDelDia = Date.from(localData.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date finalDelDia = Date.from(localData.plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());


        estadistiques.forEach(stat -> statsMap.put(
                new ExplotFetsKey(stat.getEntitatId(), stat.getProcedimentId(), stat.getOrganCodi(), stat.getUsuariCodi(), stat.getTipus(), stat.getOrigen()),
                stat));

        // Notificacions creades
        for (ExplotEnvStats explotEnvStats : explotFetsRepository.getStatsCreacioPerDay(iniciDelDia, finalDelDia)) {
            if (statsMap.get(explotEnvStats.getKey()) == null) {
                log.warn("[EstadisticaService.getStatsCreacioPerDay] statMap no conte la key " + explotEnvStats.getKey());
                return;
            }
            statsMap.get(explotEnvStats.getKey()).setTrCreades(explotEnvStats.getTotalEnviaments());
        }

        // Notificacions amb error a l'intentar registrar
        explotFetsRepository.getStatsRegEnviamentErrorPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            if (statsMap.get(stat.getKey()) == null) {
                log.warn("[EstadisticaService.getStatsRegEnviamentErrorPerDay] statMap no conte la key " + stat.getKey());
                return;
            }
            statsMap.get(stat.getKey()).setTrRegEnviadesError(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setIntentsRegistre(stat.getIntentsMig().longValue());
        });

        // Notificacions registrades
        explotFetsRepository.getStatsRegistradaPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            if (statsMap.get(stat.getKey()) == null) {
                log.warn("[EstadisticaService.getStatsRegistradaPerDay] statMap no conte la key " + stat.getKey());
                return;
            }
            statsMap.get(stat.getKey()).setTrRegistrades(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setTemsMigPendent(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setIntentsRegistre(stat.getIntentsMig().longValue());
        });

        // Comunicacions acceptades via SIR
        explotFetsRepository.getStatsRegAcceptadaPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            if (statsMap.get(stat.getKey()) == null) {
                log.warn("[EstadisticaService.getStatsRegAcceptadaPerDay] statMap no conte la key " + stat.getKey());
                return;
            }
            statsMap.get(stat.getKey()).setTrSirAcceptades(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setTemsMigRegistrada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigRegistradaPerSirAcceptada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setIntentsRegistre(stat.getIntentsMig().longValue());
            statsMap.get(stat.getKey()).setTemsMigTotal(stat.getTempsTotal().longValue());
        });

        // Comunicacions rebutjades via SIR
        explotFetsRepository.getStatsRegRebutjadaPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            if (statsMap.get(stat.getKey()) == null) {
                log.warn("[EstadisticaService.getStatsRegRebutjadaPerDay] statMap no conte la key " + stat.getKey());
                return;
            }
            statsMap.get(stat.getKey()).setTrSirRebutjades(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setTemsMigRegistrada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigRegistradaPerSirRebutjada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setIntentsRegistre(stat.getIntentsMig().longValue());
            statsMap.get(stat.getKey()).setTemsMigTotal(stat.getTempsTotal().longValue());
        });

        // Notificacions amb error a l'intentar enviar a Notifica
        explotFetsRepository.getStatsNotEnviamentErrorPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            if (statsMap.get(stat.getKey()) == null) {
                log.warn("[EstadisticaService.getStatsNotEnviamentErrorPerDay] statMap no conte la key " + stat.getKey());
                return;
            }
            statsMap.get(stat.getKey()).setTrNotEnviadesError(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setIntentsRegistre(stat.getIntentsMig().longValue());
        });

        // Notificacions enviades a Notifica
        explotFetsRepository.getStatsNotEnviadaPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            if (statsMap.get(stat.getKey()) == null) {
                log.warn("[EstadisticaService.getStatsNotEnviadaPerDay] statMap no conte la key " + stat.getKey());
                return;
            }
            statsMap.get(stat.getKey()).setTrNotEnviades(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setTemsMigRegistrada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigRegistradaPerNotificada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setIntentsNotEnviament(stat.getIntentsMig().longValue());
        });

        // Notificacions notificades via Notifica
        explotFetsRepository.getStatsNotNotificadaPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            if (statsMap.get(stat.getKey()) == null) {
                log.warn("[EstadisticaService.getStatsNotNotificadaPerDay] statMap no conte la key " + stat.getKey());
                return;
            }
            statsMap.get(stat.getKey()).setTrNotNotificades(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setTemsMigNotEnviada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigNotEnviadaPerNotificada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigTotalPerNotAcceptada(stat.getTempsTotal().longValue());
            statsMap.get(stat.getKey()).setTemsMigTotal(stat.getTempsTotal().longValue());
        });

        // Notificacions rebutjades via Notifica
        explotFetsRepository.getStatsNotRebutjadaPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            if (statsMap.get(stat.getKey()) == null) {
                log.warn("[EstadisticaService.getStatsNotRebutjadaPerDay] statMap no conte la key " + stat.getKey());
                return;
            }
            statsMap.get(stat.getKey()).setTrNotRebujtades(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setTemsMigNotEnviada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigNotEnviadaPerRebubjada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigTotalPerNotRebutjada(stat.getTempsTotal().longValue());
            statsMap.get(stat.getKey()).setTemsMigTotal(stat.getTempsTotal().longValue());
        });

        // Notificacions expirades via Notifica
        explotFetsRepository.getStatsNotExpiradaPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            if (statsMap.get(stat.getKey()) == null) {
                log.warn("[EstadisticaService.getStatsNotExpiradaPerDay] statMap no conte la key " + stat.getKey());
                return;
            }
            statsMap.get(stat.getKey()).setTrNotExpirades(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setTemsMigNotEnviada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigNotEnviadaPerExpirada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigTotalPerNotExpirada(stat.getTempsTotal().longValue());
            statsMap.get(stat.getKey()).setTemsMigTotal(stat.getTempsTotal().longValue());
        });

        // Notificacions fallades via Notifica
        explotFetsRepository.getStatsNotErrorPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            if (statsMap.get(stat.getKey()) == null) {
                log.warn("[EstadisticaService.getStatsNotErrorPerDay] statMap no conte la key " + stat.getKey());
                return;
            }
            statsMap.get(stat.getKey()).setTrNotFallades(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setTemsMigNotEnviada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigNotEnviadaPerFallada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigTotalPerNotFallada(stat.getTempsTotal().longValue());
            statsMap.get(stat.getKey()).setTemsMigTotal(stat.getTempsTotal().longValue());
        });

        // Notificacions amb error a l'intentar enviar al CIE
        explotFetsRepository.getStatsCieEnviamentErrorPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            if (statsMap.get(stat.getKey()) == null) {
                log.warn("[EstadisticaService.getStatsCieEnviamentErrorPerDay] statMap no conte la key " + stat.getKey());
                return;
            }
            statsMap.get(stat.getKey()).setTrCieEnviadesError(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setIntentsCieEnviament(stat.getIntentsMig().longValue());
        });

        // Notificacions enviades al CIE
        explotFetsRepository.getStatsCieEnviadaPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            if (statsMap.get(stat.getKey()) == null) {
                log.warn("[EstadisticaService.getStatsCieEnviadaPerDay] statMap no conte la key " + stat.getKey());
                return;
            }
            statsMap.get(stat.getKey()).setTrCieEnviades(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setIntentsCieEnviament(stat.getIntentsMig().longValue());
        });

        // Notificacions notificades via CIE
        explotFetsRepository.getStatsCieNotificadaPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            if (statsMap.get(stat.getKey()) == null) {
                log.warn("[EstadisticaService.getStatsCieNotificadaPerDay] statMap no conte la key " + stat.getKey());
                return;
            }
            statsMap.get(stat.getKey()).setTrCieNotificades(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setTemsMigCieEnviada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigCieEnviadaPerNotificada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigTotalPerCieAcceptada(stat.getTempsTotal().longValue());
            statsMap.get(stat.getKey()).setTemsMigTotal(stat.getTempsTotal().longValue());
        });

        // Notificacions rebutjades via CIE
        explotFetsRepository.getStatsCieRebutjadaPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            if (statsMap.get(stat.getKey()) == null) {
                log.warn("[EstadisticaService.getStatsCieRebutjadaPerDay] statMap no conte la key " + stat.getKey());
                return;
            }
            statsMap.get(stat.getKey()).setTrCieRebutjades(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setTemsMigCieEnviada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigCieEnviadaPerRebubjada(stat.getTempsMigEstat().longValue());
        });

        // Notificacions cancel·lades via CIE
        explotFetsRepository.getStatsCieCanceladaPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            if (statsMap.get(stat.getKey()) == null) {
                log.warn("[EstadisticaService.getStatsCieCanceladaPerDay] statMap no conte la key " + stat.getKey());
                return;
            }
            statsMap.get(stat.getKey()).setTrCieCancelades(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setTemsMigCieEnviada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigCieEnviadaPerCancelada(stat.getTempsMigEstat().longValue());
        });

        // Notificacions fallades via CIE
        explotFetsRepository.getStatsCieErrorPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            if (statsMap.get(stat.getKey()) == null) {
                log.warn("[EstadisticaService.getStatsCieErrorPerDay] statMap no conte la key " + stat.getKey());
                return;
            }
            statsMap.get(stat.getKey()).setTrCieFallades(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setTemsMigCieEnviada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigCieEnviadaPerFallada(stat.getTempsMigEstat().longValue());
        });

        // Notificacions amb error a l'intentar enviar via email
        explotFetsRepository.getStatsEmailEnviamentErrorPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            if (statsMap.get(stat.getKey()) == null) {
                log.warn("[EstadisticaService.getStatsEmailEnviamentErrorPerDay] statMap no conte la key " + stat.getKey());
                return;
            }
            statsMap.get(stat.getKey()).setTrEmailEnviadesError(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setIntentsEmailEnviament(stat.getIntentsMig().longValue());
        });

        // Notificacions enviades via email
        explotFetsRepository.getStatsEmailEnviadaPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            if (statsMap.get(stat.getKey()) == null) {
                log.warn("[EstadisticaService.getStatsEmailEnviadaPerDay] statMap no conte la key " + stat.getKey());
                return;
            }
            statsMap.get(stat.getKey()).setTrEmailEnviades(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setTemsMigRegistrada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigRegistradaPerEmail(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setIntentsEmailEnviament(stat.getIntentsMig().longValue());
        });

    }

    private int compareEstadistiquesAndDimensions(ExplotFets estadistiques, ExplotDimensioEntity dimension) {
        int result = compareFields(estadistiques.getEntitatId(), dimension.getEntitatId());
        if (result != 0) return result;

        result = compareProcediments(estadistiques.getProcedimentId(), dimension.getProcedimentId());
        if (result != 0) return result;

        result = compareFields(estadistiques.getOrganCodi(), dimension.getOrganCodi());
        if (result != 0) return result;

        result = compareFields(estadistiques.getTipus(), dimension.getTipus());
        if (result != 0) return result;

        result = compareFields(estadistiques.getOrigen(), dimension.getOrigen());
        if (result != 0) return result;

        return compareFields(estadistiques.getUsuariCodi(), dimension.getUsuariCodi());
    }

    private <T extends Comparable<T>> int compareFields(T field1, T field2) {
        if (field1 == null && field2 == null) return 0;
        if (field1 == null) return -1;
        if (field2 == null) return 1;
        return field1.compareTo(field2);
    }

    private int compareProcediments(Long procediment1, Long procediment2) {
        if (procediment1 == null && procediment2 == null) return 0;
        if (procediment1 == null) return -1;
        if (procediment2 == null) return 1;
        return procediment1.compareTo(procediment2);
    }


    private void saveToFetsEntity(ExplotFets estadistiques, ExplotDimensioEntity dimension, ExplotTempsEntity ete) {
        ExplotFetsEntity fetsEntity = new ExplotFetsEntity(dimension, ete, estadistiques);
        explotFetsRepository.save(fetsEntity);
    }

    private RegistresEstadistics toRegistresEstadistics(List<ExplotFetsEntity> fets, LocalDate data) {

        var offSetDataTime = data.atStartOfDay().atOffset(ZoneId.systemDefault().getRules().getOffset(java.time.Instant.now()));
        return new RegistresEstadistics()
                .temps(offSetDataTime)
                .fets(fets.stream().map(this::toRegistreEstadistic).collect(Collectors.toList()));
    }

    private RegistreEstadistic toRegistreEstadistic(ExplotFetsEntity fet) {
        return new RegistreEstadistic().dimensions(toDimensions(fet.getDimensio())).fets(toFets(fet));
    }

    private List<Dimensio> toDimensions(ExplotDimensioEntity dimensio) {
        return List.of(
                new DimensioNotib(DimEnum.ENT, dimensio.getEntitatCodi()),
                new DimensioNotib(DimEnum.PRC, dimensio.getProcedimentCodi()),
                new DimensioNotib(DimEnum.ORG, dimensio.getOrganCodi()),
                new DimensioNotib(DimEnum.USU, dimensio.getUsuariCodi()),
                new DimensioNotib(DimEnum.TIP, dimensio.getTipus()),
                new DimensioNotib(DimEnum.ORI, dimensio.getOrigen())
        );
    }

    private List<Fet> toFets(ExplotFetsEntity fet) {
        return List.of(
                new FetNotib(PND, fet.getPendent()),
                new FetNotib(REG_ERR, fet.getRegEnviamentError()),
                new FetNotib(REG, fet.getRegistrada()),
                new FetNotib(SIR_ACC, fet.getRegAcceptada()),
                new FetNotib(SIR_REB, fet.getRegRebutjada()),
                new FetNotib(NOT_ERR, fet.getNotEnviamentError()),
                new FetNotib(NOT_ENV, fet.getNotEnviada()),
                new FetNotib(NOT_ACC, fet.getNotNotificada()),
                new FetNotib(NOT_REB, fet.getNotRebutjada()),
                new FetNotib(NOT_EXP, fet.getNotExpirada()),
                new FetNotib(CIE_ERR, fet.getCieEnviamentError()),
                new FetNotib(CIE_ENV, fet.getCieEnviada()),
                new FetNotib(CIE_ACC, fet.getCieNotificada()),
                new FetNotib(CIE_REB, fet.getCieRebutjada()),
                new FetNotib(CIE_FAL, fet.getCieError()),
                new FetNotib(PRC, fet.getProcessada()),

                // Transicions
                new FetNotib(TR_CRE, fet.getTrCreades()),
                new FetNotib(TR_REG_ERR, fet.getTrRegEnviadesError()),
                new FetNotib(TR_REG, fet.getTrRegistrades()),
                new FetNotib(TR_SIR_ACC, fet.getTrSirAcceptades()),
                new FetNotib(TR_SIR_REB, fet.getTrSirRebutjades()),
                new FetNotib(TR_NOT_ERR, fet.getTrNotEnviadesError()),
                new FetNotib(TR_NOT_ENV, fet.getTrNotEnviades()),
                new FetNotib(TR_NOT_ACC, fet.getTrNotNotificades()),
                new FetNotib(TR_NOT_REB, fet.getTrNotRebujtades()),
                new FetNotib(TR_NOT_EXP, fet.getTrNotExpirades()),
                new FetNotib(TR_NOT_FAL, fet.getTrNotFallades()),
                new FetNotib(TR_CIE_ERR, fet.getTrCieEnviadesError()),
                new FetNotib(TR_CIE_ENV, fet.getTrCieEnviades()),
                new FetNotib(TR_CIE_ACC, fet.getTrCieNotificades()),
                new FetNotib(TR_CIE_REB, fet.getTrCieRebutjades()),
                new FetNotib(TR_CIE_CAN, fet.getTrCieCancelades()),
                new FetNotib(TR_CIE_FAL, fet.getTrCieFallades()),
                new FetNotib(TR_EML_ERR, fet.getTrEmailEnviadesError()),
                new FetNotib(TR_EML_ENV, fet.getTrEmailEnviades()),

                // Temps mitjà en estat
                new FetNotib(TMP_PND, fet.getTemsMigPendent()),
                new FetNotib(TMP_REG, fet.getTemsMigRegistrada()),
                new FetNotib(TMP_NOT, fet.getTemsMigNotEnviada()),
                new FetNotib(TMP_CIE, fet.getTemsMigCieEnviada()),
                new FetNotib(TMP_TOT, fet.getTemsMigTotal()),
                new FetNotib(TMP_REG_SAC, fet.getTemsMigRegistradaPerSirAcceptada()),
                new FetNotib(TMP_REG_SRB, fet.getTemsMigRegistradaPerSirRebutjada()),
                new FetNotib(TMP_REG_NOT, fet.getTemsMigRegistradaPerNotificada()),
                new FetNotib(TMP_REG_EML, fet.getTemsMigRegistradaPerEmail()),
                new FetNotib(TMP_NOT_NOT, fet.getTemsMigNotEnviadaPerNotificada()),
                new FetNotib(TMP_NOT_REB, fet.getTemsMigNotEnviadaPerRebubjada()),
                new FetNotib(TMP_NOT_EXP, fet.getTemsMigNotEnviadaPerExpirada()),
                new FetNotib(TMP_NOT_FAL, fet.getTemsMigNotEnviadaPerFallada()),
                new FetNotib(TMP_CIE_NOT, fet.getTemsMigCieEnviadaPerNotificada()),
                new FetNotib(TMP_CIE_REB, fet.getTemsMigCieEnviadaPerRebubjada()),
                new FetNotib(TMP_CIE_CAN, fet.getTemsMigCieEnviadaPerCancelada()),
                new FetNotib(TMP_CIE_FAL, fet.getTemsMigCieEnviadaPerFallada()),
                new FetNotib(TMP_TOT_NAC, fet.getTemsMigTotalPerNotAcceptada()),
                new FetNotib(TMP_TOT_NRB, fet.getTemsMigTotalPerNotRebutjada()),
                new FetNotib(TMP_TOT_NEX, fet.getTemsMigTotalPerNotExpirada()),
                new FetNotib(TMP_TOT_NFL, fet.getTemsMigTotalPerNotFallada()),
                new FetNotib(TMP_TOT_CAC, fet.getTemsMigTotalPerCieAcceptada()),

                // Intents
                new FetNotib(INT_REG, fet.getIntentsRegistre()),
                new FetNotib(INT_SIR, fet.getIntentsSir()),
                new FetNotib(INT_NOT, fet.getIntentsNotEnviament()),
                new FetNotib(INT_CIE, fet.getIntentsCieEnviament()),
                new FetNotib(INT_EML, fet.getIntentsEmailEnviament())
        );
    }

}
