import {
    GridPage,
    MuiGrid,
    useFormContext,
    useMuiDataGridApiRef,
} from 'reactlib';
import React, { useState } from "react";
import ContingutIcon from "./details/ContingutIcon.tsx";
import { FormControl, Grid, FormControlLabel, InputLabel, Select, MenuItem, Checkbox, Icon } from "@mui/material";
import {useContingutActions} from "./details/ContingutActions.tsx";
import GridFormField from "../../components/GridFormField.tsx";
import * as builder from '../../util/springFilterUtils.ts';

const DocumentsGridForm = (props:any) => {
    const {expedient} = props;
    const formContext = useFormContext();
    const { data } = formContext;

    const metaDocumentFilter :string = builder.and(
        builder.eq("metaExpedient.id", expedient?.metaExpedient?.id),
        builder.eq("actiu", true),
    );

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="metaDocument" filter={metaDocumentFilter}/>
        <GridFormField xs={12} name="nom"/>
        <GridFormField xs={12} name="descripcio"/>
        <GridFormField xs={12} name="dataCaptura" type={"date"} disabled required/>
        <GridFormField xs={12} name="ntiOrigen" required/>
        <GridFormField xs={12} name="ntiEstadoElaboracion" required/>
        <GridFormField xs={12} name="adjunt" type="file" required/>
        {!!data.adjunt && <GridFormField xs={6} name="hasFirma" disabled={data.documentFirmaTipus=="FIRMA_ADJUNTA"}/>}
        {!!data.adjunt && <GridFormField xs={6} name="documentFirmaTipus" disabled/>}
        {data.documentFirmaTipus=="FIRMA_SEPARADA" && <GridFormField xs={12} name="firmaAdjunt" type="file" required/>}
    </Grid>
}

const DocumentsGrid: React.FC = (props:any) => {
    const {id, entity, onRowCountChange} = props;
    const [expand, setExpand] = useState<boolean>(true);
    const [treeView, setTreeView] = useState<boolean>(true);
    const [vista, setVista] = useState<string>("carpeta");
    const dataGridApiRef = useMuiDataGridApiRef()

    const refresh = () => {
        dataGridApiRef?.current?.refresh?.();
    }
    const {
        actions: commonActionsActions,
        components: commonActionsComponents
    } = useContingutActions(refresh);

    const columns = [
        {
            field: 'nom',
            flex: 0.5,
            renderCell: (params: any) => {
                return <ContingutIcon entity={params?.row}>{params?.row.nom}</ContingutIcon>
            }
        },
        {
            field: 'descripcio',
            flex: 0.5,
        },
        {
            field: 'metaDocument',
            flex: 0.5,
            valueFormatter: (value: any) => {
                return value?.description;
            }
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
    return <GridPage>
        <MuiGrid
            resourceName="documentResource"
            columns={columns}
            paginationActive
            filter={`expedient.id:${id}`}
            perspectives={["PATH"]}
            titleDisabled
            popupEditCreateActive
            popupEditFormContent={<DocumentsGridForm expedient={entity}/>}
            formAdditionalData={{
                expedient: {
                    id: id
                },
                dataCaptura: Date.now(),
            }}
            disableColumnSorting
            disableColumnMenu
            apiRef={dataGridApiRef}
            rowAdditionalActions={commonActionsActions}
            onRowsChange={(rows) => onRowCountChange?.(rows.filter((a)=>a?.tipus=="DOCUMENT").length)}
            // checkboxSelection
            treeData={treeView}
            treeDataAdditionalRows={(_rows) => {
                const additionalRows :any[] = [];

                if(_rows!=null && vista == "carpeta") {
                    for (const row of _rows) {
                        const aditionalRow = row.parentPath
                            ?.filter((a: any) => a.id != row.id && !additionalRows.map((b)=>b.id).includes(a.id))
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
            toolbarAdditionalRow={<Grid display={"flex"} flexDirection={"row"} alignItems={"center"} justifyContent={"space-between"} pb={1}>
                <Grid item xs={2}>
                    {treeView && <FormControlLabel control={<Checkbox
                        checked={expand}
                        onChange={(event) => setExpand(event.target.checked)}

                        icon={<Icon>arrow_right</Icon>}
                        checkedIcon={<Icon>arrow_drop_down</Icon>}
                    />} label={expand ? "Contraer" : "Expandir"} />}
                </Grid>

                <Grid item xs={3}>
                    <FormControl fullWidth>
                        <InputLabel id="demo-simple-select-label">Tipo de vista</InputLabel>
                        <Select
                            labelId="demo-simple-select-label"
                            value={vista}
                            onChange={(event) => {
                                setVista(event.target.value)
                                dataGridApiRef.current.refresh()
                            }}
                        >
                            <MenuItem value={"estat"}>Vista por estado</MenuItem>
                            <MenuItem value={"tipus"}>Vista por tipo documento</MenuItem>
                            <MenuItem value={"carpeta"} selected>Vista por carpeta</MenuItem>
                        </Select>
                    </FormControl>
                </Grid>
            </Grid>}
        />
        {commonActionsComponents}
    </GridPage>
}

export default DocumentsGrid;