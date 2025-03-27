import React from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Icon from '@mui/material/Icon';
import { useBaseAppContext } from '../../BaseAppContext';

type DataNoRowsProps = {
    findDisabled?: boolean;
};

const DataNoRows: React.FC<DataNoRowsProps> = (props) => {
    const { findDisabled } = props;
    const { t } = useBaseAppContext();
    return <Box sx={{
        width: '100%',
        textAlign: 'center',
        p: 2,
    }}>
        {findDisabled ? <>
            <Icon fontSize="large" color="disabled">pending</Icon>
            <Typography variant="h5" color="text.secondary">{t('datacommon.findDisabled')}</Typography>
        </> : <>
            <Icon fontSize="large" color="disabled">block</Icon>
            <Typography variant="h5" color="text.secondary">{t('datacommon.noRows')}</Typography>
        </>}
    </Box>
}

export default DataNoRows;