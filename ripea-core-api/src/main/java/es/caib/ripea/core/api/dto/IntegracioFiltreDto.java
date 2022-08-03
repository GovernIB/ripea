package es.caib.ripea.core.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class IntegracioFiltreDto implements Serializable {

    private String entitatCodi;

    public boolean filtresOK(IntegracioAccioDto accio, String integracioCodi) {

        return ((entitatCodi == null || entitatCodi == "")
                || ((entitatCodi != null || entitatCodi != "" || accio != null || accio.getEntitat() != null)
                && accio != null && accio.getEntitat() != null && accio.getEntitat().getCodi() != null && accio.getEntitat().getCodi() != ""
                && accio.getEntitat().getCodi().toLowerCase().contains(entitatCodi.toLowerCase())));
    }
}

