// import { useTranslation } from 'react-i18next';
// import { Box, Typography } from '@mui/material';
import { GridPage } from 'reactlib';
import { MuiGrid } from '../../../lib/components/mui/grid/MuiGrid';

const ExpedientGrid: React.FC = () => {
    // const { t } = useTranslation();

    const columns = [
        {
            field: 'numero',
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

    return <GridPage>
        <MuiGrid titleDisabled resourceName={'expedientResource'} columns={columns} />
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