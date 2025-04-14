import {GridPage, MuiGrid, useMuiDataGridApiRef} from "reactlib";
import * as builder from "../../util/springFilterUtils.ts";
import {formatDate} from "../../util/dateUtils.ts";
import Icon from "@mui/material/Icon";
import usePublicacioActions from "./details/PublicacioActions.tsx";
import {Grid} from "@mui/material";
import GridFormField from "../../components/GridFormField.tsx";

const StyledEstat = (props:any) => {
    const { entity: publicacio } = props;

    switch (publicacio?.estat) {
        case 'PENDENT':
            return <Icon color={'warning'}>schedule</Icon>;
        case 'ENVIAT':
            return <Icon color={'info'}>mail</Icon>;
        case 'REBUTJAT':
            return <Icon color={'disabled'}>close</Icon>;
        case 'PROCESSAT':
            return <Icon color={'error'}>check</Icon>;
    }

    return <></>;
}

const PublicacioGridForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="tipus" required/>
        <GridFormField xs={12} name="estat" required/>
        <GridFormField xs={12} name="assumpte" required/>
        <GridFormField xs={12} name="dataPublicacio" type={"date"}/>
        <GridFormField xs={12} name="enviatData" type={"date"} required/>
        <GridFormField xs={12} name="observacions" type={"textarea"}/>
    </Grid>
}

const columns = [
    {
        field: 'tipus',
        flex: 0.5,
    },
    {
        field: 'createdDate',
        flex: 0.75,
        valueFormatter: (value: any) => formatDate(value)
    },
    {
        field: 'processatData',
        flex: 0.75,
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
        field: 'estat',
        flex: 0.5,
        renderCell: (params: any) => <StyledEstat entity={params?.row}/>
    },
]

const PublicacioGrid = (props:any) => {
    const { id, onRowCountChange } = props;

    const apiRef = useMuiDataGridApiRef()
    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {actions, components} = usePublicacioActions(refresh);

    return <GridPage>
        <MuiGrid
            resourceName="documentPublicacioResource"
            // perspectives={['']}
            popupEditActive
            popupEditFormContent={<PublicacioGridForm/>}
            columns={columns}
            rowAdditionalActions={actions}
            paginationActive
            filter={builder.and(
                builder.eq('expedient.id', id)
            )}
            titleDisabled
            apiRef={apiRef}
            staticSortModel={[{field: 'id', sort: 'asc'}]}
            onRowsChange={(rows, info) => onRowCountChange?.(info?.totalElements)}
            disableColumnMenu
            disableColumnSorting
            toolbarHideCreate
        />
        {components}
    </GridPage>
}
export default PublicacioGrid;