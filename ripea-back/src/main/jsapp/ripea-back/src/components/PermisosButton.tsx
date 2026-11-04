import { useTranslation } from 'react-i18next';
import BadgeIconButton from './BadgeIconButton.tsx';

type PermisosButtonProps = {
    /** Nombre de permisos que es mostra al badge. */
    numPermisos?: number;
    /** Pinta el badge en vermell (permisos erronis pendents de revisar). */
    error?: boolean;
    title?: string;
    /** Ruta de la pantalla de permisos. Si no s'indica, es crida `onClick`. */
    to?: string;
    onClick?: () => void;
};

/** Botó de les columnes de permisos de les graelles: icona de clau amb el nombre de permisos. */
const PermisosButton = (props: PermisosButtonProps) => {
    const { numPermisos, error, title, to, onClick } = props;
    const { t } = useTranslation();
    return (
        <BadgeIconButton
            icon={'key'}
            title={title ?? t('common.permisos')}
            badgeContent={numPermisos}
            badgeColor={error ? 'error' : 'primary'}
            to={to}
            onClick={onClick}
        />
    );
};
export default PermisosButton;
