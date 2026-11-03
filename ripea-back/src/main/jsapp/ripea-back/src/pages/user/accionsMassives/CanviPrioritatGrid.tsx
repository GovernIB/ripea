import { useTranslation } from 'react-i18next';
import { GridPage, useMuiDataGridApiRef } from 'reactlib';
import { useState } from 'react';
import { ScreenFrame } from '../../../components/ScreenFrame.tsx';
import { icons } from '../../../util/icons.ts';
import { CanviEstatFilter, CanviEstatMuiGrid } from './CanviEstatGrid.tsx';
import useCambiarPrioritat, { useCambiarPrioritatMassive } from '../../expedient/actions/CambiarPrioritat.tsx';

const namedQueries: string[] = ['MASSIVE_ACTION_QUERY'];
const perspectives: any = ['ESTAT', 'AUDITORIA'];
const CanviPrioritatGrid = () => {
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    };

    const { handleShow: handleCanviPrior, content: contentCanviPrior } = useCambiarPrioritat(refresh);
    const { handleShow: handleCanviPriorMassive, content: contentCanviPriorMassive } = useCambiarPrioritatMassive(refresh);

    const actions = [
        {
            label: t('page.expedient.action.changePrioritat.label'),
            icon: 'logout',
            showInMenu: false,
            onClick: handleCanviPrior,
        },
    ];
    const massiveActions = [
        {
            label: t('page.expedient.action.changePrioritat.label'),
            icon: 'logout',
            showInMenu: false,
            onClick: handleCanviPriorMassive,
        },
    ];

    return (
        <GridPage>
            <ScreenFrame icon={icons.massiva} title={t('navigate.massiu.canviPrioritats')}>
                <CanviEstatFilter
                    sessionKey={'MASSIVE_CANVI_PRIORITAT_FILTER'}
                    onSpringFilterChange={setSpringFilter}
                    findExpedientByName
                />

                <CanviEstatMuiGrid
                    apiRef={apiRef}
                    filter={springFilter}
                    perspectives={perspectives}
                    namedQueries={namedQueries}
                    rowAdditionalActions={actions}
                    toolbarMassiveActions={massiveActions}
                />
            </ScreenFrame>
            {contentCanviPrior}
            {contentCanviPriorMassive}
        </GridPage>
    );
};
export default CanviPrioritatGrid;
