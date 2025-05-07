import { Icon } from "@mui/material";
import {MenuActionButton} from "../../../components/MenuButton.tsx";
import {useCommonActions} from "./CommonActions.tsx";
import {useTranslation} from "react-i18next";

const ExpedientActionButton = (props:{entity:any}) => {
    const {entity} = props;
    const { t } = useTranslation();

    const refresh = () => {
        window.location.reload();
    }

    const {actions, components} = useCommonActions(refresh);

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
        actions={actions}
    >
        {components}
    </MenuActionButton>
}
export default ExpedientActionButton;