import {useTranslation} from "react-i18next";
import {useState} from "react";
import {GridPage, useBaseAppContext, useMuiDataGridApiRef, useResourceApiService} from "reactlib";
import {useConfirmDialogButtons} from "@src/util/buttonsOverride.tsx";
import {CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {Grid, Icon, Badge} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";
import LinkIcon from "../../../components/LinkIcon.tsx";

const GrupFilterForm = () => {
    return <>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="codi"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="descripcio"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="organGestor"/>
    </>
}

const springFilterBuilder = (data:any) => {
    return builder.and(
        builder.like('codi', data?.codi),
        builder.like('descripcio', data?.descripcio),
        builder.eq('organGestor.id', data?.organGestor?.id),
    );
}

const GrupFilter = (props: any) => {
    const {onSpringFilterChange} = props;

    return <StyledMuiFilter
        resourceName={"grupResource"}
        code={"FILTER"}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
    >
        <GrupFilterForm/>
    </StyledMuiFilter>
}

// Grid
const GrupForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="codi"/>
        <GridFormField name="descripcio" type={'textarea'}/>
        <GridFormField name="organGestor"/>
    </Grid>
}

const columns:any[] = [
    {
        field: 'codi',
        flex: 0.5,
    },
    {
        field: 'descripcio',
        flex: 1,
    },
    {
        field: 'organGestor',
        flex: 1,
    },
    {
        field: 'permis',
        headerName: '',
        sortable: false,
        flex: 0.25,
        renderCell: (params:any) => <LinkIcon
            aria-label="key"
            color="inherit"
            title="Permisos"
            to={`/grupPermis/${params?.row?.id}/permis`}
        >
            <Badge badgeContent={params?.row?.numPermisos} color="primary" showZero>
                <Icon>key</Icon>
            </Badge>
        </LinkIcon>
    }
]

const sortModel: any = [{field: 'codi', sort: 'asc'}]
const perspectives = ["COUNT_PERMISOS"];

const GrupGrid = () => {
    const {t} = useTranslation();
    const [springFilter, setSpringFilter] = useState<string>();
    const [seleccio, setSeleccio] = useState<any[]>([]);
    const gridApiRef = useMuiDataGridApiRef();
    const {delete: apiDelete} = useResourceApiService('grupResource');
    const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons().reverse();

    // Esborra els grups seleccionats d'un en un (com l'esborrat massiu del JSP) i
    // informa de quants s'han esborrat i quants han fallat.
    const esborrarMassiu = (ids: any[]) => {
        messageDialogShow(
            t('page.grup.action.delete.massive.check'),
            t('page.grup.action.delete.massive.description', {num: ids.length}),
            confirmDialogButtons,
            {maxWidth: 'sm', fullWidth: true})
            .then(async (value: any) => {
                if (!value) {
                    return;
                }
                const errors: any[] = [];
                for (const id of ids) {
                    await apiDelete(id).catch((error: any) => errors.push(error));
                }
                const correctes = ids.length - errors.length;
                setSeleccio([]);
                gridApiRef?.current?.refresh?.();
                if (correctes > 0) {
                    temporalMessageShow(null, t('page.grup.action.delete.massive.ok', {num: correctes}), 'success');
                }
                if (errors.length > 0) {
                    temporalMessageShow(null,
                        t('page.grup.action.delete.massive.error', {num: errors.length}) + ': ' + (errors[0]?.message ?? ''),
                        'error');
                }
            });
    }

    const actions = [
        {
            label: t('common.update'),
            icon: "edit",
            showInMenu: true,
            clickShowUpdateDialog: true,
        },
        {
            label: t('common.delete'),
            icon: "delete",
            showInMenu: true,
            clickTriggerDelete: true,
        },
    ]
    const massiveActions = [
        {
            label: t('common.delete'),
            icon: "delete",
            showInMenu: false,
            onClick: esborrarMassiu,
        },
    ]

    return <GridPage autoHeight>
        <CardPage title={t('page.user.menu.grups')}>
            <GrupFilter onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                apiRef={gridApiRef}
                resourceName={"grupResource"}
                rowSelectionModel={seleccio}
                onRowSelectionModelChange={setSeleccio}
                popupEditUpdateActive
                popupEditFormDialogResourceTitle={t('page.grup.title')}
                popupEditFormContent={<GrupForm/>}
                columns={columns}
                filter={springFilter}
                toolbarShowFilterCount
                sortModel={sortModel}
                perspectives={perspectives}
                rowAdditionalActions={actions}
                toolbarMassiveActions={massiveActions}
                toolbarCreateTitle={t('page.grup.action.new.label')}
                popupEditFormI18nKeys={{
                    createSuccess: 'page.grup.action.new.ok',
                    updateSuccess: 'page.grup.action.update.ok',
                    deleteSuccess: 'page.grup.action.delete.ok',
                }}
            />
        </CardPage>
    </GridPage>
}
export default GrupGrid;