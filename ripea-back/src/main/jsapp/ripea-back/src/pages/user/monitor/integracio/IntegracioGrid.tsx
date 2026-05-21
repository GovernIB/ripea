import {useTranslation} from "react-i18next";
import {GridPage} from "reactlib";
import {CardPage} from "../../../../components/CardData.tsx";
import {useMemo, useState} from "react";
import StyledMuiGrid from "../../../../components/StyledMuiGrid.tsx";
import {formatDate} from "../../../../util/dateUtils.ts";
import Load from "../../../../components/Load.tsx";
import {IntegracioFilter} from "./IntegracioFilter.tsx";
import {useIntegracioTab} from "./IntegracioTab.tsx";
import {useIntegracioDetail} from "./IntegracioDetail.tsx";
import {EstatMessage} from "../../../remesa/RemesaGrid.tsx";

export const StyledEstat = (props:any) => {
    const {entity, children} = props;

    switch (entity?.estat) {
        case "OK": return <EstatMessage icon={"check"} color='success'>{children}</EstatMessage>
        case "ERROR": return <EstatMessage icon={"warning"} color='error'>{children}</EstatMessage>
    }

    return <>{children}</>
}

const columns:any[] = [
    {
        field: 'data',
        flex: 0.5,
        valueFormatter: (value: any) => formatDate(value),
    },
    {
        field: 'descripcio',
        flex: 1,
    },
    {
        field: 'tipus',
        flex: 0.5,
    },
    {
        field: 'endpoint',
        flex: 0.75,
    },
    {
        field: 'entitatCodi',
        flex: 0.5,
    },
    {
        field: 'tempsResposta',
        flex: 0.4,
        valueFormatter: (value:string) => value && `${value} ms`,
    },
    {
        field: 'estat',
        flex: 0.3,
        renderCell: (params:any) => <StyledEstat entity={params?.row}>{params?.formattedValue}</StyledEstat>
    },
];
const perspectives:any[] = [];
const sortModel:any[] = [{field: 'data', sort: 'desc'}];
export const IntegracioGrid = () => {
    const {t} = useTranslation()
    const [springFilter, setSpringFilter] = useState<string>();

    const {value, integracions, tabElement} = useIntegracioTab()

    const {apiIsReady, handleOpen, dialog} = useIntegracioDetail();
    const actions = useMemo(() => [
        {
            label: t('common.detail'),
            icon: 'info',
            onClick: handleOpen,
        }
    ], [apiIsReady, value]);

    return <GridPage disableMargins>
        <CardPage title={t('navigate.integracio')}>
            <IntegracioFilter integracions={integracions} onSpringFilterChange={setSpringFilter}/>

            <Load value={value} noEffect>
            <StyledMuiGrid
                resourceName={"integracioResource"}
                filter={springFilter}
                columns={columns}
                sortModel={sortModel}
                perspectives={perspectives}
                namedQueries={useMemo(() => [value], [value])}
                // onRefresh={refresh}
                onRowClick={(params: any) => handleOpen(params?.row?.id)}
                rowAdditionalActions={actions}
                toolbarElementsWithPositions={[
                     {
                         position: 0,
                         element: tabElement
                     }
                ]}
                readOnly
            />
            </Load>
            {dialog}
        </CardPage>
    </GridPage>
}