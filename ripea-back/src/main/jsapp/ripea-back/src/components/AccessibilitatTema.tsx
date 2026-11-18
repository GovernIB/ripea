import React from 'react';
import InputLabel, { type InputLabelProps } from '@mui/material/InputLabel';

/**
 * Peces per a les regles d'accessibilitat que s'apliquen des del tema (theme.ts) a tots els components de
 * MUI, també als que pinta la llibreria (lib/), que no es pot modificar. Les verificacions són les de
 * l'OAW (Observatorio de Accesibilidad Web), metodologia UNE-EN 301549 (WCAG 2.1 AA).
 */

/**
 * Llargada a partir de la qual l'OAW (verificació 1.5, "Agrupació estructural") considera que un <div>
 * amb text és un paràgraf sense marcar. Compta també el text de les etiquetes en línia (<span>, <a>...),
 * de manera que només un element de bloc com <p> l'exclou.
 */
export const LLARGADA_PARAGRAF = 150;

/** Indica si el contingut només és text (cadenes o números), és a dir, si es pot posar dins un <p>. */
export const esNomesText = (children: React.ReactNode): boolean => {
    const fills = React.Children.toArray(children);
    return fills.length > 0 && fills.every((fill) => typeof fill === 'string' || typeof fill === 'number');
};

/**
 * Element del missatge dels Alert (slot `message`). MUI el pinta com a <div>, i un avís amb més de
 * LLARGADA_PARAGRAF caràcters de text hi queda com a paràgraf sense marcar (verificació 1.5). Quan el
 * missatge només és text, es posa dins un <p>; si conté altres elements (títol, enllaços, blocs...) es
 * pinta igual que abans, perquè dins un <p> no hi pot haver elements de bloc.
 *
 * Es fa servir com a `component` del slot, de manera que el component estilitzat de MUI (padding,
 * overflow...) es manté i només canvia l'element que pinta.
 */
export const AlertMessage = React.forwardRef<HTMLDivElement, React.HTMLAttributes<HTMLDivElement>>(
    ({ children, ...props }, ref) => (
        <div ref={ref} {...props}>
            {esNomesText(children) ? <p style={{ margin: 0 }}>{children}</p> : children}
        </div>
    )
);
AlertMessage.displayName = 'AlertMessage';

type TextFieldInputLabelProps = InputLabelProps & {
    /** Estat del TextField que el conté: MUI el passa als slots que no són elements HTML. */
    ownerState?: { select?: boolean; slotProps?: any; SelectProps?: any };
};

/**
 * Etiqueta dels TextField (slot `inputLabel`). En un TextField `select`, MUI posa a l'etiqueta un
 * `for` que apunta a l'id del <div role="combobox"> del Select, que no és un control etiquetable: l'OAW
 * (verificació 1.9) ho dona per associació incorrecta entre etiqueta i control. En aquest cas es treu el
 * `for`; el combobox continua tenint el nom accessible per `aria-labelledby` (l'id de l'etiqueta, que
 * hi posa MUI) i, als camps de la llibreria, per `aria-label`. És el mateix que fan els exemples de
 * Select de la documentació de MUI (InputLabel sense `htmlFor`). Amb un Select natiu (<select>) i a la
 * resta de camps el `for` es manté.
 */
export const TextFieldInputLabel = React.forwardRef<HTMLLabelElement, TextFieldInputLabelProps>(
    ({ ownerState, htmlFor, ...props }, ref) => {
        const selectNatiu = ownerState?.slotProps?.select?.native ?? ownerState?.SelectProps?.native;
        const selectNoNatiu = ownerState?.select && !selectNatiu;
        return <InputLabel ref={ref} htmlFor={selectNoNatiu ? undefined : htmlFor} {...props} />;
    }
);
TextFieldInputLabel.displayName = 'TextFieldInputLabel';
