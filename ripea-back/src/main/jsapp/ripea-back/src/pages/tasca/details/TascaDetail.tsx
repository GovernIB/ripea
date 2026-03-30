import {useState} from "react";
import {MuiDialog, useBaseAppContext, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import {formatDate} from "../../../util/dateUtils.ts";
import {StyledPrioritat} from "../../expedient/ExpedientGrid.tsx";
import {DetailCard} from "../../../components/CardData.tsx";
import {StyledDate} from "../TasquesGrid.tsx";
import {FieldData, MuiDetail} from "../../../components/MuiDetail.tsx";

const useTascaDetail = () => {
    const { t } = useTranslation();

    const {
        isReady: apiIsReady,
        getOne: apiGetOne,
        currentFields: fields,
    } = useResourceApiService('expedientTascaResource')
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
            title={t('page.tasca.detall.title')}
            // componentProps={{ fullWidth: true, maxWidth: 'xl'}}
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
            <MuiDetail entity={entity} fields={fields}>
            <DetailCard>
                <FieldData field={"metaExpedientTasca"}/>
                <FieldData field={"metaExpedientTascaDescription"}/>
                <FieldData size={6} field={"createdByFullName"}/>
                <FieldData size={6} field={"dataInici"}>{formatDate(entity?.dataInici)}</FieldData>
                <FieldData field={"responsablesStr"}/>
                <FieldData field={"responsableActual"}/>
                <FieldData field={"delegat"}/>
                <FieldData field={"observadorsStr"}/>
                <FieldData size={6} field={"duracio"}/>
                <FieldData size={6} field={"dataLimit"} renderCell={(formattedValue:string) => <StyledDate entity={entity}>{formatDate(formattedValue, "DD/MM/Y")}</StyledDate>}/>
                <FieldData size={6} field={"estat"}/>
                <FieldData size={6} field={"prioritat"} renderCell={(formattedValue:string) => <StyledPrioritat entity={entity}>{formattedValue}</StyledPrioritat>}/>
            </DetailCard>
            </MuiDetail>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}
export default useTascaDetail;