import {GridPage, useMuiDataGridApiRef} from "reactlib";
import * as builder from '../../util/springFilterUtils';
import {useDadaActions} from "./details/DadaActions.tsx";
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";

const dadesFilter = (entity:any, dades:any[]) :any[] => {
    return dades.filter((dada)=>dada?.node?.id == entity?.id)
}

const sortModel:any = [{ field: 'ordre', sort: 'asc' }]
const perspectives = ['DADES']

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
            valueFormatter: (value: any, row:any) => {
                if (row?.tipus?.toLowerCase() == 'DOMINI') {
                    return value?.map((dada: any) => dada?.domini?.description).join(", \n")
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
            perspectives={perspectives}
            apiRef={apiRef}
            rowAdditionalActions={actions}
            // paginationActive
            disableColumnSorting
            readOnly
            onRowsChange={(rows:any)=> {
                const array:any[] = []
                rows.forEach((row:any) => array.push(...row.dades))
                onRowCountChange?.(dadesFilter(entity, array).length)
            }}
        />
        {components}
    </GridPage>
}
export default MetaDadaGrid;