import { Routes, Route } from 'react-router-dom';
import NotFoundPage from './pages/NotFound';
import Expedient from './pages/expedient/Expedient';
import ExpedientGrid from './pages/expedient/ExpedientGrid';

const AppRoutes: React.FC = () => {
    return <Routes>
        <Route path="expedient">
            <Route index element={<ExpedientGrid />} />
            {/*<Route path=":id" element={<Expedient />} />*/}
        </Route>
        <Route path="contingut/:id" element={<Expedient />} />
        <Route path="*" element={<NotFoundPage />} />
    </Routes>;
}

export default AppRoutes;