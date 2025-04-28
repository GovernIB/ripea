import React from 'react';
import {Box, Button, ButtonGroup, Chip, Icon, MenuItem, Tooltip} from '@mui/material';
import {useResourceApiService} from 'reactlib';
import { useTranslation } from 'react-i18next';
import { GridApiCommunity } from '@mui/x-data-grid';
import MenuButton from "./MenuButton.tsx";

interface MassiveActionSelectorProps {
    resourceName: string,
    selectedRows: any[];
    setSelectedRows: (value:any[]) => void
    actions: any[]
    filter?: string
}

const MassiveActionSelector: React.FC<MassiveActionSelectorProps> = (props:any) => {
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

    return <Box sx={{ display: 'flex', alignItems: 'flex-start' }}>
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
                    <Icon color="action">check_box</Icon>
                </Button>
            </Tooltip>
            <Tooltip title={t('Netejar selecció')}>
                <Button
                    onClick={handleClearSelection}
                    sx={{ minWidth: '40px', display: 'flex', justifyContent: 'center', alignItems: 'flex-start', paddingTop: 0.75 }}
                >
                    <Icon color="action">check_box_outline_blank</Icon>
                </Button>
            </Tooltip>

            <MenuButton
                id={'massiveOpcions'}
                buttonLabel={'Opcions'}
                buttonProps={{
                    startIcon: <Chip label={selectedRows?.length} size="small" />,
                    disabled: selectedRows?.length === 0
                }}
            >
                {actions.map((action:any) =>
                    action?.showInMenu && !(action?.hidden==true || action?.hidden?.(selectedRows)) && <MenuItem onClick={()=>action?.onClick?.(selectedRows)} key={action.title} disabled={action?.disabled==true || action?.disabled?.(selectedRows)}>
                        {action.icon && <Icon>{action.icon}</Icon>}{action.title}
                    </MenuItem>
                )}
            </MenuButton>
        </ButtonGroup>
    </Box>;
};

export default MassiveActionSelector;
