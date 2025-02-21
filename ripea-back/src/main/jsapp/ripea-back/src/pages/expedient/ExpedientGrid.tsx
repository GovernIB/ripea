import React from 'react';
// import { useTranslation } from 'react-i18next';
// import { Box, Typography } from '@mui/material';
import {
    GridPage,
    MuiGrid,
    MuiFilter,
    FormField,
    //useFilterApiRef
} from 'reactlib';

const ExpedientGrid: React.FC = () => {
    // const { t } = useTranslation();
    //const filterRef = useFilterApiRef();
    //filterRef.current.clear();
    const columns = [
        {
            field: 'codi',
            flex: 0.5,
        },
        {
            field: 'nom',
            flex: 2,
        },
        {
            field: 'tipusStr',
            flex: 1.5,
        },
    ];
    const springFilterBuilder = (data: any) => {
        console.log('>>> springFilterBuilder', data)
        return '';
    }
    return <GridPage>
        <MuiFilter
            resourceName="expedientResource"
            code="EXPEDIENT_FILTER"
            springFilterBuilder={springFilterBuilder}
            commonFieldComponentProps={{ size: 'small' }}
            componentProps={{
                sx: { mb: 2 }
            }}
            apiRef={}>
            <FormField name="nom" />
        </MuiFilter>
        <MuiGrid
            resourceName="expedientResource"
            columns={columns}
            paginationActive
            filter={''} />
        {/* <Box
            sx={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                minHeight: 'calc(100vh - 72px)',
            }}>
            <Typography variant="h2">{t('page.notFound')}</Typography>
        </Box> */}
    </GridPage>;
}

export default ExpedientGrid;