import React from 'react';
import Box from '@mui/material/Box';
import Toolbar from '../Toolbar';
import { toToolbarIcon } from '../ToolbarIcon';
import { ReactElementWithPosition } from '../../../util/reactNodePosition';
import { useBaseAppContext } from '../../BaseAppContext';

export type GridToolbarType = 'default' | 'upper' | 'hidden';

type GridToolbarProps = {
    title?: string;
    elementsWithPositions?: ReactElementWithPosition[];
    upperToolbar?: boolean;
    error?: any;
};

export const useToolbar = (
    title: string,
    titleDisabled: boolean,
    toolbarType: GridToolbarType,
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
    const toolbarNodes: ReactElementWithPosition[] = [];
    const toolbarNodesPosition = 2;
    toolbarNodes.push(...(toolbarElementsWithPositions ?? []));
    !toolbarHideExport && toolbarNodes.push({
        position: toolbarNodesPosition,
        element: toToolbarIcon('file_download', {
            title: t('grid.export.title'),
            onClick: () => doExport?.(undefined, true),
        }),
    });
    !toolbarHideRefresh && toolbarNodes.push({
        position: toolbarNodesPosition,
        element: toToolbarIcon('refresh', {
            title: t('grid.refresh.title'),
            onClick: () => doRefresh(),
        }),
    });
    !toolbarHideQuickFilter && toolbarNodes.push({
        position: toolbarNodesPosition,
        element: quickFilterComponent
    });
    const toolbarElementProps = {
        title: !titleDisabled ? title : undefined,
        upperToolbar: isUpperToolbarType,
        elementsWithPositions: toolbarNodes,
        error: apiCurrentError ?? undefined,
    };
    return !isHiddenToolbarType ? <GridToolbar {...toolbarElementProps} /> : null;
}

const GridToolbar: React.FC<GridToolbarProps> = (props) => {
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

export default GridToolbar;