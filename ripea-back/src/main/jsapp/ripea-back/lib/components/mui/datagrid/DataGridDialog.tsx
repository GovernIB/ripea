import React from 'react';
import { GridColDef, GridRowParams, GridSortModel } from '@mui/x-data-grid-pro';
import { ResourceType } from '../../ResourceApiContext';
import Dialog, { DialogProps } from '../Dialog';
import MuiDataGrid from './MuiDataGrid';

type DataGridDialogProps = DialogProps & {
    resourceName: string;
    columns: GridColDef[];
    resourceType?: ResourceType;
    resourceTypeCode?: string;
    resourceFieldName?: string;
    staticFilter?: string;
    staticSortModel?: GridSortModel;
    namedQueries?: string[];
    perspectives?: string[];
    onRowClick?: (params: GridRowParams) => void;
    height?: number | null;
};

export type DataGridDialogShowFn = (title: string | null, height?: number | null, componentProps?: any) => Promise<string>;

export type UseDataGridDialogFn = (
    resourceName: string,
    columns: GridColDef[],
    resourceType?: ResourceType,
    resourceTypeCode?: string,
    resourceFieldName?: string,
    staticFilter?: string,
    staticSortModel?: GridSortModel,
    namedQueries?: string[],
    perspectives?: string[]) => [DataGridDialogShowFn, React.ReactElement];

export const useDataGridDialog: UseDataGridDialogFn = (
    resourceName: string,
    columns: GridColDef[],
    resourceType?: ResourceType,
    resourceTypeCode?: string,
    resourceFieldName?: string,
    staticFilter?: string,
    staticSortModel?: GridSortModel,
    namedQueries?: string[],
    perspectives?: string[]) => {
    const [open, setOpen] = React.useState<boolean>(false);
    const [title, setTitle] = React.useState<string | null>();
    const [height, setHeight] = React.useState<number | undefined | null>();
    const [componentProps, setComponentProps] = React.useState<any>();
    const [resolveFn, setResolveFn] = React.useState<(value?: any) => void>();
    const [rejectFn, setRejectFn] = React.useState<(value: any) => void>();
    const showDialog = (title: string | null, height?: number | null, componentProps?: any) => {
        setTitle(title);
        setHeight(height);
        setComponentProps(componentProps);
        setOpen(true);
        return new Promise<string>((resolve, reject) => {
            setResolveFn(() => resolve);
            setRejectFn(() => reject);
        });
    }
    const closeCallback = () => {
        // S'ha tancat la modal o s'ha fet click a fora de la finestra
        rejectFn?.(undefined);
        setOpen(false);
    }
    const handleRowClick = (params: GridRowParams) => {
        resolveFn?.(params.row);
        setOpen(false);
    }
    const dialogComponent = <DataGridDialog
        resourceName={resourceName}
        columns={columns}
        resourceType={resourceType}
        resourceTypeCode={resourceTypeCode}
        resourceFieldName={resourceFieldName}
        staticFilter={staticFilter}
        staticSortModel={staticSortModel}
        namedQueries={namedQueries}
        perspectives={perspectives}
        onRowClick={handleRowClick}
        height={height}
        open={open}
        closeCallback={closeCallback}
        title={title}
        componentProps={componentProps} />;
    return [showDialog, dialogComponent];
}

export const DataGridDialog: React.FC<DataGridDialogProps> = (props) => {
    const {
        columns,
        resourceName,
        resourceType,
        resourceTypeCode,
        resourceFieldName,
        staticFilter,
        staticSortModel,
        namedQueries,
        perspectives,
        onRowClick,
        height,
        children,
        ...otherProps
    } = props;
    return <Dialog {...otherProps}>
        <MuiDataGrid
            columns={columns}
            resourceName={resourceName}
            resourceType={resourceType}
            resourceTypeCode={resourceTypeCode}
            resourceFieldName={resourceFieldName}
            paginationActive
            titleDisabled
            quickFilterSetFocus
            quickFilterFullWidth
            toolbarHideRefresh
            readOnly
            staticFilter={staticFilter}
            staticSortModel={staticSortModel}
            namedQueries={namedQueries}
            perspectives={perspectives}
            onRowClick={onRowClick}
            height={height ?? 370} />
    </Dialog>;
}

export default DataGridDialog;