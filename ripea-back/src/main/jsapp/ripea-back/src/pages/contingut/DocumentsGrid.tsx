import { useState, useEffect } from "react";
import {FormControl, Grid, InputLabel, Select, MenuItem, Icon} from "@mui/material";
import {GridPage, useFormContext, useMuiDataGridApiRef, useResourceApiService} from 'reactlib';
import {useTranslation} from "react-i18next";
import ContingutIcon from "./details/ContingutIcon.tsx";
import {useContingutActions} from "./details/ContingutActions.tsx";
import useContingutMassiveActions from "./details/ContingutMassiveActions.tsx";
import GridFormField, {GridButton} from "../../components/GridFormField.tsx";
import StyledMuiGrid, {ToolbarButton} from "../../components/StyledMuiGrid.tsx";
import Load from "../../components/Load.tsx";
import {MenuActionButton} from "../../components/MenuButton.tsx";
import * as builder from '../../util/springFilterUtils.ts';
import {potModificar} from "../expedient/details/Expedient.tsx";

const DocumentsGridForm = () => {
    const { t } = useTranslation();
    const { data } = useFormContext();
    const {artifactAction: apiAction} = useResourceApiService('documentResource');

	const actualizarDatos = async () => {
        debugger;
        if (adjunt) {
            apiAction(undefined, {code :"RESUM_IA", data:{ adjunt: adjunt }})
            .then((result)=>{
                if (result) {
                    setNom(result.titol);
                    setDescripcio(result.resum);
                }
            });
        }
	};

    const metaDocumentFilter :string = builder.and(
        builder.eq("metaExpedient.id", data?.metaExpedient?.id),
        builder.eq("actiu", true),
    );

    const [nom, setNom] = useState<string>("");
    const [descripcio, setDescripcio] = useState<string>("");
    const [adjunt, setAdjunt] = useState<File | null>(null);

    useEffect(() => {
        console.log("Estado de adjunt:", adjunt);
        console.log("Estado de nom:", nom);
        console.log("Estado de descripcio:", descripcio);
    }, [adjunt, nom, descripcio]);

    const handleFileChange = (event: any) => { setAdjunt(event); };

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="metaDocument" filter={metaDocumentFilter}/>
        <GridFormField xs={data.isPluginSummarizeActiu?11:12} name="nom" value={nom}/>
        <GridButton xs={1} hidden={!data.isPluginSummarizeActiu} name="isPluginSummarizeActiu" disabled={!adjunt} icon="assistant" onClick={actualizarDatos} title={t('page.document.detall.summarize')}>
            <Icon>assistant</Icon>IA
        </GridButton>
        <GridFormField xs={12} name="descripcio" type={"textarea"} value={descripcio}/>
        <GridFormField xs={12} name="dataCaptura" type={"date"} disabled required/>
        <GridFormField xs={12} name="ntiOrigen" required/>
        <GridFormField xs={12} name="ntiEstadoElaboracion" required/>
        <GridFormField xs={12} name="adjunt" type={"file"} required onChange={handleFileChange}/>
        <GridFormField xs={6} name="hasFirma" hidden={!data.adjunt} disabled={data.documentFirmaTipus=="FIRMA_ADJUNTA"}/>
        <GridFormField xs={6} name="documentFirmaTipus" hidden={!data.adjunt} disabled/>
        <GridFormField xs={12} name="firmaAdjunt" type={"file"} hidden={data.documentFirmaTipus!="FIRMA_SEPARADA"} required/>
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

const sortModel = [{field: 'id', sort: 'desc'}]
const perspectives = ["PATH"]
const columns = [
    {
        field: 'nom',
        flex: 0.5,
        renderCell: (params: any) => <ContingutIcon entity={params?.row}/>
    },
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
        flex: 0.5,
    },
    {
        field: 'createdBy',
        flex: 0.5,
    },
];

const DocumentsGrid = (props:any) => {
    const {entity, onRowCountChange} = props;
    const { t } = useTranslation();

    const dataGridApiRef = useMuiDataGridApiRef()
    const [treeView, setTreeView] = useState<boolean>(true);
    const [expand, setExpand] = useState<boolean>(true);
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
            formAdditionalData={{
                expedient: {id: entity?.id},
                metaExpedient: {id: entity?.metaExpedient?.id},
            }}
            apiRef={dataGridApiRef}
            rowAdditionalActions={actions}
            onRowCountChange={onRowCountChange}
            treeData={treeView}
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

            isGroupExpandedByDefault={() => expand}
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
        />
        {components}
        {massiveComponents}
        </Load>
    </GridPage>
}

export default DocumentsGrid;