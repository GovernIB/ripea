import { Icon, MenuItem } from "@mui/material";
import MenuButton from "../../components/MenuButton.tsx";
import {useCommonActions} from "./actions/CommonActions.tsx";

const ExpedientActionButton = (props:{entity:any}) => {
    const {entity} = props;

    const {
        actions: commonActionsActions,
        components: commonActionsComponents
    } = useCommonActions();

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
        {commonActionsActions.map((action) =>
            action?.showInMenu && <MenuItem onClick={()=>action?.onClick?.(entity.id)} key={action.title} disabled={action?.disabled==true || action?.disabled?.(entity)}>
                {action.icon && <Icon>{action.icon}</Icon>}{action.title}
            </MenuItem>
        )}

        {commonActionsComponents}
    </MenuButton>
}
export default ExpedientActionButton;