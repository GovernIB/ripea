import {useMemo} from "react";
import {Alert, Grid} from "@mui/material";
import {useMuiFormDialogApiRef, useBaseAppContext, useFormContext} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import GridFormField from "../../../components/GridFormField.tsx";

const AfegirJustificantForm = () => {
    const {data} = useFormContext();
    const {t} = useTranslation();

    //Memoritzat perquè el desplegable recarrega les opcions cada vegada que canvia la identitat de l'objecte.
    const requestParams = useMemo(() => ({
        expedientId: data?.expedient?.id,
    }), [data?.expedient?.id]);

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <Grid size={12}>
            <Alert severity="info">{t('page.anotacio.action.afegirJustificant.info')}</Alert>
        </Grid>
        <GridFormField name="metaDocument" requestParams={requestParams} required/>
    </Grid>
}

const AfegirJustificant = (props: any) => {
    const {t} = useTranslation();

    return <FormActionDialog
        resourceName={"expedientPeticioResource"}
        action={"AFEGIR_JUSTIFICANT"}
        title={t('page.anotacio.action.afegirJustificant.title')}
        initialOnChange
        formDialogButtons={[
            {icon: 'save', text: t('common.save'), componentProps: {variant: 'contained'}, value: true},
            {text: t('common.cancel'), componentProps: {variant: 'outlined'}, value: false},
        ]}
        {...props}
    >
        <AfegirJustificantForm/>
    </FormActionDialog>
}

const useAfegirJustificant = (refresh?: () => void) => {
    const {t} = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id: any, row: any): void => {
        apiRef.current?.show?.(id, {
            expedient: row?.expedient,
        })
    }
    const onSuccess = (): void => {
        refresh?.();
        temporalMessageShow(null, t('page.anotacio.action.afegirJustificant.ok'), 'success');
    }

    return {
        handleShow,
        content: <AfegirJustificant apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useAfegirJustificant;
