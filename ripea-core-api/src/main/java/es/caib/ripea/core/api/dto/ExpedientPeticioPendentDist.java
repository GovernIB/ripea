package es.caib.ripea.core.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ExpedientPeticioPendentDist {

    private Long id;
    private String identificador;
    private Date dataAlta;
}
