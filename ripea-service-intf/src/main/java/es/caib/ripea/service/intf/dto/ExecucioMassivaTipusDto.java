package es.caib.ripea.service.intf.dto;


public enum ExecucioMassivaTipusDto {
	PORTASIGNATURES,
	CANVI_ESTAT,
	TANCAMENT,
	CUSTODIAR_ELEMENTS_PENDENTS,
	ADJUNTAR_ANNEXOS_PENDENTS,
	ACTUALITZAR_ESTAT_ANOTACIONS,
	ACTUALITZAR_ESTAT_NOTIFICACIONS,
	FIRMASIMPLEWEB,
	EXPORTAR_ZIP,
	AGAFAR_EXPEDIENT,
	ALLIBERAR_EXPEDIENT,
	RETORNAR_EXPEDIENT,
	SEGUIR_EXPEDIENT,
	UNFOLLOW_EXPEDIENT,
	ESBORRAR_EXPEDIENT,
	EXPORTAR_GENERIC,
	EXPORTAR_EXCEL,
	EXPORTAR_CSV,
	EXPORTAR_INDEX_ZIP,
	EXPORTAR_INDEX_PDF,
	EXPORTAR_INDEX_EXCEL,
	EXPORTAR_ENI,
	EXPORTAR_INSIDE,
	IMPORTAR_DOCS,
	MOURE_EXPEDIENT;

	/**
	 * Si una execució d'aquest tipus es pot cancel·lar.
	 * Les exportacions són una sola crida que genera el fitxer de cop, i moure entre expedients
	 * va en una sola transacció que no desfaria els moviments ja fets a l'Arxiu: cancel·lar-les
	 * a mitges no aturaria res o deixaria dades incoherents.
	 */
	public boolean isCancelable() {
		return !name().startsWith("EXPORTAR_") && this != MOURE_EXPEDIENT;
	}
}