import React from 'react';
import Box from '@mui/material/Box';
import Filter, { FilterProps } from '../../form/Filter';
import { useOptionalDataGridContext } from '../datagrid/DataGridContext';

type MuiFilterProps = FilterProps & {
    componentProps?: any;
    disableGridBinding?: boolean;
}

export const MuiFilter: React.FC<MuiFilterProps> = (props) => {
    const {
        componentProps,
        onSpringFilterChange,
        children,
        disableGridBinding,
        ...otherProps
    } = props;
    const gridContext = useOptionalDataGridContext();
    const handleSpringFilterChange = (filter: string | undefined) => {
        if (gridContext != null && !disableGridBinding) {
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