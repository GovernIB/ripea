import React from 'react';
import Select, { SelectChangeEvent } from '@mui/material/Select';
import Typography from '@mui/material/Typography';
import MenuItem from '@mui/material/MenuItem';
import ListItemText from '@mui/material/ListItemText';
import { useBaseAppContext } from 'reactlib';

/*const allLanguages = [{
    locale: 'ca',
    name: 'Català'
}, {
    locale: 'es',
    name: 'Castellà'
}];*/

export type LanguageItem = {
    locale: string;
    name: string;
    flag?: string;
};

type HeaderLanguageSelectorProps = {
    languages?: string[];
    onLanguageChange?: (language?: string) => void;
} & any;

const HeaderLanguageSelector: React.FC<HeaderLanguageSelectorProps> = (props) => {
    const {
        languages,
        onLanguageChange,
        ...otherProps
    } = props;
    const { currentLanguage, setCurrentLanguage } = useBaseAppContext();
    React.useEffect(() => {
        onLanguageChange?.(currentLanguage);
    }, [currentLanguage]);
    const handleOnChange = (event: SelectChangeEvent) => {
        const value = event.target.value;
        setCurrentLanguage(value);
    }
    const {
        size: otherSize,
        sx: otherSx,
        ...otherOtherProps
    } = otherProps;
    const size = otherSize ?? 'small';
    return languages ? <Select
        value={currentLanguage ?? ''}
        onChange={handleOnChange}
        renderValue={() => {
            const currentLanguageItem = languages?.find((l: LanguageItem) => l.locale === currentLanguage);
            return currentLanguageItem ? <Typography variant="body2" sx={{ textAlign: 'center' }}>
                {currentLanguageItem.name}
            </Typography> : null;
        }}
        size={size}
        sx={{ backgroundColor: 'white', ...otherSx }}
        {...otherOtherProps}>
        {languages?.map((l: LanguageItem, i: number) => <MenuItem key={i} value={l.locale}>
            <ListItemText>{l.name}</ListItemText>
        </MenuItem>)}
    </Select> : null;
}

export default HeaderLanguageSelector;