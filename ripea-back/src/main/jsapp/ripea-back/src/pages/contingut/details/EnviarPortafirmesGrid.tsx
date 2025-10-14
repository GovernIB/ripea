import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {GridPage, useFormContext, useMuiDataGridApiRef} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import {Alert} from "@mui/material";
import {useTranslation} from "react-i18next";
import {useState} from "react";
import useEnviarPortafirmes from "../actions/EnviarPortafirmes.tsx";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";
import GridFormField from "../../../components/GridFormField.tsx";
import * as builder from "../../../util/springFilterUtils.ts";

const EnviarPortafirmesFilterForm = () => {
    const {data} = useFormContext();

    const expedientFilter = data?.procediment ? builder.eq('metaExpedient.id', data?.procediment?.id) : "";
    const metaDocumentFilter = builder.eq('metaExpedient.id', data?.procediment?.id || 0);

    return <>
        <GridFormField xs={4} name="procediment"/>
        <GridFormField xs={4} name="expedient" filter={expedientFilter}/>
        <GridFormField xs={4} name="metaDocument" filter={metaDocumentFilter}/>
        <GridFormField xs={3.6} name="nom"/>
        <GridFormField xs={3} name="dataCreacioInici" type={"date"}/>
        <GridFormField xs={3} name="dataCreacioFi" type={"date"}/>
    </>
}

const springFilterBuilder = (data: any) => {
    return builder.and(
        builder.eq("expedient.metaExpedient.id", data?.procediment?.id),
        builder.eq("expedient.id", data?.expedient?.id),
        builder.eq("metaNode.id", data?.metaDocument?.id),
        builder.like("nom", data?.nom),
        builder.betweenDates("createdDate", data?.dataCreacioInici, data?.dataCreacioFi)
    );
}

const EnviarPortafirmesFilter = (props: any) => {
    const {onSpringFilterChange, setRequirements} = props;
    return <StyledMuiFilter
        resourceName={"documentResource"}
        code={"MASSIVE_PORTAFIRMES_FILTER"}
        springFilterBuilder={(data:any) => {
            setRequirements(!!data?.procediment && !!data?.metaDocument);
            return springFilterBuilder(data)
        }}
        onSpringFilterChange={onSpringFilterChange}
        filterOnFieldEnterKeyPressed
    >
        <EnviarPortafirmesFilterForm/>
    </StyledMuiFilter>
}

const sortModel: any = [{field: 'createdDate', sort: 'desc'}]
const columns = [
    {
        field: 'nom',
        flex: 0.5,
    },
    {
        field: 'metaDocument',
        flex: 0.6,
    },
    {
        field: 'expedient',
        flex: 0.75,
        renderCell: (params:any) => <a href={`/contingut/${params?.id}`}>{params?.formattedValue}</a>,
    },
    {
        field: 'createdDate',
        flex: 0.55,
    },
    {
        field: 'createdByFullName',
        flex: 0.45,
    },
]

const EnviarPortafirmesGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();
    const [haveRequirements, setRequirements] = useState<boolean>(false)

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {handleShow: handleEviarPortafirmesShow, content: contentEviarPortafirmes} = useEnviarPortafirmes(refresh);

    const actions = [
        {
            label: t('page.document.action.portafirmes.label'),
            icon: "mail",
            showInMenu: false,
            // onClick: handleEviarPortafirmesShow,
            // disabled: (row:any) => !row?.valid || row?.gesDocAdjuntId!=null,
            // hidden : (row:any) => !entity?.potModificar || !row?.metaDocumentInfo?.firmaPortafirmesActiva || !isFirmaActiva(row),
        },
    ]
    // TODO: crear acción massiva
    const massiveActions = [
        {
            label: t('page.document.action.portafirmes.label'),
            icon: "mail",
            showInMenu: false,
            // onClick: download,
        },
    ]

    return <GridPage disableMargins>
        <CardPage title={t('navigate.massiu.portafirmes')}>
            <Alert severity={'info'} sx={{mb: 1}}>{t('page.document.alert.portafirmes')}</Alert>

            <EnviarPortafirmesFilter setRequirements={setRequirements} onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={"documentResource"}
                columns={columns}
                filter={springFilter}
                // TODO: filtrar por permisos y enviable a portafirma
                // perspectives={perspectives}
                sortModel={sortModel}

                rowAdditionalActions={actions}
                toolbarMassiveActions={massiveActions}
                isRowSelectable={() => haveRequirements}

                disabledMassiveDefSelector={!haveRequirements}
                // hiddenMassiveDefSelector={true}

                toolbarHideCreate
            />
        </CardPage>
        {contentEviarPortafirmes}
    </GridPage>
}
export default EnviarPortafirmesGrid;