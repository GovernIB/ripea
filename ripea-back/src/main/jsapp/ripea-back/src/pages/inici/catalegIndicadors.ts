import { icons, IconName } from '@src/util/icons.ts';
import { TonalColor } from '@src/util/tonal.ts';

/** Com es mostra el valor de la targeta. */
export type FormatValor = 'nombre' | 'bytes';

/**
 * Presentació d'una targeta de la pantalla d'inici. Quines targetes veu cada rol ho decideix el backend
 * (IndicadorInici); aquí només hi ha com es pinta cada codi. El títol i la descripció són a les traduccions:
 * page.inici.indicador.<CODI>.titol i .descripcio (la descripció pot fer servir {{dies}}).
 */
export type PresentacioIndicador = {
    icon: IconName;
    /** Color tonal (util/tonal.ts) de la icona i del valor. */
    color: TonalColor;
    /** Pantalla de destinació, oberta com des del menú (sense filtre: versió bàsica). */
    to?: string;
    /** La targeta obre el diàleg del monitor del sistema, que no té ruta pròpia. */
    monitorSistema?: boolean;
    /** Clau de l'entrada del menú de la destinació (page.user.menu.<menu>): el peu de la targeta en mostra el nom. */
    menu: string;
    format?: FormatValor;
};

export const CATALEG_INDICADORS: Record<string, PresentacioIndicador> = {
    // Expedients
    EXPEDIENTS_AGAFATS: { icon: icons.expedientAgafat, color: 'primary', to: '/expedient', menu: 'expedient' },
    EXPEDIENTS_ANTICS: { icon: icons.historial, color: 'warning', to: '/expedient', menu: 'expedient' },
    EXPEDIENTS_OBERTS: { icon: icons.expedient, color: 'primary', to: '/expedient', menu: 'expedient' },
    EXPEDIENTS_TANCATS_RECENTS: { icon: icons.expedientTancat, color: 'success', to: '/expedient', menu: 'expedient' },
    ALERTES_NO_LLEGIDES: { icon: icons.alertes, color: 'error', to: '/alertesExpedient', menu: 'alertes' },
    // Anotacions
    ANOTACIONS_PENDENTS: { icon: icons.anotacio, color: 'info', to: '/expedientPeticio', menu: 'anotacions' },
    // Tasques de l'usuari
    TASQUES_EN_CURS: { icon: icons.tascaEnCurs, color: 'info', to: '/usuariTasca', menu: 'tasca' },
    TASQUES_PENDENTS: { icon: icons.tascaPendent, color: 'secondary', to: '/usuariTasca', menu: 'tasca' },
    TASQUES_VENCIMENT_PROPER: { icon: icons.tascaVencimentProper, color: 'warning', to: '/usuariTasca', menu: 'tasca' },
    TASQUES_FORA_TERMINI: { icon: icons.tascaForaTermini, color: 'error', to: '/usuariTasca', menu: 'tasca' },
    // Tasques de l'entitat (administrador)
    TASQUES_SENSE_ACTIVITAT: { icon: icons.tascaSenseActivitat, color: 'warning', to: '/seguimentTasques', menu: 'assignacio' },
    // Procediments (revisor i dissenyador)
    PROCEDIMENTS_PENDENTS_REVISIO: { icon: icons.revisio, color: 'warning', to: '/metaExpedient', menu: 'procediments' },
    PROCEDIMENTS_EN_DISSENY: { icon: icons.procedimentEnDisseny, color: 'info', to: '/metaExpedient', menu: 'procediments' },
    PROCEDIMENTS_REBUTJATS: { icon: icons.procedimentRebutjat, color: 'error', to: '/metaExpedient', menu: 'procediments' },
    PROCEDIMENTS_REVISATS: { icon: icons.correcte, color: 'success', to: '/metaExpedient', menu: 'procediments' },
    // Procediments de l'entitat (administrador de lectura): actius + inactius = total
    PROCEDIMENTS_ACTIUS: { icon: icons.procedimentActiu, color: 'success', to: '/metaExpedient', menu: 'procediments' },
    PROCEDIMENTS_INACTIUS: { icon: icons.procedimentInactiu, color: 'secondary', to: '/metaExpedient', menu: 'procediments' },
    // Superadministrador
    INTEGRACIONS_ERROR: { icon: icons.integracio, color: 'error', to: '/integracio', menu: 'integracions' },
    EXCEPCIONS: { icon: icons.excepcio, color: 'error', to: '/excepcio', menu: 'excepcions' },
    MEMORIA_LLIURE: { icon: icons.memoria, color: 'info', monitorSistema: true, menu: 'monitor', format: 'bytes' },
    DISC_LLIURE: { icon: icons.disc, color: 'info', monitorSistema: true, menu: 'monitor', format: 'bytes' },
};

const GB = 1024 * 1024 * 1024;

/** Valor de la targeta amb el format de l'idioma actual. */
export const formatValor = (valor: number, format: FormatValor | undefined, idioma: string): string => {
    if (format === 'bytes') {
        const gb = new Intl.NumberFormat(idioma, { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(valor / GB);
        return `${gb} GB`;
    }
    return new Intl.NumberFormat(idioma).format(valor);
};
