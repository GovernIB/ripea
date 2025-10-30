import {useResourceApiService, GridPage} from "reactlib";
import {useTranslation} from "react-i18next";
import {useNavigate, useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import Load from "../../../components/Load.tsx";
import {Button, Grid2 as Grid, Icon, Typography, Box} from "@mui/material";
import {icons} from "../../user/UserHeadToolbar.tsx";
import {ExpedientInfo} from "../../expedient/details/Expedient.tsx";
import DocumentsGrid from "../../contingut/DocumentsGrid.tsx";
import {CommentDialog} from "../../CommentDialog.tsx";
import {useActions} from "./TascaActions.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import {CardPage} from "../../../components/CardData.tsx";

const expedientPerspectives = ['COUNT', 'ESTAT', 'RELACIONAT', 'AMB_PINBAL', "META_EXPEDIENT"]
const expedientNamedQueries = ['WITHOUT_PERMISION_CHECK'];
const Tasca = () => {
    const { t } = useTranslation();
    const { id, tascaId } = useParams();
    const navigate = useNavigate();

    const { changeEstat } = useActions();

    const {
        isReady: apiIsReady,
        find: appFind,
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

    const headerMain = <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
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
                sx={{ borderRadius: '4px', backgroundColor: 'white', padding: '0px 10px'}}
                onClick={()=>navigate(-1)}
            >
                <Icon>arrow_back</Icon>
                {t('page.expedient.action.retornar.label')}
            </Button>
            {tasca?.estat == 'PENDENT' &&
                <Button
                    variant="outlined"
                    color={"inherit"}
                    sx={{ borderRadius: '4px', backgroundColor: 'white', padding: '0px 10px'}}
                    onClick={()=> {
                        changeEstat(tasca?.id, 'INICIADA', t('page.tasca.action.iniciar.ok'))
                        navigate(-1)
                    }}
                >
                    <Icon>check</Icon>
                    {t('page.tasca.action.iniciar.label')}
                </Button>
            }
            {tasca?.estat == 'INICIADA' &&
                <Button
                    variant="outlined"
                    color={"inherit"}
                    sx={{ borderRadius: '4px', backgroundColor: 'white', padding: '0px 10px'}}
                    onClick={()=> {
                        changeEstat(tasca?.id, 'FINALITZADA', t('page.tasca.action.finalitzar.ok'))
                        navigate(-1)
                    }}
                >
                    <Icon>arrow_right</Icon>
                    {t('page.tasca.action.finalitzar.label')}
                </Button>
            }
            <CommentDialog
                entity={tasca}
                iconStyle={{ fontSize: '1.2em'}}
                sx={{ padding: '0px 10px' }}
                title={`${t('page.comment.tasca')}: ${tasca?.metaExpedientTascaDescription}`}
                resourceName={'expedientTascaComentariResource'}
                resourceReference={'expedientTasca'}
                componentProps={{variant: "outlined", sx: { borderRadius: '4px' }}}
                readOnly={tasca?.usuariActualOnlyObservador}
            />
        </Box>
    </Box>;

    return <GridPage disableMargins style={{ backgroundColor: 'white' }}>
        <Load value={expedient && tasca} noEffect>
            <CardPage header={headerMain}>
                <Grid container spacing={2}>
                    <Grid size={3}>
                        <ExpedientInfo title={tasca?.expedient?.description} entity={expedient} readOnly/>
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