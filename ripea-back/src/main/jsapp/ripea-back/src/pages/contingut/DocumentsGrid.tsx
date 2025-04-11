import {
    GridPage,
    MuiGrid,
    useFormContext,
    useMuiDataGridApiRef,
} from 'reactlib';
import { useState } from "react";
import ContingutIcon from "./details/ContingutIcon.tsx";
import { FormControl, Grid, FormControlLabel, InputLabel, Select, MenuItem, Checkbox, Icon } from "@mui/material";
import {useContingutActions} from "./details/ContingutActions.tsx";
import GridFormField from "../../components/GridFormField.tsx";
import * as builder from '../../util/springFilterUtils.ts';
import {useTranslation} from "react-i18next";

const DocumentsGridForm = () => {
    const { data } = useFormContext();

    const metaDocumentFilter :string = builder.and(
        builder.eq("metaExpedient.id", data?.metaExpedient?.id),
        builder.eq("actiu", true),
    );

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="metaDocument" filter={metaDocumentFilter}/>
        <GridFormField xs={12} name="nom"/>
        <GridFormField xs={12} name="descripcio" type={"textarea"}/>
        <GridFormField xs={12} name="dataCaptura" type={"date"} disabled required/>
        <GridFormField xs={12} name="ntiOrigen" required/>
        <GridFormField xs={12} name="ntiEstadoElaboracion" required/>
        <GridFormField xs={12} name="adjunt" type={"file"} required/>
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

    return <FormControlLabel control={<Checkbox
        checked={value}
        onChange={(event) => onChange(event.target.checked)}
        icon={<Icon>arrow_right</Icon>}
        checkedIcon={<Icon>arrow_drop_down</Icon>}
    />} label={value ? t("common.contract") : t("common.expand")}/>
}

const TreeViewSelector = (props:{value: any, onChange: (value: any) => void }) => {
    const {value, onChange} = props;
    const { t } = useTranslation();

    return <Grid item xs={3}>
        <FormControl fullWidth size="small">
            <InputLabel id="demo-simple-select-label">{t('page.document.view.title')}</InputLabel>
            <Select
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

const columns = [
    {
        field: 'nom',
        flex: 0.5,
        renderCell: (params: any) => <ContingutIcon entity={params?.row}>{params?.row.nom}</ContingutIcon>
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
    const {actions, components} = useContingutActions(refresh);

    return <GridPage>
        <MuiGrid
            resourceName="documentResource"
            popupEditFormDialogResourceTitle={t('page.document.title')}
            columns={columns}
            paginationActive
            filter={`expedient.id:${entity?.id}`}
            perspectives={["PATH"]}
            staticSortModel={[{field: 'id', sort: 'desc'}]}
            titleDisabled
            popupEditCreateActive
            popupEditFormContent={<DocumentsGridForm/>}
            formAdditionalData={{
                expedient: {id: entity?.id},
                metaExpedient: {id: entity?.metaExpedient?.id},
            }}
            disableColumnSorting
            disableColumnMenu
            rowHideUpdateButton={(row:any) => row?.tipus!="DOCUMENT"}
            rowHideDeleteButton={(row:any) => row?.tipus!="DOCUMENT"}
            apiRef={dataGridApiRef}
            rowAdditionalActions={actions}
            onRowsChange={(rows) => onRowCountChange?.(rows.filter((a) => a?.tipus == "DOCUMENT").length)}
            // checkboxSelection
            treeData={treeView}
            treeDataAdditionalRows={(_rows) => {
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
            getTreeDataPath={(row) :string[] => {
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
                    position: 3,
                    element: <TreeViewSelector value={vista} onChange={(value:any) => {
                        setVista(value);
                        refresh();
                    }} />,
                }
            ]}
        />
        {components}
    </GridPage>
}

export default DocumentsGrid;