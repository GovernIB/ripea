import {Grid} from "@mui/material";
import {ToolbarButton} from "./StyledMuiGrid.tsx";

export type ViewSelectorOption = {
    /** Valor que es propaga a onChange quan es tria aquesta vista. */
    value: any,
    /** Nom de la lligadura Material Icons que identifica la vista. */
    icon: string,
    /** Text de la vista: surt com a tooltip i com a etiqueta accessible del botó. */
    label: string,
};

type ViewSelectorProps = {
    value: any,
    onChange: (value: any) => void,
    options: ViewSelectorOption[],
    /** Etiqueta accessible del grup de botons (p. ex. "Tipus de vista"). */
    groupLabel?: string,
};

/**
 * Selector de vista d'un llistat en forma de botons només amb icona: un per vista, amb el
 * text a dins del tooltip. Es construeix amb ToolbarButton, el mateix component que la resta
 * de botons de la barra d'eines, perquè en surtin idèntics en mida, vora i icona a qualsevol
 * tema. Es pinten enganxats, com una sola botonera, i la vista activa es marca amb la
 * variant contained.
 */
export const ViewSelector = (props: ViewSelectorProps) => {
    const { value, onChange, options, groupLabel } = props;

    return <Grid
        role="group"
        aria-label={groupLabel}
        sx={{
            display: 'flex',
            ml: 1,
            // Els botons van enganxats formant una sola peça: es treu el marge esquerre que el
            // tema afegeix a tot MuiButton, s'arrodoneixen només els extrems i els botons se
            // solapen 1px perquè les vores contigües no es vegin dobles. Els selectors van
            // sobre els <span> perquè ToolbarButton embolcalla cada botó amb un (pel tooltip).
            '& .MuiButton-root': { ml: 0, borderRadius: 0, position: 'relative' },
            '& > span + span .MuiButton-root': { ml: '-1px' },
            '& > span:first-of-type .MuiButton-root': { borderTopLeftRadius: '4px', borderBottomLeftRadius: '4px' },
            '& > span:last-of-type .MuiButton-root': { borderTopRightRadius: '4px', borderBottomRightRadius: '4px' },
            // La vista activa (i el botó amb el ratolí o el focus a sobre) per damunt del veí,
            // perquè el solapament no li tapi la vora.
            '& .MuiButton-root:hover, & .MuiButton-root:focus-visible': { zIndex: 2 },
            '& .MuiButton-contained': { zIndex: 1 },
        }}
    >
        {options.map((option) => <ToolbarButton
            key={option.value}
            title={option.label}
            icon={option.icon}
            color={'primary'}
            variant={value === option.value ? 'contained' : 'outlined'}
            aria-label={option.label}
            aria-pressed={value === option.value}
            onClick={() => onChange(option.value)}
        />)}
    </Grid>
}

export default ViewSelector;
