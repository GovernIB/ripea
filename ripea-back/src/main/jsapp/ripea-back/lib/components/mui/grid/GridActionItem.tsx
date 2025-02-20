import React from 'react';
import Icon from '@mui/material/Icon';
import { GridActionsCellItem } from '@mui/x-data-grid-pro';
import { useBaseAppContext } from '../../BaseAppContext';
import { useGridContext } from './GridContext';

type GridActionItemProps = {
    id: any;
    title?: string;
    icon?: string;
    link?: string;
    onClick?: (id: any, event: React.MouseEventHandler<HTMLLIElement>) => void;
    showInMenu?: boolean;
    disabled?: boolean;
};

export const toGridActionItem = (
    id: any,
    title: string,
    icon?: string,
    link?: string,
    onClick?: (id: any, event: React.MouseEventHandler<HTMLLIElement>) => void,
    showInMenu?: boolean,
    disabled?: boolean): React.ReactElement => {
    return <GridActionItem
        id={id}
        title={title}
        icon={icon}
        link={link}
        onClick={onClick}
        showInMenu={showInMenu}
        disabled={disabled} />;
}

const GridActionItem: React.FC<GridActionItemProps> = (props) => {
    const {
        id,
        title,
        icon,
        link,
        onClick,
        showInMenu,
        disabled,
    } = props;
    const { getLinkComponent } = useBaseAppContext();
    const { findArgs } = useGridContext();
    const additionalProps: any = showInMenu ? { showInMenu: true } : {};
    link && (additionalProps['component'] = getLinkComponent());
    link && (additionalProps['to'] = link);
    link && (additionalProps['state'] = { findArgs });
    return <GridActionsCellItem
        label={title}
        title={!showInMenu ? title : undefined}
        icon={icon ? <Icon>{icon}</Icon> : undefined}
        onClick={(event: React.MouseEventHandler<HTMLLIElement>) => onClick?.(id, event)}
        disabled={disabled}
        {...additionalProps} />;
}

export default GridActionItem;