import React, {ReactElement, useRef} from "react";
import {Box, Button, Icon, Menu, MenuItem} from "@mui/material";

type MenuButtonProps = {
    apiRef?: any,
    id: string;
    hidden?: boolean;
    children?: any;
    buttonLabel?: string | ReactElement;
    buttonProps?: any;
    menuProps?: any;
    arrowDown?: string;
    arrowUp?: string;
    hiddenIcon?: boolean,
    ButtonComponent?: React.ElementType;
}

const MenuButton = (props:MenuButtonProps) => {
    const defApiRef = useRef<any>({})
    const {
        apiRef = defApiRef,
        id,
        hidden,
        children,
        buttonLabel,
        buttonProps,
        menuProps,
        arrowDown = 'arrow_drop_down',
        arrowUp = 'arrow_drop_up',
        hiddenIcon = false,
        ButtonComponent = Button,
    } = props;
    const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
    const open = Boolean(anchorEl);
    const handleClick = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorEl(event.currentTarget);
    };
    const handleClose = () => {
        setAnchorEl(null);
    };

    apiRef.current = {
        handleOpen: handleClick,
        handleClose,
    }

    if (hidden){
        return <></>
    }

    return(<>
        <ButtonComponent
            id={`menu-button-${id}`}
            aria-controls={'demo-customized-menu'}
            aria-haspopup="true"
            aria-expanded={open ? 'true' : undefined}
            onClick={handleClick}

             {...(!hiddenIcon && ButtonComponent === Button
                ? { endIcon: <Icon sx={{ m: 0 }}>{open ? arrowUp : arrowDown}</Icon> }
                : {})
            }
            {...buttonProps}
        >
            {buttonLabel}
        </ButtonComponent>
        <Menu
            id={`menu-button-${id}`}
            MenuListProps={{
                'aria-labelledby': 'demo-customized-button',
            }}
            anchorEl={anchorEl}
            open={open}
            onClose={handleClose}

            elevation={3}
            anchorOrigin={{
                vertical: 'bottom',
                horizontal: 'right',
            }}
            transformOrigin={{
                vertical: 'top',
                horizontal: 'right',
            }}

            {...menuProps}
        >
            <Box>
                {children}
            </Box>
        </Menu>
    </>)
}

type MenuActionButtonProps = MenuButtonProps & {
    actions: any[];
    entity?: any;
}
export const actionToItem = (entity:any, actions:any[], handleClose?: () => void) => {
    return actions.map((action:any, index:number) =>
            !(typeof action.hidden === 'function' ? action.hidden(entity) : action.hidden)
            && (!action?.linkTo && !action?.clickShowUpdateDialog)
            && <div key={`action-${index}`} title={ typeof action.title == 'function' ?action.title?.(entity) :action.title}>
                <MenuItem onClick={() => {
                    if (entity?.id != null) {
                        action?.onClick?.(entity?.id, entity)
                    } else {
                        action?.onClick?.(entity)
                    }
                    handleClose?.()
                }} disabled={typeof action?.disabled === 'function' ? action?.disabled(entity) : action?.disabled}>
                    {action.icon && <Icon>{action.icon}</Icon>}{action.label}
                </MenuItem>
            </div>
    )
}
export const MenuActionButton = (props:MenuActionButtonProps) => {
    const defApiRef = useRef<any>({})
    const {
        apiRef = defApiRef,
        entity,
        actions,
        ...other
    } = props;

    const handleClose = () => {
        apiRef.current?.handleClose();
    }

    return <MenuButton apiRef={apiRef} {...other}>
        {actionToItem(entity, actions, handleClose)}
    </MenuButton>
}
export default MenuButton;
