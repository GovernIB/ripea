import {
    GridPage,
    MuiGrid, useFormContext,
    useMuiDataGridApiRef,
} from 'reactlib';
import { formatDate } from "../../util/dateUtils.ts";
import { Grid } from "@mui/material";
import GridFormField from "../../components/GridFormField.tsx";
import * as builder from "../../util/springFilterUtils.ts";
import { TascaCommentDialog as CommentDialog } from "../CommentDialog.tsx";
import useTascaActions from "./details/TascaActions.tsx";
import {useTranslation} from "react-i18next";

const TasquesGridForm = () => {
    const { data } = useFormContext();

    const metaTascaFilter: string = builder.and(
        builder.eq("metaExpedient.id", data?.metaExpedient?.id),
        builder.eq("activa", true),
    );

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="metaExpedientTasca" filter={metaTascaFilter} />
        <GridFormField xs={12} name="metaExpedientTascaDescription" readOnly disabled />
        <GridFormField xs={12} name="responsableActual" />
        <GridFormField xs={12} name="observadors" multiple />
        <GridFormField xs={6} name="duracio" />
        <GridFormField xs={6} name="dataLimit" type={"date"} componentProps={{ disablePast: true }} />
        <GridFormField xs={12} name="titol" />
        <GridFormField xs={12} name="observacions" type={"textarea"} />
        <GridFormField xs={12} name="prioritat" required />
    </Grid>
}

const columns = [
    {
        field: 'metaExpedientTasca',
        flex: 0.5,
    },
    {
        field: 'dataInici',
        flex: 0.5,
        valueFormatter: (value: any) => formatDate(value)
    },
    {
        field: 'dataLimit',
        flex: 0.5,
        valueFormatter: (value: any) => formatDate(value, "DD/MM/Y")
    },
    {
        field: 'titol',
        flex: 0.5,
    },
    {
        field: 'observacions',
        flex: 0.5,
    },
    {
        field: 'responsablesStr',
        flex: 0.5,
    },
    {
        field: 'responsableActual',
        flex: 0.5,
    },
    {
        field: 'estat',
        flex: 0.5,
    },
    {
        field: 'prioritat',
        flex: 0.5,
    },
    {
        field: 'numComentaris',
        headerName: '',
        sortable: false,
        disableColumnMenu: true,
        flex: 0.25,
        renderCell: (params: any) => <CommentDialog entity={params?.row} />
    },
];

const TasquesGrid = (props: any) => {
    const { entity, onRowCountChange } = props;
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();

    const { actions, components } = useTascaActions(apiRef?.current?.refresh);

    return <GridPage>
        <MuiGrid
            apiRef={apiRef}
            resourceName="expedientTascaResource"
            popupEditFormDialogResourceTitle={t('page.tasca.title')}
            columns={columns}
            paginationActive
            filter={`expedient.id:${entity?.id}`}
            titleDisabled
            perspectives={["RESPONSABLES_RESUM"]}
            onRowsChange={(rows) => onRowCountChange?.(rows.length)}
            popupEditCreateActive
            popupEditFormContent={<TasquesGridForm/>}
            formAdditionalData={{
                expedient: {id: entity?.id},
                metaExpedient: {id: entity?.metaExpedient?.id},
            }}
            rowAdditionalActions={actions}
            disableColumnMenu
            rowHideUpdateButton
            rowHideDeleteButton
        />
        {components}
    </GridPage>
}

export default TasquesGrid;