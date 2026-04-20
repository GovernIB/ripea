package es.caib.ripea.service.test.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEstatEntity;
import es.caib.ripea.persistence.entity.GrupEntity;
import es.caib.ripea.persistence.entity.MetaDadaEntity;
import es.caib.ripea.persistence.entity.MetaDocumentEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.MetaExpedientTascaEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.PinbalServeiEntity;
import es.caib.ripea.persistence.entity.TipusDocumentalEntity;
import es.caib.ripea.persistence.entity.UsuariEntity;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.persistence.repository.ExpedientEstatRepository;
import es.caib.ripea.persistence.repository.GrupRepository;
import es.caib.ripea.persistence.repository.MetaDadaRepository;
import es.caib.ripea.persistence.repository.MetaDocumentRepository;
import es.caib.ripea.persistence.repository.MetaExpedientRepository;
import es.caib.ripea.persistence.repository.MetaExpedientTascaRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.persistence.repository.PinbalServeiRepository;
import es.caib.ripea.persistence.repository.TipusDocumentalRepository;
import es.caib.ripea.persistence.repository.UsuariRepository;
import es.caib.ripea.service.intf.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.service.intf.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.service.intf.dto.MultiplicitatEnumDto;
import es.caib.ripea.service.intf.dto.NtiOrigenEnumDto;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import es.caib.ripea.service.intf.dto.TipusClassificacioEnumDto;
import es.caib.ripea.service.intf.dto.TipusProcedimentServeiEnum;

/**
 * Factoria que crea el conjunt de dades bàsic per als tests d'integració
 * del mòdul de serveis.
 *
 * Conjunt bàsic:
 *   - 1 Entitat
 *   - 1 Grup (requerit com a grupPerDefecte de MetaExpedient)
 *   - 1 PinbalServei (requerit per MetaDocument)
 *   - 3 Procediments (MetaExpedient), cadascun amb:
 *       · 3 Meta-documents
 *       · 3 Meta-dades (associades al procediment)
 *       · 3 Estats (ExpedientEstat)
 *       · 3 Tasques (MetaExpedientTasca)
 *
 * Nota: GrupEntity.rol no té setter (només @Getter al entity), per la qual cosa
 * s'assigna via ReflectionTestUtils per no modificar el codi de producció.
 */
@Component
public class TestDataFactory {

    @Autowired private EntitatRepository            entitatRepository;
    @Autowired private TipusDocumentalRepository	tipusDocumentalRepository;
    @Autowired private GrupRepository               grupRepository;
    @Autowired private PinbalServeiRepository       pinbalServeiRepository;
    @Autowired private MetaExpedientRepository      metaExpedientRepository;
    @Autowired private MetaDocumentRepository       metaDocumentRepository;
    @Autowired private MetaDadaRepository           metaDadaRepository;
    @Autowired private ExpedientEstatRepository     expedientEstatRepository;
    @Autowired private MetaExpedientTascaRepository metaExpedientTascaRepository;
    @Autowired private OrganGestorRepository        organGestorRepository;
    @Autowired private UsuariRepository             usuariRepository;

    /**
     * Crea i desa a la BBDD el conjunt bàsic de dades de test.
     * S'ha de cridar dins d'un context transaccional actiu.
     *
     * @return TestData amb les referències a totes les entitats creades.
     */
    public TestData createBaseTestData() {
        TestData data = new TestData();

        // --- Entitat ---
        data.entitat = entitatRepository.save(
                EntitatEntity.getBuilder(
                        "ENT_TEST",
                        "Entitat de test",
                        "Descripció de l'entitat de proves",
                        "A00000000",
                        "A00000000"
                ).build()
        );

        tipusDocumentalRepository.save(TipusDocumentalEntity.getBuilder("TD01", "Resolución", data.entitat, "Resolució").build());
        
        // --- Usuaris de @WithMockUser (necessaris per a la conversió entitat→DTO) ---
        // ConversioTipusHelper cerca l'usuari per createdBy/lastModifiedBy. Si no existeix, llança NPE.
        data.usuari = usuariRepository.save(
                UsuariEntity.getBuilder("usuari1", "Usuari de test", "00000001T", null, null).build()
        );

        // --- Grup (grupPerDefecte dels MetaExpedients) ---
        // GrupEntity.rol és NOT NULL però no té setter: s'assigna via reflexió.
        GrupEntity grup = GrupEntity.getBuilder(
                "GRP_TEST", "Grup de test", data.entitat, null
        ).build();
        ReflectionTestUtils.setField(grup, "rol", "ROLE_ADMIN");
        data.grup = grupRepository.save(grup);

        // --- PinbalServei (pinbalServei dels MetaDocuments) ---
        data.pinbalServei = pinbalServeiRepository.save(
                PinbalServeiEntity.builder()
                        .codi("PS_TEST")
                        .nom("Servei Pinbal de test")
                        .actiu(true)
                        .build()
        );

        // --- 2 Organs Gestors (necessaris per als tests de filtre i ordenació per organGestor) ---
        OrganGestorEntity organ1 = OrganGestorEntity.getBuilder("ORG_TEST_1")
                .nom("Organ Gestor 1")
                .entitat(data.entitat)
                .actiu(true)
                .build();
        data.organs.add(organGestorRepository.save(organ1));

        OrganGestorEntity organ2 = OrganGestorEntity.getBuilder("ORG_TEST_2")
                .nom("Organ Gestor 2")
                .entitat(data.entitat)
                .actiu(true)
                .build();
        data.organs.add(organGestorRepository.save(organ2));

        // --- 3 Procediments base amb les seves entitats dependents ---
        for (int i = 1; i <= 3; i++) {
            MetaExpedientEntity metaExp = MetaExpedientEntity.getBuilder(
                    String.format("PROC_%02d", i),
                    "Procediment " + i,
                    "Descripció del procediment " + i,
                    "SERIE_0" + i,
                    "SIA00" + i,
                    false,   // notificacioActiva
                    false,   // permetMetadocsGenerals
                    data.entitat,
                    null,    // pare
                    null,    // organGestor
                    false,   // gestioAmbGrupsActiva
                    false,   // interessatObligatori
                    false    // permisDirecte
            ).tipusClassificacio(TipusClassificacioEnumDto.SIA).build();
            metaExp.setGrupPerDefecte(data.grup);
            MetaExpedientEntity savedMetaExp = metaExpedientRepository.save(metaExp);
            data.metaExpedients.add(savedMetaExp);

            // 3 Meta-documents per procediment
            for (int j = 1; j <= 3; j++) {
                data.metaDocuments.add(
                        metaDocumentRepository.save(
                                MetaDocumentEntity.getBuilder(
                                        data.entitat,
                                        String.format("METADOC_%d_%d", i, j),
                                        "Meta-document " + i + "." + j,
                                        MultiplicitatEnumDto.M_0_N,
                                        savedMetaExp,
                                        NtiOrigenEnumDto.O1,
                                        DocumentNtiEstadoElaboracionEnumDto.EE01,
                                        "TD01",
                                        false,  // pinbalActiu
                                        null,   // pinbalFinalitat
                                        j       // ordre
                                ).pinbalServei(data.pinbalServei).build()
                        )
                );
            }

            // 3 Meta-dades per procediment
            for (int j = 1; j <= 3; j++) {
                data.metaDades.add(
                        metaDadaRepository.save(
                                MetaDadaEntity.getBuilder(
                                        String.format("METADADA_%d_%d", i, j),
                                        "Meta-dada " + i + "." + j,
                                        MetaDadaTipusEnumDto.TEXT,
                                        MultiplicitatEnumDto.M_0_1,
                                        null,   // valor per defecte
                                        false,  // readOnly
                                        j,      // ordre
                                        savedMetaExp,
                                        false,  // noAplica
                                        false,  // enviable
                                        null    // metadadaArxiu
                                ).build()
                        )
                );
            }

            // 3 Estats per procediment
            for (int j = 1; j <= 3; j++) {
                data.estats.add(
                        expedientEstatRepository.save(
                                ExpedientEstatEntity.getBuilder(
                                        String.format("EST_%d_%d", i, j),
                                        "Estat " + i + "." + j,
                                        j,
                                        "#3366FF",
                                        savedMetaExp,
                                        null  // responsableCodi
                                ).build()
                        )
                );
            }

            // 3 Tasques per procediment
            for (int j = 1; j <= 3; j++) {
                data.tasques.add(
                        metaExpedientTascaRepository.save(
                                MetaExpedientTascaEntity.getBuilder(
                                        String.format("TASCA_%d_%d", i, j),
                                        "Tasca " + i + "." + j,
                                        "Descripció de la tasca " + i + "." + j,
                                        null,   // responsable
                                        savedMetaExp,
                                        null,   // dataLimit
                                        null,   // duracio
                                        PrioritatEnumDto.B_NORMAL,
                                        null,   // estatCrearTasca
                                        null    // estatFinalitzarTasca
                                ).build()
                        )
                );
            }
        }

        // --- Procediment 4: tipus SERVEI (per al test de filtre per tipus) ---
        MetaExpedientEntity metaExp4 = MetaExpedientEntity.getBuilder(
                "PROC_04", "Procediment 4", "Descripció del procediment 4",
                "SERIE_04", "SIA004",
                false, false, data.entitat, null, null, false, false, false
        ).tipusClassificacio(TipusClassificacioEnumDto.SIA)
         .tipusProcedimentServei(TipusProcedimentServeiEnum.SERVEI)
         .build();
        metaExp4.setGrupPerDefecte(data.grup);
        data.metaExpedients.add(metaExpedientRepository.save(metaExp4));

        // --- Procediment 5: inactiu (per al test de filtre per actiu) ---
        MetaExpedientEntity metaExp5 = MetaExpedientEntity.getBuilder(
                "PROC_05", "Procediment 5", "Descripció del procediment 5",
                "SERIE_05", "SIA005",
                false, false, data.entitat, null, null, false, false, false
        ).tipusClassificacio(TipusClassificacioEnumDto.SIA).build();
        metaExp5.setGrupPerDefecte(data.grup);
        metaExp5.updateActiu(false);
        data.metaExpedients.add(metaExpedientRepository.save(metaExp5));

        // --- Procediment 6: amb organGestor 1 (per als tests de filtre i ordenació per organGestor) ---
        MetaExpedientEntity metaExp6 = MetaExpedientEntity.getBuilder(
                "PROC_06", "Procediment 6", "Descripció del procediment 6",
                "SERIE_06", "SIA006",
                false, false, data.entitat, null, data.organs.get(0), false, false, false
        ).tipusClassificacio(TipusClassificacioEnumDto.SIA).build();
        metaExp6.setGrupPerDefecte(data.grup);
        data.metaExpedients.add(metaExpedientRepository.save(metaExp6));

        // --- Procediment 7: amb organGestor 2 (per al test d'ordenació per organGestor) ---
        MetaExpedientEntity metaExp7 = MetaExpedientEntity.getBuilder(
                "PROC_07", "Procediment 7", "Descripció del procediment 7",
                "SERIE_07", "SIA007",
                false, false, data.entitat, null, data.organs.get(1), false, false, false
        ).tipusClassificacio(TipusClassificacioEnumDto.SIA).build();
        metaExp7.setGrupPerDefecte(data.grup);
        data.metaExpedients.add(metaExpedientRepository.save(metaExp7));

        // --- Entitat 2 (per validar que els filtres d'entitat aïllen correctament ENT_TEST) ---
        data.entitat2 = entitatRepository.save(
                EntitatEntity.getBuilder(
                        "ENT_TEST2",
                        "Entitat de test 2",
                        "Descripció de la segona entitat de proves",
                        "B00000000",
                        "B00000000"
                ).build()
        );

        // --- Procediment 8: pertany a ENT_TEST2 (no ha d'aparèixer en les consultes de ENT_TEST) ---
        MetaExpedientEntity metaExp8 = MetaExpedientEntity.getBuilder(
                "PROC_08", "Procediment 8", "Descripció del procediment 8",
                "SERIE_08", "SIA008",
                false, false, data.entitat2, null, null, false, false, false
        ).tipusClassificacio(TipusClassificacioEnumDto.SIA).build();
        data.metaExpedients.add(metaExpedientRepository.save(metaExp8));

        return data;
    }

    /**
     * Contenidor de les referències a les entitats creades pel conjunt bàsic de dades.
     *
     * Conté 8 procediments en total:
     *   - PROC_01..03: actius, tipus PROCEDIMENT, sense organGestor (amb metadades, documents, estats, tasques)
     *   - PROC_04: actiu, tipus SERVEI, sense organGestor
     *   - PROC_05: inactiu, tipus PROCEDIMENT, sense organGestor
     *   - PROC_06: actiu, tipus PROCEDIMENT, amb organGestor organs[0]
     *   - PROC_07: actiu, tipus PROCEDIMENT, amb organGestor organs[1]
     *   - PROC_08: pertany a ENT_TEST2 (no apareix en consultes filtrades per ENT_TEST)
     */
    public static class TestData {
        public EntitatEntity            entitat;
        public EntitatEntity            entitat2;
        public UsuariEntity             usuari;
        public GrupEntity               grup;
        public PinbalServeiEntity       pinbalServei;
        public final List<OrganGestorEntity>        organs         = new ArrayList<>();
        public final List<MetaExpedientEntity>      metaExpedients = new ArrayList<>();
        public final List<MetaDocumentEntity>       metaDocuments  = new ArrayList<>();
        public final List<MetaDadaEntity>           metaDades      = new ArrayList<>();
        public final List<ExpedientEstatEntity>     estats         = new ArrayList<>();
        public final List<MetaExpedientTascaEntity> tasques        = new ArrayList<>();
    }
}
