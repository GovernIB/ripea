import {FormControl, Icon, ListItemIcon, MenuItem, Select} from "@mui/material";
import {useEffect, useMemo, useState} from "react";
import {useTranslation} from "react-i18next";
import {useEntitatSession, useUserSession} from "./Session.tsx";
import {useNavigate} from "react-router-dom";
import {iniciaDescarga} from "../pages/expedient/details/CommonActions.tsx";
import usePerfil from "../pages/user/detail/Perfil.tsx";
import ListItemText from "@mui/material/ListItemText";
import Divider from "@mui/material/Divider";

const MenuSelect = (props:any) => {
    const {icon, value, onChange, color = "white", children, ...other} = props
    const [open, setOpen] = useState<boolean>(false)

    return <MenuItem onClick={()=>setOpen(prev=>!prev)}>
        <ListItemIcon>
            <Icon fontSize="small">{icon}</Icon>
        </ListItemIcon>

        <FormControl sx={{ minWidth: 90 }} size="small">
            <Select
                open={open}
                size="small" variant="standard"
                disableUnderline
                // startAdornment={icon}
                value={value}
                onChange={(event) => onChange(event.target.value)}
                sx={{
                    color: color,
                    backgroundColor: 'transparent',

                    '.MuiSelect-select.MuiSelect-standard': {
                        paddingBottom: '0 !important',
                    },
                    '.MuiOutlinedInput-notchedOutline': {
                        border: 'none',
                    },
                    '.MuiSvgIcon-root ': {
                        fill: `${color} !important`,
                    }
                }}
                {...other}
            >
                {children}
            </Select>
        </FormControl>
    </MenuItem>
}

const UserMenu = (props:any) => {
    const {color = "#000"} = props;
    const { t } = useTranslation();

    const { value: entitat } = useEntitatSession()
    const navigate = useNavigate();
    const textColor = entitat?.capsaleraColorLletra ?? color;

    const { value: user, permisos, save: apiSave } = useUserSession();
    const isRolActualSupAdmin = user?.rolActual == 'IPA_SUPER';
    const isRolActualAdmin = user?.rolActual == 'IPA_ADMIN';
    const isRolActualOrganAdmin = user?.rolActual == 'IPA_ORGAN_ADMIN';
    const isRolActualDissenyOrgan = user?.rolActual == 'IPA_DISSENY';

    const {handleOpen, dialog} = usePerfil();

    const [entitatId, setEntitatId] = useState<number>(user?.entitatActualId);
    const entitats :any[] = useMemo(()=>{
        return user?.permisosEntitat ?Object.values(user?.permisosEntitat) :[]
    }, [user])

    const [organId, setOrganId] = useState<number>(user?.organActualId);

    const [rol, setRol] = useState<string>(user?.rolActual)

    useEffect(() => {
        if (user){
            setEntitatId(user?.entitatActualId)
            setOrganId(user?.organActualId)
            setRol(user?.rolActual)
        }
    }, [user]);

    useEffect(() => {
        if (entitatId && entitatId!=user?.entitatActualId){
            apiSave({canviEntitat: entitatId})
        }
    }, [entitatId]);

    useEffect(() => {
        if (organId && organId!=user?.organActualId){
            apiSave({canviOrganGestor: organId})
        }
    }, [organId]);

    useEffect(() => {
        if (rol && rol!=user?.rolActual){
            apiSave({canviRol: rol})
            navigate('/expedient')
        }
    }, [rol]);


    // <MenuItem onClick={() => signOut?.()}>
    //     <ListItemIcon>
    //         <Icon fontSize="small">logout</Icon>
    //     </ListItemIcon>
    //     <ListItemText>{t('app.auth.logout')}</ListItemText>
    // </MenuItem>

    return <>
        <MenuItem onClick={handleOpen}>
            <ListItemIcon><Icon fontSize={"small"}>person</Icon></ListItemIcon>
            <ListItemText>{t('page.user.options.perfil')}</ListItemText>
        </MenuItem>{dialog}

        {(isRolActualSupAdmin || isRolActualAdmin || isRolActualOrganAdmin) &&
            <MenuItem onClick={()=>{
                const url = 'https://github.com/GovernIB/ripea/raw/ripea-1.0/doc/pdf/02_ripea_manual_administradors.pdf';
                iniciaDescarga(url, '02_ripea_manual_administradors.pdf')
            }}>
                <ListItemIcon><Icon fontSize={"small"}>download</Icon></ListItemIcon>
                <ListItemText>{t('page.user.options.manualAdmin')}</ListItemText>
            </MenuItem>}

        <MenuItem onClick={()=>{
            const url = 'https://github.com/GovernIB/ripea/raw/ripea-1.0/doc/pdf/01_ripea_manual_usuari.pdf';
            iniciaDescarga(url, '01_ripea_manual_usuari.pdf')
        }}>
            <ListItemIcon><Icon fontSize={"small"}>download</Icon></ListItemIcon>
            <ListItemText>{t('page.user.options.manual')}</ListItemText>
        </MenuItem>

        <Divider/>

        <MenuSelect
            value={entitatId}
            onChange={setEntitatId}
            icon={<Icon fontSize={"inherit"}>account_balance</Icon>}
            color={textColor}
            hidden={isRolActualSupAdmin}
        >
            {
                entitats?.map((entitat: any) =>
                    <MenuItem key={entitat?.entitatCodi} value={entitat?.entitatId}><ListItemText>{entitat?.entitatNom}</ListItemText></MenuItem>
                )
            }
        </MenuSelect>

        <MenuSelect
            value={rol}
            onChange={setRol}
            icon={<Icon fontSize={"inherit"}>badge</Icon>}
            color={textColor}
        >
            {
                user?.rols?.map((rol:any) =>
                    <MenuItem key={rol} value={rol}><ListItemText>{t(`enum.rol.${rol}`)}</ListItemText></MenuItem>
                )
            }
        </MenuSelect>

        { (isRolActualOrganAdmin || isRolActualDissenyOrgan) &&
            <MenuSelect
                value={organId}
                onChange={setOrganId}
                icon={<Icon fontSize={"inherit"}>apartment</Icon>}
                color={textColor}
            >
                {
                    permisos?.organs?.map((rol:any) =>
                        <MenuItem key={rol.codi} value={rol.id}><ListItemText>{rol.nom}</ListItemText></MenuItem>
                    )
                }
            </MenuSelect>
        }
    </>
}
export default UserMenu;