import React from 'react';
import Icon from '@mui/material/Icon';
import { GridActionsCellItem } from '@mui/x-data-grid-pro';
import { useBaseAppContext } from '../../BaseAppContext';

type DataGridActionItemProps = {
    id: any;
    label: string;
    title?: string;
    icon?: string;
    row?: any;
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
    label: string,
    title?: string,
    icon?: string,
    row?: any,
    linkTo?: string,
    linkState?: any,
    onClick?: DataGridActionItemOnClickFn,
    showInMenu?: boolean,
    disabled?: boolean): React.ReactElement => {
    return <DataGridActionItem
        id={id}
        label={label}
        title={title}
        icon={icon}
        row={row}
        linkTo={linkTo}
        linkState={linkState}
        onClickCustom={onClick}
        showInMenu={showInMenu}
        disabled={disabled} />;
}

const DataGridActionItem: React.FC<DataGridActionItemProps> = (props) => {
    const {
        id,
        label,
        title: titleProp,
        icon,
        row,
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
    const title = !showInMenu ? label : titleProp;
    const actionCellItem = <GridActionsCellItem
        label={label}
        title={title}
        icon={icon ? <Icon>{icon}</Icon> : undefined}
        onClick={event => {
            onClickCustom ? onClickCustom?.(id, row, event) : onClick(event);
        }}
        disabled={disabled}
        {...additionalProps} />;
    return title && disabled ? <div title={title}>{actionCellItem}</div> : actionCellItem;
}

export default DataGridActionItem;