import React from 'react';
import { useBaseAppContext } from './BaseAppContext';

type FormPageProps = React.PropsWithChildren & {
    disableMargins?: true;
};

export const FormPage: React.FC<FormPageProps> = (props) => {
    const { disableMargins = true, children } = props;
    const { setMarginsDisabled } = useBaseAppContext();
    React.useEffect(() => {
        setMarginsDisabled(disableMargins);
        return () => setMarginsDisabled(false);
    }, [disableMargins]);
    return children;
}

export default FormPage;