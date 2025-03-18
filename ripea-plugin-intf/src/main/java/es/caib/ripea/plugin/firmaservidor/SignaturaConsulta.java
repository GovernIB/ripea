package es.caib.ripea.plugin.firmaservidor;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SignaturaConsulta {

    private String id;
    private String nom;
    private String motiu;
    private FirmaServidorPlugin.TipusFirma tipusFirma;
    private byte[] contingut;
    private String contentType;
    private String tipusDocumental;
}
