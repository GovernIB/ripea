import {Grid} from "@mui/material";
import {GridPage} from "reactlib";
import * as builder from "../../util/springFilterUtils.ts";
import {formatDate} from "../../util/dateUtils.ts";
import GridFormField from "../../components/GridFormField.tsx";
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";
import usePublicacioActions from "./details/PublicacioActions.tsx";
import {EstatMessage} from "../remesa/RemesaGrid.tsx";

const StyledEstat = (props:any) => {
    const { entity: publicacio } = props;

    switch (publicacio?.estat) {
        case 'PENDENT':
            return <EstatMessage icon={'schedule'} color={'warning'} title={publicacio?.estat}/>;
        case 'ENVIAT':
            return <EstatMessage icon={'mail'} color={'info'} title={publicacio?.estat}/>;
        case 'REBUTJAT':
            return <EstatMessage icon={'close'} color={'disabled'} title={publicacio?.estat}/>;
        case 'PROCESSAT':
            return <EstatMessage icon={'check'} color={'error'} title={publicacio?.estat}/>;
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

const sortModel:any = [{field: 'id', sort: 'asc'}];
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
        flex: 0.25,
        renderCell: (params: any) => <StyledEstat entity={params?.row}/>
    },
]

const PublicacioGrid = (props:any) => {
    const { id, onRowCountChange } = props;

    const {actions, components} = usePublicacioActions();

    return <GridPage>
        <StyledMuiGrid
            resourceName="documentPublicacioResource"
            // perspectives={['']}
            popupEditActive
            popupEditFormContent={<PublicacioGridForm/>}
            columns={columns}
            rowAdditionalActions={actions}
            // paginationActive
            filter={builder.eq('expedient.id', id)}
            staticSortModel={sortModel}
            onRowCountChange={onRowCountChange}
            disableColumnSorting
            toolbarHideCreate
        />
        {components}
    </GridPage>
}
export default PublicacioGrid;