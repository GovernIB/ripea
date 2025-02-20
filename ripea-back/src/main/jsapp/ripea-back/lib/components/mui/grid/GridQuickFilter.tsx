import React from 'react';
import TextField from '@mui/material/TextField';
import InputAdornment from '@mui/material/InputAdornment';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';

type GridQuickFilterProps = {
    value: string;
    onChange: (value: string) => void
} & any;

export const useGridQuickFilter = (initialValue?: string, otherProps?: any): { value: string, component: React.ReactElement } => {
    const [value, setValue] = React.useState(initialValue ?? '');
    const component = <GridQuickFilter
        value={value}
        onChange={setValue}
        {...otherProps} />;
    return { value, component }
}

const GridQuickFilter: React.FC<GridQuickFilterProps> = (props) => {
    const {
        value,
        onChange,
        ...otherProps
    } = props;
    const quickFilterRef = React.useRef<HTMLInputElement>();
    return <TextField
        size="small"
        value={value}
        onChange={(event) => onChange(event.target.value)}
        InputProps={{
            endAdornment: <>
                {value &&
                    <InputAdornment position="end"><IconButton onClick={() => onChange('')}>
                        <Icon fontSize="small">clear</Icon></IconButton>
                    </InputAdornment>
                }
                <InputAdornment position="end"><Icon>search</Icon></InputAdornment>
            </>,
        }}
        inputProps={{
            ref: quickFilterRef
        }}
        {...otherProps} />;
}

export default GridQuickFilter;