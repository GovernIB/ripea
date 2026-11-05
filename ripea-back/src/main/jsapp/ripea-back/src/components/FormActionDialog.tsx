import React, {MutableRefObject, useEffect, useMemo, useRef} from "react";
import { useBaseAppContext, useFormContext, useMuiActionReportLogic, DialogButton } from "reactlib";
import {useTranslation} from "react-i18next";

/**
 * Desa la referència a l'API del formulari del diàleg, que useMuiActionReportLogic (base-react) no exposa. La referència
 * és la del component Form, que es manté encara que el diàleg desmunti el contingut mentre s'executa l'acció.
 */
const FormApiRefCapture = ({formApiRefHolder}: {formApiRefHolder: MutableRefObject<any>}) => {
    const {apiRef} = useFormContext();
    useEffect(() => {
        formApiRefHolder.current = apiRef;
    }, [apiRef, formApiRefHolder]);
    return null;
}

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
	// onSubmit?: () => void
}
type FormActionDialogProp = CommonProps & {
    action: string,
}

type FormReportDialogProp = CommonProps & {
    report: string,
    reportFileType: any,
}

const FormActionDialog = (props:FormActionDialogProp) => {
    const { t } = useTranslation();
    const {temporalMessageShow} = useBaseAppContext();
    const buttons = useMemo(() => [
        {icon: 'save', text: t('common.save'), componentProps: { variant: 'contained' }, value: true },
        {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
    ], [t])
    const {
        title,
        resourceName,
        action,
        formDialogButtons = buttons,
        formDialogComponentProps,
        initialOnChange,
        children,
        apiRef,
        formDialogResultProcessor,
        onSuccess,
        // El títol de l'error (description) sense el prefix "HTTP Error 4xx:" de message
        onError = (error:any) => {
            const missatge = error?.description ?? error?.message;
            missatge && temporalMessageShow(null, missatge, 'error');
        },
        onClose,
		// onSubmit
    } = props;
    const formApiRefHolder = useRef<MutableRefObject<any> | undefined>(undefined);
    // base-react només substitueix els errors dels camps quan l'acció torna errors de validació (422): si després falla
    // per un altre motiu, es mantindrien sota els camps errors que ja no hi són. S'esborren amb un 422 sense errors.
    const onErrorAmbNetejaCamps = (error:any) => {
        if (error?.status !== 422) {
            formApiRefHolder.current?.current?.handleSubmissionErrors?.({status: 422, errors: []} as any);
        }
        onError(error);
    }
    const {
        available: initialized,
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
        undefined,
        initialOnChange,
        <>
            {children}
            <FormApiRefCapture formApiRefHolder={formApiRefHolder}/>
        </>,
        undefined,
        formDialogButtons,
        formDialogComponentProps,
        formDialogResultProcessor,
        onSuccess,
        onErrorAmbNetejaCamps,
        onClose,
        true,
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
        onClose,
    } = props;

    const {
        available: initialized,
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
        undefined,
        initialOnChange,
        children,
        undefined,
        formDialogButtons,
        formDialogComponentProps,
        formDialogResultProcessor,
        onSuccess,
        onError,
        onClose,
        true,
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
