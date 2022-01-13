package es.caib.ripea.plugin.firmaservidor;

import lombok.Getter;

@Getter
public enum TipusMime {

    PDF("aplication/pdf");

    private String tipus;

    TipusMime(String tipus) {
        this.tipus = tipus;
    }
}
