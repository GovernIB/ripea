import {useState} from "react";
import {Alert} from "@mui/material";
import {useResourceApiService, MuiDialog, useBaseAppContext} from "reactlib";
import {useTranslation} from "react-i18next";
import Load from "../../../../../components/Load.tsx";
import {FieldData, MuiDetail} from "../../../../../components/MuiDetail.tsx";
import {formatDate} from "../../../../../util/dateUtils.ts";
import {StyledBadge} from "../../../../../components/StyledBadge.tsx";
import {StyledPrioritat} from "../../../../expedient/ExpedientGrid.tsx";
import {DetailCard} from "../../../../../components/CardData.tsx";

const MetaExpTascaDetail = (props:any) => {
    const {entity, fields} = props;
    const { t } = useTranslation();

    return <MuiDetail entity={entity} fields={fields}>
        <DetailCard>
            <FieldData field={'codi'}/>
            <FieldData field={'nom'}/>
            <FieldData field={'prioritat'} renderCell={(formattedValue:any) =>
                <StyledPrioritat entity={entity}>{formattedValue}</StyledPrioritat>}/>
            <FieldData size={6} titleSize={8} textSize={4} field={'estatCrearTasca'} renderCell={(formattedValue:any) =>
                <StyledBadge textcolor={'black'} badgecolor={entity?.estatColorCrearTasca} overlap="circular" badgeContent={formattedValue}/>}/>
            <FieldData size={6} titleSize={8} textSize={4} field={'estatFinalitzarTasca'} renderCell={(formattedValue:any) =>
                <StyledBadge textcolor={'black'} badgecolor={entity?.estatColorFinalitzarTasca} overlap="circular" badgeContent={formattedValue}/>}/>
        </DetailCard>

        <Alert severity={'info'}>
            {t('common.auditoria.create', {createdDate: formatDate(entity.createdDate), createdBy: entity.createdByFullName})}
            {entity.lastModifiedDate != null &&
                t('common.auditoria.update', {lastModifiedDate: formatDate(entity.lastModifiedDate), lastModifiedBy: entity.lastModifiedByFullName})}
        </Alert>
    </MuiDetail>
}

const perspectives:any = []
const useMetaExpTascaDetail = () => {
    const { t } = useTranslation();

    const {
        isReady: apiIsReady,
        getOne: apiGetOne,
        currentFields
    } = useResourceApiService('metaExpedientTascaResource');
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
            title={t('page.metaExpedientTasca.detall.title')}
            componentProps={{ fullWidth: true, maxWidth: 'sm' }}
            buttons={buttons}
            buttonCallback={() :void => {
                handleClose();
            }}
        >
            <Load value={entity}>
                <MetaExpTascaDetail fields={currentFields} entity={entity}/>
            </Load>
        </MuiDialog>

    return {
        apiIsReady,
        handleOpen,
        handleClose,
        dialog
    }
}
export default useMetaExpTascaDetail;