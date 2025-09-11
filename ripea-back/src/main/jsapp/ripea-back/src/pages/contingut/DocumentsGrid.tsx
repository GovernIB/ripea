import React, {useEffect, useMemo, useState} from "react";
import { DndContext, useDraggable, useDroppable } from '@dnd-kit/core';
import { FormControl, Grid, InputLabel, Select, MenuItem, Icon, IconButton, Box } from "@mui/material";
import {
    GridRow,
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

const View = {
    estat: 'TREETABLE_PER_ESTAT',
    tipus: 'TREETABLE_PER_TIPUS_DOCUMENT',
    carpeta: 'TREETABLE_PER_CARPETA',
    icona: 'GRID',
}

type DraggableContextType = {
    draggableAttributes: any;
    draggableListeners: any;
    draggableSetActivatorNodeRef: (element: HTMLElement | null) => void;
}
const DraggableContext = React.createContext<DraggableContextType | undefined>(undefined);
const useDraggableContext = () => {
    const context = React.useContext(DraggableContext);
    if (context === undefined) {
        throw new Error('useDraggableContext must be used within an DraggableContext.Provider');
    }
    return context;
}
const DraggableGridRow: React.FC<any> = (props) => {
    const {
        attributes: draggableAttributes,
        listeners: draggableListeners,
        transform: draggableTransform,
        setNodeRef: draggableSetNodeRef,
        setActivatorNodeRef: draggableSetActivatorNodeRef
    } = useDraggable({
        id: 'draggable_' + props.row.id,
        data: props.row,
    });
    const droppableProps = useDroppable({
        id: 'droppable_' + props.row.id,
        data: props.row,
    });
    const { isOver, setNodeRef: droppableSetNodeRef } = droppableProps;
    const draggableStyle = draggableTransform ? {
        transform: `translate3d(${draggableTransform.x}px, ${draggableTransform.y}px, 0)`,
    } : undefined;
    const droppableStyle = {
        border: isOver && props.row.tipus === 'CARPETA' ? '2px solid grey' : undefined,
        borderTop: isOver && props.row.tipus !== 'CARPETA' ? '2px solid grey' : undefined,
    };
    return <div ref={droppableSetNodeRef} style={droppableStyle}>
        <DraggableContext.Provider
            value={{
                draggableAttributes,
                draggableListeners,
                draggableSetActivatorNodeRef
            }}>
            <GridRow
                ref={draggableSetNodeRef}
                style={draggableStyle}
                {...props}>
            </GridRow>
        </DraggableContext.Provider>
    </div>;
}
const DraggableGridRowHandler: React.FC = () => {
    const { draggableAttributes, draggableListeners, draggableSetActivatorNodeRef } = useDraggableContext();
    return <IconButton
        size="small"
        ref={draggableSetActivatorNodeRef}
        {...draggableAttributes}
        {...draggableListeners}
        sx={{ cursor: 'grab', mr: 1 }}>
            <Icon sx={{ mr: 0 }}>swap_vert</Icon>
    </IconButton>;
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

    return <Grid item xs={3} sx={{ ml: 1 }}>
        <FormControl fullWidth size="small">
            <InputLabel id="demo-simple-select-label">{t('page.document.view.title')}</InputLabel>
            <Select
                sx={{ maxHeight: '32px' }}
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

const perspectives = ["PATH"]

export const useExpedientsCarpetes = (commonFilter: string) => {
    const {
        isReady: apiExpedientIsReady,
        find: apiExpedientFindAll,
    } = useResourceApiService('expedientResource');
    const {
        isReady: apiCarpetaIsReady,
        find: apiCarpetaFindAll,
    } = useResourceApiService('carpetaResource');
    const [expedients, setExpedients] = useState<any[]>([]);
    const [carpetes, setCarpetes] = useState<any[]>([]);
    const findExpedients = () => {
        apiExpedientFindAll({perspectives, unpaged: true, filter: commonFilter})
            .then((result)=> setExpedients(result.rows))
            .catch(()=> setExpedients([]))
    }
    const findCarpetes = () => {
        apiCarpetaFindAll({perspectives, unpaged: true, filter: commonFilter})
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
    const refresh = () => {
        findExpedients();
        findCarpetes();
    }
    return {
        isReady: apiExpedientIsReady && apiCarpetaIsReady,
        expedients,
        carpetes,
        refresh,

    };
}

const DocumentsGrid = (props: any) => {
    const { entity, onRowCountChange } = props;
    const { t } = useTranslation();
    const { value: user } = useUserSession();

    //const sortModel: any = [{ field: 'id', sort: 'desc' }]

    const sortModel = useMemo(() => {
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
    const {
        isReady,
        carpetes,
        expedients,
        refresh: refreshTree
    } = useExpedientsCarpetes(commonFilter);
    const refresh = () => {
        if (vista == View.carpeta || vista == View.icona) {
            refreshTree()
        }
        gridApiRef?.current?.refresh?.();
    }

    const { createActions, actions, hiddenDelete, components } = useContingutActions(entity, gridApiRef, refresh);
    const { actions: massiveActions, components: massiveComponents } = useContingutMassiveActions(entity, refresh);

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
        ...(vista == View.carpeta ? [{
            renderCell: (params: any) => <DraggableGridRowHandler />,
            flex: 0.1
        }] : [])
    ];

    const onDrop = React.useCallback((adjunt: any) => {
        gridApiRef?.current?.showCreateDialog?.(null, { adjunt })
    }, [])

    const handleDragEnd = (event: any) => {
        const sourceData = event.active.data.current;
        const targetData = event.over.data.current;
        //console.log('>>> ', sourceData.nom, '(', sourceData.ordre, ')->', targetData.nom, '(', targetData.ordre, ')')
        const patchData = {
            ...(targetData.tipus === 'DOCUMENT' && { ordre: targetData.ordre }),
            pare: { id: targetData.pare.id },
            ordrePatch: true
        };
        if (sourceData.tipus === 'DOCUMENT') {
            if (apiDocumentIsReady) {
                apiDocumentPatch(sourceData.id, { data: patchData }).
                then(() => gridApiRef.current.refresh());
            } else {
                console.error('Servei de l\'API pels documents no disponible')
            }
        } else if (sourceData.tipus === 'CARPETA') {
            if (apiCarpetaIsReady) {
                apiCarpetaPatch(sourceData.id, { data: patchData }).
                then(() => gridApiRef.current.refresh());
            } else {
                console.error('Servei de l\'API per les carpetes no disponible')
            }
        }
    }

    useEffect(() => {
        addFolderExpand("vista", vista)
    }, [vista]);

    return <>
        <Load value={entity && isReady}>
            <DropZone onDrop={onDrop} disabled={!entity?.potModificar}>
                <DndContext onDragEnd={handleDragEnd}>
                    <StyledMuiGrid
                        resourceName={"documentResource"}
                        popupEditFormDialogResourceTitle={t('page.document.title')}
                        columns={columns}
                        rowActionsColumnIndex={4}
                        paginationActive={false}
                        filter={commonFilter}
                        perspectives={perspectives}
                        staticSortModel={sortModel}
                        //rowReordering
                        popupEditCreateActive
                        popupEditFormContent={<DocumentsGridForm />}
                        formAdditionalData={{
                            expedient: { id: entity?.id },
                            metaExpedient: entity?.metaExpedient,
                        }}
                        apiRef={gridApiRef}
                        rowAdditionalActions={actions}
                        onRowCountChange={onRowCountChange}
                        onRefresh={refresh}
                        popupEditFormDialogButtons={[
                            {icon: 'save', text: t('common.create'), componentProps: { variant: 'contained' }, value: true },
                            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false }, 
                        ]}
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
                            const additionalRows: any[] = [];
                            switch (vista) {
                                case View.carpeta:
                                case View.icona:
                                    for (const contingut of [...carpetes, ...expedients]) {
                                        if (entity?.id!= contingut.id && !additionalRows.map((b) => b.id).includes(contingut.id)) {
                                            additionalRows.push(contingut)
                                        }
                                    }
                                    setTreeView(additionalRows?.length > 0)
                                    break;
                                case View.tipus:
                                    if (_rows!=null){
                                        for (const row of _rows) {
                                            if (!additionalRows.map((b) => b.id).includes(row?.metaNode?.id)) {
                                                additionalRows.push(row?.metaDocumentInfo)
                                            }
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
                                case View.estat: return [`${row?.expedientEstatAdditional?.description}`, `${row.id}`];
                                case View.tipus: return row?.metaNode ?[`${row?.metaNode?.id}`, `${row.id}`] :[`${row.id}`];
                                default: return row?.treePath?.filter((id:any)=>id!=entity?.id) ?? [`${row.id}`];
                            }
                        }}
                        rowExpansionChange={(params: any) => {
                            addFolderExpand(params.id, params.childrenExpanded)
                        }}
                        isGroupExpandedByDefault={(params) => {
                            const value = getFolderExpand(`${params?.id}`)
                            if (value !== undefined) {
                                return value
                            }
                            addFolderExpand(`${params?.id}`, expand)
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
                                    hidden={!entity?.potModificar}
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
                        toolbarHideCreate
                        popupEditFormComponentProps={{ initOnChangeRequest: true }}
                        popupEditFormI18nKeys={{
                            createSuccess: 'page.document.action.new.ok',
                            updateSuccess: 'page.document.action.update.ok',
                            deleteSuccess: 'page.document.action.delete.ok',
                        }}
                    />
                    {components}
                    {massiveComponents}
                </DndContext>
            </DropZone>
        </Load>
    </>
}

export default DocumentsGrid;