import {useNavigate} from "react-router-dom";
import {Button, Grid, Icon, Typography, MenuItem, Divider, Select, FormControl, ButtonGroup} from "@mui/material";
import MenuButton from "../../components/MenuButton.tsx";
import {StyledBadge} from "../../components/StyledBadge.tsx";
import usePerfil from "./detail/Perfil.tsx";
import {useEffect, useMemo, useState} from "react";
import {useEntitatSession, useUserSession} from "../../components/Session.tsx";
import {useTranslation} from "react-i18next";
import Load from "../../components/Load.tsx";
import useExecucioMassiva from "./actions/ExecucioMassivaGrid.tsx";

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
        <Grid container rowSpacing={1} columnSpacing={1} item xs={8}>
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
                title={<><Icon fontSize={"inherit"}>person</Icon>{user?.nom}</>
                }
                buttonProps={{
                    style: {color: textColor, textTransform: 'none'}
                }}
            >
                <MenuItem onClick={handleOpen}>{t('page.user.options.perfil')}</MenuItem>
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
                ><Icon>download</Icon>{t('page.user.options.manualAdmin')}</MenuItem>}

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
                ><Icon>download</Icon>{t('page.user.options.manual')}</MenuItem>

                <MenuItem onClick={logOut}><Icon>logout</Icon>{t('page.user.options.logout')}</MenuItem>
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
    const { t } = useTranslation();
    const navigate = useNavigate();

    return <>
        <HeaderButton onClick={()=>{navigate('/expedient')}} variant={"contained"}>
            <Typography display={'inline'} variant={'subtitle2'}>{t('page.user.menu.expedient')}</Typography>
        </HeaderButton>

        <HeaderMenu title={t('page.user.menu.monitoritzar')} buttonProps={{variant: "contained"}}>
            <MenuItem>{t('page.user.menu.integracions')}</MenuItem>
            <MenuItem>{t('page.user.menu.excepcions')}</MenuItem>
            <MenuItem>{t('page.user.menu.monitor')}</MenuItem>
        </HeaderMenu>

        <HeaderMenu title={t('page.user.menu.config')} buttonProps={{variant: "contained"}}>
            <MenuItem>{t('page.user.menu.props')}</MenuItem>
            <MenuItem>{t('page.user.menu.pinbal')}</MenuItem>
            <MenuItem>{t('page.user.menu.segonPla')}</MenuItem>
            <MenuItem>{t('page.user.menu.plugins')}</MenuItem>
        </HeaderMenu>

        <HeaderButton variant={"contained"}>
            <Typography display={'inline'} variant={'subtitle2'}>{t('page.user.menu.avisos')}</Typography>
        </HeaderButton>
    </>
}
const MenuAdmin = (props:any) => {
    const {sessionScope} = props;
    const { t } = useTranslation();
    const navigate = useNavigate();

    return <>
        <HeaderButton onClick={()=>{navigate('/expedient')}} variant={"contained"}>
            <Typography display={'inline'} variant={'subtitle2'}>{t('page.user.menu.expedient')}</Typography>
        </HeaderButton>
        <HeaderButton badgeContent={sessionScope?.countAnotacionsPendents} variant={"contained"}>
            <Typography display={'inline'} variant={'subtitle2'}>{t('page.user.menu.anotacions')}</Typography>
        </HeaderButton>

        <HeaderMenu title={t('page.user.menu.config')} buttonProps={{variant: "contained"}}>
            <MenuItem>
                <StyledBadge badgeContent={sessionScope?.organsNoSincronitzats} title={t('page.user.menu.procedimentsTitle')} sx={{pl: 0}}>
                    {t('page.user.menu.procediments')}
                </StyledBadge>
            </MenuItem>
            { sessionScope?.isDocumentsGeneralsEnabled &&
                <MenuItem>{t('page.user.menu.documents')}</MenuItem>
            }

            <Divider/>

            { sessionScope?.isTipusDocumentsEnabled &&
                <MenuItem>{t('page.user.menu.nti')}</MenuItem>
            }
            { sessionScope?.isDominisEnabled &&
                <MenuItem>{t('page.user.menu.nti')}</MenuItem>
            }
            <MenuItem>{t('page.user.menu.dominis')}</MenuItem>
            <MenuItem>{t('page.user.menu.organs')}</MenuItem>
            { sessionScope?.urlsInstruccioActiu &&
                <MenuItem>{t('page.user.menu.url')}</MenuItem>
            }

            <Divider/>

            <MenuItem>{t('page.user.menu.permisos')}</MenuItem>
        </HeaderMenu>
        <HeaderMenu title={t('page.user.menu.consultar')} buttonProps={{variant: "contained"}}>
            <MenuItem>{t('page.user.menu.continguts')}</MenuItem>
            <MenuItem>{t('page.user.menu.dadesEstadistiques')}</MenuItem>
            { sessionScope?.revisioActiva &&
                <MenuItem>{t('page.user.menu.revisar')}</MenuItem>
            }
            <MenuItem>{t('page.user.menu.portafib')}</MenuItem>
            <MenuItem>{t('page.user.menu.notib')}</MenuItem>
            <MenuItem>{t('page.user.menu.pinbalEnviades')}</MenuItem>
            <MenuItem>{t('page.user.menu.assignacio')}</MenuItem>
            <MenuItem>{t('page.user.menu.pendents')}</MenuItem>
            <MenuItem>{t('page.user.menu.comunicades')}</MenuItem>
        </HeaderMenu>
    </>
}
const MenuAdminOrgan = (props:any) => {
    const {sessionScope} = props;
    const { t } = useTranslation();
    const navigate = useNavigate();

    return <>
        <HeaderButton onClick={()=>{navigate('/expedient')}} variant={"contained"}>
            <Typography display={'inline'} variant={'subtitle2'}>{t('page.user.menu.expedient')}</Typography>
        </HeaderButton>
        <HeaderButton badgeContent={sessionScope?.countAnotacionsPendents} variant={"contained"}>
            <Typography display={'inline'} variant={'subtitle2'}>{t('page.user.menu.anotacions')}</Typography>
        </HeaderButton>

        <HeaderMenu title={t('page.user.menu.config')} buttonProps={{variant: "contained"}}>
            <MenuItem>
                <StyledBadge badgeContent={sessionScope?.organsNoSincronitzats} title={t('page.user.menu.procedimentsTitle')} sx={{pl: 0}}>
                    {t('page.user.menu.procediments')}
                </StyledBadge>
            </MenuItem>
            <MenuItem>{t('page.user.menu.grups')}</MenuItem>
        </HeaderMenu>
    </>
}
const MenuDissenyOrgan = () => {
    const { t } = useTranslation();

    return <>
        <HeaderButton variant={"contained"}>
            <Typography display={'inline'} variant={'subtitle2'}>{t('page.user.menu.procediments')}</Typography>
        </HeaderButton>
        <HeaderButton variant={"contained"}>
            <Typography display={'inline'} variant={'subtitle2'}>{t('page.user.menu.grups')}</Typography>
        </HeaderButton>
    </>
}
const MenuUsuari = (props:any) => {
    const {sessionScope} = props;
    const { t } = useTranslation();
    const navigate = useNavigate();

    return <>
        <HeaderButton onClick={()=>{navigate('/expedient')}} variant={"contained"}>
            <Typography display={'inline'} variant={'subtitle2'}>{t('page.user.menu.expedient')}</Typography>
        </HeaderButton>
        <HeaderButton badgeContent={sessionScope?.countAnotacionsPendents} variant={"contained"}>
            <Typography display={'inline'} variant={'subtitle2'}>{t('page.user.menu.anotacions')}</Typography>
        </HeaderButton>
        <HeaderButton badgeContent={sessionScope?.countTasquesPendent} variant={"contained"}>
            <Typography display={'inline'} variant={'subtitle2'}>{t('page.user.menu.tasca')}</Typography>
        </HeaderButton>
        { sessionScope?.isCreacioFluxUsuariActiu &&
            <HeaderButton variant={"contained"}>
                <Typography display={'inline'} variant={'subtitle2'}>{t('page.user.menu.flux')}</Typography>
            </HeaderButton>
        }
        { (sessionScope?.teAccesEstadistiques || sessionScope?.isMostrarSeguimentEnviamentsUsuariActiu) &&
            <HeaderMenu title={t('page.user.menu.consultar')} buttonProps={{variant: "contained"}}>
                { sessionScope?.teAccesEstadistiques &&
                    <MenuItem>{t('page.user.menu.dadesEstadistiques')}</MenuItem>
                }
                { sessionScope?.isMostrarSeguimentEnviamentsUsuariActiu &&
                    <>
                        <MenuItem>{t('page.user.menu.portafib')}</MenuItem>
                        <MenuItem>{t('page.user.menu.notib')}</MenuItem>
                    </>
                }
            </HeaderMenu>
        }
    </>
}

const AccionesMassivas = (props:any) => {
    const {isRolActualAdmin, sessionScope} = props;
    const { t } = useTranslation();

    const {handleOpen, dialog} = useExecucioMassiva();

    return <HeaderMenu
        title={<Typography display={'inline'} variant={'subtitle2'}>{t('page.user.massive.title')}</Typography>}
        buttonProps={{variant: "contained"}}
    >
        <MenuItem>{t('page.user.massive.portafirmes')}</MenuItem>
        <MenuItem>{t('page.user.massive.firmar')}</MenuItem>
        { sessionScope?.isConvertirDefinitiuActiu &&
            <MenuItem>{t('page.user.massive.marcar')}</MenuItem>
        }
        <MenuItem>{t('page.user.massive.estat')}</MenuItem>
        <MenuItem>{t('page.user.massive.tancar')}</MenuItem>
        <MenuItem>{t('page.user.massive.custodiar')}</MenuItem>
        { sessionScope?.isUrlValidacioDefinida &&
            <MenuItem>{t('page.user.massive.csv')}</MenuItem>
        }
        <MenuItem>{t('page.user.massive.anexos')}</MenuItem>
        { isRolActualAdmin &&
            <MenuItem>{t('page.user.massive.anotacio')}</MenuItem>
        }
        <MenuItem>{t('page.user.massive.prioritat')}</MenuItem>
        <Divider/>
        <MenuItem onClick={handleOpen}>{t('page.user.massive.masives')}</MenuItem>
        {dialog}
    </HeaderMenu>
}
const MenuRevisor = () => {
    const { t } = useTranslation();

    return <>
        <HeaderButton variant={"contained"}>
            <Typography display={'inline'} variant={'subtitle2'}>{t('page.user.menu.revisar')}</Typography>
        </HeaderButton>
    </>
}
export default UserHeadToolbar;