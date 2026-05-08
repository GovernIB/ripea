import {Grid} from "@mui/material";
import * as builder from "../../util/springFilterUtils.ts";
import {formatDate} from "../../util/dateUtils.ts";
import GridFormField from "../../components/GridFormField.tsx";
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";
import usePublicacioActions from "./details/PublicacioActions.tsx";
import {EstatMessage} from "../remesa/RemesaGrid.tsx";
import {useTranslation} from "react-i18next";

const StyledEstat = (props:any) => {
    const { estat, children } = props;

    switch (estat) {
        case 'PENDENT':
            return <EstatMessage icon={'schedule'} color={'warning'} title={children}/>;
        case 'ENVIAT':
            return <EstatMessage icon={'mail'} color={'info'} title={children}/>;
        case 'REBUTJAT':
            return <EstatMessage icon={'close'} color={'disabled'} title={children}/>;
        case 'PROCESSAT':
            return <EstatMessage icon={'check'} color={'error'} title={children}/>;
    }

    return <></>;
}

const PublicacioGridForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="tipus" required/>
        <GridFormField name="estat" required/>
        <GridFormField name="assumpte" required/>
        <GridFormField name="dataPublicacio" type={"date"}/>
        <GridFormField name="enviatData" type={"date"} required/>
        <GridFormField name="observacions" type={"textarea"}/>
    </Grid>
}

const sortModel:any = [{field: 'id', sort: 'desc'}];
const columns = [
    {
        field: 'tipus',
        flex: 0.4,
    },
    {
        field: 'createdDate',
        flex: 0.5,
        valueFormatter: (value: any) => formatDate(value)
    },
    {
        field: 'processatData',
        flex: 0.5,
        valueFormatter: (value: any) => formatDate(value)
    },
    {
        field: 'assumpte',
        flex: 0.9,
    },
    {
        field: 'document',
        flex: 0.7,
    },
    {
        field: 'estat',
        flex: 0.25,
        renderCell: (params: any) => <StyledEstat estat={params?.row?.estat}>{params?.formattedValue}</StyledEstat>
    },
]

const PublicacioGrid = (props:any) => {
    const { entity, onRowCountChange } = props;
    const { t } = useTranslation()

    const {actions, components} = usePublicacioActions(entity);

    return <>
        <StyledMuiGrid
            resourceName="documentPublicacioResource"
            popupEditFormDialogResourceTitle={t('page.publicacio.title')}
            // perspectives={['']}
            popupEditActive
            popupEditFormContent={<PublicacioGridForm/>}
            columns={columns}
            rowAdditionalActions={actions}
            paginationActive={false}
            autoHeight
            filter={builder.eq('expedient.id', entity?.id)}
            staticSortModel={sortModel}
            onRowCountChange={onRowCountChange}
            disableColumnSorting
            toolbarHideCreate

            popupEditFormI18nKeys={{
                updateSuccess: 'page.dada.action.update.ok',
                deleteSuccess: 'page.dada.action.delete.ok',
            }}
        />
        {components}
    </>
}
export default PublicacioGrid;