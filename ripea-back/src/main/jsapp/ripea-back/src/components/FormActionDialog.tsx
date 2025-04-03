import React, {MutableRefObject} from "react";
import {
    MuiFormDialogApi,
    useActionDialogButtons,
    useResourceApiService
} from "reactlib";
import {useFormDialog} from "../../lib/components/mui/form/FormDialog.tsx";

type FormActionDialogProp = {
    title?: string | ((data:any) => string),
    resourceName: string,
    action: string,
    formDialogComponentProps?: any,
    children: React.ReactElement,
    apiRef?: MutableRefObject<MuiFormDialogApi | undefined>,
    formDialogResultProcessor?: (result?: any) => React.ReactElement,
    onSuccess?: (result?: any) => void,
    onError?: (error?: any) => void
}

const FormActionDialog = (props:FormActionDialogProp) => {
    const {
        title,
        resourceName,
        action,
        formDialogComponentProps,
        children,
        apiRef,
        formDialogResultProcessor,
        onSuccess,
        onError,
    } = props;

    const actionDialogButtons = useActionDialogButtons();
    const {
        isReady: apiIsReady,
        artifacts: apiArtifacts,
        artifactAction: apiAction,
    } = useResourceApiService(resourceName);
    const execAction = (data: any) :Promise<any> => new Promise<any>( (resolve, reject) => {
        const requestArgs = { code: action, data };
        apiAction(data?.id, requestArgs)
            .then((result) => {
                onSuccess?.(result);
                resolve(formDialogResultProcessor?.(result));
            }).catch(error => {
                onError?.(error);
                reject(error);
            });
    });
    const exec = (id: any, formAdditionalData?: any) :Promise<any> => {
        const customTitle = (typeof title === 'function') ?title?.(formAdditionalData) :title;
        const formDialogTitle = apiLink?.title ?? ('Exec ' + action);
        return formDialogShow(id, {
            title: customTitle ?? formDialogTitle,
            additionalData: formAdditionalData,
            dialogComponentProps: formDialogComponentProps ?? { fullWidth: true, maxWidth: 'md' }
        });
    }

    const [formDialogShow, formDialogComponent] = useFormDialog(
        resourceName,
        actionDialogButtons,
        execAction,
        children,
        { resourceType: 'action', resourceTypeCode: action });
    const [apiLink, setApiLink] = React.useState<any>();

    React.useEffect(() => {
        if (apiIsReady) {
            apiArtifacts({ includeLinks: true }).then(artifacts => {
                const artifactType = 'ACTION';
                const artifact = artifacts.find((a: any) => a.type === artifactType && a.code === action);
                if (artifact != null) {
                    const actionReportLink = 'exec_' + action;
                    actionReportLink != null && setApiLink((artifact as any)._links[actionReportLink])
                } else {
                    console.warn('Couldn\'t find artifact (type=' + artifactType + ', code=' + action + ')');
                }
            });
        } else {
            setApiLink(undefined);
        }
    }, [apiIsReady]);

    if (apiRef != null) {
        apiRef.current = { show: exec };
    }

    return formDialogComponent;
}
export default FormActionDialog;