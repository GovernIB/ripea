import { Grid } from "@mui/material";
import {
    GridPage,
    useFormContext,
    useMuiDataGridApiRef,
} from 'reactlib';
import {useTranslation} from "react-i18next";
import GridFormField from "../../components/GridFormField.tsx";
import * as builder from "../../util/springFilterUtils.ts";
import { formatDate } from "../../util/dateUtils.ts";
import useTascaActions from "./details/TascaActions.tsx";
import {StyledPrioritat} from "../expedient/ExpedientGrid.tsx";
import {CommentDialog} from "../CommentDialog.tsx";
import StyledMuiGrid from '../../components/StyledMuiGrid.tsx';
import {potModificar} from "../expedient/details/Expedient.tsx";

const TasquesGridForm = () => {
    const { data } = useFormContext();

    const metaTascaFilter: string = builder.and(
        builder.eq("metaExpedient.id", data?.metaExpedient?.id),
        builder.eq("activa", true),
    );

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="metaExpedientTasca" filter={metaTascaFilter} />
        <GridFormField xs={12} name="metaExpedientTascaDescription" readOnly disabled />
        <GridFormField xs={12} name="responsables" multiple />
        <GridFormField xs={12} name="observadors" multiple />
        <GridFormField xs={6} name="duracio" />
        <GridFormField xs={6} name="dataLimit" type={"date"} />
        <GridFormField xs={12} name="titol" />
        <GridFormField xs={12} name="observacions" type={"textarea"} />
        <GridFormField xs={12} name="prioritat" required />
    </Grid>
}

const perspectives = ["RESPONSABLES_RESUM"]
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
        sortable: false,
    },
    {
        field: 'responsableActual',
        flex: 0.5,
        sortable: false,
    },
    {
        field: 'estat',
        flex: 0.5,
    },
    {
        field: 'prioritat',
        flex: 0.5,
        renderCell: (params: any) => <StyledPrioritat entity={params?.row}>{params.formattedValue}</StyledPrioritat>
    },
];

const sortModel:any = [{field: 'id', sort: 'asc'}];
const TasquesGrid = (props: any) => {
    const { entity, onRowCountChange } = props;
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();

    const additionalColumns = [
        ...columns,
        {
            field: 'numComentaris',
            headerName: '',
            sortable: false,
            flex: 0.25,
            renderCell: (params: any) => <CommentDialog
                entity={params?.row}
                title={`${t('page.comment.tasca')}: ${params?.row?.metaExpedientTascaDescription}`}
                resourceName={'expedientTascaComentariResource'}
                resourceReference={'expedientTasca'}
            />
        },
    ]

    const { actions, components } = useTascaActions(entity, apiRef?.current?.refresh);

    return <GridPage>
        <StyledMuiGrid
            apiRef={apiRef}
            resourceName="expedientTascaResource"
            popupEditFormDialogResourceTitle={t('page.tasca.title')}
            columns={additionalColumns}
            // paginationActive
            filter={builder.and(builder.eq('expedient.id', entity?.id))}
            perspectives={perspectives}
            sortModel={sortModel}
            onRowCountChange={onRowCountChange}
            popupEditCreateActive
			toolbarCreateTitle={t('page.tasca.action.new.label')}
            popupEditFormContent={<TasquesGridForm/>}
            formAdditionalData={{
                expedient: {id: entity?.id},
                metaExpedient: {id: entity?.metaExpedient?.id},
            }}
            rowAdditionalActions={actions}
            toolbarHideCreate={!potModificar(entity)}
        />
        {components}
    </GridPage>
}

export default TasquesGrid;