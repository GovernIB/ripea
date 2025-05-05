import { Icon, MenuItem } from "@mui/material";
import MenuButton from "../../../components/MenuButton.tsx";
import {useCommonActions} from "./CommonActions.tsx";
import {useTranslation} from "react-i18next";

const ExpedientActionButton = (props:{entity:any}) => {
    const {entity} = props;
    const { t } = useTranslation();

    const {
        actions: commonActionsActions,
        components: commonActionsComponents
    } = useCommonActions();

    return <MenuButton
        id={entity?.id}
        buttonLabel={t('common.action')}
        buttonProps={{
            startIcon:<Icon>settings</Icon>,
            sx: {borderRadius: 1},
            size: 'small',
            variant: "contained",
            disableElevation: true,
        }}
    >
        {commonActionsActions.map((action:any) =>
            action?.showInMenu
            && !(typeof action.hidden === 'function' ? action.hidden(entity) : action.hidden)
            && (action?.linkTo == null && action?.clickShowUpdateDialog == null)
            && <MenuItem onClick={()=>action?.onClick?.(entity.id, entity)} key={action.title} disabled={action?.disabled==true || action?.disabled?.(entity)}>
                {action.icon && <Icon>{action.icon}</Icon>}{action.title}
            </MenuItem>
        )}

        {commonActionsComponents}
    </MenuButton>
}
export default ExpedientActionButton;