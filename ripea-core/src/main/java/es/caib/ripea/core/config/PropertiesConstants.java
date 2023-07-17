package es.caib.ripea.core.config;

public class PropertiesConstants {

	// 1. Enviament d'execucions massives
    public static final String EXECUTAR_EXECUCIONS_MASSIVES_RATE = "es.caib.ripea.segonpla.massives.periode.comprovacio";

    // 2. Enviament de notificacions registrades a Notific@
    public static final String PROCESSAR_ANOTACIONS_PETICIONS_PENDENTS_RATE = "es.caib.ripea.tasca.consulta.anotacio.temps.espera.execucio";

    // 2.5
    public static final String REINTENTAR_CANVI_ESTAT_DISTRIBUCIO = "es.caib.ripea.segonpla.reintentar.anotacions.pendents.enviar.distribucio";

    // 3. Buidar cachés dominis
    public static final String BUIDAR_CACHES_DOMINIS_RATE = "es.caib.ripea.dominis.cache.execucio";

    // Enviar emails comentaris metaexpedients
    public static final String ENVIAR_EMAILS_PENDENTS_PROCEDIMENT_COMENTARI_CRON = "es.caib.ripea.segonpla.email.enviament.procediment.comentari.cron";
    
    // 4. Enviar emails pendents agrupats
    public static final String ENVIAR_EMAILS_PENDENTS_AGRUPATS_CRON = "es.caib.ripea.segonpla.email.enviament.agrupat.cron";
    
    // 5. Guardar en arxiu continguts pendents
    public static final String GUARDAR_ARXIU_CONTINGUTS_PENDENTS = "es.caib.ripea.segonpla.guardar.arxiu.continguts.pendents";
    
    // 6. Guardar en arxiu interessats
    public static final String GUARDAR_ARXIU_INTERESSATS = "es.caib.ripea.segonpla.guardar.arxiu.interessats";

    // 7. Actualitzar procediments
    public static final String ACTUALITZAR_PROCEDIMENTS = "es.caib.ripea.procediment.actualitzar.cron";

    // 8. Consulta de canvis en l'organigrama
    public static final String CONSULTA_CANVIS_ORGANIGRAMA = "es.caib.ripea.organs.consulta.canvis";
    
    // 9. Consulta expedients pendents de tancar a l'arxiu
    public static final String TANCAMENT_LOGIC_CRON = "es.caib.ripea.expedient.tancament.logic.cron";
}
