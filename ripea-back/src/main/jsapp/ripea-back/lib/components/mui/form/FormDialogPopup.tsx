import React from 'react';
import { useBaseAppContext, DialogButton } from '../../BaseAppContext';
import { useFormDialog, FormDialogSubmitFn } from './FormDialog';

export type FormDialogPopupApi = {
    show: (id?: any) => Promise<string>;
};

export type FormDialogPopupProps = React.PropsWithChildren & {
    resourceName: string;
    title?: string;
    resourceTitle?: string;
    dialogButtons?: DialogButton[];
    dialogComponentProps?: any;
    apiRef: React.MutableRefObject<FormDialogPopupApi | undefined>;
    formSubmit?: FormDialogSubmitFn;
    formComponentProps?: any;
};

export const FormDialogPopup: React.FC<FormDialogPopupProps> = (props) => {
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
    const show = (id?: any) => formDialogShow(
        titleProp ?? ((id != null ? t('grid.edit.update') : t('grid.edit.create')) + ' ' + (resourceTitle ?? resourceName)),
        id,
        dialogComponentProps ?? { fullWidth: true, maxWidth: 'md' });
    apiRef.current = { show };
    return <>
        {formDialogComponent}
    </>;
}

export default FormDialogPopup;