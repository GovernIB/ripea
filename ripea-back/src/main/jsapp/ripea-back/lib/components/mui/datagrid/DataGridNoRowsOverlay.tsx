import React from 'react';
import Box from '@mui/material/Box';
import DataNoRows from '../datacommon/DataNoRows';

type DataGridNoRowsOverlayProps = {
    findDisabled?: boolean;
};

const DataGridNoRowsOverlay: React.FC<DataGridNoRowsOverlayProps> = (props) => {
    const { findDisabled } = props;
    return <Box sx={{
        width: '100%',
        height: '100%',
        display: 'flex',
        alignItems: 'center',
    }}>
        <DataNoRows findDisabled={findDisabled} />
    </Box>;
}

export default DataGridNoRowsOverlay;