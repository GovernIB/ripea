import React, {useEffect, useState} from "react";
import { FormControl, Grid, InputLabel, Select, MenuItem, Icon } from "@mui/material";
import {GridTreeDataGroupingCell} from "@mui/x-data-grid-pro";
import { GridPage, useMuiDataGridApiRef, useResourceApiService } from 'reactlib';
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
        startIcon={<Icon>{value ? 'arrow_right' : 'arrow_drop_down'}</Icon>}
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
                <MenuItem value={View.carpeta} selected>{t('page.document.view.carpeta')}</MenuItem>
            </Select>
        </FormControl>
    </Grid>
}

const sortModel: any = [{ field: 'id', sort: 'desc' }]
const perspectives = ["PATH"]
const columns = [
    // {
    //     field: 'nom',
    //     flex: 0.5,
    //     renderCell: (params: any) => <ContingutIcon entity={params?.row}/>
    // },
    {
        field: 'descripcio',
        flex: 0.5,
    },
    {
        field: 'metaDocument',
        flex: 0.5,
    },
    {
        field: 'createdDate',
        flex: 0.75,
    },
    {
        field: 'createdBy',
        flex: 0.5,
    },
];

export const useTreeView = (commonFilter:string) => {
    const {
        isReady: apiExpedientIsReady,
        find: apiExpedientFindAll,
    } = useResourceApiService('expedientResource');
    const [expedients, setExpedients] = useState<any[]>([]);

    const findExpedients = () => {
        apiExpedientFindAll({perspectives, unpaged: true, filter: commonFilter})
            .then((result)=> setExpedients(result.rows))
            .catch(()=> setExpedients([]))
    }
    useEffect(() => {
        if (apiExpedientIsReady) {findExpedients()}
    }, [apiExpedientIsReady]);

    const {
        isReady: apiCarpetaIsReady,
        find: apiCarpetaFindAll,
    } = useResourceApiService('carpetaResource');
    const [carpetes, setCarpetes] = useState<any[]>([]);

    const findCarpetas = () => {
        apiCarpetaFindAll({perspectives, unpaged: true, filter: commonFilter})
            .then((result)=> setCarpetes(result.rows))
            .catch(()=> setCarpetes([]))
    }

    useEffect(() => {
        if (apiCarpetaIsReady) {findCarpetas()}
    }, [apiCarpetaIsReady]);

    const refresh = () => {
        findExpedients()
        findCarpetas()
    }

    return {
        expedients,
        carpetes,
        refresh,
        isReady: apiExpedientIsReady && apiCarpetaIsReady,
    }
}

const DocumentsGrid = (props: any) => {
    const { entity, onRowCountChange } = props;
    const { t } = useTranslation();
    const {value: user} = useUserSession();

    const commonFilter = builder.and(
        builder.or(
            builder.eq('expedient.id', entity?.id),
            builder.eq('pare.id', entity?.id),
        ),
        builder.eq('esborrat', 0),
    )

    const { get: getFolderExpand, save: addFolderExpand, removeAll } = useSessionList(`folder_expand#${entity?.id}`)

    const gridApiRef = useMuiDataGridApiRef();
    const [treeView, setTreeView] = useState<boolean>(true);
    const [expand, setExpand] = useState<boolean>(user?.conf?.expedientExpandit);
    const [vista, setVista] = useState<string>(user?.conf?.vistaActual);

    const {carpetes, expedients, refresh: refreshTree, isReady} = useTreeView(commonFilter)
    const refresh = () => {
        refreshTree()
        gridApiRef?.current?.refresh?.();
    }

    const { createActions, actions, hiddenDelete, components } = useContingutActions(entity, gridApiRef, refresh);
    const { actions: massiveActions, components: massiveComponents } = useContingutMassiveActions(entity, refresh);

    const onDrop = React.useCallback((adjunt: any) => {
        gridApiRef?.current?.showCreateDialog?.(null, { adjunt })
    }, [])

    return <GridPage>
        <Load value={entity && isReady}>
            <DropZone onDrop={onDrop} disabled={!entity?.potModificar}>
                <StyledMuiGrid
                    resourceName="documentResource"
                    popupEditFormDialogResourceTitle={t('page.document.title')}
                    columns={columns}
                    // paginationActive
                    filter={commonFilter}
                    perspectives={perspectives}
                    staticSortModel={sortModel}
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

                    groupingColDef={{
                        headerName: t('page.contingut.grid.nom'),
                        flex: 1.5,
                        valueFormatter: (value: any, row: any) => {
                            return row?.id ? <ContingutIcon entity={row} /> : value;
                        },
                        renderCell: (params: any) => {
                            return treeView
                                ? <GridTreeDataGroupingCell {...params} />
                                : params.formattedValue
                        },
                    }}
                    treeData
                    treeDataAdditionalRows={(_rows: any) => {
                        const additionalRows: any[] = [];

                        if (vista == View.carpeta || vista == View.icona) {
                            for (const contingut of [...carpetes, ...expedients]) {
                                if (entity?.id!= contingut.id && !additionalRows.map((b) => b.id).includes(contingut.id)) {
                                    additionalRows.push(contingut)
                                }
                            }

                            setTreeView(additionalRows?.length > 0)
                        } else {
                            setTreeView(true)
                        }
                        // console.log('>>> additionalRows', additionalRows)
                        return additionalRows;
                    }}
                    getTreeDataPath={(row: any): string[] => {
                        switch (vista) {
                            case View.estat: return [`${row.estat}`, `${row.id}`];
                            case View.tipus: return [`${row.metaNode?.description}`, `${row.id}`];
                            default: return row.treePath.filter((id:any)=>id!=entity?.id);
                        }
                    }}

                    rowExpansionChange={(params: any) => {
                        if (typeof params?.id === "number") {
                            addFolderExpand(params.id, params.childrenExpanded)
                        }
                    }}
                    isGroupExpandedByDefault={(params) => {
                        if (typeof params?.id === "number") {
                            const value = getFolderExpand(`${params?.id}`)
                            if (value !== undefined) {
                                return value
                            }
                            addFolderExpand(`${params?.id}`, expand)
                        }
                        return expand
                    }}
                    toolbarElementsWithPositions={[
                        {
                            position: 0,
                            element: <ExpandButton value={expand} onChange={(value:any)=>{
                                setExpand(value)
                                removeAll()
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
                    rowHideDeleteButton={hiddenDelete}

                    popupEditFormComponentProps={{ initOnChangeRequest: true }}
                />
                {components}
                {massiveComponents}
            </DropZone>
        </Load>
    </GridPage>
}

export default DocumentsGrid;