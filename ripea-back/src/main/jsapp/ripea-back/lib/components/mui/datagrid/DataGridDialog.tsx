import React from 'react';
import { GridColDef, GridRowParams } from '@mui/x-data-grid-pro';
import Dialog, { DialogProps } from '../Dialog';
import MuiGrid from './MuiDataGrid';

type DataGridDialogProps = DialogProps & {
    resourceName: string;
    columns: GridColDef[];
    resourceFieldName?: string;
    onRowClick?: (params: GridRowParams) => void;
    height?: number | null;
};

export type DataGridDialogShowFn = (title: string | null, height?: number | null, componentProps?: any) => Promise<string>;

export type UseDataGridDialogFn = (resourceName: string, columns: GridColDef[], resourceFieldName?: string) => [DataGridDialogShowFn, React.ReactElement];

export const useDataGridDialog: UseDataGridDialogFn = (resourceName: string, columns: GridColDef[], resourceFieldName?: string) => {
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
        resourceFieldName={resourceFieldName}
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
        resourceName,
        resourceFieldName,
        columns,
        onRowClick,
        height,
        children,
        ...otherProps
    } = props;
    return <Dialog {...otherProps}>
        <MuiGrid
            resourceName={resourceName}
            resourceFieldName={resourceFieldName}
            columns={columns}
            paginationActive
            titleDisabled
            quickFilterFullWidth
            //toolbarHideExport
            toolbarHideRefresh
            onRowClick={onRowClick}
            height={height ?? 370} />
    </Dialog>;
}

export default DataGridDialog;