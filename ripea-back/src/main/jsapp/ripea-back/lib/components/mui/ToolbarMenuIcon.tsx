import React from 'react';
import IconButton from '@mui/material/IconButton';
import Icon from '@mui/material/Icon';
import useSmallScreen from '../../util/useSmallScreen';
import { usePersistentState } from '../../util/usePersistentState';

export const PERSISTENT_MENU_SHRINK_MODE_KEY = 'menu-shrink-mode';

type UseToolbarMenuIconReturnType = {
    shrink: boolean;
    iconClicked: boolean;
    buttonComponent: React.ReactNode;
};

type ToolbarMenuIconProps = {
    icon: string;
    iconFlipX?: boolean;
    handleClick: () => void;
};

export const useToolbarMenuIcon = (appCode: string): UseToolbarMenuIconReturnType => {
    const { persistentStateReady, persistentStateGet, persistentStateSet } =
        usePersistentState(appCode);
    const smallScreen = useSmallScreen();
    const [shrink, setShrink] = React.useState<boolean>(false);
    const [iconClicked, setIconClicked] = React.useState<boolean>(false);
    const handleToolbarMenuIconClick = () => {
        if (!smallScreen) {
            setShrink(!shrink);
            persistentStateSet(PERSISTENT_MENU_SHRINK_MODE_KEY, !shrink ? 'shrink' : '');
        }
        setIconClicked((c) => !c);
    };
    const buttonComponent = (
        <ToolbarMenuIcon
            icon={smallScreen ? 'menu' : 'menu_open'}
            iconFlipX={shrink}
            handleClick={handleToolbarMenuIconClick}
        />
    );

    React.useEffect(() => {
        if (persistentStateReady) {
            const shrinkMode = persistentStateGet(PERSISTENT_MENU_SHRINK_MODE_KEY);
            if (shrinkMode === 'shrink') {
                setShrink(true);
            }
        }
    }, [persistentStateReady]);

    return {
        shrink,
        iconClicked,
        buttonComponent,
    };
};

export const ToolbarMenuIcon: React.FC<ToolbarMenuIconProps> = (props) => {
    const { icon, iconFlipX, handleClick } = props;
    return (
        <IconButton
            size="large"
            edge="start"
            color="inherit"
            aria-label="menu"
            onClick={handleClick}
            sx={{ mr: 2 }}>
            <Icon sx={iconFlipX ? { transform: 'scaleX(-1)' } : undefined}>{icon}</Icon>
        </IconButton>
    );
};

export default ToolbarMenuIcon;
