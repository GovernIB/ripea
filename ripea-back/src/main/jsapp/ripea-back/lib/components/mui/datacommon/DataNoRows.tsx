import React from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Icon from '@mui/material/Icon';
import { useBaseAppContext } from '../../BaseAppContext';

const DataNoRows: React.FC = () => {
    const { t } = useBaseAppContext();
    return <Box sx={{
        width: '100%',
        textAlign: 'center',
        p: 2,
    }}>
        <Icon fontSize="large" color="disabled">block</Icon>
        <Typography variant="h5" color="text.secondary">{t('datacommon.noRows')}</Typography>
    </Box>
}

export default DataNoRows;