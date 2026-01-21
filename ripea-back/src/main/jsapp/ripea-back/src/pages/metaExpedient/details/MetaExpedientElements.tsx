import {GridPage, useResourceApiService} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import {useTranslation} from "react-i18next";
import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import Load from "../../../components/Load.tsx";
import TabComponent from "../../../components/TabComponent.tsx";
import {MetDadaGrid} from "../../metaDocument/details/MetaDadaGrid.tsx";
import {MetaExpedientTascaGrid} from "./elements/MetaExpedientTascaGrid.tsx";
import {GrupGrid} from "./elements/GrupGrid.tsx";
import {MetaDocumentGrid} from "./elements/MetaDocumentGrid.tsx";
import {MetaExpedientEstatGrid} from "./elements/MetaExpedientEstatGrid.tsx";
import {setTitlePage} from "../../../TitleHeaderConfigurator.tsx";
import {MetaExpedientCarpetaGrid} from "./elements/MetaExpedientCarpetaGrid.tsx";
import {useUserSession} from "../../../components/Session.tsx";

const perspectives :string[] = ["ELEMENTS_COUNT"]
export const MetaExpedientElements = () => {
    const {t} = useTranslation()
    const { id, element } = useParams();
    const {value: user} = useUserSession();

    const {
        isReady: apiIsReady,
        getOne: appGetOne,
    } = useResourceApiService('metaExpedientResource');
    const [metaExpedient, setMetaExpedient] = useState<any>();
    const refreshMetaExpedient = () => {
        appGetOne(id, {perspectives})
            .then((app) => setMetaExpedient(app))
    }

    useEffect(() => {
        if (apiIsReady) {
            refreshMetaExpedient()
        }
    },[apiIsReady, id])

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
            content: <MetaDocumentGrid entity={metaExpedient} onRowCountChange={setNumMetaDocument}/>,
            badge: numMetaDocument ?? metaExpedient?.numMetaDocument,
            showZero: true,
        },
        {
            value: "metaDada",
            label: t('page.metaExpedient.tabs.metaDada'),
            content: <MetDadaGrid id={id} enviable onRowCountChange={setNumMetaDada}/>,
            badge: numMetaDada ?? metaExpedient?.numMetaDada,
            showZero: true,
        },
        {
            value: "estat",
            label: t('page.metaExpedient.tabs.expedientEstat'),
            content: <MetaExpedientEstatGrid entity={metaExpedient} onRowCountChange={setNumEstat}/>,
            badge: numEstat ?? metaExpedient?.numEstat,
            showZero: true,
        },
        {
            value: "tasca",
            label: t('page.metaExpedient.tabs.tasca'),
            content: <MetaExpedientTascaGrid entity={metaExpedient} onRowCountChange={setNumTasca}/>,
            badge: numTasca ?? metaExpedient?.numTasca,
            showZero: true,
        },
        {
            value: "grup",
            label: t('page.metaExpedient.tabs.grup'),
            content: <GrupGrid entity={metaExpedient} refresh={refreshMetaExpedient} onRowCountChange={setNumGrup}/>,
            badge: numGrup ?? metaExpedient?.numGrup,
            showZero: true,
            hidden: !metaExpedient?.gestioAmbGrupsActiva,
        },
        {
            value: "carpeta",
            label: t('page.metaExpedient.tabs.carpeta'),
            content: <MetaExpedientCarpetaGrid entity={metaExpedient} onRowCountChange={setNumCarpeta}/>,
            badge: numCarpeta ?? metaExpedient?.numCarpetes,
            showZero: true,
            // hidden: !user?.sessionScope?.isCarpetesDefecte,
        },
    ]

    useEffect(() => {
        if (metaExpedient) {
            setTitlePage(t('page.metaExpedient.detall.elements', {nom: metaExpedient?.nom}))
        }
    }, [metaExpedient]);

    return <GridPage disableMargins>
        <Load value={metaExpedient}>
            <CardPage title={t('page.metaExpedient.detall.elements', {nom: metaExpedient?.nom})}>
                <TabComponent defaultValue={element} tabs={tabs}/>
            </CardPage>
        </Load>
    </GridPage>
}