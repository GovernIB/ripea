package es.caib.ripea.core.api.dto;


public enum HistoricMetriquesEnumDto {
	EXPEDIENTS_CREATS,
	EXPEDIENTS_CREATS_ACUM,
//	EXPEDIENTS_OBERTS,
//	EXPEDIENTS_OBERTS_ACUM,
	EXPEDIENTS_TANCATS,
	EXPEDIENTS_TANCATS_ACUM,
//	EXPEDIENTS_AMB_ALERTES,
//	EXPEDIENTS_AMB_ERRORS_VALID,
//	DOCUMENTS_PENDENTS_SIGNAR,
	DOCUMENTS_SIGNATS,
//	DOCUMENTS_PENDENTS_NOTIFICAR,
	DOCUMENTS_NOTIFICATS;
	
	public Long getValue(HistoricExpedientDto historic) {
		Long value = 0L;
		switch (this) {
		case EXPEDIENTS_CREATS:
			value = historic.getNumExpedientsCreats();
			break;
		case EXPEDIENTS_CREATS_ACUM:
			value = historic.getNumExpedientsCreatsTotal();
			break;
//		case EXPEDIENTS_OBERTS:
//			value = historic.getNumExpedientsOberts();
//			break;
//		case EXPEDIENTS_OBERTS_ACUM:
//			value = historic.getNumExpedientsObertsTotal();
//			break;
		case EXPEDIENTS_TANCATS:
			value = historic.getNumExpedientsTancats();
			break;
		case EXPEDIENTS_TANCATS_ACUM:
			value = historic.getNumExpedientsTancatsTotal();
			break;
//		case EXPEDIENTS_AMB_ALERTES:
//			value = historic.getNumExpedientsAmbAlertes();
//			break;
//		case EXPEDIENTS_AMB_ERRORS_VALID:
//			value = historic.getNumExpedientsAmbErrorsValidacio();
//			break;
//		case DOCUMENTS_PENDENTS_SIGNAR:
//			value = historic.getNumDocsPendentsSignar();
//			break;
		case DOCUMENTS_SIGNATS:
			value = historic.getNumDocsSignats();
			break;
//		case DOCUMENTS_PENDENTS_NOTIFICAR:
//			value = historic.getNumDocsPendentsNotificar();
//			break;
		case DOCUMENTS_NOTIFICATS:
			value = historic.getNumDocsNotificats();
			break;			
		default:
			break;
		}
		return value;		
	}
}
