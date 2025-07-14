import React from 'react';
import {Box, Button, ButtonGroup, Chip, Icon, Tooltip} from '@mui/material';
import {useResourceApiService} from 'reactlib';
import { useTranslation } from 'react-i18next';
import {MenuActionButton} from "./MenuButton.tsx";
import Load from "./Load.tsx";

export type MassiveActionProps = {
    title?: string;
    icon?: string;
    showInMenu?: boolean;
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

    {/* Selection buttons */}
    const buttonActions = [
        {
            title: t('common.select.all'),
            icon: "check_box",
            showInMenu: true,
            onClick: handleSelectAll
        },
        {
            title: t('common.select.clear'),
            icon: "check_box_outline_blank",
            showInMenu: true,
            onClick: handleClearSelection
        },
        ...actions.filter(action=>!action?.showInMenu)
            .map(({ disabled, ...rest }) => ({
                ...rest,
                disabled: (row: any) => (typeof disabled === 'function' ? disabled(row) : !!disabled) || selectedRows?.length === 0
            }))
    ]

    const menuActions = actions.filter(action=>action?.showInMenu && !(typeof action.hidden === 'function' ? action.hidden(selectedRows) : action.hidden));

    return <Load value={actions.length>0 && actions.filter(a=>!a?.hidden).length>0} noEffect>
        <Box sx={{ display: 'flex', alignItems: 'flex-start', ml: 1 }}>
        <ButtonGroup
            variant="outlined"
            size="small"
            sx={{
                '& .MuiButton-root': {
                    borderColor: 'rgba(0, 0, 0, 0.23)' // Standard MUI outlined button border color
                },
                '& .MuiButton-root:hover': {
                    borderColor: 'rgba(0, 0, 0, 0.50)' // Standard MUI outlined button border color
                },
                '& .MuiButton-root.Mui-disabled': {
                    borderColor: 'rgba(0, 0, 0, 0.1)',
                    color: 'rgba(0, 0, 0, 0.3)',
                    backgroundColor: 'rgba(0, 0, 0, 0.04)',
                },
                '& .MuiButton-root.Mui-disabled .MuiSvgIcon-root': {
                    color: 'rgba(0, 0, 0, 0.3)',
                },
            }}>
            {
                buttonActions.map((action:any, index:number)=>
                    !(typeof action.hidden === 'function' ? action.hidden(selectedRows) : action.hidden)
                    && <Tooltip title={action?.title} key={`action-${index}`}>
                        <Button
                            onClick={()=>action?.onClick?.(selectedRows)}
                            disabled={typeof action?.disabled === 'function' ? action?.disabled(selectedRows) : action?.disabled}
                            sx={{ minWidth: '40px', display: 'flex', justifyContent: 'center', alignItems: 'flex-start', paddingTop: 0.75 }}
                        >
                            <Icon color="action" sx={{m: 0}}>{action?.icon}</Icon>
                        </Button>
                    </Tooltip>
                )
            }

            {menuActions?.length === 0
                ? <Button disabled><Chip label={selectedRows?.length} size="small"/></Button>
                : <MenuActionButton
                    id={'massiveOpcions'}
                    entity={selectedRows}
                    buttonLabel={t('common.options')}
                    buttonProps={{
                        startIcon: <Chip label={selectedRows?.length} size="small"/>,
                        disabled: selectedRows?.length === 0
                    }}
                    actions={menuActions}
                />
            }
        </ButtonGroup>
    </Box></Load>;
};

export default MassiveActionSelector;
