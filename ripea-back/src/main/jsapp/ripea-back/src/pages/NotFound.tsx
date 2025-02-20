import { useTranslation } from 'react-i18next';
import { Box, Typography } from '@mui/material';
import { BasePage } from 'reactlib';

const NotFound: React.FC = () => {
    const { t } = useTranslation();
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

export default NotFound;