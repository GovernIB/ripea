import {useState} from "react";
import {FormControl, Grid, InputLabel, Select, MenuItem, Icon, Alert} from "@mui/material";
import {GridTreeDataGroupingCell} from "@mui/x-data-grid-pro";
import {GridPage, useFormContext, useMuiDataGridApiRef, useResourceApiService} from 'reactlib';
import {useTranslation} from "react-i18next";
import ContingutIcon from "./details/ContingutIcon.tsx";
import {useContingutActions} from "./details/ContingutActions.tsx";
import useContingutMassiveActions from "./details/ContingutMassiveActions.tsx";
import GridFormField, {GridButton} from "../../components/GridFormField.tsx";
import StyledMuiGrid, {ToolbarButton} from "../../components/StyledMuiGrid.tsx";
import Load from "../../components/Load.tsx";
import {potModificar} from "../expedient/details/Expedient.tsx";
import {MenuActionButton} from "../../components/MenuButton.tsx";
import * as builder from '../../util/springFilterUtils.ts';
import TabComponent from "../../components/TabComponent.tsx";
import Iframe from "../../components/Iframe.tsx";
import {useScanFinalitzatSession} from "../../components/SseExpedient.tsx";
import {useSessionList} from "../../components/SessionStorageContext.tsx";

const ScanerTabForm = () => {
    const { data, apiRef } = useFormContext();
    const { t } = useTranslation();
    const { onChange } = useScanFinalitzatSession();

    onChange((value) => {
        console.log("scan", value)
        apiRef?.current?.setFieldValue("scaned", true)
        apiRef?.current?.setFieldValue("adjunt", {
            name: value?.nomDocument,
            content: value?.contingut,
            contentType: value?.mimeType,
            // contentLength: ,
        })
    });

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <Grid item xs={12} hidden={!data?.scaned}>
            <Alert severity={"success"}>El proceso de escaneo se ha realizado con éxito.</Alert>
        </Grid>

        <GridFormField xs={12} name="ntiIdDocumentoOrigen"
                       componentProps={{ title: t('page.document.detall.documentOrigenFormat') }}
                       required hidden={data?.scaned}/>
        <GridFormField xs={12} name="digitalitzacioPerfil" required hidden={data?.scaned}/>

        <Grid item xs={12} hidden={data?.scaned}>
            <Iframe src={data?.digitalitzacioProcesUrl}/>
        </Grid>
    </Grid>
}
const FileTabForm = () => {
    const { data } = useFormContext();

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="adjunt" type={"file"} required/>
        <GridFormField xs={6} name="hasFirma" hidden={!data.adjunt} disabled={data.documentFirmaTipus=="FIRMA_ADJUNTA"}/>
        <GridFormField xs={6} name="documentFirmaTipus" hidden={!data.adjunt} disabled/>
        <GridFormField xs={12} name="firmaAdjunt" type={"file"} hidden={data.documentFirmaTipus!="FIRMA_SEPARADA"} required/>
    </Grid>
}

const DocumentsGridForm = () => {
    const { t } = useTranslation();
    const { data, apiRef } = useFormContext();
    const {artifactAction: apiAction} = useResourceApiService('documentResource');

    const actualizarDatos = () => {
        if (data?.adjunt && data.pluginSummarizeActiu) {
            apiAction(undefined, {code :"RESUM_IA", data:{ adjunt: data?.adjunt }})
                .then((result)=>{
                    if (result) {
                        apiRef?.current?.setFieldValue("nom", result.titol)
                        apiRef?.current?.setFieldValue("descripcio", result.resum)
                    }
                });
        }
    };

    const metaDocumentFilter :string = builder.and(
        builder.eq("metaExpedient.id", data?.metaExpedient?.id),
        builder.eq("actiu", true),
    );

    const tabs = [
        {
            value: "file",
            label: t('page.document.tabs.file'),
            content: <FileTabForm/>,
        },
        {
            value: "scaner",
            label: t('page.document.tabs.scaner'),
            content: data?.funcionariHabilitatDigitalib
                ?<ScanerTabForm/>
                :<Alert severity={"warning"} sx={{width: '100%'}}>{t('page.document.alert.funcionariHabilitatDigitalib')}</Alert>,
        }
    ];

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="metaDocument"
                       namedQueries={
                           apiRef?.current?.getId()
                               ?[`UPDATE_DOC#${apiRef?.current?.getId()}`]
                               :[`CREATE_NEW_DOC#${data?.expedient?.id}`]
                       }
                       filter={metaDocumentFilter}/>
        <GridFormField xs={data.pluginSummarizeActiu ?11 :12} name="nom"/>
        <GridButton xs={1} title={t('page.document.detall.summarize')}
                    onClick={actualizarDatos}
                    disabled={!data?.adjunt}
                    hidden={!data.pluginSummarizeActiu}>
            <Icon>assistant</Icon>IA
        </GridButton>
        <GridFormField xs={12} name="descripcio" type={"textarea"}/>
        <GridFormField xs={12} name="dataCaptura" type={"date"} disabled required/>
        <GridFormField xs={12} name="ntiOrigen" required/>
        <GridFormField xs={12} name="ntiEstadoElaboracion" required/>

        <Grid item xs={12}>
            <TabComponent
                tabs={tabs}
                variant="scrollable"
            />
        </Grid>
    </Grid>
}

const ExpandButton = (props:{value:any, onChange:(value:any) => void, hidden: boolean}) => {
    const {value, onChange, hidden} = props;
    const { t } = useTranslation();

    if (hidden){
        return <></>
    }

    return <ToolbarButton
        startIcon={<Icon>{value ?'arrow_right' :'arrow_drop_down'}</Icon>}
        onClick={()=>onChange(!value)}
        color={'none'}
    >
        {value ? t("common.contract") : t("common.expand")}
    </ToolbarButton>
}

const TreeViewSelector = (props:{value: any, onChange: (value: any) => void }) => {
    const {value, onChange} = props;
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

const sortModel:any = [{field: 'id', sort: 'desc'}]
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

const DocumentsGrid = (props:any) => {
    const {entity, onRowCountChange} = props;
    const { t } = useTranslation();

    const { get: getFolderExpand, save: addFolderExpand } = useSessionList('folder_expand')

    const dataGridApiRef = useMuiDataGridApiRef()
    const [treeView, setTreeView] = useState<boolean>(true);
    const [expand, setExpand] = useState<boolean>(false);
    const [vista, setVista] = useState<string>("carpeta");

    const refresh = () => {
        dataGridApiRef?.current?.refresh?.();
    }
    const {createActions, actions, hiddenDelete, components} = useContingutActions(entity, dataGridApiRef, refresh);
    const {actions: massiveActions, components: massiveComponents} = useContingutMassiveActions(entity, refresh);

    return <GridPage>
        <Load value={entity}>
            <StyledMuiGrid
                resourceName="documentResource"
                popupEditFormDialogResourceTitle={t('page.document.title')}
                columns={columns}
                paginationActive
                filter={builder.and(
                    builder.eq('expedient.id', entity?.id),
                    builder.eq('esborrat', 0),
                )}
                perspectives={perspectives}
                staticSortModel={sortModel}
                popupEditCreateActive
                popupEditFormContent={<DocumentsGridForm/>}
                formInitOnChange
                formAdditionalData={{
                    expedient: {id: entity?.id},
                    metaExpedient: entity?.metaExpedient,
                }}
                apiRef={dataGridApiRef}
                rowAdditionalActions={actions}
                onRowCountChange={onRowCountChange}

                groupingColDef={{
                    headerName: t('page.contingut.grid.nom'),
                    flex: 1.5,
                    valueFormatter: (value:any, row:any) => {
                        return row?.id ?<ContingutIcon entity={row}/> :value;
                    },
                    renderCell: (params: any) => {
                        return treeView
                            ?<GridTreeDataGroupingCell {...params}/>
                            :params.formattedValue
                    },
                }}
                treeData
                treeDataAdditionalRows={(_rows:any) => {
                    const additionalRows :any[] = [];

                    if(_rows!=null && vista == "carpeta") {
                        for (const row of _rows) {
                            const aditionalRow = row.parentPath
                                ?.filter((a: any) => a.id != row.id
                                    && !additionalRows.map((b)=>b.id).includes(a.id)
                                    && !additionalRows.map((b)=>b.nom).includes(a.nom))
                            aditionalRow && additionalRows.push(...aditionalRow);
                        }
                        setTreeView(additionalRows?.length > 0)
                    }else {
                        setTreeView(true)
                    }
                    // console.log('>>> additionalRows', additionalRows)
                    return additionalRows;
                }}
                getTreeDataPath={(row:any) :string[] => {
                    switch (vista) {
                        case "estat": return [`${row.estat}`, `${row.nom}`];
                        case "tipus": return [`${row.metaNode?.description}`, `${row.nom}`];
                        default: return row.treePath;
                    }
                }}

                rowExpansionChange={(params:any)=> addFolderExpand(`${params.id}`, params.childrenExpanded) }
                isGroupExpandedByDefault={(row) => getFolderExpand(`${row?.id}`) || expand }
                toolbarElementsWithPositions={[
                    {
                        position: 0,
                        element: <ExpandButton value={expand} onChange={setExpand} hidden={!treeView}/>,
                    },
                    {
                        position: 1,
                        element: <TreeViewSelector value={vista} onChange={(value:any) => {
                            setVista(value);
                            refresh();
                        }} />,
                    },
                    {
                        position: 3,
                        element: <MenuActionButton
                            id={'createDocument'}
                            hidden={!potModificar(entity)}
                            buttonLabel={t('page.contingut.acciones.create')}
                            buttonProps={{
                                startIcon: <Icon>add</Icon>,
                                variant: "outlined",
                                sx: {borderRadius: '4px',  minWidth: '20px', minHeight: '32px', py: 0}
                            }}
                            actions={createActions}
                        />,
                    }
                ]}

                toolbarMassiveActions={massiveActions}
                isRowSelectable={(data:any)=> data?.row?.tipus=="DOCUMENT"}
                toolbarHideCreate
                rowHideDeleteButton={hiddenDelete}

                popupEditFormComponentProps={{ initOnChangeRequest: true }}
            />
            {components}
            {massiveComponents}
        </Load>
    </GridPage>
}

export default DocumentsGrid;