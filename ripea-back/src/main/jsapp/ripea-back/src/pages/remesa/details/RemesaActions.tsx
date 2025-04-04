import useRemesaDetail from "./RemesaDetail.tsx";
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
import useNotificacioInteressatGrid from "./NotificacioInteressatGrid.tsx";
import GridFormField from "../../../components/GridFormField.tsx";

const RemesaGridForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="tipus"/>
        <GridFormField xs={12} name="estat" disabled readOnly/>
        <GridFormField xs={12} name="interessats" multiple/>
        <GridFormField xs={12} name="assumpte"/>
        <GridFormField xs={12} name="serveiTipusEnum"/>
        <GridFormField xs={12} name="observacions"/>
        <GridFormField xs={12} name="dataProgramada" type={'date'}/>

        <GridFormField xs={12} name="caducitatDiesNaturals"/>
        <GridFormField xs={12} name="dataCaducitat" type={'date'}/>

        <GridFormField xs={12} name="retard"/>
        <GridFormField xs={12} name="entregaPostal" disabled/>
    </Grid>
}

const useRemesaActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {
        delete: apiDelete,
        artifactAction: apiAction,
    } = useResourceApiService('documentNotificacioResource');
    const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons();
    const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};

    const apiRef = useRef<MuiFormDialogApi>();
    const {handleOpen: handleDetallOpen, dialog: dialogDetall} = useRemesaDetail();
    const {handleOpen, content} = useNotificacioInteressatGrid(refresh);

    const actualitzarEstat = (id: any) => {
        apiAction(id, {code: 'ACTUALITZAR_ESTAT'})
            .then(() => {
                refresh?.();
                temporalMessageShow(null, '', 'success');
            })
            .catch((error) => {
                temporalMessageShow('Error', error.message, 'error');
            });
    }

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
            t('page.notificacio.dialog.delete.title'),
            t('page.notificacio.dialog.delete.message'),
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
            title: t('page.notificacio.acciones.actualitzarEstat'),
            icon: "sync",
            showInMenu: true,
            onClick: actualitzarEstat,
            hidden: (row:any) => row.estat == 'PROCESSADA',
        },
        {
            title: t('page.notificacio.acciones.notificacioInteressat'),
            icon: "send",
            showInMenu: true,
            onClick: handleOpen,
            hidden: (row:any) => row.estat == 'PROCESSADA',
        },
        {
            title: t('page.notificacio.acciones.justificant'),
            icon: "download",
            showInMenu: true,
            hidden: (row:any) => row.estat == 'PENDENT',
        },
        {
            title: t('page.notificacio.acciones.certificat'),
            icon: "download",
            showInMenu: true,
            hidden: () => true, /* enviamentCertificacio */
        },
        {
            title: t('common.update'),
            icon: "edit",
            showInMenu: true,
            onClick: update,
            hidden: (row:any) => row.tipus != 'MANUAL',
        },
        {
            title: t('common.delete'),
            icon: "delete",
            showInMenu: true,
            onClick: delette,
            hidden: (row:any) => row.tipus != 'MANUAL',
        },
    ];

    const components = <>
        <MuiFormDialog
            resourceName={"documentNotificacioResource"}
            title={''}
            apiRef={apiRef}
        >
            <RemesaGridForm/>
        </MuiFormDialog>
        {dialogDetall}
        {content}
    </>;

    return {
        actions,
        components
    }
}
export default useRemesaActions;