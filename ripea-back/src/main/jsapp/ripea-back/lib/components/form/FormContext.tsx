import React from 'react';

export type FormApi = {
    getData: () => any;
    refresh: () => void;
    reset: (data?: any, id?: any) => void;
    revert: (unconfirmed?: boolean) => void;
    save: () => Promise<any>;
    delete: () => void;
    setFieldValue: (name: string, value: any) => void;
};

export type FormApiRef = React.MutableRefObject<FormApi | undefined>;

export enum FormFieldDataActionType {
    RESET = 'RESET',
    FIELD_CHANGE = 'FIELD_CHANGE',
};

export type FormFieldDataActionPayload = {
    field: any;
    fieldName: string;
    value: any;
    changes?: any;
};

export type FormFieldDataAction = {
    type: FormFieldDataActionType;
    payload: FormFieldDataActionPayload;
};

export type FormResourceType = 'action' | 'report' | 'filter';

export type FormContextType = {
    id?: any;
    resourceName: string;
    resourceType?: FormResourceType;
    resourceTypeCode?: string;
    isLoading: boolean;
    isReady: boolean;
    apiLinks?: any;
    isSaveActionPresent: boolean;
    isDeleteActionPresent: boolean;
    fields?: any[];
    fieldErrors?: FormFieldError[];
    fieldTypeMap?: Map<string, string>;
    inline?: boolean;
    data?: any;
    modified: boolean;
    apiRef: FormApiRef;
    dataGetFieldValue: (fieldName: string) => any;
    dataDispatchAction: (action: FormFieldDataAction) => void;
    commonFieldComponentProps?: any;
};

export type FormFieldError = {
    code: string;
    field: string;
    message: string;
};

export const FormContext = React.createContext<FormContextType | undefined>(undefined);

export const useFormContext = () => {
    const context = React.useContext(FormContext);
    if (context === undefined) {
        throw new Error('useFormContext must be used within a FormProvider');
    }
    return context;
}

export const useOptionalFormContext = (): FormContextType | undefined => {
    return React.useContext(FormContext);
}

export default FormContext;