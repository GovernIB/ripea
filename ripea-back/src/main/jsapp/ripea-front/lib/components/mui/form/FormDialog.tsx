import React from 'react';
import { DialogButton } from '../../BaseAppContext';
import { useFormDialogButtons } from '../../AppButtons';
import Dialog, { DialogProps } from '../Dialog';
import Form from './MuiForm';
import { FormApi } from '../../form/FormContext';

type FormDialogProps = DialogProps & {
    resourceName: string;
    id?: any;
    apiRef?: React.MutableRefObject<FormApi>;
    formComponentProps?: any;
    noForm?: boolean;
};

export type FormDialogSubmitFn = (data?: any) => Promise<React.ReactElement | undefined>;
export type FormDialogShowFn = (title: string | null, id: any, componentProps?: any) => Promise<string>;
export type UseFormDialogFn = (
    resourceName: string,
    formContent: React.ReactNode,
    formComponentProps?: any,
    dialogButtons?: DialogButton[],
    submit?: FormDialogSubmitFn) => [FormDialogShowFn, React.ReactElement];

export const useFormDialog: UseFormDialogFn = (
    resourceName: string,
    formContent: React.ReactNode,
    formComponentProps?: any,
    dialogButtons?: DialogButton[],
    submit?: FormDialogSubmitFn) => {
    const formApiRef = React.useRef<FormApi | any>({});
    const formDialogButtons = useFormDialogButtons();
    const [open, setOpen] = React.useState<boolean>(false);
    const [title, setTitle] = React.useState<string | null>();
    const [id, setId] = React.useState<any>();
    const [componentProps, setComponentProps] = React.useState<any>();
    const [resolveFn, setResolveFn] = React.useState<(value?: any) => void>();
    const [rejectFn, setRejectFn] = React.useState<(value: any) => void>();
    const [submitReturnedContent, setSubmitReturnedContent] = React.useState<React.ReactNode | undefined>();
    const dialogShow = (title: string | null, id: any, componentProps?: any) => {
        setTitle(title);
        setId(id);
        setComponentProps(componentProps);
        setOpen(true);
        setSubmitReturnedContent(undefined);
        return new Promise<string>((resolve, reject) => {
            setResolveFn(() => resolve);
            setRejectFn(() => reject);
        });
    }
    const buttonCallback = (value: any) => {
        if (value) {
            const isCustomSubmit = submit != null;
            const result = isCustomSubmit ? submit(formApiRef.current.getData()) : formApiRef.current.save();
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
            }).catch(() => {
                // S'ha fet click al botó desar i s'han produit errors
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
        id={id}
        apiRef={formApiRef}
        open={open}
        buttonCallback={buttonCallback}
        closeCallback={closeCallback}
        title={title}
        buttons={dialogButtons ?? formDialogButtons}
        componentProps={componentProps}
        formComponentProps={formComponentProps}
        noForm={submitReturnedContent != null}>
        {submitReturnedContent ?? formContent}
    </FormDialog>;
    return [dialogShow, dialogComponent];
}

export const FormDialog: React.FC<FormDialogProps> = (props) => {
    const {
        resourceName,
        id,
        apiRef,
        formComponentProps,
        noForm,
        children,
        ...otherProps
    } = props;
    return <Dialog {...otherProps}>
        {noForm ? children : <Form
            {...formComponentProps}
            resourceName={resourceName}
            id={id}
            apiRef={apiRef}
            hiddenToolbar>
            {children}
        </Form>}
    </Dialog>;
}

export default FormDialog;