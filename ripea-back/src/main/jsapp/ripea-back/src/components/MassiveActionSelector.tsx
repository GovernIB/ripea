import React from 'react';
import {Box, Button, ButtonGroup, Chip, Icon, Tooltip, Typography} from '@mui/material';
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
    namedQueries?: string[];
    disabledDefSelector?: boolean,
    hiddenDefSelector?: boolean,
    isRowSelectable?: (row:any) => boolean
}

const MassiveActionSelector: React.FC<MassiveActionSelectorProps> = (props:MassiveActionSelectorProps) => {
    const {resourceName, filter, namedQueries, selectedRows, setSelectedRows, disabledDefSelector, hiddenDefSelector, actions, isRowSelectable } = props;
    const { t } = useTranslation();

    const {
        isReady: apiIsReady,
        find: apiFindAll,
    } = useResourceApiService(resourceName);

    // Handle selection actions
    const handleSelectAll = () => {
        if (apiIsReady) {
            apiFindAll({unpaged: true, filter: filter, namedQueries: namedQueries})
                .then((app) => {
                    const allIds = app?.rows
                        ?.filter(row=> isRowSelectable ?isRowSelectable?.({row}) :true)
                        ?.map(row=>row?.id)
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
            label: t('common.select.all'),
            icon: "check_box",
            showInMenu: true,
            onClick: handleSelectAll,
            disabled: disabledDefSelector,
            hidden: hiddenDefSelector,
        },
        {
            label: t('common.select.clear'),
            icon: "check_box_outline_blank",
            showInMenu: true,
            onClick: handleClearSelection,
            disabled: disabledDefSelector,
            hidden: hiddenDefSelector,
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
            className={'massive-selector'}
        >
            {
                buttonActions.map((action:any, index:number)=>
                    !(typeof action.hidden === 'function' ? action.hidden(selectedRows) : action.hidden)
                    && <Tooltip title={action?.label} key={`action-${index}`}>
                        <Button
                            onClick={()=> action?.onClick?.(selectedRows)}
                            disabled={typeof action?.disabled === 'function' ? action?.disabled(selectedRows) : action?.disabled}
                            sx={{ minWidth: '40px', maxHeight: '32.5px', display: 'flex', justifyContent: 'center', alignItems: 'center' }}
                        >
                            <Icon color="action" sx={{m: 0}}>{action?.icon}</Icon>
                            {action?.title && <Typography variant={'body2'} sx={{display: {xs: 'none', sm: 'none', md: 'block'}}}
                                         ml={1}>{action?.title}</Typography>}
                        </Button>
                    </Tooltip>
                )
            }
            <Button disabled><Chip label={selectedRows?.length} size="small"/></Button>

            {menuActions?.length !== 0 &&
                <MenuActionButton
                    id={'massiveOpcions'}
                    entity={selectedRows}
                    buttonLabel={t('common.options')}
                    buttonProps={{
                        disabled: selectedRows?.length === 0
                    }}
                    actions={menuActions}
                />
            }
        </ButtonGroup>
    </Box></Load>;
};

export default MassiveActionSelector;
