import React from 'react';
import {Box, Button, ButtonGroup, Chip, Icon, Tooltip} from '@mui/material';
import {useResourceApiService} from 'reactlib';
import { useTranslation } from 'react-i18next';
import {MenuActionButton} from "./MenuButton.tsx";
import Load from "./Load.tsx";

export type MassiveActionProps = {
    title?: string;
    icon?: string;
    disabled?: boolean | ((ids: any[]) => void);
    hidden?: boolean | ((ids: any[]) => void);
    onClick?: (ids: any[]) => void;
}
type MassiveActionSelectorProps = {
    resourceName: string,
    selectedRows: any[];
    setSelectedRows: (value:any[]) => void
    actions: MassiveActionProps[]
    filter?: string
}

const MassiveActionSelector: React.FC<MassiveActionSelectorProps> = (props:MassiveActionSelectorProps) => {
    const {resourceName, filter, selectedRows, setSelectedRows, actions } = props;
    const { t } = useTranslation();

    const {
        isReady: apiIsReady,
        find: apiFindAll,
    } = useResourceApiService(resourceName);

    // Handle selection actions
    const handleSelectAll = () => {
        if (apiIsReady) {
            apiFindAll({unpaged: true, filter: filter})
                .then((app) => {
                    const allIds = app?.rows?.map(row=>row?.id)
                    setSelectedRows(allIds);
                })
        }
    };

    const handleClearSelection = () => {
        setSelectedRows([]);
    };

    return <Load value={actions.filter(a=>!a?.hidden).length>0} noEffect>
        <Box sx={{ display: 'flex', alignItems: 'flex-start', ml: 1 }}>
        {/* Selection buttons */}
        <ButtonGroup
            variant="outlined"
            size="small"
            sx={{
                '& .MuiButton-root': {
                    borderColor: 'rgba(0, 0, 0, 0.23)' // Standard MUI outlined button border color
                }
            }}>
            <Tooltip title={t('Seleccionar tots')}>
                <Button
                    onClick={handleSelectAll}
                    sx={{ minWidth: '40px', display: 'flex', justifyContent: 'center', alignItems: 'flex-start', paddingTop: 0.75 }}
                >
                    <Icon color="action" sx={{m: 0}}>check_box</Icon>
                </Button>
            </Tooltip>
            <Tooltip title={t('Netejar selecció')}>
                <Button
                    onClick={handleClearSelection}
                    sx={{ minWidth: '40px', display: 'flex', justifyContent: 'center', alignItems: 'flex-start', paddingTop: 0.75 }}
                >
                    <Icon color="action" sx={{m: 0}}>check_box_outline_blank</Icon>
                </Button>
            </Tooltip>

            <MenuActionButton
                id={'massiveOpcions'}
                entity={selectedRows}
                buttonLabel={t('common.options')}
                buttonProps={{
                    startIcon: <Chip label={selectedRows?.length} size="small" />,
                    disabled: selectedRows?.length === 0
                }}
                actions={actions}
            />
        </ButtonGroup>
    </Box></Load>;
};

export default MassiveActionSelector;
