import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {GridPage, useFormContext, useMuiDataGridApiRef} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import {Alert, Grid, Link} from "@mui/material";
import {Link as RouterLink } from 'react-router-dom';
import {useTranslation} from "react-i18next";
import {useMemo, useState} from "react";
import {useEnviarPortafirmes, useEnviarPortafirmesMassive} from "../../contingut/actions/EnviarPortafirmes.tsx";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";
import GridFormField from "../../../components/GridFormField.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import {useSession} from "../../../components/SessionStorageContext.tsx";
import {GridSortDirection} from "@mui/x-data-grid-pro";
import {useGridApiRef as useMuiDatagridApiRef} from "@mui/x-data-grid-pro/hooks/utils/useGridApiRef";

const EnviarPortafirmesFilterForm = () => {
    const {data} = useFormContext();

    const expedientFilter = builder.and(builder.eq('metaExpedient.id', data?.procediment?.id));
    const metaDocumentFilter = builder.eq('metaExpedient.id', data?.procediment?.id || 0);

    return <>
        <GridFormField xs={3} name="procediment"/>
        <GridFormField xs={3} name="expedient" filter={expedientFilter}/>
        <GridFormField xs={3} name="metaDocument" filter={metaDocumentFilter}/>
        <GridFormField xs={3} name="nom"/>
        <GridFormField xs={3} name="dataCreacioInici" type={"date"}/>
        <GridFormField xs={3} name="dataCreacioFi" type={"date"}/>
        <Grid item xs={3.6}/>
    </>
}

const springFilterBuilder = (data: any) => {
    return builder.and(
        builder.like("nom", data?.nom),
        builder.eq("expedient.metaExpedient.id", data?.procediment?.id),
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
        renderCell: (params:any) => <Link component={RouterLink} to={`/contingut/${params?.row?.expedient?.id}`}>{params?.formattedValue}</Link>,
    },
    {
        field: 'createdDate',
        flex: 0.4,
    },
    {
        field: 'createdByFullName',
        flex: 0.6,
        sortProcessor: (field: string, sort: GridSortDirection) => [ { field: "createdBy", sort } ]
    },
]

const EnviarPortafirmesGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const dataApiRef = useMuiDatagridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const sessionKey = "MASSIVE_PORTAFIRMES_FILTER";
    const { value: filterData } = useSession(sessionKey);
    const haveRequirements = useMemo(() => {
        dataApiRef?.current?.setRowSelectionModel?.([])
        return !!filterData?.procediment && !!filterData?.metaDocument
    }, [filterData?.procediment, filterData?.metaDocument])

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {handleShow: handleEviarPortafirmesShow, content: contentEviarPortafirmes} = useEnviarPortafirmes(refresh);
    const {handleShow: handleEviarMassiveShow, content: contentEviarMassive} = useEnviarPortafirmesMassive(refresh);

    const actions = [
        {
            label: t('page.document.action.portafirmes.label'),
            icon: "mail",
            showInMenu: false,
            onClick: handleEviarPortafirmesShow,
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

    return <GridPage disableMargins>
        <CardPage title={t('navigate.massiu.portafirmes')}>
            <Alert severity={'info'} sx={{mb: 1}}>{t('page.document.alert.portafirmes')}</Alert>

            <EnviarPortafirmesFilter
                sessionKey={sessionKey}
                onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                apiRef={apiRef}
                datagridApiRef={dataApiRef}
                resourceName={"documentResource"}
                columns={columns}
                filter={springFilter}
                namedQueries={namedQueries}
                sortModel={sortModel}
                rowAdditionalActions={actions}
                toolbarMassiveActions={massiveActions}
                isRowSelectable={() => haveRequirements}
                disabledMassiveDefSelector={!haveRequirements}
                toolbarHideCreate
            />
        </CardPage>
        {contentEviarPortafirmes}
        {contentEviarMassive}
    </GridPage>
}
export default EnviarPortafirmesGrid;