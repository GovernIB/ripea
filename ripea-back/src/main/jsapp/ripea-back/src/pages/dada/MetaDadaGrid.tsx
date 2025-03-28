import {GridPage, MuiGrid, useMuiDataGridApiRef} from "reactlib";
import * as builder from '../../util/springFilterUtils';
import {useDadaActions} from "./details/DadaActions.tsx";

const dadesFilter = (entity:any, dades:any[]) :any[] => {
    return dades.filter((dada)=>dada?.node?.id == entity?.id)
}

const MetaDadaGrid = (props: { entity:any, onRowCountChange?: ((value:number) => void) }) => {
    const apiRef = useMuiDataGridApiRef()
    const { entity, onRowCountChange } = props

    const refresh = () => {
        apiRef.current.refresh();
    }

    const columns = [
        {
            field: 'nom',
            flex: 0.5,
        },
        {
            field: 'dades',
            flex: 0.75,
            valueGetter: (value: any) => dadesFilter(entity, value),
            valueFormatter: (value: any) => value?.map((dada: any) => dada?.valor).join(", \n"),
        }
    ]

    const {actions, components} = useDadaActions(entity,refresh);

    return <GridPage>
        <MuiGrid
            titleDisabled
            resourceName="metaDadaResource"
            columns={columns}
            filter={
                builder.and(
                    builder.eq('metaNode.id', entity?.metaNode?.id)
                )
            }
            sortModel={[{ field: 'ordre', sort: 'asc' }]}
            perspectives={['DADES']}
            apiRef={apiRef}
            rowAdditionalActions={actions}
            paginationActive
            disableColumnMenu
            disableColumnSorting
            readOnly

            onRowsChange={(rows)=> {
                const array:any[] = []
                rows.forEach(row => array.push(...row.dades))
                onRowCountChange?.(dadesFilter(entity, array).length)
            }}
        />
        {components}
    </GridPage>
}
export default MetaDadaGrid;