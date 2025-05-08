import React from 'react';
import { DialogButton } from '../../BaseAppContext';
import { ResourceType } from '../../ResourceApiContext';
import { useFormDialogButtons } from '../../AppButtons';
import { FormApi } from '../../form/FormContext';
import Dialog, { DialogProps } from '../Dialog';
import MuiForm from './MuiForm';

type FormDialogProps = DialogProps & {
    resourceName: string;
    resourceType?: ResourceType;
    resourceTypeCode?: string;
    id?: any;
    additionalData?: any;
    initOnChangeRequest?: boolean;
    apiRef?: React.RefObject<FormApi>;
    dialogComponentProps?: any;
    formComponentProps?: any;
    noForm?: boolean;
};

export type FormDialogSubmitFn = (id: any, data?: any) => Promise<React.ReactElement | undefined>;
export type FormDialogShowArgs = {
    title?: string;
    additionalData?: any;
    initOnChangeRequest?: boolean;
    formContent?: React.ReactNode;
    dialogComponentProps?: any;
    formComponentProps?: any;
};
export type FormDialogShowFn = (id: any, args?: FormDialogShowArgs) => Promise<any>;
export type UseFormDialogFn = (
    resourceName: string,
    resourceType?: ResourceType,
    resourceTypeCode?: string,
    dialogButtons?: DialogButton[],
    customSubmit?: FormDialogSubmitFn,
    customSubmitErrorMessage?: string,
    defaultFormContent?: React.ReactNode,
    defaultDialogComponentProps?: any,
    defaultFormComponentProps?: any) => [FormDialogShowFn, React.ReactElement];

export const useFormDialog: UseFormDialogFn = (
    resourceName: string,
    resourceType?: ResourceType,
    resourceTypeCode?: string,
    dialogButtons?: DialogButton[],
    customSubmit?: FormDialogSubmitFn,
    customSubmitErrorMessage?: string,
    defaultFormContent?: React.ReactNode,
    defaultDialogComponentProps?: any,
    defaultFormComponentProps?: any) => {
    const formApiRef = React.useRef<FormApi | any>({});
    const formDialogButtons = useFormDialogButtons();
    const [open, setOpen] = React.useState<boolean>(false);
    const [title, setTitle] = React.useState<string | null>();
    const [id, setId] = React.useState<any>();
    const [additionalData, setAdditionalData] = React.useState<any>();
    const [initOnChangeRequest, setInitOnChangeRequest] = React.useState<boolean>();
    const [dialogComponentProps, setDialogComponentProps] = React.useState<any>(defaultDialogComponentProps);
    const [formComponentProps, setFormComponentProps] = React.useState<any>(defaultFormComponentProps);
    const [resolveFn, setResolveFn] = React.useState<(value?: any) => void>();
    const [rejectFn, setRejectFn] = React.useState<(value: any) => void>();
    const [formContent, setFormContent] = React.useState<React.ReactNode | undefined>(defaultFormContent);
    const [submitReturnedContent, setSubmitReturnedContent] = React.useState<React.ReactNode | undefined>();
    const show = (id: any, args?: FormDialogShowArgs) => {
        setId(id);
        setTitle(args?.title);
        args?.formContent != null && setFormContent(args.formContent);
        args?.additionalData != null && setAdditionalData(args.additionalData);
        args?.initOnChangeRequest != null && setInitOnChangeRequest(args.initOnChangeRequest);
        args?.dialogComponentProps != null && setDialogComponentProps(args.dialogComponentProps);
        args?.formComponentProps != null && setFormComponentProps(args.formComponentProps);
        setOpen(true);
        setSubmitReturnedContent(undefined);
        return new Promise<any>((resolve, reject) => {
            setResolveFn(() => resolve);
            setRejectFn(() => reject);
        });
    }
    const buttonCallback = (value: any) => {
        if (value) {
            const isCustomSubmit = customSubmit != null;
            const result = isCustomSubmit ? customSubmit(formApiRef.current.getId(), formApiRef.current.getData()) : formApiRef.current.save();
            result.then((value: any) => {
                if (isCustomSubmit) {
                    // S'ha fet click al botó executar/generar i s'ha executat/generat correctament
                    if (value != null) {
                        // Si el mètode submit ha retornat alguna cosa es mostra com a contingut del diàleg
                        setSubmitReturnedContent(value);
                    } else {
                        // Si el mètode submit no retorna res es tanca el diàleg
                        setOpen(false);
                        resolveFn?.(value);
                    }
                } else {
                    // S'ha fet click al botó desar i s'ha desat correctament
                    setOpen(false);
                    resolveFn?.(value);
                }
            }).catch((error: any) => {
                // S'ha fet click al botó desar i s'han produit errors
                if (isCustomSubmit) {
                    formApiRef.current.handleSubmissionErrors(error, customSubmitErrorMessage);
                }
            });
        } else {
            // S'ha fet clic al botó de cancel·lar
            rejectFn?.(undefined);
            setOpen(false);
        }
    }
    const closeCallback = () => {
        // S'ha tancat la modal amb la 'X' o s'ha fet click a fora de la finestra
        rejectFn?.(undefined);
        setOpen(false);
    }
    const dialogComponent = <FormDialog
        resourceName={resourceName}
        resourceType={resourceType}
        resourceTypeCode={resourceTypeCode}
        id={id}
        additionalData={additionalData}
        initOnChangeRequest={initOnChangeRequest}
        apiRef={formApiRef}
        open={open}
        buttonCallback={buttonCallback}
        closeCallback={closeCallback}
        title={title}
        buttons={dialogButtons ?? formDialogButtons}
        dialogComponentProps={dialogComponentProps}
        formComponentProps={formComponentProps}
        noForm={submitReturnedContent != null}>
        {submitReturnedContent ?? formContent}
    </FormDialog>;
    return [show, dialogComponent];
}

export const FormDialog: React.FC<FormDialogProps> = (props) => {
    const {
        resourceName,
        resourceType,
        resourceTypeCode,
        id,
        additionalData,
        initOnChangeRequest,
        apiRef,
        dialogComponentProps,
        formComponentProps,
        noForm,
        children,
        ...otherProps
    } = props;
    return <Dialog componentProps={dialogComponentProps} {...otherProps}>
        {noForm ? children : <MuiForm
            {...formComponentProps}
            resourceName={resourceName}
            resourceType={resourceType}
            resourceTypeCode={resourceTypeCode}
            id={id}
            additionalData={additionalData}
            initOnChangeRequest={initOnChangeRequest}
            apiRef={apiRef}
            hiddenToolbar>
            {children}
        </MuiForm>}
    </Dialog>;
}

export default FormDialog;