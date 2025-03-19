import React from 'react';
import Button from '@mui/material/Button';
import IconButton from '@mui/material/IconButton';
import Icon from '@mui/material/Icon';
import Badge from '@mui/material/Badge';
import { useFormDialog } from './form/FormDialog';
import { useOptionalDataGridContext } from './datagrid/DataGridContext';
import {
    useActionDialogButtons,
    useReportDialogButtons,
    useConfirmDialogButtons,
} from '../AppButtons';
import { useBaseAppContext } from '../BaseAppContext';
import { useOptionalFormContext } from '../form/FormContext';
import { useResourceApiService } from '../ResourceApiProvider';

import { FormDialogSubmitFn } from './form/FormDialog';

export type ActionReportCustomButton = {
    disabled?: boolean;
    onClick?: () => void;
    title?: string;
    [x: string | number | symbol]: unknown;
};

type IconCustomButtonProps = ActionReportCustomButton & React.PropsWithChildren;
type TextCustomButtonProps = ActionReportCustomButton & React.PropsWithChildren;

type ReportOutputFormatType = 'PDF' | 'XLS' | 'CSV' | 'ODS' | 'XLSX' | 'ODT' | 'RTF' | 'DOCX' | 'PPTX';

export type ActionReportButtonProps = {
    action?: string;
    report?: string;
    reportOutputFormat?: ReportOutputFormatType;
    resourceName?: string;
    id?: any;
    idFromGridRows?: boolean;
    execWithMultipleGridRows?: boolean;
    icon?: any;
    title?: string;
    buttonComponent?: React.FC<ActionReportCustomButton>;
    confirmDisabled?: boolean;
    formAdditionalData?: any;
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
    action?: string,
    report?: string,
    reportOutputFormat?: ReportOutputFormatType,
    resourceName?: string,
    id?: any,
    idFromGridRows?: boolean,
    execWithMultipleGridRows?: boolean,
    confirmDisabled?: boolean,
    formAdditionalData?: any,
    formDialogContent?: React.ReactElement,
    formDialogComponentProps?: any,
    formDialogResultProcessor?: (result?: any) => React.ReactElement,
    onSuccess?: (result?: any) => void,
    onError?: (error?: any) => void) => {
    const { t, messageDialogShow } = useBaseAppContext();
    const {
        resourceName: gridResourceName,
        loading: gridLoading,
        rows: gridRows,
        selection: gridSelection,
    } = useOptionalDataGridContext() ?? {};
    const {
        resourceName: formResourceName,
        id: formId,
    } = useOptionalFormContext() ?? {};
    const realId = id ?? formId;
    const realResourceName = resourceName ?? gridResourceName ?? formResourceName;
    const actionDialogButtons = useActionDialogButtons();
    const reportDialogButtons = useReportDialogButtons();
    const confirmDialogButtons = useConfirmDialogButtons();
    const {
        isReady: apiIsReady,
        artifacts: apiArtifacts,
        action: apiAction,
        report: apiReport,
    } = useResourceApiService(realResourceName);
    const execAction: FormDialogSubmitFn = (data?: any) => new Promise((resolve, reject) => {
        if (action != null) {
            const requestArgs = {
                code: action,
                data
            };
            apiAction(realId, requestArgs).then((result: any) => {
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
    const generateReport: FormDialogSubmitFn = (data?: any) => new Promise((resolve, reject) => {
        if (report != null) {
            const requestArgs = {
                code: report,
                urlData: {
                    outputFormat: reportOutputFormat
                },
                data
            };
            apiReport(realId, requestArgs).then((result) => {
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
    const handleButtonClick = () => {
        if (hasForm) {
            const formDialogTitle = apiLink?.title ?? (action != null ? 'Exec ' + action : 'Generate ' + report);
            formDialogShow(
                formDialogTitle,
                id,
                formAdditionalData,
                formDialogComponentProps ?? { fullWidth: true, maxWidth: 'md' });
        } else if (action != null) {
            if (!confirmDisabled) {
                const confirmDialogComponentProps = { maxWidth: 'sm', fullWidth: true };
                messageDialogShow(
                    t('actionReport.confirm.title'),
                    t('actionReport.confirm.message', { action: apiLink?.title ?? action }),
                    confirmDialogButtons,
                    confirmDialogComponentProps).
                    then((value: any) => {
                        if (value) {
                            execAction();
                        }
                    });
            } else {
                execAction();
            }
        } else if (report != null) {
            generateReport();
        }
    }
    const [formDialogShow, formDialogComponent] = useFormDialog(
        realResourceName ?? '', // TODO Hem hagut d'afegir el '' per a evitar el warning
        formDialogContent,
        { resourceType: action ? 'action' : 'report', resourceTypeCode: action ?? report },
        action ? actionDialogButtons : (report ? reportDialogButtons : undefined),
        action ? execAction : generateReport);
    const [artifact, setArtifact] = React.useState<any>();
    const [apiLink, setApiLink] = React.useState<any>();
    const initialized = artifact != null;
    const hasForm = artifact != null && artifact.formClassActive;
    const selectedCount = execWithMultipleGridRows ? (gridSelection ? gridSelection.length : null) : null;
    const disabled = idFromGridRows ? ((selectedCount ?? 0) === 0) : false;
    React.useEffect(() => {
        if (realResourceName == null) {
            console.error('[ActionReportButton] No resourceName in parent context and empty resourceName prop');
        }
        if (action == null && report == null) {
            console.error('[ActionReportButton] No action or report prop specified');
        }
    }, []);
    React.useEffect(() => {
        if (realResourceName != null && (action != null || report != null) && apiIsReady) {
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
    }, [apiIsReady, realId, gridLoading, gridRows]);
    return {
        initialized,
        disabled,
        apiLink,
        selectedCount,
        formDialogComponent,
        handleButtonClick
    }
}

export const ActionReportButton: React.FC<ActionReportButtonProps> = (props) => {
    const {
        action,
        report,
        reportOutputFormat,
        resourceName,
        id,
        idFromGridRows,
        execWithMultipleGridRows,
        icon,
        title,
        buttonComponent: buttonComponentProp,
        confirmDisabled,
        formAdditionalData,
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
        disabled,
        apiLink,
        selectedCount,
        formDialogComponent,
        handleButtonClick,
    } = useActionReportLogic(
        action,
        report,
        reportOutputFormat,
        resourceName,
        id,
        idFromGridRows,
        execWithMultipleGridRows,
        confirmDisabled,
        formAdditionalData,
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
            handleButtonClick();
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