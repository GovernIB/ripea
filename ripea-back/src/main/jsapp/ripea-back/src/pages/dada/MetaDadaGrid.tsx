import {GridPage, useMuiDataGridApiRef, useResourceApiService} from "reactlib";
import * as builder from '../../util/springFilterUtils';
import {useDadaActions} from "./details/DadaActions.tsx";
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";
import {useEffect, useState} from "react";

const dadesFilter = (metaDada:any, dades:any[]) :any[] => {
    return dades?.filter((dada)=>dada?.metaDada?.id == metaDada?.id)
}

const sortModel:any = [{ field: 'ordre', sort: 'asc' }]

const MetaDadaGrid = (props: { entity:any, onRowCountChange?: ((value:number) => void) }) => {
    const apiRef = useMuiDataGridApiRef()
    const { entity, onRowCountChange } = props

    const {
        isReady,
        find: apiFindAll
    } = useResourceApiService('dadaResource');
    const [dades, setDades] = useState<any[]>([]);

    const findByExpedient = (id:any) => {
        if (id) {
            const filter = builder.eq('node.id', id)
            console.log("filter", filter)
            apiFindAll({unpaged: true, filter})
                .then((result) => {
                    console.log("result", result)
                    setDades(result?.rows)
                })
        }
    }

    const refresh = () => {
        apiRef.current.refresh();
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
            flex: 0.5,
        },
        {
            field: 'dades',
            flex: 0.75,
            valueGetter: (value: any, row:any) => dadesFilter(row, dades),
            valueFormatter: (value: any, row:any) => {
                if (row?.tipus == 'DOMINI') {
                    return value?.map((dada: any) => dada?.dominiDescription).join(", \n")
                }
                return value?.map((dada: any) => dada?.valor).join(", \n")
            },
        }
    ]

    const {actions, components} = useDadaActions(entity,refresh);

    return <GridPage>
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
            // paginationActive
            disableColumnSorting
            readOnly
            onRowsChange={()=> onRowCountChange?.(dades?.length)}
        />
        {components}
    </GridPage>
}
export default MetaDadaGrid;