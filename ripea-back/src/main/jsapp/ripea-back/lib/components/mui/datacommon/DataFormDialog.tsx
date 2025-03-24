import React from 'react';
import { useBaseAppContext, DialogButton } from '../../BaseAppContext';
import { useFormDialog, FormDialogSubmitFn } from '../form/FormDialog';

export type DataFormDialogApi = {
    show: (id?: any, additionalData?: any) => Promise<string>;
};

export type DataFormDialogProps = React.PropsWithChildren & {
    resourceName: string;
    title?: string;
    resourceTitle?: string;
    dialogButtons?: DialogButton[];
    dialogComponentProps?: any;
    formComponentProps?: any;
    apiRef?: React.MutableRefObject<DataFormDialogApi | undefined>;
    formSubmit?: FormDialogSubmitFn;
};

export const DataFormDialog: React.FC<DataFormDialogProps> = (props) => {
    const {
        resourceName,
        title: titleProp,
        resourceTitle,
        dialogButtons,
        dialogComponentProps,
        formComponentProps,
        apiRef,
        formSubmit,
        children
    } = props;
    const { t } = useBaseAppContext();
    const [formDialogShow, formDialogComponent] = useFormDialog(
        resourceName,
        dialogButtons,
        formSubmit,
        children,
        formComponentProps);
    const show = (id?: any, additionalData?: any) => formDialogShow(id, {
        title: titleProp ?? ((id != null ? t('form.dialog.update') : t('form.dialog.create')) + ' ' + (resourceTitle ?? resourceName)),
        additionalData,
        dialogComponentProps: dialogComponentProps ?? { fullWidth: true, maxWidth: 'md' }
    });
    if (apiRef != null) {
        apiRef.current = { show };
    }
    return <>
        {formDialogComponent}
    </>;
}

export default DataFormDialog;