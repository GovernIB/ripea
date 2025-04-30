import React, {MutableRefObject} from "react";
import {useMuiActionReportLogic} from "reactlib";

type FormActionDialogProp = {
    title?: string | ((data:any) => string),
    resourceName: string,
    action: string,
    formDialogComponentProps?: any,
    children: React.ReactElement,
    apiRef?: MutableRefObject<any>,
    formDialogResultProcessor?: (result?: any) => React.ReactElement,
    onSuccess?: (result?: any) => void,
    onError?: (error?: any) => void
}

type FormReportDialogProp = {
    title?: string | ((data:any) => string),
    resourceName: string,
    report: string,
    reportFileType: any,
    formDialogComponentProps?: any,
    children: React.ReactElement,
    apiRef?: MutableRefObject<any>,
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

    const {
        formDialogComponent,
        exec: actionExecutor
    } = useMuiActionReportLogic(
        resourceName,
        action,
        undefined,
        undefined,
        false,
        undefined,
        children,
        formDialogComponentProps,
        formDialogResultProcessor,
        onSuccess,
        onError,
    )

    const exec = (id: any, formAdditionalData?: any) :void => {
        const customTitle = (typeof title === 'function') ?title?.(formAdditionalData) :title;
        actionExecutor(id, customTitle, formAdditionalData)
    }

    if (apiRef != null) {
        apiRef.current = { show: exec };
    }

    return formDialogComponent;
}
export const FormReportDialog = (props:FormReportDialogProp) => {
    const {
        title,
        resourceName,
        report,
        reportFileType,
        formDialogComponentProps,
        children,
        apiRef,
        formDialogResultProcessor,
        onSuccess,
        onError,
    } = props;

    const {
        formDialogComponent,
        exec: actionExecutor
    } = useMuiActionReportLogic(
        resourceName,
        undefined,
        report,
        reportFileType,
        false,
        undefined,
        children,
        formDialogComponentProps,
        formDialogResultProcessor,
        onSuccess,
        onError,
    )

    const exec = (id: any, formAdditionalData?: any) :void => {
        const customTitle = (typeof title === 'function') ?title?.(formAdditionalData) :title;
        actionExecutor(id, customTitle, formAdditionalData)
    }

    if (apiRef != null) {
        apiRef.current = { show: exec };
    }

    return formDialogComponent;
}
export default FormActionDialog;