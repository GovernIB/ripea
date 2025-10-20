import { Routes, Route, Navigate } from 'react-router-dom';
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

const AppRoutes: React.FC = () => {
    return <Routes>
        <Route path="/" element={<Navigate to="/expedient" />} />
        <Route path="expedient">
            <Route index element={<ExpedientGrid />} />
            {/*<Route path=":id" element={<Expedient />} />*/}
        </Route>
        <Route path="massiu">
            <Route path={"portafirmes"} element={<EnviarPortafirmesGrid />} />
            <Route path={"firmasimpleweb"} element={<FirmaNavegadorGrid />} />
            <Route path={"canviEstat"} element={<CanviEstatGrid />} />
            <Route path={"tancament"} element={<TancarGrid />} />
            <Route path={"csv"} element={<CopiarEnllacCSVGrid />} />
            <Route path={"definitiu"} element={<MarcarDefinitiuGrid />} />
            <Route path={"canviPrioritats"} element={<CanviPrioritatGrid />} />
            <Route path={"expedientPeticioCanviEstatDistribucio"} element={<ActualitzarEstatAnotacioGrid />} />
        </Route>
        <Route path="seguimentArxiuPendents" element={<CustodiarElementsPendentsGrid />} />
        <Route path="seguimentPortafirmes" element={<DocumentEnviatsPortafirmesGrid />} />
        <Route path="seguimentNotificacions" element={<RemesesNotibGrid />} />

        <Route path="contingut/:id" element={<Expedient />} />
        <Route path="contingut/:id/tasca/:tascaId" element={<Tasca />} />
        <Route path="expedientPeticio" element={<AnotacionsGrid />} />
        <Route path="usuariTasca" element={<TasquesGrid />} />
        <Route path="*" element={<NotFoundPage />} />
    </Routes>;
}
// massiu/portafirmes

export default AppRoutes;