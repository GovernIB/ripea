import React from "react";
import {Button, Icon, Menu, MenuItem} from "@mui/material";

type MenuButtonProps = {
    id: string;
    hidden?: boolean;
    children?: any;
    buttonLabel?: string;
    buttonProps?: any;
    menuProps?: any;
    arrowDown?: string;
    arrowUp?: string;
}

const MenuButton = (props:MenuButtonProps) => {
    const {
        id,
        hidden,
        children,
        buttonLabel,
        buttonProps,
        menuProps,
        arrowDown = 'arrow_drop_down',
        arrowUp = 'arrow_drop_up',
    } = props;
    const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
    const open = Boolean(anchorEl);
    const handleClick = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorEl(event.currentTarget);
    };
    const handleClose = () => {
        setAnchorEl(null);
    };

    if (hidden){
        return <></>
    }

    return(<>
        <Button
            id={`menu-button-${id}`}
            aria-controls={open ? 'demo-customized-menu' : undefined}
            aria-haspopup="true"
            aria-expanded={open ? 'true' : undefined}
            onClick={handleClick}

            endIcon={<Icon>{open ? arrowUp : arrowDown}</Icon>}
            {...buttonProps}
        >
            {buttonLabel}
        </Button>
        <Menu
            id={`menu-button-${id}`}
            MenuListProps={{
                'aria-labelledby': 'demo-customized-button',
            }}
            anchorEl={anchorEl}
            open={open}
            onClose={handleClose}

            elevation={0}
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
            {children}
        </Menu>
    </>)
}

type MenuActionButtonProps = MenuButtonProps & {
    actions: any[];
    entity?: any;
}
export const MenuActionButton = (props:MenuActionButtonProps) => {
    const {
        entity,
        actions,
        children,
        ...other
    } = props;

    return <MenuButton {...other}>
        {actions.map((action:any) =>
                // action?.showInMenu
                !(typeof action.hidden === 'function' ? action.hidden(entity) : action.hidden)
                && (!action?.linkTo && !action?.clickShowUpdateDialog)
                && <MenuItem onClick={()=>
                    entity?.id
                        ? action?.onClick?.(entity?.id, entity)
                        : action?.onClick?.(entity)
                } key={action.title} disabled={action?.disabled==true || action?.disabled?.(entity)}>
                    {action.icon && <Icon>{action.icon}</Icon>}{action.title}
                </MenuItem>
        )}
        {children}
    </MenuButton>
}
export default MenuButton;