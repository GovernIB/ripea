import {Routes, Route, Navigate, Outlet} from 'react-router-dom';
import NotFoundPage from './pages/NotFound';
import Expedient from './pages/expedient/details/Expedient.tsx';
import ExpedientGrid from './pages/expedient/ExpedientGrid';
import AnotacionsGrid from "./pages/anotacions/AnotacionsGrid.tsx";
import TasquesGrid from "./pages/tasca/TasquesGrid.tsx";
import Tasca from "./pages/tasca/details/Tasca.tsx";
import EnviarPortafirmesGrid from "./pages/user/accionsMassives/EnviarPortafirmesGrid.tsx";
import FirmaNavegadorGrid from "./pages/user/accionsMassives/FirmaNavegadorGrid.tsx";
import CanviEstatGrid from "./pages/user/accionsMassives/CanviEstatGrid.tsx";
import TancarGrid from "./pages/user/accionsMassives/TancarGrid.tsx";
import CustodiarElementsPendentsGrid from "./pages/user/accionsMassives/CustodiarElementsPendentsGrid.tsx";
import CopiarEnllacCSVGrid from "./pages/user/accionsMassives/CopiarEnllacCSVGrid.tsx";
import MarcarDefinitiuGrid from "./pages/user/accionsMassives/MarcarDefinitiuGrid.tsx";
import CanviPrioritatGrid from "./pages/user/accionsMassives/CanviPrioritatGrid.tsx";
import ActualitzarEstatAnotacioGrid from "./pages/user/accionsMassives/ActualitzarEstatAnotacioGrid.tsx";
import DocumentEnviatsPortafirmesGrid from "./pages/user/consultes/DocumentEnviatsPortafirmesGrid.tsx";
import RemesesNotibGrid from "./pages/user/consultes/RemesesNotibGrid.tsx";
import ConsultesPinbalGrid from "./pages/user/consultes/ConsultesPinbalGrid.tsx";
import AssignacioTasquesGrid from "./pages/user/consultes/AssignacioTasquesGrid.tsx";
import ExpedientsPendentsGrid from "./pages/user/consultes/ExpedientsPendentsGrid.tsx";
import AnotacionsComunicadesGrid from "./pages/user/consultes/AnotacionsComunicadesGrid.tsx";
import ContingutGrid from "./pages/user/consultes/ContingutGrid.tsx";
import MetaExpedientGrid from "./pages/metaExpedient/MetaExpedientGrid.tsx";
import GrupGrid from "./pages/user/configurar/GrupGrid.tsx";
import OrganGestorGrid from "./pages/organGestor/OrganGestorGrid.tsx";
import TipusDocumentalGrid from "./pages/user/configurar/TipusDocumentalGrid.tsx";
import PermisEntitatGrid from "./pages/user/configurar/PermisEntitatGrid.tsx";
import PermisGrupGrid from "./pages/user/configurar/PermisGrupGrid.tsx";
import PermisOrganGestorGrid from "./pages/user/configurar/PermisOrganGestorGrid.tsx";
import PermisMetaExpedientGrid from "./pages/user/configurar/PermisMetaExpedientGrid.tsx";
import AdjuntarAnnexosPendentsGrid from "./pages/user/accionsMassives/AdjuntarAnnexosPendentsGrid.tsx";
import DominiGrid from "./pages/user/configurar/DominiGrid.tsx";
import MetaDocumentGrid from "./pages/metaDocument/MetaDocumentGrid.tsx";
import MetaDadaGrid from "./pages/metaExpedient/details/elements/MetaDadaGrid.tsx";
import {rols, useUserSession} from "./components/Session.tsx";
import {UrlInstruccioGrid} from "./pages/user/configurar/UrlInstruccioGrid.tsx";
import {MetaExpedientElements} from "./pages/metaExpedient/details/MetaExpedientElements.tsx";
import MetaExpedientTascaValidacioGrid
    from "./pages/metaExpedient/details/elements/MetaExpedientTascaValidacioGrid.tsx";
import {EntitatGrid} from "./pages/entitat/EntitatGrid.tsx";
import {AvisGrid} from "./pages/avis/AvisGrid.tsx";
import {ServeiPinbalGrid} from "./pages/user/configurar/ServeiPinbalGrid.tsx";
import {Propietats, PropietatsByEntitat} from "./pages/user/propietats/Propietats.tsx";
import {ExcepcioGrid} from "./pages/user/monitor/ExcepcioGrid.tsx";
import {IntegracioGrid} from "./pages/user/monitor/integracio/IntegracioGrid.tsx";

const ProtectedRoute = ({ allowedRoles = [], params = [] }: any) => {
    const {value: user} = useUserSession();

    if (allowedRoles?.length > 0 && !allowedRoles.includes(user.rolActual)) {
        // return <NotFoundPage />;
    }
    if (params?.length > 0 && params?.some?.((param:string) => !user?.sessionScope?.[param])) {
        // return <NotFoundPage />;
    }

    return <Outlet />;
};

const HomeRedirect = () => {
    const {value: user} = useUserSession();

    switch (user?.rolActual) {
        case rols.SUPER:
            return <Navigate to="/integracio" replace />;
        case rols.REVISIO:
            return <Navigate to="/metaExpedient" replace />;
        default:
            return <Navigate to="/expedient" replace />;
    }
};

const AppRoutes: React.FC = () => {
    return <Routes>
        <Route path="/" element={<HomeRedirect />} />
        <Route path="expedient" element={<ExpedientGrid />}/>
        <Route path="contingut/:id" element={<Expedient />} />
        <Route path="contingut/:id/tasca/:tascaId" element={<Tasca />} />
        <Route path="expedientPeticio" element={<AnotacionsGrid />} />
        <Route path="usuariTasca" element={<TasquesGrid />} />

        <Route element={<ProtectedRoute allowedRoles={[rols.SUPER]} />}>
            <Route path={"entitat"} element={<EntitatGrid />} />
            <Route path={"entitat/:id/permis"} element={<PermisEntitatGrid/>} />
            <Route path={"entitat/:id/configurar"} element={<PropietatsByEntitat/>} />
            <Route path={"avis"} element={<AvisGrid/>} />
            <Route path={"pinbalServei"} element={<ServeiPinbalGrid/>} />
            <Route path={"config"} element={<Propietats/>} />
            <Route path={"integracio"} element={<IntegracioGrid/>} />
            <Route path={"excepcio"} element={<ExcepcioGrid/>} />
        </Route>

        {/* Accions massives */}
        <Route element={<ProtectedRoute allowedRoles={[rols.ADMIN, rols.ORGAN_ADMIN, rols.tothom]} />}>
            <Route path="massiu">
                <Route path={"portafirmes"} element={<EnviarPortafirmesGrid />} />
                <Route path={"firmasimpleweb"} element={<FirmaNavegadorGrid />} />
                <Route path={"canviEstat"} element={<CanviEstatGrid />} />
                <Route path={"tancament"} element={<TancarGrid />} />
                <Route path={"csv"} element={<CopiarEnllacCSVGrid />} />
                <Route path={"definitiu"} element={<MarcarDefinitiuGrid />} />
                <Route path={"canviPrioritats"} element={<CanviPrioritatGrid />} />
                <Route path={"expedientPeticioCanviEstatDistribucio"} element={<ActualitzarEstatAnotacioGrid />} />
                <Route path={"procesarAnnexosPendents"} element={<AdjuntarAnnexosPendentsGrid />} />
            </Route>
            <Route path="seguimentArxiuPendents" element={<CustodiarElementsPendentsGrid />} />
        </Route>

        {/* Consultes */}
        <Route element={<ProtectedRoute allowedRoles={[rols.ADMIN]} />}>
            <Route path="contingutAdmin" element={<ContingutGrid/>} />
            {/*<Route path="metaExpedientRevisio" element={<RevisioMetaExpedientGrid />} />*/}
            <Route path="seguimentNotificacions" element={<RemesesNotibGrid />} />
            <Route path="seguimentPinbal" element={<ConsultesPinbalGrid />} />
            <Route path="seguimentTasques" element={<AssignacioTasquesGrid />} />
            <Route path="seguimentExpedientsPendents" element={<ExpedientsPendentsGrid />} />
            <Route path="expedientPeticioComunicades" element={<AnotacionsComunicadesGrid/>} />
        </Route>
        <Route element={<ProtectedRoute allowedRoles={[rols.ADMIN, rols.tothom]} />}>
            <Route path="seguimentPortafirmes" element={<DocumentEnviatsPortafirmesGrid />} />
        </Route>

        {/* Configurar */}
        <Route element={<ProtectedRoute allowedRoles={[rols.ADMIN, rols.ADMIN_LECTURA, rols.ORGAN_ADMIN, rols.DISSENY, rols.REVISIO]} />}>
            <Route path="metaExpedient" element={<MetaExpedientGrid/>} />
            <Route path="metaExpedient/:id/permis" element={<PermisMetaExpedientGrid/>} />
            <Route path="metaExpedient/:id/:element" element={<MetaExpedientElements/>} />
            <Route path="expedientEstat/:id" element={<MetaExpedientElements/>} />
            <Route path="metaExpedient/:id/tasca/:tascaId/validacio" element={<MetaExpedientTascaValidacioGrid/>} />
        </Route>
        <Route element={<ProtectedRoute allowedRoles={[rols.ADMIN, rols.ORGAN_ADMIN, rols.DISSENY]} />}>
            <Route path="grup" element={<GrupGrid/>} />
            <Route path="grupPermis/:id/permis" element={<PermisGrupGrid/>} />
        </Route>
        <Route element={<ProtectedRoute allowedRoles={[rols.ADMIN]} />}>
            <Route path="tipusDocumental" element={<TipusDocumentalGrid/>} />
            <Route path="organgestor" element={<OrganGestorGrid/>} />
            <Route path="organgestor/:id/permis" element={<PermisOrganGestorGrid/>} />
            <Route path="permis" element={<PermisEntitatGrid/>} />
            <Route path="domini" element={<DominiGrid/>} />
            <Route path="metaDocument" element={<MetaDocumentGrid/>} />
            <Route path="metaDocument/:id/metaDada" element={<MetaDadaGrid/>} />
            <Route element={<ProtectedRoute params={['isUrlInstruccioEnabled']}/>}>
                <Route path="urlInstruccio" element={<UrlInstruccioGrid/>} />
            </Route>
        </Route>

        <Route path="*" element={<NotFoundPage />} />
    </Routes>;
}

export default AppRoutes;