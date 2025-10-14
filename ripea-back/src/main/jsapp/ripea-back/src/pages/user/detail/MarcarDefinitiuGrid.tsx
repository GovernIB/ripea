import {GridPage, useFormContext, useMuiDataGridApiRef} from "reactlib";
import { CardPage } from "../../../components/CardData.tsx";
import {useTranslation} from "react-i18next";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {useState} from "react";
import * as builder from "../../../util/springFilterUtils.ts";
import GridFormField from "../../../components/GridFormField.tsx";
import {Grid, Icon} from "@mui/material";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";

const MarcarDefinitiuFilterFrom = () => {
    const {data} = useFormContext();

    const expedientFilter = builder.and(builder.eq('metaExpedient.id', data?.procediment?.id));

    return <>
        <GridFormField xs={3} name="procediment"/>
        <GridFormField xs={3} name="expedient" filter={expedientFilter}/>
        <GridFormField xs={3} name="metaDocument"/>
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
        builder.eq("metaDocument.id", data?.metaDocument?.id),
        builder.betweenDates("createdDate", data?.dataCreacioInici, data?.dataCreacioFi),
    );
}

const MarcarDefinitiuFilter = (props: any) => {
    const {onSpringFilterChange} = props;
    return <StyledMuiFilter
        resourceName={"documentResource"}
        code={"MASSIVE_PORTAFIRMES_FILTER"}
        sessionKey={"MASSIVE_DEFINITIVE_FILTER"}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
    >
        <MarcarDefinitiuFilterFrom/>
    </StyledMuiFilter>
}

const sortModel: any = [{field: 'createdDate', sort: 'desc'}]
const columns = [
    {
        field: 'nom',
        flex: 0.75,
    },
    {
        field: 'metaDocument',
        flex: 0.5,
    },
    {
        field: 'expedient',
        flex: 1.75,
        renderCell: (params:any) => <>
            {/** TODO: revisar columna ubicación */}
            /<a href={`/contingut/${params?.id}`} style={{ display: 'flex', alignItems: 'center' }}><Icon>folder</Icon>{params?.formattedValue}</a>
            {params?.row?.pare?.id != params?.row?.expedient?.id ?<>/.../<Icon>folder</Icon>{params?.row?.pare?.description}</> :"" }
            /<Icon>description</Icon>{params?.row?.fitxerNom}
        </>,
    },
    {
        field: 'createdDate',
        flex: 0.5,
    },
    {
        field: 'createdByFullName',
        flex: 0.5,
    },
]

const MarcarDefinitiuGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const actions = [
        {
            label: t('page.document.action.definitive.label'),
            icon: "check_circle",
            showInMenu: false,
        },
    ]
    // TODO: crear acción massiva
    const massiveActions = [
        {
            label: t('page.document.action.definitive.label'),
            icon: "check_circle",
            showInMenu: false,
        },
    ]

    return <GridPage disableMargins>
        <CardPage title={t('navigate.massiu.definitiu')}>
            <MarcarDefinitiuFilter onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={"documentResource"}
                columns={columns}
                filter={springFilter}
                // TODO: filtrar por permisos y puede marcar definitivo
                sortModel={sortModel}

                rowAdditionalActions={actions}
                toolbarMassiveActions={massiveActions}

                toolbarHideCreate
            />
        </CardPage>
    </GridPage>
}
export default MarcarDefinitiuGrid;