package es.caib.ripea.core.helper;

import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.ListUtils;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
public class MetaExpedientFiltre {

    private static final int DEFAULT_PARTITIONS_SIZE = 4;
    private static final int MAX_PARTITION_ELEMENTS = 999;

    private EntitatEntity entitat;
    private boolean esNullActiu;
    private Boolean actiu;
    private boolean esNullFiltre;
    private String filtre;
    private boolean esAdminEntitat;
    private boolean esAdminOrgan;
    private boolean esNullMetaExpedientIdPermesos;
    private List<Boolean> esNullMetaExpedientIdPermesosSplit;
    private List<Long> metaExpedientIdPermesos;
    private List<List<Long>> metaExpedientIdPermesosSplit;
    private boolean esNullOrganCodiPermesos;
    private List<Boolean> esNullOrganCodiPermesosSplit;
    private List<String> organCodiPermesos;
    private List<List<String>> organCodiPermesosSplit;
    private boolean esNullMetaExpedientOrganIdPermesos;
    private List<Boolean> esNullMetaExpedientOrganIdPermesosSplit;
    private List<Long> metaExpedientOrganIdPermesos;
    private List<List<Long>> metaExpedientOrganIdPermesosSplit;
    private boolean revisioActiva;
    private boolean organGestorIComu;
    private OrganGestorEntity organ;
    private boolean allComuns;

    public Boolean isNullMetaExpedientIdPermesos(int index) {
        return this.esNullMetaExpedientIdPermesosSplit.get(index);
    }
    public List<Long> getMetaExpedientIdPermesos(int index) {
        return getList(this.metaExpedientIdPermesosSplit, index);
    }

    public Boolean isNullOrganCodiPermesos(int index) {
        return this.esNullOrganCodiPermesosSplit.get(index);
    }
    public List<String> getOrganCodiPermesos(int index) {
        return getList(this.organCodiPermesosSplit, index);
    }

    public Boolean isNullMetaExpedientOrganIdPermesos(int index) {
        return this.esNullMetaExpedientOrganIdPermesosSplit.get(index);
    }
    public List<Long> getMetaExpedientOrganIdPermesos(int index) {
        return getList(this.metaExpedientOrganIdPermesosSplit, index);
    }

    public static class MetaExpedientFiltreBuilder {
        private boolean esNullActiu;
        private Boolean actiu;
        private boolean esNullFiltre;
        private String filtre;
        private boolean esNullMetaExpedientIdPermesos;
        private List<Boolean> esNullMetaExpedientIdPermesosSplit;
        private List<Long> metaExpedientIdPermesos;
        private List<List<Long>> metaExpedientIdPermesosSplit;
        private boolean esNullOrganCodiPermesos;
        private List<Boolean> esNullOrganCodiPermesosSplit;
        private List<String> organCodiPermesos;
        private List<List<String>> organCodiPermesosSplit;
        private boolean esNullMetaExpedientOrganIdPermesos;
        private List<Boolean> esNullMetaExpedientOrganIdPermesosSplit;
        private List<Long> metaExpedientOrganIdPermesos;
        private List<List<Long>> metaExpedientOrganIdPermesosSplit;

        public MetaExpedientFiltreBuilder actiu(boolean actiu) {
            this.actiu = actiu;
            this.esNullActiu = !actiu;
            return this;
        }

        public MetaExpedientFiltreBuilder filtre(String filtre) {
            this.filtre = filtre == null ? "" : filtre;
            this.esNullFiltre = filtre == null || "".equals(filtre.trim());
            return this;
        }

        public MetaExpedientFiltreBuilder metaExpedientIdPermesos(List<Long> metaExpedientIdPermesos) {
            this.metaExpedientIdPermesos = Utils.getNullIfEmpty(metaExpedientIdPermesos);
            this.metaExpedientIdPermesosSplit = listSplit(metaExpedientIdPermesos);
            this.esNullMetaExpedientIdPermesos = metaExpedientIdPermesos == null || metaExpedientIdPermesos.isEmpty();
            this.esNullMetaExpedientIdPermesosSplit = esNullSplit(this.metaExpedientIdPermesosSplit);
            return this;
        }

        public MetaExpedientFiltreBuilder organCodiPermesos(List<String> organCodiPermesos) {
            this.organCodiPermesos = Utils.getNullIfEmpty(organCodiPermesos);
            this.organCodiPermesosSplit = listSplit(organCodiPermesos);
            this.esNullOrganCodiPermesos = organCodiPermesos == null || organCodiPermesos.isEmpty();
            this.esNullOrganCodiPermesosSplit = esNullSplit(this.organCodiPermesosSplit);
            return this;
        }

        public MetaExpedientFiltreBuilder metaExpedientOrganIdPermesos(List<Long> metaExpedientOrganIdPermesos) {
            this.metaExpedientOrganIdPermesos = Utils.getNullIfEmpty(metaExpedientOrganIdPermesos);
            this.metaExpedientOrganIdPermesosSplit = listSplit(metaExpedientOrganIdPermesos);
            this.esNullMetaExpedientOrganIdPermesos = metaExpedientOrganIdPermesos == null || metaExpedientOrganIdPermesos.isEmpty();
            this.esNullMetaExpedientOrganIdPermesosSplit = esNullSplit(this.metaExpedientOrganIdPermesosSplit);
            return this;
        }
    }

    private static <T> List<List<T>> listSplit(List<T> list) {

        if (list == null || list.isEmpty()) {
            return initializeSplits();
        } else {
            List<List<T>> sublists = new ArrayList<>(ListUtils.partition(list, MAX_PARTITION_ELEMENTS));

            while (sublists.size() < DEFAULT_PARTITIONS_SIZE) {
                sublists.add(new ArrayList<T>());
            }
            return sublists;
        }
    }
    private static <T> List<List<T>> initializeSplits() {
        List<List<T>> newSplits = new ArrayList<>(DEFAULT_PARTITIONS_SIZE);
        for (int i = 0; i < DEFAULT_PARTITIONS_SIZE; i++) {
            newSplits.add(new ArrayList<T>());
        }
        return newSplits;
    }

    private static <T> List<Boolean> esNullSplit(List<List<T>> list) {
        List<Boolean> sonNull = new ArrayList<>();
        for (List<T> sublist: list) {
            sonNull.add(sublist == null || sublist.isEmpty());
        }
        return sonNull;
    }

    private static <T> List<T> getList(List<List<T>> list, int index) {
        if (list == null) {
            throw new NullPointerException("La llista és nul·la.");
        }
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index " + index + ". L'índex no pot ser negatiu.");
        }
        if (index > list.size()) {
            throw new IndexOutOfBoundsException("Index " + index + ". La llista només té " + list.size() + " elements.");
        }
        return Utils.getNullIfEmpty(list.get(index));
    }
}
