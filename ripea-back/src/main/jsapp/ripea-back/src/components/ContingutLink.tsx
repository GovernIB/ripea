import {ReactNode} from "react";
import {Link, LinkProps} from "@mui/material";
import {Link as RouterLink} from "react-router-dom";

interface ContingutLinkProps extends Omit<LinkProps, 'component' | 'href' | 'id'> {
    id: string | number;
    /** Si és cert (per defecte) l'enllaç s'obre en una pestanya nova. */
    novaPestanya?: boolean;
    children?: ReactNode;
}

/**
 * Enllaç a la vista de contingut (expedient/document) que, per defecte, s'obre en una pestanya nova.
 * Comportament estàndard dels enllaços a expedients dels grids d'accions massives i consultes.
 * Amb novaPestanya={false} la navegació es fa a la pestanya actual (p.ex. als avisos de la capçalera).
 */
export const ContingutLink = ({id, novaPestanya = true, children, ...other}: ContingutLinkProps) => (
    <Link
        component={RouterLink}
        to={`/contingut/${id}`}
        {...(novaPestanya ? {target: '_blank', rel: 'noopener noreferrer'} : {})}
        {...other}
    >
        {children}
    </Link>
);

export default ContingutLink;
