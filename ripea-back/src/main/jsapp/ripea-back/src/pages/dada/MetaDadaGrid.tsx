import {useMuiDataGridApiRef, useResourceApiService} from "reactlib";
import * as builder from '../../util/springFilterUtils';
import {useDadaActions} from "./details/DadaActions.tsx";
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";
import {useEffect, useMemo, useState} from "react";
import {MultiplicitatStyled} from "../contingut/details/MetaExpedient.tsx";
import useDataGrid from "./details/DataGrid.tsx";
import {StyledLabel} from "../../components/StyledLabel.tsx";

const dadesFilter = (metaDada:any, dades:any[]) :any[] => {
    return dades?.filter((dada)=>dada?.metaDada?.id == metaDada?.id)
}

const sortModel:any = [{ field: 'ordre', sort: 'asc' }]
// Només es mostren les metadades actives al procediment de l'expedient
const namedQueries = ['ACTIVES']
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
        apiRef.current?.refresh();
        onRefresh?.();
        findByExpedient(entity?.id)
    }

    useEffect(() => {
        if (isReady) {
            findByExpedient(entity?.id)
        }
    }, [isReady]);

    const columns = useMemo(() => [
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
                    // Si la descripció del domini no s'ha pogut resoldre (domini inaccessible o valor que ja no
                    // hi és) es mostra el valor guardat, com fa el detall dels valors: altrament sortiria un
                    // requadre buit i el valor semblaria perdut.
                    return value?.map((dada: any) => (
                        <StyledLabel key={dada.id} className={'multiplicitat'} sx={{marginRight: '4px'}}>{dada?.dominiDescription || dada?.valor}</StyledLabel>
                    ));
                }
                return value?.map((dada: any) => (
                    <StyledLabel key={dada.id} className={'multiplicitat'} sx={{marginRight: '4px'}}>{dada?.valor}</StyledLabel>
                ));
            },
        }
    ], [dades]);

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
            namedQueries={namedQueries}
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