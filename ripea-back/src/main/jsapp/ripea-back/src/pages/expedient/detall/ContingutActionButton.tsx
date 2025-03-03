import { Icon, MenuItem, Divider} from "@mui/material";
import MenuButton from "../../../components/MenuButton.tsx";

const ContingutActionButton = (props:{entity:any}) => {
    const {entity} = props;

    return <MenuButton
        id={entity?.id}
        buttonLabel={"Acción"}
        buttonProps={{
            startIcon:<Icon>settings</Icon>,
            endIcon:<Icon>keyboard_arrow_down</Icon>,
            sx: {borderRadius: 1},
            size: 'small',
            variant: "contained",
            // disableElevation: true,
        }}
    >
        <MenuItem disabled><Icon>autorenew</Icon>Guardar en archivo</MenuItem>
        <MenuItem disabled><Icon>folder</Icon>Detalles</MenuItem>

        <Divider sx={{ my: 0.5 }} />

        <MenuItem><Icon>download</Icon>Descargar</MenuItem>
        <MenuItem><Icon>search</Icon>Visualizar</MenuItem>
        <MenuItem><Icon>email</Icon>Enviar via email...</MenuItem>

        <Divider sx={{ my: 0.5 }} />

        <MenuItem disabled><Icon>list</Icon>Histórico de acciones</MenuItem>
        <MenuItem disabled><Icon>info</Icon>Información archivo</MenuItem>
        <MenuItem disabled><Icon>download</Icon>Exportación ENI</MenuItem>
    </MenuButton>
}
export default ContingutActionButton;