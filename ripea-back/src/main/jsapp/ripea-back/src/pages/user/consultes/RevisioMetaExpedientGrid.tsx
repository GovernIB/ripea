import {useTranslation} from "react-i18next";
import {useMemo, useState} from "react";
import {GridPage, useMuiDataGridApiRef} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {Chip, Grid, Icon, Typography} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import {CommentDialog} from "../../CommentDialog.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import TabComponent from "../../../components/TabComponent.tsx";
import {MetaExpedientForm} from "../../metaExpedient/MetaExpedientGrid.tsx";
import {CanviEstatRevisioForm} from "../../metaExpedient/actions/CanviEstatRevisio.tsx";

// Form
const RevisioMetaExpedientForm = (props:any) => {
    const {revisor = false} = props;
    const {t} = useTranslation();

    const tabs = [
        {
            value: "dades",
            label: t('page.metaExpedient.tabs.dades'),
            content: <MetaExpedientForm/>,
        },
        {
            value: "estat",
            label: t('page.metaExpedient.tabs.estat'),
            content: <CanviEstatRevisioForm revisor={revisor}/>,
        },
    ]

    return <TabComponent tabs={tabs}/>
}

// Filter
const RevisioMetaExpedientFilterForm = () => {
    return <>
        <GridFormField xs={4} name="codi"/>
        <GridFormField xs={4} name="classificacio"/>
        <GridFormField xs={4} name="nom"/>
        <GridFormField xs={3} name="revisioEstat"/>
        <GridFormField xs={3} name="organGestor"/>
        <GridFormField xs={3} name="tipus"/>
        <Grid item xs={0.6}/>
    </>
}

const springFilterBuilder = (data:any) => {
    return builder.and(
        builder.like('codi', data?.codi),
        builder.like('classificacio', data?.classificacio),
        builder.like('nom', data?.nom),
        builder.eq('revisioEstat', `'${data?.revisioEstat}'`),
        builder.eq('organGestor.id', data?.organGestor?.id),
        data?.tipus && builder.eq('tipusProcedimentServei', `'${data?.tipus}'`),
    );
}

const RevisioMetaExpedientFilter = (props: any) => {
    const {onSpringFilterChange} = props;

    return <StyledMuiFilter
        resourceName={"metaExpedientResource"}
        code={"FILTER_REVISIO"}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
    >
        <RevisioMetaExpedientFilterForm/>
    </StyledMuiFilter>
}

// Grid
const labelStyle = {padding: '1px 4px', fontSize: '11px', fontWeight: '500', borderRadius: '2px', display: 'flex', alignItems: 'center', width: 'max-content'}
const obertStyle = {border: '1px dashed #AAA'}

export const StyledEstat = (props:any) => {
    const { entity, children } = props;

    let style: any = {};
    switch (entity?.revisioEstat) {
        case 'PENDENT':
            style = {backgroundColor: '#ffebae'}
            break;
        case 'REVISAT':
            style = {backgroundColor: '#c3e8d1'}
            break;
        case 'REBUTJAT':
            style = {backgroundColor: '#d99b9d', color: 'white'}
            break;
        case 'DISSENY':
            style = obertStyle
            break;
    }

    return <Typography variant="caption" sx={{...labelStyle, ...style}}>{children}</Typography>
}

const sortModel: any[] = [{field: 'lastModifiedDate', sort: 'desc'}]
const perspectives: any[] = ['AUDITORIA']
const namedQueries: any[] = ['CONSULTA_REVISIO_ESTAT']
const columns = [
    {
        field: 'codi',
        flex: 0.75,
    },
    {
        field: 'classificacio',
        flex: 0.75,
        renderCell: (params:any) => {
            const isProcediment = params?.row?.tipusProcedimentServei === 'PROCEDIMENT'
            return <>
                {params?.row?.tipusProcedimentServei &&
                    <Chip label={isProcediment ?'P' :'S'}
                          color={isProcediment ?"primary" :"success"}
                          sx={{mr: 1}}/>}
                {params?.formattedValue}
            </>
        }
    },
    {
        field: 'nom',
        flex: 1,
    },
    {
        field: 'serieDocumental',
        flex: 1,
    },
    {
        field: 'organGestor',
        flex: 1,
    },
    {
        field: 'procedimentComu',
        flex: 0.5,
        renderCell: (params:any) => (params?.row?.procedimentComu && <Icon>check</Icon>),
    },
    {
        field: 'gestioAmbGrupsActiva',
        flex: 0.5,
        renderCell: (params:any) => (params?.row?.gestioAmbGrupsActiva && <Icon>check</Icon>),
    },
    {
        field: 'actiu',
        flex: 0.5,
        renderCell: (params:any) => (params?.row?.actiu && <Icon>check</Icon>),
    },
    {
        field: 'revisioEstat',
        flex: 0.75,
        renderCell: (params:any) => <StyledEstat entity={params?.row}>{params.formattedValue}</StyledEstat>
    },
    {
        field: 'lastModifiedByFullName',
        flex: 1,
    },
    {
        field: 'lastModifiedDate',
        flex: 1,
        valueFormatter: (value: any) => formatDate(value),
    },
]

const RevisioMetaExpedientGrid = () => {
    const {t} = useTranslation();
    const {rol} = useUserSession();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const columnsAddition :any[] = useMemo(() => [
        ...columns,
        {
            field: 'numComentaris',
            headerName: '',
            sortable: false,
            flex: 0.25,
            renderCell: (params: any) => <CommentDialog
                entity={params?.row}
                title={`${t('page.comment.metaExpedient')}: ${params?.row?.nom}`}
                resourceName={'metaExpedientComentariResource'}
                resourceReference={'metaExpedient'}
                onClose={refresh}
            />,
        },
    ], [t])

    return <GridPage disableMargins>
        <CardPage title={t('page.user.menu.revisar')}>
            <RevisioMetaExpedientFilter onSpringFilterChange={setSpringFilter}/>
            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={"metaExpedientResource"}
                columns={columnsAddition}
                filter={springFilter}
                sortModel={sortModel}
                perspectives={perspectives}
                namedQueries={namedQueries}

                toolbarHideCreate
                rowHideUpdateButton={false}
                popupEditCreateActive
                popupEditFormContent={<RevisioMetaExpedientForm revisor={rol?.isRevisio}/>}
                popupEditFormDialogResourceTitle={t('page.metaExpedient.title')}
                popupEditFormI18nKeys={{
                    updateSuccess: 'page.metaExpedient.action.update.ok',
                }}
            />
        </CardPage>
    </GridPage>
}
export default RevisioMetaExpedientGrid;