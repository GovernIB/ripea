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
    Map<Ent, PermisEnt> permisosEntitat;


    @Data
    @Builder
    public static class PermisEnt {
        private boolean usuari;
        private boolean administrador;
        private boolean organAdministrador;
//        private boolean dissenyador;
//        private boolean revisio;
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
