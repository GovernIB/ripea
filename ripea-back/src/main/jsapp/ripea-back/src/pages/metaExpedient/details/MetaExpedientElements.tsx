import {GridPage, useResourceApiService} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import {useTranslation} from "react-i18next";
import {useNavigate, useParams} from "react-router-dom";
import {useEffect, useMemo, useState} from "react";
import Load from "../../../components/Load.tsx";
import TabComponent from "../../../components/TabComponent.tsx";
import {MetDadaGrid} from "./elements/MetaDadaGrid.tsx";
import {MetaExpedientTascaGrid} from "./elements/MetaExpedientTascaGrid.tsx";
import {GrupGrid} from "./elements/GrupGrid.tsx";
import {MetaDocumentGrid} from "./elements/MetaDocumentGrid.tsx";
import {MetaExpedientEstatGrid} from "./elements/MetaExpedientEstatGrid.tsx";
import {setTitlePage} from "../../../TitleHeaderConfigurator.tsx";
import {MetaExpedientCarpetaGrid} from "./elements/MetaExpedientCarpetaGrid.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import {Button, Icon} from "@mui/material";
import {ErrorPage} from "../../../components/ErrorPage.tsx";

const perspectives :string[] = ["ELEMENTS_COUNT"]
export const MetaExpedientElements = () => {
    const {t} = useTranslation()
    const { id, element } = useParams();
    const {value: user, rol} = useUserSession();
    const navigate = useNavigate();
    const [error, setError] = useState<any>();

    const {
        isReady: apiIsReady,
        getOne: appGetOne,
    } = useResourceApiService('metaExpedientResource');
    const [metaExpedient, setMetaExpedient] = useState<any>();
    const refreshMetaExpedient = () => {
        appGetOne(id, {perspectives})
            .then((app) => setMetaExpedient(app))
            .catch((error) => setError(error))
    }

    useEffect(() => {
        if (apiIsReady) {
            refreshMetaExpedient()
        }
    },[apiIsReady, id])

    const readOnly = useMemo(() => {
        return !(rol.isAdmin || (rol.isOrganAdmin && metaExpedient?.revisioEstat != 'REVISAT') || rol.isDissenyOrgan)
    }, [metaExpedient, rol])

    const [numMetaDocument, setNumMetaDocument] = useState<number>();
    const [numMetaDada, setNumMetaDada] = useState<number>();
    const [numEstat, setNumEstat] = useState<number>();
    const [numTasca, setNumTasca] = useState<number>();
    const [numGrup, setNumGrup] = useState<number>();
    const [numCarpeta, setNumCarpeta] = useState<number>();

    const tabs :any[] = [
        {
            value: "metaDocument",
            label: t('page.metaExpedient.tabs.metaDocument'),
            content: <MetaDocumentGrid entity={metaExpedient} onRowCountChange={setNumMetaDocument} readOnly={readOnly}/>,
            badge: numMetaDocument ?? metaExpedient?.numMetaDocument,
            showZero: true,
        },
        {
            value: "metaDada",
            label: t('page.metaExpedient.tabs.metaDada'),
            content: <MetDadaGrid id={id} enviable onRowCountChange={setNumMetaDada} readOnly={readOnly}/>,
            badge: numMetaDada ?? metaExpedient?.numMetaDada,
            showZero: true,
        },
        {
            value: "estat",
            label: t('page.metaExpedient.tabs.expedientEstat'),
            content: <MetaExpedientEstatGrid entity={metaExpedient} onRowCountChange={setNumEstat} readOnly={readOnly}/>,
            badge: numEstat ?? metaExpedient?.numEstat,
            showZero: true,
        },
        {
            value: "tasca",
            label: t('page.metaExpedient.tabs.tasca'),
            content: <MetaExpedientTascaGrid entity={metaExpedient} onRowCountChange={setNumTasca} readOnly={readOnly}/>,
            badge: numTasca ?? metaExpedient?.numTasca,
            showZero: true,
        },
        {
            value: "grup",
            label: t('page.metaExpedient.tabs.grup'),
            content: <GrupGrid entity={metaExpedient} refresh={refreshMetaExpedient} onRowCountChange={setNumGrup} readOnly={readOnly}/>,
            badge: numGrup ?? metaExpedient?.numGrup,
            showZero: true,
            hidden: !metaExpedient?.gestioAmbGrupsActiva,
        },
        {
            value: "carpeta",
            label: t('page.metaExpedient.tabs.carpeta'),
            content: <MetaExpedientCarpetaGrid entity={metaExpedient} onRowCountChange={setNumCarpeta} readOnly={readOnly}/>,
            badge: numCarpeta ?? metaExpedient?.numCarpetes,
            showZero: true,
            hidden: !user?.sessionScope?.isCarpetesDefecte,
        },
    ]

    const title = useMemo(() => {
        return metaExpedient?.tipusProcedimentServei === 'PROCEDIMENT'
            ? t('page.metaExpedient.detall.elementsProc', {nom: metaExpedient?.nom})
            : t('page.metaExpedient.detall.elementsServ', {nom: metaExpedient?.nom})
    }, [t, metaExpedient])

    useEffect(() => {
        if (metaExpedient) {
            setTitlePage(title)
        }
    }, [metaExpedient]);

    if (error)
        return <ErrorPage error={error}/>

    return <GridPage autoHeight>
        <Load value={metaExpedient}>
            <CardPage title={title}
                      header={<>
                          <Button
                              variant="outlined"
                              color={"inherit"}
                              sx={{ borderRadius: '4px', padding: '0px 10px', marginLeft: "auto !important" }}
                              onClick={()=>navigate('/metaExpedient')}
                          >
                              <Icon>arrow_back</Icon>
                              {t('common.back')}
                          </Button>
                      </>}>
                <TabComponent defaultValue={element} tabs={tabs}/>
            </CardPage>
        </Load>
    </GridPage>
}