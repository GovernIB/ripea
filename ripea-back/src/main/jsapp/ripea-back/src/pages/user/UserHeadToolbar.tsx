import {useNavigate} from "react-router-dom";
import {Button, Grid, Icon, Typography, MenuItem, Divider, Select, FormControl} from "@mui/material";
import MenuButton from "../../components/MenuButton.tsx";
import {StyledBadge} from "../../components/StyledBadge.tsx";
import usePerfil from "./detail/Perfil.tsx";
import {useSessionUser} from "../../components/Session.tsx";
import {useEffect, useMemo, useState} from "react";

const HeaderButton = (props:any) => {
    const { children, badgeContent, onClick, hidden, ...other } = props;

    if (hidden){
        return <></>
    }

    return <Button onClick={onClick} {...other} size={'small'}>
        <StyledBadge
            badgeContent={badgeContent}
            badgecolor={'white'}
            textcolor={'primary'}
            style={{textTransform: 'none'}}
        >
            {children}
        </StyledBadge>
    </Button>
}
const HeaderMenu = (props:any) => {
    const { title, children, buttonProps, hidden, ...other } = props;

    if (hidden){
        return <></>
    }

    return <MenuButton
        id={title}
        buttonLabel={title}
        buttonProps={{
            size: 'small',
            style: {textTransform: 'none'},
            ...buttonProps
        }}
        {...other}
    >
        {children}
    </MenuButton>
}
const HeaderSelect = (props:any) => {
    const {icon, value, onChange, color = "white", children} = props

    return <FormControl sx={{ minWidth: 90 }} size="small">
        <Select
            startAdornment={icon}
            value={value}
            onChange={(event) => onChange(event.target.value)}
            sx={{
                color: color,
                '.MuiOutlinedInput-notchedOutline': {
                    border: 'none',
                },
                '.MuiSvgIcon-root ': {
                    fill: `${color} !important`,
                }
            }}
        >
            {children}
        </Select>
    </FormControl>
}

const UserHeadToolbar = (props:any) => {
    const {textColor = "white"} = props;
    const { value: user, save: apiSave } = useSessionUser();
    const navigate = useNavigate();

    const [entitatId, setEntitatId] = useState<number>(user?.entitatActualId);
    const entitats :any[] = useMemo(()=>{
        return user?.permisosEntitat ?Object.values(user?.permisosEntitat) :[]
    }, [user])
    const permisoEntitat :any = useMemo(()=>{
        return entitats?.find((e: any) => e?.entitatId == entitatId)
    }, [entitatId, entitats])

    const [organId, setOrganId] = useState<number>(user?.organActualId);
    const organs :any[] = useMemo(()=>{
        return permisoEntitat?.organs
    }, [permisoEntitat])

    const [rol, setRol] = useState<string>(user?.rolActual)
    const rolsEntitat :any[] = useMemo(()=>{
        return [
            // {
            //     label: 'Super User',
            //     value: 'IPA_SUPER',
            //     hidden: !user?.superusuari
            // },
            {
                label: 'Admin',
                value: 'IPA_ADMIN',
                hidden: !permisoEntitat?.permisAdministrador
            },
            {
                label: 'Admin Organ',
                value: 'IPA_ORGAN_ADMIN',
                hidden: !permisoEntitat?.permisAdministradorOrgan || !permisoEntitat?.organs
            },
            {
                label: 'User',
                value: 'tothom',
                hidden: !permisoEntitat?.permisUsuari
            }
        ].filter((rol: any) => !rol.hidden)
    },[permisoEntitat])

    const {handleOpen, dialog} = usePerfil();

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
        }
    }, [rol]);

    return <Grid container rowSpacing={1} columnSpacing={1}>
        <Grid item xs={12} display={'flex'} flexDirection={'row'} justifyContent={'end'}>
            <HeaderSelect
                value={entitatId}
                onChange={setEntitatId}
                icon={<Icon fontSize={"inherit"}>account_balance</Icon>}
                color={textColor}
            >
                {
                    entitats?.map((entitat: any) =>
                        <MenuItem key={entitat?.entitatCodi} value={entitat?.entitatId}>{entitat?.entitatNom}</MenuItem>
                    )
                }
            </HeaderSelect>

            <Divider orientation="vertical" variant="middle" flexItem sx={{mx :1, bgcolor: textColor}}/>

            {user?.rolActual == 'IPA_ORGAN_ADMIN' &&
                <>
                    <HeaderSelect
                        value={organId}
                        onChange={setOrganId}
                        icon={<Icon fontSize={"inherit"}>badge</Icon>}
                        color={textColor}
                    >
                        {
                            organs?.map((rol:any) =>
                                <MenuItem key={rol.codi} value={rol.id}>{rol.nom}</MenuItem>
                            )
                        }
                    </HeaderSelect>
                    <Divider orientation="vertical" variant="middle" flexItem sx={{mx :1, bgcolor: textColor}}/>
                </>
            }

            <HeaderSelect
                value={rol}
                onChange={setRol}
                icon={<Icon fontSize={"inherit"}>badge</Icon>}
                color={textColor}
            >
                {
                    rolsEntitat?.map((rol:any) =>
                        <MenuItem key={rol.value} value={rol.value}>{rol.label}</MenuItem>
                    )
                }
            </HeaderSelect>

            <Divider orientation="vertical" variant="middle" flexItem sx={{mx :1, bgcolor: textColor}}/>

            <HeaderMenu
                title={
                    <Typography variant={"subtitle1"} display={'flex'} alignItems={'center'}>
                        <Icon fontSize={"inherit"}>person</Icon>{user?.nom}
                    </Typography>
                }
                buttonProps={{
                    style: {color: textColor, textTransform: 'none'}
                }}
            >
                <MenuItem onClick={handleOpen}>Perfil</MenuItem>

                {user?.rolActual=='IPA_ADMIN' && <MenuItem onClick={()=>{

                    /* TODO: revisar */
                    const url = 'https://github.com/GovernIB/ripea/raw/ripea-0.9/doc/pdf/02_ripea_manual_administradors.pdf';
                    const link = document.createElement('a');
                    link.href = url;
                    link.download = '02_ripea_manual_administradors.pdf';
                    document.body.appendChild(link);
                    link.click();

                    // Limpieza
                    document.body.removeChild(link);
                    URL.revokeObjectURL(url);

                }}
                ><Icon>download</Icon>Manual de administrador</MenuItem>}

                <MenuItem
                    onClick={()=>{
                        /* TODO: revisar */
                        const url = 'https://github.com/GovernIB/ripea/raw/ripea-0.9/doc/pdf/01_ripea_manual_usuari.pdf';
                        const link = document.createElement('a');
                        link.href = url;
                        link.download = '01_ripea_manual_usuari.pdf';
                        document.body.appendChild(link);
                        link.click();

                        // Limpieza
                        document.body.removeChild(link);
                        URL.revokeObjectURL(url);
                    }}
                >
                    <Icon>download</Icon>Manual de usuario
                </MenuItem>

                <MenuItem><Icon>logout</Icon>Desconectar</MenuItem>
            </HeaderMenu>
            {dialog}
        </Grid>

        <Grid item xs={12} display={'flex'} flexDirection={'row'} justifyContent={'end'}>
            <HeaderButton onClick={()=>{navigate('/expedient')}} variant={"contained"}>
                <Typography display={'inline'} variant={'subtitle2'}>Expedientes</Typography>
            </HeaderButton>
            <HeaderButton badgeContent={3} variant={"contained"}>
                <Typography display={'inline'} variant={'subtitle2'}>Anotaciones</Typography>
            </HeaderButton>
            <HeaderButton badgeContent={1} variant={"contained"}>
                <Typography display={'inline'} variant={'subtitle2'}>Tareas</Typography>
            </HeaderButton>

            <HeaderMenu title={"Consultar"} buttonProps={{variant: "contained"}}>
                <MenuItem>Datos estadisticos</MenuItem>
                <MenuItem>Documentos enviados a Porafib</MenuItem>
                <MenuItem>Remesas enviadas a Notib</MenuItem>
            </HeaderMenu>
            <HeaderMenu title={"Acción masiva"} buttonProps={{variant: "contained"}}>
                <MenuItem>Enviar documents al portafirmes</MenuItem>
                <MenuItem>Firmar documents des del navegador</MenuItem>
                <MenuItem>Canvi d'estat d'expedients</MenuItem>
                <MenuItem>Tancament d'expedients</MenuItem>
                <MenuItem>Custodiar elements pendents</MenuItem>
                <MenuItem>Copiar enllaç CSV</MenuItem>
                <MenuItem>Adjuntar annexos pendents d'anotacions acceptades</MenuItem>
                <MenuItem>Canviar prioritat d'expedients</MenuItem>
                <Divider/>
                <MenuItem>Consultar accions massives</MenuItem>
            </HeaderMenu>
        </Grid>
    </Grid>
}
export default UserHeadToolbar;