import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {useTranslation} from "react-i18next";
import {GridPage, useFormContext, useMuiDataGridApiRef} from "reactlib";
import {useMemo, useState} from "react";
import {CardPage} from "../../../components/CardData.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import GridFormField from "../../../components/GridFormField.tsx";
import {Alert, Box, Link} from "@mui/material";
import {Link as RouterLink } from 'react-router-dom';
import {useSession} from "../../../components/SessionStorageContext.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import {GridSortDirection} from "@mui/x-data-grid-pro";
import {useReintentar, useReintentarMassive} from "../../anotacions/actions/Reintentar.tsx";
import {useExecucioMassivaContingut} from "../actions/ExecucioMassivaGrid.tsx";

const AdjuntarAnnexosPendentsFilterFrom = () => {
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
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="expedient" filter={expedientFilter}/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="numero"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="nom"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="dataInici" type={"date"}/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="dataFi" type={"date"}/>
    </>
}

const springFilterBuilder = (data: any) => {
    return builder.and(
        builder.like("titol", data?.nom),
        builder.like("registre.identificador", data?.numero),
        builder.exists(
            builder.and(
                builder.eq("registre.expedientPeticions.expedient.id", data.expedient?.id),
                builder.eq("registre.expedientPeticions.expedient.grup.id", data.grup?.id),
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
const perspectives: string[] = ['REGISTRE', 'EN_PROCES_ADJUNTAR_ANNEXOS'];
// const sortModel: any = [{field: 'expedientInfo.createdDate', sort: 'desc'}]
const AdjuntarAnnexosPendentsGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const columns = useMemo(() => [
        {
            headerName: t('page.registre.grid.nomAnnex'),
            field: 'titol',
            flex: 1,
        },
        {
            headerName: t('page.registre.grid.origenRegistreNumero'),
            field: 'registreInfo.identificador',
            flex: 1,
            sortProcessor: (_field: string, sort: GridSortDirection) => [{field: 'registre.identificador', sort}],
        },
        {
            headerName: t('page.expedient.title'),
            field: 'expedientInfo.nom',
            flex: 1,
            renderCell: (params:any) => <Link component={RouterLink} to={`/contingut/${params?.row?.expedientInfo?.id}`}>{params?.formattedValue}</Link>,
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
    ], [t]);

    const sessionKey = "ADJUNTAR_ANNEX_FILTER";
    const { value: filterData } = useSession(sessionKey);
    const haveRequirements = useMemo(() => !!filterData?.procediment, [filterData?.procediment])

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {handleShow: handleReintentar, content: contentReintentar} = useReintentar(refresh)
    const {handleShow: handleReintentarMassive, content: contentReintentarMassive} = useReintentarMassive(refresh, filterData?.procediment)
    const {handleOpen: handleContingutOpen, dialog: dialogContingut} = useExecucioMassivaContingut();

    const actions:any[] = [
        {
            label: t('page.anotacio.action.procesarAnnexosPendents.label'),
            icon: "reply",
            showInMenu: false,
            onClick: (id: any, row: any) => handleReintentar(id, row?.expedientInfo?.metaExpedient),
            hidden: (row:any) => row?.execucioMassivaAdjuntarAnnexosId,
        },
        {
            label: t('page.user.action.massives.pending'),
            icon: <Box sx={{ color: 'warning.main' }}>schedule</Box>,
            showInMenu: false,
            onClick: (_id:any, row:any) => handleContingutOpen(row?.execucioMassivaAdjuntarAnnexosId),
            hidden: (row:any) => !row?.execucioMassivaAdjuntarAnnexosId,
        },
    ]
    const massiveActions = [
        {
            label: t('page.anotacio.action.procesarAnnexosPendents.label'),
            icon: "reply",
            showInMenu: false,
            onClick: handleReintentarMassive,
        },
    ]

    return <GridPage disableMargins>
        <CardPage title={t('navigate.massiu.procesarAnnexosPendents')}>
            
            <Alert severity={'info'} sx={{mb: 1}}>{t('page.anotacio.action.procesarAnnexosPendents.info')}</Alert>
            {!haveRequirements &&
                <Alert severity={'info'} sx={{mb: 1}}>{t('page.expedient.alert.canviEstat')}</Alert>}
            
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
                isRowSelectable={(params:any) => !params?.row?.execucioMassivaAdjuntarAnnexosId
                    && haveRequirements}
                disabledMassiveDefSelector={!haveRequirements}
                toolbarHideCreate
            />
        </CardPage>
        {contentReintentar}
        {contentReintentarMassive}
        {dialogContingut}
    </GridPage>
}
export default AdjuntarAnnexosPendentsGrid;