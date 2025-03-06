import React from 'react';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemText from '@mui/material/ListItemText';
import IconButton from '@mui/material/IconButton';
import Icon from '@mui/material/Icon';
import Box from '@mui/material/Box';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import { useTheme } from '@mui/material/styles';
import { capitalize } from '../../../util/text';
import { formattedFieldValue } from '../../../util/fields';
import {
    useApiDataCommon,
    useDataCommonEditable,
    DataCommonAdditionalAction
} from '../datacommon/MuiDataCommon';
import { useDataToolbar } from '../datacommon/DataToolbar';
import DataNoRows from '../datacommon/DataNoRows';
import {
    ReactElementWithPosition,
    joinReactElementsWithPositionWithReactElementsWithPositions
} from '../../../util/reactNodePosition';
import { useResourceApiService } from '../../ResourceApiProvider';

type MuiDataListFieldRendererArgs = {
    value: any;
    row: any;
    formattedValue?: string;
};

export type MuiDataListProps = {
    title: string;
    titleDisabled?: true;
    subtitle?: string;
    resourceName: string;
    primaryField: string;
    secondaryField?: string;
    primaryFieldRenderer?: (args: MuiDataListFieldRendererArgs) => React.ReactElement;
    secondaryFieldRenderer?: (args: MuiDataListFieldRendererArgs) => React.ReactElement;
    readOnly?: boolean;
    quickFilterInitialValue?: string;
    filter?: string;
    namedQueries?: string[];
    perspectives?: string[];
    formAdditionalData?: any;
    toolbarHideExport?: boolean;
    toolbarHideRefresh?: boolean;
    toolbarHideQuickFilter?: boolean;
    toolbarCreateLink?: string;
    toolbarElementsWithPositions?: ReactElementWithPosition[];
    toolbarAdditionalRow?: React.ReactElement;
    rowDetailLink?: string;
    rowUpdateLink?: string;
    rowAdditionalActions?: DataCommonAdditionalAction[];
    popupEditActive?: boolean;
    popupEditCreateActive?: boolean;
    popupEditUpdateActive?: boolean;
    popupEditFormContent?: React.ReactElement;
    popupEditFormDialogTitle?: string;
    popupEditFormDialogResourceTitle?: string;
    popupEditFormDialogComponentProps?: any;
};

const fieldDescription = (name: string, value: any, fields: any[] | undefined) => {
    const field = fields?.find(f => f.name === name);
    return formattedFieldValue(value, field);
}

const rowActionToIconButton = (
    rowAction: DataCommonAdditionalAction,
    row: any,
    handleRowActionClick: any,
    key: any) => {
    return <IconButton
        key={key}
        onClick={(event) => handleRowActionClick(rowAction, row, event)}
        title={rowAction.title}
        size="small">
        <Icon fontSize="small">{rowAction.icon ?? 'question_mark'}</Icon>
    </IconButton>;
}

const ListItemSecondaryActions: React.FC<any> = (props) => {
    const {
        row,
        rowAdditionalActions,
        rowEditActions,
        popupUpdate
    } = props;
    const rowActions: DataCommonAdditionalAction[] = [...rowAdditionalActions, ...rowEditActions];
    const noMenuRowActions = rowActions.filter(a => !a.showInMenu);
    const showInMenuRowActions = rowActions.filter(a => a.showInMenu);
    const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
    const open = Boolean(anchorEl);
    const handleMoreMenuClick = (event: React.MouseEvent<HTMLButtonElement>) => {
        setAnchorEl(event.currentTarget);
    }
    const handleRowActionClick = (rowAction: DataCommonAdditionalAction, row: any, event: React.MouseEvent) => {
        if (rowAction.popupUpdateOnClick) {
            popupUpdate(row.id, row);
        } else {
            rowAction.onClick?.(row.id, event);
        }
    }
    const moreMenuIcon = showInMenuRowActions.length > 0 ? <>
        <IconButton size="small" onClick={handleMoreMenuClick} sx={{ ml: 1 }}>
            <Icon fontSize="small">more_vert</Icon>
        </IconButton>
        <Menu
            anchorEl={anchorEl}
            open={open}
            onClose={() => setAnchorEl(null)}>
            {showInMenuRowActions.map((ra, i) => <MenuItem key={i} onClick={(event) => handleRowActionClick(ra, row, event)}>
                <Icon color="action" sx={{ mr: 1.5 }}>{ra.icon ?? 'question_mark'}</Icon>
                {ra.title}
            </MenuItem>)}
        </Menu>
    </> : null;
    return <>
        {noMenuRowActions.map((ra, i) => rowActionToIconButton(ra, row, handleRowActionClick, i))}
        {moreMenuIcon}
    </>;
}

export const MuiDataList: React.FC<MuiDataListProps> = (props) => {
    const {
        title,
        titleDisabled,
        subtitle,
        resourceName,
        primaryField,
        secondaryField,
        primaryFieldRenderer,
        secondaryFieldRenderer,
        readOnly,
        quickFilterInitialValue,
        filter,
        namedQueries,
        perspectives,
        formAdditionalData,
        toolbarHideExport = true,
        toolbarHideRefresh,
        toolbarHideQuickFilter,
        toolbarCreateLink,
        toolbarElementsWithPositions,
        toolbarAdditionalRow,
        rowDetailLink,
        rowUpdateLink,
        rowAdditionalActions = [],
        popupEditActive,
        popupEditCreateActive,
        popupEditUpdateActive,
        popupEditFormContent,
        popupEditFormDialogTitle,
        popupEditFormDialogResourceTitle,
        popupEditFormDialogComponentProps,
    } = props;
    const theme = useTheme();
    const {
        currentActions: apiCurrentActions,
        currentError: apiCurrentError,
        currentFields: apiCurrentFields,
        delette: apiDelete,
    } = useResourceApiService(resourceName);
    const findArgs = React.useMemo(() => ({
        filter,
        namedQueries,
        perspectives,
        unpaged: true
    }), [filter, namedQueries, perspectives]);
    const {
        loading,
        rows,
        refresh,
        quickFilterComponent
    } = useApiDataCommon(
        resourceName,
        findArgs,
        quickFilterInitialValue,
        { sx: { ml: 1 } });
    const {
        toolbarAddElement,
        rowEditActions,
        popupDialog,
        popupCreate: _popupCreate,
        popupUpdate,
    } = useDataCommonEditable(
        resourceName,
        readOnly ?? false,
        formAdditionalData,
        toolbarCreateLink,
        rowUpdateLink,
        rowDetailLink,
        popupEditActive,
        popupEditCreateActive,
        popupEditUpdateActive,
        popupEditFormContent,
        popupEditFormDialogTitle,
        popupEditFormDialogResourceTitle,
        popupEditFormDialogComponentProps,
        apiCurrentActions,
        apiDelete,
        refresh);
    const toolbarNodesPosition = 2;
    const toolbarListElementsWithPositions: ReactElementWithPosition[] = [];
    toolbarAddElement != null && toolbarListElementsWithPositions.push({
        position: toolbarNodesPosition,
        element: toolbarAddElement
    });
    const joinedElementsWithPositions = joinReactElementsWithPositionWithReactElementsWithPositions(
        toolbarNodesPosition,
        toolbarListElementsWithPositions,
        toolbarElementsWithPositions);
    const toolbar = useDataToolbar(
        title ?? capitalize(resourceName) ?? '<unknown>',
        titleDisabled ?? false,
        subtitle,
        'default',
        apiCurrentError,
        quickFilterComponent,
        refresh,
        undefined,
        toolbarHideExport,
        toolbarHideRefresh,
        toolbarHideQuickFilter,
        joinedElementsWithPositions);
    return <>
        {toolbar}
        {toolbarAdditionalRow ? <Box sx={{ mb: 0 }}>{toolbarAdditionalRow}</Box> : null}
        {popupDialog}
        {rows?.length ? <List
            disablePadding
            sx={{ border: '1px solid ' + theme.palette.divider, borderRadius: '4px' }}>
            {rows.map((r, i) => {
                const primary = fieldDescription(primaryField, r[primaryField], apiCurrentFields);
                const secondary = secondaryField ? fieldDescription(secondaryField, r[secondaryField], apiCurrentFields) : undefined;
                const primaryFieldRendererArgs = {
                    value: r[primaryField],
                    row: r,
                    formattedValue: primary
                };
                const secondaryFieldRendererArgs = {
                    value: secondaryField ? r[secondaryField] : undefined,
                    row: r,
                    formattedValue: secondary
                };
                return <ListItem
                    key={r.id}
                    secondaryAction={<ListItemSecondaryActions
                        row={r}
                        rowAdditionalActions={rowAdditionalActions}
                        rowEditActions={rowEditActions}
                        popupUpdate={popupUpdate} />}
                    disablePadding
                    sx={i > 0 ? { borderTop: '1px solid #E0E0E0' } : undefined}>
                    <ListItemButton>
                        <ListItemText
                            primary={primaryFieldRenderer ? primaryFieldRenderer(primaryFieldRendererArgs) : primary}
                            secondary={secondaryFieldRenderer ? secondaryFieldRenderer(secondaryFieldRendererArgs) : secondary} />
                    </ListItemButton>
                </ListItem>;
            })}
        </List> : <Box sx={{
            border: '1px solid ' + theme.palette.divider,
            borderRadius: '4px',
        }}>
            <DataNoRows />
        </Box>}
    </>;
}

export default MuiDataList;
