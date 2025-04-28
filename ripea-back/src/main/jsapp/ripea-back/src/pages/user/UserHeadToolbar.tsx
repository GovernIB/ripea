import {useNavigate} from "react-router-dom";
import {Button, Grid, Icon, Typography, MenuItem, Divider, Select, FormControl, ButtonGroup} from "@mui/material";
import MenuButton from "../../components/MenuButton.tsx";
import {StyledBadge} from "../../components/StyledBadge.tsx";
import usePerfil from "./detail/Perfil.tsx";
import {useEffect, useMemo, useState} from "react";
import {useEntitatSession, useUserSession} from "../../components/Session.tsx";
import {useTranslation} from "react-i18next";
import Load from "../../components/Load.tsx";

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
    const {icon, value, onChange, color = "white", children, ...other} = props

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
            {...other}
        >
            {children}
        </Select>
    </FormControl>
}

const UserHeadToolbar = (props:any) => {
    const {color = "white"} = props;
    const { t } = useTranslation();

    const { value: entitat } = useEntitatSession()
    const navigate = useNavigate();
    const textColor = entitat?.capsaleraColorLletra ?? color;

    const { value: user, permisos, save: apiSave, remove: logOut } = useUserSession();

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

    const {handleOpen, dialog} = usePerfil();

    const isRolActualSupAdmin = user?.rolActual == 'IPA_SUPER';
    const isRolActualAdmin = user?.rolActual == 'IPA_ADMIN';
    const isRolActualOrganAdmin = user?.rolActual == 'IPA_ORGAN_ADMIN';
    const isRolActualDissenyOrgan = user?.rolActual == 'IPA_DISSENY';
    const isRolActualRevisor = user?.rolActual == 'IPA_REVISIO';
    const isRolActualUser = user?.rolActual == 'tothom';

    return <Load value={entitatId || organId || rol} noEffect>
        <Grid container rowSpacing={1} columnSpacing={1} xs={8}>
        <Grid item xs={12} display={'flex'} flexDirection={'row'} justifyContent={'end'}>
            { !isRolActualSupAdmin &&
                <>
                    <HeaderSelect
                        value={entitatId}
                        onChange={setEntitatId}
                        icon={<Icon fontSize={"inherit"}>account_balance</Icon>}
                        color={textColor}
                        hidden={isRolActualSupAdmin}
                    >
                        {
                            entitats?.map((entitat: any) =>
                                <MenuItem key={entitat?.entitatCodi} value={entitat?.entitatId}>{entitat?.entitatNom}</MenuItem>
                            )
                        }
                    </HeaderSelect>

                    <Divider orientation="vertical" variant="middle" flexItem sx={{mx :1, bgcolor: textColor}}/>
                </>
            }

            { (isRolActualOrganAdmin || isRolActualDissenyOrgan) &&
                <>
                    <HeaderSelect
                        value={organId}
                        onChange={setOrganId}
                        icon={<Icon fontSize={"inherit"}>badge</Icon>}
                        color={textColor}
                    >
                        {
                            permisos?.organs?.map((rol:any) =>
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
                    user?.rols?.map((rol:any) =>
                        <MenuItem key={rol} value={rol}>{t(`enum.rol.${rol}`)}</MenuItem>
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
                {dialog}

                {(isRolActualSupAdmin || isRolActualAdmin || isRolActualOrganAdmin) &&
                    <MenuItem onClick={()=>{
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

                <MenuItem onClick={logOut}><Icon>logout</Icon>Desconectar</MenuItem>
            </HeaderMenu>
        </Grid>

        <Grid item xs={12} display={'flex'} flexDirection={'row'} justifyContent={'end'}>
            <ButtonGroup>
            { isRolActualSupAdmin && <MenuSupAdmin/> }
            { isRolActualAdmin && <MenuAdmin sessionScope={user?.sessionScope}/> }
            { isRolActualOrganAdmin && <MenuAdminOrgan sessionScope={user?.sessionScope}/> }
            { isRolActualDissenyOrgan && <MenuDissenyOrgan/> }
            { isRolActualUser && <MenuUsuari sessionScope={user?.sessionScope}/> }

            {
                (
                    isRolActualAdmin
                    || isRolActualOrganAdmin
                    || isRolActualUser
                ) && <AccionesMassivas isRolActualAdmin={isRolActualAdmin} sessionScope={user?.sessionScope}/>
            }

            { isRolActualRevisor && <MenuRevisor/> }
            </ButtonGroup>
        </Grid>
    </Grid>
    </Load>
}

const MenuSupAdmin = () => {
    const navigate = useNavigate();

    return <>
        <HeaderButton onClick={()=>{navigate('/expedient')}} variant={"contained"}>
            <Typography display={'inline'} variant={'subtitle2'}>Expedientes</Typography>
        </HeaderButton>

        <HeaderMenu title={"Monitorizar"} buttonProps={{variant: "contained"}}>
            <MenuItem>Integraciones</MenuItem>
            <MenuItem>Excepciones</MenuItem>
            <MenuItem>Monitor de sistema</MenuItem>
        </HeaderMenu>

        <HeaderMenu title={"Configuración"} buttonProps={{variant: "contained"}}>
            <MenuItem>Propiedades configurables</MenuItem>
            <MenuItem>Servicios PINBAL</MenuItem>
            <MenuItem>Reiniciar tareas en segundo plano</MenuItem>
            <MenuItem>Reiniciar plugins</MenuItem>
        </HeaderMenu>

        <HeaderButton variant={"contained"}>
            <Typography display={'inline'} variant={'subtitle2'}>Avisos</Typography>
        </HeaderButton>
    </>
}
const MenuAdmin = (props:any) => {
    const {sessionScope} = props;
    const navigate = useNavigate();

    return <>
        <HeaderButton onClick={()=>{navigate('/expedient')}} variant={"contained"}>
            <Typography display={'inline'} variant={'subtitle2'}>Expedientes</Typography>
        </HeaderButton>
        <HeaderButton badgeContent={sessionScope?.countAnotacionsPendents} variant={"contained"}>
            <Typography display={'inline'} variant={'subtitle2'}>Anotaciones</Typography>
        </HeaderButton>

        <HeaderMenu title={"Configurar"} buttonProps={{variant: "contained"}}>
            <MenuItem>
                <StyledBadge badgeContent={sessionScope?.organsNoSincronitzats} title={"La entidad tiene procedimientos con órganos gestores no actualizados"} sx={{pl: 0}}>
                    Procedimientos
                </StyledBadge>
            </MenuItem>
            { sessionScope?.isDocumentsGeneralsEnabled &&
                <MenuItem>Tipos de documentos</MenuItem>
            }

            <Divider/>

            { sessionScope?.isTipusDocumentsEnabled &&
                <MenuItem>Tipos documentales NTI</MenuItem>
            }
            { sessionScope?.isDominisEnabled &&
                <MenuItem>Dominios</MenuItem>
            }
            <MenuItem>Grupos</MenuItem>
            <MenuItem>Órganos gestores</MenuItem>
            { sessionScope?.urlsInstruccioActiu &&
                <MenuItem>URLs instrucción</MenuItem>
            }

            <Divider/>

            <MenuItem>Permisos de la entidad</MenuItem>
        </HeaderMenu>
        <HeaderMenu title={"Consultar"} buttonProps={{variant: "contained"}}>
            <MenuItem>Contenidos</MenuItem>
            <MenuItem>Datos estadisticos</MenuItem>
            { sessionScope?.revisioActiva &&
                <MenuItem>Revisión de procedimientos</MenuItem>
            }
            <MenuItem>Documentos enviados a Porafib</MenuItem>
            <MenuItem>Remesas enviadas a Notib</MenuItem>
            <MenuItem>Consultas enviadas a PINBAL</MenuItem>
            <MenuItem>Asignación de tareas</MenuItem>
            <MenuItem>Expedientes pendientes de distribución</MenuItem>
            <MenuItem>Anotaciones comunicadas</MenuItem>
        </HeaderMenu>
    </>
}
const MenuAdminOrgan = (props:any) => {
    const {sessionScope} = props;
    const navigate = useNavigate();

    return <>
        <HeaderButton onClick={()=>{navigate('/expedient')}} variant={"contained"}>
            <Typography display={'inline'} variant={'subtitle2'}>Expedientes</Typography>
        </HeaderButton>
        <HeaderButton badgeContent={sessionScope?.countAnotacionsPendents} variant={"contained"}>
            <Typography display={'inline'} variant={'subtitle2'}>Anotaciones</Typography>
        </HeaderButton>

        <HeaderMenu title={"Configurar"} buttonProps={{variant: "contained"}}>
            <MenuItem>
                <StyledBadge badgeContent={sessionScope?.organsNoSincronitzats} title={"La entidad tiene procedimientos con órganos gestores no actualizados"} sx={{pl: 0}}>
                    Procedimientos
                </StyledBadge>
            </MenuItem>
            <MenuItem>Grupos</MenuItem>
        </HeaderMenu>
    </>
}
const MenuDissenyOrgan = () => {
    return <>
        <HeaderButton variant={"contained"}>
            <Typography display={'inline'} variant={'subtitle2'}>Procedimientos</Typography>
        </HeaderButton>
        <HeaderButton variant={"contained"}>
            <Typography display={'inline'} variant={'subtitle2'}>Grupos</Typography>
        </HeaderButton>
    </>
}
const MenuUsuari = (props:any) => {
    const {sessionScope} = props;
    const navigate = useNavigate();

    return <>
        <HeaderButton onClick={()=>{navigate('/expedient')}} variant={"contained"}>
            <Typography display={'inline'} variant={'subtitle2'}>Expedientes</Typography>
        </HeaderButton>
        <HeaderButton badgeContent={sessionScope?.countAnotacionsPendents} variant={"contained"}>
            <Typography display={'inline'} variant={'subtitle2'}>Anotaciones</Typography>
        </HeaderButton>
        <HeaderButton badgeContent={sessionScope?.countTasquesPendent} variant={"contained"}>
            <Typography display={'inline'} variant={'subtitle2'}>Tareas</Typography>
        </HeaderButton>
        { sessionScope?.isCreacioFluxUsuariActiu &&
            <HeaderButton variant={"contained"}>
                <Typography display={'inline'} variant={'subtitle2'}>Flujos de firma</Typography>
            </HeaderButton>
        }
        { (sessionScope?.teAccesEstadistiques || sessionScope?.isMostrarSeguimentEnviamentsUsuariActiu) &&
            <HeaderMenu title={"Consultar"} buttonProps={{variant: "contained"}}>
                { sessionScope?.teAccesEstadistiques &&
                    <MenuItem>Datos estadisticos</MenuItem>
                }
                { sessionScope?.isMostrarSeguimentEnviamentsUsuariActiu &&
                    <>
                        <MenuItem>Documentos enviados a Portafib</MenuItem>
                        <MenuItem>Remesas enviadas a Notib</MenuItem>
                    </>
                }
            </HeaderMenu>
        }
    </>
}

const AccionesMassivas = (props:any) => {
    const {isRolActualAdmin, sessionScope} = props;

    return <HeaderMenu title={"Acción masiva"} buttonProps={{variant: "contained"}}>
        <MenuItem>Enviar documentos al portafirmas</MenuItem>
        <MenuItem>Firmar documentos desde el navegador</MenuItem>
        { sessionScope?.isConvertirDefinitiuActiu &&
            <MenuItem>Marcar como definitivos</MenuItem>
        }
        <MenuItem>Cambio de estado de expedientes</MenuItem>
        <MenuItem>Cierre de expedientes</MenuItem>
        <MenuItem>Custodiar elementos pendientes</MenuItem>
        { sessionScope?.isUrlValidacioDefinida &&
            <MenuItem>Copiar enlace CSV</MenuItem>
        }
        <MenuItem>Adjuntar anexos pendientes de anotaciones aceptadas</MenuItem>
        { isRolActualAdmin &&
            <MenuItem>Actualizar estado de las anotaciones en Distribución</MenuItem>
        }
        <MenuItem>Cambiar prioridad de expedientes</MenuItem>
        <Divider/>
        <MenuItem>Consultar acciones masivas</MenuItem>
    </HeaderMenu>
}
const MenuRevisor = () => {
    return <>
        <HeaderButton variant={"contained"}>
            <Typography display={'inline'} variant={'subtitle2'}>Revisión de procedimientos</Typography>
        </HeaderButton>
    </>
}
export default UserHeadToolbar;