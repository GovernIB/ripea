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
} from 'reactlib';
import * as builder from "../../../util/springFilterUtils.ts";
import {DetailCard} from "../../../components/CardData.tsx";
import {usePropietatsDialog} from "./PropietatsDialog.tsx";
import {DraggableGridRowHandler, DraggableItem} from "../../../components/DraggableContext.tsx";
import {DndContext} from "@dnd-kit/core";

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

const PropsListItem: React.FC<{ item: any; highlight?: string, handleSaveClick: (value: any) => void }> = (props) => {
    const { item, highlight, handleSaveClick } = props;
    const disabled = item.jbossProperty;
    const password = item.type.id === 'PASSWORD' ? true : undefined;
    const decimalScale = item.type.id === 'INT' ? 0 : undefined;

    const field = getFieldFromItem(item)
    const [changedValue, setChangedValue] = React.useState<any | undefined>(field?.value);

    const handleFieldOnChange = (value: any) => {
        setChangedValue(value);
    };
    const {handleOpen, dialog} = usePropietatsDialog();
    return (
        <DraggableItem id={item.id} data={item} style={{ width: '100%' }}>
        <Grid container spacing={2} sx={{ width: '100%' }}>
            <Grid size={4.5}>
                <TextHighlight text={item.description} match={highlight} ignoreCase />
            </Grid>
            <Grid size={6}>
                <FormField
                    field={field}
                    name={'item.key'}
                    value={changedValue}
                    inline
                    // password={password}
                    decimalScale={decimalScale}
                    disabled={disabled}
                    onChange={handleFieldOnChange}
                    componentProps={{ type: password ?'password' :field.type, placeholder: item.key, helperText: item.key }}
                />
            </Grid>
            <Grid size={1.5}>
                <Box sx={{ display: 'flex', justifyContent: 'end' }}>
                     {(!disabled) && (<>
                        <IconButton
                            size="small"
                            onClick={() => handleSaveClick(changedValue)}
                            color={'success'}>
                            <Icon fontSize="small">save</Icon>
                        </IconButton>
                         {(!item?.entitatCodi && !item?.organCodi) &&
                             <IconButton size="small" onClick={() => handleOpen(item.id, {...item, value: changedValue})}>
                                 <Icon sx={{m:0}} fontSize="small">settings</Icon>
                             </IconButton>}
                    </>)}
                    {(!item?.entitatCodi && !item?.organCodi && !highlight) &&
                        <DraggableGridRowHandler/>
                    }
                </Box>
            </Grid>
            {dialog}
        </Grid>
        </DraggableItem>
    );
};

export const getFieldFromItem = (item:any) => {
    const type = fieldPropType(item.type.id, item.type.description);
    const options = item.type.description
        ? Object.fromEntries(
            item.type.description.split(',').map((v: string) => [v, v])
        )
        : undefined;
    const value = (item.type.id == 'BOOL' && typeof item.value == 'string')
        ? item.value === "true"
        : item.value

    return {
        label: item.description,
        name: item.key,
        type,
        value,
        options,
    };
}

export const PropietatsProps: React.FC<{ quickFilter?: string; entitatCodi?: string; group?: any }> = (props) => {
    const { quickFilter, entitatCodi, group } = props;
    const { t } = useTranslation();
    const {
        isReady: apiIsReady,
        find: apiFind,
        artifactAction: apiAction,
    } = useResourceApiService('configResource');
    const { temporalMessageShow } = useBaseAppContext();
    const [configs, setConfigs] = React.useState<any[]>();

    const refresh = () => {
        const args = {
            quickFilter,
            filter: builder.and(
                entitatCodi
                    ? builder.eq('entitatCodi', `'${entitatCodi}'`)
                    : builder.eq('entitatCodi', null),
                builder.eq('organCodi', null),
                builder.eq('group.key', `'${group.id}'`),
            ),
            namedQueries: entitatCodi ?[`BY_ENTITAT#${entitatCodi}`] :undefined,
            sorts: ['position,asc'],
            unpaged: true,
        };
        apiFind(args).then((response) => {
            const configs = response.rows.filter(() => true);
            setConfigs(configs);
        });
    }
    React.useEffect(() => {
        if (apiIsReady && group != null) {
            refresh();
        }
    }, [apiIsReady, quickFilter, group]);

    const update = (res:any, value:any) => {
        // console.log("update", value, { ...res, value })
        apiAction(undefined, { code: 'UPDATE', data: { ...res, value } })
            .then(() => {
                temporalMessageShow(null, t('page.propietats.action.update.ok'), 'success');
            })
            .catch((error) =>
                temporalMessageShow(null, error.message, 'error')
            );
    };

    const reordering = (key:any, position:number) => {
        apiAction(undefined, { code: 'REORDER', data: {key, position} })
            .then(() => refresh())
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    };

    const handleDragEnd = (event: any) => {
        const sourceData = event.active.data.current;
        const targetData = event.over.data.current;
        // console.log('>>> ', sourceData.id, '(', sourceData.position, ') ->', targetData.id, '(', targetData.position, ')')
        if (sourceData.id != targetData.id) {
            reordering(sourceData.id, targetData.position)
        }
    }

    return (
        group != null &&
        configs != null && (<>
            <Box sx={{ px: 3 }}>
                <DetailCard title={group.description}>
                    <Grid size={12}><DndContext onDragEnd={handleDragEnd}>
                    <List component={Paper}>
                        {configs.length ? (
                            <MuiForm
                                resourceName="configResource"
                                hiddenToolbar
                                commonFieldComponentProps={{ size: 'small' }}>
                                {configs?.map((c) => (
                                    <ListItem key={c.key} disablePadding>
                                        <ListItemButton disableRipple>
                                            <PropsListItem item={c} highlight={quickFilter} handleSaveClick={(value:any) => update(c, value)}/>
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
                    </DndContext></Grid>
                </DetailCard>
            </Box>
        </>)
    );
};
