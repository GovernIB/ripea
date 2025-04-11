package es.caib.ripea.service.intf.base.permission;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class UserPermissionInfo {
    private String codi;
    private String nom;
    private boolean superusuari;
    Map<Long, PermisosEntitat> permisosEntitat;


    @Data
    @Builder
    public static class PermisosEntitat {
        private Long entitatId;
        private String entitatCodi;
        private String entitatNom;
        private boolean permisUsuari;
        private boolean permisAdministrador;
        private boolean permisAdministradorOrgan;
        private List<Ent> organs;
    }

    @Data
    @Builder
    public static class Ent {
        private Long id;
        private String codi;
        private String nom;
    }
}
