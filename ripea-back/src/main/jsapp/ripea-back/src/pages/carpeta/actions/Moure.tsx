import {MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {useRef} from "react";
import {Grid} from "@mui/material";
import {useTranslation} from "react-i18next";
import GridFormField from "../../../components/GridFormField.tsx";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import * as builder from "../../../util/springFilterUtils.ts";

const MoureForm = () => {
    const { data } = useFormContext();

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="contingut" readOnly disabled hidden={data?.massivo}/>
        <GridFormField xs={12} name="expedient" namedQueries={['AGAFAT']} required/>
        <GridFormField xs={12} name="carpeta"
                       readOnly={!data?.expedient}
                       disabled={!data?.expedient}
                       filter={builder.and(
                           builder.eq('expedient.id', data?.expedient?.id),
                           builder.eq('esborrat', false),
                       )}/>
        <GridFormField xs={12} name="motiu" type={"textarea"}/>
        {/*<GridFormField xs={12} name="action" required/>*/}
    </Grid>
}

const Moure = (props:any) => {
    return <FormActionDialog
        resourceName={"carpetaResource"}
        action={'MOURE_COPIAR'}
        {...props}
    >
        <MoureForm/>
    </FormActionDialog>
}

const useAction = (code:string, title:string, onSuccess?: (result:any) => void) => {
    const apiRef = useRef<MuiFormDialogApi>();

    const handleShow = (id:any, row:any) :void => {
        const carpeta = row?.expedient?.id != row?.pare?.id ?row?.pare :null
        apiRef.current?.show?.(undefined, {
            ids: [id],
            massivo: false,
            contingut: row?.nom,
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
const useMassiveAction = (code:string, title:string, onSuccess?: () => void) => {
    const apiRef = useRef<MuiFormDialogApi>();

    const handleMassiveShow = (ids:any[], entity:any) :void => {
        apiRef.current?.show?.(undefined, {
            ids: ids,
            massivo: true,
            expedient: {
                id: entity?.id,
                description: entity?.nom,
            },
            action: code,
        })
    }

    return {
        handleMassiveShow,
        content: <Moure apiRef={apiRef} title={title} onSuccess={onSuccess}/>
    }
}

export const useMoure = (refresh?: () => void) => {
    const { t } = useTranslation();
    const {temporalMessageShow} = useBaseAppContext();

    const {handleShow, content} = useAction(
        'MOURE',
        t('page.contingut.action.move.title'),
        (result:any) :void => {
            refresh?.()
            temporalMessageShow(null, t('page.contingut.action.move.ok', {document: result?.nom}), 'success');
        }
    )

    const {handleMassiveShow, content: massiveContent} = useMassiveAction(
        'MOURE',
        t('page.contingut.action.move.title'),
        () :void => {
            refresh?.()
            temporalMessageShow(null, t('page.expedient.results.actionBackgroundOk'), 'success');
        }
    )

    return {
        handleShow,
        handleMassiveShow,
        content: <>
            {content}
            {massiveContent}
        </>
    }
}
export const useCopiar = (refresh?: () => void) => {
    const { t } = useTranslation();
    const {temporalMessageShow} = useBaseAppContext();

    const {handleShow, content} = useAction(
        'COPIAR',
        t('page.contingut.action.copy.title'),
        (result:any) :void => {
            refresh?.()
            temporalMessageShow(null, t('page.contingut.action.copy.ok', {document: result?.nom}), 'success');
        }
    )

    const {handleMassiveShow, content: massiveContent} = useMassiveAction(
        'COPIAR',
        t('page.contingut.action.copy.title'),
        () :void => {
            refresh?.()
            temporalMessageShow(null, t('page.expedient.results.actionBackgroundOk'), 'success');
        }
    )

    return {
        handleShow,
        handleMassiveShow,
        content: <>
            {content}
            {massiveContent}
        </>
    }
}
export const useVincular = (refresh?: () => void) => {
    const { t } = useTranslation();
    const {temporalMessageShow} = useBaseAppContext();

    const {handleShow, content} = useAction(
        'VINCULAR',
        t('page.contingut.action.vincular.title'),
        (result:any) :void => {
            refresh?.()
            temporalMessageShow(null, t('page.contingut.action.vincular.ok', {document: result?.nom}), 'success');
        }
    )

    const {handleMassiveShow, content: massiveContent} = useMassiveAction(
        'VINCULAR',
        t('page.contingut.action.vincular.title'),
        () :void => {
            refresh?.()
            temporalMessageShow(null, t('page.expedient.results.actionBackgroundOk'), 'success');
        }
    )

    return {
        handleShow,
        handleMassiveShow,
        content: <>
            {content}
            {massiveContent}
        </>
    }
}