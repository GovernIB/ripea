import {useState} from "react";
import {Alert, Grid} from "@mui/material";
import {useResourceApiService, MuiDialog, useBaseAppContext} from "reactlib";
import {useTranslation} from "react-i18next";
import Load from "../../../../../components/Load.tsx";
import {FieldData, MuiDetail} from "../../../../../components/MuiDetail.tsx";
import {formatDate} from "../../../../../util/dateUtils.ts";

const GrupDetail = (props:any) => {
    const {entity, fields} = props;
    const { t } = useTranslation();

    return <MuiDetail entity={entity} fields={fields}>
        <FieldData field={'codi'}/>
        <FieldData field={'descripcio'}/>
        <FieldData field={'organGestor'}/>

        <Grid xs={12} sx={{ pl: '8px', pt: '8px' }}>
            <Alert severity={'info'}>
                {t('common.auditoria.create', {createdDate: formatDate(entity.createdDate), createdBy: entity.createdByFullName})}
                {entity.lastModifiedDate != null &&
                    t('common.auditoria.update', {lastModifiedDate: formatDate(entity.lastModifiedDate), lastModifiedBy: entity.lastModifiedByFullName})}
            </Alert>
        </Grid>
    </MuiDetail>
}

const perspectives:any = []
const useGrupDetail = () => {
    const { t } = useTranslation();

    const {
        isReady: apiIsReady,
        getOne: apiGetOne,
        currentFields
    } = useResourceApiService('metaDadaResource');
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
        },
    ]

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.grup.detail.title')}
            componentProps={{ fullWidth: true, maxWidth: 'sm' }}
            buttons={buttons}
            buttonCallback={() :void => {
                handleClose();
            }}
        >
            <Load value={entity}>
                <GrupDetail fields={currentFields} entity={entity}/>
            </Load>
        </MuiDialog>

    return {
        apiIsReady,
        handleOpen,
        handleClose,
        dialog
    }
}
export default useGrupDetail;