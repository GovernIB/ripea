import React, { KeyboardEvent } from 'react';
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
    useFormContext,
} from './FormContext';

const LOG_PREFIX = 'FORM';

/**
 * Propietats del component Form.
 */
export type FormProps = React.PropsWithChildren & {
    /** Títol del formulari */
    title?: string;
    /** Nom del recurs de l'API REST d'on es consultarà la informació per a mostrar el formulari */
    resourceName: string;
    /** Tipus de l'artefacte associat al recurs (emplenar només quan es vulgui mostrar el formulari associat a un artefacte del recurs) */
    resourceType?: ResourceType;
    /** Codi de l'artefacte associat al recurs (emplenar només quan es vulgui mostrar el formulari associat a un artefacte del recurs) */
    resourceTypeCode?: string;
    /** Id del recurs a modificar (si no s'especifica s'assumirà que és un formulari de creació) */
    id?: any;
    /** Referència a l'api del component */
    apiRef?: FormApiRef;
    /** Dades inicials pel formulari */
    initialData?: any;
    /** Dades addicionals que s'enviaran amb les dades del formulari */
    additionalData?: any;
    /** Perspectives que s'enviaran al consultar la informació del recurs */
    perspectives?: string[];
    /** Indica si s'ha de fer una petició onChange sense cap camp associat quan es crea el component */
    initOnChangeRequest?: true;
    /** Indica si s'ha de desar el formulari quan es pitgi la tecla Intro en algun camp */
    saveOnFieldEnterKeyPressed?: true;
    /** Propietats comunes per a tots els components FormField de dins aquest component */
    commonFieldComponentProps?: any;
    /** Adreça que s'ha de mostrar una vegada creat un registre (Es substituirà el text '{{id}}' per l'id del recurs creat) */
    createLink?: string;
    /** Adreça que s'ha de mostrar una vegada modificat un registre (Es substituirà el text '{{id}}' per l'id del recurs modificat) */
    updateLink?: string;
    /** Adreça que s'ha de mostrar una vegada creat o modificat un registre (Es substituirà el text '{{id}}' per l'id del recurs creat) */
    saveLink?: string;
    /** Adreça que s'ha de mostrar al fer click al botó de retrocedir (només s'utilitzarà si l'historial està buit) */
    goBackLink?: string;
    /** Event que es llença quan es modifica alguna dada del formulari */
    onDataChange?: (data: any) => void;
    /** Event que es llença quan es crea un nou registre */
    onCreateSuccess?: (data: any) => void;
    /** Event que es llença quan es modifica un registre */
    onUpdateSuccess?: (data: any) => void;
    /** Event que es llença quan es desa un registre (creat o modificat) */
    onSaveSuccess?: (data: any) => void;
    /** Mapeig dels tipus de camp */
    fieldTypeMap?: Map<string, string>;
    /** Indica que és un formulari d'una sola línia (per exemple: formularis que es mostran a una fila de la graella) */
    inline?: true;
    /** Indica si s'han d'imprimir a la consola missatges de depuració */
    debug?: true;
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
                [payload.fieldName]: payload.value,
            };
        }
    }
};

const getInitialDataFromFields = (fields: any[] | undefined) => {
    const initialDataFromFields: any = {};
    fields?.forEach((f) => f.value && (initialDataFromFields[f.name] = f.value));
    return initialDataFromFields;
};

/**
 * Hook per a accedir a l'API de Form des de fora del context del component.
 *
 * @returns referència a l'API del component Form.
 */
export const useFormApiRef: () => React.RefObject<FormApi> = () => {
    const formApiRef = React.useRef<FormApi | any>({});
    return formApiRef;
};

/**
 * Hook per a accedir a l'API de Form des de dins el context del component.
 *
 * @returns referència a l'API del component Form.
 */
export const useFormApiContext: () => FormApiRef = () => {
    const formContext = useFormContext();
    return formContext.apiRef;
};

/**
 * Component base de formulari independent de la llibreria de interfície d'usuari.
 *
 * @param props - Propietats del component.
 * @returns Element JSX del formulari.
 */
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
        saveOnFieldEnterKeyPressed,
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
        children,
    } = props;
    const logConsole = useLogConsole(LOG_PREFIX);
    const { goBack, navigate, useLocationPath, temporalMessageShow, messageDialogShow, t } =
        useBaseAppContext();
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
        artifactFormValidate: apiArtifactFormValidate,
    } = useResourceApiService(resourceName);
    const confirmDialogButtons = useConfirmDialogButtons();
    const confirmDialogComponentProps = { maxWidth: 'sm', fullWidth: true };
    const [isLoading, setIsLoading] = React.useState<boolean>(true);
    const [modified, setModified] = React.useState<boolean>(false);
    const [fields, setFields] = React.useState<any[]>();
    const [fieldErrors, setFieldErrors] = React.useState<FormFieldError[] | undefined>();
    const [revertData, setRevertData] = React.useState<any>(undefined);
    const [apiActions, setApiActions] = React.useState<any>(undefined);
    const apiRef = React.useRef<FormApi>(undefined);
    const idFromExternalResetRef = React.useRef<any>(undefined);
    const isSaveActionPresent =
        resourceType == null ? apiActions?.[id != null ? 'update' : 'create'] != null : true;
    const isDeleteActionPresent = id && apiActions?.['delete'] != null;
    const location = useLocation();
    const additionalData = additionalDataProp ?? location.state?.additionalData;
    const calculatedId = (id?: any) => idFromExternalResetRef.current ?? id;
    const isReady = !isLoading;
    const sendOnChangeRequest = React.useCallback(
        (id: any, args: ResourceApiOnChangeArgs): Promise<any> => {
            if (resourceType == null) {
                return apiOnChange(id, args);
            } else if (resourceTypeCode != null) {
                const artifactArgs = {
                    id,
                    type: resourceType,
                    code: resourceTypeCode,
                    ...args,
                };
                return apiArtifactFormOnChange(artifactArgs);
            } else {
                return new Promise((_resolve, reject) =>
                    reject("Couldn't send artifact onChange request: empty resource type code")
                );
            }
        },
        [apiOnChange, resourceType, resourceTypeCode, apiArtifactFormOnChange]
    );
    const onChangeActionMiddleware = React.useCallback(
        (state: any, action: FormFieldDataAction) => {
            if (action.type === FormFieldDataActionType.FIELD_CHANGE) {
                const { field, fieldName, value: fieldValue } = action.payload;
                if (field?.onChangeActive) {
                    return new Promise<FormFieldDataAction>((resolve, reject) => {
                        const onChangeArgs = {
                            fieldName,
                            fieldValue,
                            previous: state,
                        };
                        sendOnChangeRequest(calculatedId(id), onChangeArgs)
                            .then((changes: any) => {
                                resolve({
                                    type: action.type,
                                    payload: { ...action.payload, changes },
                                });
                            })
                            .catch(reject);
                    });
                }
            }
        },
        [id, sendOnChangeRequest]
    );
    const [data, dataDispatchAction] = useReducerWithActionMiddleware<FormFieldDataAction>(
        formDataReducer,
        {},
        onChangeActionMiddleware,
        (error: any) => temporalMessageShow(t('form.onChange.error'), error.message, 'error')
    );
    const getId = () => calculatedId(id);
    const getData = () => data;
    const dataGetValue = (callback: (state: any) => any) => callback(data);
    const getInitialData = React.useCallback(
        async (
            id: any,
            fields: any[],
            additionalData: any,
            initOnChangeRequest?: boolean
        ): Promise<any> => {
            // Obté les dades inicials.
            // Si és un formulari d'artefacte obté les dades dels camps
            // Si no és un formulari d'artefacte:
            //     - Si és un formulari de modificació obté les dades fent una petició al servidor
            //     - Si és un formulari de creació obté les dades dels camps
            const getInitialDataFromApiGetOne = resourceType == null && id != null;
            const initialData = getInitialDataFromApiGetOne
                ? await apiGetOne(id, {
                      data: { perspectives },
                      includeLinks: true,
                  })
                : getInitialDataFromFields(fields);
            const mergedData = { ...initialData, ...additionalData };
            if (initOnChangeRequest) {
                return new Promise<any>((resolve, reject) => {
                    sendOnChangeRequest(id, { previous: mergedData })
                        .then((onChangeData) => {
                            resolve({ ...mergedData, ...onChangeData });
                        })
                        .catch(reject);
                });
            } else {
                return mergedData;
            }
        },
        [apiGetOne, sendOnChangeRequest]
    );
    const handleSubmissionErrors = (
        error: ResourceApiError,
        temporalMessageTitle?: string,
        reject?: (reason: any) => void
    ) => {
        // S'ignoren els errors de tipus cancel·lació
        if (!error.body?.modificationCanceledError) {
            // Quan es produeixen errors es fa un reject de la promesa.
            // Si els errors els tracta el mateix component Form aleshores la
            // cridada a reject es fa amb un valor buit.
            // Si l'error s'ha de mostrar a l'usuari es fa un reject amb l'error.
            if (error.status === 422) {
                const errors = error.body.errors ?? error.body.validationErrors;
                // TODO mostrar globalErrors
                //const globalErrors = errors?.find((e: any) => e.field == null);
                const fieldErrors = errors
                    ?.filter((e: any) => e.field != null)
                    .map((e: any) => ({
                        code: e.code,
                        field: e.field,
                        message: e.message,
                    }));
                setFieldErrors(fieldErrors);
            } else {
                temporalMessageShow(temporalMessageTitle ?? '', error.message, 'error');
                reject?.(error);
            }
        }
    };
    const reset = (data: any) => {
        dataDispatchAction({
            type: FormFieldDataActionType.RESET,
            payload: data,
        });
        setIsLoading(false);
        setModified(false);
        setRevertData(data);
        setFieldErrors(undefined);
        idFromExternalResetRef.current = null;
    };
    const refresh = () => {
        if (initialDataProp != null) {
            reset(initialDataProp);
        } else if (fields) {
            getInitialData(id, fields, additionalData, initOnChangeRequest).then(
                (initialData: any) => {
                    debug && logConsole.debug('Initial data loaded', initialData);
                    const { _actions: initialDataActions, ...initialDataWithoutLinks } =
                        initialData;
                    id != null && setApiActions(initialDataActions);
                    reset(initialDataWithoutLinks);
                }
            );
        }
    };
    const externalReset = (data?: any, id?: any) => {
        // Versió de reset per a cridar externament mitjançant l'API
        const mergedData = {
            ...getInitialDataFromFields(fields),
            ...additionalData,
            ...data,
        };
        if (initOnChangeRequest) {
            sendOnChangeRequest(id, { previous: mergedData }).then((changedData: any) => {
                reset({ ...additionalData, ...changedData });
                idFromExternalResetRef.current = id;
            });
        } else {
            reset(mergedData);
            idFromExternalResetRef.current = id;
        }
    };
    const revert = (unconfirmed?: boolean) => {
        const revertFn = () => {
            dataDispatchAction({
                type: FormFieldDataActionType.RESET,
                payload: revertData,
            });
            setModified(false);
        };
        if (unconfirmed) {
            revertFn();
        } else {
            messageDialogShow(
                t('form.revert.title'),
                t('form.revert.confirm'),
                confirmDialogButtons,
                confirmDialogComponentProps
            ).then((value: any) => {
                value && revertFn();
            });
        }
    };
    const validate = () =>
        new Promise<any>((resolve, reject) => {
            if (resourceType != null) {
                if (resourceTypeCode != null) {
                    setFieldErrors(undefined);
                    apiArtifactFormValidate({
                        type: resourceType,
                        code: resourceTypeCode,
                        data,
                    })
                        .then(resolve)
                        .catch((error: ResourceApiError) => {
                            handleSubmissionErrors(error, t('form.validate.error'), reject);
                        });
                } else {
                    reject("Couldn't send artifact validate request: empty resource type code");
                    console.error();
                }
            } else {
                reject('Form validation only available in form artifacts');
            }
        });
    const save = () =>
        new Promise<any>((resolve, reject) => {
            if (resourceType == null) {
                const calcId = calculatedId(id);
                setFieldErrors(undefined);
                const apiAction =
                    calcId != null ? apiUpdate(calcId, { data }) : apiCreate({ data });
                apiAction
                    .then((savedData: any) => {
                        const message =
                            calcId != null ? t('form.update.success') : t('form.create.success');
                        temporalMessageShow(null, message, 'success');
                        reset(savedData);
                        if (calcId != null) {
                            onUpdateSuccess != null
                                ? onUpdateSuccess(savedData)
                                : onSaveSuccess?.(data);
                            if (updateLink != null || saveLink != null) {
                                const link = (updateLink ?? saveLink)?.replace(
                                    '{{id}}',
                                    '' + savedData.id
                                );
                                link &&
                                    navigate(link, {
                                        replace: true,
                                        relative: 'route',
                                    });
                            }
                        } else {
                            onCreateSuccess != null
                                ? onCreateSuccess(savedData)
                                : onSaveSuccess?.(data);
                            if (createLink || saveLink) {
                                const link = (createLink ?? saveLink)?.replace(
                                    '{{id}}',
                                    '' + savedData.id
                                );
                                if (link?.startsWith('.')) {
                                    link &&
                                        navigate(locationPath + '/' + link, {
                                            replace: true,
                                        });
                                } else if (link?.startsWith('/')) {
                                    link &&
                                        navigate(link.substring(1), {
                                            replace: true,
                                        });
                                } else {
                                    const sli = locationPath?.lastIndexOf('/');
                                    if (sli != -1) {
                                        link &&
                                            navigate(locationPath.substring(0, sli + 1) + link, {
                                                replace: true,
                                            });
                                    } else {
                                        link && navigate(link, { replace: true });
                                    }
                                }
                            }
                        }
                        resolve(savedData);
                    })
                    .catch((error: ResourceApiError) => {
                        const title =
                            calcId != null ? t('form.update.error') : t('form.create.error');
                        handleSubmissionErrors(error, title, reject);
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
            confirmDialogComponentProps
        ).then((value: any) => {
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
    };
    const setFieldValue = (name: string, value: any) => {
        const field = fields?.find((f) => f.name === name);
        dataDispatchAction({
            type: FormFieldDataActionType.FIELD_CHANGE,
            payload: { fieldName: name, field, value },
        });
    };
    const handleFormEnterKeyPressed = saveOnFieldEnterKeyPressed
        ? (e: KeyboardEvent<HTMLInputElement>) => {
              if (e.key === 'Enter') {
                  e.stopPropagation();
                  e.preventDefault();
                  save();
              }
          }
        : undefined;
    React.useEffect(() => {
        if (apiCurrentError) {
            setIsLoading(false);
        }
    }, [apiCurrentError]);
    React.useEffect(() => {
        // Obté els camps pel formulari fent una petició al servidor
        if (apiIsReady) {
            debug &&
                logConsole.debug(
                    'Loading fields' + (resourceType ? ' of type' : ''),
                    resourceType,
                    resourceTypeCode
                );
            setApiActions(apiCurrentActions);
            if (resourceType == null) {
                setFields(apiCurrentFields);
            } else if (resourceTypeCode != null) {
                apiArtifacts({}).then((artifacts: any[]) => {
                    const artifact = artifacts.find(
                        (a: any) =>
                            a.type === resourceType.toUpperCase() && a.code === resourceTypeCode
                    );
                    if (artifact != null) {
                        if (artifact.formClassActive) {
                            setFields(processApiFields(artifact.fields));
                        }
                    } else {
                        console.warn(
                            "Couldn't find artifact (type=" +
                                resourceType +
                                ', code=' +
                                resourceTypeCode +
                                ')'
                        );
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
        if (isReady) {
            setModified(true);
            onDataChange?.(data);
        }
    }, [isReady, data]);
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
        handleSubmissionErrors,
    };
    if (apiRefProp) {
        if (apiRefProp.current) {
            apiRefProp.current.getId = getId;
            apiRefProp.current.getData = getData;
            apiRefProp.current.refresh = refresh;
            apiRefProp.current.reset = externalReset;
            apiRefProp.current.revert = revert;
            apiRefProp.current.validate = validate;
            apiRefProp.current.save = save;
            apiRefProp.current.delete = delette;
            apiRefProp.current.setFieldValue = setFieldValue;
            apiRefProp.current.handleSubmissionErrors = handleSubmissionErrors;
        } else {
            logConsole.warn('apiRef prop must be initialized with an empty object');
        }
    }
    const context = React.useMemo(
        () => ({
            id: calculatedId(id),
            resourceName,
            resourceType,
            resourceTypeCode,
            isLoading,
            isReady,
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
        }),
        [
            isLoading,
            apiActions,
            fields,
            fieldErrors,
            data,
            dataDispatchAction,
            commonFieldComponentProps,
        ]
    );
    return (
        <ResourceApiFormContext.Provider value={context}>
            <div onKeyDown={handleFormEnterKeyPressed}>{isReady ? children : null}</div>
        </ResourceApiFormContext.Provider>
    );
};

export default Form;
