import {useState} from "react";
import {MuiDialog, useBaseAppContext, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import {CardButton, DetailCard} from "../../../components/CardData.tsx";
import AlertExpand from "../../../components/AlertExpand.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import {useActions} from "./RemesaActions.tsx";
import Load from "../../../components/Load.tsx";
import {FieldData, MuiDetail} from "../../../components/MuiDetail.tsx";
import {Box} from "@mui/material";

const RemesaDetail = (props:any) => {
    const {entity, fields} = props;
    const { t } = useTranslation();

    const {justificant, descarregarDocumentEnviat} = useActions()

    return <MuiDetail entity={entity} fields={fields}>
        <DetailCard title={t('page.notificacio.detall.notificacioDades')}
                    header={entity?.notificacioEstat != 'PENDENT' && <Box ml="auto">
                        <CardButton icon={'download'}
                                    text={t('page.notificacio.action.justificant.label')}
                                    onClick={()=>justificant(entity?.id)}/>
                    </Box>}
        >
            <FieldData field={'emisor'}/>
            <FieldData field={'assumpte'}/>
            <FieldData field={'observacions'}/>
            <FieldData field={'notificacioEstat'}/>
            <FieldData field={'createdDate'} title={t('page.notificacio.detall.createdDate')}>{formatDate(entity?.createdDate)}</FieldData>
            <FieldData field={'processatData'} hidden={!(entity?.notificacioEstat=='FINALITZADA' || entity?.notificacioEstat=='PROCESSADA')}>
                {formatDate(entity?.processatData)}</FieldData>
            <FieldData field={'tipus'}/>
            <FieldData field={'entregaPostal'} title={t('page.notificacio.detall.entregaPostal')}>{t(`enum.siNO.${entity?.entregaPostal}`)}</FieldData>
        </DetailCard>
        <DetailCard title={t('page.notificacio.detall.notificacioDocument')}
                  buttons={[
                      {
                          text: t('page.notificacio.action.documentEnviat.label'),
                          icon: 'download',
                          onClick: ()=>{descarregarDocumentEnviat(entity?.id)},
                          flex: 2,
                      },
                  ]}
        >
            <FieldData field={'fitxerNom'} title={t('page.notificacio.detall.fitxerNom')} xs={10}/>
        </DetailCard>
    </MuiDetail>
}

const useRemesaDetail = () => {
    const { t } = useTranslation();

    const {
        isReady: apiIsReady,
        getOne: apiGetOne,
        currentFields
    } = useResourceApiService('documentNotificacioResource');
    const {temporalMessageShow} = useBaseAppContext();

    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const handleOpen = (id:any) => {
        if(apiIsReady && id){
            apiGetOne(id)
                .then((app) => setEntity(app))
                .catch((error) => {
                    handleClose()
                    temporalMessageShow(null, error?.message, 'error');
                });
        }
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setEntity(undefined);
            setOpen(false);
        }
    };

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.notificacio.detall.title')}
            componentProps={{ fullWidth: true, maxWidth: 'lg'}}
            buttons={[
                {
                    value: 'close',
                    text: t('common.close'),
                    componentProps: { variant: 'outlined' }
                },
            ]}
            buttonCallback={(value :any) :void=>{
                if (value=='close') {
                    handleClose();
                }
            }}
        >
            <Load value={entity}>
                {entity?.error &&
                    <AlertExpand severity={"error"} label={t('page.notificacio.detall.error')} sx={{mb: 1}}>{entity?.errorDescripcio}</AlertExpand>
                }
                <RemesaDetail entity={entity} fields={currentFields}/>
            </Load>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}
export default useRemesaDetail;