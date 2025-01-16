package es.caib.ripea.core.repository.command;

import es.caib.ripea.core.api.dto.ExpedientEstatEnumDto;
import es.caib.ripea.core.api.dto.PrioritatEnumDto;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientEstatEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.helper.DateHelper;
import es.caib.ripea.core.repository.ExpedientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.springframework.data.domain.Sort.Direction.DESC;

@SuppressWarnings("serial")
@Component
public class ExpedientRepositoryCommnand extends AbstractRepositoryCommnand {

    @Autowired private ExpedientRepository expedientRepository;

    public Page<ExpedientEntity> findExpedientsRelacionatsByIdIn(final EntitatEntity entitat, final MetaNodeEntity metaNode, final String numero, final String nom, final ExpedientEstatEnumDto estatEnum, final ExpedientEstatEntity estat, final List<Long> ids, final Pageable pageable) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("entitat", entitat);
            put("metaNode", metaNode);
            put("numero", numero);
            put("nom", nom);
            put("estatEnum", estatEnum);
            put("estat", estat);
            put("pageable", pageable);
        }};
        return getPage(new ExpedientByExpedientsRelacionatsCommand(params), ids, false);
    }

    public List<ExpedientEntity> findByEntitatAndIdInOrderByIdAsc(final EntitatEntity entitat, final List<Long> ids) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("entitat", entitat);
        }};
        return getList(new ExpedientByEntitatAndIdInCommand(params), ids, false);
    }

    public List<ExpedientEntity> findByEntitatAndMetaExpedient(final EntitatEntity entitat, final MetaNodeEntity metaNode, final List<? extends MetaNodeEntity> metaNodesPermesos) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("entitat", entitat);
            put("metaNode", metaNode);
        }};
        return getList(new ExpedientByEntitatAndMetaExpedientCommand(params), metaNodesPermesos, true);
    }

    public Page<ExpedientEntity> findExpedientsPerCanviEstatMassiu(
    		final EntitatEntity entitat,
    		final boolean nomesAgafats,
    		final UsuariEntity usuariActual,
    		final MetaNodeEntity metaExpedient,
    		final ExpedientEntity expedient,
    		final Date dataInici,
    		final Date dataFi,
    		final String nom,
    		final ExpedientEstatEnumDto estatEnum,
    		final ExpedientEstatEntity estat,
    		final PrioritatEnumDto prioritat,
    		final List<? extends MetaNodeEntity> metaExpedientsPermesos,
    		final Pageable pageable) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("entitat", entitat);
            put("nomesAgafats", nomesAgafats);
            put("usuariActual", usuariActual);
            put("metaExpedient", metaExpedient);
            put("expedient", expedient);
            put("dataInici", dataInici);
            put("dataFi", dataFi);
            put("nom", nom);
            put("estatEnum", estatEnum);
            put("estat", estat);
            put("prioritat", prioritat);
            put("pageable", pageable);
        }};
        return getPage(new ExpedientByExpedientsPerCanviEstatMassiuCommand(params), metaExpedientsPermesos, true);
    }

    public List<Long> findIdsExpedientsPerCanviEstatMassiu(final EntitatEntity entitat, final boolean nomesAgafats, final UsuariEntity usuariActual, final MetaNodeEntity metaExpedient, final ExpedientEntity expedient, final Date dataInici, final Date dataFi, final ExpedientEstatEnumDto estatEnum, final ExpedientEstatEntity estat, final List<? extends MetaNodeEntity> metaExpedientsPermesos) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("entitat", entitat);
            put("nomesAgafats", nomesAgafats);
            put("usuariActual", usuariActual);
            put("metaExpedient", metaExpedient);
            put("expedient", expedient);
            put("dataInici", dataInici);
            put("dataFi", dataFi);
            put("estatEnum", estatEnum);
            put("estat", estat);
        }};
        return getList(new IdsByExpedientsPerCanviEstatMassiuCommand(params), metaExpedientsPermesos, true);
    }

    public Page<ExpedientEntity> findExpedientsPerTancamentMassiu(
    		final EntitatEntity entitat,
    		final boolean nomesAgafats,
    		final UsuariEntity usuariActual,
    		final MetaNodeEntity metaExpedient,
    		final String nom,
    		final Date dataInici,
    		final Date dataFi,
    		final ExpedientEstatEnumDto estatEnum,
    		final ExpedientEstatEntity estat,    		
    		final PrioritatEnumDto prioritat,
    		final List<? extends MetaNodeEntity> metaExpedientsPermesos,
    		final Pageable pageable) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("entitat", entitat);
            put("nomesAgafats", nomesAgafats);
            put("usuariActual", usuariActual);
            put("metaExpedient", metaExpedient);
            put("nom", nom);
            put("dataInici", dataInici);
            put("dataFi", dataFi);
            put("pageable", pageable);
            put("estatEnum", estatEnum);
            put("estat", estat);
            put("prioritat", prioritat);
        }};
        return getPage(new ExpedientByExpedientsPerTancamentMassiuCommand(params), metaExpedientsPermesos, false);
    }

    public List<Long> findIdsExpedientsPerTancamentMassiu(final EntitatEntity entitat, final boolean nomesAgafats, final UsuariEntity usuariActual, final MetaNodeEntity metaExpedient, final String nom, final Date dataInici, final Date dataFi, final List<? extends MetaNodeEntity> metaExpedientsPermesos) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("entitat", entitat);
            put("nomesAgafats", nomesAgafats);
            put("usuariActual", usuariActual);
            put("metaExpedient", metaExpedient);
            put("nom", nom);
            put("dataInici", dataInici);
            put("dataFi", dataFi);
        }};
        return getList(new IdsByExpedientsPerTancamentMassiuCommand(params), metaExpedientsPermesos, false);
    }

    public Page<ExpedientEntity> findArxiuPendents(final EntitatEntity entitat, final boolean nomesAgafats, final String usuariActual, final MetaNodeEntity metaExpedient, final String nom, final Date creacioInici, final Date creacioFi, final List<? extends MetaNodeEntity> metaExpedientsPermesos, final Pageable pageable) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("entitat", entitat);
            put("nomesAgafats", nomesAgafats);
            put("usuariActual", usuariActual);
            put("metaExpedient", metaExpedient);
            put("nom", nom);
            put("creacioInici", creacioInici);
            put("creacioFi", creacioFi);
            put("pageable", pageable);
        }};
        return getPage(new ExpedientByArxiuPendentCommand(params), metaExpedientsPermesos, true);
    }

    public List<Long> findIdsArxiuPendents(final EntitatEntity entitat, final boolean nomesAgafats, final String usuariActual, final MetaNodeEntity metaExpedient, final String nom, final Date creacioInici, final Date creacioFi, final List<? extends MetaNodeEntity> metaExpedientsPermesos) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("entitat", entitat);
            put("nomesAgafats", nomesAgafats);
            put("usuariActual", usuariActual);
            put("metaExpedient", metaExpedient);
            put("nom", nom);
            put("creacioInici", creacioInici);
            put("creacioFi", creacioFi);
        }};
        return getList(new IdsByArxiuPendentCommand(params), metaExpedientsPermesos, false);
    }

    public List<ExpedientEntity> findByEntitatAndMetaExpedientAndOrgans(final EntitatEntity entitat, final MetaNodeEntity metaNode, final List<String> organsCodisPermitted) {
		Map<String, Object> params = new HashMap<String, Object>() {{
            put("entitat", entitat);
            put("metaNode", metaNode);
        }};
        return getList(new ExpedientByEntitatAndMetaExpedientAndOrgansCommand(params), organsCodisPermitted, new Order(DESC, "nom"), true);
    }


    // RepositoryCommands
    // //////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unchecked")
    private class ExpedientByExpedientsRelacionatsCommand implements RepositoryCommand<ExpedientEntity>{

        private final EntitatEntity entitat;
        private final MetaNodeEntity metaNode;
        private final String numero;
        private final String nom;
        private final ExpedientEstatEnumDto estatEnum;
        private final ExpedientEstatEntity estat;
        private final Pageable pageable;

        public ExpedientByExpedientsRelacionatsCommand(Map<String, Object> params) {
            this.entitat = (EntitatEntity) params.get("entitat");
            this.metaNode = (MetaNodeEntity) params.get("metaNode");
            this.numero = (String) params.get("numero");
            this.nom = (String) params.get("nom");
            this.estatEnum = (ExpedientEstatEnumDto) params.get("estatEnum");
            this.estat = (ExpedientEstatEntity) params.get("estat");
            this.pageable = (Pageable) params.get("pageable");
        }

        @Override
        public List<ExpedientEntity> executeList(List<?> sublist) {
            return expedientRepository.findExpedientsRelacionatsByIdIn(entitat,
                    metaNode == null, metaNode,
                    Utils.isBlank(numero), Utils.getEmptyStringIfNull(numero),
                    Utils.isEmpty(nom), Utils.getEmptyStringIfNull(nom),
                    estatEnum == null, estatEnum,
                    estat == null, estat,
                    (List<Long>)sublist);
        }
        @Override
        public Page<ExpedientEntity> executePage(List<?> sublist) {
            return expedientRepository.findExpedientsRelacionatsByIdIn(entitat,
                    metaNode == null, metaNode,
                    Utils.isBlank(numero), Utils.getEmptyStringIfNull(numero),
                    Utils.isEmpty(nom), Utils.getEmptyStringIfNull(nom),
                    estatEnum == null, estatEnum,
                    estat == null, estat,
                    (List<Long>)sublist,
                    pageable);
        }

        @Override
        public Pageable getPageable() {
            return this.pageable;
        }
    }

    @SuppressWarnings("unchecked")
    private class ExpedientByEntitatAndIdInCommand implements RepositoryCommand<ExpedientEntity>{

        private final EntitatEntity entitat;

        public ExpedientByEntitatAndIdInCommand(Map<String, Object> params) {
            this.entitat = (EntitatEntity) params.get("entitat");
        }

        @Override
        public List<ExpedientEntity> executeList(List<?> sublist) {
            return expedientRepository.findByEntitatAndIdInOrderByIdAsc(entitat, (List<Long>) sublist);
        }

        @Override
        public Page<ExpedientEntity> executePage(List<?> sublist) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Pageable getPageable() {
            throw new UnsupportedOperationException();
        }
    }

    @SuppressWarnings("unchecked")
    private class ExpedientByEntitatAndMetaExpedientCommand implements RepositoryCommand<ExpedientEntity> {

        private final EntitatEntity entitat;
        private final MetaNodeEntity metaNode;

        public ExpedientByEntitatAndMetaExpedientCommand(Map<String, Object> params) {
            this.entitat = (EntitatEntity) params.get("entitat");
            this.metaNode = (MetaNodeEntity) params.get("metaNode");
        }

        @Override
        public List<ExpedientEntity> executeList(List<?> sublist) {
            return expedientRepository.findByEntitatAndMetaExpedientOrderByNomAsc(entitat,
                    (List<? extends MetaNodeEntity>)sublist,
                    metaNode == null, metaNode);
        }
        @Override
        public Page<ExpedientEntity> executePage(List<?> sublist) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Pageable getPageable() {
            throw new UnsupportedOperationException();
        }
    }

    @SuppressWarnings("unchecked")
    private class ExpedientByExpedientsPerCanviEstatMassiuCommand implements RepositoryCommand<ExpedientEntity> {

        private final EntitatEntity entitat;
        private final boolean nomesAgafats;
        private final UsuariEntity usuariActual;
        private final MetaNodeEntity metaExpedient;
        private final ExpedientEntity expedient;
        private final Date dataInici;
        private final Date dataFi;
        private final String nom;
        private final ExpedientEstatEnumDto estatEnum;
        private final ExpedientEstatEntity estat;
        private final PrioritatEnumDto prioritat;
        private final Pageable pageable;

        public ExpedientByExpedientsPerCanviEstatMassiuCommand(Map<String, Object> params) {
            this.entitat = (EntitatEntity) params.get("entitat");
            this.nomesAgafats = (Boolean) params.get("nomesAgafats");
            this.usuariActual = (UsuariEntity) params.get("usuariActual");
            this.metaExpedient = (MetaNodeEntity) params.get("metaExpedient");
            this.expedient = (ExpedientEntity) params.get("expedient");
            this.dataInici = (Date) params.get("dataInici");
            this.dataFi = (Date) params.get("dataFi");
            this.nom = (String) params.get("nom");
            this.estatEnum = (ExpedientEstatEnumDto) params.get("estatEnum");
            this.estat = (ExpedientEstatEntity) params.get("estat");
            this.prioritat = (PrioritatEnumDto) params.get("prioritat");
            this.pageable = (Pageable) params.get("pageable");
        }

        @Override
        public List<ExpedientEntity> executeList(List<?> sublist) {
            return expedientRepository.findExpedientsPerCanviEstatMassiu(entitat,
                    nomesAgafats,
                    usuariActual,
                    Utils.getNullIfEmpty((List<MetaExpedientEntity>) sublist),
                    metaExpedient == null, metaExpedient,
                    expedient == null, expedient,
                    dataInici == null, dataInici,
                    dataFi == null, dataFi,
                    estatEnum == null, estatEnum,
                    !Utils.hasValue(nom), nom,
                    estat == null, estat,
                    prioritat == null, prioritat);
        }
        @Override
        public Page<ExpedientEntity> executePage(List<?> sublist) {
            return expedientRepository.findExpedientsPerCanviEstatMassiu(entitat,
                    nomesAgafats,
                    usuariActual,
                    Utils.getNullIfEmpty((List<MetaExpedientEntity>) sublist),
                    metaExpedient == null, metaExpedient,
                    expedient == null, expedient,
                    dataInici == null, dataInici,
                    dataFi == null, dataFi,
                    estatEnum == null, estatEnum,
                    !Utils.hasValue(nom), nom,
                    estat == null, estat,
                    prioritat == null, prioritat,
                    pageable);
        }

        @Override
        public Pageable getPageable() {
            return pageable;
        }
    }

    @SuppressWarnings("unchecked")
    private class IdsByExpedientsPerCanviEstatMassiuCommand implements RepositoryCommand<Long> {

        private final EntitatEntity entitat;
        private final boolean nomesAgafats;
        private final UsuariEntity usuariActual;
        private final MetaNodeEntity metaExpedient;
        private final ExpedientEntity expedient;
        private final Date dataInici;
        private final Date dataFi;
        private final ExpedientEstatEnumDto estatEnum;
        private final ExpedientEstatEntity estat;
        private final PrioritatEnumDto prioritat;

        public IdsByExpedientsPerCanviEstatMassiuCommand(Map<String, Object> params) {
            this.entitat = (EntitatEntity) params.get("entitat");
            this.nomesAgafats = (Boolean) params.get("nomesAgafats");
            this.usuariActual = (UsuariEntity) params.get("usuariActual");
            this.metaExpedient = (MetaNodeEntity) params.get("metaExpedient");
            this.expedient = (ExpedientEntity) params.get("expedient");
            this.dataInici = (Date) params.get("dataInici");
            this.dataFi = (Date) params.get("dataFi");
            this.estatEnum = (ExpedientEstatEnumDto) params.get("estatEnum");
            this.estat = (ExpedientEstatEntity) params.get("estat");
            this.prioritat = (PrioritatEnumDto) params.get("prioritat");
        }

        @Override
        public List<Long> executeList(List<?> sublist) {
            return expedientRepository.findIdsExpedientsPerCanviEstatMassiu(entitat,
                    nomesAgafats,
                    usuariActual,
                    Utils.getNullIfEmpty((List<MetaExpedientEntity>) sublist),
                    metaExpedient == null, metaExpedient,
                    expedient == null, expedient,
                    dataInici == null, dataInici,
                    dataFi == null, dataFi,
                    estatEnum == null, estatEnum,
                    estat == null, estat,
                    prioritat == null, prioritat);
        }
        @Override
        public Page<Long> executePage(List<?> sublist) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Pageable getPageable() {
            throw new UnsupportedOperationException();
        }
    }

    @SuppressWarnings("unchecked")
    private class ExpedientByExpedientsPerTancamentMassiuCommand implements RepositoryCommand<ExpedientEntity> {

        private final EntitatEntity entitat;
        private final boolean nomesAgafats;
        private final UsuariEntity usuariActual;
        private final MetaNodeEntity metaExpedient;
        private final String nom;
        private final Date dataInici;
        private final Date dataFi;
        private final ExpedientEstatEnumDto estatEnum;
        private final ExpedientEstatEntity estat;
        private final PrioritatEnumDto prioritat;
        private final Pageable pageable;

        public ExpedientByExpedientsPerTancamentMassiuCommand(Map<String, Object> params) {
            this.entitat = (EntitatEntity) params.get("entitat");
            this.nomesAgafats = (Boolean) params.get("nomesAgafats");
            this.usuariActual = (UsuariEntity) params.get("usuariActual");
            this.metaExpedient = (MetaNodeEntity) params.get("metaExpedient");
            this.nom = (String) params.get("nom");
            this.dataInici = (Date) params.get("dataInici");
            this.dataFi = (Date) params.get("dataFi");
            this.pageable = (Pageable) params.get("pageable");
            this.estatEnum = (ExpedientEstatEnumDto) params.get("estatEnum");
            this.estat = (ExpedientEstatEntity) params.get("estat");
            this.prioritat = (PrioritatEnumDto) params.get("prioritat");
        }

        @Override
        public List<ExpedientEntity> executeList(List<?> sublist) {
            return expedientRepository.findExpedientsPerTancamentMassiu(entitat,
                    nomesAgafats,
                    usuariActual,
                    (List<MetaExpedientEntity>) sublist,
                    metaExpedient == null, metaExpedient,
                    Utils.isEmpty(nom), Utils.getEmptyStringIfNull(nom),
                    dataInici == null, dataInici,
                    dataFi == null, dataFi,
                    estatEnum == null, estatEnum,
                    estat == null, estat,
                    prioritat == null, prioritat);
        }
        @Override
        public Page<ExpedientEntity> executePage(List<?> sublist) {
            return expedientRepository.findExpedientsPerTancamentMassiu(entitat,
                    nomesAgafats,
                    usuariActual,
                    (List<MetaExpedientEntity>) sublist,
                    metaExpedient == null, metaExpedient,
                    Utils.isEmpty(nom), Utils.getEmptyStringIfNull(nom),
                    dataInici == null, dataInici,
                    dataFi == null, dataFi,
                    estatEnum == null, estatEnum,
                    estat == null, estat,
                    prioritat == null, prioritat,
                    pageable);
        }

        @Override
        public Pageable getPageable() {
            return pageable;
        }
    }

    @SuppressWarnings("unchecked")
    private class IdsByExpedientsPerTancamentMassiuCommand implements RepositoryCommand<Long> {

        private final EntitatEntity entitat;
        private final boolean nomesAgafats;
        private final UsuariEntity usuariActual;
        private final MetaNodeEntity metaExpedient;
        private final String nom;
        private final Date dataInici;
        private final Date dataFi;

        public IdsByExpedientsPerTancamentMassiuCommand(Map<String, Object> params) {
            this.entitat = (EntitatEntity) params.get("entitat");
            this.nomesAgafats = (Boolean) params.get("nomesAgafats");
            this.usuariActual = (UsuariEntity) params.get("usuariActual");
            this.metaExpedient = (MetaNodeEntity) params.get("metaExpedient");
            this.nom = (String) params.get("nom");
            this.dataInici = (Date) params.get("dataInici");
            this.dataFi = (Date) params.get("dataFi");
        }

        @Override
        public List<Long> executeList(List<?> sublist) {
            return expedientRepository.findIdsExpedientsPerTancamentMassiu(entitat,
                    nomesAgafats,
                    usuariActual,
                    (List<MetaExpedientEntity>) sublist,
                    metaExpedient == null, metaExpedient,
                    Utils.isEmpty(nom), Utils.getEmptyStringIfNull(nom),
                    dataInici == null, dataInici,
                    dataFi == null, dataFi);
        }
        @Override
        public Page<Long> executePage(List<?> sublist) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Pageable getPageable() {
            throw new UnsupportedOperationException();
        }
    }

    @SuppressWarnings("unchecked")
    private class ExpedientByArxiuPendentCommand implements RepositoryCommand<ExpedientEntity> {

        private final EntitatEntity entitat;
        private final boolean nomesAgafats;
        private final String usuariActual;
        private final MetaExpedientEntity metaExpedient;
        private final String nom;
        private final Date creacioInici;
        private final Date creacioFi;
        private final Pageable pageable;

        public ExpedientByArxiuPendentCommand(Map<String, Object> params) {
            this.entitat = (EntitatEntity) params.get("entitat");
            this.nomesAgafats = (Boolean) params.get("nomesAgafats");
            this.usuariActual = (String) params.get("usuariActual");
            this.metaExpedient = (MetaExpedientEntity) params.get("metaExpedient");
            this.nom = (String) params.get("nom");
            this.creacioInici = (Date) params.get("creacioInici");
            this.creacioFi = (Date) params.get("creacioFi");
            this.pageable = (Pageable) params.get("pageable");
        }

        @Override
        public List<ExpedientEntity> executeList(List<?> sublist) {
            return expedientRepository.findArxiuPendents(entitat,
                    (List<MetaExpedientEntity>) sublist,
                    nomesAgafats,
                    usuariActual,
                    Utils.isEmpty(nom), Utils.getEmptyStringIfNull(nom),
                    metaExpedient == null, metaExpedient,
                    creacioInici == null, creacioInici,
                    creacioFi == null, DateHelper.toDateFinalDia(creacioFi));
        }
        @Override
        public Page<ExpedientEntity> executePage(List<?> sublist) {
            return expedientRepository.findArxiuPendents(entitat,
                    (List<MetaExpedientEntity>) sublist,
                    nomesAgafats,
                    usuariActual,
                    Utils.isEmpty(nom), Utils.getEmptyStringIfNull(nom),
                    metaExpedient == null, metaExpedient,
                    creacioInici == null, creacioInici,
                    creacioFi == null, DateHelper.toDateFinalDia(creacioFi),
                    pageable);
        }

        @Override
        public Pageable getPageable() {
            return pageable;
        }
    }

    @SuppressWarnings("unchecked")
    private class IdsByArxiuPendentCommand implements RepositoryCommand<Long> {

        private final EntitatEntity entitat;
        private final boolean nomesAgafats;
        private final String usuariActual;
        private final MetaExpedientEntity metaExpedient;
        private final String nom;
        private final Date creacioInici;
        private final Date creacioFi;

        public IdsByArxiuPendentCommand(Map<String, Object> params) {
            this.entitat = (EntitatEntity) params.get("entitat");
            this.nomesAgafats = (Boolean) params.get("nomesAgafats");
            this.usuariActual = (String) params.get("usuariActual");
            this.metaExpedient = (MetaExpedientEntity) params.get("metaExpedient");
            this.nom = (String) params.get("nom");
            this.creacioInici = (Date) params.get("creacioInici");
            this.creacioFi = (Date) params.get("creacioFi");
        }

        @Override
        public List<Long> executeList(List<?> sublist) {
            return expedientRepository.findIdsArxiuPendents(entitat,
                    (List<MetaExpedientEntity>) sublist,
                    nomesAgafats,
                    usuariActual,
                    Utils.isEmpty(nom), Utils.getEmptyStringIfNull(nom),
                    metaExpedient == null, metaExpedient,
                    creacioInici == null, creacioInici,
                    creacioFi == null, DateHelper.toDateFinalDia(creacioFi));
        }
        @Override
        public Page<Long> executePage(List<?> sublist) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Pageable getPageable() {
            throw new UnsupportedOperationException();
        }
    }

    @SuppressWarnings("unchecked")
    private class ExpedientByEntitatAndMetaExpedientAndOrgansCommand implements RepositoryCommand<ExpedientEntity> {

        private final EntitatEntity entitat;
        private final MetaNodeEntity metaNode;

        public ExpedientByEntitatAndMetaExpedientAndOrgansCommand(Map<String, Object> params) {
            this.entitat = (EntitatEntity) params.get("entitat");
            this.metaNode = (MetaNodeEntity) params.get("metaNode");
        }

        @Override
        public List<ExpedientEntity> executeList(List<?> sublist) {
            return expedientRepository.findByEntitatAndMetaExpedientAndOrgans(entitat,
                    (List<String>)sublist,
                    metaNode);
        }
        @Override
        public Page<ExpedientEntity> executePage(List<?> sublist) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Pageable getPageable() {
            throw new UnsupportedOperationException();
        }
    }
}
