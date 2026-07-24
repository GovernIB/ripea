package es.caib.ripea.service.intf.dto;

public enum EventTipusEnumDto {
	AGAFAT_ALTRE_USUARI,
	CANVI_ESTAT_PORTAFIRMES,
	CANVI_ESTAT_NOTIFICACIO,
	CANVI_ESTAT_TASCA,
	CANVI_ESTAT_VIAFIRMA,
	CANVI_ESTAT_REVISIO,
	CANVI_RESPONSABLES_TASCA,
	PROCEDIMENT_COMENTARI, //Envia correu al revisor i administradors del procediment (tasca en segon pla)
	NOVA_ANOTACIO,
	ALLIBERAT,
	MODIFICACIO_DATALIMIT_TASCA,
	DELEGAT_TASCA,
	CANCELAR_DELEGACIO_TASCA,
	ENVIAR_FICHERO,
	EXEC_MASSIVA_FINALITZADA,
	MENCIO_COMENTARI, //Envia correu al usuari mencionat, en el moment en que es crea el comentari
	FIRMA_PARCIAL_PORTAFIB,
	AVIS_ERROR_TANCAMENT_ARXIU //Avís als administradors (IPA_ADMIN) d'errors en el tancament d'expedients a l'arxiu; s'envia incondicionalment
}