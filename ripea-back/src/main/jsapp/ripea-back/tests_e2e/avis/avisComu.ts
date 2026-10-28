// ─────────────────────────────────────────────────────────────────────────────
// Dades compartides pels tests d'avisos REACT i JSP.
//
// Un avís es mostra si està actiu i dataInici <= avui <= dataFinal (o sense dataFinal),
// comparant per dia. El superusuari només veu els avisos globals (sense entitat); la resta
// d'usuaris, els globals i els de la seva entitat actual.
// ─────────────────────────────────────────────────────────────────────────────

// Data relativa a avui en format DD/MM/YYYY (el dels formularis i les columnes).
export const dia = (desplacament: number): string => {
    const d = new Date();
    d.setDate(d.getDate() + desplacament);
    const dd = String(d.getDate()).padStart(2, '0');
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    return `${dd}/${mm}/${d.getFullYear()}`;
};

// Avisos de "finestra" per provar quins es mostren segons les dates (tots globals i actius).
// visible: si s'ha de mostrar avui. AVUI prova el límit (inici i final avui) i el desfasament
// d'horari entre el client, el servidor (UTC per defecte a REACT) i la BD (+01:00).
export type AvisFinestra = { sufix: string; inici: string; final: string | null; visible: boolean };
export const finestres = (): AvisFinestra[] => [
    { sufix: 'vigent',      inici: dia(-1), final: dia(1),  visible: true },
    { sufix: 'sense final', inici: dia(-1), final: null,    visible: true },
    { sufix: 'avui',        inici: dia(0),  final: dia(0),  visible: true },
    { sufix: 'futur',       inici: dia(1),  final: dia(2),  visible: false },
    { sufix: 'caducat',     inici: dia(-3), final: dia(-1), visible: false },
];

// L'avís principal (crear, modificar, esborrar) té dates futures: així no es mostra a cap
// usuari mentre dura la prova (no interfereix amb els tests que s'executen en paral·lel).
export const DATES_CREAR     = { inici: dia(10), final: dia(20) };
export const DATES_MODIFICAR = { inici: dia(11), final: dia(30) };
