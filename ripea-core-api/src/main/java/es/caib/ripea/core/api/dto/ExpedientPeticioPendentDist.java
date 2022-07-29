package es.caib.ripea.core.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpedientPeticioPendentDist {

    private Long id;
    private String identificador;
    private Date dataAlta;
    private String expedientNom;
    private Long expedientId;
}
