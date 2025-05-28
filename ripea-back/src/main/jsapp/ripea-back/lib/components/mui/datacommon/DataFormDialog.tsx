import React from 'react';
import { useBaseAppContext, DialogButton } from '../../BaseAppContext';
import { useFormDialog, FormDialogSubmitFn } from '../form/FormDialog';

export type DataFormDialogApi = {
    show: (id?: any, additionalData?: any) => Promise<string>;
    close: () => void;
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
    onClose?: (reason?: string) => boolean;
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
        onClose,
        children
    } = props;
    const { t } = useBaseAppContext();
    const [formDialogShow, formDialogComponent, formDialogClose] = useFormDialog(
        resourceName,
        undefined,
        undefined,
        dialogButtons,
        formSubmit,
        t('datacommon.error'),
        children,
        dialogComponentProps ?? { fullWidth: true, maxWidth: 'md' },
        formComponentProps,
        onClose);
    const show = (id?: any, additionalData?: any) => formDialogShow(id, {
        title: titleProp ?? ((id != null ? t('datacommon.update.title') : t('datacommon.create.title')) + ' ' + (resourceTitle ?? resourceName)),
        additionalData,
    });
    const close = () => formDialogClose();
    if (apiRef != null) {
        apiRef.current = { show, close };
    }
    return <>
        {formDialogComponent}
    </>;
}

export default DataFormDialog;