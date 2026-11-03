/**
 * Mides del marc de l'aplicació (capçalera, menú lateral i peu), en un sol lloc perquè App.tsx,
 * theme.ts, BaseApp.tsx i DrassanaFooter.tsx hi coincideixin. Els valors derivats es calculen a
 * partir dels base: canviar-ne un es propaga als altres.
 *
 * La llibreria (reactlib) té part d'aquestes mides cablejades (alçada de capçalera de 64px, menú
 * plegat de 57px, icones de 20px...) i no es pot tocar; per això el marc s'ajusta des del tema
 * (veure `MuiDrawer` a theme.ts) i des d'App.tsx.
 */

/** Alçada de la capçalera (px). La llibreria en fa 64. */
export const HEADER_HEIGHT = 74;

/** Alçada del peu (px). Es passa a la llibreria com a `footerHeight`: el menú hi reserva l'espai. */
export const FOOTER_HEIGHT = 36;

/** Alçada dels logos de la capçalera (px). */
export const LOGO_HEIGHT = 59;

/**
 * Espai (px) entre la línia separadora de la capçalera i cada un dels dos logos, a banda i banda
 * d'aquesta: el del Govern a l'esquerra i el de l'aplicació a la dreta. El separador cau sempre a la
 * vora dreta del menú obert i no es mou en plegar-lo.
 */
export const LOGO_SEPARATOR_GAP = 12;

/**
 * Marge (px) de l'àrea de contingut (`<main>`) i separació entre els elements que hi ha apilats: la
 * pantalla, els avisos que hi pugui haver a sobre... La llibreria hi posa un marge de 16px a dalt i a
 * baix i de 24px als costats, cablejat; per això es desactiva (`marginsDisabled`) i es substitueix
 * per aquest padding des de BaseApp.tsx.
 */
export const MAIN_PADDING = 15;
export const MAIN_GAP = 15;

/** Alçada (px) de la barra de títol de la pantalla (components/ScreenFrame.tsx). */
export const SCREEN_TITLE_MIN_HEIGHT = 56;

/** Padding horitzontal (px) de la barra de títol; la barra d'eines de la graella hi respecta la vora dreta. */
export const SCREEN_TITLE_PADDING_X = 16;

/** Separació (px) entre els elements de la barra de títol: icona, títol i recompte de filtres. */
export const SCREEN_TITLE_GAP = 12;

/**
 * Alçada mínima (px) de la zona de contingut d'una pantalla amb graella: per sota d'aquesta alçada la
 * graella deixa de comprimir-se i és el contingut de la pantalla qui fa scroll, de manera que el
 * títol i el peu es mantenen sempre visibles.
 */
export const SCREEN_GRID_MIN_HEIGHT = 320;

/** Mida de les icones del menú lateral (px). La llibreria les pinta a 20. */
export const ICON_SIZE = 24;

/** Amplada del menú lateral obert (px): el valor per defecte del Drawer de la llibreria. */
export const MENU_WIDTH = 240;

/**
 * Columna (centre horitzontal, en px) de les icones del menú lateral, on també hi cau el centre del
 * botó d'expandir/contreure de la capçalera. És la meitat del menú plegat.
 */
export const MENU_ICON_COLUMN = 37;

/**
 * Amplada del menú lateral plegat (px). Les icones hi queden centrades, així que és el doble de la
 * columna d'icones.
 */
export const MENU_WIDTH_COLLAPSED = 2 * MENU_ICON_COLUMN;

/**
 * Vora dreta (px) del menú lateral permanent. Dins l'amplada total, així que l'espai útil és un
 * píxel més estret i el centrat de la llibreria deixa la icona plegada mig píxel a l'esquerra de la
 * columna (veure `MuiDrawer` a theme.ts).
 */
export const MENU_BORDER_WIDTH = 1;

/**
 * Per sota d'aquesta amplada (px) el menú es considera plegat i se n'amaga el text de les entrades
 * (consulta de contenidor de MuiDrawer a theme.ts). Ha de quedar entre la plegada i l'oberta.
 */
export const MENU_COLLAPSED_BELOW = MENU_WIDTH_COLLAPSED + 36;

/**
 * Padding esquerre (px) que la llibreria dona a les entrades de nivell 0 del menú obert. La icona
 * hi suma el seu propi marge, que el tema substitueix per un de calculat (veure `MuiDrawer` a
 * theme.ts) perquè el centre de la icona caigui a MENU_ICON_COLUMN.
 */
export const MENU_ITEM_PADDING_LEFT = 24;

/**
 * Padding esquerre de la barra de capçalera (px) perquè el botó de menú quedi centrat a la columna
 * d'icones. El botó (IconButton `size="large"` + `edge="start"`) fa 48px d'ample i duu un marge
 * esquerre de -12px, així que el centre queda a `padding + 12`.
 */
const MENU_BUTTON_EDGE = 12;
const MENU_BUTTON_SIZE = 48;
const MENU_BUTTON_MARGIN_RIGHT = 16;
export const APPBAR_PADDING_LEFT = MENU_ICON_COLUMN - MENU_BUTTON_EDGE;

/**
 * On comença la caixa del logo: el padding de la capçalera, més el botó de menú (48px, amb el marge
 * esquerre de -12px) i el seu marge dret de 16px.
 */
export const LOGO_BOX_LEFT = APPBAR_PADDING_LEFT - MENU_BUTTON_EDGE + MENU_BUTTON_SIZE + MENU_BUTTON_MARGIN_RIGHT;
