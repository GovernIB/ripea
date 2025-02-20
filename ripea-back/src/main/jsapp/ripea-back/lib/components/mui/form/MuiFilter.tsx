import React from 'react';
import Filter, { FilterProps } from '../../form/Filter';
import { useOptionalGridContext } from '../grid/GridContext';

export const MuiFilter: React.FC<FilterProps> = (props) => {
    const {
        onSpringFilterChange,
        children,
        ...otherProps
    } = props;
    const gridContext = useOptionalGridContext();
    const handleSpringFilterChange = (filter: string | undefined) => {
        if (gridContext != null) {
            gridContext.apiRef.current?.setFilter(filter);
        }
        onSpringFilterChange?.(filter);
    }
    return <Filter
        onSpringFilterChange={handleSpringFilterChange}
        {...otherProps}>
        {children}
    </Filter>;
}

export default MuiFilter;