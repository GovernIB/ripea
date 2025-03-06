import React from 'react';
import Box from '@mui/material/Box';
import Filter, { FilterProps } from '../../form/Filter';
import { useOptionalGridContext } from '../datagrid/DataGridContext';

type MuiFilterProps = FilterProps & {
    componentProps?: any;
}

export const MuiFilter: React.FC<MuiFilterProps> = (props) => {
    const {
        componentProps,
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
    return <Box {...componentProps}>
        <Filter
            onSpringFilterChange={handleSpringFilterChange}
            {...otherProps}>
            {children}
        </Filter>
    </Box>;
}

export default MuiFilter;