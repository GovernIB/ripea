import {Box} from "@mui/material";
import React from "react";
import {MAIN_GAP} from "../util/layout.ts";

/**
 * Props per a modals (MuiDialog `componentProps` / `formDialogComponentProps`) amb un filtre i/o una graella.
 *
 * Una graella `autoHeight` creix amb les files de la pàgina i, si no hi caben, fa scroll tot el contingut de la
 * modal (amagant el filtre, el paginador i els botons). Amb aquestes props el contingut de la modal no fa scroll:
 * és una columna flex i només fa scroll l'últim fill de GridDialogBody (la graella).
 *
 * L'alçada NO és fixa: la modal fa la mida del contingut (un detall amb 3 files queda petit) i només quan no hi
 * cap s'ajusta a l'alçada de la finestra.
 */
export const gridDialogProps = (maxWidth: 'sm' | 'md' | 'lg' | 'xl' = 'lg') => ({
    fullWidth: true,
    maxWidth,
    sx: {
        '& .MuiDialogContent-root': {display: 'flex', flexDirection: 'column', overflow: 'hidden'},
        // Entre el contingut de la modal i GridDialogBody hi pot haver contenidors (el formulari d'una acció...):
        // sense això no es poden encongir per sota del contingut (min-height: auto) i la graella no fa scroll.
        '& .MuiDialogContent-root :has(.gridDialogBody)': {minHeight: 0},
    },
});

/**
 * Cos d'una modal amb graella (cal usar `gridDialogProps` a la modal): apila els fills (filtre, graella...) amb la
 * separació estàndard. Els fills mantenen la seva alçada excepte l'últim (la graella), que és el que s'encongeix i fa
 * scroll quan no hi ha espai. La graella va amb `autoHeight` (creix amb les files fins on pot).
 *
 * @param gap Separació (px) entre els fills. Per defecte, MAIN_GAP.
 */
const GridDialogBody = ({children, gap = MAIN_GAP}: {children: React.ReactNode, gap?: number}) =>
    <Box className="gridDialogBody" sx={{
        display: 'flex',
        flexDirection: 'column',
        gap: `${gap}px`,
        minHeight: 0,
        // Es pot encongir si no hi cap...
        flex: '0 1 auto',
        // ...però només l'últim fill (la graella): els altres (el filtre) no es comprimeixen.
        '& > *': {flexShrink: 0},
        '& > :last-child': {flexShrink: 1, minHeight: 0, overflow: 'auto'},
    }}>
        {children}
    </Box>

export default GridDialogBody;
