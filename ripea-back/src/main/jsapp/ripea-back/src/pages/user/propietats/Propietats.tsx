import React from 'react';
import { useTranslation } from 'react-i18next';
// import { useDebounce } from 'reactlib';
import {useDebounce} from "../../../../lib/util/useDebounce.ts";
import { PropietatsGroups } from './PropietatsGroups.tsx';
import { PropietatsProps } from './PropietatsProps.tsx';
import {TextField, InputAdornment, Icon, IconButton, Grid2 as Grid, Button} from "@mui/material";
import {GridPage} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";

const PropietatsQuickFilter: React.FC<{ onChange: (quickFilter: string | undefined) => void }> = (
    props
) => {
    const { onChange } = props;
    // const { t } = useTranslation();
    const [quickFilter, setQuickFilter] = React.useState<string>('');
    const quickFilterDebounced = useDebounce(quickFilter);
    React.useEffect(() => {
        onChange?.(quickFilterDebounced);
    }, [quickFilterDebounced]);
    return (
        <TextField
            value={quickFilter}
            onChange={(event) => setQuickFilter(event.target.value)}
            // label={t('page.propietats.find')}
            variant="outlined"
            // fullWidth
            size="small"
            slotProps={{
                input: {
                    startAdornment: (
                        <InputAdornment position="start">
                            <Icon fontSize="small">search</Icon>
                        </InputAdornment>
                    ),
                    endAdornment: quickFilter && (
                        <InputAdornment position="end">
                            <IconButton size="small" onClick={() => setQuickFilter('')}>
                                <Icon fontSize="inherit">clear</Icon>
                            </IconButton>
                        </InputAdornment>
                    ),
                },
            }}
        />
    );
};

const Propietats: React.FC = () => {
    const {t} = useTranslation();
    const [quickFilter, setQuickFilter] = React.useState<string>();
    const [selectedGroup, setSelectedGroup] = React.useState<any>();
    return <GridPage disableMargins>
        <CardPage title={t('page.user.menu.props')}>
            <Grid container spacing={2}>
                <Grid size={12} sx={{ px: 1 }} display={'flex'} justifyContent={'end'}>
                    <PropietatsQuickFilter onChange={setQuickFilter} />
                    <Button variant="outlined" size="small" sx={{ borderRadius: '4px' }}>
                        <Icon>cached</Icon>{t('Sincronitzar amb JBoss')}
                    </Button>
                </Grid>
                <Grid size={3}>
                    <PropietatsGroups quickFilter={quickFilter} onChange={setSelectedGroup} />
                </Grid>
                <Grid size={9}>
                    <PropietatsProps group={selectedGroup} quickFilter={quickFilter} />
                </Grid>
            </Grid>
        </CardPage>
    </GridPage>
};

export default Propietats;
