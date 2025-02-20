import { useTranslation } from 'react-i18next';
import { Box, Typography } from '@mui/material';
// import { useParams } from 'react-router-dom';
import { BasePage } from 'reactlib';

const Expedient: React.FC = () => {
    const { t } = useTranslation();
    // const { id } = useParams();
    return <BasePage>
        <Box
            sx={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                minHeight: 'calc(100vh - 72px)',
            }}>
            <Typography variant="h2">{t('page.notFound')}</Typography>
        </Box>
    </BasePage>;
}

export default Expedient;