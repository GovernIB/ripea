package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.ExpedientTascaResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.UsuariResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.model.ExpedientTascaResource;
import es.caib.ripea.service.intf.resourceservice.ExpedientTascaResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Implementació del servei de gestió de tasques.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExpedientTascaResourceServiceImpl extends BaseMutableResourceService<ExpedientTascaResource, Long, ExpedientTascaResourceEntity> implements ExpedientTascaResourceService {

	@PostConstruct
	public void init() {
		register("RESPONSABLES_RESUM", new ResponsablesPerspectiveApplicator());
        register("duracio", new DuracioOnchangeLogicProcessor());
        register("dataLimit", new DataLimitOnchangeLogicProcessor());
	}

	private class ResponsablesPerspectiveApplicator implements PerspectiveApplicator<ExpedientTascaResource, ExpedientTascaResourceEntity> {
		@Override
		public void applySingle(String code, ExpedientTascaResourceEntity entity, ExpedientTascaResource resource) throws PerspectiveApplicationException {
			if (entity.getResponsables() != null && !entity.getResponsables().isEmpty()) {
				List<String> responsablesStr = new ArrayList<>();
				for (UsuariResourceEntity responsable: entity.getResponsables()) {
					responsablesStr.add(responsable.getCodi());
				}
				resource.setResponsablesStr(StringUtils.join(responsablesStr, ","));
			}
		}
	}

    private class DuracioOnchangeLogicProcessor implements OnChangeLogicProcessor<ExpedientTascaResource> {
        @Override
        public void onChange(
                ExpedientTascaResource previous,
                String fieldName,
                Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                ExpedientTascaResource target) {

            if (fieldValue != null) {
                Date dataLimit= DateUtils.addDays(new Date(), (Integer) fieldValue);
                if (previous.getDataLimit() == null || !DateUtils.isSameDay(previous.getDataLimit(), dataLimit)) {
                    target.setDataLimit(dataLimit);
                }
            } else {
                if (previous.getDataLimit()!=null) {
                    target.setDataLimit(null);
                }
            }
        }
    }

    private class DataLimitOnchangeLogicProcessor implements OnChangeLogicProcessor<ExpedientTascaResource> {
        @Override
        public void onChange(
                ExpedientTascaResource previous,
                String fieldName,
                Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                ExpedientTascaResource target) {

            if (fieldValue != null) {
                LocalDate start = LocalDate.now();
                LocalDate end = ((Date)fieldValue).toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                int dias = (int) start.until(end, ChronoUnit.DAYS);

                if (!Objects.equals(previous.getDuracio(), dias)) {
                    target.setDuracio(dias);
                }
            } else {
                if (previous.getDuracio()!=null) {
                    target.setDuracio(null);
                }
            }
        }
    }
}
