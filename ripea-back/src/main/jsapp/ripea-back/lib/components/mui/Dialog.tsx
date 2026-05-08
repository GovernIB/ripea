import React from 'react';
import MuiDialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import { DialogButton, ContentDialogShowFn, MessageDialogShowFn } from '../BaseAppContext';
import { useMessageDialogButtons, useConfirmDialogButtons } from '../AppButtons';
import DialogButtons from './DialogButtons';

export type DialogProps = React.PropsWithChildren & {
    open: boolean;
    closeCallback: (reason: string) => void;
    title?: string | null;
    buttons?: DialogButton[];
    buttonCallback?: (value: any) => void;
    closeIcon?: boolean;
    componentProps?: any;
    ref?: React.RefObject<HTMLDivElement | null>;
};

export const useContentDialog: (
    dialogButtons?: DialogButton[],
    validateFn?: (value?: any) => Promise<boolean>,
    resolveValueFn?: (value?: any) => any,
    closeFn?: (reason?: string) => boolean
) => [ContentDialogShowFn, React.ReactElement, React.RefObject<HTMLDivElement | null>] = (
    dialogButtons,
    validateFn,
    resolveValueFn,
    closeFn
) => {
    const defaultDialogButtons = useConfirmDialogButtons();
    const [open, setOpen] = React.useState<boolean>(false);
    const [title, setTitle] = React.useState<string | null>();
    const [content, setContent] = React.useState<React.ReactElement>();
    const [buttons, setButtons] = React.useState<DialogButton[]>(
        dialogButtons ?? defaultDialogButtons
    );
    const [dialogProps, setDialogProps] = React.useState<any>();
    const [resolveFn, setResolveFn] = React.useState<(value: any) => void>();
    const [rejectFn, setRejectFn] = React.useState<(value: any) => void>();
    const dialogRef = React.useRef<HTMLDivElement | null>(null);
    const showDialog: ContentDialogShowFn = (
        title: string | null,
        content: React.ReactElement,
        dialogButtons?: DialogButton[],
        dialogProps?: any
    ) => {
        setTitle(title);
        setContent(content);
        dialogButtons && setButtons(dialogButtons);
        setDialogProps(dialogProps);
        setOpen(true);
        return new Promise<string>((resolve, reject) => {
            setResolveFn(() => resolve);
            setRejectFn(() => reject);
        });
    };
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
    };
    const closeCallback = (reason: string) => {
        // S'ha tancat la modal amb la 'x' o s'ha fet click a fora de la finestra
        const doClose = closeFn != null ? closeFn(reason) : true;
        if (doClose) {
            rejectFn?.(undefined);
            setOpen(false);
        }
    };
    const dialogComponent = (
        <Dialog
            open={open}
            buttonCallback={buttonCallback}
            closeCallback={closeCallback}
            title={title}
            buttons={buttons}
            componentProps={dialogProps}
            ref={dialogRef}>
            {content}
        </Dialog>
    );
    return [showDialog, dialogComponent, dialogRef];
};

export const useMessageDialog: () => [MessageDialogShowFn, React.ReactElement] = () => {
    const dialogButtons = useMessageDialogButtons();
    const [showContentDialog, dialogComponent] = useContentDialog(dialogButtons);
    const showDialog: MessageDialogShowFn = (
        title: string | null,
        message: string | React.ReactElement,
        dialogButtons?: DialogButton[],
        componentProps?: any
    ) => {
        const content = message ? <DialogContentText>{message}</DialogContentText> : <></>;
        return showContentDialog(title, content, dialogButtons, componentProps);
    };
    return [showDialog, dialogComponent];
};

export const Dialog: React.FC<DialogProps> = (props) => {
    const {
        open,
        buttonCallback,
        closeCallback,
        title,
        buttons,
        closeIcon = true,
        componentProps,
        ref,
        children,
    } = props;
    return (
        <MuiDialog
            open={open}
            onClose={(_event, reason) => closeCallback(reason)}
            ref={ref}
            {...componentProps}>
            {title && <DialogTitle>{title}</DialogTitle>}
            {closeIcon && (
                <IconButton
                    aria-label="close"
                    onClick={() => closeCallback('buttonClick')}
                    size="small"
                    sx={(theme) => ({
                        position: 'absolute',
                        right: 8,
                        top: 8,
                        color: theme.palette.grey[500],
                    })}>
                    <Icon fontSize="small">close</Icon>
                </IconButton>
            )}
            <DialogContent sx={title ? { pt: 0 } : undefined}>{children}</DialogContent>
            {buttons && (
                <DialogButtons
                    buttons={buttons}
                    handleClose={(value: any) => buttonCallback?.(value)}
                />
            )}
        </MuiDialog>
    );
};

export default Dialog;
