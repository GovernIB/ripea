import React from 'react';
import Icon from '@mui/material/Icon';
import { GridActionsCellItem } from '@mui/x-data-grid-pro';
import { useBaseAppContext } from '../../BaseAppContext';

type DataGridActionItemProps = {
    id: any;
    title?: string;
    row?: any;
    icon?: string;
    linkTo?: string;
    linkState?: any;
    onClick?: any;
    onClickCustom?: DataGridActionItemOnClickFn;
    showInMenu?: boolean;
    disabled?: boolean;
};

export type DataGridActionItemOnClickFn = (id: any, row: any, event: React.MouseEvent) => void;

export const toDataGridActionItem = (
    id: any,
    title: string,
    row?: any,
    icon?: string,
    linkTo?: string,
    linkState?: any,
    onClick?: DataGridActionItemOnClickFn,
    showInMenu?: boolean,
    disabled?: boolean): React.ReactElement => {
    return <DataGridActionItem
        id={id}
        title={title}
        row={row}
        icon={icon}
        linkTo={linkTo}
        linkState={linkState}
        onClickCustom={onClick}
        showInMenu={showInMenu}
        disabled={disabled} />;
}

const DataGridActionItem: React.FC<DataGridActionItemProps> = (props) => {
    const {
        id,
        title,
        row,
        icon,
        linkTo,
        linkState,
        onClick,
        onClickCustom,
        showInMenu,
        disabled,
    } = props;
    const { getLinkComponent } = useBaseAppContext();
    const additionalProps: any = showInMenu ? { showInMenu: true } : {};
    linkTo && (additionalProps['component'] = getLinkComponent());
    linkTo && (additionalProps['to'] = linkTo);
    linkState && (additionalProps['state'] = linkState);
    return <GridActionsCellItem
        label={title}
        title={!showInMenu ? title : undefined}
        icon={icon ? <Icon>{icon}</Icon> : undefined}
        onClick={event => {
            onClickCustom ? onClickCustom?.(id, row, event) : onClick(event);
        }}
        disabled={disabled}
        {...additionalProps} />;
}

export default DataGridActionItem;