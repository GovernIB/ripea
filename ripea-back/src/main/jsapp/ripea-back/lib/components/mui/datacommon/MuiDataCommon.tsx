import React from 'react';
import {
    useResourceApiService,
    ResourceApiFindCommonArgs,
    ResourceApiError,
} from '../../ResourceApiProvider';
import { ResourceType, ExportFileType } from '../../ResourceApiContext';
import { useDataQuickFilter } from './DataQuickFilter';
import { toToolbarIcon } from '../ToolbarIcon';
import DataFormDialog, { DataFormDialogApi } from './DataFormDialog';
import { useBaseAppContext } from '../../BaseAppContext';
import { useConfirmDialogButtons } from '../../AppButtons';

export type DataCommonFindArgs = ResourceApiFindCommonArgs;

export type DataCommonAdditionalAction = {
    title?: string;
    icon?: string;
    showInMenu?: ((row: any) => boolean) | boolean;
    disabled?: ((row: any) => boolean) | boolean;
    hidden?: ((row: any) => boolean) | boolean;
    linkTo?: ((row: any) => string) | string;
    linkState?: ((row: any) => any) | any;
    rowLink?: string;
    action?: string;
    report?: string;
    clickShowCreateDialog?: boolean;
    clickShowUpdateDialog?: boolean;
    onClick?: (id: any, row: any, event: React.MouseEvent) => void;
};

export type DataCommonExportFn = (fields?: string[], fileType?: ExportFileType, forceUnpaged?: boolean) => void;
export type DataCommonShowCreateDialogFn = (row?: any) => void;
export type DataCommonShowUpdateDialogFn = (id: any, row?: any) => void;

export const useApiDataCommon = (
    resourceName: string,
    resourceType?: ResourceType,
    resourceTypeCode?: string,
    resourceFieldName?: string,
    findDisabled?: boolean,
    findArgs?: DataCommonFindArgs,
    quickFilterInitialValue?: string,
    quickFilterProps?: any,
    getArtifacts?: boolean) => {
    const { saveAs } = useBaseAppContext();
    const {
        isReady: apiIsReady,
        currentFields: apiCurrentFields,
        find: apiFind,
        export: apiExport,
        artifacts: apiArtifacts,
        artifactFieldOptionsFields: apiArtifactFieldOptionsFields,
        artifactFieldOptionsFind: apiArtifactFieldOptionsFind,
        fieldOptionsFields: apiFieldOptionsFields,
        fieldOptionsFind: apiFieldOptionsFind,
    } = useResourceApiService(resourceName);
    const [loading, setLoading] = React.useState<boolean>(false);
    const [fields, setFields] = React.useState<any[]>([]);
    const [rows, setRows] = React.useState<any[]>([]);
    const [pageInfo, setPageInfo] = React.useState<any>();
    const [artifacts, setArtifacts] = React.useState<any[]>();
    const {
        value: quickFilterValue,
        component: quickFilterComponent
    } = useDataQuickFilter(quickFilterInitialValue, quickFilterProps);
    const refresh = () => {
        if (apiIsReady && !findDisabled) {
            const processedFindArgs = {
                ...(findArgs ?? {}),
                quickFilter: quickFilterValue?.length ? quickFilterValue : undefined,
                includeLinksInRows: true,
            };
            setLoading(true);
            if (resourceFieldName == null) {
                apiFind(processedFindArgs).then((response) => {
                    setRows(response.rows);
                    setPageInfo(response.page);
                }).finally(() => setLoading(false));
            } else if (resourceType == null) {
                apiFieldOptionsFind({ fieldName: resourceFieldName, ...processedFindArgs }).then((response) => {
                    setRows(response.rows);
                    setPageInfo(response.page);
                }).finally(() => setLoading(false));
            } else {
                const args = {
                    type: resourceType,
                    code: resourceTypeCode ?? '',
                    fieldName: resourceFieldName,
                    ...processedFindArgs
                };
                apiArtifactFieldOptionsFind(args).then((response) => {
                    setRows(response.rows);
                    setPageInfo(response.page);
                }).finally(() => setLoading(false));
            }
        }
    }
    const exportt: DataCommonExportFn = (fields?: string[], fileType?: ExportFileType, forceUnpaged?: boolean) => {
        const args = {
            ...(findArgs ?? {}),
            quickFilter: quickFilterValue?.length ? quickFilterValue : undefined,
            fields,
            fileType,
        };
        apiExport(forceUnpaged ? { ...args, unpaged: true } : args).then(response => {
            if (saveAs) {
                saveAs(response.blob, response.fileName);
            } else {
                console.error('Couldn\'t save export file ' + response.fileName + ': saveAs not available in BaseAppContext');
            }
        })
    }
    React.useEffect(() => {
        if (apiIsReady) {
            if (resourceFieldName == null) {
                setFields(apiCurrentFields ?? []);
            } else if (resourceType == null) {
                apiFieldOptionsFields({ fieldName: resourceFieldName }).then(fields => {
                    setFields(fields);
                });
            } else {
                const args = {
                    type: resourceType,
                    code: resourceTypeCode ?? '',
                    fieldName: resourceFieldName
                };
                apiArtifactFieldOptionsFields(args).then(fields => {
                    setFields(fields);
                });
            }
            refresh();
        }
    }, [
        apiIsReady,
        quickFilterValue,
        findDisabled,
        findArgs,
    ]);
    React.useEffect(() => {
        if (getArtifacts) {
            if (apiIsReady) {
                apiArtifacts({}).then(artifacts => {
                    setArtifacts(artifacts);
                });
            }
        } else {
            artifacts != null && setArtifacts(undefined);
        }
    }, [apiIsReady, getArtifacts]);
    return {
        loading,
        fields,
        rows: findDisabled ? [] : rows,
        pageInfo,
        artifacts,
        refresh,
        export: exportt,
        quickFilterComponent,
    };
}

export const useDataCommonEditable = (
    resourceName: string,
    readOnly: boolean,
    formAdditionalData: ((row?: any) => any) | any,
    toolbarCreateLink: string | undefined,
    rowDetailLink: string | undefined,
    rowUpdateLink: string | undefined,
    rowHideUpdateButton: boolean | undefined,
    rowHideDeleteButton: boolean | undefined,
    rowHideDetailsButton: boolean | undefined,
    popupEditActive: boolean | undefined,
    popupEditCreateActive: boolean | undefined,
    popupEditUpdateActive: boolean | undefined,
    popupEditFormContent: React.ReactElement | undefined,
    popupEditFormDialogTitle: string | undefined,
    popupEditFormDialogResourceTitle: string | undefined,
    popupEditFormDialogComponentProps: any,
    apiCurrentActions: any,
    apiDelete: (id: any) => Promise<any>,
    refresh: () => void) => {
    const {
        t,
        temporalMessageShow,
        messageDialogShow,
    } = useBaseAppContext();
    const dataDialogPopupApiRef = React.useRef<DataFormDialogApi>();
    const confirmDialogButtons = useConfirmDialogButtons();
    const confirmDialogComponentProps = { maxWidth: 'sm', fullWidth: true };
    const isPopupEditCreate = popupEditActive || popupEditCreateActive;
    const isPopupEditUpdate = popupEditActive || popupEditUpdateActive;
    const showCreateDialog: DataCommonShowCreateDialogFn = (row?: any) => {
        dataDialogPopupApiRef.current?.show(
            undefined,
            (typeof formAdditionalData === 'function') ? formAdditionalData(row, 'create') : formAdditionalData).
            then(() => {
                refresh?.();
            }).
            catch(() => {
                // Feim un catch buit perquè no aparegui a la consola el missatge: Uncaught (in promise)
            });
    }
    const showUpdateDialog: DataCommonShowUpdateDialogFn = (id: any, row?: any) => {
        dataDialogPopupApiRef.current?.show(
            id,
            (typeof formAdditionalData === 'function') ? formAdditionalData(row, 'update') : formAdditionalData).
            then(() => {
                refresh?.();
            }).
            catch(() => {
                // Feim un catch buit perquè no aparegui a la consola el missatge: Uncaught (in promise)
            });
    }
    const doDelete = (id: any) => {
        messageDialogShow(
            t('datacommon.delete.single.title'),
            t('datacommon.delete.single.confirm'),
            confirmDialogButtons,
            confirmDialogComponentProps).
            then((value: any) => {
                if (value) {
                    apiDelete(id)
                        .then(() => {
                            refresh?.();
                            temporalMessageShow(null, t('datacommon.delete.single.success'), 'success');
                        })
                        .catch((error: ResourceApiError) => {
                            temporalMessageShow(t('datacommon.delete.single.error'), error.message, 'error');
                        });
                }
            }).
            catch(() => {
                // Feim un catch buit perquè no aparegui a la consola el missatge: Uncaught (in promise)
            });
    }
    const isCreateLinkPresent = apiCurrentActions?.['create'] != null;
    const toolbarAddElement = isCreateLinkPresent && !readOnly ? toToolbarIcon('add', {
        title: t('datacommon.create.title'),
        linkTo: toolbarCreateLink,
        linkState: formAdditionalData ? { additionalData: formAdditionalData } : undefined,
        onClick: !toolbarCreateLink ? showCreateDialog : undefined,
    }) : undefined;
    const rowEditActions: DataCommonAdditionalAction[] = [];
    !readOnly && !rowHideUpdateButton && rowEditActions.push({
        title: t('datacommon.update.title'),
        rowLink: 'update',
        icon: 'edit',
        linkTo: rowUpdateLink,
        linkState: rowUpdateLink != null && formAdditionalData != null ? { additionalData: formAdditionalData } : undefined,
        clickShowUpdateDialog: rowUpdateLink == null,
    });
    !readOnly && !rowHideDeleteButton && rowEditActions.push({
        title: t('datacommon.delete.title'),
        icon: 'delete',
        onClick: doDelete,
        showInMenu: true,
        rowLink: 'delete',
    });
    rowDetailLink && !rowHideDetailsButton && rowEditActions.push({
        title: t('datacommon.details.title'),
        icon: 'info',
        linkTo: rowDetailLink,
        rowLink: readOnly ? undefined : '!update',
    });
    const formDialogComponent = !readOnly && (isPopupEditCreate || isPopupEditUpdate) ? <DataFormDialog
        resourceName={resourceName}
        title={popupEditFormDialogTitle}
        resourceTitle={popupEditFormDialogResourceTitle}
        dialogComponentProps={popupEditFormDialogComponentProps}
        apiRef={dataDialogPopupApiRef}>
        {popupEditFormContent}
    </DataFormDialog> : null;
    return {
        toolbarAddElement,
        rowEditActions,
        formDialogComponent,
        showCreateDialog,
        showUpdateDialog,
    };
}
