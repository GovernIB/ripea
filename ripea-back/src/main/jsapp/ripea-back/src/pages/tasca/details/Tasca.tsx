import {GridPage, useResourceApiService} from "reactlib";
import SseExpedient from "../../../components/SseExpedient.tsx";
import {useTranslation} from "react-i18next";
import {useNavigate, useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import Load from "../../../components/Load.tsx";
import { CardData } from "../../../components/CardData.tsx";
import {Button, Grid, Icon, Typography} from "@mui/material";
import {icons} from "../../user/UserHeadToolbar.tsx";
import {ExpedientInfo} from "../../expedient/details/Expedient.tsx";
import DocumentsGrid from "../../contingut/DocumentsGrid.tsx";
import {CommentDialog} from "../../CommentDialog.tsx";
import {useActions} from "./TascaActions.tsx";

const expedientPerspectives = ['COUNT', 'ESTAT', 'RELACIONAT', 'AMB_PINBAL', "META_EXPEDIENT"]
const Tasca = () => {
    const { t } = useTranslation();
    const { id, tascaId } = useParams();
    const navigate = useNavigate();

    const { changeEstat } = useActions();

    const {
        isReady: apiIsReady,
        getOne: appGetOne,
    } = useResourceApiService('expedientResource');
    const [expedient, setExpedient] = useState<any>();

    useEffect(()=>{
        if (apiIsReady) {
            appGetOne(id, {perspectives: expedientPerspectives}).then((app) => setExpedient(app))
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

    return <GridPage>
        <SseExpedient id={id}/>

        <Load value={expedient && tasca} noEffect>
            <CardData header={
                <Grid container direction={'row'} columnSpacing={1} sx={{justifyContent: "space-between", alignItems: "center"}}>
                    <Grid item xs={10}>
                        <Typography variant="h5"><Icon sx={{ fontSize: "2rem" }}>{icons.tasca}</Icon>{tasca?.metaExpedientTasca?.description}</Typography>
                        <Typography variant="subtitle1" color={'grey'}>{tasca?.metaExpedientTascaDescription}</Typography>
                    </Grid>
                    <Grid item xs={2} display={'flex'} justifyContent={'end'}>
                        <Button
                            title={t('page.expedient.action.retornar.label')}
                            variant="outlined"
                            color={"inherit"}
                            sx={{ borderRadius: '4px' }}
                            onClick={()=>navigate(-1)}
                        >
                            <Icon>arrow_left_alt</Icon>
                        </Button>
                        {tasca?.estat == 'PENDENT' &&
                            <Button
                                title={t('page.tasca.action.iniciar.label')}
                                variant="outlined"
                                color={"inherit"}
                                sx={{ borderRadius: '4px' }}
                                onClick={()=> {
                                    changeEstat(tasca?.id, 'INICIADA', t('page.tasca.action.iniciar.ok'))
                                    navigate(-1)
                                }}
                            >
                                <Icon>check</Icon>
                            </Button>
                        }
                        {tasca?.estat == 'INICIADA' &&
                            <Button
                                title={t('page.tasca.action.finalitzar.label')}
                                variant="outlined"
                                color={"inherit"}
                                sx={{ borderRadius: '4px' }}
                                onClick={()=> {
                                    changeEstat(tasca?.id, 'FINALITZADA', t('page.tasca.action.finalitzar.ok'))
                                    navigate(-1)
                                }}
                            >
                                <Icon>arrow_right</Icon>
                            </Button>
                        }
                        <CommentDialog
                            entity={tasca}
                            title={`${t('page.comment.tasca')}: ${tasca?.metaExpedientTascaDescription}`}
                            resourceName={'expedientTascaComentariResource'}
                            resourceReference={'expedientTasca'}
                            componentProps={{variant: "outlined", sx: { borderRadius: '4px' }}}
                            readOnly={tasca?.usuariActualOnlyObservador}
                        />
                    </Grid>
                </Grid>
            }>
                <ExpedientInfo title={tasca?.expedient?.description} entity={expedient} xs={3} readOnly/>
                <Grid item xs={9}>
                    <DocumentsGrid entity={expedient}/>
                </Grid>
            </CardData>
        </Load>
    </GridPage>
}
export default Tasca;