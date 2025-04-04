import {Grid} from "@mui/material";
import {useState} from "react";
import {formatDate} from "../../../util/dateUtils.ts";
import {useTranslation} from "react-i18next";
import Dialog from "../../../../lib/components/mui/Dialog.tsx";
import {StyledPrioritat} from "../../expedient/ExpedientGrid.tsx";
import {ContenidoData} from "../../../components/DetailComponents.tsx";

const useTascaDetail = () => {
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();
    const { t } = useTranslation();

    const handleOpen = (id:any, row:any) => {
        console.log(id, row)
        setEntity(row);
        setOpen(true);
    }

    const handleClose = () => {
        setOpen(false);
    };

    const dialog =
        <Dialog
            open={open}
            closeCallback={handleClose}
            title={t('page.tasca.detall.title')}
            // componentProps={{ fullWidth: true, maxWidth: 'xl'}}
            buttons={[
                {
                    value: 'close',
                    text: t('common.close'),
                    icon: 'close'
                },
            ]}
            buttonCallback={(value :any) :void=>{
                if (value=='close') {
                    handleClose();
                }
            }}
        >
            <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
                <ContenidoData title={t('page.tasca.detall.metaExpedientTasca')}>{entity?.metaExpedientTasca?.description}</ContenidoData>
                <ContenidoData title={t('page.tasca.detall.metaExpedientTascaDescription')}>{entity?.metaExpedientTascaDescription}</ContenidoData>
                <ContenidoData title={t('page.tasca.detall.createdBy')}>{entity?.createdBy}</ContenidoData>
                <ContenidoData title={t('page.tasca.detall.responsablesStr')}>{entity?.responsablesStr}</ContenidoData>
                <ContenidoData title={t('page.tasca.detall.responsableActual')}>{entity?.responsableActual?.description}</ContenidoData>
                <ContenidoData title={t('page.tasca.detall.delegat')}>{entity?.delegat?.description}</ContenidoData>
                <ContenidoData title={t('page.tasca.detall.observadors')}>
                    {entity?.observadors?.map?.((obs:any)=>`${obs.description}\n`)}
                </ContenidoData>
                <ContenidoData title={t('page.tasca.detall.dataInici')}>{formatDate(entity?.dataInici)}</ContenidoData>
                <ContenidoData title={t('page.tasca.detall.duracio')}>{entity?.duracio}</ContenidoData>
                <ContenidoData title={t('page.tasca.detall.dataLimit')}>{formatDate(entity?.dataLimit, "DD/MM/Y")}</ContenidoData>
                <ContenidoData title={t('page.tasca.detall.estat')}>{entity?.estat}</ContenidoData>
                <ContenidoData title={t('page.tasca.detall.prioritat')}><StyledPrioritat entity={entity}/></ContenidoData>
            </Grid>
        </Dialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}
export default useTascaDetail;