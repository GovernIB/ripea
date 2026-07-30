package es.caib.ripea.persistence.test;

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
import es.caib.ripea.persistence.entity.PinbalServeiEntity;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.persistence.repository.ExpedientEstatRepository;
import es.caib.ripea.persistence.repository.GrupRepository;
import es.caib.ripea.persistence.repository.MetaDadaRepository;
import es.caib.ripea.persistence.repository.MetaDocumentRepository;
import es.caib.ripea.persistence.repository.MetaExpedientRepository;
import es.caib.ripea.persistence.repository.MetaExpedientTascaRepository;
import es.caib.ripea.persistence.repository.PinbalServeiRepository;
import es.caib.ripea.service.intf.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.service.intf.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.service.intf.dto.MultiplicitatEnumDto;
import es.caib.ripea.service.intf.dto.NtiOrigenEnumDto;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import es.caib.ripea.service.intf.dto.TipusClassificacioEnumDto;

/**
 * Factoria que crea el conjunt de dades bàsic per als tests d'integració.
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

    @Autowired private EntitatRepository           entitatRepository;
    @Autowired private GrupRepository              grupRepository;
    @Autowired private PinbalServeiRepository      pinbalServeiRepository;
    @Autowired private MetaExpedientRepository     metaExpedientRepository;
    @Autowired private MetaDocumentRepository      metaDocumentRepository;
    @Autowired private MetaDadaRepository          metaDadaRepository;
    @Autowired private ExpedientEstatRepository    expedientEstatRepository;
    @Autowired private MetaExpedientTascaRepository metaExpedientTascaRepository;

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

        // --- 3 Procediments amb les seves entitats dependents ---
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
                                        j,
                                        "Descripció de la tasca " + i + "." + j,
                                        null,   // responsable
                                        savedMetaExp,
                                        null,   // dataLimit
                                        null,   // duracio
                                        PrioritatEnumDto.B_NORMAL,
                                        false,
                                        null,   // estatCrearTasca
                                        null    // estatFinalitzarTasca
                                ).build()
                        )
                );
            }
        }

        return data;
    }

    /**
     * Contenidor de les referències a les entitats creades pel conjunt bàsic de dades.
     */
    public static class TestData {
        public EntitatEntity            entitat;
        public GrupEntity               grup;
        public PinbalServeiEntity       pinbalServei;
        public final List<MetaExpedientEntity>      metaExpedients = new ArrayList<>();
        public final List<MetaDocumentEntity>       metaDocuments  = new ArrayList<>();
        public final List<MetaDadaEntity>           metaDades      = new ArrayList<>();
        public final List<ExpedientEstatEntity>     estats         = new ArrayList<>();
        public final List<MetaExpedientTascaEntity> tasques        = new ArrayList<>();
    }
}
