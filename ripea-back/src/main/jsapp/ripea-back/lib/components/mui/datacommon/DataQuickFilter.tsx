import React from 'react';
import TextField from '@mui/material/TextField';
import InputAdornment from '@mui/material/InputAdornment';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';

type DataQuickFilterProps = {
    value: string;
    setFocus?: true;
    onChange: (value: string) => void
} & any;

export const useDataQuickFilter = (
    initialValue?: string,
    setFocus?: true,
    otherProps?: any): { value: string, component: React.ReactElement } => {
    const [value, setValue] = React.useState(initialValue ?? '');
    const component = <DataQuickFilter
        value={value}
        setFocus={setFocus}
        onChange={setValue}
        {...otherProps} />;
    return { value, component }
}

const DataQuickFilter: React.FC<DataQuickFilterProps> = (props) => {
    const {
        value,
        setFocus,
        onChange,
        ...otherProps
    } = props;
    return <TextField
        size="small"
        value={value}
        inputRef={setFocus ? input => input && input.focus() : undefined}
        onChange={(event) => onChange(event.target.value)}
        slotProps={{
            input: {
                endAdornment: <>
                    {value &&
                        <InputAdornment position="end"><IconButton size="small" onClick={() => onChange('')}>
                            <Icon fontSize="inherit">clear</Icon></IconButton>
                        </InputAdornment>
                    }
                    <InputAdornment position="end"><Icon>search</Icon></InputAdornment>
                </>,
            }
        }}
        {...otherProps} />;
}

export default DataQuickFilter;