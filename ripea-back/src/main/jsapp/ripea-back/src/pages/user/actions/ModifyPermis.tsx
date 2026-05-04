import { useRef } from "react";
import { useTranslation } from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import {
    MuiFormDialogApi,
    useBaseAppContext,
    useConfirmDialogButtons,
    useFormContext,
    useResourceApiService
} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {CardData} from "../../../components/CardData.tsx";

export const usePermisActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {
        isReady: apiIsReady,
        artifactAction: apiAction,
    } = useResourceApiService('aclSidResource');
    const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons().reverse();
    const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};

    const eliminar = (id:any, data:any) :void => {
        messageDialogShow(
            t('page.permision.action.delete.check'),
            t('page.permision.action.delete.description'),
            confirmDialogButtons,
            confirmDialogComponentProps)
            .then((value: any) => {
                if (value) {
                    // classType
                    // objectId
                    apiAction(id, {code: 'DELETE_PERMISION', data})
                        .then((result) => {
                            refresh?.();
                            temporalMessageShow(null, t('page.permision.action.delete.ok', {data: result}), 'success');
                        })
                        .catch((error) => {
                            temporalMessageShow(null, error?.message, 'error');
                        });
                }
            });
    }
    return {
        apiIsReady,
        eliminar,
    }
}

const usePermisDialog = ({
                                    classType,
                                    form,
                                    isModify = false,
                                    refresh,
                                }: {
    classType: "ORGAN" | "GRUP" | "ENTITY" | "MET_NOD" | "MET_EXP_ORG";
    form: any;
    isModify?: boolean;
    refresh?: () => void;
}) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id: any, row?: any, additionalData?: any): void => {
        apiRef.current?.show?.(undefined, {
            ...(row ?? {}),
            classType,
            objectId: id,
            ...(additionalData || {})
        });
    };

    const onSuccess = (result: any): void => {
        refresh?.();
        temporalMessageShow(
            null,
            t(isModify ? "page.permision.action.update.ok" : "page.permision.action.new.ok", { data: result }),
            "success"
        );
    };

    return {
        handleShow,
        content: (
            <FormActionDialog
                resourceName="aclSidResource"
                action="MODIFY_PERMISION"
                initialOnChange
                apiRef={apiRef}
                onSuccess={onSuccess}
                title={t(
                    isModify
                        ? "page.permision.action.update.title"
                        : "page.permision.action.new.title"
                )}
                formDialogButtons={[
                    {
                        icon: "save",
                        text: t(isModify ? "common.update" : "common.save"),
                        componentProps: { variant: "contained" },
                        value: true,
                    },
                    {
                        text: t("common.cancel"),
                        componentProps: { variant: "outlined" },
                        value: false,
                    },
                ]}
            >
                {form}
            </FormActionDialog>
        ),
    };
};


// MetaExpedientOrgan
const PermisMetaExpedientOrganCreateForm = () => {
    const {data} = useFormContext()
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="principal" required disabled={data?.id}/>
        <GridFormField name="sid" disabled={data?.id}/>
        <GridFormField name="organGestor" required disabled={data?.id}/>

        <GridFormField name="all"/>
        <Grid size={2}/><GridFormField size={10} name="create"/>
        <Grid size={2}/><GridFormField size={10} name="read"/>
        <Grid size={2}/><GridFormField size={10} name="write"/>
        <Grid size={2}/><GridFormField size={10} name="delete"/>
        <Grid size={2}/><GridFormField size={10} name="estadistic"/>
    </Grid>
}
export const usePermisMetaExpedientOrganCreate = (refresh?: () => void) =>
    usePermisDialog({
        classType: "MET_EXP_ORG",
        form: <PermisMetaExpedientOrganCreateForm/>,
        refresh,
    });
export const usePermisMetaExpedientOrganModify = (refresh?: () => void) =>
    usePermisDialog({
        classType: "MET_EXP_ORG",
        form: <PermisMetaExpedientOrganCreateForm/>,
        isModify: true,
        refresh,
    });
// MetaExpedientNode
const PermisMetaExpedientNodeCreateForm = () => {
    const {data} = useFormContext()
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="principal" required disabled={data?.id}/>
        <GridFormField name="sid" disabled={data?.id}/>

        <GridFormField name="all"/>
        <Grid size={2}/><GridFormField size={10} name="create"/>
        <Grid size={2}/><GridFormField size={10} name="read"/>
        <Grid size={2}/><GridFormField size={10} name="write"/>
        <Grid size={2}/><GridFormField size={10} name="delete"/>
        <Grid size={2}/><GridFormField size={10} name="estadistic"/>
    </Grid>
}
export const usePermisMetaExpedientNodeCreate = (refresh?: () => void) =>
    usePermisDialog({
        classType: "MET_NOD",
        form: <PermisMetaExpedientNodeCreateForm/>,
        refresh,
    });
export const usePermisMetaExpedientNodeModify = (refresh?: () => void) =>
    usePermisDialog({
        classType: "MET_NOD",
        form: <PermisMetaExpedientNodeCreateForm/>,
        isModify: true,
        refresh,
    });

// OrganGestor
const PermisOrganGestorCreateForm = () => {
    const { data } = useFormContext();
    const { t } = useTranslation();
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="organGestor" disabled/>
        <GridFormField name="principal" required disabled={data?.id}/>
        <GridFormField name="sid" disabled={data?.id}/>

        <CardData size={6} title={t("page.permision.tabs.expedient")}>
            <GridFormField name="all" disabled={!!data?.admin}/>
            <Grid size={2}/><GridFormField size={10} name="read" disabled={!!data?.admin}/>
            <Grid size={2}/><GridFormField size={10} name="create" disabled={!!data?.admin}/>
            <Grid size={2}/><GridFormField size={10} name="write" disabled={!!data?.admin}/>
            <Grid size={2}/><GridFormField size={10} name="delete" disabled={!!data?.admin}/>
        </CardData>
        <CardData size={6} title={t("page.permision.tabs.admin")}>
            <GridFormField name="procedimentsComuns"/>
            <GridFormField name="admin" disabled={!!data?.adminComuns}/>
            <GridFormField name="adminComuns"/>
            <GridFormField name="disseny"/>
        </CardData>
    </Grid>
}
export const usePermisOrganGestorCreate = (refresh?: () => void) =>
    usePermisDialog({
        classType: "ORGAN",
        form: <PermisOrganGestorCreateForm/>,
        refresh,
    });
export const usePermisOrganGestorModify = (refresh?: () => void) =>
    usePermisDialog({
        classType: "ORGAN",
        form: <PermisOrganGestorCreateForm/>,
        isModify: true,
        refresh,
    });

// Grup
const PermisGrupCreateForm = () => {
    const { data } = useFormContext();
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="principal" required disabled={data?.id}/>
        <GridFormField name="sid" disabled={data?.id}/>
    </Grid>
}
export const usePermisGrupCreate = (refresh?: () => void) =>
    usePermisDialog({
        classType: "GRUP",
        form: <PermisGrupCreateForm/>,
        refresh,
    });

// Entitat
const PermisEntitatModifyForm = () => {
    const { data } = useFormContext();
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="principal" disabled={!!data?.id} required/>
        <GridFormField name="sid" disabled={!!data?.id}/>
        <GridFormField name="admin"/>
        <GridFormField name="adminLectura"/>
        <GridFormField name="user"/>
    </Grid>
}

export const usePermisEntitatCreate = (refresh?: () => void) =>
    usePermisDialog({
        classType: "ENTITY",
        form: <PermisEntitatModifyForm/>,
        refresh,
    });

export const usePermisEntitatModify = (refresh?: () => void) =>
    usePermisDialog({
        classType: "ENTITY",
        form: <PermisEntitatModifyForm/>,
        isModify: true,
        refresh,
    });