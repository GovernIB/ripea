import {Grid} from "@mui/material";
import {
    useFormContext,
    useMuiDataGridApiRef,
} from 'reactlib';
import {useTranslation} from "react-i18next";
import GridFormField from "../../components/GridFormField.tsx";
import * as builder from "../../util/springFilterUtils.ts";
import { formatDate } from "../../util/dateUtils.ts";
import useTascaActions from "./details/TascaActions.tsx";
import {StyledPrioritat} from "../expedient/ExpedientGrid.tsx";
import {TascaComment} from "../CommentDialog.tsx";
import StyledMuiGrid from '../../components/StyledMuiGrid.tsx';
import useTascaDetail from "./details/TascaDetail.tsx";
import {StyledDate} from "./TasquesGrid.tsx";

const TasquesGridForm = () => {
    const { data } = useFormContext();

    const metaTascaFilter: string = builder.and(
        builder.eq("metaExpedient.id", data?.metaExpedient?.id),
        builder.eq("activa", true),
    );

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="metaExpedientTasca" filter={metaTascaFilter}/>
        <GridFormField xs={12} name="metaExpedientTascaDescription" type={"textarea"} readOnly disabled/>
        <GridFormField xs={12} name="responsables" multiple required/>
        <GridFormField xs={12} name="observadors" multiple/>
        <GridFormField xs={6} name="duracio" debounce/>
        <GridFormField xs={6} name="dataLimit" type={"date"}/>
        <GridFormField xs={12} name="titol"/>
        <GridFormField xs={12} name="observacions" type={"textarea"}/>
        <GridFormField xs={12} name="prioritat" required/>
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
        flex: 0.55,
        valueFormatter: (value: any) => formatDate(value)
    },
    {
        field: 'dataLimit',
        flex: 0.45,
        valueFormatter: (value: any) => formatDate(value, "DD/MM/Y"),
        renderCell: (params: any) => <StyledDate entity={params?.row}>{params?.formattedValue}</StyledDate>
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
const TasquesExpedientGrid = (props: any) => {
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
            renderCell: (params: any) => <TascaComment
                entity={params?.row}
                readOnly={params?.row?.usuariActualOnlyObservador}
                onClose={apiRef?.current?.refresh}
            />
        },
    ]

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const { actions, components } = useTascaActions(entity, refresh);
    const { handleOpen, dialog } = useTascaDetail();

    return <>
        <StyledMuiGrid
            apiRef={apiRef}
            resourceName="expedientTascaResource"
            popupEditFormDialogResourceTitle={t('page.tasca.title')}
            columns={additionalColumns}
            paginationActive={false}
            autoHeight
            filter={builder.eq('expedient.id', entity?.id)}
            perspectives={perspectives}
            sortModel={sortModel}
            onRowCountChange={onRowCountChange}
            popupEditCreateActive
			toolbarCreateTitle={t('page.tasca.action.new.label')}
            popupEditFormContent={<TasquesGridForm/>}
            formAdditionalData={{
                expedient: {id: entity?.id},
                metaExpedient: entity?.metaExpedient,
            }}
            rowAdditionalActions={actions}
            toolbarHideCreate={!entity?.potModificar}

            onRowClick={(params: any) => handleOpen(params?.row?.id, params?.row) }
            popupEditFormI18nKeys={{
                createSuccess: 'page.tasca.action.new.ok',
            }}
        />
        {components}
        {dialog}
    </>
}

export default TasquesExpedientGrid;