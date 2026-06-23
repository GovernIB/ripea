import React, {useCallback, useEffect} from 'react';
import { useTranslation } from 'react-i18next';
import { useDebounce } from 'reactlib';
import { PropietatsGroups } from './PropietatsGroups.tsx';
import { PropietatsProps } from './PropietatsProps.tsx';
import {TextField, InputAdornment, Icon, IconButton, Grid, Button} from "@mui/material";
import {GridPage, useBaseAppContext, useResourceApiService} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import {useParams} from "react-router-dom";
import Load from "../../../components/Load.tsx";
import {setTitlePage} from "../../../TitleHeaderConfigurator.tsx";

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

export const Propietats: React.FC = () => {
    const {t} = useTranslation();
    const [quickFilter, setQuickFilter] = React.useState<string>();
    const [selectedGroup, setSelectedGroup] = React.useState<any>();

    const {
        isReady: apiIsReady,
        artifactAction: apiAction
    } = useResourceApiService('configResource');
    const {temporalMessageShow} = useBaseAppContext();

    const sync = useCallback(() => {
        if (apiIsReady) {
            apiAction(undefined, {code: 'SYNC_JBOSS'})
                .then(() => {
                    temporalMessageShow(null, t('page.propietats.action.sync.ok'), 'success');
                })
                .catch((error) => {
                    temporalMessageShow(null, error?.message, 'error');
                });
        }
    }, [apiIsReady]);

    return <GridPage autoHeight>
        <CardPage title={t('page.user.menu.props')}>
            <Grid container spacing={2}>
                <Grid size={12} sx={{ px: 1 }} display={'flex'} justifyContent={'end'}>
                    <PropietatsQuickFilter onChange={setQuickFilter} />
                    <Button variant="outlined" size="small" sx={{ borderRadius: '4px' }} onClick={sync}>
                        <Icon>cached</Icon>{t('page.propietats.action.sync.label')}
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

export const PropietatsByEntitat: React.FC = () => {
    const {t} = useTranslation();
    const { id } = useParams();
    const [entity, setEntity] = React.useState<any>();
    const [quickFilter, setQuickFilter] = React.useState<string>();
    const [selectedGroup, setSelectedGroup] = React.useState<any>();

    const {
        isReady: apiIsReady,
        getOne: apiGetOne
    } = useResourceApiService('entitatResource');
    const {temporalMessageShow} = useBaseAppContext();

    useEffect(() => {
        if (apiIsReady) {
            apiGetOne(id)
                .then((response) => setEntity(response))
                .catch((error) => {
                    temporalMessageShow(null, error?.message, 'error');
                });
        }
    }, [apiIsReady]);

    useEffect(() => {
        if (entity) {
            setTitlePage(`${t('page.user.menu.props')} - ${entity?.nom}`)
        }
    }, [entity]);

    return <GridPage autoHeight>
        <CardPage title={`${t('page.user.menu.props')} - ${entity?.nom}`}>
            <Load value={entity}>
                <Grid container spacing={2}>
                    <Grid size={12} sx={{ px: 1 }} display={'flex'} justifyContent={'end'}>
                        <PropietatsQuickFilter onChange={setQuickFilter} />
                    </Grid>
                    <Grid size={3}>
                        <PropietatsGroups quickFilter={quickFilter} onChange={setSelectedGroup} />
                    </Grid>
                    <Grid size={9}>
                        <PropietatsProps entitatCodi={entity?.codi} group={selectedGroup} quickFilter={quickFilter} />
                    </Grid>
                </Grid>
            </Load>
        </CardPage>
    </GridPage>
};
