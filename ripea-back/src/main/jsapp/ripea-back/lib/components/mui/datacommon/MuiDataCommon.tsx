import React from 'react';
import {
    useResourceApiService,
    ResourceApiFindCommonArgs,
    ResourceApiError
} from '../../ResourceApiProvider';
import { useDataQuickFilter } from './DataQuickFilter';
import { toToolbarIcon } from '../ToolbarIcon';
import DataFormDialog, { DataFormDialogApi } from './DataFormDialog';
import { useBaseAppContext } from '../../BaseAppContext';
import { useConfirmDialogButtons } from '../../AppButtons';

export type DataCommonFindArgs = ResourceApiFindCommonArgs;

export type DataCommonAdditionalAction = {
    title?: string;
    rowApiLink?: string;
    rowApiAction?: string;
    rowApiReport?: string;
    icon?: string;
    linkTo?: ((row: any) => string) | string;
    linkState?: ((row: any) => any) | any;
    onClick?: (id: any, event: React.MouseEvent) => void;
    popupCreateOnClick?: boolean;
    popupUpdateOnClick?: boolean;
    showInMenu?: ((row: any) => boolean) | boolean;
    disabled?: ((row: any) => boolean) | boolean;
    hidden?: ((row: any) => boolean) | boolean;
};

export const useApiDataCommon = (
    resourceName: string,
    findArgs?: DataCommonFindArgs,
    quickFilterInitialValue?: string,
    quickFilterProps?: any) => {
    const [loading, setLoading] = React.useState<boolean>(false);
    const [rows, setRows] = React.useState<any[]>([]);
    const [pageInfo, setPageInfo] = React.useState<any>();
    const {
        isReady: apiIsReady,
        find: apiFind,
    } = useResourceApiService(resourceName);
    const {
        value: quickFilterValue,
        component: quickFilterComponent
    } = useDataQuickFilter(quickFilterInitialValue, quickFilterProps);
    const refresh = () => {
        if (apiIsReady) {
            const processedFindArgs = {
                ...(findArgs ?? {}),
                quickFilter: quickFilterValue?.length ? quickFilterValue : undefined,
                includeLinksInRows: true,
            };
            setLoading(true);
            apiFind(processedFindArgs).then((response) => {
                setRows(response.rows);
                setPageInfo(response.page);
            }).finally(() => setLoading(false));
        }
    }
    React.useEffect(() => {
        if (apiIsReady) {
            refresh();
        }
    }, [
        apiIsReady,
        quickFilterValue,
        findArgs,
    ]);
    return {
        loading,
        rows,
        pageInfo,
        refresh,
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
    const popupCreate = (row?: any) => {
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
    const popupUpdate = (id: any, row?: any) => {
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
        onClick: !toolbarCreateLink ? popupCreate : undefined,
    }) : undefined;
    const rowEditActions: DataCommonAdditionalAction[] = [];
    !readOnly && !rowHideUpdateButton && rowEditActions.push({
        title: t('datacommon.update.title'),
        rowApiLink: 'update',
        icon: 'edit',
        linkTo: rowUpdateLink,
        linkState: rowUpdateLink != null && formAdditionalData != null ? { additionalData: formAdditionalData } : undefined,
        popupUpdateOnClick: rowUpdateLink == null,
    });
    !readOnly && !rowHideDeleteButton && rowEditActions.push({
        title: t('datacommon.delete.title'),
        rowApiLink: 'delete',
        icon: 'delete',
        onClick: doDelete,
        showInMenu: true,
    });
    rowDetailLink && !rowHideDetailsButton && rowEditActions.push({
        title: t('datacommon.details.title'),
        rowApiLink: readOnly ? undefined : '!update',
        icon: 'info',
        linkTo: rowDetailLink,
    });
    const popupDialog = !readOnly && (isPopupEditCreate || isPopupEditUpdate) ? <DataFormDialog
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
        popupDialog,
        popupCreate,
        popupUpdate,
    };
}
