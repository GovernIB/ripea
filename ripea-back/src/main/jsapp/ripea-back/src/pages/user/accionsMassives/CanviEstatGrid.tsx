import {useTranslation} from "react-i18next";
import {GridPage, useFormContext, useMuiDataGridApiRef} from "reactlib";
import {useMemo, useState} from "react";
import { CardPage } from "../../../components/CardData.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {Alert, Box, Link} from "@mui/material";
import {Link as RouterLink } from 'react-router-dom';
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import GridFormField from "../../../components/GridFormField.tsx";
import {GridSortDirection} from "@mui/x-data-grid-pro";
import {StyledEstat, StyledPrioritat} from "../../expedient/ExpedientGrid.tsx";
import {useSession} from "../../../components/SessionStorageContext.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import useCambiarEstat, {useCambiarEstatMassive} from "../../expedient/actions/CambiarEstat.tsx";
import {useExecucioMassivaContingut} from "../actions/ExecucioMassivaGrid.tsx";

const CanviEstatFilterFrom = (props:any) => {
    const { findExpedientByName = false } = props;
    const {data} = useFormContext();
    const { value: user } = useUserSession();

    const expedientFilter = builder.and(
        builder.eq('metaExpedient.id', data?.procediment?.id),
        builder.eq('grup.id', data?.grup?.id)
    );

    return <>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="procediment"/>
        <GridFormField name="grup" size={{xs: 12, sm: 6, md: 3}}
                       namedQueries={[`BY_PROCEDIMENT#${data?.procediment?.id}`]}
                       disabled={!data?.procediment}
                       hidden={!user?.sessionScope?.isFiltreGrupsVisible}/>

        {findExpedientByName
            ? <GridFormField size={{xs: 12, sm: 6, md: 3}} name="nom"/>
            : <GridFormField size={{xs: 12, sm: 6, md: 3}} name="expedient" filter={expedientFilter}/>
        }

        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="dataCreacioInici" type={"date"}/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="dataCreacioFi" type={"date"}/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="estatCustom" requestParams={{metaExpedientId: data?.procediment?.id, withoutTancar: true}}/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="prioritat"/>
    </>
}

const springFilterBuilder = (data: any) => {
    return builder.and(
        builder.eq("metaExpedient.id", data?.procediment?.id),
        builder.eq("grup.id", data?.grup?.id),
        builder.eq("id", data?.expedient?.id),
        builder.like("nom", data?.nom),
        builder.betweenDates("createdDate", data?.dataCreacioInici, data?.dataCreacioFi),
        data.estatCustom && builder.equals("estat", `'TANCAT'`, (data.estatCustom === '-1')),
        data.estatCustom && (data.estatCustom != '0' && data.estatCustom != '-1') && builder.eq("estatAdditional.id", data.estatCustom),
        builder.eq("prioritat", `'${data?.prioritat}'`),
    );
}

export const CanviEstatFilter = (props: any) => {
    const {onSpringFilterChange, sessionKey, findExpedientByName} = props;
    return <StyledMuiFilter
        resourceName={"expedientResource"}
        code={"MASSIVE_CANVI_ESTAT_FILTER"}
        sessionKey={sessionKey}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
    >
        <CanviEstatFilterFrom findExpedientByName={findExpedientByName}/>
    </StyledMuiFilter>
}

const namedQueries: string[] = ['MASSIVE_ACTION_QUERY']
const sortModel: any = [{field: 'createdDate', sort: 'desc'}]
const columns = [
    {
        field: 'metaExpedient',
        flex: 1,
    },
    {
        field: 'nom',
        flex: 1,
        renderCell: (params:any) => <Link component={RouterLink} to={`/contingut/${params?.id}`}>{params?.formattedValue}</Link>,
    },    
    {
        field: 'estat',
        flex: 0.75,
        renderCell: (params: any) => <StyledEstat entity={params?.row}>{params.formattedValue}</StyledEstat>,
        sortProcessor: (field: string, sort: GridSortDirection) => {
            return [
                { field: "estatAdditional", sort },
                { field: field, sort },
                { field: "id", sort }
            ]
        }
    },
    {
        field: 'prioritat',
        flex: 0.5,
        renderCell: (params: any) => <StyledPrioritat entity={params?.row}>{params.formattedValue}</StyledPrioritat>
    },
    {
        field: 'createdDate',
        flex: 0.5,
    },
    {
        field: 'createdByFullName',
        flex: 0.8,
    },
]

export const CanviEstatMuiGrid = (props:any) => {
    return <StyledMuiGrid
        resourceName={"expedientResource"}
        columns={columns}
        sortModel={sortModel}
        toolbarHideCreate
        {...props}
    />
}

const perspectives:any = ['ESTAT', 'AUDITORIA', 'EN_PROCES_CANVI_ESTAT'];
const CanviEstatGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const sessionKey = "MASSIVE_CANVI_ESTAT_FILTER";
    const { value: filterData } = useSession(sessionKey);
    const haveRequirements = useMemo(() => !!filterData?.procediment, [filterData?.procediment])

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {handleShow: handleCanviEstat, content: contentCanviEstat} = useCambiarEstat(refresh)
    const {handleShow: handleCanviEstatMassive, content: contentCanviEstatMassive} = useCambiarEstatMassive(refresh)
    const {handleOpen: handleContingutOpen, dialog: dialogContingut} = useExecucioMassivaContingut();

    const actions:any[] = [
        {
            label: t('page.expedient.action.changeEstat.label'),
            icon: "logout",
            showInMenu: false,
            onClick: handleCanviEstat,
            hidden: (row:any) => row?.execucioMassivaCanviEstatId
        },
        {
            label: t('page.user.action.massives.pending'),
            icon: <Box sx={{ color: 'warning.main' }}>schedule</Box>,
            showInMenu: false,
            onClick: (_id:any, row:any) => handleContingutOpen(row?.execucioMassivaCanviEstatId),
            hidden: (row:any) => !row?.execucioMassivaCanviEstatId,
        },
    ]
    const massiveActions = [
        {
            label: t('page.expedient.action.changeEstat.label'),
            icon: "logout",
            showInMenu: false,
            onClick: (ids:any[]) => handleCanviEstatMassive(ids, {metaExpedient: filterData?.procediment}),
        },
    ]

    return <GridPage>
        <CardPage title={t('navigate.massiu.canviEstat')}>
            {!haveRequirements &&
                <Alert severity={'info'} sx={{mb: 1}}>{t('page.expedient.alert.canviEstat')}</Alert>}

            <CanviEstatFilter
                sessionKey={sessionKey}
                onSpringFilterChange={setSpringFilter}/>

            <CanviEstatMuiGrid
                apiRef={apiRef}
                filter={springFilter}
                perspectives={perspectives}
                namedQueries={namedQueries}
                rowAdditionalActions={actions}
                toolbarMassiveActions={massiveActions}
                isRowSelectable={(params:any) => !params?.row?.execucioMassivaCanviEstatId
                    && haveRequirements}
                disabledMassiveDefSelector={!haveRequirements}
            />
        </CardPage>
        {contentCanviEstat}
        {contentCanviEstatMassive}
        {dialogContingut}
    </GridPage>
}
export default CanviEstatGrid;