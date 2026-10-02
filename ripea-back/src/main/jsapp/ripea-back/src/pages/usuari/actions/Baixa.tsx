import {useRef} from "react";
import {Grid} from "@mui/material";
import {useMuiFormDialogApiRef, useBaseAppContext} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import GridFormField from "../../../components/GridFormField.tsx";

const BaixaForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="motiu" type={"textarea"} required/>
    </Grid>
}

const Baixa = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"usuariResource"}
        action={"BAIXA"}
        formDialogButtons={[
            {icon: 'person_off', text: t('page.usuari.action.baixa.button'), componentProps: { variant: 'contained', color: 'error' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        {...props}
    >
        <BaixaForm/>
    </FormActionDialog>
}

const useBaixa = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    const {temporalMessageShow} = useBaseAppContext();
    // El títol es calcula en obrir el diàleg; l'usuari no s'envia com a dada del formulari
    const usuariRef = useRef<string>('');

    const handleShow = (id:any, row?:any) :void => {
        usuariRef.current = row?.codiAndNom ?? id;
        apiRef.current?.show?.(id)
    }
    const onSuccess = () :void => {
        refresh?.();
        temporalMessageShow(null, t('page.usuari.action.baixa.ok'), 'success');
    }

    return {
        handleShow,
        content: <Baixa
            apiRef={apiRef}
            title={() => t('page.usuari.action.baixa.title', {usuari: usuariRef.current})}
            onSuccess={onSuccess}/>
    }
}
export default useBaixa;
