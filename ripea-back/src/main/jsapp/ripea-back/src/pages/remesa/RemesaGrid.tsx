import {GridPage, MuiGrid, useMuiDataGridApiRef} from "reactlib";
import {formatDate} from "../../util/dateUtils.ts";
import {Typography} from "@mui/material";
import useRemesaActions from "./details/RemesaActions.tsx";
import Icon from "@mui/material/Icon";
import * as builder from "../../util/springFilterUtils.ts";

const StyledEstat = (props:any) => {
    const { entity } = props;

    const commonStyle = {p: 0.5, display: 'flex', alignItems: 'center', borderRadius: '5px', width: 'max-content'}
    const style = entity?.error
        ? { backgroundColor: '#d99b9d' }
        : { backgroundColor: '#c3e8d1' }

    return <Typography variant="caption" sx={{ ...commonStyle, ...style }}>
        <Icon fontSize={"inherit"}>{entity?.error ?'close' :'check'}</Icon>
        {entity?.notificacioEstat}
    </Typography>
}

const columns = [
    {
        field: 'tipus',
        flex: 0.5
    },
    {
        field: 'createdDate',
        flex: 0.5,
        valueFormatter: (value: any) => formatDate(value)
    },
    {
        field: 'dataEnviada',
        flex: 0.5,
        valueFormatter: (value: any) => formatDate(value)
    },
    {
        field: 'dataFinalitzada',
        flex: 0.5,
        valueFormatter: (value: any) => formatDate(value)
    },
    {
        field: 'assumpte',
        flex: 0.5,
    },
    {
        field: 'document',
        flex: 0.5,
    },
    {
        field: 'notificacioEstat',
        flex: 0.5,
        renderCell: (params:any) => <StyledEstat entity={params?.row}/>
    },
]

const RemesaGrid = (props:any) => {
    const { id } = props;

    const apiRef = useMuiDataGridApiRef()
    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {actions, components} = useRemesaActions(refresh);

    return <GridPage>
        <MuiGrid
            resourceName="documentNotificacioResource"
            // perspectives={['']}
            columns={columns}
            rowAdditionalActions={actions}
            paginationActive
            filter={builder.and(
                builder.eq('expedient.id', id)
            )}
            titleDisabled
            apiRef={apiRef}
            staticSortModel={[{field: 'id', sort: 'asc'}]}
            disableColumnMenu
            disableColumnSorting
            readOnly
        />
        {components}
    </GridPage>
}
export default RemesaGrid;