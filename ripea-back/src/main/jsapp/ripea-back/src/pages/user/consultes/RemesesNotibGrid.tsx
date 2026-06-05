import {useTranslation} from "react-i18next";
import {useState} from "react";
import {GridPage, useMuiDataGridApiRef} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import {StyledEstat} from "../../remesa/RemesaGrid.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import GridFormField, {GridButtonField} from "../../../components/GridFormField.tsx";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";
import {Link as RouterLink} from "react-router-dom";
import {Link} from "@mui/material";
import {useActions} from "../../remesa/details/RemesaActions.tsx";

// Filter
const RemesesNotibFilterForm = () => {
    return <>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="tipusEnviament"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="concepte"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="estat"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="interessat"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="expedient"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="nom"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="emisor"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="procediment"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="dataEnviamentInici" type={"date"}/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="dataEnviamentFi" type={"date"}/>
        <GridButtonField size={{xs: 12, sm: 2, md: 1}} name={"nomesAmbError"} icon={"warning"}/>
    </>
}

const springFilterBuilder = (data:any) => {
    return builder.and(
        builder.like('document.nom', data?.nom),
        builder.eq('tipus', `'${data?.tipusEnviament}'`),
        builder.eq('notificacioEstat', `'${data?.estat}'`),
        builder.like('assumpte', data?.concepte),
        builder.betweenDates("enviatData", data?.dataEnviamentInici, data?.dataEnviamentFi),
        builder.exists(
            builder.or(
                builder.like("documentInteressats.interessat.documentNum", data.interessat),
                builder.like(builder.concat("documentInteressats.interessat.nom", "documentInteressats.interessat.llinatge1", "documentInteressats.interessat.llinatge2"), data.interessat),
                builder.like("documentInteressats.interessat.raoSocial", data.interessat),
                builder.like("documentInteressats.interessat.organNom", data.interessat)
            )
        ),
        builder.eq('expedient.id', data?.expedient?.id),
        builder.eq('emisor.id', data?.emisor?.id),
        builder.eq('expedient.metaExpedient.id', data?.procediment?.id),
        data?.nomesAmbError && builder.and(
            builder.eq('error', 'true'),
            builder.neq('errorDescripcio', null),
        )
    );
}

const RemesesNotibFilter = (props: any) => {
    const {onSpringFilterChange} = props;

    return <StyledMuiFilter
        resourceName={"documentNotificacioResource"}
        code={"FILTER_ENVIATS_NOTIB"}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
    >
        <RemesesNotibFilterForm/>
    </StyledMuiFilter>
}

// Grid
const sortModel: any = [{field: 'createdDate', sort: 'desc'}]
const columns: any[] = [
    {
        field: 'createdDate',
        flex: 0.5,
        align: 'left',
        valueFormatter: (value: any) => formatDate(value)
    },
    {
        field: 'emisor',
        flex: 1,
    },
    {
        field: 'procediment',
        flex: 1,
    },
    {
        field: 'expedient',
        flex: 1,
        renderCell: (params:any) => <Link component={RouterLink} to={`/contingut/${params?.row?.expedient?.id}`}>{params?.formattedValue}</Link>,
    },
    {
        field: 'assumpte',
        flex: 1.5,
    },
    {
        field: 'document',
        flex: 1,
    },
    {
        field: 'processatData',
        flex: 0.5,
        valueFormatter: (value: any) => formatDate(value)
    },
    {
        field: 'notificacioEstat',
        flex: 0.75,
        renderCell: (params:any) => <StyledEstat entity={params?.row}>{params.formattedValue}</StyledEstat>
    },
    {
        field: 'destinatari',
        flex: 1,
        valueFormatter: (interessat: any) => {
            let resum = '';
            if (!!interessat) {
                switch (interessat.tipus) {
                    case 'InteressatPersonaFisicaEntity':
                        resum += interessat?.nom == null ? "" : interessat?.nom + " ";
                        resum += interessat?.llinatge1 == null ? "" : interessat?.llinatge1 + " ";
                        resum += interessat?.llinatge2 == null ? "" : interessat?.llinatge2 + " ";
                        resum += "(" + interessat?.documentNum + ")" + "\n";
                        break;
                    case 'InteressatPersonaJuridicaEntity':
                        resum += interessat?.raoSocial + " ";
                        resum += "(" + interessat?.documentNum + ")" + "\n";
                        break;
                    case 'InteressatAdministracioEntity':
                        resum += interessat?.nomComplet + " ";
                        resum += "(" + interessat?.documentNum + ")" + "\n";
                        break;
                }
            }
            return resum;
        },
    },
]

const RemesesNotibGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {actualitzarEstat, actualitzarEstatMassive} = useActions(refresh);

    const actions = [
        {
            label: t('page.notificacio.action.actualitzarEstat.label'),
            icon: "autorenew",
            showInMenu: false,
            onClick: actualitzarEstat,
            hidden: (row: any) => row?.notificacioEstat === 'PROCESSADA',
        },
    ]
    const massiveActions = [
        {
            label: t('page.notificacio.action.actualitzarEstat.label'),
            icon: "autorenew",
            showInMenu: false,
            onClick: actualitzarEstatMassive,
        },
    ]

    return <GridPage autoHeight>
        <CardPage title={t('page.user.menu.notib')}>
            <RemesesNotibFilter onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={"documentNotificacioResource"}
                columns={columns}
                filter={springFilter}
                sortModel={sortModel}
                rowAdditionalActions={actions}
                toolbarMassiveActions={massiveActions}
                toolbarHideCreate
            />
        </CardPage>
    </GridPage>
}
export default RemesesNotibGrid;