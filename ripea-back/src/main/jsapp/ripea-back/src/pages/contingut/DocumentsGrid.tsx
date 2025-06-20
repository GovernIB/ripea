import React, {useEffect, useRef, useState} from "react";
import { FormControl, Grid, InputLabel, Select, MenuItem, Icon, Alert } from "@mui/material";
import { GridTreeDataGroupingCell } from "@mui/x-data-grid-pro";
import { GridPage, useFormContext, useMuiDataGridApiRef, useResourceApiService } from 'reactlib';
import { useTranslation } from "react-i18next";
import ContingutIcon from "./details/ContingutIcon.tsx";
import { useContingutActions } from "./details/ContingutActions.tsx";
import useContingutMassiveActions from "./details/ContingutMassiveActions.tsx";
import GridFormField, { GridButton } from "../../components/GridFormField.tsx";
import StyledMuiGrid, { ToolbarButton } from "../../components/StyledMuiGrid.tsx";
import Load from "../../components/Load.tsx";
import { potModificar } from "../expedient/details/Expedient.tsx";
import { MenuActionButton } from "../../components/MenuButton.tsx";
import * as builder from '../../util/springFilterUtils.ts';
import TabComponent from "../../components/TabComponent.tsx";
import Iframe from "../../components/Iframe.tsx";
import { useScanFinalitzatSession } from "../../components/SseExpedient.tsx";
import { useUserSession } from "../../components/Session.tsx";
import { useSessionList } from "../../components/SessionStorageContext.tsx";
import DropZone from "../../components/DropZone.tsx";

const ScanerTabForm = () => {
    const { data, apiRef } = useFormContext();
    const { t } = useTranslation();
    const { onChange } = useScanFinalitzatSession();
    const { value: user } = useUserSession()

    onChange((value) => {
        if (user?.codi == value?.usuari) {
            apiRef?.current?.setFieldValue("scaned", true)
            apiRef?.current?.setFieldValue("adjunt", {
                name: value?.nomDocument,
                content: value?.contingut,
                contentType: value?.mimeType
            });
        }
    });

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <Grid item xs={12} hidden={!data?.scaned}>
            <Alert severity={"success"}>{t('page.document.alert.scaned')}</Alert>
        </Grid>

        <GridFormField xs={12} name="ntiIdDocumentoOrigen"
            componentProps={{ title: t('page.document.detall.documentOrigenFormat') }}
            required />
        <GridFormField xs={12} name="digitalitzacioPerfil" required />

        <Grid item xs={12}>
            <Iframe src={data?.digitalitzacioProcesUrl} />
        </Grid>
    </Grid>
}
const FileTabForm = () => {
    const { data } = useFormContext();

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="adjunt" type={"file"} required />
        <GridFormField xs={6} name="hasFirma" hidden={!data.adjunt} disabled={data.documentFirmaTipus == "FIRMA_ADJUNTA"} />
        <GridFormField xs={6} name="documentFirmaTipus" hidden={!data.adjunt} disabled />
        <GridFormField xs={12} name="firmaAdjunt" type={"file"} hidden={data.documentFirmaTipus != "FIRMA_SEPARADA"} required />
    </Grid>
}

const DocumentsGridForm = () => {
    const { t } = useTranslation();
    const { data, apiRef } = useFormContext();
    const { artifactAction: apiAction } = useResourceApiService('documentResource');

    const actualizarDatos = () => {
        if (data?.adjunt && data.pluginSummarizeActiu) {
            apiAction(undefined, { code: "RESUM_IA", data: { adjunt: data?.adjunt } })
                .then((result) => {
                    if (result) {
                        apiRef?.current?.setFieldValue("nom", result.titol)
                        apiRef?.current?.setFieldValue("descripcio", result.resum)
                    }
                });
        }
    };

    const metaDocumentFilter: string = builder.and(
        builder.eq("metaExpedient.id", data?.metaExpedient?.id),
        builder.eq("actiu", true),
    );

    const tabs = [
        {
            value: "file",
            label: t('page.document.tabs.file'),
            content: <FileTabForm />,
        },
        {
            value: "scaner",
            label: t('page.document.tabs.scaner'),
            content: !data?.funcionariHabilitatDigitalib
                ? <ScanerTabForm />
                : <Alert severity={"warning"} sx={{ width: '100%' }}>{t('page.document.alert.funcionariHabilitatDigitalib')}</Alert>,
        }
    ];

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="metaDocument"
            namedQueries={
                apiRef?.current?.getId()
                    ? [`UPDATE_DOC#${apiRef?.current?.getId()}`]
                    : [`CREATE_NEW_DOC#${data?.expedient?.id}`]
            }
            filter={metaDocumentFilter} />
        <GridFormField xs={data.pluginSummarizeActiu ? 11 : 12} name="nom" />
        <GridButton xs={1} title={t('page.document.detall.summarize')}
            onClick={actualizarDatos}
            disabled={!data?.adjunt}
            hidden={!data.pluginSummarizeActiu}>
            <Icon>assistant</Icon>IA
        </GridButton>
        <GridFormField xs={12} name="descripcio" type={"textarea"} />
        <GridFormField xs={12} name="dataCaptura" type={"date"} disabled required />
        <GridFormField xs={12} name="ntiOrigen" required />
        <GridFormField xs={12} name="ntiEstadoElaboracion" required />

        <Grid item xs={12}>
            <TabComponent
                tabs={tabs}
                variant="scrollable"
            />
        </Grid>
    </Grid>
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
                <MenuItem value={"estat"}>{t('page.document.view.estat')}</MenuItem>
                <MenuItem value={"tipus"}>{t('page.document.view.tipus')}</MenuItem>
                <MenuItem value={"carpeta"} selected>{t('page.document.view.carpeta')}</MenuItem>
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

const DocumentsGrid = (props: any) => {
    const { entity, onRowCountChange } = props;
    const { t } = useTranslation();

    const commonFilter = builder.and(
        builder.or(
            builder.eq('expedient.id', entity?.id),
            builder.eq('pare.id', entity?.id),
        ),
        builder.eq('esborrat', 0),
    )

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

    const { get: getFolderExpand, save: addFolderExpand, removeAll } = useSessionList(`folder_expand#${entity?.id}`)

    const gridApiRef = useMuiDataGridApiRef();
    const [treeView, setTreeView] = useState<boolean>(true);
    const [expand, setExpand] = useState<boolean>(false);
    const isFirstRender = useRef(true);
    const [vista, setVista] = useState<string>("carpeta");

    const refresh = () => {
        findExpedients()
        findCarpetas()
        gridApiRef?.current?.refresh?.();
    }
    const { createActions, actions, hiddenDelete, components } = useContingutActions(entity, gridApiRef, refresh);
    const { actions: massiveActions, components: massiveComponents } = useContingutMassiveActions(entity, refresh);

    const onDrop = React.useCallback((adjunt: any) => {
        gridApiRef?.current?.showCreateDialog?.(null, { adjunt })
    }, [])

    useEffect(() => {
        if (isFirstRender.current) {
            isFirstRender.current = false;
            return;
        }
        removeAll()
    }, [expand]);

    return <GridPage>
        <Load value={entity && apiExpedientIsReady && apiCarpetaIsReady}>
            <DropZone onDrop={onDrop} disabled={!potModificar(entity)}>
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
                    formInitOnChange
                    formAdditionalData={{
                        expedient: { id: entity?.id },
                        metaExpedient: entity?.metaExpedient,
                    }}
                    apiRef={gridApiRef}
                    rowAdditionalActions={actions}
                    onRowCountChange={onRowCountChange}

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

                        if (vista == "carpeta") {
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
                            case "estat": return [`${row.estat}`, `${row.nom}`];
                            case "tipus": return [`${row.metaNode?.description}`, `${row.nom}`];
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
                            element: <ExpandButton value={expand} onChange={setExpand} hidden={!treeView} />,
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
                                hidden={!potModificar(entity)}
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