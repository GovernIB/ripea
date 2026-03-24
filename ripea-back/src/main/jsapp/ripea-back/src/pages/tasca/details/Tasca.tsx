import {useResourceApiService, GridPage} from "reactlib";
import {useTranslation} from "react-i18next";
import {useNavigate, useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import Load from "../../../components/Load.tsx";
import {Button, Grid2 as Grid, Icon, Typography, Box} from "@mui/material";
import {icons} from "../../user/UserHeadToolbar.tsx";
import {ExpedientInfo} from "../../expedient/details/Expedient.tsx";
import DocumentsGrid from "../../contingut/DocumentsGrid.tsx";
import {TascaComment} from "../../CommentDialog.tsx";
import {useActions} from "./TascaActions.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import {CardPage} from "../../../components/CardData.tsx";

const expedientPerspectives = ['COUNT', 'ESTAT', 'RELACIONAT', 'AMB_PINBAL', "META_EXPEDIENT", "PERMIS_CONTINGUT"]
const expedientNamedQueries = ['WITHOUT_PERMISION_CHECK'];
const Tasca = () => {
    const { t } = useTranslation();
    const { id, tascaId } = useParams();
    const navigate = useNavigate();

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
        }
    },[apiIsReady])

    const {
        isReady: apiTascaIsReady,
        getOne: appTascaGetOne,
    } = useResourceApiService('expedientTascaResource');
    const [tasca, setTasca] = useState<any>();

    useEffect(()=>{
        if (apiTascaIsReady) {
            appTascaGetOne(tascaId).then((app) => setTasca(app))
        }
    },[apiTascaIsReady])

    const headerMain = <>
        <Box sx={{ display: 'flex', alignItems: 'center'}}>
            <Icon sx={{ fontSize: '2rem' }}>{icons.tasca}</Icon>
            <Typography variant="h4" sx={{ display: 'flex' }}>{tasca?.metaExpedientTasca?.description}</Typography>
        </Box>
        <Box>
            <Typography sx={{ paddingTop: '5px'}} variant="subtitle1" color={'grey'}>{tasca?.metaExpedientTascaDescription}</Typography>
        </Box>
        <Box sx={{ paddingTop: '5px'}}>
            <Button
                variant="outlined"
                color={"inherit"}
                sx={{ borderRadius: '4px', padding: '0px 10px'}}
                onClick={()=>navigate('/usuariTasca')}
            >
                <Icon>arrow_back</Icon>
                {t('common.back')}
            </Button>
            {tasca?.estat == 'PENDENT' &&
                <Button
                    variant="outlined"
                    color={"inherit"}
                    sx={{ borderRadius: '4px', padding: '0px 10px'}}
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
                    variant="outlined"
                    color={"inherit"}
                    sx={{ borderRadius: '4px', padding: '0px 10px'}}
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
                iconStyle={{ fontSize: '1.2em'}}
                sx={{ padding: '0px 10px' }}
                componentProps={{variant: "outlined", sx: { borderRadius: '4px' }}}
                readOnly={tasca?.usuariActualOnlyObservador}
            />
        </Box>
    </>;

    return <GridPage disableMargins>
        <Load value={expedient && tasca} noEffect>
            <CardPage header={headerMain} componentProps={{ justifyContent: 'space-between' }}>
                <Grid container spacing={2}>
                    <Grid size={3}>
                        <ExpedientInfo title={tasca?.expedient?.description} entity={expedient} fields={fields} readOnly/>
                    </Grid>
                    <Grid size={9}>
                        <DocumentsGrid entity={expedient}/>
                    </Grid>
                </Grid>
            </CardPage>
        </Load>
    </GridPage>
}
export default Tasca;