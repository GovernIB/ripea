import React, {RefObject, useCallback, useEffect, useMemo, useState} from "react";
import { FormControl, Grid, Select, MenuItem, Icon, Box } from "@mui/material";
import {GridApiPro, GridTreeDataGroupingCell} from "@mui/x-data-grid-pro";
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
import useVisualitzar from "./actions/Visualitzar.tsx";
import {useGridApiRef as useMuiDatagridApiRef} from "@mui/x-data-grid-pro";

enum View {
    estat = 'TREETABLE_PER_ESTAT',
    tipus = 'TREETABLE_PER_TIPUS_DOCUMENT',
    carpeta = 'TREETABLE_PER_CARPETA',
    icona = 'GRID',
}

// Només deixa les files del subarbre de la carpeta. Filtre de client damunt el llistat de l'expedient
const rowsEnSubarbre = (rows: any[], contingutScopeId: string | number) => {
    const scope = String(contingutScopeId);
    return rows.filter((row: any) => {
        if (String(row?.id) === scope) {
            return false;
        }
        const path = row?.treePath;
        if (Array.isArray(path) && path.length > 0) {
            return path.some((p: any) => String(p) === scope);
        }
        const pareId = row?.pare?.id ?? row?.pareId;
        return pareId != null && String(pareId) === scope;
    });
};

const ExpandButton = (props: { value: any, onChange: (value: any) => void, hidden: boolean }) => {
    const { value, onChange, hidden } = props;
    const { t } = useTranslation();

    if (hidden) {
        return <></>
    }

    return <ToolbarButton
        startIcon={<Icon sx={{m: 0}}>{value ? 'keyboard_arrow_up' : 'keyboard_arrow_down'}</Icon>}
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
const expedientPerspectives = ["PATH"];
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
        return apiExpedientFindAll({perspectives: expedientPerspectives, unpaged: true, filter: commonFilter, sorts: ['ordre']})
            .then((result)=> setExpedients(result.rows))
            .catch(()=> setExpedients([]))
    }

    const findCarpetes = () => {
        return apiCarpetaFindAll({perspectives: carpetaPerspectives, unpaged: true, filter: commonFilter, sorts: ['ordre']})
            .then((result)=> setCarpetes(result.rows))
            .catch(()=> setCarpetes([]))
    }
    useEffect(() => {
        if (apiExpedientIsReady) {
            findExpedients();
        }
    }, [apiExpedientIsReady, commonFilter]);
    useEffect(() => {
        if (apiCarpetaIsReady) {
            findCarpetes();
        }
    }, [apiCarpetaIsReady, commonFilter]);
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

const perspectives = ["PATH", "RESUM"]
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
    const { entity, onRowCountChange, contingutScopeId } = props;
    const { t } = useTranslation();
    const { value: user, rol } = useUserSession();
    const contingutCarpetaDetallAccesActiva = user?.sessionScope?.isContingutCarpetaDetallAccesActiva === true;

    const sortModel:any[] = useMemo(() => {
        if (user?.sessionScope?.ordenacioContingutPermesa) {
            return [{ field: 'ordre', sort: 'asc' }];
        }
        return [{ field: 'id', sort: 'desc' }];
    }, [user?.sessionScope?.ordenacioContingutPermesa]);

    const {
        isReady: apiContingutIsReady,
        artifactAction: apiAction,
    } = useResourceApiService('contingutResource');

    const commonFilter = useMemo(() => builder.and(
        builder.or(
            builder.eq('expedient.id', entity?.id),
            builder.eq('pare.id', entity?.id),
        ),
        builder.eq('esborrat', 0),
    ), [entity?.id]);

    const folderSessionKey = `folder_expand#${entity?.id}#${contingutScopeId ?? 'root'}`;
    const { get: getFolderExpand, save: addFolderExpand, removeAll } = useSessionList(folderSessionKey)

    const apiRef = useMuiDataGridApiRef();
    const dataApiRef: RefObject<GridApiPro | null> = useMuiDatagridApiRef();
    const [treeView, setTreeView] = useState<boolean>(true);
    const [expand, setExpand] = useState<boolean>(user?.conf?.expedientExpandit);
    const [vista, setVista] = useState<View>(getFolderExpand("vista") ?? user?.conf?.vistaActual);
    const [disabled, setDisabled] = useState<boolean>(false);
    const {
        isReady,
        carpetes,
        expedients,
        refresh: refreshTree
    } = useExpedientsCarpetes(commonFilter);

    const formCarpetaDestiPerScope = useMemo(() => {
        if (contingutScopeId == null || contingutScopeId === '') {
            return undefined;
        }
        const actual = carpetes?.find((c: any) => String(c?.id) === String(contingutScopeId));
        const nom = actual?.nom;
        return {
            id: contingutScopeId,
            description: nom != null && nom !== '' ? nom : String(contingutScopeId),
        };
    }, [contingutScopeId, carpetes]);

    const refresh = () => {
        if (vista == View.carpeta || vista == View.icona) {
            refreshTree()
                .then(() => apiRef?.current?.refresh?.())
        } else {
            apiRef?.current?.refresh?.();
        }
    }

    useEffect(() => {
        if (entity?.id == null) {
            return;
        }
        apiRef?.current?.refresh?.();
    }, [contingutScopeId, entity?.id]);

    const {handleOpen: handleVisualitzarOpen, dialog: dialogVisualitzar, isValid} = useVisualitzar();
    const { createActions, actions, components } = useContingutActions(entity, apiRef, refresh, contingutScopeId, formCarpetaDestiPerScope?.description);
    const { actions: massiveActions, components: massiveComponents } = useContingutMassiveActions(entity, refresh);

    const draggable = useMemo(()=> (
        vista == View.carpeta && (entity?.potModificarContingut || entity?.potModificar) && user?.sessionScope?.ordenacioContingutPermesa
    ),[vista, entity?.potModificarContingut, entity?.potModificar, user?.sessionScope?.ordenacioContingutPermesa])

    const onDrop = React.useCallback((adjunt: any) => {
        apiRef?.current?.triggerCreate?.(null, { adjunt })
    }, [apiRef])

    const toggleCarpetaExpansion = useCallback((rowId: any) => {
        const api = dataApiRef.current;
        if (!api || !treeView) {
            return;
        }
        const node = api.getRowNode(rowId);
        const expanded = (node as { childrenExpanded?: boolean })?.childrenExpanded ?? false;
        api.setRowChildrenExpansion(rowId, !expanded);
        addFolderExpand(`${rowId}`, !expanded);
    }, [dataApiRef, treeView, addFolderExpand]);

    const handleDragEnd = (params: any) => {
        if (params.newParent != params.oldParent || params.targetIndex != params.oldIndex) {
            console.log('>>> Canvi d\'ordre', params.targetIndex + 1, params)
            const parePerDefecte = params.newParent || contingutScopeId || entity.id;
            const patchData = {
                ordre: params.targetIndex + 1,
                pare: parePerDefecte,
            };
            if (apiContingutIsReady) {
                apiAction(params.row.id, {code: 'REORDER', data: patchData})
                    .catch(() => refresh())
            } else {
                console.error('Servei de l\'API pels documents no disponible'); refresh()
            }
        }
    }
    const processRowUpdate = (newRow: any, oldRow: any) => {
        const newParent = +newRow.treePath.at(-2) || contingutScopeId || entity?.id;
        const oldParent = +oldRow.treePath.at(-2) || contingutScopeId || entity?.id;
        if (newParent != oldParent && carpetes?.find((c: any) => c?.id == newParent) != null) {
            console.log('>>> Canvi de pare', oldParent, newParent)
            return new Promise((resolve, reject) => {
                const patchData = {
                    ordre: 1,
                    pare: newParent,
                };
                apiAction(newRow.id, {code: 'REORDER', data: patchData}).
                then(() => resolve(newRow)).
                catch(reject);
            });
        } else {
            return newRow;
        }
    }

    useEffect(() => {
        addFolderExpand("vista", vista)
    }, [vista]);

    useEffect(() => {
        if (contingutScopeId != null && contingutScopeId !== '' && contingutCarpetaDetallAccesActiva) {
            setVista(View.carpeta);
        }
    }, [contingutScopeId, contingutCarpetaDetallAccesActiva]);

    return <>
        <Load value={entity && carpetes && expedients && isReady}>
            <DropZone onDrop={onDrop} disabled={!(entity?.potModificarContingut || entity?.potModificar)} aria-label={t('page.document.action.new.dropMessg')}>
                {/*<DndContext onDragEnd={handleDragEnd} accessibility={{screenReaderInstructions: dndScreenReaderInstructions}}>*/}
                    <StyledMuiGrid
                        resourceName={"documentResource"}
                        popupEditFormDialogResourceTitle={t('page.document.title')}
                        columns={columns}
                        paginationActive={false}
                        autoHeight
                        filter={commonFilter}
                        perspectives={perspectives}
                        staticSortModel={sortModel}
                        popupEditCreateActive
                        popupEditFormContent={<DocumentsGridForm setDisabled={setDisabled} />}
                        formAdditionalData={{
                            expedient: { id: entity?.id },
                            metaExpedient: entity?.metaExpedient,
                            ...(formCarpetaDestiPerScope
                                ? {
                                    carpeta: formCarpetaDestiPerScope,
                                    contingutScopeDestiCarpeta: true,
                                }
                                : {}),
                        }}
                        apiRef={apiRef}
                        datagridApiRef={dataApiRef}
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
                                    ? <GridTreeDataGroupingCell {...params} />
                                    : params.formattedValue
                            },
                        }}
                        treeData
                        rowReordering={draggable}
                        processRowUpdate={processRowUpdate}
                        onRowOrderChange={handleDragEnd}
                        setTreeDataPath={(path, row) => ({...row, treePath: path})}
                        rowsTransformer={(_rows: any) => {
                            if (!_rows) return [];
                            const additionalRows: any[] = _rows;
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
                            /**
                             * Vista dins una carpeta ('contingutScopeId'):
                             * - ordenam per 'ordre' i filtram amb 'rowsEnSubarbre' (només files del subarbre)
                             */
                            let sorted = additionalRows.sort((a, b) => a?.ordre - b?.ordre);
                            if (contingutScopeId != null && contingutScopeId !== '') {
                                sorted = rowsEnSubarbre(sorted, contingutScopeId);
                            }
                            return sorted;
                        }}
                        getTreeDataPath={(row: any): string[] => {
                            switch (vista) {
                                case View.estat: return [(`${row?.expedientEstatAdditional?.description || t('page.document.view.nullEstat')}`), `${row.id}`];
                                case View.tipus: return row?.autogenerated ?[`${row.id}`] :row?.metaNode ?[`${row?.metaNode?.id}`, `${row.id}`] :[t('page.document.detall.senseTipus'),`${row.id}`];
                                default: {
									/**
									 * Vista dins una carpeta ('contingutScopeId'):
									 * - Camí d'arbre relatiu tallant 'treePath' després de la carpeta.
									 */
                                    if (contingutScopeId != null && contingutScopeId !== '' && Array.isArray(row?.treePath)) {
                                        const scope = String(contingutScopeId);
                                        const idx = row.treePath.findIndex((p: any) => String(p) === scope);
                                        if (idx >= 0) {
                                            const sub = row.treePath.slice(idx + 1).map((x: any) => String(x));
                                            return sub.length ? sub : [`${row.id}`];
                                        }
                                    }
                                    return row?.treePath?.filter((id:any)=>id!=entity?.id) ?? [`${row.id}`];
                                }
                            }
                        }}
                        rowExpansionChange={(params: any) => {
                            if (params.groupingKey) {
                                addFolderExpand(`${params.groupingKey}`, params.childrenExpanded)
                            }
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

                            const value = getFolderExpand(`${params.groupingKey}`)
                            if (value !== undefined) { return value }
                            addFolderExpand(`${params.groupingKey}`, expand)
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
                                element: contingutScopeId != null && contingutScopeId !== ''
                                    ? <></>
                                    : <TreeViewSelector value={vista} onChange={(value: any) => {
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
                            if (params?.row.tipus === 'CARPETA' && params?.row?.id != null) {
                                toggleCarpetaExpansion(params.row.id);
                                return;
                            }
                            if (params?.row.tipus === 'DOCUMENT' && isValid(params?.row)) {
                                handleVisualitzarOpen(params?.id)
                            }
                        }}

                        toolbarHideCreate
                        popupEditFormComponentProps={{
                            initOnChangeRequest: true,
                            onBeforeCreateSuccess: (formData: any) => {
                                const clean: any = { ...formData };
                                delete clean.contingutScopeDestiCarpeta;
                                if (!contingutCarpetaDetallAccesActiva || contingutScopeId == null || contingutScopeId === '') {
                                    return clean;
                                }
                                if (clean?.carpeta != null && clean?.carpeta?.id != null) {
                                    return clean;
                                }
                                const scopeId = contingutScopeId;
                                const actual = carpetes?.find((c: any) => String(c?.id) === String(scopeId));
                                const nom = actual?.nom;
                                return {
                                    ...clean,
                                    carpeta: {
                                        id: scopeId,
                                        description: nom != null && nom !== '' ? nom : String(scopeId),
                                    },
                                };
                            },
                        }}
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
                {/*</DndContext>*/}

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