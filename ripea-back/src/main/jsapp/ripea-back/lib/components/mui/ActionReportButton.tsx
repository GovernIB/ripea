import React from 'react';
import Button from '@mui/material/Button';
import IconButton from '@mui/material/IconButton';
import Icon from '@mui/material/Icon';
import Badge from '@mui/material/Badge';
import { useFormDialog, FormDialogSubmitFn } from './form/FormDialog';
import {
    useActionDialogButtons,
    useReportDialogButtons,
    useConfirmDialogButtons,
} from '../AppButtons';
import { useBaseAppContext } from '../BaseAppContext';
import { ExportFileType } from '../ResourceApiContext';
import { useResourceApiService } from '../ResourceApiProvider';

export type ActionReportCustomButton = {
    disabled?: boolean;
    onClick?: () => void;
    title?: string;
    [x: string | number | symbol]: unknown;
};

type IconCustomButtonProps = ActionReportCustomButton & React.PropsWithChildren;
type TextCustomButtonProps = ActionReportCustomButton & React.PropsWithChildren;

export type ActionReportButtonProps = {
    resourceName: string;
    action?: string;
    report?: string;
    reportFileType?: ExportFileType;
    id?: any;
    icon?: any;
    title?: string;
    confirm?: boolean;
    disabled?: boolean;
    selectedCount?: number;
    buttonComponent?: React.FC<ActionReportCustomButton>;
    formAdditionalData?: any;
    formInitOnChangeRequest?: boolean;
    formDialogContent?: React.ReactElement;
    formDialogComponentProps?: any;
    formDialogResultProcessor?: (result?: any) => React.ReactElement;
    onSuccess?: (result?: any) => void;
    onError?: (error?: any) => void;
    buttonComponentProps?: any;
    iconComponentProps?: any;
};

const getActionReportLink = (action: string | undefined, report: string | undefined) => {
    if (action != null) {
        return 'exec_' + action;
    } else if (report != null) {
        return 'generate_' + report;
    }
}

const ButtonWithBadge: React.FC<any & React.PropsWithChildren> = (props) => {
    const { selectedCount, children } = props;
    return <Badge
        badgeContent={selectedCount}
        color="secondary">
        {children}
    </Badge>;
}

const IconCustomButton: React.FC<IconCustomButtonProps> = (props) => {
    const {
        disabled,
        onClick,
        title,
        children,
        ...otherProps
    } = props;
    return <IconButton disabled={disabled} onClick={onClick} title={title} {...otherProps}>{children}</IconButton>;
}

const TextCustomButton: React.FC<TextCustomButtonProps> = (props) => {
    const {
        disabled,
        onClick,
        title,
        ...otherProps
    } = props;
    return <Button disabled={disabled} onClick={onClick} {...otherProps}>{title}</Button>;
}

export const useActionReportLogic = (
    resourceName: string,
    action?: string,
    report?: string,
    reportFileType?: ExportFileType,
    confirm?: boolean,
    formAdditionalDataArg?: any,
    formInitOnChangeRequest?: boolean,
    formDialogContent?: React.ReactElement,
    formDialogComponentPropsArg?: any,
    formDialogResultProcessor?: (result?: any) => React.ReactElement,
    onSuccess?: (result?: any) => void,
    onError?: (error?: any) => void) => {
    const { t, messageDialogShow, saveAs } = useBaseAppContext();
    const actionDialogButtons = useActionDialogButtons();
    const reportDialogButtons = useReportDialogButtons();
    const confirmDialogButtons = useConfirmDialogButtons();
    const {
        isReady: apiIsReady,
        artifacts: apiArtifacts,
        artifactAction: apiArtifactAction,
        artifactReport: apiArtifactReport,
    } = useResourceApiService(resourceName);
    const execAction: FormDialogSubmitFn = (id: any, data?: any) => new Promise((resolve, reject) => {
        if (action != null) {
            const requestArgs = { code: action, data };
            apiArtifactAction(id, requestArgs).then((result: any) => {
                onSuccess?.(result);
                resolve(formDialogResultProcessor?.(result));
            }).catch(error => {
                onError?.(error);
                reject(error);
            });
        } else {
            console.error('Couldn\'t exec action without code');
        }
    });
    const generateReport: FormDialogSubmitFn = (id: any, data?: any) => new Promise((resolve, reject) => {
        if (report != null) {
            const requestArgs = { code: report, fileType: reportFileType, data };
            apiArtifactReport(id, requestArgs).then((result: any) => {
                saveAs?.(result.blob, result.fileName);
                onSuccess?.(result);
                resolve(formDialogResultProcessor?.(result));
            }).catch(error => {
                onError?.(error);
                reject(error);
            });
        } else {
            console.error('Couldn\'t generate report without code');
        }
    });
    const dialogDisabled = formDialogContent == null;
    const [formDialogShow, formDialogComponent] = useFormDialog(
        resourceName,
        action ? 'ACTION' : (report ? 'REPORT' : undefined),
        action ? action : (report ? report : undefined),
        action ? actionDialogButtons : (report ? reportDialogButtons : undefined),
        action ? execAction : generateReport,
        formDialogContent,
        { resourceType: action ? 'action' : 'report', resourceTypeCode: action ?? report });
    const exec = (id: any, dialogTitle?: any, formAdditionalData?: any, formDialogComponentProps?: any) => {
        if (hasForm && !dialogDisabled) {
            const formDialogTitle = apiLink?.title ?? (action != null ? 'Exec ' + action : 'Generate ' + report);
            formDialogShow(id, {
                title: dialogTitle ?? formDialogTitle,
                additionalData: formAdditionalData ?? formAdditionalDataArg,
                initOnChangeRequest: formInitOnChangeRequest,
                dialogComponentProps: formDialogComponentProps ?? formDialogComponentPropsArg ?? { fullWidth: true, maxWidth: 'md' }
            }).catch(_error => {});
        } else if (action != null) {
            if (confirm) {
                const confirmDialogComponentProps = { maxWidth: 'sm', fullWidth: true };
                messageDialogShow(
                    t('actionReport.confirm.title'),
                    t('actionReport.confirm.message', { action: apiLink?.title ?? action }),
                    confirmDialogButtons,
                    confirmDialogComponentProps).
                    then((value: any) => {
                        if (value) {
                            execAction(id, formAdditionalDataArg);
                        }
                    });
            } else {
                execAction(id, formAdditionalDataArg);
            }
        } else if (report != null) {
            generateReport(null, formAdditionalDataArg);
        }
    }
    const [artifact, setArtifact] = React.useState<any>();
    const [apiLink, setApiLink] = React.useState<any>();
    const initialized = artifact != null;
    const hasForm = artifact != null && artifact.formClassActive;
    React.useEffect(() => {
        if (action == null && report == null) {
            console.error('[ActionReportButton] No action or report prop specified');
        }
    }, []);
    React.useEffect(() => {
        if (resourceName != null && (action != null || report != null) && apiIsReady) {
            apiArtifacts({ includeLinks: true }).then(artifacts => {
                const artifactType = action != null ? 'ACTION' : 'REPORT';
                const artifactCode = action ?? report;
                const artifact = artifacts.find((a: any) => a.type === artifactType && a.code === artifactCode);
                if (artifact != null) {
                    setArtifact(artifact);
                    const actionReportLink = getActionReportLink(action, report);
                    actionReportLink != null && setApiLink((artifact as any)._links[actionReportLink])
                } else {
                    console.warn('Couldn\'t find artifact (type=' + artifactType + ', code=' + artifactCode + ')');
                }
            });
        } else {
            setApiLink(undefined);
        }
    }, [apiIsReady]);
    return {
        initialized,
        apiLink,
        formDialogComponent,
        exec
    }
}

export const ActionReportButton: React.FC<ActionReportButtonProps> = (props) => {
    const {
        resourceName,
        action,
        report,
        reportFileType = 'PDF',
        id,
        icon,
        title,
        confirm,
        disabled,
        selectedCount,
        buttonComponent: buttonComponentProp,
        formAdditionalData,
        formInitOnChangeRequest,
        formDialogContent,
        formDialogComponentProps,
        formDialogResultProcessor,
        onSuccess,
        onError,
        buttonComponentProps,
        iconComponentProps,
    } = props;
    const {
        initialized,
        apiLink,
        formDialogComponent,
        exec: handleButtonClick,
    } = useActionReportLogic(
        resourceName,
        action,
        report,
        reportFileType,
        confirm,
        formAdditionalData,
        formInitOnChangeRequest,
        formDialogContent,
        formDialogComponentProps,
        formDialogResultProcessor,
        onSuccess,
        onError);
    const buttonTitle = title ?? apiLink?.title ?? action ?? report;
    const ButtonComponent = buttonComponentProp ?? (icon != null ? IconCustomButton : TextCustomButton);
    const {
        onClick: onClickFromComponentProps,
        ...otherButtonComponentProps
    } = buttonComponentProps ?? {};
    const button = <ButtonComponent
        disabled={disabled}
        onClick={() => {
            handleButtonClick(id);
            onClickFromComponentProps?.();
        }}
        title={buttonTitle}
        {...otherButtonComponentProps}>
        {icon != null && <Icon {...iconComponentProps}>{icon}</Icon>}
    </ButtonComponent>;
    return initialized ? <>
        {selectedCount ? <ButtonWithBadge selectedCount={selectedCount}>{button}</ButtonWithBadge> : button}
        {formDialogComponent}
    </> : null;
}

export default ActionReportButton;