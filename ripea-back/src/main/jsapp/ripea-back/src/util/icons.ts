/**
 * Catàleg d'icones (nom de ligadura de Material Icons) per **concepte**. És l'única font d'icones de
 * l'aplicació: l'entrada de menú, el títol de la pantalla i els botons que representen el mateix
 * concepte han de fer servir sempre la mateixa icona, i el mateix concepte ha de tenir la mateixa
 * icona a totes les aplicacions de Drassana (Distribucio, ConCSV, Notib...). Per això les seccions
 * d'accions, filtre i selecció són comunes amb les altres aplicacions i només canvien les entrades de
 * pantalla. Els conceptes de pantalla compartits amb Distribucio duen la mateixa clau i la mateixa
 * icona que al seu `src/util/icons.ts` (p. ex. `anotacio`, `consulta`).
 *
 * Si una pantalla o acció nova necessita una icona, s'hi afegeix aquí per concepte; no es posa el nom
 * de la icona en línia al component.
 */
export const icons = {
    // --- Pantalles principals de RIPEA (entrades de menú i títol de la pantalla) --------------
    expedient: 'topic',
    anotacio: 'email',
    tasca: 'assignment_turned_in',
    consulta: 'search',
    firma: 'draw',
    inici: 'home',
    entitat: 'account_balance',
    configuracio: 'settings',
    avis: 'campaign',
    usuari: 'manage_accounts',
    monitoritzacio: 'monitor',
    // Mateixa icona que té avui l'opció «Procediments» del menú (variant outlined).
    procediment: 'assignment',
    massiva: 'list_alt',
    permis: 'key',
    accessibilitat: 'accessibility',

    // --- Pantalles d'entrades de submenú (menú lateral i títol de la pantalla) --------------
    // Les que també existeixen a Distribucio/Notib/ConCSV hi duen la mateixa icona.
    integracio: 'sync_alt',
    excepcio: 'bug_report',
    monitorSistema: 'monitor_heart',
    propietat: 'display_settings',
    domini: 'domain',
    grup: 'group',
    organGestor: 'account_tree',
    contingut: 'archive',
    estadistica: 'stacked_bar_chart',
    revisio: 'fact_check',
    urlInstruccio: 'link',
    tipusDocumental: 'article',
    pinbal: 'badge',
    portafirmes: 'draw',
    notificacio: 'notifications',
    mapaWeb: 'map',

    // --- Accions -----------------------------------------------------------------------------
    crea: 'add',
    modifica: 'edit',
    esborra: 'delete',
    esborraTot: 'delete_sweep',
    desa: 'save',
    desfes: 'undo',
    cancela: 'close',
    accepta: 'check',
    activa: 'check',
    desactiva: 'close',
    executa: 'bolt',
    refresca: 'refresh',
    sincronitza: 'cached',
    copia: 'content_copy',
    descarrega: 'file_download',
    carrega: 'file_upload',
    // Icono estándar para las opciones de menú de "detalle" (solo lectura).
    detall: 'info',
    // Icono estándar para "visualizar documento" (abrir el visor de un documento).
    visualitza: 'search',
    informe: 'description',
    llista: 'list',
    diagnostic: 'monitor_heart',
    envia: 'send',
    imprimeix: 'print',
    obreNovaFinestra: 'open_in_new',
    mou: 'turn_right',
    avanca: 'fast_forward',
    reprenExecucio: 'play_circle',
    pausaExecucio: 'pause',
    afegeixUsuari: 'person_add',
    bloqueja: 'lock',
    desbloqueja: 'lock_open_right',
    mostra: 'visibility',
    amunt: 'arrow_upward',
    avall: 'arrow_downward',
    anterior: 'keyboard_double_arrow_left',
    seguent: 'keyboard_double_arrow_right',
    plega: 'expand_less',
    desplega: 'expand_more',
    mesAccions: 'more_vert',
    buida: 'clear',
    interficieClassica: 'fast_rewind',

    // --- Estats i avisos ---------------------------------------------------------------------
    informacio: 'info',
    advertencia: 'warning',
    error: 'error',
    problema: 'report_problem',
    correcte: 'check_circle',
    incorrecte: 'cancel',
    programat: 'schedule',
    senseDades: 'block',
    favorit: 'star',
    alertes: 'note_stack',
    historial: 'history',
    comentaris: 'forum',

    // --- Objectes i vistes -------------------------------------------------------------------
    correu: 'mail',
    rol: 'badge',
    taula: 'table',
    llistaPunts: 'format_list_bulleted',

    // --- Filtre i cerca ----------------------------------------------------------------------
    cerca: 'search',
    filtre: 'filter_alt',
    neteja: 'filter_alt_off',
    cercaAvancadaObre: 'zoom_in',
    cercaAvancadaTanca: 'zoom_out',

    // --- Selecció (accions massives) ---------------------------------------------------------
    seleccionaTot: 'check_box',
    deseleccionaTot: 'check_box_outline_blank',
    opcioMarcada: 'check_box',
    opcioDesmarcada: 'check_box_outline_blank',
} as const;

export type IconName = (typeof icons)[keyof typeof icons];
