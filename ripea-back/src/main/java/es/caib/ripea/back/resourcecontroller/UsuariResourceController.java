package es.caib.ripea.back.resourcecontroller;

import es.caib.ripea.back.base.controller.BaseMutableResourceController;
import es.caib.ripea.back.helper.EntitatHelper;
import es.caib.ripea.back.helper.RolHelper;
import es.caib.ripea.service.intf.base.permission.UserPermissionInfo;
import es.caib.ripea.service.intf.base.permission.UserPermissionInfo.Ent;
import es.caib.ripea.service.intf.base.permission.UserPermissionInfo.PermisosEntitat;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.OrganGestorDto;
import es.caib.ripea.service.intf.model.UsuariResource;
import es.caib.ripea.service.intf.resourceservice.UsuariResourceService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Servei REST de gestió d'usuaris.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/usuari")
@Tag(name = "Usuaris", description = "Servei de gestió d'usuaris")
public class UsuariResourceController extends BaseMutableResourceController<UsuariResource, String> {

    @Value("${es.caib.ripea.develope.mode:false}")
    private boolean developmentMode;

    @Hidden
    @GetMapping("/actual/securityInfo")
    @PreAuthorize("this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('FIND'))")
    public ResponseEntity<UserPermissionInfo> getUsauriActualSecurityInfo(HttpServletRequest request) throws MethodArgumentNotValidException {

        EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
        OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
        String rolActual = RolHelper.getRolActual(request);

        if (developmentMode) {
            return ResponseEntity.ok(UserPermissionInfo.builder()
                    .codi("rip_admin")
                    .nom("Administrador Ripea")
                    .superusuari(true)
                    .entitatActualId(entitatActual != null ? entitatActual.getId() : null)
                    .organActualId(organActual != null ? organActual.getId() : null)
                    .rolActual(rolActual)
                    .permisosEntitat(Map.of(
                            1L,
                            PermisosEntitat.builder()
                                    .entitatId(1L)
                                    .entitatCodi("GOIB")
                                    .entitatNom("Govern de les Illes Balears")
                                    .permisUsuari(true)
                                    .permisAdministrador(true)
                                    .permisAdministradorOrgan(true)
                                    .organs(List.of(
                                            Ent.builder().id(73L).codi("A04026973").nom("Direcció General d' Innovació i Transformació Digital").build(),
                                            Ent.builder().id(118L).codi("A04013554").nom("Direcció General de Medi Natural i Gestió Forestal").build()
                                    ))
                                    .build(),

                            3721L,
                            PermisosEntitat.builder()
                                    .entitatId(3721L)
                                    .entitatCodi("GONA")
                                    .entitatNom("Gobierno de Navarra")
                                    .permisUsuari(true)
                                    .permisAdministrador(false)
                                    .permisAdministradorOrgan(false)
                                    .build()
                    ))
                    .build());
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null || SecurityContextHolder.getContext().getAuthentication().getName() == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        UserPermissionInfo userPermissionInfo = ((UsuariResourceService) readonlyResourceService).getCurrentUserPermissionInfo();
        userPermissionInfo.setEntitatActualId(entitatActual != null ? entitatActual.getId() : null);
        userPermissionInfo.setOrganActualId(organActual != null ? organActual.getId() : null);
        userPermissionInfo.setRolActual(rolActual);
        return ResponseEntity.ok(userPermissionInfo);
    }

}
