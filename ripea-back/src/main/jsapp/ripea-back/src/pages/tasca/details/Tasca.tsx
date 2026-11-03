import { useResourceApiService, GridPage } from 'reactlib';
import { useTranslation } from 'react-i18next';
import { useNavigate, useParams } from 'react-router-dom';
import React, { useEffect, useState } from 'react';
import Load from '../../../components/Load.tsx';
import { Button, Icon, Typography, Box, useTheme, Divider } from '@mui/material';
import { ExpedientInfo } from '../../expedient/details/Expedient.tsx';
import DocumentsGrid from '../../contingut/DocumentsGrid.tsx';
import { TascaComment } from '../../CommentDialog.tsx';
import { useActions } from './TascaActions.tsx';
import * as builder from '../../../util/springFilterUtils.ts';
import { ScreenFrame } from '../../../components/ScreenFrame.tsx';
import { ErrorPage } from '../../../components/ErrorPage.tsx';
import { useUserSession } from '@src/components/Session.tsx';
import { icons } from '@src/util/icons';
import { formatDate } from '@src/util/dateUtils.ts';

const expedientPerspectives = ['COUNT', 'ESTAT', 'RELACIONAT', 'AMB_PINBAL', 'META_EXPEDIENT', 'PERMIS_CONTINGUT'];
const expedientNamedQueries = ['WITHOUT_PERMISION_CHECK'];

const HeaderMain = (props: any) => {
    const { tasca, expanded, fields, expedient } = props;
    const theme = useTheme();
    const headerTextColor = theme.palette.text.primary;

    const campsExpedientContret = [
        { name: 'numero', value: expedient?.numero },
        { name: 'ntiClasificacionSia', value: expedient?.ntiClasificacionSia },
        { name: 'organGestor', value: expedient?.organGestor?.description },
        { name: 'ntiFechaApertura', value: formatDate(expedient?.ntiFechaApertura) },
        { name: 'grup', value: expedient?.grup?.description },
    ];

    return (
        <>
            {tasca?.metaExpedientTascaDescription && tasca.metaExpedientTascaDescription.trim().toLowerCase() !== tasca?.metaExpedientTasca?.description?.trim().toLowerCase() && (
                <Typography variant="subtitle1" sx={{ color: 'text.secondary' }}>
                    {tasca?.metaExpedientTascaDescription}
                </Typography>
            )}
            {!expanded && (
                <Box sx={{ display: 'flex', flexWrap: 'wrap', alignItems: 'center', gap: 2 }}>
                    {campsExpedientContret.map((camp, index) => (
                        <React.Fragment key={camp.name}>
                            {index > 0 && <Divider orientation="vertical" flexItem sx={{ borderColor: headerTextColor, opacity: 0.5 }} />}
                            <Typography variant="body2" sx={{ fontWeight: 'bold', color: headerTextColor }}>
                                {fields.find((field: any) => field?.name === camp.name)?.label}:&nbsp;
                                <Typography component="span" variant="body2">
                                    {camp.value ?? '-'}
                                </Typography>
                            </Typography>
                        </React.Fragment>
                    ))}
                </Box>
            )}
        </>
    );
};

// Accions de la tasca: van a la dreta de la barra de títol del ScreenFrame
const TascaActions = (props: any) => {
    const { tasca } = props;
    const { t } = useTranslation();
    const navigate = useNavigate();
    const { changeEstat } = useActions();
    return (
        <>
            <Button variant="outlined" onClick={() => navigate('/usuariTasca')}>
                <Icon>arrow_back</Icon>
                {t('common.back')}
            </Button>
            {tasca?.estat == 'PENDENT' && (
                <Button
                    variant="outlined"
                    onClick={() => {
                        changeEstat(tasca?.id, 'INICIADA', t('page.tasca.action.iniciar.ok'), () => window.location.reload());
                    }}
                >
                    <Icon>play_arrow</Icon>
                    {t('page.tasca.action.iniciar.label')}
                </Button>
            )}
            {tasca?.estat == 'INICIADA' && (
                <Button
                    variant="outlined"
                    onClick={() => {
                        changeEstat(tasca?.id, 'FINALITZADA', t('page.tasca.action.finalitzar.ok'), () => navigate('/usuariTasca'));
                    }}
                >
                    <Icon>check</Icon>
                    {t('page.tasca.action.finalitzar.label')}
                </Button>
            )}
            <TascaComment
                entity={tasca}
                iconStyle={{ fontSize: '1.2em', color: (theme: any) => theme.palette.text.primary }}
                readOnly={tasca?.usuariActualOnlyObservador}
            />
        </>
    );
};

const Tasca = () => {
    const { id, tascaId } = useParams();
    const [error, setError] = useState<any>();
    const { value: user, save: apiSave } = useUserSession();
    const [expanded, setExpanded] = useState<boolean>(!!user?.conf?.informacioExpedientExpandit);

    const { isReady: apiIsReady, find: appFind, currentFields: fieldsExpedient } = useResourceApiService('expedientResource');
    const [expedient, setExpedient] = useState<any>();

    useEffect(() => {
        if (apiIsReady) {
            appFind({
                unpaged: true,
                filter: builder.eq('id', id),
                perspectives: expedientPerspectives,
                namedQueries: expedientNamedQueries,
            })
                .then((params) => setExpedient(params?.rows?.[0] || undefined))
                .catch((error) => setError(error));
        }
    }, [apiIsReady]);

    useEffect(() => {
        if (user?.conf?.informacioExpedientExpandit !== undefined) {
            setExpanded(user.conf.informacioExpedientExpandit);
        }
    }, [user?.conf?.informacioExpedientExpandit]);

    const { isReady: apiTascaIsReady, getOne: appTascaGetOne } = useResourceApiService('expedientTascaResource');
    const [tasca, setTasca] = useState<any>();

    useEffect(() => {
        if (apiTascaIsReady) {
            appTascaGetOne(tascaId, { perspectives: ['CONTEXT_USUARI'] })
                .then((app) => setTasca(app))
                .catch((error) => setError(error));
        }
    }, [apiTascaIsReady]);

    const handleToggle = () => {
        const nouValor = !expanded;
        setExpanded(nouValor);

        apiSave({ canviInformacioExpandit: nouValor });
    };

    if (error) return <ErrorPage error={error} />;

    return (
        <GridPage>
            <ScreenFrame
                icon={icons.tasca}
                title={tasca?.metaExpedientTasca?.description ?? ''}
                actions={<TascaActions tasca={tasca} />}
            >
            <Load value={expedient && tasca} noEffect>
                <>
                    <HeaderMain
                        tasca={tasca}
                        expanded={expanded}
                        expedient={expedient}
                        fields={fieldsExpedient}
                        handleToggle={handleToggle}
                    />
                    <Box sx={{ display: 'flex', gap: 2, alignItems: 'flex-start' }}>
                        <Box sx={{ width: expanded ? 444 : 72, flexShrink: 0 }}>
                            <ExpedientInfo entity={expedient} fields={fieldsExpedient} expanded={expanded} setExpanded={handleToggle} />
                        </Box>
                        <Box sx={{ flex: 1, minWidth: 0 }}>
                            <DocumentsGrid
                                entity={{
                                    ...expedient,
                                    potModificar: expedient?.potModificar || tasca?.usuariActualResponsable || tasca?.usuariActualDelegat,
                                }}
                            />
                        </Box>
                    </Box>
                </>
            </Load>
            </ScreenFrame>
        </GridPage>
    );
};
export default Tasca;
