import {ReactNode} from "react";
import {Link, LinkProps} from "@mui/material";
import {Link as RouterLink} from "react-router-dom";

interface ContingutLinkProps extends Omit<LinkProps, 'component' | 'href' | 'id'> {
    id: string | number;
    children?: ReactNode;
}

/**
 * Enllaç a la vista de contingut (expedient/document) que s'obre en una pestanya nova.
 * Comportament estàndard dels enllaços a expedients dels grids d'accions massives i consultes.
 */
export const ContingutLink = ({id, children, ...other}: ContingutLinkProps) => (
    <Link
        component={RouterLink}
        to={`/contingut/${id}`}
        target="_blank"
        rel="noopener noreferrer"
        {...other}
    >
        {children}
    </Link>
);

export default ContingutLink;
