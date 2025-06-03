import {useState} from "react";
import {Grid, Alert, Icon} from "@mui/material";
import {MuiDialog} from "reactlib";
import {useTranslation} from "react-i18next";
import Load from "../../../components/Load.tsx";

const ErrorValidacio = (props:any) => {
    const {entity} = props;
    const { t } = useTranslation();
    const errors = entity?.errors;

    const hiHaDocumentsSenseMetaNode = errors?.some((error:any)=>error?.documentsWithoutMetaDocument);
    const hiHaNotificacionsNoFinalitzades = errors?.some((error:any)=>error?.withNotificacionsNoFinalitzades);
    const expedientAmbInteressatObligatori = errors?.some((error:any)=>error?.expedientWithoutInteressats);

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        {
            errors.map((error:any)=> <>
                {error?.metaDada && <Grid item xs={12} key={`metaDada-${error?.id}`}><Alert severity="warning" icon={<Icon>create</Icon>}>{t('page.alert.errors.metaDada')} {error?.metaDada?.nom} ({error?.metaDada?.tipus})</Alert></Grid>}
                {error?.metaDocument && <Grid item xs={12} key={`metaDocument-${error?.id}`}><Alert severity="warning" icon={<Icon>insert_drive_file</Icon>}>{t('page.alert.errors.metaDocument')} {error?.metaDocument?.nom}</Alert></Grid>}
            </>)
        }
        <Grid item xs={12} hidden={!hiHaDocumentsSenseMetaNode}><Alert severity="warning">{t('page.alert.errors.metaNode')}</Alert></Grid>
        <Grid item xs={12} hidden={!hiHaNotificacionsNoFinalitzades}><Alert severity="warning">{t('page.alert.errors.noFinalitzades')}</Alert></Grid>
        <Grid item xs={12} hidden={!expedientAmbInteressatObligatori}><Alert severity="warning">{t('page.alert.errors.interessatObligatori')}</Alert></Grid>
    </Grid>
}
const useErrorValidacio = () => {
    const { t } = useTranslation();

    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

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

    const dialog = <MuiDialog
        open={open}
        closeCallback={handleClose}
        title={t('page.alert.title')}
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
        <Load value={entity} noEffect>
            <ErrorValidacio entity={entity}/>
        </Load>
    </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog,
    }
}
export default useErrorValidacio;