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
    apiRef?: React.MutableRefObject<DataFormDialogApi | undefined>;
    formSubmit?: FormDialogSubmitFn;
    formComponentProps?: any;
};

export const DataFormDialog: React.FC<DataFormDialogProps> = (props) => {
    const {
        resourceName,
        title: titleProp,
        resourceTitle,
        dialogButtons,
        dialogComponentProps,
        apiRef,
        formSubmit,
        formComponentProps,
        children
    } = props;
    const { t } = useBaseAppContext();
    const [formDialogShow, formDialogComponent] = useFormDialog(
        resourceName,
        children,
        formComponentProps,
        dialogButtons,
        formSubmit);
    const show = (id?: any, additionalData?: any) => formDialogShow(
        titleProp ?? ((id != null ? t('form.dialog.update') : t('form.dialog.create')) + ' ' + (resourceTitle ?? resourceName)),
        id,
        additionalData,
        dialogComponentProps ?? { fullWidth: true, maxWidth: 'md' });
    if (apiRef != null) {
        apiRef.current = { show };
    }
    return <>
        {formDialogComponent}
    </>;
}

export default DataFormDialog;