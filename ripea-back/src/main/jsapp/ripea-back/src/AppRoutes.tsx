import { Routes, Route, Navigate } from 'react-router-dom';
import NotFoundPage from './pages/NotFound';
import Expedient from './pages/expedient/details/Expedient.tsx';
import ExpedientGrid from './pages/expedient/ExpedientGrid';
import AnotacionsGrid from "./pages/anotacions/AnotacionsGrid.tsx";
import TasquesGrid from "./pages/tasca/TasquesGrid.tsx";
import Tasca from "./pages/tasca/details/Tasca.tsx";
import EnviarPortafirmesGrid from "./pages/user/detail/EnviarPortafirmesGrid.tsx";
import FirmaNavegadorGrid from "./pages/user/detail/FirmaNavegadorGrid.tsx";
import CanviEstatGrid from "./pages/user/detail/CanviEstatGrid.tsx";
import TancarGrid from "./pages/user/detail/TancarGrid.tsx";
import CustodiarElementsPendentsGrid from "./pages/user/detail/CustodiarElementsPendentsGrid.tsx";
import CopiarEnllacCSVGrid from "./pages/user/detail/CopiarEnllacCSVGrid.tsx";
import MarcarDefinitiuGrid from "./pages/user/detail/MarcarDefinitiuGrid.tsx";

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
        </Route>
        <Route path="seguimentArxiuPendents" element={<CustodiarElementsPendentsGrid />} />
        <Route path="contingut/:id" element={<Expedient />} />
        <Route path="contingut/:id/tasca/:tascaId" element={<Tasca />} />
        <Route path="expedientPeticio" element={<AnotacionsGrid />} />
        <Route path="usuariTasca" element={<TasquesGrid />} />
        <Route path="*" element={<NotFoundPage />} />
    </Routes>;
}
// massiu/portafirmes

export default AppRoutes;