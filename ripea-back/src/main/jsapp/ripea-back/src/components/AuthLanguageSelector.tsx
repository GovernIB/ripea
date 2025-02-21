import React from 'react';
import Box from '@mui/material/Box';
import ButtonGroup from '@mui/material/ButtonGroup';
import Button from '@mui/material/Button';
import { useBaseAppContext } from 'reactlib';

type AuthLanguageSelectorProps = {
    languages?: string[]; // 'ca', 'es, 'en', ...
    onLanguageChange?: (language?: string) => void;
} & any;

//const interleave = (arr: any, what: any) => [].concat(...arr.map((n: any) => [n, what])).slice(0, -1);

const AuthLanguageSelector: React.FC<AuthLanguageSelectorProps> = (props) => {
    const {
        languages,
        onLanguageChange,
    } = props;
    const { currentLanguage, setCurrentLanguage } = useBaseAppContext();
    React.useEffect(() => {
        onLanguageChange?.(currentLanguage);
    }, [currentLanguage]);
    return languages && <Box sx={{ display: 'flex', justifyContent: 'center', mb: 1 }}>
        <ButtonGroup size="small">
            {languages?.map((l: string, i: number) => <Button
                key={i}
                onClick={() => setCurrentLanguage(l)}
                sx={{ fontWeight: currentLanguage === l ? 'bold' : 'normal' }}>
                {l}
            </Button>)}
        </ButtonGroup>
    </Box>;
}

export default AuthLanguageSelector;