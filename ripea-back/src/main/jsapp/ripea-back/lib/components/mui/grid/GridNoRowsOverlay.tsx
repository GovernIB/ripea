import React from 'react';
import Box from '@mui/material/Box';
import Icon from '@mui/material/Icon';
import Typography from '@mui/material/Typography';
import { useBaseAppContext } from '../../BaseAppContext';

const GridNoRowsOverlay: React.FC = () => {
    const { t } = useBaseAppContext();
    return <Box sx={{
        width: '100%',
        height: '100%',
        display: 'flex',
        alignItems: 'center',
    }}>
        <Box sx={{
            width: '100%',
            textAlign: 'center',
        }}>
            <Icon fontSize="large" color="disabled">block</Icon>
            <Typography variant="h5" color="text.secondary">{t('grid.noRows')}</Typography>
        </Box>
    </Box>;
}

export default GridNoRowsOverlay;