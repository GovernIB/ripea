import React from 'react';
import Box from '@mui/material/Box';
import DataNoRows from '../datacommon/DataNoRows';

const DataGridNoRowsOverlay: React.FC = () => {
    return <Box sx={{
        width: '100%',
        height: '100%',
        display: 'flex',
        alignItems: 'center',
    }}>
        <DataNoRows />
    </Box>;
}

export default DataGridNoRowsOverlay;