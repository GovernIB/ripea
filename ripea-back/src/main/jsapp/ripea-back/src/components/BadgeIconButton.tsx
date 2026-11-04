import React from 'react';
import { Badge, BadgeProps, Icon, IconButton, IconButtonProps } from '@mui/material';
import { useNavigate } from 'react-router-dom';

type BadgeIconButtonProps = Omit<IconButtonProps, 'children' | 'color'> & {
    icon: string;
    badgeContent?: BadgeProps['badgeContent'];
    badgeColor?: BadgeProps['color'];
    /** Ruta on es navega en fer clic. Si no s'indica, es crida `onClick`. */
    to?: string;
};

/**
 * Botó d'icona amb un badge (comptador), pensat per a les columnes de les graelles (permisos, validacions...).
 * La icona es manté a la mida normal (`grid-icon-medium`, veure StyledMuiGrid) i el clic no arriba a la fila.
 */
const BadgeIconButton = (props: BadgeIconButtonProps) => {
    const { icon, badgeContent, badgeColor = 'primary', to, title, onClick, ...other } = props;
    const navigate = useNavigate();
    return (
        <IconButton
            color="inherit"
            aria-label={title ?? icon}
            title={title}
            {...other}
            onClick={(event: React.MouseEvent<HTMLButtonElement>) => {
                event.stopPropagation();
                if (to) {
                    navigate(to);
                } else {
                    onClick?.(event);
                }
            }}
        >
            <Badge badgeContent={badgeContent} color={badgeColor} showZero>
                <Icon className="grid-icon-medium">{icon}</Icon>
            </Badge>
        </IconButton>
    );
};
export default BadgeIconButton;
