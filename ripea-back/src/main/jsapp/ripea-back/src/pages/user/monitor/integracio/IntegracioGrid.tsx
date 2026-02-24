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
        flex: 0.5,
    },
    {
        field: 'estat',
        flex: 0.5,
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
                namedQueries={[value]}
                // onRefresh={refresh}
                rowAdditionalActions={actions}
                toolbarElementsWithPositions={[
                     {
                         position: 0,
                         element: tabElement
                     }
                ]}
                readOnly
            />
            {dialog}
            </Load>
        </CardPage>
    </GridPage>
}