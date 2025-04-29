import React from 'react';
import { useLocation } from 'react-router-dom';
import { useBaseAppContext } from '../BaseAppContext';
import {
    ResourceApiError,
    ResourceApiOnChangeArgs,
    useResourceApiService,
} from '../ResourceApiProvider';
import { ResourceType } from '../ResourceApiContext';
import { useConfirmDialogButtons } from '../AppButtons';
import { processApiFields } from '../../util/fields';
import useLogConsole from '../../util/useLogConsole';
import { useReducerWithActionMiddleware } from '../../util/useReducerWithActionMiddleware';
import ResourceApiFormContext, {
    FormApi,
    FormApiRef,
    FormFieldError,
    FormFieldDataAction,
    FormFieldDataActionType,
    useFormContext
} from './FormContext';

const LOG_PREFIX = 'FORM';

export type FormProps = React.PropsWithChildren & {
    title?: string;
    resourceName: string;
    resourceType?: ResourceType;
    resourceTypeCode?: string;
    id?: any;
    apiRef?: FormApiRef;
    initialData?: any;
    additionalData?: any;
    perspectives?: string[];
    initOnChangeRequest?: boolean;
    commonFieldComponentProps?: any;
    createLink?: string;
    updateLink?: string;
    saveLink?: string;
    goBackLink?: string;
    onDataChange?: (data: any) => void;
    onCreateSuccess?: (data: any) => void;
    onUpdateSuccess?: (data: any) => void;
    onSaveSuccess?: (data: any) => void;
    fieldTypeMap?: Map<string, string>;
    inline?: boolean;
    debug?: boolean;
};

const formDataReducer = (state: any, action: FormFieldDataAction): any => {
    const { type, payload } = action;
    switch (type) {
        case FormFieldDataActionType.RESET: {
            return payload;
        }
        case FormFieldDataActionType.FIELD_CHANGE: {
            return {
                ...state,
                ...payload.changes,
                [payload.fieldName]: payload.value
            };
        }
    }
}

const processValidationErrors = (
    error: ResourceApiError,
    setFieldErrors: (fieldErrors?: FormFieldError[]) => void): ResourceApiError | undefined => {
    // Es processen únicament els errors de validació (HTTP status 422)
    if (error.status === 422) {
        const errors = error.body.errors ?? error.body.validationErrors;
        // TODO mostrar globalErrors
        //const globalErrors = errors?.find((e: any) => e.field == null);
        const fieldErrors = errors?.
            filter((e: any) => e.field != null).
            map((e: any) => ({
                code: e.code,
                field: e.field,
                message: e.message,
            }));
        setFieldErrors(fieldErrors);
    } else {
        return error;
    }
}

const getInitialDataFromFields = (fields: any[] | undefined) => {
    const initialDataFromFields: any = {};
    fields?.forEach(f => f.value && (initialDataFromFields[f.name] = f.value));
    return initialDataFromFields;
}

export const useFormApiRef: () => React.MutableRefObject<FormApi> = () => {
    const formApiRef = React.useRef<FormApi | any>({});
    return formApiRef;
}

export const useFormApiContext: () => FormApiRef = () => {
    const formContext = useFormContext();
    return formContext.apiRef;
}

export const Form: React.FC<FormProps> = (props) => {
    const {
        resourceName,
        resourceType,
        resourceTypeCode,
        id,
        apiRef: apiRefProp,
        initialData: initialDataProp,
        additionalData: additionalDataProp,
        perspectives,
        initOnChangeRequest,
        commonFieldComponentProps,
        createLink,
        updateLink,
        saveLink,
        goBackLink,
        onDataChange,
        onCreateSuccess,
        onUpdateSuccess,
        onSaveSuccess,
        fieldTypeMap,
        inline,
        debug = false,
        children
    } = props;
    const logConsole = useLogConsole(LOG_PREFIX);
    const {
        goBack,
        navigate,
        useLocationPath,
        temporalMessageShow,
        messageDialogShow,
        t,
    } = useBaseAppContext();
    const locationPath = useLocationPath();
    const {
        isReady: apiIsReady,
        currentFields: apiCurrentFields,
        currentActions: apiCurrentActions,
        currentError: apiCurrentError,
        getOne: apiGetOne,
        onChange: apiOnChange,
        create: apiCreate,
        update: apiUpdate,
        delete: apiDelete,
        artifacts: apiArtifacts,
        artifactFormOnChange: apiArtifactFormOnChange,
        artifactFormValidate: apiArtifactFormValidate
    } = useResourceApiService(resourceName);
    const confirmDialogButtons = useConfirmDialogButtons();
    const confirmDialogComponentProps = { maxWidth: 'sm', fullWidth: true };
    const [isLoading, setIsLoading] = React.useState<boolean>(true);
    const [modified, setModified] = React.useState<boolean>(false);
    const [fields, setFields] = React.useState<any[]>();
    const [fieldErrors, setFieldErrors] = React.useState<FormFieldError[] | undefined>();
    const [revertData, setRevertData] = React.useState<any>(undefined);
    const [apiActions, setApiActions] = React.useState<any>(undefined);
    const apiRef = React.useRef<FormApi>();
    const idFromExternalResetRef = React.useRef<any>();
    const isSaveActionPresent = resourceType == null ? apiActions?.[id != null ? 'update' : 'create'] != null : true;
    const isDeleteActionPresent = id && apiActions?.['delete'] != null;
    const location = useLocation();
    const additionalData = additionalDataProp ?? location.state?.additionalData;
    const calculatedId = (id?: any) => idFromExternalResetRef.current ?? id;
    const sendOnChangeRequest = React.useCallback((id: any, args: ResourceApiOnChangeArgs): Promise<any> => {
        if (resourceType == null) {
            return apiOnChange(id, args);
        } else if (resourceTypeCode != null) {
            const artifactArgs = {
                type: resourceType,
                code: resourceTypeCode,
                ...args
            };
            return apiArtifactFormOnChange(artifactArgs);
        } else {
            return new Promise((_resolve, reject) => reject('Couldn\'t send artifact onChange request: empty resource type code'));
        }
    }, [apiOnChange, resourceType, resourceTypeCode, apiArtifactFormOnChange]);
    const onChangeActionMiddleware = React.useCallback((state: any, action: FormFieldDataAction) => {
        if (action.type === FormFieldDataActionType.FIELD_CHANGE) {
            const { field, fieldName, value: fieldValue } = action.payload;
            if (field?.onChangeActive) {
                return new Promise<FormFieldDataAction>((resolve, reject) => {
                    const onChangeArgs = {
                        fieldName,
                        fieldValue,
                        previous: state,
                    };
                    sendOnChangeRequest(calculatedId(id), onChangeArgs).
                        then((changes: any) => {
                            resolve({
                                type: action.type,
                                payload: { ...action.payload, changes }
                            });
                        }).
                        catch(reject);
                });
            }
        }
    }, [id, sendOnChangeRequest]);
    const [data, dataDispatchAction] = useReducerWithActionMiddleware<FormFieldDataAction>(
        formDataReducer,
        {},
        onChangeActionMiddleware,
        (error: any) => temporalMessageShow(t('form.onChange.error'), error.message, 'error'));
    const getId = () => calculatedId(id);
    const getData = () => data;
    const dataGetValue = (callback: (state: any) => any) => callback(data);
    const getInitialData = React.useCallback(async (id: any, fields: any[], additionalData: any, initOnChangeRequest?: boolean): Promise<any> => {
        // Obté les dades inicials.
        // Si és un formulari d'artefacte obté les dades dels camps
        // Si no és un formulari d'artefacte:
        //     - Si és un formulari de modificació obté les dades fent una petició al servidor
        //     - Si és un formulari de creació obté les dades dels camps
        const getInitialDataFromApiGetOne = resourceType == null && id != null;
        const initialData = getInitialDataFromApiGetOne ? await apiGetOne(id, { data: { perspectives }, includeLinks: true }) : getInitialDataFromFields(fields);
        const mergedData = { ...initialData, ...additionalData };
        return initOnChangeRequest ? await sendOnChangeRequest(id, { previous: mergedData }) : mergedData;
    }, [apiGetOne, sendOnChangeRequest]);
    const processSubmitError = (
        error: ResourceApiError,
        temporalMessageTitle: string,
        reject?: (reason: any) => void) => {
        // S'ignoren els errors de tipus cancel·lació
        if (!error.body?.modificationCanceledError) {
            // Quan es produeixen errors es fa un reject de la promesa.
            // Si els errors els tracta el mateix component Form aleshores la
            // cridada a reject es fa amb un valor buit.
            // Si l'error s'ha de mostrar a l'usuari es fa un reject amb l'error.
            const errorToShow = processValidationErrors(error, setFieldErrors);
            if (errorToShow != null) {
                temporalMessageShow(temporalMessageTitle, error.message, 'error');
                reject?.(errorToShow);
            }
        }
    }
    const refresh = () => {
        if (initialDataProp != null) {
            reset(initialDataProp);
        } else if (fields) {
            getInitialData(id, fields, additionalData, initOnChangeRequest).
                then((initialData: any) => {
                    debug && logConsole.debug('Initial data loaded', initialData);
                    const { _actions: initialDataActions, ...initialDataWithoutLinks } = initialData;
                    id != null && setApiActions(initialDataActions);
                    reset(initialDataWithoutLinks);
                });
        }
    }
    const reset = (data: any) => {
        // Accions per a reiniciar l'estat del formulari
        dataDispatchAction({
            type: FormFieldDataActionType.RESET,
            payload: data,
        });
        setIsLoading(false);
        setModified(false);
        setRevertData(data);
        setFieldErrors(undefined);
        idFromExternalResetRef.current = null;
    }
    const externalReset = (data?: any, id?: any) => {
        // Versió de reset per a cridar externament mitjançant l'API
        const mergedData = data != null ? { ...data, ...additionalData } : additionalData;
        if (initOnChangeRequest) {
            sendOnChangeRequest(id, { previous: mergedData }).
                then((changedData: any) => {
                    reset(changedData);
                    idFromExternalResetRef.current = id;
                });
        } else {
            reset(mergedData);
            idFromExternalResetRef.current = id;
        }
    }
    const revert = (unconfirmed?: boolean) => {
        const revertFn = () => {
            dataDispatchAction({
                type: FormFieldDataActionType.RESET,
                payload: revertData,
            });
            setModified(false);
        }
        if (unconfirmed) {
            revertFn();
        } else {
            messageDialogShow(
                t('form.revert.title'),
                t('form.revert.confirm'),
                confirmDialogButtons,
                confirmDialogComponentProps).
                then((value: any) => {
                    value && revertFn();
                });
        }
    }
    const validate = () => new Promise<any>((resolve, reject) => {
        if (resourceType != null) {
            if (resourceTypeCode != null) {
                setFieldErrors(undefined);
                apiArtifactFormValidate({ type: resourceType, code: resourceTypeCode, data }).
                    then(resolve).
                    catch((error: ResourceApiError) => {
                        processSubmitError(error, t('form.validate.error'), reject)
                    });
            } else {
                reject('Couldn\'t send artifact validate request: empty resource type code');
                console.error()
            }
        } else {
            reject('Form validation only available in form artifacts');
        }
    });
    const save = () => new Promise<any>((resolve, reject) => {
        if (resourceType == null) {
            const calcId = calculatedId(id);
            setFieldErrors(undefined);
            const apiAction = calcId != null ? apiUpdate(calcId, { data }) : apiCreate({ data });
            apiAction.
                then((savedData: any) => {
                    const message = calcId != null ? t('form.update.success') : t('form.create.success');
                    temporalMessageShow(null, message, 'success');
                    reset(savedData);
                    if (calcId != null) {
                        onUpdateSuccess != null ? onUpdateSuccess(savedData) : onSaveSuccess?.(data);
                        if (updateLink != null || saveLink != null) {
                            const link = (updateLink ?? saveLink)?.replace('{{id}}', '' + savedData.id);
                            link && navigate(link, { replace: true, relative: 'route' });
                        }
                    } else {
                        onCreateSuccess != null ? onCreateSuccess(savedData) : onSaveSuccess?.(data);
                        if (createLink || saveLink) {
                            const link = (createLink ?? saveLink)?.replace('{{id}}', '' + savedData.id);
                            if (link?.startsWith('.')) {
                                link && navigate(locationPath + '/' + link, { replace: true });
                            } else if (link?.startsWith('/')) {
                                link && navigate(link.substring(1), { replace: true });
                            } else {
                                const sli = locationPath?.lastIndexOf('/');
                                if (sli != -1) {
                                    link && navigate(locationPath.substring(0, sli + 1) + link, { replace: true });
                                } else {
                                    link && navigate(link, { replace: true });
                                }
                            }
                        }
                    }
                    resolve(savedData);
                }).
                catch((error: ResourceApiError) => {
                    const title = calcId != null ? t('form.update.error') : t('form.create.error');
                    processSubmitError(error, title, reject)
                });
        } else {
            reject(t('form.update.wrong_resource_type', { resourceType }));
        }
    });
    const delette = () => {
        messageDialogShow(
            t('form.delete.title'),
            t('form.delete.confirm'),
            confirmDialogButtons,
            confirmDialogComponentProps).
            then((value: any) => {
                if (value) {
                    const calcId = calculatedId(id);
                    apiDelete(calcId)
                        .then(() => {
                            goBack(goBackLink);
                            temporalMessageShow(null, t('form.delete.success'), 'success');
                        })
                        .catch((error: ResourceApiError) => {
                            temporalMessageShow(t('form.delete.error'), error.message, 'error');
                        });
                }
            });
    }
    const setFieldValue = (name: string, value: any) => {
        const field = fields?.find(f => f.name === name);
        dataDispatchAction({
            type: FormFieldDataActionType.FIELD_CHANGE,
            payload: { fieldName: name, field, value }
        });
    }
    React.useEffect(() => {
        if (apiCurrentError) {
            setIsLoading(false);
        }
    }, [apiCurrentError]);
    React.useEffect(() => {
        // Obté els camps pel formulari fent una petició al servidor
        if (apiIsReady) {
            debug && logConsole.debug('Loading fields' + (resourceType ? ' of type' : ''), resourceType, resourceTypeCode);
            setApiActions(apiCurrentActions);
            if (resourceType == null) {
                setFields(apiCurrentFields);
            } else if (resourceTypeCode != null) {
                apiArtifacts({}).then((artifacts: any[]) => {
                    const artifact = artifacts.find((a: any) => a.type === resourceType.toUpperCase() && a.code === resourceTypeCode);
                    if (artifact != null) {
                        if (artifact.formClassActive) {
                            setFields(processApiFields(artifact.fields));
                        }
                    } else {
                        console.warn('Couldn\'t find artifact (type=' + resourceType + ', code=' + resourceTypeCode + ')');
                    }
                });
            }
        }
    }, [apiIsReady]);
    React.useEffect(() => {
        // Obté les dades inicials pel formulari
        refresh();
    }, [id, fields]);
    React.useEffect(() => {
        // Controla l'estat de formulari amb modificacions
        !isLoading && setModified(true);
        onDataChange?.(data);
    }, [data]);
    apiRef.current = {
        getId,
        getData,
        refresh,
        reset: externalReset,
        revert,
        validate,
        save,
        delete: delette,
        setFieldValue,
    };
    if (apiRefProp) {
        if (apiRefProp.current) {
            apiRefProp.current.getId = getId,
                apiRefProp.current.getData = getData;
            apiRefProp.current.refresh = refresh;
            apiRefProp.current.reset = externalReset;
            apiRefProp.current.revert = revert;
            apiRefProp.current.validate = validate;
            apiRefProp.current.save = save;
            apiRefProp.current.delete = delette;
            apiRefProp.current.setFieldValue = setFieldValue;
        } else {
            logConsole.warn('apiRef prop must be initialized with an empty object');
        }
    }
    const context = React.useMemo(() => ({
        id: calculatedId(id),
        resourceName,
        resourceType,
        resourceTypeCode,
        isLoading,
        isReady: !isLoading,
        apiActions,
        isSaveActionPresent,
        isDeleteActionPresent,
        fields,
        fieldErrors,
        fieldTypeMap,
        inline,
        data,
        modified: modified ?? false,
        apiRef,
        dataGetFieldValue: (fieldName: string) => dataGetValue((state) => state?.[fieldName]),
        dataDispatchAction,
        commonFieldComponentProps,
    }), [isLoading, apiActions, fields, fieldErrors, data, dataDispatchAction, commonFieldComponentProps]);
    return <ResourceApiFormContext.Provider value={context}>
        {!isLoading ? children : null}
    </ResourceApiFormContext.Provider>;
}

export default Form;