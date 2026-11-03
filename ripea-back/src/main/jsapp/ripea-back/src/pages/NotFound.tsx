import { useTranslation } from 'react-i18next';
import { Box, Typography } from '@mui/material';
import { BasePage } from 'reactlib';

type NotFoundProps = {
    message?: string;
    variant?: 'h2' | 'h3' | 'h4' | 'h5';
};

const NotFound: React.FC<NotFoundProps> = ({ message, variant = 'h2' }) => {
    const { t } = useTranslation();
    return (
        <BasePage expandHeight>
            <Box
                sx={{
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                    textAlign: 'center',
                    height: '100%',
                    px: 2,
                }}
            >
                <Typography variant={variant}>{message ?? t('page.notFound')}</Typography>
            </Box>
        </BasePage>
    );
};

export default NotFound;
