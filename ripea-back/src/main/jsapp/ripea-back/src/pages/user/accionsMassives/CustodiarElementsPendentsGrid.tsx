import {GridPage, useFormContext, useMuiDataGridApiRef} from "reactlib";
import { CardPage } from "../../../components/CardData.tsx";
import {useTranslation} from "react-i18next";
import TabComponent from "../../../components/TabComponent.tsx";
import {useState} from "react";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import * as builder from "../../../util/springFilterUtils.ts";
import GridFormField from "../../../components/GridFormField.tsx";
import {Grid, Link} from "@mui/material";
import {Link as RouterLink } from 'react-router-dom';
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";
import {useActions as useDocumentActions} from "../../contingut/details/ContingutActions.tsx";
import {useMassiveActions as useDocumentMassiveActions} from "../../contingut/details/ContingutMassiveActions.tsx";
import {useActions as useExpedientActions} from "../../expedient/details/CommonActions.tsx";
import {useMassiveActions as useExpedientMassiveActions} from "../../expedient/details/ExpedientMassiveActions.tsx";

import {useActions as useInteressatActions, useMassiveActions as useInteressatMassiveActions} from "../../interessats/details/InteressatActions.tsx";

const sortModel:any = [{field: 'createdDate', sort: 'desc'}]
const CustodiarPendentsFilterFrom = (props:any) => {
    const { filtrarExpedient = false } = props
    const {data} = useFormContext();

    const expedientFilter = builder.and(builder.eq('metaExpedient.id', data?.procediment?.id));

    return <>
        <GridFormField xs={4} name="nom"/>
        <GridFormField xs={4} name="procediment"/>
        {filtrarExpedient
            ?<GridFormField xs={4} name="expedient" filter={expedientFilter}/>
            :<Grid item xs={4}/>
        }
        <GridFormField xs={4} name="dataCreacioInici" type={"date"}/>
        <GridFormField xs={4} name="dataCreacioFi" type={"date"}/>
    </>
}

const CustodiarPendentsFilter = (props: any) => {
    const {onSpringFilterChange, sessionKey, springFilterBuilder, filtrarExpedient = false} = props;
    return <StyledMuiFilter
        resourceName={"expedientResource"}
        code={"MASSIVE_CUSTODIAR_FILTER"}
        sessionKey={sessionKey}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
        filterOnFieldEnterKeyPressed
    >
        <CustodiarPendentsFilterFrom filtrarExpedient={filtrarExpedient}/>
    </StyledMuiFilter>
}

// Expedient
const springExpedientFilterBuilder = (data: any) => {
    return builder.and(
        builder.like("nom", data?.nom),
        builder.eq("metaExpedient.id", data?.procediment?.id),
        builder.betweenDates("createdDate", data?.dataCreacioInici, data?.dataCreacioFi),
    );
}

const namedQueriesExpedient: string[] = ['MASSIVE_ACTION_QUERY', 'MASSIVE_ARXIU_PENDENT']
const expedientColumns = [
    {
        field: 'nom',
        flex: 1.5,
        renderCell: (params:any) => <Link component={RouterLink} to={`/contingut/${params?.id}`}>{params?.formattedValue}</Link>,
    },
    {
        field: 'metaExpedient',
        flex: 1.5,
    },
    {
        field: 'createdDate',
        flex: 0.8,
    },
    {
        field: 'arxiuIntentData',
        flex: 0.8,
        valueFormatter: (value: any) => formatDate(value),
    },
]

const CustodiarExpedientsPendentsGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }
    const {guardarArxiu} = useExpedientActions(refresh);
    const {guardarArxiu: guardarArxiuMassive} = useExpedientMassiveActions(refresh);

    const actions = [
        {
            label: t('page.contingut.action.custodiar.label'),
            icon: "autorenew",
            showInMenu: false,
            onClick: guardarArxiu,
        },
    ]
    const massiveActions = [
        {
            label: t('page.contingut.action.custodiar.label'),
            icon: "autorenew",
            showInMenu: false,
            onClick: guardarArxiuMassive,
        },
    ]

    return <GridPage disableMargins>
        <CustodiarPendentsFilter
            sessionKey={"MASSIVE_CUSTODIAR_EXPEDIENT_FILTER"}
            springFilterBuilder={springExpedientFilterBuilder}
            onSpringFilterChange={setSpringFilter}/>

        <StyledMuiGrid
            apiRef={apiRef}
            resourceName={"expedientResource"}
            columns={expedientColumns}
            filter={springFilter}
            sortModel={sortModel}
            namedQueries={namedQueriesExpedient}
            rowAdditionalActions={actions}
            toolbarMassiveActions={massiveActions}
            toolbarHideCreate
        />
    </GridPage>
}

// Document
const namedQueriesDocument: string[] = ['MASSIU_PENDENT_ARXIU']
const perspectivesDocument = ['PROCEDIMENT'];
const springDocumentFilterBuilder = (data: any) => {
    return builder.and(
        builder.like("nom", data?.nom),
        builder.eq("expedient.metaExpedient.id", data?.procediment?.id),
        builder.eq("expedient.id", data?.expedient?.id),
        builder.betweenDates("createdDate", data?.dataCreacioInici, data?.dataCreacioFi),
    );
}

const documentColumns = [
    {
        field: 'nom',
        flex: 1,
    },
    {
        field: 'expedient',
        flex: 1.5,
        renderCell: (params:any) => <Link component={RouterLink} to={`/contingut/${params?.row?.expedient?.id}`}>{params?.formattedValue}</Link>,
    },
    {
        field: 'metaExpedient',
        flex: 1.5,
    },
    {
        field: 'createdDate',
        flex: 0.8,
    },
    {
        field: 'arxiuIntentData',
        flex: 0.8,
        valueFormatter: (value: any) => formatDate(value),
    },
]

const CustodiarDocumentsPendentsGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {guardarArxiu} = useDocumentActions(refresh)
    const {guardarArxiu: guardarArxiuMassive} = useDocumentMassiveActions(refresh)
    const actions = [
        {
            label: t('page.contingut.action.custodiar.label'),
            icon: "autorenew",
            showInMenu: false,
            onClick: guardarArxiu,
        },
    ]
    const massiveActions = [
        {
            label: t('page.contingut.action.custodiar.label'),
            icon: "autorenew",
            showInMenu: false,
            onClick: guardarArxiuMassive,
        },
    ]

    return <GridPage disableMargins>
        <CustodiarPendentsFilter
            sessionKey={"MASSIVE_CUSTODIAR_DOCUMENT_FILTER"}
            springFilterBuilder={springDocumentFilterBuilder}
            onSpringFilterChange={setSpringFilter}
            filtrarExpedient/>

        <StyledMuiGrid
            apiRef={apiRef}
            resourceName={"documentResource"}
            columns={documentColumns}
            filter={springFilter}
            sortModel={sortModel}
            namedQueries={namedQueriesDocument}
            perspectives={perspectivesDocument}
            rowAdditionalActions={actions}
            toolbarMassiveActions={massiveActions}
            toolbarHideCreate
        />
    </GridPage>
}

// Interessat
const namedQueriesInteressat: string[] = ['MASSIU_PENDENT_ARXIU']
const perspectivesInteressat = ['PROCEDIMENT'];
const springInteressatFilterBuilder = (data: any) => {
    return builder.and(
        builder.like("nom", data?.nom),
        builder.eq("expedient.metaExpedient.id", data?.procediment?.id),
        builder.eq("id", data?.expedient?.id),
        builder.betweenDates("createdDate", data?.dataCreacioInici, data?.dataCreacioFi),
    );
}

const interessatColumns = [
    {
        field: 'nom',
        flex: 1.5,
    },
    {
        field: 'expedient',
        flex: 1.5,
        renderCell: (params:any) => <Link component={RouterLink} to={`/contingut/${params?.row?.expedient?.id}`}>{params?.formattedValue}</Link>,
    },
    {
        field: 'metaExpedient',
        flex: 1,
    },
    {
        field: 'createdDate',
        flex: 0.8,
    },
    {
        field: 'arxiuIntentData',
        flex: 0.8,
        valueFormatter: (value: any) => formatDate(value),
    },
]

const CustodiarInteressatsPendentsGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }
    const {guardarArxiu} = useInteressatActions(refresh)
    const {guardarArxiu: guardarArxiuMassive} = useInteressatMassiveActions(refresh)

    const actions = [
        {
            label: t('page.contingut.action.custodiar.label'),
            icon: "autorenew",
            showInMenu: false,
            onClick: guardarArxiu,
        },
    ]
    const massiveActions = [
        {
            label: t('page.contingut.action.custodiar.label'),
            icon: "autorenew",
            showInMenu: false,
            onClick: guardarArxiuMassive,
        },
    ]

    return <GridPage disableMargins>
        <CustodiarPendentsFilter
            sessionKey={"MASSIVE_CUSTODIAR_INTERESSAT_FILTER"}
            springFilterBuilder={springInteressatFilterBuilder}
            onSpringFilterChange={setSpringFilter}
            filtrarExpedient/>

        <StyledMuiGrid
            apiRef={apiRef}
            resourceName={"interessatResource"}
            columns={interessatColumns}
            filter={springFilter}
            sortModel={sortModel}
            perspectives={perspectivesInteressat}
            namedQueries={namedQueriesInteressat}
            rowAdditionalActions={actions}
            toolbarMassiveActions={massiveActions}
            toolbarHideCreate
        />
    </GridPage>
}

const CustodiarElementsPendentsGrid = () => {
    const {t} = useTranslation();

    const tabs = [
        {
            value: "expedient",
            label: t('page.expedient.title'),
            content: <CustodiarExpedientsPendentsGrid/>,
        },
        {
            value: "document",
            label: t('page.document.title'),
            content: <CustodiarDocumentsPendentsGrid/>,
        },
        {
            value: "interessat",
            label: t('page.interessat.title'),
            content: <CustodiarInteressatsPendentsGrid/>,
        },
    ]

    return <GridPage disableMargins>
        <CardPage title={t('navigate.massiu.seguimentArxiuPendents')}>
            <TabComponent
                tabs={tabs}
                variant="scrollable"
            />
        </CardPage>
    </GridPage>
}
export default CustodiarElementsPendentsGrid;