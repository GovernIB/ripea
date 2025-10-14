import {GridPage, useFormContext, useMuiDataGridApiRef} from "reactlib";
import { CardPage } from "../../../components/CardData";
import {useTranslation} from "react-i18next";
import TabComponent from "../../../components/TabComponent.tsx";
import {useState} from "react";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import * as builder from "../../../util/springFilterUtils.ts";
import GridFormField from "../../../components/GridFormField.tsx";
import {Grid} from "@mui/material";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";

// Expedient
const CustodiarExpedientsPendentsFilterFrom = () => {
    return <>
        <GridFormField xs={4} name="nom"/>
        <GridFormField xs={4} name="procediment"/>
        <Grid item xs={4}/>
        <GridFormField xs={4} name="dataCreacioInici" type={"date"}/>
        <GridFormField xs={4} name="dataCreacioFi" type={"date"}/>
        <Grid item xs={1.6}/>
    </>
}

const springExpedientFilterBuilder = (data: any) => {
    return builder.and(
        builder.like("nom", data?.nom),
        builder.eq("metaExpedient.id", data?.procediment?.id),
        builder.betweenDates("createdDate", data?.dataCreacioInici, data?.dataCreacioFi),
    );
}

const CustodiarExpedientsPendentsFilter = (props: any) => {
    const {onSpringFilterChange} = props;
    return <StyledMuiFilter
        resourceName={"expedientResource"}
        code={"MASSIVE_CUSTODIAR_FILTER"}
        sessionKey={"MASSIVE_CUSTODIAR_EXPEDIENT_FILTER"}
        springFilterBuilder={springExpedientFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
        filterOnFieldEnterKeyPressed
    >
        <CustodiarExpedientsPendentsFilterFrom/>
    </StyledMuiFilter>
}

const expedientSortModel:any = [{field: 'createdDate', sort: 'desc'}]
const expedientColumns = [
    {
        field: 'nom',
        flex: 1,
        renderCell: (params:any) => <a href={`/contingut/${params?.id}`}>{params?.formattedValue}</a>,
    },
    {
        field: 'metaExpedient',
        flex: 1.5,
    },
    {
        field: 'createdDate',
        flex: 1,
    },
    {
        field: 'arxiuIntentData',
        flex: 1,
        valueFormatter: (value: any) => formatDate(value),
    },
]

const CustodiarExpedientsPendentsGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const actions = [
        {
            label: t('page.contingut.action.custodiar.label'),
            icon: "autorenew",
            showInMenu: false,
        },
    ]
    // TODO: crear acción massiva
    const massiveActions = [
        {
            label: t('page.contingut.action.custodiar.label'),
            icon: "autorenew",
            showInMenu: false,
        },
    ]

    return <GridPage disableMargins>
        <CustodiarExpedientsPendentsFilter onSpringFilterChange={setSpringFilter}/>

        <StyledMuiGrid
            apiRef={apiRef}
            resourceName={"expedientResource"}
            columns={expedientColumns}
            filter={springFilter}
            // TODO: filtrar pot custodiar
            sortModel={expedientSortModel}

            rowAdditionalActions={actions}
            toolbarMassiveActions={massiveActions}

            toolbarHideCreate
        />
    </GridPage>
}

// Document
const CustodiarDocumentsPendentsFilterFrom = () => {
    const {data} = useFormContext();

    const expedientFilter = builder.and(builder.eq('metaExpedient.id', data?.procediment?.id));

    return <>
        <GridFormField xs={4} name="nom"/>
        <GridFormField xs={4} name="procediment"/>
        <GridFormField xs={4} name="expedient" filter={expedientFilter}/>
        <GridFormField xs={4} name="dataCreacioInici" type={"date"}/>
        <GridFormField xs={4} name="dataCreacioFi" type={"date"}/>
        <Grid item xs={1.6}/>
    </>
}

const springDocumentFilterBuilder = (data: any) => {
    return builder.and(
        builder.like("nom", data?.nom),
        builder.eq("expedient.metaExpedient.id", data?.procediment?.id),
        builder.eq("expedient.id", data?.expedient?.id),
        builder.betweenDates("createdDate", data?.dataCreacioInici, data?.dataCreacioFi),
    );
}

const CustodiarDocumentsPendentsFilter = (props: any) => {
    const {onSpringFilterChange} = props;
    return <StyledMuiFilter
        resourceName={"expedientResource"}
        code={"MASSIVE_CUSTODIAR_FILTER"}
        sessionKey={"MASSIVE_CUSTODIAR_DOCUMENT_FILTER"}
        springFilterBuilder={springDocumentFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
        filterOnFieldEnterKeyPressed
    >
        <CustodiarDocumentsPendentsFilterFrom/>
    </StyledMuiFilter>
}

const documentSortModel:any = [{field: 'createdDate', sort: 'desc'}]
const documentColumns = [
    {
        field: 'nom',
        flex: 1,
    },
    {
        field: 'expedient',
        flex: 1.5,
        renderCell: (params:any) => <a href={`/contingut/${params?.expedient?.id}`}>{params?.formattedValue}</a>,
    },
    // {
    //     field: 'metaNode',
    //     flex: 1,
    // },
    {
        field: 'createdDate',
        flex: 1,
    },
    {
        field: 'arxiuIntentData',
        flex: 1,
        valueFormatter: (value: any) => formatDate(value),
    },
]

const CustodiarDocumentsPendentsGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const actions = [
        {
            label: t('page.contingut.action.custodiar.label'),
            icon: "autorenew",
            showInMenu: false,
        },
    ]
    // TODO: crear acción massiva
    const massiveActions = [
        {
            label: t('page.contingut.action.custodiar.label'),
            icon: "autorenew",
            showInMenu: false,
        },
    ]

    return <GridPage disableMargins>
        <CustodiarDocumentsPendentsFilter onSpringFilterChange={setSpringFilter}/>

        <StyledMuiGrid
            apiRef={apiRef}
            resourceName={"documentResource"}
            columns={documentColumns}
            filter={springFilter}
            // TODO: filtrar pot custodiar
            sortModel={documentSortModel}

            rowAdditionalActions={actions}
            toolbarMassiveActions={massiveActions}

            toolbarHideCreate
        />
    </GridPage>
}

// Interessat
const CustodiarInteressatsPendentsFilterFrom = () => {
    const {data} = useFormContext();

    const expedientFilter = builder.and(builder.eq('metaExpedient.id', data?.procediment?.id));

    return <>
        <GridFormField xs={4} name="nom"/>
        <GridFormField xs={4} name="procediment"/>
        <GridFormField xs={4} name="expedient" filter={expedientFilter}/>
        <GridFormField xs={4} name="dataCreacioInici" type={"date"}/>
        <GridFormField xs={4} name="dataCreacioFi" type={"date"}/>
        <Grid item xs={1.6}/>
    </>
}

const springInteressatFilterBuilder = (data: any) => {
    return builder.and(
        builder.like("nom", data?.nom),
        builder.eq("metaExpedient.id", data?.procediment?.id),
        builder.eq("id", data?.expedient?.id),
        builder.betweenDates("createdDate", data?.dataCreacioInici, data?.dataCreacioFi),
    );
}

const CustodiarInteressatsPendentsFilter = (props: any) => {
    const {onSpringFilterChange} = props;
    return <StyledMuiFilter
        resourceName={"expedientResource"}
        code={"MASSIVE_CUSTODIAR_FILTER"}
        sessionKey={"MASSIVE_CUSTODIAR_INTERESSAT_FILTER"}
        springFilterBuilder={springInteressatFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
        filterOnFieldEnterKeyPressed
    >
        <CustodiarInteressatsPendentsFilterFrom/>
    </StyledMuiFilter>
}

const interessatSortModel:any = [{field: 'createdDate', sort: 'desc'}]
const interessatColumns = [
    {
        field: 'nom',
        flex: 1,
    },
    {
        field: 'expedient',
        flex: 1.5,
        renderCell: (params:any) => <a href={`/contingut/${params?.expedient?.id}`}>{params?.formattedValue}</a>,
    },
    // {
    //     field: 'metaNode',
    //     flex: 1,
    // },
    {
        field: 'createdDate',
        flex: 1,
    },
    {
        field: 'arxiuIntentData',
        flex: 1,
        valueFormatter: (value: any) => formatDate(value),
    },
]

const CustodiarInteressatsPendentsGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const actions = [
        {
            label: t('page.contingut.action.custodiar.label'),
            icon: "autorenew",
            showInMenu: false,
        },
    ]
    // TODO: crear acción massiva
    const massiveActions = [
        {
            label: t('page.contingut.action.custodiar.label'),
            icon: "autorenew",
            showInMenu: false,
        },
    ]

    return <GridPage disableMargins>
        <CustodiarInteressatsPendentsFilter onSpringFilterChange={setSpringFilter}/>

        <StyledMuiGrid
            apiRef={apiRef}
            resourceName={"interessatResource"}
            columns={interessatColumns}
            filter={springFilter}
            // TODO: filtrar pot custodiar
            sortModel={interessatSortModel}

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