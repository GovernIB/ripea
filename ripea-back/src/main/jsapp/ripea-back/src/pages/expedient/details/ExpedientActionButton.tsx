import { Icon, IconButton, Tooltip } from '@mui/material';
import { MenuActionButton } from '../../../components/MenuButton.tsx';
import { useCommonActions } from './CommonActions.tsx';
import { useTranslation } from 'react-i18next';

const ExpedientActionButton = (props: any) => {
    const { entity, variant = 'button', iconStyle, iconButtonStyle } = props;
    const { t } = useTranslation();
    
    const refresh = () => {
        window.location.reload();
    };
    
    const { actions, components } = useCommonActions(refresh);

    if (variant === 'icon') {
        return (
            <MenuActionButton
                id={'accionsExpedient'}
                entity={entity}
                ButtonComponent={IconButton}
                hiddenIcon
                buttonLabel={
                    <Tooltip title={t('common.action')} arrow placement="right">
                        <Icon sx={{ ...iconStyle }}>settings</Icon>
                    </Tooltip>
                }
                buttonProps={{ sx: { ...iconButtonStyle } }}
                actions={actions}
            >
                {components}
            </MenuActionButton>
        );
    }

    return (
        <MenuActionButton
            id={'accionsExpedient'}
            entity={entity}
            buttonLabel={t('common.action')}
            buttonProps={{
                startIcon: <Icon>settings</Icon>,
                sx: { borderRadius: 1 },
                size: 'small',
                variant: 'contained',
                disableElevation: true,
            }}
            actions={actions}
        >
            {components}
        </MenuActionButton>
    );
};
export default ExpedientActionButton;
