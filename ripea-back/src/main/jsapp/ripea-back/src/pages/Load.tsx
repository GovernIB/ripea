import {Box, CircularProgress} from '@mui/material';
import { BasePage } from 'reactlib';

const Load: React.FC = () => {
    return <BasePage>
        <Box
            sx={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
            }}>
            <CircularProgress />
        </Box>
    </BasePage>;
}

export default Load;