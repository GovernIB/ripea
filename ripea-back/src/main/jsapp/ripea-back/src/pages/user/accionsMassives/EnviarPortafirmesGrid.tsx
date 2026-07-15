import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {GridPage, useFormContext, useMuiDataGridApiRef} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import {Alert} from "@mui/material";
import ContingutLink from "../../../components/ContingutLink.tsx";
import {useTranslation} from "react-i18next";
import {useMemo, useState} from "react";
import {useEnviarPortafirmes, useEnviarPortafirmesMassive} from "../../contingut/actions/EnviarPortafirmes.tsx";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";
import GridFormField from "../../../components/GridFormField.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import {useSession} from "../../../components/SessionStorageContext.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import {GridSortDirection} from "@mui/x-data-grid-pro";
import {useExecucioMassivaContingut} from "../actions/ExecucioMassivaGrid.tsx";

const EnviarPortafirmesFilterForm = () => {
    const {data} = useFormContext();
    const { value: user } = useUserSession();

    const expedientFilter = builder.and(
        builder.eq('metaExpedient.id', data?.procediment?.id),
        builder.eq('grup.id', data?.grup?.id)
    );
    const metaDocumentFilter = builder.eq('metaExpedient.id', data?.procediment?.id || 0);

    return <>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="procediment" />
        <GridFormField name="grup" size={{xs: 12, sm: 6, md: 3}}
                       namedQueries={[`BY_PROCEDIMENT#${data?.procediment?.id}`]}
                       disabled={!data?.procediment}
                       hidden={!user?.sessionScope?.isFiltreGrupsVisible}/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="expedient" filter={expedientFilter} disabled={!data.procediment}/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="metaDocument" filter={metaDocumentFilter} disabled={!data.procediment}/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="nom"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="dataCreacioInici" type={"date"}/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="dataCreacioFi" type={"date"}/>
    </>
}

const springFilterBuilder = (data: any) => {
    return builder.and(
        builder.like("nom", data?.nom),
        builder.eq("expedient.metaExpedient.id", data?.procediment?.id),
        builder.eq("expedient.grup.id", data?.grup?.id),
        builder.eq("expedient.id", data?.expedient?.id),
        builder.eq("metaNode.id", data?.metaDocument?.id),
        builder.betweenDates("createdDate", data?.dataCreacioInici, data?.dataCreacioFi)
    );
}

export const EnviarPortafirmesFilter = (props: any) => {
    const {sessionKey, onSpringFilterChange} = props;
    return <StyledMuiFilter
        resourceName={"documentResource"}
        code={"MASSIVE_PORTAFIRMES_FILTER"}
        sessionKey={sessionKey}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
        filterOnFieldEnterKeyPressed
    >
        <EnviarPortafirmesFilterForm/>
    </StyledMuiFilter>
}

const namedQueries: string[] = ['MASSIU_PORTAFIRMES']
const perspectives: string[] = ['EN_PROCES_PORTAFIB', 'RESUM']
const sortModel: any = [{field: 'createdDate', sort: 'desc'}]
const columns = [
    {
        field: 'nom',
        flex: 0.8,
    },
    {
        field: 'metaDocument',
        flex: 0.6,
    },
    {
        field: 'expedient',
        flex: 0.6,
        renderCell: (params:any) => <ContingutLink id={params?.row?.expedient?.id}>{params?.formattedValue}</ContingutLink>,
    },
    {
        field: 'createdDate',
        flex: 0.4,
    },
    {
        field: 'createdByFullName',
        flex: 0.6,
        sortProcessor: (_field: string, sort: GridSortDirection) => [ { field: "createdBy", sort } ]
    },
]

const EnviarPortafirmesGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const sessionKey = "MASSIVE_PORTAFIRMES_FILTER";
    const { value: filterData } = useSession(sessionKey);
    const haveRequirements = useMemo(() =>
        !!filterData?.procediment && !!filterData?.metaDocument,
        [filterData?.procediment, filterData?.metaDocument])

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {handleShow: handleEviarPortafirmesShow, content: contentEviarPortafirmes} = useEnviarPortafirmes(refresh);
    const {handleShow: handleEviarMassiveShow, content: contentEviarMassive} = useEnviarPortafirmesMassive(refresh);
    const {handleOpen: handleContingutOpen, dialog: dialogContingut} = useExecucioMassivaContingut();

    const actions:any[] = [
        {
            label: t('page.document.action.portafirmes.label'),
            icon: "mail",
            showInMenu: false,
            onClick: handleEviarPortafirmesShow,
            hidden: (row:any) => row?.execucioMassivaPortafibId
        },
        {
            label: t('page.user.action.massives.pending'),
            icon: <div style={{ color: 'orange' }}>schedule</div>,
            showInMenu: false,
            onClick: (_id:any, row:any) => handleContingutOpen(row?.execucioMassivaPortafibId),
            hidden: (row:any) => !row?.execucioMassivaPortafibId
        },
    ]
    const massiveActions = [
        {
            label: t('page.document.action.portafirmes.label'),
            icon: "mail",
            showInMenu: false,
            onClick: handleEviarMassiveShow,
        },
    ]

    return <GridPage autoHeight>
        <CardPage title={t('navigate.massiu.portafirmes')}>
            {!haveRequirements &&
                <Alert severity={'info'} sx={{mb: 1}}>{t('page.document.alert.portafirmes')}</Alert>}

            <EnviarPortafirmesFilter
                sessionKey={sessionKey}
                onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={"documentResource"}
                persistentStateKey={"documentResource_massEnviarPortafirmes"}
                columns={columns}
                filter={springFilter}
                perspectives={perspectives}
                namedQueries={namedQueries}
                sortModel={sortModel}
                rowAdditionalActions={actions}
                toolbarMassiveActions={massiveActions}
                isRowSelectable={(params:any) => !params?.row?.execucioMassivaPortafibId
                    && haveRequirements}
                disabledMassiveDefSelector={!haveRequirements}
                toolbarHideCreate
            />
        </CardPage>
        {contentEviarPortafirmes}
        {contentEviarMassive}
        {dialogContingut}
    </GridPage>
}
export default EnviarPortafirmesGrid;