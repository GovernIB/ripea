import React from 'react';
import Box from '@mui/material/Box';
import { GridRow as MuiGridRow } from '@mui/x-data-grid';
import { useBaseAppContext } from '../../BaseAppContext';

type DataGridRowProps = any & {
    cursorPointer?: boolean;
};

const DataGridRow: React.FC<DataGridRowProps> = (props) => {
    const {
        linkTo,
        cursorPointer,
        rowId,
        style,
        ...otherProps
    } = props;
    const { getLinkComponent } = useBaseAppContext();
    const row = <MuiGridRow
        rowId={rowId}
        style={{ cursor: cursorPointer ? 'pointer' : undefined, ...style }}
        {...otherProps} />;
    return linkTo ? <Box
        component={getLinkComponent()}
        to={linkTo.replace('{{id}}', '' + rowId)}
        sx={{
            color: 'inherit',
            textDecoration: 'inherit',
        }}>{row}</Box> : row;
}

export default DataGridRow;