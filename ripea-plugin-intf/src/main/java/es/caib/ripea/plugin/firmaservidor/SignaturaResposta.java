package es.caib.ripea.plugin.firmaservidor;

import lombok.Data;

@Data
public class SignaturaResposta {

    private byte[] contingut;
    private String nom;
    private String mime;
    private String tipusFirma;
    private String tipusFirmaEni;
    private String perfilFirmaEni;
}
