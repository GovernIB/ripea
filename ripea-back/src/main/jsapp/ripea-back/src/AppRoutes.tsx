import { Routes, Route, Navigate } from 'react-router-dom';
import NotFoundPage from './pages/NotFound';
import Expedient from './pages/expedient/details/Expedient.tsx';
import ExpedientGrid from './pages/expedient/ExpedientGrid';

const AppRoutes: React.FC = () => {
    return <Routes>
        <Route path="/" element={<Navigate to="/expedient" />} />
        <Route path="expedient">
            <Route index element={<ExpedientGrid />} />
            {/*<Route path=":id" element={<Expedient />} />*/}
        </Route>
        <Route path="contingut/:id" element={<Expedient />} />
        <Route path="*" element={<NotFoundPage />} />
    </Routes>;
}

export default AppRoutes;