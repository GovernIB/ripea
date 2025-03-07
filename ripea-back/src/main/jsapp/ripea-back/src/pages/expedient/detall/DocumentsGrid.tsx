import {
    GridPage,
    MuiGrid, useMuiDataGridApiRef,
} from 'reactlib';
import React, { useState } from "react";
import { useParams } from "react-router-dom";
import ContingutIcon from "./ContingutIcon.tsx";
import { FormControl, Grid, FormControlLabel, InputLabel, Select, MenuItem, Checkbox, Icon } from "@mui/material";

const DocumentsGrid: React.FC = () => {
    const { id } = useParams();
    const [expand, setExpand] = useState<boolean>(true);
    const [treeView, setTreeView] = useState<boolean>(true);
    const [vista, setVista] = useState<string>("carpeta");
    const dataGridApiRef = useMuiDataGridApiRef()

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
            field: 'metaNode',
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
            readOnly
            disableColumnSorting
            disableColumnMenu
            apiRef={dataGridApiRef}
            // checkboxSelection
            treeData={treeView}
            treeDataAdditionalRows={(_rows) => {
                const additionalRows :any[] = [];

                if(_rows!=null && vista == "carpeta") {
                    for (const row of _rows) {
                        const aditionalRow = row.parentPath
                            .filter((a: any) => a.id != row.id)
                            .filter((a: any) => !additionalRows.map((b)=>b.id).includes(a.id))
                            .map((a: any) =>{a.group="A";return a})
                        additionalRows.push(...aditionalRow);
                    }
                    setTreeView(additionalRows.length > 0)
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
    </GridPage>
}

export default DocumentsGrid;