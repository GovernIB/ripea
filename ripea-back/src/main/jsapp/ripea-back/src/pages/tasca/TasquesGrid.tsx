import {
    GridPage,
    MuiGrid,
    useMuiDataGridApiRef,
} from 'reactlib';
import { formatDate } from "../../util/dateUtils.ts";
import { Grid } from "@mui/material";
import GridFormField from "../../components/GridFormField.tsx";
import React from "react";
import * as builder from "../../util/springFilterUtils.ts";
import { TascaCommentDialog as CommentDialog } from "../CommentDialog.tsx";
import useTascaActions from "./details/TascaActions.tsx";
import {useTranslation} from "react-i18next";

const TasquesGridForm = (props: any) => {
    const { expedient } = props;

    const metaTascaFilter: string = builder.and(
        builder.eq("metaExpedient.id", expedient?.metaExpedient?.id),
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

const TasquesGrid: React.FC = (props: any) => {
    const { id, entity, onRowCountChange } = props;
    const apiRef = useMuiDataGridApiRef()
    const { t } = useTranslation();

    const columns = [
        {
            field: 'metaExpedientTasca',
            flex: 0.5,
            valueFormatter: (value: any) => {
                return value?.description;
            }
        },
        {
            field: 'dataInici',
            flex: 0.5,
            valueFormatter: (value: any) => {
                return formatDate(value);
            }
        },
        {
            field: 'dataLimit',
            flex: 0.5,
            valueFormatter: (value: any) => {
                return formatDate(value, "DD/MM/Y");
            }
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
            valueFormatter: (value: any) => {
                return value?.description;
            }
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
            renderCell: (params: any) => {
                return <CommentDialog entity={params?.row} />;
            }
        },
    ];
    const { actions, components } = useTascaActions(apiRef?.current?.refresh);

    return <GridPage>
        <MuiGrid
            apiRef={apiRef}
            resourceName="expedientTascaResource"
            popupEditFormDialogResourceTitle={t('page.tasca.title')}
            columns={columns}
            paginationActive
            filter={`expedient.id:${id}`}
            titleDisabled
            perspectives={["RESPONSABLES_RESUM"]}
            onRowsChange={(rows) => onRowCountChange?.(rows.length)}
            popupEditCreateActive
            popupEditFormContent={<TasquesGridForm expedient={entity} />}
            formAdditionalData={{
                expedient: {
                    id: id
                },
            }}
            rowAdditionalActions={actions}
            disableColumnMenu
            rowHideUpdateButton
        // rowHideDeleteButton
        />
        {components}
    </GridPage>
}

export default TasquesGrid;