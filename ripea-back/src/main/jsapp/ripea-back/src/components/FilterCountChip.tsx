import { Icon, Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { icons } from '../util/icons';

/**
 * Recompte de filtres aplicats d'un llistat, com a text breu: «( [icona de filtre] 2 filtres aplicats )».
 * Surt a la barra de títol de la pantalla, al costat del títol (veure StyledMuiGrid i ScreenFrame).
 */

/** Substitueix per espais el contingut dels literals entre cometes simples (admet l'escapament amb `\`). */
const maskLiterals = (filter: string): string => {
    let out = '';
    let inLiteral = false;
    for (let i = 0; i < filter.length; i++) {
        const c = filter[i];
        if (inLiteral && c === '\\') {
            out += '  ';
            i++;
        } else if (c === "'") {
            inLiteral = !inLiteral;
            out += c;
        } else {
            out += inLiteral ? ' ' : c;
        }
    }
    return out;
};

/**
 * Compta els operands de primer nivell d'una cadena Spring Filter: treu els parèntesis que
 * envolten tota l'expressió, ignora el contingut dels parèntesis interiors i compta els
 * AND/OR que queden a fora. Els literals (valors escrits per l'usuari, que poden dur «and»,
 * «or» o parèntesis) s'ignoren.
 */
export const countTopLevel = (filter?: string): number => {
    if (!filter || !filter?.trim()) return 0;
    let str = maskLiterals(filter.trim());

    if (str.startsWith('(') && str.endsWith(')')) {
        let bal = 0;
        for (let i = 0; i < str.length; i++) {
            bal += str[i] === '(' ? 1 : str[i] === ')' ? -1 : 0;
            if (bal === 0 && i < str.length - 1) {
                bal = 1;
                break;
            }
        }
        if (bal === 0) str = str.slice(1, -1);
    }

    let depth = 0;
    let flat = '';
    for (let i = 0; i < str.length; i++) {
        const c = str[i];
        if (c === '(') depth++;
        else if (c === ')') depth--;
        else if (depth === 0) flat += c;
    }

    const matches = flat.match(/(?<=\s)(AND|OR)(?=\s)/gi);
    return matches ? matches.length + 1 : 1;
};

export type FilterCount = number | ((num: number) => number);

/** Nombre de filtres aplicats: el compte del filtre spring, ajustat si es rep una funció. */
export const resolveFilterCount = (filter?: string, filterCount?: FilterCount): number =>
    typeof filterCount === 'function' ? filterCount(countTopLevel(filter)) : (filterCount ?? countTopLevel(filter));

export const FilterCountChip = (props: { filter?: string; filterCount?: FilterCount; sx?: object }) => {
    const { filter, filterCount, sx } = props;
    const { t } = useTranslation();
    const num = resolveFilterCount(filter, filterCount);

    if (!num) {
        return null;
    }

    return (
        <Typography
            variant="body2"
            color="text.secondary"
            noWrap
            sx={{ whiteSpace: 'nowrap', display: 'inline-flex', alignItems: 'center', gap: 0.5, ...sx }}
        >
            {'('}
            <Icon sx={{ fontSize: '1.1rem' }}>{icons.filtre}</Icon>
            {/* `count` és el que fa servir i18next per a triar la forma singular/plural; `num` és el
                que interpola el text de la clau. */}
            {`${t('common.filterCount', { num, count: num })} )`}
        </Typography>
    );
};

export default FilterCountChip;
