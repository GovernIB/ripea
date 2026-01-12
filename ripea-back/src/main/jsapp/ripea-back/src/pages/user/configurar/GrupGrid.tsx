import {useTranslation} from "react-i18next";
import {useState} from "react";
import {GridPage} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {Grid, Icon, Badge, IconButton} from "@mui/material";
import { useNavigate } from "react-router-dom";
import GridFormField from "../../../components/GridFormField.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";

const GrupFilterForm = () => {
    return <>
        <GridFormField xs={3} name="codi"/>
        <GridFormField xs={3} name="descripcio"/>
        <GridFormField xs={3} name="organGestor"/>
        <Grid item xs={0.6}/>
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
        <GridFormField xs={12} name="codi"/>
        <GridFormField xs={12} name="descripcio"/>
        <GridFormField xs={12} name="organGestor"/>
    </Grid>
}

const sortModel: any = [{field: 'codi', sort: 'asc'}]

const perspectives = ["COUNT_PERMISOS"];

const GrupGrid = () => {

    const {t} = useTranslation();
    const navigate = useNavigate();
    const [springFilter, setSpringFilter] = useState<string>();

    const columns = [
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
            renderCell: (params:any) => <IconButton 
                aria-label="key" 
                color="inherit"
                title="Permisos"
                onClick={(e:any) => { e.stopPropagation(); navigate(`/grupPermis/${params?.row?.id}/permis`); }}
            >
                <Badge badgeContent={params?.row?.numPermisos} color="primary" showZero>
                    <Icon>key</Icon>
                </Badge>
            </IconButton>
        }
    ]

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
        },
    ]

    return <GridPage disableMargins>
        <CardPage title={t('page.user.menu.grups')}>
            <GrupFilter onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                resourceName={"grupResource"}
                popupEditUpdateActive
                popupEditFormDialogResourceTitle={t('page.grup.title')}
                popupEditFormContent={<GrupForm/>}
                columns={columns}
                filter={springFilter}
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