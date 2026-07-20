import {useResourceApiService, GridPage} from "reactlib";
import {useTranslation} from "react-i18next";
import {useNavigate, useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import Load from "../../../components/Load.tsx";
import {Button, Grid, Icon, Typography, Box, alpha} from "@mui/material";
import {icons} from "@src/util/icons.ts";
import {ExpedientInfo} from "../../expedient/details/Expedient.tsx";
import DocumentsGrid from "../../contingut/DocumentsGrid.tsx";
import {TascaComment} from "../../CommentDialog.tsx";
import {useActions} from "./TascaActions.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import {CardPage} from "../../../components/CardData.tsx";
import {ErrorPage} from "../../../components/ErrorPage.tsx";

const expedientPerspectives = ['COUNT', 'ESTAT', 'RELACIONAT', 'AMB_PINBAL', "META_EXPEDIENT", "PERMIS_CONTINGUT"]
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
const Tasca = () => {
    const { t } = useTranslation();
    const { id, tascaId } = useParams();
    const navigate = useNavigate();
    const [error, setError] = useState<any>();

    const { changeEstat } = useActions();

    const {
        isReady: apiIsReady,
        find: appFind,
        currentFields: fields,
    } = useResourceApiService('expedientResource');
    const [expedient, setExpedient] = useState<any>();

    useEffect(()=>{
        if (apiIsReady) {
            appFind( {unpaged: true, filter: builder.eq('id', id), perspectives: expedientPerspectives, namedQueries: expedientNamedQueries} )
                .then((params) => setExpedient(params?.rows?.[0] || undefined) )
                .catch((error) => setError(error))
        }
    },[apiIsReady])

    const {
        isReady: apiTascaIsReady,
        getOne: appTascaGetOne,
    } = useResourceApiService('expedientTascaResource');
    const [tasca, setTasca] = useState<any>();

    useEffect(()=>{
        if (apiTascaIsReady) {
            appTascaGetOne(tascaId, {perspectives: ['CONTEXT_USUARI']})
                .then((app) => setTasca(app))
                .catch((error) => setError(error))
        }
    },[apiTascaIsReady])

    if (error)
        return <ErrorPage error={error}/>

    const headerMain = <>
        <Box sx={{ display: 'flex', alignItems: 'center'}}>
            <Icon sx={{ fontSize: '2rem', color: 'primary.light' }}>{icons.tasca}</Icon>
            <Typography variant="h4" component="h1" sx={{ display: 'flex' }}>{tasca?.metaExpedientTasca?.description}</Typography>
        </Box>
        <Box>
            <Typography sx={{ paddingTop: '5px', color: (theme) => alpha(theme.palette.primary.contrastText, 0.85) }} variant="subtitle1">{tasca?.metaExpedientTascaDescription}</Typography>
        </Box>
        <Box sx={{ paddingTop: '5px'}}>
            <Button
                variant="contained"
                sx={headerButtonSx}
                onClick={()=>navigate('/usuariTasca')}
            >
                <Icon>arrow_back</Icon>
                {t('common.back')}
            </Button>
            {tasca?.estat == 'PENDENT' &&
                <Button
                    variant="contained"
                    sx={headerButtonSx}
                    onClick={()=> {
                        changeEstat(tasca?.id, 'INICIADA', t('page.tasca.action.iniciar.ok'), () => window.location.reload())
                    }}
                >
                    <Icon>play_arrow</Icon>
                    {t('page.tasca.action.iniciar.label')}
                </Button>
            }
            {tasca?.estat == 'INICIADA' &&
                <Button
                    variant="contained"
                    sx={headerButtonSx}
                    onClick={()=> {
                        changeEstat(tasca?.id, 'FINALITZADA', t('page.tasca.action.finalitzar.ok'), () => navigate('/usuariTasca'))
                    }}
                >
                    <Icon>check</Icon>
                    {t('page.tasca.action.finalitzar.label')}
                </Button>
            }
            <TascaComment
                entity={tasca}
                iconStyle={{ fontSize: '1.2em', color: (theme: any) => theme.palette.primary.contrastText }}
                readOnly={tasca?.usuariActualOnlyObservador}
            />
        </Box>
    </>;

    return <GridPage autoHeight>
        <Load value={expedient && tasca} noEffect>
            <CardPage header={headerMain} componentProps={{ justifyContent: 'space-between' }}>
                <Grid container spacing={2}>
                    <Grid size={3}>
                        <ExpedientInfo title={tasca?.expedient?.description} entity={expedient} fields={fields} readOnly/>
                    </Grid>
                    <Grid size={9}>
                        <DocumentsGrid entity={{...expedient, potModificar: (expedient?.potModificar || tasca?.usuariActualResponsable || tasca?.usuariActualDelegat)}}/>
                    </Grid>
                </Grid>
            </CardPage>
        </Load>
    </GridPage>
}
export default Tasca;