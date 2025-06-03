import React from 'react';
import { GridRowSelectionModel } from '@mui/x-data-grid-pro';

export type MuiDataGridApi = {
    refresh: () => void;
    export: () => void;
    showCreateDialog: (row?: any, additionalData?: any) => void;
    showUpdateDialog: (id: any, row?: any, additionalData?: any) => void;
    setFilter: (filter: string | undefined) => void;
};

export type MuiDataGridApiRef = React.MutableRefObject<MuiDataGridApi | undefined>;

export type DataGridContextType = {
    resourceName: string;
    loading: boolean;
    findArgs: any;
    rows: any[];
    selection: GridRowSelectionModel | undefined;
    apiRef: MuiDataGridApiRef;
}

export const DataGridContext = React.createContext<DataGridContextType | undefined>(undefined);
export const useDataGridContext = () => {
    const context = React.useContext(DataGridContext);
    if (context === undefined) {
        throw new Error('useGridContext must be used within a GridProvider');
    }
    return context;
}

export const useOptionalDataGridContext = (): DataGridContextType | undefined => {
    return React.useContext(DataGridContext);
}

export default DataGridContext;