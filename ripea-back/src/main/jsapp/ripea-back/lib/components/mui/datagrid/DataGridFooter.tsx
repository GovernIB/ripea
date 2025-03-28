import React from 'react';
import Box from '@mui/material/Box';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import {
    GridFooterContainer,
    useGridApiContext,
    useGridSelector,
    gridPageSelector,
    gridPageSizeSelector,
    selectedGridRowsCountSelector,
    GridRowSelectionModel,
} from '@mui/x-data-grid';
import Pagination from '@mui/material/Pagination';
import { useBaseAppContext } from '../../BaseAppContext';

type DataGridFooterSelectionProps = {
    setRowSelectionModel: (rowSelectionModel: GridRowSelectionModel) => void;
};

type DataGridFooterPaginationProps = {
    pageInfo: any;
};

type DataGridFooterProps = {
    selectionActive: boolean;
    paginationActive: boolean;
    pageInfo: any;
    setRowSelectionModel: (rowSelectionModel: GridRowSelectionModel) => void;
};

const DataGridFooterSelection: React.FC<DataGridFooterSelectionProps> = (props) => {
    const { setRowSelectionModel } = props;
    const { t } = useBaseAppContext();
    const apiRef = useGridApiContext();
    const selectedRowCount = useGridSelector(apiRef, selectedGridRowsCountSelector);
    const handleClearClick = () => {
        setRowSelectionModel([]);
    }
    const selectedMessage = selectedRowCount > 1 ? t('grid.selection.multiple', { count: selectedRowCount }) : t('grid.selection.one');
    return <Box sx={{ ml: 2 }}>
        {selectedRowCount ? selectedMessage : null}
        {selectedRowCount ? <IconButton onClick={handleClearClick} size="small">
            <Icon fontSize="small">clear</Icon>
        </IconButton> : null}
    </Box>;
}

const GridFooterPagination: React.FC<DataGridFooterPaginationProps> = (props) => {
    const { pageInfo } = props;
    const { t } = useBaseAppContext();
    const apiRef = useGridApiContext();
    const page = useGridSelector(apiRef, gridPageSelector);
    const pageSize = useGridSelector(apiRef, gridPageSizeSelector);
    const pageCount = pageInfo?.totalElements && pageSize ? Math.ceil(pageInfo.totalElements / pageSize) : undefined;
    const pageRowCount = page === (pageCount ?? 0) - 1 ? pageInfo.totalElements % pageSize : pageSize;
    const firstElementIndex = page * pageSize + 1;
    const boxStyle = { display: 'flex', justifContent: 'flex-end', alignItems: 'center' };
    return <Box style={boxStyle}>
        <Box>
            {pageInfo != null ? t('grid.pageInfo', { from: firstElementIndex, to: (firstElementIndex + pageRowCount - 1), count: pageInfo.totalElements }) : ''}
        </Box>
        <Pagination
            color="primary"
            count={pageSize ? pageCount : 0}
            page={page + 1}
            onChange={(_event, value) => apiRef.current.setPage(value - 1)} />
    </Box>;
}

const DataGridFooter: React.FC<DataGridFooterProps> = (props) => {
    const {
        selectionActive,
        setRowSelectionModel,
        paginationActive,
        pageInfo,
    } = props;
    const showFooter = selectionActive || paginationActive;
    return showFooter ? <GridFooterContainer>
        <DataGridFooterSelection setRowSelectionModel={setRowSelectionModel} />
        {paginationActive && <GridFooterPagination pageInfo={pageInfo} />}
    </GridFooterContainer> : null;
}

export default DataGridFooter;