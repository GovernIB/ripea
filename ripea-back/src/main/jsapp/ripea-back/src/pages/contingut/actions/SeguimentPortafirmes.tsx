import {useState} from "react";
import {Grid} from "@mui/material";
import {MuiDialog, useBaseAppContext, useConfirmDialogButtons, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import {DetailCard} from "../../../components/CardData.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import Load from "../../../components/Load.tsx";
import * as builder from '../../../util/springFilterUtils.ts'
import Iframe from "../../../components/Iframe.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import {FieldData, MuiDetail} from "../../../components/MuiDetail.tsx";

export const SeguimentPortafirmes = (props:any) => {
    const {entity, fields} = props;
    return <Load value={entity}>
        <MuiDetail entity={entity} fields={fields}>
            <DetailCard size={6} title={entity?.document?.description}>
                <FieldData field={"assumpte"}/>
                <FieldData field={"enviatData"}>{formatDate(entity?.enviatData)}</FieldData>
                <FieldData field={"estat"}/>
                <FieldData field={"prioritat"}/>
                <FieldData field={"documentTipusNom"}/>
                <FieldData field={"fluxTipus"}/>
                <FieldData field={"responsables"}/>
                <FieldData field={"sequenciaTipus"}/>
                <FieldData field={"portafirmesId"}/>
            </DetailCard>

            <Grid size={6}>
                <Iframe src={entity?.urlFluxSeguiment} style={{ height: '100%' }}/>
            </Grid>
        </MuiDetail>
    </Load>
}

const useActions = (refresh?: () => void) => {
    const { t } = useTranslation();
    const {
        artifactAction: apiAction,
    } = useResourceApiService('documentPortafirmesResource')
    const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons().reverse();
    const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};

    const cancelarFirma = (id:any) => {
        messageDialogShow(
            t('page.document.action.cancel.check'),
            t('page.document.action.cancel.description'),
            confirmDialogButtons,
            confirmDialogComponentProps)
            .then((value: any) => {
                if (value) {
                    apiAction(id, {code: 'CANCEL_FIRMA'})
                        .then(() => {
                            refresh?.()
                            temporalMessageShow(null, t('page.document.action.cancel.ok'), 'success');
                        })
                        .catch((error) => {
                            temporalMessageShow(null, error?.message, 'error');
                        });
                }
            });
    }

    return {
        cancelarFirma
    }
}

const useSeguimentPortafirmes = (potModificar:boolean, refresh?: () => void) => {
    const { t } = useTranslation();
    const { value: user } = useUserSession();

    const {
        isReady: apiIsReady,
        find: apiFind,
        currentFields: fields,
    } = useResourceApiService('documentPortafirmesResource')
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const {cancelarFirma} = useActions(refresh)

    const handleOpen = (id:any) => {
        if (apiIsReady && id){
            apiFind({
                filter: builder.eq('document.id', id),
                sorts: ['createdDate,desc']
            })
                .then((result) => {
                    if (result?.rows?.length>0){
                        setEntity(result?.rows[0])
                    }
                })
            setOpen(true)
        }
    }
    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setEntity(undefined);
            setOpen(false);
        }
    };

    const buttons = [
         {
            value: 'cancel',
            text: t('page.document.action.cancel.label'),
            icon: 'cancel',
            hidden: !(entity?.estat == 'ENVIAT' && potModificar && user?.rolActual != "IPA_ADMIN_LECTURA"),
            componentProps: { variant: 'contained' }
        },
        {
            value: 'close',
            text: t('common.close'),
            componentProps: { variant: 'outlined' }
        },
    ]
        .filter((button:any)=>!button?.hidden)

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.document.action.seguiment.title')}
            componentProps={{ fullWidth: true, maxWidth: 'xl'}}
            buttons={buttons}
            buttonCallback={(value :any) :void=>{
                if (value=='cancel' && entity?.estat == 'ENVIAT' && potModificar && user?.rolActual != "IPA_ADMIN_LECTURA") {
                    cancelarFirma(entity?.id)
                    handleClose();
                }
                if (value=='close') {
                    handleClose();
                }
            }}
        >
            <SeguimentPortafirmes entity={entity} fields={fields}/>
        </MuiDialog>;

    return {
        handleOpen,
        handleClose,
        dialog,
    }
}
export default useSeguimentPortafirmes;