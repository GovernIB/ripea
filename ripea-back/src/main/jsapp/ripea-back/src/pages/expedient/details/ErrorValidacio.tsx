import {useState} from "react";
import {Grid, Alert, Icon} from "@mui/material";
import {MuiDialog} from "reactlib";
import {useTranslation} from "react-i18next";
import Load from "../../../components/Load.tsx";
import {useValidacioSession} from "../../../components/SseExpedient.tsx";

const ErrorValidacio = (props:any) => {
    const {errors} = props;
    const { t } = useTranslation();

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        {
            errors?.map((validacio:any, index:number)=> <Grid item xs={12} key={`validacio-${index}`} container direction={"row"} columnSpacing={1} rowSpacing={1}>
                <Grid item xs={12} hidden={!validacio?.metaDada}><Alert severity="warning" icon={<Icon>create</Icon>}>{t('page.alert.errors.metaDada')} {validacio?.metaDada?.nom} ({validacio?.metaDada?.tipus})</Alert></Grid>
                <Grid item xs={12} hidden={!validacio?.metaDocument}><Alert severity="warning" icon={<Icon>insert_drive_file</Icon>}>{t('page.alert.errors.metaDocument')} {validacio?.metaDocument?.nom}</Alert></Grid>

                <Grid item xs={12} hidden={!validacio?.documentsWithoutMetaDocument}><Alert severity="warning">{t('page.alert.errors.metaNode')}</Alert></Grid>
                <Grid item xs={12} hidden={!validacio?.withNotificacionsNoFinalitzades}><Alert severity="warning">{t('page.alert.errors.noFinalitzades')}</Alert></Grid>
                <Grid item xs={12} hidden={!validacio?.expedientWithoutInteressats}><Alert severity="warning">{t('page.alert.errors.interessatObligatori')}</Alert></Grid>
            </Grid>)
        }
    </Grid>
}
const useErrorValidacio = () => {
    const { t } = useTranslation();
    const {value: validacions} = useValidacioSession()

    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const handleOpen = (_id:any, row:any) => {
        // console.log(id, row)
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
                componentProps: { variant: 'outlined' }
            },
        ]}
        buttonCallback={(value :any) :void=>{
            if (value=='close') {
                handleClose();
            }
        }}
    >
        <Load value={entity} noEffect>
            <ErrorValidacio errors={validacions?.expedientId == entity?.id
                ?validacions?.errorsValidacio
                :entity?.errors}/>
        </Load>
    </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog,
    }
}
export default useErrorValidacio;