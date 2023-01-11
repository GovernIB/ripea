/**
 * 
 */
package es.caib.ripea.core.api.dto;

/**
 * Enumeració amb els possibles tipus de serveis de PINBAL.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum MetaDocumentPinbalServeiEnumDto {
	SVDDGPCIWS02, // Consulta de datos de identidad
	SVDDGPVIWS02, // Verificación de datos de identidad
	SVDCCAACPASWS01, // Estar al corriente de obligaciones tributarias para solicitud de subvenciones y ayudas de la CCAA
	SVDSCDDWS01, // Servei de consulta de dades de discapacitat
	SCDCPAJU, // Servei de consulta de padró de convivència
	SVDSCTFNWS01, // Servei de consulta de família nombrosa
	SVDCCAACPCWS01, // Estar al corriente de obligaciones tributarias para contratación con la CCAA
	Q2827003ATGSS001, // Estar al corriente de pago con la Seguridad Social
	SVDDELSEXWS01 // Consulta de inexistencia de delitos sexuales por datos de filiación
}
