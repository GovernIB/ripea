import {useState} from "react";
import {MuiDialog} from "reactlib";
import {useTranslation} from "react-i18next";
import {formatDate} from "../../../util/dateUtils.ts";
import {StyledPrioritat} from "../../expedient/ExpedientGrid.tsx";
import {DetailCardContent, DetailCard} from "../../../components/CardData.tsx";
import {StyledDate} from "../TasquesGrid.tsx";

const useTascaDetail = () => {
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();
    const { t } = useTranslation();

    const handleOpen = (id:any, row:any) => {
        console.log(id, row)
        setEntity(row);
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
            <DetailCard>
                <DetailCardContent title={t('page.tasca.detall.metaExpedientTasca')}               >{entity?.metaExpedientTasca?.description}</DetailCardContent>
                <DetailCardContent title={t('page.tasca.detall.metaExpedientTascaDescription')}    >{entity?.metaExpedientTascaDescription}</DetailCardContent>
                <DetailCardContent title={t('page.tasca.detall.createdBy')}                        >{entity?.createdBy}</DetailCardContent>
                <DetailCardContent title={t('page.tasca.detall.responsablesStr')}                  >{entity?.responsablesStr}</DetailCardContent>
                <DetailCardContent title={t('page.tasca.detall.responsableActual')}                >{entity?.responsableActual?.description}</DetailCardContent>
                <DetailCardContent title={t('page.tasca.detall.delegat')}                          >{entity?.delegat?.description}</DetailCardContent>
                <DetailCardContent title={t('page.tasca.detall.observadors')}                      >{entity?.observadorsStr}</DetailCardContent>
                <DetailCardContent title={t('page.tasca.detall.dataInici')}                        >{formatDate(entity?.dataInici)}</DetailCardContent>
                <DetailCardContent title={t('page.tasca.detall.duracio')}                          size={6}>{entity?.duracio}</DetailCardContent>
                <DetailCardContent title={t('page.tasca.detall.dataLimit')}                        size={6}><StyledDate entity={entity}>{formatDate(entity?.dataLimit, "DD/MM/Y")}</StyledDate></DetailCardContent>
                <DetailCardContent title={t('page.tasca.detall.estat')}                            size={6}>{entity?.estat}</DetailCardContent>
                <DetailCardContent title={t('page.tasca.detall.prioritat')}                        size={6}><StyledPrioritat entity={entity}>{t(`enum.prioritat.${entity?.prioritat}`)}</StyledPrioritat></DetailCardContent>
            </DetailCard>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}
export default useTascaDetail;