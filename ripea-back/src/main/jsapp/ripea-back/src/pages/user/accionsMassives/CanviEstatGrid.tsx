import {useTranslation} from "react-i18next";
import {GridPage, useFormContext, useMuiDataGridApiRef} from "reactlib";
import {useMemo, useState} from "react";
import { CardPage } from "../../../components/CardData.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {Alert, Link} from "@mui/material";
import {Link as RouterLink } from 'react-router-dom';
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import GridFormField from "../../../components/GridFormField.tsx";
import {GridSortDirection} from "@mui/x-data-grid-pro";
import {StyledEstat, StyledPrioritat} from "../../expedient/ExpedientGrid.tsx";
import {useSession} from "../../../components/SessionStorageContext.tsx";
import useCambiarEstat, {useCambiarEstatMassive} from "../../expedient/actions/CambiarEstat.tsx";

const CanviEstatFilterFrom = (props:any) => {
    const { findExpedientByName = false } = props;
    const {data} = useFormContext();

    const expedientFilter = builder.and(builder.eq('metaExpedient.id', data?.procediment?.id));

    return <>
        <GridFormField xs={3} name="procediment"/>
        {findExpedientByName
            ? <GridFormField xs={3} name="nom"/>
            : <GridFormField xs={3} name="expedient" filter={expedientFilter}/>
        }

        <GridFormField xs={3} name="dataCreacioInici" type={"date"}/>
        <GridFormField xs={3} name="dataCreacioFi" type={"date"}/>
        <GridFormField xs={3} name="estat" requestParams={{metaExpedientId: data?.procediment?.id, withoutTancar: true}}/>
        <GridFormField xs={3} name="prioritat"/>
    </>
}

const springFilterBuilder = (data: any) => {
    return builder.and(
        builder.eq("metaExpedient.id", data?.procediment?.id),
        builder.eq("id", data?.expedient?.id),
        builder.like("nom", data?.nom),
        builder.betweenDates("createdDate", data?.dataCreacioInici, data?.dataCreacioFi),
		data.estat && (
		    (data.estat === 'OBERT' || data.estat === '0')
				? builder.neq("estat", `'TANCAT'`)
				: (data.estat === 'TANCAT' || data.estat === '-1')
				    ? builder.eq("estat", `'TANCAT'`)
		            : data?.procediment?.id &&builder.eq("estatAdditional.id", data.estat)
		),
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
        filterOnFieldEnterKeyPressed
    >
        <CanviEstatFilterFrom findExpedientByName={findExpedientByName}/>
    </StyledMuiFilter>
}

const namedQueries: string[] = ['MASSIVE_ACTION_QUERY']
const sortModel: any = [{field: 'createdDate', sort: 'desc'}]
const columns = [
    {
        field: 'nom',
        flex: 1,
        renderCell: (params:any) => <Link component={RouterLink} to={`/contingut/${params?.id}`}>{params?.formattedValue}</Link>,
    },
    {
        field: 'metaExpedient',
        flex: 1,
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

const perspectives:any = ['ESTAT', 'AUDITORIA']
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

    const actions = [
        {
            label: t('page.expedient.action.changeEstat.label'),
            icon: "logout",
            showInMenu: false,
            onClick: handleCanviEstat,
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

    return <GridPage disableMargins>
        <CardPage title={t('navigate.massiu.canviEstat')}>
            <Alert severity={'info'} sx={{mb: 1}}>{t('page.expedient.alert.canviEstat')}</Alert>

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
                isRowSelectable={() => haveRequirements}
                disabledMassiveDefSelector={!haveRequirements}
            />
        </CardPage>
        {contentCanviEstat}
        {contentCanviEstatMassive}
    </GridPage>
}
export default CanviEstatGrid;