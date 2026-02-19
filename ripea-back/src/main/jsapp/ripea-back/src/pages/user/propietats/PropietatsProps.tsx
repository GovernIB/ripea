import React from 'react';
import { useTranslation } from 'react-i18next';
import {Grid2 as Grid} from '@mui/material';
import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import {
    MuiForm,
    FormField,
    useBaseAppContext,
    useResourceApiService,
    // ResourceApiRequestArgs,
} from 'reactlib';
import {ResourceApiRequestArgs} from "../../../../lib/components/ResourceApiProvider.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import {DetailCard} from "../../../components/CardData.tsx";

type PropsContextType = {
    // apiCreate: (args: ResourceApiRequestArgs) => Promise<any>;
    apiPatch: (id: any, args: ResourceApiRequestArgs) => Promise<any>;
};
const PropsContext = React.createContext<PropsContextType | undefined>(undefined);
const usePropsContext = () => {
    const context = React.useContext(PropsContext);
    if (context === undefined) {
        throw new Error('usePropsContext must be used within a PropsContext provider');
    }
    return context;
};

const fieldPropType = (typeCode: string, typeValue?: string) => {
    if (typeValue != null) {
        return 'search';
    } else {
        switch (typeCode) {
            case 'INT':
            case 'FLOAT':
                return 'number';
            case 'BOOL':
                return 'checkbox';
            default:
                return 'text';
        }
    }
};

export const TextHighlight: React.FC<{ text: string; match?: string; ignoreCase?: boolean }> = (
    props
) => {
    const { text, match, ignoreCase } = props;
    if (!match) {
        return <Typography>{text}</Typography>;
    }
    const escapedMatch = match.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    const flags = ignoreCase ? 'gi' : 'g';
    const pattern = new RegExp(`(${escapedMatch})`, flags);
    const parts = text.split(pattern);
    return (
        <Typography>
            {parts.map((part, index) =>
                pattern.test(part) ? (
                    <mark key={index}>{part}</mark>
                ) : (
                    <span key={index}>{part}</span>
                )
            )}
        </Typography>
    );
};

const PropsListItem: React.FC<{ item: any; highlight?: string }> = (props) => {
    const { item, highlight } = props;
    const { t } = useTranslation();
    const { apiPatch } = usePropsContext();
    const { temporalMessageShow } = useBaseAppContext();
    const [initialValue, setInitialValue] = React.useState<any | undefined>(item?.value);
    const [changedValue, setChangedValue] = React.useState<any | undefined>(item?.value);
    const disabled = item.jbossProperty;
    const password = item.type.id === 'PASSWORD' ? true : undefined;
    const decimalScale = item.type.id === 'INT' ? 0 : undefined;

    const field = getFieldFromItem(item)

    const handleFieldOnChange = (value: any) => {
        setChangedValue(value);
    };
    const handleSaveClick = () => {
        apiPatch(item.id, { data: { value: changedValue } })
            .then((response) => {
                setInitialValue(response.value)
                temporalMessageShow(null, t('page.propietats.save.success'), 'success');
            })
            .catch((error) =>
                temporalMessageShow(t('page.propietats.save.error'), error.message, 'error')
            );
    };
    const handleExpandClick = () => {
        console.log('>>> expand (TODO)', item);
    };
    return (
        <Grid container spacing={2} sx={{ width: '100%' }}>
            <Grid size={5}>
                <TextHighlight text={item.description} match={highlight} ignoreCase />
            </Grid>
            <Grid size={6}>
                <FormField
                    field={field}
                    name={item.key}
                    value={item.value}
                    inline
                    // password={password}
                    decimalScale={decimalScale}
                    disabled={disabled}
                    onChange={handleFieldOnChange}
                    componentProps={{ type: password ?'password' :'text', helperText: item.key }}
                />
            </Grid>
            <Grid size={1}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                    <IconButton
                        size="small"
                        onClick={handleSaveClick}
                        color={'success'}
                        disabled={disabled || changedValue === initialValue}>
                        <Icon fontSize="small" >save</Icon>
                    </IconButton>
                    {item.configurable && (
                        <IconButton size="small" onClick={handleExpandClick} sx={{ ml: 1 }}>
                            <Icon sx={{m:0}} fontSize="small">more_vert</Icon>
                        </IconButton>
                    )}
                </Box>
            </Grid>
        </Grid>
    );
};

const getFieldFromItem = (item:any) => {
    const type = fieldPropType(item.type.id, item.type.description);
    const options = item.type.description
        ? Object.fromEntries(
            item.type.description.split(',').map((v: string) => [v, v])
        )
        : undefined;
    return {
        label: item.description,
        name: item.key,
        type,
        value: item.value,
        options,
    };
}

export const PropietatsProps: React.FC<{ quickFilter?: string; group?: any }> = (props) => {
    const { quickFilter, group } = props;
    const { t } = useTranslation();
    const {
        isReady: apiIsReady,
        find: apiFind,
        // create: apiCreate,
        patch: apiPatch,
    } = useResourceApiService('configResource');
    const [configs, setConfigs] = React.useState<any[]>();
    React.useEffect(() => {
        if (apiIsReady && group != null) {
            const args = {
                quickFilter,
                filter: builder.and(
                    builder.eq('entitatCodi', null),
                    builder.eq('organCodi', null),
                    builder.eq('group.key', `'${group.id}'`),
                ),
                sorts: ['position,asc'],
                unpaged: true,
            };
            apiFind(args).then((response) => {
                const configs = response.rows.filter(() => true);
                setConfigs(configs);
            });
        }
    }, [apiIsReady, quickFilter, group]);
    const propsContextValue = {
        // apiCreate,
        apiPatch,
    };
    return (
        group != null &&
        configs != null && (<>
            <PropsContext.Provider value={propsContextValue}>
                <Box sx={{ px: 3 }}>
                    <DetailCard title={group.description}>
                        <Grid size={12}>
                        <List component={Paper}>
                            {configs.length ? (
                                <MuiForm
                                    resourceName="configResource"
                                    hiddenToolbar
                                    commonFieldComponentProps={{ size: 'small' }}>
                                    {configs?.map((c) => (
                                        <ListItem key={c.key} disablePadding>
                                            <ListItemButton disableRipple>
                                                <PropsListItem item={c} highlight={quickFilter} />
                                            </ListItemButton>
                                        </ListItem>
                                    ))}
                                </MuiForm>
                            ) : (
                                <Box
                                    sx={{
                                        width: '100%',
                                        textAlign: 'center',
                                        px: 2,
                                        py: 4,
                                    }}>
                                    <Icon fontSize="large" color="disabled">
                                        block
                                    </Icon>
                                    <Typography variant="h5" color="text.secondary">
                                        {t('page.propietats.empty')}
                                    </Typography>
                                </Box>
                            )}
                        </List>
                        </Grid>
                    </DetailCard>
                </Box>
            </PropsContext.Provider>
        </>)
    );
};
