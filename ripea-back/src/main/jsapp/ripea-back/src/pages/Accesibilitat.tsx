import { useTranslation } from 'react-i18next';
import { Box, Typography } from '@mui/material';
import { BasePage } from 'reactlib';
import {CardPage} from "../components/CardData.tsx";

const Accesibilitat: React.FC = () => {
    const { t } = useTranslation();
    return <BasePage>
        <CardPage>
            <Box
                sx={{
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                    minHeight: 'calc(100vh - 172px)',
                }}>
                <Typography variant="h2">En proceso</Typography>
            </Box>
        </CardPage>
    </BasePage>;
}

export default Accesibilitat;