import { Icon, MenuItem, Divider} from "@mui/material";
import MenuButton from "../../components/MenuButton.tsx";
import { useNavigate } from "react-router-dom";

const ExpedientActionButton = (props:{entity:any}) => {
    const {entity} = props;
    let navigate = useNavigate();

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
        <MenuItem onClick={()=>navigate(`/contingut/${entity?.id}`)}><Icon>folder</Icon>Gestionar</MenuItem>
        <MenuItem><Icon>person_add</Icon>Seguir</MenuItem>

        <Divider sx={{ my: 0.5 }} />

        <MenuItem><Icon>edit</Icon>Modificar...</MenuItem>

        <Divider sx={{ my: 0.5 }} />

        <MenuItem><Icon>lock</Icon>Coger</MenuItem>
        <MenuItem><Icon>lock_open</Icon>Liberar</MenuItem>
        <MenuItem><Icon></Icon>Cambiar prioridad...</MenuItem>
        <MenuItem><Icon></Icon>Cambiar estado...</MenuItem>
        <MenuItem><Icon>link</Icon>Relacionar...</MenuItem>
        <MenuItem><Icon>check</Icon>Cerrar...</MenuItem>
        <MenuItem><Icon>delete</Icon>Borrar</MenuItem>

        <Divider sx={{ my: 0.5 }} />

        <MenuItem><Icon>list</Icon>Histórico de acciones</MenuItem>
        <MenuItem><Icon>download</Icon>Descargar documentos...</MenuItem>
        <MenuItem><Icon>format_list_numbered</Icon>Exportar indice PDF...</MenuItem>
        <MenuItem disabled><Icon>format_list_numbered</Icon>Indice PDF y exportación EIN...</MenuItem>
        <MenuItem disabled><Icon>info</Icon>Información archivo</MenuItem>
        <MenuItem><Icon>autorenew</Icon>Sincronizar estado con archivo</MenuItem>
    </MenuButton>
}
export default ExpedientActionButton;