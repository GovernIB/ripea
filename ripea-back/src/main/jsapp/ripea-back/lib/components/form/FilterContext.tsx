import React from 'react';

export type FilterApi = {
    clear: (data?: any) => void;
    filter: (data?: any) => void;
};

export type FilterApiRef = React.RefObject<FilterApi | undefined>;

export type FilterContextType = {
    resourceName: string;
    code?: string;
    apiRef: FilterApiRef;
};

export const FilterContext = React.createContext<FilterContextType | undefined>(undefined);

export const useFilterContext = () => {
    const context = React.useContext(FilterContext);
    if (context === undefined) {
        throw new Error('useFilterContext must be used within a FilterProvider');
    }
    return context;
};

export const useOptionalFilterContext = (): FilterContextType | undefined => {
    return React.useContext(FilterContext);
};

export default FilterContext;
