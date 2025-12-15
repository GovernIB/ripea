import React, {MutableRefObject} from "react";
import { useBaseAppContext, useMuiActionReportLogic, DialogButton } from "reactlib";

type CommonProps = {
    title?: string | ((data:any) => string),
    resourceName: string,
    formDialogButtons?: DialogButton[],
    formDialogComponentProps?: any,
    initialOnChange: boolean,
    children: React.ReactElement,
    apiRef?: MutableRefObject<any>,
    formDialogResultProcessor?: (result?: any) => React.ReactElement,
    onSuccess?: (result?: any) => void,
    onError?: (error?: any) => void,
    onClose?: (error?: any) => void,
	onSubmit?: () => void
}
type FormActionDialogProp = CommonProps & {
    action: string,
}

type FormReportDialogProp = CommonProps & {
    report: string,
    reportFileType: any,
}

const FormActionDialog = (props:FormActionDialogProp) => {
    const {temporalMessageShow} = useBaseAppContext();
    const {
        title,
        resourceName,
        action,
        formDialogButtons,
        formDialogComponentProps,
        initialOnChange,
        children,
        apiRef,
        formDialogResultProcessor,
        onSuccess,
        onError = (error:any) => error?.message && temporalMessageShow(null, error?.message, 'error'),
        onClose,
		onSubmit
    } = props;
    const {
        initialized,
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
        undefined,
        initialOnChange,
        children,
        formDialogButtons,
        formDialogComponentProps,
        formDialogResultProcessor,
        onSuccess,
        onError,
        onClose,
		onSubmit,
        (reason?: string) => reason !== 'backdropClick'
    )

    const exec = (id: any, formAdditionalData?: any) :void => {
        if(initialized) {
            const customTitle = (typeof title === 'function') ? title?.(formAdditionalData) : title;
            actionExecutor(id, customTitle, formAdditionalData)
        }
    }

    if (apiRef != null) {
        apiRef.current = { show: exec, close };
    }

    return formDialogComponent;
}
export const FormReportDialog = (props:FormReportDialogProp) => {
    const {temporalMessageShow} = useBaseAppContext();
    const {
        title,
        resourceName,
        report,
        reportFileType = 'PDF',
        formDialogButtons,
        formDialogComponentProps,
        initialOnChange,
        children,
        apiRef,
        formDialogResultProcessor,
        onSuccess,
        onError = (error:any) => error?.message && temporalMessageShow(null, error?.message, 'error'),
    } = props;

    const {
        initialized,
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
        undefined,
        initialOnChange,
        children,
        formDialogButtons,
        formDialogComponentProps,
        formDialogResultProcessor,
        onSuccess,
        onError,
        (reason?: string) => reason !== 'backdropClick'
    )

    const exec = (id: any, formAdditionalData?: any) :void => {
        if(initialized) {
            const customTitle = (typeof title === 'function') ? title?.(formAdditionalData) : title;
            reportExecutor(id, customTitle, formAdditionalData)
        }
    }

    if (apiRef != null) {
        apiRef.current = { show: exec, close };
    }

    return formDialogComponent;
}
export default FormActionDialog;