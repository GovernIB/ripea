import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {useTranslation} from "react-i18next";
import {GridPage, useMuiDataGridApiRef} from "reactlib";
import {useMemo, useState} from "react";
import {CardPage} from "../../../components/CardData.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import GridFormField from "../../../components/GridFormField.tsx";
import {Alert, Grid, Link} from "@mui/material";
import {useSession} from "../../../components/SessionStorageContext.tsx";
import {GridSortDirection} from "@mui/x-data-grid-pro";
import {useAnexxActions} from "../../anotacions/details/AnotacioActions.tsx";
import {useGridApiRef as useMuiDatagridApiRef} from "@mui/x-data-grid-pro/hooks/utils/useGridApiRef";

const AdjuntarAnnexosPendentsFilterFrom = () => {
    return <>
        <GridFormField xs={3} name="nom"/>
        <GridFormField xs={3} name="numero"/>
        <GridFormField xs={3} name="dataInici" type={"date"}/>
        <GridFormField xs={3} name="dataFi" type={"date"}/>
        <GridFormField xs={4} name="procediment"/>
        <GridFormField xs={4} name="expedient"/>
        <Grid item xs={1.6}/>
    </>
}

const springFilterBuilder = (data: any) => {
    return builder.and(
        builder.like("titol", data?.nom),
        builder.like("registre.identificador", data?.numero),
        builder.exists(
            builder.and(
                builder.eq("registre.expedientPeticions.expedient.id", data.expedient?.id),
                builder.eq("registre.expedientPeticions.expedient.metaExpedient.id", data.procediment?.id),
                builder.betweenDates("registre.expedientPeticions.expedient.createdDate", data?.dataAltaInici, data?.dataAltaFi),
            )
        ),
    );
}

const AdjuntarAnnexosPendentsFilter = (props: any) => {
    const {onSpringFilterChange, sessionKey} = props;
    return <StyledMuiFilter
        resourceName={"registreAnnexResource"}
        code={"ADJUNTAR_ANNEX_FILTER"}
        sessionKey={sessionKey}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
        filterOnFieldEnterKeyPressed
    >
        <AdjuntarAnnexosPendentsFilterFrom/>
    </StyledMuiFilter>

}

const namedQuery: string[] = ['MASSIU_PENDENT_PROCESSAR']
const perspectives: string[] = ['REGISTRE']
const sortModel: any = [{field: 'expedientInfo.createdDate', sort: 'desc'}]
const AdjuntarAnnexosPendentsGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const columns = [
        {
            headerName: t('page.registre.grid.nomAnnex'),
            field: 'titol',
            flex: 1,
        },
        {
            headerName: t('page.registre.grid.origenRegistreNumero'),
            field: 'registreInfo.identificador',
            flex: 1,
            sortProcessor: (field: string, sort: GridSortDirection) => [{field: 'registre.identificador', sort}],
        },
        {
            headerName: t('page.expedient.title'),
            field: 'expedientInfo.nom',
            flex: 1,
            renderCell: (params:any) => <Link href={`/contingut/${params?.row?.expedientInfo?.id}`}>{params?.formattedValue}</Link>,
            // sortProcessor: (field: string, sort: GridSortDirection) => [{field: 'registre.expedientPeticions.expedient.createdDate', sort}],
            sortable: false,
        },
        {
            headerName: t('page.registre.grid.dataExpedient'),
            field: 'expedientInfo.createdDate',
            flex: 0.75,
            valueFormatter: (value: any) => formatDate(value),
            // sortProcessor: (field: string, sort: GridSortDirection) => [{field: 'registre.expedientPeticions.expedient.createdDate', sort}],
            sortable: false,
        },
    ]

    const sessionKey = "ADJUNTAR_ANNEX_FILTER";
    const { value: filterData } = useSession(sessionKey);
    const haveRequirements = useMemo(() => !!filterData?.procediment, [filterData?.procediment])

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const { reintentar, reintentarMassive } = useAnexxActions(refresh)

    const actions = [
        {
            label: t('page.anotacio.action.procesarAnnexosPendents.label'),
            icon: "reply",
            showInMenu: false,
            onClick: reintentar,
        },
    ]
    const massiveActions = [
        {
            label: t('page.anotacio.action.procesarAnnexosPendents.label'),
            icon: "reply",
            showInMenu: false,
            onClick: reintentarMassive,
        },
    ]

    return <GridPage disableMargins>
        <CardPage title={t('navigate.massiu.procesarAnnexosPendents')}>
            
            <Alert severity={'info'} sx={{mb: 1}}>{t('page.anotacio.action.procesarAnnexosPendents.info')}</Alert>
            <Alert severity={'info'} sx={{mb: 1}}>{t('page.expedient.alert.canviEstat')}</Alert>
            
            <AdjuntarAnnexosPendentsFilter
                sessionKey={sessionKey}
                onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={"registreAnnexResource"}
                columns={columns}
                filter={springFilter}
                perspectives={perspectives}
                // sortModel={sortModel}
                namedQueries={namedQuery}
                rowAdditionalActions={actions}
                toolbarMassiveActions={massiveActions}
                isRowSelectable={() => haveRequirements}
                disabledMassiveDefSelector={!haveRequirements}
                toolbarHideCreate
            />
        </CardPage>
    </GridPage>
}
export default AdjuntarAnnexosPendentsGrid;