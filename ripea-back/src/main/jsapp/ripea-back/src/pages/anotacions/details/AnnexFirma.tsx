import {useTranslation} from "react-i18next";
import {useState} from "react";
import {MuiDialog} from "reactlib";
import {Grid} from "@mui/material";
import {CardData, ContenidoData} from "../../../components/CardData.tsx";

const AnnexFirma = (props:any) => {
    const {entity} = props;
    const { t } = useTranslation();

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={2}>
        {
            entity?.firmes.map((firma:any)=>{
                return <CardData title={firma?.fitxerNom}>
                    <ContenidoData xs={6} title={t('page.registre.justificant.firmaTipus')}>{firma?.tipus}</ContenidoData>
                    <ContenidoData xs={6} title={t('page.registre.justificant.firmaPerfil')}>{firma?.perfil}</ContenidoData>
                </CardData>
            })
        }
    </Grid>
}

const useAnnexFirma = () => {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const handleOpen = (id: any, row:any) => {
        console.log(id, row)
        setEntity(row)
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
            title={t('page.anotacio.action.firma.title')}
            componentProps={{ fullWidth: true, maxWidth: 'md'}}
            buttons={[
                {
                    value: 'close',
                    text: t('common.close')
                },
            ]}
            buttonCallback={(value: any): void => {
                if (value == 'close') {
                    handleClose();
                }
            }}
        >
            <AnnexFirma entity={entity}/>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog,
    }
}
export default useAnnexFirma;