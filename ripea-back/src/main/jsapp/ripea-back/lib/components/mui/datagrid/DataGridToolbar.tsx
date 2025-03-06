import React from 'react';
import Box from '@mui/material/Box';
import Toolbar from '../Toolbar';
import { toToolbarIcon } from '../ToolbarIcon';
import {
    ReactElementWithPosition,
    joinReactElementsWithPositionWithReactElementsWithPositions
} from '../../../util/reactNodePosition';
import { useBaseAppContext } from '../../BaseAppContext';

export type DataGridToolbarType = 'default' | 'upper' | 'hidden';

type DataGridToolbarProps = {
    title?: string;
    elementsWithPositions?: ReactElementWithPosition[];
    upperToolbar?: boolean;
    error?: any;
};

export const useDataGridToolbar = (
    title: string,
    titleDisabled: boolean,
    toolbarType: DataGridToolbarType,
    apiCurrentError: any,
    quickFilterComponent: React.ReactElement,
    doRefresh: () => void,
    doExport?: (type?: string, forceUnpaged?: boolean) => void,
    toolbarHideExport?: boolean,
    toolbarHideRefresh?: boolean,
    toolbarHideQuickFilter?: boolean,
    toolbarElementsWithPositions?: ReactElementWithPosition[]) => {
    const { t } = useBaseAppContext();
    const isUpperToolbarType = toolbarType === 'upper';
    const isHiddenToolbarType = toolbarType === 'hidden';
    const elementsWithPosition: ReactElementWithPosition[] = [];
    const toolbarNodesPosition = 2;
    !toolbarHideExport && elementsWithPosition.push({
        position: toolbarNodesPosition,
        element: toToolbarIcon('file_download', {
            title: t('grid.export.title'),
            onClick: () => doExport?.(undefined, true),
        }),
    });
    !toolbarHideRefresh && elementsWithPosition.push({
        position: toolbarNodesPosition,
        element: toToolbarIcon('refresh', {
            title: t('grid.refresh.title'),
            onClick: () => doRefresh(),
        }),
    });
    !toolbarHideQuickFilter && elementsWithPosition.push({
        position: toolbarNodesPosition,
        element: quickFilterComponent
    });
    const joinedElementsWithPositions = joinReactElementsWithPositionWithReactElementsWithPositions(
            2,
            elementsWithPosition,
            toolbarElementsWithPositions);
    const toolbarElementProps = {
        title: !titleDisabled ? title : undefined,
        upperToolbar: isUpperToolbarType,
        elementsWithPositions: joinedElementsWithPositions,
        error: apiCurrentError ?? undefined,
    };
    return !isHiddenToolbarType ? <DataGridToolbar {...toolbarElementProps} /> : null;
}

const DataGridToolbar: React.FC<DataGridToolbarProps> = (props) => {
    const {
        title,
        elementsWithPositions: elementsWithPositionsProp,
        upperToolbar,
        error,
    } = props;
    const { t } = useBaseAppContext();
    const [errorShown, setErrorShown] = React.useState<boolean>(false);
    React.useEffect(() => {
        error && setErrorShown(true);
    }, [error]);
    const elementsWithPositions: ReactElementWithPosition[] = [];
    if (error) {
        elementsWithPositions.push({
            position: 0,
            element: toToolbarIcon('warning', {
                title: error.message,
                color: 'error',
                onClick: () => setErrorShown(true),
                sx: { mx: 1 },
            }),
        });
        errorShown && elementsWithPositions.push({
            position: 1,
            element: toToolbarIcon('close', {
                title: 'Tancar',
                onClick: () => setErrorShown(false),
                sx: { mr: 1 },
            }),
        });
    }
    !errorShown && elementsWithPositions.push(...(elementsWithPositionsProp ?? []));
    return <Box sx={{ p: 0 }}>
        <Toolbar
            title={error && errorShown ? t('grid.error.toolbar') : title}
            subtitle={error && errorShown && error?.message}
            elementsWithPositions={elementsWithPositions}
            upperToolbar={upperToolbar}
            error={error}
            sx={!upperToolbar ? { mt: -1.5 } : undefined} />
    </Box>;
}

export default DataGridToolbar;