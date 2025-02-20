import React from 'react';
import { GridRowSelectionModel } from '@mui/x-data-grid-pro';

export type GridApi = {
    refresh: () => void;
    setFilter: (filter: string | undefined) => void;
};

export type GridApiRef = React.MutableRefObject<GridApi | undefined>;

export type GridContextType = {
    resourceName: string;
    loading: boolean;
    findArgs: any;
    rows: any[];
    selection: GridRowSelectionModel | undefined;
    apiRef: GridApiRef;
}

export const GridContext = React.createContext<GridContextType | undefined>(undefined);
export const useGridContext = () => {
    const context = React.useContext(GridContext);
    if (context === undefined) {
        throw new Error('useGridContext must be used within a GridProvider');
    }
    return context;
}

export const useOptionalGridContext = (): GridContextType | undefined => {
    return React.useContext(GridContext);
}

export default GridContext;