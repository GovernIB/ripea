import React, {useEffect, useMemo, useState} from "react";
import { DndContext } from '@dnd-kit/core';
import { FormControl, Grid, Select, MenuItem, Icon, Box } from "@mui/material";
import {
    GridSlots,
    GridTreeDataGroupingCell,
} from "@mui/x-data-grid-pro";
import { useMuiDataGridApiRef, useResourceApiService } from 'reactlib';
import { useTranslation } from "react-i18next";
import ContingutIcon from "./details/ContingutIcon.tsx";
import { useContingutActions } from "./details/ContingutActions.tsx";
import useContingutMassiveActions from "./details/ContingutMassiveActions.tsx";
import StyledMuiGrid, { ToolbarButton } from "../../components/StyledMuiGrid.tsx";
import Load from "../../components/Load.tsx";
import { MenuActionButton } from "../../components/MenuButton.tsx";
import * as builder from '../../util/springFilterUtils.ts';
import { useUserSession } from "../../components/Session.tsx";
import { useSessionList } from "../../components/SessionStorageContext.tsx";
import DropZone from "../../components/DropZone.tsx";
import DocumentsGridForm from "./DocumentGridForm.tsx";
import MetaExpedient from "./details/MetaExpedient.tsx";
import {DraggableGridRow, DraggableGridRowHandler} from "../../components/DraggableContext.tsx";
import useVisualitzar from "./actions/Visualitzar.tsx";

const View = {
    estat: 'TREETABLE_PER_ESTAT',
    tipus: 'TREETABLE_PER_TIPUS_DOCUMENT',
    carpeta: 'TREETABLE_PER_CARPETA',
    icona: 'GRID',
}

const ExpandButton = (props: { value: any, onChange: (value: any) => void, hidden: boolean }) => {
    const { value, onChange, hidden } = props;
    const { t } = useTranslation();

    if (hidden) {
        return <></>
    }

    return <ToolbarButton
        startIcon={<Icon>{value ? 'keyboard_arrow_up' : 'keyboard_arrow_down'}</Icon>}
        onClick={() => onChange(!value)}
        color={'none'}
    >
        {value ? t("common.contract") : t("common.expand")}
    </ToolbarButton>
}

const TreeViewSelector = (props: { value: any, onChange: (value: any) => void }) => {
    const { value, onChange } = props;
    const { t } = useTranslation();

    return <Grid size={3} sx={{ ml: 1 }}>
        <FormControl fullWidth size="small">
            {/*<InputLabel id="demo-simple-select-label">{t('page.document.view.title')}</InputLabel>*/}
            <Select
                sx={{ maxHeight: '32px' }}
                title={t('page.document.view.title')}
                labelId="demo-simple-select-label"
                value={value}
                onChange={(event) => onChange(event.target.value)}
            >
                <MenuItem value={View.estat}>{t('page.document.view.estat')}</MenuItem>
                <MenuItem value={View.tipus}>{t('page.document.view.tipus')}</MenuItem>
                <MenuItem value={View.carpeta}>{t('page.document.view.carpeta')}</MenuItem>
            </Select>
        </FormControl>
    </Grid>
}

const carpetaPerspectives =  ["PATH" , "RESTRICCIONS", "RESPONSABLE_RESTRICCIO"];
export const useExpedientsCarpetes = (commonFilter: string) => {
    const {
        isReady: apiExpedientIsReady,
        find: apiExpedientFindAll,
    } = useResourceApiService('expedientResource');
    const {
        isReady: apiCarpetaIsReady,
        find: apiCarpetaFindAll,
    } = useResourceApiService('carpetaResource');
    const [expedients, setExpedients] = useState<any[]>();
    const [carpetes, setCarpetes] = useState<any[]>();
    const findExpedients = () => {
        return apiExpedientFindAll({perspectives, unpaged: true, filter: commonFilter})
            .then((result)=> setExpedients(result.rows))
            .catch(()=> setExpedients([]))
    }

    const findCarpetes = () => {
        return apiCarpetaFindAll({perspectives: carpetaPerspectives, unpaged: true, filter: commonFilter})
            .then((result)=> setCarpetes(result.rows))
            .catch(()=> setCarpetes([]))
    }
    useEffect(() => {
        if (apiExpedientIsReady) {
            findExpedients();
        }
    }, [apiExpedientIsReady]);
    useEffect(() => {
        if (apiCarpetaIsReady) {
            findCarpetes();
        }
    }, [apiCarpetaIsReady]);
    const refresh = async () => {
        await Promise.allSettled([findExpedients(), findCarpetes()]);
    }
    return {
        isReady: apiExpedientIsReady && apiCarpetaIsReady,
        expedients: expedients,
        carpetes,
        refresh,
    };
}

const perspectives = ["PATH"]
const columns = [
    // {
    //     field: 'nom',
    //     flex: 0.5,
    //     renderCell: (params: any) => <ContingutIcon entity={params?.row}/>
    // },
    {
        field: 'descripcio',
        flex: 0.75,
    },
    {
        field: 'metaDocument',
        flex: 0.6,
    },
    {
        field: 'createdDate',
        flex: 0.55,
    },
    {
        field: 'createdByFullName',
        flex: 0.45,
    },
];
const DocumentsGrid = (props: any) => {
    const { entity, onRowCountChange } = props;
    const { t } = useTranslation();
    const { value: user, rol } = useUserSession();

    const sortModel:any[] = useMemo(() => {
        if (user?.sessionScope?.ordenacioContingutPermesa) {
            return [{ field: 'ordre', sort: 'asc' }];
        }
        return [{ field: 'id', sort: 'desc' }];
    }, [user?.sessionScope?.ordenacioContingutPermesa]);

    const {
        isReady: apiDocumentIsReady,
        patch: apiDocumentPatch,
    } = useResourceApiService('documentResource');
    const {
        isReady: apiCarpetaIsReady,
        patch: apiCarpetaPatch,
    } = useResourceApiService('carpetaResource');

    const commonFilter = useMemo(() => builder.and(
        builder.or(
            builder.eq('expedient.id', entity?.id),
            builder.eq('pare.id', entity?.id),
        ),
        builder.eq('esborrat', 0),
    ), [entity?.id]);

    const { get: getFolderExpand, save: addFolderExpand, removeAll } = useSessionList(`folder_expand#${entity?.id}`)

    const gridApiRef = useMuiDataGridApiRef();
    const [treeView, setTreeView] = useState<boolean>(true);
    const [expand, setExpand] = useState<boolean>(user?.conf?.expedientExpandit);
    const [vista, setVista] = useState<string>(getFolderExpand("vista") ?? user?.conf?.vistaActual);
    const [disabled, setDisabled] = useState<boolean>(false);
    const {
        isReady,
        carpetes,
        expedients,
        refresh: refreshTree
    } = useExpedientsCarpetes(commonFilter);
    const refresh = async () => {
        if (vista == View.carpeta || vista == View.icona) {
            await refreshTree()
        }
        gridApiRef?.current?.refresh?.();
    }

    const {handleOpen: handleVisualitzarOpen, dialog: dialogVisualitzar, isValid} = useVisualitzar();
    const { createActions, actions, components } = useContingutActions(entity, gridApiRef, refresh);
    const { actions: massiveActions, components: massiveComponents } = useContingutMassiveActions(entity, refresh);

    const draggable = useMemo(()=> (
        vista == View.carpeta && (entity?.potModificarContingut || entity?.potModificar) && user?.sessionScope?.ordenacioContingutPermesa
    ),[vista, entity?.potModificarContingut, entity?.potModificar, user?.sessionScope?.ordenacioContingutPermesa])
    const additionalColumns:any[] = useMemo(()=>[
        ...columns,
        ...( draggable? [{
            renderCell: () => <DraggableGridRowHandler />,
            flex: 0.1
        }] : [])
    ], [draggable])

    const onDrop = React.useCallback((adjunt: any) => {
        gridApiRef?.current?.triggerCreate?.(null, { adjunt })
    }, [])

    const handleDragEnd = (event: any) => {
        const sourceData = event.active.data.current;
        const targetData = event.over.data.current;
        //console.log('>>> ', sourceData.nom, '(', sourceData.ordre, ')->', targetData.nom, '(', targetData.ordre, ')')
        if (sourceData.id != targetData.id || sourceData.pare.id != targetData.pare.id) {
            const patchData = {
                ...(targetData.tipus === 'DOCUMENT' && { ordre: targetData.ordre }),
                pare: { id: targetData.tipus === 'DOCUMENT' ? targetData.pare.id : targetData.id },
                ordrePatch: true
            };
            if (sourceData.tipus === 'DOCUMENT') {
                //console.log('>>> document patch', patchData)
                if (apiDocumentIsReady) {
                    apiDocumentPatch(sourceData.id, {data: patchData}).then(() => refresh());
                } else {
                    console.error('Servei de l\'API pels documents no disponible')
                }
            } else if (sourceData.tipus === 'CARPETA') {
                //console.log('>>> carpeta patch', patchData)
                if (apiCarpetaIsReady) {
                    apiCarpetaPatch(sourceData.id, {data: patchData}).then(() => refresh());
                } else {
                    console.error('Servei de l\'API per les carpetes no disponible')
                }
            }
        }
    }

    useEffect(() => {
        addFolderExpand("vista", vista)
    }, [vista]);

    return <>
        <Load value={entity && carpetes && expedients && isReady}>
            <DropZone onDrop={onDrop} disabled={!(entity?.potModificarContingut || entity?.potModificar)}>
                <DndContext onDragEnd={handleDragEnd}>
                    <StyledMuiGrid
                        resourceName={"documentResource"}
                        popupEditFormDialogResourceTitle={t('page.document.title')}
                        columns={additionalColumns}
                        rowActionsColumnIndex={draggable?-1:undefined}
                        paginationActive={false}
                        autoHeight
                        filter={commonFilter}
                        perspectives={perspectives}
                        staticSortModel={sortModel}
                        //rowReordering
                        popupEditCreateActive
                        popupEditFormContent={<DocumentsGridForm setDisabled={setDisabled} />}
                        formAdditionalData={{
                            expedient: { id: entity?.id },
                            metaExpedient: entity?.metaExpedient,
                        }}
                        apiRef={gridApiRef}
                        rowAdditionalActions={actions}
                        onRowCountChange={onRowCountChange}
                        onRefresh={refresh}
                        groupingColDef={{
                            headerName: t('page.contingut.grid.nom'),
                            flex: 1.5,
                            valueFormatter: (value: any, row: any) => {
                                if (row?.id) {
                                    if (vista == View.tipus && row?.multiplicitat) {
                                        return <MetaExpedient entity={row}/>;
                                    }
                                    return <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                        <ContingutIcon entity={row} />
                                    </Box>
                                }
                                return value;
                            },
                            renderCell: (params: any) => {
                                return treeView
                                    ? <>
                                        <GridTreeDataGroupingCell {...params} />
                                    </>
                                    : params.formattedValue
                            },
                        }}
                        slots={{
                            row: DraggableGridRow as GridSlots['row'],
                        }}
                        treeData
                        treeDataAdditionalRows={(_rows: any) => {
                            if (!_rows) return [];
                            const additionalRows: any[] = [];
                            switch (vista) {
                                case View.carpeta:
                                case View.icona:
                                    for (const contingut of [...(carpetes || []), ...(expedients || [])]) {
                                        if (contingut.id != null && entity?.id != contingut.id && !additionalRows.map((b) => b.id).includes(contingut.id)) {
                                            additionalRows.push(contingut)
                                        }
                                    }
                                    setTreeView(additionalRows?.length > 0)
                                    break;
                                case View.tipus:
                                    for (const row of _rows) {
                                        if (row?.metaDocumentInfo && !additionalRows.map((b) => b.id).includes(row?.metaDocumentInfo?.id)) {
                                            additionalRows.push({
                                                ...row?.metaDocumentInfo,
                                                tipus: "META_" + row?.metaDocumentInfo?.tipus,
                                                autogenerated: true,
                                            })
                                        }
                                    }
                                    setTreeView(true)
                                    break;
                                case View.estat:
                                    setTreeView(true)
                                    break;
                            }
                            return additionalRows;
                        }}
                        getTreeDataPath={(row: any): string[] => {
                            switch (vista) {
                                case View.estat: return [(`${row?.expedientEstatAdditional?.description || t('page.document.view.nullEstat')}`), `${row.id}`];
                                case View.tipus: return row?.autogenerated ?[`${row.id}`] :row?.metaNode ?[`${row?.metaNode?.id}`, `${row.id}`] :[t('page.document.detall.senseTipus'),`${row.id}`];
                                default: return row?.treePath?.filter((id:any)=>id!=entity?.id) ?? [`${row.id}`];
                            }
                        }}
                        rowExpansionChange={(params: any) => {
                            addFolderExpand(params.groupingKey, params.childrenExpanded)
                        }}
                        isGroupExpandedByDefault={(params) => {
							const carpeta = carpetes?.find?.(c => c.id === params.groupingKey);
							if (carpeta && carpeta.restringida) {
								const isResponsableRestriccio = carpeta?.responsableRestriccio?.id === user?.codi;
								const isUsuariAmbPermis = carpeta?.restriccions?.some(
									(restriccio: any) => restriccio?.id === user?.codi
								) ?? false;

								if (!isResponsableRestriccio && !isUsuariAmbPermis && !rol?.isAdmin) {
									return false;
								}
							}

                            const value = getFolderExpand(`${params?.groupingKey}`)
                            if (value !== undefined) {
                                return value
                            }
                            addFolderExpand(`${params?.groupingKey}`, expand)
                            return expand
                        }}
                        toolbarElementsWithPositions={[
                            {
                                position: 0,
                                element: <ExpandButton value={expand} onChange={(value) => {
                                    removeAll()
                                    addFolderExpand("vista", vista)
                                    setExpand(value)
                                }} hidden={!treeView} />,
                            },
                            {
                                position: 1,
                                element: <TreeViewSelector value={vista} onChange={(value: any) => {
                                    setVista(value);
                                    refresh();
                                }} />,
                            },
                            {
                                position: 3,
                                element: <MenuActionButton
                                    id={'createDocument'}
                                    hidden={!(entity?.potModificarContingut || entity?.potModificar)}
                                    buttonLabel={t('page.contingut.action.create.label')}
                                    buttonProps={{
                                        startIcon: <Icon>add</Icon>,
                                        variant: "outlined",
                                        sx: { borderRadius: '4px', minWidth: '20px', minHeight: '32px', py: 0 }
                                    }}
                                    actions={createActions}
                                />,
                            }
                        ]}
                        toolbarMassiveActions={massiveActions}
                        isRowSelectable={(data: any) => data?.row?.tipus == "DOCUMENT"}
                        onRowClick={(params: any) => {
                            if (params?.row.tipus === 'DOCUMENT' && isValid(params?.row)) {
                                handleVisualitzarOpen(params?.id)
                            }
                        }}

                        toolbarHideCreate
                        popupEditFormComponentProps={{ initOnChangeRequest: true }}
                        popupEditFormDialogButtons={[
                            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
                            {icon: 'save', text: t('common.save'), componentProps: { variant: 'contained', disabled: disabled }, value: true },
                        ]}
                        popupEditFormI18nKeys={{
                            createSuccess: 'page.document.action.new.ok',
                            updateSuccess: 'page.document.action.update.ok',
                            deleteSuccess: 'page.document.action.delete.ok',
                        }}
                    />
                    {components}
                    {massiveComponents}
                    {dialogVisualitzar}
                </DndContext>

                {(entity?.potModificarContingut || entity?.potModificar) && <Box
                    sx={{
                        minHeight: '50px',
                        display: 'flex',
                        flexDirection: 'column',
                        justifyContent: 'center',
                        alignItems: 'center',
                        opacity: 0.5,
                    }}
                >
                    <Icon sx={{fontSize: '5rem'}}>upload</Icon>
                    {t('page.document.action.new.dropMessg')}
                </Box>}
            </DropZone>
        </Load>
    </>
}

export default DocumentsGrid;