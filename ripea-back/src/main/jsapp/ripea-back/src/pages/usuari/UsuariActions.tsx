import {useTranslation} from "react-i18next";
import {useBaseAppContext, useResourceApiService} from "reactlib";
import {useUserSession} from "../../components/Session.tsx";
import useBaixa from "./actions/Baixa.tsx";
import {usePermisosUsuariDialog} from "./PermisosUsuariDialog.tsx";

const useAlta = (refresh?: () => void) => {
    const {t} = useTranslation();
    const {artifactAction: apiAction} = useResourceApiService('usuariResource');
    const {messageDialogShow, temporalMessageShow} = useBaseAppContext();

    return (id: any, row: any) => {
        messageDialogShow(
            t('page.usuari.action.alta.title'),
            t('page.usuari.action.alta.confirm', {usuari: row?.codiAndNom ?? id}),
            [
                {value: true, text: t('page.usuari.action.alta.label'), componentProps: {variant: 'contained'}},
                {value: false, text: t('common.cancel'), componentProps: {variant: 'outlined'}},
            ],
            {maxWidth: 'sm', fullWidth: true})
            .then((value: any) => {
                if (!value) return;
                apiAction(id, {code: 'ALTA'})
                    .then(() => {
                        refresh?.();
                        temporalMessageShow(null, t('page.usuari.action.alta.ok'), 'success');
                    })
                    .catch((error: any) => temporalMessageShow(null, error?.message, 'error'));
            });
    }
}

export const useUsuariActions = (refresh?: () => void) => {
    const {t} = useTranslation();
    const {value: user} = useUserSession();
    const alta = useAlta(refresh);
    const {handleShow: handleBaixa, content: contentBaixa} = useBaixa(refresh);
    const {handleOpen: handlePermisos, dialog: dialogPermisos} = usePermisosUsuariDialog();

    const actions = [
        {
            label: t('page.usuari.action.permisos.label'),
            icon: "key",
            showInMenu: true,
            onClick: handlePermisos,
        },
        {
            label: t('page.usuari.action.baixa.label'),
            icon: "person_off",
            showInMenu: true,
            onClick: handleBaixa,
            // El backend tampoc permet que el superusuari es doni de baixa a si mateix
            hidden: (row: any) => !row?.actiu || row?.codi?.toLowerCase() === user?.codi?.toLowerCase(),
        },
        {
            label: t('page.usuari.action.alta.label'),
            icon: "person_add",
            showInMenu: true,
            onClick: alta,
            hidden: (row: any) => row?.actiu,
        },
    ]

    const components = <>
        {contentBaixa}
        {dialogPermisos}
    </>

    return {
        actions,
        components,
        handlePermisos,
    }
}
