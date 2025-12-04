import {useTranslation} from "react-i18next";
import {GridPage, useMuiDataGridApiRef} from "reactlib";
import {useState} from "react";
import { CardPage } from "../../../components/CardData.tsx";
import {CanviEstatFilter, CanviEstatMuiGrid} from "./CanviEstatGrid.tsx";

const namedQueries: string[] = ['MASSIVE_ACTION_QUERY']
const perspectives:any = ['ESTAT']
const TancarGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const actions = [
        {
            label: t('page.document.action.close.label'),
            icon: "check",
            showInMenu: false,
        },
    ]
    // TODO: crear acción massiva
    const massiveActions = [
        {
            label: t('page.document.action.close.label'),
            icon: "check",
            showInMenu: false,
        },
    ]

    return <GridPage disableMargins>
        <CardPage title={t('navigate.massiu.tancament')}>
            <CanviEstatFilter
                sessionKey={"MASSIVE_TANCAR_FILTER"}
                onSpringFilterChange={setSpringFilter}
                findExpedientByName/>

            <CanviEstatMuiGrid
                apiRef={apiRef}
                filter={springFilter}
                perspectives={perspectives}
                namedQueries={namedQueries}
                rowAdditionalActions={actions}
                toolbarMassiveActions={massiveActions}
            />
        </CardPage>
    </GridPage>
}
export default TancarGrid;