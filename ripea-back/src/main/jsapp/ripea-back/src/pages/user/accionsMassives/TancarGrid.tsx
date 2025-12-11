import {useTranslation} from "react-i18next";
import {GridPage, useMuiDataGridApiRef} from "reactlib";
import {useState} from "react";
import { CardPage } from "../../../components/CardData.tsx";
import {CanviEstatFilter, CanviEstatMuiGrid} from "./CanviEstatGrid.tsx";
import useTancar, {useTancarMassive} from "../../expedient/actions/Tancar.tsx";

const namedQueries: string[] = ['MASSIVE_ACTION_QUERY', 'MASSIVE_ACTION_TANCAR']
const perspectives:any = ['ESTAT']
const TancarGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {handleShow: handleTancar, content: contentTancar} = useTancar(refresh)
    const {handleShow: handleTancarMassive, content: contentTancarMassive} = useTancarMassive(refresh)

    const actions = [
        {
            label: t('page.expedient.action.close.label'),
            icon: "check",
            showInMenu: false,
            onClick: handleTancar,
        },
    ]
    const massiveActions = [
        {
            label: t('page.expedient.action.close.label'),
            icon: "check",
            showInMenu: false,
            onClick: handleTancarMassive,
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
        {contentTancar}
        {contentTancarMassive}
    </GridPage>
}
export default TancarGrid;