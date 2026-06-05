import {GridPage, useMuiDataGridApiRef} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import {useTranslation} from "react-i18next";
import {useState} from "react";
import {EstatMessage} from "../../remesa/RemesaGrid.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";
import GridFormField from "../../../components/GridFormField.tsx";
import {Link as RouterLink} from "react-router-dom";
import {Link} from "@mui/material";
import useDocumentDetail from "../../contingut/details/DocumentDetail.tsx";
import {useActions} from "../../contingut/details/ContingutActions.tsx";
import useVisualitzar from "../../contingut/actions/Visualitzar.tsx";
import Load from "../../../components/Load.tsx";

// Action
const useConsultaPinbalActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const { apiDownload } = useActions(refresh)
    const {apiIsReady: isReadyDetall, handleOpen: handleDetallOpen, dialog: dialogDetall} = useDocumentDetail({}, refresh);
    const {apiIsReady: isReadyVisualitzar, handleOpen: handleVisualitzarOpen, dialog: dialogVisualitzar, isValid} = useVisualitzar();

    const isDigitalOrImportat = (row:any) => {
        return isInOptions(row.documentTipus, 'DIGITAL', 'IMPORTAT');
    };
    const isInOptions = (value:string, ...options:string[]) => {
        return options.includes(value)
    }

    const actions = [
        {
            label: t('page.document.action.detall.label'),
            icon: "folder",
            showInMenu: true,
            onClick: (_id:any, row:any) => handleDetallOpen(row?.documentInfo?.id),
            hidden: (row:any) => row?.documentInfo == null
        },
        {
            label: t('page.document.action.descarregarOriginal.label'),
            icon: "download",
            showInMenu: true,
            onClick: (_id:any, row:any) => apiDownload(row?.documentInfo?.id, 'adjunt', t('page.document.action.descarregarOriginal.ok')),
            hidden: (row:any) => row?.documentInfo == null || !isDigitalOrImportat(row?.documentInfo)
        },
        {
            label: t('page.document.action.view.label'),
            title: t('page.document.alert.view'),
            icon: "search",
            showInMenu: true,
            onClick: (_id:any, row:any) => handleVisualitzarOpen(row?.documentInfo?.id),
            disabled: (row:any) => !isValid(row?.documentInfo),
            hidden: (row:any) => row?.documentInfo == null || !isDigitalOrImportat(row?.documentInfo)
        },
    ]

    const components = <>
        {dialogDetall}
        {dialogVisualitzar}
    </>

    return {
        apiIsReady: isReadyDetall && isReadyVisualitzar,
        actions,
        components,
    }
}

// Filter
const ConsultesPinbalFilterForm = () => {
    return <>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="metaExpedient"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="expedient"/>        
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="servei"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="createdBy"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="createdDateInici" type={"date"}/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="createdDateFi" type={"date"}/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="estat"/>
    </>
}

const springFilterBuilder = (data:any) => {
    return builder.and(
        builder.eq('expedient.id', data?.expedient?.id),
        builder.eq('metaExpedient.id', data?.metaExpedient?.id),
        builder.eq('servei.id',  data?.servei?.id),
        builder.eq('createdBy',  `'${data?.createdBy?.id}'`),
        builder.betweenDates('createdDate', data?.createdDateInici, data?.createdDateFi),
        builder.eq('estat', `'${data?.estat}'`),
    );
}

const ConsultesPinbalFilter = (props: any) => {
    const {onSpringFilterChange} = props;

    return <StyledMuiFilter
        resourceName={"consultaPinbalResource"}
        code={"FILTER_CONSULTA_PINBAL"}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
    >
        <ConsultesPinbalFilterForm/>
    </StyledMuiFilter>
}

// Grid
const StyledEstat = (props:any) => {
    const { entity, children } = props;

    switch (entity?.estat) {
        case 'TRAMITADA':
            return <EstatMessage icon={"check"} color='success'>{children}</EstatMessage>
        case 'ERROR':
            return <EstatMessage icon={"warning"} color={'error'} title={entity?.error}>{children}</EstatMessage>
    }

    return <></>
}

const sortModel: any[] = [{field: 'createdDate', sort: 'desc'}]
const perspectives: any[] = ['DOCUMENT', 'AUDITORIA']
const columns = [
    {
        field: 'expedient',
        flex: 1,
        renderCell: (params:any) => <Link component={RouterLink} to={`/contingut/${params?.row?.expedient?.id}`}>{params?.formattedValue}</Link>,
    },
    {
        field: 'metaExpedient',
        flex: 1,
    },
    {
        field: 'servei',
        flex: 1,
    },
    {
        field: 'createdByFullName',
        flex: 0.5,
    },
    {
        field: 'createdDate',
        flex: 0.75,
        valueFormatter: (value: any) => formatDate(value),
    },
    {
        field: 'estat',
        flex: 0.5,
        renderCell: (params:any) => <StyledEstat entity={params?.row}>{params.formattedValue}</StyledEstat>
    },
    {
        field: 'document',
        flex: 1,
    },
]

const ConsultesPinbalGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {apiIsReady, actions, components} = useConsultaPinbalActions(refresh);

    return <GridPage autoHeight>
        <CardPage title={t('page.user.menu.pinbalEnviades')}>
            <ConsultesPinbalFilter onSpringFilterChange={setSpringFilter}/>

            <Load value={apiIsReady}>
            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={"consultaPinbalResource"}
                columns={columns}
                filter={springFilter}
                perspectives={perspectives}
                sortModel={sortModel}
                rowAdditionalActions={actions}
                readOnly
            /></Load>
        </CardPage>
        {components}
    </GridPage>
}
export default ConsultesPinbalGrid;