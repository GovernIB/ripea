package es.caib.ripea.service.intf.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MetaExpedientTascaValidacioDto implements Serializable {

	private Long id;
    private ItemValidacioTascaEnum itemValidacio;
    private TipusValidacioTascaEnum tipusValidacio;
    private Long itemId;
    private String itemNom;
    private boolean activa = true;
    private MetaExpedientTascaDto metaExpedientTasca;

    private static final long serialVersionUID = -83342526628802159L;
}