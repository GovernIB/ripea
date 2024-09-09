package es.caib.ripea.core.repository.command;

import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.GrupEntity;
import es.caib.ripea.core.repository.GrupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GrupRepositoryCommnand extends AbstractRepositoryCommnand {

    @Autowired
    private GrupRepository grupRepository;


    public List<GrupEntity> findByEntitatAndOrgan(final EntitatEntity entitat, final Long metaexpedientId, final List<String> codisOrgansFills) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("entitat", entitat);
            put("metaexpedientId", metaexpedientId);
        }};
        return getList(new GrupByEntitatAndOrganCommand(params), codisOrgansFills, true);
    }

    public Page<GrupEntity> findByEntitatAndProcediment(final EntitatEntity entitat, final String filtre, final Long metaexpedientId, final List<String> codisOrgansFills, final Pageable pageable) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("entitat", entitat);
            put("filtre", filtre);
            put("metaexpedientId", metaexpedientId);
            put("pageable", pageable);
        }};
        return getPage(new GrupByEntitatAndProcedimentAndFiltreCommand(params), codisOrgansFills, true);
    }

    public Page<GrupEntity> findByEntitatAndProcediment(final EntitatEntity entitat, final String codi, final String descripcio, final Long metaexpedientId, final Long organGestorId, final List<String> codisOrgansFills, final Pageable pageable) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("entitat", entitat);
            put("codi", codi);
            put("descripcio", descripcio);
            put("metaexpedientId", metaexpedientId);
            put("organGestorId", organGestorId);
            put("pageable", pageable);
        }};
        return getPage(new GrupByEntitatAndProcedimentCommand(params), codisOrgansFills, true);
    }



    // RepositoryCommands
    // //////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unchecked")
    private class GrupByEntitatAndOrganCommand implements RepositoryCommand<GrupEntity>{

        private final EntitatEntity entitat;
        private final Long metaExpedientId;

        public GrupByEntitatAndOrganCommand(Map<String, Object> params) {
            this.entitat = (EntitatEntity) params.get("entitat");
            this.metaExpedientId = (Long) params.get("metaexpedientId");
        }

        @Override
        public List<GrupEntity> executeList(List<?> sublist) {
            return grupRepository.findByEntitatAndOrgan(entitat,
                    metaExpedientId == null, metaExpedientId,
                    sublist == null, (List<String>) sublist);
        }
        @Override
        public Page<GrupEntity> executePage(List<?> sublist) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Pageable getPageable() {
            throw new UnsupportedOperationException();
        }
    }

    @SuppressWarnings("unchecked")
    private class GrupByEntitatAndProcedimentAndFiltreCommand implements RepositoryCommand<GrupEntity> {

        private final EntitatEntity entitat;
        private final String filtre;
        private final Long metaExpedientId;
        private final Pageable pageable;

        public GrupByEntitatAndProcedimentAndFiltreCommand(Map<String, Object> params) {
            this.entitat = (EntitatEntity) params.get("entitat");
            this.filtre = (String) params.get("filtre");
            this.metaExpedientId = (Long) params.get("metaexpedientId");
            this.pageable = (Pageable) params.get("pageable");
        }


        @Override
        public List<GrupEntity> executeList(List<?> sublist) {
            return grupRepository.findByEntitatAndProcediment(entitat,
                    filtre == null, filtre,
                    metaExpedientId == null, metaExpedientId,
                    sublist == null, (List<String>)sublist);
        }
        @Override
        public Page<GrupEntity> executePage(List<?> sublist) {
            return grupRepository.findByEntitatAndProcediment(entitat,
                    filtre == null, filtre,
                    metaExpedientId == null, metaExpedientId,
                    sublist == null, (List<String>)sublist,
                    pageable);
        }

        @Override
        public Pageable getPageable() {
            return this.pageable;
        }
    }

    @SuppressWarnings("unchecked")
    private class GrupByEntitatAndProcedimentCommand implements RepositoryCommand<GrupEntity> {

        private final EntitatEntity entitat;
        private final String codi;
        private final String descripcio;
        private final Long metaExpedientId;
        private final Long organGestorId;
        private final Pageable pageable;

        public GrupByEntitatAndProcedimentCommand(Map<String, Object> params) {
            this.entitat = (EntitatEntity) params.get("entitat");
            this.codi = (String) params.get("codi");
            this.descripcio = (String) params.get("descripcio");
            this.metaExpedientId = (Long) params.get("metaexpedientId");
            this.organGestorId = (Long) params.get("organGestorId");
            this.pageable = (Pageable) params.get("pageable");
        }

        @Override
        public List<GrupEntity> executeList(List<?> sublist) {
            return grupRepository.findByEntitatAndProcediment(entitat,
                    Utils.isEmpty(codi), Utils.getEmptyStringIfNull(codi),
                    Utils.isEmpty(descripcio), Utils.getEmptyStringIfNull(descripcio),
                    metaExpedientId == null, metaExpedientId,
                    organGestorId == null, organGestorId,
                    sublist == null, (List<String>)sublist);
        }
        @Override
        public Page<GrupEntity> executePage(List<?> sublist) {
            return grupRepository.findByEntitatAndProcediment(entitat,
                    Utils.isEmpty(codi), Utils.getEmptyStringIfNull(codi),
                    Utils.isEmpty(descripcio), Utils.getEmptyStringIfNull(descripcio),
                    metaExpedientId == null, metaExpedientId,
                    organGestorId == null, organGestorId,
                    sublist == null, (List<String>)sublist,
                    pageable);
        }

        @Override
        public Pageable getPageable() {
            return this.pageable;
        }
    }

}
