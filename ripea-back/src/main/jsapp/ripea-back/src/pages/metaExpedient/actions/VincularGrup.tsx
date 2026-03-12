import {MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";

const VincularGrupForm = () => {
    const {data} = useFormContext()
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="grup" namedQueries={`VINCULAR_PROCEDIMENT#${data?.id}`}/>
        <GridFormField xs={12} name="organGestor" disabled/>
        <GridFormField xs={12} name="perDefecte"/>
    </Grid>
}

const VincularGrup = (props: any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"metaExpedientResource"}
        title={t('page.grup.action.link.title')}
        action={"VINCULAR_GRUP"}
        formDialogButtons={[
            {icon: 'add', text: t('page.grup.action.link.button'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        {...props}
    >
        <VincularGrupForm/>
    </FormActionDialog>
}

export const useVincularGrup = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any) :void => {
        apiRef.current?.show?.(id, {id})
    }
    const onSuccess = (response:any) :void => {
        refresh?.()
        temporalMessageShow(null, t('page.grup.action.link.ok', {data: response}), 'success');
    }

    return {
        handleShow,
        content: <VincularGrup apiRef={apiRef} onSuccess={onSuccess}/>
    }
}