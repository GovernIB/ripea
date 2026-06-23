import React from 'react';
import { FormFieldReference } from '../../lib/components/mui/form/FormFieldReference';
import { FormFieldCustomProps } from '../../lib/components/form/FormField';
import { useUserSession } from './Session';

const DEFAULT_PAGE_SIZE = 30;

const AppFormFieldReference: React.FC<FormFieldCustomProps> = (props) => {
    const { value: user } = useUserSession();
    const optionsPageSize = (props as any).optionsPageSize ?? user?.sessionScope?.maxResultSelects ?? DEFAULT_PAGE_SIZE;
    return <FormFieldReference {...(props as any)} optionsPageSize={optionsPageSize} />;
};

export default AppFormFieldReference;
