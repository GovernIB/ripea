import {useTranslation} from "react-i18next";
import {GridPage, useMuiDataGridApiRef} from "reactlib";
import {useState} from "react";
import { CardPage } from "../../../components/CardData.tsx";
import {CanviEstatFilter, CanviEstatMuiGrid} from "./CanviEstatGrid.tsx";
import useTancar, {useTancarMassive} from "../../expedient/actions/Tancar.tsx";
import {useExecucioMassivaContingut} from "../actions/ExecucioMassivaGrid.tsx";

const namedQueries: string[] = ['MASSIVE_ACTION_QUERY', 'MASSIVE_ACTION_TANCAR']
const perspectives:any = ['ESTAT','AUDITORIA', 'EN_PROCES_TANCAMENT'];
const TancarGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {handleShow: handleTancar, content: contentTancar} = useTancar(refresh)
    const {handleShow: handleTancarMassive, content: contentTancarMassive} = useTancarMassive(refresh)
    const {handleOpen: handleContingutOpen, dialog: dialogContingut} = useExecucioMassivaContingut();

    const actions = [
        {
            label: t('page.expedient.action.close.label'),
            icon: "check",
            showInMenu: false,
            onClick: handleTancar,
            hidden: (row:any) => row?.execucioMassivaTancamentId,
        },
        {
            label: t('page.user.action.massives.pending'),
            icon: "schedule",
            showInMenu: false,
            onClick: (row:any) => handleContingutOpen(row?.execucioMassivaTancamentId),
            hidden: (row:any) => !row?.execucioMassivaTancamentId,
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
                isRowSelectable={(params:any) => !params?.row?.execucioMassivaTancamentId}
            />
        </CardPage>
        {contentTancar}
        {contentTancarMassive}
        {dialogContingut}
    </GridPage>
}
export default TancarGrid;