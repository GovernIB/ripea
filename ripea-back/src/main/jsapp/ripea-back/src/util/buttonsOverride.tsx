import { useTranslation } from "react-i18next";
import { DialogButton } from "reactlib";

/**
 * S'han creat els botons (duplicats de base-react, concretament de AppButtons.tsx) perque els originals no es poden modificar les seves traduccions.
 * Per aquest motiu es crea aquest arxiu duplicat amb traduccions diferents i s'han modificat els imports que anaven a AppButtons.tsx pels botons d'aquí.
 */

export const useMessageDialogButtons = (): DialogButton[] => {
    const { t } = useTranslation();
    return [
        {
            value: true,
            text: t("buttons.confirm.accept"),
            icon: "check",
            componentProps: { variant: "contained" },
        },
    ];
};

export const useConfirmDialogButtons = (): DialogButton[] => {
    const { t } = useTranslation();
    return [
        {
            value: false,
            text: t("buttons.confirm.cancel"),
            componentProps: { variant: "outlined" },
        },
        {
            value: true,
            text: t("buttons.confirm.accept"),
            icon: "check",
            componentProps: { variant: "contained" },
        },
    ];
};

export const useCloseDialogButtons: () => DialogButton[] = () => {
    const { t } = useTranslation();
    return [
        {
            value: false,
            text: t("buttons.misc.close"),
            componentProps: { variant: "contained" },
        },
    ];
};

export const useFormDialogButtons: () => DialogButton[] = () => {
    const { t } = useTranslation();
    return [
        {
            value: false,
            text: t("buttons.form.cancel"),
            componentProps: { variant: "outlined" },
        },
        {
            value: true,
            text: t("buttons.form.save"),
            icon: "save",
            componentProps: { variant: "contained" },
        },
    ];
};

export const useActionDialogButtons: () => DialogButton[] = () => {
    const { t } = useTranslation();
    return [
        {
            value: false,
            text: t("buttons.action.cancel"),
            componentProps: { variant: "outlined" },
        },
        {
            value: true,
            text: t("buttons.action.exec"),
            icon: "bolt",
            componentProps: { variant: "contained" },
        },
    ];
};

export const useReportDialogButtons: () => DialogButton[] = () => {
    const { t } = useTranslation();
    return [
        {
            value: false,
            text: t("buttons.report.cancel"),
            componentProps: { variant: "outlined" },
        },
        {
            value: true,
            text: t("buttons.report.generate"),
            icon: "summarize",
            componentProps: { variant: "contained" },
        },
    ];
};
