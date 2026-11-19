package es.caib.ripea.service.intf.dto;

/**
 * Estat de lectura d'una alerta, per filtrar la consulta d'alertes d'expedients.
 */
public enum AlertaEstatEnumDto {
    /** Pendent de llegir: és la que fa aparèixer la icona d'alertes al llistat d'expedients. */
    NO_LLEGIDA,
    LLEGIDA
}
