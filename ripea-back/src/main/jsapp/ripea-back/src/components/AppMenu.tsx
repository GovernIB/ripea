import React from 'react';
import {MenuEntry} from "reactlib";
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import SideMenu from "./SideMenu.tsx";
import {useEntitatSession} from "./Session.tsx";
import {useTranslation} from "react-i18next";

const menuIcon = 'menu';

export interface AppMenuProps {
  menuEntries: MenuEntry[];
  logo?: string
}

export const AppMenu: React.FC<AppMenuProps> = ({ menuEntries, logo }) => {
  const {t} = useTranslation()
  const [open, setOpen] = React.useState(false);
  const { value: entitat } = useEntitatSession()

  const toggleMenu = () => {
    setOpen(!open);
  };

  return (
    <>
      <IconButton
        title={t('page.user.menu.title')}
        aria-label="open menu"
        onClick={toggleMenu}
        edge="start"
        // sx={{ mr: 2 }}
        style={{color: entitat?.conf?.colorLletra}}
      >
        <Icon sx={{ fontSize: '24px', m: 0 }} fontSize={'medium'}>{menuIcon}</Icon>
      </IconButton>
      {open && <SideMenu
          entries={menuEntries}
          logo={logo}
          drawerWidth={350}
          iconClicked={open}
          onTitleClose={() => setOpen(false)}
          onClose={() => setOpen(false)}
      />}
    </>
  );
};

export default AppMenu;
