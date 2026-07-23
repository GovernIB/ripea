import { useResourceApiService, GridPage } from 'reactlib';
import { useTranslation } from 'react-i18next';
import { useNavigate, useParams } from 'react-router-dom';
import React, { useEffect, useState } from 'react';
import Load from '../../../components/Load.tsx';
import { Button, Icon, Typography, Box, alpha, useTheme, Divider } from '@mui/material';
import { ExpedientInfo } from '../../expedient/details/Expedient.tsx';
import DocumentsGrid from '../../contingut/DocumentsGrid.tsx';
import { TascaComment } from '../../CommentDialog.tsx';
import { useActions } from './TascaActions.tsx';
import * as builder from '../../../util/springFilterUtils.ts';
import { CardPage } from '../../../components/CardData.tsx';
import { ErrorPage } from '../../../components/ErrorPage.tsx';
import { useUserSession } from '@src/components/Session.tsx';
import { icons as iconsAppMenu } from '@src/util/icons';
import { formatDate } from '@src/util/dateUtils.ts';

const expedientPerspectives = ['COUNT', 'ESTAT', 'RELACIONAT', 'AMB_PINBAL', 'META_EXPEDIENT', 'PERMIS_CONTINGUT'];
const expedientNamedQueries = ['WITHOUT_PERMISION_CHECK'];

// Botones de la cabecera azul: misma "pastilla" de fondo blanco que la cabecera del expediente
const headerButtonSx = {
    borderRadius: '4px',
    px: 1.25,
    py: 0,
    mr: 1,
    border: '1px solid #e3e3e3',
    boxShadow: 'none',
    bgcolor: (theme: any) => alpha(theme.palette.common.white, 0.9),
    color: (theme: any) => theme.palette.getContrastText(theme.palette.common.white),
    '&:hover': { boxShadow: 'none', bgcolor: (theme: any) => theme.palette.common.white },
};

const HeaderMain = (props: any) => {
    const { tasca, expanded, fields, expedient } = props;
    const { t } = useTranslation();
    const navigate = useNavigate();
    const { changeEstat } = useActions();
    const theme = useTheme();
    const headerTextColor = theme.palette.mode === 'dark' ? '#464646' : theme.palette.primary.contrastText;

    const campsExpedientContret = [
        { name: 'numero', value: expedient?.numero },
        { name: 'ntiClasificacionSia', value: expedient?.ntiClasificacionSia },
        { name: 'organGestor', value: expedient?.organGestor?.description },
        { name: 'ntiFechaApertura', value: formatDate(expedient?.ntiFechaApertura) },
        { name: 'grup', value: expedient?.grup?.description },
    ];

    return (
        <Box sx={{ display: 'flex', flexDirection: 'column', width: '100%' }}>
            <Box sx={{ display: 'flex', flexDirection: 'row', justifyContent: 'space-between', width: '100%' }}>
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                    <Icon sx={{ fontSize: '2rem', color: 'text.primary' }}>{iconsAppMenu.tasca}</Icon>
                    <Typography variant="h4" component="h1" sx={{ display: 'flex' }}>
                        {tasca?.metaExpedientTasca?.description}
                    </Typography>
                </Box>
                <Box>
                    <Typography
                        sx={{ paddingTop: '5px', color: (theme) => alpha(theme.palette.primary.contrastText, 0.85) }}
                        variant="subtitle1"
                    >
                        {tasca?.metaExpedientTascaDescription}
                    </Typography>
                </Box>
                <Box sx={{ paddingTop: '5px' }}>
                    <Button variant="contained" sx={headerButtonSx} onClick={() => navigate('/usuariTasca')}>
                        <Icon>arrow_back</Icon>
                        {t('common.back')}
                    </Button>
                    {tasca?.estat == 'PENDENT' && (
                        <Button
                            variant="contained"
                            sx={headerButtonSx}
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
                            variant="contained"
                            sx={headerButtonSx}
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
                        iconStyle={{ fontSize: '1.2em', color: (theme: any) => theme.palette.primary.contrastText }}
                        readOnly={tasca?.usuariActualOnlyObservador}
                    />
                </Box>
            </Box>

            {!expanded && (
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mt: 1 }}>
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
        </Box>
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
        <GridPage autoHeight>
            <Load value={expedient && tasca} noEffect>
                <CardPage
                    header={
                        <HeaderMain
                            tasca={tasca}
                            expanded={expanded}
                            expedient={expedient}
                            fields={fieldsExpedient}
                            handleToggle={handleToggle}
                        />
                    }
                    componentProps={{ justifyContent: 'space-between' }}
                >
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
                </CardPage>
            </Load>
        </GridPage>
    );
};
export default Tasca;
