import {useTranslation} from "react-i18next";
import {useMemo, useState} from "react";
import {GridPage, useMuiDataGridApiRef} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {Chip, Icon} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import {CommentDialog} from "../../CommentDialog.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import TabComponent from "../../../components/TabComponent.tsx";
import {MetaExpedientForm} from "../../metaExpedient/MetaExpedientGrid.tsx";
import {CanviEstatRevisioForm} from "../../metaExpedient/actions/CanviEstatRevisio.tsx";
import {StyledLabel} from "../../../components/StyledLabel.tsx";

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
        <GridFormField size={{xs: 12, sm: 6, md: 4}} name="codi"/>
        <GridFormField size={{xs: 12, sm: 6, md: 4}} name="classificacio"/>
        <GridFormField size={{xs: 12, sm: 6, md: 4}} name="nom"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="revisioEstat"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="organGestor"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="tipus"/>
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
export const StyledEstat = (props:any) => {
    const { entity, children } = props;

    switch (entity?.revisioEstat) {
        case 'PENDENT':
            return <StyledLabel backgroundColor={'#ffebae'}>{children}</StyledLabel>
        case 'REVISAT':
                return <StyledLabel backgroundColor={'#c3e8d1'}>{children}</StyledLabel>
        case 'REBUTJAT':
            return <StyledLabel backgroundColor={'#d99b9d'}>{children}</StyledLabel>
        case 'DISSENY':
            return <StyledLabel dashed>{children}</StyledLabel>
    }

    return <>{children}</>
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
                          size={'small'}
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

    return <GridPage>
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
                popupEditFormContent={<RevisioMetaExpedientForm revisor={rol?.isRevisor}/>}
                popupEditFormDialogResourceTitle={t('page.metaExpedient.title')}
                popupEditFormI18nKeys={{
                    updateSuccess: 'page.metaExpedient.action.update.ok',
                }}
            />
        </CardPage>
    </GridPage>
}
export default RevisioMetaExpedientGrid;