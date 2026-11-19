package es.caib.ripea.service.intf.dto;

/**
 * Pantalla on entra l'usuari a la interfície REACT (preferència del perfil).
 */
public enum PaginaEntradaEnum {
    /** Pantalla per defecte del rol actiu (llistat d'expedients, de procediments o monitor d'integracions). */
    ROL,
    /** Pantalla d'inici, amb els indicadors i les llistes resum del rol actiu. */
    INICI
}
