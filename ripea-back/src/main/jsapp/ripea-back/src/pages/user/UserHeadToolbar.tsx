import {useNavigate} from "react-router-dom";
import {Button, Grid, Icon, Typography, MenuItem, Divider, Select, FormControl} from "@mui/material";
import MenuButton from "../../components/MenuButton.tsx";
import {StyledBadge} from "../../components/StyledBadge.tsx";
import usePerfil from "./detail/Perfil.tsx";
import {useSessionEntitat, useSessionRol, useSessionUser} from "../../components/Session.tsx";
import {useEffect, useState} from "react";

const HeaderButton = (props:any) => {
    const { children, badgeContent, onClick, ...other } = props;

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
    const { title, children, buttonProps, ...other } = props;

    return <MenuButton
        id={title}
        buttonLabel={title}
        buttonProps={{
            endIcon:<Icon>arrow_drop_down</Icon>,
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
    const {icon, value, onChange, children} = props

    return <FormControl sx={{ minWidth: 90 }} size="small">
        <Select
            startAdornment={icon}
            value={value}
            onChange={(event) => onChange(event.target.value)}
            sx={{
                color: "white",
                '.MuiOutlinedInput-notchedOutline': {
                    border: 'none',
                },
                '.MuiSvgIcon-root ': {
                    fill: "white !important",
                }
            }}
        >
            {children}
        </Select>
    </FormControl>
}

const UserHeadToolbar = () => {
    const { user } = useSessionUser();
    const navigate = useNavigate();

    const { value: sessionEntitat, save: saveSessionEntitat } = useSessionEntitat();
    const entitats:any[] = user && Object.values(user?.permisosEntitat);
    const [entitat, setEntitat] = useState(sessionEntitat || entitats?.[0]);

    const { value: sessionRol, save: saveSessionRol } = useSessionRol();
    const roles :any[] = [
        // {
        //     label: 'Super User',
        //     value: 'superusuari',
        //     hidden: !user?.superusuari
        // },
        {
            label: 'Admin',
            value: 'admin',
            hidden: !entitat?.permisAdministrador
        },
        {
            label: 'Admin Organ',
            value: 'organ',
            hidden: !entitat?.permisAdministradorOrgan || entitat?.organs
        },
        {
            label: 'User',
            value: 'usuari',
            hidden: !entitat?.permisUsuari
        }
    ].filter((rol:any)=> !rol.hidden)
    const [rol, setRol] = useState(sessionRol || roles?.[0]?.value);

    const {handleOpen, dialog} = usePerfil();

    useEffect(() => {
        if(entitat) {
            saveSessionEntitat(entitat)

            const found = roles?.find((r) => r?.value == rol)
            if (!found){
                setRol(roles?.[0]?.value)
            }
        }
    }, [entitat]);
    useEffect(() => {
        if(rol) {
            saveSessionRol(rol)
        }
    }, [rol]);

    return <Grid container rowSpacing={1} columnSpacing={1} maxWidth={'100%'}>
        <Grid item xs={12} display={'flex'} flexDirection={'row'} justifyContent={'end'}>

            <HeaderSelect
                value={entitat?.entitatId}
                onChange={(entitatId:any) => {
                    setEntitat(entitats?.find((e) => e?.entitatId == entitatId))
                }}
                icon={<Icon fontSize={"inherit"}>account_balance</Icon>}
            >
                {
                    entitats?.map((entitat: any) =>
                        <MenuItem key={entitat?.entitatCodi} value={entitat?.entitatId}>{entitat?.entitatNom}</MenuItem>
                    )
                }
            </HeaderSelect>

            <Divider orientation="vertical" variant="middle" flexItem sx={{mx :1, bgcolor: 'white'}}/>

            <HeaderSelect
                value={rol}
                onChange={setRol}
                icon={<Icon fontSize={"inherit"}>badge</Icon>}
            >
                {
                    roles.map((rol:any) =>
                        <MenuItem key={rol.value} value={rol.value}>{rol.label}</MenuItem>
                    )
                }
            </HeaderSelect>

            <Divider orientation="vertical" variant="middle" flexItem sx={{mx :1, bgcolor: 'white'}}/>

            <HeaderMenu
                title={
                    <Typography variant={"subtitle1"} display={'flex'} alignItems={'center'}>
                        <Icon fontSize={"inherit"}>person</Icon>{user?.nom}
                    </Typography>
                }
                buttonProps={{color: 'white'}}
            >
                <MenuItem onClick={handleOpen}>Perfil</MenuItem>

                {rol=='admin' && <MenuItem onClick={()=>{

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

                <MenuItem onClick={()=>{

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
                ><Icon>download</Icon>Manual de usuario</MenuItem>

                <MenuItem><Icon>logout</Icon>Desconectar</MenuItem>
            </HeaderMenu>

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
        {dialog}
    </Grid>
}
export default UserHeadToolbar;