package es.caib.ripea.service.resourcehelper;

import es.caib.ripea.service.helper.CacheHelper;
import es.caib.ripea.service.intf.base.permission.UserPermissionInfo.Ent;
import es.caib.ripea.service.intf.base.permission.UserPermissionInfo.PermisEnt;
import es.caib.ripea.service.intf.dto.EntitatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UsuariResourceHelper {

    private final CacheHelper cacheHelper;

    public Map<Ent, PermisEnt> getPermisosEntitat(String usuariCodi) {
        List<EntitatDto> entitatsAccessiblesUsuari = cacheHelper.findEntitatsAccessiblesUsuari(usuariCodi);
        return entitatsAccessiblesUsuari.stream().collect(Collectors.toMap(
                entitatDto -> Ent.builder()
                        .id(entitatDto.getId())
                        .codi(entitatDto.getCodi())
                        .nom(entitatDto.getNom())
                        .build(),
                entitatDto -> PermisEnt.builder()
                        .usuari(entitatDto.isUsuariActualRead())
                        .administrador(entitatDto.isUsuariActualAdministration())
                        .organAdministrador(entitatDto.isUsuariActualTeOrgans())
                        .organs(entitatDto.getOrgansGestors() != null
                                ? entitatDto.getOrgansGestors().stream().map(
                                        organ -> Ent.builder()
                                                .id(organ.getId())
                                                .codi(organ.getCodi())
                                                .nom(organ.getNom())
                                                .build())
                                .collect(Collectors.toList())
                                : null)
                        .build()
        ));
    }

}
