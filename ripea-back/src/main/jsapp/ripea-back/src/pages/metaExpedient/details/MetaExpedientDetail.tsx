import {useState} from "react";
import {Alert, Grid} from "@mui/material";
import {useResourceApiService, MuiDialog, useBaseAppContext} from "reactlib";
import {useTranslation} from "react-i18next";
import Load from "../../../components/Load.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import {FieldData, MuiDetail} from "../../../components/MuiDetail.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import {DetailCard} from "../../../components/CardData.tsx";

const MetaExpedientDetail = (props:any) => {
    const {entity, fields, isOrganAdmin} = props;
    const { t } = useTranslation();

    return <MuiDetail entity={entity} fields={fields}>
        <DetailCard>
            {(entity?.revisioEstat == 'REVISAT' && isOrganAdmin) &&
                <Grid size={12}>
                    <Alert severity={'info'}>{t('page.metaExpedient.action.consultar.revisat')}</Alert>
                </Grid>
            }

            <FieldData field={'codi'}/>
            <FieldData field={'nom'}/>
            <FieldData field={'descripcio'}/>
            <FieldData field={'tipus'}/>
            <FieldData field={'revisioEstat'}/>
            <FieldData field={'tipusProcedimentServei'}/>
        </DetailCard>

        <Alert severity={'info'}>
            {t('common.auditoria.create', {createdDate: formatDate(entity.createdDate), createdBy: entity.createdByFullName})}
            {entity.lastModifiedDate != null &&
                t('common.auditoria.update', {lastModifiedDate: formatDate(entity.lastModifiedDate), lastModifiedBy: entity.lastModifiedByFullName})}
        </Alert>
    </MuiDetail>
}

const perspectives:any = []
const useMetaExpedientDetail = () => {
    const { t } = useTranslation();
    const {rol} = useUserSession()

    const {
        isReady: apiIsReady,
        getOne: apiGetOne,
        currentFields
    } = useResourceApiService('metaExpedientResource');
    const {temporalMessageShow} = useBaseAppContext();

    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const handleOpen = (id:any) => {
        if(apiIsReady && id){
            apiGetOne(id, {perspectives})
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

    const buttons :any[] = [
        {
            value: 'close',
            text: t('common.close'),
            icon: 'close',
            componentProps: { variant: 'outlined' },
        },
    ]

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.metaExpedient.action.consultar.title')}
            componentProps={{ fullWidth: true, maxWidth: 'md' }}
            buttons={buttons}
            buttonCallback={() :void => {
                handleClose();
            }}
        >
            <Load value={entity}>
                <MetaExpedientDetail fields={currentFields} entity={entity} isOrganAdmin={rol?.isOrganAdmin}/>
            </Load>
        </MuiDialog>

    return {
        apiIsReady,
        handleOpen,
        handleClose,
        dialog
    }
}
export default useMetaExpedientDetail;