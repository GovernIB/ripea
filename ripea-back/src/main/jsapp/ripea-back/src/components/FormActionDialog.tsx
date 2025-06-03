import React, {MutableRefObject} from "react";
import {useMuiActionReportLogic} from "reactlib";

type CommonProps = {
    title?: string | ((data:any) => string),
    resourceName: string,
    formDialogComponentProps?: any,
    initialOnChange: boolean,
    children: React.ReactElement,
    apiRef?: MutableRefObject<any>,
    formDialogResultProcessor?: (result?: any) => React.ReactElement,
    onSuccess?: (result?: any) => void,
    onError?: (error?: any) => void
}
type FormActionDialogProp = CommonProps & {
    action: string,
}

type FormReportDialogProp = CommonProps & {
    report: string,
    reportFileType: any,
}

const FormActionDialog = (props:FormActionDialogProp) => {
    const {
        title,
        resourceName,
        action,
        formDialogComponentProps,
        initialOnChange,
        children,
        apiRef,
        formDialogResultProcessor,
        onSuccess,
        onError,
    } = props;

    const {
        formDialogComponent,
        exec: actionExecutor,
        close,
    } = useMuiActionReportLogic(
        resourceName,
        action,
        undefined,
        undefined,
        false,
        undefined,
        initialOnChange,
        children,
        formDialogComponentProps,
        formDialogResultProcessor,
        onSuccess,
        onError,
        (reason?: string) => reason !== 'backdropClick'
    )

    const exec = (id: any, formAdditionalData?: any) :void => {
        const customTitle = (typeof title === 'function') ?title?.(formAdditionalData) :title;
        actionExecutor(id, customTitle, formAdditionalData)
    }

    if (apiRef != null) {
        apiRef.current = { show: exec, close };
    }

    return formDialogComponent;
}
export const FormReportDialog = (props:FormReportDialogProp) => {
    const {
        title,
        resourceName,
        report,
        reportFileType = 'PDF',
        formDialogComponentProps,
        initialOnChange,
        children,
        apiRef,
        formDialogResultProcessor,
        onSuccess,
        onError,
    } = props;

    const {
        formDialogComponent,
        exec: reportExecutor,
        close,
    } = useMuiActionReportLogic(
        resourceName,
        undefined,
        report,
        reportFileType,
        false,
        undefined,
        initialOnChange,
        children,
        formDialogComponentProps,
        formDialogResultProcessor,
        onSuccess,
        onError,
        (reason?: string) => reason !== 'backdropClick'
    )

    const exec = (id: any, formAdditionalData?: any) :void => {
        const customTitle = (typeof title === 'function') ?title?.(formAdditionalData) :title;
        reportExecutor(id, customTitle, formAdditionalData)
    }

    if (apiRef != null) {
        apiRef.current = { show: exec, close };
    }

    return formDialogComponent;
}
export default FormActionDialog;