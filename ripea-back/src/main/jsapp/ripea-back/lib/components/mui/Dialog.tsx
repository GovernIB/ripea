import React from 'react';
import {Dialog as MuiDialog, IconButton, DialogTitle, DialogContent, DialogContentText, Icon} from '@mui/material';
import { DialogButton, ContentDialogShowFn, MessageDialogShowFn } from '../BaseAppContext';
import { useMessageDialogButtons, useConfirmDialogButtons } from '../AppButtons';
import DialogButtons from './DialogButtons';

export type DialogProps = React.PropsWithChildren & {
    open: boolean;
    closeCallback: () => void;
    title?: string | null;
    buttons?: DialogButton[];
    buttonCallback?: (value: any) => void;
    componentProps?: any;
    ref?: React.RefObject<HTMLDivElement | null>;
};

export const useContentDialog: ((
    dialogButtons?: DialogButton[],
    validateFn?: (value?: any) => Promise<boolean>,
    resolveValueFn?: (value?: any) => any) => [ContentDialogShowFn, React.ReactElement, React.RefObject<HTMLDivElement | null>]) = (dialogButtons, validateFn, resolveValueFn) => {
    const defaultDialogButtons = useConfirmDialogButtons();
    const [open, setOpen] = React.useState<boolean>(false);
    const [title, setTitle] = React.useState<string | null>();
    const [content, setContent] = React.useState<React.ReactElement>();
    const [buttons, setButtons] = React.useState<DialogButton[]>(dialogButtons ?? defaultDialogButtons);
    const [dialogProps, setDialogProps] = React.useState<any>();
    const [resolveFn, setResolveFn] = React.useState<(value: any) => void>();
    const [rejectFn, setRejectFn] = React.useState<(value: any) => void>();
    const dialogRef = React.useRef<HTMLDivElement | null>(null);
    const showDialog: ContentDialogShowFn = (
        title: string | null,
        content: React.ReactElement,
        dialogButtons?: DialogButton[],
        dialogProps?: any) => {
        setTitle(title);
        setContent(content);
        dialogButtons && setButtons(dialogButtons);
        setDialogProps(dialogProps);
        setOpen(true);
        return new Promise<string>((resolve, reject) => {
            setResolveFn(() => resolve);
            setRejectFn(() => reject);
        });
    }
    const buttonCallback = (value?: any) => {
        if (value) {
            if (validateFn) {
                validateFn(value).then((valid) => {
                    if (valid) {
                        const resolveValue = resolveValueFn ? resolveValueFn(value) : value;
                        resolveFn?.(resolveValue);
                        setOpen(false);
                    }
                });
            } else {
                const resolveValue = resolveValueFn ? resolveValueFn(value) : value;
                resolveFn?.(resolveValue);
                setOpen(false);
            }
        } else {
            rejectFn?.(value);
            setOpen(false);
        }
    }
    const closeCallback = () => {
        // S'ha tancat la modal amb la 'X' o s'ha fet click a fora de la finestra
        rejectFn?.(undefined);
        setOpen(false);
    }
    const dialogComponent = <Dialog
        open={open}
        buttonCallback={buttonCallback}
        closeCallback={closeCallback}
        title={title}
        buttons={buttons}
        componentProps={dialogProps}
        ref={dialogRef}>{content}</Dialog>;
    return [showDialog, dialogComponent, dialogRef];
}

export const useMessageDialog: (() => [MessageDialogShowFn, React.ReactElement]) = () => {
    const dialogButtons = useMessageDialogButtons();
    const [showContentDialog, dialogComponent] = useContentDialog(dialogButtons);
    const showDialog: MessageDialogShowFn = (
        title: string | null,
        message: string | React.ReactElement,
        dialogButtons?: DialogButton[],
        componentProps?: any) => {
        const content = message ? <DialogContentText>{message}</DialogContentText> : <></>;
        return showContentDialog(title, content, dialogButtons, componentProps);
    }
    return [showDialog, dialogComponent];
}

export const Dialog: React.FC<DialogProps> = (props) => {
    const {
        open,
        buttonCallback,
        closeCallback,
        title,
        buttons,
        componentProps,
        ref,
        children,
    } = props;

    const closeSiNoClickFora = (event: React.MouseEvent, reason?: string) => {
        if (reason === "backdropClick") return; // Evita cerrar por clic en fondo
        closeCallback();
    };

    return <MuiDialog
        open={open}
        onClose={closeSiNoClickFora}
        ref={ref}
        {...componentProps}>
        {<DialogTitle sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", backgroundColor: "#f5f5f5", borderBottom: "1px solid #e3e3e3", padding: "5px 24px" }}>
            <span>{title?title:""}</span>
            <IconButton onClick={() => closeCallback()} aria-label="close">
                <Icon>close</Icon>
            </IconButton>
        </DialogTitle>}
        <DialogContent>{children}</DialogContent>
        {buttons && <DialogButtons
            buttons={buttons}
            handleClose={(value: any) => buttonCallback?.(value)} />}
    </MuiDialog>;
}

export default Dialog;