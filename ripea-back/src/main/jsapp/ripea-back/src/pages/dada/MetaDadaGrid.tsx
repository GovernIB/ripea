import {useMuiDataGridApiRef, useResourceApiService} from "reactlib";
import * as builder from '../../util/springFilterUtils';
import {useDadaActions} from "./details/DadaActions.tsx";
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";
import {useEffect, useState} from "react";
import {MultiplicitatStyled} from "../contingut/details/MetaExpedient.tsx";
import {Typography} from "@mui/material";
import useDataGrid from "./details/DataGrid.tsx";

const dadesFilter = (metaDada:any, dades:any[]) :any[] => {
    return dades?.filter((dada)=>dada?.metaDada?.id == metaDada?.id)
}

const sortModel:any = [{ field: 'ordre', sort: 'asc' }]

export const StyledDadaValor = (props: any) => {
    const {valor} = props;
    const style = {
                border: '1px solid lightgray',
                borderRadius: '4px',
                padding: '2px 6px',
                marginRight: '4px',
                display: 'inline-block'
            };
    return <Typography variant="caption" sx={style}> {valor} </Typography>
}

const MetaDadaGrid = (props: any) => {
    const apiRef = useMuiDataGridApiRef()
    const { entity, onRowCountChange, onRefresh } = props
    const {
        isReady,
        find: apiFindAll
    } = useResourceApiService('dadaResource');
    const [dades, setDades] = useState<any[]>([]);

    const findByExpedient = (id:any) => {
        if (id) {
            const filter = builder.eq('node.id', id)
            apiFindAll({unpaged: true, filter})
                .then((result) => {
                    setDades(result?.rows)
                    onRowCountChange?.(result?.rows?.length)
                })
        }
    }

    const refresh = () => {
        apiRef.current.refresh();
        onRefresh?.();
        findByExpedient(entity?.id)
    }

    useEffect(() => {
        if (isReady) {
            findByExpedient(entity?.id)
        }
    }, [isReady]);

    const columns = [
        {
            field: 'nom',
            flex: 0.7,
        },
        {
            field: 'multiplicitat',
            flex: 0.2,
            renderCell: (params:any) => <MultiplicitatStyled multiplicitat={params?.formattedValue}/>
        },
        {
            field: 'dades',
            flex: 0.8,
            valueGetter: (_value: any, row:any) => dadesFilter(row, dades),
            renderCell: (params: any) => {
                const value = params.value;
                const row = params.row;
                if (row?.tipus == 'DOMINI') {
                    return value?.map((dada: any) => (
                        <StyledDadaValor valor={dada?.dominiDescription}/>
                    ));
                }
                return value?.map((dada: any) => (
                    <StyledDadaValor valor={dada?.valor}/>
                ));
            },
        }
    ]

    const {actions, components} = useDadaActions(entity, refresh);
    const {handleOpen, content} = useDataGrid(entity, refresh)

    return <>
        <StyledMuiGrid
            resourceName="metaDadaResource"
            columns={columns}
            filter={
                builder.and(
                    builder.eq('metaNode.id', entity?.metaNode?.id)
                )
            }
            staticSortModel={sortModel}
            apiRef={apiRef}
            rowAdditionalActions={actions}
            toolbarHide
            disableColumnSorting
            readOnly
            onRowClick={(params) => {
                if (entity?.potModificar) {
                    handleOpen(null, params.row)
                }
            }}

            paginationActive={false}
            autoHeight
        />
        {components}
        {content}
    </>
}
export default MetaDadaGrid;