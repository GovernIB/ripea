import {MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {useRef} from "react";
import {Grid} from "@mui/material";
import {useTranslation} from "react-i18next";
import GridFormField from "../../../components/GridFormField.tsx";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import {useUserSession} from "../../../components/Session.tsx";

const MoureForm = () => {
    const { data } = useFormContext();
    const { value: user } = useUserSession();

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="expedient" namedQueries={['AGAFAT']} hidden={user?.sessionScope?.moureMateixExpedients} required/>
        <GridFormField name="carpeta"
                       readOnly={!data?.expedient}
                       disabled={!data?.expedient}
                       filter={builder.and(
                           builder.eq('expedient.id', data?.expedient?.id),
                           builder.eq('esborrat', 0),
                       )}/>
		<GridFormField name="carpetaNova"
		   			   readOnly={!data?.expedient}
		               disabled={!data?.expedient}/>
        <GridFormField name="motiu" type={"textarea"}/>
        {/*<GridFormField name="action" required/>*/}
    </Grid>
}

const Moure = (props:any) => {
    const { t } = useTranslation();
    return <FormActionDialog
        resourceName={"carpetaResource"}
        action={'MOURE_COPIAR'}
        formDialogButtons={[
            {icon: 'save', text: t('common.save'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}        
        {...props}
    >
        <MoureForm/>
    </FormActionDialog>
}

const useAction = (code:string, title:string, onSuccess?: (result:any) => void) => {
    const apiRef = useRef<MuiFormDialogApi>();

    const handleShow = (id:any, row:any) :void => {
        const carpeta = row?.expedient?.id != row?.pare?.id ?row?.pare :null
        apiRef.current?.show?.(id, {
            expedient: row?.expedient,
            carpeta: carpeta,
            action: code,
        })
    }

    return {
        handleShow,
        content: <Moure apiRef={apiRef} title={title} onSuccess={onSuccess}/>
    }
}

export const useMoure = (refresh?: () => void) => {
    const { t } = useTranslation();
    const {temporalMessageShow} = useBaseAppContext();

    return useAction(
        'MOURE',
        t('page.contingut.action.move.title'),
        (result:any) :void => {
            refresh?.()
            temporalMessageShow(null, t('page.contingut.action.move.ok', {document: result?.nom}), 'success');
        }
    )
}
export const useCopiar = (refresh?: () => void) => {
    const { t } = useTranslation();
    const {temporalMessageShow} = useBaseAppContext();

    return useAction(
        'COPIAR',
        t('page.contingut.action.copy.title'),
        (result:any) :void => {
            refresh?.()
            temporalMessageShow(null, t('page.contingut.action.copy.ok', {document: result?.nom}), 'success');
        }
    )
}