import {useTranslation} from "react-i18next";
import {
    MuiFormDialog,
    MuiFormDialogApi,
    useBaseAppContext,
    useConfirmDialogButtons,
    useResourceApiService
} from "reactlib";
import {useRef} from "react";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import usePublicacioDetail from "./PublicacioDetail.tsx";

const RemesaGridForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="tipus" required/>
        <GridFormField xs={12} name="estat" required/>
        <GridFormField xs={12} name="assumpte" required/>
        <GridFormField xs={12} name="dataPublicacio" type={"date"}/>
        <GridFormField xs={12} name="enviatData" type={"date"} required/>
        <GridFormField xs={12} name="observacions" type={"textarea"}/>
    </Grid>
}

const usePublicacioActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {
        delete: apiDelete,
    } = useResourceApiService('documentPublicacioResource');
    const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons();
    const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};

    const apiRef = useRef<MuiFormDialogApi>();
    const {handleOpen: handleDetallOpen, dialog: dialogDetall} = usePublicacioDetail();

    const update = (id: any) => {
        apiRef.current?.show(id)
            .then(() => {
                refresh?.();
                temporalMessageShow(null, '', 'success');
            })
            .catch((error) => {
                temporalMessageShow('Error', error.message, 'error');
            });
    }
    const delette = (id: any) => {
        messageDialogShow(
            t('page.publicacio.action.delete.title'),
            t('page.publicacio.action.delete.message'),
            confirmDialogButtons,
            confirmDialogComponentProps)
            .then((value: any) => {
                if (value) {
                    apiDelete(id)
                        .then(() => {
                            refresh?.();
                            temporalMessageShow(null, '', 'success');
                        })
                        .catch((error) => {
                            temporalMessageShow('Error', error.message, 'error');
                        });
                }
            });
    }

    const actions = [
        {
            title: t('common.detail'),
            icon: "info",
            showInMenu: true,
            onClick: handleDetallOpen,
        },
        {
            title: t('common.update'),
            icon: "edit",
            showInMenu: true,
            onClick: update,
        },
        {
            title: t('common.delete'),
            icon: "delete",
            showInMenu: true,
            onClick: delette,
        },
    ];

    const components = <>
        <MuiFormDialog
            resourceName={"documentPublicacioResource"}
            title={t('page.publicacio.action.update')}
            apiRef={apiRef}
        >
            <RemesaGridForm/>
        </MuiFormDialog>
        {dialogDetall}
    </>;

    return {
        actions,
        components
    }
}
export default usePublicacioActions;