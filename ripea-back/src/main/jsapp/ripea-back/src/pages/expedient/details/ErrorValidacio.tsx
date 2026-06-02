import { useState } from 'react';
import { Grid, Alert, Icon } from '@mui/material';
import { MuiDialog } from 'reactlib';
import { useTranslation } from 'react-i18next';
import Load from '../../../components/Load.tsx';
import { useValidacioSession } from '../../../components/SseExpedient.tsx';

export const getResumErrorsText = (errors: any[], t: any): string => {
    if (!errors || errors.length === 0) return '';

    const primerError = errors[0];
    let primerMissatge = '';

    // Detectem quin tipus d'error és replicant la lògica de la modal
    if (primerError?.metaDada) {
        primerMissatge = `${t('page.alert.errors.metaDada')} ${primerError.metaDada.nom} (${primerError.metaDada.tipus})`;
    } else if (primerError?.metaDocument) {
        primerMissatge = `${t('page.alert.errors.metaDocument')} ${primerError.metaDocument.nom}`;
    } else if (primerError?.documentsWithoutMetaDocument) {
        primerMissatge = t('page.alert.errors.metaNode');
    } else if (primerError?.withNotificacionsNoFinalitzades) {
        primerMissatge = t('page.alert.errors.noFinalitzades');
    } else if (primerError?.expedientWithoutInteressats) {
        primerMissatge = t('page.alert.errors.interessatObligatori');
    } else {
        // Per si de cas arriba un error desconegut o buit
        primerMissatge = t('page.expedient.alert.validation');
    }

    // Si només hi ha 1 error, retornem directament el seu text
    if (errors.length === 1) {
        return primerMissatge;
    }

    const restants = errors.length - 1;

    if (restants === 1) {
        return `${primerMissatge}${t('page.alert.errors.unAltreError')}`;
    }

    return `${primerMissatge}${t('page.alert.errors.altresErrors', { restants: restants })}`;
};

const ErrorValidacio = (props: any) => {
    const { errors } = props;
    const { t } = useTranslation();

    return (
        <Grid container direction={'row'} columnSpacing={1} rowSpacing={1}>
            {errors?.map((validacio: any, index: number) => (
                <Grid size={12} key={`validacio-${index}`} container direction={'row'} columnSpacing={1} rowSpacing={1}>
                    <Grid size={12} hidden={!validacio?.metaDada}>
                        <Alert severity="warning" icon={<Icon>create</Icon>}>
                            {t('page.alert.errors.metaDada')} {validacio?.metaDada?.nom} ({validacio?.metaDada?.tipus})
                        </Alert>
                    </Grid>
                    <Grid size={12} hidden={!validacio?.metaDocument}>
                        <Alert severity="warning" icon={<Icon>insert_drive_file</Icon>}>
                            {t('page.alert.errors.metaDocument')} {validacio?.metaDocument?.nom}
                        </Alert>
                    </Grid>

                    <Grid size={12} hidden={!validacio?.documentsWithoutMetaDocument}>
                        <Alert severity="warning">{t('page.alert.errors.metaNode')}</Alert>
                    </Grid>
                    <Grid size={12} hidden={!validacio?.withNotificacionsNoFinalitzades}>
                        <Alert severity="warning">{t('page.alert.errors.noFinalitzades')}</Alert>
                    </Grid>
                    <Grid size={12} hidden={!validacio?.expedientWithoutInteressats}>
                        <Alert severity="warning">{t('page.alert.errors.interessatObligatori')}</Alert>
                    </Grid>
                </Grid>
            ))}
        </Grid>
    );
};

const useErrorValidacio = (titleDialog?: string) => {
    const { t } = useTranslation();
    const { value: validacions } = useValidacioSession();

    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const handleOpen = (_id: any, row: any) => {
        // console.log(id, row)
        setEntity(row);
        setOpen(true);
    };

    const handleClose = (reason?: string) => {
        if (reason !== 'backdropClick') {
            setEntity(undefined);
            setOpen(false);
        }
    };

    const dialog = (
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={titleDialog ? titleDialog : t('page.alert.titleExpedient')}
            componentProps={{ fullWidth: true, maxWidth: 'sm' }}
            dialogContentProps={{ sx: { px: 2 } }}
            buttons={[
                {
                    value: 'close',
                    text: t('common.close'),
                    componentProps: { variant: 'outlined' },
                },
            ]}
            buttonCallback={(value: any): void => {
                if (value == 'close') {
                    handleClose();
                }
            }}
        >
            <Load value={entity} noEffect>
                <ErrorValidacio
                    errors={validacions?.expedientId == entity?.id ? validacions?.errorsValidacio : entity?.errors}
                />
            </Load>
        </MuiDialog>
    );

    return {
        handleOpen,
        handleClose,
        dialog,
    };
};
export default useErrorValidacio;
