import {useState} from "react";
import {Grid} from "@mui/material";
import {MuiDialog} from "reactlib";
import {useTranslation} from "react-i18next";
import {ContenidoData} from "../../../components/CardData.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import Load from "../../../components/Load.tsx";

const PublicacioDetail = (props:any) => {
    const {entity} = props
    const { t } = useTranslation();

    return <Grid container direction={"row"} columnSpacing={1}>
        <ContenidoData title={t('page.publicacio.detall.document')}>{entity?.document?.description}</ContenidoData>
        <ContenidoData title={t('page.publicacio.detall.enviatData')}>{formatDate(entity?.enviatData)}</ContenidoData>
        <ContenidoData title={t('page.publicacio.detall.estat')}>{entity?.estat}</ContenidoData>
        <ContenidoData title={t('page.publicacio.detall.tipus')}>{entity?.tipus}</ContenidoData>
        <ContenidoData title={t('page.publicacio.detall.assumpte')}>{entity?.assumpte}</ContenidoData>
        <ContenidoData title={t('page.publicacio.detall.observacions')} hiddenIfEmpty>{entity?.observacions}</ContenidoData>
    </Grid>
}

const usePublicacioDetail = () => {
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
            title={t('page.publicacio.detall.title')}
            componentProps={{ fullWidth: true, maxWidth: 'sm'}}
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
            <Load value={entity}>
                <PublicacioDetail entity={entity}/>
            </Load>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}
export default usePublicacioDetail;