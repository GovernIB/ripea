import { Icon } from "@mui/material";
import {MenuActionButton} from "../../../components/MenuButton.tsx";
import {useCommonActions} from "./CommonActions.tsx";
import {useTranslation} from "react-i18next";
import useModifyExpedient from "../actions/ModifyExpedient.tsx";

const ExpedientActionButton = (props:{entity:any}) => {
    const {entity} = props;
    const { t } = useTranslation();

    const refresh = () => {
        window.location.reload();
    }

    const {actions, components} = useCommonActions(refresh);
    const {handleShow: handleModifyExpedient, content: contentModifyExpedient} = useModifyExpedient(refresh)

    const additionalActions = actions.map((a:any)=>{
        if (a?.clickShowUpdateDialog){
            return {
                ...a,
                clickShowUpdateDialog: false,
                onClick: handleModifyExpedient,
            }
        }
        return a;
    })

    return <MenuActionButton
        id={'accionsExpedient'}
        entity={entity}
        buttonLabel={t('common.action')}
        buttonProps={{
            startIcon:<Icon>settings</Icon>,
            sx: {borderRadius: 1},
            size: 'small',
            variant: "contained",
            disableElevation: true,
        }}
        actions={additionalActions}
    >
        {components}
        {contentModifyExpedient}
    </MenuActionButton>
}
export default ExpedientActionButton;