package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.PinbalServeiResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.dto.PinbalServeiDocPermesEnumDto;
import es.caib.ripea.service.intf.model.PinbalServeiResource;
import es.caib.ripea.service.intf.resourceservice.PinbalServeiResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PinbalServeiResourceServiceImpl extends BaseMutableResourceService<PinbalServeiResource, Long, PinbalServeiResourceEntity> implements PinbalServeiResourceService {

    private void updatePinbalServeiDocPermes(PinbalServeiResourceEntity entity, List<PinbalServeiDocPermesEnumDto> permes) {
        if (permes != null) {
            entity.setPinbalServeiDocPermesDni(permes.contains(PinbalServeiDocPermesEnumDto.DNI));
            entity.setPinbalServeiDocPermesNif(permes.contains(PinbalServeiDocPermesEnumDto.NIF));
            entity.setPinbalServeiDocPermesCif(permes.contains(PinbalServeiDocPermesEnumDto.CIF));
            entity.setPinbalServeiDocPermesNie(permes.contains(PinbalServeiDocPermesEnumDto.NIE));
            entity.setPinbalServeiDocPermesPas(permes.contains(PinbalServeiDocPermesEnumDto.PASSAPORT));
        }
    }

    @Override
    protected void beforeCreateSave(PinbalServeiResourceEntity entity, PinbalServeiResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        this.updatePinbalServeiDocPermes(entity, resource.getPinbalServeiDocPermesEnum());
    }

    @Override
    protected void beforeUpdateSave(PinbalServeiResourceEntity entity, PinbalServeiResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        this.updatePinbalServeiDocPermes(entity, resource.getPinbalServeiDocPermesEnum());
    }

    @Override
    protected void afterConversion(PinbalServeiResourceEntity entity, PinbalServeiResource resource) {
        if (resource.isPinbalServeiDocPermesDni())
            resource.getPinbalServeiDocPermesEnum().add(PinbalServeiDocPermesEnumDto.DNI);
        if (resource.isPinbalServeiDocPermesNif())
            resource.getPinbalServeiDocPermesEnum().add(PinbalServeiDocPermesEnumDto.NIF);
        if (resource.isPinbalServeiDocPermesCif())
            resource.getPinbalServeiDocPermesEnum().add(PinbalServeiDocPermesEnumDto.CIF);
        if (resource.isPinbalServeiDocPermesNie())
            resource.getPinbalServeiDocPermesEnum().add(PinbalServeiDocPermesEnumDto.NIE);
        if (resource.isPinbalServeiDocPermesPas())
            resource.getPinbalServeiDocPermesEnum().add(PinbalServeiDocPermesEnumDto.PASSAPORT);
    }
}