import React from 'react';
import { ResourceType } from '../../ResourceApiContext';
import { MuiDataGridColDef } from '../datagrid/MuiDataGrid';
import { useDataGridDialog, DataGridDialogShowArgs } from '../datagrid/DataGridDialog';

export type DataGridDialogApi = {
    show: (args?: DataGridDialogShowArgs) => Promise<any>;
    close: () => void;
};

export type DataGridDialogProps = {
    resourceName: string;
    title?: string;
    columns: MuiDataGridColDef[];
    resourceType?: ResourceType;
    resourceTypeCode?: string;
    resourceFieldName?: string;
    onRowClickEnabled?: boolean | ((row: any) => boolean);
    dialogComponentProps?: any;
    dataGridComponentProps?: any;
    apiRef?: React.RefObject<DataGridDialogApi | undefined>;
};

export const DataGridDialog: React.FC<DataGridDialogProps> = (props) => {
    const {
        resourceName,
        title: titleProp,
        columns,
        resourceType,
        resourceTypeCode,
        resourceFieldName,
        onRowClickEnabled,
        dialogComponentProps,
        dataGridComponentProps,
        apiRef,
    } = props;
    const [dataGridDialogShow, dataGridDialogComponent, dataGridDialogClose] = useDataGridDialog(
        resourceName,
        columns,
        resourceType,
        resourceTypeCode,
        resourceFieldName,
        onRowClickEnabled,
        dialogComponentProps ?? { fullWidth: true, maxWidth: 'md' },
        dataGridComponentProps
    );
    const show = (args?: DataGridDialogShowArgs) =>
        dataGridDialogShow({
            title: titleProp ?? resourceName,
            ...args,
        });
    const close = () => dataGridDialogClose();
    if (apiRef != null) {
        apiRef.current = { show, close };
    }
    return <>{dataGridDialogComponent}</>;
};

export default DataGridDialog;
